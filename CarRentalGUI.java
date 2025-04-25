import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;

public class CarRentalGUI {
    private CarRental carRental;
    private JTextArea commandOutputArea; // Text area to display console output

    public CarRentalGUI(CarRental carRental) {
        this.carRental = carRental;
        initializeGUI();
    }

    private void initializeGUI() {
        JFrame frame = new JFrame("Car Rental System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Set color scheme
        Color backgroundColor = new Color(240, 248, 255); // Light blue
        Color panelColor = new Color(173, 216, 230); // Lighter blue
        Color buttonColor = new Color(100, 149, 237); // Cornflower blue

        // Add title to the GUI
        JLabel titleLabel = new JLabel("Car Rental System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.DARK_GRAY);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Rent Tab
        JPanel rentPanel = new JPanel(new BorderLayout());
        rentPanel.setBackground(panelColor);
        JLabel rentLabel = new JLabel("Enter Car Type to Rent:");
        JTextField rentField = new JTextField();
        JButton rentButton = new JButton("Rent");
        rentButton.setBackground(buttonColor);
        rentButton.setForeground(Color.WHITE);
        JPanel rentInputPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        rentInputPanel.add(rentLabel);
        rentInputPanel.add(rentField);
        rentPanel.add(rentInputPanel, BorderLayout.CENTER);
        rentPanel.add(rentButton, BorderLayout.SOUTH);

        // Return Tab
        JPanel returnPanel = new JPanel(new BorderLayout());
        returnPanel.setBackground(panelColor);
        JLabel returnLabel = new JLabel("Enter License Plate and KM (comma-separated):");
        JTextField returnField = new JTextField();
        JButton returnButton = new JButton("Return");
        returnButton.setBackground(buttonColor);
        returnButton.setForeground(Color.WHITE);
        JPanel returnInputPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        returnInputPanel.add(returnLabel);
        returnInputPanel.add(returnField);
        returnPanel.add(returnInputPanel, BorderLayout.CENTER);
        returnPanel.add(returnButton, BorderLayout.SOUTH);

        // List Cars Tab
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(panelColor);
        JButton listButton = new JButton("List Cars");
        listButton.setBackground(buttonColor);
        listButton.setForeground(Color.WHITE);
        listPanel.add(listButton, BorderLayout.NORTH);

        // Transactions Tab
        JPanel transactionsPanel = new JPanel(new BorderLayout());
        transactionsPanel.setBackground(panelColor);
        JButton transactionsButton = new JButton("View Transactions");
        transactionsButton.setBackground(buttonColor);
        transactionsButton.setForeground(Color.WHITE);
        transactionsPanel.add(transactionsButton, BorderLayout.NORTH);

        // Add tabs to tabbed pane
        tabbedPane.addTab("Rent", rentPanel);
        tabbedPane.addTab("Return", returnPanel);
        tabbedPane.addTab("List Cars", listPanel);
        tabbedPane.addTab("Transactions", transactionsPanel);

        // Command Output Panel (Visible on all tabs)
        JPanel commandOutputPanel = new JPanel(new BorderLayout());
        commandOutputPanel.setBackground(backgroundColor);
        commandOutputArea = new JTextArea();
        commandOutputArea.setEditable(false);
        JScrollPane commandOutputScrollPane = new JScrollPane(commandOutputArea);
        commandOutputPanel.add(new JLabel("Console Output:"), BorderLayout.NORTH);
        commandOutputPanel.add(commandOutputScrollPane, BorderLayout.CENTER);

        // Add title, tabbed pane, and command output panel to frame
        frame.setLayout(new BorderLayout());
        frame.add(titleLabel, BorderLayout.NORTH);
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.add(commandOutputPanel, BorderLayout.SOUTH);

        // Redirect console output to the JTextArea
        redirectSystemStreams();

        // Action Listeners
        rentButton.addActionListener(e -> {
            String carType = rentField.getText().trim().toUpperCase();
            if (carType.isEmpty()) {
                System.out.println("No parameters were given for RENT command!");
                System.out.println("Unable to perform operation!");
                return;
            }
            carRental.RentCar(carType);
        });

        returnButton.addActionListener(e -> {
            String[] inputs = returnField.getText().trim().split(",");
            if (inputs.length != 2) {
                System.out.println("Error: Please enter license plate and kilometers separated by a comma.");
                return;
            }
            String licensePlate = inputs[0].trim().toUpperCase();
            try {
                double kilometers = Double.parseDouble(inputs[1].trim());
                carRental.ReturnCar(licensePlate, kilometers);
            } catch (NumberFormatException ex) {
                System.out.println("Error: Invalid kilometers value.");
            }
        });

        listButton.addActionListener(e -> carRental.GetList());

        transactionsButton.addActionListener(e -> carRental.GetTransactions());

        frame.setVisible(true);
    }

    // Redirect System.out and System.err to the JTextArea
    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                commandOutputArea.append(String.valueOf((char) b));
                commandOutputArea.setCaretPosition(commandOutputArea.getDocument().getLength());
            }

            @Override
            public void write(byte[] b, int off, int len) {
                commandOutputArea.append(new String(b, off, len));
                commandOutputArea.setCaretPosition(commandOutputArea.getDocument().getLength());
            }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }
}