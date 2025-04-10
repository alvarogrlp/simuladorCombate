package es.alvarogrlp.marvelsimu.backend.controller;

import es.alvarogrlp.marvelsimu.backend.controller.abstracts.AbstractController;
import es.alvarogrlp.marvelsimu.PrincipalApplication;
import es.alvarogrlp.marvelsimu.backend.config.ConfigManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

// Importaciones - reemplazar las importaciones de animación personalizadas por estas:
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

// Añadir import
import es.alvarogrlp.marvelsimu.backend.util.SessionManager;

public class PrincipalController extends AbstractController {
    
    @FXML
    private TextField textFieldUsuario;
    @FXML
    private ImageView imagenPerfil;
    @FXML
    private SplitMenuButton perfilButton;
    @FXML
    private SplitMenuButton onBatallaButton;
    @FXML
    private ImageView iconoModo;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            // Inicializar el tema
            initializeTheme(onBatallaButton, iconoModo);

            // Configurar textos según el idioma
            onBatallaButton.setText(ConfigManager.ConfigProperties.getProperty("onBatallaButton", "Batalla"));
            perfilButton.getStyleClass().add("no-arrow");
            
            // Configurar menús
            configurarMenuBatalla();
            configurarMenuPerfil();
            
            // Eliminar acciones generales
            // onBatallaButton.setOnAction(...);
            // perfilButton.setOnAction(...);

            // Agregar evento para mostrar el menú al pulsar
            perfilButton.setOnMouseClicked(event -> {
                if (!perfilButton.isShowing()) {
                    perfilButton.show();
                }
            });
        });

        imagenCiurcular();
    }
    
    private void configurarMenuBatalla() {
        onBatallaButton.getItems().clear();
        
        MenuItem batallaIndividual = new MenuItem("Batalla Individual");
        batallaIndividual.setOnAction(event -> onBatallaIndividualClick());
        
        MenuItem batallaEquipos = new MenuItem("Batalla por Equipos");
        batallaEquipos.setOnAction(event -> onBatallaEquiposClick());
        
        MenuItem torneo = new MenuItem("Torneo");
        torneo.setOnAction(event -> onTorneoClick());
        
        onBatallaButton.getItems().addAll(batallaIndividual, batallaEquipos, torneo);
    }

    // También necesitas configurar tu perfilButton para que tenga sus MenuItems
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
    
    // Métodos para el menú de batalla
    @FXML
    protected void onBatallaIndividualClick() {
        System.out.println("Abriendo batalla individual");
    }
    
    @FXML
    protected void onBatallaEquiposClick() {
        System.out.println("Abriendo batalla por equipos");
    }
    
    @FXML
    protected void onTorneoClick() {
        System.out.println("Abriendo torneo");
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

   
}
