package com.scnsoft.eldermark.dao.basic;

import org.springframework.data.repository.core.RepositoryInformation;

public interface RepositoryInformationAware {
    void setRepositoryInformation(RepositoryInformation information);
}
