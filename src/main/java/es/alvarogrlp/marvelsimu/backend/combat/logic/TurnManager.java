package es.alvarogrlp.marvelsimu.backend.combat.logic;

import es.alvarogrlp.marvelsimu.backend.combat.ui.MessageDisplayManager;
import es.alvarogrlp.marvelsimu.backend.model.AtaqueModel;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class TurnManager {
    
    private CombatManager combatManager;
    private MessageDisplayManager messageManager;
    private boolean isPlayerTurn = true;
    
    public TurnManager(CombatManager combatManager, MessageDisplayManager messageManager) {
        this.combatManager = combatManager;
        this.messageManager = messageManager;
    }
    
    public void startCombat() {
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
            messageManager.displayMessage("Turno del oponente", false, 
                    () -> startAITurn());
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
            // Pequeña pausa antes de que la IA actúe
            messageManager.displayMessage("Turno de la IA", false, () -> {
                if (!combatManager.isCombatFinished()) {
                    // Ejecutar turno de la IA directamente
                    PauseTransition delay = new PauseTransition(Duration.millis(500));
                    delay.setOnFinished(e -> combatManager.aiTurn());
                    delay.play();
                }
            });
        } else {
            System.err.println("ERROR: Intentando iniciar turno de IA durante turno del jugador");
        }
    }
    
    /**
     * Finaliza el turno del AI y habilita los controles del jugador
     */
    public void finishAITurn() {
        isPlayerTurn = true;
        
        // Actualizar cooldowns de ataques del jugador
        PersonajeModel playerCharacter = combatManager.getCurrentPlayerCharacter();
        for (AtaqueModel ataque : playerCharacter.getAtaques()) {
            ataque.finalizarTurno();
        }
        
        // Habilitar controles del jugador para el nuevo turno
        combatManager.getUIManager().enablePlayerControls();
        
        // Actualizar indicador de turno en la UI
        combatManager.getUIManager().setPlayerTurnIndicator(true);
        
        // Mostrar mensaje de inicio de turno del jugador
        messageManager.displayMessage("¡Tu turno!", true);
    }
    
    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }
}