package es.alvarogrlp.marvelsimu.backend.selection.ui;

import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import es.alvarogrlp.marvelsimu.backend.selection.logic.SelectionManager;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Clase encargada de crear tarjetas visuales para personajes
 */
public class CharacterCardFactory {
    
    private SelectionManager selectionManager;
    
    public CharacterCardFactory(SelectionManager selectionManager) {
        this.selectionManager = selectionManager;
    }
    
    /**
     * Crea una tarjeta para un personaje seleccionado para mostrar en el equipo
     * @param character Personaje
     * @param sourceButton Botón original del personaje
     * @param isPlayerTeam Si es para el equipo del jugador
     * @return VBox con la vista del personaje
     */
    public VBox createCharacterCard(PersonajeModel character, Button sourceButton, boolean isPlayerTeam) {
        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER);
        container.getStyleClass().add("selected-character-container");
        container.setMaxWidth(180);
        
        // Guardar referencia al personaje para uso futuro
        container.setUserData(character);
        
        // Nombre del personaje
        Text nombre = new Text(character.getNombre());
        nombre.getStyleClass().add("selected-character-name");
        container.getChildren().add(nombre);
        
        // Imagen del personaje
        try {
            Image imagen = new Image(getClass().getClassLoader().getResourceAsStream(character.getImagenMiniatura()));
            if (!imagen.isError()) {
                ImageView imageView = new ImageView(imagen);
                imageView.setFitHeight(120);
                imageView.setPreserveRatio(true);
                container.getChildren().add(0, imageView);
            } else {
                System.err.println("Error al cargar la imagen para el equipo: " + character.getImagenMiniatura());
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen para el equipo: " + e.getMessage());
        }
        
        // Configurar el manejo de clics para ambos equipos: jugador y IA
        container.setOnMouseClicked(e -> {
            // Eliminar directamente de la UI primero
            VBox parent = (VBox) container.getParent();
            if (parent != null) {
                // Eliminar con animación
                removeWithAnimation(container, parent);
                
                // Habilitar el botón original
                if (sourceButton != null) {
                    sourceButton.setDisable(false);
                }
                
                // Actualizar el modelo de datos después
                selectionManager.handleCharacterRemoval(character, isPlayerTeam);
                
                // Mostrar mensaje específico según el equipo
                String mensaje = character.getNombre() + " eliminado del " +
                                (isPlayerTeam ? "equipo del jugador" : "equipo de la IA");
                selectionManager.getUIManager().showInfoMessage(mensaje);
                
                e.consume();
            }
        });
        
        // Efectos visuales para indicar que es clickeable
        container.setOnMouseEntered(e -> {
            container.setCursor(Cursor.HAND);
            // Efecto de escala
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), container);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });
        
        container.setOnMouseExited(e -> {
            // Restaurar escala
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), container);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
        
        return container;
    }
    
    /**
     * Elimina una tarjeta con animación
     */
    private void removeWithAnimation(VBox card, VBox parent) {
        // Crear animación de desvanecimiento
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), card);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        // Crear animación de escala
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(300), card);
        scaleOut.setFromX(1.0);
        scaleOut.setFromY(1.0);
        scaleOut.setToX(0.7);
        scaleOut.setToY(0.7);
        
        // Combinar animaciones
        ParallelTransition exitAnimation = new ParallelTransition(fadeOut, scaleOut);
        
        // Eliminar inmediatamente de la vista al finalizar la animación
        exitAnimation.setOnFinished(e -> parent.getChildren().remove(card));
        
        // Iniciar animación
        exitAnimation.play();
    }
}