package com.scnsoft.eldermark.services.transformer.adt.dto;

import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.datatype.CXExtendedCompositeId;
import com.scnsoft.eldermark.entity.xds.datatype.XCNExtendedCompositeIdNumberAndNameForPersons;
import com.scnsoft.eldermark.entity.xds.datatype.XONExtendedCompositeNameAndIdForOrganizations;
import com.scnsoft.eldermark.entity.xds.segment.AdtPD1AdditionalDemographicSegment;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.adt.PD1AdditionalDemographicSegmentDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.CECodedElementDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.CXExtendedCompositeIdDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.XCNExtendedCompositeIdNumberAndNameForPersonsDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.XONExtendedCompositeNameAndIdForOrganizationsDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PD1SegmentTransformer extends ListAndItemTransformer<AdtPD1AdditionalDemographicSegment, PD1AdditionalDemographicSegmentDto> {

    @Autowired
    private ListAndItemTransformer<XONExtendedCompositeNameAndIdForOrganizations, XONExtendedCompositeNameAndIdForOrganizationsDto> xonExtendedCompositeNameAndIdForOrganizationsDtoTransformer;

    @Autowired
    private ListAndItemTransformer<XCNExtendedCompositeIdNumberAndNameForPersons, XCNExtendedCompositeIdNumberAndNameForPersonsDto> xcnExtendedCompositeIdNumberAndNameForPersonsDtoTransformer;

    @Autowired
    private ListAndItemTransformer<CXExtendedCompositeId, CXExtendedCompositeIdDto> cxExtendedCompositeIdTransformer;

    @Autowired
    private Converter<CECodedElement, CECodedElementDto> ceCodedElementTransformer;

    @Override
    public PD1AdditionalDemographicSegmentDto convert(AdtPD1AdditionalDemographicSegment adtPD1AdditionalDemographicSegment) {
        if (adtPD1AdditionalDemographicSegment == null) {
            return null;
        }
        PD1AdditionalDemographicSegmentDto target = new PD1AdditionalDemographicSegmentDto();
        //TODO livingDependencyList
        target.setLivingArrangement(adtPD1AdditionalDemographicSegment.getLivingArrangement());
        if (CollectionUtils.isNotEmpty(adtPD1AdditionalDemographicSegment.getPrimaryFacilityList())) {
            List<XONExtendedCompositeNameAndIdForOrganizationsDto> primaryFacilityDtos = new ArrayList<>();
            xonExtendedCompositeNameAndIdForOrganizationsDtoTransformer.convertList(adtPD1AdditionalDemographicSegment.getPrimaryFacilityList(), primaryFacilityDtos);
            target.setPrimaryFacilityList(primaryFacilityDtos);
        }
        if (CollectionUtils.isNotEmpty(adtPD1AdditionalDemographicSegment.getPrimaryCareProviderList())) {
            List<XCNExtendedCompositeIdNumberAndNameForPersonsDto> primaryCareProviderDtos = new ArrayList<>();
            xcnExtendedCompositeIdNumberAndNameForPersonsDtoTransformer.convertList(adtPD1AdditionalDemographicSegment.getPrimaryCareProviderList(), primaryCareProviderDtos);
            target.setPrimaryCareProviderList(primaryCareProviderDtos);
        }
        target.setStudentIndicator(adtPD1AdditionalDemographicSegment.getStudentIndicator());
        target.setHandicap(adtPD1AdditionalDemographicSegment.getHandicap());
        target.setLivingWill(adtPD1AdditionalDemographicSegment.getLivingWill());
        target.setOrganDonor(adtPD1AdditionalDemographicSegment.getOrganDonor());
        target.setSeparateBill(adtPD1AdditionalDemographicSegment.getSeparateBill());
        if (CollectionUtils.isNotEmpty(adtPD1AdditionalDemographicSegment.getDuplicatePatientList())) {
            List<CXExtendedCompositeIdDto> duplicatePatientListDtos = new ArrayList<>();
            cxExtendedCompositeIdTransformer.convertList(adtPD1AdditionalDemographicSegment.getDuplicatePatientList(), duplicatePatientListDtos);
            target.setDuplicatePatientList(duplicatePatientListDtos);
        }
        target.setPublicityCode(ceCodedElementTransformer.convert(adtPD1AdditionalDemographicSegment.getPublicityCode()));
        target.setProtectionIndicator(adtPD1AdditionalDemographicSegment.getProtectionIndicator());
        return target;
    }
}
