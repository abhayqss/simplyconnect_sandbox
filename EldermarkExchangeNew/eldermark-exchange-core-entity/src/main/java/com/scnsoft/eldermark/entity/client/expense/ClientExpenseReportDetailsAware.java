package com.scnsoft.eldermark.entity.client.expense;

import com.scnsoft.eldermark.beans.projection.AuthorIdNamesAware;
import com.scnsoft.eldermark.beans.projection.ClientActiveAware;
import com.scnsoft.eldermark.beans.projection.ClientCommunityIdNameAware;
import com.scnsoft.eldermark.beans.projection.ClientIdNamesAware;

import java.time.Instant;

public interface ClientExpenseReportDetailsAware
        extends AuthorIdNamesAware, ClientIdNamesAware, ClientCommunityIdNameAware, ClientActiveAware, CostAware, ExpenseTypeAware {

    Long getCumulativeCost();

    String getComment();

    Instant getDate();

    Instant getReportedDate();
}
