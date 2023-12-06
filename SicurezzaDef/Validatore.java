import java.io.*;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.util.concurrent.TimeUnit;

import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.cert.X509Certificate;
import java.security.PrivateKey;
import java.security.KeyStore;

import java.util.Arrays;

import java.security.spec.PKCS8EncodedKeySpec;

/**
 *
 * @author Kryptos
 */

public class Validatore {
    public static String s = "Server";

    /**
     * ProtocolWriteOnChain è una funzione che permette di scrivere sulla ItalyChain
     * (identificata come un file di testo)
     * si distinguono due casi:
     * -il caso in cui il file non è mai stato creato, prima viene creato (con
     * l'header) e dopo si scrive la transazione.
     * -il caso in cui il file esiste già, viene aperto in modalità append
     * sfruttando una classe da noi creata (evita di scrivere l'header) e scrive la
     * transazione.
     * 
     * @param transaction
     * @param VoterPK
     * @return
     * @throws Exception
     */
    static Boolean ProtocolWriteOnChain(byte[] transaction, PublicKey VoterPK) throws Exception {
        File file1 = new File("ItalyChain.txt");

        // Checks if File exists
        if (file1.exists() && !file1.isDirectory()) {
            try {
                AppendingObjectOutputStream outputStreamExist = new AppendingObjectOutputStream(
                        new FileOutputStream("ItalyChain.txt", true));
                outputStreamExist.writeObject(VoterPK.getEncoded());
                outputStreamExist.writeObject(transaction);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            try {
                ObjectOutputStream outputStreamNew = new ObjectOutputStream(new FileOutputStream("ItalyChain.txt"));
                outputStreamNew.writeObject(VoterPK.getEncoded());
                outputStreamNew.writeObject(transaction);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    /**
     * in questo protocollo il Votante manda un array di bytes di una dimensione
     * fissata come parametro firmato al Validatore.
     * Questo controlla che la firma sia coretta e lo ritorna al chiamante al quale
     * poi sono delegati comportamenti successivi
     * 
     * @param sSock
     * @param VoterPK
     * @return
     * @throws Exception
     */
    static byte[] ClientComunication(Socket sSock, PublicKey VoterPK, int messageDimension) throws Exception {

        System.out.println("session started.");
        // convert the socket to input and output stream
        InputStream in = sSock.getInputStream();
        // messaggio da ricevere 234 32
        byte[] message = new byte[messageDimension];
        int tmp = 0;
        int i = 0;
        TimeUnit.MILLISECONDS.sleep(1000);
        // con il seguente for si apprendono tutti i byte del messaggio nell'apposito
        // array
        for (i = 0; i < messageDimension; i++) {
            tmp = in.read();
            message[i] = (byte) tmp;
        }
        // si definisce la lunghezza dei byte del vettore firma in base ai byte che
        // ancora devono essere letti
        int signatureLength = in.available();
        byte[] signature = new byte[signatureLength];
        // con il seguente for si apprendono tutti i byte della firma nell'apposito
        // array
        for (i = 0; i < signatureLength; i++) {
            tmp = in.read();
            signature[i] = (byte) tmp;
        }
        TimeUnit.MILLISECONDS.sleep(50);
        // viene controllata l'autentiucità del messaggio
        Boolean verify = Cryptare.verifySignature(VoterPK, signature, message);
        // in caso in cui la firma non è corretta verrà stampato un messaggio e tornato
        // null
        if (verify != true) {
            System.out.println("\nMessaggio non firmato correttamente: ");
            return null;
        }

        System.out.println("Session closed.");
        return Utils.concatBytes(message, signature);
    }

    /**
     * la seguente funzione definisce il protocollo con cui la società invia la
     * propria secret key al validatore utililizzata poi
     * per invocare la funzione dello smart contract adibita a calcolare l'esito
     * finale del referndum
     * 
     * @param sSock            socket per la comunicazione
     * @param SocietyPK        publicKey della società
     * @param messageDimension dimensione del messaggio che il validatore riceve
     * @return
     * @throws Exception
     */
    static PrivateKey SocietyFinalComunication(Socket sSock, PublicKey SocietyPK, int messageDimension)
            throws Exception {

        byte[] message = ClientComunication(sSock, SocietyPK, messageDimension);
        // Alla classica client comunication si aggiunge la rigenerazione della chiave
        // della società partendo dai bytes ricevuti da quest'ultima
        KeyFactory kf = KeyFactory.getInstance("EC", "BC"); // or "EC" or whatever
        PrivateKey privateKey = kf
                .generatePrivate(new PKCS8EncodedKeySpec(Arrays.copyOfRange(message, 0, messageDimension)));

        return privateKey;
    }

    /**
     * la seguente funzione permette di ottenere la public key dell'i-esimo client
     * partire dal certificato dello stesso contenuto nel trustore del
     * Validatore (è solo operativa senza grossi significati)
     * 
     * @param truststore
     * @param IDClient
     * @return
     * @throws Exception
     */
    static PublicKey obtainVoterPK(KeyStore truststore, int IDClient) throws Exception {
        String alias = "sslClient" + String.valueOf(IDClient);
        // Get certificate of public key
        X509Certificate cert = (X509Certificate) truststore.getCertificate(alias);
        return cert.getPublicKey();
    }

    /**
     * la seguente funzione permette di ottenere la public key della società partire
     * dal certificato della stessa contenuto nel trustore del
     * Validatore (è solo operativa senza grossi significati)
     * 
     * @param truststore
     * @return
     * @throws Exception
     */
    static PublicKey obtainSocPK(KeyStore truststore) throws Exception {
        String alias = "sslSociety";
        X509Certificate cert = (X509Certificate) truststore.getCertificate(alias);
        return cert.getPublicKey();
    }

    /**
     * la seguente funzione permette di inviare la conferma del completamento delle
     * operazioni sulla transazione
     * 
     * @param cSock Socket su cui inviare il byte di ack
     * @param esito esito dell'invio (positivo o negativo)
     * @throws Exception
     */
    static void ConfirmTransaction(Socket cSock, String esito) throws Exception {
        // imposta la socket in output
        OutputStream out = cSock.getOutputStream();
        // invia un byte di conferma del completamento della transazione
        out.write(esito.getBytes());
        out.write(Utils.toByteArray("\n"));
    }

    /**
     * la seguente funzione definisce le operazioni svolte nella finestra temporale
     * T1-T2 per le transazioni,
     * in particolare prelevano il messagio inviatogli dal client, ne controllano la
     * firma (ClientComunication)
     * contrallano che questo è in possesso dell'NFT (checkNFT) e in caso
     * affermativo procede con la scrittura del
     * messaggio firmato sulla chain (ProtocolWriteOnChain) infine ritorna "true" se
     * tutte le operazioni sono andate a buon fine
     * inviando al client la conferma dell'avvenuta transazione.
     * 
     * @param sSock
     * @param cSock      socket su cui comunicare
     * @param truststore truststore Validatore
     * @param clientPK   public Key del client
     * @param IDclient   ID del client
     * @return
     * @throws Exception
     */
    static boolean ClientVoteT1T2(SSLServerSocket sSock, Socket cSock, KeyStore truststore, PublicKey clientPK,
            int IDclient) throws Exception {
        Boolean correctnessFirst = false;
        if (clientPK == null) {
            System.out.println("il client non è nel truststore del server non avverranno comunicazioni");
            return false;
        }
        byte[] firstTransaction = ClientComunication(cSock, clientPK, 234);
        if (SmartContract.checkNFT(truststore, IDclient + 1, clientPK))
            correctnessFirst = ProtocolWriteOnChain(firstTransaction, clientPK);
        return correctnessFirst;
    }

    /**
     * la seguente funzione definisce le operazioni svolte nella finestra temporale
     * T2-T3 per le transazioni,
     * in particolare prelevano la randomness inviatagli dal client, ne controllano
     * la firma (ClientComunication)
     * contrallano che questo è in possesso dell'NFT (checkNFT) e in caso
     * affermativo procede con la scrittura della
     * randomness firmata sulla chain (ProtocolWriteOnChain) infine ritorna "true"
     * se tutte le operazioni sono andate a buon fine
     * inviando al client la conferma dell'avvenuta transazione.
     * 
     * @param sSock
     * @param cSock      socket su cui comunicare
     * @param truststore truststore Validatore
     * @param clientPK   public Key del client
     * @param IDclient   ID del client
     * @return
     * @throws Exception
     */
    static boolean ClientConfirmT2T3(SSLServerSocket sSock, Socket cSock, KeyStore truststore, PublicKey clientPK,
            int IDclient) throws Exception {
        byte[] secondTransaction = ClientComunication(cSock, clientPK, 32);
        Boolean correctnessSecond = false;
        if (SmartContract.checkNFT(truststore, IDclient + 1, clientPK))
            correctnessSecond = ProtocolWriteOnChain(secondTransaction, clientPK);
        return correctnessSecond;
    }

    /**
     * la seguente funzione definisce le operazioni svolte nella finestra temporale
     * T3-T4 per le transazioni,
     * in particolare prelevano la private key inviatagli dalla società, ne
     * controllano la firma (SocietyFinalComunication)
     * procede a eseguire la funzione dell smart conrtact adibita al calcolo
     * dell'esito del referendum.
     * Infine ritorna "true" se tutte le operazioni sono andate a buon fine inviando
     * alla Società la conferma dell'avvenuta transazione.
     * 
     * @param sSock
     * @param cSock      socket su cui comunicare
     * @param truststore truststore Validatore
     * @param SocietyPK  public Key del client
     * @return
     * @throws Exception
     */
    static boolean SocietaFinalCommunication(SSLServerSocket sSock, Socket cSock, KeyStore truststore,
            PublicKey SocietyPK) throws Exception {
        PrivateKey SocPrivateKey = SocietyFinalComunication(cSock, SocietyPK, 67);
        ConfirmTransaction(cSock, "0");
        SmartContract.computeFinalResult(SocPrivateKey, 4);
        return true;
    }

    /**
     * questa funzione è operativa per separare le varie fasi della votazione
     * 
     * @param split split da scrivere nel file
     * @throws Exception
     */
    static void WriteSplitPhase(String split) throws Exception {
        try {
            AppendingObjectOutputStream outputStreamExist = new AppendingObjectOutputStream(
                    new FileOutputStream("ItalyChain.txt", true));
            if (split != null)
                outputStreamExist.writeObject(split.getBytes());
            else
                outputStreamExist.writeObject(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        KeyStore truststore = null;
        try {
            /* CLIENT PART PK */
            File file = new File("truststoreServer.jks");
            FileInputStream is = new FileInputStream(file);
            truststore = KeyStore.getInstance(KeyStore.getDefaultType());
            String password = "c7bc135a855d40b7c799b5e7e217c8730054a32d46bb43ba6e905f435f1a2867";
            // getting the key
            truststore.load(is, password.toCharArray());

        } catch (Exception e) {
            System.out.println(e);
        }
        SSLServerSocketFactory sockfact = Utils.obtainServerSocketFactory("Serverkeystore.jks", "sslVal"); //

        // create a factory object to handle server connections initialized with the
        // keystore passed as argument in the commandline (see Slides)
        SSLSocket[] sslSock = new SSLSocket[5];
        PublicKey[] clientPK = new PublicKey[4];

        SSLServerSocket sSock = (SSLServerSocket) sockfact.createServerSocket(4000); // bind to port 4000

        int i = 0;
        // il seguente ciclo permtte di ottenere le PK dei votanti di esempio dal trust
        // store del Validatore
        for (i = 0; i < 4; i++) {
            if (truststore != null)
                clientPK[i] = obtainVoterPK(truststore, i + 1);
        }
        System.out.println("\n\nCOMINCIA T1-T2 ");
        // questa porzione di codice rappresenta la seconda fase T1-T2 in cui 4 votanti
        // di esempio inviano il proprio voto
        for (i = 0; i < 4; i++) {
            System.out.println("Attendo le connessioni della fase T1-T2 ");
            sslSock[i] = (SSLSocket) sSock.accept(); // accept connections
            System.out.println("\nConnessione con Client numero " + i);
            // simula la votazione nella fase T1-T2 con annessa scrittura on chain
            Boolean correctnessFirst = ClientVoteT1T2(sSock, sslSock[i], truststore, clientPK[i], i);
            System.out.println("Invio la conferma dell'avvenuta prima transazione");
            // codice relativo alla conferma della transazione
            if (correctnessFirst) {
                ConfirmTransaction(sslSock[i], "0");
            } else {
                ConfirmTransaction(sslSock[i], "1");
            }
        }
        // simula la fine di T1-T2
        TimeUnit.MILLISECONDS.sleep(5000);
        for (i = 0; i < 4; i++) {
            sslSock[i].close();
        }
        // simula l'inizio della fase T2-T3 in particolare per separare i blocchi
        // relativi alla prima fase e alla seconda
        // nel condice seguente abbiamo aggiunto un separatore "split"
        String split = "T2-T3";
        WriteSplitPhase(split);

        System.out.println("\n\nCOMINCIA T2-T3 ");
        // questa porzione di codice rappresenta la seconda fase T2-T3 in cui 4 votanti
        // di esempio confermano il proprio voto inviando la propria randomness
        for (i = 0; i < 4; i++) {
            System.out.println("Attendo le connessioni della fase T2-T3 ");
            sslSock[i] = (SSLSocket) sSock.accept(); // accept connections
            System.out.println("\nConnessione con Client numero " + i);
            // simula la votazione nella fase T2-T3 con annessa scrittura on chain
            Boolean correctnessSecond = ClientConfirmT2T3(sSock, sslSock[i], truststore, clientPK[i], i);
            // codice relativo alla conferma della transazione
            System.out.println("Invio la conferma dell'avvenuta seconda transazione");
            if (correctnessSecond) {
                ConfirmTransaction(sslSock[i], "0");
            } else
                ConfirmTransaction(sslSock[i], "1");
        }

        // simula la fine di T2-T3
        TimeUnit.MILLISECONDS.sleep(5000);
        for (i = 0; i < 4; i++) {
            sslSock[i].close();
        }
        // simula l'inizio della fase T3-T4 in particolare per separare i blocchi
        // relativi alla prima fase e alla seconda
        // nel condice seguente abbiamo aggiunto un separatore "split"
        WriteSplitPhase(null);

        // si ottiene la chiave pubblica della società
        System.out.println("\n\nCOMINCIA T3-T4");
        PublicKey SocietyPK = obtainSocPK(truststore);
        System.out.println("In attesa della connessione con la Societa \n");

        sslSock[4] = (SSLSocket) sSock.accept();
        System.out.println("\nConnessione con Societa avvenuta con successo");
        // la seguente funzione completerà una comunicazione con la società per ottenere
        // la privatekey e poi invocherà la
        // funzione dello smart contract adibita al calcolo dell'esito del referendum
        SocietaFinalCommunication(sSock, sslSock[4], truststore, SocietyPK);
        System.out.println("FINE T3-T4");
    }
}
