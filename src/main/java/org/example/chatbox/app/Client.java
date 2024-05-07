package org.example.chatbox.app;
import org.example.chatbox.Message.MessageReceiver;
import org.example.chatbox.Message.MessageSender;
import org.example.chatbox.sockets.SocketHandler;
import org.example.chatbox.File.FileSender;
import org.example.chatbox.File.FileReceiver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame {
    private JPanel chatPanel;
    private JTextField messageField;
    BufferedWriter bufferedWriter;
    OutputStream fileOutputStream;
    public Client()  {
        setTitle("Chat App2");
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

        add(inputPanel, BorderLayout.SOUTH);
    }
    public boolean sendMsg(){
        String message = messageField.getText();
        if (!message.isEmpty()) {
            addMessage("You", message, true);
            try {
                MessageSender messageSender = new MessageSender(this.bufferedWriter);
                messageSender.setMessage(messageField.getText());

                if(!messageSender.send()){
                    addErrorMessage("Connection failed: Unable to connect to server.");
                    return false;
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            messageField.setText("");
        }

        return true ;
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
        fileSender.send();
        System.out.println("Sending file: " + file.getName());
    }
    public void setBufferedWriter(BufferedWriter bufferedWriter){
        this.bufferedWriter = bufferedWriter;
    }
    public void setFileOutputStream(OutputStream outputStream){this.fileOutputStream = outputStream;}


    public static void testConnection(Client chatApp2){
        boolean flag = true;
        for(int i = 0;i<4;++i)
            if(!chatApp2.sendMsg())
                flag = false;

        if(flag)
            chatApp2.addSuccessMessage("Connection successful: Connected to server.");
    }
    static void connect(SocketHandler client){

    }
    static void runApp(String username){
        SwingUtilities.invokeLater(() -> {
            Client chatApp2 = new Client();
            chatApp2.setVisible(true);
            SocketHandler client = null;
            try {
                client = new SocketHandler(12345);
                //client = new SocketHandler(new Socket("0.tcp.eu.ngrok.io",19657));
                client.send(username);
            } catch (IOException e) {
                chatApp2.addErrorMessage("Connection failed: Unable to connect to server.");
            }
            if(client!=null)
              chatApp2.setBufferedWriter(client.getBufferedWriter());

            testConnection(chatApp2);

            final SocketHandler finalClient = client;
            // MessageThread
            new Thread(() -> {
                BufferedReader bufferedReader = finalClient.getBufferedReader();
                MessageReceiver messageReceiver = new MessageReceiver(bufferedReader);
                messageReceiver.start();
                while (true) {
                    // Receive message
                    if(!messageReceiver.receive()) {
                        break;

                    }
                    //System.out.println(messageReceiver.getMessage());
                    // Update GUI with received message
                    SwingUtilities.invokeLater(() -> {
                        chatApp2.addMessage("Server", messageReceiver.getMessage(), false);
                    });
                }



            }).start();

            SocketHandler fileSocket = null;
            try {
                //fileSocket = new SocketHandler(12346);
                fileSocket = new SocketHandler(new Socket("4.tcp.eu.ngrok.io",16732));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            SocketHandler finalFileSocket = fileSocket;
            chatApp2.setFileOutputStream(finalFileSocket.getOutputStream());
            //file receiver thread
            new Thread(()->{
                FileReceiver fileReceiver = new FileReceiver(finalFileSocket.getInputStream());
                fileReceiver.start();
                while(true){
                    if(fileReceiver.receive())
                        JOptionPane.showMessageDialog(chatApp2,fileReceiver.getFilename()+"Received!");
                    else break;
                }
            }).start();

        });
    }
    public static void main(String[] args) {
        runApp("AhmedHossam");
    }

}
