package com.scnsoft.eldermark.facades.ccd;

import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.shared.ccd.CcdHeaderDetailsDto;
import com.scnsoft.eldermark.shared.ccd.CcdHeaderPatientDto;
import com.scnsoft.eldermark.shared.ccd.CcdSectionDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CcdFacade {
    <T extends CcdSectionDto> List<T> getCcdSectionDto(String sectionName, Long residentId, Pageable pageable, Boolean aggregated);

    long getCcdSectionDtoCount(String sectionName, Long residentId, Boolean aggregated);

    CcdHeaderDetailsDto getCcdHeaderDetails(Long residentId, Boolean aggregated);

    CcdHeaderPatientDto getCcdHeaderPatient(Long residentId, boolean showSsn);

    boolean validateSectionName(String sectionName);

    boolean canAddCcd(Long residentId);

    boolean canEditCcd(Long residentId);

    boolean canViewCcd(Long residentId);

    boolean canDeleteCcd(Long residentId);

    boolean canAddCcd(Resident resident);

    boolean canEditCcd(Resident resident);

    boolean canViewCcd(Resident resident);

    boolean canDeleteCcd(Resident resident);

    void canAddCcdOrThrow(Long residentId);

    void canEditCcdOrThrow(Long residentId);

    void canViewCcdOrThrow(Long residentId);

    void canDeleteCcdOrThrow(Long residentId);

    String findFreeTextBySectionAndId(String sectionName, Long id);

}
