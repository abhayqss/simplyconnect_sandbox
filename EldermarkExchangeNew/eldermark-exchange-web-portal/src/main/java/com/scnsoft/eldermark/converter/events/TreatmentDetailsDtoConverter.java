package com.scnsoft.eldermark.converter.events;

import com.scnsoft.eldermark.converter.event.base.TreatmentViewDataConverter;
import com.scnsoft.eldermark.dto.events.HospitalDto;
import com.scnsoft.eldermark.dto.events.PhysicianDto;
import com.scnsoft.eldermark.dto.events.TreatmentDto;
import com.scnsoft.eldermark.entity.event.EventTreatingHospital;
import com.scnsoft.eldermark.entity.event.EventTreatingPhysician;
import com.scnsoft.eldermark.util.DataUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class TreatmentDetailsDtoConverter extends TreatmentViewDataConverter<PhysicianDto, HospitalDto, TreatmentDto> {

    @Override
    protected TreatmentDto create() {
        return new TreatmentDto();
    }

    @Override
    protected PhysicianDto createPhysician() {
        return new PhysicianDto();
    }

    @Override
    protected HospitalDto createHospital() {
        return new HospitalDto();
    }

    @Override
    protected void fillPhysician(EventTreatingPhysician physician, PhysicianDto physicianDto, TreatmentDto treatmentDto) {
        super.fillPhysician(physician, physicianDto, treatmentDto);
        physicianDto.setId(physician.getId());
        physicianDto.setFirstName(physician.getFirstName());
        physicianDto.setLastName(physician.getLastName());
        physicianDto.setHasAddress(DataUtils.hasData(physicianDto.getAddress()));
        treatmentDto.setHasPhysician(true);
    }

    @Override
    protected void fillHospital(EventTreatingHospital hospital, HospitalDto hospitalDto, TreatmentDto treatmentDto) {
        super.fillHospital(hospital, hospitalDto, treatmentDto);
        hospitalDto.setId(hospital.getId());
        hospitalDto.setHasAddress(DataUtils.hasData(hospitalDto.getAddress()));
        treatmentDto.setHasHospital(true);
    }
}
