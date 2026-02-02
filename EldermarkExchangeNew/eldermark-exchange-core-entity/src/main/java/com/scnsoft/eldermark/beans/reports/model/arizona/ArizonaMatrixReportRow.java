package com.scnsoft.eldermark.beans.reports.model.arizona;

import java.util.List;

public class ArizonaMatrixReportRow {

    private String communityName;

    private List<ArizonaMatrixReportRowClient> clients;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public List<ArizonaMatrixReportRowClient> getClients() {
        return clients;
    }

    public void setClients(List<ArizonaMatrixReportRowClient> clients) {
        this.clients = clients;
    }
}
