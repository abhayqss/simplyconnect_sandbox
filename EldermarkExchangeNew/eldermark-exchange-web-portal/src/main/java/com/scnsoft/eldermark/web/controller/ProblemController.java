package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.ClientProblemFilter;
import com.scnsoft.eldermark.dto.ClientProblemDto;
import com.scnsoft.eldermark.dto.ClientProblemListItemDto;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledValueEntityDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.facade.ClientProblemFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients/{clientId}/problems")
public class ProblemController {

    @Autowired
    private ClientProblemFacade problemFacade;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ClientProblemListItemDto>> find(@PathVariable(value = "clientId") Long clientId,
                                                         @ModelAttribute ClientProblemFilter problemObservationFilter,
                                                         @PageableDefault(sort = {"identifiedDate"}, direction = Sort.Direction.DESC, size = Integer.MAX_VALUE) Pageable pageRequest) {
        problemObservationFilter.setClientId(clientId);
        return Response.pageResponse(problemFacade.find(problemObservationFilter, pageRequest));
    }

    @GetMapping(value = "/{problemId}")
    public Response<ClientProblemDto> findById(@PathVariable("problemId") Long problemId) {
        return Response.successResponse(problemFacade.findById(problemId));
    }

    @GetMapping(value = "/statistics")
    public Response<List<NamedTitledValueEntityDto<Long>>> countGroupedByStatus(@PathVariable("clientId") Long clientId) {
        var filter = new ClientProblemFilter();
        filter.setClientId(clientId);
        return Response.successResponse(problemFacade.countGroupedByStatus(filter));
    }

}