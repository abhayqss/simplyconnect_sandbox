package com.scnsoft.eldermark.services.bluestone;

import org.apache.cxf.helpers.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.ws.security.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.Future;

/**
 * Created by pzhurba on 24-Nov-15.
 */
@Service
public class BlueStoneServiceImpl implements BlueStoneService {
    private static final Logger logger = LoggerFactory.getLogger(BlueStoneServiceImpl.class);

    @Value("${blue.stone.url}")
    private String blueStoneUrl;
    @Value("${blue.stone.username}")
    private String blueStoneUserName;
    @Value("${blue.stone.password}")
    private String blueStonePassword;

    DefaultHttpClient httpClient;


    @PostConstruct
    void init() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("SSL");

        // set up a TrustManager that trusts everything
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }}, new SecureRandom());

        final SSLSocketFactory sf = new SSLSocketFactory(sslContext) {
        };
        final Scheme httpsScheme = new Scheme("https", 443, sf);
        final SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(httpsScheme);

        httpClient = new DefaultHttpClient(new BasicClientConnectionManager(schemeRegistry));
    }

    @Async
    public Future<Boolean> sendBlueStoneNotification(String content) {
        logger.info("Sending message to bluestone : {}", content);
        HttpPost post = new HttpPost(blueStoneUrl);
        try {
            String encodeHash = Base64.encode((blueStoneUserName + ":" + blueStonePassword).getBytes("UTF-8"));
            post.addHeader("Authorization", "Basic " + encodeHash);

            final BasicHttpEntity httpEntity = new BasicHttpEntity();
            httpEntity.setContent(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
            httpEntity.setContentType(MediaType.APPLICATION_XML_VALUE);
            httpEntity.setContentEncoding("UTF-8");

            post.setEntity(httpEntity);
            logger.info("{}", post.toString());
            HttpResponse response = httpClient.execute(post);
            logger.info("Blue Stone Response : \n {} \n {}", response.toString(), IOUtils.toString(response.getEntity().getContent()));
            if (response.getStatusLine().getStatusCode() == 200) {
                post.releaseConnection();
                return new AsyncResult<Boolean>(true);
            }

        } catch (Exception e) {
            logger.error("Error communication with Blue stone bridge", e);
        } finally {
            post.releaseConnection();
        }
        return new AsyncResult<Boolean>(false);
    }
}
