import java.util.Random;

public final class Card {

    private String suit;
    private int value;
    private int card = 0;

    public Card(int cardValue) throws NoCardException {
        final int suitNum = (int)cardValue/13;
        value = cardValue%13;

        switch(suitNum){
            case 0:
                suit = "Hearts";
                break;
            case 1:
                suit = "Diamonds";
                break;
            case 2:
                suit = "Spades";
                break;
            case 3:
                suit = "Clubs";
                break;
            default:
                throw new NoCardException(suitNum);
        }
    }

    public class NoCardException extends Exception{
        public NoCardException (int suitNum){
            System.out.println("suitNum: " + suitNum);
        }

    }

    @Override
    public String toString() {
        return value + " of " + suit;
    }

    public static int generateCard(){
       Random random = new Random();
       int cardNum = random.nextInt(51) + 1;
       return cardNum;
    }

    public String getSuit() {
        return suit;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }

    public int getValue() {
        return value;
    }
    public String getValueString(){
        String stringValue = "";
        if(value == 0)
            stringValue = "2";
        else if(value == 1)
            stringValue = "3";
        else if(value == 2)
            stringValue = "4";
        else if(value == 3)
            stringValue = "5";
        else if(value == 4)
            stringValue = "6";
        else if(value == 5)
            stringValue = "7";
        else if(value == 6)
            stringValue = "8";
        else if(value == 7)
            stringValue = "9";
        else if(value == 8)
            stringValue = "10";
        else if(value == 9)
            stringValue = "Jack";
        else if(value == 10)
            stringValue = "Queen";
        else if(value == 11)
            stringValue = "King";
        else
            stringValue = "Ace";

        return stringValue;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getCard(){
        return card;
    }

}
