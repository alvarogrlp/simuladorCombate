package es.alvarogrlp.marvelsimu.backend.controller;

import java.sql.SQLException;

import es.alvarogrlp.marvelsimu.PrincipalApplication;
import es.alvarogrlp.marvelsimu.backend.config.ConfigManager;
import es.alvarogrlp.marvelsimu.backend.model.UsuarioModel;
import es.alvarogrlp.marvelsimu.backend.controller.abstracts.AbstractController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RegistroController extends AbstractController {

    @FXML
    Button onVolverButton;

    @FXML
    TextField textFiledUsuario;

    @FXML
    TextField textFieldEmail;

    @FXML
    Text textMensaje;

    @FXML
    Button onRegistrarButton;

    @FXML
    PasswordField textFieldPassword;

    @FXML
    PasswordField textFieldPasswordRepit;

    @FXML
    private Text textUsuario;

    @FXML
    private Text textContrasenia;
    
    @FXML
    private Text textRepetirContrasenia;
    
    @FXML
    private Text textEmail;

    /**
     * Inicializa el controlador de registro.
     * Configura los textos y los placeholders de los campos según el idioma seleccionado.
     */
    @FXML
    public void initialize() {
        textUsuario.setText(ConfigManager.ConfigProperties.getProperty("textUsuario"));
        textEmail.setText(ConfigManager.ConfigProperties.getProperty("textEmail"));
        textContrasenia.setText(ConfigManager.ConfigProperties.getProperty("textContrasenia"));
        textRepetirContrasenia.setText(ConfigManager.ConfigProperties.getProperty("textRepetirContrasenia"));
        onRegistrarButton.setText(ConfigManager.ConfigProperties.getProperty("onRegistrarButton"));
        onVolverButton.setText(ConfigManager.ConfigProperties.getProperty("onVolverButton"));
        
        textFiledUsuario.setPromptText(ConfigManager.ConfigProperties.getProperty("promptUsuario"));
        textFieldEmail.setPromptText(ConfigManager.ConfigProperties.getProperty("promptEmail"));
        textFieldPassword.setPromptText(ConfigManager.ConfigProperties.getProperty("promptContrasenia"));
        textFieldPasswordRepit.setPromptText(ConfigManager.ConfigProperties.getProperty("promptRepetirContrasenia"));
        
    }

    /**
     * Maneja el evento de clic en el botón de registro.
     * Valida los datos ingresados por el usuario y registra un nuevo usuario si los datos son válidos.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    @FXML
    protected void onClickRegistar() throws SQLException {

        if (textFieldPassword == null || textFieldPassword.getText().isEmpty()
                || textFieldPasswordRepit == null || textFieldPasswordRepit.getText().isEmpty()) {
            textMensaje.setText("¡El password no puede ser nulo o vacio!");
            return;
        }

        if (textFieldPassword.getText().equals(textFieldPasswordRepit.getText())) {
            textMensaje.setText("¡El password es correcto!");
        }

        UsuarioModel usuarioNuevo = new UsuarioModel(textFieldEmail.getText(), textFiledUsuario.getText(),
                textFieldPassword.getText());

        if (!getUsuarioServiceModel().agregarUsuario(usuarioNuevo)) {
            textMensaje.setText("Usuario ya registrado o null");
            return;
        } else {
            textMensaje.setText("Usuario Registrado Correctamente");
            openVolverClick();
            return;
        }
    }

    /**
     * Abre la pantalla de login.
     * Cambia la escena actual a la pantalla de login.
     */
    @FXML
    protected void openVolverClick() {
        try {
            Stage stage = (Stage) onVolverButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(PrincipalApplication.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 410, 810);
            stage.setTitle("Pantalla Inicio");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}