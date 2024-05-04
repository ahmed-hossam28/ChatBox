import java.io.BufferedReader;
import java.io.IOException;

public class MessageReceiver {
    String message;
    BufferedReader bufferedReader;

    public MessageReceiver(BufferedReader bufferedReader){
        this.bufferedReader = bufferedReader;
    }

    public void receive(){
        try {
            message = bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMessage() {
        return message;
    }
}
