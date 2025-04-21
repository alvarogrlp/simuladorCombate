package es.alvarogrlp.marvelsimu.backend.selection.ui;

import java.util.List;
import java.util.Map;

import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import es.alvarogrlp.marvelsimu.backend.selection.animation.MessageDisplayManager;
import es.alvarogrlp.marvelsimu.backend.selection.animation.SelectionAnimationHandler;
import es.alvarogrlp.marvelsimu.backend.selection.logic.SelectionManager;
import eu.iamgio.animated.transition.AnimationPair;
import eu.iamgio.animated.transition.container.AnimatedVBox;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.geometry.Insets;

/**
 * Clase encargada de gestionar la interfaz de usuario para la selección de personajes
 */
public class SelectionUIManager {
    
    private AnchorPane rootPane;
    private SelectionManager selectionManager;
    
    private AnimatedVBox characterInfoContainer;
    private AnimatedVBox playerTeamContainer;
    private AnimatedVBox aiTeamContainer;
    
    private Button fightButton;
    private Button backButton;
    
    private Button btnRandom;
    private Button btnCaptain;
    private Button btnHulk;
    private Button btnIronMan;
    private Button btnSpiderMan;
    private Button btnDrStrange;
    private Button btnMagik;
    
    private VBox playerTeamBox;
    private VBox aiTeamBox;
    
    private CharacterInfoPanel infoPanel;
    private CharacterCardFactory cardFactory;
    private MessageDisplayManager messageManager;
    private SelectionAnimationHandler animationHandler;
    private TeamDisplayManager teamDisplayManager;
    
    private VBox infoContainer;
    private VBox currentInfoPanel;
    private boolean characterInfoVisible;
    private FlowPane characterSelectionPane;

    /**
     * Constructor
     * @param rootPane Panel raíz de la UI
     * @param selectionManager Gestor de selección
     */
    public SelectionUIManager(AnchorPane rootPane, SelectionManager selectionManager) {
        this.rootPane = rootPane;
        this.selectionManager = selectionManager;
        this.infoPanel = new CharacterInfoPanel(selectionManager);
        this.cardFactory = new CharacterCardFactory(selectionManager);
        this.messageManager = new MessageDisplayManager(rootPane);
        this.animationHandler = new SelectionAnimationHandler();
        this.teamDisplayManager = new TeamDisplayManager();
    }
    
