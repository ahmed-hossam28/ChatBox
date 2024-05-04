package org.example.chatbox.app;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Scanner;

public class Client extends JFrame {
   static private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private String username;
    SocketHandler socketHandler;


    public Client(String username) throws IOException {
        this.username = username;
        socketHandler = new SocketHandler();
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
                    sendMessage();
                }
            }
        });
        bottomPanel.add(messageField, BorderLayout.CENTER);


        Thread thread = new Thread(()-> {
            while (true) {
                String msg = null;
                try {
                   // chatArea.append("works");
                    System.out.println("waiting for data");
                    msg = socketHandler.receive();
                    System.out.println("data receive");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                chatArea.append("Server :" + msg+'\n');
            }
        });
        thread.start();

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });


        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

    }

    private void sendMessage() {
        String message = messageField.getText();
        // Process the message (e.g., send it to the server)
        chatArea.append(username + ": " + message + "\n");

        messageField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new Client("Client").setVisible(true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        String msg;
        Scanner sc = new Scanner(System.in);
        while(true) {
            msg = sc.nextLine();
            chatArea.append("hello\n");
        }
    }
}
