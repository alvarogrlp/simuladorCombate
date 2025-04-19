package es.alvarogrlp.marvelsimu.backend.combat.animation;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Maneja las animaciones específicas del jugador durante el combate
 */
public class PlayerAnimationHandler {
    
    private AnchorPane rootPane;
    private boolean animationInProgress = false;
    
    public PlayerAnimationHandler(AnchorPane rootPane) {
        this.rootPane = rootPane;
    }
    
    /**
     * Anima un ataque del jugador
     * 
     * @param playerImage Imagen del personaje del jugador
     * @param enemyImage Imagen del personaje enemigo
     * @param attackType Tipo de ataque (melee, lejano, habilidad1, habilidad2)
     * @param onComplete Acción a ejecutar al terminar la animación
     */
    public void animateAttack(ImageView playerImage, ImageView enemyImage, String attackType, Runnable onComplete) {
        // Evitar comenzar una nueva animación si hay una en progreso
        if (animationInProgress) return;
        
        animationInProgress = true;
        
        switch (attackType) {
            case "melee":
                animateMeleeAttack(playerImage, enemyImage, () -> {
                    animationInProgress = false;
                    if (onComplete != null) onComplete.run();
                });
                break;
            case "lejano":
                animateRangedAttack(playerImage, enemyImage, () -> {
                    animationInProgress = false;
                    if (onComplete != null) onComplete.run();
                });
                break;
            case "habilidad1":
                animateAbility(playerImage, enemyImage, true, () -> {
                    animationInProgress = false;
                    if (onComplete != null) onComplete.run();
                });
                break;
            case "habilidad2":
                animateAbility(playerImage, enemyImage, false, () -> {
                    animationInProgress = false;
                    if (onComplete != null) onComplete.run();
                });
                break;
            default:
                animationInProgress = false;
                if (onComplete != null) onComplete.run();
                break;
        }
    }
    
    /**
     * Anima un ataque cuerpo a cuerpo
     */
    private void animateMeleeAttack(ImageView playerImage, ImageView enemyImage, Runnable onComplete) {
        // Avanzar rápidamente hacia el enemigo
        TranslateTransition avanzarRapido = new TranslateTransition(Duration.millis(180), playerImage);
        avanzarRapido.setByX(120);
        
        // Retroceder a posición original
        TranslateTransition retrocederRapido = new TranslateTransition(Duration.millis(250), playerImage);
        retrocederRapido.setToX(0);
        
        // Hacer que el enemigo se sacuda
        TranslateTransition sacudirEnemigo = new TranslateTransition(Duration.millis(40), enemyImage);
        sacudirEnemigo.setByX(-20);
        sacudirEnemigo.setCycleCount(4);
        sacudirEnemigo.setAutoReverse(true);
        
        // Secuencia de animación completa
        SequentialTransition secuencia = new SequentialTransition(
            avanzarRapido,
            sacudirEnemigo,
            retrocederRapido
        );
        
        // Ejecutar acción al finalizar
        if (onComplete != null) {
            secuencia.setOnFinished(e -> onComplete.run());
        }
        
        secuencia.play();
    }
    
