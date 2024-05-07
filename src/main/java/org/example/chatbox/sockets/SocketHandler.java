package org.example.chatbox.sockets;

import java.io.*;
import java.net.Socket;

public class SocketHandler {
    String host = "ip address";
    int port = 17413;
    Socket socket;
    InputStream inputStream;
    InputStreamReader inputStreamReader;
    BufferedReader bufferedReader;

    OutputStream outputStream;
    OutputStreamWriter outputStreamWriter;
    BufferedWriter bufferedWriter;
   public SocketHandler() {
       try {
           socket = new Socket("0.tcp.eu.ngrok.io",this.port);//
       } catch (IOException e) {
           System.err.println(e.getMessage());
       }
       try {
           initDataCommunication();//
       } catch (IOException e) {
           System.err.println(e.getMessage());
       }
   }

   public void  initDataCommunication() throws IOException {
        inputStream = this.socket.getInputStream();
        inputStreamReader = new InputStreamReader(inputStream);
        bufferedReader = new BufferedReader(inputStreamReader);

        outputStream = socket.getOutputStream();
        outputStreamWriter  = new OutputStreamWriter(outputStream);
        bufferedWriter = new BufferedWriter(outputStreamWriter);
    }

  public  SocketHandler(Socket socket){
       this.socket = socket;
        try {
            initDataCommunication();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }



    public SocketHandler(int port) throws IOException {
        socket = new Socket("localhost",port);
        initDataCommunication();
    }

    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        initDataCommunication();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void send(String msg)  {

        try {
            bufferedWriter.write(msg);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String receive() throws IOException {
        return bufferedReader.readLine();
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
}
