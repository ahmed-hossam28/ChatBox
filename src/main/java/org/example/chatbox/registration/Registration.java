package org.example.chatbox.registration;

import org.example.chatbox.database.DBConnection;
import org.example.chatbox.database.MySQL;
import org.example.chatbox.login.Login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.*;

public class Registration extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JButton loginButton; // Added login button
    private Connection conn;

    public Registration() {
        try {
            connectToDatabase();
        }catch (SQLException e){
            System.err.println("Failed  to connect to  database");
        }

        setTitle("Chat App - Registration");
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

        JLabel titleLabel = new JLabel("Chat App - Registration");
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
        registerButton = new JButton("Register");
        registerButton.setBackground(new Color(66, 139, 202));
        registerButton.setForeground(Color.BLACK);
        backgroundPanel.add(registerButton, gbc);

        // Add action listener for register button
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                try {
                    if (registerUser(username, password)) {
                        JOptionPane.showMessageDialog(Registration.this, "Registration successful. You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(Registration.this, "Registration failed. Please try again.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Registration.this, "Error: " + ex.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        gbc.gridy++;
        loginButton = new JButton("Login");
        backgroundPanel.add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Login().setVisible(true);
            }
        });

        // Add the background panel to the frame
        add(backgroundPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);

        // Set the frame visible
        setVisible(true);
    }

    private boolean registerUser(String username, String password) throws SQLException {
        String query = "INSERT INTO users (name, pass) VALUES (?, ?)";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, username);
        statement.setString(2, password);
        int rowsInserted = statement.executeUpdate();
        return rowsInserted > 0;
    }

    private void connectToDatabase() throws SQLException {
        DBConnection mySQL = new MySQL("root", System.getenv("DB_PASSWORD"), "omda");
        conn = mySQL.connect();
    }

}
