package es.alvarogrlp.marvelsimu.backend.controller;

import es.alvarogrlp.marvelsimu.backend.controller.abstracts.AbstractController;
import es.alvarogrlp.marvelsimu.backend.util.AlertUtils;
import es.alvarogrlp.marvelsimu.PrincipalApplication;
import es.alvarogrlp.marvelsimu.backend.config.ThemeManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import eu.iamgio.animated.transition.AnimatedThemeSwitcher;
import eu.iamgio.animated.transition.animations.clip.CircleClipOut;

public class BatallaController extends AbstractController {

    @FXML
    private Button on3v3Button;
    @FXML
    private Button on5v5Button;
    @FXML
    private Button onHistoraiButton;
    @FXML
    private Button onVolverButton;
    @FXML
    private ImageView fondo;

    private AnimatedThemeSwitcher themeSwitcher;

    @FXML
    public void initialize() {
        // Aplicamos el tema actual utilizando el método unificado del AbstractController
        applyCurrentTheme(on3v3Button, fondo, null);
    }

    @FXML
    protected void on3vs3Click() {
       abrirVentanaJuego(on3v3Button, "seleccionPersonajes.fxml");
    }

    @FXML
    protected void on5vs5Click() {
        AlertUtils.mostrarInfo("Modo 5 vs 5", "Este modo está en desarrollo. Pronto podrás disfrutar de batallas 5 contra 5.");
    }

    @FXML
    protected void onHistoriaClick() {
        AlertUtils.mostrarInfo("Modo Historia", "El modo historia está en desarrollo. ¡Mantente atento a las actualizaciones!");
    }

    @FXML
    protected void onVolverClick() {
        abrirVentana(onVolverButton, "principal.fxml");
    }

    public void abrirVentanaJuego(Button boton, String fxml) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(PrincipalApplication.class.getResource(fxml));
            Scene scene = new Scene(fxmlLoader.load(), 896, 810);
            Stage stage = (Stage) boton.getScene().getWindow();
            stage.setTitle("Pantalla " + fxml.replace(".fxml", ""));
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}