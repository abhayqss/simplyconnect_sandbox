package com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.template;

import com.scnsoft.eldermark.framework.Pair;
import com.scnsoft.eldermark.framework.connector4d.ResultListParameters;
import com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.schema.CommunicationRequest;
import com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.schema.CommunicationResponse;
import com.scnsoft.eldermark.framework.exceptions.DataAccessException;
import org.springframework.util.StringUtils;

import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by averazub on 5/24/2016.
 */
public class Xml4DConnection {

    private static final String RESPONSE_START_STRING = "<SMXmlEnvelope>";
    private static final String RESPONSE_ENDING_STRING = "</SMXmlEnvelope>";
    private static final int RESPONSE_ENDING_STRING_LENGTH = RESPONSE_ENDING_STRING.length();
    private final Integer RETRY_COUNT = 3;


    private static Long sqlRequestId = 0l;

    String host;
    Integer port;
    String userName;
    String password;
    SocketFactory socketFactory;

    public Xml4DConnection(SocketFactory socketFactory, String host, Integer port, String userName, String password) throws Exception {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.socketFactory = socketFactory;
    }


    private Socket connect() throws Exception {
        return socketFactory.createSocket(host, port);
    }

    private void disconnect(Socket socket) throws Exception {
        if (!socket.isClosed()) {
            socket.close();
        }
    }



    public Xml4DResultSet query(String sql, ResultListParameters resultListParameters, boolean isSingleResult) {

        CommunicationRequest.Authentication authentication = new CommunicationRequest.Authentication();
        authentication.setUserloginName(userName);
        authentication.setUserloginPassword(password);


        Pair<Long, String> request = Xml4DParser.serializeRequest(sql, resultListParameters, isSingleResult, authentication, sqlRequestId++);
        int reconnectCount = RETRY_COUNT;
        Xml4DResultSet resultSet = null;
        while (resultSet == null) {
            try {

                String resultXml = this.sendXmlRequest(request.getValue());
                CommunicationResponse response = Xml4DParser.parseResponse(resultXml);
                resultSet = Xml4DResultSet.readFromResponse(response);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                reconnectCount--;
                if (reconnectCount==0) throw new RuntimeException(e.getMessage(), e);
            }
        }
        return resultSet;
    }



    public String sendXmlRequest(String xmlRequest) throws DataAccessException {
        String wrapped = wrapXml(xmlRequest);
        StringBuilder result = new StringBuilder();
        try {
            Socket socket = connect();

            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.write(wrapped);
            writer.flush();

            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            int responseEndingCounter=0;
            int readed = 0;
            while ((readed!=-1) && (responseEndingCounter<RESPONSE_ENDING_STRING_LENGTH)) {
                readed = inFromServer.read();
                if (readed!=-1) {
                    result.append((char)readed);
                    if ((char)readed==RESPONSE_ENDING_STRING.charAt(responseEndingCounter)) {
                        responseEndingCounter++;
                    } else {
                        responseEndingCounter=0;
                    }
                }
            }
            disconnect(socket);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage(), e);
        }
        String resultStr = result.toString();
        if (StringUtils.isEmpty(resultStr)) throw new DataAccessException("Response from Server is null");
        if (!resultStr.startsWith(RESPONSE_START_STRING)) throw new DataAccessException("Got Error from Data Source: "+resultStr);
        return unwrapXml(result.toString());
    }

    private static String wrapXml(String xml) {
        return RESPONSE_START_STRING + xml + RESPONSE_ENDING_STRING;
    }

    private static String unwrapXml(String xml) {
        return xml.substring(RESPONSE_START_STRING.length(), xml.lastIndexOf(RESPONSE_ENDING_STRING));
    }



}