package com.scnsoft.exchange.audit.model.filters;


public class SyncStatsReportFilter extends ReportFilterDto {
    private Boolean showDetails;

    public Boolean getShowDetails() {
        return showDetails;
    }

    public void setShowDetails(Boolean showDetails) {
        this.showDetails = showDetails;
    }

    public <V> V getCriteria(FilterBy filterBy, Class<V> valueClass) {
        V superClassRetVal = super.getCriteria(filterBy, valueClass);
        if (superClassRetVal != null)
            return superClassRetVal;

        switch (filterBy) {
            case SHOW_DETAILS:
                return valueClass.cast(getShowDetails());
            default:
                return null;
        }
    }
}
