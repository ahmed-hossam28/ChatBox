package Message;

import data.DataSender;

import java.io.*;
public class MessageSender implements DataSender {
  private   BufferedWriter bufferedWriter;
  private   String message;
  public MessageSender(BufferedWriter bufferedWriter) throws IOException {
        this.bufferedWriter = bufferedWriter;
    }

     public MessageSender(BufferedWriter bufferedWriter , String message) throws IOException {
            this.bufferedWriter = bufferedWriter;
            this.message = message;
        }

        @Override
    public boolean send() {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            return true;
        } catch (IOException e) {
            System.err.println("IO ERR" + e.getMessage());
            return false;
        }

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
