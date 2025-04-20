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
     * Finaliza el turno del jugador
     */
    public void finishPlayerTurn(boolean playerActed) {
        if (isPlayerTurn) {
            isPlayerTurn = false;
            
            // Actualizar indicador visual del turno
            combatManager.getUIManager().setPlayerTurnIndicator(false);
            
            // Si el jugador realizó una acción, comenzar el turno de la IA
            // Si no, simplemente devolver el turno al jugador (útil para cancelaciones)
            if (playerActed) {
                // Actualizar cooldowns de ataques del personaje del jugador
                PersonajeModel playerCharacter = combatManager.getCurrentPlayerCharacter();
                for (AtaqueModel ataque : playerCharacter.getAtaques()) {
                    ataque.finalizarTurno();
                }
                
                startAITurn();
            } else {
                startPlayerTurn();
            }
        }
    }
    
    /**
     * Comienza el turno de la IA
     */
    public void startAITurn() {
        if (!isPlayerTurn) {
            // Pequeña pausa antes de que la IA actúe
            messageManager.displayMessage("Turno de la IA", false, () -> {
                if (!combatManager.isCombatFinished()) {
                    combatManager.aiTurn();
                }
            });
        }
    }
    
    /**
     * Finaliza el turno de la IA
     */
    public void finishAITurn() {
        if (!isPlayerTurn) {
            isPlayerTurn = true;
            
            // Iniciar el siguiente turno del jugador
            startPlayerTurn();
        }
    }
    
    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }
}