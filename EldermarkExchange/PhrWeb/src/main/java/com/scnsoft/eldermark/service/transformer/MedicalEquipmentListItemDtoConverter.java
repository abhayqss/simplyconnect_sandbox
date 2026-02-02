package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.MedicalEquipment;
import com.scnsoft.eldermark.web.entity.MedicalEquipmentInfoDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MedicalEquipmentListItemDtoConverter implements Converter<MedicalEquipment, MedicalEquipmentInfoDto> {
    @Override
    public MedicalEquipmentInfoDto convert(MedicalEquipment medicalEquipment) {
        final MedicalEquipmentInfoDto medicalEquipmentInfoDto = new MedicalEquipmentInfoDto();
        medicalEquipmentInfoDto.setId(medicalEquipment.getId());
        medicalEquipmentInfoDto.setSupplyDevice(medicalEquipment.getProductInstance() != null ? medicalEquipment.getProductInstance().getDeviceCode().getDisplayName() : null);
        medicalEquipmentInfoDto.setDateSupplied(medicalEquipment.getEffectiveTimeHigh() != null ? medicalEquipment.getEffectiveTimeHigh().getTime() : null);
        return medicalEquipmentInfoDto;
    }
}
