package sajipay.enums;

public enum Payment {
    CASH(0.10),
    CREDIT_CARD(0.12),
    DEBIT_CARD(0.10),
    BANK_TRANSFER(0.09),
    E_WALLET(0.07),
    QRIS(0.08);

    private final double taxRate;

    Payment(double taxRate) {
        this.taxRate = taxRate;
    }

    public double getTaxRate() {
        return taxRate;
    }
}
