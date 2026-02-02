package com.scnsoft.eldermark.services.opentok;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.opentok.OpenTok;
import com.opentok.Session;
import com.opentok.TokenOptions;
import com.opentok.exception.OpenTokException;
import com.scnsoft.eldermark.entity.phr.OpentokEntity;

@Service
public class OpentokServiceImpl implements OpentokService {

    @Value("${opentok.access.key}")
    private int apiKey;

    @Value("${opentok.secret.key}")
    private String apiSecret;

    @Value("${opentok.session.time}")
    private int sessionTime;

    private static final Logger logger = LoggerFactory.getLogger(OpentokService.class);

    @Override
    public OpentokEntity createSession() throws OpenTokException {
        OpenTok opentok = new OpenTok(apiKey, apiSecret);
        Session opentokSession = opentok.createSession();
        String videoCallSessionId = opentokSession.getSessionId();
        String videoCallToken = opentokSession.generateToken(
                new TokenOptions.Builder().expireTime((System.currentTimeMillis() / 1000L) + (sessionTime)).build());
        OpentokEntity opentokBuilderDto = new OpentokEntity();
        opentokBuilderDto.setSessionId(videoCallSessionId);
        opentokBuilderDto.setToken(videoCallToken);
        opentokBuilderDto.setApiKey(apiKey);
        logger.debug("Opentok session created with session id: {}", opentokBuilderDto.getSessionId());
        return opentokBuilderDto;
    }

}
