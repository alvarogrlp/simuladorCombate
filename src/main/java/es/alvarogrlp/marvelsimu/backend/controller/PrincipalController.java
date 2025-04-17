package es.alvarogrlp.marvelsimu.backend.controller;

import es.alvarogrlp.marvelsimu.backend.controller.abstracts.AbstractController;
import es.alvarogrlp.marvelsimu.PrincipalApplication;
import es.alvarogrlp.marvelsimu.backend.config.ConfigManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import es.alvarogrlp.marvelsimu.backend.util.SessionManager;

public class PrincipalController extends AbstractController {

    @FXML
    private TextField textFieldUsuario;
    @FXML
    private ImageView imagenPerfil;
    @FXML
    private SplitMenuButton perfilButton;
    @FXML
    private Button onBatallaButton; 
    @FXML
    private ImageView iconoModo;

    @FXML
    public void initialize() {
        // Aplicar el tema actual utilizando el método unificado
        applyCurrentTheme(onBatallaButton, null, iconoModo);
        
        Platform.runLater(() -> {
            try {
                // Configurar el menú de perfil
                perfilButton.getStyleClass().add("no-arrow");
                configurarMenuPerfil();
                
                // Configurar evento para mostrar el menú al hacer clic
                perfilButton.setOnMouseClicked(event -> {
                    if (!perfilButton.isShowing()) {
                        perfilButton.show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error inicializando la pantalla principal: " + e.getMessage());
            }
        });
        
        // Aplicar estilo circular a la imagen de perfil
        imagenCiurcular();
    }

    // Eliminamos configurarMenuBatalla() ya que ya no tenemos un SplitMenuButton para batalla
    
    // Mantenemos la configuración del menú de perfil
    private void configurarMenuPerfil() {
        perfilButton.getItems().clear();

        MenuItem perfil = new MenuItem("Perfil");
        perfil.setOnAction(event -> onPerfilClick());

        MenuItem historial = new MenuItem("Historial");
        historial.setOnAction(event -> onHistorialClick());

        MenuItem cerrarSesion = new MenuItem("Cerrar Sesión");
        cerrarSesion.setOnAction(event -> onCerrarSesionClick());

        perfilButton.getItems().addAll(perfil, historial, cerrarSesion);
    }

    // Métodos para el menú de perfil
    @FXML
    protected void onPerfilClick() {
        abrirVentana(perfilButton, "perfil.fxml");
    }

    @FXML
    protected void onHistorialClick() {
        System.out.println("Abriendo historial de combates");
    }

    @FXML
    protected void onCerrarSesionClick() {
        // Cerrar la sesión
        SessionManager.cerrarSesion();
        // Redirigir al login
        abrirVentana(perfilButton, "login.fxml");
    }

    @FXML
    protected void imagenCiurcular() {
        double size = 180;
        Circle clip = new Circle(size / 2, size / 2, size / 2);
        imagenPerfil.setClip(clip);
    }

    @FXML
    public void abrirVentana(SplitMenuButton boton, String fxml) {
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

    // Añadimos un método sobrecargado para abrirVentana que acepta un Button
    @FXML
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

    // Este es el método que se llamará cuando se haga clic en el botón de batalla
    @FXML
    protected void onBatallaClick() {
        abrirVentana(onBatallaButton, "batalla.fxml");
    }
}
