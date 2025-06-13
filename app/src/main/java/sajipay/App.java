package sajipay;

import javafx.application.Application;
import javafx.stage.Stage;
import sajipay.models.Management;
import sajipay.services.AuthService;
import sajipay.services.DatabaseService;
import sajipay.services.NavigationManager;
import sajipay.utils.DatabaseConnection;

public class App extends Application {

    @Override
    public void init() throws Exception {
        DatabaseService.getInstance();
        Management.getInstance();
        AuthService.getInstance();
        NavigationManager.getInstance();
    }

    @Override
    public void start(Stage primaryStage) {
        NavigationManager navManager = NavigationManager.getInstance();

        navManager.initialize(primaryStage);

        navManager.navigateToLogin();

        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        DatabaseConnection.getInstance().closeConnection();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}