import org.example.chatbox.app.SocketHandler;

import java.io.*;
import java.net.Socket;

public class FileSender {
    String host = "localhost";
    int port = 12345;
    Socket socket;
    File file;

    FileSender(File file){
        this.file =file;
        try {
            socket = new Socket(host,port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    void setPort(int port){
        this.port = port;
    }
    void setFile(File file){
        this.file = file;
    }
    boolean send(){
        try{
            String filename = file.getName();
            FileInputStream fileInputStream = new FileInputStream(file);
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(filename);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("File sent successfully");
            return true;
        }catch (IOException e){
            System.err.println("Error with Sending File:" + e.getMessage());
            return false;
        }
    }




}
