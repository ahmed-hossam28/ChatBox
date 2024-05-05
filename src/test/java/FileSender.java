import org.example.chatbox.app.SocketHandler;

import java.io.*;
import java.net.Socket;

public class FileSender implements DataSender {

     OutputStream outputStream;
     File file;

    public FileSender(File file,OutputStream outputStream){
        this.file =file;
        this.outputStream = outputStream;
    }


    public void setFile(File file){
        this.file = file;
    }
    @Override
   public boolean send(){
        try{
            String filename = file.getName();
            FileInputStream fileInputStream = new FileInputStream(file);
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF("FILE:"+filename);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("File "+filename+" sent successfully");
            return true;
        }catch (IOException e){
            System.err.println("Error with Sending File:" + e.getMessage());
            return false;
        }
    }




}
