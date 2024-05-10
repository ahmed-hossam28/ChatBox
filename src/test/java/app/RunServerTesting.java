package app;

import app.ChatServer;
import org.example.chatbox.File.FileReceiver;
import org.example.chatbox.Message.MessageReceiver;
import org.example.chatbox.Message.MessageSender;
import org.example.chatbox.pair.Pair;
import org.example.chatbox.sockets.SocketHandler;
import org.example.chatbox.user.User;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;

public class RunServerTesting {
   static  ChatServer server;
   static String chosen = "youssef";
    static void handelReceiveMessages(ChatServer server, String username){
        //each thread for each client so connection remain
        new Thread(() -> {
            User user = null;
           for(var user1:server.users){
                if(user1.first.getMessageSocketHandler().getSocket() == server.messageServer.getSocket()){
                    user = user1.first;
                    break;
                }
           }
            BufferedReader bufferedReader = user.getBufferedReader();
            MessageReceiver messageReceiver = new MessageReceiver(bufferedReader);
            while (true) {
                // Receive message
                if(!messageReceiver.receive())
                    break;
                // System.out.println(messageReceiver.getMessage());
                // Update GUI with received message
                if(user.getName().equals("AhmadHossam")) {
                    if (sendMessageToChosenUser(chosen, messageReceiver.getMessage()))
                        System.out.println("message to yousef was sent!");
                }
                else if(user.getName().equals(chosen)){
                    if (sendMessageToChosenUser("AhmadHossam", messageReceiver.getMessage()))
                        System.out.println("message to ahmad hossam was sent!");
                }

                SwingUtilities.invokeLater(() -> {
                    server.addMessage(username, messageReceiver.getMessage(), false);
                });
            }
        }).start();
    }
    static boolean sendMessageToChosenUser(String username,String message){
        MessageSender messageSender = null;
        User chosen = null;
        for(var user:server.users){
            if(user.first.getName().equals(username)){
                chosen  = user.first;
                break;
            }
        }
        if(chosen==null)
            return false;
        try {
            messageSender = new MessageSender(chosen.getMessageSocketHandler().getBufferedWriter());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        messageSender.setMessage(message);
        return messageSender.send();
    }
    static void handleMessagingRequests(ChatServer server){
        //MessageThread
        new Thread(()-> {
            try {
                while (true) {
                    server.messageServer.start();
                    String username = server.messageServer.receive();
                    User user = new User(username, server.messageServer.getSocket());
                    server.users.add(new Pair<>(user,true));
                    handelReceiveMessages(server,username);

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    static void handleFileRequests(ChatServer server){
        //FileThread
        new Thread(()->{
            while(true){
                try {
                    System.out.print("FILE:");
                    server.fileServer.start();
                    String username =  server.fileServer.receive();
                    User user = new User(username, server.fileServer.getSocket());
                    server.users.add(new Pair<>(user,true));
                    server.userFileConnections1.add(new Pair<>(user,true));
                    server.userFileConnections.add(new Pair<>(new SocketHandler(server.fileServer.getSocket()),true));

                } catch (IOException e) {
                    System.err.println("file :"+e.getMessage());
                }
                //receiving files thread

                handleReceiveFiles(server);
            }
        }).start();
    }
    static void handleReceiveFiles(ChatServer server){
        new Thread(()->{
            User user = null;
            for(var user1:server.userFileConnections1){
                //it is fileSocketHandler
                if(user1.first.getMessageSocketHandler().getSocket() == server.fileServer.getSocket()){
                    user = user1.first;
                    break;
                }
            }
            FileReceiver fileReceiver = new FileReceiver(user.getInputStream());
            fileReceiver.start();
            while(true){
                if(fileReceiver.receive())
                    JOptionPane.showMessageDialog(server,fileReceiver.getFilename()+" Received!");
                else break;
            }
        }).start();
    }
    static void runApp(){
        SwingUtilities.invokeLater(() -> {
            server = new ChatServer();
            server.setVisible(true);

            handleMessagingRequests(server);
            handleFileRequests(server);
        });
    }

    public static void main(String[] args) {
        runApp();
    }
}
