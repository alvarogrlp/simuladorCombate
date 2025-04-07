package es.alvarogrlp.marvelsimu.backend.config;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ThemeManager {
    private static final String propiedadTema = "theme";
    private static final String darkMode = "dark";
    private static final String lightMode = "light";

    /**
     * * Aplica el tema actual a la escena y al icono de modo.
     * @param scene 
     * @param iconoModo 
     */
    public static void applyTheme(Scene scene, ImageView iconoModo) {
        String currentTheme = ConfigManager.ConfigProperties.getProperty(propiedadTema, darkMode);
        setTheme(scene, iconoModo, currentTheme);
    }

    /**
     * * Cambia el tema de la escena y actualiza el icono de modo.
     * @param scene
     * @param iconoModo
     */
    public static void toggleTheme(Scene scene, ImageView iconoModo) {
        String currentTheme = ConfigManager.ConfigProperties.getProperty(propiedadTema, darkMode);
        String newTheme = darkMode.equals(currentTheme) ? lightMode : darkMode;
        
        setTheme(scene, iconoModo, newTheme);
        ConfigManager.ConfigProperties.setProperty(propiedadTema, newTheme);
    }

    /**
     * * Establece el tema de la escena y actualiza el icono de modo.
     * @param scene
     * @param iconoModo
     * @param theme
     */
    private static void setTheme(Scene scene, ImageView iconoModo, String theme) {
        var stylesheets = scene.getStylesheets();
        stylesheets.clear();
        
        if (lightMode.equals(theme)) {
            stylesheets.add(ThemeManager.class.getResource("/es/alvarogrlp/marvelsimu/light-mode.css").toExternalForm());
            if (iconoModo != null) {
                iconoModo.setImage(new Image(ThemeManager.class.getResource("/images/oscuro.png").toExternalForm()));
            }
        } else {
            stylesheets.add(ThemeManager.class.getResource("/es/alvarogrlp/marvelsimu/dark-mode.css").toExternalForm());
            if (iconoModo != null) {
                iconoModo.setImage(new Image(ThemeManager.class.getResource("/images/luz.png").toExternalForm()));
            }
        }
    }
}