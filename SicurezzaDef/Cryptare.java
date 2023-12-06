/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.*;
import java.security.NoSuchProviderException;
import java.util.Arrays;

/**
 * Classe che contiene le varie funzioni utilizzare dagli attori in gioco del
 * sistema per la cifratura, decifratura, firma e verifica della firma.
 * 
 * @author Kryptos
 */
public class Cryptare {

    /**
     * Funzione che ritorna la firma di un messaggio con una private key (passati
     * entrambi come parametri) attraverso l'algoritmo ECDSA.
     * 
     * @param privateKey
     * @param message
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws NoSuchProviderException
     */
    public static byte[] signature(PrivateKey privateKey, byte[] message)throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
        // sign using the private key
        Security.addProvider(new BouncyCastleProvider());
        Signature sig = Signature.getInstance("ECDSA", "BC");
        sig.initSign(privateKey);
        sig.update(message);
        byte[] signature = sig.sign();

        return signature;
    }

    /**
     * Funzione che ritorna un boolean che verifica la firma di un messaggio
     * attraverso la public key passata come parametro.
     * 
     * @param publicKey
     * @param signature
     * @param message
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws NoSuchProviderException
     */
    public static Boolean verifySignature(PublicKey publicKey, byte[] signature, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleProvider());
        Signature sig = Signature.getInstance("ECDSA", "BC");
        // verify signature using the public key
        sig.initVerify(publicKey);
        sig.update(message);
        boolean keyPairMatches = sig.verify(signature);
        return keyPairMatches;
    }

    /**
     * Funzione che permette di cifrare un plaintext (parametro message) con una
     * public key attraverso l'algoritmo ECIES.
     * Ritorna il ciphertext generato dalla cifratura.
     * 
     * @param message
     * @param publicKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws NoSuchProviderException
     */
    public static byte[] encrypt(byte[] message, PublicKey publicKey) throws NoSuchProviderException {
        byte cipherText[] = null;
        try {
            Security.addProvider(new BouncyCastleProvider());
            Cipher iesCipher = Cipher.getInstance("ECIES", "BC");
            iesCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cipherText = new byte[iesCipher.getOutputSize(message.length)];
            int ctlength = iesCipher.update(message, 0, message.length, cipherText, 0);
            ctlength += iesCipher.doFinal(cipherText, ctlength);
        } catch (Exception ex) {
            Logger.getLogger(Cryptare.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cipherText;

    }

    /**
     * Funzione che permette di decifrare un ciphertext cifrato con algoritmo ECIES
     * attraverso la private key.
     * Ritorna il plaintext generato dalla decifratura.
     * 
     * @param cipherText
     * @param privateKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws NoSuchProviderException
     */
    public static byte[] decrypt(byte cipherText[], PrivateKey privateKey) throws NoSuchProviderException {
        byte[] plainText = null;
        
        try {
            Security.addProvider(new BouncyCastleProvider());
            Cipher iesCipher2 = Cipher.getInstance("ECIES", "BC");
            iesCipher2.init(Cipher.DECRYPT_MODE, privateKey);
            plainText = new byte[iesCipher2.getOutputSize(cipherText.length)];
            int ctlength2 = iesCipher2.update(cipherText, 0, cipherText.length, plainText, 0);
            ctlength2 += iesCipher2.doFinal(plainText, ctlength2);
        } catch (Exception ex) {
            Logger.getLogger(Cryptare.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Arrays.copyOfRange(plainText, 0, plainText.length - 1);
    }

}
