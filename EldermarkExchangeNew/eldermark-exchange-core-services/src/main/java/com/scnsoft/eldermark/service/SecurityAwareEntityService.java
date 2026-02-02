package com.scnsoft.eldermark.service;

import java.util.Collection;
import java.util.List;

public interface SecurityAwareEntityService<T, ID> {

    T findSecurityAwareEntity(ID id);

    List<T> findSecurityAwareEntities(Collection<ID> ids);
}
