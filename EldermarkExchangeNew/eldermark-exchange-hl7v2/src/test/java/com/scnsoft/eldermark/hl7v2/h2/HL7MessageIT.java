package com.scnsoft.eldermark.hl7v2.h2;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.Message;
import com.scnsoft.eldermark.dao.AdtMessageDao;
import com.scnsoft.eldermark.entity.xds.datatype.*;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7DefinedCodeTable;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7UserDefinedCodeTable;
import com.scnsoft.eldermark.entity.xds.message.ADTA08;
import com.scnsoft.eldermark.entity.xds.segment.*;
import com.scnsoft.eldermark.hl7v2.source.HL7v2IntegrationPartner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;

public class HL7MessageIT extends BaseHL7H2IT {
    //we need to use unique message control to be able to fetch by message control id in test
    private static final AtomicInteger messageControlIdAtomic = new AtomicInteger(1);

    @Value("${hl7server.port}")
    private int tcpServerPort;

    @Autowired
    private HapiContext h2hl7v2HapiContext;

    @Autowired
    private AdtMessageDao adtMessageDao;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void testExchangeMessage1LogAndClient() throws HL7Exception, LLPException, IOException {
        var messageControlId = messageControlIdAtomic.getAndIncrement();
        var msg = "MSH|^~\\&|OTHER_KIOSK|HIMSSSANDIEGO|XDSb_REG_MISYS|MISYS|20160112163931||ADT^A08^ADT_A01|" + messageControlId + "|P|2.5|\r" +
                "EVN|A08|200911011022|200911021022|1|74357|20180803141432|event^facility^iso\r" +
                "PID|1|5057|ChounoRieka^^^IHENA&1.3.6.1.4.1.21367.2010.1.2.300&ISO^ANC^fqsdf&4.3.2.2.2.1.&ISO~4321^^^FFDSA&1.2.3.4.5.6.7.8&ISO||Frazier^Luck^J^JR^DR^ME^L^A^~Frazier2^Luck2^V^III^Sir^PHD^TEMP^I|Rogers^Fiona~fff^qwer|19771208|M|alLast^alFirst^H~Alias2|2106-3~1002-5|820 JORIE BLVD^des^CHICAGO^IL^60523^USA^H^Des2^County^Census^A|GL|379-1212~123456789^BPN^BP^asdf@asdf.com^1^234^125^43213^text|271-3434~5431212345|EN|S|ABC|MRN12345001^2^M10|123654987|987654^NC^20220515|Mother ident~fdsasdf|N~H|Deerfield||1|United States Of America~Canada|Military|USA|20180701114900|N\r" +
                "PV1|1|I|159^R1^B1^FACILITY&4.3.2.1&ISO^LSTATUS^C^BUILDING1^FLOOR1^locdescr|A||166^42^1^FFF&4.4.4.4.4.4&ISO^STAT3^D^654^1^locdescr2|1234567890^Smith^Jack^T^JR^DR^MD^T1234^AA&5.4.3.2.1&ISO^A^-^-^ACSN^AF&5.4.3.2.1&ISO^A|987654222^referring^Doctor^J^III^Mr^MT^T4321^AAA&5.4.3.1&ISO^K^-^-^ANT^AFF&5.3.2.1&ISO^P|22222222^Consulting^Doc^Q^JJ^Sr^PA^T4444^A&5.4.3.1.1&ISO^NAV^-^-^DI^FFF&5.3.2.1.1&ISO^I|||PRE|R|5|A0~A7||55543214^doc^admitting~54532345^doc2^admitting2|||||||||||||||||||05|DISCHARGE LOCATION^20211020163110||servicing facility|||||20180613163209|20210917163215|||||||5435^other^provider~5352^other2^provider2\r" +
                "AL1|1|DA|00026^Penicillins^MDDX^cc^code name^I10|MI|UNK~Rash|20171026|\r" +
                "AL1|2|EA|00034^Sulfa Antibiotics^MDDX|U|UNK|\r" +
                "DG1|1|ICD10|F32.9^Major depressive disorder, single episode, unspecified^I10^0000025^Depression^MDDX|diagnosis description|20170605000000|F||||||||||54312^House^Greg^Q~6666^fam^given|||||\r" +
                "DG1|1||0005.0^STAPH FOOD POISONING^I9C||20070816|\r" +
                "DG1|2||535.61^DUODENITIS W/HEMORRHAGE^I9C||20070816|\r" +
                "DG1|3||787.01^NAUSEA WITH VOMITING^I9C||20070816|W\r" +
                "PR1|1|I9C|111^CODE151^NoCS^AltComps^AltText^NoAltCS|Common Procedure|20171110034253-0500|A|||||||||0020^Typhoid fever^ICD-9-CM^AltComps^AltText^NoAltCS\r" +
                "GT1|3|GR16383~4443234|Ponsovich^John~Smith^Asdf||moscow~New York|5555555555^PRN^CP^^^555^5555555~43214|123|20060601|A|Guarantor type|CHD|||||||||3||||||||||||||||French\r" +
                "GT1|4||paul^given||minsk|53434|78978||A|||||||||||||||||||||||||||ENGLISH\r" +
                "IN1|2|PRIV^Private Pay^Pharmsoft|ins company id|Private Pay^A^554325^-^-^REWQ&4.2.4.1&ISO^ANC^QWER&4.2.4.1.1&ISO^A|PO Box 1007^^NEW YORK^NY^10108||54322345~4555555|group number|group name 1~group name 2|||20220715|20220506||PLT|name1~name2|EMC|19561010101852|123 street^^New York^NY^10001^USA||IN|2||||||0|||M|||||554323452|||||||||||||||||\r" +
                "IN1|3|MEDC3^610014 - MEDC3 COMMERICAL HIT^Pharmsoft|610014|610014 - MEDC3 COMMERICAL HIT||||TXS000000287356|||||||||||||IN|201|||||||||||||||||||||||||||||||\r";

        sendMessage(msg);

        var log = runValidations("ChounoRieka",
                "1.3.6.1.4.1.21367.2010.1.2.300",
                "ISO",
                HL7v2IntegrationPartner.EXCHANGE,
                "ADT^A08^ADT_A01|" + messageControlId + "|P");

        validateAdtMessageExchangeMessage1(log.getAdtMessageId(), messageControlId);
    }

