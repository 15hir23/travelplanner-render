package com.taskmanager;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class TravelPlanner {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(LoginUI::new);
    }
}

class RoundedButton extends JButton {
    private Color hoverBackgroundColor;
    private Color pressedBackgroundColor;
    private Color backgroundColor;
    private int radius = 20;

    public RoundedButton(String text) {
        super(text);
        this.backgroundColor = new Color(41, 128, 185);
        this.hoverBackgroundColor = new Color(52, 152, 219);
        this.pressedBackgroundColor = new Color(26, 86, 126);
        
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverBackgroundColor);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(backgroundColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(pressedBackgroundColor);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(hoverBackgroundColor);
                repaint();
            }
        });
    }

    public void setColors(Color background, Color hover, Color pressed) {
        this.backgroundColor = background;
        this.hoverBackgroundColor = hover;
        this.pressedBackgroundColor = pressed;
        setBackground(background);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (getModel().isPressed()) {
            g2.setColor(pressedBackgroundColor);
        } else if (getModel().isRollover()) {
            g2.setColor(hoverBackgroundColor);
        } else {
            g2.setColor(backgroundColor);
        }
        
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
        g2.dispose();
        
        super.paintComponent(g);
    }
}

class RoundedTextField extends JTextField {
    private Shape shape;
    private Color borderColor = new Color(41, 128, 185);
    private int radius = 15;
    
    public RoundedTextField() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, radius, radius));
        g2.setColor(borderColor);
        g2.draw(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, radius, radius));
        g2.dispose();
        super.paintComponent(g);
    }
    
    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, radius, radius);
        }
        return shape.contains(x, y);
    }
}

class RoundedPasswordField extends JPasswordField {
    private Shape shape;
    private Color borderColor = new Color(41, 128, 185);
    private int radius = 15;
    
    public RoundedPasswordField() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, radius, radius));
        g2.setColor(borderColor);
        g2.draw(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, radius, radius));
        g2.dispose();
        super.paintComponent(g);
    }
    
    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, radius, radius);
        }
        return shape.contains(x, y);
    }
}

class BackgroundPanel extends JPanel {
    private Image backgroundImage;
    private float opacity = 0.8f;

    public BackgroundPanel(String imagePath) {
        try {
            backgroundImage = new ImageIcon(imagePath).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Draw background image
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        
        // Apply semi-transparent overlay
        g2d.setColor(new Color(0, 0, 0, 0.4f));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g2d.dispose();
    }
}

class LoginUI {
    private JFrame frame;
    private RoundedTextField userField;
    private RoundedPasswordField passField;

    LoginUI() {
        frame = new JFrame("Travel Planner - Login");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        // Create background panel with airplane image
        BackgroundPanel backgroundPanel = new BackgroundPanel("resources/flightanimated.jpg");
        frame.setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new BorderLayout());
        
