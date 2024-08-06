import javax.swing.*;
import javax.swing.table.DefaultTableModel; // Added import for DefaultTableModel
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;

public class SearchPanel extends JPanel {
    private JTextField airlineField, originField, destinationField, dateField, priceField;
    private JButton searchButton, clearButton;
    private JTable resultsTable;

    public SearchPanel() {
        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new GridLayout(7, 2));
        searchPanel.add(new JLabel("Airline:"));
        airlineField = new JTextField();
        searchPanel.add(airlineField);

        searchPanel.add(new JLabel("Origin:"));
        originField = new JTextField();
        searchPanel.add(originField);

        searchPanel.add(new JLabel("Destination:"));
        destinationField = new JTextField();
        searchPanel.add(destinationField);

        searchPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        searchPanel.add(dateField);

        searchPanel.add(new JLabel("Max Price:"));
        priceField = new JTextField();
        searchPanel.add(priceField);

        searchButton = new JButton("Search");
        searchPanel.add(searchButton);

        clearButton = new JButton("Clear");
        searchPanel.add(clearButton);

        add(searchPanel, BorderLayout.NORTH);

        resultsTable = new JTable();
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        searchButton.addActionListener(new SearchAction());
        clearButton.addActionListener(e -> clearFilters());
        resultsTable.addMouseListener(new TableClickListener());
    }

    private void clearFilters() {
        airlineField.setText("");
        originField.setText("");
        destinationField.setText("");
        dateField.setText("");
        priceField.setText("");
        resultsTable.setModel(new DefaultTableModel());
    }

    private class SearchAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String airline = airlineField.getText();
            String origin = originField.getText();
            String destination = destinationField.getText();
            String date = dateField.getText();
            String price = priceField.getText();

            ArrayList<Object[]> flights = searchFlights(airline, origin, destination, date, price);
            if (flights != null) {
                String[] columnNames = {"Airline", "Origin", "Destination", "Date", "Price"};
                Object[][] data = flights.toArray(new Object[0][]);
                resultsTable.setModel(new DefaultTableModel(data, columnNames));
            }
        }

        private ArrayList<Object[]> searchFlights(String airline, String origin, String destination, String date, String price) {
            ArrayList<Object[]> flights = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/flight", "root", "");
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM flights WHERE airline LIKE ? AND origin LIKE ? AND destination LIKE ? AND date LIKE ? AND price <= ?")) {

                ps.setString(1, "%" + airline + "%");
                ps.setString(2, "%" + origin + "%");
                ps.setString(3, "%" + destination + "%");
                ps.setString(4, "%" + date + "%");
                ps.setString(5, price.isEmpty() ? "999999.99" : price);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    flights.add(new Object[]{
                            rs.getString("airline"),
                            rs.getString("origin"),
                            rs.getString("destination"),
                            rs.getDate("date"),
                            rs.getDouble("price")
                    });
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return flights;
        }
    }

    private class TableClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1) {
                int row = resultsTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    String airline = (String) resultsTable.getValueAt(row, 0);
                    String origin = (String) resultsTable.getValueAt(row, 1);
                    String destination = (String) resultsTable.getValueAt(row, 2);
                    String date = resultsTable.getValueAt(row, 3).toString();
                    double price = (Double) resultsTable.getValueAt(row, 4);
                    showBookingManagementPanel(airline, origin, destination, date, price);
                }
            }
        }

        private void showBookingManagementPanel(String airline, String origin, String destination, String date, double price) {
            // Notify MainFrame to switch to BookingManagementPanel with flight details
            MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(SearchPanel.this);
            if (mainFrame != null) {
                BookingManagementPanel bookingPanel = new BookingManagementPanel();
                // Set flight details in booking panel if necessary
                // For example, you might want to add a method to set flight details
                mainFrame.switchToBookingManagementPanel(airline, origin, destination, date, price);
            }
        }
    }
}
