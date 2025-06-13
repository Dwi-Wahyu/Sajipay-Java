package sajipay.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sajipay.enums.Payment;
import sajipay.helper.AlertHelper;
import sajipay.helper.FormHelper;
import sajipay.models.Customer;
import sajipay.models.Management;
import sajipay.services.NavigationManager;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField balanceField;

    @FXML
    private ComboBox<Payment> paymentCombo;

    @FXML
    private PasswordField pinField;

    private final Management management = Management.getInstance();
    private final NavigationManager navigationManager = NavigationManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Mengisi ComboBox dengan semua nilai dari enum Payment
        paymentCombo.getItems().addAll(Payment.values());
    }

    @FXML
    void handleRegisterButtonAction() {
        try {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String pin = pinField.getText().trim();
            String balanceStr = balanceField.getText().trim();

            // 1. Validasi input yang wajib diisi
            if (username.isEmpty() || password.isEmpty() || pin.isEmpty()) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Username, password, and PIN are required.");
                return;
            }

            // 2. Validasi keunikan username
            if (management.getUserByUsername(username) != null) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Username already exists.");
                return;
            }

            // 3. Validasi panjang password
            if (password.length() < 1) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Password must be at least 1 characters.");
                return;
            }

            // 4. Validasi format PIN
            if (!pin.matches("\\d{6}")) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "PIN must be exactly 6 digits.");
                return;
            }

            // 5. Validasi dan parse saldo
            double balance = 0.0;
            if (!balanceStr.isEmpty()) {
                balance = Double.parseDouble(balanceStr);
                if (balance < 0) {
                    AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Balance cannot be negative.");
                    return;
                }
            }

            // 6. Buat objek Customer baru dan simpan
            Payment payment = paymentCombo.getValue();
            Customer newCustomer = new Customer(username, password, balance, payment, pin);
            management.addUser(newCustomer);

            // 7. Beri notifikasi sukses dan arahkan ke halaman login
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful! Please log in.");

            // Bersihkan form
            FormHelper.clearFields(usernameField, passwordField, balanceField, pinField);
            paymentCombo.getSelectionModel().clearSelection();

            navigationManager.navigateToLogin();

        } catch (NumberFormatException ex) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid number for balance.");
        }
    }

    @FXML
    void handleBackButtonAction() {
        navigationManager.navigateToLogin();
    }
}
