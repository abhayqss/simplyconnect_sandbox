package com.scnsoft.eldermark.tcpip.hl7;

import com.scnsoft.eldermark.services.hl7.MessageService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class TcpIpConnection extends Thread implements TcpIpReader {
    private DataOutputStream output;
    private Socket clientSocket;
    private MessageService executor;
    private TcpIpReader tcpIpReader;

    TcpIpConnection(Socket clientSocket, MessageService executor) {
        try {
            this.executor = executor;
            this.clientSocket = clientSocket;
            output = new DataOutputStream(clientSocket.getOutputStream());
            this.tcpIpReader = new TcpIpReaderImpl();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            /*String response = executor.processMessage(
                    fetchDataFromSocket(serverReader),
                    clientSocket.getInetAddress(),
                    clientSocket.getPort()
            );*/
            executor.processIncomingMessage(fetchDataFromSocket(serverReader));
            //output.writeBytes(response);
            //output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String fetchDataFromSocket(BufferedReader serverReader) throws IOException {
        String out = this.tcpIpReader.fetchDataFromSocket(serverReader);
        return out != null ? out.trim() : "";
    }
}