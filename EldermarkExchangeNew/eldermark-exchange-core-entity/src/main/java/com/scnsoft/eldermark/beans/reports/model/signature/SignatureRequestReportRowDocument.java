package com.scnsoft.eldermark.beans.reports.model.signature;

import java.util.List;

public class SignatureRequestReportRowDocument {
    private String templateName;
    private List<SignatureRequestReportRowAction> actionRows;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public List<SignatureRequestReportRowAction> getActionRows() {
        return actionRows;
    }

    public void setActionRows(List<SignatureRequestReportRowAction> actionRows) {
        this.actionRows = actionRows;
    }
}
