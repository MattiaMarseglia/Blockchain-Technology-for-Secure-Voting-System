/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

/**
 *
 * @author Kryptos
 */

/**
 * Questa classe contiene vari metodi utili per il Votante. L'utilizzo di questi
 * metodi è dato dalla classe SSLClient
 * che simula effettivamente l'onesto votante all'interno del sistema.
 * I metodi al suo interno sono relativi all'invio del voto espresso e
 * successivamente della conferma.
 * L'unico attributo del Votante risulta essere la Public Key della Società,
 * utilizzata per la cifratura del voto.
 */
public class toVote {

    static PublicKey PkSocietà = null;

    /**
     * Metodo per settare l'attributo statico della classe per il salvataggio della
     * PublicKey della Società.
     * 
     * @param PkSocietà
     */
    public static void setPkSocietà(PublicKey PkSocietà) {
        toVote.PkSocietà = PkSocietà;
    }

    /**
     * Metodo tramite il quale un votante genera l'output da inviare per esprimere
     * un voto. La funzione viene utilizzata dall'SSLClient che passerà
     * come parametri la private key associata al votante (generata in SSLClient),
     * la public key della società con la quale viene
     * effettuata la cifratura, il voto da inviare e la publick key associata al
     * cliente (generata insieme alla SK in SSLClient).
     * Inizialmente generiamo la randomness r, che viene cifrata con la public key
     * del votante ottenendo R=Enc(PkV,r). La randomness r viene
     * concatenata con il voto da esprimere; la concatenazione viene cifrata con
     * SHA256, ottenendo quindi C=SHA256(r||x). Successivamente
     * viene effettuata la cifratura con ECIES di C con la Public Key della Società,
     * quindi otteniamo E=Enc(PkSocietà,C). A questo punto
     * c'è la parte di firma del messaggio m = (R,E) con ECDSA, quindi potremo
     * ritornare il contenuto da inviare sulla ItalyChain, ovvero
     * (m,Sigma) dove Sigma = Sig(SecretKeyVotante, m).
     * 
     * @param privateKey
     * @param PublicKeySocietà
     * @param voto
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static byte[] vote(PrivateKey privateKey, PublicKey PublicKeySocietà, String voto, PublicKey publicKey)
            throws Exception {

        SecureRandom random = new SecureRandom();
        byte Randomness[] = new byte[32];
        random.nextBytes(Randomness);

        byte[] encryptedRandomness = Cryptare.encrypt(Randomness, publicKey);

        byte[] randVoto = Utils.concatBytes(Randomness, voto.getBytes());

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        digest.update(randVoto);

        byte[] hashOfRandVoto = digest.digest();

        byte[] encryptedCypherText = Cryptare.encrypt(hashOfRandVoto, PublicKeySocietà);

        byte[] result = Utils.concatBytes(encryptedRandomness, encryptedCypherText);

        byte[] signature = Cryptare.signature(privateKey, result);

        return Utils.concatBytes(result, signature);
    }

    /**
     * Metodo per inviare la conferma del voto. Questa conferma sarà effettuata
     * attraverso l’invio della randomness r.
     * Dalla blockchain verrà prelevato il valore R associato alla specifica chiave
     * pubblica del votante passata come parametro,
     * dal quale si potrà risalire con un algoritmo di decifratura alla randomness
     * r, attraverso la chiave privata del votante stesso, passata come parametro.
     * Una volta decifrato r, il valore viene ritornato alla classe SSLClient che
     * utilizza questo metodo, la quale provvederà a firmare il messaggio
     * e inviando quindi sulla chain (r,Sigma) con Sigma=Sig(SkV,r), con firma
     * ottenuta sempre con l'algoritmo ECDSA.
     * 
     * @param privateKey
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static byte[] confirmVote(PrivateKey privateKey, PublicKey publicKey) throws Exception {
        byte[] encryptedMessage = null;
        byte[] possiblePublicKey = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("ItalyChain.txt"));
            possiblePublicKey = (byte[]) ois.readObject();
            while (Arrays.equals(possiblePublicKey, "T2-T3".getBytes()) == false) {
                if (Arrays.equals(possiblePublicKey, publicKey.getEncoded())) {
                    byte[] tmp = (byte[]) ois.readObject();
                    encryptedMessage = Arrays.copyOfRange(tmp, 0, 234);
                    break;
                } else
                    ois.readObject();
                possiblePublicKey = (byte[]) ois.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] randomness = null;
        if (encryptedMessage != null) {
            randomness = Cryptare.decrypt(Arrays.copyOfRange(encryptedMessage, 0, 117), privateKey);
            System.out.println("Randomness ottenuta correttamente dalla chain ");
        }
        return randomness;
    }
}
