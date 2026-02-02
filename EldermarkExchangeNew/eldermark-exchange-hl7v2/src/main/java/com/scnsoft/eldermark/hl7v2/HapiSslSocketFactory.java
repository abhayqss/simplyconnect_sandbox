package com.scnsoft.eldermark.hl7v2;

import ca.uhn.hl7v2.util.SocketFactory;
import ca.uhn.hl7v2.util.StandardSocketFactory;

import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

public class HapiSslSocketFactory implements SocketFactory {

    public static final String DEFAULT_TLS_VERSION = "TLSv1.2";
    private final String tlsVersion;

    private final SSLSocketFactory clientSocketFactory;
    private final SSLServerSocketFactory serverSocketFactory;


    public HapiSslSocketFactory(InputStream keyStore, String keyStorePass, String keyStoreType,
                                InputStream trustStore, String trustStorePass, String trustStoreType) {
        this(DEFAULT_TLS_VERSION,
                keyStore, keyStorePass, keyStoreType,
                trustStore, trustStorePass, trustStoreType);
    }

    public HapiSslSocketFactory(String tlsVersion,
                                InputStream keyStore, String keyStorePass, String keyStoreType,
                                InputStream trustStore, String trustStorePass, String trustStoreType) {
        this.tlsVersion = tlsVersion;
        try {
            SSLContext sc = SSLContext.getInstance(this.tlsVersion);

            sc.init(this.getKeyManagers(keyStore, keyStorePass, keyStoreType),
                    this.getTrustManagers(trustStore, trustStorePass, trustStoreType), null);
            this.clientSocketFactory = sc.getSocketFactory();
            this.serverSocketFactory = sc.getServerSocketFactory();
        } catch (GeneralSecurityException var5) {
            throw new RuntimeException("SSL keystores initialization fails", var5);
        } catch (IOException var6) {
            throw new RuntimeException("Unable to load keystores", var6);
        }
    }

    private TrustManager[] getTrustManagers(InputStream trustStore, String trustStorePass, String trustStoreType) throws IOException, GeneralSecurityException {
        KeyStore ts = KeyStore.getInstance(trustStoreType);
        ts.load(trustStore, trustStorePass.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        tmf.init(ts);
        return tmf.getTrustManagers();
    }

    private KeyManager[] getKeyManagers(InputStream keyStore, String keyStorePass, String keyStoreType) throws IOException, GeneralSecurityException {
        KeyStore ts = KeyStore.getInstance(keyStoreType);
        ts.load(keyStore, keyStorePass.toCharArray());

        final KeyManagerFactory kmFact = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmFact.init(ts, keyStorePass.toCharArray());

        return kmFact.getKeyManagers();
    }

    public Socket createSocket() throws IOException {
        return javax.net.SocketFactory.getDefault().createSocket();
    }

    /**
     * {@inheritDoc}
     */
    public Socket createTlsSocket() throws IOException {
        return clientSocketFactory.createSocket();
    }

    /**
     * {@inheritDoc}
     */
    public ServerSocket createServerSocket() throws IOException {
        return ServerSocketFactory.getDefault().createServerSocket();
    }

    /**
     * {@inheritDoc}
     */
    public ServerSocket createTlsServerSocket() throws IOException {
        return serverSocketFactory.createServerSocket();
    }

    public void configureNewAcceptedSocket(Socket theSocket) throws SocketException {
        theSocket.setSoTimeout(StandardSocketFactory.DEFAULT_ACCEPTED_SOCKET_TIMEOUT);
    }
}
