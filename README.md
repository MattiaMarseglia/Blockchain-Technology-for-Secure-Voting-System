# Blockchain for Correct Voting Project

Tale progetto ha lo scopo di simulare la realizzazione di un sistema di e-vote basato su BlockChain.

I file riportati nella cartella "SicurezzaDef" comprono lo svolgimento del WP4. Nello specifico gli attori previsti sono: un Validatore, quattro Votanti e la Societa, ciascuno rappresentato da uno specifico programma. Questi diversi programmi comunicano tra di loro mediante l'impiego di Socket, simulando in questo modo l'invio del voto correttamente cifrato e firmato e della annessa randomness, da parte dei votanti al validatore e l'invio della chiave privata della Societa al validatore.
L’intero progetto si inserisce in un contesto TLS.
Come simulazione della BlockChain il validatore farà riferimento a metodi contenuti all'interno della classe SmartContract, simulando l'aggiunta di blocchi attraverso la scrittura su un file di testo "ItalyChain.txt" (da eliminare prima di ogni esecuzione).
Le altre classi ovvero, Utils.java, Cryptare.java, SmartContract.java, MyKeyManager.java, AppendingObjectOutputStream.java e toVote.java contengono metodi funzionali allo svolgimento della simulazione di votazione.
L’autorizzazione a votare per ciascun votante, descritta nel documento come possesso di un NFT, è stato simulato attraverso la verifica dello Smart Contract del possesso di un certificato pre-generato da parte di ciascun votante, utilizzando KeyStore e TrustStore.
I due tool, Tor e SPID, di cui si è fatto utilizzo nella progettazione in WP2 e nell’analisi in WP3, non sono stati simulati all’interno di questo WP.

### More Information
for more detailed information: [Project Report](APS_ProjectWork_ConsegnaMidterm_GruppoKryptos.pdf)

### How To Execute

Il jar di bouncyCastle per poter essere inviato tramite Gmail è stato convertito in un file .txt, per una corretta esecuzione da riga di comando va ripristinata la sua estensione (.jar).

Nella cartella execute si trovano:

- exec.sh, da modificare opportunamente per permettere l'esecuzione di terminali relativamente al proprio sistema operativo, è necessario anche modificare il path presente nella prima riga in modo da poter cancellare automaticamente il file ItalyChain.txt
- execClient#.sh, da modificare opportunamente per accedere alla directory corretta (dove è contenuto il file Votante.java)
- execServer.sh, da modificare opportunamente per accedere alla directory corretta (dove è contenuto il file Validatore.java)

- execSocieta.sh, da modificare opportunamente per accedere alla directory corretta (dove è contenuto il file Societa.java)

Dopo aver effettuato le dovute modifiche il progetto può essere lanciato eseguendo solo il file "./exec.sh"

- "Generazione Certificati.txt", che contiene le istruzioni eseguite per generare i certificati utilizzati nel progetto, keyStores e trustStores
- "Run Manuale.txt", che contiene le istruzioni necessarie per la compilazione ed esecuzione manuale dell'intero WP4. Ogni istruzione necessita di un proprio terminale.
