package sajipay.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import sajipay.services.NavigationManager;

public class AvatarComponent extends HBox {
    public AvatarComponent(String username, String role) {
        // Initialize component
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(10);
        this.getStyleClass().add("avatar-component");

        NavigationManager navigationManager = NavigationManager.getInstance();

        // Create avatar circle with initial
        String initial = username.isEmpty() ? "?" : username.substring(0, 1).toUpperCase();
        Label initialLabel = new Label(initial);
        initialLabel.getStyleClass().add("avatar-initial");
        initialLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

        Circle circle = new Circle(25); // Radius 25
        circle.getStyleClass().add("avatar-circle");

        StackPane avatarPane = new StackPane(circle, initialLabel);
        avatarPane.getStyleClass().add("avatar-pane");

        VBox vBox = new VBox();

        // Create username and role labels
        Label usernameLabel = new Label(username);
        usernameLabel.getStyleClass().add("avatar-username");
        usernameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label roleLabel = new Label(role);
        roleLabel.getStyleClass().add("avatar-role");
        roleLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));

        vBox.getChildren().addAll(usernameLabel, roleLabel);

        this.setOnMouseClicked(event -> {
            navigationManager.navigateToProfile();
        });

        // Add components to HBox
        this.getChildren().addAll(avatarPane, vBox);
    }
}
