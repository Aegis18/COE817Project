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
    private int winnings = 0;
    private List<Player> winners;
    private int gameCounter = 1;
    private Scanner scanner;
    private int numOfPlayers;
    private String numOfPlayersString;
    @Override
    public void run() {
        //wait until there are min of 2 players.
        winners = new ArrayList<Player>();
        System.out.println("Please type in how many players you want to join the game:");
        System.out.println("(NOTE that this cannot be reset unless the program is restarted, additionally the game won't start unless correct amount of clients have joined the game!)");
        scanner = new Scanner(System.in);
        numOfPlayersString = scanner.nextLine();
        System.out.println("restarted and that the game will not start until all players have joined.");
        numOfPlayers = Integer.valueOf(numOfPlayersString);
        while(players.size() < numOfPlayers){
            System.out.println("Number of players that have connected: " + players.size());
        }
        game();
    }
    public void game(){
        //Game start:
        //Tell everyone the game has started
        informAllPlayersOfAnEvent("Game #"+gameCounter+" has started!!");
        //Generate cards for all players
        generateCardsForAllPlayers();
        //To inform players of their cards
        System.out.println("Cards used by players are as follows:");
        Iterator<Card> cardInterator = usedCards.iterator();
        while(cardInterator.hasNext()){
            Card card = cardInterator.next();
            System.out.println(card.getValueString()+" of "+card.getSuit());
        }
        initializePlayersWithGameInfo();
        //Round #1
        informAllPlayersOfAnEvent("Round #1 has started.......");
        this.setRaise(0);
        Round();
        //generate the table cards
        informAllPlayersOfAnEvent("Round #1 is over.");
        generateTableCards();

        cardInterator = tableCards.iterator();
        System.out.println("Table cards are as follows:");
        while(cardInterator.hasNext()){
            Card card = cardInterator.next();
            System.out.println(card.getValueString()+" of "+card.getSuit());
        }
        System.out.println("All cards that are now in use are as follows:");
        cardInterator = usedCards.iterator();
        while(cardInterator.hasNext()){
            Card card = cardInterator.next();
            System.out.println(card.getValueString()+" of "+card.getSuit());
        }
        informAllPlayersOfAnEvent("--------------------------------------------");
        informAllPlayersOfAnEvent("The three cards have been revealed on the table.");
        informAllPlayersOfAnEvent("1. (NEW CARD) "+tableCards.get(0).getValueString()+" of "+tableCards.get(0).getSuit()+".");
        informAllPlayersOfAnEvent("2. (NEW CARD) "+tableCards.get(1).getValueString()+" of "+tableCards.get(1).getSuit()+".");
        informAllPlayersOfAnEvent("3. (NEW CARD) "+tableCards.get(2).getValueString()+" of "+tableCards.get(2).getSuit()+".");
        informAllPlayersOfAnEvent("--------------------------------------------");
        informAllPlayersOfAnEvent("Round #2 has started.......");
        this.setRaise(0);
        Round();
        informAllPlayersOfAnEvent("Round #2 is over.");
        informAllPlayersOfAnEvent("--------------------------------------------");
        informAllPlayersOfAnEvent("The four cards have been revealed on the table.");
        informAllPlayersOfAnEvent("1. "+tableCards.get(0).getValueString()+" of "+tableCards.get(0).getSuit()+".");
        informAllPlayersOfAnEvent("2. "+tableCards.get(1).getValueString()+" of "+tableCards.get(1).getSuit()+".");
        informAllPlayersOfAnEvent("3. "+tableCards.get(2).getValueString()+" of "+tableCards.get(2).getSuit()+".");
        informAllPlayersOfAnEvent("4. (NEW CARD) "+tableCards.get(3).getValueString()+" of "+tableCards.get(3).getSuit()+".");
        informAllPlayersOfAnEvent("--------------------------------------------");
        informAllPlayersOfAnEvent("Round #3 has started.......");
        this.setRaise(0);
        Round();
        informAllPlayersOfAnEvent("Round #3 is over.");
        informAllPlayersOfAnEvent("--------------------------------------------");
        informAllPlayersOfAnEvent("All cards have been revealed on the table.");
        informAllPlayersOfAnEvent("1. "+tableCards.get(0).getValueString()+" of "+tableCards.get(0).getSuit()+".");
        informAllPlayersOfAnEvent("2. "+tableCards.get(1).getValueString()+" of "+tableCards.get(1).getSuit()+".");
        informAllPlayersOfAnEvent("3. "+tableCards.get(2).getValueString()+" of "+tableCards.get(2).getSuit()+".");
        informAllPlayersOfAnEvent("4. "+tableCards.get(3).getValueString()+" of "+tableCards.get(3).getSuit()+".");
        informAllPlayersOfAnEvent("5. (NEW CARD) "+tableCards.get(4).getValueString()+" of "+tableCards.get(4).getSuit()+".");
        informAllPlayersOfAnEvent("--------------------------------------------");
        informAllPlayersOfAnEvent("Round #4 has started.......");
        this.setRaise(0);
        Round();
        informAllPlayersOfAnEvent("Round #4 is over.");
        calculateWinner();
        gameCounter++;
        usedCards.clear();
        tableCards.clear();
        resetPool();
        resetAmountPutIn();
        setRaise(0);
        winnings = 0;
        winners = null;
        Iterator<Player> playerIterator = players.iterator();
        while(playerIterator.hasNext()) {
            Player player = playerIterator.next();
            player.resetCards();
            player.setHandScore(0);
            player.resetAmountPutIn();
        }
        game();

    }
    public void resetPool(){
        pool = 0;
    }
    public void resetAmountPutIn(){
        amountThatPlayersNeedToPutIn = 0;
    }


    public void calculateWinner(){
        Iterator<Player> playerIterator = players.iterator();
        while(playerIterator.hasNext()){
            Player player = playerIterator.next();
            List<Card> temp = tableCards;
            temp.addAll(Arrays.asList(player.getCards()));
            HandCalculator handCalculator = new HandCalculator(temp);
            player.setHandScore(handCalculator.getScore());
            player.writeToClient("You hand score is: "+player.getHandScore());
        }
        int highestScore = 0;
        playerIterator = players.iterator();
        while(playerIterator.hasNext()) {
            Player player = playerIterator.next();
            if(player.getHandScore()>highestScore){
               highestScore = player.getHandScore();
            }
        }
        playerIterator = players.iterator();
        while(playerIterator.hasNext()) {
            Player player = playerIterator.next();
            if(player.getHandScore() == highestScore){
                winners.add(player);
            }
        }
        winnings = (int) Math.ceil(pool/winners.size());
        Iterator<Player> winnerIterator = winners.iterator();
        while(winnerIterator.hasNext()){
            Player winner = winnerIterator.next();
            if(winners.size()>1){
                informAllPlayersOfAnEvent("There has been a "+winners.size()+" way tie");
            }else {
                winner.writeToClient("You are the winner, congratulations!!");
                informAllPlayersOfAnEvent("The winner of the game was player "+winner.getID());
            }
            winner.setBank(1, winnings);
            winner.writeToClient("The pool was $"+getPool());
            winner.writeToClient("You have won the amount of $"+winnings+" and, you bank account is now $"+winner.getBank());
        }
        informAllPlayersOfAnEvent("Game #"+gameCounter+ " is over.");
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
            player.writeToClient("Please indicate what you would like to do? (type 'fold', 'raise', 'call', 'check', or 'disconnect')");
            player.writeToClient("Before making your choice note that the amount you need to put in to stay in the game is $"+getAmountThatPlayersNeedToPutIn()+", and the amount that you have put in is $"+player.getAmountPutIn()+".");
            player.writeToClient("(NOTE** if you type in an invalid response/inappropriate (i.e. typing cheque if you need to raise) then you will be kicked from the game!)");
            String choice = player.readFromClient();
            System.out.print("Player "+player.getID()+" has chosen to "+choice);
            if(choice.equals("fold")){
                informAllPlayersOfAnEvent("Player: "+player.getID()+" has folded.");
                player.writeToClient("You have been disconnected.");
                playerIterator.remove();
            }else if(choice.equals("raise")){
                player.writeToClient("You have $"+player.getBank()+" in your bank.");
                player.writeToClient("Please type out the amount that you would like to bet: (NOTE** if you type in an invalid amount then you will be kicked from the game!)");
                if(getRaise()<=player.getBank()){//when someone raises they first need to call the previous raise (that is the point of this first if statement.
                    player.setAmountPutIn(getRaise());
                    setNewPool(getRaise());
                    player.setBank(0,getRaise());
                }else{
                    player.writeToClient("You have raised more than you have in your bank and have thus been disconnected.");
                    try {
                        player.getSocket().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    playerIterator.remove();
                }
                setRaise(Integer.valueOf(player.readFromClient()));
                if(getRaise()<=player.getBank()){
                    player.setAmountPutIn(getRaise());
                    setAmountThatPlayersNeedToPutIn(getRaise());
                    player.setBank(0, getRaise());
                    setNewPool(getRaise());
                    informAllPlayersOfAnEvent("Player "+player.getID()+" has raised by "+getRaise()+", the pool is now: $"+getPool());
                    player.writeToClient("Your bank now has $"+player.getBank());
                    Round();
                }else{
                    player.writeToClient("You have raised more than you have in your bank and have thus been disconnected.");
                    try {
                        player.getSocket().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    playerIterator.remove();

                }
            }
            else if(choice.equals("call")){
                if(getRaise() <=player.getBank()){
                    player.setAmountPutIn(getRaise());
                    setNewPool(getRaise());
                    informAllPlayersOfAnEvent("Player "+player.getID()+" has called the raise of $"+getRaise()+", and the new pool is $"+getPool()+".");
                    player.setBank(0,getRaise());
                    player.writeToClient("Your bank now has $"+player.getBank());
                }
                else{
                    informAllPlayersOfAnEvent("Player: "+player.getID()+" been disconnected due to invalid response.");
                    player.writeToClient("The amount that you have called is more than you have in your bank and have thus been disconnected.");
                    try {
                        player.getSocket().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    playerIterator.remove();
                }
            }
            else if(choice.equals("check") && (getAmountThatPlayersNeedToPutIn() == player.getAmountPutIn())){
                informAllPlayersOfAnEvent("Player: "+player.getID()+" has checked.");
            }else if(choice.equals("disconnect")){
                informAllPlayersOfAnEvent("Player "+player.getID()+" has disconnected from the game.");
                player.writeToClient("You have been disconnected.");
                try {
                    player.getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                playerIterator.remove();
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
    public int getAmountThatPlayersNeedToPutIn(){
        return amountThatPlayersNeedToPutIn;
    }
    public void setAmountThatPlayersNeedToPutIn(int raise){
        amountThatPlayersNeedToPutIn =  amountThatPlayersNeedToPutIn +raise;
    }

    public int getRaise(){
        return raise;
    }
    public void setRaise(int raise){
        this.raise = raise;
    }
}



