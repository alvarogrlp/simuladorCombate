package es.alvarogrlp.marvelsimu.backend.combat.animation;

import es.alvarogrlp.marvelsimu.backend.combat.model.Stat;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Clase simplificada para efectos visuales en habilidades especiales
 */
public class SpecialEffectsAnimator {
    
    private AnchorPane rootPane;
    
    public SpecialEffectsAnimator(AnchorPane rootPane) {
        this.rootPane = rootPane;
    }
    
    /**
     * Anima un efecto de daño inmediato de habilidad
     * @param character Personaje que recibe el daño
     * @param damage Cantidad de daño
     * @param isPlayerCharacter Si es el personaje del jugador
     */
    public void animateAbilityDamage(PersonajeModel character, int damage, boolean isPlayerCharacter) {
        ImageView characterImage = getCharacterImage(isPlayerCharacter);
        if (characterImage == null) return;
        
        // Coordenadas del personaje
        double centerX = characterImage.getLayoutX() + characterImage.getFitWidth() / 2;
        double centerY = characterImage.getLayoutY() + characterImage.getFitHeight() / 2;
        
        // Crear efecto visual de impacto
        StackPane impactEffect = new StackPane();
        
        // Círculo de impacto
        Circle impactCircle = new Circle(50);
        impactCircle.setFill(Color.TRANSPARENT);
        impactCircle.setStroke(Color.ORANGERED);
        impactCircle.setStrokeWidth(3);
        
        impactEffect.getChildren().add(impactCircle);
        impactEffect.setLayoutX(centerX);
        impactEffect.setLayoutY(centerY);
        impactEffect.setOpacity(0);
        
        rootPane.getChildren().add(impactEffect);
        
        // Texto de daño
        Text damageText = new Text(Integer.toString(damage));
        damageText.setFont(Font.font("System", FontWeight.BOLD, 36));
        damageText.setFill(Color.RED);
        damageText.setStroke(Color.BLACK);
        damageText.setStrokeWidth(2);
        
        StackPane textContainer = new StackPane(damageText);
        textContainer.setLayoutX(centerX - 20);
        textContainer.setLayoutY(centerY - 80);
        textContainer.setOpacity(0);
        
        rootPane.getChildren().add(textContainer);
        
        // Animación de impacto
        FadeTransition showImpact = new FadeTransition(Duration.millis(200), impactEffect);
        showImpact.setToValue(1.0);
        
        ScaleTransition expandImpact = new ScaleTransition(Duration.millis(500), impactCircle);
        expandImpact.setFromX(0.5);
        expandImpact.setFromY(0.5);
        expandImpact.setToX(1.2);
        expandImpact.setToY(1.2);
        
        FadeTransition fadeImpact = new FadeTransition(Duration.millis(300), impactEffect);
        fadeImpact.setFromValue(1.0);
        fadeImpact.setToValue(0.0);
        fadeImpact.setDelay(Duration.millis(200));
        
        // Animación del texto
        FadeTransition showText = new FadeTransition(Duration.millis(200), textContainer);
        showText.setToValue(1.0);
        
        TranslateTransition floatText = new TranslateTransition(Duration.millis(1000), textContainer);
        floatText.setByY(-40);
        
        FadeTransition fadeText = new FadeTransition(Duration.millis(500), textContainer);
        fadeText.setFromValue(1.0);
        fadeText.setToValue(0.0);
        fadeText.setDelay(Duration.millis(700));
        
        // Sacudir al personaje
        TranslateTransition shakeCharacter = new TranslateTransition(Duration.millis(50), characterImage);
        shakeCharacter.setFromX(0);
        shakeCharacter.setByX(isPlayerCharacter ? -10 : 10);
        shakeCharacter.setCycleCount(6);
        shakeCharacter.setAutoReverse(true);
        
        // Secuencia completa
        SequentialTransition sequence = new SequentialTransition(
            new ParallelTransition(showImpact, expandImpact, fadeImpact, shakeCharacter),
            new ParallelTransition(showText, floatText, fadeText)
        );
        
        sequence.setOnFinished(e -> {
            rootPane.getChildren().removeAll(impactEffect, textContainer);
        });
        
        sequence.play();
    }
    
