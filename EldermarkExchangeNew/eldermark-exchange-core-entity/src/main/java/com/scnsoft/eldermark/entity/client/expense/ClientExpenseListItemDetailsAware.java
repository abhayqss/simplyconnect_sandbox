package com.scnsoft.eldermark.entity.client.expense;

import com.scnsoft.eldermark.beans.projection.AuthorIdNamesAware;
import com.scnsoft.eldermark.beans.projection.IdAware;

import java.time.Instant;

public interface ClientExpenseListItemDetailsAware extends IdAware, AuthorIdNamesAware, CostAware, ExpenseTypeAware {

    String getComment();

    Instant getDate();

    Instant getReportedDate();
}
