package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.client.expense.ClientExpenseDto;
import com.scnsoft.eldermark.dto.client.expense.ClientExpenseListItemDto;
import com.scnsoft.eldermark.facade.client.expense.ClientExpenseFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients/{clientId}/expenses")
public class ClientExpenseController {

    @Autowired
    private ClientExpenseFacade clientExpenseFacade;

    @PostMapping
    public Response<Long> create(@PathVariable Long clientId, @ModelAttribute ClientExpenseDto expense) {
        expense.setClientId(clientId);
        return Response.successResponse(clientExpenseFacade.create(expense));
    }

    @GetMapping
    public Response<List<ClientExpenseListItemDto>> find(@PathVariable Long clientId, Pageable pageable) {
        return Response.pagedResponse(clientExpenseFacade.find(clientId, pageable));
    }

    @GetMapping("/count")
    public Response<Long> count(@PathVariable Long clientId) {
        return Response.successResponse(clientExpenseFacade.count(clientId));
    }

    @GetMapping("/{expenseId}")
    public Response<ClientExpenseDto> getById(@PathVariable Long clientId, @PathVariable Long expenseId) {
        return Response.successResponse(clientExpenseFacade.findById(expenseId));
    }

    @GetMapping("/total")
    public Response<Long> getTotalCost(@PathVariable Long clientId) {
        return Response.successResponse(clientExpenseFacade.getTotalCost(clientId));
    }

    @GetMapping("/can-view")
    public Response<Boolean> canView(@PathVariable Long clientId) {
        return Response.successResponse(clientExpenseFacade.canViewList(clientId));
    }

    @GetMapping("/can-add")
    public Response<Boolean> canAdd(@PathVariable Long clientId) {
        return Response.successResponse(clientExpenseFacade.canAdd(clientId));
    }
}
