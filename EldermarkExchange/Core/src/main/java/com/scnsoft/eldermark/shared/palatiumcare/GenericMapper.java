package com.scnsoft.eldermark.shared.palatiumcare;

import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

public abstract class GenericMapper<E, D> implements BidirectionalMapper<E, D> {

    private ModelMapper modelMapper = new ModelMapper();

    protected abstract Class<E> getEntityClass();

    protected abstract Class<D> getDtoClass();

    protected ModelMapper getModelMapper() {
        return modelMapper;
    }

    private D createDto() {
        D dtoItem = null;
        try {
            dtoItem = getDtoClass().newInstance();
        } catch (InstantiationException|IllegalAccessException  e) {
            e.printStackTrace();
        }
        return dtoItem;
    }

    private E createEntity() {
        E item = null;
        try {
            item = getEntityClass().newInstance();
        } catch (InstantiationException|IllegalAccessException  e) {
            e.printStackTrace();
        }
        return item;
    }


    @Override
    public D entityToDto(E entity) {
        if(entity == null) return null;
        D dto = createDto();
        modelMapper.map(entity, dto);
        return dto;
    }

    @Override
    public E dtoToEntity(D dto) {
        if(dto == null) return null;
        E entity = createEntity();
        modelMapper.map(dto, entity);
        return entity;
    }

    @Override
    public List<E> dtoListToEntityList(List<D> dtoList) {
        List<E> entityList = new ArrayList<>();
        for(D dto : dtoList) {
            E entity = createEntity();
            modelMapper.map(dto, entity);
            entityList.add(entity);
        }
        return entityList;
    }

    @Override
    public List<D> entityListToDtoList(List<E> entityList) {
        List<D> dtoList = new ArrayList<>();
        for(E entity : entityList) {
            D dto = createDto();
            modelMapper.map(entity, dto);
            dtoList.add(dto);
        }
        return dtoList;
    }
}
