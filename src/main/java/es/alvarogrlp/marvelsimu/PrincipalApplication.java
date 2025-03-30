package es.alvarogrlp.marvelsimu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class PrincipalApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Font.loadFont(PrincipalApplication.class.getResourceAsStream("/fonts/BebasNeue-Regular.ttf"), 10);

        FXMLLoader fxmlLoader = new FXMLLoader(PrincipalApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 800);
        stage.setTitle("Pantalla Principal");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}