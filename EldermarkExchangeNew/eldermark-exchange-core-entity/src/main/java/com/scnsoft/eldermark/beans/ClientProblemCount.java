package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.entity.document.ccd.ClientProblemStatus;

public class ClientProblemCount {

    private ClientProblemStatus status;
    private Long count;

    public ClientProblemCount(ClientProblemStatus status, Long count) {
        this.status = status;
        this.count = count;
    }

    public ClientProblemStatus getStatus() {
        return status;
    }

    public void setStatus(ClientProblemStatus status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
