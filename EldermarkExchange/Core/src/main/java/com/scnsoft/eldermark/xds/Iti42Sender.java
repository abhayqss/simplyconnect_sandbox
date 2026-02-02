package com.scnsoft.eldermark.xds;

import com.scnsoft.eldermark.shared.exceptions.NHINIoException;
import com.scnsoft.eldermark.xds.ssl.SslConnectionFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.regex.Matcher;

/**
 * Created by averazub on 8/9/2016.
 */
public class Iti42Sender {

    private static final Logger logger = LoggerFactory.getLogger(Iti42Sender.class);

    protected String submitDocumentMessageTemplate;
    protected String approveDocumentMessageTemplate;
    protected String deprecateDocumentMessageTemplate;
    protected String repositoryUniqueId;
    protected String assigningAuthorityId;
    protected String registryUrl;
    protected SslConnectionFactory sslConnectionFactory;

    static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

    static {
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static final String PLH_CONTENTTYPE_CODE = "\\$contentTypeCode";
    public static final String PLH_CONTENTTYPE_CODE_LOCALIZED = "\\$contentTypeCodeLocalized";
    public static final String PLH_CONFIDENTIALITY_CODE = "\\$confidentialityCode";
    public static final String PLH_CONFIDENTIALITY_CODE_LOCALIZED = "\\$confidentialityCodeLocalized";
    public static final String PLH_CREATION_TIME = "\\$creationTime";
    public static final String PLH_DOC_UUID = "\\$docUUID";
    public static final String PLH_FORMAT_CODE = "\\$formatCode";
    public static final String PLH_FORMAT_CODE_LOCALIZED = "\\$formatCodeLocalized";
    public static final String PLH_HLTHCARE_FACILITY_TYPE_CODE = "\\$healthcareFacilityTypeCode";
    public static final String PLH_LANGUAGE_CODE = "\\$languageCode";
    public static final String PLH_MIME_TYPE = "\\$mimeType";
    public static final String PLH_PATIENT_ID = "\\$patientId";
    public static final String PLH_REPOSITORY_UNIQUE_ID = "\\$repositoryUniqueId";
    public static final String PLH_SOURCE_PATIENT_ID = "\\$sourcePatientId";
    public static final String PLH_DOC_TITLE = "\\$docTitle";

    public static final String PLH_DOC_ENTRY_UNIQUE_ID = "\\$XDSDocumentEntry.uniqueId";
    public static final String PLH_SUBM_TIME = "\\$submissionTime";
    public static final String PLH_SUBM_SET_ENTRY_SOURCE_ID = "\\$XDSSubmissionSet.sourceId";
    public static final String PLH_PRACTICE_SETTING_CODE = "\\$practiceSettingCode";
    public static final String PLH_SUBM_SET_ENTRY_UNIQUE_ID = "\\$XDSSubmissionSet.uniqueId";
    public static final String PLH_HASH = "\\$hash";
    public static final String PLH_SIZE = "\\$size";


    public static final String PLH_REGISTRY_ADDRESS = "\\$RegistryURL";
    public static final String PLH_MESSAGE_ID = "\\$MessageId";


    public static final String MESSAGE_ID_UUID = "urn:uuid:966B3495C1ADB6DD841470925451149";


    public Iti42Sender(String repositoryUniqueId, String assigningAuthorityId, String registryUrl, SslConnectionFactory sslConnectionFactory) {
        this.registryUrl = registryUrl;
        this.repositoryUniqueId = repositoryUniqueId;
        this.assigningAuthorityId = assigningAuthorityId;
        this.sslConnectionFactory = sslConnectionFactory;
        try {
            InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("xds_submit_document_set.xml");
            submitDocumentMessageTemplate = getStringFromInputStream(fis);
            fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("xds_approve_document.xml");
            approveDocumentMessageTemplate = getStringFromInputStream(fis);
            fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("xds_deprecate_document.xml");
            deprecateDocumentMessageTemplate = getStringFromInputStream(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean sendDocumentMetadata(Iti42DocumentData data) throws IOException {
        String sumbSetSourceId = assigningAuthorityId + ".2." + System.currentTimeMillis();

        String message = submitDocumentMessageTemplate
                .replaceAll(PLH_CONTENTTYPE_CODE_LOCALIZED, Matcher.quoteReplacement(data.getContentTypeCodeLocalized()))
                .replaceAll(PLH_CONTENTTYPE_CODE, Matcher.quoteReplacement(data.getContentTypeCode()))
                .replaceAll(PLH_CONFIDENTIALITY_CODE_LOCALIZED, Matcher.quoteReplacement(data.getConfidentiallyCodeLocalized()))
                .replaceAll(PLH_CONFIDENTIALITY_CODE, Matcher.quoteReplacement(data.getConfidentiallyCode()))
                .replaceAll(PLH_CREATION_TIME, data.getCreationTime() == null ? null : df.format(data.getCreationTime()))
                .replaceAll(PLH_DOC_UUID, Matcher.quoteReplacement(data.getDocumentUUID()))
                .replaceAll(PLH_FORMAT_CODE_LOCALIZED, Matcher.quoteReplacement(data.getFormatCodeLocalized()))
                .replaceAll(PLH_FORMAT_CODE, Matcher.quoteReplacement(data.getFormatCode()))
                .replaceAll(PLH_HLTHCARE_FACILITY_TYPE_CODE, Matcher.quoteReplacement(data.getHealthcareFacilityTypeCode()))
                .replaceAll(PLH_LANGUAGE_CODE, Matcher.quoteReplacement(data.getLanguageCode()))
                .replaceAll(PLH_MIME_TYPE, Matcher.quoteReplacement(data.getMimeType()))
                .replaceAll(PLH_PATIENT_ID, Matcher.quoteReplacement(data.getPatientId()))
                .replaceAll(PLH_REPOSITORY_UNIQUE_ID, Matcher.quoteReplacement(repositoryUniqueId))
                .replaceAll(PLH_SOURCE_PATIENT_ID, Matcher.quoteReplacement(data.getSourcePatientId()))
                .replaceAll(PLH_DOC_TITLE, Matcher.quoteReplacement(data.getDocumentTitle()))
                .replaceAll(PLH_DOC_ENTRY_UNIQUE_ID, Matcher.quoteReplacement(data.getUniqueId()))
                .replaceAll(PLH_SUBM_TIME, Matcher.quoteReplacement(df.format(new Date())))
                .replaceAll(PLH_SUBM_SET_ENTRY_SOURCE_ID, Matcher.quoteReplacement(sumbSetSourceId))
                .replaceAll(PLH_SUBM_SET_ENTRY_UNIQUE_ID, Matcher.quoteReplacement(sumbSetSourceId))
                .replaceAll(PLH_PRACTICE_SETTING_CODE, Matcher.quoteReplacement(data.getPracticeSettingCode()))

                .replaceAll(PLH_REGISTRY_ADDRESS, Matcher.quoteReplacement(registryUrl))
                .replaceAll(PLH_MESSAGE_ID, Matcher.quoteReplacement(MESSAGE_ID_UUID))

                .replaceAll(PLH_HASH, data.getHash() != null ? data.getHash() : "")
                .replaceAll(PLH_SIZE, data.getSize() != null ? data.getSize().toString() : "");
        postMessage(message);
        return true;
    }

    public boolean deprecateDocumentEntry(String documentUUID) throws IOException {


        String message = deprecateDocumentMessageTemplate
                .replaceAll(PLH_DOC_UUID, "urn:uuid:" + documentUUID)
                .replaceAll(PLH_REGISTRY_ADDRESS, registryUrl)
                .replaceAll(PLH_MESSAGE_ID, MESSAGE_ID_UUID);
        postMessage(message);
        return true;
    }


    public boolean approveDocumentEntry(String documentUUID) throws IOException {
        String message = approveDocumentMessageTemplate
                .replaceAll(PLH_DOC_UUID, "urn:uuid:" + documentUUID)
                .replaceAll(PLH_REGISTRY_ADDRESS, registryUrl)
                .replaceAll(PLH_MESSAGE_ID, MESSAGE_ID_UUID);
        postMessage(message);
        return true;
    }


    protected static String getStringFromInputStream(InputStream in) throws java.io.IOException {
        int count;
        byte[] by = new byte[256];

        StringBuffer buf = new StringBuffer();
        while ((count = in.read(by)) > 0) {
            for (int i = 0; i < count; i++) {
                by[i] &= 0x7f;
            }
            buf.append(new String(by, 0, count));
        }
        return new String(buf);
    }

    // HTTP POST request
    protected void postMessage(String soapMessage) throws IOException {
        URL obj = new URL(registryUrl);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setSSLSocketFactory(sslConnectionFactory.getSSLSocketFactory());
        con.setHostnameVerifier(sslConnectionFactory.getLocalhostResolvedHostnameVerifier());

        //add reuqest header
        con.setRequestMethod("POST");

        con.setRequestProperty("Content-Type", "application/xop+xml; charset=UTF-8; type=\"application/soap+xml\"");
        con.setRequestProperty("SOAPAction", "urn:ihe:iti:2007:RegisterDocumentSet-b");
        con.setRequestProperty("User-Agent", "Axis2");
        con.setRequestProperty("Content-Transfer-Encoding", "Axis2");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(soapMessage);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        logger.info("Sending 'POST' request to URL : {}", registryUrl);
        logger.info("Response Code : {}", responseCode);

        BufferedReader in;
        if (responseCode == 200) {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            logger.debug("INPUT STREAM");
        } else {
            in = new BufferedReader(
                    new InputStreamReader(con.getErrorStream()));
            logger.debug("ERROR STREAM");
        }
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        checkXDSResponse(response.toString());

        //print result


    }

    protected void checkXDSResponse(String responseStr) throws NHINIoException {

        OMElement respOM = xmlStringToOM(responseStr);
        Iterator<OMElement> respChildren = respOM.getChildren();
        OMElement registryResponse = null;
        while ((respChildren.hasNext()) && (registryResponse == null)) {
            OMElement next = respChildren.next();
            if ("Body".equals(next.getLocalName())) {
                registryResponse = next.getFirstElement();
            }
        }
        if (registryResponse == null) throw new NHINIoException("Something goes wrong");
        OMAttribute status = registryResponse.getAttribute(new QName("status"));
        Boolean statusOk = (status != null) && "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success".equals(status.getAttributeValue());
        if (!statusOk) {
            OMElement registryError = ((OMElement) registryResponse.getChildrenWithName(new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", "RegistryErrorList")).next()).getFirstElement();
            String codeContext = registryError.getAttribute(new QName("codeContext")).getAttributeValue();
            throw new NHINIoException(codeContext);
        }

    }

    protected static OMElement xmlStringToOM(String inputString) throws NHINIoException {

        //Extract multipart if any
        if (inputString.contains("--MIMEBoundary")) {
            inputString = inputString.substring(inputString.indexOf("<?xml"));
            inputString = inputString.substring(0, inputString.indexOf("--MIMEBoundary"));
        }

        byte[] ba = inputString.getBytes();
        try {
            //		create the parser
            XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(ba));
            //		create the builder
            StAXOMBuilder builder = new StAXOMBuilder(parser);

            //		get the root element (in this case the envelope)
            OMElement documentElement = builder.getDocumentElement();

            return documentElement;
        } catch (XMLStreamException e) {
            throw new NHINIoException(e.getMessage(), e.getCause());
        }
    }


}
