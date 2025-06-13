package sajipay.models;

import sajipay.enums.BeverageSize;

public class Beverage extends Product {
    public boolean isIced;
    public BeverageSize size;
    public int sugarLevel;

    public Beverage() {

    }

    public Beverage(String id, String name, String description, double price, double cost, int stock) {
        super(id, name, description, price, cost, stock);
    }
}