    /**
     * Anima un ataque mortal instantáneo
     */
    public void animateInstantKill(PersonajeModel character, boolean isPlayerCharacter, Runnable onComplete) {
        ImageView characterImage = getCharacterImage(isPlayerCharacter);
        if (characterImage == null) {
            if (onComplete != null) onComplete.run();
            return;
        }
        
        // Crear efecto visual dramático para muerte instantánea
        StackPane killEffect = new StackPane();
        
        // Círculo de energía para el efecto visual
        Circle energyCircle = new Circle(100);
        energyCircle.setFill(Color.TRANSPARENT);
        energyCircle.setStroke(Color.CRIMSON);
        energyCircle.setStrokeWidth(5);
        
        // Aplicar efecto de resplandor
        Glow glow = new Glow(0.8);
        DropShadow shadow = new DropShadow(20, Color.RED);
        shadow.setInput(glow);
        energyCircle.setEffect(shadow);
        
        killEffect.getChildren().add(energyCircle);
        
        // Posicionar sobre el personaje
        double centerX = characterImage.getLayoutX() + characterImage.getFitWidth() / 2;
        double centerY = characterImage.getLayoutY() + characterImage.getFitHeight() / 2;
        
        killEffect.setLayoutX(centerX);
        killEffect.setLayoutY(centerY);
        killEffect.setOpacity(0);
        
        rootPane.getChildren().add(killEffect);
        
        // Texto dramático
        Text killText = new Text("¡EJECUCIÓN INSTANTÁNEA!");
        killText.setFont(Font.font("System", FontWeight.BOLD, 32));
        killText.setFill(Color.RED);
        killText.setStroke(Color.BLACK);
        killText.setStrokeWidth(2);
        
        StackPane textContainer = new StackPane(killText);
        textContainer.setLayoutX(centerX - 150);
        textContainer.setLayoutY(centerY - 150);
        textContainer.setOpacity(0);
        
        rootPane.getChildren().add(textContainer);
        
        // Animaciones
        FadeTransition showEffect = new FadeTransition(Duration.millis(400), killEffect);
        showEffect.setToValue(1.0);
        
        ScaleTransition pulseEffect = new ScaleTransition(Duration.millis(800), energyCircle);
        pulseEffect.setFromX(0.3);
        pulseEffect.setFromY(0.3);
        pulseEffect.setToX(1.5);
        pulseEffect.setToY(1.5);
        
        FadeTransition showText = new FadeTransition(Duration.millis(300), textContainer);
        showText.setToValue(1.0);
        
        // Flash final
        FadeTransition fadeToWhite = new FadeTransition(Duration.millis(200), characterImage);
        fadeToWhite.setToValue(0.2);
        
        // Secuencia completa
        SequentialTransition sequence = new SequentialTransition(
            new ParallelTransition(showEffect, pulseEffect, showText),
            fadeToWhite,
            new PauseTransition(Duration.millis(500))
        );
        
        sequence.setOnFinished(e -> {
            rootPane.getChildren().removeAll(killEffect, textContainer);
            if (onComplete != null) onComplete.run();
        });
        
        sequence.play();
    }
    
