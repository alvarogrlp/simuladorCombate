package es.alvarogrlp.marvelsimu.backend.controller;

import es.alvarogrlp.marvelsimu.backend.controller.abstracts.AbstractController;
import es.alvarogrlp.marvelsimu.backend.util.AlertUtils;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.util.Duration;

// Importaciones para animaciones
import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import animatefx.animation.SlideInUp;
import animatefx.animation.SlideOutDown;
import eu.iamgio.animated.transition.AnimationPair;
import eu.iamgio.animated.transition.container.AnimatedVBox;
import eu.iamgio.animated.binding.Animated;
import eu.iamgio.animated.binding.presets.AnimatedScale;
import eu.iamgio.animated.common.Curve;

/**
 * Controlador para la pantalla de selección de personajes
 */
public class SeleccionPersonajesController extends AbstractController {

    // Elementos de la interfaz
    @FXML
    private Text txtTitulo;
    @FXML
    private Button btnCaptain;
    @FXML
    private Button btnHulk;
    @FXML
    private Button btnIronMan;
    @FXML
    private Button btnSpiderMan;
    @FXML
    private Button btnDrStrange;
    @FXML
    private Button btnMagik;
    @FXML
    private Button btnVolver;
    @FXML
    private ImageView fondo;
    @FXML
    private VBox miEquipo;
    @FXML
    private VBox equipoIA;
    @FXML
    private AnchorPane rootPane;

    // Contenedor animado para la información del personaje
    private AnimatedVBox characterInfoContainer;
    
    // Personaje actualmente seleccionado
    private String currentCharacter;
    private Button currentButton;
    private String currentImagePath;
    
    // Control de selección de equipos
    private int seleccionesRealizadas = 0;
    private final int MAX_PERSONAJES_EQUIPO = 3;
    
    // VBox animados para los equipos
    private AnimatedVBox equipoJugadorAnimado;
    private AnimatedVBox equipoIAAnimado;

