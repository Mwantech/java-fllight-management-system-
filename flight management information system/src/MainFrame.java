import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private BookingManagementPanel bookingPanel;

    public MainFrame() {
        setTitle("Flight Management System");
        setLayout(new BorderLayout());

        // Create navigation panel
        JPanel navigationPanel = new JPanel(new GridLayout(6, 1)); // Adjusted for the logout button
        JButton searchButton = new JButton("Search Flights");
        JButton bookingButton = new JButton("Manage Bookings");
        JButton customerButton = new JButton("Customer Records");
        JButton paymentButton = new JButton("Process Payments");
        JButton logoutButton = new JButton("Logout");

        navigationPanel.add(searchButton);
        navigationPanel.add(bookingButton);
        navigationPanel.add(customerButton);
        navigationPanel.add(paymentButton);
        navigationPanel.add(logoutButton);

        add(navigationPanel, BorderLayout.WEST);

        // Create main content panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        SearchPanel searchPanel = new SearchPanel();
        bookingPanel = new BookingManagementPanel();
        mainPanel.add(searchPanel, "Search");
        mainPanel.add(bookingPanel, "Booking");
        mainPanel.add(new CustomerRecordsPanel(), "Customer");
        mainPanel.add(new PaymentPanel(), "Payment");

        add(mainPanel, BorderLayout.CENTER);

        // Set default panel
        cardLayout.show(mainPanel, "Search");

        // Add action listeners for navigation buttons
        searchButton.addActionListener(e -> cardLayout.show(mainPanel, "Search"));
        bookingButton.addActionListener(e -> cardLayout.show(mainPanel, "Booking"));
        customerButton.addActionListener(e -> cardLayout.show(mainPanel, "Customer"));
        paymentButton.addActionListener(e -> cardLayout.show(mainPanel, "Payment"));
        logoutButton.addActionListener(e -> logout());

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void switchToBookingManagementPanel(String airline, String origin, String destination, String date, double price) {
        bookingPanel.setFlightDetails(airline, origin, destination, date, price);
        cardLayout.show(mainPanel, "Booking");
    }

    public void switchToSearchPanel() {
        cardLayout.show(mainPanel, "Search");
    }

    private void logout() {
        dispose();  // Close the current frame
        SwingUtilities.invokeLater(LoginPanel::new);  // Show the login panel
    }
}
