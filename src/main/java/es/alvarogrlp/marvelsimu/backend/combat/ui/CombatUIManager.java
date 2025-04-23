package es.alvarogrlp.marvelsimu.backend.combat.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import es.alvarogrlp.marvelsimu.backend.combat.logic.CombatManager;
import es.alvarogrlp.marvelsimu.backend.model.AtaqueModel;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import es.alvarogrlp.marvelsimu.backend.util.AlertUtils;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class CombatUIManager {
    
    private AnchorPane rootPane;
    
    // UI elements references
    private ImageView playerCharacterImage;
    private ImageView aiCharacterImage;
    private Label playerHealthLabel;
    private Label aiHealthLabel;
    private ProgressBar playerHealthBar;
    private ProgressBar aiHealthBar;
    private Button attackButton;
    private Button changeButton;
    private Button backButton;
    private HBox attackContainer;
    private Button meleeAttackButton;
    private Button rangedAttackButton;
    private Button ability1Button;
    private Button ability2Button;
    private Label turnIndicator;
    private VBox playerTeamContainer;
    private VBox aiTeamContainer;
    
    // Añadir estos campos a la sección de variables de clase
    private List<PersonajeModel> playerCharacters;
    private List<PersonajeModel> aiCharacters;
    private int playerCharacterIndex;
    private int aiCharacterIndex;
    
    // Agregar referencias para etiquetas de nombres
    private Label playerNameLabel;
    private Label aiNameLabel;
    
    // Character selection dialog
    private CharacterSelectionDialog selectionDialog;
    
    public CombatUIManager(AnchorPane rootPane) {
        this.rootPane = rootPane;
        initializeReferences();
        loadBackground();
        
        // Importante: Esto debe ejecutarse DESPUÉS de que se haya establecido el userData del rootPane
        // con la instancia de CombatManager en el constructor de CombatManager.
        // Por lo tanto, mejor hacerlo bajo demanda.
    }
    
    private void initializeReferences() {
        try {
            playerCharacterImage = (ImageView) rootPane.lookup("#imgPersonajeJugador");
            aiCharacterImage = (ImageView) rootPane.lookup("#imgPersonajeIA");
            playerHealthLabel = (Label) rootPane.lookup("#lblVidaJugador");
            aiHealthLabel = (Label) rootPane.lookup("#lblVidaIA");
            playerHealthBar = (ProgressBar) rootPane.lookup("#barraVidaJugador");
            aiHealthBar = (ProgressBar) rootPane.lookup("#barraVidaIA");
            attackButton = (Button) rootPane.lookup("#btnAtacar");
            changeButton = (Button) rootPane.lookup("#btnCambiar");
            backButton = (Button) rootPane.lookup("#btnVolver");
            attackContainer = (HBox) rootPane.lookup("#contenedorAtaques");
            meleeAttackButton = (Button) rootPane.lookup("#btnAtaqueMelee");
            rangedAttackButton = (Button) rootPane.lookup("#btnAtaqueLejano");
            ability1Button = (Button) rootPane.lookup("#btnHabilidad1");
            ability2Button = (Button) rootPane.lookup("#btnHabilidad2");
            turnIndicator = (Label) rootPane.lookup("#lblTurno");
            playerTeamContainer = (VBox) rootPane.lookup("#equipoJugador");
            aiTeamContainer = (VBox) rootPane.lookup("#equipoIA");
            
            // Inicializar referencias a las etiquetas de nombres
            playerNameLabel = (Label) rootPane.lookup("#lblNombreJugador");
            aiNameLabel = (Label) rootPane.lookup("#lblNombreIA");
            
            // Verificar que todos los elementos se han encontrado
            if (playerCharacterImage == null || aiCharacterImage == null ||
                playerHealthLabel == null || aiHealthLabel == null || 
                playerHealthBar == null || aiHealthBar == null ||
                attackButton == null || changeButton == null || backButton == null ||
                attackContainer == null || meleeAttackButton == null || 
                rangedAttackButton == null || ability1Button == null || ability2Button == null ||
                turnIndicator == null || playerTeamContainer == null || aiTeamContainer == null ||
                playerNameLabel == null || aiNameLabel == null) {
                
                // Imprimir qué elementos son nulos para depuración
                System.err.println("Elementos nulos:");
                if (playerCharacterImage == null) System.err.println("- playerCharacterImage");
                if (aiCharacterImage == null) System.err.println("- aiCharacterImage");
                if (playerHealthLabel == null) System.err.println("- playerHealthLabel");
                if (aiHealthLabel == null) System.err.println("- aiHealthLabel");
                if (playerHealthBar == null) System.err.println("- playerHealthBar");
                if (aiHealthBar == null) System.err.println("- aiHealthBar");
                if (attackButton == null) System.err.println("- attackButton");
                if (changeButton == null) System.err.println("- changeButton");
                if (backButton == null) System.err.println("- backButton");
                if (attackContainer == null) System.err.println("- attackContainer");
                if (meleeAttackButton == null) System.err.println("- meleeAttackButton");
                if (rangedAttackButton == null) System.err.println("- rangedAttackButton");
                if (ability1Button == null) System.err.println("- ability1Button");
                if (ability2Button == null) System.err.println("- ability2Button");
                if (turnIndicator == null) System.err.println("- turnIndicator");
                if (playerTeamContainer == null) System.err.println("- playerTeamContainer");
                if (aiTeamContainer == null) System.err.println("- aiTeamContainer");
                
                // Añadir comprobaciones para etiquetas de nombres
                if (playerNameLabel == null) System.err.println("- playerNameLabel");
                if (aiNameLabel == null) System.err.println("- aiNameLabel");
                
                throw new RuntimeException("No se pudieron localizar todos los elementos de la UI");
            }
            
            // Inicializar el diálogo de selección
            selectionDialog = new CharacterSelectionDialog(rootPane);
            
        } catch (Exception e) {
            System.err.println("Error inicializando referencias UI: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.mostrarError("Error", "No se pudo inicializar la interfaz de combate");
        }
    }
    
    private void setupEventHandlers() {
        // Verificar que tenemos referencia al CombatManager
        if (rootPane.getUserData() instanceof CombatManager) {
            CombatManager combatManager = (CombatManager) rootPane.getUserData();
            
            // Configurar el botón de atacar como toggle para mostrar/ocultar opciones
            attackButton.setOnAction(e -> {
                System.out.println("Clic en botón atacar/cancelar - Estado contenedor: " + 
                                 (attackContainer.isVisible() ? "visible" : "oculto"));
                
                if (attackContainer.isVisible()) {
                    // Si el menú ya está visible, ocultarlo (funcionalidad de cancelar)
                    attackContainer.setVisible(false);
                    attackButton.setText("Atacar");
                    changeButton.setDisable(false);
                    backButton.setDisable(false);
                    System.out.println("Ocultando opciones de ataque");
                } else {
                    // Si el menú está oculto, mostrarlo
                    showAttackOptions();
                }
            });
            
            // Establecer handlers para los botones de ataque
            meleeAttackButton.setOnAction(e -> {
                System.out.println("Clic en ataque melee");
                if (combatManager.getTurnManager().isPlayerTurn()) {
                    combatManager.playerAttack("melee");
                }
            });
            
            rangedAttackButton.setOnAction(e -> {
                System.out.println("Clic en ataque a distancia");
                if (combatManager.getTurnManager().isPlayerTurn()) {
                    combatManager.playerAttack("lejano");
                }
            });
            
            ability1Button.setOnAction(e -> {
                System.out.println("Clic en habilidad 1");
                if (combatManager.getTurnManager().isPlayerTurn()) {
                    combatManager.playerAttack("habilidad1");
                }
            });
            
            ability2Button.setOnAction(e -> {
                System.out.println("Clic en habilidad 2");
                if (combatManager.getTurnManager().isPlayerTurn()) {
                    combatManager.playerAttack("habilidad2");
                }
            });
        }
    }
    
    private void loadBackground() {
        try {
            ImageView backgroundImage = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("images/Fondos/limbo.png")));
            backgroundImage.setFitWidth(896);
            backgroundImage.setFitHeight(810);
            backgroundImage.setPreserveRatio(false);
            
            rootPane.getChildren().add(0, backgroundImage);
        } catch (Exception e) {
            System.err.println("Error cargando fondo: " + e.getMessage());
        }
    }
    
    /**
     * Oculta temporalmente el personaje del jugador para transiciones
     */
    public void hidePlayerCharacter() {
        ImageView playerImage = (ImageView) rootPane.lookup("#imgPersonajeJugador");
        if (playerImage != null) {
            playerImage.setVisible(false);
            // Importante: eliminar cualquier efecto o clase que indique que está derrotado
            playerImage.getStyleClass().removeAll("character-defeated");
            playerImage.setEffect(null);
            playerImage.setOpacity(1.0);
            playerImage.setTranslateY(0); // Resetear cualquier transformación
        }
    }

    /**
     * Muestra el personaje del jugador después de una transición
     */
    public void showPlayerCharacter() {
        ImageView playerImage = (ImageView) rootPane.lookup("#imgPersonajeJugador");
        if (playerImage != null) {
            // Asegurarse de que no tenga efectos de "derrotado"
            playerImage.getStyleClass().removeAll("character-defeated");
            playerImage.setEffect(null);
            playerImage.setOpacity(1.0);
            playerImage.setVisible(true);
            
            // Aplicar animación de entrada
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), playerImage);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }
    }

    /**
     * Oculta temporalmente el personaje de la IA para transiciones
     */
    public void hideAICharacter() {
        ImageView aiImage = (ImageView) rootPane.lookup("#imgPersonajeIA");
        if (aiImage != null) {
            // Limpiar cualquier efecto o clase de derrota antes de ocultar
            aiImage.setEffect(null);
            aiImage.getStyleClass().removeAll("character-defeated");
            aiImage.setOpacity(1.0);
            aiImage.setTranslateY(0); // Resetear cualquier transformación
            aiImage.setVisible(false);
        }
    }

    /**
     * Muestra el personaje de la IA después de una transición
     */
    public void showAICharacter() {
        ImageView aiImage = (ImageView) rootPane.lookup("#imgPersonajeIA");
        if (aiImage != null) {
            // Asegurar que no tenga efectos visuales de derrota
            aiImage.setEffect(null);
            aiImage.getStyleClass().removeAll("character-defeated");
            aiImage.setOpacity(1.0);
            aiImage.setVisible(true);
            
            // Aplicar animación de entrada
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), aiImage);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }
    }

    /**
     * Actualiza las vistas de personajes, limpiando efectos visuales primero
     */
    public void updateCharacterViews(
        PersonajeModel playerCharacter, 
        PersonajeModel aiCharacter,
        List<PersonajeModel> playerTeam,
        List<PersonajeModel> aiTeam,
        int playerIndex,
        int aiIndex) {
        
        // Guardar referencias para uso interno
        this.playerCharacters = new ArrayList<>(playerTeam);
        this.aiCharacters = new ArrayList<>(aiTeam);
        this.playerCharacterIndex = playerIndex;
        this.aiCharacterIndex = aiIndex;
        
        // Limpiar efectos visuales primero
        ImageView playerCharacterImage = (ImageView) rootPane.lookup("#imgPersonajeJugador");
        ImageView aiCharacterImage = (ImageView) rootPane.lookup("#imgPersonajeIA");
        
        if (playerCharacterImage != null) {
            playerCharacterImage.getStyleClass().removeAll("character-defeated");
            playerCharacterImage.setEffect(null);
            playerCharacterImage.setOpacity(1.0);
        }
        
        if (aiCharacterImage != null) {
            aiCharacterImage.getStyleClass().removeAll("character-defeated");
            aiCharacterImage.setEffect(null);
            aiCharacterImage.setOpacity(1.0);
        }
        
        // Cargar imágenes
        if (playerCharacterImage != null && aiCharacterImage != null) {
            try {
                // Cargar imágenes de los personajes
                Image playerImage = loadImageFromMultipleSources(playerCharacter.getImagenCombate());
                Image aiImage = loadImageFromMultipleSources(aiCharacter.getImagenCombate());
                
                if (playerImage != null) {
                    playerCharacterImage.setImage(playerImage);
                    playerCharacterImage.setScaleX(1);
                }
                
                if (aiImage != null) {
                    aiCharacterImage.setImage(aiImage);
                    aiCharacterImage.setScaleX(-1);
                }
            } catch (Exception e) {
                System.err.println("Error al cargar imágenes: " + e.getMessage());
            }
        }
        
        // Actualizar nombres de personajes
        if (playerNameLabel != null) {
            playerNameLabel.setText(playerCharacter.getNombre());
        }
        
        if (aiNameLabel != null) {
            aiNameLabel.setText(aiCharacter.getNombre());
        }
        
        // Actualizar barras de vida
        updateCharacterHealth(playerCharacter, aiCharacter);
        
        // Actualizar miniaturas del equipo
        updateTeamThumbnails(playerTeam, aiTeam, playerIndex, aiIndex);
        
        // Actualizar los botones de ataque
        updateAttackButtons(playerCharacter);
    }
    
    private String mapCharacterCodeToFile(String nombreCodigo) {
        if (nombreCodigo.contains("hulk")) return "hulk";
        if (nombreCodigo.contains("spider")) return "spiderman";
        if (nombreCodigo.contains("iron")) return "ironman";
        if (nombreCodigo.contains("cap") || nombreCodigo.contains("amer")) return "captainamerica";
        if (nombreCodigo.contains("strange") || nombreCodigo.contains("doctor")) return "doctorstrange";
        if (nombreCodigo.contains("magik")) return "magik";
        
        System.err.println("No se pudo mapear el código: " + nombreCodigo);
        return "default";
    }
    
    /**
     * Carga una imagen desde una ruta específica
     */
    private Image loadImageFromMultipleSources(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            System.err.println("Ruta de imagen vacía");
            return loadDefaultImage();
        }
        
        try {
            // Simplemente cargar la imagen directamente
            InputStream is = getClass().getClassLoader().getResourceAsStream(imagePath);
            if (is != null) {
                return new Image(is);
            }
            
            // Si no encuentra la imagen, mostrar error y usar imagen por defecto
            System.err.println("No se encontró la imagen: " + imagePath);
            return loadDefaultImage();
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + e.getMessage());
            return loadDefaultImage();
        }
    }
    
    /**
     * Carga una imagen por defecto según el tipo
     */
    private Image loadDefaultImage() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("images/Personajes/random.png");
            if (is != null) {
                return new Image(is);
            }
            return new WritableImage(100, 100);
        } catch (Exception e) {
            return new WritableImage(100, 100);
        }
    }
    
    public void updateCharacterHealth(PersonajeModel playerCharacter, PersonajeModel aiCharacter) {
        // Barra de vida del jugador
        double playerHealthPercent = Math.max(0, Math.min(1.0, (double) playerCharacter.getVidaActual() / playerCharacter.getVida()));
        playerHealthBar.setProgress(playerHealthPercent);
        playerHealthLabel.setText(playerCharacter.getVidaActual() + "/" + playerCharacter.getVida());
        
        // Barra de vida de la IA
        double aiHealthPercent = Math.max(0, Math.min(1.0, (double) aiCharacter.getVidaActual() / aiCharacter.getVida()));
        aiHealthBar.setProgress(aiHealthPercent);
        aiHealthLabel.setText(aiCharacter.getVidaActual() + "/" + aiCharacter.getVida());
    }
    
    public void updateTeamThumbnails(
            List<PersonajeModel> playerTeam, 
            List<PersonajeModel> aiTeam, 
            int playerIndex, 
            int aiIndex) {
        
        playerTeamContainer.getChildren().clear();
        aiTeamContainer.getChildren().clear();
        
        // Miniaturas equipo jugador
        for (int i = 0; i < playerTeam.size(); i++) {
            PersonajeModel character = playerTeam.get(i);
            ImageView thumbnail = new ImageView(loadImageFromMultipleSources(character.getImagenMiniatura()));
            thumbnail.setFitWidth(50);
            thumbnail.setFitHeight(50);
            thumbnail.setPreserveRatio(true);
            
            VBox container = new VBox(thumbnail);
            container.setAlignment(Pos.CENTER);
            container.getStyleClass().add("team-character");
            
            if (i == playerIndex) {
                container.getStyleClass().add("team-character-active");
            }
            
            if (character.isDerrotado()) {
                container.getStyleClass().add("character-defeated");
            }
            
            playerTeamContainer.getChildren().add(container);
        }
        
        // Miniaturas equipo IA
        for (int i = 0; i < aiTeam.size(); i++) {
            PersonajeModel character = aiTeam.get(i);
            ImageView thumbnail = new ImageView(loadImageFromMultipleSources(character.getImagenMiniatura()));
            thumbnail.setFitWidth(50);
            thumbnail.setFitHeight(50);
            thumbnail.setPreserveRatio(true);
            
            VBox container = new VBox(thumbnail);
            container.setAlignment(Pos.CENTER);
            container.getStyleClass().add("enemy-team-character");
            
            if (i == aiIndex) {
                container.getStyleClass().add("enemy-character-active");
            }
            
            if (character.isDerrotado()) {
                container.getStyleClass().add("character-defeated");
            }
            
            aiTeamContainer.getChildren().add(container);
        }
    }
    
    /**
     * Actualiza los botones de ataque con la información del personaje actual
     */
    public void updateAttackButtons(PersonajeModel character) {
        if (character == null) {
            System.err.println("No se puede actualizar botones de ataque: personaje nulo");
            return;
        }
        
        System.out.println("Actualizando botones para: " + character.getNombre());
        
        // DIAGNÓSTICO: Mostrar todos los ataques disponibles
        List<AtaqueModel> ataques = character.getAtaques();
        System.out.println("Ataques disponibles (" + ataques.size() + "):");
        for (AtaqueModel ataque : ataques) {
            System.out.println("  - " + ataque.getNombre() + 
                              " (Tipo: " + ataque.getTipo() + 
                              ", TipoClave: " + ataque.getTipoAtaqueClave() + 
                              ", Código: " + ataque.getCodigo() + ")");
        }
        
        // 1) ATAQUE MELEE - buscar por tipo y luego por código como fallback
        AtaqueModel ataqueCC = character.getAtaquePorTipo("ACC");
        if (ataqueCC == null) {
            // Buscar directamente por código si no se encuentra por tipo
            for (AtaqueModel ataque : ataques) {
                if (ataque.getCodigo() != null && ataque.getCodigo().endsWith("_melee")) {
                    ataqueCC = ataque;
                    // Establecer tipo para futuras búsquedas
                    ataqueCC.setTipo("ACC");
                    break;
                }
            }
        }
        
        if (ataqueCC != null) {
            meleeAttackButton.setText(ataqueCC.getNombre());
            meleeAttackButton.setDisable(!ataqueCC.estaDisponible());
            System.out.println("Botón melee actualizado: " + ataqueCC.getNombre());
        } else {
            meleeAttackButton.setText("Ataque melee");
            System.out.println("¡ADVERTENCIA! No se encontró ataque melee");
        }
        
        // 2) ATAQUE A DISTANCIA - similar al melee
        AtaqueModel ataqueAD = character.getAtaquePorTipo("AAD");
        if (ataqueAD == null) {
            for (AtaqueModel ataque : ataques) {
                if (ataque.getCodigo() != null && ataque.getCodigo().endsWith("_range")) {
                    ataqueAD = ataque;
                    ataqueAD.setTipo("AAD");
                    break;
                }
            }
        }
        
        if (ataqueAD != null) {
            rangedAttackButton.setText(ataqueAD.getNombre());
            rangedAttackButton.setDisable(!ataqueAD.estaDisponible());
            System.out.println("Botón ranged actualizado: " + ataqueAD.getNombre());
        } else {
            rangedAttackButton.setText("Ataque a distancia");
            System.out.println("¡ADVERTENCIA! No se encontró ataque a distancia");
        }
        
        // 3) HABILIDAD 1 - buscar por tipo y luego por código
        AtaqueModel hab1 = character.getAtaquePorTipo("habilidad_mas_poderosa");
        if (hab1 == null) {
            for (AtaqueModel ataque : ataques) {
                if (ataque.getCodigo() != null && ataque.getCodigo().endsWith("_hab1")) {
                    hab1 = ataque;
                    hab1.setTipo("habilidad_mas_poderosa");
                    break;
                }
            }
        }
        
        if (hab1 != null) {
            ability1Button.setText(hab1.getNombre());
            ability1Button.setDisable(!hab1.estaDisponible());
            System.out.println("Botón habilidad 1 actualizado: " + hab1.getNombre());
        } else {
            ability1Button.setText("Habilidad 1");
            System.out.println("¡ADVERTENCIA! No se encontró habilidad 1");
        }
        
        // 4) HABILIDAD 2 - similar a habilidad 1
        AtaqueModel hab2 = character.getAtaquePorTipo("habilidad_caracteristica");
        if (hab2 == null) {
            for (AtaqueModel ataque : ataques) {
                if (ataque.getCodigo() != null && ataque.getCodigo().endsWith("_hab2")) {
                    hab2 = ataque;
                    hab2.setTipo("habilidad_caracteristica");
                    break;
                }
            }
        }
        
        if (hab2 != null) {
            ability2Button.setText(hab2.getNombre());
            ability2Button.setDisable(!hab2.estaDisponible());
            System.out.println("Botón habilidad 2 actualizado: " + hab2.getNombre());
        } else {
            ability2Button.setText("Habilidad 2");
            System.out.println("¡ADVERTENCIA! No se encontró habilidad 2");
        }
        
        // Forzar una actualización visual para garantizar que se vean los cambios
        meleeAttackButton.applyCss();
        rangedAttackButton.applyCss();
        ability1Button.applyCss();
        ability2Button.applyCss();
    }
    
    /**
     * Ejecutar este método para forzar la carga correcta de los nombres de ataques
     */
    private void forzarCargaNombresAtaques() {
        PersonajeModel playerCharacter = getCurrentPlayerCharacter();
        if (playerCharacter != null) {
            System.out.println("\n=== FORZANDO CARGA DE ATAQUES PARA: " + playerCharacter.getNombre() + " ===");
            
            // 1) ATAQUE MELEE - buscar tanto por tipo como por código
            AtaqueModel ataqueCC = null;
            // Primero intentar por tipo
            ataqueCC = playerCharacter.getAtaquePorTipo("ACC");
            if (ataqueCC == null) {
                // Luego intentar buscar por tipoAtaqueClave
                for (AtaqueModel ataque : playerCharacter.getAtaques()) {
                    if ("ACC".equals(ataque.getTipoAtaqueClave())) {
                        ataqueCC = ataque;
                        ataque.setTipo("ACC"); // Corregir el tipo para futuras búsquedas
                        break;
                    }
                }
            }
            // Si aún no se encuentra, buscar por código
            if (ataqueCC == null) {
                for (AtaqueModel ataque : playerCharacter.getAtaques()) {
                    if (ataque.getCodigo() != null && ataque.getCodigo().endsWith("_melee")) {
                        ataqueCC = ataque;
                        ataque.setTipo("ACC"); // Corregir el tipo
                        break;
                    }
                }
            }
            
            if (ataqueCC != null) {
                System.out.println("Melee encontrado: " + ataqueCC.getNombre());
                meleeAttackButton.setText(ataqueCC.getNombre());
                meleeAttackButton.setDisable(false);
            } else {
                System.out.println("ERROR: No se encontró ataque melee por ningún método");
                meleeAttackButton.setText("Ataque básico");
            }
            
            // 2) ATAQUE A DISTANCIA - igual que el melee
            AtaqueModel ataqueAD = null;
            // Primero intentar por tipo
            ataqueAD = playerCharacter.getAtaquePorTipo("AAD");
            if (ataqueAD == null) {
                // Luego intentar buscar por tipoAtaqueClave
                for (AtaqueModel ataque : playerCharacter.getAtaques()) {
                    if ("AAD".equals(ataque.getTipoAtaqueClave())) {
                        ataqueAD = ataque;
                        ataque.setTipo("AAD"); // Corregir el tipo para futuras búsquedas
                        break;
                    }
                }
            }
            // Si aún no se encuentra, buscar por código
            if (ataqueAD == null) {
                for (AtaqueModel ataque : playerCharacter.getAtaques()) {
                    if (ataque.getCodigo() != null && ataque.getCodigo().endsWith("_range")) {
                        ataqueAD = ataque;
                        ataque.setTipo("AAD"); // Corregir el tipo
                        break;
                    }
                }
            }
            
            if (ataqueAD != null) {
                System.out.println("Ranged encontrado: " + ataqueAD.getNombre());
                rangedAttackButton.setText(ataqueAD.getNombre());
                rangedAttackButton.setDisable(false);
            } else {
                System.out.println("ERROR: No se encontró ataque a distancia por ningún método");
                rangedAttackButton.setText("Ataque distancia");
            }
            
            // 3) HABILIDAD 1
            AtaqueModel hab1 = null;
            // Primero intentar por tipo
            hab1 = playerCharacter.getAtaquePorTipo("habilidad_mas_poderosa");
            if (hab1 == null) {
                // Luego intentar buscar por tipoAtaqueClave
                for (AtaqueModel ataque : playerCharacter.getAtaques()) {
                    if ("habilidad_mas_poderosa".equals(ataque.getTipoAtaqueClave())) {
                        hab1 = ataque;
                        ataque.setTipo("habilidad_mas_poderosa");
                        break;
                    }
                }
            }
            // Si aún no se encuentra, buscar por código
            if (hab1 == null) {
                for (AtaqueModel ataque : playerCharacter.getAtaques()) {
                    if (ataque.getCodigo() != null && ataque.getCodigo().endsWith("_hab1")) {
                        hab1 = ataque;
                        ataque.setTipo("habilidad_mas_poderosa");
                        break;
                    }
                }
            }
            
            if (hab1 != null) {
                System.out.println("Habilidad 1 encontrada: " + hab1.getNombre());
                ability1Button.setText(hab1.getNombre());
                ability1Button.setDisable(false);
            } else {
                System.out.println("ERROR: No se encontró habilidad 1 por ningún método");
                ability1Button.setText("Habilidad 1");
            }
            
            // 4) HABILIDAD 2
            AtaqueModel hab2 = null;
            // Primero intentar por tipo
            hab2 = playerCharacter.getAtaquePorTipo("habilidad_caracteristica");
            if (hab2 == null) {
                // Luego intentar buscar por tipoAtaqueClave
                for (AtaqueModel ataque : playerCharacter.getAtaques()) {
                    if ("habilidad_caracteristica".equals(ataque.getTipoAtaqueClave())) {
                        hab2 = ataque;
                        ataque.setTipo("habilidad_caracteristica");
                        break;
                    }
                }
            }
            // Si aún no se encuentra, buscar por código
            if (hab2 == null) {
                for (AtaqueModel ataque : playerCharacter.getAtaques()) {
                    if (ataque.getCodigo() != null && ataque.getCodigo().endsWith("_hab2")) {
                        hab2 = ataque;
                        ataque.setTipo("habilidad_caracteristica");
                        break;
                    }
                }
            }
            
            if (hab2 != null) {
                System.out.println("Habilidad 2 encontrada: " + hab2.getNombre());
                ability2Button.setText(hab2.getNombre());
                ability2Button.setDisable(false);
            } else {
                System.out.println("ERROR: No se encontró habilidad 2 por ningún método");
                ability2Button.setText("Habilidad 2");
            }
            
            // Información adicional de diagnóstico
            System.out.println("\nListado de todos los ataques:");
            for (AtaqueModel ataque : playerCharacter.getAtaques()) {
                System.out.println(String.format(
                    "Ataque: %s, Tipo: %s, Código: %s, TipoAtaqueClave: %s", 
                    ataque.getNombre(), 
                    ataque.getTipo(), 
                    ataque.getCodigo(),
                    ataque.getTipoAtaqueClave()
                ));
            }
            
            // Forzar actualización visual
            meleeAttackButton.applyCss();
            rangedAttackButton.applyCss();
            ability1Button.applyCss();
            ability2Button.applyCss();
            
            System.out.println("=== FIN DE FORZADO DE CARGA ===\n");
        } else {
            System.err.println("ERROR: No se pudo obtener el personaje actual para forzar la carga de ataques");
        }
    }
    
    public void showAttackOptions() {
        attackContainer.setVisible(true);
        // No deshabilitar el botón de atacar, para permitir cerrarlo
        attackButton.setText("Cancelar");
        changeButton.setDisable(true);
        backButton.setDisable(true);
        
        // Obtener personaje actual
        PersonajeModel playerCharacter = getCurrentPlayerCharacter();
        if (playerCharacter == null) {
            System.err.println("Error: No se pudo obtener el personaje actual");
            return;
        }
        
        System.out.println("Mostrando opciones para: " + playerCharacter.getNombre());
        
        // FORZAR habilitación de todos los botones de ataque primero
        meleeAttackButton.setDisable(false);
        rangedAttackButton.setDisable(false);
        ability1Button.setDisable(false);
        ability2Button.setDisable(false);
        
        // USAR NUESTRO NUEVO MÉTODO DE DIAGNÓSTICO Y CARGA FORZADA
        forzarCargaNombresAtaques();
        
        // Establecer manejadores de eventos si no se hizo antes
        if (meleeAttackButton.getOnAction() == null) {
            setupEventHandlers();
        }
        
        // Forzar traer el contenedor al frente
        attackContainer.toFront();
    }
    
    public void hideAttackOptions() {
        attackContainer.setVisible(false);
        attackButton.setText("Atacar");
        attackButton.setDisable(false);
        changeButton.setDisable(false);
        backButton.setDisable(false);
        
        System.out.println("Opciones de ataque ocultadas");
    }
    
    public void showCharacterSelection(CombatManager manager) {
        selectionDialog.showDialog(manager.getPlayerCharacters(), manager.getPlayerCharacterIndex(), index -> {
            manager.changePlayerCharacter(index);
        });
    }
    
    public void showForceCharacterSelection(CombatManager manager) {
        selectionDialog.showForceDialog(manager.getPlayerCharacters(), manager.getPlayerCharacterIndex(), index -> {
            manager.changePlayerCharacter(index);
        });
    }
    
    public void showCombatEndScreen(boolean victory) {
        Rectangle darkBackground = new Rectangle(0, 0, rootPane.getWidth(), rootPane.getHeight());
        darkBackground.setFill(Color.rgb(0, 0, 0, 0.7));
        
        VBox endContainer = new VBox(30);
        endContainer.setAlignment(Pos.CENTER);
        
        Text endText = new Text(victory ? "¡VICTORIA!" : "¡DERROTA!");
        endText.setFont(Font.font("System", FontWeight.BOLD, 72));
        endText.setFill(victory ? Color.GOLD : Color.RED);
        endText.setTextAlignment(TextAlignment.CENTER);
        endText.setStroke(Color.BLACK);
        endText.setStrokeWidth(2);
        
        Button btnReturnToSelection = new Button("Volver a Selección de Personajes");
        btnReturnToSelection.getStyleClass().add("action-button");
        btnReturnToSelection.setPrefWidth(300);
        btnReturnToSelection.setOnAction(e -> {
            // Navegar a la pantalla de selección
            navigateToCharacterSelection();
        });
        
        endContainer.getChildren().addAll(endText, btnReturnToSelection);
        endContainer.setOpacity(0);
        
        rootPane.getChildren().addAll(darkBackground, endContainer);
        
        darkBackground.toFront();
        endContainer.toFront();
        
        double centerX = rootPane.getWidth() / 2;
        double centerY = rootPane.getHeight() / 2;
        endContainer.setLayoutX(centerX - 300);
        endContainer.setLayoutY(centerY - 150);
        
        FadeTransition fadeInBackground = new FadeTransition(Duration.millis(500), darkBackground);
        fadeInBackground.setFromValue(0);
        fadeInBackground.setToValue(1);
        
        FadeTransition fadeInContainer = new FadeTransition(Duration.millis(800), endContainer);
        fadeInContainer.setFromValue(0);
        fadeInContainer.setToValue(1);
        
        ScaleTransition pulse = new ScaleTransition(Duration.millis(800), endText);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.1);
        pulse.setToY(1.1);
        pulse.setCycleCount(javafx.animation.Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        
        ParallelTransition entryAnimation = new ParallelTransition(fadeInBackground, fadeInContainer);
        entryAnimation.setOnFinished(e -> pulse.play());
        entryAnimation.play();
    }
    
    private void navigateToCharacterSelection() {
        try {
            // Crear una instancia del navegador y navegar a la página de selección
            // Esto es una simplificación - implementa la lógica real de navegación según tu estructura
            backButton.fire(); // Usa el botón existente para navegar
        } catch (Exception e) {
            System.err.println("Error navegando a selección de personajes: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.mostrarError("Error", "No se pudo volver a la pantalla de selección");
        }
    }
    
    public void setPlayerTurnIndicator(boolean isPlayerTurn) {
        turnIndicator.setText(isPlayerTurn ? "TU TURNO" : "TURNO DE IA");
        
        if (isPlayerTurn) {
            turnIndicator.getStyleClass().remove("enemy-turn");
            turnIndicator.getStyleClass().add("player-turn");
        } else {
            turnIndicator.getStyleClass().remove("player-turn");
            turnIndicator.getStyleClass().add("enemy-turn");
        }
    }
    
    /**
     * Deshabilita todos los controles del jugador durante el turno de la IA
     */
    public void disablePlayerControls() {
        // Ocultar el contenedor de ataques si está visible
        attackContainer.setVisible(false);
        
        // Deshabilitar todos los botones de acción
        attackButton.setDisable(true);
        changeButton.setDisable(true);
        backButton.setDisable(true);
        
        // También deshabilitar los botones de ataque específicos para evitar cualquier interacción
        meleeAttackButton.setDisable(true);
        rangedAttackButton.setDisable(true);
        ability1Button.setDisable(true);
        ability2Button.setDisable(true);
    }
    
    /**
     * Habilita los controles principales del jugador durante su turno
     */
    public void enablePlayerControls() {
        // Forzar actualización en el hilo de UI
        Platform.runLater(() -> {
            // Ocultar menú de ataques si estuviera visible
            attackContainer.setVisible(false);
            
            // Restaurar texto original del botón de atacar
            attackButton.setText("Atacar");
            
            // Habilitar los botones principales
            attackButton.setDisable(false);
            changeButton.setDisable(false);
            backButton.setDisable(false);
            
            // Actualizar estado de botones de habilidad según disponibilidad
            PersonajeModel playerCharacter = getCurrentPlayerCharacter();
            if (playerCharacter != null) {
                // Verificar disponibilidad de habilidades usando el nuevo modelo
                AtaqueModel habilidad1 = playerCharacter.getAtaquePorTipo("habilidad_mas_poderosa");
                AtaqueModel habilidad2 = playerCharacter.getAtaquePorTipo("habilidad_caracteristica");
                
                if (habilidad1 != null) {
                    ability1Button.setDisable(habilidad1.getUsosRestantes() <= 0);
                } else {
                    // Si no está en el nuevo modelo, verificar con el modelo de compatibilidad
                    ability1Button.setDisable(playerCharacter.getHabilidad1Poder() <= 0);
                }
                
                if (habilidad2 != null) {
                    ability2Button.setDisable(habilidad2.getUsosRestantes() <= 0);
                } else {
                    // Si no está en el nuevo modelo, verificar con el modelo de compatibilidad
                    ability2Button.setDisable(playerCharacter.getHabilidad2Poder() <= 0);
                }
            }
            
            // Log para diagnosticar
            System.out.println("Controles del jugador habilitados");
        });
    }
    
    // Getters para acceder a los botones y conectarlos con los event handlers
    public Button getAttackButton() {
        return attackButton;
    }
    
    public Button getChangeButton() {
        return changeButton;
    }
    
    public Button getBackButton() {
        return backButton;
    }
    
    public Button getMeleeAttackButton() {
        return meleeAttackButton;
    }
    
    public Button getRangedAttackButton() {
        return rangedAttackButton;
    }
    
    public Button getAbility1Button() {
        return ability1Button;
    }
    
    public Button getAbility2Button() {
        return ability2Button;
    }
    
    /**
     * Marca la interfaz para indicar que el personaje del jugador ha sido derrotado
     */
    public void markPlayerDefeated() {
        // Obtener la imagen y el contenedor del personaje
        ImageView playerImage = (ImageView) rootPane.lookup("#imgPersonajeJugador");
        Node playerContainer = rootPane.lookup("#playerCharacterContainer");
        
        if (playerImage != null) {
            // Aplicar estilos CSS para indicar derrota
            playerImage.getStyleClass().add("character-defeated");
        }
        
        if (playerContainer != null) {
            playerContainer.getStyleClass().add("character-defeated-container");
        }
        
        // Actualizar las miniaturas del equipo
        updateTeamThumbnails(
            getPlayerCharacters(),
            getAICharacters(),
            getPlayerCharacterIndex(),
            getAICharacterIndex()
        );
    }

    /**
     * Marca la interfaz para indicar que el personaje de la IA ha sido derrotado
     */
    public void markAIDefeated() {
        // Obtener la imagen y el contenedor del personaje
        ImageView aiImage = (ImageView) rootPane.lookup("#imgPersonajeIA");
        Node aiContainer = rootPane.lookup("#aiCharacterContainer");
        
        if (aiImage != null) {
            // Aplicar estilos CSS para indicar derrota
            aiImage.getStyleClass().add("character-defeated");
        }
        
        if (aiContainer != null) {
            aiContainer.getStyleClass().add("character-defeated-container");
        }
        
        // Actualizar las miniaturas del equipo
        updateTeamThumbnails(
            getPlayerCharacters(),
            getAICharacters(),
            getPlayerCharacterIndex(),
            getAICharacterIndex()
        );
    }

    /**
     * Obtiene el personaje actual del jugador desde el CombatManager
     * @return El personaje actual del jugador, o null si no se puede obtener
     */
    private PersonajeModel getCurrentPlayerCharacter() {
        try {
            if (rootPane.getUserData() instanceof CombatManager) {
                CombatManager manager = (CombatManager) rootPane.getUserData();
                return manager.getCurrentPlayerCharacter();
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error obteniendo el personaje actual: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene la lista de personajes del jugador desde el CombatManager
     * @return Lista de personajes del jugador, o una lista vacía si no se puede obtener
     */
    private List<PersonajeModel> getPlayerCharacters() {
        try {
            if (rootPane.getUserData() instanceof CombatManager) {
                CombatManager manager = (CombatManager) rootPane.getUserData();
                return manager.getPlayerCharacters();
            }
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error obteniendo la lista de personajes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene la lista de personajes de la IA desde el CombatManager
     * @return Lista de personajes de la IA, o una lista vacía si no se puede obtener
     */
    private List<PersonajeModel> getAICharacters() {
        try {
            if (rootPane.getUserData() instanceof CombatManager) {
                CombatManager manager = (CombatManager) rootPane.getUserData();
                return manager.getAICharacters();
            }
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error obteniendo la lista de personajes IA: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene el índice del personaje actual del jugador
     * @return Índice del personaje actual del jugador, o 0 si no se puede obtener
     */
    private int getPlayerCharacterIndex() {
        try {
            if (rootPane.getUserData() instanceof CombatManager) {
                CombatManager manager = (CombatManager) rootPane.getUserData();
                return manager.getPlayerCharacterIndex();
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Error obteniendo el índice del personaje actual: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Obtiene el índice del personaje actual de la IA
     * @return Índice del personaje actual de la IA, o 0 si no se puede obtener
     */
    private int getAICharacterIndex() {
        try {
            if (rootPane.getUserData() instanceof CombatManager) {
                CombatManager manager = (CombatManager) rootPane.getUserData();
                return manager.getAICharacterIndex();
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Error obteniendo el índice del personaje IA: " + e.getMessage());
            return 0;
        }
    }

    // Añadir este método para depuración

    private void diagnosticoAtaques(PersonajeModel character) {
        System.out.println("\n=== DIAGNÓSTICO DE ATAQUES ===");
        System.out.println("Personaje: " + character.getNombre() + " (" + character.getNombreCodigo() + ")");
        
        System.out.println("Ataques disponibles:");
        for (AtaqueModel ataque : character.getAtaques()) {
            System.out.println(" - " + ataque.getNombre() + " (Tipo: " + ataque.getTipo() + ", Código: " + ataque.getCodigo() + ")");
        }
        
        System.out.println("Búsqueda por tipo:");
        System.out.println(" - ACC: " + (character.getAtaquePorTipo("ACC") != null ? character.getAtaquePorTipo("ACC").getNombre() : "null"));
        System.out.println(" - AAD: " + (character.getAtaquePorTipo("AAD") != null ? character.getAtaquePorTipo("AAD").getNombre() : "null"));
        System.out.println(" - habilidad_mas_poderosa: " + (character.getAtaquePorTipo("habilidad_mas_poderosa") != null ? 
                                   character.getAtaquePorTipo("habilidad_mas_poderosa").getNombre() : "null"));
        System.out.println(" - habilidad_caracteristica: " + (character.getAtaquePorTipo("habilidad_caracteristica") != null ? 
                                   character.getAtaquePorTipo("habilidad_caracteristica").getNombre() : "null"));
        
        System.out.println("Estado de botones:");
        System.out.println(" - meleeAttackButton: " + meleeAttackButton.getText() + " (Disabled: " + meleeAttackButton.isDisabled() + ")");
        System.out.println(" - rangedAttackButton: " + rangedAttackButton.getText() + " (Disabled: " + rangedAttackButton.isDisabled() + ")");
        System.out.println(" - ability1Button: " + ability1Button.getText() + " (Disabled: " + ability1Button.isDisabled() + ")");
        System.out.println(" - ability2Button: " + ability2Button.getText() + " (Disabled: " + ability2Button.isDisabled() + ")");
        System.out.println("===========================\n");
    }
}