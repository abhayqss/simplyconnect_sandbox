package com.scnsoft.eldermark.facade.impl;

import com.scnsoft.eldermark.entity.MedicalEquipment;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.facade.BasePhrFacade;
import com.scnsoft.eldermark.facade.MedicalEquipmentFacade;
import com.scnsoft.eldermark.service.MedicalEquipmentService;
import com.scnsoft.eldermark.web.entity.MedicalEquipmentDto;
import com.scnsoft.eldermark.web.entity.MedicalEquipmentInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class MedicalEquipmentFacadeImpl extends BasePhrFacade implements MedicalEquipmentFacade {

    @Autowired
    private MedicalEquipmentService medicalEquipmentService;

    @Autowired
    private Converter<MedicalEquipment, MedicalEquipmentInfoDto> medicalEquipmentListItemDtoConverter;

    @Autowired
    private Converter<MedicalEquipment, MedicalEquipmentDto> medicalEquipmentDtoConverter;

    @Override
    public Page<MedicalEquipmentInfoDto> getMedicalEquipmentsForUser(Long userId, Pageable pageable) {
        return medicalEquipmentService.getMedicalEquipments(getUserResidentIds(userId, AccessRight.Code.MY_PHR), pageable)
                .map(medicalEquipmentListItemDtoConverter);
    }

    @Override
    public Page<MedicalEquipmentInfoDto> getMedicalEquipmentsForReceiver(Long receiverId, Pageable pageable) {
        return medicalEquipmentService.getMedicalEquipments(getReceiverResidentIds(receiverId, AccessRight.Code.MY_PHR), pageable)
                .map(medicalEquipmentListItemDtoConverter);
    }

    @Override
    public MedicalEquipmentDto getMedicalEquipment(Long medicalEquipmentId) {
        final MedicalEquipment medicalEquipment = medicalEquipmentService.getMedicalEquipment(medicalEquipmentId);
        validateAssociation(medicalEquipment.getResident().getId(), AccessRight.Code.MY_PHR);
        return medicalEquipmentDtoConverter.convert(medicalEquipment);
    }
}
