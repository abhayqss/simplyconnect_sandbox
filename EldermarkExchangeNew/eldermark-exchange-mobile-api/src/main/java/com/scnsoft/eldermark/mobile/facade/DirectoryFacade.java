package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;

import java.util.List;

public interface DirectoryFacade {

    List<IdentifiedNamedTitledEntityDto> getStates();
}
