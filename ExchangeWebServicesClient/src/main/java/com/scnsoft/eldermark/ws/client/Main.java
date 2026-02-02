package com.scnsoft.eldermark.ws.client;

import com.scnsoft.eldermark.ws.api.*;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        SpringBusFactory bf = new SpringBusFactory();
        File busFile = new File("src\\main\\resources\\cxf-client.xml");
        if (!busFile.exists()) {
            throw new Exception("No cxf-client.xml found");
        }

        Bus bus = bf.createBus(busFile.toString());
        BusFactory.setDefaultBus(bus);


       residentsDiscovery();
//       queryForDocuments(42);
//       documentUpload("D:\\ccd.txt", "ccd.txt", 42);
//       documentDownload(4, "D:\\ccd_1.txt", false);
//       documentDelete(5);
//       generateCcd(109, "D:\\ccd.txt",true);
    }

    private static void residentsDiscovery() {
        ResidentsEndpointImplService service = new ResidentsEndpointImplService();
        ResidentsEndpoint port = service.getResidentsEndpointImplPort();

        ResidentFilter filter = new ResidentFilter();
        filter.setFirstName("Tanya");
        filter.setLastName("Beckman");
        filter.setGender(Gender.FEMALE);
        XMLGregorianCalendar calendar = new XMLGregorianCalendarImpl();
        calendar.setDay(20);
        calendar.setMonth(2);
        calendar.setYear(1921);
        filter.setDateOfBirth(calendar);
        filter.setLastFourDigitsOfSsn("8988");

        List<Resident> residents = port.searchResidents(filter);
        for (Resident resident: residents) {
            System.out.println(resident.getId() + " " + resident.getFirstName() + " " + resident.getLastName() + " " +
                    resident.getGender() + " " + resident.getDateOfBirth() + " " + resident.getCity());
        }
    }

    private static void queryForDocuments(long residentId) throws Exception {
        DocumentsEndpointImplService service = new DocumentsEndpointImplService();
        DocumentsEndpoint port = service.getDocumentsEndpointImplPort();

        try {
            List<Document> documents = port.queryForDocuments(residentId);
            for (Document document: documents) {
                System.out.println(document.getId() + " " + document.getDocumentTitle() + " "
                    + document.getMimeType() + " " + document.getAuthorName());
            }
        } catch (ResidentNotFoundFault f) {
            throw f;
        }
    }

    private static void documentDelete(long documentId) throws Exception {
        DocumentsEndpointImplService service = new DocumentsEndpointImplService();
        DocumentsEndpoint port = service.getDocumentsEndpointImplPort();

        port.deleteDocument(documentId);
    }

    private static void generateCcd(long residentId, String filePath, boolean useMtom) throws Exception {
        DocumentsDownloadEndpointImplService service = new DocumentsDownloadEndpointImplService();
        DocumentsDownloadEndpoint  port = service.getDocumentsDownloadEndpointImplPort();

        try {
            DocumentRetrieve response = port.generateCcd(residentId);
            try (
                    InputStream input = new ByteArrayInputStream(response.getData());
                    OutputStream output = new FileOutputStream(new File(filePath))
            ) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while((bytesRead=input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void documentUpload(String pathToDocument, String fileName, long residentId) throws Exception {
        DocumentsUploadEndpointImplService service = new DocumentsUploadEndpointImplService();

        DocumentsUploadEndpoint port = service.getDocumentsUploadEndpointImplPort();

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

        try {
            DocumentShareOptions shareOptions = new DocumentShareOptions();
            shareOptions.setIsSharedWithAll(false);
            port.uploadDocument(residentId, fileName, mimeType, dh, shareOptions);
        } catch (ResidentNotFoundFault f) {
            throw f;
        }
    }

    private static void documentDownload(long documentId, String filePath, boolean useMtom) throws Exception {
        DocumentsDownloadEndpointImplService service = new DocumentsDownloadEndpointImplService();
        DocumentsDownloadEndpoint port = service.getDocumentsDownloadEndpointImplPort();

        try {
            DocumentRetrieve dr = port.downloadDocument(documentId);
            try (
                    InputStream input = new ByteArrayInputStream(dr.getData());
                    OutputStream output = new FileOutputStream(new File(filePath))
            ) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while((bytesRead=input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentNotFoundFault f) {
            throw f;
        }
    }
}
