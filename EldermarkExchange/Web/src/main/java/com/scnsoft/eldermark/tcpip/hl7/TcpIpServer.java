package com.scnsoft.eldermark.tcpip.hl7;

import com.scnsoft.eldermark.services.hl7.MessageService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpIpServer {

   public TcpIpServer(int serverPort, MessageService processingService) throws IOException {
        try {
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while (true) {
                Socket clientSocket = listenSocket.accept();
                TcpIpConnection connection = new TcpIpConnection(clientSocket, processingService);
                connection.start();
            }
        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }
    }
}