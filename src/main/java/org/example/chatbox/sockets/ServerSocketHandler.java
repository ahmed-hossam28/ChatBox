package org.example.chatbox.sockets;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketHandler {
    ServerSocket serverSocket;
     int port = 12345;
     Socket socket;//connection of client
    //input
    InputStream inputStream;
    InputStreamReader inputStreamReader;
    BufferedReader bufferedReader;
    //output
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
          socket =   serverSocket.accept();//return socket  waiting//block waiting for connection
        System.out.println("[+] connection from "+ socket);
          //input

          inputStream = socket.getInputStream();

          inputStreamReader = new InputStreamReader(inputStream);
          bufferedReader = new BufferedReader(inputStreamReader);


          outputStream = socket.getOutputStream();
          outputStreamWriter = new OutputStreamWriter(outputStream);
          bufferedWriter = new BufferedWriter(outputStreamWriter);

    }

  public  String receive()  {
      try {
          return bufferedReader.readLine();
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
  }
  public void send(String msg)throws IOException{
      bufferedWriter.write(msg);
      bufferedWriter.newLine();//
      bufferedWriter.flush();//send to outputstream
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public int getPort() {
        return port;
    }

    public Socket getSocket() {
        return socket;
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

    public void close() throws IOException {
        serverSocket.close();
        bufferedReader.close();
        inputStream.close();
        outputStream.close();
        bufferedWriter.close();
    }
}
