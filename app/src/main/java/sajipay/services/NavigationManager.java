package sajipay.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import sajipay.controllers.UpdateEmployeeController;
import sajipay.helper.AlertHelper;
import sajipay.models.Employee;

import java.io.IOException;
import java.net.URL;

public class NavigationManager {

    private static final NavigationManager INSTANCE = new NavigationManager();
    private Stage primaryStage;
    private Scene currentScene;

    private NavigationManager() {
    }

    public static NavigationManager getInstance() {
        return INSTANCE;
    }

    public void initialize(Stage stage) {
        if (this.primaryStage == null) {
            this.primaryStage = stage;
            this.primaryStage.setTitle("Sajipay App");
        }
    }

    private void navigateTo(String fxmlPath) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("Tidak dapat menemukan file FXML di path: " + fxmlPath);
                // Tambahkan alert untuk memberitahu pengguna
                AlertHelper.showAlert(AlertType.ERROR, "Fatal Error", "Resource file not found: " + fxmlPath);
                return;
            }

            Parent root = FXMLLoader.load(fxmlUrl);

            if (currentScene == null) {
                currentScene = new Scene(root);
                primaryStage.setScene(currentScene);
            } else {
                currentScene.setRoot(root);
            }

            primaryStage.sizeToScene();
            primaryStage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("Gagal memuat FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public void navigateToUpdateEmployee(Employee employee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UpdateEmployee.fxml"));
            Parent root = loader.load();

            // Mengirim data karyawan ke controller halaman update
            UpdateEmployeeController controller = loader.getController();
            controller.initData(employee);

            if (currentScene == null) {
                currentScene = new Scene(root);
                primaryStage.setScene(currentScene);
            } else {
                currentScene.setRoot(root);
            }
            primaryStage.sizeToScene();
            primaryStage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("Failed to load FXML: /views/UpdateEmployee.fxml");
            e.printStackTrace();
        }
    }

    public void navigateToLogin() {
        navigateTo("/views/Login.fxml");
    }

    public void navigateToProfile() {
        navigateTo("/views/Profile.fxml");
    }

    public void navigateToEmployeeDashboard() {
        navigateTo("/views/Employee.fxml");
    }

    public void navigateToAddProduct() {
        navigateTo("/views/AddProduct.fxml");
    }

    public void navigateToAddEmployee() {
        navigateTo("/views/AddEmployee.fxml");
    }

    public void navigateToManagerDashboard() {
        navigateTo("/views/ManagerDashboard.fxml");
    }

    public void navigateToChefDashboard() {
        navigateTo("/views/ChefDashboard.fxml");
    }

    public void navigateToRegisterCustomer() {
        navigateTo("/views/Register.fxml");
    }

    public void navigateToListProduct() {
        navigateTo("/views/ListProduct.fxml");
    }

    public void navigateToOrderHistory() {
        navigateTo("/views/OrderHistory.fxml");
    }
}