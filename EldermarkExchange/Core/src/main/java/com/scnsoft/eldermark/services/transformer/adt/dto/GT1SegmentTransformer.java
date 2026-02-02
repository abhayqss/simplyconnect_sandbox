package com.scnsoft.eldermark.services.transformer.adt.dto;

import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.datatype.XADPatientAddress;
import com.scnsoft.eldermark.entity.xds.datatype.XPNPersonName;
import com.scnsoft.eldermark.entity.xds.datatype.XTNPhoneNumber;
import com.scnsoft.eldermark.entity.xds.segment.AdtGT1GuarantorSegment;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.adt.GT1GuarantorSegmentDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.CECodedElementDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.XADPatientAddressDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.XTNPhoneNumberDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GT1SegmentTransformer extends ListAndItemTransformer<AdtGT1GuarantorSegment, GT1GuarantorSegmentDto> {

    @Autowired
    private Converter<CECodedElement, CECodedElementDto> ceCodedElementTransformer;

    @Autowired
    private Converter<XADPatientAddress, XADPatientAddressDto> xadPatientAddressTransformer;

    @Autowired
    private ListAndItemTransformer<XTNPhoneNumber, XTNPhoneNumberDto> xtnPhoneNumberTransformer;

    @Override
    public GT1GuarantorSegmentDto convert(AdtGT1GuarantorSegment adtGT1GuarantorSegment) {
        if (adtGT1GuarantorSegment == null) {
            return null;
        }
        GT1GuarantorSegmentDto target = new GT1GuarantorSegmentDto();
        target.setSetId(adtGT1GuarantorSegment.getSetId());
        if (adtGT1GuarantorSegment.getPrimaryLanguage() != null) {
            target.setPrimaryLanguage(ceCodedElementTransformer.convert(adtGT1GuarantorSegment.getPrimaryLanguage()));
        }
        if (CollectionUtils.isNotEmpty(adtGT1GuarantorSegment.getGuarantorNameList())) {
            List<String> guarantorNameDtoList = new ArrayList<>();
            for (XPNPersonName guarantorName : adtGT1GuarantorSegment.getGuarantorNameList()) {
                guarantorNameDtoList.add(CareCoordinationUtils.getFullName(guarantorName.getFirstName(), guarantorName.getLastName()));
            }
            target.setGuarantorNameList(guarantorNameDtoList);
        }
        if (CollectionUtils.isNotEmpty(adtGT1GuarantorSegment.getGuarantorAddressList())) {
            List<XADPatientAddressDto> guarantorAddressDtoList = new ArrayList<>();
            for (XADPatientAddress guarantorAddress : adtGT1GuarantorSegment.getGuarantorAddressList()) {
                guarantorAddressDtoList.add(xadPatientAddressTransformer.convert(guarantorAddress));
            }
            target.setGuarantorAddressList(guarantorAddressDtoList);
        }
        if (CollectionUtils.isNotEmpty(adtGT1GuarantorSegment.getGuarantorPhNumHomeList())) {
            List<XTNPhoneNumberDto> guarantorPhonesDtoList = new ArrayList<>();
            for (XTNPhoneNumber guarantorPhone : adtGT1GuarantorSegment.getGuarantorPhNumHomeList()) {
                guarantorPhonesDtoList.add(xtnPhoneNumberTransformer.convert(guarantorPhone));
            }
            target.setGuarantorPhNumHomeList(guarantorPhonesDtoList);
        }
        return target;
    }
}
