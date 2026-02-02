package com.scnsoft.eldermark.hl7v2.config.integrations.yardi.poll;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class YardiPollFileDataToMessageStringConverterTest {

    YardiPollFileDataToMessageStringConverter instance = new YardiPollFileDataToMessageStringConverter();

    @Test
    void testParsing() {
        var source = "<HL7MessageBroker><DestinationIP>dev.simplyconnect.me</DestinationIP><DestinationPort>3614</DestinationPort><response>MSH|^~\\&amp;|YardiTest|TestCommunity|SimplyHIE|Yardi|20220624131450|12345|ADT^A01|409|P|2.5||||||&#13;EVN|A01|20220624131450|20220624131450||||Yardi&#13;PID|1||27460||Resident^Demo^||20170604000000|M|||329 Silver Street Rd^^Malvern^PA^19876|^|||^|UTD|^||888888888||||||||||&#13;PV1|1|I^|Forest River^504     ^^TestCommunity||||1144265877&amp;BF4927476&amp;^RIEDINGER^JENNIFER^||||||||||&amp;&amp;^^^|||||||||||||||||||||||||||2017-06-01|&#13;AL1|1|DA|^Penicllin&#13;DG1|1||G31.83^Dementia with Lewy bodies^I10^|||W|||||||||||||&#13;DG1|2||^DIABETES MELLITUS^^|||W|||||||||||||&#13;DG1|3||^BENIGN ESSENTIAL HYPERTENSION^^|||W|||||||||||||&#13;DG1|4||^ACUTE PULMONARY HEART DISEASE^^|||W|||||||||||||&#13;</response></HL7MessageBroker>";
        var expected = "MSH|^~\\&|YardiTest|TestCommunity|SimplyHIE|Yardi|20220624131450|12345|ADT^A01|409|P|2.5||||||\r" +
                "EVN|A01|20220624131450|20220624131450||||Yardi\r" +
                "PID|1||27460||Resident^Demo^||20170604000000|M|||329 Silver Street Rd^^Malvern^PA^19876|^|||^|UTD|^||888888888||||||||||\r" +
                "PV1|1|I^|Forest River^504     ^^TestCommunity||||1144265877&BF4927476&^RIEDINGER^JENNIFER^||||||||||&&^^^|||||||||||||||||||||||||||2017-06-01|\r" +
                "AL1|1|DA|^Penicllin\r" +
                "DG1|1||G31.83^Dementia with Lewy bodies^I10^|||W|||||||||||||\r" +
                "DG1|2||^DIABETES MELLITUS^^|||W|||||||||||||\r" +
                "DG1|3||^BENIGN ESSENTIAL HYPERTENSION^^|||W|||||||||||||\r" +
                "DG1|4||^ACUTE PULMONARY HEART DISEASE^^|||W|||||||||||||\r";

        var actual = instance.convert(source);

        assertThat(actual).isEqualTo(expected);
    }

}
