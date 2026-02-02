package org.openhealthtools.openxds.registry.adapter.omar31;

import org.openhealthtools.openxds.registry.DocumentBriefData;
import org.openhealthtools.openxds.registry.dao.XdsRegistryCustomDao;

/**
 * Created by averazub on 8/29/2016.
 */
public class XdsRegistryCustomServiceImpl {

    XdsRegistryCustomDao xdsRegistryCustomDao;

    public void updateDocumentEntryTitle(String uuid, String newTitle) {
        xdsRegistryCustomDao.updateDocumentEntryTitle("urn:uuid:"+uuid, newTitle);
    }

    public DocumentBriefData getDocumentData(String uuid) {
        return xdsRegistryCustomDao.getDocumentData("urn:uuid:"+uuid);
    }

    public XdsRegistryCustomDao getXdsRegistryCustomDao() {
        return xdsRegistryCustomDao;
    }

    public void setXdsRegistryCustomDao(XdsRegistryCustomDao xdsRegistryCustomDao) {
        this.xdsRegistryCustomDao = xdsRegistryCustomDao;
    }
}
