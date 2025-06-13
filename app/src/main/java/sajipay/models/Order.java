package sajipay.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private String orderId;
    private Customer customer;
    private List<OrderItem> orderItems = new ArrayList<>();
    private LocalDateTime date;
    private double totalPrice = 0;

    public Order(Customer customer) {
        this.orderId = UUID.randomUUID().toString();
        this.customer = customer;
        this.date = LocalDateTime.now();
    }

    public Order(String orderId, Customer customer, LocalDateTime date) {
        this.orderId = orderId;
        this.customer = customer;
        this.date = date;
    }

    public void addItem(OrderItem item) {
        orderItems.add(item);
        totalPrice += item.getTotalPrice();
    }

    public void clearItems() {
        orderItems.clear();
        totalPrice = 0;
    }

    public double getTotalPriceBeforeTax() {
        return totalPrice;
    }

    public double getTaxAmount() {
        return totalPrice * customer.getPaymentTax();
    }

    public double getTotalWithTax() {
        return totalPrice + getTaxAmount();
    }

    public double getTaxAmountPerItem(OrderItem item) {
        return item.getTotalPrice() * customer.getPaymentTax();
    }

    public double getTotalWithTaxPerItem(OrderItem item) {
        return item.getTotalPrice() + getTaxAmountPerItem(item);
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public String getOrderSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(orderId).append("\n"); // Tampilkan Order ID
        sb.append("Customer: ").append(customer.username).append("\n");
        sb.append("Payment Type: ").append(customer.getPayment()).append("\n");
        sb.append("Order Date: ").append(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append("\n\n");
        sb.append("Products:\n");

        for (OrderItem item : orderItems) {
            sb.append("- ").append(item.getName()).append(" x").append(item.getQuantity())
                    .append(" = Rp").append(String.format("%.2f", item.getTotalPrice())).append("\n");
        }

        sb.append("\nTotal: Rp").append(String.format("%.2f", getTotalPriceBeforeTax()));
        sb.append("\nTax (").append((int) (customer.getPaymentTax() * 100)).append("%): Rp")
                .append(String.format("%.2f", getTaxAmount()));
        sb.append("\nTotal with Tax: Rp").append(String.format("%.2f", getTotalWithTax()));

        return sb.toString();
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getOrderId() { // Tambahkan getter untuk orderId
        return orderId;
    }
}