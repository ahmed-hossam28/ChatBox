import app.Client;
import app.RunClient;

import java.util.Scanner;

public class Run {
    public static void main(String[] args) {

     Client client =  new Client("ahmad");
     client.setVisible(true);
     client.connect();
    }

}