    @Test
    void testExchangeMessage2LogAndClient() throws HL7Exception, LLPException, IOException {
        var messageControlId = messageControlIdAtomic.getAndIncrement();
        var msg = "MSH|^~\\&|OTHER_KIOSK|HIMSSSANDIEGO|XDSb_REG_MISYS|MISYS|20160112163932||ADT^A08|" + messageControlId + "|P|2.5|\r" +
                "EVN|A08|20160112163931|||||159|\r" +
                "PID|1|5057|ChounoRieka^^^IHENA&1.3.6.1.4.1.21367.2010.1.2.300&ISO||Chouno^Rieka^J||19251104000000|F|||4663 Lakeland Park Drive^^Cartersville^GA^30120||7706065813||||||672073840\r" +
                "PV1|1|I|159||||1234567890^Smith^Jack|\r" +
                "AL1|1|DA|00026^Penicillins^MDDX|U|UNK|\r" +
                "AL1|2|DA|00034^Sulfa Antibiotics^MDDX|U|UNK|\r" +
                "DG1|1|ICD10|F32.9^Major depressive disorder, single episode, unspecified^I10^0000025^Depression^MDDX||20170605000000||||||||||||||||\r" +
                "DG1|1||0005.0^STAPH FOOD POISONING^I9C||20070816|\r" +
                "DG1|2||535.61^DUODENITIS W/HEMORRHAGE^I9C||20070816|\r" +
                "DG1|3||787.01^NAUSEA WITH VOMITING^I9C||20070816|\r" +
                "IN1|2|PRIV^Private Pay^Pharmsoft||Private Pay|||||||||||||||||IN|2|||||||||||||||||||||||||||||||\r" +
                "IN1|3|MEDC3^610014 - MEDC3 COMMERICAL HIT^Pharmsoft|610014|610014 - MEDC3 COMMERICAL HIT||||TXS000000287356|||||||||||||IN|201|||||||||||||||||||||||||||||||\r";

        sendMessage(msg);

        runValidations("ChounoRieka",
                "1.3.6.1.4.1.21367.2010.1.2.300",
                "ISO",
                HL7v2IntegrationPartner.EXCHANGE,
                "ADT^A08|" + messageControlId + "|P");
    }

    @Test
    void testYardiMessage1LogAndClient() throws HL7Exception, LLPException, IOException {
        var messageControlId = messageControlIdAtomic.getAndIncrement();
        var msg = "MSH|^~\\&|YARDI|K301|YARDI|159|20160105100233||ADT^A01|" + messageControlId + "|P|2.5\r" +
                "EVN|A01|20160105100233|||||159\r" +
                "PID|1|5497|RN123||NGO^ROGER^H||19450401|M|||430 S FAIRVIEW AVE^APT 2^GOLETA^CA^93117||8051118888|||||||611337777|\r" +
                "PV1|1|I|159||||1234567890^Smith^Jack||||||||||||||||||||||||||||||||||||||20160102|20170102|\r" +
                "AL1|1||^Peanuts|\r" +
                "AL1|2||^Shellfish|\r";

        sendMessage(msg);

        runValidations("RN123",
                HL7v2IntegrationPartner.YARDI_OID,
                "ISO",
                HL7v2IntegrationPartner.YARDI,
                "ADT^A01|" + messageControlId + "|P");
    }

    @Test
    void testYardiMessage2LogAndClient() throws HL7Exception, LLPException, IOException {
        var messageControlId = messageControlIdAtomic.getAndIncrement();
        var msg = "MSH|^~\\&|YARDI|K301|YARDI|159|20160112163931||ADT^A08|" + messageControlId + "|P|2.5\r" +
                "EVN|A08|20160112163931|||||159\r" +
                "PID|1|5057|ChounoRieka||Chouno^Rieka^J||19251104000000|F|||4663 Lakeland Park Drive^^Cartersville^GA^30120||7706065813||||||672073840\r" +
                "PV1|1|I|159||||1234567890^Smith^Jack\r" +
                "AL1|1|DA|00026^Penicillins^MDDX|U|UNK\r" +
                "AL1|2|DA|00034^Sulfa Antibiotics^MDDX|U|UNK\r" +
                "DG1|1|ICD10|F32.9^Major depressive disorder, single episode, unspecified^I10^0000025^Depression^MDDX||20170605000000\r" +
                "DG1|1||0005.0^STAPH FOOD POISONING^I9C||20070508\r" +
                "DG1|2||535.61^DUODENITIS W/HEMORRHAGE^I9C||20070508\r" +
                "DG1|3||787.01^NAUSEA WITH VOMITING^I9C||20070508\r" +
                "IN1|1|03 3HOSP^HOSPICE OF CITRUS COUNTY^Pharmsoft|011891|HOSPICE OF CITRUS COUNTY|||||||||||||SEL^Self||||IN|1\r" +
                "IN1|2|03 3AV1^SILVERSCRIPT/AVMED^Pharmsoft|004336|SILVERSCRIPT/AVMED||||RXCVSD|||||||||SEL^Self||||IN|2\r" +
                "IN1|3|03 3ADV^ADV CAREMARK^Pharmsoft|004336|ADV CARESTAFF|PHARMACY HELP# 800-555-5555^AMBETTER PHHD #877-555-5555|||RX3843|||||||||||||IN|3\r";

        sendMessage(msg);

        runValidations("ChounoRieka",
                HL7v2IntegrationPartner.YARDI_OID,
                "ISO",
                HL7v2IntegrationPartner.YARDI,
                "ADT^A08|" + messageControlId + "|P");
    }

    @Test
    void testYardiMessage3LogAndClient() throws HL7Exception, LLPException, IOException {
        var messageControlId = messageControlIdAtomic.getAndIncrement();
        var msg = "MSH|^~\\&|YardiTest|YardiCommunity|SimplyHIE|Yardi|20220627150941||ADT^A05|" + messageControlId + "|P|2.5||||||\r" +
                "EVN|A05|20220627150941|20220627150941||||Yardi\r" +
                "PID|1||29035||Solo^Han^||19500202000000|M|||329 Silver Street Rd^^Malvern^PA^19876|^|||^|UTD|^||887859654||||||||||\r" +
                "PV1|1|I^|Tree Top^518     ^^YardiCommunity||||&&^^^||||||||||&&^^^|||||||||||||||||||||||||||20220601|\r";
        sendMessage(msg);

        runValidations("29035",
                HL7v2IntegrationPartner.YARDI_OID,
                "ISO",
                HL7v2IntegrationPartner.YARDI,
                "ADT^A05|" + messageControlId + "|P");
    }

    @Test
    void testYardiMessage4LogAndClient() throws HL7Exception, LLPException, IOException {
        var messageControlId = messageControlIdAtomic.getAndIncrement();
        var msg = "MSH|^~\\&|YardiTest|YardiCommunity|SimplyHIE|Yardi|20220627151255||ADT^A60|" + messageControlId + "|P|2.5||||||\r" +
                "EVN|A60|20220627151255|20220627151255||||Yardi\r" +
                "PID|1||29035||Solo^Han^||19500202000000|M|||329 Silver Street Rd^^Malvern^PA^19876|^|||^|UTD|^||887859654||||||||||\r" +
                "PV1|1|I^|Tree Top^518     ^^YardiCommunity||||&&^^^||||||||||&&^^^|||||||||||||||||||||||||||20220601|\r" +
                "AL1|1|DA|^Codeine\r";
        sendMessage(msg);

        runValidations("29035",
                HL7v2IntegrationPartner.YARDI_OID,
                "ISO",
                HL7v2IntegrationPartner.YARDI,
                "ADT^A60|" + messageControlId + "|P");
    }

