import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class JavaFXTest extends Application {

    @Override
    public void start(Stage stage) {

        Label title = new Label("Smart Student Academic Performance Analyzer");

        Label message = new Label("JavaFX is working successfully!");

        VBox root = new VBox(20);
        root.getChildren().addAll(title, message);

        Scene scene = new Scene(root, 500, 200);

        stage.setTitle("Student Performance Analyzer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}