        // Create translucent card panel for login
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBackground(new Color(255, 255, 255, 200));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // App title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        
        JLabel logoLabel = new JLabel(new ImageIcon("resources/travel_logo.png"));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Travel Planner");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Plan your adventures with ease");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        subtitleLabel.setForeground(new Color(52, 73, 94));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titlePanel.add(Box.createVerticalGlue());
        titlePanel.add(logoLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        titlePanel.add(Box.createVerticalGlue());
        
        // Login form panel
        JPanel loginFormPanel = new JPanel();
        loginFormPanel.setLayout(new BoxLayout(loginFormPanel, BoxLayout.Y_AXIS));
        loginFormPanel.setOpaque(false);
        loginFormPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel loginLabel = new JLabel("Login to Your Account");
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loginLabel.setForeground(new Color(52, 73, 94));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel userPanel = new JPanel(new BorderLayout(10, 0));
     
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(new Color(52, 73, 94));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel userInputPanel = new JPanel(new BorderLayout(10, 0));
        userInputPanel.setOpaque(false);
        JLabel userIcon = new JLabel(new ImageIcon("resources/user_icon.png"));
        userField = new RoundedTextField();
        userField.setBackground(new Color(240, 240, 240));
        userInputPanel.add(userIcon, BorderLayout.WEST);
        userInputPanel.add(userField, BorderLayout.CENTER);

        userPanel.add(userLabel);
        userPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Add some spacing
        userPanel.add(userInputPanel);

        // Password field with label
        JPanel passPanel = new JPanel();
        passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.Y_AXIS));
        passPanel.setOpaque(false);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setForeground(new Color(52, 73, 94));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel passInputPanel = new JPanel(new BorderLayout(10, 0));
        passInputPanel.setOpaque(false);
        JLabel passIcon = new JLabel(new ImageIcon("resources/lock_icon.png"));
        passField = new RoundedPasswordField();
        passField.setBackground(new Color(240, 240, 240));
        passInputPanel.add(passIcon, BorderLayout.WEST);
        passInputPanel.add(passField, BorderLayout.CENTER);

        passPanel.add(passLabel);
        passPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Add some spacing
        passPanel.add(passInputPanel);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);

        RoundedButton loginButton = new RoundedButton("Login");
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setColors(new Color(41, 128, 185), new Color(52, 152, 219), new Color(26, 86, 126));

        RoundedButton signupButton = new RoundedButton("Sign Up");
        signupButton.setPreferredSize(new Dimension(120, 40));
        signupButton.setColors(new Color(46, 204, 113), new Color(39, 174, 96), new Color(27, 94, 54));

        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(signupButton);

        loginButton.addActionListener(e -> handleLogin());
        signupButton.addActionListener(e -> handleSignup());

        // Assemble the login form
        loginFormPanel.add(loginLabel);
        loginFormPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        loginFormPanel.add(userPanel);
        loginFormPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        loginFormPanel.add(passPanel);
        loginFormPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        loginFormPanel.add(buttonPanel);
        
        // Add panels to card
        cardPanel.add(titlePanel, BorderLayout.WEST);
        cardPanel.add(loginFormPanel, BorderLayout.CENTER);
        
        // Add card to main panel with centering
        JPanel centeringPanel = new JPanel(new GridBagLayout());
        centeringPanel.setOpaque(false);
        centeringPanel.add(cardPanel);
        backgroundPanel.add(centeringPanel, BorderLayout.CENTER);
        
        frame.setVisible(true);
    }

    private void handleLogin() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            showErrorMessage("Please enter both username and password.");
            return;
        }

        if (validateUser(username, password)) {
            showSuccessMessage("Login Successful!");
            frame.dispose();
            SwingUtilities.invokeLater(() -> new ItineraryDashboard(username));
        } else {
            showErrorMessage("Invalid Credentials!");
        }
    }

    private void handleSignup() {
        JPanel signupPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        signupPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        RoundedTextField newUserField = new RoundedTextField();
        RoundedPasswordField newPassField = new RoundedPasswordField();
        
        JPanel userPanel = new JPanel(new BorderLayout(5, 0));
        userPanel.add(new JLabel("Username:"), BorderLayout.WEST);
        userPanel.add(newUserField, BorderLayout.CENTER);
        
        JPanel passPanel = new JPanel(new BorderLayout(5, 0));
        passPanel.add(new JLabel("Password:"), BorderLayout.WEST);
        passPanel.add(newPassField, BorderLayout.CENTER);
        
        signupPanel.add(userPanel);
        signupPanel.add(passPanel);

        JOptionPane optionPane = new JOptionPane(signupPanel, JOptionPane.PLAIN_MESSAGE, 
                                  JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = optionPane.createDialog(frame, "Sign Up");
        dialog.setVisible(true);
        
        Object result = optionPane.getValue();
        if (result != null && (Integer) result == JOptionPane.OK_OPTION) {
            String newUsername = newUserField.getText().trim();
            String newPassword = new String(newPassField.getPassword()).trim();

            if (newUsername.isEmpty() || newPassword.isEmpty()) {
                showErrorMessage("Username and password cannot be empty.");
                return;
            }

            if (registerUser(newUsername, newPassword)) {
                showSuccessMessage("Sign Up Successful! Please login.");
            } else {
                showErrorMessage("Sign Up Failed. Username may already exist.");
            }
        }
    }

    private void showErrorMessage(String message) {
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JOptionPane.showMessageDialog(frame, messageLabel, "Error", 
                                     JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessMessage(String message) {
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JOptionPane.showMessageDialog(frame, messageLabel, "Success", 
                                     JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean validateUser(String username, String password) {
        String query = "SELECT id FROM users WHERE username=? AND password=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
            return false;
        }
    }

    private boolean registerUser(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
            return false;
        }
    }
}

class ItineraryDashboard {
    private JFrame frame;
    private DefaultListModel<String> destinationListModel;
    private String username;
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color accentColor = new Color(46, 204, 113);
    private Color warningColor = new Color(231, 76, 60);

    ItineraryDashboard(String username) {
        this.username = username;

        frame = new JFrame("Travel Planner - Dashboard");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        // Create background panel with travel image
        BackgroundPanel backgroundPanel = new BackgroundPanel("resources/flightanimated.jpg");
        frame.setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        
        // Banner Panel
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(new Color(41, 128, 185, 200));
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel appLabel = new JLabel("Travel Planner Dashboard", SwingConstants.RIGHT);
        appLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        appLabel.setForeground(Color.WHITE);
        
        bannerPanel.add(welcomeLabel, BorderLayout.WEST);
        bannerPanel.add(appLabel, BorderLayout.EAST);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create destination list panel
        JPanel listPanel = new JPanel(new BorderLayout(10, 10));
        listPanel.setBackground(new Color(255, 255, 255, 220));
        listPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor, 2, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        
        JLabel listTitle = new JLabel("Your Destinations");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        listTitle.setForeground(primaryColor);
        listTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        destinationListModel = new DefaultListModel<>();
        JList<String> destinationList = new JList<>(destinationListModel);
        destinationList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        destinationList.setSelectionBackground(new Color(52, 152, 219, 150));
        destinationList.setSelectionForeground(Color.WHITE);
        destinationList.setFixedCellHeight(40);
        
     // Custom cell renderer for the list
        destinationList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                JLabel label = (JLabel) c;
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                // Load the icon and resize it
                ImageIcon originalIcon = new ImageIcon("resources/destination.png");
                Image originalImage = originalIcon.getImage();

                // Set the desired size for the icon
                int iconWidth = 20; // Adjust as needed
                int iconHeight = 20; // Adjust as needed

                // Resize the image
                Image resizedImage = originalImage.getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH);
                ImageIcon resizedIcon = new ImageIcon(resizedImage);

                // Set the resized icon
                label.setIcon(resizedIcon);

                // Set text color for unselected items
                if (!isSelected) {
                    label.setForeground(new Color(52, 73, 94));
                }

                return label;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(destinationList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        listPanel.add(listTitle, BorderLayout.NORTH);
        listPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 5, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        RoundedButton addButton = new RoundedButton("Add place");
        addButton.setColors(primaryColor, secondaryColor, primaryColor.darker());
        
        RoundedButton editButton = new RoundedButton("Edit");
        editButton.setColors(new Color(243, 156, 18), new Color(241, 196, 15), new Color(211, 84, 0));
        
        RoundedButton removeButton = new RoundedButton("Remove");
        removeButton.setColors(warningColor, warningColor.brighter(), warningColor.darker());
        
        RoundedButton exploreButton = new RoundedButton("Explore");
        exploreButton.setColors(accentColor, accentColor.brighter(), accentColor.darker());
        
        RoundedButton logoutButton = new RoundedButton("Logout");
        logoutButton.setColors(new Color(142, 68, 173), new Color(155, 89, 182), new Color(125, 60, 152));
        
        addButton.addActionListener(e -> addDestination());
        editButton.addActionListener(e -> editDestination(destinationList));
        removeButton.addActionListener(e -> removeDestination(destinationList));
        exploreButton.addActionListener(e -> exploreDestinations());
        logoutButton.addActionListener(e -> logout());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(exploreButton);
        buttonPanel.add(logoutButton);
        
        // Dashboard statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setOpaque(false);
        
     
        
        // Calculate total budget
        double totalBudget = getTotalBudget();
        JPanel budgetPanel = createStatPanel("Total Budget", "₹" + String.format("%.2f", totalBudget), new Color(46, 204, 113, 220));
        
        // Get upcoming trip
        String upcomingTrip = getUpcomingTrip();
        JPanel upcomingPanel = createStatPanel("Next Destination", upcomingTrip != null ? upcomingTrip : "None planned", new Color(243, 156, 18, 220));
        
        // Calculate expenses
        double totalExpenses = getTotalExpenses();
        JPanel expensesPanel = createStatPanel("Total Expenses", "₹" + String.format("%.2f", totalExpenses), new Color(231, 76, 60, 220));
        
     // Remove or comment out the totalTripsPanel creation and addition
        JPanel totalTripsPanel = createStatPanel("QUOTE OF THE DAY", "Travel more, <br> worry less.", new Color(41, 128, 185, 220));
        statsPanel.add(totalTripsPanel);
        
      statsPanel.add(totalTripsPanel);
        statsPanel.add(budgetPanel);
        statsPanel.add(upcomingPanel);
        statsPanel.add(expensesPanel);
        
        // Add panels to content panel
        JPanel leftPanel = new JPanel(new BorderLayout(15, 15));
        leftPanel.setOpaque(false);
        leftPanel.add(listPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(leftPanel, BorderLayout.CENTER);
        contentPanel.add(statsPanel, BorderLayout.EAST);
        
        // Add all panels to main frame
        backgroundPanel.add(bannerPanel, BorderLayout.NORTH);
        backgroundPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add a status bar at the bottom
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(52, 73, 94, 200));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel statusLabel = new JLabel("Ready to plan your next adventure!");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        
        JLabel dateLabel = new JLabel(java.time.LocalDate.now().toString());
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(dateLabel, BorderLayout.EAST);
        
        backgroundPanel.add(statusBar, BorderLayout.SOUTH);
        
        loadItinerary();
        frame.setVisible(true);
    }
     
    private JPanel createStatPanel(String title, String quote, Color bgColor) {
        JPanel panel = new JPanel(new BorderLayout(5, 10));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(Color.WHITE);
        
        // Quote label
        JLabel quoteLabel = new JLabel("<html><center>" + quote + "</center></html>", SwingConstants.CENTER);
        quoteLabel.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 18));
        quoteLabel.setForeground(Color.WHITE);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(quoteLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private double getTotalBudget() {
        double total = 0.0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT SUM(budget) AS total FROM itineraries WHERE user_id=(SELECT id FROM users WHERE username=?)")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
    
    private double getTotalExpenses() {
        double total = 0.0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT SUM(amount) AS total FROM expenses WHERE user_id=(SELECT id FROM users WHERE username=?)")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
    
    private String getUpcomingTrip() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT destination FROM itineraries WHERE user_id=(SELECT id FROM users WHERE username=?) " +
                     "ORDER BY id DESC LIMIT 1")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("destination");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addDestination() {
        JPanel inputPanel = new JPanel(new BorderLayout(0, 10));
        
        RoundedTextField destinationField = new RoundedTextField();
        destinationField.setPreferredSize(new Dimension(250, 35));
        
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setOpaque(false);
        JLabel label = new JLabel("Enter new destination:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelPanel.add(label);
        
        inputPanel.add(labelPanel, BorderLayout.NORTH);
        inputPanel.add(destinationField, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(frame, inputPanel, "Add New Destination", 
                                                 JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String destination = destinationField.getText().trim();
            if (!destination.isEmpty()) {
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                             "INSERT INTO itineraries (user_id, destination, budget) " +
                             "VALUES ((SELECT id FROM users WHERE username=?), ?, 0.0)")) {
                    stmt.setString(1, username);
                    stmt.setString(2, destination);
                    stmt.executeUpdate();
                    destinationListModel.addElement(destination);
                    
                    
                    SwingUtilities.invokeLater(() -> {
                        frame.dispose();
                        new ItineraryDashboard(username);
                    });
                } catch (SQLException e) {
                    showErrorMessage("Database Error: " + e.getMessage());
                }
            } else {
                showErrorMessage("Destination cannot be empty.");
            }
        }
    }

    private void editDestination(JList<String> destinationList) {
        int selectedIndex = destinationList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedDestination = destinationListModel.getElementAt(selectedIndex);
            int tripId = getTripId(selectedDestination);
            if (tripId == -1) {
                showErrorMessage("Error fetching trip details.");
                return;
            }

            JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            
            RoundedButton budgetButton = new RoundedButton("Manage Budget");
            budgetButton.setColors(primaryColor, secondaryColor, primaryColor.darker());
            
            RoundedButton expenseButton = new RoundedButton("Add Expense");
            expenseButton.setColors(new Color(243, 156, 18), new Color(241, 196, 15), new Color(211, 84, 0));
            
            RoundedButton statusButton = new RoundedButton("Change Status");
            statusButton.setColors(new Color(142, 68, 173), new Color(155, 89, 182), new Color(125, 60, 152));
            
            RoundedButton viewButton = new RoundedButton("View Budget");
            viewButton.setColors(accentColor, accentColor.brighter(), accentColor.darker());
            
            optionsPanel.add(budgetButton);
            optionsPanel.add(expenseButton);
            optionsPanel.add(statusButton);
            optionsPanel.add(viewButton);
            
            JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
            JLabel titleLabel = new JLabel("Edit: " + selectedDestination);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            mainPanel.add(optionsPanel, BorderLayout.CENTER);
            
            JDialog dialog = new JDialog(frame, "Edit Destination", true);
            dialog.setLayout(new BorderLayout());
            dialog.add(mainPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setSize(400, 250);
            
            budgetButton.addActionListener(e -> {
                dialog.dispose();
                manageBudget(tripId);
            });
            
            expenseButton.addActionListener(e -> {
                dialog.dispose();
                addExpense(tripId);
            });
            
            statusButton.addActionListener(e -> {
                dialog.dispose();
                markNotPlanned(tripId);
            });
            
            viewButton.addActionListener(e -> {
                dialog.dispose();
                showRemainingBudget(tripId);
            });
            
            dialog.setVisible(true);
        } else {
            showErrorMessage("Please select a destination to edit.");
        }
    }

    private void showRemainingBudget(int tripId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Fetch budget and destination name
            double budget = 0.0;
            String destination = "";
            try (PreparedStatement stmt = conn.prepareStatement("SELECT budget, destination FROM itineraries WHERE id=?")) {
                stmt.setInt(1, tripId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    budget = rs.getDouble("budget");
                    destination = rs.getString("destination");
                }
            }

            // Sum expenses
            double totalExpenses = 0.0;
            try (PreparedStatement stmt = conn.prepareStatement("SELECT SUM(amount) AS total FROM expenses WHERE trip_id=?")) {
                stmt.setInt(1, tripId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    totalExpenses = rs.getDouble("total");
                }
            }

            // Get expense breakdown
            List<String> expenseDetails = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement("SELECT expense_name, amount FROM expenses WHERE trip_id=? ORDER BY amount DESC")) {
                stmt.setInt(1, tripId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    expenseDetails.add(rs.getString("expense_name") + ": ₹" + rs.getDouble("amount"));
                }
            }

            // Calculate remaining budget
            double remainingBudget = budget - totalExpenses;
            
            // Create a custom panel for budget details
            JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            JLabel titleLabel = new JLabel("Budget Summary: " + destination);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JPanel summaryPanel = new JPanel(new GridLayout(3, 2, 10, 5));
            summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            
            JLabel budgetLabel = new JLabel("Total Budget:");
            budgetLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JLabel budgetValueLabel = new JLabel("₹" + String.format("%.2f", budget));
            budgetValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            JLabel expenseLabel = new JLabel("Total Expenses:");
            expenseLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JLabel expenseValueLabel = new JLabel("₹" + String.format("%.2f", totalExpenses));
            expenseValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            JLabel balanceLabel = new JLabel(remainingBudget >= 0 ? "Remaining:" : "Deficit:");
            balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JLabel balanceValueLabel = new JLabel("₹" + String.format("%.2f", Math.abs(remainingBudget)));
            balanceValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            if (remainingBudget >= 0) {
                balanceValueLabel.setForeground(new Color(46, 204, 113));
            } else {
                balanceValueLabel.setForeground(new Color(231, 76, 60));
            }
            
            summaryPanel.add(budgetLabel);
            summaryPanel.add(budgetValueLabel);
            summaryPanel.add(expenseLabel);
            summaryPanel.add(expenseValueLabel);
            summaryPanel.add(balanceLabel);
            summaryPanel.add(balanceValueLabel);
            
            JPanel detailsPanel = new JPanel(new BorderLayout());
            
            if (!expenseDetails.isEmpty()) {
                JLabel detailsLabel = new JLabel("Expense Details:");
                detailsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                
                DefaultListModel<String> expenseListModel = new DefaultListModel<>();
                for (String expense : expenseDetails) {
                    expenseListModel.addElement(expense);
                }
                
                JList<String> expenseList = new JList<>(expenseListModel);
                expenseList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                JScrollPane scrollPane = new JScrollPane(expenseList);
                scrollPane.setPreferredSize(new Dimension(300, 150));
                
                detailsPanel.add(detailsLabel, BorderLayout.NORTH);
                detailsPanel.add(scrollPane, BorderLayout.CENTER);
            }
            
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            mainPanel.add(summaryPanel, BorderLayout.CENTER);
            mainPanel.add(detailsPanel, BorderLayout.SOUTH);
            
            JDialog dialog = new JDialog(frame, "Budget Details", true);
            dialog.add(mainPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);

        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
        }
    }

    private void manageBudget(int tripId) {
        // Get current budget
        double currentBudget = 0.0;
        String destination = "";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT budget, destination FROM itineraries WHERE id=?")) {
            stmt.setInt(1, tripId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentBudget = rs.getDouble("budget");
                destination = rs.getString("destination");
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
            return;
        }
        
        JPanel inputPanel = new JPanel(new BorderLayout(0, 10));
        
        JLabel infoLabel = new JLabel("Current budget for " + destination + ": ₹" + currentBudget);
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        RoundedTextField budgetField = new RoundedTextField();
        budgetField.setText(String.valueOf(currentBudget));
        budgetField.setPreferredSize(new Dimension(250, 35));
        
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setOpaque(false);
        JLabel label = new JLabel("Enter new budget:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelPanel.add(label);
        
        inputPanel.add(infoLabel, BorderLayout.NORTH);
        inputPanel.add(labelPanel, BorderLayout.CENTER);
        inputPanel.add(budgetField, BorderLayout.SOUTH);
        
        int result = JOptionPane.showConfirmDialog(frame, inputPanel, "Update Budget", 
                                                 JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String newBudgetStr = budgetField.getText().trim();
            if (!newBudgetStr.isEmpty()) {
                try {
                    double newBudget = Double.parseDouble(newBudgetStr);
                    
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement("UPDATE itineraries SET budget=? WHERE id=?")) {
                        stmt.setDouble(1, newBudget);
                        stmt.setInt(2, tripId);
                        stmt.executeUpdate();
                        
                        // Show success message
                        JPanel successPanel = new JPanel(new BorderLayout());
                        JLabel successLabel = new JLabel("Budget updated successfully!");
                        successLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                        successLabel.setForeground(new Color(46, 204, 113));
                        successPanel.add(successLabel);
                        
                        JOptionPane.showMessageDialog(frame, successPanel, "Success", JOptionPane.PLAIN_MESSAGE);
                        
                        // Refresh dashboard
                        SwingUtilities.invokeLater(() -> {
                            frame.dispose();
                            new ItineraryDashboard(username);
                        });
                    } catch (SQLException e) {
                        showErrorMessage("Database Error: " + e.getMessage());
                    }
                } catch (NumberFormatException e) {
                    showErrorMessage("Invalid budget amount. Please enter a valid number.");
                }
            } else {
                showErrorMessage("Budget amount cannot be empty.");
            }
        }
    }

    private void addExpense(int tripId) {
        // Get destination name
        String destination = "";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT destination FROM itineraries WHERE id=?")) {
            stmt.setInt(1, tripId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                destination = rs.getString("destination");
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
            return;
        }
        
        JPanel inputPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Add Expense for " + destination);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JPanel namePanel = new JPanel(new BorderLayout(5, 0));
        JLabel nameLabel = new JLabel("Expense Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        RoundedTextField expenseNameField = new RoundedTextField();
        namePanel.add(nameLabel, BorderLayout.NORTH);
        namePanel.add(expenseNameField, BorderLayout.CENTER);
        
        JPanel amountPanel = new JPanel(new BorderLayout(5, 0));
        JLabel amountLabel = new JLabel("Amount (₹):");
        amountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        RoundedTextField expenseAmountField = new RoundedTextField();
        amountPanel.add(amountLabel, BorderLayout.NORTH);
        amountPanel.add(expenseAmountField, BorderLayout.CENTER);
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        fieldsPanel.add(namePanel);
        fieldsPanel.add(amountPanel);
        
        mainPanel.add(fieldsPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(frame, mainPanel, "Add Expense", 
                                                 JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String expenseName = expenseNameField.getText().trim();
            String expenseStr = expenseAmountField.getText().trim();

            if (!expenseName.isEmpty() && !expenseStr.isEmpty()) {
                try {
                    double expenseAmount = Double.parseDouble(expenseStr);
                    
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        int userId = -1;
                        try (PreparedStatement userStmt = conn.prepareStatement("SELECT id FROM users WHERE username=?")) {
                            userStmt.setString(1, username);
                            ResultSet rs = userStmt.executeQuery();
                            if (rs.next()) {
                                userId = rs.getInt("id");
                            } else {
                                showErrorMessage("User not found.");
                                return;
                            }
                        }

                        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO expenses (user_id, trip_id, expense_name, amount) VALUES (?, ?, ?, ?)")) {
                            stmt.setInt(1, userId);
                            stmt.setInt(2, tripId);
                            stmt.setString(3, expenseName);
                            stmt.setDouble(4, expenseAmount);
                            stmt.executeUpdate();
                            
                            // Show success message
                            JPanel successPanel = new JPanel(new BorderLayout());
                            JLabel successLabel = new JLabel("Expense added successfully!");
                            successLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                            successLabel.setForeground(new Color(46, 204, 113));
                            successPanel.add(successLabel);
                            
                            JOptionPane.showMessageDialog(frame, successPanel, "Success", JOptionPane.PLAIN_MESSAGE);
                            
                            // Refresh dashboard
                            SwingUtilities.invokeLater(() -> {
                                frame.dispose();
                                new ItineraryDashboard(username);
                            });
                        }
                    } catch (SQLException e) {
                        showErrorMessage("Database Error: " + e.getMessage());
                    }
                } catch (NumberFormatException e) {
                    showErrorMessage("Invalid expense amount. Please enter a valid number.");
                }
            } else {
                showErrorMessage("Expense name and amount cannot be empty.");
            }
        }
    }
    private void markNotPlanned(int tripId) {
        // Get destination name
        String destination = "";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT destination FROM itineraries WHERE id=?")) {
            stmt.setInt(1, tripId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                destination = rs.getString("destination");
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
            return;
        }
        
        String[] statuses = {"Not Yet Planned", "Planning", "Booked", "Completed", "Canceled"};
        
        JPanel statusPanel = new JPanel(new BorderLayout(0, 10));
        
        JLabel infoLabel = new JLabel("Change status for: " + destination);
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JComboBox<String> statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setPreferredSize(new Dimension(250, 35));
        statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        statusPanel.add(infoLabel, BorderLayout.NORTH);
        statusPanel.add(statusComboBox, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(frame, statusPanel, "Change Trip Status", 
                                                 JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String selectedStatus = (String) statusComboBox.getSelectedItem();
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE itineraries SET status=? WHERE id=?")) {
                stmt.setString(1, selectedStatus);
                stmt.setInt(2, tripId);
                stmt.executeUpdate();
                
                // Show success message
                JPanel successPanel = new JPanel(new BorderLayout());
                JLabel successLabel = new JLabel("Status updated to '" + selectedStatus + "' successfully!");
                successLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                successLabel.setForeground(new Color(46, 204, 113));
                successPanel.add(successLabel);
                
                JOptionPane.showMessageDialog(frame, successPanel, "Success", JOptionPane.PLAIN_MESSAGE);
            } catch (SQLException e) {
                showErrorMessage("Database Error: " + e.getMessage());
            }
        }
    }

    private int getTripId(String destination) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM itineraries WHERE user_id=(SELECT id FROM users WHERE username=?) AND LOWER(destination)=LOWER(?)")) {
            stmt.setString(1, username);
            stmt.setString(2, destination);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
        }
        return -1;
    }

    private void removeDestination(JList<String> destinationList) {
        int selectedIndex = destinationList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedDestination = destinationListModel.getElementAt(selectedIndex);
            
            // Confirmation dialog
            JPanel confirmPanel = new JPanel(new BorderLayout());
            JLabel confirmLabel = new JLabel("Are you sure you want to remove '" + selectedDestination + "'?");
            confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            confirmPanel.add(confirmLabel);
            
            int confirm = JOptionPane.showConfirmDialog(frame, confirmPanel, "Confirm Removal", 
                                                     JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    // First remove any expenses associated with this trip
                    int tripId = getTripId(selectedDestination);
                    if (tripId != -1) {
                        try (PreparedStatement expStmt = conn.prepareStatement("DELETE FROM expenses WHERE trip_id=?")) {
                            expStmt.setInt(1, tripId);
                            expStmt.executeUpdate();
                        }
                    }
                    
                    // Then remove the trip itself
                    try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM itineraries WHERE user_id=(SELECT id FROM users WHERE username=?) AND destination=?")) {
                        stmt.setString(1, username);
                        stmt.setString(2, selectedDestination);
                        stmt.executeUpdate();
                        destinationListModel.remove(selectedIndex);
                        
                        // Show success message
                        JPanel successPanel = new JPanel(new BorderLayout());
                        JLabel successLabel = new JLabel("Destination removed successfully!");
                        successLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                        successLabel.setForeground(new Color(46, 204, 113));
                        successPanel.add(successLabel);
                        
                        JOptionPane.showMessageDialog(frame, successPanel, "Success", JOptionPane.PLAIN_MESSAGE);
                        
                        // Refresh dashboard
                        SwingUtilities.invokeLater(() -> {
                            frame.dispose();
                            new ItineraryDashboard(username);
                        });
                    }
                } catch (SQLException e) {
                    showErrorMessage("Database Error: " + e.getMessage());
                }
            }
        } else {
            showErrorMessage("Please select a destination to remove.");
        }
    }
    
    private void exploreDestinations() {
        JPanel explorePanel = new JPanel(new BorderLayout(0, 15));
        explorePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Explore Popular Destinations");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        
        String[][] popularDestinations = {
            {"Goa", "Sun, Sand, and Beaches", "goa+beach"},
            {"Kerala", "God's Own Country", "kerala+backwaters"},
            {"Manali", "Mountain Paradise", "manali+mountains"},
            {"Jaipur", "The Pink City", "jaipur+palace"},
            {"Darjeeling", "Queen of Hills", "darjeeling+tea"},
            {"Andaman", "Island Paradise", "andaman+islands"},
            {"Varanasi", "The Spiritual Capital", "varanasi+ganges"},
            {"Udaipur", "City of Lakes", "udaipur+lake"},
            {"Rishikesh", "Adventure Hub", "rishikesh+river"},
            {"Agra", "Home of the Taj Mahal", "taj+mahal"}
        };
        
        // Create a custom panel for destinations with images
        JPanel destinationsPanel = new JPanel();
        destinationsPanel.setLayout(new BoxLayout(destinationsPanel, BoxLayout.Y_AXIS));
        
        for (String[] destination : popularDestinations) {
            JPanel destPanel = createDestinationPanel(destination[0], destination[1], destination[2]);
            destinationsPanel.add(destPanel);
            destinationsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // spacing between items
        }
        
        JScrollPane scrollPane = new JScrollPane(destinationsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        RoundedButton closeButton = new RoundedButton("Close");
        closeButton.setColors(new Color(189, 195, 199), new Color(189, 195, 199).brighter(), new Color(189, 195, 199).darker());
        closeButton.setPreferredSize(new Dimension(120, 40));
        
        buttonPanel.add(closeButton);
        
        explorePanel.add(titleLabel, BorderLayout.NORTH);
        explorePanel.add(scrollPane, BorderLayout.CENTER);
        explorePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JDialog dialog = new JDialog(frame, "Explore Destinations", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(explorePanel);
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(frame);
        
        closeButton.addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
    }

    private JPanel createDestinationPanel(String name, String description, String searchTerm) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        panel.setBackground(Color.WHITE);

        JLabel imageLabel = new JLabel("Loading image...");
        imageLabel.setPreferredSize(new Dimension(150, 100));
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Load image in a separate thread
        new Thread(() -> {
            try {
                // Generate a random nature image
                URL imageURL = new URL("https://picsum.photos/150/100");

                // Load and scale the image
                BufferedImage originalImage = ImageIO.read(imageURL);
                Image scaledImage = originalImage.getScaledInstance(150, 100, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(scaledImage);

                // Update UI on the Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    imageLabel.setIcon(icon);
                    imageLabel.setText(""); // Remove placeholder text
                    panel.revalidate();
                    panel.repaint();
                });
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                SwingUtilities.invokeLater(() -> imageLabel.setText("Image load failed."));
            }
        }).start();

        // Text information
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(100, 100, 100));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton addButton = new JButton("Add to My Destinations");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setBackground(new Color(46, 134, 222));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addButton.setMaximumSize(new Dimension(200, 30));

        // Add ActionListener to the button
        addButton.addActionListener(e -> {
            // Call the method to add the destination to the user's itinerary
            addDestinationToItinerary(name);
        });

        textPanel.add(nameLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(descLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        textPanel.add(addButton);

        panel.add(imageLabel, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    // Method to add destination to the user's itinerary
    private void addDestinationToItinerary(String destination) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if the destination already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT id FROM itineraries WHERE user_id=(SELECT id FROM users WHERE username=?) AND destination=?")) {
                checkStmt.setString(1, username);
                checkStmt.setString(2, destination);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    showErrorMessage("Destination already exists in your itinerary.");
                    return;
                }
            }

            // Insert the new destination
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO itineraries (user_id, destination, budget) " +
                    "VALUES ((SELECT id FROM users WHERE username=?), ?, 0.0)")) {
                stmt.setString(1, username);
                stmt.setString(2, destination);
                stmt.executeUpdate();

                // Show success message
                JPanel successPanel = new JPanel(new BorderLayout());
                JLabel successLabel = new JLabel("Destination added successfully!");
                successLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                successLabel.setForeground(new Color(46, 204, 113));
                successPanel.add(successLabel);

                JOptionPane.showMessageDialog(frame, successPanel, "Success", JOptionPane.PLAIN_MESSAGE);

                // Refresh the destination list
                destinationListModel.addElement(destination);
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
        }
    }
    
    private void logout() {
        // Confirmation dialog
        JPanel confirmPanel = new JPanel(new BorderLayout());
        JLabel confirmLabel = new JLabel("Are you sure you want to logout?");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmPanel.add(confirmLabel);
        
        int confirm = JOptionPane.showConfirmDialog(frame, confirmPanel, "Confirm Logout", 
                                                 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            frame.dispose();
            SwingUtilities.invokeLater(LoginUI::new);
        }
    }

    private void loadItinerary() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT destination FROM itineraries WHERE user_id=(SELECT id FROM users WHERE username=?) ORDER BY id DESC")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                destinationListModel.addElement(rs.getString("destination"));
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error: " + e.getMessage());
        }
    }
    
    private void showErrorMessage(String message) {
        JPanel errorPanel = new JPanel(new BorderLayout());
        JLabel errorLabel = new JLabel(message);
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        errorLabel.setForeground(warningColor);
        errorPanel.add(errorLabel);
        
        JOptionPane.showMessageDialog(frame, errorPanel, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Inner class for rounded text field
    private class RoundedTextField extends JTextField {
        public RoundedTextField() {
            setOpaque(false);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            g2.setColor(new Color(189, 195, 199));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            super.paintComponent(g);
            g2.dispose();
        }
    }
    
    // Inner class for rounded button
    private class RoundedButton extends JButton {
        private Color baseColor = new Color(41, 128, 185);
        private Color hoverColor = new Color(52, 152, 219);
        private Color pressedColor = new Color(36, 113, 163);
        private boolean isMouseOver = false;
        private boolean isMousePressed = false;
        
        public RoundedButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isMouseOver = true;
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isMouseOver = false;
                    repaint();
                }
                
                @Override
                public void mousePressed(MouseEvent e) {
                    isMousePressed = true;
                    repaint();
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    isMousePressed = false;
                    repaint();
                }
            });
        }
        
        public void setColors(Color baseColor, Color hoverColor, Color pressedColor) {
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;
            this.pressedColor = pressedColor;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (isMousePressed) {
                g2.setColor(pressedColor);
            } else if (isMouseOver) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(baseColor);
            }
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            
            FontMetrics fm = g2.getFontMetrics();
            Rectangle textRect = fm.getStringBounds(getText(), g2).getBounds();
            int x = (getWidth() - textRect.width) / 2;
            int y = (getHeight() - textRect.height) / 2 + fm.getAscent();
            
            g2.setColor(Color.WHITE);
            g2.setFont(getFont());
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }
    
    // Inner class for background panel with image
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        
        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = ImageIO.read(new File(imagePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}