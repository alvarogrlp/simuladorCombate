package es.alvarogrlp.marvelsimu.backend.config;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ThemeManager {
    private static final String THEME_PROPERTY = "theme";
    private static final String DARK_MODE = "dark";
    private static final String LIGHT_MODE = "light";

    public static void applyTheme(Scene scene, ImageView iconoModo) {
        String currentTheme = ConfigManager.ConfigProperties.getProperty(THEME_PROPERTY, DARK_MODE);
        setTheme(scene, iconoModo, currentTheme);
    }

    public static void toggleTheme(Scene scene, ImageView iconoModo) {
        String currentTheme = ConfigManager.ConfigProperties.getProperty(THEME_PROPERTY, DARK_MODE);
        String newTheme = DARK_MODE.equals(currentTheme) ? LIGHT_MODE : DARK_MODE;
        
        setTheme(scene, iconoModo, newTheme);
        ConfigManager.ConfigProperties.setProperty(THEME_PROPERTY, newTheme);
    }

    private static void setTheme(Scene scene, ImageView iconoModo, String theme) {
        var stylesheets = scene.getStylesheets();
        stylesheets.clear();
        
        if (LIGHT_MODE.equals(theme)) {
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