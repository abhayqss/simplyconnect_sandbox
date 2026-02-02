package com.scnsoft.eldermark.dump.model;

import java.util.List;

public class ClientDemographicsDump extends Dump {

    private List<ClientDemographicInfo> demographicList;

    public List<ClientDemographicInfo> getDemographicList() {
        return demographicList;
    }

    public void setDemographicList(List<ClientDemographicInfo> demographicList) {
        this.demographicList = demographicList;
    }

    @Override
    public DumpType getDumpType() {
        return DumpType.CLIENT_DEMOGRAPHICS;
    }
}
