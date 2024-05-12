package org.example.chatbox.app;

import org.example.chatbox.Message.MessageSender;
import org.example.chatbox.sockets.SocketHandler;
import org.example.chatbox.File.FileSender;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame {
    public  boolean connectionStatus = true;
    public  boolean proxy  = false;
    private JPanel chatPanel;
    private JTextField messageField;
    public String username;
    public SocketHandler messageSocketHandler;
    public SocketHandler fileSocketHandler;
    BufferedWriter bufferedWriter;
    OutputStream fileOutputStream;
    String proxyHost1;
    int proxyPort1;
    String proxyHost2;
    int proxyPort2;
    public Client(String username)  {
        this.username = username;
        setTitle("ChatBox@"+username);
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
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg();
            }
        });
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMsg();
                }
            }

        });
        inputPanel.add(sendButton, BorderLayout.EAST);

        JButton fileButton = new JButton("Send File");
        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(Client.this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                sendFile(selectedFile);
            }
        });
        inputPanel.add(fileButton, BorderLayout.WEST);
        JButton reconnectButton = new JButton("Reconnect");

        if(!proxy)
            reconnectButton.addActionListener(e -> reconnect());
        else
            reconnectButton.addActionListener(e -> reconnectUsingProxy());

        inputPanel.add(reconnectButton, BorderLayout.NORTH);

        add(inputPanel, BorderLayout.SOUTH);
    }

    public void setProxyConf(String proxyHost1,int proxyPort1,String proxyHost2,int proxyPort2){
        this.proxyHost1 = proxyHost1;
        this.proxyPort1 = proxyPort1;
        this.proxyHost2 = proxyHost2;
        this.proxyPort2 = proxyPort2;
    }


    public void initDataCommunication(){
        this.bufferedWriter = messageSocketHandler.getBufferedWriter();
        this.fileOutputStream = fileSocketHandler.getOutputStream();
    }
    public void setSocketHandlers(Socket socket1,Socket socket2) throws IOException {
        messageSocketHandler = new SocketHandler(socket1);
        fileSocketHandler = new SocketHandler(socket2);
        initDataCommunication();

    }
    public void setSocketHandlers(SocketHandler socketHandler1,SocketHandler socketHandler2){
        messageSocketHandler = socketHandler1;
        fileSocketHandler = socketHandler2;
        initDataCommunication();

    }
    public void setMessageSocketHandler(Socket socket) throws IOException {
        this.messageSocketHandler = new SocketHandler(socket);
        this.bufferedWriter = messageSocketHandler.getBufferedWriter();
    }
    public void setMessageSocketHandler(SocketHandler socketHandler) {
        this.messageSocketHandler = socketHandler;
        this.bufferedWriter = messageSocketHandler.getBufferedWriter();
    }
    public void setFileSocketHandler(Socket socket) throws IOException {
        this.fileSocketHandler = new SocketHandler(socket);
        this.fileOutputStream = fileSocketHandler.getOutputStream();
    }
    public void setFileSocketHandler(SocketHandler socketHandler) {
        this.fileSocketHandler = socketHandler;
        this.fileOutputStream = fileSocketHandler.getOutputStream();
    }

    public void sendMsg(){
        String message = messageField.getText();
        if (!message.isEmpty()) {
            addMessage("You", message, true);
            try {
                MessageSender messageSender = new MessageSender(this.bufferedWriter);
                messageSender.setMessage(messageField.getText());

                if(!messageSender.send()){
                    addErrorMessage("Connection failed: Unable to connect to server.");
                    connectionStatus = false;
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            messageField.setText("");
        }

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

    public void addSuccessMessage(String successMessage) {
        JPanel successPanel = new JPanel();
        successPanel.setLayout(new BorderLayout());
        successPanel.setBackground(new Color(144, 238, 144)); // Light green color
        successPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel successLabel = new JLabel(successMessage);
        successLabel.setFont(new Font("Arial", Font.BOLD, 14));
        successLabel.setForeground(Color.BLACK); // Set text color for success messages
        successPanel.add(successLabel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        chatPanel.add(successPanel, gbc);

        revalidate();
        repaint();
    }
    public void addErrorMessage(String errorMessage) {
        JPanel errorPanel = new JPanel();
        errorPanel.setLayout(new BorderLayout());
        errorPanel.setBackground(Color.RED); // Set background color for error messages
        errorPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel errorLabel = new JLabel(errorMessage);
        errorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        errorLabel.setForeground(Color.WHITE); // Set text color for error messages
        errorPanel.add(errorLabel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        chatPanel.add(errorPanel, gbc);

        revalidate();
        repaint();
    }

    private void sendFile(File file) {
        FileSender fileSender = new FileSender(file,fileOutputStream);
        if(!fileSender.send()) {
            addErrorMessage("Connection failed: Unable to connect to server.");
            connectionStatus = false;
        }
        else System.out.println("Sending file: " + file.getName());
    }

    public void setBufferedWriter(BufferedWriter bufferedWriter){
        this.bufferedWriter = bufferedWriter;
    }
    public void setFileOutputStream(OutputStream outputStream){this.fileOutputStream = outputStream;}

    public void reconnect() {
        try {
            messageSocketHandler = new SocketHandler(12345);
            fileSocketHandler = new SocketHandler(12346);
            messageSocketHandler.send(this.username);
            bufferedWriter = messageSocketHandler.getBufferedWriter();
            fileOutputStream = fileSocketHandler.getOutputStream();
            addSuccessMessage("Reconnection successful: Connected to server.");
            System.out.println("Reconnection successful: Connected to server.");
            connectionStatus = true;
        } catch (IOException e) {
            addErrorMessage("Reconnection failed: Unable to connect to server.");
            connectionStatus = false;
        }
    }
    public void reconnect(Socket socket1,Socket socket2) {
        try {
            messageSocketHandler = new SocketHandler(socket1);
            fileSocketHandler = new SocketHandler(socket2);
            messageSocketHandler.send(this.username);
            bufferedWriter = messageSocketHandler.getBufferedWriter();
            fileOutputStream = fileSocketHandler.getOutputStream();
            addSuccessMessage("Reconnection successful: Connected to server.");
            connectionStatus = true;
        } catch (IOException e) {
            addErrorMessage("Reconnection failed: Unable to connect to server.");
            connectionStatus = false;
        }
    }
    public void reconnectUsingProxy() {
        try {
            reconnect(new Socket(proxyHost1, proxyPort1), new Socket(proxyHost2, proxyPort2));
        }catch (IOException ex){
            addErrorMessage("Reconnection failed: Unable to connect to server.");
            connectionStatus = false;
        }
    }

    public void reconnectInBackground(boolean proxy){
        //without error message to the user
        if(proxy){
            try {
                reconnect(new Socket(proxyHost1, proxyPort1), new Socket(proxyHost2, proxyPort2));
            }catch (IOException ex){
                connectionStatus = false;
            }
        }
        else {
            try {
                messageSocketHandler = new SocketHandler(12345);
                fileSocketHandler = new SocketHandler(12346);
                messageSocketHandler.send(this.username);
                bufferedWriter = messageSocketHandler.getBufferedWriter();
                fileOutputStream = fileSocketHandler.getOutputStream();
                addSuccessMessage("Reconnection successful: Connected to server.");
                System.out.println("Reconnection successful: Connected to server.");
                connectionStatus = true;
            } catch (IOException e) {
                connectionStatus = false;
            }
        }
    }
    public void connect(){
        try {
            messageSocketHandler = new SocketHandler(12345);//connection1
            fileSocketHandler = new SocketHandler(12346);//connection2
            messageSocketHandler.send(this.username);
            bufferedWriter = messageSocketHandler.getBufferedWriter();
            fileOutputStream = fileSocketHandler.getOutputStream();
            addSuccessMessage("Connection successful: Connected to server.");
            connectionStatus = true ;
        } catch (IOException e) {
            addErrorMessage("Connection failed: Unable to connect to server.");
            connectionStatus = false;
        }
    }
    public void connect(Socket socket1, Socket socket2){
        try {
            messageSocketHandler = new SocketHandler(socket1);
            fileSocketHandler = new SocketHandler(socket2);
            messageSocketHandler.send(this.username);
            bufferedWriter = messageSocketHandler.getBufferedWriter();
            fileOutputStream = fileSocketHandler.getOutputStream();
            addSuccessMessage("Connection successful: Connected to server.");
            connectionStatus = true;
        } catch (IOException e) {
            addErrorMessage("Connection failed: Unable to connect to server.");
            connectionStatus = false;
        }
    }
    public void connect(int port1, int port2){
        try {
            messageSocketHandler = new SocketHandler(port1);
            fileSocketHandler = new SocketHandler(port2);
            messageSocketHandler.send(this.username);
            bufferedWriter = messageSocketHandler.getBufferedWriter();
            fileOutputStream = fileSocketHandler.getOutputStream();
            addSuccessMessage("Connection successful: Connected to server.");
            connectionStatus = true;
        } catch (IOException e) {
            addErrorMessage("Connection failed: Unable to connect to server.");
            connectionStatus = false;
        }
    }

    public void error(){
        addErrorMessage("Connection failed: Unable to connect to server.");
        connectionStatus = false;
    }
    public void setProxy(boolean proxy){
        this.proxy = proxy;
    }


}
