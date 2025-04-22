package es.alvarogrlp.marvelsimu.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import es.alvarogrlp.marvelsimu.backend.combat.logic.CombatManager;
import es.alvarogrlp.marvelsimu.backend.controller.abstracts.AbstractController;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import es.alvarogrlp.marvelsimu.backend.selection.logic.SelectionManager;
import es.alvarogrlp.marvelsimu.backend.util.AlertUtils;
import es.alvarogrlp.marvelsimu.backend.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.AnchorPane;

public class CombateController extends AbstractController {

    @FXML private Button btnVolver;
    @FXML private AnchorPane rootPane;
    
    private CombatManager combatManager;
    
    // Variables para recibir los personajes de la pantalla anterior
    private static List<PersonajeModel> personajesJugadorSeleccionados;
    private static List<PersonajeModel> personajesIASeleccionados;
    
    /**
     * Establece los personajes seleccionados estáticamente para mantenerlos entre pantallas
     * Este método es llamado desde SeleccionPersonajesController
     */
    public static void setPersonajesSeleccionados(List<PersonajeModel> jugadorSeleccionados, List<PersonajeModel> iaSeleccionados) {
        personajesJugadorSeleccionados = new ArrayList<>();
        personajesIASeleccionados = new ArrayList<>();
        
        // Clonar los personajes para no modificar los originales
        if (jugadorSeleccionados != null) {
            for (PersonajeModel personaje : jugadorSeleccionados) {
                personajesJugadorSeleccionados.add(personaje.clonar());
            }
        }
        
        if (iaSeleccionados != null) {
            for (PersonajeModel personaje : iaSeleccionados) {
                personajesIASeleccionados.add(personaje.clonar());
            }
        }
    }

    @FXML
    public void initialize() {
        try {
            if (rootPane == null) {
                System.err.println("Error: rootPane no está inicializado");
                return;
            }
            
            // Obtener el SelectionManager actual de SessionManager
            SelectionManager selectionManager = SessionManager.getInstance().getSelectionManager();
            
            // Verificar que selectionManager no sea null
            if (selectionManager == null) {
                System.err.println("Error: SelectionManager es null, creando uno nuevo");
                selectionManager = new SelectionManager(rootPane);
            }
            
            // Inicializar el CombatManager pasando el SelectionManager
            combatManager = new CombatManager(
                rootPane, 
                personajesJugadorSeleccionados, 
                personajesIASeleccionados,
                selectionManager
            );
            
            // Configurar los event handlers
            setupEventHandlers();
            
            // Iniciar el combate
            combatManager.getTurnManager().startCombat();
            
        } catch (Exception e) {
            System.err.println("Error en initialize: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.mostrarError("Error", "No se pudo inicializar la pantalla de combate");
        }
    }
    
    private void setupEventHandlers() {
        // Botones principales
        combatManager.getUIManager().getAttackButton().setOnAction(e -> onAtacarClick());
        combatManager.getUIManager().getChangeButton().setOnAction(e -> onCambiarClick());
        combatManager.getUIManager().getBackButton().setOnAction(e -> onVolverClick());
        
        // Botones de ataque
        combatManager.getUIManager().getMeleeAttackButton().setOnAction(e -> onAtaqueMeleeClick());
        combatManager.getUIManager().getRangedAttackButton().setOnAction(e -> onAtaqueLejanoClick());
        combatManager.getUIManager().getAbility1Button().setOnAction(e -> onHabilidad1Click());
        combatManager.getUIManager().getAbility2Button().setOnAction(e -> onHabilidad2Click());
    }
    
    @FXML
    protected void onAtacarClick() {
        // Verificar que sea el turno del jugador
        if (combatManager.getTurnManager().isPlayerTurn()) {
            combatManager.getUIManager().showAttackOptions();
        } else {
            System.out.println("No es el turno del jugador, ignorando clic en atacar");
        }
    }

    @FXML
    protected void onCambiarClick() {
        if (combatManager.getTurnManager().isPlayerTurn()) {
            combatManager.getUIManager().showCharacterSelection(combatManager);
        }
    }

    @FXML
    protected void onAtaqueMeleeClick() {
        // Verificación adicional para evitar acciones durante el turno de la IA
        if (combatManager.getTurnManager().isPlayerTurn()) {
            combatManager.playerAttack("melee");
        }
    }

    @FXML
    protected void onAtaqueLejanoClick() {
        if (combatManager.getTurnManager().isPlayerTurn()) {
            combatManager.playerAttack("lejano");
        }
    }

    @FXML
    protected void onHabilidad1Click() {
        if (combatManager.getTurnManager().isPlayerTurn()) {
            combatManager.playerAttack("habilidad1");
        }
    }

    @FXML
    protected void onHabilidad2Click() {
        if (combatManager.getTurnManager().isPlayerTurn()) {
            combatManager.playerAttack("habilidad2");
        }
    }

    @FXML
    protected void onVolverClick() {
        if (!combatManager.isCombatFinished()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Abandonar combate");
            alert.setHeaderText("¿Estás seguro de que quieres abandonar el combate?");
            alert.setContentText("El progreso actual se perderá.");
            
            ButtonType buttonTypeYes = new ButtonType("Sí, abandonar");
            ButtonType buttonTypeNo = new ButtonType("No, continuar", ButtonBar.ButtonData.CANCEL_CLOSE);
            
            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
            
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                getClass().getResource("/es/alvarogrlp/marvelsimu/css/alerts.css").toExternalForm());
            dialogPane.getStyleClass().add("custom-alert");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == buttonTypeYes) {
                // Volver a la pantalla de selección usando el método específico para ventanas de juego
                abrirVentanaJuego(btnVolver, "seleccionPersonajes.fxml");
            }
        } else {
            // Si el combate ya terminó, volver directamente
            abrirVentanaJuego(btnVolver, "seleccionPersonajes.fxml");
        }
    }
}