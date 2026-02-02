package com.scnsoft.eldermark.dump.model;

import java.util.List;

public class ClientInsuranceDump extends Dump {

    private List<ClientInsuranceInfo> clientInsuranceInfoList;

    public List<ClientInsuranceInfo> getClientInsuranceInfoList() {
        return clientInsuranceInfoList;
    }

    public void setClientInsuranceInfoList(List<ClientInsuranceInfo> clientInsuranceInfoList) {
        this.clientInsuranceInfoList = clientInsuranceInfoList;
    }

    @Override
    public DumpType getDumpType() {
        return DumpType.CLIENT_INSURANCE;
    }
}
