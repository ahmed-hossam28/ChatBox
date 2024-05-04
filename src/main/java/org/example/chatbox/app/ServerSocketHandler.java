package org.example.chatbox.app;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketHandler {
    ServerSocket serverSocket;
     int port = 12345;
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
            serverSocket = new ServerSocket(port);
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

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public int getPort() {
        return port;
    }

    public Socket getClient() {
        return client;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public InputStreamReader getInputStreamReader() {
        return inputStreamReader;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public OutputStreamWriter getOutputStreamWriter() {
        return outputStreamWriter;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }
}
