package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dto.TlsConnectivityCheckResult;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.KeyStoreUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Service
public class TlsConnectivityCheckerImpl implements TlsConnectivityChecker {
    private static final Logger logger = LoggerFactory.getLogger(TlsConnectivityCheckerImpl.class);

    @Override
    public TlsConnectivityCheckResult checkTls(String urlPath) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SavingTrustManager trustManager = null;
        var result = new TlsConnectivityCheckResult();
        try {
            URL url = new URL(urlPath);

            var connection = prepareConnection(url);
            var pair = createSSLContextWithCertSaver();
            trustManager = pair.getSecond();

            connection.setSSLSocketFactory(pair.getFirst().getSocketFactory());

            result.setResponseCode(executeRequest(connection));
            result.setSuccess(true);

        } catch (SSLHandshakeException ex) {
            logger.info("SSL handshake failed: {}", ex.getMessage());
            result.setSuccess(false);
        } finally {
            if (trustManager != null) {
                if (ArrayUtils.isNotEmpty(trustManager.chain)) {
                    result.setCertificate(trustManager.chain[0]);
                }
            }
        }
        return result;
    }

    @Override
    public TlsConnectivityCheckResult checkTls(String urlPath, Certificate serverCertificate) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        var result = new TlsConnectivityCheckResult();
        try {
            URL url = new URL(urlPath);

            var connection = prepareConnection(url);

            connection.setSSLSocketFactory(createSSLContext(serverCertificate).getSocketFactory());
            connection.setHostnameVerifier((s, sslSession) -> s.equals(url.getHost()));

            result.setResponseCode(executeRequest(connection));
            result.setSuccess(true);

        } catch (SSLHandshakeException ex) {
            logger.info("SSL handshake failed: {}", ex.getMessage());
        }
        return result;
    }

    private Pair<SSLContext, SavingTrustManager> createSSLContextWithCertSaver() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        var tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init((KeyStore) null);
        var trustManagers = tmf.getTrustManagers();

        var savingTrustManager = new SavingTrustManager((X509TrustManager) trustManagers[0]);
        var tm = new TrustManager[]{savingTrustManager};

        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        ctx.init(null, tm, null);

        return new Pair<>(ctx, savingTrustManager);
    }

    private SSLContext createSSLContext(Certificate serverCertificate) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
        var password = "tmp";
        var trustStore = KeyStoreUtil.createKeyStore("JKS", password);
        KeyStoreUtil.addCertificate(trustStore, "alias", serverCertificate);

        var tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        var trustManagers = tmf.getTrustManagers();

        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        ctx.init(null, trustManagers, null);

        return ctx;
    }

    @SuppressFBWarnings(
        value = "URLCONNECTION_SSRF_FD",
        justification = "URL is always use HTTP protocol. It is not possible to access local files using it"
    )
    private HttpsURLConnection prepareConnection(URL url) throws IOException {
        var con = (HttpsURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        return con;
    }

    private int executeRequest(HttpsURLConnection connection) throws IOException {
        var response = connection.getResponseCode();
        logger.info("Response code from {} while validating certificate: {}", connection.getURL().toExternalForm(), response);
        return response;
    }

    private static class SavingTrustManager implements X509TrustManager {
        private final X509TrustManager delegate;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager delegate) {
            this.delegate = delegate;
        }

        public X509Certificate[] getAcceptedIssuers() {
            return delegate.getAcceptedIssuers();
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            delegate.checkClientTrusted(chain, authType);
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            this.chain = chain;
            delegate.checkServerTrusted(chain, authType);
        }
    }
}
