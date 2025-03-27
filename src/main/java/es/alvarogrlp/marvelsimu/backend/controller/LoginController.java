package es.ies.puerto.controller;

import es.ies.puerto.PrincipalApplication;
import es.ies.puerto.abstractas.AbstractController;
import es.ies.puerto.config.ConfigManager;
import es.ies.puerto.model.Usuario;
import es.ies.puerto.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginController extends AbstractController {

    @FXML
    private TextField textFieldUsuario;

    @FXML
    private PasswordField textFieldPassword;

    @FXML
    private Text textFieldMensaje;

    @FXML
    private Text textUsuario;

    @FXML
    private Text textContrasenia;

    private UsuarioService usuarioService;

    public LoginController() {
        usuarioService = new UsuarioService();
    }

    @FXML
    protected void onLoginButtonClick() {

        if (textFieldUsuario == null || textFieldUsuario.getText().isEmpty() || 
            textFieldPassword == null || textFieldPassword.getText().isEmpty()) {
            return;
        }

        Usuario usuario = usuarioService.getUsuarios().stream()
            .filter(u -> u.getNombreUsuario().equals(textFieldUsuario.getText()) && u.getPassword().equals(textFieldPassword.getText()))
            .findFirst()
            .orElse(null);

        if (usuario == null) {
            return;
        }
        mostrarDatosUsuario(usuario);
    }
}