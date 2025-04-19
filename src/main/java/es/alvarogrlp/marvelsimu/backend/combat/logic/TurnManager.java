package es.alvarogrlp.marvelsimu.backend.combat.logic;

import es.alvarogrlp.marvelsimu.backend.combat.ui.MessageDisplayManager;
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
    
    public void finishPlayerTurn(boolean executeAITurn) {
        // Deshabilitar los controles del jugador al terminar su turno
        combatManager.getUIManager().disablePlayerControls();
        if (executeAITurn) {
            startAITurn();
        }
    }
    
    public void startAITurn() {
        isPlayerTurn = false;
        // Asegurarnos de que los controles estén deshabilitados durante el turno de la IA
        combatManager.getUIManager().disablePlayerControls();
        combatManager.getUIManager().setPlayerTurnIndicator(false);
        
        PauseTransition aiDelay = new PauseTransition(Duration.millis(1200));
        aiDelay.setOnFinished(e -> combatManager.aiTurn());
        aiDelay.play();
    }
    
    public void finishAITurn() {
        // Asegurarnos de añadir un pequeño retraso antes de habilitar los controles 
        // para evitar clic accidental
        PauseTransition endTurnDelay = new PauseTransition(Duration.millis(800));
        endTurnDelay.setOnFinished(e -> {
            // IMPORTANTE: Explícitamente habilitamos los controles aquí
            isPlayerTurn = true;
            combatManager.getUIManager().setPlayerTurnIndicator(true);
            combatManager.getUIManager().enablePlayerControls();
        });
        endTurnDelay.play();
    }
    
    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }
}