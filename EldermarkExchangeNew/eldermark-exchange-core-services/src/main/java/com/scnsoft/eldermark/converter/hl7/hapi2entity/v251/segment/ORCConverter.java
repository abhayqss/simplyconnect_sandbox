package com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.segment;

import ca.uhn.hl7v2.model.v251.segment.ORC;
import com.scnsoft.eldermark.entity.xds.segment.ORCCommonOrderSegment;
import org.springframework.stereotype.Service;

@Service
public class ORCConverter extends HL7SegmentConverter<ORC, ORCCommonOrderSegment> {

    @Override
    protected ORCCommonOrderSegment doConvert(ORC source) {
        var orc = new ORCCommonOrderSegment();
        orc.setOrderControl(dataTypeService.getValue(source.getOrc1_OrderControl()));
        orc.setPlaceOrderNumber(dataTypeService.createEI(source.getOrc2_PlacerOrderNumber()));
        orc.setFillerOrderNumber(dataTypeService.createEI(source.getOrc3_FillerOrderNumber()));
        orc.setDatetimeOfTransaction(dataTypeService.convertTS(source.getOrc9_DateTimeOfTransaction()));
        orc.setOrderingProviders(dataTypeService.createXCNList(source.getOrc12_OrderingProvider()));
        orc.setEntererLocation(dataTypeService.createPL(source.getOrc13_EntererSLocation()));
        orc.setOrderEffectiveDatetime(dataTypeService.convertTS(source.getOrc15_OrderEffectiveDateTime()));
        orc.setEnteringOrganization(dataTypeService.createCE(source.getOrc17_EnteringOrganization()));
        orc.setActionByList(dataTypeService.createXCNList(source.getOrc19_ActionBy()));
        return orc;
    }

}
