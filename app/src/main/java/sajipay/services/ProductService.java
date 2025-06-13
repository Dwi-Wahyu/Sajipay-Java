package sajipay.services;

import sajipay.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

// Contoh di kelas lain
public class ProductService {
    public void displayProducts() {
        String sql = "SELECT id, name, price FROM products";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            // Loop melalui hasil query
            while (rs.next()) {
                System.out.println(rs.getString("id") + "\t" +
                        rs.getString("name") + "\t" +
                        rs.getDouble("price"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}