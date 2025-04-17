package es.alvarogrlp.marvelsimu.backend.controller;

import java.sql.SQLException;

import es.alvarogrlp.marvelsimu.PrincipalApplication;
import es.alvarogrlp.marvelsimu.backend.config.ConfigManager;
import es.alvarogrlp.marvelsimu.backend.config.ThemeManager;
import es.alvarogrlp.marvelsimu.backend.controller.abstracts.AbstractController;
import es.alvarogrlp.marvelsimu.backend.model.UsuarioModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RecuperarController extends AbstractController {
    
    @FXML
    private TextField textFieldEmail;
    @FXML
    private Button onEnviarButton;
    @FXML
    private Text textAviso;
    @FXML
    private Button onVolverButton;

    @FXML
    public void initialize() {
        // Aplicar el tema actual utilizando el método unificado del AbstractController
        applyCurrentTheme(textFieldEmail, null, null);

        // Inicializar los textos con el idioma actual
        Platform.runLater(() -> {
            textAviso.setText("");
            onVolverButton.setText(ConfigManager.ConfigProperties.getProperty("onVolverButton", "Volver"));
            onEnviarButton.setText(ConfigManager.ConfigProperties.getProperty("onEnviarButton", "Enviar"));
            textFieldEmail.setPromptText(ConfigManager.ConfigProperties.getProperty("promptEmail", "Email"));
        });
    }

    @FXML
    protected void onClickRecuperar() {

        if (textFieldEmail == null || textFieldEmail.getText().isEmpty()) {
            textAviso.setText("¡El password no puede ser vacio!");
            return;
        }

        UsuarioModel usuarioEntity = getUsuarioServiceModel().obtenerCredencialesUsuario(textFieldEmail.getText());

        if (usuarioEntity == null) {
            textAviso.setText("El usuario no existe");
            return;
        }

        textAviso.setText("Recuperación enviada");
    }

    @FXML
    protected void openVolverClick() {
        abrirVentana(onVolverButton, "login.fxml");
    }
}