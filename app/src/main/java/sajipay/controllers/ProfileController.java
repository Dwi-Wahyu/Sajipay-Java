package sajipay.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import sajipay.components.AvatarComponent;
import sajipay.enums.Role;
import sajipay.helper.AlertHelper;
import sajipay.models.Customer;
import sajipay.models.Employee;
import sajipay.models.User;
import sajipay.services.AuthService;
import sajipay.services.NavigationManager;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML
    private VBox mainLayout;
    @FXML
    private GridPane detailsGrid;
    @FXML
    private Button logoutButton;
    @FXML
    private Button backButton;
    @FXML
    private VBox customerActionsVBox;
    @FXML
    private TextField topUpField;
    @FXML
    private Button topUpButton;
    @FXML
    private Button historyButton;

    private final AuthService authService = AuthService.getInstance();
    private final NavigationManager navigationManager = NavigationManager.getInstance();
    private User currentUser;
    private Label balanceValueLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            navigationManager.navigateToLogin();
            return;
        }

        AvatarComponent avatar = new AvatarComponent(currentUser.username, authService.getRole());
        mainLayout.getChildren().add(0, avatar);

        if ("Customer".equals(authService.getRole()) && currentUser instanceof Customer) {
            displayCustomerProfile((Customer) currentUser);

            // Cukup tampilkan komponen yang sudah ada dari FXML
            customerActionsVBox.setVisible(true);
            customerActionsVBox.setManaged(true);
            historyButton.setVisible(true);
            historyButton.setManaged(true);

            // --- KESALAHAN 1: Hapus kode di bawah ini ---
            // Kode yang mencoba menambahkan ulang 'historyButton' telah dihapus
            // karena tombol sudah menjadi bagian dari 'mainLayout' melalui FXML.

        } else if ("Employee".equals(authService.getRole()) && currentUser instanceof Employee) {
            displayEmployeeProfile((Employee) currentUser);
        }
    }

    private void displayCustomerProfile(Customer customer) {
        balanceValueLabel = new Label(String.format("Rp %,.2f", customer.getBalance()));
        addDetailRow("Balance:", balanceValueLabel);

        // --- KESALAHAN 2: Perbaiki potensi NullPointerException di sini ---
        String paymentText = customer.getPayment() != null ? customer.getPayment().name() : "Not set";
        Label paymentLabel = new Label(paymentText);
        addDetailRow("Payment:", paymentLabel);
    }

    private void displayEmployeeProfile(Employee employee) {
        // Kode ini sudah benar, tidak perlu diubah
        addDetailRow("Role:", new Label(employee.role.name()));
        addDetailRow("Experience:", new Label(employee.yearsOfExperience + " years"));
        addDetailRow("Salary:", new Label(String.format("Rp %,.2f", employee.getSalary())));
    }

    private void addDetailRow(String key, Label valueLabel) {
        int rowIndex = detailsGrid.getRowCount();
        Label keyLabel = new Label(key);
        keyLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        valueLabel.setFont(Font.font("System", 14));
        detailsGrid.add(keyLabel, 0, rowIndex);
        detailsGrid.add(valueLabel, 1, rowIndex);
    }

    @FXML
    void handleTopUpButtonAction() {
        Customer customer = (Customer) currentUser;
        String amountText = topUpField.getText().trim();

        if (amountText.isEmpty()) {
            AlertHelper.showAlert(AlertType.ERROR, "Error", "Please enter a top-up amount.");
            return;
        }

        double topUpAmount;
        try {
            topUpAmount = Double.parseDouble(amountText);
            if (topUpAmount <= 0) {
                AlertHelper.showAlert(AlertType.ERROR, "Error", "Top-up amount must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            AlertHelper.showAlert(AlertType.ERROR, "Error", "Please enter a valid number for the amount.");
            return;
        }

        TextInputDialog pinDialog = new TextInputDialog();
        pinDialog.setTitle("PIN Verification");
        pinDialog.setHeaderText("Enter your 6-digit PIN to confirm the top-up.");
        pinDialog.setContentText("PIN:");

        Optional<String> result = pinDialog.showAndWait();
        if (result.isPresent()) {
            String enteredPin = result.get();
            if (customer.getPin().equals(enteredPin)) {
                double newBalance = customer.getBalance() + topUpAmount;
                customer.setBalance(newBalance);

                AlertHelper.showAlert(AlertType.INFORMATION, "Success", "Top-up successful!");

                balanceValueLabel.setText(String.format("Rp %,.2f", newBalance));
                topUpField.clear();

            } else {
                AlertHelper.showAlert(AlertType.ERROR, "Error", "Incorrect PIN. Top-up failed.");
            }
        }
    }

    @FXML
    void handleLogoutButtonAction() {
        authService.logout();
        navigationManager.navigateToLogin();
    }

    @FXML
    void handleBackButtonAction() {
        if ("Customer".equals(authService.getRole())) {
            navigationManager.navigateToListProduct();
        } else if ("Employee".equals(authService.getRole())) {
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
    }

    @FXML
    void handleHistoryButtonAction() {
        navigationManager.navigateToOrderHistory();
    }
}