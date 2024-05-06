import org.example.chatbox.app.SocketHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ChatApp2 extends JFrame {
    private JPanel chatPanel;
    private JTextField messageField;
    BufferedWriter bufferedWriter;

    public ChatApp2()  {
        setTitle("Chat App");
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
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(ChatApp2.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    sendFile(selectedFile);
                }
            }
        });
        inputPanel.add(fileButton, BorderLayout.WEST);

        add(inputPanel, BorderLayout.SOUTH);
    }
    public void sendMsg(){
     String message = messageField.getText();
     if (!message.isEmpty()) {
         addMessage("You", message, true);
         try {
             MessageSender messageSender = new MessageSender(this.bufferedWriter);
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

    private void sendFile(File file) {
        System.out.println("Sending file: " + file.getName());
    }
   public void setBufferedWriter(BufferedWriter bufferedWriter){
        this.bufferedWriter = bufferedWriter;
   }

    static void runApp(){
        SwingUtilities.invokeLater(() -> {
            ChatApp2 chatApp2 = new ChatApp2();
            chatApp2.setVisible(true);
            SocketHandler client = null;
            try {
                client = new SocketHandler(12345);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            chatApp2.setBufferedWriter(client.getBufferedWriter());

            // MessageThread
          final SocketHandler finalClient = client;
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
                        chatApp2.addMessage("Friend", messageReceiver.getMessage(), false);
                    });
                }



            }).start();

            SocketHandler fileSocket = null;
            try {
                fileSocket = new SocketHandler(12346);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            SocketHandler finalFileSocket = fileSocket;
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
        runApp();
    }

}
