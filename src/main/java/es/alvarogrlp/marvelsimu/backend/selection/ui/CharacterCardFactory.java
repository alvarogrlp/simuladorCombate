package es.alvarogrlp.marvelsimu.backend.selection.ui;

import java.io.InputStream;

import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import es.alvarogrlp.marvelsimu.backend.selection.logic.SelectionManager;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
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
     * Crea una tarjeta para un personaje
     * @param character Personaje para la tarjeta
     * @param sourceButton Botón origen (puede ser null)
     * @param isPlayerTeam Si es para el equipo del jugador
     * @return VBox con la tarjeta del personaje
     */
    public VBox createCharacterCard(PersonajeModel character, Button sourceButton, boolean isPlayerTeam) {
        VBox card = new VBox(8);
        card.setPrefSize(160, 160);
        card.setMaxSize(160, 160);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(8));
        card.getStyleClass().add("character-card");
        
        // Crear contenedor para la miniatura con efecto de borde
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(120, 120);
        imageContainer.setMaxSize(120, 120);
        imageContainer.getStyleClass().add("image-container");
        
        // Crear miniatura
        ImageView thumbnail = new ImageView();
        thumbnail.setFitWidth(120);
        thumbnail.setFitHeight(120);
        thumbnail.setPreserveRatio(true);
        
        // Cargar imagen
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(character.getImagenMiniatura());
            if (is != null) {
                Image image = new Image(is);
                thumbnail.setImage(image);
                
                // Aplicar efecto de recorte circular si se desea
                // Circle clip = new Circle(60, 60, 60);
                // thumbnail.setClip(clip);
            } else {
                System.err.println("No se pudo cargar la imagen: " + character.getImagenMiniatura());
                // Cargar imagen por defecto
                is = getClass().getClassLoader().getResourceAsStream("images/Personajes/random.png");
                if (is != null) {
                    thumbnail.setImage(new Image(is));
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + e.getMessage());
        }
        
        // Añadir miniatura al contenedor
        imageContainer.getChildren().add(thumbnail);
        
        // Configurar nombre del personaje
        Label nameLabel = new Label(character.getNombre());
        nameLabel.getStyleClass().add("character-name-label");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(150);
        
        // Añadir elementos a la tarjeta
        card.getChildren().addAll(imageContainer, nameLabel);
        
        // Permitir eliminar con clic para AMBOS equipos
        card.setOnMouseClicked(e -> {
            // Eliminar directamente sin confirmación
            selectionManager.removeCharacterFromTeam(character, isPlayerTeam);
            
            // Reactivar el botón asociado
            Button button = selectionManager.findCharacterButton(character);
            if (button != null) {
                button.setDisable(false);
            }
        });
        
        // Añadir efecto de hover para ambos equipos
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-cursor: hand;");
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle("");
        });
        
        // Guardar el personaje como datos del componente para acceso rápido
        card.setUserData(character);
        
        return card;
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
    
    /**
     * Carga una imagen directamente desde la ruta
     */
    private Image cargarImagen(String rutaImagen) {
        if (rutaImagen == null || rutaImagen.isEmpty()) {
            System.err.println("Ruta de imagen vacía");
            return cargarImagenPorDefecto();
        }
        
        try {
            // Intentar cargar la imagen directamente
            InputStream is = getClass().getClassLoader().getResourceAsStream(rutaImagen);
            if (is != null) {
                Image imagen = new Image(is);
                if (!imagen.isError()) {
                    return imagen;
                }
            }
            
            // Si no se puede cargar, usar imagen por defecto
            System.err.println("No se pudo cargar la imagen: " + rutaImagen);
            return cargarImagenPorDefecto();
        } catch (Exception e) {
            System.err.println("Error cargando imagen '" + rutaImagen + "': " + e.getMessage());
            return cargarImagenPorDefecto();
        }
    }
    
    /**
     * Carga la imagen por defecto
     */
    private Image cargarImagenPorDefecto() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("images/Personajes/random.png");
            if (is != null) {
                return new Image(is);
            }
            return new WritableImage(100, 100);
        } catch (Exception e) {
            return new WritableImage(100, 100);
        }
    }
}