    /**
     * Anima un ataque a distancia
     */
    private void animateRangedAttack(ImageView playerImage, ImageView enemyImage, Runnable onComplete) {
        // Crear proyectil
        Circle proyectil = new Circle(10);
        proyectil.setFill(Color.rgb(50, 150, 255));
        
        // Obtener posiciones inicial y final basadas en las imágenes reales
        double startX = playerImage.localToScene(playerImage.getBoundsInLocal()).getMaxX() - 30;
        double startY = playerImage.localToScene(playerImage.getBoundsInLocal()).getMinY() + 
                        playerImage.getFitHeight() / 2;
        
        // Posicionar proyectil en el pane
        proyectil.setCenterX(startX);
        proyectil.setCenterY(startY);
        
        // Añadir el proyectil a la escena
        rootPane.getChildren().add(proyectil);
        
        // Calcular punto de impacto
        double endX = enemyImage.localToScene(enemyImage.getBoundsInLocal()).getMinX() + 20;
        double endY = enemyImage.localToScene(enemyImage.getBoundsInLocal()).getMinY() + 
                      enemyImage.getFitHeight() / 2;
        
        // Animar movimiento del proyectil
        TranslateTransition movimientoProyectil = new TranslateTransition(Duration.millis(300), proyectil);
        movimientoProyectil.setToX(endX - startX);
        movimientoProyectil.setToY(endY - startY);
        
        // Sacudir al enemigo al impactar
        TranslateTransition sacudirEnemigo = new TranslateTransition(Duration.millis(40), enemyImage);
        sacudirEnemigo.setByX(-15);
        sacudirEnemigo.setCycleCount(3);
        sacudirEnemigo.setAutoReverse(true);
        
        // Eliminar proyectil tras el impacto
        movimientoProyectil.setOnFinished(e -> {
            sacudirEnemigo.play();
            FadeTransition fadeOut = new FadeTransition(Duration.millis(100), proyectil);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                rootPane.getChildren().remove(proyectil);
                if (onComplete != null) onComplete.run();
            });
            fadeOut.play();
        });
        
        movimientoProyectil.play();
    }
    
    /**
     * Anima una habilidad especial
     * 
     * @param isFirstAbility true para habilidad1, false para habilidad2
     */
    private void animateAbility(ImageView playerImage, ImageView enemyImage, boolean isFirstAbility, Runnable onComplete) {
        // Pequeño avance
        TranslateTransition avanzarMedio = new TranslateTransition(Duration.millis(200), playerImage);
        avanzarMedio.setByX(50);
        
        // Retroceder a posición original
        TranslateTransition retrocederMedio = new TranslateTransition(Duration.millis(300), playerImage);
        retrocederMedio.setToX(0);
        
        // Efecto especial alrededor del personaje - usar coordenadas absolutas
        double playerCenterX = playerImage.localToScene(playerImage.getBoundsInLocal()).getMinX() + 
                             playerImage.getFitWidth() / 2;
        double playerCenterY = playerImage.localToScene(playerImage.getBoundsInLocal()).getMinY() + 
                             playerImage.getFitHeight() / 2;
        
        // Crear contenedor para el efecto - ajustado correctamente
        StackPane efectoEspecial = new StackPane();
        efectoEspecial.setLayoutX(playerCenterX - 60);
        efectoEspecial.setLayoutY(playerCenterY - 60);
        
        // Círculo de energía
        Circle aura = new Circle(60);
        aura.setFill(Color.TRANSPARENT);
        aura.setStroke(isFirstAbility ? Color.PURPLE : Color.RED);
        aura.setStrokeWidth(3);
        
        efectoEspecial.getChildren().add(aura);
        rootPane.getChildren().add(efectoEspecial);
        
        // Animar aura
        ScaleTransition expandirAura = new ScaleTransition(Duration.millis(300), aura);
        expandirAura.setFromX(0.2);
        expandirAura.setFromY(0.2);
        expandirAura.setToX(1.5);
        expandirAura.setToY(1.5);
        
        FadeTransition desvanecerAura = new FadeTransition(Duration.millis(300), aura);
        desvanecerAura.setFromValue(1.0);
        desvanecerAura.setToValue(0.0);
        
        ParallelTransition animacionAura = new ParallelTransition(expandirAura, desvanecerAura);
        animacionAura.setOnFinished(e -> rootPane.getChildren().remove(efectoEspecial));
        
        // Efecto en el enemigo
        TranslateTransition sacudirFuerte = new TranslateTransition(Duration.millis(30), enemyImage);
        sacudirFuerte.setByX(-25);
        sacudirFuerte.setCycleCount(6);
        sacudirFuerte.setAutoReverse(true);
        
        // Secuencia completa
        SequentialTransition secuenciaHabilidad = new SequentialTransition(
            avanzarMedio,
            animacionAura,
            sacudirFuerte,
            retrocederMedio
        );
        
        // Ejecutar acción al finalizar
        if (onComplete != null) {
            secuenciaHabilidad.setOnFinished(e -> onComplete.run());
        }
        
        secuenciaHabilidad.play();
    }
}