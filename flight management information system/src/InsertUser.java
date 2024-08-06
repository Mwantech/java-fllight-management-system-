import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertUser {
    public static void main(String[] args) {
        // Database connection details
        String url = "jdbc:mysql://localhost:3306/flight";
        String user = "root";
        String password = "";
        
        // User details to be inserted
        String usernameToInsert = "joshua";
        String passwordToInsert = "1234";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection
            connection = DriverManager.getConnection(url, user, password);

            // SQL query to insert a new user
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

            // Prepare the statement
            preparedStatement = connection.prepareStatement(sql);

            // Set parameters
            preparedStatement.setString(1, usernameToInsert);
            preparedStatement.setString(2, passwordToInsert);

            // Execute the query
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted.");

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database error.");
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
