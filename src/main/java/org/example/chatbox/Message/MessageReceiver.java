package org.example.chatbox.Message;

import java.io.BufferedReader;
import java.io.IOException;

public class MessageReceiver {
    String message;
    BufferedReader bufferedReader;

    public MessageReceiver(BufferedReader bufferedReader){
        this.bufferedReader = bufferedReader;
    }

    public void start(){
        sleep();
        System.out.println("receiving messages starts");
        sleep();
        System.out.println("receiving messages...");
    }


    public boolean receive(){

        try {
            message = bufferedReader.readLine();
            if(message==null){
                return false;
            }
        } catch (IOException e) {
            System.err.println("IO err "+e.getMessage());
            return false;
        }

        return true;
    }

    public String getMessage() {
        return message;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    void sleep(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
