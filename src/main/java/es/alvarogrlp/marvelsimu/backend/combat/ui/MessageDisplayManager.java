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
    
    // Duración de mensajes aumentada
    private static final int DEFAULT_DISPLAY_TIME = 2500;  // Aumentado de 1800 a 2500
    private static final int SHORT_DISPLAY_TIME = 1800;    // Aumentado de 1000 a 1800
    private static final int LONG_DISPLAY_TIME = 3500;     // Aumentado de 2500 a 3500
    private static final int EFFECT_DISPLAY_TIME = 2200;   // Nuevo tiempo para mensajes de efectos
    private static final int TURN_DISPLAY_TIME = 1200;     // Tiempo reducido específico para mensajes de turno
    
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
        messageContainer.setMinWidth(450);  // Aumentado para mejor visibilidad
        messageContainer.setMinHeight(80);  // Aumentado para mejor visibilidad
        messageContainer.setMaxWidth(450);  
        messageContainer.setPrefWidth(450); 
        messageContainer.setAlignment(Pos.CENTER);
        messageContainer.setPadding(new Insets(12, 18, 12, 18));  // Padding mejorado
        messageContainer.getStyleClass().add("combat-message-container"); // Usar clase CSS
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
            case TURN_CHANGE:  // Nuevo tipo para mensajes de turno
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
            case TURN_CHANGE:  // Tiempo reducido para mensajes de turno
                displayTime = TURN_DISPLAY_TIME;
                break;
            case EVASION:
            case REDUCTION:
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
     * @param onComplete Callback para ejecutar al finalizar
     */
    public void displayDamageMessage(int damage, boolean isCritical, String target, boolean isPlayerAction, Runnable onComplete) {
        String message = damage + " de daño";
        
        // Añadir indicador de crítico
        if (isCritical) {
            message += " (¡CRÍTICO!)";
        }
        
        // Añadir a quién se le aplicó el daño
        message += " a " + target;
        
        // Mostrar mensaje con callback
        displayMessage(message, isPlayerAction, onComplete);
    }

    // Sobrecarga para mantener compatibilidad
    public void displayDamageMessage(int damage, boolean isCritical, String target, boolean isPlayerAction) {
        displayDamageMessage(damage, isCritical, target, isPlayerAction, null);
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
        
        // Reducir el tamaño de la fuente si el texto sigue siendo demasiado largo
        String text = messageText.getText();
        if (text.length() > 30) {
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
    
    // Nuevos métodos para mostrar mensajes de efectos específicos
    public void displayStatBuffMessage(String characterName, String statName, boolean isIncrease, boolean isPlayerAction) {
        String action = isIncrease ? "aumentado" : "reducido";
        String message = characterName + " ha " + action + " su " + statName;
        displayMessage(message, isPlayerAction);
    }
    
    /**
     * Muestra un mensaje sobre reducción de daño
     */
    public void displayDamageReductionMessage(String characterName, double reductionFactor, boolean isPlayerAction) {
        int percentage = (int)(reductionFactor * 100);
        String message = characterName + " reduce el daño recibido en un " + percentage + "%";
        displayMessage(message, isPlayerAction);
    }
    
    /**
     * Muestra un mensaje sobre reflejo de daño
     */
    public void displayReflectionMessage(String characterName, double reflectionFactor, boolean isPlayerAction) {
        int percentage = (int)(reflectionFactor * 100);
        String message = characterName + " refleja un " + percentage + "% del daño recibido";
        displayMessage(message, isPlayerAction);
    }
    
    /**
     * Muestra un mensaje sobre bloqueo de curación
     */
    public void displayHealingBlockMessage(String characterName, boolean isPlayerAction) {
        String message = characterName + " no puede curarse mientras dure el efecto";
        displayMessage(message, isPlayerAction);
    }
    
    /**
     * Muestra un mensaje sobre restricción a ataques básicos
     */
    public void displayRestrictedMessage(String characterName, int duration, boolean isPlayerAction) {
        String message = characterName + " solo puede usar ataques básicos durante " + duration + " turnos";
        displayMessage(message, isPlayerAction);
    }
    
    public void displayDamageBoostMessage(String characterName, double percentage, boolean isPlayerAction) {
        int percent = (int)(percentage * 100);
        String message = characterName + " aumenta su daño en un " + percent + "%";
        displayMessage(message, isPlayerAction);
    }
    
    public void displayStatusEffectMessage(String characterName, String effectName, boolean isPlayerAction) {
        String message = characterName + " sufre el efecto: " + effectName;
        displayMessage(message, isPlayerAction);
    }
}