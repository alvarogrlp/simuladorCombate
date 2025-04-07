package es.alvarogrlp.marvelsimu.backend.controller.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import es.alvarogrlp.marvelsimu.PrincipalApplication;
import es.alvarogrlp.marvelsimu.backend.model.UsuarioModel;
import es.alvarogrlp.marvelsimu.backend.model.UsuarioServiceModel;
import es.alvarogrlp.marvelsimu.backend.config.ThemeManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public abstract class AbstractController {

    static final String PATH_DB = "src/main/resources/usuarios.db";

    private UsuarioServiceModel usuarioServiceModel;

    private Properties propertiesIdioma;

    /**
     * Constructor de la clase AbstractController.
     * Inicializa el servicio de usuario con la base de datos especificada.
     */
    public AbstractController() {
        try {
            usuarioServiceModel = new UsuarioServiceModel(PATH_DB);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Establece las propiedades del idioma.
     * 
     * @param properties Objeto Properties con las configuraciones del idioma.
     */
    public void setpropertiesIdioma(Properties properties) {
        propertiesIdioma = properties;
    }

    /**
     * Obtiene las propiedades del idioma configuradas.
     * 
     * @return Objeto Properties con las configuraciones del idioma.
     */
    public Properties getPropertiesIdioma() {
        return propertiesIdioma;
    }

    /**
     * Carga las propiedades del idioma desde un archivo.
     * 
     * @param nombreFichero Nombre base del archivo de propiedades.
     * @param idioma        Código del idioma (por ejemplo, "es", "en").
     * @return Objeto Properties con las configuraciones cargadas.
     */
    public Properties loadIdioma(String nombreFichero, String idioma) {
        Properties properties = new Properties();

        if (nombreFichero == null || idioma == null) {
            return properties;
        }

        String path = "src/main/resources/" + nombreFichero + "-" + idioma + ".properties";

        File file = new File(path);

        if (!file.exists() || !file.isFile()) {
            System.out.println("Path:" + file.getAbsolutePath());
            return properties;
        }

        try {
            FileInputStream input = new FileInputStream(path);
            InputStreamReader isr = new InputStreamReader(input, "UTF-8");
            properties.load(isr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return properties;
    }

    /**
     * Obtiene el servicio de usuario configurado.
     * 
     * @return Objeto UsuarioServiceModel para interactuar con los usuarios.
     */
    public UsuarioServiceModel getUsuarioServiceModel() {
        return this.usuarioServiceModel;
    }

    /**
     * Inicializa el tema en el control especificado.
     * 
     * @param anyControl Control en el que se aplicará el tema.
     * @param themeIcon  Icono del tema.
     */
    protected void initializeTheme(Control anyControl, ImageView themeIcon) {
        Platform.runLater(() -> {
            Scene scene = anyControl.getScene();
            if (scene != null) {
                ThemeManager.applyTheme(scene, themeIcon);
            }
        });
    }

    /**
     * Alterna el tema en el control especificado.
     * 
     * @param anyControl Control en el que se alternará el tema.
     * @param themeIcon  Icono del tema.
     */
    protected void toggleTheme(Control anyControl, ImageView themeIcon) {
        Scene scene = anyControl.getScene();
        if (scene != null) {
            ThemeManager.toggleTheme(scene, themeIcon);
        }
    }

    public void abrirVentana(Button boton, String fxml) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(PrincipalApplication.class.getResource(fxml));
            Scene scene = new Scene(fxmlLoader.load(), 410, 810);
            Stage stage = (Stage) boton.getScene().getWindow();
            stage.setTitle("Pantalla " + fxml.replace(".fxml", ""));
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
