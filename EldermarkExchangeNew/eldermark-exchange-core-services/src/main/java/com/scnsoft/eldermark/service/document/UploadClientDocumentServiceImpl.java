package com.scnsoft.eldermark.service.document;

import com.scnsoft.eldermark.dao.ClientDocumentDao;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.ClientDocumentUploadData;
import com.scnsoft.eldermark.entity.document.Document;
import com.scnsoft.eldermark.entity.document.SharingOption;
import com.scnsoft.eldermark.exception.XdsCommunicationException;
import com.scnsoft.eldermark.service.EventNotificationService;
import com.scnsoft.eldermark.service.xds.XdsRegistryConnectorService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service("clientDocumentUploadService")
public class UploadClientDocumentServiceImpl extends BaseUploadDocumentService<ClientDocumentUploadData> implements UploadClientDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(UploadClientDocumentServiceImpl.class);

    @Value("${home.community.id}")
    private String homeCommunityId;

    @Autowired
    private XdsRegistryConnectorService xdsRegistryConnectorService;

    @Autowired
    private EventNotificationService eventNotificationService;

    @Autowired
    private ClientDocumentDao clientDocumentDao;

    @Autowired
    private ClientDocumentService clientDocumentService;

    @Override
    @Transactional
    public Document upload(ClientDocumentUploadData data) {
        var document = super.upload(data);

        document.setClientOrganizationAlternativeId(data.getClient().getOrganization().getAlternativeId());
        document.setClientLegacyId(data.getClient().getLegacyId());

        document.setEldermarkShared(SharingOption.ALL == data.getSharingOption());
        document.setLabObx(data.getLabObx());
        document.setConsanaMapId(data.getConsanaMapId());
        document.setLabResearchOrder(data.getLabResearchOrder());

        var shareWithOrganizations = new ArrayList<Organization>();
        if (SharingOption.MY_COMPANY == data.getSharingOption()) {
            shareWithOrganizations.add(data.getClient().getOrganization());
        }

        document.setSharedWithOrganizations(shareWithOrganizations);
        document.setIsCDA(clientDocumentService.defineIsCdaDocument(document));

        if (StringUtils.isNotEmpty(data.getUniqueId())) {
            document.setUniqueId(data.getUniqueId());
        } else {
            document = documentDao.save(document);
            document.setUniqueId(homeCommunityId + ".3." + document.getId());
        }
        document = documentDao.save(document);

        try {
            xdsRegistryConnectorService.saveNewFileInRegistry(document, data.getClient().getId());
            if (!xdsRegistryConnectorService.isDocumentVisibleInRegistry(document)) {
                xdsRegistryConnectorService.deprecateDocumentInRepository(document.getUuid());
            }
        } catch (XdsCommunicationException e) {
            logger.warn("Failed to save document in XDS registry", e);
            //todo reuse deletion
            document.setVisible(false);
            documentDao.save(document);
            documentFileService.delete(document); //or we shouldn't delete file?
            throw e;
        }

        if (StringUtils.isNotEmpty(document.getConsanaMapId())) {
            eventNotificationService.sendNewMAP(clientDocumentDao.findById(document.getId()).orElseThrow());
        }

        return document;
    }
}
