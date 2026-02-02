package com.scnsoft.eldermark.mapper;

import java.util.List;

public interface ToEntityMapper  <E, D> {

    E dtoToEntity(D dto);

    List<E> dtoListToEntityList(List<D> dtoList);

}
