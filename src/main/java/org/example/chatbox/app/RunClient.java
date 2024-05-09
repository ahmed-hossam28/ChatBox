package org.example.chatbox.app;

import org.example.chatbox.File.FileReceiver;
import org.example.chatbox.Message.MessageReceiver;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public class RunClient {
   static boolean proxy = false;
   static String proxyHost1 = "0.tcp.eu.ngrok.io";
   static String proxyHost2 = "4.tcp.eu.ngrok.io";
   static int port1 = 19657;
   static int port2 = 16732;
   static void messageThread(Client chatApp2){
        new Thread(() -> {
            while (true) {
                if(!chatApp2.connectionStatus) {
                    chatApp2.reconnectInBackground(proxy);
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
                        System.err.println("problem is here");
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



        }).start();
    }
   static void fileThread(Client chatApp2){
        new Thread(()->{
            FileReceiver fileReceiver = null;
            while(true){
                if(!chatApp2.connectionStatus) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }
                while(true) {
                    try {
                        fileReceiver  = new FileReceiver(chatApp2.fileSocketHandler.getInputStream());
                        fileReceiver.start();
                    }catch (NullPointerException ex){
                        continue;
                    }
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
   static void fileThreadTest(Client chatApp2){
       new Thread(() -> {
           while (true) {
               if(!chatApp2.connectionStatus) {
                   try {
                       Thread.sleep(1000);
                   } catch (InterruptedException e) {
                       throw new RuntimeException(e);
                   }
                   continue;
               }
               FileReceiver fileReceiver = null;
               try {
                   fileReceiver = new FileReceiver(chatApp2.fileSocketHandler.getInputStream());
               }catch (NullPointerException e){
                   System.out.println("it is null ");
                   continue;
               }
               // Receive message
               while(true) {
                   // System.out.println("working inner loop");
                   if(fileReceiver.getInputStream() == null) {
                       System.err.println("problem is here");
                       break;
                   }
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
               Socket proxy1 = new Socket(proxyHost1, port1);
               Socket proxy2 = new Socket(proxyHost2, port2);
               chatApp2.connect(proxy1, proxy2);
           } catch (IOException ex) {
               chatApp2.error();
           }
    }

  public  static void runApp(String username){
        SwingUtilities.invokeLater(() -> {
            Client chatApp2 = new Client(username);
            chatApp2.setVisible(true);

            if(proxy) {
                connectUsingProxy(chatApp2);
                chatApp2.setProxy(true);
                chatApp2.setProxyConf(proxyHost1,port1,proxyHost2,port2);
            }
            else
                chatApp2.connect();


            messageThread(chatApp2);//
           // fileThread(chatApp2);
            fileThreadTest(chatApp2);//

        });
    }
    public static void main(String[] args) {
        runApp("AhmadHossam");
    }
}
