package org.example.chatbox.app;

import org.example.chatbox.File.FileReceiver;
import org.example.chatbox.File.FileSender;
import org.example.chatbox.Message.MessageReceiver;
import org.example.chatbox.pair.Pair;
import org.example.chatbox.user.User;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class RunServer {

    static void handelReceiveMessages(Server server, String sender) {
        new Thread(() -> {
            BufferedReader bufferedReader = server.messageServer.getBufferedReader();
            MessageReceiver messageReceiver = new MessageReceiver(bufferedReader);
            while (true) {
                if (!messageReceiver.receive())
                    break;

                 String message = messageReceiver.getMessage();

                // Send to all users except the one who sent it
                for (var user : server.users) {
                    if (!user.first.getName().equals(sender)) {
                        try {
                            // Send the message along with the sender's username
                            user.first.getSocketHandler().send(sender + ": " + message);
                        } catch (Exception ex) {
                            System.err.println("Connection broken for user: " + user.first.getName());
                        }
                    }
                }

                // Update GUI with received message
                SwingUtilities.invokeLater(() -> {
                    server.addMessage(sender, message, false); // Display sender's username along with the message
                });
            }
        }).start();
    }

    static void handleMessagingConnectionRequests(Server server){
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
            } catch (Exception e) {
                System.err.println("Connection is failed while connecting");
            }
        }).start();
    }
    static void handleFileConnectionRequests(Server server){
        //FileThread
        new Thread(()->{
            while(true){
                try {
                    System.out.print("FILE:");
                    server.fileServer.start();
                    User user = new User(server.fileServer.receive(),server.fileServer.getSocket());
                    server.userFileConnections.add(new Pair<>(user,true));

                    handleReceiveFiles(server,user.getName());

                } catch (IOException e) {
                    System.err.println("file :"+e.getMessage());
                }
                //receiving files thread


            }
        }).start();
    }
    static void handleReceiveFiles(Server server,String sender){
        new Thread(()->{
            FileReceiver fileReceiver = new FileReceiver(server.fileServer.getInputStream());
            fileReceiver.start();//for console to know that server is receiving files

            while(true){
                if(fileReceiver.receive()) {
                   // server.sendToMultipleUsers(fileReceiver.getFile());

                    for(var user:server.userFileConnections){
                        if (user.first.getName().equals(sender)) continue;
                        new Thread(()->{
                            File file = new File("received_files/"+fileReceiver.getFilename());
                            FileSender fileSender = new FileSender(file, user.first.getSocketHandler().getOutputStream());
                            if (!fileSender.send()) {
                                System.err.println("err sending file");
                                System.out.println("[-]file server for " + user.first.getSocketHandler().getSocket());
                                user.second = false;
                            }
                        }).start();
                    }

                    server.userFileConnections.removeIf(user->!user.second);

                   // JOptionPane.showMessageDialog(server, fileReceiver.getFilename() + "Received!");

                }
                else break;
            }
        }).start();
    }
    static void runApp(){
        SwingUtilities.invokeLater(() -> {
            Server server = new Server();
            server.setVisible(true);

           handleMessagingConnectionRequests(server);
           handleFileConnectionRequests(server);
        });
    }

    public static void main(String[] args) {
        runApp();
    }
}
