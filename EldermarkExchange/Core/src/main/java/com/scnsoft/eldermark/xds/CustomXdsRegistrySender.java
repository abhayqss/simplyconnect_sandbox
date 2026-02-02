package com.scnsoft.eldermark.xds;

import com.scnsoft.eldermark.xds.ssl.SslConnectionFactory;
import org.apache.axis.encoding.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CustomXdsRegistrySender {

    private static final Logger logger = LoggerFactory.getLogger(CustomXdsRegistrySender.class);

    private final SslConnectionFactory sslConnectionFactory;

    private final String xdsRegistryCustomUrl;
    private final String xdsUserBasic;

    public CustomXdsRegistrySender(String xdsRegistryCustomUrl, String xdsUser, String xdsUserPassword) {
        this.xdsRegistryCustomUrl = xdsRegistryCustomUrl;
        this.xdsUserBasic = "Basic " + Base64.encode((xdsUser + ":" + xdsUserPassword).getBytes());

        this.sslConnectionFactory = new SslConnectionFactory();
    }

    public String getDocumentData(String uuid) throws IOException {
        String endpoint = "/documentData?uuid=" + uuid;
        return sendGet(endpoint);
    }

    public String updateTitle(String uuid, String title) throws IOException {
        String endpoint = "/updateDocEntryTitle?uuid=" + uuid + "&docTitle=" + URLEncoder.encode(title, "UTF-8");
        return sendGet(endpoint);
    }

    private String sendGet(String endpoint) throws IOException {
        String url = xdsRegistryCustomUrl + endpoint;
        logger.info("Sending GET {}", url);

        HttpURLConnection connection = createGetConnection(url);
        authenticate(connection);

        String response = readResponse(connection);
        return response;
    }

    private HttpURLConnection createGetConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection).setSSLSocketFactory(sslConnectionFactory.getSSLSocketFactory());
        }

        return connection;
    }

    private void authenticate(HttpURLConnection connection) {
        connection.setRequestProperty("Authorization", xdsUserBasic);
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
