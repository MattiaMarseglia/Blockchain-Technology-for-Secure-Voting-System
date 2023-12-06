
cd /Users/nando/Library/Mobile\ Documents/com\~apple\~CloudDocs/Unisa/Quarto\ anno/Secondo\ Semstre/Sicurezza/ProjectWork/Def/SicurezzaDef/SicurezzaDef/SicurezzaDef

##Client


javac -cp .:bcprov-jdk15on-159.jar Votante.java;java -Djavax.net.ssl.keyStore=Client3keystore.jks -Djavax.net.ssl.keyStorePassword=c7bc135a855d40b7c799b5e7e217c8730054a32d46bb43ba6e905f435f1a2867 -Djavax.net.ssl.trustStore=truststoreClient3.jks -Djavax.net.ssl.trustStorePassword=c7bc135a855d40b7c799b5e7e217c8730054a32d46bb43ba6e905f435f1a2867 -cp .:bcprov-jdk15on-159.jar Votante 3 00