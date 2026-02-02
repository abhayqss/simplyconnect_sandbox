package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.model.entity.LongLegacyIdAwareEntity;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import org.hl7.fhir.instance.model.DomainResource;

public interface ConsanaDomainResourceToLongLegacyIdAwareEntityConverter<S extends DomainResource, T extends LongLegacyIdAwareEntity> {

    T convert(S source, Resident resident);

    T convertInto(S source, Resident resident, T target);
}