    @Test
    void testPrognocisMessage1() throws HL7Exception, LLPException, IOException {
        var messageControlId = messageControlIdAtomic.getAndIncrement();
        var msg = "MSH|^~\\&|PROGNOCIS|Blount Senior Care Partners, PLLC|||202205231201||ADT^A08|" + messageControlId + "|P|2.3|||||||||||\r" +
                "EVN|A08|202205231201||||\r" +
                "PID|1||SCL17150^^^^||Dummy^Simplyconnect^^^Mr.||19561010|M||1002-5^American Indian or Alaska Native^|123 street^^New York^NY^10001^USA^M||9999999999^PRN^PH^^^999^9999999~6565265216^PRN^CP^^^656^5265216|4546759565^WPN^PH^^^454^6759565||Single|Catholic Christian|SCL17150||||2135-2^Hispanic or Latino^HL2063||||||USA||\r" +
                "PV1||O|^^^^^CL^^^Blount Senior Care Partners, PLLC||||1720242480^Garrido-Zambrano^Alex^I^^Dr.|||||||||||||||||||||||||||||||||||||||||||||\r" +
                "IN1|1||^|1199 Local Benefit Fund|PO Box 1007^^NEW YORK^NY^10108||6464737160^WPN^PH^^^646^4737160|987654|||||||FECA|Dummy^Simplyconnect|Self|195610100000|123 street^^New York^NY^10001^USA|||||||||0|||N|||||1EG4-TE5-MK72||||||||||||||\r" +
                "IN1|2||^|360 Alliance  Gilsbar|PO Box 998^^COVINGTON^LA^70434||8882159841^WPN^PH^^^888^2159841|788545|||||||OTHER|Dummy^Simplyconnect|Self|195610100000|123 street^^New York^NY^10001^USA|||||||||0|||N|||||4159||||||||||||||\r" +
                "GT1|1|GR16383|Dummy^Blount^^^Mr.|||5555555555^PRN^CP^^^555^5555555||20060601|M||Spouse|||||||||7446|||||||||||||||||||||||||||||||||||\r";

        sendMessage(msg);

        runValidations("SCL17150",
                HL7v2IntegrationPartner.PROGNOCIS_OID,
                "ISO",
                HL7v2IntegrationPartner.PROGNOCIS,
                "ADT^A08|" + messageControlId + "|P");
    }

    @Test
    void testPrognocisMessage2() throws HL7Exception, LLPException, IOException {
        var messageControlId = messageControlIdAtomic.getAndIncrement();
        var msg = "MSH|^~\\&|PROGNOCIS|Blount Senior Care Partners, PLLC|||202205231159||ADT^A04|" + messageControlId + "|P|2.3|||||||||||\r" +
                "EVN|A04|202205231159||||\r" +
                "PID|1||SCL17150^^^^||Dummy^Simplyconnect^^^Mr.||19561010|M||1002-5^American Indian or Alaska Native^|123 street^^New York^NY^10001^USA^M||9999999999^PRN^PH^^^999^9999999~6565265216^PRN^CP^^^656^5265216|4546759565^WPN^PH^^^454^6759565||Single|Catholic Christian|SCL17150||||2135-2^Hispanic or Latino^HL2063||||||USA||\r" +
                "PV1||O|^^^^^CL^^^Blount Senior Care Partners, PLLC||||1720242480^Garrido-Zambrano^Alex^I^^Dr.|||||||||||||||||||||||||||||||||||||||||||||\r" +
                "GT1|1|GR16383|Dummy^Blount^^^Mr.|||5555555555^PRN^CP^^^555^5555555||20060601|M||Spouse|||||||||7446|||||||||||||||||||||||||||||||||||\r";

        sendMessage(msg);

        runValidations("SCL17150",
                HL7v2IntegrationPartner.PROGNOCIS_OID,
                "ISO",
                HL7v2IntegrationPartner.PROGNOCIS,
                "ADT^A04|" + messageControlId + "|P");
    }


    @Test
    void testUnknownPartnerMessage() throws HL7Exception, LLPException, IOException {
        var messageControlId = messageControlIdAtomic.getAndIncrement();
        var msg = "MSH|^~\\&|UNKNOWN_PARTNER|HIMSSSANDIEGO|XDSb_REG_MISYS|MISYS|20160112163931||ADT^A08|" + messageControlId + "|P|2.5|\r" +
                "EVN|A08|20160112163931|||||159|\r" +
                "PID|1|5057|ChounoRieka^^^IHENA&1.3.6.1.4.1.21367.2010.1.2.300&ISO||Chouno^Rieka^J||19251104000000|F|||4663 Lakeland Park Drive^^Cartersville^GA^30120||7706065813||||||672073840\r" +
                "PV1|1|I|159||||1234567890^Smith^Jack|\r" +
                "AL1|1|DA|00026^Penicillins^MDDX|U|UNK|\r" +
                "AL1|2|DA|00034^Sulfa Antibiotics^MDDX|U|UNK|\r" +
                "DG1|1|ICD10|F32.9^Major depressive disorder, single episode, unspecified^I10^0000025^Depression^MDDX||20170605000000||||||||||||||||\r" +
                "DG1|1||0005.0^STAPH FOOD POISONING^I9C||20070816|\r" +
                "DG1|2||535.61^DUODENITIS W/HEMORRHAGE^I9C||20070816|\r" +
                "DG1|3||787.01^NAUSEA WITH VOMITING^I9C||20070816|\r" +
                "IN1|2|PRIV^Private Pay^Pharmsoft||Private Pay|||||||||||||||||IN|2|||||||||||||||||||||||||||||||\r" +
                "IN1|3|MEDC3^610014 - MEDC3 COMMERICAL HIT^Pharmsoft|610014|610014 - MEDC3 COMMERICAL HIT||||TXS000000287356|||||||||||||IN|201|||||||||||||||||||||||||||||||\r";

        sendMessage(msg);

        var log = findLog("ADT^A08|" + messageControlId + "|P");
        assertThat(log.getAdtMessageId()).isNull();
        assertThat(log.getAffectedClient1Id()).isNull();
        assertThat(log.getAffectedClient2Id()).isNull();
        assertThat(log.getResolvedIntegration()).isEqualTo(null);
        assertThat(log.isSuccess()).isFalse();
    }

