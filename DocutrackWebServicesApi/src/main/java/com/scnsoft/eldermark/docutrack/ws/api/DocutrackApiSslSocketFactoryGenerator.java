package com.scnsoft.eldermark.docutrack.ws.api;

import javax.net.ssl.SSLSocketFactory;
import java.io.InputStream;


public interface DocutrackApiSslSocketFactoryGenerator {

    SSLSocketFactory getSSLSocketFactory(InputStream trustStore, String trustStorePass, String trustStoreType);

}
