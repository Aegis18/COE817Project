import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Player {

    private static final String RSA = "RSA";
    private static final String DES = "DES";

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    private Cipher encryptCipher;
    private Cipher decryptCipher;

    private KeyPair keyPair;
    private KeyPairGenerator keyPairGenerator;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private X509EncodedKeySpec x509EncodedKeySpec;
    private PublicKey clientPublicKey;
    private SecretKey secretKey;

    private int ID;
    private Card[] cards;
    private PokerHouse pokerHouse;

    private int bank = 100000;
    private int amountPutIn = 0;
    private int handScore = 0;

    private String message;

    public Player(Socket socket, int ID){
        this.socket = socket;
        this.ID = ID;
        this.cards = new Card[2];
        pokerHouse = PokerHouse.getInstance();
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            encryptCipher = Cipher.getInstance(RSA);
            decryptCipher = Cipher.getInstance(RSA);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        init();
    }

    private void init() {
        try {
            //************************************************
            //GENERATE THE PUBLIC/PRIVATE KEYS
            //************************************************
            keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            keyPairGenerator.initialize(1024);
            keyPair = keyPairGenerator.generateKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();

            System.out.println("************************************************");
            System.out.println("PUBLIC KEY: " + publicKey);
            System.out.println("************************************************");
            System.out.println("");
            System.out.println("************************************************");
            System.out.println("PRIVATE KEY: " + privateKey);
            System.out.println("************************************************");
            System.out.println("");

            //************************************************
            //SEND THE PUBLIC KEY
            //************************************************

            printWriter.println(Base64.getEncoder().encodeToString(publicKey.getEncoded()));

            //************************************************
            //GET PUBLIC KEY OF CLIENT
            //************************************************

            String input = bufferedReader.readLine();

            x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(input));
            clientPublicKey = KeyFactory.getInstance(RSA).generatePublic(x509EncodedKeySpec);

            System.out.println("************************************************");
            System.out.println("Client Public Key: " + clientPublicKey);
            System.out.println("************************************************");
            System.out.println("");

            //************************************************
            //SET UP CIPHER
            //************************************************

            encryptCipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);

            //************************************************
            //GENERATE DES KEY
            //************************************************

            secretKey = KeyGenerator.getInstance(DES).generateKey();

            System.out.println("************************************************");
            System.out.println("GENERATED DES KEY: " + secretKey);
            System.out.println("************************************************");
            System.out.println("");

            //************************************************
            //SEND DES KEY
            //************************************************

            byte[] encryptedSecretKey = encryptCipher.doFinal(secretKey.getEncoded());
            printWriter.println(Base64.getEncoder().encodeToString(encryptedSecretKey));

            //************************************************
            //SET UP DES CIPHER
            //************************************************

            encryptCipher = Cipher.getInstance(DES);
            decryptCipher = Cipher.getInstance(DES);

            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public String readFromClient(){

        //************************************************
        //READ CLIENTS INPUT IF EMPTY THROW ILLEGAL ARGUMENT EXCEPTION
        //************************************************
        try {
            String encryptedMessage;
            while((encryptedMessage = bufferedReader.readLine())!=null) {
                byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage.getBytes());
                byte[] messageBytes = decryptCipher.doFinal(encryptedBytes);
                String message = new String(messageBytes);
                this.message = message;
            }

        }catch (IOException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return message;
    }

    public void writeToClient(String message){
        //************************************************
        //SEND MESSAGE TO CLIENT
        //************************************************
        byte[] messageBytes = message.getBytes();
        try{
            byte[] encryptedMessage = encryptCipher.doFinal(messageBytes);
            encryptedMessage = Base64.getEncoder().encode(encryptedMessage);
            String encryptedString = new String(encryptedMessage);
            printWriter.println(encryptedString);
        }catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Card[] getCards() {
        return cards;
    }

    public void setCards(Card[] cards) {
        if(cards.length > 2)
            throw new IllegalArgumentException();
        else
            this.cards = cards;
    }

    public int getBank(){
        return bank;
    }

    public void setBank(int type , int amount){

        if(type == 0) //if the player has bet
            bank = bank - amount;
        else          //if the player wins the pot
            bank = bank + amount;
    }

    public Socket getSocket() {
        return socket;
    }

    public int getAmountPutIn() {
        return amountPutIn;
    }
    public void setAmountPutIn(int amount){
        amountPutIn = amountPutIn + amount;
    }

    public int getHandScore(){
        return handScore;
    }
    public void setHandScore(int score){
        handScore = score;
    }
}
