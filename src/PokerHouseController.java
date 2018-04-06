import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PokerHouseController {

    public static final int maxUsers = 5;
    private static final int port = 5555;

        public static void main(String[] args){
            ServerSocket serverSocket;
            PokerHouse pokerHouse = PokerHouse.getInstance();
            int counter = 0;
            int numOfPlayers;
            List<Player> players;
            while(true) {
                try {
                    serverSocket = new ServerSocket(port);
                    while (counter < maxUsers) {
                        pokerHouse.addPlayer(serverSocket.accept());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
