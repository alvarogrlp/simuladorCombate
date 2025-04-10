package es.alvarogrlp.marvelsimu.backend.controller;

import es.alvarogrlp.marvelsimu.backend.controller.abstracts.AbstractController;
import es.alvarogrlp.marvelsimu.backend.model.UsuarioModel;
import es.alvarogrlp.marvelsimu.backend.model.UsuarioServiceModel;
import es.alvarogrlp.marvelsimu.backend.util.AlertUtils;
import eu.iamgio.animated.transition.AnimatedThemeSwitcher;
import es.alvarogrlp.marvelsimu.PrincipalApplication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.shape.Circle;
import es.alvarogrlp.marvelsimu.backend.util.SessionManager;

public class PerfilController extends AbstractController {

    @FXML
    private TextField textFieldUsuario;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPassword;
    @FXML
    private Button onEliminarButton;
    @FXML
    private Button onVolverButton;
    @FXML
    private ImageView fotoPerfil;
    @FXML
    private ImageView iconoModo;

    private AnimatedThemeSwitcher themeSwitcher;
    private UsuarioModel usuarioActual;
    private UsuarioServiceModel usuarioServiceModel; 

    @FXML
    public void initialize() {
        initializeTheme(textFieldUsuario, iconoModo);

        Platform.runLater(() -> {
            try {
                hacerImagenCircular();
                cargarDatosUsuarioReal();
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtils.mostrarError("Error de conexión", 
                            "No se pudo conectar a la base de datos: " + e.getMessage());
            }
        });
    }

    private void hacerImagenCircular() {
        double radio = Math.min(fotoPerfil.getFitWidth(), fotoPerfil.getFitHeight()) / 2;
        Circle clip = new Circle(radio, radio, radio);
        fotoPerfil.setClip(clip);
    }

    private void cargarDatosUsuarioReal() {
        try {
            usuarioActual = SessionManager.getUsuarioActual();
            
            if (usuarioActual != null) {
                textFieldUsuario.setText(usuarioActual.getNombre());
                textFieldEmail.setText(usuarioActual.getEmail());
                textFieldPassword.setText("••••••••");
            } else {
                UsuarioServiceModel service = getUsuarioServiceModel();
                if (service != null) {
                    AlertUtils.mostrarError("Error", "No hay sesión activa. Por favor vuelve a iniciar sesión.");
                    volverAlLogin();
                } else {
                    AlertUtils.mostrarError("Error", "No se pudo inicializar el servicio de usuario.");
                    volverAlLogin();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error", "Error al cargar datos: " + e.getMessage());
        }
    }

    @FXML
    protected void onEliminarCuentaClick() {
        Alert alert = AlertUtils.mostrarAlerta(
            AlertType.CONFIRMATION, 
            "Confirmar eliminación", 
            "Eliminar cuenta permanentemente", 
            "¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede revertir."
        );
        
        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    boolean exito = getUsuarioServiceModel().eliminarUsuario(usuarioActual);
                    if (exito) {
                        SessionManager.cerrarSesion();
                        
                        AlertUtils.mostrarInfo("Cuenta eliminada", "Tu cuenta ha sido eliminada correctamente.");
                        volverAlLogin();
                    } else {
                        AlertUtils.mostrarError("No se pudo eliminar la cuenta", 
                            "Ocurrió un error al eliminar tu cuenta. Inténtalo de nuevo.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertUtils.mostrarError("Error", "Ocurrió un error: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    protected void onVolverClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(PrincipalApplication.class.getResource("principal.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 410, 810);
            Stage stage = (Stage) onVolverButton.getScene().getWindow();
            stage.setTitle("Pantalla Principal");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error", "No se pudo volver a la pantalla principal: " + e.getMessage());
        }
    }

    private void volverAlLogin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(PrincipalApplication.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 410, 810);
            Stage stage = (Stage) (onEliminarButton != null ? onEliminarButton.getScene() : textFieldUsuario.getScene()).getWindow();
            stage.setTitle("Iniciar Sesión");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error", "No se pudo redirigir al login: " + e.getMessage());
        }
    }

    /**
     * Alterna entre modo oscuro y modo claro.
     * Cambia el estilo de la aplicación y el icono según el modo seleccionado.
     */
    @FXML
    protected void cambiarModo() {
        toggleTheme(textFieldUsuario, iconoModo);
    }
}