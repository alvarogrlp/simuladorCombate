package es.alvarogrlp.marvelsimu.backend.combat.animation;

import es.alvarogrlp.marvelsimu.backend.combat.ui.CombatUIManager;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class CombatAnimationManager {
    
    private AnchorPane rootPane;
    private CombatUIManager uiManager;
    private PlayerAnimationHandler playerAnimationHandler;
    private EnemyAnimationHandler enemyAnimationHandler;
    private VisualEffectsManager effectsManager;
    
    public CombatAnimationManager(AnchorPane rootPane, CombatUIManager uiManager) {
        this.rootPane = rootPane;
        this.uiManager = uiManager;
        this.playerAnimationHandler = new PlayerAnimationHandler(rootPane);
        this.enemyAnimationHandler = new EnemyAnimationHandler(rootPane);
        this.effectsManager = new VisualEffectsManager(rootPane);
    }
    
    public void animatePlayerAttack(String attackType, Runnable onComplete) {
        ImageView playerImage = (ImageView) rootPane.lookup("#imgPersonajeJugador");
        ImageView aiImage = (ImageView) rootPane.lookup("#imgPersonajeIA");
        
        if (playerImage == null || aiImage == null) {
            if (onComplete != null) onComplete.run();
            return;
        }
        
        playerAnimationHandler.animateAttack(playerImage, aiImage, attackType, onComplete);
    }
    
    public void animateAIAttack(String attackType, Runnable onComplete) {
        ImageView playerImage = (ImageView) rootPane.lookup("#imgPersonajeJugador");
        ImageView aiImage = (ImageView) rootPane.lookup("#imgPersonajeIA");
        
        if (playerImage == null || aiImage == null) {
            if (onComplete != null) onComplete.run();
            return;
        }
        
        enemyAnimationHandler.animateAttack(aiImage, playerImage, attackType, onComplete);
    }
    
    /**
     * Muestra el texto de daño
     */
    public void showDamageText(int damage, boolean isPlayerAttack, boolean isCritical, boolean isTrueDamage) {
        // Cuando el daño es 0, no mostramos nada
        if (damage <= 0) return;
        
        Text damageText = new Text(String.valueOf(damage));
        damageText.getStyleClass().add("damage-text");
        
        if (isPlayerAttack) {
            damageText.getStyleClass().add("player-damage");
        } else {
            damageText.getStyleClass().add("enemy-damage");
        }
        
        if (isCritical) {
            damageText.getStyleClass().add("critical-damage");
            damageText.setFont(Font.font("Roboto", FontWeight.BOLD, 32));
        }
        
        if (isTrueDamage) {
            damageText.getStyleClass().add("true-damage-text");
        }
        
        Rectangle damageBackground = new Rectangle();
        damageBackground.setArcWidth(15);
        damageBackground.setArcHeight(15);
        damageBackground.setFill(Color.rgb(0, 0, 0, 0.6));
        
        damageText.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            damageBackground.setWidth(newBounds.getWidth() + 15);
            damageBackground.setHeight(newBounds.getHeight() + 8);
        });
        
        StackPane damageContainer = new StackPane(damageBackground, damageText);
        
        // Usar Platform.runLater para garantizar que las coordenadas sean correctas
        Platform.runLater(() -> {
            ImageView targetImage = isPlayerAttack ? 
                    (ImageView) rootPane.lookup("#imgPersonajeIA") : 
                    (ImageView) rootPane.lookup("#imgPersonajeJugador");
            
            if (targetImage != null) {
                // Coordenadas basadas en la posición real del personaje
                double centerX = targetImage.localToScene(targetImage.getBoundsInLocal()).getMinX() + 
                               targetImage.getFitWidth() / 2 - 20;
                double centerY = targetImage.localToScene(targetImage.getBoundsInLocal()).getMinY() + 100;
                
                AnchorPane.setLeftAnchor(damageContainer, centerX);
                AnchorPane.setTopAnchor(damageContainer, centerY);
                
                rootPane.getChildren().add(damageContainer);
                
                TranslateTransition moveUp = new TranslateTransition(Duration.millis(700), damageContainer);
                moveUp.setByY(-40);
                
                FadeTransition fadeOut = new FadeTransition(Duration.millis(700), damageContainer);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e -> rootPane.getChildren().remove(damageContainer));
                
                ParallelTransition animation = new ParallelTransition(moveUp, fadeOut);
                animation.play();
            }
        });
    }
    
    public void showEvasionEffect(PersonajeModel character, boolean isPlayerAttack) {
        effectsManager.showEvasionEffect(character, isPlayerAttack);
    }
    
    public void showDamageReductionEffect(PersonajeModel character, boolean isPlayerAttack) {
        effectsManager.showDamageReductionEffect(character, isPlayerAttack);
    }
    
    public void showRegenerationEffect(PersonajeModel character, boolean isPlayerAttack) {
        effectsManager.showRegenerationEffect(character, isPlayerAttack);
    }
    
    public void showTrueDamageEffect(PersonajeModel character, boolean isPlayerAttack) {
        effectsManager.showTrueDamageEffect(character, isPlayerAttack);
    }
}