import java.util.ArrayList;
import java.util.Comparator;

public class testMain {

    public static void main(String[] args){
        ArrayList<Card> cards = new ArrayList<Card>();
        try {
            Card card0 = new Card(12);
            Card card1 = new Card(11);
            Card card2 = new Card(10);
            Card card3 = new Card(9);
            Card card4 = new Card(8);
            cards.add(card0);
            cards.add(card1);
            cards.add(card2);
            cards.add(card3);
            cards.add(card4);
        }catch(Exception e){}

        cards.sort(Comparator.comparingInt(Card::getValue));
        HandCalculator handCalculator = new HandCalculator(cards);
        if(handCalculator.isStraight() == true)
            System.out.println("there is a straight");


        for(Card card : cards){
            System.out.println(card.getValue());

        }



    }
}
