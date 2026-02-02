package org.openhealthtools.openxds.registry.patient.parser.v231.segment.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.v231.datatype.CX;
import ca.uhn.hl7v2.model.v231.datatype.XCN;
import ca.uhn.hl7v2.model.v231.datatype.XON;
import ca.uhn.hl7v2.model.v231.segment.PD1;
import org.openhealthtools.openxds.entity.datatype.CECodedElement;
import org.openhealthtools.openxds.entity.datatype.CXExtendedCompositeId;
import org.openhealthtools.openxds.entity.datatype.XCNExtendedCompositeIdNumberAndNameForPersons;
import org.openhealthtools.openxds.entity.datatype.XONExtendedCompositeNameAndIdForOrganizations;
import org.openhealthtools.openxds.entity.segment.AdtPD1AdditionalDemographicSegment;
import org.openhealthtools.openxds.registry.patient.parser.datatype.DataTypeService;
import org.openhealthtools.openxds.registry.patient.parser.util.Function;
import org.openhealthtools.openxds.registry.patient.parser.util.SegmentParserUtil;
import org.openhealthtools.openxds.registry.patient.parser.v231.segment.Pd1AdditionalDemographicSegmentParser;
import org.openhealthtools.openxds.util.SafeGetNullableUtil;
import org.openhealthtools.openxds.util.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Pd1AdditionalDemographicSegmentParserImpl extends AbstractAdtSegmentParser<AdtPD1AdditionalDemographicSegment, PD1>
        implements Pd1AdditionalDemographicSegmentParser {

    @Autowired
    private DataTypeService dataTypeService;

    @Override
    public AdtPD1AdditionalDemographicSegment doParse(final PD1 segment) throws HL7Exception, ApplicationException {
        final AdtPD1AdditionalDemographicSegment result = new AdtPD1AdditionalDemographicSegment();
        //TODO ggavrysh
//        result.setLivingDependencyList(SegmentParserUtil.parseArray(segment.getLivingDependency(), new Function<IS, String>() {
//            @Override
//            public String apply(final IS param) {
//                return param.getValue();
//            }
//        }));
        result.setLivingArrangement(SafeGetNullableUtil.safeNpeGet(new Supplier<String>() {
            @Override
            public String supply() {
                return segment.getLivingArrangement().getValue();
            }
        }));
        result.setPrimaryFacilityList(SegmentParserUtil.parseArray(segment.getPatientPrimaryFacility(), new Function<XON, XONExtendedCompositeNameAndIdForOrganizations>() {
            @Override
            public XONExtendedCompositeNameAndIdForOrganizations apply(final XON param) {
                return getDataTypeService().createXON(param);
            }
        }));
        result.setPrimaryCareProviderList(SegmentParserUtil.parseArray(segment.getPatientPrimaryCareProviderNameIDNo(), new Function<XCN, XCNExtendedCompositeIdNumberAndNameForPersons>() {
            @Override
            public XCNExtendedCompositeIdNumberAndNameForPersons apply(final XCN param) {
                return getDataTypeService().createXCN(param);
            }
        }));
        result.setStudentIndicator(SafeGetNullableUtil.safeNpeGet(new Supplier<String>() {
            @Override
            public String supply() {
                return segment.getStudentIndicator().getValue();
            }
        }));
        result.setHandicap(SafeGetNullableUtil.safeNpeGet(new Supplier<String>() {
            @Override
            public String supply() {
                return segment.getHandicap().getValue();
            }
        }));
        result.setLivingWill(SafeGetNullableUtil.safeNpeGet(new Supplier<String>() {
            @Override
            public String supply() {
                return segment.getLivingWill().getValue();
            }
        }));
        result.setOrganDonor(SafeGetNullableUtil.safeNpeGet(new Supplier<String>() {
            @Override
            public String supply() {
                return segment.getOrganDonor().getValue();
            }
        }));
        result.setSeparateBill(SafeGetNullableUtil.safeNpeGet(new Supplier<String>() {
            @Override
            public String supply() {
                return segment.getSeparateBill().getValue();
            }
        }));
        result.setDuplicatePatientList(SegmentParserUtil.parseArray(segment.getDuplicatePatient(), new Function<CX, CXExtendedCompositeId>() {
            @Override
            public CXExtendedCompositeId apply(final CX param) {
                return getDataTypeService().createCX(param);
            }
        }));
        result.setPublicityCode(SafeGetNullableUtil.safeNpeGet(new Supplier<CECodedElement>() {
            @Override
            public CECodedElement supply() {
                return getDataTypeService().createCE(segment.getPublicityCode());
            }
        }));
        result.setProtectionIndicator(SafeGetNullableUtil.safeNpeGet(new Supplier<String>() {
            @Override
            public String supply() {
                return segment.getProtectionIndicator().getValue();
            }
        }));
        return result;
    }

    public DataTypeService getDataTypeService() {
        return dataTypeService;
    }

    public void setDataTypeService(final DataTypeService dataTypeService) {
        this.dataTypeService = dataTypeService;
    }
}
