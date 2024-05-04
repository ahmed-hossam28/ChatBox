import java.io.*;
import java.net.*;
import javax.swing.*;

public class FileSenderTest {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;

        // Create and configure the file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose File to Send");
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filename = selectedFile.getName();
            try (Socket socket = new Socket(host, port);
                 FileInputStream fileInputStream = new FileInputStream(selectedFile);
                 OutputStream outputStream = socket.getOutputStream()) {
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeUTF(filename);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("File sent successfully");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("File selection canceled");
        }
    }
}
