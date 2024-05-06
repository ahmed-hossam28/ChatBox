import Message.MessageReceiver;
import app.ChatApp2;
import org.example.chatbox.app.SocketHandler;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;

public class Run {
    public static void main(String[] args) {
         runApp();
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

            // Start a new thread for receiving messages
            final SocketHandler finalClient = client;
            new Thread(() -> {
                BufferedReader bufferedReader = finalClient.getBufferedReader();
                MessageReceiver messageReceiver = new MessageReceiver(bufferedReader);
                while (true) {
                    // Receive message
                    if(!messageReceiver.receive()) {
                        break;

                    }
                    System.out.println(messageReceiver.getMessage());
                    // Update GUI with received message
                    SwingUtilities.invokeLater(() -> {
                        chatApp2.addMessage("Friend", messageReceiver.getMessage(), false);
                    });
                }



            }).start();

        });
    }
}
