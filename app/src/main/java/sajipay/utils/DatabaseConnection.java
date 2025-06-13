package sajipay.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:src/main/resources/database.db";
    private static final String EMPLOYEES_FILE = "src/main/resources/data/employees.csv";
    private static final String CUSTOMERS_FILE = "src/main/resources/data/customers.csv";
    private static final String PRODUCTS_FILE = "src/main/resources/data/products.csv";

    private static volatile DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(URL);
            System.out.println("Koneksi ke SQLite berhasil dibuat.");
            if (isNewDatabase()) {
                System.out.println("Database baru terdeteksi, memulai inisialisasi tabel dan data...");
                initializeDatabase();
            }
        } catch (SQLException e) {
            System.err.println("Gagal membuat koneksi ke SQLite: " + e.getMessage());
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Koneksi ke SQLite berhasil ditutup.");
            } catch (SQLException e) {
                System.err.println("Gagal menutup koneksi: " + e.getMessage());
            }
        }
    }

    private boolean isNewDatabase() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='customers'");
            return !rs.next();
        } catch (SQLException e) {
            return true;
        }
    }

    private void initializeDatabase() {
        createTables();
        importCsvData(CUSTOMERS_FILE, "customers", 6);
        importCsvData(EMPLOYEES_FILE, "employees", 6);
        importProductsFromCsv(PRODUCTS_FILE);
        System.out.println("Inisialisasi database selesai.");
    }

    private void createTables() {
        String createCustomersTable = "CREATE TABLE IF NOT EXISTS customers (" +
                " type TEXT, name TEXT PRIMARY KEY, password TEXT, balance REAL, " +
                " payment TEXT, pin TEXT);";

        String createEmployeesTable = "CREATE TABLE IF NOT EXISTS employees (" +
                " type TEXT, name TEXT PRIMARY KEY, password TEXT, role TEXT, " +
                " yearsOfExperience INTEGER, salary REAL);";

        String createProductsTable = "CREATE TABLE IF NOT EXISTS products (" +
                " type TEXT, id TEXT PRIMARY KEY, name TEXT, description TEXT, " +
                " price REAL, cost REAL, stock INTEGER, calories INTEGER, " +
                " isVegetarian BOOLEAN, isIced BOOLEAN, size TEXT, sugarLevel TEXT);";

        String createOrderHistoryTable = "CREATE TABLE IF NOT EXISTS order_history (" +
                " OrderId TEXT, Username TEXT, OrderDate TEXT, " +
                " ProductID TEXT, ProductName TEXT, Quantity INTEGER, Price REAL, " +
                " TotalPrice REAL, TaxRate TEXT, TotalWithTax REAL, " +
                " PRIMARY KEY (OrderId, ProductID));";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createCustomersTable);
            stmt.execute(createEmployeesTable);
            stmt.execute(createProductsTable);
            stmt.execute(createOrderHistoryTable);
            System.out.println("Semua tabel berhasil dibuat.");
        } catch (SQLException e) {
            System.err.println("Gagal membuat tabel: " + e.getMessage());
        }
    }

    private void importCsvData(String csvFilePath, String tableName, int columnCount) {
        String sql = buildInsertSql(tableName, columnCount);
        if (sql == null)
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    for (int i = 0; i < columnCount; i++) {
                        if (i < data.length && !data[i].isEmpty()) {
                            pstmt.setString(i + 1, data[i]);
                        } else {
                            pstmt.setNull(i + 1, Types.VARCHAR);
                        }
                    }
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    System.err.println(
                            "Gagal memasukkan data ke tabel " + tableName + ": " + e.getMessage() + " | Data: " + line);
                }
            }
            System.out.println("Data dari " + csvFilePath + " berhasil diimpor ke tabel " + tableName + ".");
        } catch (IOException e) {
            System.err.println("Gagal membaca file CSV " + csvFilePath + ": " + e.getMessage());
        }
    }

    private void importProductsFromCsv(String csvFilePath) {
        String sql = buildInsertSql("products", 12);

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length < 12) {
                    System.err.println("Baris data produk tidak valid (kurang dari 12 kolom): " + line);
                    continue;
                }

                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    for (int i = 0; i < 12; i++) {
                        if (!Objects.equals(data[i], "")) {
                            pstmt.setString(i + 1, data[i]);
                        } else {
                            pstmt.setNull(i + 1, Types.VARCHAR);
                        }
                    }
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    System.err.println("Gagal memasukkan data produk: " + e.getMessage() + " | Data: " + line);
                }
            }
            System.out.println("Data dari " + csvFilePath + " berhasil diimpor ke tabel products.");
        } catch (IOException e) {
            System.err.println("Gagal membaca file CSV " + csvFilePath + ": " + e.getMessage());
        }
    }

    private String buildInsertSql(String tableName, int columnCount) {
        if (columnCount == 0)
            return null;

        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
        for (int i = 0; i < columnCount; i++) {
            sql.append("?");
            if (i < columnCount - 1) {
                sql.append(",");
            }
        }
        sql.append(")");
        return sql.toString();
    }
}
