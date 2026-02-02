package org.openhealthtools.openxds.registry.dao;

import org.hibernate.Session;
import org.openhealthtools.openxds.registry.DocumentBriefData;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

/**
 * Created by averazub on 8/29/2016.
 */
public class XdsRegistryCustomDao extends HibernateDaoSupport {
    protected String APPROVED_STATUS_CODE = "urn:oasis:names:tc:ebxml-regrep:StatusType:Approved";
    public void updateDocumentEntryTitle(String uuid, String newTitle) {
        Session session = null;
        try {
            session = this.getSessionFactory().openSession();
            session.createSQLQuery("Update name_ set value=:docTitle WHERE parent=:uuid")
                    .setString("docTitle", newTitle)
                    .setString("uuid", uuid)
                    .executeUpdate();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public DocumentBriefData getDocumentData(String uuid) {
        Session session = null;
        try {
            session = this.getSessionFactory().openSession();
            @SuppressWarnings("unchecked")
            List<Object[]> results = session.createSQLQuery("Select id, status from extrinsicobject WHERE id=:uuid")
                    .setString("uuid", uuid)
                    .list();
            if (results.size()==0) {
                return new DocumentBriefData(uuid, false, null);
            } else if (results.size()==1) {
                String approvedStr = results.get(0)[1].toString();
                boolean approved = APPROVED_STATUS_CODE.equals(approvedStr);
                return new DocumentBriefData(uuid, true, approved);
            } else {
                throw new RuntimeException("2 documents with same uuid found");
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }

    }
}
