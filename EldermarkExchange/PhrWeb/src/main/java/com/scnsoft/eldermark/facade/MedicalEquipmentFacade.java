package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.web.entity.MedicalEquipmentDto;
import com.scnsoft.eldermark.web.entity.MedicalEquipmentInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicalEquipmentFacade {

    Page<MedicalEquipmentInfoDto> getMedicalEquipmentsForUser(Long userId, Pageable pageable);

    Page<MedicalEquipmentInfoDto> getMedicalEquipmentsForReceiver(Long receiverId, Pageable pageable);

    MedicalEquipmentDto getMedicalEquipment(Long medicalEquipmentId);
}
