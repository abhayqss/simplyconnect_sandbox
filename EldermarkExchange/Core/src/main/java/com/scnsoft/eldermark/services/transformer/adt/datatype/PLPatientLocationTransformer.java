package com.scnsoft.eldermark.services.transformer.adt.datatype;

import com.scnsoft.eldermark.entity.xds.datatype.HDHierarchicDesignator;
import com.scnsoft.eldermark.entity.xds.datatype.PLPatientLocation;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.HDHierarchicDesignatorDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.PLPatientLocationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PLPatientLocationTransformer implements Converter<PLPatientLocation, PLPatientLocationDto> {

    @Autowired
    private Converter<HDHierarchicDesignator, HDHierarchicDesignatorDto> hdHierarchicDesignatorTransformer;

    @Override
    public PLPatientLocationDto convert(PLPatientLocation plPatientLocation) {
        if (plPatientLocation == null) {
            return null;
        }
        PLPatientLocationDto target = new PLPatientLocationDto();
        target.setPointOfCare(plPatientLocation.getPointOfCare());
        target.setRoom(plPatientLocation.getRoom());
        target.setBed(plPatientLocation.getBed());
        target.setFacility(hdHierarchicDesignatorTransformer.convert(plPatientLocation.getFacility()));
        target.setLocationStatus(plPatientLocation.getLocationStatus());
        target.setPersonLocationType(plPatientLocation.getPersonLocationType());
        target.setBuilding(plPatientLocation.getBuilding());
        target.setFloor(plPatientLocation.getFloor());
        target.setLocationDescription(plPatientLocation.getLocationDescription());
        return target;
    }
}
