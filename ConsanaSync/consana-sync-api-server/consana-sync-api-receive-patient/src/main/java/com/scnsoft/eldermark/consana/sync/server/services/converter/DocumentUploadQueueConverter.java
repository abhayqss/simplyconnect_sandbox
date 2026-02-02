package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.common.model.dto.DocumentUploadQueueDto;
import com.scnsoft.eldermark.consana.sync.server.model.entity.MedicationActionPlanData;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import com.scnsoft.eldermark.consana.sync.server.model.enums.SharingOption;
import com.scnsoft.eldermark.consana.sync.server.model.fhir.XMedicationActionPlan;
import com.scnsoft.eldermark.consana.sync.server.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentUploadQueueConverter {

    @Autowired
    private EmployeeService employeeService;

    public DocumentUploadQueueDto convert(MedicationActionPlanData source, Resident resident, XMedicationActionPlan plan) {
        var target = new DocumentUploadQueueDto();
        target.setTitle(source.getTitle());
        target.setOriginalFileName(source.getOriginalFileName());
        target.setMimeType(source.getMimeType());
        target.setData(source.getData());
        target.setClientId(resident.getId());
        var author = employeeService.getConsanaAuthorEmployee();
        target.setAuthorId(author.getId());
        target.setSharingOption(SharingOption.ALL.name());
        target.setConsanaMapId(plan.getId());
        return target;
    }
}
