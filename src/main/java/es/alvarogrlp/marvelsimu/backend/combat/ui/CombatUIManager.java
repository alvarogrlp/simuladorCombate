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
import javafx.scene.control.Tooltip;
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
    
    // Character selection dialog
    private CharacterSelectionDialog selectionDialog;
    
    public CombatUIManager(AnchorPane rootPane) {
        this.rootPane = rootPane;
        initializeReferences();
        loadBackground();
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
            
            // Verificar que todos los elementos se han encontrado
            if (playerCharacterImage == null || aiCharacterImage == null ||
                playerHealthLabel == null || aiHealthLabel == null || 
                playerHealthBar == null || aiHealthBar == null ||
                attackButton == null || changeButton == null || backButton == null ||
                attackContainer == null || meleeAttackButton == null || 
                rangedAttackButton == null || ability1Button == null || ability2Button == null ||
                turnIndicator == null || playerTeamContainer == null || aiTeamContainer == null) {
                
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
        // Obtener referencias a los labels de nombre
        Label lblNombreJugador = (Label) rootPane.lookup("#lblNombreJugador");
        Label lblNombreIA = (Label) rootPane.lookup("#lblNombreIA");
        
        // Actualizar nombres
        if (lblNombreJugador != null) lblNombreJugador.setText(playerCharacter.getNombre());
        if (lblNombreIA != null) lblNombreIA.setText(aiCharacter.getNombre());
        
        // Obtener valores de vida
        int playerHealth = playerCharacter.getVidaActual();
        int playerMaxHealth = playerCharacter.getVida();
        int aiHealth = aiCharacter.getVidaActual();
        int aiMaxHealth = aiCharacter.getVida();
        
        // Calcular porcentajes
        double playerHealthPercent = (double) playerHealth / playerMaxHealth;
        double aiHealthPercent = (double) aiHealth / aiMaxHealth;
        
        // Actualizar textos de vida con formato mejorado
        playerHealthLabel.setText(String.format("%d / %d", playerHealth, playerMaxHealth));
        aiHealthLabel.setText(String.format("%d / %d", aiHealth, aiMaxHealth));
        
        // Actualizar barras de progreso con transición suave
        playerHealthBar.setProgress(playerHealthPercent);
        aiHealthBar.setProgress(aiHealthPercent);
        
        // Aplicar estilos para vida crítica (menos del 25%)
        if (playerHealthPercent < 0.25) {
            playerHealthBar.getStyleClass().add("hp-critical");
        } else {
            playerHealthBar.getStyleClass().removeAll("hp-critical");
        }
        
        if (aiHealthPercent < 0.25) {
            aiHealthBar.getStyleClass().add("hp-critical");
        } else {
            aiHealthBar.getStyleClass().removeAll("hp-critical");
        }
        
        // Opcional: Añadir animación sutil de parpadeo cuando la vida es crítica
        if (playerHealthPercent < 0.25) {
            addPulseEffect(playerHealthLabel);
        } else {
            removePulseEffect(playerHealthLabel);
        }
        
        if (aiHealthPercent < 0.25) {
            addPulseEffect(aiHealthLabel);
        } else {
            removePulseEffect(aiHealthLabel);
        }
    }
    
    // Método auxiliar para añadir efecto de pulsación
    private void addPulseEffect(Label label) {
        if (!label.getStyleClass().contains("pulse-effect")) {
            label.getStyleClass().add("pulse-effect");
            
            // Crear efecto de pulsación con opacidad
            FadeTransition fade = new FadeTransition(Duration.millis(700), label);
            fade.setFromValue(1.0);
            fade.setToValue(0.6);
            fade.setCycleCount(javafx.animation.Animation.INDEFINITE);
            fade.setAutoReverse(true);
            fade.play();
            
            // Guardar la animación en el userData para poder detenerla después
            label.setUserData(fade);
        }
    }
    
    // Método auxiliar para quitar efecto de pulsación
    private void removePulseEffect(Label label) {
        if (label.getStyleClass().contains("pulse-effect")) {
            label.getStyleClass().remove("pulse-effect");
            
            // Detener la animación si existe
            if (label.getUserData() instanceof FadeTransition) {
                FadeTransition fade = (FadeTransition) label.getUserData();
                fade.stop();
                label.setOpacity(1.0);
            }
        }
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
        // Obtener los ataques del personaje
        AtaqueModel ataqueCC = character.getAtaquePorTipo("ACC");
        AtaqueModel ataqueAD = character.getAtaquePorTipo("AAD");
        AtaqueModel habilidad1 = character.getAtaquePorTipo("habilidad_mas_poderosa");
        AtaqueModel habilidad2 = character.getAtaquePorTipo("habilidad_caracteristica");
        
        // Obtener referencias a los botones
        Button btnMelee = (Button) rootPane.lookup("#btnMelee");
        Button btnRanged = (Button) rootPane.lookup("#btnRanged");
        Button btnAbility1 = (Button) rootPane.lookup("#btnAbility1");
        Button btnAbility2 = (Button) rootPane.lookup("#btnAbility2");
        
        // Actualizar textos y disponibilidad de los botones
        if (btnMelee != null && ataqueCC != null) {
            btnMelee.setText(ataqueCC.getNombre());
            boolean disponible = ataqueCC.estaDisponible();
            btnMelee.setDisable(!disponible);
            
            if (!disponible) {
                // Añadir información sobre por qué no está disponible
                if (ataqueCC.getCooldownActual() > 0) {
                    btnMelee.setText(ataqueCC.getNombre() + " (CD: " + ataqueCC.getCooldownActual() + ")");
                } else if (ataqueCC.getUsosMaximos() > 0 && ataqueCC.getUsosRestantes() <= 0) {
                    btnMelee.setText(ataqueCC.getNombre() + " (No usos)");
                }
                
                btnMelee.setStyle("-fx-opacity: 0.6; -fx-background-color: #555555;");
            } else {
                btnMelee.setStyle("-fx-opacity: 1.0; -fx-background-color: #4a7ba7;");
            }
        }
        
        if (btnRanged != null && ataqueAD != null) {
            btnRanged.setText(ataqueAD.getNombre());
            boolean disponible = ataqueAD.estaDisponible();
            btnRanged.setDisable(!disponible);
            
            if (!disponible) {
                // Añadir información sobre por qué no está disponible
                if (ataqueAD.getCooldownActual() > 0) {
                    btnRanged.setText(ataqueAD.getNombre() + " (CD: " + ataqueAD.getCooldownActual() + ")");
                } else if (ataqueAD.getUsosMaximos() > 0 && ataqueAD.getUsosRestantes() <= 0) {
                    btnRanged.setText(ataqueAD.getNombre() + " (No usos)");
                }
                
                btnRanged.setStyle("-fx-opacity: 0.6; -fx-background-color: #555555;");
            } else {
                btnRanged.setStyle("-fx-opacity: 1.0; -fx-background-color: #4a7ba7;");
            }
        }
        
        if (btnAbility1 != null && habilidad1 != null) {
            btnAbility1.setText(habilidad1.getNombre());
            boolean disponible = habilidad1.estaDisponible();
            btnAbility1.setDisable(!disponible);
            
            if (!disponible) {
                // Añadir información sobre por qué no está disponible
                if (habilidad1.getCooldownActual() > 0) {
                    btnAbility1.setText(habilidad1.getNombre() + " (CD: " + habilidad1.getCooldownActual() + ")");
                } else if (habilidad1.getUsosMaximos() > 0 && habilidad1.getUsosRestantes() <= 0) {
                    btnAbility1.setText(habilidad1.getNombre() + " (No usos)");
                }
                
                btnAbility1.setStyle("-fx-opacity: 0.6; -fx-background-color: #555555;");
            } else {
                btnAbility1.setStyle("-fx-opacity: 1.0; -fx-background-color: #4a7ba7;");
            }
        }
        
        if (btnAbility2 != null && habilidad2 != null) {
            btnAbility2.setText(habilidad2.getNombre());
            boolean disponible = habilidad2.estaDisponible();
            btnAbility2.setDisable(!disponible);
            
            if (!disponible) {
                // Añadir información sobre por qué no está disponible
                if (habilidad2.getCooldownActual() > 0) {
                    btnAbility2.setText(habilidad2.getNombre() + " (CD: " + habilidad2.getCooldownActual() + ")");
                } else if (habilidad2.getUsosMaximos() > 0 && habilidad2.getUsosRestantes() <= 0) {
                    btnAbility2.setText(habilidad2.getNombre() + " (No usos)");
                }
                
                btnAbility2.setStyle("-fx-opacity: 0.6; -fx-background-color: #555555;");
            } else {
                btnAbility2.setStyle("-fx-opacity: 1.0; -fx-background-color: #4a7ba7;");
            }
        }
    }
    
    /**
     * Formatea el tipo de ataque para mostrarlo más legible
     */
    private String formatTipoAtaque(String tipo) {
        if (tipo == null) return "Físico";
        
        switch (tipo.toLowerCase()) {
            case "fisico": return "Físico";
            case "magico": return "Mágico";
            case "fisico_penetrante": return "Físico Penetrante";
            case "magico_penetrante": return "Mágico Penetrante";
            case "daño_verdadero": return "Daño Verdadero";
            default: return tipo;
        }
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
            // Habilitar los botones principales
            attackButton.setDisable(false);
            changeButton.setDisable(false);
            backButton.setDisable(false);
            
            // Mantener los botones de ataque deshabilitados hasta que se presione el botón de atacar
            attackContainer.setVisible(false);
            
            // Asegurarnos de que todos los botones de ataque están listos para usarse
            meleeAttackButton.setDisable(false);
            rangedAttackButton.setDisable(false);
            
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
    
    /**
     * Muestra las opciones de ataque y prepara los botones
     */
    public void showAttackOptions() {
        // Primero verificar que es el turno del jugador
        if (rootPane.getUserData() instanceof CombatManager) {
            CombatManager manager = (CombatManager) rootPane.getUserData();
            if (!manager.getTurnManager().isPlayerTurn()) {
                return; // No mostrar opciones si no es el turno del jugador
            }
        }
        
        attackContainer.setVisible(true);
        attackButton.setDisable(true);
        changeButton.setDisable(true);
        backButton.setDisable(true);
        
        // Asegurarnos de habilitar los botones de ataque
        meleeAttackButton.setDisable(false);
        rangedAttackButton.setDisable(false);
        
        // Los botones de habilidad se actualizan según disponibilidad
        PersonajeModel playerCharacter = getCurrentPlayerCharacter();
        if (playerCharacter != null) {
            // Verificar disponibilidad de habilidades usando el nuevo modelo
            AtaqueModel habilidad1 = playerCharacter.getAtaquePorTipo("habilidad_mas_poderosa");
            AtaqueModel habilidad2 = playerCharacter.getAtaquePorTipo("habilidad_caracteristica");
            
            if (habilidad1 != null) {
                ability1Button.setDisable(habilidad1.getUsosRestantes() <= 0);
            } else {
                // Si no está en el nuevo modelo, usar el modelo de compatibilidad
                ability1Button.setDisable(playerCharacter.getHabilidad1Poder() <= 0);
            }
            
            if (habilidad2 != null) {
                ability2Button.setDisable(habilidad2.getUsosRestantes() <= 0);
            } else {
                // Si no está en el nuevo modelo, usar el modelo de compatibilidad
                ability2Button.setDisable(playerCharacter.getHabilidad2Poder() <= 0);
            }
        }
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
}