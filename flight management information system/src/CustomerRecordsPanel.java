import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class CustomerRecordsPanel extends JPanel {
    private JTextField searchField;
    private JButton searchButton;
    private JTable customersTable;

    public CustomerRecordsPanel() {
        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new GridLayout(1, 2));
        searchPanel.add(new JLabel("Search by Name:"));
        searchField = new JTextField();
        searchPanel.add(searchField);

        searchButton = new JButton("Search");
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        customersTable = new JTable();
        add(new JScrollPane(customersTable), BorderLayout.CENTER);

        searchButton.addActionListener(new SearchAction());
    }

    private class SearchAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = searchField.getText();
            ArrayList<Object[]> customers = searchCustomers(name);
            if (customers != null) {
                String[] columnNames = {"Booking ID", "Name", "Contact Details"};
                Object[][] data = customers.toArray(new Object[0][]);
                customersTable.setModel(new DefaultTableModel(data, columnNames));
            }
        }

        private ArrayList<Object[]> searchCustomers(String name) {
            ArrayList<Object[]> customers = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/flight", "root", "");
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM customers WHERE name LIKE ?")) {

                ps.setString(1, "%" + name + "%");
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    customers.add(new Object[]{
                            rs.getInt("booking_id"),
                            rs.getString("name"),
                            rs.getString("contact_details")
                    });
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return customers;
        }
    }
}
