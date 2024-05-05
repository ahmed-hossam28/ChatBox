import java.io.BufferedReader;
import java.io.IOException;

public class MessageReceiver {
    String message;
    BufferedReader bufferedReader;

    public MessageReceiver(BufferedReader bufferedReader){
        this.bufferedReader = bufferedReader;
    }

    public boolean receive(){
        try {
            message = bufferedReader.readLine();
            if(message==null){
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
}
