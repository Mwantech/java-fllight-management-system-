import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class BookingManagementPanel extends JPanel {
    private JTextField flightIdField;
    private JButton loadSeatsButton;
    private JTable seatsTable;
    private JButton bookButton;
    private JButton backButton;
    private JButton nextButton;
    private JLabel totalPriceLabel;
    private double seatPrice;
    private String flightId;

    public BookingManagementPanel() {
        setLayout(new BorderLayout());

        // Input panel for flight ID
        JPanel inputPanel = new JPanel(new GridLayout(1, 3));
        inputPanel.add(new JLabel("Flight ID:"));
        flightIdField = new JTextField();
        inputPanel.add(flightIdField);

        loadSeatsButton = new JButton("Load Seats");
        inputPanel.add(loadSeatsButton);
        add(inputPanel, BorderLayout.NORTH);

        // Table to display seats
        seatsTable = new JTable();
        add(new JScrollPane(seatsTable), BorderLayout.CENTER);

        // Booking panel with total price, book button, back button, and next button
        JPanel bookingPanel = new JPanel(new BorderLayout());
        totalPriceLabel = new JLabel("Total Price: $0.00");
        bookingPanel.add(totalPriceLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        bookButton = new JButton("Book Selected Seats");
        backButton = new JButton("Back");
        nextButton = new JButton("Next");
        buttonPanel.add(bookButton);
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);
        bookingPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(bookingPanel, BorderLayout.SOUTH);

        // Action listeners for buttons
        loadSeatsButton.addActionListener(new LoadSeatsAction());
        bookButton.addActionListener(new BookAction());
        backButton.addActionListener(e -> backToSearchPanel());
        nextButton.addActionListener(e -> proceedToCustomerInfoPanel());
    }

    // Method to set flight details and initialize seat price if needed
    public void setFlightDetails(String airline, String origin, String destination, String date, double price) {
        // Convert flight details to flight ID if needed or set directly
        flightIdField.setText("1234");  // Example flight ID, replace with actual logic
        // Set seat price if needed
        seatPrice = price;
    }

    // Method to navigate back to the search panel
    private void backToSearchPanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) frame;
            mainFrame.switchToSearchPanel();  // Assuming a method for switching back to SearchPanel
        }
    }

    // Method to proceed to the customer information panel
    private void proceedToCustomerInfoPanel() {
        System.out.println("Switching to Customer Info Panel");
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) frame;
            mainFrame.switchToCustomerRecordsPanel();
        }
    }

    // Action listener to load seats for the selected flight
    private class LoadSeatsAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                flightId = flightIdField.getText().trim();
                int flightIdInt = Integer.parseInt(flightId);
                loadSeats(flightIdInt);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid Flight ID. Please enter a valid number.");
            }
        }

        private void loadSeats(int flightId) {
            System.out.println("Loading seats for flight ID: " + flightId);
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/flight", "root", "");
                 PreparedStatement ps = conn.prepareStatement("SELECT seat_number, price FROM seats WHERE flight_id = ? AND availability = 'available'")) {

                ps.setInt(1, flightId);
                ResultSet rs = ps.executeQuery();

                ArrayList<Object[]> seats = new ArrayList<>();
                while (rs.next()) {
                    System.out.println("Found seat: " + rs.getString("seat_number") + ", Price: " + rs.getDouble("price"));
                    seats.add(new Object[]{
                        rs.getString("seat_number"),
                        rs.getDouble("price")
                    });
                }

                if (seats.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No available seats found for this flight.");
                } else {
                    String[] columnNames = {"Seat Number", "Price"};
                    Object[][] data = seats.toArray(new Object[0][]);
                    seatsTable.setModel(new DefaultTableModel(data, columnNames));
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error loading seats: " + ex.getMessage());
            }
        }
    }

    // Action listener to book selected seats
    private class BookAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int flightId = Integer.parseInt(flightIdField.getText());
                int userId = 1; // Example user ID
                double totalPrice = 0;

                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/flight", "root", "")) {
                    conn.setAutoCommit(false);

                    for (int seatIndex : seatsTable.getSelectedRows()) {
                        String seatNumber = (String) seatsTable.getValueAt(seatIndex, 0);
                        double price = (Double) seatsTable.getValueAt(seatIndex, 1);
                        totalPrice += price;

                        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO bookings (flight_id, user_id, seat_number, total_price, status) VALUES (?, ?, ?, ?, 'unpaid')")) {
                            ps.setInt(1, flightId);
                            ps.setInt(2, userId);
                            ps.setString(3, seatNumber);
                            ps.setDouble(4, totalPrice);
                            ps.executeUpdate();
                        }

                        try (PreparedStatement ps = conn.prepareStatement("UPDATE seats SET availability = 'booked' WHERE flight_id = ? AND seat_number = ?")) {
                            ps.setInt(1, flightId);
                            ps.setString(2, seatNumber);
                            ps.executeUpdate();
                        }
                    }
                    conn.commit();
                    totalPriceLabel.setText("Total Price: $" + totalPrice);
                    JOptionPane.showMessageDialog(null, "Seats booked successfully!");

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error booking seats: " + ex.getMessage());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid Flight ID. Please enter a valid number.");
            }
        }
    }
}
