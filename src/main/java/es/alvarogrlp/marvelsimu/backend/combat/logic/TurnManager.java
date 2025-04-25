package es.alvarogrlp.marvelsimu.backend.combat.logic;

import es.alvarogrlp.marvelsimu.backend.combat.model.CombatMessage;
import es.alvarogrlp.marvelsimu.backend.combat.ui.MessageDisplayManager;
import es.alvarogrlp.marvelsimu.backend.model.AtaqueModel;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class TurnManager {
    
    private CombatManager combatManager;
    private MessageDisplayManager messageManager;
    private boolean isPlayerTurn = true;
    private int currentTurn = 1; // Añadir esta variable de instancia
    
    public TurnManager(CombatManager combatManager, MessageDisplayManager messageManager) {
        this.combatManager = combatManager;
        this.messageManager = messageManager;
    }
    
    public void startCombat() {
        // Inicializar el contador de turnos
        currentTurn = 1;
        
        PauseTransition startDelay = new PauseTransition(Duration.millis(1000));
        startDelay.setOnFinished(e -> {
            messageManager.displayMessage("¡Comienza el combate!", true);
            startPlayerTurn();
        });
        startDelay.play();
    }
    
    public void startPlayerTurn() {
        isPlayerTurn = true;
        combatManager.getUIManager().setPlayerTurnIndicator(true);
        // Asegurarnos de habilitar los controles del jugador al iniciar su turno
        combatManager.getUIManager().enablePlayerControls();
    }
    
    /**
     * Termina el turno del jugador
     * @param animateTransition Si debe animarse la transición
     */
    public void finishPlayerTurn(boolean animateTransition) {
        System.out.println("Finalizando turno del jugador, animateTransition=" + animateTransition);
        isPlayerTurn = false;
        combatManager.getUIManager().disablePlayerControls();
        
        // Actualizar indicador de turno en la UI
        combatManager.getUIManager().setPlayerTurnIndicator(false);
        
        if (animateTransition) {
            // Usar el mensaje de tipo TURN_CHANGE
            CombatMessage turnMessage = CombatMessage.createTurnChangeMessage("TURNO DEL OPONENTE", false);
            messageManager.displayCombatMessage(turnMessage, () -> startAITurn());
        } else {
            startAITurn();
        }
    }
    
    /**
     * Comienza el turno de la IA
     */
    public void startAITurn() {
        System.out.println("Iniciando turno de la IA en TurnManager");
        if (!isPlayerTurn) {
            // No mostrar mensaje aquí ya que ya lo hicimos en finishPlayerTurn
            // Pequeña pausa antes de que la IA actúe
            PauseTransition delay = new PauseTransition(Duration.millis(500));
            delay.setOnFinished(e -> {
                if (!combatManager.isCombatFinished()) {
                    combatManager.aiTurn();
                }
            });
            delay.play();
        } else {
            System.err.println("ERROR: Intentando iniciar turno de IA durante turno del jugador");
        }
    }
    
    /**
     * Finaliza el turno del AI y habilita los controles del jugador
     */
    public void finishAITurn() {
        isPlayerTurn = true;
        
        // Incrementar el contador de turnos cuando termina el turno de la IA
        currentTurn++;
        
        // Procesar efectos programados para este turno
        combatManager.processScheduledEffects(currentTurn);
        
        // Actualizar cooldowns de ataques del jugador
        PersonajeModel playerCharacter = combatManager.getCurrentPlayerCharacter();
        for (AtaqueModel ataque : playerCharacter.getAtaques()) {
            ataque.finalizarTurno();
        }
        
        // Actualizar efectos activos del jugador
        combatManager.getEffectsManager().tickEffects(playerCharacter);
        
        // Habilitar controles del jugador para el nuevo turno
        combatManager.getUIManager().enablePlayerControls();
        
        // Actualizar indicador de turno en la UI
        combatManager.getUIManager().setPlayerTurnIndicator(true);
        
        // Mostrar mensaje de inicio de turno del jugador con tipo TURN_CHANGE
        CombatMessage turnMessage = CombatMessage.createTurnChangeMessage("¡TU TURNO!", true);
        messageManager.displayCombatMessage(turnMessage, null);
    }
    
    /**
     * Obtiene el número del turno actual
     * @return El número del turno actual
     */
    public int getCurrentTurn() {
        return currentTurn;
    }
    
    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }
}