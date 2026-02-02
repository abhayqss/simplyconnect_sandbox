package com.scnsoft.eldermark.services.palatiumcare;

import java.io.Serializable;
import java.util.List;

public interface CrudService <E, D> {

    E save(D dtoItem);

    void remove(Long id) throws Exception;

    D get(Long id);

    List<D> getList();

}
