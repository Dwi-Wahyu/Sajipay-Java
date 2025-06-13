package sajipay.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import sajipay.enums.Role;
import sajipay.helper.AlertHelper;
import sajipay.helper.FormHelper;
import sajipay.models.Employee;
import sajipay.services.AuthService;
import sajipay.services.NavigationManager;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleCombo;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    // Inisialisasi service yang dibutuhkan
    private final AuthService authService = AuthService.getInstance();
    private final NavigationManager navigationManager = NavigationManager.getInstance();

    /**
     * Metode ini dipanggil secara otomatis setelah file FXML selesai dimuat.
     * Gunakan untuk inisialisasi awal.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Mengisi pilihan pada ComboBox
        roleCombo.getItems().addAll("Employee", "Customer");
    }

    /**
     * Menangani aksi ketika tombol Login ditekan.
     */
    @FXML
    void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleCombo.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Harap isi semua kolom.");
            return;
        }

        boolean success = authService.authenticateAndLogin(username, password, role);

        if (success) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Sukses", "Login berhasil!");

            FormHelper.clearFields(usernameField, passwordField);
            roleCombo.getSelectionModel().clearSelection();

            if (role.equals("Customer")) {
                navigationManager.navigateToListProduct();
            } else {
                Employee employee = (Employee) authService.getCurrentUser();
                if (employee.role == Role.MANAGER) {
                    navigationManager.navigateToManagerDashboard();
                } else if (employee.role == Role.CHEF) {
                    navigationManager.navigateToChefDashboard();
                } else {
                    AlertHelper.showAlert(AlertType.WARNING, "Info", "Dashboard for your role is not available yet.");
                    navigationManager.navigateToLogin();
                }

            }
        } else {
            // Jika gagal, tampilkan pesan error
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Username, password, atau role salah.");
        }
    }

    /**
     * Menangani aksi ketika tombol Register ditekan.
     */
    @FXML
    void handleRegisterButtonAction() {
        navigationManager.navigateToRegisterCustomer();
    }
}