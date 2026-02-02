package com.scnsoft.eldermark.hl7v2.parse.v251.segment.impl;

import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.v251.segment.MSH;
import com.scnsoft.eldermark.entity.xds.segment.MSHMessageHeaderSegment;
import com.scnsoft.eldermark.hl7v2.parse.v251.segment.MshSegmentParser;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class MshSegmentParserImpl extends AbstractAdtSegmentParser<MSHMessageHeaderSegment, MSH> implements MshSegmentParser {

    @Override
    public MSHMessageHeaderSegment doParse(final MSH segment, MessageSource messageSource) throws ApplicationException {
        var msh = new MSHMessageHeaderSegment();
        msh.setFieldSeparator(segment.getMsh1_FieldSeparator().getValue());
        msh.setEncodingCharacters(segment.getMsh2_EncodingCharacters().getValue());
        msh.setSendingApplication(dataTypeService.createHd(segment.getMsh3_SendingApplication()));
        msh.setSendingFacility(dataTypeService.createHd(segment.getMsh4_SendingFacility()));
        msh.setReceivingApplication(dataTypeService.createHd(segment.getMsh5_ReceivingApplication()));
        msh.setReceivingFacility(dataTypeService.createHd(segment.getMsh6_ReceivingFacility()));
        msh.setDatetime(dataTypeService.convertTS(segment.getMsh7_DateTimeOfMessage()));
        msh.setMessageType(dataTypeService.createMSG(segment.getMsh9_MessageType()));
        msh.setMessageControlId(segment.getMsh10_MessageControlID().getValue());
        msh.setProcessingId(dataTypeService.convertPT(segment.getMsh11_ProcessingID()));
        msh.setVersionId(segment.getMessage().getVersion());
        return msh;
    }
}
