package com.scnsoft.eldermark.mapper;

import java.util.List;

public interface ToDtoMapper  <E, D> {

    D entityToDto(E entity);

    List<D> entityListToDtoList(List<E> entityList);
}
