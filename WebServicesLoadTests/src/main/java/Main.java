import com.scnsoft.eldermark.ws.client.*;
import com.sun.xml.internal.ws.developer.JAXWSProperties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.MTOMFeature;
import java.io.*;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Main {
    static {
        //for localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier() {
                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        final List<String> allowedHosts = Arrays.asList("207.250.113.236", "dev.simplyhie.com", "dbahachou", "phomal");
                        return allowedHosts.contains(hostname);
                    }
                });
    }

    private static final int USER_ACTIVITY_FREQUENCY = 10000;
    private static final int PAUSE_BETWEEN_OPERATIONS = 3000;
    private static final double NUMBER_OF_USER_SESSIONS = 30;

    private static AtomicLong residentsSumTime = new AtomicLong(0L);
    private static AtomicLong queryForDocsSumTime = new AtomicLong(0L);
    private static AtomicLong downloadDoc1SumTime = new AtomicLong(0L);
    private static AtomicLong downloadDoc2SumTime = new AtomicLong(0L);
    private static AtomicLong uploadDocSumTime = new AtomicLong(0L);
    private static AtomicInteger numberOfExecutions = new AtomicInteger();

    private static final Object waitObject = new Object();

    public static void main(String[] args) throws Exception {
        disableCertificatesCheck();

        for (int i = 0; i < NUMBER_OF_USER_SESSIONS; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        long t1 = System.currentTimeMillis();
                        residentsDiscovery();
                        residentsSumTime.addAndGet(System.currentTimeMillis() - t1);

                        Thread.sleep(PAUSE_BETWEEN_OPERATIONS);

                        long t2 = System.currentTimeMillis();
                        queryForDocuments(181);
                        queryForDocsSumTime.addAndGet(System.currentTimeMillis() - t2);

                        Thread.sleep(PAUSE_BETWEEN_OPERATIONS);

                        long t3 = System.currentTimeMillis();
                        documentDownload(4, "D:\\Temp\\4.bin", true);
                        downloadDoc1SumTime.addAndGet(System.currentTimeMillis() - t3);

                        Thread.sleep(PAUSE_BETWEEN_OPERATIONS);

                        long t4 = System.currentTimeMillis();
                        documentDownload(5, "D:\\Temp\\5.bin", true);
                        downloadDoc2SumTime.addAndGet(System.currentTimeMillis() - t4);

                        Thread.sleep(PAUSE_BETWEEN_OPERATIONS);

                        long t5 = System.currentTimeMillis();
                        documentUpload("D:\\Doma\\SampleFiles\\foot-xray.jpg", "foot-xray.jpg", 181, true);
                        uploadDocSumTime.addAndGet(System.currentTimeMillis() - t5);

                        numberOfExecutions.incrementAndGet();
                        synchronized (waitObject) {
                            waitObject.notifyAll();
                        }
                    } catch (Exception e) {

                    }
                }
            }).start();

            Thread.sleep(USER_ACTIVITY_FREQUENCY);
        }

        while (numberOfExecutions.get() != NUMBER_OF_USER_SESSIONS) {
            synchronized (waitObject) {
                waitObject.wait();
            }
        }

        println("Residents discovery (avg) = " + (residentsSumTime.get() / NUMBER_OF_USER_SESSIONS) + " ms");
        println("Query for documents (avg) = " + (queryForDocsSumTime.get() / NUMBER_OF_USER_SESSIONS) + " ms");
        println("Download doc1 (avg) = " + (downloadDoc1SumTime.get() / NUMBER_OF_USER_SESSIONS) + " ms");
        println("Download doc2 (avg) = " + (downloadDoc2SumTime.get() / NUMBER_OF_USER_SESSIONS) + " ms");
        println("Upload doc (avg) = " + (uploadDocSumTime.get() / NUMBER_OF_USER_SESSIONS) + " ms");
    }

    private static void disableCertificatesCheck() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void residentsDiscovery() {
        println("Residents discovery: started");
        ResidentsEndpointImplService service = new ResidentsEndpointImplService();
        ResidentsEndpoint port = service.getResidentsEndpointImplPort();
        Map<String, Object> ctxt = ((BindingProvider) port).getRequestContext();
        Map<String, List<String>> headersMap = createCredentialsMap();
        ctxt.put(MessageContext.HTTP_REQUEST_HEADERS, headersMap);

        ResidentFilter filter = new ResidentFilter();
        filter.setFirstName("Maxine");
        filter.setLastName("Allburg");
        filter.setGender(Gender.FEMALE);

        List<Resident> residents = port.searchResidents(filter);
        for (Resident resident : residents) {
            println(resident.getId() + " " + resident.getFirstName() + " " + resident.getLastName() + " " +
                    resident.getGender() + " " + resident.getDateOfBirth() + " " + resident.getCity());
        }
        println("Residents discovery: finished");
    }

    private static void queryForDocuments(long residentId) throws Exception {
        println("Query for documents: started");
        DocumentsEndpointImplService service = new DocumentsEndpointImplService();
        DocumentsEndpoint port = service.getDocumentsEndpointImplPort();
        Map<String, Object> ctxt = ((BindingProvider) port).getRequestContext();
        Map<String, List<String>> headersMap = createCredentialsMap();
        ctxt.put(MessageContext.HTTP_REQUEST_HEADERS, headersMap);

        List<Document> documents = port.queryForDocuments(residentId);
        for (Document document : documents) {
            println(document.getId() + " " + document.getDocumentTitle() + " "
                    + document.getMimeType() + " " + document.getAuthorName());
        }
        println("Query for documents: finished");
    }

    private static void documentDelete(long documentId) throws Exception {
        println("Delete document: started");
        DocumentsEndpointImplService service = new DocumentsEndpointImplService();
        DocumentsEndpoint port = service.getDocumentsEndpointImplPort();

        Map<String, Object> ctxt = ((BindingProvider) port).getRequestContext();
        ctxt.put(JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, 8192);
//        ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8444/exchange/ws/documentsDownload");
        Map<String, List<String>> headersMap = createCredentialsMap();
        ctxt.put(MessageContext.HTTP_REQUEST_HEADERS, headersMap);

        port.deleteDocument(documentId);
        println("Delete document: finished");
    }

    private static void documentUpload(String pathToDocument, String fileName, long residentId, boolean useMtom)
            throws Exception {
        println("Document upload: started");
        DocumentsUploadEndpointImplService service = new DocumentsUploadEndpointImplService();

        MTOMFeature mtomFeature = new MTOMFeature();
        DocumentsUploadEndpoint port;
        if (useMtom) {
            port = service.getDocumentsUploadEndpointImplPort(mtomFeature);
        } else {
            port = service.getDocumentsUploadEndpointImplPort();
        }

        Map<String, Object> ctxt = ((BindingProvider) port).getRequestContext();
        ctxt.put(JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, 8192);
//        ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8444/exchange/ws/exchange");
        ctxt.put(MessageContext.HTTP_REQUEST_HEADERS, createCredentialsMap());

        //Upload test
        String mimeType;
        DataHandler dh;
        try {
            FileDataSource fileDataSource = new FileDataSource(pathToDocument);
            dh = new DataHandler(fileDataSource);
            mimeType = Files.probeContentType(fileDataSource.getFile().toPath());
        } catch (IOException e) {
            throw e;
        }


        port.uploadDocument(residentId, fileName, mimeType, dh);
        println("Document upload: finished");
    }

    private static void documentDownload(long documentId, String filePath, boolean useMtom) throws Exception {
        println("Document download: started");
        DocumentsDownloadEndpointImplService service = new DocumentsDownloadEndpointImplService();

        MTOMFeature mtomFeature = new MTOMFeature();
        DocumentsDownloadEndpoint port;
        if (useMtom) {
            port = service.getDocumentsDownloadEndpointImplPort(mtomFeature);
        } else {
            port = service.getDocumentsDownloadEndpointImplPort();
        }

        Map<String, Object> ctxt = ((BindingProvider) port).getRequestContext();
        ctxt.put(JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, 8192);
//        ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8444/exchange/ws/documentsDownload");
        Map<String, List<String>> headersMap = createCredentialsMap();
        ctxt.put(MessageContext.HTTP_REQUEST_HEADERS, headersMap);

        try {
            DataHandler dh = port.downloadDocument(documentId);
            InputStream input = dh.getInputStream();
            OutputStream output = new FileOutputStream(new File(filePath));
            try {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } finally {
                input.close();
                if (output != null) {
                    output.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        println("Document download: finished");
    }

    private static Map<String, List<String>> createCredentialsMap() {
        Map<String, List<String>> credentialsMap = new HashMap<String, List<String>>();
        credentialsMap.put("Username", Collections.singletonList("dbahachou"));
        credentialsMap.put("Password", Collections.singletonList("1"));
        credentialsMap.put("Organization", Collections.singletonList("copy1Test1"));
        return credentialsMap;
    }

    private static void println(String message) {
        synchronized (System.out) {
            System.out.println(Thread.currentThread().getName() + ": " + message);
        }
    }

}
