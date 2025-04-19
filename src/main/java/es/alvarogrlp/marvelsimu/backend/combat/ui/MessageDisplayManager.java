package es.alvarogrlp.marvelsimu.backend.combat.ui;

import es.alvarogrlp.marvelsimu.backend.combat.model.CombatMessage;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * Gestiona la visualización de mensajes durante el combate
 */
public class MessageDisplayManager {
    
    private AnchorPane rootPane;
    private StackPane messageContainer;
    private boolean isDisplayingMessage = false;
    
    // Duración de mensajes por defecto
    private static final int DEFAULT_DISPLAY_TIME = 1800;
    private static final int SHORT_DISPLAY_TIME = 1000;
    private static final int LONG_DISPLAY_TIME = 2500;
    
    public MessageDisplayManager(AnchorPane rootPane) {
        this.rootPane = rootPane;
        initializeMessageContainer();
    }
    
    private void initializeMessageContainer() {
        messageContainer = new StackPane();
        messageContainer.setMinWidth(420);  // Aumentado de 400
        messageContainer.setMinHeight(70);  // Aumentado de 60
        messageContainer.setMaxWidth(420);  // Aumentado de 400
        messageContainer.setPrefWidth(420); // Aumentado de 400
        messageContainer.setAlignment(Pos.CENTER);
        messageContainer.setPadding(new Insets(10, 15, 10, 15));  // Añadir padding
        messageContainer.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 0.7), new CornerRadii(10), Insets.EMPTY)));
        messageContainer.setVisible(false);
        
        // Centrar en la pantalla
        AnchorPane.setLeftAnchor(messageContainer, 238.0);  // Ajustado para centrar
        AnchorPane.setTopAnchor(messageContainer, 200.0);
        
        rootPane.getChildren().add(messageContainer);
    }
    
    /**
     * Muestra un mensaje simple durante el combate
     * 
     * @param message Texto del mensaje
     * @param isPlayerAction Si es acción del jugador (true) o IA (false)
     */
    public void displayMessage(String message, boolean isPlayerAction) {
        displayMessage(message, isPlayerAction, null);
    }
    
    /**
     * Muestra un mensaje con callback al finalizar
     * 
     * @param message Texto del mensaje
     * @param isPlayerAction Si es acción del jugador (true) o IA (false)
     * @param onComplete Acción a ejecutar al finalizar
     */
    public void displayMessage(String message, boolean isPlayerAction, Runnable onComplete) {
        // Si hay un mensaje mostrándose, esperar un momento
        if (isDisplayingMessage) {
            PauseTransition delay = new PauseTransition(Duration.millis(500));
            delay.setOnFinished(e -> displayMessageImpl(message, isPlayerAction, onComplete));
            delay.play();
            return;
        }
        
        displayMessageImpl(message, isPlayerAction, onComplete);
    }
    
    /**
     * Muestra un mensaje con metadatos
     */
    public void displayCombatMessage(CombatMessage combatMessage, Runnable onComplete) {
        // Personalizar la visualización según el tipo de mensaje
        String text = combatMessage.getText();
        boolean isPlayerAction = combatMessage.isPlayerAction();
        CombatMessage.MessageType type = combatMessage.getType();
        
        // Simplificar el mensaje para que sea más corto
        String simplifiedText = simplifyMessage(text, isPlayerAction);
        
        Text messageText = new Text(simplifiedText);
        messageText.setFill(Color.WHITE);
        messageText.setFont(Font.font("System", FontWeight.BOLD, 18));
        messageText.setTextAlignment(TextAlignment.CENTER);
        
        // Limitar el ancho del texto para asegurar que no se salga del contenedor
        messageText.setWrappingWidth(380);
        
        // Aplicar estilos específicos según el tipo de mensaje
        switch (type) {
            case CRITICAL:
                messageText.setFill(Color.YELLOW);
                messageText.setFont(Font.font("System", FontWeight.BOLD, 20));
                break;
            case EVASION:
                messageText.setFill(Color.LIGHTBLUE);
                break;
            case REDUCTION:
                messageText.setFill(Color.LIGHTGREEN);
                break;
            case DEFEAT:
                messageText.setFill(Color.ORANGE);
                messageText.setFont(Font.font("System", FontWeight.BOLD, 20));
                break;
            case VICTORY:
                messageText.setFill(Color.GOLD);
                messageText.setFont(Font.font("System", FontWeight.BOLD, 22));
                break;
            case WARNING:
                messageText.setFill(Color.RED);
                break;
            default:
                break;
        }
        
        // Aplicar borde al texto para mejor visibilidad
        messageText.setStroke(Color.BLACK);
        messageText.setStrokeWidth(0.5);
        
        // Determinar duración del mensaje según su tipo
        int displayTime;
        switch (type) {
            case VICTORY:
            case DEFEAT:
                displayTime = LONG_DISPLAY_TIME;
                break;
            case EVASION:
            case REDUCTION:
                displayTime = SHORT_DISPLAY_TIME;
                break;
            default:
                displayTime = DEFAULT_DISPLAY_TIME;
        }
        
        // Mostrar el mensaje
        showAnimatedMessage(messageText, isPlayerAction, displayTime, onComplete);
    }
    
    /**
     * Implementación interna para mostrar mensajes
     */
    private void displayMessageImpl(String message, boolean isPlayerAction, Runnable onComplete) {
        // Simplificar el mensaje para que sea más corto
        String simplifiedMessage = simplifyMessage(message, isPlayerAction);
        
        Text messageText = new Text(simplifiedMessage);
        messageText.setFill(Color.WHITE);
        messageText.setFont(Font.font("System", FontWeight.BOLD, 18));
        messageText.setTextAlignment(TextAlignment.CENTER);
        
        // Limitar el ancho del texto para asegurar que no se salga del contenedor
        messageText.setWrappingWidth(380);
        
        // Aplicar borde al texto para mejor visibilidad
        messageText.setStroke(Color.BLACK);
        messageText.setStrokeWidth(0.5);
        
        showAnimatedMessage(messageText, isPlayerAction, DEFAULT_DISPLAY_TIME, onComplete);
    }
    
    /**
     * Simplifica un mensaje de combate para que sea más conciso
     * @param originalMessage Mensaje original
     * @param isPlayerAction Si es una acción del jugador
     * @return Mensaje simplificado
     */
    private String simplifyMessage(String originalMessage, boolean isPlayerAction) {
        // Eliminar texto de "daño verdadero" si aparece
        String message = originalMessage.replace(" (DAÑO VERDADERO)", "");
        
        // Encontrar patrones comunes en los mensajes y simplificarlos
        if (message.contains("ha usado")) {
            // Caso: "[Personaje] ha usado [Ataque]"
            String simplified;
            
            if (isPlayerAction) {
                simplified = "Has usado ";
            } else {
                simplified = "IA usa ";
            }
            
            // Extraer el nombre del ataque y si es crítico
            int startIndex = message.lastIndexOf("ha usado") + 8;
            String attackPart = message.substring(startIndex).trim();
            
            // Verificar si hay texto de crítico
            boolean isCritical = attackPart.contains("CRÍTICO");
            
            // Extraer solo el nombre del ataque (hasta el primer espacio después del ataque)
            if (attackPart.contains(" y ")) {
                attackPart = attackPart.substring(0, attackPart.indexOf(" y "));
            } else if (attackPart.contains(" causando ")) {
                attackPart = attackPart.substring(0, attackPart.indexOf(" causando "));
            }
            
            simplified += attackPart;
            
            // Añadir marcador de crítico si es necesario
            if (isCritical) {
                simplified += " (¡CRÍTICO!)";
            }
            
            return simplified;
        } 
        else if (message.contains(" de daño a ")) {
            // Caso: "[Daño] de daño a [Personaje]"
            String damage = message.substring(0, message.indexOf(" de daño"));
            String target = isPlayerAction ? "IA" : "ti";
            
            // Si mencionan a un personaje específico, extraer solo eso
            if (message.contains(" de daño a ")) {
                target = message.substring(message.lastIndexOf(" a ") + 3);
                // Simplificar al nombre del personaje sin títulos
                if (target.contains(" (")) {
                    target = target.substring(0, target.indexOf(" ("));
                }
            }
            
            boolean isCritical = message.contains("CRÍTICO");
            String simplified = damage + " daño a " + target;
            
            if (isCritical) {
                simplified += " (¡CRÍTICO!)";
            }
            
            return simplified;
        }
        
        // Si no coincide con patrones conocidos, al menos acortar el mensaje
        if (message.length() > 40) {
            // Mantener el texto original pero truncado a 40 caracteres
            return message.substring(0, 37) + "...";
        }
        
        return message;
    }
    
    /**
     * Muestra un mensaje de daño en combate
     * @param damage Cantidad de daño
     * @param isCritical Si es golpe crítico
     * @param target Objetivo del ataque
     * @param isPlayerAction Si es acción del jugador
     */
    public void displayDamageMessage(int damage, boolean isCritical, String target, boolean isPlayerAction) {
        String message = damage + " de daño";
        
        // Añadir indicador de crítico (pero no de daño verdadero)
        if (isCritical) {
            message += " (¡CRÍTICO!)";
        }
        
        // Añadir a quién se le aplicó el daño
        message += " a " + target;
        
        // Mostrar mensaje
        displayMessage(message, isPlayerAction);
    }
    
    /**
     * Muestra un mensaje con animación
     */
    private void showAnimatedMessage(Text messageText, boolean isPlayerAction, int displayTime, Runnable onComplete) {
        isDisplayingMessage = true;
        messageContainer.getChildren().clear();
        
        // Reducir el tamaño de la fuente si el texto sigue siendo demasiado largo
        String text = messageText.getText();
        if (text.length() > 30) {
            messageText.setFont(Font.font("System", FontWeight.BOLD, 16));
        }
        
        // Eliminar contenido específicamente problemático
        if (text.contains("Soulsword") && text.contains("CRÍTICO")) {
            messageText.setText(text.replace("(¡CRÍTICO!)", "!"));
        }
        
        messageContainer.getChildren().add(messageText);
        
        // Color de fondo según quien realiza la acción
        Background background;
        if (isPlayerAction) {
            background = new Background(new BackgroundFill(
                Color.rgb(0, 50, 100, 0.8), new CornerRadii(10), Insets.EMPTY));
        } else {
            background = new Background(new BackgroundFill(
                Color.rgb(100, 20, 20, 0.8), new CornerRadii(10), Insets.EMPTY));
        }
        messageContainer.setBackground(background);
        
        // Efectos para el contenedor
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.BLACK);
        messageContainer.setEffect(dropShadow);
        
        // Animaciones de entrada y salida
        messageContainer.setOpacity(0);
        messageContainer.setVisible(true);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), messageContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        PauseTransition display = new PauseTransition(Duration.millis(displayTime));
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), messageContainer);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            messageContainer.setVisible(false);
            isDisplayingMessage = false;
            
            // Ejecutar callback si existe
            if (onComplete != null) {
                onComplete.run();
            }
        });
        
        SequentialTransition sequence = new SequentialTransition(fadeIn, display, fadeOut);
        sequence.play();
    }
}