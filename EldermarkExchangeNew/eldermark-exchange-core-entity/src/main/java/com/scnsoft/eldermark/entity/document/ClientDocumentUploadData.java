package com.scnsoft.eldermark.entity.document;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder;
import com.scnsoft.eldermark.entity.xds.segment.OBXObservationResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class ClientDocumentUploadData extends BaseUploadData {
    private final Client client;
    private final SharingOption sharingOption;

    private OBXObservationResult labObx;
    private LabResearchOrder labResearchOrder;
    private String uniqueId;

    private String consanaMapId;

    public ClientDocumentUploadData(
        MultipartFile doc,
        String customTitle,
        Client client,
        Employee author,
        SharingOption sharingOption,
        String description,
        List<Long> categoryIds
    ) throws IOException {
        super(doc, customTitle, author, description, client.getOrganization(), categoryIds);
        this.client = Objects.requireNonNull(client);
        this.sharingOption = Objects.requireNonNull(sharingOption);
    }


    public ClientDocumentUploadData(
        String title,
        String originalFileName,
        String mimeType,
        InputStream inputStream,
        Client client,
        Employee author,
        SharingOption sharingOption
    ) {
        super(title, originalFileName, mimeType, inputStream, author, null, client.getOrganization(), null);
        this.client = Objects.requireNonNull(client);
        this.sharingOption = Objects.requireNonNull(sharingOption);
    }

    public Client getClient() {
        return client;
    }

    public SharingOption getSharingOption() {
        return sharingOption;
    }

    public OBXObservationResult getLabObx() {
        return labObx;
    }

    public ClientDocumentUploadData withLabObx(OBXObservationResult labObx) {
        this.labObx = labObx;
        return this;
    }

    public LabResearchOrder getLabResearchOrder() {
        return labResearchOrder;
    }

    public ClientDocumentUploadData withLabResearchOrder(LabResearchOrder labResearchOrder) {
        this.labResearchOrder = labResearchOrder;
        return this;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public ClientDocumentUploadData withUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    public String getConsanaMapId() {
        return consanaMapId;
    }

    public ClientDocumentUploadData withConsanaMapId(String consanaMapId) {
        this.consanaMapId = consanaMapId;
        return this;
    }
}
