package app;

import org.example.chatbox.Message.MessageSender;
import org.example.chatbox.File.FileSender;
import org.example.chatbox.pair.Pair;
import org.example.chatbox.sockets.ServerSocketHandler;
import org.example.chatbox.sockets.SocketHandler;
import org.example.chatbox.user.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class ChatServer extends JFrame {
    ArrayList<Pair<User,Boolean>>users;
    ArrayList<Pair<SocketHandler,Boolean>>userFileConnections;
    private JPanel chatPanel;
    private JTextField messageField;
    ServerSocketHandler messageServer;
    ServerSocketHandler fileServer;
    public ChatServer() {
        users = new ArrayList<>();
        userFileConnections = new ArrayList<>();
        try {
            messageServer = new ServerSocketHandler();
            fileServer = new ServerSocketHandler(12346);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setTitle("Server");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the chat panel
        chatPanel = new JPanel();
        chatPanel.setLayout(new GridBagLayout());
        chatPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Create the message input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        messageField = new JTextField();
        messageField.setPreferredSize(new Dimension(300, 30));
        inputPanel.add(messageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMsg());

        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    sendMsg();
                }
            }

        });
        inputPanel.add(sendButton, BorderLayout.EAST);

        JButton fileButton = new JButton("Send File");
        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(ChatServer.this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                sendFile(selectedFile);
            }
        });
        inputPanel.add(fileButton, BorderLayout.WEST);

        add(inputPanel, BorderLayout.SOUTH);
    }
    void sendMsg(){
        String message = messageField.getText();
        if (!message.isEmpty()) {
            addMessage("You", message, true);

            sendToMultipleUsers();
            //sendToSingleUser();

            messageField.setText("");
        }
    }

    void sendToMultipleUsers(){
        try {
           for(var user:users) {
               MessageSender messageSender = new MessageSender(user.first.getMessageSocketHandler().getBufferedWriter());
               messageSender.setMessage(messageField.getText());

              if(user.second) {
                  if (!messageSender.send()) {
                      System.out.println("[-] connection "+user.first.getName()+" at addr" +user.first.getMessageSocketHandler().getSocket() + " has disconnected!");
                      user.second = false;
                  }
              }
           }

           users.removeIf(user-> !user.second);

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    void sendToSingleUser(){
        MessageSender messageSender = null;
        try {
            messageSender = new MessageSender(messageServer.getBufferedWriter());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        messageSender.setMessage(messageField.getText());
        messageSender.send();
    }
    public void addMessage(String sender, String message, boolean isSender) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messagePanel.setBackground(isSender ? new Color(173, 216, 230) : Color.LIGHT_GRAY);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel nameLabel = new JLabel(sender);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(Color.BLACK);
        messagePanel.add(nameLabel, BorderLayout.NORTH);

        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBackground(isSender ?new Color(173, 216, 230) : Color.LIGHT_GRAY);
        messagePanel.add(messageArea, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.weightx = 1.0;
        gbc.anchor = isSender ? GridBagConstraints.EAST : GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        chatPanel.add(messagePanel, gbc);

        revalidate();
        repaint();
    }

    public void sendFile(File file) {
        sendToMultipleUsers(file);
    }
    public void sendToSingleUser(File file){
        FileSender fileSender = new FileSender(file, fileServer.getOutputStream());
        if(!fileSender.send())
            System.err.println("err sending file");
        System.out.println("Sending file: " + file.getName());
    }
    public void sendToMultipleUsers(File file){
        for(var usersFileHandler:userFileConnections){
            FileSender fileSender = new FileSender(file, usersFileHandler.first.getOutputStream());
            if(!fileSender.send()) {
                System.err.println("err sending file");
                System.out.println("[-]file server for "+usersFileHandler.first.getSocket());
                usersFileHandler.second=false;
            }
            System.out.println("Sending file: " + file.getName());
        }

        userFileConnections.removeIf(user->!user.second);
    }


}
