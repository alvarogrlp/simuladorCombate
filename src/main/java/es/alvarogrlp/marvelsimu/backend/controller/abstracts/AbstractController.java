package es.alvarogrlp.marvelsimu.backend.controller.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import es.alvarogrlp.marvelsimu.backend.config.ThemeManager;
import es.alvarogrlp.marvelsimu.backend.model.UsuarioModel;
import es.alvarogrlp.marvelsimu.backend.model.UsuarioServiceModel;
import es.alvarogrlp.marvelsimu.backend.util.AlertUtils;
import es.alvarogrlp.marvelsimu.backend.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public abstract class AbstractController {

    @FXML
    private TextField textFieldUsuario;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPassword;

    protected static final String PATH_DB = "src/main/resources/marvelSimu.db";

    private UsuarioServiceModel usuarioServiceModel;

    private Properties propertiesIdioma;

    private UsuarioModel usuarioActual;

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

    /**
     * Aplica el tema seleccionado por el usuario a la escena actual.
     * Este método debe llamarse en el initialize() de cada controlador
     * para mantener la coherencia del tema en toda la aplicación.
     * 
     * @param anyControl Cualquier control de la escena actual
     * @param fondo ImageView de fondo que se actualizará según el tema (puede ser null)
     * @param iconoModo Icono para cambiar de tema (puede ser null si la ventana no permite cambiar el tema)
     */
    protected void applyCurrentTheme(Control anyControl, ImageView fondo, ImageView iconoModo) {
        Platform.runLater(() -> {
            try {
                // Verificar que el control no sea nulo antes de intentar acceder a su escena
                if (anyControl == null) {
                    System.err.println("Error: Control nulo en applyCurrentTheme");
                    return;
                }
                
                Scene scene = anyControl.getScene();
                if (scene != null) {
                    // Aplicar el tema actual sin animación
                    ThemeManager.applyTheme(scene, iconoModo);
                    
                    // Si hay un fondo, actualizarlo según el tema
                    if (fondo != null) {
                        String imagePath = ThemeManager.isDarkMode() ? 
                            "/images/fondoNegro.png" : 
                            "/images/fondoBlanco.png";
                        
                        try {
                            Image image = new Image(getClass().getResourceAsStream(imagePath));
                            fondo.setImage(image);
                        } catch (Exception e) {
                            System.err.println("Error cargando la imagen de fondo: " + e.getMessage());
                        }
                    }
                } else {
                    System.err.println("Advertencia: Scene nula en applyCurrentTheme para el control: " + 
                        (anyControl != null ? anyControl.getClass().getSimpleName() : "desconocido"));
                }
            } catch (Exception e) {
                System.err.println("Error en applyCurrentTheme: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Método para abrir una nueva ventana manteniendo las dimensiones correctas
     */
    protected void abrirVentana(Button boton, String fxml) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/alvarogrlp/marvelsimu/" + fxml));
            Scene scene = new Scene(fxmlLoader.load(), 410, 810);
            Stage stage = (Stage) boton.getScene().getWindow();
            stage.setTitle("Marvel Simulator - " + fxml.replace(".fxml", ""));
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error al abrir ventana", 
                "No se pudo abrir la ventana " + fxml + ". Error: " + e.getMessage());
        }
    }

    protected void abrirVentanaJuego(Button boton, String fxml) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/alvarogrlp/marvelsimu/" + fxml));
            Scene scene = new Scene(fxmlLoader.load(), 896, 810);
            Stage stage = (Stage) boton.getScene().getWindow();
            stage.setTitle("Marvel Simulator - " + fxml.replace(".fxml", ""));
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error al abrir ventana", 
                "No se pudo abrir la ventana " + fxml + ". Error: " + e.getMessage());
        }
    }

    /**
     * Carga los datos del usuario que está logeado
     */
    public void cargarDatosUsuarioReal() {
        try {
            usuarioActual = SessionManager.getUsuarioActual();
            
            if (usuarioActual != null) {
                textFieldUsuario.setText(usuarioActual.getNombre());
                textFieldEmail.setText(usuarioActual.getEmail());
                textFieldPassword.setText("••••••••");
            } else {
                UsuarioServiceModel service = getUsuarioServiceModel();
                if (service != null) {
                    System.out.println("No hay sesión activa. Por favor vuelve a iniciar sesión.");
                } else {
                    System.out.println("No se pudo inicializar el servicio de usuario.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al cargar datos: " + e.getMessage());
        }
    }
}