    private void sendMessage(String msg) throws HL7Exception, LLPException, IOException {

        // Create a message to send
//        String msg = "MSH|^~\\&|HIS|RIH|EKG|EKG|199904140038||ADT^A01|12345|P|2.2\r"
//                + "PID|0001|00009874|00001122|A00977|SMITH^JOHN^M|MOM|19581119|F|NOREAL^LINDA^M|C|564 SPRING ST^^NEEDHAM^MA^02494^US|0002|(818)565-1551|(425)828-3344|E|S|C|0000444444|252-00-4414||||SA|||SA||||NONE\r"
//                + "NK1|0222555|NOTREAL^JAMES^R|FA|STREET^OTHER STREET^CITY^ST^55566|(222)111-3333|(888)999-0000|||||||ORGANIZATION\r"
//                + "PV1|0001|I|D.ER^1F^M950^01|ER|P000998|11B^M011^02|070615^BATMAN^GEORGE^L|555888^OKNEL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^VOICE^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|||||5555112333|||666097^DNOTREAL^MANNY^P\r"
//                + "PV2|||0112^TESTING|55555^PATIENT IS NORMAL|NONE|||19990225|19990226|1|1|TESTING|555888^NOTREAL^BOB^K^DR^MD||||||||||PROD^003^099|02|ER||NONE|19990225|19990223|19990316|NONE\r"
//                + "AL1||SEV|001^POLLEN\r"
//                + "GT1||0222PL|NOTREAL^BOB^B||STREET^OTHER STREET^CITY^ST^77787|(444)999-3333|(222)777-5555||||MO|111-33-5555||||NOTREAL GILL N|STREET^OTHER STREET^CITY^ST^99999|(111)222-3333\r"
//                + "IN1||022254P|4558PD|BLUE CROSS|STREET^OTHER STREET^CITY^ST^00990||(333)333-6666||221K|LENIX|||19980515|19990515|||PATIENT01 TEST D||||||||||||||||||02LL|022LP554";

        System.out.println(msg.replaceAll("\r", "\r\r"));
        var p = h2hl7v2HapiContext.getPipeParser();
        Message adt = p.parse(msg);


        // A connection object represents a socket attached to an HL7 server
        Connection connection = h2hl7v2HapiContext.newClient("localhost", tcpServerPort, true);

        // The initiator is used to transmit unsolicited messages
        Initiator initiator = connection.getInitiator();
        Message response = initiator.sendAndReceive(adt);

        String responseString = p.encode(response);
        System.out.println("Received response:\r" + responseString);

        connection.close();
    }

    private void validateAdtMessageExchangeMessage1(Long adtMessageId, Integer messageControlId) {
        new TransactionTemplate(transactionManager).executeWithoutResult(transactionStatus -> {
                    var message = adtMessageDao.findById(adtMessageId).orElseThrow();

                    assertThat(message).isInstanceOf(ADTA08.class);

                    var a08 = (ADTA08) message;

                    validateMSH(a08.getMsh(), messageControlId);
                    validateEVN(a08.getEvn());
                    validatePID(a08.getPid());
                    validatePV1(a08.getPv1());
                    validateDG1List(a08.getDg1List());
                    validateAL1List(a08.getAL1List());
                    validatePR1List(a08.getPr1List());
                    validateGT1List(a08.getGt1List());
                    validateIN1List(a08.getIn1List());
                }
        );

    }

    private void validateMSH(MSHMessageHeaderSegment msh, Integer messageControlId) {
        assertThat(msh).isNotNull();
        assertThat(msh.getFieldSeparator()).isEqualTo("|");
        assertThat(msh.getEncodingCharacters()).isEqualTo("^~\\&");
        assertHd(msh.getSendingApplication(), new HDHierarchicDesignator(
                "OTHER_KIOSK", null, null
        ));
        assertHd(msh.getSendingFacility(), new HDHierarchicDesignator(
                "HIMSSSANDIEGO", null, null
        ));
        assertHd(msh.getReceivingApplication(), new HDHierarchicDesignator(
                "XDSb_REG_MISYS", null, null
        ));
        assertHd(msh.getReceivingFacility(), new HDHierarchicDesignator(
                "MISYS", null, null
        ));

        assertThat(msh.getDatetime()).isEqualTo("2016-01-12T16:39:31Z");

        assertThat(msh.getMessageType()).isNotNull();
        assertThat(msh.getMessageType().getMessageType()).isEqualTo("ADT");
        assertThat(msh.getMessageType().getTriggerEvent()).isEqualTo("A08");
        assertThat(msh.getMessageType().getMessageStructure()).isEqualTo("ADT_A01");

        assertThat(msh.getMessageControlId()).isEqualTo(messageControlId.toString());

        assertThat(msh.getProcessingId()).isNotNull();
        assertThat(msh.getProcessingId().getProcessingId()).isEqualTo("P");
        assertThat(msh.getProcessingId().getProcessingMode()).isNull();

        assertThat(msh.getVersionId()).isEqualTo("2.5.1");
    }

    private void validateEVN(EVNEventTypeSegment evn) {
        assertThat(evn).isNotNull();

        assertThat(evn.getEventTypeCode()).isEqualTo("A08");

        assertThat(evn.getRecordedDatetime()).isEqualTo("2009-11-01T10:22:00Z");

        assertCodedValue(evn.getEventReasonCode(), "1", "Patient request");

        assertThat(evn.getEventOccurred()).isEqualTo("2018-08-03T14:14:32Z");

        assertHd(evn.getEventFacility(), new HDHierarchicDesignator("event", "facility", "iso"));
    }

