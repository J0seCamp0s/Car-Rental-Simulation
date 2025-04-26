import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import javax.swing.*;

public class CarRentalGUI {
    private CarRental carRental;
    private JTextArea commandOutputArea;
    private JTextField rentField;
    private JTextField returnField;
    private JButton rentButton;
    private JButton returnButton;
    private JButton listButton;
    private JButton transactionsButton;
    private JPanel rentPanel;
    private JPanel returnPanel;
    private JPanel listPanel;
    private JPanel transactionsPanel;

    public CarRentalGUI(CarRental carRental) {
        this.carRental = carRental;
        initializeGUI();
    }

    private void initializeGUI() {
        JFrame frame = new JFrame("Car Rental System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Set color scheme based on logo colors
        Color mainBlue = new Color(173, 216, 230);      
        Color darkBlue = new Color(25, 25, 112);        
        Color backgroundColor = new Color(224, 255, 255); 
        Color buttonTextColor = Color.WHITE;

        // Style the frame
        frame.getContentPane().setBackground(backgroundColor);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Style the header panel
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, darkBlue));

        try {
            // Load and scale the logo
            ImageIcon originalIcon = new ImageIcon("car_logo.jpeg");
            Image scaledImage = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            
            // Set window icon
            frame.setIconImage(originalIcon.getImage());
            
            // Create centered logo panel
            JPanel logoCenterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            logoCenterPanel.setBackground(backgroundColor);
            JLabel logoLabel = new JLabel(scaledIcon);
            logoCenterPanel.add(logoLabel);
            
            // Create title with large, bold font
            JLabel titleLabel = new JLabel("Car Rental System");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
            titleLabel.setForeground(darkBlue);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            // Add logo and title to header panel
            headerPanel.add(logoCenterPanel, BorderLayout.CENTER);
            headerPanel.add(titleLabel, BorderLayout.SOUTH);
            
        } catch (Exception e) {
            // Fallback to just title if image loading fails
            JLabel titleLabel = new JLabel("Car Rental System");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
            titleLabel.setForeground(darkBlue);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            headerPanel.add(titleLabel, BorderLayout.CENTER);
        }

        // Add header panel to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create tabbed pane and add it to main panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(mainBlue);
        tabbedPane.setForeground(darkBlue);
        tabbedPane.setBorder(BorderFactory.createLineBorder(darkBlue, 1));

        // Style the tabs
        UIManager.put("TabbedPane.selected", mainBlue);
        UIManager.put("TabbedPane.contentAreaColor", backgroundColor);

        // Rent Tab
        rentPanel = new JPanel(new BorderLayout());
        JLabel rentLabel = new JLabel("Enter Car Type (SEDAN/SUV/VAN):");
        rentField = new JTextField();
        rentButton = new JButton("Rent");
        JPanel rentInputPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        rentInputPanel.add(rentLabel);
        rentInputPanel.add(rentField);
        rentPanel.add(rentInputPanel, BorderLayout.CENTER);
        rentPanel.add(rentButton, BorderLayout.SOUTH);

        // Return Tab
        returnPanel = new JPanel(new BorderLayout());
        JLabel returnLabel = new JLabel("Enter License Plate and KM (space-separated):");
        returnField = new JTextField();
        returnButton = new JButton("Return");
        JPanel returnInputPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        returnInputPanel.add(returnLabel);
        returnInputPanel.add(returnField);
        returnPanel.add(returnInputPanel, BorderLayout.CENTER);
        returnPanel.add(returnButton, BorderLayout.SOUTH);

        // List Cars Tab
        listPanel = new JPanel(new BorderLayout(10, 10));
        listButton = new JButton("List Cars");
        listPanel.add(listButton, BorderLayout.CENTER);

        // Transactions Tab
        transactionsPanel = new JPanel(new BorderLayout(10, 10));
        transactionsButton = new JButton("View Transactions");
        transactionsPanel.add(transactionsButton, BorderLayout.CENTER);

