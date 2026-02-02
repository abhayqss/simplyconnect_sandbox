package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.service.DirectoryService;
import com.scnsoft.eldermark.service.StateService;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledEntityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DirectoryFacadeImpl implements DirectoryFacade {

    @Autowired
    private DirectoryService directoryService;

    @Override
    public List<IdentifiedNamedTitledEntityDto> getStates() {

        return directoryService.getStates()
                .map(state -> new IdentifiedNamedTitledEntityDto(
                        state.getId(),
                        state.getAbbr(),
                        state.getName()
                ))
                .collect(Collectors.toList());
    }
}
