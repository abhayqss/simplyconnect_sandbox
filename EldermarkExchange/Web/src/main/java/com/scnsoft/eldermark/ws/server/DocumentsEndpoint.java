package com.scnsoft.eldermark.ws.server;

import com.scnsoft.eldermark.shared.DocumentDto;
import com.scnsoft.eldermark.ws.server.exceptions.DocumentNotFoundException;
import com.scnsoft.eldermark.ws.server.exceptions.ResidentNotFoundException;
import com.scnsoft.eldermark.ws.server.exceptions.ResidentOptedOutException;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Groups documents-related operations (except download and upload, which should be separated).
 */
//@WebService(targetNamespace = Constants.WEB_SERVICES_NAMESPACE_DOCUMENTS)
public interface DocumentsEndpoint {
//    @WebMethod
    List<DocumentDto> queryForDocuments(
//            @XmlElement(name = "residentId", required = true)
            Long residentId
    ) throws ResidentNotFoundException, ResidentOptedOutException;

//    @WebMethod
    String deleteDocument(
//            @XmlElement(name = "documentId", required = true)
            Long documentId
    ) throws DocumentNotFoundException, ResidentOptedOutException;
}
