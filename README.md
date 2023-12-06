# Blockchain for Correct Voting Project

This project aims to simulate the development of an e-voting system based on Blockchain.

The files in the "SicurezzaDef" folder comprise the implementation of WP4. Specifically, the anticipated actors include a Validator, four Voters, and the Company, each represented by a specific program. These different programs communicate with each other using Sockets, thus simulating the transmission of correctly encrypted and signed votes along with the associated randomness from the voters to the validator and the transmission of the Company's private key to the validator.
The entire project is set within a TLS context.
As a simulation of the Blockchain, the validator will refer to methods contained within the SmartContract class, simulating the addition of blocks by writing to a text file, "ItalyChain.txt" (to be deleted before each execution).
The other classes, namely Utils.java, Cryptare.java, SmartContract.java, MyKeyManager.java, AppendingObjectOutputStream.java, and toVote.java, contain methods essential to the simulation of the voting process.
The authorization to vote for each voter, described in the document as the possession of an NFT, has been simulated through the Smart Contract's verification of the possession of a pre-generated certificate by each voter, using KeyStore and TrustStore.
The two tools, Tor and SPID, utilized in the design in WP2 and the analysis in WP3, have not been simulated within this WP.

### More Information
for more detailed information: [Project Report](APS_ProjectWork_ConsegnaMidterm_GruppoKryptos.pdf)

### How To Execute

The BouncyCastle jar, in order to be sent via Gmail, has been converted into a .txt file. For proper command-line execution, its extension (.jar) needs to be restored.

In the "execute" folder, you will find:

- exec.sh, which needs to be appropriately modified to enable the execution of terminals according to your operating system. It is also necessary to modify the path in the first line to automatically delete the file ItalyChain.txt.
- execClient#.sh, which should be modified accordingly to access the correct directory (where the Votante.java file is located).
- execServer.sh, which should be modified accordingly to access the correct directory (where the Validatore.java file is located).

- execSocieta.sh, which should be appropriately modified to access the correct directory (where the Societa.java file is located).

After making the necessary modifications, the project can be launched by executing only the "./exec.sh" file.

- "Generazione Certificati.txt," which contains the instructions executed to generate the certificates used in the project, including keyStores and trustStores.
- "Run Manuale.txt," which contains the instructions necessary for manual compilation and execution of the entire WP4. Each instruction requires its own terminal.
