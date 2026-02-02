package com.scnsoft.eldermark.adapterdocumentrepository;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.docrepository.adapter.proxy.AdapterComponentDocRepositoryProxy;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.apache.log4j.Logger;

public class AdapterComponentDocRepositoryProxyEldermarkImpl implements AdapterComponentDocRepositoryProxy {
    private static final Logger LOG = Logger.getLogger(AdapterComponentDocRepositoryProxyEldermarkImpl.class);

    public RetrieveDocumentSetResponseType retrieveDocument(RetrieveDocumentSetRequestType request,
                                                            AssertionType assertion) {
        LOG.debug("Using Eldermark Implementation for Adapter Component Doc Repository Service");
        return new AdapterComponentDocRepositoryOrchEldermarkImpl().documentRepositoryRetrieveDocumentSet(request);
    }

    public RegistryResponseType provideAndRegisterDocumentSet(ProvideAndRegisterDocumentSetRequestType body, AssertionType assertion) {
        LOG.debug("Using Eldermark Implementation for Adapter Component Doc Repository Service");
        return new AdapterComponentDocRepositoryOrchEldermarkImpl().documentRepositoryProvideAndRegisterDocumentSet(body);
    }

}
