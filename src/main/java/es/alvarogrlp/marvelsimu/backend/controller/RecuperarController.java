package es.alvarogrlp.marvelsimu.backend.controller;

import es.alvarogrlp.marvelsimu.backend.config.ConfigManager;
import es.alvarogrlp.marvelsimu.backend.controller.abstracts.AbstractController;
import es.alvarogrlp.marvelsimu.backend.model.UsuarioModel;
import es.alvarogrlp.marvelsimu.backend.util.AlertUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

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
        try {
            if (textFieldEmail == null || textFieldEmail.getText().isEmpty()) {
                textAviso.setText("❌ " + ConfigManager.ConfigProperties.getProperty("mensajeEmailVacio", "¡El email no puede estar vacío!") + " ❌");
                textAviso.setStyle("-fx-fill: red;");
                return;
            }

            // Usar el nuevo método para trabajar con marvelSimu.db
            UsuarioModel usuarioEntity = getUsuarioServiceModel().obtenerCredencialesUsuario(textFieldEmail.getText());

            if (usuarioEntity == null) {
                textAviso.setText("❌ " + ConfigManager.ConfigProperties.getProperty("mensajeUsuarioNoExiste", "El usuario no existe") + " ❌");
                textAviso.setStyle("-fx-fill: red;");
                return;
            }

            // Aquí podría ir un código para enviar un email real de recuperación
            // Por ahora solo mostramos un mensaje de confirmación
            textAviso.setText("✅ " + ConfigManager.ConfigProperties.getProperty("mensajeRecuperacionEnviada", "Recuperación enviada") + " ✅");
            textAviso.setStyle("-fx-fill: green;");
            
            // Opcional: Redirigir al usuario a la pantalla de login después de un tiempo
            new Thread(() -> {
                try {
                    Thread.sleep(3000); // Esperar 3 segundos
                    Platform.runLater(() -> openVolverClick());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
        } catch (Exception e) {
            e.printStackTrace();
            textAviso.setText("❌ " + ConfigManager.ConfigProperties.getProperty("mensajeErrorSistema", "Error en el sistema") + " ❌");
            textAviso.setStyle("-fx-fill: red;");
            
            // Registrar el error detallado en la consola
            System.err.println("Error en recuperación de contraseña: " + e.getMessage());
            AlertUtils.mostrarError("Error", "Ha ocurrido un error: " + e.getMessage());
        }
    }

    @FXML
    protected void openVolverClick() {
        abrirVentana(onVolverButton, "login.fxml");
    }
}