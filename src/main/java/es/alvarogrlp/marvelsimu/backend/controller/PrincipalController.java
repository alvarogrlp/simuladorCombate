package es.alvarogrlp.marvelsimu.backend.controller;

import es.alvarogrlp.marvelsimu.backend.controller.abstracts.AbstractController;
import es.alvarogrlp.marvelsimu.backend.config.ConfigManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class PrincipalController extends AbstractController {
    @FXML
    private ImageView imagenPerfil;

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
        });
        imagenCiurcular();

    }

    @FXML
    protected void imagenCiurcular() {
        double size = 180; 
        Circle clip = new Circle(size / 2, size / 2, size / 2);
        imagenPerfil.setClip(clip);
    }
}
