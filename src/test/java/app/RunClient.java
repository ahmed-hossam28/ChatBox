package app;

import org.example.chatbox.File.FileReceiver;
import org.example.chatbox.Message.MessageReceiver;

import javax.swing.*;

public class RunClient {

   static void messageThread(Client chatApp2){
        new Thread(() -> {
            while (true) {
                MessageReceiver messageReceiver = null;
                try {
                    messageReceiver = new MessageReceiver(chatApp2.messageSocketHandler.getBufferedReader());
                }catch (NullPointerException e){
                    continue;
                }
                // Receive message
                while(true) {
                    if(messageReceiver.getBufferedReader() == null)break;
                    if (!messageReceiver.receive()) {
                        break;
                    }
                    //System.out.println(messageReceiver.getMessage());
                    // Update GUI with received message
                    MessageReceiver finalMessageReceiver = messageReceiver;
                    SwingUtilities.invokeLater(() -> {
                        chatApp2.addMessage("Server", finalMessageReceiver.getMessage(), false);
                    });
                }
            }



        }).start();
    }
   static void fileThread(Client chatApp2){
        new Thread(()->{
            FileReceiver fileReceiver = null;
           try {
              fileReceiver  = new FileReceiver(chatApp2.fileSocketHandler.getInputStream());
               fileReceiver.start();
           }catch (NullPointerException ex){
               System.err.println(ex.getMessage());
           }
            while(true){
                //for reconnection
                try {
                    fileReceiver  = new FileReceiver(chatApp2.fileSocketHandler.getInputStream());
                    fileReceiver.start();
                }catch (NullPointerException ex){
                    continue;
                }
                while(true) {
                    if (fileReceiver.receive())
                        JOptionPane.showMessageDialog(chatApp2, fileReceiver.getFilename() + "Received!");
                    else break;
                }
            }
        }).start();
    }

    static void runApp(String username){
        SwingUtilities.invokeLater(() -> {
            Client chatApp2 = new Client(username);
            chatApp2.setVisible(true);
            chatApp2.connect();
            messageThread(chatApp2);
            fileThread(chatApp2);

        });
    }
    public static void main(String[] args) {
        runApp("@AhmadHossam");
    }
}
