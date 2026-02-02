package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ClientAllergyFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.scnsoft.eldermark.dto.AllergyDto;
import com.scnsoft.eldermark.dto.AllergyListItemDto;

public interface ClientAllergyFacade {
    Long count(ClientAllergyFilter filter);

    AllergyDto findById(Long id);

    Page<AllergyListItemDto> find(ClientAllergyFilter filter, Pageable pageable);
}
