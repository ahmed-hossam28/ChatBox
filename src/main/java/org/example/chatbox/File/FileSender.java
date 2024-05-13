package org.example.chatbox.File;
import org.example.chatbox.data.DataSender;

import javax.swing.*;
import java.io.*;

public class FileSender{

     OutputStream outputStream;
     File file;

    public FileSender(File file,OutputStream outputStream){
        this.file =file;
        this.outputStream = outputStream;
    }



    public boolean send() {
        try {
            String filename = file.getName();
            long fileSize = file.length();//mohema
            FileInputStream fileInputStream = new FileInputStream(file);

            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            // Write filename to the output stream
            dataOutputStream.writeUTF(filename);
            dataOutputStream.writeLong(fileSize);//
            dataOutputStream.flush();
            //

            // Write file contents to the output stream
            byte[] buffer = new byte[1<<20];//array
            int bytesRead;
            System.out.println("Sending file: " + file.getName()+"...");
            System.out.println("file size = "+ fileSize);
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                outputStream.flush();
            }


           fileInputStream.close();//saftey
            // Flush and close streams

            System.out.println("File " + filename + " sent successfully");

        } catch (IOException e) {
            System.err.println("Error with Sending File: " + e.getMessage());
            return false;
        }
        return true;
    }

    public void setFile(File file){
        this.file = file;
    }


}
