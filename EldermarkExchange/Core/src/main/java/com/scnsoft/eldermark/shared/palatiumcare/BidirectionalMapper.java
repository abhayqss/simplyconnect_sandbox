package com.scnsoft.eldermark.shared.palatiumcare;

import java.util.List;

public interface BidirectionalMapper <E, D> {

    D entityToDto(E entity);

    E dtoToEntity(D dto);

    List<E> dtoListToEntityList(List<D> dtoList);

    List<D> entityListToDtoList(List<E> entityList);
}
