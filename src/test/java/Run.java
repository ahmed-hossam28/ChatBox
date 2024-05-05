import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;

public class Run {
    public static void main(String[] args) {
        //FileReceiver fileReceiver = new FileReceiver(System.in);
         runApp();

    }
    static void runApp(){
        SwingUtilities.invokeLater(() -> {
            ChatApp2 chatApp2 = new ChatApp2();
            chatApp2.setVisible(true);

            // Start a new thread for receiving messages
            new Thread(() -> {
                BufferedReader bufferedReader = chatApp2.client.getBufferedReader();
                MessageReceiver messageReceiver = new MessageReceiver(bufferedReader);
                while (true) {
                    // Receive message
                      messageReceiver.receive();
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
