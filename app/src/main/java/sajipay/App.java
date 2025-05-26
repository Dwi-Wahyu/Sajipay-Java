package sajipay;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Membuat label
        Label label = new Label("Selamat Datang di Sajipay !");

        // Membuat tombol
        Button button = new Button("Klik Saya");
        button.setOnAction(e -> label.setText("Tombol telah diklik!"));

        // Mengatur layout
        VBox layout = new VBox(10); // Vertical Box dengan jarak 10px
        layout.getChildren().addAll(label, button);

        // Membuat scene
        Scene scene = new Scene(layout, 300, 200);

        // Mengatur stage
        primaryStage.setTitle("Contoh Aplikasi JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}