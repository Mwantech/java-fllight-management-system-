import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginPanel extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton clearButton;
    private JButton exitButton;
    private JLabel statusLabel;

    public LoginPanel() {
        setTitle("Flight Management System - Login");
        setLayout(new GridLayout(6, 2, 10, 10)); // Adjusted layout for better spacing

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Login");
        add(loginButton);

        clearButton = new JButton("Clear");
        add(clearButton);

        exitButton = new JButton("Exit");
        add(exitButton);

        statusLabel = new JLabel();
        statusLabel.setForeground(Color.RED); // Red color for error messages
        add(statusLabel);

        loginButton.addActionListener(new LoginAction());
        clearButton.addActionListener(new ClearAction());
        exitButton.addActionListener(new ExitAction());

        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticateUser(username, password)) {
                SwingUtilities.invokeLater(() -> {
                    new MainFrame().setVisible(true); // Create and show the main frame
                    dispose(); // Close the login panel
                });
            } else {
                statusLabel.setText("Incorrect username or password");
            }
        }

        private boolean authenticateUser(String username, String password) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/flight", "root", "");
                 PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setString(1, username);
                ps.setString(2, password);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }

            } catch (SQLException ex) {
                statusLabel.setText("Database error: " + ex.getMessage());
                ex.printStackTrace();
                return false;
            }
        }
    }

    private class ClearAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            usernameField.setText("");
            passwordField.setText("");
            statusLabel.setText("");
        }
    }

    private class ExitAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPanel::new); // Show the login panel first
    }
}
