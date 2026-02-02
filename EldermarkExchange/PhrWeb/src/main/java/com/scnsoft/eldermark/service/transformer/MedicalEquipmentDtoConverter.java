package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.MedicalEquipment;
import com.scnsoft.eldermark.service.DataSourceService;
import com.scnsoft.eldermark.web.entity.MedicalEquipmentDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MedicalEquipmentDtoConverter implements Converter<MedicalEquipment, MedicalEquipmentDto> {
    @Override
    public MedicalEquipmentDto convert(MedicalEquipment medicalEquipment) {
        final MedicalEquipmentDto medicalEquipmentDto = new MedicalEquipmentDto();
        medicalEquipmentDto.setId(medicalEquipment.getId());
        medicalEquipmentDto.setSupplyDevice(medicalEquipment.getProductInstance() != null ? medicalEquipment.getProductInstance().getDeviceCode().getDisplayName() : null);
        medicalEquipmentDto.setDateSupplied(medicalEquipment.getEffectiveTimeHigh() != null ? medicalEquipment.getEffectiveTimeHigh().getTime() : null);
        medicalEquipmentDto.setStatus(medicalEquipment.getStatusCode());
        medicalEquipmentDto.setQuantity(medicalEquipment.getQuantity());
        medicalEquipmentDto.setDataSource(DataSourceService.transform(medicalEquipment.getDatabase(), medicalEquipment.getResident().getId()));
        return medicalEquipmentDto;
    }
}
