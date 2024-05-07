package app;

import org.example.chatbox.File.FileReceiver;
import org.example.chatbox.Message.MessageReceiver;
import org.example.chatbox.pair.Pair;
import org.example.chatbox.user.User;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;

public class RunServer {



    static void handelReceiveMessages(ChatServer chatServer,String username){
        //each thread for each client so connection remain
        new Thread(() -> {
            BufferedReader bufferedReader = chatServer.messageServer.getBufferedReader();
            MessageReceiver messageReceiver = new MessageReceiver(bufferedReader);
            while (true) {
                // Receive message
                if(!messageReceiver.receive())
                    break;
                // System.out.println(messageReceiver.getMessage());
                // Update GUI with received message
                SwingUtilities.invokeLater(() -> {
                    chatServer.addMessage(username, messageReceiver.getMessage(), false);
                });
            }
        }).start();
    }
   static void receiveTestConnectionMessage(ChatServer chatServer){
        BufferedReader bufferedReader = chatServer.messageServer.getBufferedReader();
        MessageReceiver messageReceiver = new MessageReceiver(bufferedReader);
        messageReceiver.receive();
        messageReceiver.receive();
    }
    static void handleMessagingRequests(ChatServer chatServer){
        //MessageThread
        new Thread(()-> {
            try {
                while (true) {
                    chatServer.messageServer.start();
                    String username = chatServer.messageServer.receive();
                    User user = new User(username, chatServer.messageServer.getClient());
                    chatServer.users.add(new Pair<>(user,true));

                    handelReceiveMessages(chatServer,username);


                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    static void handleFileRequests(ChatServer chatServer){
        //FileThread
        new Thread(()->{
            while(true){
                try {
                    System.out.print("FILE:");
                    chatServer.fileServer.start();

                } catch (IOException e) {
                    System.err.println("file :"+e.getMessage());
                }
                //receiving files thread

                handleReceiveFiles(chatServer);
            }
        }).start();
    }
    static void handleReceiveFiles(ChatServer chatServer){
        new Thread(()->{
            FileReceiver fileReceiver = new FileReceiver(chatServer.fileServer.getInputStream());
            fileReceiver.start();
            while(true){
                if(fileReceiver.receive())
                    JOptionPane.showMessageDialog(chatServer,fileReceiver.getFilename()+"Received!");
                else break;
            }
        }).start();
    }
    static void runApp(){
        SwingUtilities.invokeLater(() -> {
            ChatServer chatServer = new ChatServer();
            chatServer.setVisible(true);

           handleMessagingRequests(chatServer);
           handleFileRequests(chatServer);
        });
    }

    public static void main(String[] args) {
        runApp();
    }
}
