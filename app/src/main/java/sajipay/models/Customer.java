package sajipay.models;

import sajipay.enums.Payment;
import sajipay.services.DatabaseService;

public class Customer extends User {
    private double balance;
    private Payment payment;
    private String pin;
    private DatabaseService databaseService = DatabaseService.getInstance();

    public Customer(String name, String password) {
        super(name, password);
    }

    public Customer(String name, String password, double balance, Payment payment, String pin) {
        this(name, password);
        this.balance = balance;
        this.payment = payment;
        this.pin = pin;
    }

    public double getPaymentTax() {
        if (payment == null)
            return 0.10;
        return payment.getTaxRate();
    }

    @Override
    public void setPassword(String password) {
        super.setPassword(password);
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    public void setBalance(double newBalance) {
        boolean isSuccess = databaseService.updateCustomerBalance(this.username, newBalance);

        if (isSuccess) {
            this.balance = newBalance;
            System.out.println("Berhasil memperbarui saldo untuk " + this.username);
        } else {
            System.err.println(
                    "Gagal memperbarui saldo di database untuk " + this.username + ". Saldo objek tidak diubah.");
        }
    }

    public double getBalance() {
        return balance;
    }

    public void setPaymentType(Payment payment) {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }

}
