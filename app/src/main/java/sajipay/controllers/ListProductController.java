package sajipay.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sajipay.components.AvatarComponent;
import sajipay.helper.AlertHelper;
import sajipay.models.*;
import sajipay.services.AuthService;
import sajipay.services.NavigationManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ListProductController implements Initializable {

    // --- FXML Components ---
    @FXML
    private VBox mainLayout;
    @FXML
    private ListView<String> productListView;
    @FXML
    private Spinner<Integer> quantitySpinner;
    @FXML
    private Label feedbackLabel;
    @FXML
    private ListView<String> cartListView;

    // --- Services and Models ---
    private AuthService authService;
    private Management management;
    private Customer customer;
    private Order currentOrder;
    private NavigationManager navigationManager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize services
        authService = AuthService.getInstance();
        management = Management.getInstance();
        navigationManager = NavigationManager.getInstance();

        // Get logged-in user, redirect if not a customer
        if (!(authService.getCurrentUser() instanceof Customer)) {
            AlertHelper.showAlert(AlertType.ERROR, "Access Denied", "This page is for customers only.");
            navigationManager.navigateToLogin();
            return;
        }

        customer = (Customer) authService.getCurrentUser();
        currentOrder = new Order(customer);

        // Setup UI components
        setupInitialUI();
    }

    private void setupInitialUI() {
        // Add custom component programmatically
        AvatarComponent avatar = new AvatarComponent(customer.username, authService.getRole());
        mainLayout.getChildren().add(0, avatar);

        // Configure Spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        quantitySpinner.setValueFactory(valueFactory);

        // Populate product list
        populateProductList();
    }

    private void populateProductList() {
        productListView.getItems().clear();
        for (Product product : management.getProducts()) {
            String productDisplay;
            if (product instanceof Food food) {
                String vegStatus = food.isVegetarian ? ", Vegetarian" : "";
                productDisplay = String.format("%s - Rp%,.2f\n%s, %d cal%s",
                        food.name, food.price, food.description, food.calories, vegStatus);
            } else if (product instanceof Beverage beverage) {
                String icedStatus = beverage.isIced ? ", Iced" : "";
                productDisplay = String.format("%s - Rp%,.2f\n%s, %s, %d%% sugar%s",
                        beverage.name, beverage.price, beverage.description, beverage.size, beverage.sugarLevel,
                        icedStatus);
            } else {
                continue;
            }
            productListView.getItems().add(productDisplay);
        }
    }

    @FXML
    void handleAddToCart() {
        String selected = productListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            feedbackLabel.setText("Pilih produk terlebih dahulu.");
            return;
        }

        int qty = quantitySpinner.getValue();
        String productName = selected.split(" - ")[0];
        Product product = management.findProductByName(productName);

        if (product != null) {
            OrderItem item = new OrderItem(product.id, product.name, qty, product.price);
            currentOrder.addItem(item);
            cartListView.getItems().add(item.getQuantity() + "x " + item.getName());
            feedbackLabel.setText("Berhasil ditambahkan ke keranjang.");
        }
    }

    @FXML
    void handleRemoveItem() {
        int selectedIndex = cartListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            currentOrder.getOrderItems().remove(selectedIndex);
            cartListView.getItems().remove(selectedIndex);
            feedbackLabel.setText("Item dihapus dari keranjang.");
        }
    }

    @FXML
    void handleCheckout() {
        if (currentOrder.getOrderItems().isEmpty()) {
            AlertHelper.showAlert(AlertType.WARNING, "Peringatan", "Keranjang Anda Kosong");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/OrderSummaryPopup.fxml"));
            Parent root = loader.load();

            OrderSummaryController controller = loader.getController();
            controller.initData(currentOrder, () -> {
                processPayment();
            });

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Ringkasan Pesanan");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showAlert(AlertType.ERROR, "Error", "Gagal membuka ringkasan pesanan.");
        }
    }

    private void processPayment() {
        double total = currentOrder.getTotalWithTax();
        customer.setBalance(customer.getBalance() - total);
        management.addTransaction(currentOrder);

        AlertHelper.showAlert(AlertType.CONFIRMATION, "Berhasil!", "Pembayaran berhasil diproses.");

        for (OrderItem orderItem : currentOrder.getOrderItems()) {
            for (Product product : management.getProducts()) {
                if (product.getId().equals(orderItem.getId())) {
                    product.reduceStock(orderItem.getQuantity());
                }
            }
        }

        // Reset cart
        currentOrder.clearItems();
        cartListView.getItems().clear();
        feedbackLabel.setText("Pembayaran berhasil!");
    }
}
