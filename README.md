# Blockchain Technology for Secure Voting System

<div align="center">
  <img src="voting.png" alt="voting">
</div>

## Overview

This project aims to simulate the development of an e-voting system based on Blockchain. The system is designed to ensure the integrity, privacy, availability, authenticity, and transparency of the voting process.

## Project Structure

### Work Package 1 (WP1): Threat Model

#### Completeness
- Detailed analysis of the system's completeness.

#### Threat Models
- **Il Venditore di favori**: An adversary who tries to buy votes.
- **Il Ficcanaso**: An eavesdropper who tries to intercept communications.
- **La viscida governance della società**: Malicious governance within the society.
- **Gli Sviluppatori procaccianti**: Developers who may insert malicious code.
- **Lo Schieramento polarizzato**: Polarized political groups trying to influence the vote.
- **L’ISP cospiratore**: A conspiring ISP attempting to interfere with the voting process.
- **Il miserabile Ministero del voto**: A corrupt ministry handling the votes.
- **Il Ladro d’identità**: An identity thief.
- **Il DDoSSer**: An attacker aiming to disrupt the system via DDoS attacks.

#### Properties
- **Integrity**
- **Privacy**
- **Availability**
- **Authenticity**
- **Credibility**
- **Transparency**

### Work Package 2 (WP2): Solution

#### Chain Characteristics
- Detailed description of the blockchain characteristics.

#### Smart Contract Characteristics
- Explanation of the smart contract features.

#### Time Window Analysis
- Analysis of the voting process's time windows from T0 to T4.

#### Functionality
- Detailed description of the system's functionality.

#### Authentication (SPID)
- Characteristics of the SPID authentication method.

#### Communication with Validator
- Details on the communication process with the validator.

### Work Package 3 (WP3): Analysis

#### Completeness
- Analysis of the system's completeness against the threat model.

#### Integrity
- Measures to ensure the system's integrity.

#### Confidentiality
- Methods to ensure data confidentiality.

#### Efficiency
- Analysis of the system's efficiency against potential DDoS attacks.

#### Transparency
- Discussion on the transparency benefits of using SPID.

### Work Package 4 (WP4): Implementation

The files in the "SicurezzaDef" folder comprise the implementation of WP4. Specifically, the anticipated actors include a Validator, four Voters, and the Company, each represented by a specific program. These different programs communicate with each other using Sockets, thus simulating the transmission of correctly encrypted and signed votes along with the associated randomness from the voters to the validator and the transmission of the Company's private key to the validator.

The entire project is set within a TLS context. As a simulation of the Blockchain, the validator will refer to methods contained within the SmartContract class, simulating the addition of blocks by writing to a text file, "ItalyChain.txt" (to be deleted before each execution).

The other classes, namely Utils.java, Cryptare.java, SmartContract.java, MyKeyManager.java, AppendingObjectOutputStream.java, and toVote.java, contain methods essential to the simulation of the voting process. The authorization to vote for each voter, described in the document as the possession of an NFT, has been simulated through the Smart Contract's verification of the possession of a pre-generated certificate by each voter, using KeyStore and TrustStore.

The two tools, Tor and SPID, utilized in the design in WP2 and the analysis in WP3, have not been simulated within this WP.

## How To Execute

The BouncyCastle jar, in order to be sent via Gmail, has been converted into a .txt file. For proper command-line execution, its extension (.jar) needs to be restored.

In the "execute" folder, you will find:

- **exec.sh**: Modify this script to enable the execution of terminals according to your operating system. Also, modify the path in the first line to automatically delete the file ItalyChain.txt.
- **execClient#.sh**: Modify these scripts to access the correct directory (where the Votante.java file is located).
- **execServer.sh**: Modify this script to access the correct directory (where the Validatore.java file is located).
- **execSocieta.sh**: Modify this script to access the correct directory (where the Societa.java file is located).

After making the necessary modifications, the project can be launched by executing only the `./exec.sh` file.

- **Generazione Certificati.txt**: Contains the instructions executed to generate the certificates used in the project, including keyStores and trustStores.
- **Run Manuale.txt**: Contains the instructions necessary for manual compilation and execution of the entire WP4. Each instruction requires its own terminal.

## More Information

For more detailed information, please refer to the [Project Report](APS_ProjectWork_ConsegnaMidterm_GruppoKryptos.pdf).

Feel free to reach out for any clarifications or suggestions. Thank you for your contribution to the "Blockchain for Correct Voting" Project!
