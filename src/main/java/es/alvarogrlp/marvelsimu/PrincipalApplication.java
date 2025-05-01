package es.alvarogrlp.marvelsimu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import es.alvarogrlp.marvelsimu.backend.config.ConfigManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PrincipalApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Inicializar las propiedades si no existen
        if (ConfigManager.ConfigProperties.getProperty("theme") == null) {
            ConfigManager.ConfigProperties.setProperty("theme", "dark");
        }

        // Cargar y establecer el icono personalizado
        try {
            // Intentar cargar primero como recurso
            InputStream iconStream = getClass().getClassLoader().getResourceAsStream("images/icon-logo.png");
            
            // Si no está en los recursos, cargar desde la ruta directa
            if (iconStream == null) {
                iconStream = new FileInputStream("docs/images/icon-logo.png");
            }
            
            stage.getIcons().add(new Image(iconStream));
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono: " + e.getMessage());
        }

        FXMLLoader fxmlLoader = new FXMLLoader(PrincipalApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 410, 810);
        stage.setTitle("Marvel Combat Simulator");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}