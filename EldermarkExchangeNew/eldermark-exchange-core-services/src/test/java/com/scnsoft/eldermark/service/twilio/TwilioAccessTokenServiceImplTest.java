package com.scnsoft.eldermark.service.twilio;

import com.twilio.jwt.accesstoken.VideoGrant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TwilioAccessTokenServiceImplTest {

    @InjectMocks
    TwilioAccessTokenServiceImpl instance;


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(instance, "twilioAccountSid", "AC86821c954d0e0043925955841ff35bc8");
        ReflectionTestUtils.setField(instance, "twilioApiKey", "SK40ae8d49691e85236cf87c2f381be363");
        ReflectionTestUtils.setField(instance, "twilioApiSecret", "dTxEFssWSEo3OPueyE5JVFiu5Cead4CH");
        ReflectionTestUtils.setField(instance, "jwtExpirationInMs", 1800000);
        ReflectionTestUtils.setField(instance, "maxParticipantDuration", 14400000);
    }

    @Test
    void generateVideoToken_canBeParsedBack() {
        var identity = "e1234";
        var roomSid = "RM12345678976";

        var generatedToken = instance.generateVideoToken(identity, roomSid);
        var parsedToken = instance.parse(generatedToken);

        Assertions.assertThat(parsedToken.getIdentity()).isEqualTo(identity);
        Assertions.assertThat(parsedToken.getVideoGrant().map(VideoGrant::getRoom)).contains(roomSid);
    }
}