    /**
     * Configura la interfaz de usuario
     */
    public void setupUI() {
        Platform.runLater(() -> {
            try {
                // Mejorar la carga del CSS globalmente
                try {
                    // Cargar el CSS usando getClass().getResource() que es más confiable
                    String cssPath = "/es/alvarogrlp/marvelsimu/seleccionPersonajes.css";
                    java.net.URL cssResource = getClass().getResource(cssPath);
                    
                    if (cssResource != null) {
                        // Aplicar al rootPane
                        rootPane.getStylesheets().add(cssResource.toExternalForm());
                        System.out.println("CSS cargado correctamente desde: " + cssResource.toExternalForm());
                    } else {
                        System.err.println("CRÍTICO: No se encontró el archivo CSS en la ruta: " + cssPath);
                        
                        // Intento alternativo usando ClassLoader
                        java.net.URL alternativeResource = getClass().getClassLoader().getResource("es/alvarogrlp/marvelsimu/seleccionPersonajes.css");
                        if (alternativeResource != null) {
                            rootPane.getStylesheets().add(alternativeResource.toExternalForm());
                            System.out.println("CSS cargado alternativamente desde: " + alternativeResource.toExternalForm());
                        } else {
                            System.err.println("CRÍTICO: También falló la carga alternativa del CSS");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("ERROR al cargar el CSS: " + e.getMessage());
                    e.printStackTrace();
                }
                
                // Localizar elementos de la UI
                findUIElements();
                
                // Configurar contenedor animado para la información del personaje
                setupCharacterInfoContainer();
                
                // Configurar VBox animados para los equipos
                setupTeamContainers();
                
                // Configurar botones y eventos
                setupCharacterButtons();
                setupFightButton();
                
                // Añadir evento para detectar clics fuera de los personajes
                setupDocumentClickListener();
                
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error inicializando UI: " + e.getMessage());
            }
        });
    }
    
    /**
     * Localiza los elementos de la UI
     */
    private void findUIElements() {
        try {
            // Buscar el contenedor de selección de personajes
            characterSelectionPane = (FlowPane) rootPane.lookup("#characterSelectionPane");
            if (characterSelectionPane == null) {
                System.err.println("ADVERTENCIA: characterSelectionPane no encontrado");
            }
            
            // Buscar los contenedores de equipos
            playerTeamBox = (VBox) rootPane.lookup("#miEquipo");
            if (playerTeamBox == null) {
                System.err.println("ADVERTENCIA: playerTeamBox no encontrado");
            }
            
            aiTeamBox = (VBox) rootPane.lookup("#equipoIA");
            if (aiTeamBox == null) {
                System.err.println("ADVERTENCIA: aiTeamBox no encontrado");
            }
            
            // Buscar el botón de luchar
            fightButton = (Button) rootPane.lookup("#onLucharButton");
            if (fightButton == null) {
                System.err.println("ADVERTENCIA: fightButton no encontrado");
            }
            
            // Limpiar referencias a botones estáticos que ya no existen en el FXML
            btnCaptain = null;
            btnHulk = null;
            btnIronMan = null;
            btnSpiderMan = null;
            btnDrStrange = null;
            btnMagik = null;
            btnRandom = null;
            
            System.out.println("UI elements initialized successfully");
        } catch (Exception e) {
            System.err.println("Error en findUIElements: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Configura el contenedor de información del personaje
     */
    private void setupCharacterInfoContainer() {
        AnimationPair animationsInfo = animationHandler.createFadeAnimation();
        
        characterInfoContainer = new AnimatedVBox(animationsInfo);
        characterInfoContainer.setAlignment(Pos.CENTER);
        characterInfoContainer.setSpacing(10);
        characterInfoContainer.setMaxWidth(580);
        characterInfoContainer.setVisible(false);
        characterInfoContainer.setOpacity(0);
        
        // Centrar considerando la posición de la barra de desplazamiento
        // La ventana es 896px de ancho, el panel 580px
        // Para centrar: (896 - 580) / 2 = 158, pero ajustamos a 153 para compensar la barra
        AnchorPane.setTopAnchor(characterInfoContainer, 50.0);
        AnchorPane.setLeftAnchor(characterInfoContainer, 153.0); 
        AnchorPane.setRightAnchor(characterInfoContainer, 163.0); // Ajustado para compensar la barra
        
        rootPane.getChildren().add(characterInfoContainer);
    }
    
    /**
     * Configura los contenedores de equipos
     */
    private void setupTeamContainers() {
        // Corregir posición de los contenedores
        teamDisplayManager.positionTeamContainers(playerTeamBox, aiTeamBox);
        
        // Crear contenedores animados
        AnimationPair slideAnimation = animationHandler.createSlideAnimation();
        
        playerTeamContainer = new AnimatedVBox(slideAnimation);
        playerTeamContainer.setAlignment(Pos.TOP_CENTER);
        playerTeamContainer.setSpacing(10);
        
        aiTeamContainer = new AnimatedVBox(slideAnimation);
        aiTeamContainer.setAlignment(Pos.TOP_CENTER);
        aiTeamContainer.setSpacing(10);
        
        // Añadir a los contenedores del FXML
        playerTeamBox.getChildren().add(playerTeamContainer);
        aiTeamBox.getChildren().add(aiTeamContainer);
    }
    
    /**
     * Configura los botones de personajes
     */
    private void setupCharacterButtons() {
        Map<String, PersonajeModel> characters = selectionManager.getCharactersMap();
        
        // Primero añadir el botón random como primer personaje
        createAndAddRandomButton();
        
        // Lista de transformaciones que deben ser excluidas
        List<String> transformationCodes = List.of(
            "magik_darkchild", 
            "thanos_gauntlet"
        );
        
        // Crear botones solo para personajes que NO son transformaciones
        for (PersonajeModel character : characters.values()) {
            String codigo = character.getNombreCodigo().toLowerCase();
            
            // Verificar si es una transformación usando la lista
            if (!character.isEsTransformacion() && !transformationCodes.contains(codigo)) {
                createAndAddCharacterButton(character);
            } else {
                System.out.println("Personaje filtrado (transformación): " + character.getNombre() + 
                                 " - Código: " + character.getNombreCodigo());
            }
        }
    }

    /**
     * Crea y añade el botón de selección aleatoria
     */
    private void createAndAddRandomButton() {
        if (characterSelectionPane == null) {
            System.err.println("Error: characterSelectionPane es null");
            return;
        }
        
        // Crear botón aleatorio
        Button randomButton = new Button();
        randomButton.getStyleClass().addAll("character-button", "random-button");
        btnRandom = randomButton; // Guardar referencia
        
        // Contenedor para la imagen con efecto de borde
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(131, 131);
        imageContainer.setMaxSize(131, 131);
        imageContainer.getStyleClass().add("random-image-container");
        
        // Configurar imagen
        try {
            ImageView imageView = new ImageView();
            Image image = new Image(getClass().getClassLoader().getResourceAsStream("images/Personajes/random.png"));
            if (!image.isError()) {
                imageView.setImage(image);
                imageView.setFitHeight(131);
                imageView.setFitWidth(131);
                imageView.setPreserveRatio(true);
                imageView.setPickOnBounds(true);
                imageContainer.getChildren().add(imageView);
                randomButton.setGraphic(imageContainer);
            } else {
                System.err.println("Error al cargar la imagen random.png");
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen random: " + e.getMessage());
        }
        
        // Configurar acción - Añadir directamente al equipo sin mostrar panel de info
        randomButton.setOnAction(e -> selectionManager.addRandomCharacterToTeam());
        
        // Tooltip
        Tooltip tooltip = new Tooltip("Añadir personaje aleatorio al equipo");
        tooltip.setShowDelay(Duration.millis(500));
        Tooltip.install(randomButton, tooltip);
        
        // Añadir al panel
        characterSelectionPane.getChildren().add(randomButton);
        
        // Aplicar animación hover
        animationHandler.applyHoverAnimation(randomButton);
    }

    /**
     * Crea y añade un botón para un personaje
     */
    private void createAndAddCharacterButton(PersonajeModel character) {
        if (characterSelectionPane == null) {
            System.err.println("Error: characterSelectionPane es null");
            return;
        }
        
        // Crear botón
        Button charButton = new Button();
        charButton.getStyleClass().add("character-button");
        
        // Contenedor para la imagen con efecto de borde
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(131, 131);
        imageContainer.setMaxSize(131, 131);
        imageContainer.getStyleClass().add("character-image-container");
        
        // Configurar imagen
        try {
            ImageView imageView = new ImageView();
            Image image = new Image(getClass().getClassLoader().getResourceAsStream(character.getImagenMiniatura()));
            if (!image.isError()) {
                imageView.setImage(image);
                imageView.setFitHeight(131);
                imageView.setFitWidth(131);
                imageView.setPreserveRatio(true);
                imageView.setPickOnBounds(true);
                imageContainer.getChildren().add(imageView);
                charButton.setGraphic(imageContainer);
            } else {
                System.err.println("Error al cargar la imagen: " + character.getImagenMiniatura());
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + e.getMessage());
        }
        
        // Configurar acción
        charButton.setOnAction(e -> {
            selectionManager.selectCharacter(character, charButton);
        });
        
        // Tooltip
        Tooltip tooltip = new Tooltip(character.getNombre());
        tooltip.setShowDelay(Duration.millis(500));
        Tooltip.install(charButton, tooltip);
        
        // Registrar botón
        selectionManager.registerDynamicButton(charButton, character);
        
        // Añadir al panel
        characterSelectionPane.getChildren().add(charButton);
        
        // Aplicar animación hover
        animationHandler.applyHoverAnimation(charButton);
    }
    
    /**
     * Configura el botón de luchar
     */
    private void setupFightButton() {
        // Inicialmente deshabilitado
        fightButton.setDisable(true);
        fightButton.setOpacity(0.5);
    }
    
    /**
     * Configura el listener para clics en el documento
     */
    private void setupDocumentClickListener() {
        rootPane.setOnMouseClicked(e -> {
            if (e.getTarget() == rootPane) {
                selectionManager.deselectCurrentCharacter();
                hideCharacterInfo();
            }
        });
    }
    
    /**
     * Muestra información detallada del personaje con animación
     * @param character Personaje a mostrar
     */
    public void showCharacterInfo(PersonajeModel character) {
        // Limpiar el contenedor
        characterInfoContainer.getChildren().clear();
        
        // Aplicar estilos directamente al contenedor
        characterInfoContainer.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-padding: 10px;"
        );
        
        // Crear y añadir el panel de información
        VBox infoPanel = this.infoPanel.createInfoPanel(character);
        characterInfoContainer.getChildren().add(infoPanel);
        
        // Guardar referencia del panel actual
        currentInfoPanel = infoPanel;
        characterInfoVisible = true;
        
        // Mostrar el contenedor
        characterInfoContainer.setVisible(true);
        characterInfoContainer.setOpacity(0); // Iniciar invisible para la animación
        characterInfoContainer.setScaleX(0.95);
        characterInfoContainer.setScaleY(0.95);
        
        // Crear animación de entrada
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), characterInfoContainer);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), characterInfoContainer);
        scaleIn.setFromX(0.95);
        scaleIn.setFromY(0.95);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        
        ParallelTransition entryAnimation = new ParallelTransition(fadeIn, scaleIn);
        entryAnimation.play();
        
        // Imprimir información de debug
        System.out.println("Panel de información mostrado para: " + character.getNombre());
    }
    
    /**
     * Oculta la información del personaje con animación
     */
    public void hideCharacterInfo() {
        if (characterInfoContainer != null && characterInfoContainer.isVisible()) {
            // Crear animación de salida
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), characterInfoContainer);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            
            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(300), characterInfoContainer);
            scaleOut.setFromX(1.0);
            scaleOut.setFromY(1.0);
            scaleOut.setToX(0.95);
            scaleOut.setToY(0.95);
            
