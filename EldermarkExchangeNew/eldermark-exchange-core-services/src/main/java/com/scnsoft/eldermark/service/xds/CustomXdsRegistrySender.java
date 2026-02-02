package com.scnsoft.eldermark.service.xds;


import com.scnsoft.eldermark.service.xds.ssl.SslConnectionFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CustomXdsRegistrySender {

    private static final Logger logger = LoggerFactory.getLogger(CustomXdsRegistrySender.class);

    private final SslConnectionFactory sslConnectionFactory;

    private final String xdsRegistryCustomUrl;
    private final String xdsCustomUserBasic;

    public CustomXdsRegistrySender(String xdsRegistryCustomUrl, String xdsUser, String xdsUserPassword) {
        this.xdsRegistryCustomUrl = xdsRegistryCustomUrl;
        this.xdsCustomUserBasic = "Basic " + new String(Base64.getEncoder().encode((xdsUser + ":" + xdsUserPassword).getBytes()));

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

    @SuppressFBWarnings(value = "URLCONNECTION_SSRF_FD", justification = "Connection is made to restricted set of urls")
    private HttpURLConnection createGetConnection(String url) throws IOException {
        var connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection).setSSLSocketFactory(sslConnectionFactory.getSSLSocketFactory());
        }

        return connection;
    }

    private void authenticate(HttpURLConnection connection) {
        connection.setRequestProperty("Authorization", xdsCustomUserBasic);
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        return IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
    }
}
