package org.example.chatbox.user;

import org.example.chatbox.sockets.SocketHandler;

import java.io.IOException;
import java.net.Socket;

public class User {
    String name;
    SocketHandler messageSocketHandler;
  public  User(String name,Socket socket) throws IOException {
        this.name =name;
        messageSocketHandler = new SocketHandler(socket);
    }
  public void setMessageSocketHandler(SocketHandler messageSocketHandler) {
        this.messageSocketHandler = messageSocketHandler;
    }
    public void setSocketHandler(Socket socket) throws IOException {
      this.messageSocketHandler = new SocketHandler(socket);
    }

    public String getName() {
        return name;
    }

    public SocketHandler getMessageSocketHandler() {
        return messageSocketHandler;
    }
}
