package com.scnsoft.eldermark.ws.server;

import com.scnsoft.eldermark.ws.server.dto.DocumentShareOptionsWsDto;
import com.scnsoft.eldermark.ws.server.exceptions.DocumentSharePolicyViolation;
import com.scnsoft.eldermark.ws.server.exceptions.OrganizationNotFoundException;
import com.scnsoft.eldermark.ws.server.exceptions.ResidentNotFoundException;
import com.scnsoft.eldermark.ws.server.exceptions.ResidentOptedOutException;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;

/**
 * An endpoint for document uploads. It is separated from other web service operations in order to be able to
 * set and check special policies for upload operation (for example, force MTOM).
 */
//@WebService(targetNamespace = Constants.WEB_SERVICES_NAMESPACE_DOCUMENTS_UPLOAD)
public interface DocumentsUploadEndpoint {
//    @WebMethod
    Long uploadDocument(
            @XmlElement(name = "residentId", required = true)
            Long residentId,

            @XmlElement(name = "fileName", required = true)
            String fileName,

            @XmlElement(name = "mimeType", required = true)
            String mimeType,

            @XmlElement(name = "data", required = true)
            @XmlMimeType("application/octet-stream")
            DataHandler data,

            @XmlElement(name = "shareOptions", required = true)
            DocumentShareOptionsWsDto shareOptions

    ) throws ResidentNotFoundException, ResidentOptedOutException, OrganizationNotFoundException,
            DocumentSharePolicyViolation;
}
