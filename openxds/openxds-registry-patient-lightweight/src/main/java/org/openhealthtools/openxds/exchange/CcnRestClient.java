package org.openhealthtools.openxds.exchange;

import org.apache.axiom.om.util.Base64;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * Created by averazub on 12/13/2016.
 */
public class CcnRestClient {

    // SslConnectionFactory sslConnectionFactory;

    protected String exchangeHomeUrl;
    private String authUsername;
    private String authPassword;

    private final String EVENTS_URL_POSTFIX = "/events/adt";


    // HTTP POST request
    public void postAdt(Long residentId, String adtType, Long msgId, boolean newPatient) {
        String eventUrl = exchangeHomeUrl + EVENTS_URL_POSTFIX;
        String auth = Base64.encode((authUsername + ":" + authPassword).getBytes());
        new Thread(new PostEventRunner(eventUrl, residentId, adtType, msgId, newPatient, auth)).start();
    }

    public static class PostEventRunner implements Runnable {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String eventHandlerUrl;
        Long residentId;
        String adtType;
        private Long msgId;
        private boolean newPatient;
        private String basicAuth;


        PostEventRunner(String eventHandlerUrl, Long residentId, String adtType, Long msgId, boolean newPatient, String basicAuth) {
            this.eventHandlerUrl = eventHandlerUrl;
            this.residentId = residentId;
            this.adtType = adtType;
            this.msgId = msgId;
            this.newPatient = newPatient;
            this.basicAuth = basicAuth;
        }

        @Override
        public void run() {
            try {

                final URL obj = new URL(eventHandlerUrl);
/*
        HttpsURLConnection.setDefaultSSLSocketFactory(new SSLSocketFactoryImpl());
        HttpsURLConnection.setDefaultHostnameVerifier(sslConnectionFactory.getLocalhostResolvedHostnameVerifier());
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
*/
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                String requestBody = "";
                requestBody += "residentId=" + residentId;
                requestBody += "&adtType=" + adtType;
                requestBody += "&isNewPatient=" + newPatient;
                if (msgId != null) {
                    requestBody += "&msgId=" + msgId;
                }

                //add request header
                con.setRequestMethod("POST");

                con.setRequestProperty("Authorization", "Basic " + basicAuth);

                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(requestBody);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
                System.out.println("Response Code : " + responseCode);

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getExchangeHomeUrl() {
        return exchangeHomeUrl;
    }

    public void setExchangeHomeUrl(String exchangeHomeUrl) {
        this.exchangeHomeUrl = exchangeHomeUrl;
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public void setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }
}
