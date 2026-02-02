package org.openhealthtools.custom.net;

import com.misyshealthcare.connect.net.ConnectionCertificateHandler;
import com.misyshealthcare.connect.net.SecureConnectionDescription;
import com.misyshealthcare.connect.net.SecureSocketFactory;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.log4j.Logger;

import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.io.IOException;
import java.net.*;
import java.security.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *  [copypaste]
 *
 *  This class is intended to customize behavior of com.misyshealthcare.connect.net.SecureSocketFactory
 */
public class CustomSecureSocketFactory implements SecureProtocolSocketFactory  {
    private static final Logger LOG = Logger.getLogger(SecureSocketFactory.class);
    private URL keystoreUrl = null;
    private String keystorePassword = null;
    private URL truststoreUrl = null;
    private String truststorePassword = null;
    private SSLContext sslcontext = null;
    private SecureConnectionDescription scd;

    public CustomSecureSocketFactory(SecureConnectionDescription scd) {
        this.keystoreUrl = scd.getKeyStore();
        this.keystorePassword = scd.getKeyStorePassword();
        this.truststoreUrl = scd.getTrustStore();
        this.truststorePassword = scd.getTrustStorePassword();
        this.scd = scd;
    }

    private SSLContext createSSLContext() throws IOException {
        try {
            LOG.info("Attempting to create ssl context.");
            KeyManager[] keymanagers = null;
            TrustManager[] trustmanagers = null;
            KeyStore keystore;
            if (this.keystoreUrl != null) {
                keystore = ConnectionCertificateHandler.createKeyStore(this.keystoreUrl, this.keystorePassword);
                if (LOG.isDebugEnabled()) {
                    ConnectionCertificateHandler.printKeyCertificates(keystore);
                }

                keymanagers = ConnectionCertificateHandler.createKeyManagers(keystore, this.keystorePassword);
            }

            if (this.truststoreUrl != null) {
                keystore = ConnectionCertificateHandler.createKeyStore(this.truststoreUrl, this.truststorePassword);
                if (LOG.isDebugEnabled()) {
                    ConnectionCertificateHandler.printTrustCerts(keystore);
                }

                trustmanagers = ConnectionCertificateHandler.createTrustManagers(keystore, this.scd);
            }
            //difference with original - SSLContext.getInstance("TLSv1.2") instead od "TLS"
            SSLContext sslcontext = SSLContext.getInstance("TLSv1.2");
            sslcontext.init(keymanagers, trustmanagers, (SecureRandom)null);
            return sslcontext;
        } catch (NoSuchAlgorithmException var4) {
            LOG.error("NSA: " + var4.getMessage(), var4);
            throw new IOException("Unsupported algorithm exception: " + var4.getMessage());
        } catch (KeyStoreException var5) {
            LOG.error("Key Store: " + var5.getMessage(), var5);
            throw new IOException("Keystore exception: " + var5.getMessage());
        } catch (GeneralSecurityException var6) {
            LOG.error("General: " + var6.getMessage(), var6);
            throw new IOException("Key management exception: " + var6.getMessage());
        } catch (IOException var7) {
            LOG.error("I/O exception: " + var7.getMessage(), var7);
            throw new IOException("I/O error reading keystore/truststore file: " + var7.getMessage());
        }
    }

    private SSLContext getSSLContext() throws IOException {
        if (this.sslcontext == null) {
            this.sslcontext = this.createSSLContext();
        }

        return this.sslcontext;
    }

    /**
     * Looks like this method is used for calls where OpenXds acts as client,
     * for example self registry ITI-42 call on document ITI-41 submit
     *
     * The same updates are applied to ciphers and TLS version as in createServerSocket
     * @param secureSocket
     */
    private void setAtnaProtocols(SSLSocket secureSocket) throws IOException {
        secureSocket.setEnabledProtocols(buildEnabledProtocols());
        secureSocket.setEnabledCipherSuites(buildEnabledCipherSuites());
    }

