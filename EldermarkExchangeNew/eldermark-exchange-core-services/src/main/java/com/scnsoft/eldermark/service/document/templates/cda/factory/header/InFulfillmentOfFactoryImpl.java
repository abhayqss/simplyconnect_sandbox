package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.InFulfillmentOf;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * inFulfillmentOf/order: Order being fulfilled by this document (e.g. X-Ray order for a Diagnostic Imaging Report)
 */
@Component
public class InFulfillmentOfFactoryImpl extends OptionalTemplateFactory implements InFulfillmentOfFactory {

    @Value("${header.inFulfillmentOfs.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public List<InFulfillmentOf> buildTemplateInstance(Collection<BasicEntity> inFulfillmentOfs) {
        final List<InFulfillmentOf> list = new ArrayList<>();
        list.add(CDAFactory.eINSTANCE.createInFulfillmentOf());
        return list;
    }

    @Override
    public List<BasicEntity> parseSection(Client client, Collection<InFulfillmentOf> inFulfillmentOfs) {
        if (CollectionUtils.isEmpty(inFulfillmentOfs)) {
            return Collections.emptyList();
        }
        checkNotNull(client);

        final List<BasicEntity> resultList = new ArrayList<>();
        for (InFulfillmentOf inFulfillmentOf : inFulfillmentOfs) {
            // TODO implement
        }
        return resultList;
    }

}
