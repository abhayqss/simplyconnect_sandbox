package com.scnsoft.eldermark.services.transformer.adt.datatype;

import com.scnsoft.eldermark.entity.xds.datatype.DLDDischargeLocation;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.DLDDischargeLocationDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DLDDischargeLocationTransformer implements Converter<DLDDischargeLocation, DLDDischargeLocationDto> {
    @Override
    public DLDDischargeLocationDto convert(DLDDischargeLocation dldDischargeLocation) {
        if (dldDischargeLocation == null) {
            return null;
        }
        DLDDischargeLocationDto target = new DLDDischargeLocationDto();
        target.setDischargeLocation(dldDischargeLocation.getDischargeLocation());
        target.setEffectiveDate(dldDischargeLocation.getEffectiveDate());
        return target;
    }
}
