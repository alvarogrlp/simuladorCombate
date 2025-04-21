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
     * @param character Personaje a mostrar
     * @param sourceButton Botón fuente (puede ser null)
     * @param isPlayerTeam Si es para el equipo del jugador
     * @return VBox con la tarjeta
     */
    public VBox createCharacterCard(PersonajeModel character, Button sourceButton, boolean isPlayerTeam) {
        VBox cardBox = new VBox(8);
        cardBox.setAlignment(Pos.CENTER);
        cardBox.setPadding(new Insets(5));
        cardBox.getStyleClass().add("character-card");
        
        // Crear contenedor para la imagen
        StackPane imagePane = new StackPane();
        imagePane.setMaxSize(120, 120);
        imagePane.setPrefSize(120, 120);
        imagePane.setMinSize(120, 120);
        imagePane.getStyleClass().add("character-image-container");
        
        // Crear imagen del personaje con tamaño consistente
        ImageView characterImageView = createCharacterImageView(character);
        characterImageView.setFitWidth(110);
        characterImageView.setFitHeight(110);
        characterImageView.setPreserveRatio(true);
        
        // Añadir al contenedor de imagen
        imagePane.getChildren().add(characterImageView);
        
        // Crear etiqueta para el nombre
        Label nameLabel = new Label(character.getNombre());
        nameLabel.getStyleClass().add("character-name-label");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(150);
        nameLabel.setAlignment(Pos.CENTER);
        
        // Añadir componentes a la tarjeta
        cardBox.getChildren().addAll(imagePane, nameLabel);
        
        // Configurar tamaño de la tarjeta
        cardBox.setPrefSize(160, 180);
        cardBox.setMaxSize(160, 180);
        cardBox.setMinSize(160, 180);
        
        return cardBox;
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
    
    /**
     * Crea la vista de imagen para un personaje
     */
    private ImageView createCharacterImageView(PersonajeModel character) {
        ImageView imageView = new ImageView();
        
        try {
            // Intentar primero con la ruta del modelo
            String imagePath = character.getImagenMiniatura();
            InputStream is = getClass().getClassLoader().getResourceAsStream(imagePath);
            
            // Si no funciona, intentar con la ruta construida
            if (is == null) {
                imagePath = "images/Personajes/" + character.getNombreCodigo() + ".png";
                is = getClass().getClassLoader().getResourceAsStream(imagePath);
            }
            
            // Si todavía es nulo, intentar con otra variación de la ruta
            if (is == null) {
                imagePath = "/images/Personajes/" + character.getNombreCodigo() + ".png";
                is = getClass().getResourceAsStream(imagePath);
            }
            
            if (is != null) {
                Image image = new Image(is);
                imageView.setImage(image);
                imageView.setFitWidth(110);
                imageView.setFitHeight(110);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.getStyleClass().add("character-image");
                imageView.setCache(true);
            } else {
                System.err.println("No se pudo cargar la imagen para: " + character.getNombreCodigo());
                // Cargar imagen placeholder
                loadPlaceholderImage(imageView);
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen para " + character.getNombreCodigo() + ": " + e.getMessage());
            // Cargar imagen placeholder
            loadPlaceholderImage(imageView);
        }
        
        return imageView;
    }
    
    /**
     * Carga una imagen placeholder cuando no se puede cargar la original
     */
    private void loadPlaceholderImage(ImageView imageView) {
        try {
            // Intentar múltiples rutas para la imagen placeholder
            InputStream is = getClass().getResourceAsStream("/images/Personajes/random.png");
            if (is == null) {
                is = getClass().getClassLoader().getResourceAsStream("images/Personajes/random.png");
            }
            
            if (is != null) {
                Image placeholderImage = new Image(is);
                imageView.setImage(placeholderImage);
            } else {
                System.err.println("No se pudo cargar ni siquiera la imagen placeholder");
            }
        } catch (Exception ex) {
            System.err.println("No se pudo cargar la imagen placeholder: " + ex.getMessage());
        }
    }
    
    /**
     * Método de diagnóstico para verificar si un recurso existe
     */
    private boolean resourceExists(String path) {
        boolean exists = false;
        try {
            // Intentar varias formas de acceder al recurso
            exists = getClass().getResource(path) != null;
            if (!exists) {
                exists = getClass().getClassLoader().getResource(path) != null;
            }
            if (!exists) {
                exists = getClass().getResourceAsStream(path) != null;
            }
        } catch (Exception e) {
            // Ignorar errores
        }
        return exists;
    }
}