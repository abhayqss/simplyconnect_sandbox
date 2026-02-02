package com.scnsoft.eldermark.services.hl7;

import ca.uhn.hl7v2.hoh.sockets.CustomCertificateTlsSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;

public class HL7CertificateTlsSocketFactory extends CustomCertificateTlsSocketFactory {

    private static final Logger logger = LoggerFactory.getLogger(HL7CertificateTlsSocketFactory.class);

    private KeyStore myKeystore;
    private String myKeystoreFilename;
    private String myKeystorePassphrase;
    private String myKeystoreType = "JKS";
    private SSLServerSocketFactory myServerSocketFactory;
    private SSLSocketFactory mySocketFactory = null;

    public HL7CertificateTlsSocketFactory() {
    }

    public HL7CertificateTlsSocketFactory(KeyStore theKeystore, String theKeystorePass) {
        if (theKeystore == null) {
            throw new NullPointerException("KeyStore can not be null");
        } else {
            this.myKeystore = theKeystore;
            this.myKeystorePassphrase = theKeystorePass;
        }
    }

    public HL7CertificateTlsSocketFactory(String theKeystoreType, String theKeystoreFilename, String theKeystorePassphrase) {
        this.myKeystoreType = theKeystoreType;
        this.myKeystoreFilename = theKeystoreFilename;
        this.myKeystorePassphrase = theKeystorePassphrase;
    }

    public Socket createClientSocket() throws IOException {
        this.initialize();
        logger.debug("Creating client socket");
        return this.mySocketFactory.createSocket();
    }

    public ServerSocket createServerSocket() throws IOException {
        this.initialize();
        logger.debug("Creating server socket");
        return this.myServerSocketFactory.createServerSocket();
    }

    private void initialize() throws IOException {
        if (this.mySocketFactory == null) {
            try {
                char[] passphrase = this.myKeystorePassphrase != null ? this.myKeystorePassphrase.toCharArray() : null;
                if (this.myKeystore == null) {
                    this.myKeystore = KeyStore.getInstance(this.myKeystoreType);

                    try {
                        this.myKeystore.load(this.getClass().getClassLoader().getResource("security/KobleTrustStore.jks").openStream(),  passphrase);
                    } catch (IOException var7) {
                        throw new IOException("Failed to load keystore: " + this.myKeystoreFilename, var7);
                    }
                }

                SSLContext ctx = SSLContext.getInstance("TLS");
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                kmf.init(this.myKeystore, passphrase);
                tmf.init(this.myKeystore);
                TrustManager[] trustManagers = tmf.getTrustManagers();
                KeyManager[] keyManagers = kmf.getKeyManagers();
                ctx.init(keyManagers, trustManagers, (SecureRandom)null);
                this.mySocketFactory = ctx.getSocketFactory();
                this.myServerSocketFactory = ctx.getServerSocketFactory();
            } catch (NoSuchAlgorithmException var8) {
                throw new IOException("Failed to initialize socket factory: " + var8.getMessage(), var8);
            } catch (CertificateException var9) {
                throw new IOException("Failed to initialize socket factory: " + var9.getMessage(), var9);
            } catch (FileNotFoundException var10) {
                throw new IOException("Failed to initialize socket factory: " + var10.getMessage(), var10);
            } catch (UnrecoverableKeyException var11) {
                throw new IOException("Failed to initialize socket factory: " + var11.getMessage(), var11);
            } catch (KeyStoreException var12) {
                throw new IOException("Failed to initialize socket factory: " + var12.getMessage(), var12);
            } catch (KeyManagementException var13) {
                throw new IOException("Failed to initialize socket factory: " + var13.getMessage(), var13);
            }
        }
    }

    public void setKeystoreFilename(String theKeystoreFilename) {
        this.myKeystoreFilename = theKeystoreFilename;
    }

    public void setKeystorePassphrase(String theKeystorePassphrase) {
        this.myKeystorePassphrase = theKeystorePassphrase;
    }

    public void setKeystoreType(String theKeystoreType) {
        this.myKeystoreType = theKeystoreType;
    }

}
