package es.alvarogrlp.marvelsimu.backend.controller;

import es.alvarogrlp.marvelsimu.backend.controller.abstracts.AbstractController;
import es.alvarogrlp.marvelsimu.backend.selection.logic.SelectionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Controlador refactorizado para la pantalla de selección de personajes
 */
public class SeleccionPersonajesController extends AbstractController {

    // Elementos de la interfaz
    @FXML private Text txtTitulo;
    @FXML private Text txtTitulo1;
    @FXML private Text txtTitulo11;
    @FXML private Button onLucharButton;
    @FXML private Button btnVolver;
    @FXML private ImageView fondo;
    @FXML private VBox miEquipo;
    @FXML private VBox equipoIA;
    @FXML private AnchorPane rootPane;

    // Gestor de selección - contiene toda la lógica
    private SelectionManager selectionManager;

    /**
     * Inicializa la pantalla
     */
    @FXML
    public void initialize() {
        // Intentar cargar el CSS específico directamente
        try {
            String cssPath = "/es/alvarogrlp/marvelsimu/seleccionPersonajes.css";
            java.net.URL cssResource = getClass().getResource(cssPath);
            
            if (cssResource != null) {
                // Verificar si el CSS ya está cargado para evitar duplicados
                if (!rootPane.getStylesheets().contains(cssResource.toExternalForm())) {
                    rootPane.getStylesheets().add(cssResource.toExternalForm());
                    System.out.println("CSS específico cargado correctamente: " + cssResource.toExternalForm());
                }
                
                // Cargar también el CSS del tema actual manualmente
                String themeCssPath = es.alvarogrlp.marvelsimu.backend.config.ThemeManager.isDarkMode() ?
                    "/es/alvarogrlp/marvelsimu/dark-mode.css" :
                    "/es/alvarogrlp/marvelsimu/light-mode.css";
                    
                java.net.URL themeCssResource = getClass().getResource(themeCssPath);
                if (themeCssResource != null && !rootPane.getStylesheets().contains(themeCssResource.toExternalForm())) {
                    rootPane.getStylesheets().add(themeCssResource.toExternalForm());
                    System.out.println("CSS del tema cargado correctamente: " + themeCssResource.toExternalForm());
                }
                
                // Configurar el fondo manualmente
                if (fondo != null) {
                    String imagePath = es.alvarogrlp.marvelsimu.backend.config.ThemeManager.isDarkMode() ?
                        "/images/fondoNegro.png" :
                        "/images/fondoBlanco.png";
                    
                    try {
                        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream(imagePath));
                        fondo.setImage(image);
                    } catch (Exception e) {
                        System.err.println("Error cargando la imagen de fondo: " + e.getMessage());
                    }
                }
            } else {
                System.err.println("Error: No se pudo encontrar el CSS específico en: " + cssPath);
            }
            
            // Aplicar manualmente algunas clases que podrían faltar
            if (rootPane != null) rootPane.getStyleClass().add("root-pane");
            if (miEquipo != null) miEquipo.getStyleClass().add("container-seleccion");
            if (equipoIA != null) equipoIA.getStyleClass().add("container-seleccion");
            if (txtTitulo != null) txtTitulo.getStyleClass().add("title-text");
            if (txtTitulo1 != null) txtTitulo1.getStyleClass().add("title-text");
            if (txtTitulo11 != null) txtTitulo11.getStyleClass().add("title-text");
        } catch (Exception e) {
            System.err.println("Error cargando CSS en el controlador: " + e.getMessage());
        }
        
        // Crear el gestor de selección
        selectionManager = new SelectionManager(rootPane);
        
        // Inicializar la interfaz
        selectionManager.initializeUI();
    }

    /**
     * Método que se ejecuta al hacer clic en el botón Volver
     */
    @FXML
    protected void onVolverClick() {
        // Volver a la pantalla anterior
        abrirVentana(btnVolver, "batalla.fxml");
    }
    
    /**
     * Método que se ejecuta al hacer clic en el botón Luchar
     */
    @FXML
    private void onLucharClick() {
        // Verificar si el equipo del jugador está completo
        if (selectionManager.isPlayerTeamComplete()) {
            // Si el equipo de IA no está completo, completarlo automáticamente con aleatorios
            while (!selectionManager.isAITeamComplete()) {
                selectionManager.selectRandomCharacter();
            }
            
            // Ahora intentar preparar el combate
            if (selectionManager.prepareTeamsForCombat()) {
                // Navegación a la pantalla de combate
                abrirVentanaJuego(onLucharButton, "combate.fxml");
            }
        } else {
            // Mostrar mensaje si el equipo del jugador no está completo
            selectionManager.getUIManager().showErrorMessage("Debes completar tu equipo antes de luchar");
        }
    }
}
