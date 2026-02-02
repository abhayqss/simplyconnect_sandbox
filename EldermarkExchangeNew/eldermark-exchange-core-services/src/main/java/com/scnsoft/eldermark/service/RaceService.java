package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.event.incident.Race;

public interface RaceService {

    Race getById(Long id);

    Race findByCodeAndCodeSystem(String code, String codeSystem);
}
