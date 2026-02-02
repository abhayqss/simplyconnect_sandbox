package com.scnsoft.eldermark.converter.event.base;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.event.TreatingHospitalViewData;
import com.scnsoft.eldermark.dto.event.TreatingPhysicianViewData;
import com.scnsoft.eldermark.dto.event.TreatmentViewData;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventAddress;
import com.scnsoft.eldermark.entity.event.EventTreatingHospital;
import com.scnsoft.eldermark.entity.event.EventTreatingPhysician;
import com.scnsoft.eldermark.util.DataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public abstract class TreatmentViewDataConverter<TP extends TreatingPhysicianViewData,
        TH extends TreatingHospitalViewData,
        T extends TreatmentViewData<TP, TH>> implements Converter<Event, T> {

    @Autowired
    private Converter<EventAddress, AddressDto> addressDtoConverter;

    @Override
    public T convert(Event event) {
        var treatmentDetails = create();
        var physician = event.getEventTreatingPhysician();
        if (physician != null) {
            var physicianDto = createPhysician();

            fillPhysician(physician, physicianDto, treatmentDetails);

            treatmentDetails.setPhysician(physicianDto);
        }

        var hospital = event.getEventTreatingHospital();
        if (hospital != null) {
            var hospitalDto = createHospital();

            fillHospital(hospital, hospitalDto, treatmentDetails);

            treatmentDetails.setHospital(hospitalDto);
        }

        if (DataUtils.hasData(treatmentDetails)) {
            return treatmentDetails;
        }

        return null;
    }

    protected void fillPhysician(EventTreatingPhysician physician, TP physicianDto, T treatmentDetails) {
        physicianDto.setFullName(physician.getFullName());
        physicianDto.setAddress(addressDtoConverter.convert(physician.getEventAddress()));
        physicianDto.setPhone(physician.getPhone());
    }

    protected void fillHospital(EventTreatingHospital hospital, TH hospitalDto, T treatmentDetails) {
        hospitalDto.setName(hospital.getName());
        hospitalDto.setAddress(addressDtoConverter.convert(hospital.getEventAddress()));
        hospitalDto.setPhone(hospital.getPhone());
    }

    protected abstract T create();

    protected abstract TP createPhysician();

    protected abstract TH createHospital();
}
