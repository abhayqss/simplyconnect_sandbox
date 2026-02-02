package com.scnsoft.eldermark.hl7v2.config.integrations.yardi.poll;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class YardiHttpPollResponseAnalyzerTest {

    private final String MSG = StringEscapeUtils.escapeXml11("MSH|^~\\&|YARDI|K301|YARDI|159|20160105100233||ADT^A01|10000|P|2.5\r" +
            "EVN|A01|20160105100233|||||159\r" +
            "PID|1|5497|RN123||NGO^ROGER^H||19450401|M|||430 S FAIRVIEW AVE^APT 2^GOLETA^CA^93117||8051118888|||||||611337777|\r" +
            "PV1|1|I|159||||1234567890^Smith^Jack||||||||||||||||||||||||||||||||||||||20160102|20170102|\r" +
            "AL1|1||^Peanuts|\r" +
            "AL1|2||^Shellfish|\r");

    private final String NO_MESSAGES = "MSH|^~\\&amp;|Yardi|159|Pharm|051|20220531010620|dHMdxB7Oervu8aUfNosAW9l8aPg=|ACK^Q11|3|P|2.4||||||ASCII|||\rMSA|CR|20220531010620|";

    private final String OTHER_ERRORS = "MSH|^~\\&amp;|Yardi|159|Pharm|051|20220531010620|dHMdxB7Oervu8aUfNosAW9l8aPg=|ACK^Q11|3|P|2.4||||||ASCII|||\rMSA|CE|20220531010620|";

    YardiHttpPollResponseAnalyzer instance = new YardiHttpPollResponseAnalyzer();

    @Test
    void isNoFurtherMessagesResponse_nullResponse_returnsTrue() {
        assertThat(instance.isNoFurtherMessagesResponse(null)).isTrue();
    }

    @Test
    void isNoFurtherMessagesResponse_nullBody_returnsTrue() {
        var response = mockResponse(null);

        assertThat(instance.isNoFurtherMessagesResponse(null)).isTrue();
    }


    @Test
    void isNoFurtherMessagesResponse_noMessages_returnsTrue() {
        var response = mockResponse(NO_MESSAGES);

        assertThat(instance.isNoFurtherMessagesResponse(response)).isTrue();
    }

    @Test
    void isNoFurtherMessagesResponse_yardiError_returnsTrue() {
        var response = mockResponse(OTHER_ERRORS);

        assertThat(instance.isNoFurtherMessagesResponse(response)).isTrue();
    }

    @Test
    void isNoFurtherMessagesResponse_hasMessage_returnsFalse() {
        var response = mockResponse(MSG);

        assertThat(instance.isNoFurtherMessagesResponse(response)).isFalse();
    }


    private HttpResponse<String> mockResponse(String body) {
        var mockedResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockedResponse.body()).thenReturn(body);

        return mockedResponse;
    }
}