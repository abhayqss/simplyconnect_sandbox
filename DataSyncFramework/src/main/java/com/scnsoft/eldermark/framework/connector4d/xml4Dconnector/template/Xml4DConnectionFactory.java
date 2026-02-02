package com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.template;

import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;

/**
 * Created by averazub on 5/24/2016.
 */
public class Xml4DConnectionFactory {

    String host;
    Integer port;
    SocketFactory socketFactory;
    String userName;
    String password;

    public Xml4DConnectionFactory(String host, Integer port, boolean isSsl, String userName, String password) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
        try {
            if (isSsl) {
                this.socketFactory = initSSLSocketFactory();
            } else {
                this.socketFactory = SocketFactory.getDefault();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Xml4DConnection createConnection() {
        try {
            Xml4DConnection connection = new Xml4DConnection(socketFactory, host, port, userName, password);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    private SSLSocketFactory initSSLSocketFactory() throws Exception {

        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(
                            X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            X509Certificate[] certs, String authType) {
                    }
                }
        };

/*
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream("KeyStore"), "password".toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(keystore);


        TrustManager[] trustManagers = tmf.getTrustManagers();*/
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, trustAllCerts, null);

        SSLSocketFactory sf = context.getSocketFactory();

        return sf;
    }


}
