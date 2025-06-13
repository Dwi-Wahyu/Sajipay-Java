package sajipay.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import sajipay.enums.Payment;
import sajipay.enums.Role;
import sajipay.helper.AlertHelper;
import sajipay.helper.FormHelper;
import sajipay.models.Customer;
import sajipay.models.Employee;
import sajipay.models.Management;
import sajipay.services.NavigationManager;

import java.net.URL;
import java.util.ResourceBundle;

public class AddEmployeeController implements Initializable {

    // --- FXML Fields for Common Inputs ---
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private VBox dynamicFieldsContainer;
    @FXML
    private Button submitButton;

    // --- Dynamic Input Fields (instantiated in code) ---
    private TextField balanceField;
    private ComboBox<Payment> paymentCombo;
    private PasswordField pinField;
    private ComboBox<Role> roleCombo;
    private TextField experienceField;
    private TextField salaryField;

    private final Management management = Management.getInstance();
    private final NavigationManager navigationManager = NavigationManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeCombo.getItems().addAll("Customer", "Employee");
        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateUserForm(newVal));
    }

    private void updateUserForm(String userType) {
        dynamicFieldsContainer.getChildren().clear();
        if (userType == null) {
            submitButton.setDisable(true);
            return;
        }

        GridPane dynamicGrid = createDynamicGrid();

        if (userType.equals("Customer")) {
            balanceField = new TextField();
            paymentCombo = new ComboBox<>();
            paymentCombo.getItems().addAll(Payment.values());
            pinField = new PasswordField();

            dynamicGrid.add(new Label("Balance:"), 0, 0);
            dynamicGrid.add(balanceField, 1, 0);
            dynamicGrid.add(new Label("Payment Type:"), 0, 1);
            dynamicGrid.add(paymentCombo, 1, 1);
            dynamicGrid.add(new Label("PIN:"), 0, 2);
            dynamicGrid.add(pinField, 1, 2);

        } else if (userType.equals("Employee")) {
            roleCombo = new ComboBox<>();
            roleCombo.getItems().addAll(Role.values());
            experienceField = new TextField();
            salaryField = new TextField();

            dynamicGrid.add(new Label("Role:"), 0, 0);
            dynamicGrid.add(roleCombo, 1, 0);
            dynamicGrid.add(new Label("Years of Experience:"), 0, 1);
            dynamicGrid.add(experienceField, 1, 1);
            dynamicGrid.add(new Label("Salary:"), 0, 2);
            dynamicGrid.add(salaryField, 1, 2);
        }

        dynamicFieldsContainer.getChildren().add(dynamicGrid);
        submitButton.setDisable(false);
    }

    @FXML
    void handleSubmitButtonAction() {
        String userType = typeCombo.getValue();
        if (userType == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Please select a user type.");
            return;
        }

        try {
            if (userType.equals("Customer")) {
                addCustomer();
            } else if (userType.equals("Employee")) {
                addEmployee();
            }
        } catch (IllegalArgumentException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
        } catch (Exception e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error",
                    "An unexpected error occurred. Please check your inputs.");
            e.printStackTrace();
        }
    }

    private void addCustomer() {
        String username = getRequiredTextField(usernameField, "Username");
        String password = getRequiredPasswordField(passwordField, "Password");

        if (management.getUserByUsername(username) != null) {
            throw new IllegalArgumentException("Username '" + username + "' already exists.");
        }

        double balance = parseDoubleField(balanceField, 0.0);
        Payment payment = paymentCombo.getValue(); // Can be null
        String pin = getRequiredPasswordField(pinField, "PIN");

        if (!pin.matches("\\d{6}")) {
            throw new IllegalArgumentException("PIN must be exactly 6 digits.");
        }

        Customer customer = new Customer(username, password, balance, payment, pin);
        management.addUser(customer);

        AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success",
                "Customer '" + username + "' added successfully!");
        clearAllFields();
    }

    private void addEmployee() {
        String username = getRequiredTextField(usernameField, "Username");
        String password = getRequiredPasswordField(passwordField, "Password");

        if (management.getUserByUsername(username) != null) {
            throw new IllegalArgumentException("Username '" + username + "' already exists.");
        }

        Role role = roleCombo.getValue();
        if (role == null) {
            throw new IllegalArgumentException("Employee role must be selected.");
        }

        int experience = parseIntField(experienceField, 0);
        double salary = parseDoubleField(salaryField, 0.0);

        Employee employee = new Employee(username, password, role, experience, salary);
        management.addUser(employee);

        AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success",
                "Employee '" + username + "' added successfully!");
        clearAllFields();
    }

    @FXML
    void handleBackButtonAction() {
        navigationManager.navigateToManagerDashboard();
    }

    // --- Helper & Validation Methods ---

    private GridPane createDynamicGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        return grid;
    }

    private String getRequiredTextField(TextField field, String fieldName) {
        if (field.getText() == null || field.getText().trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return field.getText().trim();
    }

    private String getRequiredPasswordField(PasswordField field, String fieldName) {
        if (field.getText() == null || field.getText().trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return field.getText().trim();
    }

    private double parseDoubleField(TextField field, double defaultValue) {
        try {
            return field.getText().isEmpty() ? defaultValue : Double.parseDouble(field.getText());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format for field: " + field.getPromptText());
        }
    }

    private int parseIntField(TextField field, int defaultValue) {
        try {
            return field.getText().isEmpty() ? defaultValue : Integer.parseInt(field.getText());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format for field: " + field.getPromptText());
        }
    }

    private void clearAllFields() {
        FormHelper.clearFields(usernameField, passwordField);
        typeCombo.getSelectionModel().clearSelection();
        dynamicFieldsContainer.getChildren().clear();
        submitButton.setDisable(true);
    }
}
