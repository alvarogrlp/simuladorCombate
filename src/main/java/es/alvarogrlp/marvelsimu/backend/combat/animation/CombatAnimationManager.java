package es.alvarogrlp.marvelsimu.backend.combat.animation;

import es.alvarogrlp.marvelsimu.backend.combat.ui.CombatUIManager;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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
    public void showDamageText(int damage, boolean isPlayerAttack, boolean unused1, boolean unused2) {
        try {
            // Crear contenedor para el texto de daño
            StackPane damageContainer = new StackPane();
            
            // Crear texto con el daño
            Text damageText = new Text(Integer.toString(damage));
            damageText.setFont(Font.font("System", FontWeight.BOLD, 36));
            
            // Color según si es aliado o enemigo
            if (isPlayerAttack) {
                damageText.setFill(Color.RED);
                damageText.getStyleClass().add("enemy-damage");
            } else {
                damageText.setFill(Color.RED);
                damageText.getStyleClass().add("player-damage");
            }
            
            // Añadir sombra al texto para mejor visibilidad
            damageText.setEffect(new DropShadow(5, Color.BLACK));
            
            // Añadir texto al contenedor
            damageContainer.getChildren().add(damageText);
            
            // Posicionar el texto según el objetivo del ataque
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
                
                // Crear animación de subida y desvanecimiento
                TranslateTransition moveUp = new TranslateTransition(Duration.millis(700), damageContainer);
                moveUp.setByY(-50);
                
                FadeTransition fadeOut = new FadeTransition(Duration.millis(500), damageContainer);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setDelay(Duration.millis(200));
                
                // Combinar ambas animaciones
                ParallelTransition animation = new ParallelTransition(moveUp, fadeOut);
                
                // Limpiar después de la animación
                animation.setOnFinished(e -> rootPane.getChildren().remove(damageContainer));
                
                // Iniciar animación
                animation.play();
            }
        } catch (Exception e) {
            System.err.println("Error mostrando texto de daño: " + e.getMessage());
        }
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
    
    /**
     * Anima la derrota de un personaje
     * @param defeated El personaje derrotado
     * @param isPlayer Indica si es un personaje del jugador (true) o de la IA (false)
     * @param onComplete Acción a ejecutar al finalizar la animación
     */
    public void animateDefeat(PersonajeModel defeated, boolean isPlayer, Runnable onComplete) {
        try {
            // Obtener la imagen del personaje derrotado
            ImageView characterImage = isPlayer ? 
                    (ImageView) rootPane.lookup("#imgPersonajeJugador") : 
                    (ImageView) rootPane.lookup("#imgPersonajeIA");
            
            if (characterImage == null) {
                if (onComplete != null) onComplete.run();
                return;
            }
            
            // Crear efecto de desvanecimiento
            FadeTransition fadeOut = new FadeTransition(Duration.millis(800), characterImage);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.3); // No desaparece completamente para que se vea "derrotado"
            
            // Animar caída
            TranslateTransition fallDown = new TranslateTransition(Duration.millis(500), characterImage);
            fallDown.setByY(30); // El personaje "cae" ligeramente
            
            // Agregar efecto de oscurecimiento
            ColorAdjust grayOut = new ColorAdjust();
            grayOut.setBrightness(-0.5);
            grayOut.setSaturation(-0.8); // Casi en escala de grises
            
            // Aplicar el efecto gradualmente
            Timeline effectTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, 
                    new KeyValue(grayOut.brightnessProperty(), 0),
                    new KeyValue(grayOut.saturationProperty(), 0)
                ),
                new KeyFrame(Duration.millis(500), 
                    new KeyValue(grayOut.brightnessProperty(), -0.5),
                    new KeyValue(grayOut.saturationProperty(), -0.8)
                )
            );
            
            // Asignar el efecto a la imagen
            characterImage.setEffect(grayOut);
            
            // Combinar todas las animaciones
            ParallelTransition defeatAnimation = new ParallelTransition(
                fadeOut, fallDown, effectTimeline
            );
            
            // Ejecutar callback al finalizar
            defeatAnimation.setOnFinished(e -> {
                // Actualizar la UI para mostrar claramente que está derrotado
                characterImage.setOpacity(0.3); // Para asegurar la opacidad final
                
                // Marcar la imagen para indicar que está derrotado
                if (isPlayer) {
                    uiManager.markPlayerDefeated();
                } else {
                    uiManager.markAIDefeated();
                }
                
                // IMPORTANTE: Reset the translation to avoid affecting the next character
                characterImage.setTranslateY(0);
                
                // Completar la acción
                if (onComplete != null) {
                    onComplete.run();
                }
            });
            
            // Iniciar la animación
            defeatAnimation.play();
            
        } catch (Exception e) {
            System.err.println("Error animando derrota: " + e.getMessage());
            e.printStackTrace();
            
            // Asegurar que se complete la acción incluso si hay error
            if (onComplete != null) {
                onComplete.run();
            }
        }
    }
}