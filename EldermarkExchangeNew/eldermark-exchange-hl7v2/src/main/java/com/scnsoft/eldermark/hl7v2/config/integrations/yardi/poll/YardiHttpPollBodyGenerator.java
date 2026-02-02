package com.scnsoft.eldermark.hl7v2.config.integrations.yardi.poll;

import com.scnsoft.eldermark.hl7v2.poll.http.HttpPollBody;
import com.scnsoft.eldermark.hl7v2.poll.http.HttpPollBodyGenerator;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class YardiHttpPollBodyGenerator implements HttpPollBodyGenerator {
    private final String msh8Password;
    private final Clock clock;

    private final DateTimeFormatter timestampHL7Pattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSSS");
    private final DateTimeFormatter identifierPattern = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSSSSSSSS");

    private final String sendingApplication;
    private final String sendingFacility;
    private final String receivingApplication;
    private final String receivingFacility;

    public YardiHttpPollBodyGenerator(String msh8Password,
                                      String sendingApplication,
                                      String sendingFacility,
                                      String receivingApplication,
                                      String receivingFacility,
                                      Clock clock) {
        this.msh8Password = msh8Password;
        this.clock = clock;
        this.sendingApplication = sendingApplication;
        this.sendingFacility = sendingFacility;
        this.receivingApplication = receivingApplication;
        this.receivingFacility = receivingFacility;
    }

    @Override
    public HttpPollBody generateBody() {
        var now = LocalDateTime.now(clock);
        var timestamp = timestampHL7Pattern.format(LocalDateTime.now(clock));
        var content = "<HL7MessageBroker>" +
                "<request>MSH|^~\\&amp;|" +
                sendingApplication + "|" +
                sendingFacility + "|" +
                receivingApplication + "|" +
                receivingFacility + "|" + timestamp + "|" + msh8Password + "|QBP^Q11|" +
                timestamp + "|P|2.4|&#13;QPD|Check Mailbox|Q-CM1||&#13;</request>" +
                "</HL7MessageBroker>";

        return new HttpPollBody(
                identifierPattern.format(now),
                content,
                "text/xml"
        );
    }
}
