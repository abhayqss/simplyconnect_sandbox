package com.scnsoft.eldermark.service.document.community;

import com.scnsoft.eldermark.entity.document.Document;
import com.scnsoft.eldermark.entity.document.community.CommunityDocumentUploadData;
import com.scnsoft.eldermark.service.document.BaseUploadDocumentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UploadCommunityDocumentServiceImpl extends BaseUploadDocumentService<CommunityDocumentUploadData> implements UploadCommunityDocumentService {

    @Override
    @Transactional
    public Document upload(CommunityDocumentUploadData data) {
        var document = super.upload(data);
        document.setCommunity(data.getCommunity());
        document.setFolder(data.getFolder());
        document.setEldermarkShared(false);
        return documentDao.save(document);
    }
}
