package com.scnsoft.eldermark.services.connect;

import org.apache.cxf.endpoint.ConduitSelector;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.UpfrontConduitSelector;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.http.HTTPConduit;

/**
 * Created by pzhurba on 01-Feb-16.
 */
public class CxfConduilSelector extends UpfrontConduitSelector {
    final HTTPConduit defaultConduit;
    final ConduitSelector parent;

    public CxfConduilSelector(ConduitSelector parent, HTTPConduit defaultConduit) {
        this.parent = parent;
        this.defaultConduit = defaultConduit;
    }

    @Override
    protected Conduit findCompatibleConduit(Message message) {
        if (defaultConduit == null) {
            return super.findCompatibleConduit(message);
        } else {
            return defaultConduit;
        }

    }

    @Override
    public Endpoint getEndpoint() {
        return parent.getEndpoint();
    }
}

