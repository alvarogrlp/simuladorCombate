package es.alvarogrlp.marvelsimu.backend.combat.animation;

import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
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

/**
 * Maneja los efectos visuales especiales durante el combate
 */
public class VisualEffectsManager {
    
    private AnchorPane rootPane;
    
    public VisualEffectsManager(AnchorPane rootPane) {
        this.rootPane = rootPane;
    }
    
    /**
     * Muestra un efecto de evasión
     */
    public void showEvasionEffect(PersonajeModel character, boolean isPlayerAttack) {
        // Determinar posición del efecto basado en la imagen del personaje
        ImageView characterImage = getCharacterImage(isPlayerAttack);
        if (characterImage == null) return;
        
        double x = characterImage.localToScene(characterImage.getBoundsInLocal()).getMinX() + 
                   characterImage.getFitWidth() / 2 - 50;
        double y = characterImage.localToScene(characterImage.getBoundsInLocal()).getMinY() + 80;
        
        // Crear texto
        Text dodgeText = new Text("¡EVITADO!");
        dodgeText.setFont(Font.font("System", FontWeight.BOLD, 28));
        dodgeText.setFill(Color.LIGHTBLUE);
        dodgeText.setStroke(Color.DARKBLUE);
        dodgeText.setStrokeWidth(1.5);
        
        StackPane effectContainer = new StackPane(dodgeText);
        AnchorPane.setLeftAnchor(effectContainer, x);
        AnchorPane.setTopAnchor(effectContainer, y);
        
        rootPane.getChildren().add(effectContainer);
        
        // Animar aparición y desaparición
        TranslateTransition moveUp = new TranslateTransition(Duration.millis(800), effectContainer);
        moveUp.setByY(-30);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), effectContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), effectContainer);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.millis(400));
        
        ParallelTransition parallelFade = new ParallelTransition(moveUp, fadeIn, fadeOut);
        parallelFade.setOnFinished(e -> rootPane.getChildren().remove(effectContainer));
        parallelFade.play();
        
        // Efecto en el personaje
        if (characterImage != null) {
            TranslateTransition dodge = new TranslateTransition(Duration.millis(200), characterImage);
            dodge.setByY(-20);
            dodge.setCycleCount(2);
            dodge.setAutoReverse(true);
            dodge.play();
        }
    }
    
    /**
     * Muestra un efecto de reducción de daño
     */
    public void showDamageReductionEffect(PersonajeModel character, boolean isPlayerAttack) {
        ImageView characterImage = getCharacterImage(isPlayerAttack);
        if (characterImage == null) return;
        
        // Crear un escudo visual
        Circle shield = new Circle(60);
        shield.setFill(Color.TRANSPARENT);
        shield.setStroke(Color.rgb(100, 200, 255, 0.7));
        shield.setStrokeWidth(4);
        
        // Coordenadas basadas en la posición real del personaje
        double centerX = characterImage.localToScene(characterImage.getBoundsInLocal()).getMinX() + 
                        characterImage.getFitWidth() / 2;
        double centerY = characterImage.localToScene(characterImage.getBoundsInLocal()).getMinY() + 
                        characterImage.getFitHeight() / 2;
        
        StackPane shieldContainer = new StackPane(shield);
        AnchorPane.setLeftAnchor(shieldContainer, centerX - 60);
        AnchorPane.setTopAnchor(shieldContainer, centerY - 60);
        
        rootPane.getChildren().add(shieldContainer);
        
        // Efectos de brillo en el personaje
        Glow glow = new Glow(0.5);
        characterImage.setEffect(glow);
        
        // Animar escudo
        ScaleTransition pulseShield = new ScaleTransition(Duration.millis(300), shield);
        pulseShield.setFromX(0.8);
        pulseShield.setFromY(0.8);
        pulseShield.setToX(1.2);
        pulseShield.setToY(1.2);
        pulseShield.setCycleCount(2);
        pulseShield.setAutoReverse(true);
        
        FadeTransition fadeShield = new FadeTransition(Duration.millis(800), shieldContainer);
        fadeShield.setFromValue(1.0);
        fadeShield.setToValue(0.0);
        fadeShield.setDelay(Duration.millis(600));
        
        ParallelTransition animation = new ParallelTransition(pulseShield, fadeShield);
        animation.setOnFinished(e -> {
            rootPane.getChildren().remove(shieldContainer);
            characterImage.setEffect(null);
        });
        
        animation.play();
    }
    
    /**
     * Muestra un efecto de regeneración de vida
     */
    public void showRegenerationEffect(PersonajeModel character, boolean isPlayerAttack) {
        ImageView characterImage = getCharacterImage(isPlayerAttack);
        if (characterImage == null) return;
        
        // Crear las partículas de curación
        StackPane healContainer = new StackPane();
        
        // Coordenadas basadas en la posición real del personaje
        double centerX = characterImage.localToScene(characterImage.getBoundsInLocal()).getMinX() + 
                        characterImage.getFitWidth() / 2;
        double centerY = characterImage.localToScene(characterImage.getBoundsInLocal()).getMinY() + 
                        characterImage.getFitHeight() / 2;
        
        AnchorPane.setLeftAnchor(healContainer, centerX - 60);
        AnchorPane.setTopAnchor(healContainer, centerY - 60);
        
        for (int i = 0; i < 8; i++) {
            Circle healParticle = new Circle(Math.random() * 5 + 3);
            healParticle.setFill(Color.rgb(100, 255, 100));
            
            // Posicionar partículas alrededor del personaje
            double angle = Math.random() * 360;
            double radius = Math.random() * 50 + 30;
            double particleX = Math.cos(Math.toRadians(angle)) * radius;
            double particleY = Math.sin(Math.toRadians(angle)) * radius;
            
            healParticle.setTranslateX(particleX);
            healParticle.setTranslateY(particleY);
            
            healContainer.getChildren().add(healParticle);
            
            // Animar partícula moviéndose hacia el centro
            TranslateTransition moveToCenter = new TranslateTransition(Duration.millis(800 + Math.random() * 400), healParticle);
            moveToCenter.setToX(0);
            moveToCenter.setToY(0);
            
            ScaleTransition shrink = new ScaleTransition(Duration.millis(800 + Math.random() * 400), healParticle);
            shrink.setToX(0.1);
            shrink.setToY(0.1);
            
            ParallelTransition animation = new ParallelTransition(moveToCenter, shrink);
            animation.play();
        }
        
        rootPane.getChildren().add(healContainer);
        
        // Efecto de brillo en el personaje
        Bloom bloom = new Bloom(0.4);
        Glow glow = new Glow(0.6);
        glow.setInput(bloom);
        
        characterImage.setEffect(glow);
        
        // Texto de regeneración (ajustado para aparecer sobre el personaje)
        Text healText = new Text("+" + character.getVida() * character.getPasivaValor() / 100);
        healText.setFont(Font.font("System", FontWeight.BOLD, 24));
        healText.setFill(Color.rgb(50, 200, 50));
        healText.setStroke(Color.rgb(0, 100, 0));
        healText.setStrokeWidth(1);
        
        StackPane textContainer = new StackPane(healText);
        AnchorPane.setLeftAnchor(textContainer, centerX - 20);
        AnchorPane.setTopAnchor(textContainer, centerY - 80);
        
        rootPane.getChildren().add(textContainer);
        
        // Animar texto
        TranslateTransition moveTextUp = new TranslateTransition(Duration.millis(1000), textContainer);
        moveTextUp.setByY(-30);
        
        FadeTransition fadeTextOut = new FadeTransition(Duration.millis(1000), textContainer);
        fadeTextOut.setFromValue(1.0);
        fadeTextOut.setToValue(0.0);
        fadeTextOut.setDelay(Duration.millis(500));
        
        // Eliminar efectos después de un tiempo
        FadeTransition fadeHealContainer = new FadeTransition(Duration.millis(600), healContainer);
        fadeHealContainer.setFromValue(1.0);
        fadeHealContainer.setToValue(0.0);
        fadeHealContainer.setDelay(Duration.millis(1000));
        
        ParallelTransition finalAnimation = new ParallelTransition(moveTextUp, fadeTextOut, fadeHealContainer);
        finalAnimation.setOnFinished(e -> {
            rootPane.getChildren().removeAll(healContainer, textContainer);
            characterImage.setEffect(null);
        });
        
        finalAnimation.play();
    }
    
    /**
     * Muestra un efecto de daño verdadero
     */
    public void showTrueDamageEffect(PersonajeModel character, boolean isPlayerAttack) {
        ImageView characterImage = getCharacterImage(isPlayerAttack);
        if (characterImage == null) return;
        
        // Crear efecto de brillo
        DropShadow glow = new DropShadow();
        glow.setColor(Color.WHITE);
        glow.setRadius(30);
        glow.setSpread(0.7);
        
        // Aplicar efecto temporalmente
        characterImage.setEffect(glow);
        
        // Crear un destello de luz
        Rectangle flash = new Rectangle(characterImage.getBoundsInParent().getWidth() + 40, 
                                      characterImage.getBoundsInParent().getHeight() + 40);
        flash.setFill(Color.WHITE);
        
        double x, y;
        if (isPlayerAttack) {
            // Destello alrededor del enemigo
            x = 540;
            y = 240;
        } else {
            // Destello alrededor del jugador
            x = 140;
            y = 240;
        }
        
        AnchorPane.setLeftAnchor(flash, x);
        AnchorPane.setTopAnchor(flash, y);
        
        rootPane.getChildren().add(flash);
        
        // Animar destello
        FadeTransition flashFade = new FadeTransition(Duration.millis(200), flash);
        flashFade.setFromValue(0.8);
        flashFade.setToValue(0.0);
        
        // Sacudir personaje violentamente
        TranslateTransition shakeX = new TranslateTransition(Duration.millis(50), characterImage);
        shakeX.setFromX(0);
        shakeX.setByX(isPlayerAttack ? -20 : 20);
        shakeX.setCycleCount(6);
        shakeX.setAutoReverse(true);
        
        // Eliminar efectos
        SequentialTransition sequence = new SequentialTransition(flashFade, shakeX);
        sequence.setOnFinished(e -> {
            rootPane.getChildren().remove(flash);
            characterImage.setEffect(null);
        });
        
        sequence.play();
    }
    
    /**
     * Obtiene la imagen del personaje según quién recibe el efecto
     */
    private ImageView getCharacterImage(boolean isPlayerAttack) {
        ImageView characterImage;
        if (isPlayerAttack) {
            // Efecto en el personaje enemigo
            characterImage = (ImageView) rootPane.lookup("#imgPersonajeIA");
        } else {
            // Efecto en el personaje del jugador
            characterImage = (ImageView) rootPane.lookup("#imgPersonajeJugador");
        }
        return characterImage;
    }
}