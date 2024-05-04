package org.example.chatbox.app;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketHandler {
    ServerSocket serverSocket;
    final int PORT  = 12345;
    Socket client;
    //input
    InputStream inputStream;
    InputStreamReader inputStreamReader;
    BufferedReader bufferedReader;

    OutputStream outputStream;
    OutputStreamWriter outputStreamWriter;
    BufferedWriter bufferedWriter;
  public ServerSocketHandler() throws IOException {
       //tcp connection
            serverSocket = new ServerSocket(PORT);
    }
    public ServerSocketHandler(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }
    public void start() throws IOException {
        System.out.println("waiting....");
          client =   serverSocket.accept();//return socket //block waiting for connection
        System.out.println("[+] connection from "+client);
          //input
          inputStream = client.getInputStream();
          inputStreamReader = new InputStreamReader(inputStream);
          bufferedReader = new BufferedReader(inputStreamReader);

          outputStream = client.getOutputStream();
          outputStreamWriter = new OutputStreamWriter(outputStream);
          bufferedWriter = new BufferedWriter(outputStreamWriter);

    }

    String receive() throws IOException {
      return bufferedReader.readLine();
    }
    void send(String msg)throws IOException{
      bufferedWriter.write(msg);
      bufferedWriter.newLine();
      bufferedWriter.flush();
    }

}
