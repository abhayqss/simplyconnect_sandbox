package com.scnsoft.eldermark.tcpip.hl7;

import com.scnsoft.eldermark.services.hl7.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;

//@Configuration
public class TcpIpConfiguration {

    @Value("${tcp.ip.hl7.this.port}")
    private Integer tcpIpHl7Port;

    @Autowired
    MessageService messageService;

//    @PostConstruct
    public void postConstruct() {
        (new Thread() {
            public void run() {
                try {
                    new TcpIpServer(tcpIpHl7Port, messageService);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
