package com.scnsoft.eldermark.service.twilio.media;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.http.HttpMethod;
import com.twilio.http.NetworkHttpClient;
import com.twilio.http.Request;
import com.twilio.http.Response;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This is twilio http client which acts as default NetworkHttpClient in all cases except for processing custom
 * MultipartRequest. We need to send multipart/form-data POST to upload media file, whereas NetworkHttpClient sends
 * x-www-form-urlencoded POST requests
 */
public class CustomTwilioHttpClient extends NetworkHttpClient {

    private final org.apache.http.client.HttpClient superClient;

    public CustomTwilioHttpClient() throws NoSuchAlgorithmException, KeyManagementException, IllegalAccessException {
        this(DEFAULT_REQUEST_CONFIG);
    }

    /**
     * Create a new HTTP Client with a custom request config.
     *
     * @param requestConfig a RequestConfig.
     */
    public CustomTwilioHttpClient(final RequestConfig requestConfig) throws NoSuchAlgorithmException, KeyManagementException, IllegalAccessException {
        this(requestConfig, DEFAULT_SOCKET_CONFIG);
    }

    public CustomTwilioHttpClient(final RequestConfig requestConfig, final SocketConfig socketConfig) throws IllegalAccessException, NoSuchAlgorithmException, KeyManagementException {
        super(requestConfig, socketConfig);
        //copypaste from NetworkHttpClient exept that TLS1.2 only allowed. There are handshake issues with TLS1.3 and twilio during media fetch
        Collection<BasicHeader> headers = Arrays.asList(
                new BasicHeader("X-Twilio-Client", "java-" + Twilio.VERSION),
                new BasicHeader(HttpHeaders.USER_AGENT, "twilio-java/" + Twilio.VERSION + " (" + Twilio.JAVA_VERSION + ")"),
                new BasicHeader(HttpHeaders.ACCEPT, "application/json"),
                new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "utf-8")
        );

        String googleAppEngineVersion = System.getProperty("com.google.appengine.runtime.version");
        boolean isGoogleAppEngine = googleAppEngineVersion != null && !googleAppEngineVersion.isEmpty();

        org.apache.http.impl.client.HttpClientBuilder clientBuilder = HttpClientBuilder.create();

        if (!isGoogleAppEngine) {
            clientBuilder.useSystemProperties();
        }


        var sslContext = SSLContextBuilder.create()
                .setProtocol("TLSv1.2")
                .build();

        var sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", sslConnectionSocketFactory) //here we registered custom factory
                        .build()
        );
        connectionManager.setDefaultSocketConfig(socketConfig);
        connectionManager.setDefaultMaxPerRoute(10);
        connectionManager.setMaxTotal(10 * 2);

        superClient = clientBuilder
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setDefaultHeaders(headers)
                .setRedirectStrategy(this.getRedirectStrategy())
                .build();

        FieldUtils.writeField(this, "client", superClient, true);
    }

    @Override
    public Response makeRequest(Request request) {
         if (request instanceof MultipartRequest) {
            return makeMultipartRequest((MultipartRequest) request);
        } else {
            return super.makeRequest(request);
        }
    }

    private Response makeMultipartRequest(MultipartRequest request) {
        HttpMethod method = request.getMethod();

        if (method != HttpMethod.POST) {
            throw new ApiException("Multipart request can only be POST");
        }

        RequestBuilder builder = RequestBuilder.create(method.toString())
                .setUri(request.constructURL().toString())
                .setVersion(HttpVersion.HTTP_1_1)
                .setCharset(StandardCharsets.UTF_8);

        if (request.requiresAuthentication()) {
            builder.addHeader(HttpHeaders.AUTHORIZATION, request.getAuthString());
        }

        for (Map.Entry<String, List<String>> entry : request.getHeaderParams().entrySet()) {
            for (String value : entry.getValue()) {
                builder.addHeader(entry.getKey(), value);
            }
        }

        builder.setEntity(request.getHttpEntity());

        HttpResponse response = null;

        try {
            response = superClient.execute(builder.build());
            HttpEntity entity = response.getEntity();
            return new Response(
                    // Consume the entire HTTP response before returning the stream
                    entity == null ? null : new BufferedHttpEntity(entity).getContent(),
                    response.getStatusLine().getStatusCode(),
                    response.getAllHeaders()
            );
        } catch (IOException e) {
            throw new ApiException(e.getMessage(), e);
        } finally {

            // Ensure this response is properly closed
            HttpClientUtils.closeQuietly(response);

        }
    }
}
