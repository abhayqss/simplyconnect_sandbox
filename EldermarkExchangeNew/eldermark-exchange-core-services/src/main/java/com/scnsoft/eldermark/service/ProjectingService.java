package com.scnsoft.eldermark.service;

import java.util.Collection;
import java.util.List;

public interface ProjectingService<ID> {

    <P> P findById(ID id, Class<P> projection);

    <P> List<P> findAllById(Collection<ID> ids, Class<P> projection);
}
