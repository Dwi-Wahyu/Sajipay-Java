package sajipay.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import sajipay.models.Order;

public class OrderSummaryController {

    @FXML
    private Label summaryLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;

    private Runnable onConfirmAction;
    private Stage stage;

    public void initData(Order order, Runnable onConfirm) {
        this.onConfirmAction = onConfirm;

        // Isi data ke label
        summaryLabel.setText(order.getOrderSummary());

        double balance = order.getCustomer().getBalance();
        balanceLabel.setText(String.format("Saldo Anda: Rp %,.2f", balance));

        double totalPrice = order.getTotalWithTax();
        totalLabel.setText(String.format("Total Pembayaran: Rp %,.2f", totalPrice));

        // Nonaktifkan tombol konfirmasi jika saldo tidak cukup
        confirmButton.setDisable(balance < totalPrice);
    }

    @FXML
    void handleConfirm() {
        if (onConfirmAction != null) {
            onConfirmAction.run();
        }
        closeStage();
    }

    @FXML
    void handleCancel() {
        closeStage();
    }

    // Metode untuk mendapatkan stage dari luar
    private void closeStage() {
        stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}