    /**
     * Inicializa la pantalla
     */
    @FXML
    public void initialize() {
        // Aplicar el tema actual utilizando el método unificado del AbstractController
        applyCurrentTheme(btnCaptain, fondo, null);
        
        Platform.runLater(() -> {
            try {
                // Crear contenedor animado para la información del personaje - Cambiar animación a FadeIn/FadeOut
                AnimationPair animationsInfo = new AnimationPair(new FadeIn(), new FadeOut())
                    .setSpeed(1.5, 1.5);  // Velocidad reducida
                
                characterInfoContainer = new AnimatedVBox(animationsInfo);
                characterInfoContainer.setAlignment(Pos.CENTER);
                characterInfoContainer.setSpacing(15);
                characterInfoContainer.getStyleClass().add("character-info-container");
                characterInfoContainer.setMaxWidth(350);
                characterInfoContainer.setVisible(false); // Ocultarlo inicialmente
                
                // Añadir al AnchorPane
                AnchorPane.setBottomAnchor(characterInfoContainer, 100.0);
                AnchorPane.setLeftAnchor(characterInfoContainer, 273.0);
                AnchorPane.setRightAnchor(characterInfoContainer, 273.0);
                rootPane.getChildren().add(characterInfoContainer);
                
                // Corregir la posición del miEquipo y equipoIA
                if (miEquipo != null) {
                    miEquipo.setLayoutX(14.0);
                    miEquipo.setLayoutY(79.0);
                }
                
                if (equipoIA != null) {
                    equipoIA.setLayoutX(674.0); // Ajustar para que esté a la derecha
                    equipoIA.setLayoutY(79.0);
                    equipoIA.setVisible(true); // Asegurarnos de que sea visible
                }
                
                // Configurar VBox animados para los equipos
                // Para el equipo del jugador: animación desde la izquierda
                equipoJugadorAnimado = new AnimatedVBox(
                    new AnimationPair(new SlideInUp(), new SlideOutDown())
                        .setSpeed(1.5, 1.5)
                );
                equipoJugadorAnimado.setAlignment(Pos.TOP_CENTER);
                equipoJugadorAnimado.setSpacing(10);
                
                // Para el equipo IA: animación desde la derecha
                equipoIAAnimado = new AnimatedVBox(
                    new AnimationPair(new SlideInUp(), new SlideOutDown())
                        .setSpeed(1.5, 1.5)
                );
                equipoIAAnimado.setAlignment(Pos.TOP_CENTER);
                equipoIAAnimado.setSpacing(10);
                
                // Añadir los VBox animados dentro de los VBox del FXML
                miEquipo.getChildren().add(equipoJugadorAnimado);
                equipoIA.getChildren().add(equipoIAAnimado);
                
                // Configurar eventos y animaciones
                configurarBotonesPersonajes();
                aplicarAnimacionesBotones();
                
                // Añadir evento para detectar clics fuera de los personajes
                rootPane.setOnMouseClicked(e -> {
                    // Verificar que el clic fue directamente en el rootPane
                    if (e.getTarget() == rootPane) {
                        deseleccionarPersonajeActual();
                    }
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error inicializando animaciones: " + e.getMessage());
            }
        });
    }

    /**
     * Configura un listener de hover para animar el botón
     */
    private void configurarAnimacionHover(Button button) {
        // Animación simple de hover usando escalado
        button.setOnMouseEntered(e -> {
            button.setScaleX(1.1);
            button.setScaleY(1.1);
        });
        
        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
        
        // Añadir transiciones suaves
        button.setStyle("-fx-transition: all 200ms ease-out;");
    }

    /**
     * Aplica animaciones a los botones de personajes
     */
    private void aplicarAnimacionesBotones() {
        configurarAnimacionHover(btnCaptain);
        configurarAnimacionHover(btnHulk);
        configurarAnimacionHover(btnIronMan);
        configurarAnimacionHover(btnSpiderMan);
        configurarAnimacionHover(btnDrStrange);
        configurarAnimacionHover(btnMagik);
    }

    /**
     * Configura los eventos para los botones de selección de personajes
     */
    private void configurarBotonesPersonajes() {
        // Corregir rutas de imágenes: eliminar la barra inicial y usar las imágenes existentes
        btnCaptain.setOnAction(e -> seleccionarPersonaje("Capitán América", btnCaptain, "images/Personajes/captain-america.png"));
        btnHulk.setOnAction(e -> seleccionarPersonaje("Hulk", btnHulk, "images/Personajes/hulk.png"));
        btnIronMan.setOnAction(e -> seleccionarPersonaje("Iron Man", btnIronMan, "images/Personajes/ironman.png"));
        btnSpiderMan.setOnAction(e -> seleccionarPersonaje("Spider-Man", btnSpiderMan, "images/Personajes/spiderman.png"));
        btnDrStrange.setOnAction(e -> seleccionarPersonaje("Doctor Strange", btnDrStrange, "images/Personajes/doctor-strange.png"));
        btnMagik.setOnAction(e -> seleccionarPersonaje("Magik", btnMagik, "images/Personajes/magik.png"));
    }

    /**
     * Método llamado cuando se selecciona un personaje
     */
    private void seleccionarPersonaje(String nombrePersonaje, Button boton, String imagePath) {
        // Si hay un personaje seleccionado, no permitir seleccionar otro
        // El usuario debe deseleccionar primero usando la X en la ventana de descripción
        if (currentCharacter != null) {
            return;
        }
        
        // Si el personaje ya está seleccionado (botón deshabilitado), no hacemos nada
        if (!boton.isDisable()) {
            // Quitar resaltado de TODOS los botones primero
            quitarResaltadoTodosLosBotones();
            
            // Actualizar el personaje actual
            currentCharacter = nombrePersonaje;
            currentButton = boton;
            currentImagePath = imagePath;
            
            // Resaltar el botón actual
            boton.getStyleClass().add("selected-character");
            
            // Limpiar el contenedor (activará la animación de salida)
            characterInfoContainer.getChildren().clear();
            
            // Crear y añadir el panel de información (activará la animación de entrada)
            VBox infoPanel = crearPanelInformacion(nombrePersonaje, imagePath);
            characterInfoContainer.getChildren().add(infoPanel);
            
            // Mostrar el contenedor si no estaba visible
            if (!characterInfoContainer.isVisible()) {
                characterInfoContainer.setVisible(true);
            }
        }
    }

    /**
     * Quita el resaltado de todos los botones de personajes
     */
    private void quitarResaltadoTodosLosBotones() {
        btnCaptain.getStyleClass().remove("selected-character");
        btnHulk.getStyleClass().remove("selected-character");
        btnIronMan.getStyleClass().remove("selected-character");
        btnSpiderMan.getStyleClass().remove("selected-character");
        btnDrStrange.getStyleClass().remove("selected-character");
        btnMagik.getStyleClass().remove("selected-character");
    }
    
    /**
     * Crea el panel de información del personaje con botón de cierre
     */
    private VBox crearPanelInformacion(String nombre, String imagePath) {
        // Crear un contenedor para el título y el botón de cerrar
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPrefWidth(330);
        headerBox.setSpacing(10);
        
        // Título del personaje (alineado a la izquierda pero con espacio para el botón)
        Text titulo = new Text(nombre);
        titulo.getStyleClass().add("character-title");
        
        // Contenedor espaciador para empujar el botón a la derecha
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        // Botón de cerrar (X) con texto mejorado
        Button btnCerrar = new Button("X"); // Cambiado de "✕" a "X" más visible
        btnCerrar.getStyleClass().add("close-button");
        btnCerrar.setOnAction(e -> deseleccionarPersonajeActual());
        
        // Añadir elementos al header
        headerBox.getChildren().addAll(titulo, spacer, btnCerrar);
        
        // Contenedor principal
        VBox panel = new VBox(15);
        panel.setAlignment(Pos.CENTER);
        panel.setPrefWidth(330);
        
        // Añadir el header
        panel.getChildren().add(headerBox);
        
        // Imagen del personaje
        try {
            Image imagen = new Image(getClass().getClassLoader().getResourceAsStream(imagePath));
            if (!imagen.isError()) {
                ImageView imageView = new ImageView(imagen);
                imageView.setFitHeight(180);
                imageView.setPreserveRatio(true);
                imageView.getStyleClass().add("character-image");
                panel.getChildren().add(imageView);
            } else {
                System.err.println("Error al cargar la imagen: " + imagePath);
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen: " + imagePath + ", error: " + e.getMessage());
        }
        
        // El resto del código permanece igual
        HBox stats = new HBox(20);
        stats.setAlignment(Pos.CENTER);
        
        VBox fuerza = crearEstadistica("Fuerza", getEstadistica(nombre, "fuerza"));
        VBox agilidad = crearEstadistica("Agilidad", getEstadistica(nombre, "agilidad"));
        VBox poder = crearEstadistica("Poder", getEstadistica(nombre, "poder"));
        
        stats.getChildren().addAll(fuerza, agilidad, poder);
        
        // Descripción
        Text descripcion = new Text(getDescripcion(nombre));
        descripcion.getStyleClass().add("character-description");
        descripcion.setWrappingWidth(300);
        
        // Botón para confirmar selección
        Button confirmar = new Button("Confirmar selección");
        confirmar.getStyleClass().add("confirm-button");
        confirmar.setOnAction(e -> confirmarSeleccion());
        
        // Añadir todo al panel
        panel.getChildren().addAll(stats, descripcion, confirmar);
        return panel;
    }

    /**
     * Confirma la selección del personaje actual
     */
    private void confirmarSeleccion() {
        if (currentButton == null || currentCharacter == null) {
            return;
        }
        
        // Verificar si ya se han realizado todas las selecciones
        if (seleccionesRealizadas >= MAX_PERSONAJES_EQUIPO * 2) {
            return;
        }
        
        // Crear una copia visual del personaje para añadirlo al equipo
        VBox personajeSeleccionado = crearVistaPersonajeSeleccionado();
        
        // Determinar a qué equipo va el personaje
        if (seleccionesRealizadas < MAX_PERSONAJES_EQUIPO) {
            // Va al equipo del jugador
            equipoJugadorAnimado.getChildren().add(personajeSeleccionado);
        } else {
            // Va al equipo IA
            equipoIAAnimado.getChildren().add(personajeSeleccionado);
        }
        
        // Deshabilitar el botón para que no se pueda seleccionar de nuevo
        currentButton.setDisable(true);
        currentButton.setOpacity(0.5);
        
        // Limpiar la selección actual
        characterInfoContainer.getChildren().clear();
        characterInfoContainer.setVisible(false); // Ocultar el contenedor
        
        // Incrementar contador de selecciones
        seleccionesRealizadas++;
        
        // Limpiar referencias actuales
        currentCharacter = null;
        currentButton = null;
        currentImagePath = null;
        
        // Si ya se completaron ambos equipos, activar el botón para continuar
        if (seleccionesRealizadas == MAX_PERSONAJES_EQUIPO * 2) {
            btnVolver.setText("Comenzar Batalla");
            btnVolver.getStyleClass().add("battle-ready-button");
        }
    }
    
    /**
     * Crea una vista visual del personaje seleccionado para mostrar en el equipo
     */
    private VBox crearVistaPersonajeSeleccionado() {
        VBox contenedor = new VBox(5);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.getStyleClass().add("selected-character-container");
        contenedor.setMaxWidth(180);
        
        // Guardar referencia al botón que representa este personaje
        Button botonOriginal = currentButton;
        String personaje = currentCharacter;
        
        // Determinar si este personaje irá al equipo del jugador (para control de eliminación)
        boolean esEquipoJugador = seleccionesRealizadas < MAX_PERSONAJES_EQUIPO;
        
        // Nombre del personaje
        Text nombre = new Text(currentCharacter);
        nombre.getStyleClass().add("selected-character-name");
        contenedor.getChildren().add(nombre);
        
        // Imagen del personaje
        try {
            Image imagen = new Image(getClass().getClassLoader().getResourceAsStream(currentImagePath));
            if (!imagen.isError()) {
                ImageView imageView = new ImageView(imagen);
                imageView.setFitHeight(120);
                imageView.setPreserveRatio(true);
                contenedor.getChildren().add(0, imageView);
            } else {
                System.err.println("Error al cargar la imagen para el equipo: " + currentImagePath);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen para el equipo: " + e.getMessage());
        }
        
        // Guardar la selección actual para referencia futura (para regla de eliminación)
        final int seleccionActual = seleccionesRealizadas;
        
        // Añadir evento para eliminar el personaje al hacer clic
        contenedor.setOnMouseClicked(e -> {
            // Verificar si se permite eliminar este personaje según el estado actual
            if (esEquipoJugador && seleccionesRealizadas > MAX_PERSONAJES_EQUIPO) {
                // Si es del equipo del jugador y ya estamos eligiendo IA, mostrar mensaje de error
                mostrarMensajeError("Debes eliminar primero los personajes del equipo IA");
                return;
            }
            
            eliminarPersonajeDeEquipo(contenedor, botonOriginal, personaje);
            e.consume(); // Evitar propagación del evento
        });
        
        // Añadir cursor para indicar acción
        contenedor.setCursor(javafx.scene.Cursor.HAND);
        
        return contenedor;
    }

    /**
     * Muestra un mensaje flotante en el centro de la pantalla
     * @param mensaje Texto del mensaje
     * @param esError Si true, se aplican estilos y animaciones de error
     */
    private void mostrarMensaje(String mensaje, boolean esError) {
        Text mensajeText = new Text(mensaje);
        mensajeText.getStyleClass().add("mensaje-flotante");
        
        if (esError) {
            mensajeText.getStyleClass().add("mensaje-error");
        }
        
        // Posición fija en la pantalla en coordenadas absolutas
        mensajeText.setLayoutX(448 - 150); // Centro de la pantalla (896/2) menos la mitad del ancho del texto
        mensajeText.setLayoutY(640); // Posición Y fija, encima del botón volver
        
        // Configuración del texto
        mensajeText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        mensajeText.setWrappingWidth(300);
        
        // Añadir al rootPane
        rootPane.getChildren().add(mensajeText);
        
        // Si es un error, añadir animación de sacudida
        if (esError) {
            TranslateTransition shake = new TranslateTransition(Duration.millis(50), mensajeText);
            shake.setFromX(-5);
            shake.setToX(5);
            shake.setCycleCount(6);
            shake.setAutoReverse(true);
            shake.play();
        }
        
        // Desvanecimiento
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(esError ? 2.5 : 2.0), mensajeText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        if (esError) {
            fadeOut.setDelay(Duration.seconds(1));
        }
        fadeOut.setOnFinished(e -> rootPane.getChildren().remove(mensajeText));
        fadeOut.play();
    }

    private void mostrarMensajeError(String mensaje) {
        mostrarMensaje(mensaje, true);
    }

    /**
     * Elimina un personaje del equipo y lo vuelve a habilitar para selección
     */
    private void eliminarPersonajeDeEquipo(VBox contenedor, Button botonOriginal, String nombrePersonaje) {
        // Determinar de qué equipo eliminar
        if (equipoJugadorAnimado.getChildren().contains(contenedor)) {
            equipoJugadorAnimado.getChildren().remove(contenedor);
            seleccionesRealizadas--;
        } else if (equipoIAAnimado.getChildren().contains(contenedor)) {
            equipoIAAnimado.getChildren().remove(contenedor);
            seleccionesRealizadas--;
        }
        
        // Volver a habilitar el botón original
        if (botonOriginal != null) {
            botonOriginal.setDisable(false);
            botonOriginal.setOpacity(1.0);
        }
        
        // Si estaba en "Comenzar Batalla", volver a "Volver"
        if (seleccionesRealizadas < MAX_PERSONAJES_EQUIPO * 2 && 
            "Comenzar Batalla".equals(btnVolver.getText())) {
            btnVolver.setText("Volver");
            btnVolver.getStyleClass().remove("battle-ready-button");
        }
        
        // Mostrar feedback visual
        mostrarMensajeFlotante(nombrePersonaje + " eliminado del equipo");
    }

    /**
     * Muestra un mensaje flotante temporal en el centro de la pantalla sobre el botón Volver
     */
    private void mostrarMensajeFlotante(String mensaje) {
        mostrarMensaje(mensaje, false);
    }
    
    /**
     * Métodos auxiliares para obtener información de los personajes
     */
    private VBox crearEstadistica(String nombre, int valor) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        
        Text valorTexto = new Text(String.valueOf(valor));
        valorTexto.getStyleClass().add("stat-value");
        
        Text nombreTexto = new Text(nombre);
        nombreTexto.getStyleClass().add("stat-name");
        
        box.getChildren().addAll(valorTexto, nombreTexto);
        return box;
    }
    
    /**
     * Método que se ejecuta al hacer clic en el botón Volver / Comenzar Batalla
     */
    @FXML
    protected void onVolverClick() {
        // Si ya hemos completado los equipos y el botón dice "Comenzar Batalla"
        if (seleccionesRealizadas == MAX_PERSONAJES_EQUIPO * 2) {
            // Aquí iríamos a la pantalla de batalla
            abrirVentana(btnVolver, "batalla.fxml");
        } else {
            // Sino, volvemos a la pantalla anterior
            abrirVentana(btnVolver, "batalla.fxml");
        }
    }
    
    // Métodos auxiliares para obtener datos de personajes (usa los que ya tenías implementados)
    private int getEstadistica(String nombrePersonaje, String tipo) {
        // En un sistema real, estos valores vendrían de una base de datos
        switch (nombrePersonaje) {
            case "Capitán América":
                if ("fuerza".equals(tipo)) return 85;
                if ("agilidad".equals(tipo)) return 80;
                if ("poder".equals(tipo)) return 70;
                break;
            case "Hulk":
                if ("fuerza".equals(tipo)) return 100;
                if ("agilidad".equals(tipo)) return 65;
                if ("poder".equals(tipo)) return 85;
                break;
            case "Iron Man":
                if ("fuerza".equals(tipo)) return 80;
                if ("agilidad".equals(tipo)) return 75;
                if ("poder".equals(tipo)) return 90;
                break;
            case "Spider-Man":
                if ("fuerza".equals(tipo)) return 75;
                if ("agilidad".equals(tipo)) return 95;
                if ("poder".equals(tipo)) return 70;
                break;
            case "Doctor Strange":
                if ("fuerza".equals(tipo)) return 60;
                if ("agilidad".equals(tipo)) return 75;
                if ("poder".equals(tipo)) return 100;
                break;
            case "Magik":
                if ("fuerza".equals(tipo)) return 70;
                if ("agilidad".equals(tipo)) return 80;
                if ("poder".equals(tipo)) return 90;
                break;
        }
        return 50; // Valor predeterminado
    }
    
    /**
     * Obtiene la descripción de un personaje
     */
    private String getDescripcion(String nombrePersonaje) {
        switch (nombrePersonaje) {
            case "Capitán América":
                return "Steve Rogers, el Primer Vengador y símbolo del heroísmo americano. Sus habilidades incluyen fuerza sobrehumana, agilidad y un escudo indestructible.";
            case "Hulk":
                return "Bruce Banner, tras exponerse a radiación gamma, se transforma en Hulk cuando se enfada. Posee fuerza ilimitada que aumenta con su ira.";
            case "Iron Man":
                return "Tony Stark, genio inventor y multimillonario. Su armadura de alta tecnología le proporciona fuerza, vuelo y un arsenal de armas avanzadas.";
            case "Spider-Man":
                return "Peter Parker, que tras ser mordido por una araña radiactiva, obtuvo fuerza, agilidad y sentido arácnido. Con gran responsabilidad.";
            case "Doctor Strange":
                return "Stephen Strange, antiguo cirujano convertido en Hechicero Supremo. Maestro de las artes místicas y protector de la realidad.";
            case "Magik":
                return "Illyana Rasputin, hermana de Coloso y mutante con poderes místicos. Portadora de la Soulsword y gobernante del reino demoníaco de Limbo.";
            default:
                return "Un poderoso héroe del universo Marvel.";
        }
    }

    /**
     * Deselecciona el personaje actual
     */
    private void deseleccionarPersonajeActual() {
        if (currentButton != null) {
            currentButton.getStyleClass().remove("selected-character");
        }
        
        // Ocultar y limpiar el panel de información
        if (characterInfoContainer != null) {
            characterInfoContainer.getChildren().clear();
            characterInfoContainer.setVisible(false);
        }
        
        // Resetear variables
        currentCharacter = null;
        currentButton = null;
        currentImagePath = null;
    }
}
