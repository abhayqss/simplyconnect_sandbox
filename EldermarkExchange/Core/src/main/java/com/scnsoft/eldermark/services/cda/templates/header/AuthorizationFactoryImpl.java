package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.BasicEntity;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.templates.OptionalTemplateFactory;
import org.eclipse.mdht.uml.cda.Authorization;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * authorization/consent: Various “consents” relevant to the document (e.g. consent to perform procedure being documented)
 */
// Stub
@Component
public class AuthorizationFactoryImpl extends OptionalTemplateFactory implements AuthorizationFactory {

    @Value("${header.authorizations.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public List<Authorization> buildTemplateInstance(Collection<BasicEntity> authorizations) {
        final List<Authorization> list = new ArrayList<>();
        list.add(CDAFactory.eINSTANCE.createAuthorization());
        return list;
    }

    @Override
    public List<BasicEntity> parseSection(Resident resident, Collection<Authorization> authorizations) {
        if (CollectionUtils.isEmpty(authorizations)) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final List<BasicEntity> resultList = new ArrayList<>();
        for (Authorization authorization : authorizations) {
            // TODO implement
        }
        return resultList;
    }

}
