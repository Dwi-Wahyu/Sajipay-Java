package sajipay.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import sajipay.enums.Role;
import sajipay.helper.AlertHelper;
import sajipay.models.Employee;
import sajipay.models.Management;
import sajipay.models.Product;
import sajipay.services.AuthService;
import sajipay.services.NavigationManager;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ChefDashboardController implements Initializable {

    @FXML
    private Label welcomeLabel;
    @FXML
    private ListView<String> productListView;
    // --- Tambahkan FXML field untuk tombol hapus ---
    @FXML
    private Button deleteProductButton;

    private final AuthService authService = AuthService.getInstance();
    private final Management management = Management.getInstance();
    private final NavigationManager navigationManager = NavigationManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Employee currentUser = (Employee) authService.getCurrentUser();

        if (currentUser == null || currentUser.role != Role.CHEF) {
            AlertHelper.showAlert(AlertType.ERROR, "Access Denied", "This page is for chefs only.");
            navigationManager.navigateToLogin();
            return;
        }

        welcomeLabel.setText("Welcome, " + currentUser.username);
        populateProductList();
    }

    private void populateProductList() {
        productListView.getItems().clear();
        for (Product product : management.getProducts()) {
            String details = String.format(
                    "%s - Stock: %d\nPrice: Rp%,.2f | Cost: Rp%,.2f",
                    product.name,
                    product.getStock(),
                    product.price,
                    product.getCost());
            productListView.getItems().add(details);
        }
    }

    // --- TAMBAHKAN METODE HANDLER BARU INI ---
    @FXML
    void handleDeleteProductButton() {
        String selectedItem = productListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            AlertHelper.showAlert(AlertType.WARNING, "No Selection", "Please select a product to delete.");
            return;
        }

        // Tampilkan dialog konfirmasi
        Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Are you sure you want to delete this product?");
        confirmationAlert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Jika user menekan OK, lanjutkan penghapusan
            String productName = selectedItem.split(" - ")[0];
            Product productToDelete = management.findProductByName(productName);

            if (productToDelete != null) {
                management.deleteProduct(productToDelete);
                AlertHelper.showAlert(AlertType.INFORMATION, "Success",
                        "Product '" + productName + "' has been deleted.");
                // Refresh daftar produk di UI
                populateProductList();
            } else {
                AlertHelper.showAlert(AlertType.ERROR, "Error", "Could not find the selected product to delete.");
            }
        }
    }

    @FXML
    void handleAddProductButton() {
        navigationManager.navigateToAddProduct();
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
}