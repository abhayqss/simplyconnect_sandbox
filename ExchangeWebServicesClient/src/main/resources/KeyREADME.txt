# The below scripts show the commands used to generate the self-signed key for Exchange service and this cliet..
# For production recommended to use keys signed by a third-party certificate authority (CA)

# Create the combination keystore/truststore for the client and service.
keytool -genkey -keyalg RSA -sigalg SHA1withRSA -validity 730 -alias myservicekey -keypass skpass -storepass sspass -keystore serviceKeystore.jks -dname "cn=localhost"
keytool -genkey -keyalg RSA -sigalg SHA1withRSA -validity 730 -alias myclientkey  -keypass ckpass -storepass cspass -keystore clientKeystore.jks -dname

# Place server public cert in client key/truststore
keytool -export -rfc -keystore clientKeystore.jks -storepass cspass -alias myclientkey -file MyClient.cer
keytool -import -trustcacerts -keystore serviceKeystore.jks -storepass sspass -alias myclientkey -file MyClient.cer -noprompt

# Place client public cert in service key/truststore
keytool -export -rfc -keystore serviceKeystore.jks -storepass sspass -alias myservicekey -file MyService.cer
keytool -import -trustcacerts -keystore clientKeystore.jks -storepass cspass -alias myservicekey -file MyService.cer -noprompt