            ParallelTransition exitAnimation = new ParallelTransition(fadeOut, scaleOut);
            
            // Al finalizar la animación, ocultar el panel
            exitAnimation.setOnFinished(e -> {
                characterInfoContainer.getChildren().clear();
                characterInfoContainer.setVisible(false);
                characterInfoVisible = false;
            });
            
            // Iniciar animación
            exitAnimation.play();
        } else {
            // Si por alguna razón no hay animación, ocultar directamente
            if (characterInfoContainer != null) {
                characterInfoContainer.getChildren().clear();
                characterInfoContainer.setVisible(false);
            }
            characterInfoVisible = false;
        }
    }
    
    /**
     * Añade un personaje a la vista del equipo de forma simple y directa
     */
    public void addCharacterToTeamDisplay(PersonajeModel character, Button sourceButton, boolean isPlayerTeam) {
        // Obtener el contenedor correcto
        AnimatedVBox container = isPlayerTeam ? playerTeamContainer : aiTeamContainer;
        
        if (container == null || character == null) {
            System.err.println("Container o personaje nulo en addCharacterToTeamDisplay");
            return;
        }
        
        // Crear tarjeta directamente con la fábrica, pasando null como botón fuente
        VBox characterCard = cardFactory.createCharacterCard(character, null, isPlayerTeam);
        
        // Configurar tamaño explícitamente
        characterCard.setPrefSize(160, 180);
        characterCard.setMaxSize(160, 180);
        characterCard.setMinSize(160, 180);
        
        // Verificación de debug
        System.out.println("Añadiendo personaje al equipo " + (isPlayerTeam ? "del jugador" : "de la IA") + ": " + character.getNombre());
        
        // Establecer el manejador de clic para permitir eliminar personajes
        setupCharacterCardClickHandler(characterCard, character, isPlayerTeam);
        
        // Añadir la tarjeta al contenedor
        container.getChildren().add(characterCard);
        
        // Asegurar que sea visible
        characterCard.setVisible(true);
        characterCard.setOpacity(1.0);
    }

    /**
     * Configura el manejador de clic para una tarjeta de personaje
     * @param card Tarjeta de personaje
     * @param character Personaje asociado
     * @param isPlayerTeam Si es del equipo del jugador
     */
    private void setupCharacterCardClickHandler(VBox card, PersonajeModel character, boolean isPlayerTeam) {
        // Permitir eliminar personajes de ambos equipos
        // Añadir clase para estilos específicos
        card.getStyleClass().add("character-card");
        
        // Configurar efecto hover
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-cursor: hand;");
            // Efecto de escala
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), card);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });
        
        card.setOnMouseExited(e -> {
            // Restaurar escala
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), card);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
        
        // Configurar evento de clic
        card.setOnMouseClicked(e -> {
            // Encontrar el botón original
            Button originalButton = findCharacterButton(character);
            
            // Habilitar el botón original
            if (originalButton != null) {
                originalButton.setDisable(false);
            }
            
            // Eliminar del equipo
            selectionManager.removeCharacterFromTeam(character, isPlayerTeam);
            
            // Mostrar mensaje de confirmación
            showInfoMessage(character.getNombre() + " eliminado del " + (isPlayerTeam ? "equipo del jugador" : "equipo IA"));
            
            // Actualizar estado del botón de luchar
            updateFightButtonState();
        });
    }

    /**
     * Encuentra el botón original para un personaje
     */
    private Button findCharacterButton(PersonajeModel character) {
        if (character == null) return null;
        
        String nombre = character.getNombre().toLowerCase();
        
        if (nombre.contains("captain") || nombre.contains("america")) {
            return btnCaptain;
        } else if (nombre.contains("hulk")) {
            return btnHulk;
        } else if (nombre.contains("iron") || nombre.contains("stark")) {
            return btnIronMan;
        } else if (nombre.contains("spider") || nombre.contains("parker")) {
            return btnSpiderMan;
        } else if (nombre.contains("strange") || nombre.contains("doctor")) {
            return btnDrStrange;
        } else if (nombre.contains("magik")) {
            return btnMagik;
        }
        
        return null;
    }

    /**
     * Actualiza el estado del botón de luchar
     */
    public void updateFightButtonState() {
        if (selectionManager.isPlayerTeamComplete() && selectionManager.isAITeamComplete()) {
            fightButton.setDisable(false);
            fightButton.setOpacity(1.0);
        } else {
            fightButton.setDisable(true);
            fightButton.setOpacity(0.5);
        }
    }

    /**
     * Anima la entrada de una tarjeta de personaje
     * @param characterCard Tarjeta a animar
     */
    private void animateAddCharacterCard(Node characterCard) {
        // Establecer propiedades iniciales
        characterCard.setOpacity(0);
        characterCard.setScaleX(0.8);
        characterCard.setScaleY(0.8);
        
        // Crear animación de aparición
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), characterCard);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1.0);
        
        // Crear animación de escala
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(500), characterCard);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        
        // Combinar las animaciones
        ParallelTransition entryAnimation = new ParallelTransition(fadeIn, scaleIn);
        
        // Iniciar animación
        entryAnimation.play();
    }
    
    /**
     * Elimina un personaje de la vista del equipo
     * @param characterCard Tarjeta de personaje a eliminar
     * @param isPlayerTeam Si es del equipo del jugador
     */
    public void removeCharacterFromDisplay(VBox characterCard, boolean isPlayerTeam) {
        if (characterCard == null) {
            return;
        }
        
        // Crear animación de salida
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), characterCard);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(300), characterCard);
        scaleOut.setFromX(1.0);
        scaleOut.setFromY(1.0);
        scaleOut.setToX(0.8);
        scaleOut.setToY(0.8);
        
        ParallelTransition exitAnimation = new ParallelTransition(fadeOut, scaleOut);
        
        // Al finalizar la animación, eliminar el nodo
        exitAnimation.setOnFinished(e -> {
            if (isPlayerTeam) {
                playerTeamContainer.getChildren().remove(characterCard);
            } else {
                aiTeamContainer.getChildren().remove(characterCard);
            }
        });
        
        // Iniciar animación
        exitAnimation.play();
    }
    
    /**
     * Elimina una tarjeta de personaje por su índice con eliminación forzada
     * @param index Índice de la tarjeta a eliminar
     * @param isPlayerTeam Si es del equipo del jugador
     */
    public void removeCharacterCardByIndex(int index, boolean isPlayerTeam) {
        try {
            AnimatedVBox container = isPlayerTeam ? playerTeamContainer : aiTeamContainer;
            
            if (index >= 0 && index < container.getChildren().size()) {
                VBox characterCard = (VBox) container.getChildren().get(index);
                
                // Asegurarnos de que la tarjeta es realmente eliminada
                // Primero la eliminamos inmediatamente del contenedor
                container.getChildren().remove(characterCard);
                
                // Luego creamos la animación de desvanecimiento para un efecto visual
                // pero trabajando con una copia de la tarjeta original
                VBox cardCopy = new VBox();
                cardCopy.getStyleClass().addAll(characterCard.getStyleClass());
                cardCopy.getChildren().addAll(characterCard.getChildren());
                cardCopy.setStyle(characterCard.getStyle());
                cardCopy.setPrefSize(characterCard.getPrefWidth(), characterCard.getPrefHeight());
                
                // Añadimos la copia para animarla
                container.getChildren().add(index, cardCopy);
                
                // Crear animación de salida
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), cardCopy);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                
                ScaleTransition scaleOut = new ScaleTransition(Duration.millis(300), cardCopy);
                scaleOut.setFromX(1.0);
                scaleOut.setFromY(1.0);
                scaleOut.setToX(0.8);
                scaleOut.setToY(0.8);
                
                ParallelTransition exitAnimation = new ParallelTransition(fadeOut, scaleOut);
                
                // Al finalizar la animación, eliminar la copia
                exitAnimation.setOnFinished(e -> {
                    // Eliminar la copia animada
                    container.getChildren().remove(cardCopy);
                    
                    // Reorganizar las tarjetas restantes para asegurar que no haya espacios
                    reorganizeTeamDisplay(isPlayerTeam);
                });
                
                // Iniciar animación
                exitAnimation.play();
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar tarjeta de personaje: " + e.getMessage());
            
            // En caso de error, forzar la eliminación directa
            forceRemoveCardByIndex(index, isPlayerTeam);
        }
    }

    /**
     * Fuerza la eliminación de una tarjeta sin animación en caso de error
     */
    private void forceRemoveCardByIndex(int index, boolean isPlayerTeam) {
        try {
            AnimatedVBox container = isPlayerTeam ? playerTeamContainer : aiTeamContainer;
            if (index >= 0 && index < container.getChildren().size()) {
                container.getChildren().remove(index);
            }
        } catch (Exception e) {
            System.err.println("Error en eliminación forzada: " + e.getMessage());
        }
    }

    /**
     * Reorganiza la visualización del equipo si es necesario
     */
    private void reorganizeTeamDisplay(boolean isPlayerTeam) {
        // Este método queda simplificado
        // Al eliminar directamente, JavaFX actualiza el layout automáticamente
    }
    
    /**
     * Habilita el botón de luchar
     */
    public void enableFightButton() {
        fightButton.setDisable(false);
        fightButton.setOpacity(1.0);
    }
    
    /**
     * Deshabilita el botón de luchar
     */
    public void disableFightButton() {
        fightButton.setDisable(true);
        fightButton.setOpacity(0.5);
    }
    
    /**
     * Limpia completamente el contenedor del equipo del jugador
     */
    public void clearPlayerTeam() {
        if (playerTeamContainer != null) {
            playerTeamContainer.getChildren().clear();
        }
    }

    /**
     * Limpia completamente el contenedor del equipo de la IA
     */
    public void clearAITeam() {
        if (aiTeamContainer != null) {
            aiTeamContainer.getChildren().clear();
        }
    }
    
    /**
     * Limpia el resaltado de todos los botones de personajes
     */
    public void clearAllButtonsHighlight() {
        try {
            // Recorrer solo los botones que existen en el flujo de personajes
            if (characterSelectionPane != null) {
                for (Node node : characterSelectionPane.getChildren()) {
                    if (node instanceof Button) {
                        Button button = (Button) node;
                        button.getStyleClass().remove("selected-character");
                    }
                }
            }
            
            // Limpiar botones específicos si existen
            if (btnCaptain != null) btnCaptain.getStyleClass().remove("selected-character");
            if (btnHulk != null) btnHulk.getStyleClass().remove("selected-character");
            if (btnIronMan != null) btnIronMan.getStyleClass().remove("selected-character");
            if (btnSpiderMan != null) btnSpiderMan.getStyleClass().remove("selected-character");
            if (btnDrStrange != null) btnDrStrange.getStyleClass().remove("selected-character");
            if (btnMagik != null) btnMagik.getStyleClass().remove("selected-character");
            if (btnRandom != null) btnRandom.getStyleClass().remove("selected-character");
        } catch (Exception e) {
            System.err.println("Error al limpiar resaltado de botones: " + e.getMessage());
        }
    }
    
    /**
     * Limpia el resaltado de un botón específico
     */
    public void clearButtonHighlight(Button button) {
        button.getStyleClass().remove("selected-character");
    }
    
    /**
     * Resalta un botón
     */
    public void highlightButton(Button button) {
        button.getStyleClass().add("selected-character");
    }
    
    /**
     * Muestra un mensaje informativo temporal
     */
    public void showInfoMessage(String message) {
        messageManager.showMessage(message, false);
    }
    
    /**
     * Muestra un mensaje de error
     */
    public void showErrorMessage(String message) {
        messageManager.showMessage(message, true);
    }
    
    /**
     * Verifica si el panel de información está visible
     */
    public boolean isCharacterInfoVisible() {
        return characterInfoContainer != null && characterInfoContainer.isVisible();
    }
    
    /**
     * Obtiene el contenedor de equipo del jugador
     */
    public AnimatedVBox getPlayerTeamContainer() {
        return playerTeamContainer;
    }
    
    /**
     * Obtiene el contenedor de equipo de la IA
     */
    public AnimatedVBox getAITeamContainer() {
        return aiTeamContainer;
    }
    
    /**
     * Obtiene el botón de luchar
     */
    public Button getFightButton() {
        return fightButton;
    }
    
    /**
     * Habilita o deshabilita todos los controles interactivos de la interfaz
     * @param enable true para habilitar, false para deshabilitar
     */
    private void enableAllControls(boolean enable) {
        if (characterSelectionPane != null) {
            for (Node node : characterSelectionPane.getChildren()) {
                if (node instanceof Button) {
                    Button button = (Button) node;
                    // No cambiar el estado de los botones ya deshabilitados por otras razones
                    if (!button.isDisabled() || enable) {
                        button.setDisable(!enable);
                        button.setOpacity(enable ? 1.0 : 0.7);
                    }
                }
            }
        }
        
        // No deshabilitamos el botón de luchar aquí ya que tiene su propia lógica
        if (backButton != null) {
            backButton.setDisable(!enable);
            backButton.setOpacity(enable ? 1.0 : 0.7);
        }
    }
    
    /**
     * Getters para los botones de personajes
     */
    public Button getCaptainButton() {
        return btnCaptain;
    }

    public Button getHulkButton() {
        return btnHulk;
    }

    public Button getIronManButton() {
        return btnIronMan;
    }

    public Button getSpiderManButton() {
        return btnSpiderMan;
    }

    public Button getDrStrangeButton() {
        return btnDrStrange;
    }

    public Button getMagikButton() {
        return btnMagik;
    }

    public Button getRandomButton() {
        return btnRandom;
    }

    // Getter para CharacterCardFactory
    public CharacterCardFactory getCardFactory() { return cardFactory; }

    /**
     * Anima la eliminación de una tarjeta de personaje por su índice
     * @param index Índice de la tarjeta a eliminar
     * @param isPlayerTeam Si es del equipo del jugador
     */
    public void animateRemoveCharacterCard(int index, boolean isPlayerTeam) {
        AnimatedVBox container = isPlayerTeam ? playerTeamContainer : aiTeamContainer;
        
        if (container == null || index < 0 || index >= container.getChildren().size()) {
            updateFightButtonState();
            return;
        }
        
        // Obtener la tarjeta a eliminar
        VBox cardToRemove = (VBox) container.getChildren().get(index);
        
        // Crear una copia exacta de la tarjeta que será animada
        VBox cardCopy = new VBox();
        cardCopy.getStyleClass().addAll(cardToRemove.getStyleClass());
        
        // Copiar los hijos (contenido) de la tarjeta original
        for (Node child : cardToRemove.getChildren()) {
            try {
                // Intentar clonar o copiar cada nodo hijo
                Node clone = CloneUtils.cloneNode(child);
                if (clone != null) {
                    cardCopy.getChildren().add(clone);
                }
            } catch (Exception e) {
                // Si no se puede clonar, simplemente añadir la referencia
                cardCopy.getChildren().add(child);
            }
        }
        
        // Copiar el tamaño y estilo
        cardCopy.setMinSize(cardToRemove.getMinWidth(), cardToRemove.getMinHeight());
        cardCopy.setPrefSize(cardToRemove.getPrefWidth(), cardToRemove.getPrefHeight());
        cardCopy.setMaxSize(cardToRemove.getMaxWidth(), cardToRemove.getMaxHeight());
        cardCopy.setStyle(cardToRemove.getStyle());
        
        // Posicionar la copia exactamente en la misma posición
        AnchorPane root = (AnchorPane) rootPane;
        
        // Convertir coordenadas del VBox al AnchorPane
        javafx.geometry.Bounds bounds = cardToRemove.localToScene(cardToRemove.getBoundsInLocal());
        javafx.geometry.Point2D point = root.sceneToLocal(bounds.getMinX(), bounds.getMinY());
        
        cardCopy.setLayoutX(point.getX());
        cardCopy.setLayoutY(point.getY());
        cardCopy.setManaged(false);
        
        // Eliminar la tarjeta original del contenedor sin animación
        container.getChildren().remove(cardToRemove);
        
        // Añadir la copia al rootPane (no al contenedor) para animarla independientemente
        root.getChildren().add(cardCopy);
        
        // Crear animación de desvanecimiento
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), cardCopy);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        // Añadir efecto de escala para hacerlo más suave
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(300), cardCopy);
        scaleOut.setFromX(1.0);
        scaleOut.setFromY(1.0);
        scaleOut.setToX(0.9);
        scaleOut.setToY(0.9);
        
        // Combinar animaciones
        ParallelTransition exitAnimation = new ParallelTransition(fadeOut, scaleOut);
        
        // Al finalizar la animación, eliminar la copia
        exitAnimation.setOnFinished(e -> {
            // Eliminar la copia del rootPane
            root.getChildren().remove(cardCopy);
            
            // Actualizar el estado del botón de luchar
            updateFightButtonState();
        });
        
        // Iniciar la animación
        exitAnimation.play();
    }

    /**
     * Utilidad para clonar nodos JavaFX (simplificada)
     */
    private static class CloneUtils {
        public static Node cloneNode(Node node) {
            if (node instanceof ImageView) {
                ImageView original = (ImageView) node;
                ImageView clone = new ImageView();
                clone.setImage(original.getImage());
                clone.setFitWidth(original.getFitWidth());
                clone.setFitHeight(original.getFitHeight());
                clone.setPreserveRatio(original.isPreserveRatio());
                return clone;
            } else if (node instanceof Label) {
                Label original = (Label) node;
                Label clone = new Label(original.getText());
                clone.setStyle(original.getStyle());
                clone.getStyleClass().addAll(original.getStyleClass());
                return clone;
            }
            // Para otros tipos de nodos, se pueden implementar clonaciones específicas
            return null;
        }
    }

    /**
     * Fuerza una actualización de la visualización del equipo sin animaciones
     * @param isPlayerTeam Si es el equipo del jugador
     */
    public void forceUpdateTeamDisplay(boolean isPlayerTeam) {
        // Esta función se llamará solo en caso de error para asegurar consistencia
        VBox container = isPlayerTeam ? playerTeamContainer : aiTeamContainer;
        List<PersonajeModel> team = isPlayerTeam ? 
            selectionManager.getTeamBuilder().getPlayerTeam() : 
            selectionManager.getTeamBuilder().getAITeam();
        
        // Limpiar el contenedor
        container.getChildren().clear();
        
        // Reconstruir la visualización desde el modelo de datos
        for (PersonajeModel character : team) {
            Button sourceButton = findCharacterButton(character);
            addCharacterToTeamDisplay(character, sourceButton, isPlayerTeam);
        }
        
        updateFightButtonState();
    }
}