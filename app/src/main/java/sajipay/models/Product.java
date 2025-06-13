package sajipay.models;

import sajipay.helper.ProductIdGenerator;
import sajipay.interfaces.IProduct;
import sajipay.services.DatabaseService;

public abstract class Product implements IProduct {
    public String name, description, id;
    public double price;
    private double cost;
    private int stock;
    private DatabaseService databaseService = DatabaseService.getInstance();

    public Product() {
        this.id = ProductIdGenerator.generateId("food");
    }

    public Product(String id, String name, String description, double price, double cost, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.cost = cost;
        this.stock = stock;
    }

    @Override
    public void reduceStock(int amountToReduce) {
        int newStock = this.stock - amountToReduce;
        if (newStock < 0) {
            throw new IllegalArgumentException("Stok tidak bisa kurang dari nol.");
        }

        databaseService.updateProductStock(this.id, newStock);

        this.stock = newStock;
    }

    @Override
    public void addStock(int amountToAdd) {
        int newStock = this.stock + amountToAdd;

        databaseService.updateProductStock(this.id, newStock);

        this.stock = newStock;
    }

    public void setStock(int totalStock) {
        if (totalStock < 0) {
            throw new IllegalArgumentException("Stok tidak bisa negatif.");
        }

        this.stock = totalStock;
    }

    @Override
    public int getStock() {
        return stock;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPriceWithTax(double taxRate) {
        return price + (price * taxRate);
    }
}
