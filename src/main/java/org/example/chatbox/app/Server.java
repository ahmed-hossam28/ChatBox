package org.example.chatbox.app;

import org.example.chatbox.sockets.ServerSocketHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

public class Server extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private String username;
    private ServerSocketHandler server;
    ArrayList<String>messages;
    public Server(String username) {
        try {
            server = new ServerSocketHandler(); //solve it later
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

        Thread thread = new Thread(()->{
            try {
                server.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        messages = new ArrayList<>();
        this.username = username;
        setTitle("ChatBox - " + username);
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        // Add key listener to the message field
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        sendMessage();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        bottomPanel.add(messageField, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    sendMessage();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void sendMessage() throws IOException {
        String message = messageField.getText();
        messages.add(message);
        // Process the message (e.g., send it to the server)
        chatArea.append(username + ": " + message + "\n");
        messageField.setText("");


        //send to client
        //for (String s : messages)

        System.out.println("message sent");

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Server("Server").setVisible(true);
            }
        });
    }
}
