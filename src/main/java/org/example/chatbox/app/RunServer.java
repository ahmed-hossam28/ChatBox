package org.example.chatbox.app;

import org.example.chatbox.File.FileReceiver;
import org.example.chatbox.Message.MessageReceiver;
import org.example.chatbox.pair.Pair;
import org.example.chatbox.sockets.SocketHandler;
import org.example.chatbox.user.User;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;

public class RunServer {

    static void handelReceiveMessages(Server server, String username){
        //each thread for each client so connection remain
        new Thread(() -> {
            BufferedReader bufferedReader = server.messageServer.getBufferedReader();
            MessageReceiver messageReceiver = new MessageReceiver(bufferedReader);
            while (true) {
                // Receive message
                if(!messageReceiver.receive())
                    break;
                // System.out.println(messageReceiver.getMessage());
                // Update GUI with received message
                SwingUtilities.invokeLater(() -> {
                    server.addMessage(username, messageReceiver.getMessage(), false);
                });
            }
        }).start();
    }
    static void handleMessagingRequests(Server server){
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
    static void handleFileRequests(Server server){
        //FileThread
        new Thread(()->{
            while(true){
                try {
                    System.out.print("FILE:");
                    server.fileServer.start();
                    server.userFileConnections.add(new Pair<>(new SocketHandler(server.fileServer.getSocket()),true));

                } catch (IOException e) {
                    System.err.println("file :"+e.getMessage());
                }
                //receiving files thread

                handleReceiveFiles(server);
            }
        }).start();
    }
    static void handleReceiveFiles(Server server){
        new Thread(()->{
            FileReceiver fileReceiver = new FileReceiver(server.fileServer.getInputStream());
            fileReceiver.start();
            while(true){
                if(fileReceiver.receive())
                    JOptionPane.showMessageDialog(server,fileReceiver.getFilename()+"Received!");
                else break;
            }
        }).start();
    }
    static void runApp(){
        SwingUtilities.invokeLater(() -> {
            Server server = new Server();
            server.setVisible(true);

           handleMessagingRequests(server);
           handleFileRequests(server);
        });
    }

    public static void main(String[] args) {
        runApp();
    }
}