    /**
     * Anima un golpe crítico basado en stat
     */
    public void animateStatBasedAttack(PersonajeModel character, Stat baseStat, int damage, boolean isPlayerCharacter) {
        ImageView characterImage = getCharacterImage(isPlayerCharacter);
        if (characterImage == null) return;
        
        // Coordenadas del personaje
        double centerX = characterImage.getLayoutX() + characterImage.getFitWidth() / 2;
        double centerY = characterImage.getLayoutY() + characterImage.getFitHeight() / 2;
        
        // Colores según la stat base
        Color effectColor;
        String statName;
        
        switch (baseStat) {
            case FUERZA:
                effectColor = Color.ORANGERED;
                statName = "FUERZA";
                break;
            case VELOCIDAD:
                effectColor = Color.LIGHTBLUE;
                statName = "VELOCIDAD";
                break;
            case PODER:
                effectColor = Color.PURPLE;
                statName = "PODER";
                break;
            default:
                effectColor = Color.GOLD;
                statName = "ESPECIAL";
        }
        
        // Crear efecto de impacto
        Circle impactCircle = new Circle(50);
        impactCircle.setFill(Color.TRANSPARENT);
        impactCircle.setStroke(effectColor);
        impactCircle.setStrokeWidth(3);
        impactCircle.setEffect(new Glow(0.5));
        
        StackPane effectContainer = new StackPane(impactCircle);
        effectContainer.setLayoutX(centerX);
        effectContainer.setLayoutY(centerY);
        effectContainer.setOpacity(0);
        
        rootPane.getChildren().add(effectContainer);
        
        // Texto de daño y tipo de ataque
        Text damageText = new Text(damage + " - " + statName);
        damageText.setFont(Font.font("System", FontWeight.BOLD, 24));
        damageText.setFill(effectColor);
        damageText.setStroke(Color.BLACK);
        damageText.setStrokeWidth(1.5);
        
        StackPane textContainer = new StackPane(damageText);
        textContainer.setLayoutX(centerX - 60);
        textContainer.setLayoutY(centerY - 80);
        textContainer.setOpacity(0);
        
        rootPane.getChildren().add(textContainer);
        
        // Animaciones
        FadeTransition showEffect = new FadeTransition(Duration.millis(200), effectContainer);
        showEffect.setToValue(1.0);
        
        ScaleTransition pulseEffect = new ScaleTransition(Duration.millis(500), impactCircle);
        pulseEffect.setFromX(0.5);
        pulseEffect.setFromY(0.5);
        pulseEffect.setToX(1.2);
        pulseEffect.setToY(1.2);
        
        FadeTransition fadeEffect = new FadeTransition(Duration.millis(300), effectContainer);
        fadeEffect.setFromValue(1.0);
        fadeEffect.setToValue(0.0);
        fadeEffect.setDelay(Duration.millis(300));
        
        // Texto
        FadeTransition showText = new FadeTransition(Duration.millis(200), textContainer);
        showText.setToValue(1.0);
        
        TranslateTransition floatText = new TranslateTransition(Duration.millis(1000), textContainer);
        floatText.setByY(-40);
        
        FadeTransition fadeText = new FadeTransition(Duration.millis(500), textContainer);
        fadeText.setFromValue(1.0);
        fadeText.setToValue(0.0);
        fadeText.setDelay(Duration.millis(700));
        
        // Sacudir al personaje
        TranslateTransition shakeCharacter = new TranslateTransition(Duration.millis(50), characterImage);
        shakeCharacter.setFromX(0);
        shakeCharacter.setByX(isPlayerCharacter ? -8 : 8);
        shakeCharacter.setCycleCount(5);
        shakeCharacter.setAutoReverse(true);
        
        // Secuencia completa
        ParallelTransition allEffects = new ParallelTransition(
            showEffect, pulseEffect, fadeEffect, showText, floatText, fadeText, shakeCharacter
        );
        
        allEffects.setOnFinished(e -> {
            rootPane.getChildren().removeAll(effectContainer, textContainer);
        });
        
        allEffects.play();
    }
    
    /**
     * Obtiene la imagen del personaje según el bando
     */
    private ImageView getCharacterImage(boolean isPlayerCharacter) {
        return isPlayerCharacter ? 
                (ImageView) rootPane.lookup("#imgPersonajeJugador") : 
                (ImageView) rootPane.lookup("#imgPersonajeIA");
    }
}