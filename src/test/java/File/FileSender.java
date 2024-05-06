package File;

import java.io.*;

public class FileSender  {

     OutputStream outputStream;
     File file;

    public FileSender(File file,OutputStream outputStream){
        this.file =file;
        this.outputStream = outputStream;
    }

    public void setFile(File file){
        this.file = file;
    }
   // @Override
    public boolean send() {
        try {
            String filename = file.getName();
            long fileSize = file.length();
            FileInputStream fileInputStream = new FileInputStream(file);
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            // Write filename to the output stream
            dataOutputStream.writeUTF(filename);
            dataOutputStream.writeLong(fileSize);

            // Write file contents to the output stream
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

           fileInputStream.close();
            // Flush and close streams
            dataOutputStream.flush();
            outputStream.flush();

            //System.out.println("File " + filename + " sent successfully");
            return true;
        } catch (IOException e) {
            System.err.println("Error with Sending File: " + e.getMessage());
            return false;
        }
    }





}