package com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.segment;

import ca.uhn.hl7v2.model.v251.segment.MSH;
import com.scnsoft.eldermark.entity.xds.segment.MSHMessageHeaderSegment;
import org.springframework.stereotype.Component;

@Component
public class MSHConverter extends HL7SegmentConverter<MSH, MSHMessageHeaderSegment> {

    @Override
    protected MSHMessageHeaderSegment doConvert(MSH source) {
        var msh = new MSHMessageHeaderSegment();
        msh.setFieldSeparator(source.getMsh1_FieldSeparator().getValue());
        msh.setEncodingCharacters(source.getMsh2_EncodingCharacters().getValue());
        msh.setSendingApplication(dataTypeService.createHd(source.getMsh3_SendingApplication()));
        msh.setSendingFacility(dataTypeService.createHd(source.getMsh4_SendingFacility()));
        msh.setReceivingApplication(dataTypeService.createHd(source.getMsh5_ReceivingApplication()));
        msh.setReceivingFacility(dataTypeService.createHd(source.getMsh6_ReceivingFacility()));
        msh.setDatetime(dataTypeService.convertTS(source.getMsh7_DateTimeOfMessage()));
        msh.setMessageType(dataTypeService.createMSG(source.getMsh9_MessageType()));
        msh.setMessageControlId(source.getMsh10_MessageControlID().getValue());
        msh.setProcessingId(dataTypeService.convertPT(source.getMsh11_ProcessingID()));
        msh.setVersionId(source.getMessage().getVersion());
        return msh;
    }
}
