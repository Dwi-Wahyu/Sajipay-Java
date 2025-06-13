package sajipay.services;

import sajipay.enums.BeverageSize;
import sajipay.enums.Payment;
import sajipay.enums.Role;
import sajipay.models.*;
import sajipay.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DatabaseService {
    private static DatabaseService instance;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Connection connection;

    private DatabaseService() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public static DatabaseService getInstance() {
        if (instance == null) {
            synchronized (DatabaseService.class) {
                if (instance == null) {
                    instance = new DatabaseService();
                }
            }
        }
        return instance;
    }

    public void saveUser(User user) {
        if (user instanceof Employee) {
            saveEmployee((Employee) user);
        } else if (user instanceof Customer) {
            saveCustomer((Customer) user);
        }
    }

    private void saveEmployee(Employee emp) {
        String sql = "INSERT OR REPLACE INTO employees (type, name, password, role, yearsOfExperience, salary) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "Employee");
            pstmt.setString(2, emp.username);
            pstmt.setString(3, emp.getPassword());
            pstmt.setString(4, emp.role.name());
            pstmt.setInt(5, emp.yearsOfExperience);
            pstmt.setDouble(6, emp.salary);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Gagal menyimpan employee: " + e.getMessage());
        }
    }

    private void saveCustomer(Customer cust) {
        String sql = "INSERT OR REPLACE INTO customers (type, name, password, balance, payment, pin) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "Customer");
            pstmt.setString(2, cust.username);
            pstmt.setString(3, cust.getPassword());
            pstmt.setDouble(4, cust.getBalance());
            pstmt.setString(5, cust.getPayment() != null ? cust.getPayment().name() : "QRIS");
            pstmt.setString(6, cust.getPin());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Gagal menyimpan customer: " + e.getMessage());
        }
    }

    public List<User> loadAllUsers() {
        List<User> users = new ArrayList<>();

        String sqlEmployees = "SELECT * FROM employees";
        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sqlEmployees)) {
            while (rs.next()) {
                users.add(new Employee(
                        rs.getString("name"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role")),
                        rs.getInt("yearsOfExperience"),
                        rs.getDouble("salary")));
            }
        } catch (SQLException e) {
            System.err.println("Gagal memuat employees: " + e.getMessage());
        }

        String sqlCustomers = "SELECT * FROM customers";
        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sqlCustomers)) {
            while (rs.next()) {
                users.add(new Customer(
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getDouble("balance"),
                        rs.getString("payment") != null ? Payment.valueOf(rs.getString("payment")) : null,
                        rs.getString("pin")));
            }
        } catch (SQLException e) {
            System.err.println("Gagal memuat customers: " + e.getMessage());
        }

        return users;
    }

    public void saveProduct(Product product) {
        String sql = "INSERT OR REPLACE INTO products (type, id, name, description, price, cost, stock, calories, isVegetarian, isIced, size, sugarLevel) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(2, product.id);
            pstmt.setString(3, product.name);
            pstmt.setString(4, product.description);
            pstmt.setDouble(5, product.price);
            pstmt.setDouble(6, product.getCost());
            pstmt.setInt(7, product.getStock());

            if (product instanceof Food food) {
                pstmt.setString(1, "Food");
                pstmt.setInt(8, food.calories);
                pstmt.setBoolean(9, food.isVegetarian);
                pstmt.setNull(10, Types.BOOLEAN);
                pstmt.setNull(11, Types.VARCHAR);
                pstmt.setNull(12, Types.VARCHAR);
            } else if (product instanceof Beverage bev) {
                pstmt.setString(1, "Beverage");
                pstmt.setNull(8, Types.INTEGER);
                pstmt.setNull(9, Types.BOOLEAN);
                pstmt.setBoolean(10, bev.isIced);
                pstmt.setString(11, bev.size != null ? bev.size.name() : null);
                pstmt.setString(12, String.valueOf(bev.sugarLevel));
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Gagal menyimpan product: " + e.getMessage());
        }
    }

    public List<Product> loadAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String type = rs.getString("type");
                if ("Food".equals(type)) {
                    Food food = new Food();
                    food.setId(rs.getString("id"));
                    food.name = rs.getString("name");
                    food.description = rs.getString("description");
                    food.price = rs.getDouble("price");
                    food.setCost(rs.getDouble("cost"));
                    food.setStock(rs.getInt("stock"));
                    food.calories = rs.getInt("calories");
                    food.isVegetarian = rs.getBoolean("isVegetarian");
                    products.add(food);
                } else if ("Beverage".equals(type)) {
                    Beverage bev = new Beverage();
                    bev.setId(rs.getString("id"));
                    bev.name = rs.getString("name");
                    bev.description = rs.getString("description");
                    bev.price = rs.getDouble("price");
                    bev.setCost(rs.getDouble("cost"));
                    bev.setStock(rs.getInt("stock"));
                    bev.isIced = rs.getBoolean("isIced");
                    String size = rs.getString("size");
                    if (size != null && !size.isEmpty()) {
                        bev.size = BeverageSize.valueOf(size);
                    }
                    String sugar = rs.getString("sugarLevel");
                    if (sugar != null && !sugar.isEmpty() && !sugar.equalsIgnoreCase("null")) {
                        bev.sugarLevel = Integer.parseInt(sugar);
                    }
                    products.add(bev);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal memuat products: " + e.getMessage());
        }
        return products;
    }

    public void saveOrder(Order order) {
        String sql = "INSERT INTO order_history (OrderId, Username, OrderDate, ProductID, ProductName, Quantity, Price, TotalPrice, TaxRate, TotalWithTax) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String orderId = order.getOrderId();
            String username = order.getCustomer().username;
            String date = order.getDate().format(DATE_TIME_FORMATTER);
            double taxRate = order.getCustomer().getPaymentTax();
            String taxRateStr = String.format("%.0f%%", taxRate * 100);

            for (OrderItem item : order.getOrderItems()) {
                pstmt.setString(1, orderId);
                pstmt.setString(2, username);
                pstmt.setString(3, date);
                pstmt.setString(4, item.getId());
                pstmt.setString(5, item.getName());
                pstmt.setInt(6, item.getQuantity());
                pstmt.setDouble(7, item.getPrice());
                pstmt.setDouble(8, item.getTotalPrice());
                pstmt.setString(9, taxRateStr);
                pstmt.setDouble(10, item.getTotalPriceWithTax(taxRate));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Gagal menyimpan order: " + e.getMessage());
        }
    }

    public List<Order> loadAllOrders(List<Customer> allCustomers) {
        Map<String, Order> ordersMap = new LinkedHashMap<>();
        String sql = "SELECT * FROM order_history";

        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String orderId = rs.getString("OrderId");
                String username = rs.getString("Username");

                Order current = ordersMap.get(orderId);
                if (current == null) {
                    Customer customer = findCustomerByUsername(username, allCustomers);
                    if (customer == null) {
                        System.err.println("Customer tidak ditemukan untuk OrderId " + orderId);
                        continue;
                    }
                    current = new Order(orderId, customer,
                            LocalDateTime.parse(rs.getString("OrderDate"), DATE_TIME_FORMATTER));
                    ordersMap.put(orderId, current);
                }

                OrderItem item = new OrderItem(
                        rs.getString("ProductID"),
                        rs.getString("ProductName"),
                        rs.getInt("Quantity"),
                        rs.getDouble("Price"));
                current.addItem(item);
            }
        } catch (SQLException e) {
            System.err.println("Gagal memuat order history: " + e.getMessage());
        }

        return new ArrayList<>(ordersMap.values());
    }

    private Customer findCustomerByUsername(String username, List<Customer> customers) {
        return customers.stream().filter(c -> c.username.equals(username)).findFirst().orElse(null);
    }

    public boolean updateCustomerBalance(String username, double newBalance) {
        String sql = "UPDATE customers SET balance = ? WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal memperbarui saldo: " + e.getMessage());
            return false;
        }
    }

    public void updateProductStock(String productId, int newStock) {
        String sql = "UPDATE products SET stock = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, newStock);
            pstmt.setString(2, productId);
            System.out.println("Berhasil memperbarui stock");
        } catch (Exception e) {
            System.err.println("Gagal memperbarui stok: " + e.getMessage());
        }
    }

    public void deleteProduct(String productId) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            pstmt.executeUpdate();
            System.out.println("Produk dengan ID " + productId + " berhasil dihapus dari database.");
        } catch (SQLException e) {
            System.err.println("Gagal menghapus produk: " + e.getMessage());
        }
    }

    public void deleteUser(String username) {
        String sqlEmployee = "DELETE FROM employees WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlEmployee)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete employee: " + e.getMessage());
        }

        String sqlCustomer = "DELETE FROM customers WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlCustomer)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete customer: " + e.getMessage());
        }
        System.out.println("User " + username + " has been removed from the database.");
    }

    public void updateEmployee(Employee emp) {
        String sql = "UPDATE employees SET password = ?, role = ?, yearsOfExperience = ?, salary = ? WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emp.getPassword());
            pstmt.setString(2, emp.role.name());
            pstmt.setInt(3, emp.getYearsOfExperience());
            pstmt.setDouble(4, emp.getSalary());
            pstmt.setString(5, emp.username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to update employee: " + e.getMessage());
        }
    }
}
