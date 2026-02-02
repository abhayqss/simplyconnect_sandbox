package com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.segment;

import ca.uhn.hl7v2.model.v251.segment.OBX;
import com.scnsoft.eldermark.entity.xds.datatype.OBXValue;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0078AbnormalFlags;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0085ObservationResultStatusCodesInterpretation;
import com.scnsoft.eldermark.entity.xds.segment.OBXObservationResult;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class OBXConverter extends HL7SegmentConverter<OBX, OBXObservationResult> {

    @Override
    protected OBXObservationResult doConvert(OBX source) {
        var obx = new OBXObservationResult();
        obx.setSetId(dataTypeService.getValue(source.getObx1_SetIDOBX()));
        obx.setValueType(dataTypeService.getValue(source.getObx2_ValueType()));
        obx.setObsvIdentifier(dataTypeService.createCE(source.getObx3_ObservationIdentifier()));
        obx.setObsvValues(dataTypeService.createStringList(source.getObx5_ObservationValue()).stream()
                .map(str -> new OBXValue(obx, str))
                .collect(Collectors.toList()));
        obx.setUnitsId(dataTypeService.createCE(source.getObx6_Units()));
        obx.setReferencesRange(dataTypeService.getValue(source.getObx7_ReferencesRange()));
        obx.setAbnormalFlags(dataTypeService.createISList(source.getObx8_AbnormalFlags(), HL7CodeTable0078AbnormalFlags.class));
        obx.setObservationResultStatus(dataTypeService.createID(source.getObx11_ObservationResultStatus(),
                HL7CodeTable0085ObservationResultStatusCodesInterpretation.class));
        obx.setDatetimeOfObservation(dataTypeService.convertTS(source.getObx14_DateTimeOfTheObservation()));
        obx.setPerformingOrgName(dataTypeService.createXON(source.getObx23_PerformingOrganizationName()));
        obx.setPerformingOrgAddr(dataTypeService.createXAD(source.getObx24_PerformingOrganizationAddress()));
        obx.setPerformingOrgMedicalDirector(dataTypeService.createXCN(source.getObx25_PerformingOrganizationMedicalDirector()));
        return obx;
    }

}
