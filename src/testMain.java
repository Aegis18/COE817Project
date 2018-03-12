import java.util.ArrayList;
import java.util.Comparator;

public class testMain {

    public static void main(String[] args){
        ArrayList<Card> cards = new ArrayList<Card>();
        try {
            cards.add(new Card(1));
            cards.add(new Card(1));
            cards.add(new Card(1));
            cards.add(new Card(2));
            cards.add(new Card(5));
            cards.add(new Card(7));
            cards.add(new Card(7));

            cards.sort(Comparator.comparingInt(Card::getValue));
            HandCalculator handCalculator = new HandCalculator(cards);
            if(handCalculator.isFullHouse()){
                System.out.println("True");
            }
            else
                System.out.println("false");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
