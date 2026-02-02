package org.openhealthtools.custom.net;

import com.misyshealthcare.connect.net.IConnectionDescription;
import com.misyshealthcare.connect.net.IServerConnection;
import com.misyshealthcare.connect.net.StandardServerConnection;

/**
 *  [copypaste]
 *
 *  This class is intended to customize behavior of com.misyshealthcare.connect.net.ConnectionFactory
 */
public class CustomConnectionFactory {

    public static IServerConnection getServerConnection(IConnectionDescription connectionDescription) {
        IServerConnection serverConnection;
        if (connectionDescription.isSecure()) {
            //difference with original method - CustomSecureServerConnection is used
            serverConnection = new CustomSecureServerConnection(connectionDescription);
        } else {
            serverConnection = new StandardServerConnection(connectionDescription);
        }

        serverConnection.connect();
        return serverConnection;
    }

}
