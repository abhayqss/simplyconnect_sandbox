package com.scnsoft.eldermark.ws.server;

import com.scnsoft.eldermark.shared.DocumentRetrieveDto;
import com.scnsoft.eldermark.ws.server.exceptions.DocumentNotFoundException;
import com.scnsoft.eldermark.ws.server.exceptions.ResidentNotFoundException;
import com.scnsoft.eldermark.ws.server.exceptions.ResidentOptedOutException;
import org.apache.cxf.annotations.Policy;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;

/**
 * An endpoint for document downloads. It is separated from other web service operations in order to be able to
 * set and check special policies for download operation (for example, force MTOM).
 */
//@WebService(targetNamespace = Constants.WEB_SERVICES_NAMESPACE_DOCUMENTS_DOWNLOAD)
public interface DocumentsDownloadEndpoint {
//    @XmlMimeType("application/octet-stream")
//    @WebMethod
    DocumentRetrieveDto downloadDocument(
            @XmlElement(name = "documentId", required = true)
            Long documentId
    ) throws DocumentNotFoundException, ResidentOptedOutException;

//    @WebMethod
    DocumentRetrieveDto generateCcd(
//            @XmlElement(name = "residentId", required = true)
            Long residentId
    ) throws ResidentNotFoundException, ResidentOptedOutException;

//    @WebMethod
    DocumentRetrieveDto generateFacesheet(
//            @XmlElement(name = "residentId", required = true)
            Long residentId
    ) throws ResidentNotFoundException, ResidentOptedOutException;
}
