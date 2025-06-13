package sajipay.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sajipay.enums.Role;
import sajipay.helper.AlertHelper;
import sajipay.models.Employee;
import sajipay.models.Management;
import sajipay.services.NavigationManager;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateEmployeeController implements Initializable {

    @FXML
    private Label usernameLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<Role> roleCombo;
    @FXML
    private TextField experienceField;
    @FXML
    private TextField salaryField;

    private Employee employeeToUpdate;
    private final Management management = Management.getInstance();
    private final NavigationManager navigationManager = NavigationManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        roleCombo.getItems().addAll(Role.values());
    }

    /**
     * Menginisialisasi form dengan data dari karyawan yang dipilih.
     * Metode ini harus dipanggil setelah memuat FXML.
     */
    public void initData(Employee employee) {
        this.employeeToUpdate = employee;
        usernameLabel.setText(employee.username);
        roleCombo.setValue(employee.role);
        experienceField.setText(String.valueOf(employee.getYearsOfExperience()));
        salaryField.setText(String.format("%.0f", employee.getSalary()));
    }

    @FXML
    void handleUpdateButtonAction() {
        if (employeeToUpdate == null) {
            AlertHelper.showAlert(AlertType.ERROR, "Error", "No employee selected for update.");
            return;
        }

        try {
            // Validasi input
            int experience = Integer.parseInt(experienceField.getText());
            double salary = Double.parseDouble(salaryField.getText());
            Role role = roleCombo.getValue();

            if (role == null) {
                AlertHelper.showAlert(AlertType.ERROR, "Validation Error", "Role must be selected.");
                return;
            }

            // Update atribut karyawan
            employeeToUpdate.setYearsOfExperience(experience);
            employeeToUpdate.setSalary(salary);
            employeeToUpdate.role = role;

            // Update password hanya jika diisi
            String newPassword = passwordField.getText();
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                employeeToUpdate.setPassword(newPassword);
            }

            // Simpan perubahan ke database (melalui Management service)
            management.updateEmployee(employeeToUpdate);

            AlertHelper.showAlert(AlertType.INFORMATION, "Success", "Employee data updated successfully!");
            navigationManager.navigateToManagerDashboard();

        } catch (NumberFormatException e) {
            AlertHelper.showAlert(AlertType.ERROR, "Invalid Input",
                    "Please enter valid numbers for experience and salary.");
        } catch (Exception e) {
            AlertHelper.showAlert(AlertType.ERROR, "Update Failed", "An error occurred: " + e.getMessage());
        }
    }

    @FXML
    void handleBackButtonAction() {
        navigationManager.navigateToManagerDashboard();
    }
}