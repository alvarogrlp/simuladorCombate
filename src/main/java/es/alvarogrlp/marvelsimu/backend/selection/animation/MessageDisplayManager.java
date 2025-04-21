package es.alvarogrlp.marvelsimu.backend.selection.animation;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * Clase encargada de mostrar mensajes en la pantalla de selección
 */
public class MessageDisplayManager {
    
    private AnchorPane rootPane;
    
    public MessageDisplayManager(AnchorPane rootPane) {
        this.rootPane = rootPane;
    }
    
    /**
     * Muestra un mensaje en la pantalla
     * @param message Texto del mensaje
     * @param isError Si es un mensaje de error
     */
    public void showMessage(String message, boolean isError) {
        // Crear el texto del mensaje con estilos directos
        Text messageText = new Text(message);
        messageText.setStyle(
            "-fx-fill: " + (isError ? "#ff5252" : "#ffffff") + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Roboto', sans-serif;" +
            "-fx-effect: dropshadow(gaussian, " + (isError ? "rgba(255, 0, 0, 0.4)" : "rgba(0, 0, 0, 0.7)") + ", 3, 0.5, 0, 1);"
        );
        
        // Contenedor con fondo
        StackPane messageContainer = new StackPane();
        messageContainer.getChildren().add(messageText);
        messageContainer.setPadding(new Insets(10, 15, 10, 15));
        messageContainer.setStyle(
            "-fx-background-color: " + (isError ? "rgba(80, 20, 20, 0.9)" : "rgba(40, 70, 120, 0.9)") + ";" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: " + (isError ? "rgba(255, 100, 100, 0.7)" : "rgba(100, 150, 255, 0.7)") + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 8px;"
        );
        
        // Posicionar mucho más abajo en la pantalla - debajo del selector de personajes
        messageContainer.setLayoutX(285); // Centrado (896/2 - 150)
        messageContainer.setLayoutY(650); // Posición Y mucho más abajo (ajustado)
        
        // Configuración del texto
        messageText.setTextAlignment(TextAlignment.CENTER);
        messageText.setWrappingWidth(300);
        
        // Añadir directamente al rootPane
        rootPane.getChildren().add(messageContainer);
        
        // Animaciones
        if (isError) {
            // Animación de sacudida para errores
            TranslateTransition shake = new TranslateTransition(Duration.millis(50), messageContainer);
            shake.setFromX(-5);
            shake.setToX(5);
            shake.setCycleCount(6);
            shake.setAutoReverse(true);
            shake.play();
        }
        
        // Aparecer con animación
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), messageContainer);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        
        // Desaparecer con animación
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(isError ? 2.5 : 2.0), messageContainer);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.seconds(isError ? 2.0 : 1.5)); // Demorar la desaparición
        
        // Eliminar al terminar
        fadeOut.setOnFinished(e -> rootPane.getChildren().remove(messageContainer));
        fadeOut.play();
    }
}