        // Style the buttons
        for (JButton button : Arrays.asList(rentButton, returnButton, listButton, transactionsButton)) {
            button.setBackground(darkBlue);
            button.setForeground(buttonTextColor);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createRaisedBevelBorder());
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(button.getBackground().darker());
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(darkBlue);
                }
            });
        }

        // Style the input panels
        for (JPanel panel : Arrays.asList(rentPanel, returnPanel, listPanel, transactionsPanel)) {
            panel.setBackground(backgroundColor);
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(darkBlue, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
        }

        // Style the text fields
        for (JTextField field : Arrays.asList(rentField, returnField)) {
            field.setBackground(Color.WHITE);
            field.setForeground(darkBlue);
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(darkBlue),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        }

        // Add tabs to tabbed pane
        tabbedPane.addTab("Rent", rentPanel);
        tabbedPane.addTab("Return", returnPanel);
        tabbedPane.addTab("List Cars", listPanel);
        tabbedPane.addTab("Transactions", transactionsPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Command Output Panel (Visible on all tabs)
        JPanel commandOutputPanel = new JPanel(new BorderLayout(5, 5));
        commandOutputPanel.setBackground(backgroundColor);
        commandOutputArea = new JTextArea();
        commandOutputArea.setEditable(false);
        commandOutputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        // Add margins to the text area
        commandOutputArea.setMargin(new Insets(5, 5, 5, 5));
        commandOutputArea.setBackground(Color.WHITE);
        commandOutputArea.setForeground(darkBlue);
        commandOutputArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(darkBlue),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane commandOutputScrollPane = new JScrollPane(commandOutputArea);
        commandOutputScrollPane.setPreferredSize(new Dimension(frame.getWidth(), 200));

        JLabel outputLabel = new JLabel(" Console Output:", SwingConstants.LEFT);
        outputLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 0));
        commandOutputPanel.add(outputLabel, BorderLayout.NORTH);
        commandOutputPanel.add(commandOutputScrollPane, BorderLayout.CENTER);

        // Create a JSplitPane for resizable command output
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, commandOutputPanel);
        splitPane.setResizeWeight(0.7);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Add main panel to frame
        frame.add(mainPanel);

        // Redirect console output to the JTextArea
        redirectSystemStreams();

        // Action Listeners with RunCommands functionality
        rentButton.addActionListener(e -> {
            String carType = rentField.getText().trim().toUpperCase();
            if (!carType.isBlank()) {
                Car selectedCar = carRental.RentCar(carType);
                if (selectedCar != null) {
                    System.out.println(String.format("Car with license plate %s has been rented out sucessfully with a discount of %d%%!", selectedCar.GetLicensePlate(), selectedCar.GetDiscountRate()));
                    carRental.UpdateRentedCarsFile(selectedCar);
                    rentField.setText(""); // Clear the input field after successful rental
                }
            } else {
                System.out.println("No parameters were given for RENT command!");
                System.out.println("Unable to perform operation!");
            }
            //Update allocated cars for shop
            carRental.UpdateShopCars();
        });

        returnButton.addActionListener(e -> {
            String[] inputs = returnField.getText().trim().split(" ");
            if (inputs.length != 2) {
                System.out.println("Incorrect number of parameters for RETURN command!");
                System.out.println("Format: LICENSE_PLATE KILOMETERS");
                System.out.println("Unable to perform operation!");
                return;
            }

            String licensePlate = inputs[0].trim().toUpperCase();
            if (licensePlate.isEmpty()) {
                System.out.println("License plate cannot be empty!");
                System.out.println("Unable to perform operation!");
                return;
            }
            else if (!carRental.CheckLincensePlateFormat(licensePlate)) {
                System.out.println(String.format("%s is not a valid license plate format!", licensePlate));
                System.out.println("Unable to perform operation!");
                return;
            }

            try {
                Double kilometers = Double.valueOf(inputs[1].trim());
                carRental.ReturnCar(licensePlate, kilometers);
                returnField.setText(""); // Clear the input field after successful return
            } catch (NumberFormatException ex) {
                System.out.println("Invalid kilometers value!");
                System.out.println("Unable to perform operation!");
            }
            //Update allocated cars for shop
            carRental.UpdateShopCars();
        });

        listButton.addActionListener(e -> {
            commandOutputArea.setText(""); // Clear previous output
            carRental.GetList();
        });

        transactionsButton.addActionListener(e -> {
            commandOutputArea.setText(""); // Clear previous output
            carRental.GetTransactions();
        });

        frame.setVisible(true);
    }

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            private StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) {
                buffer.append((char) b);
                if (b == '\n') {
                    final String text = buffer.toString();
                    SwingUtilities.invokeLater(() -> {
                        commandOutputArea.append(text);
                        commandOutputArea.setCaretPosition(commandOutputArea.getDocument().getLength());
                    });
                    buffer.setLength(0);
                }
            }

            @Override
            public void write(byte[] b, int off, int len) {
                String text = new String(b, off, len);
                SwingUtilities.invokeLater(() -> {
                    commandOutputArea.append(text);
                    commandOutputArea.setCaretPosition(commandOutputArea.getDocument().getLength());
                });
            }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }
}