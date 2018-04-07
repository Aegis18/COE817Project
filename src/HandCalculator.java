import java.util.*;

public class HandCalculator {

    private ArrayList<Card> fullHand;
    private ArrayList<Card> straightFlushTest;
    private ArrayList<Card> twoPairtest;
    private int highCard = 0;
    private int score = 0;

     public HandCalculator(ArrayList<Card> table, Player player){
        fullHand = new ArrayList<>();
        straightFlushTest = new ArrayList<>();
        twoPairtest = new ArrayList<>();
        fullHand.addAll(table);
        ArrayList<Card> playerCards = new ArrayList<Card>(Arrays.asList(player.getCards()));
        fullHand.addAll(playerCards);
        fullHand.sort(Comparator.comparingInt(Card::getValue));
        Collections.reverse(fullHand);
    }

    public HandCalculator(List<Card> allCards){
        fullHand = new ArrayList<>();
        straightFlushTest = new ArrayList<>();
        twoPairtest = new ArrayList<>();
        fullHand.addAll(allCards);
        fullHand.sort(Comparator.comparingInt(Card::getValue));
        Collections.reverse(fullHand);
    }

    private boolean isStraightFlush(){
         return isFlush() && isStraight(straightFlushTest);
    }

    private boolean isFourOfAKind(){
        ArrayList<Card> testFourOfAKind = fullHand;
        int handSize = testFourOfAKind.size();
        int fourCounter = 0;
        for(int i = 0; i<handSize;i++){
            for(int j = 0; j<handSize; j++){
                if(testFourOfAKind.get(i).getValue() == testFourOfAKind.get(j).getValue()){
                    fourCounter++;
                }
            }
            if(fourCounter == 4){
                highCard = testFourOfAKind.get(i).getValue();
                return true;
            }
            else
                fourCounter = 0;
        }
        return false;
    }
    public boolean isFullHouse(){
        return(isThreeOfAKind() && isPair(twoPairtest));
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
            else if(card.getSuit().equals("Diamonds"))
                diamonds.add(card);
        }

        System.out.println(hearts.size());
        if(hearts.size()>=5) {
            hearts.sort(Comparator.comparingInt(Card::getValue));
            highCard = hearts.get(0).getValue();
            straightFlushTest = hearts;
            return true;
        }else if(diamonds.size()>=5){
            diamonds.sort(Comparator.comparingInt(Card::getValue));
            highCard = diamonds.get(0).getValue();
            straightFlushTest = diamonds;
            return true;
        }else if(spades.size()>=5){
            spades.sort(Comparator.comparingInt(Card::getValue));
            highCard = spades.get(0).getValue();
            straightFlushTest = spades;
            return true;
        }else if(clubs.size()>=5){
            clubs.sort(Comparator.comparingInt(Card::getValue));
            highCard = clubs.get(0).getValue();
            straightFlushTest = clubs;
            return true;
        } else
            return false;
    }

    private boolean isStraight(){
        ArrayList<Card> testHand = new ArrayList<>(fullHand);
        ArrayList<Card> straight = new ArrayList<Card>();
        Boolean trueStraight = false; //if this is ever set to true the method knows that there is a true straight
         //counter to make sure straight ArrayList is no more than 5 (0,1,2,3,4) elements but is used for checking if a straight has been found.

        for(int i = 0; i<testHand.size(); i++){
            if((i+5) <= 7){
                if((testHand.get(i).getValue()==(testHand.get(i+1).getValue()+1)) && (testHand.get(i).getValue()==(testHand.get(i+2).getValue()+2)) && (testHand.get(i).getValue()==(testHand.get(i+3).getValue()+3)) && (testHand.get(i).getValue()==(testHand.get(i+4).getValue()+4))){
                    highCard = testHand.get(i).getValue();
                    System.out.println("highcard = " +highCard);
                    return true;
                }
            }else{
                break;
            }
        }
        return false;
    }

    private boolean isStraight(ArrayList straightFlushTest){//this mmethod is only used by the isStraightFlush method to check whether a given flush is also a straight.
        ArrayList<Card> testHand = new ArrayList<>(straightFlushTest);
        Collections.reverse(testHand);
        for(int i = 0; i<testHand.size(); i++){
            if((i+5) <= 7){
                if((testHand.get(i).getValue()==(testHand.get(i+1).getValue()+1)) && (testHand.get(i).getValue()==(testHand.get(i+2).getValue()+2)) && (testHand.get(i).getValue()==(testHand.get(i+3).getValue()+3)) && (testHand.get(i).getValue()==(testHand.get(i+4).getValue()+4))){
                    highCard = testHand.get(i).getValue();
                    System.out.println("highcard = " +highCard);
                    return true;
                }
            }else{
                break;
            }
        }
        return false;
    }
    private boolean isThreeOfAKind(){
        ArrayList<Card> testThree = new ArrayList<>(fullHand);
        int handSize = testThree.size();
        int threeCounter = 0;
        for(int i = 0; i<handSize;i++){
            for(int j = 0; j<handSize; j++){
                if(testThree.get(i).getValue() == testThree.get(j).getValue()){
                    threeCounter++;
                }
            }
            if(threeCounter == 3){
                highCard = testThree.get(i).getValue();
                Iterator<Card> hand = testThree.iterator();
                while(hand.hasNext()){
                    Card card = hand.next();
                    if(card.getValue() == highCard)
                        hand.remove();
                }
                twoPairtest = testThree;
                return true;
            }
            else
                threeCounter = 0;
        }
        return false;
    }
    private boolean isDoublePair(){
        return isPair() && isPair(twoPairtest);
//        if(isPair()){
//           if(isPair(twoPairtest))
//               return true;
//           else
//               return false;
//        }else
//        return false;
    }
    private boolean isPair(){
        ArrayList<Card> testPair = new ArrayList<Card>(fullHand);
        int handSize = testPair.size();
        int pairCounter = 0;
        for(int i = 0; i<handSize;i++){
            for(int j = 0; j<handSize; j++){
                if(testPair.get(i).getValue() == testPair.get(j).getValue()){
                    pairCounter++;
                }
            }
            if(pairCounter == 2){
                highCard = testPair.get(i).getValue();
                Iterator<Card> hand = testPair.iterator();
                while(hand.hasNext()){
                    Card card = hand.next();
                    if(card.getValue() == highCard)
                        hand.remove();
                }
                twoPairtest = testPair;
                return true;
            }
            else
                pairCounter = 0;
        }
        return false;
    }

    private boolean isPair(ArrayList doubleTestPair){
        ArrayList<Card> testPair = new ArrayList<Card>(doubleTestPair);
        int handSize = testPair.size();
        int pairCounter = 0;
        for(int i = 0; i<handSize;i++){
            for(int j = 0; j<handSize; j++){
                if(testPair.get(i).getValue() == testPair.get(j).getValue()){
                    pairCounter++;
                }
            }
            if(pairCounter == 2){
               if(testPair.get(i).getValue() > highCard)
                    highCard = testPair.get(i).getValue();
               return true;
            }
            else
                pairCounter = 0;
        }
        return false;
    }
    private int HighCard(){
        return fullHand.get(0).getValue();
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
        else if(this.isDoublePair() == true)
            score = 200 + highCard;
        else if(this.isPair() == true)
            score = 100 + highCard;
        else
            score = this.HighCard();

        return score;

    }
}
