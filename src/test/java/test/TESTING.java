package test;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TESTING extends JFrame {
    public JTextPane chatArea;
    public JTextField messageField;
    public JButton sendButton;
    public String username;

    TESTING(){
        this.username = username;
        setTitle("ChatBox - " + username);
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextPane();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        bottomPanel.add(messageField, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TESTING chatBox = new TESTING();
            chatBox.setVisible(true);

            try {
                // Read content from external HTML file
                String content = new String(Files.readAllBytes(Paths.get("chat_content.html")));
                chatBox.chatArea.setContentType("text/html");
                chatBox.chatArea.setText(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
