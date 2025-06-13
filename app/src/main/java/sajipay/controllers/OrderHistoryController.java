package sajipay.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import sajipay.helper.AlertHelper;
import sajipay.models.Customer;
import sajipay.models.Management;
import sajipay.models.Order;
import sajipay.services.AuthService;
import sajipay.services.NavigationManager;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class OrderHistoryController implements Initializable {

    @FXML
    private ListView<String> historyListView;

    @FXML
    private TextArea orderDetailsArea;

    @FXML
    private Button backButton;

    private Management management;
    private AuthService authService;
    private NavigationManager navigationManager;

    private List<Order> customerOrders;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inisialisasi service
        management = Management.getInstance();
        authService = AuthService.getInstance();
        navigationManager = NavigationManager.getInstance();

        // Pastikan user adalah customer
        if (!(authService.getCurrentUser() instanceof Customer)) {
            AlertHelper.showAlert(AlertType.ERROR, "Access Denied", "This page is for customers only.");
            navigationManager.navigateToLogin();
            return;
        }

        Customer currentCustomer = (Customer) authService.getCurrentUser();
        loadOrderHistory(currentCustomer);
        setupListViewListener();
    }

    private void loadOrderHistory(Customer customer) {
        // Filter transaksi hanya untuk customer yang sedang login
        customerOrders = management.getTransactions().stream()
                .filter(order -> order.getCustomer().username.equals(customer.username))
                .collect(Collectors.toList());

        if (customerOrders.isEmpty()) {
            historyListView.getItems().add("No order history found.");
            historyListView.setDisable(true);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Order order : customerOrders) {
                String displayText = String.format("Order on %s - Total: Rp %,.2f",
                        order.getDate().format(formatter),
                        order.getTotalWithTax());
                historyListView.getItems().add(displayText);
            }
        }
    }

    private void setupListViewListener() {
        historyListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() >= 0 && newValue.intValue() < customerOrders.size()) {
                // Ambil order yang dipilih berdasarkan indeks
                Order selectedOrder = customerOrders.get(newValue.intValue());
                // Tampilkan ringkasan order di TextArea
                orderDetailsArea.setText(selectedOrder.getOrderSummary());
            }
        });
    }

    @FXML
    void handleBackButtonAction() {
        navigationManager.navigateToProfile();
    }
}