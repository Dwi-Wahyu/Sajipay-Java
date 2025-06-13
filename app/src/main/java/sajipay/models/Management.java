package sajipay.models;

import sajipay.enums.Role;
import sajipay.helper.EmployeesCheck;
import sajipay.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class Management {
    private List<User> users;
    private List<Product> products;
    private double grossProfit, netIncome;
    private List<Order> transactions;

    private static Management instance;
    private DatabaseService databaseService;

    public static Management getInstance() {
        if (instance == null) {
            synchronized (Management.class) {
                if (instance == null) {
                    instance = new Management();
                }
            }
        }
        return instance;
    }

    public Management() {
        this.databaseService = DatabaseService.getInstance();

        this.users = databaseService.loadAllUsers();
        this.products = databaseService.loadAllProducts();
        this.transactions = databaseService.loadAllOrders(getCustomers());
    }

    public List<Customer> getCustomers() {
        List<Customer> customers = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Customer) {
                customers.add((Customer) user);
            }
        }
        return customers;
    }

    public List<Employee> getEmployees() {
        List<Employee> employees = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Employee) {
                employees.add((Employee) user);
            }
        }
        return employees;
    }

    public User getUserByUsername(String username) {
        return users.stream()
                .filter(user -> user.username.equals(username))
                .findFirst()
                .orElse(null);
    }

    public void addProducts(Product product, User user) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        EmployeesCheck.CheckEmployeesRole(user, Role.CHEF, "Only CHEF can add products.");
        products.add(product);
        databaseService.saveProduct(product); // Simpan ke database
    }

    public Product findProductByName(String name) {
        for (Product product : products) {
            if (product.name.equalsIgnoreCase(name)) {
                return product;
            }
        }
        return null;
    }

    public List<Product> getProducts() {
        return products;
    }

    public double getNetIncome() {
        return netIncome;
    }

    public double getGrossProfit() {
        return grossProfit;
    }

    public void addTransaction(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null.");
        }
        transactions.add(order);
        databaseService.saveOrder(order); // Simpan transaksi ke database
    }

    public void addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        users.add(user);
        databaseService.saveUser(user); // Simpan ke database
    }

    public void deleteProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        // Hapus dari daftar di memori
        products.remove(product);
        // Hapus dari database melalui service
        databaseService.deleteProduct(product.id);
    }

    public void deleteUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        // Hapus dari daftar di memori
        users.remove(user);
        // Hapus dari database melalui service
        databaseService.deleteUser(user.username);
    }

    public void updateEmployee(Employee employee) {
        databaseService.updateEmployee(employee);
    }

    // Getter untuk transactions (order history)
    public List<Order> getTransactions() {
        return transactions;
    }
}