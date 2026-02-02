package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Encounter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface EncounterService {
    Page<Encounter> getEncountersForResidents(Collection<Long> residentIds, Pageable pageable);

    Encounter getEncounter(Long encounterId);
}
