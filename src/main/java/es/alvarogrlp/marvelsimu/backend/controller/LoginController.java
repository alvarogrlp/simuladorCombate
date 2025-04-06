package es.alvarogrlp.marvelsimu.backend.controller;

import java.util.ArrayList;
import java.util.List;

import es.alvarogrlp.marvelsimu.PrincipalApplication;
import es.alvarogrlp.marvelsimu.backend.config.ConfigManager;
import es.alvarogrlp.marvelsimu.backend.controller.abstracts.AbstractController;
import es.alvarogrlp.marvelsimu.backend.model.UsuarioModel;
import es.alvarogrlp.marvelsimu.backend.util.ThemeManager;
import eu.iamgio.animated.transition.AnimatedThemeSwitcher;
import eu.iamgio.animated.transition.animations.clip.CircleClipOut;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginController extends AbstractController {

    @FXML
    private TextField textFieldUsuario;

    @FXML
    private PasswordField textFieldPassword;

    @FXML
    private Text textMensaje;

    @FXML
    private Text textUsuario;

    @FXML
    private Text textPregunta;

    @FXML
    private Button onRecuperarButton;
    
    @FXML
    private Button onRegistrarButton;

    @FXML
    private Button onEntrarButton;

    @FXML
    private Text textContrasenia;

    @FXML
    private ComboBox comboIdioma;

    @FXML
    private ImageView iconoModo;

    /**
     * Inicializa el controlador de login.
     * Carga los idiomas disponibles en el ComboBox y establece el idioma actual.
     */
    @FXML
    public void initialize() {
        // Ejecutar después de que la escena esté completamente inicializada
        Platform.runLater(() -> {
            ThemeManager.applyTheme(textFieldUsuario.getScene(), iconoModo);
        });

        // Configurar idiomas en el ComboBox
        List<String> idiomas = new ArrayList<>();
        idiomas.add("es");
        idiomas.add("en");
        idiomas.add("fr");
        comboIdioma.getItems().addAll(idiomas);

        String idiomaActual = ConfigManager.ConfigProperties.getProperty("idiomaActual", "es");
        comboIdioma.setValue(idiomaActual);

        cambiarIdioma();
    }

    /**
     * Cambia el idioma de la aplicación según el seleccionado en el ComboBox.
     * Actualiza los textos de la interfaz al idioma seleccionado.
     */
    @FXML
    protected void cambiarIdioma() {
        String idiomaSeleccionado = comboIdioma.getValue().toString();
        String path = "src/main/resources/idioma-" + idiomaSeleccionado + ".properties";

        ConfigManager.ConfigProperties.setPath(path);
        ConfigManager.ConfigProperties.setProperty("idiomaActual", idiomaSeleccionado);

        textUsuario.setText(ConfigManager.ConfigProperties.getProperty("textUsuario"));
        textContrasenia.setText(ConfigManager.ConfigProperties.getProperty("textContrasenia"));
        textPregunta.setText(ConfigManager.ConfigProperties.getProperty("textPregunta"));
        onRecuperarButton.setText(ConfigManager.ConfigProperties.getProperty("onRecuperarButton"));
        onRegistrarButton.setText(ConfigManager.ConfigProperties.getProperty("onRegistrarButton"));
        onEntrarButton.setText(ConfigManager.ConfigProperties.getProperty("onEntrarButton"));

        textFieldUsuario.setPromptText(ConfigManager.ConfigProperties.getProperty("promptUsuario"));
        textFieldPassword.setPromptText(ConfigManager.ConfigProperties.getProperty("promptContrasenia"));
    }

    /**
     * Maneja el evento de clic en el botón de login.
     * Valida las credenciales ingresadas por el usuario.
     */
    @FXML
    protected void onLoginButtonClick() {
        if (textFieldUsuario == null || textFieldUsuario.getText().isEmpty() ||
                textFieldPassword == null || textFieldPassword.getText().isEmpty()) {
            textMensaje.setText("❌ " + ConfigManager.ConfigProperties.getProperty("mensajeCredencialesVacias") + " ❌");
            textMensaje.setStyle("-fx-fill: red;"); // Cambiar el color a rojo
            return;
        }

        UsuarioModel usuarioEntity = getUsuarioServiceModel().obtenerCredencialesUsuario(textFieldUsuario.getText());

        if (usuarioEntity == null) {
            textMensaje.setText("❌ " + ConfigManager.ConfigProperties.getProperty("mensajeUsuarioNoExiste") + " ❌");
            textMensaje.setStyle("-fx-fill: red;"); // Cambiar el color a rojo
            return;
        }

        if ((textFieldUsuario.getText().equals(usuarioEntity.getEmail())
                || textFieldUsuario.getText().equals(usuarioEntity.getNombre()))
                && textFieldPassword.getText().equals(usuarioEntity.getContrasenia())) {
            textMensaje.setText("✅ " + ConfigManager.ConfigProperties.getProperty("mensajeUsuarioValidado") + " ✅");
            textMensaje.setStyle("-fx-fill: green;"); // Cambiar el color a verde para éxito
            return;
        }

        textMensaje.setText("❌ " + ConfigManager.ConfigProperties.getProperty("mensajeCredencialesInvalidas") + " ❌");
        textMensaje.setStyle("-fx-fill: red;"); // Cambiar el color a rojo
    }

    /**
     * Abre la pantalla de registro.
     * Cambia la escena actual a la pantalla de registro.
     */
    @FXML
    protected void openRegistrarClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(PrincipalApplication.class.getResource("registro.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 410, 810);
            Stage stage = (Stage) onRegistrarButton.getScene().getWindow();
            stage.setTitle("Pantalla Registro");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre la pantalla de recuperación de contraseña.
     * Cambia la escena actual a la pantalla de recuperación.
     */
    @FXML
    protected void openRecuperarClick() {
        try {
            Stage stage = (Stage) onRecuperarButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(PrincipalApplication.class.getResource("recuperar.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 410, 810);
            stage.setTitle("Pantalla Recuperacion");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Alterna entre modo oscuro y modo claro.
     * Cambia el estilo de la aplicación y el icono según el modo seleccionado.
     */
    @FXML
    protected void cambiarModo() {
        var scene = textFieldUsuario.getScene();
        AnimatedThemeSwitcher themeSwitcher = new AnimatedThemeSwitcher(scene, new CircleClipOut());
        themeSwitcher.init();
        ThemeManager.toggleTheme(scene, iconoModo);
    }

    /**
     * Establece el idioma de la aplicación.
     * @param idioma Código del idioma seleccionado (por ejemplo, "es", "en").
     */
    public void setIdioma(String idioma) {
        ConfigManager.ConfigProperties.setPath("src/main/resources/idioma-" + idioma + ".properties");
        cambiarIdioma();
    }
}