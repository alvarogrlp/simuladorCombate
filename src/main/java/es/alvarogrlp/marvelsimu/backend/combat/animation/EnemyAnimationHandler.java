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
 * Maneja las animaciones específicas del enemigo (IA) durante el combate
 */
public class EnemyAnimationHandler {
    
    private AnchorPane rootPane;
    
    public EnemyAnimationHandler(AnchorPane rootPane) {
        this.rootPane = rootPane;
    }
    
    /**
     * Anima un ataque del enemigo
     * 
     * @param enemyImage Imagen del personaje enemigo
     * @param playerImage Imagen del personaje del jugador
     * @param attackType Tipo de ataque (melee, lejano, habilidad1, habilidad2)
     * @param onComplete Acción a ejecutar al terminar la animación
     */
    public void animateAttack(ImageView enemyImage, ImageView playerImage, String attackType, Runnable onComplete) {
        switch (attackType) {
            case "melee":
                animateMeleeAttack(enemyImage, playerImage, onComplete);
                break;
            case "lejano":
                animateRangedAttack(enemyImage, playerImage, onComplete);
                break;
            case "habilidad1":
                animateAbility(enemyImage, playerImage, true, onComplete);
                break;
            case "habilidad2":
                animateAbility(enemyImage, playerImage, false, onComplete);
                break;
            default:
                if (onComplete != null) onComplete.run();
                break;
        }
    }
    
    /**
     * Anima un ataque cuerpo a cuerpo
     */
    private void animateMeleeAttack(ImageView enemyImage, ImageView playerImage, Runnable onComplete) {
        // Avanzar rápidamente hacia el jugador
        TranslateTransition avanzarRapido = new TranslateTransition(Duration.millis(180), enemyImage);
        avanzarRapido.setByX(-120);
        
        // Retroceder a posición original
        TranslateTransition retrocederRapido = new TranslateTransition(Duration.millis(250), enemyImage);
        retrocederRapido.setToX(0);
        
        // Hacer que el jugador se sacuda
        TranslateTransition sacudirJugador = new TranslateTransition(Duration.millis(40), playerImage);
        sacudirJugador.setByX(20);
        sacudirJugador.setCycleCount(4);
        sacudirJugador.setAutoReverse(true);
        
        // Secuencia de animación completa
        SequentialTransition secuencia = new SequentialTransition(
            avanzarRapido,
            sacudirJugador,
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
    private void animateRangedAttack(ImageView enemyImage, ImageView playerImage, Runnable onComplete) {
        // Crear proyectil
        Circle proyectil = new Circle(10);
        proyectil.setFill(Color.rgb(255, 100, 50));
        
        // Obtener posiciones inicial y final basadas en coordenadas absolutas
        double startX = enemyImage.localToScene(enemyImage.getBoundsInLocal()).getMinX() + 30;
        double startY = enemyImage.localToScene(enemyImage.getBoundsInLocal()).getMinY() + 
                        enemyImage.getFitHeight() / 2;
        
        // Posicionar proyectil en el pane
        proyectil.setCenterX(startX);
        proyectil.setCenterY(startY);
        
        // Añadir el proyectil a la escena
        rootPane.getChildren().add(proyectil);
        
        // Calcular punto de impacto
        double endX = playerImage.localToScene(playerImage.getBoundsInLocal()).getMaxX() - 20;
        double endY = playerImage.localToScene(playerImage.getBoundsInLocal()).getMinY() + 
                      playerImage.getFitHeight() / 2;
        
        // Animar movimiento del proyectil
        TranslateTransition movimientoProyectil = new TranslateTransition(Duration.millis(300), proyectil);
        movimientoProyectil.setToX(endX - startX);
        movimientoProyectil.setToY(endY - startY);
        
        // Sacudir al jugador al impactar
        TranslateTransition sacudirJugador = new TranslateTransition(Duration.millis(40), playerImage);
        sacudirJugador.setByX(15);
        sacudirJugador.setCycleCount(3);
        sacudirJugador.setAutoReverse(true);
        
        // Eliminar proyectil tras el impacto
        movimientoProyectil.setOnFinished(e -> {
            sacudirJugador.play();
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
    private void animateAbility(ImageView enemyImage, ImageView playerImage, boolean isFirstAbility, Runnable onComplete) {
        // Pequeño avance
        TranslateTransition avanzarMedio = new TranslateTransition(Duration.millis(200), enemyImage);
        avanzarMedio.setByX(-50);
        
        // Retroceder a posición original
        TranslateTransition retrocederMedio = new TranslateTransition(Duration.millis(300), enemyImage);
        retrocederMedio.setToX(0);
        
        // Efecto especial alrededor del personaje - usar coordenadas absolutas
        double enemyCenterX = enemyImage.localToScene(enemyImage.getBoundsInLocal()).getMinX() + 
                            enemyImage.getFitWidth() / 2;
        double enemyCenterY = enemyImage.localToScene(enemyImage.getBoundsInLocal()).getMinY() + 
                            enemyImage.getFitHeight() / 2;
        
        // Crear contenedor para el efecto
        StackPane efectoEspecial = new StackPane();
        efectoEspecial.setLayoutX(enemyCenterX - 60);
        efectoEspecial.setLayoutY(enemyCenterY - 60);
        
        // Círculo de energía
        Circle aura = new Circle(60);
        aura.setFill(Color.TRANSPARENT);
        aura.setStroke(isFirstAbility ? Color.DARKBLUE : Color.DARKRED);
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
        
        // Efecto en el jugador
        TranslateTransition sacudirFuerte = new TranslateTransition(Duration.millis(30), playerImage);
        sacudirFuerte.setByX(25);
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