package com.scnsoft.eldermark.services.transformer.adt.datatype;

import com.scnsoft.eldermark.entity.xds.datatype.DLNDriverSLicenseNumber;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.DLNDriverSLicenseNumberDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DLNDriverSLicenseNumberTransformer implements Converter<DLNDriverSLicenseNumber, DLNDriverSLicenseNumberDto> {

    @Override
    public DLNDriverSLicenseNumberDto convert(DLNDriverSLicenseNumber dlnDriverSLicenseNumber) {
        if (dlnDriverSLicenseNumber == null) {
            return null;
        }
        final DLNDriverSLicenseNumberDto dln = new DLNDriverSLicenseNumberDto();
        dln.setLicenseNumber(dlnDriverSLicenseNumber.getLicenseNumber());
        dln.setIssuingStateProvinceCountry(dlnDriverSLicenseNumber.getIssuingStateProvinceCountry());
        dln.setExpirationDate(dlnDriverSLicenseNumber.getExpirationDate());
        return dln;
    }
}
