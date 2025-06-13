package sajipay.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import sajipay.enums.Role;
import sajipay.helper.AlertHelper;
import sajipay.models.Employee;
import sajipay.models.Management;
import sajipay.models.User;
import sajipay.services.AuthService;
import sajipay.services.NavigationManager;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManagerDashboardController implements Initializable {

    @FXML
    private Label welcomeLabel;
    @FXML
    private ListView<String> employeeListView;

    private final AuthService authService = AuthService.getInstance();
    private final Management management = Management.getInstance();
    private final NavigationManager navigationManager = NavigationManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Employee currentUser = (Employee) authService.getCurrentUser();

        if (currentUser == null || currentUser.role != Role.MANAGER) {
            AlertHelper.showAlert(AlertType.ERROR, "Access Denied", "This page is for managers only.");
            navigationManager.navigateToLogin();
            return;
        }

        welcomeLabel.setText("Welcome, " + currentUser.username);
        populateEmployeeList();
    }

    private void populateEmployeeList() {
        employeeListView.getItems().clear();
        for (User user : management.getEmployees()) {
            if (user instanceof Employee) {
                Employee emp = (Employee) user;
                String info = String.format(
                        "%s - Role: %s\nExperience: %d years | Salary: Rp%,.2f",
                        emp.username,
                        emp.role.name(),
                        emp.yearsOfExperience,
                        emp.getSalary());
                employeeListView.getItems().add(info);
            }
        }
    }

    @FXML
    void handleAddEmployeeButton() {
        navigationManager.navigateToAddEmployee();
    }

    /**
     * Menangani aksi penghapusan karyawan yang dipilih dari daftar.
     */
    @FXML
    void handleDeleteEmployeeButton() {
        String selectedItem = employeeListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            AlertHelper.showAlert(AlertType.WARNING, "No Selection", "Please select an employee to delete.");
            return;
        }

        // Tampilkan dialog konfirmasi
        Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Are you sure you want to delete this employee?");
        confirmationAlert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Ekstrak username dari item yang dipilih
            String usernameToDelete = selectedItem.split(" - ")[0];
            User userToDelete = management.getUserByUsername(usernameToDelete);

            if (userToDelete != null) {
                // Hapus pengguna dari sistem
                management.deleteUser(userToDelete);
                AlertHelper.showAlert(AlertType.INFORMATION, "Success",
                        "Employee '" + usernameToDelete + "' has been deleted.");
                // Muat ulang daftar karyawan
                populateEmployeeList();
            } else {
                AlertHelper.showAlert(AlertType.ERROR, "Error", "Could not find the selected employee to delete.");
            }
        }
    }

    @FXML
    void handleProfileButton() {
        navigationManager.navigateToProfile();
    }

    @FXML
    void handleLogoutButton() {
        authService.logout();
        navigationManager.navigateToLogin();
    }

    @FXML
    void handleUpdateEmployeeButton() {
        String selectedItem = employeeListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            AlertHelper.showAlert(AlertType.WARNING, "No Selection", "Please select an employee to update.");
            return;
        }

        String usernameToUpdate = selectedItem.split(" - ")[0];
        User user = management.getUserByUsername(usernameToUpdate);

        if (user instanceof Employee) {
            navigationManager.navigateToUpdateEmployee((Employee) user);
        } else {
            AlertHelper.showAlert(AlertType.ERROR, "Error", "Selected user is not an employee.");
        }
    }
}