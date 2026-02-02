package com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.segment;

import ca.uhn.hl7v2.model.v251.segment.SPM;
import com.scnsoft.eldermark.entity.xds.segment.SPMSpecimen;
import org.springframework.stereotype.Service;

@Service
public class SPMConverter extends HL7SegmentConverter<SPM, SPMSpecimen> {

    @Override
    protected SPMSpecimen doConvert(SPM source) {
        var spm = new SPMSpecimen();
        spm.setSetId(dataTypeService.getValue(source.getSpm1_SetIDSPM()));
        spm.setSpecimenID(dataTypeService.createEIP(source.getSpm2_SpecimenID()));
        spm.setSpecimenType(dataTypeService.createCE(source.getSpm4_SpecimenType()));
        spm.setSpecimenCollectionDatetime(dataTypeService.createDR(source.getSpm17_SpecimenCollectionDateTime()));
        spm.setSpecimenReceivedDatetime(dataTypeService.convertTS(source.getSpm18_SpecimenReceivedDateTime()));
        return spm;
    }
}
