package com.scnsoft.exchange.adt.controller;


import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.segment.MSA;
import ca.uhn.hl7v2.parser.ParserConfiguration;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.SocketFactory;
import com.scnsoft.exchange.adt.entity.Adt39RequestDto;
import com.scnsoft.exchange.adt.entity.AdtResponseDto;
import com.scnsoft.exchange.adt.entity.Template;
import com.scnsoft.exchange.adt.ssl.SslConnectionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.validation.Valid;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;

@Controller
@RequestMapping(value = "/iti8adt39")
@PreAuthorize("isAuthenticated()")
public class Iti8Adt39Controller extends Iti8Iti30BaseController {

    public static Resource TEMPLATE = new ClassPathResource("/xmltemplates/adt39.hl7");

    @RequestMapping(method = RequestMethod.GET)
    public String root(Model model) {
        Adt39RequestDto request = new Adt39RequestDto();

        request.setHost(defaultHost);
        request.setPort("3614");


        request.setMessageDate(df.format(new Date()));
        request.setEventDate(df.format(new Date()));
        request.setPid("S5631^^^IHENA&1.3.6.1.4.1.21367.2010.1.2.300&ISO");
        request.setLastName("DUCK");
        request.setFirstName("DONALD");
        request.setBirthDate("19781208");
        request.setSex("M");
        request.setMergePid("N2674^^^IHENA&1.3.6.1.4.1.21367.2010.1.2.300&ISO");


        Resource template = TEMPLATE;
        request.setTemplate(getContentFromFile(template));

        model.addAttribute("requestDto", request);

        return "send-adt-39";
    }

    @RequestMapping(value = "/applyTemplate", method = RequestMethod.POST)
    public @ResponseBody String applyTemplate(Model model,
                                              @ModelAttribute("requestDto") Adt39RequestDto requestDto) {
        String templateSrc = getContentFromFile(TEMPLATE);
        Template template = new Template(templateSrc)
                .addReplacements(requestDto);
        return template.build();
    }


    @RequestMapping(method = RequestMethod.POST)
    public String postRequest(@Valid @ModelAttribute("requestDto") Adt39RequestDto requestDto, Model model) throws Exception {


        PipeParser pipeParser = new PipeParser();

        Message adt = pipeParser.parse(requestDto.getTemplate());
        ParserConfiguration pc = new ParserConfiguration();
        HapiContext context = new DefaultHapiContext();

        final SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);
        final SSLSocketFactory sslSocketFactory = sslConnectionFactory.getSSLSocketFactory();


        context.setSocketFactory(new SocketFactory() {
                                     public static final int DEFAULT_ACCEPTED_SOCKET_TIMEOUT = 500;
                                     private int myAcceptedSocketTimeout = 500;

                                     public int getAcceptedSocketTimeout() {
                                         return this.myAcceptedSocketTimeout;
                                     }

                                     public void setAcceptedSocketTimeout(int theAcceptedSocketTimeout) {
                                         if (theAcceptedSocketTimeout < 0) {
                                             throw new IllegalArgumentException("Timeout can\'t be negative");
                                         } else {
                                             this.myAcceptedSocketTimeout = theAcceptedSocketTimeout;
                                         }
                                     }

                                     public Socket createSocket() throws IOException {
                                         Socket retVal = javax.net.SocketFactory.getDefault().createSocket();
                                         retVal.setKeepAlive(true);
                                         retVal.setTcpNoDelay(true);
                                         return retVal;
                                     }

                                     public Socket createTlsSocket() throws IOException {
                                         Socket retVal = sslSocketFactory.createSocket();
                                         retVal.setKeepAlive(true);
                                         retVal.setTcpNoDelay(true);
                                         return retVal;
                                     }

                                     public ServerSocket createServerSocket() throws IOException {
                                         return ServerSocketFactory.getDefault().createServerSocket();
                                     }

                                     public ServerSocket createTlsServerSocket() throws IOException {
                                         return SSLServerSocketFactory.getDefault().createServerSocket();
                                     }

                                     public void configureNewAcceptedSocket(Socket theSocket) throws SocketException {
                                         theSocket.setSoTimeout(this.myAcceptedSocketTimeout);
                                     }
                                 }
        );

        //ConnectionHub connectionHub = ConnectionHub.getNewInstance(context);

//new PipeParser(), MinLowerLayerProtocol.class,

        Connection connection = context.newClient(requestDto.getHost(), Integer.valueOf(requestDto.getPort()), true);
        Initiator initiator = connection.getInitiator();
        Message response = initiator.sendAndReceive(adt);
        String responseString = pipeParser.encode(response);
        MSA msa = (MSA) response.get("MSA");

        AdtResponseDto responseDto = new AdtResponseDto();
        responseDto.setStatus("AA".equals(msa.getAcknowledgmentCode().getValue()));
        responseDto.setText(responseString);

        model.addAttribute("responseDto", responseDto);

        return "send-adt-39";
    }


}
