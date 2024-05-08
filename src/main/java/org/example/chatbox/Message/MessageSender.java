package org.example.chatbox.Message;

import org.example.chatbox.data.DataSender;

import java.io.*;
public class MessageSender {
  private   BufferedWriter bufferedWriter;
  private   String message;
  public MessageSender(BufferedWriter bufferedWriter) throws IOException {
        this.bufferedWriter = bufferedWriter;
    }

  public MessageSender(BufferedWriter bufferedWriter , String message) throws IOException {
            this.bufferedWriter = bufferedWriter;
            this.message = message;
        }


    public boolean send() {
        try {
            if(bufferedWriter == null)
                 return false;
            bufferedWriter.write(message);
            bufferedWriter.newLine();//
            bufferedWriter.flush();// send to outputStream
        } catch (IOException e) {
            System.err.println("IO ERR " + e.getMessage());
            return false;
        }

        return true;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
