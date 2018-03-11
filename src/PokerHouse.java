import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PokerHouse {

    private UUID gameID;
    private int round;
    private int state;
    private List<Player> players;
    private List<Card> usedCards;
    private List<Card> pool;
    private int playerID;



    private PokerHouse(){
        players = new ArrayList<Player>();
        usedCards = new ArrayList<Card>();
        pool = new ArrayList<Card>();
        playerID = 0;
        round = 0;
        state = 0;
    }

    public void generateCardsForAllPlayers(){
        Iterator<Player> playerIterator = players.iterator();
        while(playerIterator.hasNext()){
            Player player = playerIterator.next();
            player.setCards(generateCardsForPlayer());
        }
    }

    private Card[] generateCardsForPlayer(){
        Card[] array = new Card[2];
        try {
            array = new Card[]{
                    new Card(generateCard()),
                    new Card(generateCard())
            };
        } catch (Card.NoCardException e) {
            e.printStackTrace();
        }
        return array;
    }

    private void generatePoolCards(){
        for(int i = 0; i<3 ; i++ ) {
            try {
                pool.add(new Card(generateCard()));
            } catch (Card.NoCardException e) {
                e.printStackTrace();
            }
        }
    }

    private int generateCard(){
        int card = Card.generateCard();
        while(isUsed(card)){
            card = Card.generateCard();
        }
        try {
            usedCards.add(new Card(card));
        } catch (Card.NoCardException e) {
            e.printStackTrace();
        }
        return card;
    }

    private void addCardToPool(){
        try {
            pool.add(new Card(generateCard()));
        } catch (Card.NoCardException e) {
            e.printStackTrace();
        }
    }

    private boolean isUsed(int cardValue){
        boolean hasCard = false;
        Iterator<Card> cardIterator = usedCards.iterator();
        while (cardIterator.hasNext()){
            Card card = cardIterator.next();
            if(card.getValue() == cardValue){
                hasCard = true;
                break;
            }
        }
        return hasCard;
    }

    public void addPlayer(Socket socket){
        if(socket != null){
            players.add(new Player(socket, playerID++));
        }
    }

    private void tellPlayerTheirCards(){
        Iterator<Player> playerIterator = players.iterator();
        while(playerIterator.hasNext()){
            Player player = playerIterator.next();
            Card cards[] = player.getCards();
            String message = "************************************************" + System.lineSeparator();
            message = message + "Your cards are: " + System.lineSeparator();
            message = message + cards[0].toString() + System.lineSeparator();
            message = message + cards[1].toString() + System.lineSeparator();
            message = message + "************************************************" + System.lineSeparator();
            player.writeToClient(message);
        }
    }

    public void removePlayer(int ID){
        Iterator<Player> playerIterator = players.iterator();
        while(playerIterator.hasNext()){
            Player player = playerIterator.next();
            if(player.getID() == ID){
                playerIterator.remove();
                break;
            }
        }
    }

    private Player getPlayer(int playerID){
        Player selectedPlayer = null;
        Iterator<Player> playerIterator = players.iterator();
        while(playerIterator.hasNext()){
            Player player = playerIterator.next();
            if(player.getID() == playerID){
                selectedPlayer = player;
                break;
            }
        }
        return selectedPlayer;
    }

    public static PokerHouse getInstance(){
        return PokerHouseHolder.instance;
    }

    private static class PokerHouseHolder{
        private static final PokerHouse instance = new PokerHouse();
    }
}
