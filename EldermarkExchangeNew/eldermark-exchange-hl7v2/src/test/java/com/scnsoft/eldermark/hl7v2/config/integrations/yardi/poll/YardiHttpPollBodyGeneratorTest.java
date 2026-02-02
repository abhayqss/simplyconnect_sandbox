package com.scnsoft.eldermark.hl7v2.config.integrations.yardi.poll;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class YardiHttpPollBodyGeneratorTest {

    @Test
    void generateBody() {
        var timestamp = LocalDateTime.of(2022, 6, 27, 14, 24, 52, 123456789);

        var clock = Clock.fixed(timestamp.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        var instance = new YardiHttpPollBodyGenerator(
                "qwerty123456789",
                "SimplyHIE",
                "Yardi",
                "YardiTest",
                "YardiCommunity",
                clock
        );

        var body = instance.generateBody();

        assertThat(body.getContentType()).isEqualTo("text/xml");
        assertThat(body.getIdentifier()).isEqualTo("20220627_142452_123456789");
        assertThat(body.getContent()).isEqualTo("<HL7MessageBroker>" +
                "<request>MSH|^~\\&amp;|SimplyHIE|Yardi|YardiTest|YardiCommunity|20220627142452.1234|qwerty123456789|QBP^Q11|20220627142452.1234|P|2.4|&#13;QPD|Check Mailbox|Q-CM1||&#13;</request>" +
                "</HL7MessageBroker>");
    }
}
