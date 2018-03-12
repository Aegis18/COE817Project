import java.util.Random;

public final class Card {

    private String suit;
    private int value;
    private static int card = 0;

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
       Random random = new Random(123456789);
       card = random.nextInt(51) + 1;
       return card;
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

    public void setValue(int value) {
        this.value = value;
    }

    public int getCard(){
        return card;
    }

}