    private void validatePID(PIDPatientIdentificationSegment pid) {
        assertThat(pid).isNotNull();

        assertCX(pid.getPatientID(), "5057", null, null, null);

        assertThat(pid.getPatientIdentifiers()).hasSize(2);
        assertCX(pid.getPatientIdentifiers().get(0), "ChounoRieka",
                new HDHierarchicDesignator("IHENA", "1.3.6.1.4.1.21367.2010.1.2.300", "ISO"),
                "ANC",
                new HDHierarchicDesignator("fqsdf", "4.3.2.2.2.1.", "ISO"));
        assertCX(pid.getPatientIdentifiers().get(1), "4321",
                new HDHierarchicDesignator("FFDSA", "1.2.3.4.5.6.7.8", "ISO"),
                null, null
        );

        assertThat(pid.getPatientNames()).hasSize(2);
        assertXPN(pid.getPatientNames().get(0),
                new XPNPersonName("Frazier", "Luck", "J", "JR", "DR", "ME", "L", "A")
        );
        assertXPN(pid.getPatientNames().get(1),
                new XPNPersonName("Frazier2", "Luck2", "V", "III", "Sir", "PHD", "TEMP", "I")
        );

        assertThat(pid.getMothersMaidenNames()).hasSize(2);
        assertXPN(pid.getMothersMaidenNames().get(0), new XPNPersonName("Rogers", "Fiona", null));
        assertXPN(pid.getMothersMaidenNames().get(1), new XPNPersonName("fff", "qwer", null));

        assertThat(pid.getDateTimeOfBirth()).isEqualTo("1977-12-08");

        assertCodedValue(pid.getAdministrativeSex(), "M", "Male");

        assertThat(pid.getPatientAliases()).hasSize(2);
        assertXPN(pid.getPatientAliases().get(0), new XPNPersonName("alLast", "alFirst", "H"));
        assertXPN(pid.getPatientAliases().get(1), new XPNPersonName("Alias2", null, null));

        assertThat(pid.getRaces()).hasSize(2);
        assertCE(pid.getRaces().get(0), new CECodedElement("2106-3", null, null), "White");
        assertCE(pid.getRaces().get(1), new CECodedElement("1002-5", null, null), "American Indian or Alaska Native");

        assertThat(pid.getPatientAddresses()).hasSize(1);
        assertXAD(pid.getPatientAddresses().get(0), new XADPatientAddress(
                "820 JORIE BLVD",
                "des",
                "CHICAGO",
                "IL",
                "60523",
                createID("USA", "United States"),
                createID("H", "Home"),
                "Des2", "County",
                "Census",
                createID("A", "Alphabetic (i.e., Default or some single-byte)")
        ));

        assertThat(pid.getPhoneNumbersHome()).hasSize(2);
        assertXTN(pid.getPhoneNumbersHome().get(0), new XTNPhoneNumber("379-1212"));
        assertXTN(pid.getPhoneNumbersHome().get(1), new XTNPhoneNumber("123456789",
                createID("BPN", "Beeper Number"),
                createID("BP", "Beeper"),
                "asdf@asdf.com",
                "1", "234", "125", "43213", "text"));

        assertThat(pid.getPhoneNumbersBusiness()).hasSize(2);
        assertXTN(pid.getPhoneNumbersBusiness().get(0), new XTNPhoneNumber("271-3434"));
        assertXTN(pid.getPhoneNumbersBusiness().get(1), new XTNPhoneNumber("5431212345"));

        assertCE(pid.getPrimaryLanguage(), new CECodedElement("EN"));

        assertCE(pid.getMaritalStatus(), new CECodedElement("S"), "Single");

        assertCE(pid.getReligion(), new CECodedElement("ABC"), "Christian: American Baptist Church");

        assertCX(pid.getPatientAccountNumber(), new CXExtendedCompositeId("MRN12345001")
        );

        assertThat(pid.getSsnNumberPatient()).isEqualTo("123654987");

        assertThat(pid.getDriversLicenseNumber()).isNotNull();
        assertThat(pid.getDriversLicenseNumber().getLicenseNumber()).isEqualTo("987654");
        assertThat(pid.getDriversLicenseNumber().getIssuingStateProvinceCountry()).isEqualTo("NC");
        assertThat(pid.getDriversLicenseNumber().getExpirationDate()).isEqualTo("2022-05-15T00:00:00Z");

        assertThat(pid.getMotherIdentifiers()).hasSize(2);
        assertCX(pid.getMotherIdentifiers().get(0), new CXExtendedCompositeId("Mother ident"));
        assertCX(pid.getMotherIdentifiers().get(1), new CXExtendedCompositeId("fdsasdf"));

        assertThat(pid.getEthnicGroups()).hasSize(2);
        assertCE(pid.getEthnicGroups().get(0), new CECodedElement("N"), "Not Hispanic or Latino");
        assertCE(pid.getEthnicGroups().get(1), new CECodedElement("H"), "Hispanic or Latino");

        assertThat(pid.getBirthPlace()).isEqualTo("Deerfield");

        assertThat(pid.getBirthOrder()).isEqualTo(1);

        assertThat(pid.getCitizenships()).hasSize(2);
        assertCE(pid.getCitizenships().get(0), new CECodedElement("United States Of America"));
        assertCE(pid.getCitizenships().get(1), new CECodedElement("Canada"));

        assertCE(pid.getVeteransMilitaryStatus(), new CECodedElement("Military"));

        assertCE(pid.getNationality(), new CECodedElement("USA"));

        assertThat(pid.getPatientDeathDateAndTime()).isEqualTo("2018-07-01T11:49:00Z");

        assertCodedValue(pid.getPatientDeathIndicator(), "N", "No");
    }

    private void validatePV1(PV1ClientVisitSegment pv1) {
        assertThat(pv1).isNotNull();

        assertCodedValue(pv1.getPatientClass(), "I", "Inpatient");

        assertPL(pv1.getAssignedPatientLocation(), new PLPatientLocation(
                "159", "R1", "B1",
                new HDHierarchicDesignator("FACILITY", "4.3.2.1", "ISO"),
                "LSTATUS", "C", "BUILDING1", "FLOOR1", "locdescr"
        ));

        assertCodedValue(pv1.getAdmissionType(), "A", "Accident");

        assertPL(pv1.getPriorPatientLocation(), new PLPatientLocation(
                "166", "42", "1",
                new HDHierarchicDesignator("FFF", "4.4.4.4.4.4", "ISO"),
                "STAT3", "D", "654", "1", "locdescr2"
        ));

        assertXCN(pv1.getAttendingDoctor(), new XCNExtendedCompositeIdNumberAndNameForPersons(
                "1234567890", "Smith", "Jack", "T", "JR", "DR", "MD",
                "T1234", new HDHierarchicDesignator("AA", "5.4.3.2.1", "ISO"),
                "A", "ACSN", new HDHierarchicDesignator("AF", "5.4.3.2.1", "ISO"),
                "A"));

        assertXCN(pv1.getRefferingDoctor(), new XCNExtendedCompositeIdNumberAndNameForPersons(
                "987654222", "referring", "Doctor", "J", "III", "Mr", "MT",
                "T4321", new HDHierarchicDesignator("AAA", "5.4.3.1", "ISO"),
                "K", "ANT", new HDHierarchicDesignator("AFF", "5.3.2.1", "ISO"),
                "P"));

        assertXCN(pv1.getConsultingDoctor(), new XCNExtendedCompositeIdNumberAndNameForPersons(
                "22222222", "Consulting", "Doc", "Q", "JJ", "Sr", "PA",
                "T4444", new HDHierarchicDesignator("A", "5.4.3.1.1", "ISO"),
                "NAV", "DI", new HDHierarchicDesignator("FFF", "5.3.2.1.1", "ISO"),
                "I"));

        assertThat(pv1.getPreadmitTestIndicator()).isEqualTo("PRE");

        assertCodedValue(pv1.getReadmissionIndicator(), "R", "Re-admission");

        assertCodedValue(pv1.getAdmitSource(), "5", "Transfer from a skilled nursing facility");

        assertThat(pv1.getAmbulatoryStatuses()).hasSize(2);
        assertCodedValue(pv1.getAmbulatoryStatuses().get(0), "A0", "No functional limitations");
        assertCodedValue(pv1.getAmbulatoryStatuses().get(1), "A7", "Speech impaired");

        assertList(pv1.getAdmittingDoctors(), List.of(
                        new XCNExtendedCompositeIdNumberAndNameForPersons("55543214", "doc", "admitting", null),
                        new XCNExtendedCompositeIdNumberAndNameForPersons("54532345", "doc2", "admitting2", null)),
                this::assertXCN);

        assertThat(pv1.getDischargeDisposition()).isEqualTo("05");

        assertThat(pv1.getDischargedToLocation()).isNotNull();
        assertThat(pv1.getDischargedToLocation().getDischargeLocation()).isEqualTo("DISCHARGE LOCATION");
        assertThat(pv1.getDischargedToLocation().getEffectiveDate()).isEqualTo("2021-10-20T16:31:10Z");

        assertThat(pv1.getServicingFacility()).isEqualTo("servicing facility");

        assertThat(pv1.getAdmitDatetime()).isEqualTo("2018-06-13T16:32:09Z");
        assertThat(pv1.getDischargeDatetime()).isEqualTo("2021-09-17T16:32:15Z");

        assertList(pv1.getOtherHealthcareProviders(), List.of(
                        new XCNExtendedCompositeIdNumberAndNameForPersons("5435", "other", "provider", null),
                        new XCNExtendedCompositeIdNumberAndNameForPersons("5352", "other2", "provider2", null)),
                this::assertXCN);
    }

