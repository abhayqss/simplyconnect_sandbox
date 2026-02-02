package com.scnsoft.eldermark.hl7v2;


import com.scnsoft.eldermark.hl7v2.dao.HL7MessageLogDao;
import com.scnsoft.eldermark.hl7v2.processor.MessageProcessingResult;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

/**
 * Copied from OpenXds and refactored
 */
@Service
public class CcnRestClientImpl implements CcnRestClient {
    private static final Logger logger = LoggerFactory.getLogger(CcnRestClientImpl.class);

    // SslConnectionFactory sslConnectionFactory;

    @Autowired
    private HL7MessageLogDao hl7MessageLogDao;

    private final String EVENTS_URL_POSTFIX = "/events/adt";

    private final String eventUrl;
    private final String auth;

    public CcnRestClientImpl(@Value("${openxds-api.home.url}") String openXdsApiHomeUrl,
                             @Value("${xds.custom.auth.username}") String authUsername,
                             @Value("${xds.custom.auth.password}") String authPassword) {
        this.eventUrl = openXdsApiHomeUrl + EVENTS_URL_POSTFIX;
        this.auth = Base64.getEncoder().encodeToString((authUsername + ":" + authPassword).getBytes());
    }

    // HTTP POST request
    @Override
    @Async //todo should send synchronously instead?
    public void postAdt(MessageProcessingResult messageProcessingResult, Long hl7MessageLogId) {
        try {
            logger.info("Sent processing results to openxds-api, log id {}", hl7MessageLogId);

            final URL obj = new URL(eventUrl);
/*
        HttpsURLConnection.setDefaultSSLSocketFactory(new SSLSocketFactoryImpl());
        HttpsURLConnection.setDefaultHostnameVerifier(sslConnectionFactory.getLocalhostResolvedHostnameVerifier());
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
*/
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            String requestBody = "";
            requestBody += "residentId=" + messageProcessingResult.getClientId();
            requestBody += "&adtType=" + messageProcessingResult.getAdtType();
            requestBody += "&isNewPatient=" + messageProcessingResult.isClientNew();
            if (messageProcessingResult.getParsedAdtMessageId() != null) {
                requestBody += "&msgId=" + messageProcessingResult.getParsedAdtMessageId();
            }

            //add request header
            con.setRequestMethod("POST");

            con.setRequestProperty("Authorization", "Basic " + auth);

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(requestBody);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            logger.info("Response Code: {}", responseCode);

            BufferedReader in;
            if (responseCode == 200) {
                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
            } else {
                in = new BufferedReader(
                        new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            final StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if (responseCode != 200) {
                throw new RuntimeException(response.toString());
            }
            logger.info("Sent processing results to openxds-api, log id {}", hl7MessageLogId);
            hl7MessageLogDao.openXdsApiSuccess(hl7MessageLogId);
        } catch (Exception e) {
            logger.error("Error during sending adt message to openxds-api, log id {}", hl7MessageLogId, e);
            try {
                hl7MessageLogDao.openXdsApiFail(hl7MessageLogId, ExceptionUtils.getStackTrace(e));
            } catch (Exception ee) {
                logger.error("Failed to update hl7 message log {} with openXds fail", hl7MessageLogId);
            }
        }
    }
}
