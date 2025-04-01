package es.alvarogrlp.marvelsimu.backend.controller;

import java.sql.SQLException;

import es.alvarogrlp.marvelsimu.PrincipalApplication;
import es.alvarogrlp.marvelsimu.backend.controller.abstracts.AbstractController;
import es.alvarogrlp.marvelsimu.backend.model.UsuarioModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RecuperarController extends AbstractController {
    @FXML
    Button onVolverButton;
    @FXML
    Text textAviso;
    @FXML
    TextField textFieldEmail;
    @FXML
    Button onRecuperarButton;

    @FXML
    protected void onClickRecuperar() {

        if (textFieldEmail == null || textFieldEmail.getText().isEmpty()) {
            textAviso.setText("¡El password no puede ser nulo o vacio!");
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
        try {
            Stage stage = (Stage) onVolverButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(PrincipalApplication.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 820, 640);
            stage.setTitle("Pantalla Inicio");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}