package login;

import org.example.chatbox.app.RunClient;
import org.example.chatbox.database.DBConnection;
import org.example.chatbox.database.MySQL;
import org.example.chatbox.registration.Registration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton; // Added registration button
    private Connection conn;

    public Login() {

        setTitle("Chat App - Login");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imageIcon = new ImageIcon("background.jpg");
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Chat App - Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        backgroundPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        backgroundPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(50);
        backgroundPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        backgroundPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(50);
        backgroundPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(66, 139, 202));
        loginButton.setForeground(Color.BLACK);
        backgroundPanel.add(loginButton, gbc);

        // Add action listener for login button
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                try {
                    if (authenticateUser(username, password)) {
                        openChatApp(username);
                    } else {
                        JOptionPane.showMessageDialog(Login.this, "Invalid username or password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Add registration button
        gbc.gridy++;
        registerButton = new JButton("Register");
        backgroundPanel.add(registerButton, gbc);

        // Add action listener for registration button
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the login window
                Registration registrationPage = new Registration();
                registrationPage.setVisible(true); // Open the registration page
            }
        });

        // Add the background panel to the frame
        add(backgroundPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);

        // Set the frame visible
        setVisible(true);
    }

    private boolean authenticateUser(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE name = ? AND pass = ?";
        PreparedStatement statement = conn.prepareStatement(query); // for security
        statement.setString(1, username);
        statement.setString(2, password);
        ResultSet resultSet = statement.executeQuery();

        return resultSet.next(); // true of false //one record
    }

    private void connectToDatabase() throws SQLException {
        DBConnection mySQL = new MySQL("root", System.getenv("DB_PASSWORD"), "omda");
        conn = mySQL.connect();
    }

    private void openChatApp(String username) {
        dispose(); // Close the login window
        RunClient.runApp(username);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Login loginApp = new Login();
                loginApp.connectToDatabase();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
