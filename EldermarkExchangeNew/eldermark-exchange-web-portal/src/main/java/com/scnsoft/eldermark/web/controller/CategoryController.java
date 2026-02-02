package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.document.category.DocumentCategoryDto;
import com.scnsoft.eldermark.facade.document.category.DocumentCategoryFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private DocumentCategoryFacade documentCategoryFacade;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<DocumentCategoryDto>> findAll(@RequestParam Long organizationId, Pageable pageable) {
        return Response.pagedResponse(documentCategoryFacade.findByOrganizationId(organizationId, pageable));
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> save(@Valid @ModelAttribute DocumentCategoryDto documentCategoryDto) {
        var id = documentCategoryDto.getId() == null
                ? documentCategoryFacade.add(documentCategoryDto)
                : documentCategoryFacade.edit(documentCategoryDto);
        return Response.successResponse(id);
    }

    @DeleteMapping(value = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> deleteById(@PathVariable Long categoryId) {
        documentCategoryFacade.deleteById(categoryId);
        return Response.successResponse();
    }

    @GetMapping(value = "/validate-uniq", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> validateUniqueness(
            @RequestParam(required = false) Long categoryId,
            @RequestParam Long organizationId,
            @RequestParam String name
    ) {
        return Response.successResponse(documentCategoryFacade.validateUniqueInOrganization(categoryId, organizationId, name));
    }

    @GetMapping(value = "/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canAdd(@RequestParam Long organizationId) {
        return Response.successResponse(documentCategoryFacade.canAdd(organizationId));
    }

    @GetMapping(value = "/can-view", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canView(@RequestParam Long organizationId) {
        return Response.successResponse(documentCategoryFacade.canViewList(organizationId));
    }
}
