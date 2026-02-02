package org.openhealthtools.openxds.registry.patient.parser.v231.segment.impl;

import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import org.openhealthexchange.openpixpdq.data.MessageHeader;
import org.openhealthexchange.openpixpdq.ihe.impl_v2.hl7.HL7Header;
import org.openhealthtools.openxds.entity.datatype.HDHierarchicDesignator;
import org.openhealthtools.openxds.entity.datatype.MSGMessageType;
import org.openhealthtools.openxds.entity.segment.MSHMessageHeaderSegment;
import org.openhealthtools.openxds.registry.patient.parser.v231.segment.MshSegmentParser;
import org.springframework.stereotype.Service;

@Service
public class MshSegmentParserImpl extends AbstractAdtSegmentParser<MSHMessageHeaderSegment, MSH> implements MshSegmentParser {

    @Override
    public MSHMessageHeaderSegment doParse(final MSH segment) throws ApplicationException {
        final MSHMessageHeaderSegment msh = new MSHMessageHeaderSegment();
        final HL7Header hl7Header = new HL7Header(segment.getMessage());
        final MessageHeader header = hl7Header.toMessageHeader();
        msh.setFieldSeparator(segment.getFieldSeparator().getValue());
        msh.setEncodingCharacters(segment.getEncodingCharacters().getValue());
        msh.setSendingApplication(new HDHierarchicDesignator(header.getSendingApplication().getNamespaceId(), header.getSendingApplication().getUniversalId(), header.getSendingApplication().getUniversalIdType()));
        msh.setReceivingApplication(new HDHierarchicDesignator(header.getReceivingApplication().getNamespaceId(), header.getReceivingApplication().getUniversalId(), header.getReceivingApplication().getUniversalIdType()));
        msh.setSendingFacility(new HDHierarchicDesignator(header.getSendingFacility().getNamespaceId(), header.getSendingFacility().getUniversalId(), header.getSendingFacility().getUniversalIdType()));
        msh.setReceivingFacility(new HDHierarchicDesignator(header.getReceivingFacility().getNamespaceId(), header.getReceivingFacility().getUniversalId(), header.getReceivingFacility().getUniversalIdType()));
        if (header.getMessgeDate() != null) {
            msh.setDatetime(header.getMessgeDate().getTime());
        }
        msh.setMessageType(new MSGMessageType(header.getMessageCode(), header.getTriggerEvent(), header.getMessageStructure()));
        msh.setMessageControlId(segment.getMessageControlID().getValue());
        msh.setVersionId(segment.getMessage().getVersion());
        return msh;
    }
}
