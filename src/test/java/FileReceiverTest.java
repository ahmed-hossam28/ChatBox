import java.io.*;
import java.net.*;

public class FileReceiverTest {
    public static void main(String[] args) {
        int port = 12345;
        String saveDir = "received_files/";
        System.out.println("waiting for incomming connections .....");
        try (ServerSocket serverSocket = new ServerSocket(port);
             Socket socket = serverSocket.accept();
             InputStream inputStream = socket.getInputStream();
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {

            // Receive the filename from the client
            String filename = dataInputStream.readUTF();
            String savePath = saveDir + filename;

            // Create directories if they don't exist
            File directory = new File(saveDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Receive and save the file
            try (FileOutputStream fileOutputStream = new FileOutputStream(savePath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("File received and saved successfully");


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
