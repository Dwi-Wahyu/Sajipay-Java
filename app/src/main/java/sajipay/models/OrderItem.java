package sajipay.models;

public class OrderItem {
    private String id;
    private String name;
    private int quantity;
    private double price;

    public OrderItem(String id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    // --- Calculation Methods ---
    public double getTotalPrice() {
        return quantity * price;
    }

    // Tambahkan metode ini untuk menghitung jumlah pajak untuk item ini
    public double getTaxAmount(double taxRate) {
        return getTotalPrice() * taxRate;
    }

    // Tambahkan metode ini untuk menghitung total harga item ini beserta pajaknya
    public double getTotalPriceWithTax(double taxRate) {
        return getTotalPrice() + getTaxAmount(taxRate);
    }

    @Override
    public String toString() {
        return quantity + "x " + name + " - Rp" + String.format("%.2f", getTotalPrice());
    }
}