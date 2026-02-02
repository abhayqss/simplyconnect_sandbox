package com.scnsoft.eldermark.hl7v2;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication(scanBasePackages = "com.scnsoft.eldermark")
public class HL7v2Main implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(HL7v2Main.class, args);
    }

    @Autowired
    private HL7Service tcpServer;

    @Override
    public void run(String... args) throws Exception {
        tcpServer.startAndWait();

        //testClient();
    }

    @Autowired
    HapiContext hl7v2HapiContext;

    private void testClient() throws HL7Exception, LLPException, IOException {

        // Create a message to send
//        String msg = "MSH|^~\\&|HIS|RIH|EKG|EKG|199904140038||ADT^A01|12345|P|2.2\r"
//                + "PID|0001|00009874|00001122|A00977|SMITH^JOHN^M|MOM|19581119|F|NOREAL^LINDA^M|C|564 SPRING ST^^NEEDHAM^MA^02494^US|0002|(818)565-1551|(425)828-3344|E|S|C|0000444444|252-00-4414||||SA|||SA||||NONE\r"
//                + "NK1|0222555|NOTREAL^JAMES^R|FA|STREET^OTHER STREET^CITY^ST^55566|(222)111-3333|(888)999-0000|||||||ORGANIZATION\r"
//                + "PV1|0001|I|D.ER^1F^M950^01|ER|P000998|11B^M011^02|070615^BATMAN^GEORGE^L|555888^OKNEL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^VOICE^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|||||5555112333|||666097^DNOTREAL^MANNY^P\r"
//                + "PV2|||0112^TESTING|55555^PATIENT IS NORMAL|NONE|||19990225|19990226|1|1|TESTING|555888^NOTREAL^BOB^K^DR^MD||||||||||PROD^003^099|02|ER||NONE|19990225|19990223|19990316|NONE\r"
//                + "AL1||SEV|001^POLLEN\r"
//                + "GT1||0222PL|NOTREAL^BOB^B||STREET^OTHER STREET^CITY^ST^77787|(444)999-3333|(222)777-5555||||MO|111-33-5555||||NOTREAL GILL N|STREET^OTHER STREET^CITY^ST^99999|(111)222-3333\r"
//                + "IN1||022254P|4558PD|BLUE CROSS|STREET^OTHER STREET^CITY^ST^00990||(333)333-6666||221K|LENIX|||19980515|19990515|||PATIENT01 TEST D||||||||||||||||||02LL|022LP554";

        var msg = "MSH|^~\\&|OTHER_KIOSK|HIMSSSANDIEGO|XDSb_REG_MISYS|MISYS|20160112163931||ADT^A08|0|P|2.5|\r" +
                "EVN|A08|20160112163931|||||159|\r" +
                "PID|1|5057|ChounoRieka^^^IHENA&1.3.6.1.4.1.21367.2010.1.2.300&ISO||Chouno^Rieka^J||19251104000000|F|||4663 Lakeland Park Drive^^Cartersville^GA^30120||7706065813||||||672073840\r" +
                "PV1|1|I|159||||1234567890^Smith^Jack|\r" +
                "AL1|1|DA|00026^Penicillins^MDDX|U|UNK|\r" +
                "AL1|2|DA|00034^Sulfa Antibiotics^MDDX|U|reaction1~reaction2|\r" +
                "DG1|1|ICD10|F32.9^Major depressive disorder, single episode, unspecified^I10^0000025^Depression^MDDX||20170605000000||||||||||||||||\r" +
                "DG1|1||0005.0^STAPH FOOD POISONING^I9C||20070816|\r" +
                "DG1|2||535.61^DUODENITIS W/HEMORRHAGE^I9C||20070816|\r" +
                "DG1|3||787.01^NAUSEA WITH VOMITING^I9C||20070816|\r" +
                "IN1|2|PRIV^Private Pay^Pharmsoft||Private Pay|||||||||||||||||IN|2|||||||||||||||||||||||||||||||\r" +
                "IN1|3|MEDC3^610014 - MEDC3 COMMERICAL HIT^Pharmsoft|610014|610014 - MEDC3 COMMERICAL HIT||||TXS000000287356|||||||||||||IN|201|||||||||||||||||||||||||||||||\r";


        System.out.println(msg.replaceAll("\r", "\r\n"));
        var p = hl7v2HapiContext.getPipeParser();
        Message adt = p.parse(msg);


        // A connection object represents a socket attached to an HL7 server
//        Connection connection = hl7v2HapiContext.newClient("dev.simplyconnect.me", 3614, true);
        Connection connection = hl7v2HapiContext.newClient("localhost", 3614, true);

        // The initiator is used to transmit unsolicited messages
        Initiator initiator = connection.getInitiator();
        Message response = initiator.sendAndReceive(adt);

        String responseString = p.encode(response);
        System.out.println("Received response:\r" + responseString);

               /*
152        * MSH|^~\&|||||20070218200627.515-0500||ACK|54|P|2.2 MSA|AA|12345
153        */

               /*
156        * If you want to send another message to the same destination, it's fine
157        * to ask the context again for a client to attach to the same host/port.
158        * The context will be smart about it and return the same (already
159        * connected) client Connection instance, assuming it hasn't been closed.
160        */
//        connection = hl7v2HapiContext.newClient("localhost", 3614, true);
//        initiator = connection.getInitiator();
//        response = initiator.sendAndReceive(adt);

               /*
166        * Close the connection when you are done with it. If you are designing a
167        * system which will continuously send out messages, you may want to
168        * consider not closing the connection until you have no more messages to
169        * send out. This is more efficient, as most (if not all) HL7 receiving
170        * applications are capable of receiving lots of messages in a row over
171        * the same connection, even with a long delay between messages.
172        *
173        * See
174        * http://hl7api.sourceforge.net/xref/ca/uhn/hl7v2/examples/SendLotsOfMessages.html
175        * for an example of this.
176        */
        connection.close();

        tcpServer.stopAndWait();
    }
}
