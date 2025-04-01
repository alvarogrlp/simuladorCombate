package es.alvarogrlp.marvelsimu.backend.controller;

import java.util.ArrayList;
import java.util.List;
import es.alvarogrlp.marvelsimu.PrincipalApplication;
import es.alvarogrlp.marvelsimu.backend.config.ConfigManager;
import es.alvarogrlp.marvelsimu.backend.controller.abstracts.AbstractController;
import es.alvarogrlp.marvelsimu.backend.model.UsuarioModel;
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
    private Button onRecuperarButton;
    
    @FXML
    private Button onMostrarButton;

    @FXML
    private Text textContrasenia;

    @FXML
    private ComboBox comboIdioma;

    @FXML
    public void initialize() {
        List<String> idiomas = new ArrayList<>();
        idiomas.add("es");
        idiomas.add("en");
        idiomas.add("fr");
        comboIdioma.getItems().addAll(idiomas);
    }

    @FXML
    protected void cambiarIdioma() {
        String path = "src/main/resources/idioma-" + comboIdioma.getValue().toString() + ".properties";

        ConfigManager.ConfigProperties.setPath(path);

        textUsuario.setText(ConfigManager.ConfigProperties.getProperty("textUsuario"));
        textContrasenia.setText(ConfigManager.ConfigProperties.getProperty("textContrasenia"));
    }

    @FXML
    protected void onLoginButtonClick() {

        if (textFieldUsuario == null || textFieldUsuario.getText().isEmpty() ||
                textFieldPassword == null || textFieldPassword.getText().isEmpty()) {
            textFieldMensaje.setText("Credenciales null o vacias");
            return;
        }

        UsuarioModel usuarioEntity = getUsuarioServiceModel().obtenerCredencialesUsuario(textFieldUsuario.getText());

        if (usuarioEntity == null) {
            textFieldMensaje.setText("El usuario no existe");
            return;
        }

        if ((textFieldUsuario.getText().equals(usuarioEntity.getEmail())
                || textFieldUsuario.getText().equals(usuarioEntity.getNombre()))
                        && textFieldPassword.getText().equals(usuarioEntity.getContrasenia())) {
            textFieldMensaje.setText("Usuario validado correctamente");
            return;

        }
        textFieldMensaje.setText("Credenciales invalidas");
    }

    @FXML
    protected void openRegistrarClick() {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(PrincipalApplication.class.getResource("registro.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 410, 810);
            Stage stage = (Stage) onRecuperarButton.getScene().getWindow();
            stage.setTitle("Pantalla Registro");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void openRecuperarClick() {

        try {
            Stage stage = (Stage) onRecuperarButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(PrincipalApplication.class.getResource("recuperar.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 820, 640);
            stage.setTitle("Pantalla Recuperacion");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void openMostrarClick() {

        try {
            Stage stage = (Stage) onMostrarButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(PrincipalApplication.class.getResource("mostrarUsuarios.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 820, 640);
            stage.setTitle("Pantalla Recuperacion");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}