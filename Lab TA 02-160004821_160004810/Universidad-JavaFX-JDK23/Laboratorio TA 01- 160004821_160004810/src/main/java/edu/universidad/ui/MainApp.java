package edu.universidad.ui;

import edu.universidad.persistence.Schema;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Schema.crearTablas();

        URL fxml = getClass().getResource("/vistas/MainView.fxml");
        if (fxml == null) {
            throw new IllegalStateException("No se encontró /vistas/MainView.fxml en resources");
        }

        Parent root = FXMLLoader.load(fxml);
        Scene scene = new Scene(root);

        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("Universidad - Universidad (JavaFX + H2)");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
