package es.alvarogrlp.marvelsimu.backend.selection.animation;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.AnchorPane;
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
        Text messageText = new Text(message);
        messageText.getStyleClass().add("mensaje-flotante");
        
        if (isError) {
            messageText.getStyleClass().add("mensaje-error");
        }
        
        // Posición fija en la pantalla en coordenadas absolutas
        messageText.setLayoutX(448 - 150); // Centro de la pantalla (896/2) menos la mitad del ancho del texto
        messageText.setLayoutY(640); // Posición Y fija, encima del botón volver
        
        // Configuración del texto
        messageText.setTextAlignment(TextAlignment.CENTER);
        messageText.setWrappingWidth(300);
        
        // Añadir al rootPane
        rootPane.getChildren().add(messageText);
        
        // Si es un error, añadir animación de sacudida
        if (isError) {
            TranslateTransition shake = new TranslateTransition(Duration.millis(50), messageText);
            shake.setFromX(-5);
            shake.setToX(5);
            shake.setCycleCount(6);
            shake.setAutoReverse(true);
            shake.play();
        }
        
        // Desvanecimiento
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(isError ? 2.5 : 2.0), messageText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        if (isError) {
            fadeOut.setDelay(Duration.seconds(1));
        }
        fadeOut.setOnFinished(e -> rootPane.getChildren().remove(messageText));
        fadeOut.play();
    }
}