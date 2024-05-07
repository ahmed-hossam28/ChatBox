package org.example.chatbox.user;

import org.example.chatbox.sockets.SocketHandler;

import java.io.IOException;
import java.net.Socket;

public class User {
    String name;
    SocketHandler socketHandler;
  public  User(String name,Socket socket) throws IOException {
        this.name =name;
        socketHandler = new SocketHandler(socket);
    }
  public void setSocketHandler(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }
    public void setSocketHandler(Socket socket) throws IOException {
      this.socketHandler = new SocketHandler(socket);
    }

    public String getName() {
        return name;
    }

    public SocketHandler getSocketHandler() {
        return socketHandler;
    }
}
