package sajipay.views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sajipay.models.Order;

public class OrderSummaryPopup {

    public static void show(Order order, Runnable onConfirm) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Ringkasan Pesanan");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        // Ringkasan pesanan
        Label summaryLabel = new Label(order.getOrderSummary());
        summaryLabel.setWrapText(true);

        // Saldo customer
        double balance = order.getCustomer().getBalance();
        Label balanceLabel = new Label("Saldo Anda: Rp" + String.format("%.2f", balance));

        // Total harga
        double totalPrice = order.getTotalWithTax();
        Label totalLabel = new Label("Total Pembayaran: Rp" + String.format("%.2f", totalPrice));

        // Tombol konfirmasi
        Button confirmButton = new Button("Konfirmasi Pembayaran");
        confirmButton.setDisable(balance < totalPrice);

        // Aksi konfirmasi
        confirmButton.setOnAction(e -> {
            onConfirm.run();
            popupStage.close();
        });

        Button cancelButton = new Button("Batal");
        cancelButton.setOnAction(e -> popupStage.close());

        layout.getChildren().addAll(summaryLabel, balanceLabel, totalLabel, confirmButton, cancelButton);

        Scene scene = new Scene(layout, 400, 300);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
}
