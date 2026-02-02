package com.scnsoft.eldermark.util;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.scnsoft.eldermark.entity.document.ClientDocument;
import com.scnsoft.eldermark.entity.document.Document;
import com.scnsoft.eldermark.entity.document.DocumentType;
import com.scnsoft.eldermark.entity.document.community.CommunityDocument;
import com.scnsoft.eldermark.entity.document.marco.MarcoIntegrationDocument;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.CareCoordinationConstants;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public final class DocumentUtils {

    private static final HashFunction DOCUMENT_HASH_ALGORITHM = Hashing.md5();
    public static final String MARCO_DOCUMENTS_DATASOURCE = "Simply Connect HIE";

    private DocumentUtils() {
    }

    public static String resolveMimeType(CommunityDocument document) {
        return resolveMimeType(document.getOriginalFileName(), document.getDocumentTitle(), document.getMimeType());
    }

    public static String resolveMimeType(ClientDocument document) {
        return resolveMimeType(document.getOriginalFileName(), document.getDocumentTitle(), document.getMimeType());
    }

    public static String resolveMimeType(Document document) {
        return resolveMimeType(document.getOriginalFileName(), document.getDocumentTitle(), document.getMimeType());
    }

    public static String resolveMimeType(String originalFileName, String documentTitle, String mimeType) {
        //third parties can send CCD xml documents with mime type different from text/xml,
        //therefore, fixing it prior to sending to UI
        if (originalFileName.endsWith(".xml") || documentTitle.endsWith(".xml")) {
            return MediaType.TEXT_XML_VALUE;
        }
        return mimeType;
    }

    public static void validateUploadedFile(MultipartFile file, Set<String> allowedExtensions) {
        if (file == null) {
            //TODo change to validation exception
            throw new InternalServerException(InternalServerExceptionType.DOCUMENTS_NULL);
        }
        if (file.getSize() > CareCoordinationConstants.ONE_MB * CareCoordinationConstants.MAX_FILE_SIZE_MB) {
            throw new InternalServerException(InternalServerExceptionType.DOCUMENTS_INCORRECT_SIZE);
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(extension) || !allowedExtensions.contains(extension.toUpperCase())) {
            throw new InternalServerException(InternalServerExceptionType.DOCUMENTS_INCORRECT_TYPE);
        }
    }

    public static String hash(byte[] content) {
        return DOCUMENT_HASH_ALGORITHM.hashBytes(content).toString();
    }

    public static void adjustForIntegrations(ClientDocument source, DocumentDtoAdjustableForIntegrations target) {
        MarcoIntegrationDocument marcoIntegrationDocLog = source.getMarcoIntegrationDocument();
        if (marcoIntegrationDocLog != null) {
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(marcoIntegrationDocLog.getAuthor())) {
                target.setAuthor(marcoIntegrationDocLog.getAuthor());
            }
            target.setOrganizationTitle(MARCO_DOCUMENTS_DATASOURCE);
            target.setOrganizationOid(null);
        }

        if (DocumentType.LAB_RESULT.equals(source.getDocumentType())) {
            target.setAuthor("System");
        }
    }

    public interface DocumentDtoAdjustableForIntegrations {
        void setAuthor(String author);
        void setOrganizationTitle(String organizationTitle);
        void setOrganizationOid(String oid);
    }
}
