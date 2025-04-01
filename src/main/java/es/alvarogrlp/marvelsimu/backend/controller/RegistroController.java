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
    Button buttonRegistrar;

    @FXML
    PasswordField textFieldPassword;

    @FXML
    PasswordField textFieldPasswordRepit;

    @FXML
    private Text textUsuario;

    @FXML
    private Text textContrasenia;

    @FXML
    public void initialize() {
        textUsuario.setText(ConfigManager.ConfigProperties.getProperty("textUsuario"));
        textContrasenia.setText(ConfigManager.ConfigProperties.getProperty("textContrasenia"));
    }

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