    public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) {
        Socket socket = null;

        try {
            socket = this.getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
            this.setAtnaProtocols((SSLSocket)socket);
        } catch (ConnectException var7) {
            LOG.error("Connection was refused when connecting to socket.", var7);
        } catch (IOException var8) {
            LOG.error("I/O problem creating socket.", var8);
        } catch (Exception var9) {
            LOG.error("Problem creating socket.", var9);
        }

        return socket;
    }

    public Socket createSocket(String host, int port) {
        Socket socket = null;

        try {
            socket = this.getSSLContext().getSocketFactory().createSocket(host, port);
            this.setAtnaProtocols((SSLSocket)socket);
        } catch (ConnectException var5) {
            LOG.error("Connection was refused when connecting to socket.", var5);
        } catch (IOException var6) {
            LOG.error("I/O problem creating socket.", var6);
        } catch (Exception var7) {
            LOG.error("Problem creating socket.", var7);
        }

        return socket;
    }

    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) {
        Socket lsocket = null;

        try {
            lsocket = this.getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
            this.setAtnaProtocols((SSLSocket)lsocket);
        } catch (ConnectException var7) {
            LOG.error("Connection was refused when connecting to socket.", var7);
        } catch (IOException var8) {
            LOG.error("I/O problem creating socket.", var8);
        } catch (Exception var9) {
            LOG.error("Problem creating socket.", var9);
        }

        return lsocket;
    }

    public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort, HttpConnectionParams params) {
        Socket lsocket = null;
        //this one isused
        try {
            lsocket = this.getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
            this.setAtnaProtocols((SSLSocket)lsocket);
        } catch (ConnectException var8) {
            LOG.error("Connection was refused when connecting to socket.", var8);
        } catch (IOException var9) {
            LOG.error("I/O problem creating socket.", var9);
        } catch (Exception var10) {
            LOG.error("Problem creating socket.", var10);
        }

        return lsocket;
    }

    /**
     * Difference with original method:
     * 1. Stopped support for previous weaker cipher suites
     *
     * 2. Added java7 available ciphers from OWASP Cipher String 'C" ciphers
     *  https://cheatsheetseries.owasp.org/cheatsheets/TLS_Cipher_String_Cheat_Sheet.html
     *
     *  If JCE Unlimited is applied to JRE, more stronger ciphers from that list will also be added.
     *  At the moment of comment, JCE Unlimited is applied to Test and Production envs.
     *
     * 3. TLSv1.2 was set as the only supported protocol (older versions were disabled).
     *
     */
    public ServerSocket createServerSocket(int port) {
        SSLServerSocket ss = null;

        try {
            ServerSocketFactory socketFactory = this.getSSLContext().getServerSocketFactory();
            ss = (SSLServerSocket)socketFactory.createServerSocket(port);
            ss.setNeedClientAuth(true);

            ss.setEnabledCipherSuites(buildEnabledCipherSuites());
            ss.setEnabledProtocols(buildEnabledProtocols());

        } catch (IOException var4) {
            LOG.error("I/O problem creating server socket.", var4);
        }

        return ss;
    }

    private String[] buildEnabledProtocols() {
        return new String[] {"TLSv1.2"};
    }

    private String[] buildEnabledCipherSuites() throws IOException {
        List<String> ciphers = new ArrayList<String>();
        addJava7SupportedCiphers(ciphers);
        addJava7JceUnlimitedCyphersIfSupported(ciphers,  this.getSSLContext().getSocketFactory().getSupportedCipherSuites());
        return ciphers.toArray(new String[0]);
    }


    private void addJava7SupportedCiphers(List<String> ciphers) {
        ciphers.add("TLS_DHE_RSA_WITH_AES_128_CBC_SHA");
        ciphers.add("TLS_DHE_RSA_WITH_AES_128_CBC_SHA256");
        ciphers.add("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA");
        ciphers.add("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256");
    }

    private void addJava7JceUnlimitedCyphersIfSupported(List<String> ciphers, String[] supportedCipherSuites) {
        HashSet<String> jceUnlimitedCiphers = new HashSet<String>();
        jceUnlimitedCiphers.add("TLS_DHE_RSA_WITH_AES_256_CBC_SHA");
        jceUnlimitedCiphers.add("TLS_DHE_RSA_WITH_AES_256_CBC_SHA256");
        jceUnlimitedCiphers.add("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA");
        jceUnlimitedCiphers.add("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384");

        for (int i = 0; i < supportedCipherSuites.length; i++) {
            if (jceUnlimitedCiphers.contains(supportedCipherSuites[i])) {
                ciphers.add(supportedCipherSuites[i]);
            }
        }
    }
}
