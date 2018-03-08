import java.util.Random;

public class Card {

    private String suit;
    private int value;

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
            case 3:
                suit = "Spades";
                break;
            case 4:
                suit = "Clubs";
                break;
            default:
                throw new NoCardException();
        }
    }

    public class NoCardException extends Exception{

    }

    @Override
    public String toString() {
        return value + " of " + suit;
    }

    public static int generateCard(){
        Random random = new Random(123456789);
        int card =0;
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
}
