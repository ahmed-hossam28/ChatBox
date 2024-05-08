package app;

import org.example.chatbox.File.FileReceiver;
import org.example.chatbox.Message.MessageReceiver;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunClient {
   static boolean proxy = false;
   static ExecutorService executorService = Executors.newFixedThreadPool(10);
   static void messageThread(Client chatApp2){
        executorService.execute(() -> {
            while (true) {
                if(!chatApp2.connectionStatus) {
                    System.out.println("outer loop");
                    continue;
                }
                MessageReceiver messageReceiver = null;
                try {
                    messageReceiver = new MessageReceiver(chatApp2.messageSocketHandler.getBufferedReader());
                }catch (NullPointerException e){
                    System.out.println("it is null ");
                    continue;
                }
                // Receive message
                while(true) {
                   // System.out.println("working inner loop");
                    if(messageReceiver.getBufferedReader() == null) {
                        System.out.println("problem is here");
                        break;
                    }
                    if (!messageReceiver.receive()) {
                        SwingUtilities.invokeLater(chatApp2::error);
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



        });
    }
   static void fileThread(Client chatApp2){
        new Thread(()->{
            FileReceiver fileReceiver = null;
            while(true){
                if(!chatApp2.connectionStatus) {
                    System.out.println(" ");
                    continue;
                }
                //for reconnection
                try {
                    fileReceiver  = new FileReceiver(chatApp2.fileSocketHandler.getInputStream());
                    fileReceiver.start();
                }catch (NullPointerException ex){
                    continue;
                }
                while(true) {
                    System.out.println("works inner loop");
                    if (fileReceiver.receive())
                        JOptionPane.showMessageDialog(chatApp2, fileReceiver.getFilename() + " Received!");
                    else {
                        System.out.println("Connection Broken!");
                        chatApp2.connectionStatus = false;
                        SwingUtilities.invokeLater(chatApp2::error);
                        break;
                    }
                }
            }
        }).start();
    }
   static void connectUsingProxy(Client chatApp2){
           try {
               Socket proxy1 = new Socket("0.tcp.eu.ngrok.io", 19657);
               Socket proxy2 = new Socket("4.tcp.eu.ngrok.io", 16732);
               chatApp2.connect(proxy1, proxy2);
           } catch (IOException ex) {
               chatApp2.error();
           }
    }

  public  static void runApp(String username){
        SwingUtilities.invokeLater(() -> {
            Client chatApp2 = new Client(username);
            chatApp2.setVisible(true);

            if(proxy)
                connectUsingProxy(chatApp2);
            else
                chatApp2.connect();

            messageThread(chatApp2);
            fileThread(chatApp2);

        });
    }
    public static void main(String[] args) {
        runApp("AhmadHossam");
    }
}
