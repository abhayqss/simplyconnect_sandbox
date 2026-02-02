package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ClientProblemFilter;
import com.scnsoft.eldermark.dto.ClientProblemDto;
import com.scnsoft.eldermark.dto.ClientProblemListItemDto;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledValueEntityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientProblemFacade {

    Page<ClientProblemListItemDto> find(ClientProblemFilter filter, Pageable pageRequest);

    ClientProblemDto findById(Long id);

    List<NamedTitledValueEntityDto<Long>> countGroupedByStatus(ClientProblemFilter filter);

}
