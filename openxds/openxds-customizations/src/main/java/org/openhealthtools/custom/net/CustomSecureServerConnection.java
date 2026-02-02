package org.openhealthtools.custom.net;

import com.misyshealthcare.connect.net.IConnectionDescription;
import com.misyshealthcare.connect.net.SecureConnectionDescription;
import com.misyshealthcare.connect.net.SecureServerConnection;

import javax.net.ssl.SSLServerSocket;

/**
 *  [copypaste]
 *
 *  This class is intended to customize behavior of
 *  com.misyshealthcare.connect.net.SecureServerConnection
 */
public class CustomSecureServerConnection extends SecureServerConnection {

    public CustomSecureServerConnection(IConnectionDescription connectionDescription) {
        super(connectionDescription);
    }

    @Override
    public void connect() {
        SSLServerSocket secureServerSocket = null;
        if (this.description != null && this.description instanceof SecureConnectionDescription) {
            SecureConnectionDescription scd = (SecureConnectionDescription)this.description;
            //difference with original method - CustomSecureSocketFactory is used
            CustomSecureSocketFactory sslFactory = new CustomSecureSocketFactory(scd);
            secureServerSocket = (SSLServerSocket)sslFactory.createServerSocket(this.description.getPort());
        }

        this.ssocket = secureServerSocket;
    }
}
