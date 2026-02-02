package org.openhealthtools.common.ws.server.ssl;

import org.apache.axis2.transport.http.server.AxisHttpConnectionImpl;
import org.apache.http.params.HttpParams;

import javax.net.ssl.SSLSocket;
import java.io.IOException;

public class AxisHttpsConnectionImpl extends AxisHttpConnectionImpl {
    private SSLSocket sslSocket;

    public AxisHttpsConnectionImpl(SSLSocket socket, HttpParams params) throws IOException {
        super(socket, params);
        this.sslSocket = socket;
    }

    public void close() throws IOException {
        this.flush();
        this.sslSocket.close();
    }

}
