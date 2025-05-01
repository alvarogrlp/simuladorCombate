package es.alvarogrlp.marvelsimu.backend.combat.ui;

import java.util.LinkedList;
import java.util.Queue;

import es.alvarogrlp.marvelsimu.backend.combat.model.CombatMessage;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

    // Agregar una cola de mensajes 
    private Queue<MessageEntry> messageQueue = new LinkedList<>();
    private boolean isProcessingQueue = false;
    
    // Duración de mensajes
    private static final int DEFAULT_DISPLAY_TIME = 2500;
    private static final int SHORT_DISPLAY_TIME = 1800;
    private static final int LONG_DISPLAY_TIME = 3500;
    private static final int EFFECT_DISPLAY_TIME = 2200;
    private static final int TURN_DISPLAY_TIME = 1200;
    
    // Clase interna para entradas en la cola
    private class MessageEntry {
        Text messageText;
        boolean isPlayerAction;
        int displayTime;
        Runnable onComplete;
        
        public MessageEntry(Text messageText, boolean isPlayerAction, int displayTime, Runnable onComplete) {
            this.messageText = messageText;
            this.isPlayerAction = isPlayerAction;
            this.displayTime = displayTime;
            this.onComplete = onComplete;
        }
    }
    
    public MessageDisplayManager(AnchorPane rootPane) {
        this.rootPane = rootPane;
        initializeMessageContainer();
    }
    
    private void initializeMessageContainer() {
        messageContainer = new StackPane();
        messageContainer.setMinWidth(450);
        messageContainer.setMinHeight(80);
        messageContainer.setMaxWidth(450);  
        messageContainer.setPrefWidth(450); 
        messageContainer.setAlignment(Pos.CENTER);
        messageContainer.setPadding(new Insets(12, 18, 12, 18));
        messageContainer.getStyleClass().add("combat-message-container");
        messageContainer.setVisible(false);
        
        // Posición ajustada para mejor visibilidad
        AnchorPane.setLeftAnchor(messageContainer, 223.0);  // Centrado
        AnchorPane.setTopAnchor(messageContainer, 250.0);  // Un poco más abajo para no tapar personajes
        
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
        // Simplificar el mensaje para que sea más corto
        String simplifiedMessage = simplifyMessage(message, isPlayerAction);
        
        Text messageText = new Text(simplifiedMessage);
        messageText.setFill(Color.WHITE);
        messageText.setFont(Font.font("System", FontWeight.BOLD, 18));
        messageText.setTextAlignment(TextAlignment.CENTER);
        messageText.setWrappingWidth(380);
        messageText.setStroke(Color.BLACK);
        messageText.setStrokeWidth(0.5);
        
        // Encolar el mensaje en lugar de mostrarlo inmediatamente
        enqueueMessage(messageText, isPlayerAction, DEFAULT_DISPLAY_TIME, onComplete);
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
        messageText.setWrappingWidth(380);
        
        // Aplicar estilos específicos según el tipo de mensaje
        switch (type) {
            case EVASION:
                messageText.setFill(Color.LIGHTBLUE);
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
            case TURN_CHANGE:
                messageText.setFont(Font.font("System", FontWeight.BOLD, 22));
                if (isPlayerAction) {
                    messageText.setFill(Color.LIGHTBLUE);
                } else {
                    messageText.setFill(Color.LIGHTCORAL);
                }
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
            case TURN_CHANGE:
                displayTime = TURN_DISPLAY_TIME;
                break;
            case EVASION:
                displayTime = SHORT_DISPLAY_TIME;
                break;
            case ABILITY:
                displayTime = EFFECT_DISPLAY_TIME;
                break;
            default:
                displayTime = DEFAULT_DISPLAY_TIME;
        }
        
        // Encolar el mensaje
        enqueueMessage(messageText, isPlayerAction, displayTime, onComplete);
    }
    
    /**
     * Método específico para mostrar mensajes de cambio de turno con duración más corta
     */
    public void displayTurnMessage(boolean isPlayerTurn, Runnable onComplete) {
        String message = isPlayerTurn ? "¡TU TURNO!" : "TURNO DEL OPONENTE";
        
        Text messageText = new Text(message);
        messageText.setFill(isPlayerTurn ? Color.LIGHTBLUE : Color.LIGHTCORAL);
        messageText.setFont(Font.font("System", FontWeight.BOLD, 22));
        messageText.setTextAlignment(TextAlignment.CENTER);
        messageText.setStroke(Color.BLACK);
        messageText.setStrokeWidth(0.5);
        
        // Usar tiempo más corto para mensajes de turno
        enqueueMessage(messageText, isPlayerTurn, TURN_DISPLAY_TIME, onComplete);
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
            
            // Extraer el nombre del ataque
            int startIndex = message.lastIndexOf("ha usado") + 8;
            String attackPart = message.substring(startIndex).trim();
            
            // Extraer solo el nombre del ataque (hasta el primer espacio después del ataque)
            if (attackPart.contains(" y ")) {
                attackPart = attackPart.substring(0, attackPart.indexOf(" y "));
            } else if (attackPart.contains(" causando ")) {
                attackPart = attackPart.substring(0, attackPart.indexOf(" causando "));
            }
            
            simplified += attackPart;
            
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
            
            String simplified = damage + " daño a " + target;
            return simplified;
        }
        
        // Si no coincide con patrones conocidos, mostrar el mensaje completo
        return message;
    }
    
    /**
     * Muestra un mensaje de daño en combate
     * @param damage Cantidad de daño
     * @param target Objetivo del ataque
     * @param isPlayerAction Si es acción del jugador
     * @param onComplete Callback para ejecutar al finalizar
     */
    public void displayDamageMessage(int damage, String target, boolean isPlayerAction, Runnable onComplete) {
        String message = damage + " de daño";
        
        // Añadir a quién se le aplicó el daño
        message += " a " + target;
        
        // Mostrar mensaje con callback
        displayMessage(message, isPlayerAction, onComplete);
    }

    // Sobrecarga para mantener compatibilidad
    public void displayDamageMessage(int damage, String target, boolean isPlayerAction) {
        displayDamageMessage(damage, target, isPlayerAction, null);
    }
    
    /**
     * Nuevo método para encolar mensajes
     */
    private void enqueueMessage(Text messageText, boolean isPlayerAction, int displayTime, Runnable onComplete) {
        // Agregar el mensaje a la cola
        messageQueue.offer(new MessageEntry(messageText, isPlayerAction, displayTime, onComplete));
        
        // Si no estamos procesando la cola, comenzar a procesar
        if (!isProcessingQueue) {
            processNextMessage();
        }
    }
    
    /**
     * Método para procesar el siguiente mensaje en la cola
     */
    private void processNextMessage() {
        if (messageQueue.isEmpty()) {
            isProcessingQueue = false;
            return;
        }
        
        isProcessingQueue = true;
        MessageEntry entry = messageQueue.poll();
        
        // Crear un callback que procese el siguiente mensaje cuando termine
        Runnable nextCallback = () -> {
            // Ejecutar el callback original si existe
            if (entry.onComplete != null) {
                entry.onComplete.run();
            }
            
            // Pequeña pausa entre mensajes para mejor legibilidad
            PauseTransition pause = new PauseTransition(Duration.millis(250));
            pause.setOnFinished(e -> processNextMessage());
            pause.play();
        };
        
        // Mostrar el mensaje con el callback actualizado
        showAnimatedMessage(entry.messageText, entry.isPlayerAction, entry.displayTime, nextCallback);
    }
    
    /**
     * Muestra un mensaje con animación
     */
    private void showAnimatedMessage(Text messageText, boolean isPlayerAction, int displayTime, Runnable onComplete) {
        isDisplayingMessage = true;
        messageContainer.getChildren().clear();
        
        // Ajustar el tamaño de la fuente según la longitud del texto
        String text = messageText.getText();
        if (text.length() > 50) {
            messageText.setFont(Font.font("System", FontWeight.BOLD, 14));
            // Para mensajes muy largos, aumentar el ancho del contenedor
            messageContainer.setMaxWidth(550);
            messageContainer.setPrefWidth(550);
            messageText.setWrappingWidth(500);
        } else if (text.length() > 30) {
            messageText.setFont(Font.font("System", FontWeight.BOLD, 16));
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
        
        // Asegurarse de que el contenedor esté centrado horizontalmente
        if (messageContainer.getMaxWidth() > 450) {
            // Ajustar el anclaje para mantenerlo centrado si cambió el tamaño
            double newLeft = (896 - messageContainer.getMaxWidth()) / 2;
            AnchorPane.setLeftAnchor(messageContainer, newLeft);
        } else {
            // Volver a los valores originales si no hay cambios
            AnchorPane.setLeftAnchor(messageContainer, 223.0);
            messageContainer.setMaxWidth(450);
            messageContainer.setPrefWidth(450);
            messageText.setWrappingWidth(380);
        }
        
        // Animaciones de entrada y salida
        messageContainer.setOpacity(0);
        messageContainer.setVisible(true);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), messageContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        PauseTransition display = new PauseTransition(Duration.millis(displayTime));
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), messageContainer);
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