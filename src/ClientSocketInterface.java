import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ClientSocketInterface implements Runnable {

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
    public ClientSocketInterface(Socket socket) {
        this.socket = socket;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
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
        }
    }


    @Override
    public void run(){
        init();
        String clientInput = "";

        try {
            while ((clientInput = bufferedReader.readLine()) != null) {

            }
        }catch(IOException e){
//            PokerHouse.getInstance().removePlayer(socket);
        }
    }
}
