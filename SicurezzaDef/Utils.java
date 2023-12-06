
//package projectsecurity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.PrivateKey;

public class Utils {
    private static String digits = "0123456789abcdef";

    public static String toHex(byte[] data, int length) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i != length; i++) {
            int v = data[i] & 0xff;
            buf.append(digits.charAt(v >> 4));
            buf.append(digits.charAt(v & 0xf));
        }
        return buf.toString();
    }

    public static String toHex(byte[] data) {
        return toHex(data, data.length);
    }

    public static byte[] toByteArray(
            String string) {
        byte[] bytes = new byte[string.length()];
        char[] chars = string.toCharArray();

        for (int i = 0; i != chars.length; i++) {
            bytes[i] = (byte) chars[i];
        }

        return bytes;
    }

    public static byte[] concatBytes(byte[] first, byte[] second) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(first);
        outputStream.write(second);
        return outputStream.toByteArray();
    }

    /**
     * Questa funzione è utile per poter ottenere la propria chiave pubblica dal
     * proprio keystore.
     * 
     * @param nomeFile riferimento al keystore
     * @param password password di accesso
     * @param alias    alias identificativo
     * @return la chiave pubblica
     * @throws Exception
     */
    public static PublicKey obtainPublicFromKeystore(String nomeFile, String password, String alias) throws Exception {
        File file = new File(nomeFile);
        FileInputStream is = new FileInputStream(file);
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, password.toCharArray());
        // Certificato della public key
        X509Certificate cert = (X509Certificate) keystore.getCertificate(alias);
        PublicKey publicKey = cert.getPublicKey(); // chiave pubblica
        return publicKey;

    }

    /**
     * Questa funzione è utile per poter ottenere la propria chiave privata dal
     * proprio keystore.
     * 
     * @param nomeFile riferimento al keystore
     * @param password password di accesso
     * @param alias    alias identificativo
     * @return la chiave privata
     * @throws Exception
     */
    public static PrivateKey obtainPrivateFromKeystore(String nomeFile, String password, String alias)
            throws Exception {
        File file = new File(nomeFile);
        FileInputStream is = new FileInputStream(file);
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, password.toCharArray());
        PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, password.toCharArray()); // chiave privata
        return privateKey;

    }

    /**
     * Questa funzione è utile per poter ottenere la chiave pubblica dall'alias
     * passato come parametro nel proprio truststore.
     * 
     * @param nomeFile riferimento al trustore
     * @param password password di accesso
     * @param alias    alias identificativo
     * @return la chiave pubblica
     * @throws Exception
     */
    public static PublicKey obtainValuesFromTruststore(String nomeFile, String password, String alias)
            throws Exception {
        File file = new File(nomeFile);
        FileInputStream is = new FileInputStream(file);
        KeyStore truststore = KeyStore.getInstance(KeyStore.getDefaultType());
        truststore.load(is, password.toCharArray());
        // certificato della chiave pubblica
        X509Certificate cert = (X509Certificate) truststore.getCertificate(alias);
        PublicKey publicKey = cert.getPublicKey(); // chiave pubblica della società

        return publicKey;

    }

    public static SSLSocketFactory obtainClientSocketFactory(String keyStoreFile, String alias)
            throws IOException, GeneralSecurityException {

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(
                new X509KeyManager[] { new MyKeyManager(keyStoreFile,
                        "c7bc135a855d40b7c799b5e7e217c8730054a32d46bb43ba6e905f435f1a2867".toCharArray(), alias) },
                null, new SecureRandom());
        return sslContext.getSocketFactory();

    }

    public static SSLServerSocketFactory obtainServerSocketFactory(String keyStoreFile, String alias)
            throws IOException, GeneralSecurityException {

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(
                new X509KeyManager[] { new MyKeyManager(keyStoreFile,
                        "c7bc135a855d40b7c799b5e7e217c8730054a32d46bb43ba6e905f435f1a2867".toCharArray(), alias) },
                null, new SecureRandom());
        return sslContext.getServerSocketFactory();

    }

}
