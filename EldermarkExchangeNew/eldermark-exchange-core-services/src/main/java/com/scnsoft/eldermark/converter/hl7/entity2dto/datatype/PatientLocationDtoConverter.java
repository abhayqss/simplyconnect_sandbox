package com.scnsoft.eldermark.converter.hl7.entity2dto.datatype;

import com.scnsoft.eldermark.dto.adt.datatype.ClientLocationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.dto.adt.datatype.HDHierarchicDesignatorDto;
import com.scnsoft.eldermark.entity.xds.datatype.HDHierarchicDesignator;
import com.scnsoft.eldermark.entity.xds.datatype.PLPatientLocation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly =  true)
public class PatientLocationDtoConverter implements Converter<PLPatientLocation, ClientLocationDto> {

    @Autowired
    private Converter<HDHierarchicDesignator, HDHierarchicDesignatorDto> hdHierarchicDesignatorConverter;

    @Override
    public ClientLocationDto convert(PLPatientLocation source) {
        if (source == null) {
            return null;
        }
        ClientLocationDto target = new ClientLocationDto();
        target.setPointOfCare(source.getPointOfCare());
        target.setRoom(source.getRoom());
        target.setBed(source.getBed());
        target.setFacility(hdHierarchicDesignatorConverter.convert(source.getFacility()));
        target.setLocationStatus(source.getLocationStatus());
        target.setPersonLocationStatus(source.getPersonLocationType());
        target.setBuilding(source.getBuilding());
        target.setFloor(source.getFloor());
        target.setLocationDescription(source.getLocationDescription());
        return target;
    }

}
