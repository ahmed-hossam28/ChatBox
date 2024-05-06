package app;

import Message.MessageReceiver;
import Message.MessageSender;
import File.FileReceiver;
import File.FileSender;
import org.example.chatbox.sockets.ServerSocketHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ChatApp extends JFrame {
    private JPanel chatPanel;
    private JTextField messageField;
    ServerSocketHandler messageServer;
    ServerSocketHandler fileServer;
    public ChatApp() {
        try {
            messageServer = new ServerSocketHandler();
            fileServer = new ServerSocketHandler(12346);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setTitle("Chat App1");
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
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    sendMsg();
                }
            }

        });
        inputPanel.add(sendButton, BorderLayout.EAST);

        JButton fileButton = new JButton("Send File");
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(ChatApp.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    sendFile(selectedFile);
                }
            }
        });
        inputPanel.add(fileButton, BorderLayout.WEST);

        add(inputPanel, BorderLayout.SOUTH);
    }
    void sendMsg(){
        String message = messageField.getText();
        if (!message.isEmpty()) {
            addMessage("You", message, true);
            try {
                MessageSender messageSender = new MessageSender(messageServer.getBufferedWriter());
                messageSender.setMessage(messageField.getText());
                messageSender.send();
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

    public void sendFile(File file) {

        FileSender fileSender = new FileSender(file, fileServer.getOutputStream());
        fileSender.send();
        System.out.println("Sending file: " + file.getName());
    }


   static void runApp(){
       SwingUtilities.invokeLater(() -> {
           ChatApp chatApp = new ChatApp();
           chatApp.setVisible(true);

           //MessageThread
           new Thread(()-> {
               try {
                   while (true) {
                       chatApp.messageServer.start();
                       new Thread(() -> {
                           BufferedReader bufferedReader = chatApp.messageServer.getBufferedReader();
                           MessageReceiver messageReceiver = new MessageReceiver(bufferedReader);
                           while (true) {
                               // Receive message
                               if(!messageReceiver.receive())
                                   break;
                              // System.out.println(messageReceiver.getMessage());
                               // Update GUI with received message
                               SwingUtilities.invokeLater(() -> {
                                   chatApp.addMessage("Friend", messageReceiver.getMessage(), false);
                               });
                           }
                       }).start();
                   }
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
           }).start();


           //FileThread
           new Thread(()->{
               while(true){
                   try {
                       System.out.print("FILE:");
                       chatApp.fileServer.start();

                   } catch (IOException e) {
                       System.err.println("file :"+e.getMessage());
                   }

                   //receiving files thread
                   new Thread(()->{
                       FileReceiver fileReceiver = new FileReceiver(chatApp.fileServer.getInputStream());
                       fileReceiver.start();
                       while(true){
                           if(fileReceiver.receive())
                               JOptionPane.showMessageDialog(chatApp,fileReceiver.getFilename()+"Received!");
                           else break;
                       }
                   }).start();

               }
           }).start();

       });
   }
    public static void main(String[] args) {
        runApp();
    }

}
