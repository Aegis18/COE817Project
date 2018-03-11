import java.util.*;

public class HandCalculator {

    private ArrayList<Card> fullHand = new ArrayList<>();
    private ArrayList<Card> straightFlushTest = new ArrayList<>();
    private int highCard = 0;
    private int baseHandScore = 0;
    private int score = 0;

    private HandCalculator(ArrayList<Card> table, Player player){
        fullHand.addAll(table);
        ArrayList<Card> playerCards = new ArrayList<Card>(Arrays.asList(player.getCards()));
        fullHand.addAll(playerCards);
        fullHand.sort(Comparator.comparingInt(Card::getValue));
        Collections.reverse(fullHand);
        }

    public HandCalculator(ArrayList<Card> fullHand){
        this.fullHand = fullHand;
    }

    private boolean isStraightFlush(){
        Boolean flushTest = this.isFlush();
        Boolean straightTest = false;
        if(flushTest == true)
            straightTest = this.isStraight(straightFlushTest);

        if(straightTest == true)
            return true;
        else
            return false;

    }
    private boolean isFourOfAKind(){

        return false;
    }
    private boolean isFullHouse(){

        return false;
    }
    private boolean isFlush(){
        Iterator<Card> hand = fullHand.iterator();
        ArrayList<Card> hearts = new ArrayList<Card>();
        ArrayList<Card> diamonds = new ArrayList<Card>();
        ArrayList<Card> spades = new ArrayList<Card>();
        ArrayList<Card> clubs = new ArrayList<Card>();
        while(hand.hasNext()){
            Card card = hand.next();
            if(card.getSuit().equals("Hearts"))
                hearts.add(card);
            else if(card.getSuit().equals("Spades"))
                spades.add(card);
            else if(card.getSuit().equals("Clubs"))
                clubs.add(card);
            else
                diamonds.add(card);
        }
        if(hearts.size()==5) {
            hearts.sort(Comparator.comparingInt(Card::getValue));
            highCard = hearts.get(0).getValue();
            straightFlushTest = hearts;
            return true;
        }else if(diamonds.size()==5){
            diamonds.sort(Comparator.comparingInt(Card::getValue));
            highCard = diamonds.get(0).getValue();
            straightFlushTest = diamonds;
            return true;
        }else if(spades.size()==5){
            spades.sort(Comparator.comparingInt(Card::getValue));
            highCard = spades.get(0).getValue();
            straightFlushTest = spades;
            return true;
        }else if(clubs.size()==5){
            clubs.sort(Comparator.comparingInt(Card::getValue));
            highCard = clubs.get(0).getValue();
            straightFlushTest = clubs;
            return true;
        } else
            return false;
    }
    private boolean isStraight(){
        ArrayList<Card> testHand = fullHand;
        ArrayList<Card> straight = new ArrayList<Card>();
        Boolean trueStraight = false; //if this is ever set to true the method knows that there is a true straight
        int x = 0;      //counter to make sure straight ArrayList is no more than 5 (0,1,2,3,4) elements but is used for checking if a straight has been found.

        for(int i = 0; i<=testHand.size(); i++){
            if((testHand.get(i).getValue() == (testHand.get(i+1).getValue()+1)) && x!=4){
                straight.add(x, testHand.get(i));
                x++;
            }else if(x==4)
                break;
        }
        //now we need to check if the straight is truly a sequence of value and something like (12,11,10,9,6,5,4) does not flag true as a straight
        if((straight.get(0).getValue()==(straight.get(1).getValue()+1)) && (straight.get(0).getValue()==(straight.get(2).getValue()+2)) && (straight.get(0).getValue()==(straight.get(3).getValue()+3)) && (straight.get(0).getValue()==(straight.get(4).getValue()+4))){
            trueStraight = true;
        }

        if(x==4 && trueStraight == true){
            highCard = straight.get(0).getValue();//this is used to calculate the score in the getScore method
            return true;
        }else
            return false;

    }
    private boolean isStraight(ArrayList straightFlushTest){//this mmethod is only used by the isStraightFlush method to check whether a given flush is also a straight.
        ArrayList<Card> testHand = straightFlushTest;
        ArrayList<Card> straight = new ArrayList<Card>();
        Boolean trueStraight = false; //if this is ever set to true the method knows that there is a true straight
        int x = 0;      //counter to make sure straight ArrayList is no more than 5 (0,1,2,3,4) elements but is used for checking if a straight has been found.

        for(int i = 0; i<=testHand.size(); i++){
            if((testHand.get(i).getValue() == (testHand.get(i+1).getValue()+1)) && x!=4){
                straight.add(x, testHand.get(i));
                x++;
            }else if(x==4)
                break;
        }
        //now we need to check if the straight is truly a sequence of value and something like (12,11,10,9,6,5,4) does not flag true as a straight
        if((straight.get(0).getValue()==(straight.get(1).getValue()+1)) && (straight.get(0).getValue()==(straight.get(2).getValue()+2)) && (straight.get(0).getValue()==(straight.get(3).getValue()+3)) && (straight.get(0).getValue()==(straight.get(4).getValue()+4))){
            trueStraight = true;
        }

        if(x==4 && trueStraight == true){
            highCard = straight.get(0).getValue();//this is used to calculate the score in the getScore method
            return true;
        }else
            return false;

    }
    private boolean isThreeOfAKind(){

        return false;
    }
    private boolean isDoublePairs(){

        return false;
    }
    private boolean isPair(){
        return false;
    }
    private int HighCard(){
        highCard = fullHand.get(0).getValue();
        return highCard;
    }


    public int getScore(){
        if(this.isStraightFlush() == true)
           score = 800 + highCard;
        else if(this.isFourOfAKind() == true)
            score = 700 + highCard;
        else if(this.isFullHouse() == true)
            score = 600 + highCard;
        else if(this.isFlush() == true)
            score = 500 + highCard;
        else if (this.isStraight() == true)
            score = 400 + highCard;
        else if(this.isThreeOfAKind() == true)
            score = 300 + highCard;
        else if(this.isDoublePairs() == true)
            score = 200 + highCard;
        else if(this.isPair() == true)
            score = 100 +highCard;
        else
            score = this.HighCard();

        return score;

    }
}
