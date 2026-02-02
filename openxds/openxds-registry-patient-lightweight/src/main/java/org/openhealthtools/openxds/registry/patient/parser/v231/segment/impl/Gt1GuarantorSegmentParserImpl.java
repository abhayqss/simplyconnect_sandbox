package org.openhealthtools.openxds.registry.patient.parser.v231.segment.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.v231.datatype.XAD;
import ca.uhn.hl7v2.model.v231.datatype.XPN;
import ca.uhn.hl7v2.model.v231.datatype.XTN;
import ca.uhn.hl7v2.model.v231.segment.GT1;
import org.openhealthtools.openxds.entity.datatype.XADPatientAddress;
import org.openhealthtools.openxds.entity.datatype.XPNPersonName;
import org.openhealthtools.openxds.entity.datatype.XTNPhoneNumber;
import org.openhealthtools.openxds.entity.segment.AdtGT1GuarantorSegment;
import org.openhealthtools.openxds.registry.patient.parser.datatype.DataTypeService;
import org.openhealthtools.openxds.registry.patient.parser.datatype.EmptyHL7Field231Service;
import org.openhealthtools.openxds.registry.patient.parser.v231.segment.Gt1GuarantorSegmentParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Gt1GuarantorSegmentParserImpl extends AbstractAdtSegmentParser<AdtGT1GuarantorSegment, GT1> implements Gt1GuarantorSegmentParser {

    @Autowired
    private DataTypeService dataTypeService;

    @Autowired
    private EmptyHL7Field231Service emptyHL7Field231Service;

    @Override
    public AdtGT1GuarantorSegment doParse(final GT1 segment) throws HL7Exception, ApplicationException {
        final AdtGT1GuarantorSegment result = new AdtGT1GuarantorSegment();
        result.setSetId(segment.getSetIDGT1().getValue());
        result.setPrimaryLanguage(getDataTypeService().createCE(segment.getPrimaryLanguage()));
        result.setGuarantorNameList(createGuarantorNameList(segment));
        result.setGuarantorAddressList(createGuarantorAddressList(segment));
        result.setGuarantorPhNumHomeList(createPhNumList(segment));
        return result;
    }

    @Override
    public boolean isHl7SegmentEmpty(final GT1 gt1) {
        return gt1 == null || getEmptyHL7Field231Service().isAbstractPrimitiveEmpty(gt1.getSetIDGT1());
    }

    //TODO ggavrysh remove duplicate code
    private List<XTNPhoneNumber> createPhNumList(final GT1 segment) {
        final List<XTNPhoneNumber> result = new ArrayList<XTNPhoneNumber>();
        for (XTN xtn : segment.getGuarantorPhNumHome()) {
            result.add(getDataTypeService().createXTN(xtn));
        }
        return result;
    }

    private List<XADPatientAddress> createGuarantorAddressList(final GT1 segment) {
        final List<XADPatientAddress> result = new ArrayList<XADPatientAddress>();
        for (XAD guarantorAddress : segment.getGuarantorAddress()) {
            result.add(getDataTypeService().createXAD(guarantorAddress));
        }
        return result;
    }

    private List<XPNPersonName> createGuarantorNameList(GT1 segment) {
        final List<XPNPersonName> result = new ArrayList<XPNPersonName>();
        for (XPN guarantorNameItem : segment.getGuarantorName()) {
            result.add(getDataTypeService().createXPN(guarantorNameItem));
        }
        return result;
    }

    public DataTypeService getDataTypeService() {
        return dataTypeService;
    }

    public void setDataTypeService(DataTypeService dataTypeService) {
        this.dataTypeService = dataTypeService;
    }

    public EmptyHL7Field231Service getEmptyHL7Field231Service() {
        return emptyHL7Field231Service;
    }

    public void setEmptyHL7Field231Service(final EmptyHL7Field231Service emptyHL7Field231Service) {
        this.emptyHL7Field231Service = emptyHL7Field231Service;
    }
}
