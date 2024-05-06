import java.io.*;

public class FileReceiver {
    String saveDir = "received_files";
     InputStream inputStream;
     File file;
     String filename;
     String savePath;
     long fileSize;
   public  FileReceiver(InputStream inputStream){
         this.inputStream = inputStream;
         File dir = new File(saveDir);
         if(!dir.exists()){
             dir.mkdirs();
         }
     }
    void sleep(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void start(){
        sleep();
        System.out.println("receiving files starts");
        sleep();
        System.out.println("receiving files....");
    }
     public boolean receive(){
         DataInputStream dataInputStream = new DataInputStream(inputStream);

         // Receive the filename from the client
         String filename = null;
         try {
             filename = dataInputStream.readUTF();
             long fileSize = dataInputStream.readLong();
             this.filename = filename;
             this.fileSize = fileSize;
             System.out.println(filename);
         } catch (IOException e) {
            // System.err.println(e.getMessage());
             return false;
         }
         savePath = saveDir+"/"+filename;
         try  {
             FileOutputStream fileOutputStream = new FileOutputStream(savePath);
             byte[] buffer = new byte[1024];
             int bytesRead;
             long bytesReceived = 0;
             while (bytesReceived< this.fileSize && (bytesRead = inputStream.read(buffer)) != -1) {
                 fileOutputStream.write(buffer, 0, bytesRead);
                 bytesReceived+=bytesRead;
             }
             if(bytesReceived!=this.fileSize){
                 System.err.println("Incomplete file received");
             }
             else {
                 System.out.println("File received and saved successfully");
                 return true;
             }
            // file = new File(savePath);
         }
         catch (IOException e) {
             System.err.println(e.getMessage());
             return false;
         }
         return true;
     }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public File getFile() {
        return file;
    }

    public String getFilename() {
        return filename;
    }
}
