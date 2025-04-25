package es.alvarogrlp.marvelsimu.backend.combat.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.alvarogrlp.marvelsimu.backend.combat.model.Stat;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * Clase especializada en efectos visuales para habilidades especiales
 */
public class SpecialEffectsAnimator {
    
    private AnchorPane rootPane;
    private Random random = new Random();
    
    public SpecialEffectsAnimator(AnchorPane rootPane) {
        this.rootPane = rootPane;
    }
    
    /**
     * Anima una transformación de personaje
     * @param character Personaje que se transforma
     * @param isPlayerCharacter Si es el personaje del jugador
     * @param onComplete Acción a ejecutar al completar
     */
    public void animateTransformation(PersonajeModel character, boolean isPlayerCharacter, Runnable onComplete) {
        ImageView characterImage = getCharacterImage(isPlayerCharacter);
        if (characterImage == null) {
            if (onComplete != null) onComplete.run();
            return;
        }
        
        // Guardar la posición original para restaurarla después
        double originalX = characterImage.getLayoutX();
        double originalY = characterImage.getLayoutY();
        
        // Crear un contenedor para el efecto de luz brillante que rodeará al personaje
        StackPane transformEffect = new StackPane();
        Circle glowCircle = new Circle(150);
        glowCircle.setFill(Color.TRANSPARENT);
        glowCircle.setStroke(Color.WHITE);
        glowCircle.setStrokeWidth(5);
        glowCircle.setEffect(new Glow(0.8));
        
        transformEffect.getChildren().add(glowCircle);
        
        // Añadir líneas de energía que fluyen hacia el personaje
        int numLines = 12;
        for (int i = 0; i < numLines; i++) {
            double angle = 2 * Math.PI * i / numLines;
            double startX = 250 * Math.cos(angle);
            double startY = 250 * Math.sin(angle);
            
            Line energyLine = createEnergyLine(0, 0, startX, startY);
            transformEffect.getChildren().add(energyLine);
        }
        
        // Posicionar efecto sobre el personaje
        double centerX = characterImage.getLayoutX() + characterImage.getFitWidth() / 2;
        double centerY = characterImage.getLayoutY() + characterImage.getFitHeight() / 2;
        transformEffect.setLayoutX(centerX);
        transformEffect.setLayoutY(centerY);
        transformEffect.setOpacity(0);
        
        // Añadir a la escena
        rootPane.getChildren().add(transformEffect);
        
        // Crear texto de transformación
        Text transformText = new Text("¡TRANSFORMACIÓN!");
        transformText.setFont(Font.font("System", FontWeight.BOLD, 42));
        transformText.setFill(Color.WHITE);
        transformText.setStroke(Color.PURPLE);
        transformText.setStrokeWidth(2);
        transformText.setEffect(new Glow(0.8));
        
        StackPane textContainer = new StackPane(transformText);
        textContainer.setLayoutX(centerX - 150);
        textContainer.setLayoutY(centerY - 200);
        textContainer.setOpacity(0);
        
        rootPane.getChildren().add(textContainer);
        
        // Efectos para el personaje
        Bloom bloom = new Bloom(0.7);
        Glow glow = new Glow(0.8);
        DropShadow shadow = new DropShadow(20, Color.WHITE);
        shadow.setInput(glow);
        glow.setInput(bloom);
        
        // Secuencia de animación
        FadeTransition fadeInEffect = new FadeTransition(Duration.millis(500), transformEffect);
        fadeInEffect.setToValue(1.0);
        
        FadeTransition fadeInText = new FadeTransition(Duration.millis(300), textContainer);
        fadeInText.setToValue(1.0);
        
        ScaleTransition pulseEffect = new ScaleTransition(Duration.millis(1500), transformEffect);
        pulseEffect.setFromX(0.5);
        pulseEffect.setFromY(0.5);
        pulseEffect.setToX(1.2);
        pulseEffect.setToY(1.2);
        pulseEffect.setCycleCount(2);
        pulseEffect.setAutoReverse(true);
        
        // Rotar el círculo
        Timeline rotateTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glowCircle.rotateProperty(), 0)),
            new KeyFrame(Duration.millis(2000), new KeyValue(glowCircle.rotateProperty(), 360))
        );
        rotateTimeline.setCycleCount(2);
        
        // Animar personaje
        ScaleTransition characterScale = new ScaleTransition(Duration.millis(1000), characterImage);
        characterScale.setFromX(1.0);
        characterScale.setFromY(1.0);
        characterScale.setToX(1.3);
        characterScale.setToY(1.3);
        characterScale.setCycleCount(2);
        characterScale.setAutoReverse(true);
        
        // Aplicar efecto
        characterImage.setEffect(shadow);
        
        // Flash final
        Rectangle flash = new Rectangle(rootPane.getWidth(), rootPane.getHeight());
        flash.setFill(Color.WHITE);
        flash.setOpacity(0);
        
        rootPane.getChildren().add(flash);
        
        FadeTransition flashIn = new FadeTransition(Duration.millis(200), flash);
        flashIn.setToValue(0.9);
        
        FadeTransition flashOut = new FadeTransition(Duration.millis(500), flash);
        flashOut.setFromValue(0.9);
        flashOut.setToValue(0);
        
        // Ocultar personaje temporalmente durante la transformación
        FadeTransition hideCharacter = new FadeTransition(Duration.millis(100), characterImage);
        hideCharacter.setToValue(0);
        
        FadeTransition showCharacter = new FadeTransition(Duration.millis(500), characterImage);
        showCharacter.setToValue(1.0);
        
        // Eliminar y limpiar efectos
        FadeTransition fadeOutEffect = new FadeTransition(Duration.millis(500), transformEffect);
        fadeOutEffect.setToValue(0);
        
        FadeTransition fadeOutText = new FadeTransition(Duration.millis(300), textContainer);
        fadeOutText.setToValue(0);
        
        // Secuencia completa
        SequentialTransition sequence = new SequentialTransition(
            new ParallelTransition(fadeInEffect, fadeInText),
            new ParallelTransition(pulseEffect, rotateTimeline, characterScale),
            hideCharacter,
            flashIn,
            flashOut,
            showCharacter,
            new ParallelTransition(fadeOutEffect, fadeOutText)
        );
        
        sequence.setOnFinished(e -> {
            // Limpiar
            rootPane.getChildren().removeAll(transformEffect, textContainer, flash);
            characterImage.setEffect(null);
            characterImage.setScaleX(1.0);
            characterImage.setScaleY(1.0);
            characterImage.setOpacity(1.0);
            
            // Completar
            if (onComplete != null) {
                onComplete.run();
            }
        });
        
        sequence.play();
    }
    
    /**
     * Anima un efecto de stat modificado (buff/debuff)
     */
    public void animateStatModifier(PersonajeModel character, Stat stat, double multiplier, boolean isPlayerCharacter) {
        ImageView characterImage = getCharacterImage(isPlayerCharacter);
        if (characterImage == null) return;
        
        // Determinar si es buff o debuff
        boolean isBuff = multiplier > 0;
        
        // Crear contenedor para el efecto
        StackPane effectContainer = new StackPane();
        
        // Colores según tipo
        Color primaryColor;
        Color secondaryColor;
        String statName;
        
        switch (stat) {
            case FUERZA:
                primaryColor = isBuff ? Color.ORANGERED : Color.BROWN;
                secondaryColor = isBuff ? Color.YELLOW : Color.DARKRED;
                statName = "FUERZA";
                break;
            case VELOCIDAD:
                primaryColor = isBuff ? Color.LIGHTBLUE : Color.NAVY;
                secondaryColor = isBuff ? Color.WHITE : Color.DARKBLUE;
                statName = "VELOCIDAD";
                break;
            case PODER:
                primaryColor = isBuff ? Color.PURPLE : Color.DARKVIOLET;
                secondaryColor = isBuff ? Color.PINK : Color.INDIGO;
                statName = "PODER";
                break;
            default:
                primaryColor = isBuff ? Color.GREEN : Color.RED;
                secondaryColor = isBuff ? Color.LIGHTGREEN : Color.DARKRED;
                statName = "STATS";
        }
        
        // Crear círculo de aura
        Circle aura = new Circle(60);
        aura.setFill(Color.TRANSPARENT);
        aura.setStroke(primaryColor);
        aura.setStrokeWidth(3);
        aura.setEffect(new Glow(0.7));
        
        effectContainer.getChildren().add(aura);
        
        // Añadir partículas
        for (int i = 0; i < 12; i++) {
            Circle particle = new Circle(random.nextDouble() * 4 + 2);
            particle.setFill(secondaryColor);
            
            // Posición relativa en círculo
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = random.nextDouble() * 40 + 20;
            particle.setTranslateX(Math.cos(angle) * distance);
            particle.setTranslateY(Math.sin(angle) * distance);
            
            effectContainer.getChildren().add(particle);
            
            // Animar partícula
            double finalDistance = isBuff ? 80 : 20;
            
            Timeline particleTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, 
                    new KeyValue(particle.translateXProperty(), particle.getTranslateX()),
                    new KeyValue(particle.translateYProperty(), particle.getTranslateY())),
                new KeyFrame(Duration.millis(1000), 
                    new KeyValue(particle.translateXProperty(), Math.cos(angle) * finalDistance),
                    new KeyValue(particle.translateYProperty(), Math.sin(angle) * finalDistance))
            );
            particleTimeline.setCycleCount(Animation.INDEFINITE);
            particleTimeline.setAutoReverse(true);
            particleTimeline.play();
        }
        
        // Texto de modificación
        Text modText = new Text((multiplier > 0 ? "+" : "") + 
                               (int)(multiplier * 100) + "% " + statName);
        modText.setFont(Font.font("System", FontWeight.BOLD, 18));
        modText.setFill(primaryColor);
        modText.setStroke(Color.BLACK);
        modText.setStrokeWidth(1);
        
        // Colocar texto encima del personaje
        StackPane textContainer = new StackPane(modText);
        
        // Posicionar efectos
        double centerX = characterImage.getLayoutX() + characterImage.getFitWidth() / 2;
        double centerY = characterImage.getLayoutY() + characterImage.getFitHeight() / 2;
        
        effectContainer.setLayoutX(centerX);
        effectContainer.setLayoutY(centerY);
        
        textContainer.setLayoutX(centerX - 60);
        textContainer.setLayoutY(centerY - 80);
        
        rootPane.getChildren().addAll(effectContainer, textContainer);
        
        // Aplicar efecto al personaje
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(isBuff ? 0.2 : -0.2);
        colorAdjust.setSaturation(isBuff ? 0.3 : -0.2);
        
        DropShadow glow = new DropShadow(15, primaryColor);
        glow.setInput(colorAdjust);
        
        characterImage.setEffect(glow);
        
        // Animaciones
        ScaleTransition pulseAura = new ScaleTransition(Duration.millis(1000), aura);
        pulseAura.setFromX(0.8);
        pulseAura.setFromY(0.8);
        pulseAura.setToX(1.2);
        pulseAura.setToY(1.2);
        pulseAura.setCycleCount(2);
        pulseAura.setAutoReverse(true);
        
        // Animar texto
        TranslateTransition floatText = new TranslateTransition(Duration.millis(1000), textContainer);
        floatText.setByY(-30);
        
        FadeTransition fadeText = new FadeTransition(Duration.millis(1000), textContainer);
        fadeText.setFromValue(1.0);
        fadeText.setToValue(0.0);
        fadeText.setDelay(Duration.millis(1000));
        
        // Desvanecer efecto
        FadeTransition fadeEffect = new FadeTransition(Duration.millis(800), effectContainer);
        fadeEffect.setFromValue(1.0);
        fadeEffect.setToValue(0.0);
        fadeEffect.setDelay(Duration.millis(2000));
        
        // Ejecutar animaciones
        ParallelTransition allAnimation = new ParallelTransition(
            pulseAura, floatText, fadeText, fadeEffect
        );
        
        allAnimation.setOnFinished(e -> {
            rootPane.getChildren().removeAll(effectContainer, textContainer);
            characterImage.setEffect(null);
        });
        
        allAnimation.play();
    }
    
    /**
     * Anima el efecto de bloqueo de curación
     * @param character Personaje afectado
     * @param isPlayerCharacter Indica si es el personaje del jugador (true) o de la IA (false)
     */
    public void animateHealBlock(PersonajeModel character, boolean isPlayerCharacter) {
        ImageView characterImage = isPlayerCharacter ? 
                (ImageView) rootPane.lookup("#imgPersonajeJugador") : 
                (ImageView) rootPane.lookup("#imgPersonajeIA");
        
        if (characterImage == null) return;
        
        // Coordenadas centradas en el personaje
        double centerX = characterImage.getLayoutX() + characterImage.getFitWidth() / 2;
        double centerY = characterImage.getLayoutY() + characterImage.getFitHeight() / 2;
        
        // Crear un signo de curación tachado (cruz médica con X encima)
        StackPane healBlockIcon = new StackPane();
        
        // Cruz médica (roja)
        Circle circle = new Circle(20, Color.TRANSPARENT);
        circle.setStroke(Color.RED);
        circle.setStrokeWidth(2);
        
        Rectangle verticalRect = new Rectangle(8, 30, Color.RED);
        Rectangle horizontalRect = new Rectangle(30, 8, Color.RED);
        
        Group crossGroup = new Group(circle, verticalRect, horizontalRect);
        
        // Línea diagonal que cruza (símbolo de prohibición)
        Line diagonalLine = new Line(-20, -20, 20, 20);
        diagonalLine.setStroke(Color.RED);
        diagonalLine.setStrokeWidth(4);
        
        healBlockIcon.getChildren().addAll(crossGroup, diagonalLine);
        healBlockIcon.setLayoutX(centerX - 20);
        healBlockIcon.setLayoutY(centerY - 50);
        healBlockIcon.setOpacity(0);
        
        // Añadir un efecto de brillo
        DropShadow glow = new DropShadow();
        glow.setColor(Color.RED);
        glow.setRadius(15);
        healBlockIcon.setEffect(glow);
        
        rootPane.getChildren().add(healBlockIcon);
        
        // Texto descriptivo
        Text blockText = new Text("¡CURAS BLOQUEADAS!");
        blockText.setFont(Font.font("System", FontWeight.BOLD, 16));
        blockText.setFill(Color.RED);
        blockText.setStroke(Color.BLACK);
        blockText.setStrokeWidth(0.5);
        blockText.setTextAlignment(TextAlignment.CENTER);
        
        StackPane textContainer = new StackPane(blockText);
        textContainer.setLayoutX(centerX - 80);
        textContainer.setLayoutY(centerY - 90);
        textContainer.setOpacity(0);
        
        rootPane.getChildren().add(textContainer);
        
        // Animación de aparición del icono
        FadeTransition fadeInIcon = new FadeTransition(Duration.millis(400), healBlockIcon);
        fadeInIcon.setToValue(1.0);
        
        // Animación de aparición del texto
        FadeTransition fadeInText = new FadeTransition(Duration.millis(400), textContainer);
        fadeInText.setToValue(1.0);
        
        // Animación de pulso para el icono
        Timeline pulseIcon = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(healBlockIcon.scaleXProperty(), 1.0), 
                                   new KeyValue(healBlockIcon.scaleYProperty(), 1.0)),
            new KeyFrame(Duration.millis(500), new KeyValue(healBlockIcon.scaleXProperty(), 1.3), 
                                        new KeyValue(healBlockIcon.scaleYProperty(), 1.3)),
            new KeyFrame(Duration.millis(1000), new KeyValue(healBlockIcon.scaleXProperty(), 1.0), 
                                     new KeyValue(healBlockIcon.scaleYProperty(), 1.0))
        );
        pulseIcon.setCycleCount(2);
        
        // Animación de desaparición del icono
        FadeTransition fadeOutIcon = new FadeTransition(Duration.millis(500), healBlockIcon);
        fadeOutIcon.setToValue(0.0);
        
        // Animación de desaparición del texto
        FadeTransition fadeOutText = new FadeTransition(Duration.millis(500), textContainer);
        fadeOutText.setToValue(0.0);
        
        // Agregar un efecto rojo parpadeante al personaje para indicar el estado
        ColorAdjust redTint = new ColorAdjust();
        redTint.setHue(-0.5); // Tinte rojo
        redTint.setSaturation(0.2);
        
        // Pulso del efecto
        Timeline pulseEffect = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(redTint.saturationProperty(), 0.2)),
            new KeyFrame(Duration.millis(500), new KeyValue(redTint.saturationProperty(), 0.5)),
            new KeyFrame(Duration.millis(1000), new KeyValue(redTint.saturationProperty(), 0.2))
        );
        pulseEffect.setCycleCount(2);
        
        // Secuencia completa de animación
        SequentialTransition sequence = new SequentialTransition(
            new ParallelTransition(fadeInIcon, fadeInText),
            new ParallelTransition(pulseIcon, pulseEffect),
            new ParallelTransition(fadeOutIcon, fadeOutText)
        );
        
        // Aplicar el efecto rojo al personaje
        characterImage.setEffect(redTint);
        
        sequence.setOnFinished(e -> {
            rootPane.getChildren().removeAll(healBlockIcon, textContainer);
            
            // Dejar un tinte rojo leve persistente para indicar el estado
            ColorAdjust persistentTint = new ColorAdjust();
            persistentTint.setHue(-0.3);
            persistentTint.setSaturation(0.1);
            characterImage.setEffect(persistentTint);
            
            // Programar la eliminación del tinte después de un tiempo
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> {
                FadeTransition fadeEffect = new FadeTransition(Duration.millis(500), characterImage);
                fadeEffect.setOnFinished(evt -> characterImage.setEffect(null));
                fadeEffect.play();
            });
            pause.play();
        });
        
        sequence.play();
    }
    
    /**
     * Anima un efecto de curación bloqueada
     */
    public void animateHealingBlocked(PersonajeModel character, boolean isPlayerCharacter) {
        ImageView characterImage = getCharacterImage(isPlayerCharacter);
        if (characterImage == null) return;
        
        // Coordenadas del personaje
        double centerX = characterImage.getLayoutX() + characterImage.getFitWidth() / 2;
        double centerY = characterImage.getLayoutY() + characterImage.getFitHeight() / 2;
        
        // Crear símbolo de curación bloqueada
        StackPane blockEffect = new StackPane();
        
        // Círculo de curación
        Circle healCircle = new Circle(40);
        healCircle.setFill(Color.TRANSPARENT);
        healCircle.setStroke(Color.DARKGREEN);
        healCircle.setStrokeWidth(3);
        
        // Cruz de curación
        Rectangle verticalLine = new Rectangle(10, 60);
        verticalLine.setFill(Color.GREEN);
        Rectangle horizontalLine = new Rectangle(60, 10);
        horizontalLine.setFill(Color.GREEN);
        
        // Línea diagonal de bloqueo
        Rectangle blockLine = new Rectangle(80, 10);
        blockLine.setFill(Color.RED);
        blockLine.setRotate(45);
        
        blockEffect.getChildren().addAll(healCircle, verticalLine, horizontalLine, blockLine);
        blockEffect.setLayoutX(centerX);
        blockEffect.setLayoutY(centerY);
        blockEffect.setOpacity(0);
        
        rootPane.getChildren().add(blockEffect);
        
        // Texto de bloqueo
        Text blockText = new Text("CURACIÓN BLOQUEADA");
        blockText.setFont(Font.font("System", FontWeight.BOLD, 18));
        blockText.setFill(Color.RED);
        blockText.setStroke(Color.BLACK);
        blockText.setStrokeWidth(1);
        
        StackPane textContainer = new StackPane(blockText);
        textContainer.setLayoutX(centerX - 80);
        textContainer.setLayoutY(centerY - 100);
        textContainer.setOpacity(0);
        
        rootPane.getChildren().add(textContainer);
        
        // Aplicar efecto al personaje
        ColorAdjust colorFilter = new ColorAdjust();
        colorFilter.setSaturation(-0.5);
        colorFilter.setBrightness(-0.2);
        
        // Animaciones
        FadeTransition fadeInEffect = new FadeTransition(Duration.millis(500), blockEffect);
        fadeInEffect.setToValue(1.0);
        
        FadeTransition fadeInText = new FadeTransition(Duration.millis(500), textContainer);
        fadeInText.setToValue(1.0);
        
        ScaleTransition pulseEffect = new ScaleTransition(Duration.millis(800), blockEffect);
        pulseEffect.setFromX(0.5);
        pulseEffect.setFromY(0.5);
        pulseEffect.setToX(1.2);
        pulseEffect.setToY(1.2);
        pulseEffect.setCycleCount(1);
        
        Timeline blockPulse = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(blockLine.opacityProperty(), 1.0)),
            new KeyFrame(Duration.millis(500), new KeyValue(blockLine.opacityProperty(), 0.3)),
            new KeyFrame(Duration.millis(1000), new KeyValue(blockLine.opacityProperty(), 1.0))
        );
        blockPulse.setCycleCount(2);
        
        TranslateTransition moveText = new TranslateTransition(Duration.millis(1000), textContainer);
        moveText.setByY(-30);
        
        FadeTransition fadeOutEffect = new FadeTransition(Duration.millis(500), blockEffect);
        fadeOutEffect.setToValue(0.0);
        fadeOutEffect.setDelay(Duration.millis(2000));
        
        FadeTransition fadeOutText = new FadeTransition(Duration.millis(500), textContainer);
        fadeOutText.setToValue(0.0);
        fadeOutText.setDelay(Duration.millis(2000));
        
        // Secuencia
        SequentialTransition sequence = new SequentialTransition(
            new ParallelTransition(fadeInEffect, fadeInText),
            new ParallelTransition(pulseEffect, blockPulse, moveText),
            new ParallelTransition(fadeOutEffect, fadeOutText)
        );
        
        // Aplicar efecto de desaturación temporalmente
        characterImage.setEffect(colorFilter);
        
        sequence.setOnFinished(e -> {
            rootPane.getChildren().removeAll(blockEffect, textContainer);
            
            // Eliminar efecto de desaturación lentamente
            FadeTransition fadeFilter = new FadeTransition(Duration.millis(500), characterImage);
            fadeFilter.setOnFinished(event -> characterImage.setEffect(null));
            fadeFilter.play();
        });
        
        sequence.play();
    }
    
    /**
     * Anima un efecto de restricción a ataques básicos
     */
    public void animateRestrictedToBasic(PersonajeModel character, boolean isPlayerCharacter) {
        ImageView characterImage = getCharacterImage(isPlayerCharacter);
        if (characterImage == null) return;
        
        // Coordenadas del personaje
        double centerX = characterImage.getLayoutX() + characterImage.getFitWidth() / 2;
        double centerY = characterImage.getLayoutY() + characterImage.getFitHeight() / 2;
        
        // Crear efecto de cadenas
        StackPane restrictEffect = new StackPane();
        
        // Círculo alrededor del personaje
        Circle boundCircle = new Circle(70);
        boundCircle.setFill(Color.TRANSPARENT);
        boundCircle.setStroke(Color.DARKGREY);
        boundCircle.setStrokeWidth(3);
        
        restrictEffect.getChildren().add(boundCircle);
        
        // Añadir cadenas/símbolos de restricción
        for (int i = 0; i < 4; i++) {
            Rectangle chain = new Rectangle(10, 40);
            chain.setFill(Color.SILVER);
            chain.setRotate(i * 90);
            chain.setTranslateX(Math.cos(Math.toRadians(i * 90)) * 40);
            chain.setTranslateY(Math.sin(Math.toRadians(i * 90)) * 40);
            
            Circle link1 = new Circle(4);
            link1.setFill(Color.DARKGREY);
            link1.setTranslateX(Math.cos(Math.toRadians(i * 90)) * 60);
            link1.setTranslateY(Math.sin(Math.toRadians(i * 90)) * 60);
            
            Circle link2 = new Circle(4);
            link2.setFill(Color.DARKGREY);
            link2.setTranslateX(Math.cos(Math.toRadians(i * 90)) * 70);
            link2.setTranslateY(Math.sin(Math.toRadians(i * 90)) * 70);
            
            restrictEffect.getChildren().addAll(chain, link1, link2);
        }
        
        restrictEffect.setLayoutX(centerX);
        restrictEffect.setLayoutY(centerY);
        restrictEffect.setOpacity(0);
        
        // Texto de restricción
        Text restrictText = new Text("HABILIDADES RESTRINGIDAS");
        restrictText.setFont(Font.font("System", FontWeight.BOLD, 18));
        restrictText.setFill(Color.DARKGREY);
        restrictText.setStroke(Color.BLACK);
        restrictText.setStrokeWidth(1);
        
        StackPane textContainer = new StackPane(restrictText);
        textContainer.setLayoutX(centerX - 100);
        textContainer.setLayoutY(centerY - 100);
        textContainer.setOpacity(0);
        
        rootPane.getChildren().addAll(restrictEffect, textContainer);
        
        // Efecto para el personaje
        DropShadow shadow = new DropShadow(10, Color.DARKGRAY);
        ColorAdjust desaturate = new ColorAdjust();
        desaturate.setSaturation(-0.3);
        desaturate.setBrightness(-0.1);
        shadow.setInput(desaturate);
        
        characterImage.setEffect(shadow);
        
        // Animaciones
        FadeTransition fadeInEffect = new FadeTransition(Duration.millis(500), restrictEffect);
        fadeInEffect.setToValue(1.0);
        
        FadeTransition fadeInText = new FadeTransition(Duration.millis(500), textContainer);
        fadeInText.setToValue(1.0);
        
        // Rotar las cadenas para efecto de apriete
        Timeline tightenChains = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(boundCircle.radiusProperty(), 70)),
            new KeyFrame(Duration.millis(1000), new KeyValue(boundCircle.radiusProperty(), 55)),
            new KeyFrame(Duration.millis(2000), new KeyValue(boundCircle.radiusProperty(), 70))
        );
        tightenChains.setCycleCount(1);
        
        TranslateTransition shakeCharacter = new TranslateTransition(Duration.millis(100), characterImage);
        shakeCharacter.setFromX(0);
        shakeCharacter.setByX(5);
        shakeCharacter.setCycleCount(4);
        shakeCharacter.setAutoReverse(true);
        
        TranslateTransition moveText = new TranslateTransition(Duration.millis(1500), textContainer);
        moveText.setByY(-30);
        
        FadeTransition fadeOutEffect = new FadeTransition(Duration.millis(500), restrictEffect);
        fadeOutEffect.setToValue(0.0);
        fadeOutEffect.setDelay(Duration.millis(2000));
        
        FadeTransition fadeOutText = new FadeTransition(Duration.millis(500), textContainer);
        fadeOutText.setToValue(0.0);
        fadeOutText.setDelay(Duration.millis(2000));
        
        // Secuencia completa
        SequentialTransition sequence = new SequentialTransition(
            new ParallelTransition(fadeInEffect, fadeInText),
            new ParallelTransition(tightenChains, shakeCharacter, moveText),
            new ParallelTransition(fadeOutEffect, fadeOutText)
        );
        
        sequence.setOnFinished(e -> {
            rootPane.getChildren().removeAll(restrictEffect, textContainer);
            
            // Eliminar efecto gradualmente
            FadeTransition fadeEffect = new FadeTransition(Duration.millis(500), characterImage);
            fadeEffect.setOnFinished(event -> characterImage.setEffect(null));
            fadeEffect.play();
        });
        
        sequence.play();
    }
    
    /**
     * Anima un efecto de daño reflejado
     */
    public void animateDamageReflection(PersonajeModel attacker, PersonajeModel defender, 
                                      int damageAmount, boolean isPlayerAttacker) {
        ImageView attackerImage = getCharacterImage(isPlayerAttacker);
        ImageView defenderImage = getCharacterImage(!isPlayerAttacker);
        
        if (attackerImage == null || defenderImage == null) return;
        
        // Crear una barrera reflectante
        StackPane mirrorShield = new StackPane();
        
        // Coordenadas del defensor
        double defenderX = defenderImage.getLayoutX() + defenderImage.getFitWidth() / 2;
        double defenderY = defenderImage.getLayoutY() + defenderImage.getFitHeight() / 2;
        
        // Coordenadas del atacante
        double attackerX = attackerImage.getLayoutX() + attackerImage.getFitWidth() / 2;
        double attackerY = attackerImage.getLayoutY() + attackerImage.getFitHeight() / 2;
        
        // Barrera semicircular que enfrenta al atacante
        Rectangle barrier = new Rectangle(100, 150);
        barrier.setArcWidth(100);
        barrier.setArcHeight(150);
        barrier.setFill(Color.TRANSPARENT);
        barrier.setStroke(Color.LIGHTBLUE);
        barrier.setStrokeWidth(3);
        
        // Efectos visuales para la barrera
        Bloom bloom = new Bloom(0.7);
        DropShadow glow = new DropShadow(15, Color.LIGHTSKYBLUE);
        glow.setInput(bloom);
        barrier.setEffect(glow);
        
        mirrorShield.getChildren().add(barrier);
        
        // Rotar para que la parte curva apunte al atacante
        double angle = Math.toDegrees(Math.atan2(attackerY - defenderY, attackerX - defenderX));
        mirrorShield.setRotate(angle);
        
        // Posicionar frente al defensor
        mirrorShield.setLayoutX(defenderX);
        mirrorShield.setLayoutY(defenderY);
        mirrorShield.setOpacity(0);
        
        rootPane.getChildren().add(mirrorShield);
        
        // Texto del daño reflejado
        Text reflectText = new Text("¡REFLEJADO! " + damageAmount);
        reflectText.setFont(Font.font("System", FontWeight.BOLD, 22));
        reflectText.setFill(Color.LIGHTSKYBLUE);
        reflectText.setStroke(Color.BLACK);
        reflectText.setStrokeWidth(1.5);
        reflectText.setTextAlignment(TextAlignment.CENTER);
        
        StackPane textContainer = new StackPane(reflectText);
        textContainer.setLayoutX(attackerX - 60);
        textContainer.setLayoutY(attackerY - 80);
        textContainer.setOpacity(0);
        
        rootPane.getChildren().add(textContainer);
        
        // Animación de la barrera apareciendo
        FadeTransition showBarrier = new FadeTransition(Duration.millis(300), mirrorShield);
        showBarrier.setToValue(1.0);
        
        // Efectos de brillo pulsante
        Timeline pulseBarrier = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(barrier.strokeWidthProperty(), 3)),
            new KeyFrame(Duration.millis(500), new KeyValue(barrier.strokeWidthProperty(), 7)),
            new KeyFrame(Duration.millis(1000), new KeyValue(barrier.strokeWidthProperty(), 3))
        );
        pulseBarrier.setCycleCount(1);
        
        // Crear efecto de "rayo" reflejado
        Path reflectPath = new Path();
        MoveTo moveTo = new MoveTo(defenderX, defenderY);
        LineTo lineTo = new LineTo(attackerX, attackerY);
        reflectPath.getElements().addAll(moveTo, lineTo);
        
        Circle reflectBolt = new Circle(8, Color.DEEPSKYBLUE);
        reflectBolt.setEffect(new Glow(0.8));
        reflectBolt.setOpacity(0);
        
        rootPane.getChildren().add(reflectBolt);
        
        // Animar el rayo desde el defensor al atacante
        PathTransition boltTravel = new PathTransition(Duration.millis(500), reflectPath, reflectBolt);
        
        // Crear la transición fadeInBolt que faltaba
        FadeTransition fadeInBolt = new FadeTransition(Duration.millis(100), reflectBolt);
        fadeInBolt.setToValue(1.0);
        
        // Crear también fadeOutBolt que se usa más adelante
        FadeTransition fadeOutBolt = new FadeTransition(Duration.millis(200), reflectBolt);
        fadeOutBolt.setToValue(0.0);
        
        // Mostrar texto de daño reflejado
        FadeTransition showText = new FadeTransition(Duration.millis(300), textContainer);
        showText.setToValue(1.0);
        
        TranslateTransition floatText = new TranslateTransition(Duration.millis(1500), textContainer);
        floatText.setByY(-40);
        
        // Efecto de temblor en el atacante
        TranslateTransition shakeAttacker = new TranslateTransition(Duration.millis(50), attackerImage);
        shakeAttacker.setFromX(0);
        shakeAttacker.setByX(isPlayerAttacker ? -10 : 10);
        shakeAttacker.setCycleCount(6);
        shakeAttacker.setAutoReverse(true);
        
        // Efecto en el atacante
        ColorAdjust damageEffect = new ColorAdjust();
        damageEffect.setBrightness(-0.4);
        damageEffect.setContrast(0.4);
        
        // Fade out
        FadeTransition fadeOutBarrier = new FadeTransition(Duration.millis(500), mirrorShield);
        fadeOutBarrier.setToValue(0.0);
        
        FadeTransition fadeOutText = new FadeTransition(Duration.millis(500), textContainer);
        fadeOutText.setToValue(0.0);
        
        // Secuencia completa
        SequentialTransition sequence = new SequentialTransition(
            showBarrier,
            pulseBarrier,
            new ParallelTransition(
                boltTravel,
                fadeInBolt
            ),
            new ParallelTransition(
                shakeAttacker,
                showText,
                fadeOutBolt,
                new Timeline(new KeyFrame(Duration.ZERO, e -> attackerImage.setEffect(damageEffect)))
            ),
            floatText,
            new ParallelTransition(fadeOutBarrier, fadeOutText)
        );
        
        sequence.setOnFinished(e -> {
            rootPane.getChildren().removeAll(mirrorShield, textContainer, reflectBolt);
            
            // Quitar efecto del atacante gradualmente
            FadeTransition fadeEffect = new FadeTransition(Duration.millis(500), attackerImage);
            fadeEffect.setOnFinished(event -> attackerImage.setEffect(null));
            fadeEffect.play();
        });
        
        sequence.play();
    }
    
    /**
     * Anima saltar turnos del enemigo (efecto de tiempo)
     */
    public void animateTimeSkip(PersonajeModel caster, int turnsSkipped, boolean isPlayerCaster) {
        ImageView casterImage = getCharacterImage(isPlayerCaster);
        ImageView targetImage = getCharacterImage(!isPlayerCaster);
        
        if (casterImage == null || targetImage == null) return;
        
        // Coordenadas
        double casterX = casterImage.getLayoutX() + casterImage.getFitWidth() / 2;
        double casterY = casterImage.getLayoutY() + casterImage.getFitHeight() / 2;
        
        double targetX = targetImage.getLayoutX() + targetImage.getFitWidth() / 2;
        double targetY = targetImage.getLayoutY() + targetImage.getFitHeight() / 2;
        
        // Crear efecto de reloj/tiempo
        StackPane timeEffect = new StackPane();
        
        // Círculo exterior del reloj
        Circle clockOutline = new Circle(60);
        clockOutline.setFill(Color.TRANSPARENT);
        clockOutline.setStroke(Color.GOLDENROD);
        clockOutline.setStrokeWidth(3);
        
        // Añadir manecillas del reloj
        Rectangle hourHand = new Rectangle(5, 30);
        hourHand.setFill(Color.GOLD);
        hourHand.setTranslateY(-15);
        
        Rectangle minuteHand = new Rectangle(3, 40);
        minuteHand.setFill(Color.GOLD);
        minuteHand.setTranslateY(-20);
        
        // Punto central
        Circle centerDot = new Circle(5, Color.GOLD);
        
        timeEffect.getChildren().addAll(clockOutline, hourHand, minuteHand, centerDot);
        timeEffect.setLayoutX(targetX);
        timeEffect.setLayoutY(targetY);
        timeEffect.setOpacity(0);
        
        rootPane.getChildren().add(timeEffect);
        
        // Texto sobre turnos omitidos
        Text skipText = new Text("TURNOS OMITIDOS: " + turnsSkipped);
        skipText.setFont(Font.font("System", FontWeight.BOLD, 22));
        skipText.setFill(Color.GOLD);
        skipText.setStroke(Color.BLACK);
        skipText.setStrokeWidth(1.5);
        
        StackPane textContainer = new StackPane(skipText);
        textContainer.setLayoutX(targetX - 100);
        textContainer.setLayoutY(targetY - 100);
        textContainer.setOpacity(0);
        
        rootPane.getChildren().add(textContainer);
        
        // Rayos de tiempo que emanan del lanzador
        List<Node> timeRays = new ArrayList<>();
        int numRays = 8;
        for (int i = 0; i < numRays; i++) {
            double angle = 2 * Math.PI * i / numRays;
            double rayX = casterX + Math.cos(angle) * 50;
            double rayY = casterY + Math.sin(angle) * 50;
            
            Circle ray = new Circle(5, Color.GOLD);
            ray.setLayoutX(rayX);
            ray.setLayoutY(rayY);
            ray.setOpacity(0);
            
            rootPane.getChildren().add(ray);
            timeRays.add(ray);
        }
        
        // Efecto en el objetivo congelado
        ColorAdjust freezeEffect = new ColorAdjust();
        freezeEffect.setBrightness(-0.2);
        freezeEffect.setSaturation(-0.5);
        freezeEffect.setHue(-0.5); // Tinte azulado
        
        // Efectos para el lanzador
        Bloom timePower = new Bloom(0.7);
        DropShadow timePowerGlow = new DropShadow(20, Color.GOLD);
        timePowerGlow.setInput(timePower);
        
        casterImage.setEffect(timePowerGlow);
        
        // Animaciones
        
        // Hacer brillar al lanzador
        ScaleTransition empowerCaster = new ScaleTransition(Duration.millis(800), casterImage);
        empowerCaster.setToX(1.2);
        empowerCaster.setToY(1.2);
        
        // Mostrar rayos de tiempo
        List<FadeTransition> rayFades = new ArrayList<>();
        for (Node ray : timeRays) {
            FadeTransition fadeRay = new FadeTransition(Duration.millis(300), ray);
            fadeRay.setToValue(1.0);
            rayFades.add(fadeRay);
            
            // También animar movimiento hacia afuera
            double dx = ray.getLayoutX() - casterX;
            double dy = ray.getLayoutY() - casterY;
            double distance = Math.sqrt(dx*dx + dy*dy);
            double unitX = dx / distance;
            double unitY = dy / distance;
            
            TranslateTransition moveRay = new TranslateTransition(Duration.millis(1000), ray);
            moveRay.setByX(unitX * 100);
            moveRay.setByY(unitY * 100);
            moveRay.play();
        }
        
        // Mostrar efecto de reloj
        FadeTransition showClock = new FadeTransition(Duration.millis(500), timeEffect);
        showClock.setToValue(1.0);
        
        // Animar manecillas girando rápido
        Timeline spinHands = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(hourHand.rotateProperty(), 0),
                new KeyValue(minuteHand.rotateProperty(), 0)),
            new KeyFrame(Duration.millis(2000), 
                new KeyValue(hourHand.rotateProperty(), 720),  // 2 vueltas completas
                new KeyValue(minuteHand.rotateProperty(), 4320)) // 12 vueltas completas
        );
        
        // Aplicar efecto congelado al objetivo
        Timeline freezeTarget = new Timeline(
            new KeyFrame(Duration.ZERO, e -> targetImage.setEffect(freezeEffect))
        );
        
        // Mostrar texto
        FadeTransition showText = new FadeTransition(Duration.millis(400), textContainer);
        showText.setToValue(1.0);
        
        TranslateTransition floatText = new TranslateTransition(Duration.millis(1500), textContainer);
        floatText.setByY(-30);
        
        // Fade out de todos los elementos
        FadeTransition fadeOutClock = new FadeTransition(Duration.millis(500), timeEffect);
        fadeOutClock.setToValue(0.0);
        
        FadeTransition fadeOutText = new FadeTransition(Duration.millis(500), textContainer);
        fadeOutText.setToValue(0.0);
        
        List<FadeTransition> rayFadeOuts = new ArrayList<>();
        for (Node ray : timeRays) {
            FadeTransition fadeRay = new FadeTransition(Duration.millis(300), ray);
            fadeRay.setToValue(0.0);
            rayFadeOuts.add(fadeRay);
        }
        
        // Devolver lanzador a tamaño normal
        ScaleTransition normalizeCaster = new ScaleTransition(Duration.millis(500), casterImage);
        normalizeCaster.setToX(1.0);
        normalizeCaster.setToY(1.0);
        
        // Secuencia completa
        SequentialTransition sequence = new SequentialTransition(
            new ParallelTransition(empowerCaster, new ParallelTransition(rayFades.toArray(new Animation[0]))),
            new ParallelTransition(showClock, spinHands, freezeTarget, showText),
            floatText,
            new ParallelTransition(
                fadeOutClock, 
                fadeOutText, 
                new ParallelTransition(rayFadeOuts.toArray(new Animation[0])),
                normalizeCaster
            )
        );
        
        sequence.setOnFinished(e -> {
            // Limpiar escena
            rootPane.getChildren().removeAll(timeEffect, textContainer);
            for (Node ray : timeRays) {
                rootPane.getChildren().remove(ray);
            }
            
            // Mantener el efecto congelado en el enemigo
            casterImage.setEffect(null);
            
            // El efecto congelado se eliminará cuando se procese el siguiente turno
        });
        
        sequence.play();
    }
    
    /**
     * Anima un ataque de daño directo (true damage)
     */
    public void animateTrueDamage(PersonajeModel target, int damageAmount, boolean isPlayerTarget) {
        ImageView targetImage = getCharacterImage(isPlayerTarget);
        if (targetImage == null) return;
        
        // Coordenadas del objetivo
        double targetX = targetImage.getLayoutX() + targetImage.getFitWidth() / 2;
        double targetY = targetImage.getLayoutY() + targetImage.getFitHeight() / 2;
        
        // Crear círculos de daño que convergen en el objetivo
        int numCircles = 8;
        List<Circle> damageCircles = new ArrayList<>();
        
        for (int i = 0; i < numCircles; i++) {
            double angle = 2 * Math.PI * i / numCircles;
            double startX = targetX + Math.cos(angle) * 200;
            double startY = targetY + Math.sin(angle) * 200;
            
            Circle damageCircle = new Circle(random.nextDouble() * 10 + 5);
            damageCircle.setFill(Color.PURPLE);
            damageCircle.setLayoutX(startX);
            damageCircle.setLayoutY(startY);
            damageCircle.setOpacity(0.8);
            
            DropShadow glow = new DropShadow(10, Color.MAGENTA);
            damageCircle.setEffect(glow);
            
            rootPane.getChildren().add(damageCircle);
            damageCircles.add(damageCircle);
        }
        
        // Texto de daño verdadero
        Text damageText = new Text("DAÑO DIRECTO: " + damageAmount);
        damageText.setFont(Font.font("System", FontWeight.BOLD, 24));
        damageText.setFill(Color.MAGENTA);
        damageText.setStroke(Color.BLACK);
        damageText.setStrokeWidth(2);
        
        StackPane textContainer = new StackPane(damageText);
        textContainer.setLayoutX(targetX - 90);
        textContainer.setLayoutY(targetY - 80);
        textContainer.setOpacity(0);
        
        rootPane.getChildren().add(textContainer);
        
        // Destello de impacto
        Circle impactFlash = new Circle(60);
        impactFlash.setFill(Color.TRANSPARENT);
        impactFlash.setStroke(Color.PURPLE);
        impactFlash.setStrokeWidth(5);
        impactFlash.setLayoutX(targetX);
        impactFlash.setLayoutY(targetY);
        impactFlash.setOpacity(0);
        
        rootPane.getChildren().add(impactFlash);
        
        // Efecto para el objetivo
        ColorAdjust trueHit = new ColorAdjust();
        trueHit.setHue(-0.3);
        trueHit.setBrightness(0.4);
        trueHit.setContrast(0.8);
        
        // Animar círculos de daño convergiendo en el objetivo
        List<TranslateTransition> circleMovements = new ArrayList<>();
        for (Circle circle : damageCircles) {
            TranslateTransition moveCircle = new TranslateTransition(
                Duration.millis(700 + random.nextInt(300)), circle);
            
            double dx = targetX - circle.getLayoutX();
            double dy = targetY - circle.getLayoutY();
            
            moveCircle.setByX(dx);
            moveCircle.setByY(dy);
            
            circleMovements.add(moveCircle);
        }
        
        // Mostrar destello al impactar
        FadeTransition showFlash = new FadeTransition(Duration.millis(200), impactFlash);
        showFlash.setToValue(1.0);
        
        ScaleTransition expandFlash = new ScaleTransition(Duration.millis(500), impactFlash);
        expandFlash.setToX(2.0);
        expandFlash.setToY(2.0);
        
        FadeTransition fadeFlash = new FadeTransition(Duration.millis(300), impactFlash);
        fadeFlash.setFromValue(1.0);
        fadeFlash.setToValue(0.0);
        fadeFlash.setDelay(Duration.millis(200));
        
        // Sacudir el objetivo
        TranslateTransition shakeTarget = new TranslateTransition(Duration.millis(50), targetImage);
        shakeTarget.setFromX(0);
        shakeTarget.setByX(isPlayerTarget ? -8 : 8);
        shakeTarget.setCycleCount(8);
        shakeTarget.setAutoReverse(true);
        
        // Mostrar texto de daño
        FadeTransition showText = new FadeTransition(Duration.millis(300), textContainer);
        showText.setToValue(1.0);
        
        TranslateTransition floatText = new TranslateTransition(Duration.millis(1500), textContainer);
        floatText.setByY(-30);
        
        // Fade out del texto
        FadeTransition fadeText = new FadeTransition(Duration.millis(500), textContainer);
        fadeText.setToValue(0.0);
        fadeText.setDelay(Duration.millis(1000));
        
        // Secuencia completa
        SequentialTransition sequence = new SequentialTransition(
            new ParallelTransition(circleMovements.toArray(new Animation[0])),
            new ParallelTransition(
                showFlash,
                expandFlash,
                fadeFlash,
                shakeTarget,
                showText,
                new Timeline(new KeyFrame(Duration.ZERO, e -> targetImage.setEffect(trueHit)))
            ),
            floatText,
            fadeText
        );
        
        sequence.setOnFinished(e -> {
            // Limpiar
            rootPane.getChildren().removeAll(textContainer, impactFlash);
            for (Circle circle : damageCircles) {
                rootPane.getChildren().remove(circle);
            }
            
            // Eliminar efecto del objetivo gradualmente
            FadeTransition fadeEffect = new FadeTransition(Duration.millis(500), targetImage);
            fadeEffect.setOnFinished(event -> targetImage.setEffect(null));
            fadeEffect.play();
        });
        
        sequence.play();
    }
    
    /**
     * Anima un efecto de escudo de daño
     */
    public void animateDamageShield(PersonajeModel character, double reductionFactor, boolean isPlayerCharacter) {
        ImageView characterImage = getCharacterImage(isPlayerCharacter);
        if (characterImage == null) return;
        
        // Coordenadas del personaje
        double centerX = characterImage.getLayoutX() + characterImage.getFitWidth() / 2;
        double centerY = characterImage.getLayoutY() + characterImage.getFitHeight() / 2;
        
        // Crear efecto de escudo
        StackPane shieldEffect = new StackPane();
        
        // Escudo exterior
        Circle outerShield = new Circle(80);
        outerShield.setFill(Color.TRANSPARENT);
        outerShield.setStroke(Color.rgb(100, 200, 255, 0.7));
        outerShield.setStrokeWidth(3);
        
        // Escudo interior
        Circle innerShield = new Circle(70);
        innerShield.setFill(Color.TRANSPARENT);
        innerShield.setStroke(Color.rgb(150, 220, 255, 0.5));
        innerShield.setStrokeWidth(2);
        
        // Efecto de brillo
        Bloom bloom = new Bloom(0.7);
        DropShadow glow = new DropShadow(15, Color.LIGHTBLUE);
        glow.setInput(bloom);
        
        outerShield.setEffect(glow);
        
        shieldEffect.getChildren().addAll(outerShield, innerShield);
        shieldEffect.setLayoutX(centerX);
        shieldEffect.setLayoutY(centerY);
        shieldEffect.setOpacity(0);
        
        rootPane.getChildren().add(shieldEffect);
        
        // Texto del porcentaje de reducción
        Text reductionText = new Text("ESCUDO: " + (int)(reductionFactor * 100) + "%");
        reductionText.setFont(Font.font("System", FontWeight.BOLD, 20));
        reductionText.setFill(Color.LIGHTBLUE);
        reductionText.setStroke(Color.BLACK);
        reductionText.setStrokeWidth(1.5);
        
        StackPane textContainer = new StackPane(reductionText);
        textContainer.setLayoutX(centerX - 60);
        textContainer.setLayoutY(centerY - 100);
        textContainer.setOpacity(0);
        
        rootPane.getChildren().add(textContainer);
        
        // Efectos hexagonales que orbitan
        List<Node> hexes = new ArrayList<>();
        int numHex = 6;
        for (int i = 0; i < numHex; i++) {
            double angle = 2 * Math.PI * i / numHex;
            
            // Crear un pequeño círculo como hexágono estilizado
            Circle hex = new Circle(8);
            hex.setFill(Color.TRANSPARENT);
            hex.setStroke(Color.LIGHTSKYBLUE);
            hex.setStrokeWidth(2);
            
            // Posicionar alrededor del escudo
            double hexX = centerX + Math.cos(angle) * 90;
            double hexY = centerY + Math.sin(angle) * 90;
            
            hex.setLayoutX(hexX);
            hex.setLayoutY(hexY);
            hex.setOpacity(0);
            
            rootPane.getChildren().add(hex);
            hexes.add(hex);
        }
        
        // Efecto para el personaje protegido
        ColorAdjust protected_effect = new ColorAdjust();
        protected_effect.setHue(0.2);  // Ligeramente azulado
        protected_effect.setSaturation(0.2);
        
        Glow protectedGlow = new Glow(0.4);
        protectedGlow.setInput(protected_effect);
        
        // Animaciones
        
        // Mostrar escudo
        FadeTransition showShield = new FadeTransition(Duration.millis(500), shieldEffect);
        showShield.setToValue(1.0);
        
        // Pulsar escudo
        ScaleTransition pulseShield = new ScaleTransition(Duration.millis(1500), outerShield);
        pulseShield.setFromX(0.9);
        pulseShield.setFromY(0.9);
        pulseShield.setToX(1.1);
        pulseShield.setToY(1.1);
        pulseShield.setCycleCount(2);
        pulseShield.setAutoReverse(true);
        
        // Rotar escudo interior en sentido contrario
        Timeline rotateInner = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(innerShield.rotateProperty(), 0)),
            new KeyFrame(Duration.millis(3000), new KeyValue(innerShield.rotateProperty(), 360))
        );
        
        // Mostrar hexágonos
        List<FadeTransition> hexFades = new ArrayList<>();
        for (Node hex : hexes) {
            FadeTransition fadeHex = new FadeTransition(Duration.millis(300), hex);
            fadeHex.setToValue(0.7);
            hexFades.add(fadeHex);
        }
        
        // Orbitar los hexágonos
        List<PathTransition> hexOrbits = new ArrayList<>();
        for (int i = 0; i < hexes.size(); i++) {
            double startAngle = 2 * Math.PI * i / numHex;
            Path orbitPath = new Path();
            
            // Crear un círculo para la órbita
            orbitPath.getElements().add(new MoveTo(
                centerX + Math.cos(startAngle) * 90, 
                centerY + Math.sin(startAngle) * 90));
            
            for (int a = 1; a <= 360; a += 10) {
                double angle = Math.toRadians(a) + startAngle;
                orbitPath.getElements().add(new LineTo(
                    centerX + Math.cos(angle) * 90,
                    centerY + Math.sin(angle) * 90
                ));
            }
            
            PathTransition orbit = new PathTransition(Duration.millis(3000), orbitPath, hexes.get(i));
            orbit.setCycleCount(1);
            hexOrbits.add(orbit);
        }
        
        // Mostrar texto
        FadeTransition showText = new FadeTransition(Duration.millis(400), textContainer);
        showText.setToValue(1.0);
        
        TranslateTransition floatText = new TranslateTransition(Duration.millis(1500), textContainer);
        floatText.setByY(-30);
        
        // Desvanecimiento final
        FadeTransition fadeShield = new FadeTransition(Duration.millis(500), shieldEffect);
        fadeShield.setToValue(0.0);
        
        FadeTransition fadeText = new FadeTransition(Duration.millis(500), textContainer);
        fadeText.setToValue(0.0);
        
        List<FadeTransition> hexFadeOuts = new ArrayList<>();
        for (Node hex : hexes) {
            FadeTransition fadeHex = new FadeTransition(Duration.millis(300), hex);
            fadeHex.setToValue(0.0);
            hexFadeOuts.add(fadeHex);
        }
        
        // Secuencia completa
        SequentialTransition sequence = new SequentialTransition(
            new ParallelTransition(showShield, new ParallelTransition(hexFades.toArray(new Animation[0]))),
            new ParallelTransition(pulseShield, rotateInner, new ParallelTransition(hexOrbits.toArray(new Animation[0])), 
                showText, new Timeline(new KeyFrame(Duration.ZERO, e -> characterImage.setEffect(protectedGlow)))),
            floatText,
            new ParallelTransition(fadeShield, fadeText, new ParallelTransition(hexFadeOuts.toArray(new Animation[0])))
        );
        
        sequence.setOnFinished(e -> {
            // Limpiar
            rootPane.getChildren().removeAll(shieldEffect, textContainer);
            for (Node hex : hexes) {
                rootPane.getChildren().remove(hex);
            }
            
            // Eliminar efecto de protección gradualmente
            FadeTransition fadeEffect = new FadeTransition(Duration.millis(500), characterImage);
            fadeEffect.setOnFinished(event -> characterImage.setEffect(null));
            fadeEffect.play();
        });
        
        sequence.play();
    }
    
    /**
     * Obtiene la imagen del personaje según el bando
     */
    private ImageView getCharacterImage(boolean isPlayerCharacter) {
        return (ImageView) rootPane.lookup(isPlayerCharacter ? "#imgPersonajeJugador" : "#imgPersonajeIA");
    }
    
    /**
     * Crea una línea de energía animada
     */
    private Line createEnergyLine(double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(2 + random.nextDouble() * 3);
        
        // Efecto de brillo
        line.setEffect(new Glow(0.8));
        
        // Animar parpadeo
        Timeline blink = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(line.opacityProperty(), 0.3)),
            new KeyFrame(Duration.millis(150), new KeyValue(line.opacityProperty(), 1.0)),
            new KeyFrame(Duration.millis(300), new KeyValue(line.opacityProperty(), 0.3))
        );
        blink.setCycleCount(Animation.INDEFINITE);
        blink.play();
        
        return line;
    }
}