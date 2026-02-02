package com.scnsoft.eldermark.hl7v2.h2.poll.http.yardi;

import com.scnsoft.eldermark.hl7v2.h2.BaseHL7H2IT;
import com.scnsoft.eldermark.hl7v2.source.HL7v2IntegrationPartner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class YardiHttpPollTest extends BaseHL7H2IT {

    //also tests case when date contains '-' which is real case for Yardi
    private final String MSG1 = "<HL7MessageBroker><DestinationIP>dev.simplyconnect.me</DestinationIP><DestinationPort>3614</DestinationPort><response>MSH|^~\\&amp;|YardiTest|TestCommunity|SimplyHIE|Yardi|20220624131429|3Led#gq#^~E6?*Vu&lt;6H^S'B7zvjmb%|ADT^A08|408|P|2.5||||||&#13;EVN|A08|20220624131429|20220624131429||||Yardi&#13;PID|1||27460||Resident^Demo^||20170604000000|M|||329 Silver Street Rd^^Malvern^PA^19876|^|||^|UTD|^||888888888||||||||||&#13;PV1|1|I^|Forest River^504     ^^TestCommunity||||1144265877&amp;BF4927476&amp;^RIEDINGER^JENNIFER^||||||||||&amp;&amp;^^^|||||||||||||||||||||||||||2017-06-01|&#13;AL1|1|DA|^Penicllin&#13;DG1|1||G31.83^Dementia with Lewy bodies^I10^|||W|||||||||||||&#13;DG1|2||^DIABETES MELLITUS^^|||W|||||||||||||&#13;DG1|3||^BENIGN ESSENTIAL HYPERTENSION^^|||W|||||||||||||&#13;DG1|4||^ACUTE PULMONARY HEART DISEASE^^|||W|||||||||||||&#13;</response></HL7MessageBroker>";
    private final String MSG2 = "<HL7MessageBroker><DestinationIP>dev.simplyconnect.me</DestinationIP><DestinationPort>3614</DestinationPort><response>MSH|^~\\&amp;|YardiTest|TestCommunity|SimplyHIE|Yardi|20220624131450|3Led#gq#^~E6?*Vu&lt;6H^S'B7zvjmb%|ADT^A01|409|P|2.5||||||&#13;EVN|A01|20220624131450|20220624131450||||Yardi&#13;PID|1||27460||Resident^Demo^||20170604000000|M|||329 Silver Street Rd^^Malvern^PA^19876|^|||^|UTD|^||888888888||||||||||&#13;PV1|1|I^|Forest River^504     ^^TestCommunity||||1144265877&amp;BF4927476&amp;^RIEDINGER^JENNIFER^||||||||||&amp;&amp;^^^|||||||||||||||||||||||||||2017-06-01|&#13;AL1|1|DA|^Penicllin&#13;DG1|1||G31.83^Dementia with Lewy bodies^I10^|||W|||||||||||||&#13;DG1|2||^DIABETES MELLITUS^^|||W|||||||||||||&#13;DG1|3||^BENIGN ESSENTIAL HYPERTENSION^^|||W|||||||||||||&#13;DG1|4||^ACUTE PULMONARY HEART DISEASE^^|||W|||||||||||||&#13;</response></HL7MessageBroker>";

    private final String NO_MESSAGES = "<HL7MessageBroker><DestinationIP/><DestinationPort/><response>MSH|^~&amp;|SimplyHIE|Yardi|YardiTest|YardiCommunity|20220627030222|3LedgqE6?Vu6HS'B7zvjmb%|ACK^Q11|20220627030222P|2.4|&amp;#13;MSA|CR|20220627030222|&amp;#13;</response></HL7MessageBroker>";
    @Value("${yardi.http.poll.endpoint.fetchMessage}")
    private String fetchMessageEndpoint;

    @Autowired
    @Qualifier("yardiHttpClient")
    private HttpClient yardiHttpClient;

    @Value("${yardi.localStorage.base}")
    private String yardiBaseLocalStorageDirName;

    @Value("${yardi.statusFolder.ok}")
    private String yardiStatusFolderOk;

    @BeforeEach
    public void cleanUpLocalStore() throws IOException {
        Files.walk(Path.of(yardiBaseLocalStorageDirName))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    void test1() throws IOException, InterruptedException {
        var response1 = mockResponse(MSG1);
        var response2 = mockResponse(MSG2);
        var noMessagesResponse = mockResponse(NO_MESSAGES);
        when(yardiHttpClient.send(fetchMessageHttpRequestMather(), (HttpResponse.BodyHandler<String>) any()))
                .thenReturn(response1)
                .thenReturn(response2)
                .thenReturn(noMessagesResponse);

        //give time for messages to be grabbed and processed
        for (int i = 0; i < 7; i++) {
            if (countFiles(yardiBaseLocalStorageDirName) < 4) {
                Thread.sleep(1000);
            }
        }

        assertThat(countFiles(yardiBaseLocalStorageDirName + "/" + yardiStatusFolderOk)).isEqualTo(4L);

        runValidations(
                "27460",
                HL7v2IntegrationPartner.YARDI_OID,
                "ISO",
                HL7v2IntegrationPartner.YARDI,
                "ADT^A08|408|P");

        runValidations(
                "27460",
                HL7v2IntegrationPartner.YARDI_OID,
                "ISO",
                HL7v2IntegrationPartner.YARDI,
                "ADT^A01|409|P");

        //todo check reports
    }

    private long countFiles(String dir) throws IOException {
        return Files.walk(Path.of(dir))
                .filter(Files::isRegularFile)
                .count();
    }


    private HttpRequest fetchMessageHttpRequestMather() {
        return argThat(httpRequest -> httpRequest.uri().equals(URI.create(fetchMessageEndpoint)));
    }

    private HttpResponse<String> mockResponse(String body) {
        var mockedResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockedResponse.body()).thenReturn(body);

        return mockedResponse;
    }
}
