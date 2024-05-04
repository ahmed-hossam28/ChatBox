package org.example.chatbox.app;

import java.io.*;
import java.net.Socket;

public class SocketHandler {
    Socket socket;

    InputStream inputStream;
    InputStreamReader inputStreamReader;
    BufferedReader bufferedReader;

    OutputStream outputStream;
    OutputStreamWriter outputStreamWriter;
    BufferedWriter bufferedWriter;
    SocketHandler() throws IOException {
         socket = new Socket("localhost",12345);
         inputStream = socket.getInputStream();
         inputStreamReader = new InputStreamReader(inputStream);
         bufferedReader = new BufferedReader(inputStreamReader);

         outputStream = socket.getOutputStream();
         outputStreamWriter  = new OutputStreamWriter(outputStream);
         bufferedWriter = new BufferedWriter(outputStreamWriter);
    }
    public void send(String msg) throws IOException {
        bufferedWriter.write(msg);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public String receive() throws IOException {
        return bufferedReader.readLine();
    }





}
