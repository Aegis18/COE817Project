import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class PokerHouse implements Runnable{

    private UUID gameID;
    private int round;
    private int state;
    private List<Player> players;
    private List<Card> usedCards;
    private List<Card> tableCards;
    private int pool = 0;
    private int playerID;
    private int amountThatPlayersNeedToPutIn = 0;
    private int raise = 0;
    private Player winner;
    @Override
    public void run() {
        //wait until there are min of 2 players.
        while(players.size() < 2){
           System.out.println("player size =" + players.size());
        }
        //Timer section

        //Game start:
        //Tell everyone the game has started
        this.informAllPlayersOfAnEvent("Game has started!!");
        //Generate cards for all players
        this.generateCardsForAllPlayers();
        //To inform players of their cards
        System.out.println("Cards used by players are as follows:");
        Iterator<Card> cardInterator = usedCards.iterator();
        while(cardInterator.hasNext()){
            Card card = cardInterator.next();
            System.out.println(card.getValueString()+" of "+card.getSuit());
        }
        this.initializePlayersWithGameInfo();
        //Round #1
        this.informAllPlayersOfAnEvent("Round #1 has started.......");
        this.Round();
        //generate the table cards
        this.informAllPlayersOfAnEvent("Round #1 is over.");
        this.generateTableCards();

        cardInterator = tableCards.iterator();
        System.out.println("Table cards are as follows:");
        while(cardInterator.hasNext()){
            Card card = cardInterator.next();
            System.out.println(card.getValue()+" of "+card.getSuit());
        }
        this.informAllPlayersOfAnEvent("--------------------------------------------");
        this.informAllPlayersOfAnEvent("The three cards have been revealed on the table.");
        this.informAllPlayersOfAnEvent("1. "+tableCards.get(0).getValueString()+" of "+tableCards.get(0).getSuit()+".");
        this.informAllPlayersOfAnEvent("2. "+tableCards.get(1).getValueString()+" of "+tableCards.get(1).getSuit()+".");
        this.informAllPlayersOfAnEvent("3. "+tableCards.get(2).getValueString()+" of "+tableCards.get(2).getSuit()+".");
        this.informAllPlayersOfAnEvent("--------------------------------------------");
        this.informAllPlayersOfAnEvent("Round #2 has started.......");
        this.Round();
        this.informAllPlayersOfAnEvent("Round #2 is over.");
        this.informAllPlayersOfAnEvent("--------------------------------------------");
        this.informAllPlayersOfAnEvent("The four cards have been revealed on the table.");
        this.informAllPlayersOfAnEvent("1. "+tableCards.get(0).getValueString()+" of "+tableCards.get(0).getSuit()+".");
        this.informAllPlayersOfAnEvent("2. "+tableCards.get(1).getValueString()+" of "+tableCards.get(1).getSuit()+".");
        this.informAllPlayersOfAnEvent("3. "+tableCards.get(2).getValueString()+" of "+tableCards.get(2).getSuit()+".");
        this.informAllPlayersOfAnEvent("4. "+tableCards.get(3).getValueString()+" of "+tableCards.get(3).getSuit()+".");
        this.informAllPlayersOfAnEvent("--------------------------------------------");
        this.informAllPlayersOfAnEvent("Round #3 has started.......");
        this.Round();
        this.informAllPlayersOfAnEvent("Round #3 is over.");
        this.informAllPlayersOfAnEvent("--------------------------------------------");
        this.informAllPlayersOfAnEvent("All cards have been revealed on the table.");
        this.informAllPlayersOfAnEvent("1. "+tableCards.get(0).getValueString()+" of "+tableCards.get(0).getSuit()+".");
        this.informAllPlayersOfAnEvent("2. "+tableCards.get(1).getValueString()+" of "+tableCards.get(1).getSuit()+".");
        this.informAllPlayersOfAnEvent("3. "+tableCards.get(2).getValueString()+" of "+tableCards.get(2).getSuit()+".");
        this.informAllPlayersOfAnEvent("4. "+tableCards.get(3).getValueString()+" of "+tableCards.get(3).getSuit()+".");
        this.informAllPlayersOfAnEvent("5. "+tableCards.get(4).getValueString()+" of "+tableCards.get(4).getSuit()+".");
        this.informAllPlayersOfAnEvent("--------------------------------------------");
        this.informAllPlayersOfAnEvent("Round #4 has started.......");
        this.Round();
        this.informAllPlayersOfAnEvent("Round #4 is over.");
        this.calculateWinner();
    }

    public void calculateWinner(){
        Iterator<Player> playerIterator = players.iterator();
        while(playerIterator.hasNext()){
            Player player = playerIterator.next();
            List<Card> temp = tableCards;
            temp.addAll(Arrays.asList(player.getCards()));
            HandCalculator handCalculator = new HandCalculator(temp);
            player.setHandScore(handCalculator.getScore());
        }
        int highestScore = 0;
        playerIterator = players.iterator();
        while(playerIterator.hasNext()) {
            Player player = playerIterator.next();
            if(player.getHandScore()>highestScore){
                winner = player;
            }else{
                player.writeToClient("You did not win.");
            }
        }
        winner.writeToClient("You are the winner, congratulations!!");
        this.informAllPlayersOfAnEvent("The winner of the game was player "+winner.getID());
        this.informAllPlayersOfAnEvent("Game is over. You will now be disconnected.");
        playerIterator = players.iterator();
        while(playerIterator.hasNext()) {
            Player player = playerIterator.next();

            try {
                player.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            playerIterator.remove();
        }

    }


    public void informAllPlayersOfAnEvent(String notification){
        Iterator<Player> playerIterator = players.iterator();
        while(playerIterator.hasNext()){
            Player player = playerIterator.next();
            player.writeToClient(notification);
        }
    }
    public void initializePlayersWithGameInfo(){
        Iterator<Player> playerIterator = players.iterator();
        while(playerIterator.hasNext()){
            Player player = playerIterator.next();
            ArrayList<Card> cards = new ArrayList<Card>(Arrays.asList(player.getCards()));
            player.writeToClient("There are "+players.size()+" in the game.");
            player.writeToClient("You are player: "+player.getID());
            player.writeToClient("You have in your bank: $"+player.getBank());
            player.writeToClient("Your cards are as follows:");
            int cardNumber = 1;
            Iterator<Card> playerCardIterator = cards.iterator();
            while(playerCardIterator.hasNext()){
                Card card = playerCardIterator.next();
                player.writeToClient("Card #"+cardNumber+":"+card.getValueString()+" of "+card.getSuit());
                cardNumber++;
            }
        }

    }
    public void Round(){
        Iterator<Player> playerIterator = players.iterator();
        while(playerIterator.hasNext()){
            Player player = playerIterator.next();
            if(players.size()<2){
                player.writeToClient("You are the winner, congratulations!!");
                player.writeToClient("Game has ended.");
                try {
                    player.getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            player.writeToClient("Your turn!");
            player.writeToClient("Please indicate what you would like to do? (type 'fold', 'raise', 'call', or 'cheque')");
            player.writeToClient("Before making your choice note that the amount you need to put in to stay in the game is $"+amountThatPlayersNeedToPutIn+", and the amount that you have put in is $"+player.getAmountPutIn()+".");
            player.writeToClient("(NOTE** if you type in an invalid response/inappropriate (i.e. typing cheque if you need to raise) then you will be kicked from the game!)");
            String choice = player.readFromClient();
            System.out.print("Player "+player.getID()+" has chosen to "+choice);
            if(choice.equals("fold")){
                this.informAllPlayersOfAnEvent("Player: "+player.getID()+" has folded.");
                player.writeToClient("You have been disconnected.");
                try {
                    player.getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                playerIterator.remove();
            }else if(choice.equals("raise")){
                player.writeToClient("You have $"+player.getBank()+" in your bank.");
                player.writeToClient("Please type out the amount that you would like to bet: (NOTE** if you type in an invalid amount then you will be kicked from the game!)");
                raise = Integer.valueOf(player.readFromClient());
                player.setAmountPutIn(raise);
                if(raise<=player.getBank()){
                    amountThatPlayersNeedToPutIn = amountThatPlayersNeedToPutIn + raise;
                    player.setBank(0, raise);
                    this.setNewPool(raise);
                    this.informAllPlayersOfAnEvent("Player "+player.getID()+" has raised by "+raise+", the pool is now: $"+getPool());
                    player.writeToClient("Your bank now has $"+player.getBank());
                    player.setAmountPutIn(raise);
                    this.Round();
                }else{
                    player.writeToClient("You have entered an invalid amount/response and have thus been disconnected.");
                    try {
                        player.getSocket().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    playerIterator.remove();

                }
            }
            else if(choice.equals("call")){
                if((amountThatPlayersNeedToPutIn - player.getAmountPutIn()) <=player.getBank()){
                    player.setAmountPutIn(amountThatPlayersNeedToPutIn - player.getAmountPutIn());
                    this.setNewPool((amountThatPlayersNeedToPutIn - player.getAmountPutIn()));
                    this.informAllPlayersOfAnEvent("Player "+player.getID()+" has called the raise of $"+raise+", and the new pool is $"+this.getPool()+".");
                    player.setBank(0,(amountThatPlayersNeedToPutIn - player.getAmountPutIn()));
                    player.writeToClient("Your bank now has $"+player.getBank());
                }
                else{
                    this.informAllPlayersOfAnEvent("Player: "+player.getID()+" been disconnected due to invalid response.");
                    player.writeToClient("You have entered an invalid response and have thus been disconnected.");
                    try {
                        player.getSocket().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    playerIterator.remove();
                }
            }
            else if(choice.equals("check") && (amountThatPlayersNeedToPutIn == player.getAmountPutIn())){
                this.informAllPlayersOfAnEvent("Player: "+player.getID()+" has checked.");
            }
            else{
                this.informAllPlayersOfAnEvent("Player: "+player.getID()+" been disconnected due to invalid response.");
                player.writeToClient("You have entered an invalid response and have thus been disconnected.");
                try {
                    player.getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                playerIterator.remove();
            }
        }
        amountThatPlayersNeedToPutIn  = 0;
        raise = 0;
    }

    public UUID getGameID() {
        return gameID;
    }

    public int getRound() {
        return round;
    }

    public int getState() {
        return state;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Card> getUsedCards() {
        return usedCards;
    }

    public List<Card> getTableCards() {
        return tableCards;
    }

    public int getPlayerID() {
        return playerID;
    }

    private PokerHouse(){
        players = new ArrayList<Player>();
        usedCards = new ArrayList<Card>();
        tableCards = new ArrayList<Card>();
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

    private void generateTableCards(){
        for(int i = 0; i<=4 ; i++ ) {
            try {
                tableCards.add(new Card(generateCard()));
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
            System.out.println("A new Player has connected");
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

    public int getPool(){
        return pool;
    }
    public void setNewPool(int raise){
        pool = pool+raise;
    }
}
