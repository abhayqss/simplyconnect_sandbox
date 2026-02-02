package com.scnsoft.eldermark.adapterdocumentregistry;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.docregistry.adapter.proxy.AdapterComponentDocRegistryProxy;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import org.apache.log4j.Logger;

public class AdapterComponentDocRegistryProxyEldermarkImpl  implements AdapterComponentDocRegistryProxy {

    private static final Logger LOG = Logger.getLogger(AdapterComponentDocRegistryProxyEldermarkImpl.class);

    public AdhocQueryResponse registryStoredQuery(AdhocQueryRequest request, AssertionType assertion) {
        LOG.trace("Using Eldermark Implementation for Adapter Component Doc Registry");
        return new AdapterComponentDocRegistryOrchEldermarkImpl().registryStoredQuery(request);
    }
}
