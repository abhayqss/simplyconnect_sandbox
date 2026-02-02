package com.scnsoft.eldermark.tcpip.hl7;

import java.io.BufferedReader;
import java.io.IOException;

public class TcpIpReaderImpl implements TcpIpReader {

    public String fetchDataFromSocket(BufferedReader reader) throws IOException {
        String line;
        StringBuilder sb = new StringBuilder();
        while(true){
            line = reader.readLine ();
            if(line == null || "".equals(line.trim())) {
                break;
            }
            sb.append(line).append("\r\n");
        }
        return sb.toString();
    }

}