    private void validateDG1List(List<AdtDG1DiagnosisSegment> dg1List) {
        assertThat(dg1List).hasSize(4);

        validateDG1(dg1List.get(0),
                "1",
                "ICD10",
                new CECodedElement("F32.9", "Major depressive disorder, single episode, unspecified", "I10", "0000025", "Depression", "MDDX"),
                "diagnosis description",
                "2017-06-05T00:00:00Z",
                "F", "Final",
                List.of(new XCNExtendedCompositeIdNumberAndNameForPersons(
                                "54312", "House", "Greg", "Q"),
                        new XCNExtendedCompositeIdNumberAndNameForPersons("6666", "fam", "given", null))
        );

        validateDG1(dg1List.get(1),
                "1",
                null,
                new CECodedElement("0005.0", "STAPH FOOD POISONING", "I9C"),
                null,
                "2007-08-16T00:00:00Z",
                null, null,
                List.of()
        );

        validateDG1(dg1List.get(2),
                "2",
                null,
                new CECodedElement("535.61", "DUODENITIS W/HEMORRHAGE", "I9C"),
                null,
                "2007-08-16T00:00:00Z",
                null, null,
                List.of()
        );

        validateDG1(dg1List.get(3),
                "3",
                null,
                new CECodedElement("787.01", "NAUSEA WITH VOMITING", "I9C"),
                null,
                "2007-08-16T00:00:00Z",
                "W", "Working",
                List.of()
        );
    }

    private void validateDG1(AdtDG1DiagnosisSegment dg1,
                             String setId,
                             String diagnosisCodingMethod,
                             CECodedElement diagnosisCode,
                             String diagnosisDescription,
                             String diagnosisDateTime,
                             String diagnosisType, String diagnosisTypeValue,
                             List<XCNExtendedCompositeIdNumberAndNameForPersons> diagnosingClinitians) {
        assertThat(dg1).isNotNull();

        assertThat(dg1.getSetId()).isEqualTo(setId);

        assertThat(dg1.getDiagnosisCodingMethod()).isEqualTo(diagnosisCodingMethod);

        assertCE(dg1.getDiagnosisCode(), diagnosisCode, null);

        assertThat(dg1.getDiagnosisDescription()).isEqualTo(diagnosisDescription);

        assertThat(dg1.getDiagnosisDateTime()).isEqualTo(diagnosisDateTime);

        assertCodedValue(dg1.getDiagnosisType(), diagnosisType, diagnosisTypeValue);

        assertList(dg1.getDiagnosingClinicianList(), diagnosingClinitians, this::assertXCN);
    }

    private void validateAL1List(List<AdtAL1AllergySegment> al1List) {
        assertThat(al1List).hasSize(2);
        validateAL1(al1List.get(0),
                "1",
                new CECodedElement("DA", null, null), "Drug allergy",
                new CECodedElement("00026", "Penicillins", "MDDX", "cc", "code name", "I10"),
                "MI", "Mild",
                List.of("UNK", "Rash"),
                "2017-10-26T00:00:00Z"
        );

        validateAL1(al1List.get(1),
                "2",
                new CECodedElement("EA", null, null), "Environmental Allergy",
                new CECodedElement("00034", "Sulfa Antibiotics", "MDDX"),
                "U", "Unknown",
                List.of("UNK"),
                null
        );
    }

    private void validateAL1(AdtAL1AllergySegment al1,
                             String setId,
                             CECodedElement allergenType, String allergenTypeCodeValue,
                             CECodedElement allergyCode,
                             String severityCode, String severityValue,
                             List<String> reactions,
                             String identificationDate) {
        assertThat(al1).isNotNull();

        assertThat(al1.getSetId()).isEqualTo(setId);

        assertCE(al1.getAllergenType(), allergenType, allergenTypeCodeValue);

        assertCE(al1.getAllergyCode(), allergyCode, null);

        assertCodedValue(al1.getAllergySeverity(), severityCode, severityValue);

        assertThat(al1.getAllergyReactions()).containsExactlyElementsOf(reactions);

        if (identificationDate == null) {
            assertThat(al1.getIdentificationDate()).isNull();
        } else {
            assertThat(al1.getIdentificationDate()).isEqualTo(identificationDate);
        }
    }

    private void validatePR1List(List<PR1ProceduresSegment> pr1List) {
        assertThat(pr1List).hasSize(1);

        validatePR1(
                pr1List.get(0),
                "1",
                "I9C",
                new CECodedElement("111", "CODE151", "NoCS", "AltComps", "AltText", "NoAltCS"),
                "Common Procedure",
                "2017-11-10T08:42:53Z",
                "A", "Anesthesia",
                new CECodedElement("0020", "Typhoid fever", "ICD-9-CM", "AltComps", "AltText", "NoAltCS")
        );
    }

    private void validatePR1(PR1ProceduresSegment pr1,
                             String setId,
                             String procedureCodingMethod,
                             CECodedElement procedureCode,
                             String procedureDescription,
                             String procedureDatetime,
                             String procedureFunctionalType, String procedureFunctionalTypeValue,
                             CECodedElement associatedDiagnosisCode
    ) {
        assertThat(pr1).isNotNull();

        assertThat(pr1.getSetId()).isEqualTo(setId);

        assertThat(pr1.getProcedureCodingMethod()).isEqualTo(procedureCodingMethod);

        assertCE(pr1.getProcedureCode(), procedureCode, null);

        assertThat(pr1.getProcedureDescription()).isEqualTo(procedureDescription);

        assertThat(pr1.getProcedureDatetime()).isEqualTo(procedureDatetime);

        assertCodedValue(pr1.getProcedureFunctionalType(), procedureFunctionalType, procedureFunctionalTypeValue);

        assertCE(pr1.getAssociatedDiagnosisCode(), associatedDiagnosisCode, null);
    }

    private void validateGT1List(List<AdtGT1GuarantorSegment> gt1List) {
        assertThat(gt1List).hasSize(2);

        validateGT1(
                gt1List.get(0),
                "3",
                List.of(new CXExtendedCompositeId("GR16383"), new CXExtendedCompositeId("4443234")),
                List.of(
                        new XPNPersonName("Ponsovich", "John", null),
                        new XPNPersonName("Smith", "Asdf", null)
                ),
                List.of(
                        new XADPatientAddress("moscow"),
                        new XADPatientAddress("New York")
                ),
                List.of(
                        new XTNPhoneNumber(
                                "5555555555",
                                createID("PRN", "Primary Residence Number"),
                                createID("CP", "Cellular Phone"),
                                null,
                                null,
                                "555",
                                "5555555",
                                null,
                                null),
                        new XTNPhoneNumber("43214")
                ),
                "2006-06-01T00:00:00Z",
                "A", "Ambiguous",
                "Guarantor type",
                new CECodedElement("CHD"), "Child",
                "3", "Unemployed",
                new CECodedElement("French")
        );

//        GT1|4||paul^given||minsk|53434|78978||A|||||||||||||||||||||||||||ENGLISH
        validateGT1(
                gt1List.get(1),
                "4",
                List.of(),
                List.of(
                        new XPNPersonName("paul", "given", null)
                ),
                List.of(
                        new XADPatientAddress("minsk")
                ),
                List.of(
                        new XTNPhoneNumber("53434")
                ),
                null,
                "A", "Ambiguous",
                null,
                null, null,
                null, null,
                new CECodedElement("ENGLISH")
        );
    }

