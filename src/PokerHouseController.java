import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class PokerHouseController {

    public static final int maxUsers = 5;
    private static final int port = 5555;

    public static void main(String[] args){
        ServerSocket serverSocket;
        PokerHouse pokerHouse = PokerHouse.getInstance();
        int counter = 0;
        try {
            serverSocket = new ServerSocket(port);
            while(counter < 5){
                pokerHouse.addPlayer(serverSocket.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
