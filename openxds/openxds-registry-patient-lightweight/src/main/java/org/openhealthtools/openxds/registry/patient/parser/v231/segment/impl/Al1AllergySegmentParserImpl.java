package org.openhealthtools.openxds.registry.patient.parser.v231.segment.impl;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v231.segment.AL1;
import org.openhealthtools.openxds.entity.datatype.CECodedElement;
import org.openhealthtools.openxds.entity.datatype.ISCodedValueForUserDefinedTables;
import org.openhealthtools.openxds.entity.hl7table.HL7CodeTable0127AllergenType;
import org.openhealthtools.openxds.entity.hl7table.HL7CodeTable0128AllergySeverity;
import org.openhealthtools.openxds.entity.segment.AdtAL1AllergySegment;
import org.openhealthtools.openxds.registry.patient.parser.datatype.DataTypeService;
import org.openhealthtools.openxds.registry.patient.parser.datatype.EmptyHL7Field231Service;
import org.openhealthtools.openxds.registry.patient.parser.v231.segment.Al1AllergySegmentParser;
import org.openhealthtools.openxds.util.SafeGetNullableUtil;
import org.openhealthtools.openxds.util.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class Al1AllergySegmentParserImpl extends AbstractAdtSegmentParser<AdtAL1AllergySegment, AL1>  implements Al1AllergySegmentParser {

    @Autowired
    private DataTypeService dataTypeService;

    @Autowired
    private EmptyHL7Field231Service emptyHL7Field231Service;

    @Override
    protected AdtAL1AllergySegment doParse(final AL1 segment) {
        final AdtAL1AllergySegment result = new AdtAL1AllergySegment();
        result.setSetId(SafeGetNullableUtil.safeNpeGet(new Supplier<String>() {
            @Override
            public String supply() {
                return segment.getSetIDAL1().getValue();
            }
        }));

        // [update hint] this field is of type CE since HL7 v2.4
        result.setAllergenType(SafeGetNullableUtil.safeNpeGet(new Supplier<CECodedElement>() {
            @Override
            public CECodedElement supply() {
                return getDataTypeService().createCEWithCodedValuesFromIS(segment.getAllergyType(), HL7CodeTable0127AllergenType.class);
            }
        }));
        result.setAllergyCode(SafeGetNullableUtil.safeNpeGet(new Supplier<CECodedElement>() {
            @Override
            public CECodedElement supply() {
                return getDataTypeService().createCE(segment.getAllergyCodeMnemonicDescription());
            }
        }));
        result.setAllergySeverity(SafeGetNullableUtil.safeNpeGet(new Supplier<ISCodedValueForUserDefinedTables<HL7CodeTable0128AllergySeverity>>() {
            @Override
            public ISCodedValueForUserDefinedTables<HL7CodeTable0128AllergySeverity> supply() {
                return getDataTypeService().createIS(segment.getAllergySeverity(), HL7CodeTable0128AllergySeverity.class);
            }
        }));
        result.setAllergyReactionList(getDataTypeService().createStringList(segment.getAllergyReaction()));
        result.setIdentificationDate(SafeGetNullableUtil.safeNpeGet(new Supplier<Date>() {
            @Override
            public Date supply() {
                try {
                    return getDataTypeService().convertDtToDate(segment.getIdentificationDate());
                } catch (DataTypeException e) {
                    return null;
                }
            }
        }));
        return result;
    }

    @Override
    public boolean isHl7SegmentEmpty(final AL1 al1) {
        return al1 == null || getEmptyHL7Field231Service().isAbstractPrimitiveEmpty(al1.getSetIDAL1());
    }

    public DataTypeService getDataTypeService() {
        return dataTypeService;
    }

    public void setDataTypeService(final DataTypeService dataTypeService) {
        this.dataTypeService = dataTypeService;
    }

    public EmptyHL7Field231Service getEmptyHL7Field231Service() {
        return emptyHL7Field231Service;
    }

    public void setEmptyHL7Field231Service(final EmptyHL7Field231Service emptyHL7Field231Service) {
        this.emptyHL7Field231Service = emptyHL7Field231Service;
    }
}
