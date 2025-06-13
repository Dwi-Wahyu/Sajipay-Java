package sajipay.helper;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class StyleHelper {
    public static void styleButton(Button button) {
        button.setStyle(
                "-fx-background-color: #0984e3; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 10 30; " +
                        "-fx-cursor: hand;");
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #74b9ff; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 10 30; " +
                        "-fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #0984e3; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 10 30; " +
                        "-fx-cursor: hand;"));
    }

    public static void styleTextField(TextField textField) {
        textField.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #dfe6e9; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 8; " +
                        "-fx-font-size: 14px;");
        textField.setPrefWidth(200);
        // Hover effect
        textField.setOnMouseEntered(e -> textField.setStyle(
                "-fx-background-color: #f8f9fa; " +
                        "-fx-border-color: #74b9ff; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 8; " +
                        "-fx-font-size: 14px;"));
        textField.setOnMouseExited(e -> textField.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #dfe6e9; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 8; " +
                        "-fx-font-size: 14px;"));
    }

    public static void styleComboBox(ComboBox<?> comboBox) {
        comboBox.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #dfe6e9; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-font-size: 14px;");
        comboBox.setPrefWidth(250);
    }

    public static void styleCheckBox(CheckBox checkBox) {
        checkBox.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-text-fill: #34495e;");
    }

}
