package org.openhealthtools.custom.net;

import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.llp.LowerLayerProtocol;
import ca.uhn.hl7v2.parser.Parser;
import com.misyshealthcare.connect.net.IConnectionDescription;
import com.misyshealthcare.connect.net.IServerConnection;
import org.apache.log4j.Logger;
import org.openhealthexchange.openpixpdq.ihe.impl_v2.hl7.HL7Server;

import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *  [copypaste]
 *
 *  This class is intended to customize behavior of
 *  org.openhealthexchange.openpixpdq.ihe.impl_v2.hl7.HL7Server
 */
public class CustomHL7Server extends HL7Server {
    private static Logger log = Logger.getLogger(CustomHL7Server.class);
    private IConnectionDescription connection;

    public CustomHL7Server(IConnectionDescription conn, LowerLayerProtocol llp, Parser parser) {
        super(conn, llp, parser);
        this.connection = conn;
    }

    public void run() {
        try {
            //difference with original method - CustomConnectionFactory is used
            IServerConnection serverConn = CustomConnectionFactory.getServerConnection(this.connection);
            ServerSocket ss = serverConn.getServerSocket();
            ss.setSoTimeout(10000);
            log.info(this.connection.getDescription() + " is running on port " + ss.getLocalPort());

            while(this.keepRunning()) {
                try {
                    Socket newSocket = ss.accept();
                    log.info("Accepted connection from " + newSocket.getInetAddress().getHostAddress());
                    Connection conn = new Connection(this.parser, this.llp, newSocket);
                    this.newConnection(conn);
                } catch (InterruptedIOException var10) {
                    ;
                } catch (Exception var11) {
                    log.error("Error accepting HL7 connections: ", var11);
                }
            }

            ss.close();
        } catch (Exception var12) {
            log.error(var12);
        } finally {
            this.stop();
        }

    }
}
