package com.scnsoft.eldermark.converter.hl7.entity2dto.datatype;

import com.scnsoft.eldermark.dto.adt.datatype.DischargeLocationDto;
import com.scnsoft.eldermark.entity.xds.datatype.DLDDischargeLocation;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly =  true)
public class DischargeLocationDtoConverter implements Converter<DLDDischargeLocation, DischargeLocationDto> {

    @Override
    public DischargeLocationDto convert(DLDDischargeLocation source) {
        if (source == null) {
            return null;
        }
        var target = new DischargeLocationDto();
        target.setDischargeLocation(source.getDischargeLocation());
        target.setEffectiveDate(DateTimeUtils.toEpochMilli(source.getEffectiveDate()));
        return target;
    }

}
