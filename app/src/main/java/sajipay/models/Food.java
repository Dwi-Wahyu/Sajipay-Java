package sajipay.models;

public class Food extends Product {
    public int calories;
    public boolean isVegetarian;

    public Food() {

    }

    public Food(String id, String name, String description, double price, double cost, int stock) {
        super(id, name, description, price, cost, stock);
    }

}