    private void validateGT1(AdtGT1GuarantorSegment gt1,
                             String setId,
                             List<CXExtendedCompositeId> guarantorNumbers,
                             List<XPNPersonName> guarantorNames,
                             List<XADPatientAddress> guarantorAddresses,
                             List<XTNPhoneNumber> guarantorPhNums,
                             String guarantorDatetimeOfBirth,
                             String guarantorAdministrativeSex, String guarantorAdministrativeSexValue,
                             String guarantorType,
                             CECodedElement guarantorRelationship, String guarantorRelationshipValue,
                             String guarantorEmploymentStatus, String guarantorEmploymentStatusValue,
                             CECodedElement primaryLanguage) {
        assertThat(gt1).isNotNull();

        assertThat(gt1.getSetId()).isEqualTo(setId);

        assertList(gt1.getGuarantorNumbers(), guarantorNumbers, this::assertCX);

        assertList(gt1.getGuarantorNameList(), guarantorNames, this::assertXPN);

        assertList(gt1.getGuarantorAddressList(), guarantorAddresses, this::assertXAD);

        assertList(gt1.getGuarantorPhNumHomeList(), guarantorPhNums, this::assertXTN);

        assertInstant(gt1.getGuarantorDatetimeOfBirth(), guarantorDatetimeOfBirth);

        assertCodedValue(gt1.getGuarantorAdministrativeSex(), guarantorAdministrativeSex, guarantorAdministrativeSexValue);

        assertThat(gt1.getGuarantorType()).isEqualTo(guarantorType);

        assertCE(gt1.getGuarantorRelationship(), guarantorRelationship, guarantorRelationshipValue);

        assertCodedValue(gt1.getGuarantorEmploymentStatus(), guarantorEmploymentStatus, guarantorEmploymentStatusValue);

        assertCE(gt1.getPrimaryLanguage(), primaryLanguage, null);
    }

    private void assertInstant(Instant actual, String expected) {
        if (actual == null) {
            assertThat(actual).isNull();
        } else {
            assertThat(actual).isEqualTo(expected);
        }
    }

    private void validateIN1List(List<IN1InsuranceSegment> in1List) {
        assertThat(in1List).hasSize(2);

        validateIN1(
                in1List.get(0),
                "2",
                new CECodedElement("PRIV", "Private Pay", "Pharmsoft"),
                new CXExtendedCompositeId("ins company id"),
                new XONExtendedCompositeNameAndIdForOrganizations("Private Pay", "A", "554325",
                        new HDHierarchicDesignator("REWQ", "4.2.4.1", "ISO"), "ANC",
                        new HDHierarchicDesignator("QWER", "4.2.4.1.1", "ISO"), "A"
                ),
                List.of(new XADPatientAddress("PO Box 1007", null, "NEW YORK", "NY", "10108", null, null, null, null, null, null)),
                List.of(new XTNPhoneNumber("54322345"), new XTNPhoneNumber("4555555")),
                "group number",
                List.of(new XONExtendedCompositeNameAndIdForOrganizations("group name 1"),
                        new XONExtendedCompositeNameAndIdForOrganizations("group name 2")
                ),
                "2022-07-15T00:00:00Z",
                "2022-05-06T00:00:00Z",
                "PLT",
                List.of(new XPNPersonName("name1", null, null), new XPNPersonName("name2", null, null)),
                new CECodedElement("EMC"), "Emergency contact",
                "1956-10-10T10:18:52Z",
                List.of(new XADPatientAddress("123 street", null, "New York", "NY", "10001",
                        createID("USA", "United States"), null, null, null, null, null)),
                "0",
                "M", "Maternity",
                "554323452");

        validateIN1(
                in1List.get(1),
                "3",
                new CECodedElement("MEDC3", "610014 - MEDC3 COMMERICAL HIT", "Pharmsoft"),
                new CXExtendedCompositeId("610014"),
                new XONExtendedCompositeNameAndIdForOrganizations("610014 - MEDC3 COMMERICAL HIT"),
                List.of(),
                List.of(),
                "TXS000000287356",
                List.of(),
                null,
                null,
                null,
                List.of(),
                null, null,
                null,
                List.of(),
                null,
                null, null,
                null);
    }

    private void validateIN1(IN1InsuranceSegment in1,
                             String setId,
                             CECodedElement insurancePlanId,
                             CXExtendedCompositeId insuranceCompanyId,
                             XONExtendedCompositeNameAndIdForOrganizations insuranceCompanyName,
                             List<XADPatientAddress> insuranceCompanyAddresses,
                             List<XTNPhoneNumber> insuranceCoPhoneNumbers,
                             String groupNumber,
                             List<XONExtendedCompositeNameAndIdForOrganizations> groupNames,
                             String planEffectiveDate,
                             String planExpirationDate,
                             String planType,
                             List<XPNPersonName> namesOfInsured,
                             CECodedElement insuredsRelationshipToPatient, String insuredsRelationshipToPatientValue,
                             String insuredsDateOfBirth,
                             List<XADPatientAddress> insuredsAddresses,
                             String preAdmitCert,
                             String typeOfAgreementCode, String typeOfAgreementCodeValue,
                             String policyNumber
    ) {
        assertThat(in1).isNotNull();

        assertThat(in1.getSetId()).isEqualTo(setId);

        assertCE(in1.getInsurancePlanId(), insurancePlanId, null);

        assertCX(in1.getInsuranceCompanyId(), insuranceCompanyId);

        assertXON(in1.getInsuranceCompanyName(), insuranceCompanyName);

        assertList(in1.getInsuranceCompanyAddresses(), insuranceCompanyAddresses, this::assertXAD);

        assertList(in1.getInsuranceCoPhoneNumbers(), insuranceCoPhoneNumbers, this::assertXTN);

        assertThat(in1.getGroupNumber()).isEqualTo(groupNumber);

        assertList(in1.getGroupNames(), groupNames, this::assertXON);

        if (planEffectiveDate == null) {
            assertThat(in1.getPlanEffectiveDate()).isNull();
        } else {
            assertThat(in1.getPlanEffectiveDate()).isEqualTo(planEffectiveDate);
        }

        if (planEffectiveDate == null) {
            assertThat(in1.getPlanExpirationDate()).isNull();
        } else {
            assertThat(in1.getPlanExpirationDate()).isEqualTo(planExpirationDate);
        }

        assertThat(in1.getPlanType()).isEqualTo(planType);

        assertList(in1.getNamesOfInsured(), namesOfInsured, this::assertXPN);

        assertCE(in1.getInsuredsRelationshipToPatient(), insuredsRelationshipToPatient, insuredsRelationshipToPatientValue);

        if (insuredsDateOfBirth == null) {
            assertThat(in1.getInsuredsDateOfBirth()).isNull();
        } else {
            assertThat(in1.getInsuredsDateOfBirth()).isEqualTo(insuredsDateOfBirth);
        }

        assertList(in1.getInsuredsAddresses(), insuredsAddresses, this::assertXAD);

        assertThat(in1.getPreAdmitCert()).isEqualTo(preAdmitCert);

        assertCodedValue(in1.getTypeOfAgreementCode(), typeOfAgreementCode, typeOfAgreementCodeValue);

        assertThat(in1.getPolicyNumber()).isEqualTo(policyNumber);
    }

