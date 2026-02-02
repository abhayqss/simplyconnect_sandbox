package com.scnsoft.eldermark.tcpip.hl7;

import java.io.BufferedReader;
import java.io.IOException;

public interface TcpIpReader {
    String fetchDataFromSocket(BufferedReader socket) throws IOException;
}
