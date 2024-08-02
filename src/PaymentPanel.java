import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class PaymentPanel extends JPanel {
    private JTextField bookingIdField;
    private JLabel totalPriceLabel;
    private JButton processPaymentButton;
    private JButton backButton;

    public PaymentPanel() {
        setLayout(new GridLayout(4, 2));

        add(new JLabel("Booking ID:"));
        bookingIdField = new JTextField();
        add(bookingIdField);

        add(new JLabel("Total Price:"));
        totalPriceLabel = new JLabel("$0.00");
        add(totalPriceLabel);

        processPaymentButton = new JButton("Process Payment");
        add(processPaymentButton);

        backButton = new JButton("Back");
        add(backButton);

        processPaymentButton.addActionListener(new ProcessPaymentAction());
        backButton.addActionListener(e -> backToBookingManagementPanel());
    }

    private class ProcessPaymentAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int bookingId;
            try {
                bookingId = Integer.parseInt(bookingIdField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid Booking ID");
                return;
            }

            double totalPrice = getTotalPrice(bookingId);
            if (totalPrice == 0) {
                JOptionPane.showMessageDialog(null, "Booking not found or already paid.");
                return;
            }

            totalPriceLabel.setText("$" + totalPrice);

            // Simulate processing payment
            SwingUtilities.invokeLater(() -> {
                JDialog loadingDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(PaymentPanel.this), "Processing Payment", true);
                loadingDialog.setLayout(new FlowLayout());
                loadingDialog.add(new JLabel("Processing..."));
                loadingDialog.setSize(200, 100);
                loadingDialog.setLocationRelativeTo(null);
                loadingDialog.setVisible(true);

                new Timer(2000, evt -> {
                    loadingDialog.dispose();
                    int response = JOptionPane.showConfirmDialog(null, "Proceed with payment of $" + totalPrice + "?", "Payment Confirmation", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        processPayment(bookingId, totalPrice);
                    }
                }).start();
            });
        }

        private double getTotalPrice(int bookingId) {
            double totalPrice = 0;
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/flight", "root", "");
                 PreparedStatement ps = conn.prepareStatement("SELECT total_price FROM bookings WHERE id = ? AND status = 'unpaid'")) {

                ps.setInt(1, bookingId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    totalPrice = rs.getDouble("total_price");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return totalPrice;
        }

        private void processPayment(int bookingId, double totalPrice) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/flight", "root", "");
                 PreparedStatement ps = conn.prepareStatement("UPDATE bookings SET status = 'paid' WHERE id = ?")) {

                ps.setInt(1, bookingId);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(null, "Payment of $" + totalPrice + " processed successfully!");

                // Generate and show receipt (for simplicity, using JOptionPane)
                JOptionPane.showMessageDialog(null, "Receipt:\nBooking ID: " + bookingId + "\nTotal Price: $" + totalPrice + "\nStatus: Paid");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void backToBookingManagementPanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) frame;
            mainFrame.switchToBookingManagementPanel();  // Assuming a method for switching back to BookingManagementPanel
        }
    }
}
