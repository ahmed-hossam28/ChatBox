import app.RunClient;
import org.example.chatbox.File.FileSender;
import org.example.chatbox.Message.MessageReceiver;
import app.ChatApp2;
import org.example.chatbox.Message.MessageSender;
import org.example.chatbox.sockets.SocketHandler;

import javax.swing.*;
import java.io.*;
import java.util.Scanner;

public class Run {
    public static void main(String[] args) {
        for(int i = 0;i<2;++i)
             RunClient.runApp("person"+i);
    }

}
