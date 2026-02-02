package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.model.entity.MedicationActionPlanData;
import com.scnsoft.eldermark.consana.sync.server.model.fhir.XMedia;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class XMapToMedicationActionPlanDataConverter {

    public MedicationActionPlanData convert(XMedia source) {
        if (source == null || source.getContent() == null) {
            return null;
        }
        var fileName = source.getContent().getTitle();
        var contentType = source.getContent().getContentType();
        var data = source.getContent().getData();
        if (fileName != null && contentType != null && ArrayUtils.isNotEmpty(data)) {
            return new MedicationActionPlanData(fileName, fileName, contentType, data);
        }
        return null;
    }
}
