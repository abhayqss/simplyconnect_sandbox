package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.PhysicianExtendedDto;

public interface PhysiciansService {

    PhysicianExtendedDto get(Long physicianId);
}
