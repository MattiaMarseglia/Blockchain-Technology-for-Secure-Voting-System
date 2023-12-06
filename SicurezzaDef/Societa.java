
import java.io.*;

import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.cert.X509Certificate;
import java.security.PrivateKey;
import java.security.KeyStore;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author Kryptos
 */

public class Societa {
    /*
     * La classe Societa ha lo scopo di replicare in codice le azioni che dovrebbe
     * effettuare la Societa descritta nel documento.
     * In particolare ha lo scopo di rilasciare la propria chiave segreta al termine
     * del referendum, permettendo cosi la decifratura dei voti.
     * 
     * Nella prima parte verranno caricati da file quelli che sono i certificati
     * della Societa, ricavandone la chiave pubblica e la chiave privata
     * 
     * Per simulare il termine del referendum, il programma si ferma per 60 secondi,
     * permettendo ai Votanti di, appunto, votare e mandare la randomness in
     * seguito. Dopo i 60 secondi viene stabilita una comunicazione con il server e
     * si procede all'invio della PrivateKey
     * 
     * 
     */

    public static void main(String[] args) throws Exception {
        String nomeFile = "Societykeystore.jks"; // args[0] contiene l'ID del votante
        String password = "c7bc135a855d40b7c799b5e7e217c8730054a32d46bb43ba6e905f435f1a2867";
        String alias = "sslSociety";
        PrivateKey SocietaPrivateKey = null;
        PublicKey SocietaPublicKey = null;
        try {

            // getting the key
            SocietaPublicKey = Utils.obtainPublicFromKeystore(nomeFile, password, alias);
            SocietaPrivateKey = Utils.obtainPrivateFromKeystore(nomeFile, password, alias);

        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("Attendo fase T3-T4 per inviare Private Key");
        TimeUnit.MILLISECONDS.sleep(60000);
        System.out.println("Attesa terminata, inizio comunicazione con Validatore");
        SSLSocketFactory sockfact = Utils.obtainClientSocketFactory(nomeFile, alias); // similar to the server except
        // use SSLSocketFactory instead of SSLSocketServerFactory
        SSLSocket SocSock = (SSLSocket) sockfact.createSocket("localhost", 4000); // specify host and port
        SocSock.startHandshake();

        char esito = 'c';
        OutputStream out = SocSock.getOutputStream();
        InputStream in = SocSock.getInputStream();
        while (!String.valueOf(esito).equals("0")) {

            byte[] signature = Cryptare.signature(SocietaPrivateKey, SocietaPrivateKey.getEncoded());

            byte[] result = Utils.concatBytes(SocietaPrivateKey.getEncoded(), signature);

            out.write(result);

            out.write(Utils.toByteArray("\n"));

            TimeUnit.MILLISECONDS.sleep(7000);

            out.flush();

            esito = (char) in.read();

        }
        System.out.println(" Private Key correttamente inviata dalla Societa \nArrivederci!!!");

    }
}