    private void assertXON(XONExtendedCompositeNameAndIdForOrganizations xon,
                           XONExtendedCompositeNameAndIdForOrganizations expected) {
        if (expected == null) {
            assertThat(xon).isNull();
        } else {
            assertThat(xon).usingRecursiveComparison()
                    .ignoringFields("id", "assigningFacility.id", "assigningAuthority.id")
                    .isEqualTo(expected);
        }
    }


    private void assertXCN(XCNExtendedCompositeIdNumberAndNameForPersons xcn, XCNExtendedCompositeIdNumberAndNameForPersons expected) {
        if (expected == null) {
            assertThat(xcn).isNull();
        } else {
            assertThat(xcn).isNotNull();

            assertThat(xcn)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "assigningAuthority.id", "assigningFacility.id")
                    .isEqualTo(expected);
        }
    }

    private void assertPL(PLPatientLocation pl, PLPatientLocation expected) {
        if (expected == null) {
            assertThat(pl).isNull();
        } else {
            assertThat(pl).isNotNull();

            assertThat(pl)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "facility.id")
                    .isEqualTo(expected);
        }

    }

    private void assertXAD(XADPatientAddress xad, XADPatientAddress expected) {
        if (expected == null) {
            assertThat(xad).isNull();
        } else {
            assertThat(xad).isNotNull();

            assertThat(xad).usingRecursiveComparison()
                    .ignoringFields("id", "addressType.id", "country.id", "addressRepresentationCode.id"
                            , "country.hl7CodeTable.id", "addressType.hl7CodeTable.id", "addressRepresentationCode.hl7CodeTable.id"
                    )
                    .isEqualTo(expected);
        }
    }

    private void assertXAD(XADPatientAddress xad, XADPatientAddress expected, String asdf, String fdsa) {
        if (expected == null) {
            assertThat(xad).isNull();
        } else {
            assertThat(xad).isNotNull();

            assertThat(xad).usingRecursiveComparison()
                    .ignoringFields("id", "addressType", "country", "addressRepresentationCode") //rest
                    .isEqualTo(expected);
        }
    }

    private void assertCE(CECodedElement ce, CECodedElement expected) {
        assertCE(ce, expected, null);
    }

    private void assertCE(CECodedElement ce, CECodedElement expected, String hl7CodeTableValue) {
        if (expected == null) {
            assertThat(ce).isNull();
        } else {
            assertThat(ce).usingRecursiveComparison().ignoringFields("id", "hl7CodeTable")
                    .isEqualTo(expected);

            if (hl7CodeTableValue != null) {
                assertThat(ce.getHl7CodeTable()).isNotNull();
                assertThat(ce.getHl7CodeTable().getCode()).isEqualTo(expected.getIdentifier());
                assertThat(ce.getHl7CodeTable().getValue()).isEqualTo(hl7CodeTableValue);
            }
        }
    }

    private void assertXPN(XPNPersonName xpn, XPNPersonName expected) {
        if (expected == null) {
            assertThat(xpn).isNull();
        } else {
            assertThat(xpn).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
        }
    }

    private void assertHd(HDHierarchicDesignator hd, HDHierarchicDesignator expected) {
        if (expected == null) {
            assertThat(hd).isNull();
        } else {
            assertThat(hd).isNotNull();
            assertThat(hd.getNamespaceID()).isEqualTo(expected.getNamespaceID());
            assertThat(hd.getUniversalID()).isEqualTo(expected.getUniversalID());
            assertThat(hd.getUniversalIDType()).isEqualTo(expected.getUniversalIDType());
        }
    }

    private void assertCX(CXExtendedCompositeId cx, CXExtendedCompositeId expected) {
        if (expected == null) {
            assertThat(cx).isNull();
        } else {
            assertCX(cx, expected.getpId(), expected.getAssigningAuthority(), expected.getIdentifierTypeCode(), expected.getAssigningFacility());
        }
    }

    private void assertCX(CXExtendedCompositeId cx, String idNumber, HDHierarchicDesignator assigningAuthority,
                          String identifierTypeCode, HDHierarchicDesignator assigningFacility) {
        assertThat(cx).isNotNull();
        assertThat(cx.getpId()).isEqualTo(idNumber);
        assertHd(cx.getAssigningAuthority(), assigningAuthority);
        assertThat(cx.getIdentifierTypeCode()).isEqualTo(identifierTypeCode);
        assertHd(cx.getAssigningFacility(), assigningFacility);
    }

    private void assertCodedValue(CodedValueForHL7Table coded, String code, String description) {
        if (code == null) {
            assertThat(coded).isNull();
        } else {
            assertThat(coded.getRawCode()).isEqualTo(code);
            if (description != null) {
                assertThat(coded.getHl7CodeTable()).isNotNull();
                assertThat(coded.getHl7CodeTable().getCode()).isEqualTo(code);
                assertThat(coded.getHl7CodeTable().getValue()).isEqualTo(description);
            }
        }
    }

    private <T> void assertList(List<T> actual, List<T> expected, BiConsumer<T, T> comparator) {
        if (expected == null) {
            assertThat(actual).isNull();
        }
        assertThat(actual).hasSameSizeAs(expected);
        for (int i = 0; i < expected.size(); ++i) {
            comparator.accept(actual.get(i), expected.get(i));
        }
    }

    private void assertXTN(XTNPhoneNumber xtn, XTNPhoneNumber expected) {
        if (expected == null) {
            assertThat(xtn).isNull();
        } else {
            assertThat(xtn).usingRecursiveComparison()
                    .ignoringFields("id", "telecommunicationUseCode.id", "telecommunicationEquipmentType.id",
                            "telecommunicationUseCode.hl7CodeTable.id", "telecommunicationEquipmentType.hl7CodeTable.id"
                    )
                    .isEqualTo(expected);
        }
    }

    private <TABLE extends HL7UserDefinedCodeTable> ISCodedValueForUserDefinedTables<TABLE> createIS(String rawCode, String value) {
        return new ISCodedValueForUserDefinedTables<>(
                rawCode,
                new HL7UserDefinedCodeTable(rawCode, value) {
                }
        );
    }

    private <TABLE extends HL7DefinedCodeTable> IDCodedValueForHL7Tables<TABLE> createID(String rawCode, String value) {
        return new IDCodedValueForHL7Tables<>(
                rawCode,
                new HL7DefinedCodeTable(rawCode, value) {
                }
        );
    }
}
