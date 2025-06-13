package sajipay.interfaces;

public interface IProduct {
    public void reduceStock(int stock);

    public void addStock(int stock);

    public int getStock();
}
