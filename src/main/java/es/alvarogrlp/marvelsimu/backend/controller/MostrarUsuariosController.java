package es.alvarogrlp.marvelsimu.backend.controller;

import java.sql.SQLException;
import java.util.ArrayList;

import es.alvarogrlp.marvelsimu.PrincipalApplication;
import es.alvarogrlp.marvelsimu.backend.controller.abstracts.AbstractController;
import es.alvarogrlp.marvelsimu.backend.model.UsuarioModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MostrarUsuariosController extends AbstractController {
    @FXML
    Button onVolverButton;
    @FXML
    Button onMostrarrButton;
    @FXML
    Text textUsuarios;

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

    @FXML
    protected void openMostrarClick() throws SQLException {

        String lista = "";
        ArrayList<UsuarioModel> usuarios = getUsuarioServiceModel().obtenerUsuarios();

        for (UsuarioModel usuarioEntity : usuarios) {
            lista += usuarioEntity.toString() + "\n";
        }
        textUsuarios.setText(lista);
    }
}