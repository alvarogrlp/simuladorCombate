package es.alvarogrlp.marvelsimu.backend.combat.model;

/**
 * Modelo simplificado para representar mensajes del combate con metadatos
 */
public class CombatMessage {
    
    // Tipos de mensaje simplificados
    public enum MessageType {
        ACTION,      // Acción normal (ataque, etc.)
        RESULT,      // Resultado de una acción (daño, etc.)
        TURN,        // Cambio de turno
        EVASION,     // Ataque evadido
        DEFEAT,      // Personaje derrotado
        ABILITY,     // Uso de habilidad
        VICTORY,     // Victoria en combate
        WARNING,     // Mensaje de advertencia o error
        TURN_CHANGE  // Cambio de turno entre jugadores
    }
    
    private String text;
    private MessageType type;
    private boolean isPlayerAction;
    private int relatedValue;
    private String additionalInfo;
    private boolean success;
    
    /**
     * Constructor interno ajustado
     */
    private CombatMessage(String text, MessageType type, boolean isPlayerAction, int relatedValue, String additionalInfo, boolean success) {
        this.text = text;
        this.type = type;
        this.isPlayerAction = isPlayerAction;
        this.relatedValue = relatedValue;
        this.additionalInfo = additionalInfo;
        this.success = success;
    }
    
    /**
     * Constructor para mensajes simples
     */
    public CombatMessage(String text, MessageType type, boolean isPlayerAction) {
        this(text, type, isPlayerAction, 0, null, true);
    }
    
    /**
     * Constructor completo con información adicional
     */
    public CombatMessage(String text, MessageType type, boolean isPlayerAction, int relatedValue, String additionalInfo) {
        this(text, type, isPlayerAction, relatedValue, additionalInfo, true);
    }
    
    /**
     * Crea un mensaje de acción
     */
    public static CombatMessage createActionMessage(String text, boolean isPlayerAction) {
        return new CombatMessage(text, MessageType.ACTION, isPlayerAction);
    }
    
    /**
     * Crea un mensaje de resultado de daño
     */
    public static CombatMessage createDamageMessage(String text, boolean isPlayerAction, int damage) {
        return new CombatMessage(text, MessageType.RESULT, isPlayerAction, damage, null);
    }
    
    /**
     * Crea un mensaje de turno
     */
    public static CombatMessage createTurnMessage(boolean isPlayerTurn) {
        String text = isPlayerTurn ? "¡Tu turno!" : "Turno de la IA";
        return new CombatMessage(text, MessageType.TURN, isPlayerTurn);
    }
    
    /**
     * Crea un mensaje de evasión
     */
    public static CombatMessage createEvasionMessage(String characterName, boolean isPlayerAction) {
        String text = characterName + " evade el ataque";
        return new CombatMessage(text, MessageType.EVASION, isPlayerAction);
    }
    
    /**
     * Crea un mensaje de derrota
     */
    public static CombatMessage createDefeatMessage(String characterName, boolean isPlayerAction) {
        String text = characterName + " ha sido derrotado";
        return new CombatMessage(text, MessageType.DEFEAT, isPlayerAction);
    }
    
    /**
     * Crea un mensaje de uso de habilidad
     */
    public static CombatMessage createAbilityMessage(String characterName, String abilityName, boolean isPlayerAction, boolean success) {
        String text = characterName + " usa " + abilityName;
        return new CombatMessage(text, MessageType.ABILITY, isPlayerAction, 0, abilityName, success);
    }

    /**
     * Sobrecarga para crear un mensaje de habilidad (éxito implícito = true).
     */
    public static CombatMessage createAbilityMessage(
            String characterName,
            String abilityName,
            boolean isPlayerAction) {
        return createAbilityMessage(characterName, abilityName, isPlayerAction, true);
    }
    
    /**
     * Crea un mensaje de victoria
     */
    public static CombatMessage createVictoryMessage(boolean playerWon) {
        String text = playerWon ? "¡Victoria!" : "¡Derrota!";
        return new CombatMessage(text, MessageType.VICTORY, playerWon);
    }
    
    /**
     * Crea un mensaje de advertencia
     */
    public static CombatMessage createWarningMessage(String text, boolean isPlayerAction) {
        return new CombatMessage(text, MessageType.WARNING, isPlayerAction);
    }
    
    /**
     * Crea un mensaje de fallo al tratar de usar una habilidad.
     */
    public static CombatMessage createFailedMessage(String actor, String abilityName) {
        return createAbilityMessage(actor, abilityName, true, false);
    }
    
    /**
     * Crear un mensaje para cambio de turno
     */
    public static CombatMessage createTurnChangeMessage(String text, boolean isPlayerTurn) {
        return new CombatMessage(text, MessageType.TURN_CHANGE, isPlayerTurn);
    }
    
    // Getters
    
    public String getText() {
        return text;
    }
    
    public MessageType getType() {
        return type;
    }
    
    public boolean isPlayerAction() {
        return isPlayerAction;
    }
    
    public int getRelatedValue() {
        return relatedValue;
    }
    
    public String getAdditionalInfo() {
        return additionalInfo;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    @Override
    public String toString() {
        return "CombatMessage{" +
                "text='" + text + '\'' +
                ", type=" + type +
                ", isPlayerAction=" + isPlayerAction +
                ", relatedValue=" + relatedValue +
                ", additionalInfo='" + additionalInfo + '\'' +
                ", success=" + success +
                '}';
    }
}