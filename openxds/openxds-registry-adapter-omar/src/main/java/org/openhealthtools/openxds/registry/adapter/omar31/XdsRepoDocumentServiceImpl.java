package org.openhealthtools.openxds.registry.adapter.omar31;


import org.openhealthtools.openxds.registry.Document;
import org.openhealthtools.openxds.registry.dao.XdsRepoDocumentDao;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.SUPPORTS)
public class XdsRepoDocumentServiceImpl implements XdsRepoDocumentService {


    private XdsRepoDocumentDao xdsRepoDocumentDao;

    @Override
    public void saveDocument(Document document) {
        xdsRepoDocumentDao.save(document);
    }

    public void setXdsRepoDocumentDao(XdsRepoDocumentDao xdsRepoDocumentDao) {
        this.xdsRepoDocumentDao = xdsRepoDocumentDao;
    }
}
