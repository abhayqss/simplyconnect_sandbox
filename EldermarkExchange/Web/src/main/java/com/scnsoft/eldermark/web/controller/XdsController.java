package com.scnsoft.eldermark.web.controller;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.*;
import com.scnsoft.eldermark.services.beans.DocumentMetadata;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.shared.exceptions.CcdGenerationException;
import com.scnsoft.eldermark.shared.exceptions.DocumentNotFoundException;
import com.scnsoft.eldermark.shared.exceptions.FileIOException;
import com.scnsoft.eldermark.xds.XdsDocument;
import com.scnsoft.eldermark.xds.XdsFacade;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;

/**
 * Created by averazub on 8/31/2016.
 */
//@Controller
public class XdsController {


    @Autowired
    private DocumentService documentService;

    @Autowired
    private ReportGeneratorFactory reportGeneratorFactory;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private XdsFacade xdsFacade;

    @Autowired
    private MPIService mpiService;

    @Value("${home.community.id}")
    private String homeCommunityId;


    @RequestMapping(value = "/documents/xds/synchronize_all", method = RequestMethod.GET)
    public void syncAll(@RequestParam(value = "fromTime") Date fromTime,  HttpServletResponse response) throws IOException {
        documentService.synchronizeAllDocumentsWithXdsRegistry(response.getWriter(), fromTime==null?new Date(0):fromTime);
        response.getWriter().close();
    }

    @RequestMapping(value = "/documents/xds/upload", method = RequestMethod.POST)
    @ResponseBody
    public Long uploadDocument (@RequestBody final XdsDocument uploadDocument) {
        DocumentMetadata documentMetadata = new DocumentMetadata.Builder()
                .setDocumentTitle(uploadDocument.getTitle ())
                .setFileName(uploadDocument.getTitle())
                .setMimeType(uploadDocument.getMimeType ())
                .build();

        final Long result = xdsFacade.saveAndParseCDA(uploadDocument, documentMetadata,
                uploadDocument.getResidentId(),
                uploadDocument.getUuid(),
                uploadDocument.getUniqueId(),
                uploadDocument.getHash(),
                new SaveDocumentCallbackImpl() {
                    @Override
                    public void saveToFile(File file) {
                        try {
                            FileCopyUtils.copy(uploadDocument.getContent(), new FileOutputStream(file));
                        } catch (IOException e) {
                            throw new FileIOException("Failed to save file " + uploadDocument.getTitle (), e);
                        }
                    }
                });
        return result;
    }

    @RequestMapping(value = "/documents/xds/download", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public XdsDocument getDocument (@RequestParam(value = "documentUniqueId") String documentUniqueId) {

        Document document;
        Long residentId = null;
        byte[] content;
        if (documentUniqueId.endsWith(".0")) {
            String[] ar = documentUniqueId.split("\\.");
            residentId = Long.parseLong(ar[ar.length-2]);
//            documentFacade.downloadOrViewReport(residentId,"ccd",false,true);
            if (residentId==null || !documentUniqueId.contains(homeCommunityId)) {
                throw new DocumentNotFoundException(documentUniqueId);
            }

            ReportGenerator generator = reportGeneratorFactory.getGenerator("ccd");
            Report report = generator.generate(residentId, true);

//            String contentType = "text/xml";

//            response.setContentType(contentType);
//            response.setHeader("Content-Disposition", openType + ";filename=\"" + document.getDocumentTitle() + "\"");

            try {
//                FileCopyUtils.copy(document.getInputStream(), response.getOutputStream());
                content=IOUtils.toByteArray(report.getInputStream());
                ByteSource byteSource = ByteSource.wrap(content);
                String hash = byteSource.hash(Hashing.md5()).toString();
                document = new Document();
                document.setHash(hash);
                document.setMimeType("text/xml");
                document.setDocumentTitle("CCD.xml");

            } catch (IOException e) {
                throw new CcdGenerationException();
            }


        } else {
            document = xdsFacade.getDocument(documentUniqueId);
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();

                File f = documentService.getDocumentFile(document);
                FileCopyUtils.copy(new FileInputStream(f), os);
                content = os.toByteArray();

                Resident resident = residentService.getResident(document.getResidentLegacyId(), document.getResidentDatabaseAlternativeId());
                residentId=resident.getId();
            } catch (IOException e) {
                throw new FileIOException("Failed to get file " + document.getOriginalFileName(), e);
            }
        }

        XdsDocument dest = new XdsDocument();
        dest.setUuid(document.getUuid());
        dest.setUniqueId(documentUniqueId);
        dest.setHash(document.getHash());
        dest.setMimeType(document.getMimeType());
        dest.setTitle(document.getDocumentTitle());
        dest.setContent(content);
        dest.setResidentId(residentId);
        return dest;
    }

    @RequestMapping(value = "/documents/xds/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void deleteDocument (@RequestParam(value = "documentUniqueId") String documentUniqueId) {
        try {
            xdsFacade.deleteDocument(documentUniqueId);
        } catch (IOException e) {
            throw new FileIOException ("Failed to delete file with unique id" + documentUniqueId, e);
        }

    }



}
