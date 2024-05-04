import java.io.*;

public class FileReceiver {
    String saveDir = "received_files/";
     InputStream inputStream;
     File file;
     String filename;
     String savePath;
   public  FileReceiver(InputStream inputStream){
         this.inputStream = inputStream;
     }

     public void receive(){
         DataInputStream dataInputStream = new DataInputStream(inputStream);
         try {
             filename = dataInputStream.readUTF();
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
         savePath = saveDir+filename;
         try (FileOutputStream fileOutputStream = new FileOutputStream(savePath)) {
             byte[] buffer = new byte[1024];
             int bytesRead;
             while ((bytesRead = inputStream.read(buffer)) != -1) {
                 fileOutputStream.write(buffer, 0, bytesRead);
             }
             System.out.println("File received and saved successfully");
             file = new File(savePath);
         }
         catch (IOException e) {
             throw new RuntimeException(e);
         }

     }

    public File getFile() {
        return file;
    }

    public String getFilename() {
        return filename;
    }
}
