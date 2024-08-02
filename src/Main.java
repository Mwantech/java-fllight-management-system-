import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Schedule a job for the event dispatch thread
        SwingUtilities.invokeLater(() -> {
            new LoginPanel(); // Create and show the login panel
        });
    }
}
