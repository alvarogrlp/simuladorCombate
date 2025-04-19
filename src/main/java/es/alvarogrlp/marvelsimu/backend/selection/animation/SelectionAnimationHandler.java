package es.alvarogrlp.marvelsimu.backend.selection.animation;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import animatefx.animation.SlideInUp;
import animatefx.animation.SlideOutDown;
import eu.iamgio.animated.transition.AnimationPair;
import javafx.scene.control.Button;

/**
 * Clase encargada de gestionar las animaciones en la pantalla de selección
 */
public class SelectionAnimationHandler {

    /**
     * Crea una animación de desvanecimiento
     * @return Par de animaciones entrada/salida
     */
    public AnimationPair createFadeAnimation() {
        return new AnimationPair(new FadeIn(), new FadeOut())
            .setSpeed(1.5, 1.5);
    }
    
    /**
     * Crea una animación de deslizamiento
     * @return Par de animaciones entrada/salida
     */
    public AnimationPair createSlideAnimation() {
        return new AnimationPair(new SlideInUp(), new SlideOutDown())
            .setSpeed(1.5, 1.5);
    }
    
    /**
     * Aplica una animación de hover a un botón
     * @param button Botón a animar
     */
    public void applyHoverAnimation(Button button) {
        button.setOnMouseEntered(e -> {
            button.setScaleX(1.1);
            button.setScaleY(1.1);
        });
        
        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
        
        button.setStyle("-fx-transition: all 200ms ease-out;");
    }
}