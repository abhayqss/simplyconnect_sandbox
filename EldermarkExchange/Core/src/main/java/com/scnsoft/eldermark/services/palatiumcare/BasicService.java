package com.scnsoft.eldermark.services.palatiumcare;

import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import com.scnsoft.eldermark.util.EldermarkCollectionUtils;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

//E is for Entity, D is for Dto
@Service
public abstract class BasicService<E, D> implements CrudService<E, D> {

    private GenericMapper<E, D> genericMapper;

    protected abstract GenericMapper<E, D> getMapper();

    protected abstract CrudRepository<E, Long> getCrudRepository();

    @Override
    @Transactional
    public E save(D dtoItem) {
        E entity = getMapper().dtoToEntity(dtoItem);
        if(entity != null) {
            return getCrudRepository().save(entity);
        }
        return null;
    }

    @Override
    public void remove(Long id) throws Exception {
        try {
            getCrudRepository().delete(id);
        }
        catch (Exception exc) {
            throw exc;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public D get(Long id) {
        E item = getCrudRepository().findOne(id);
        D dtoItem = getMapper().entityToDto(item);
        if(dtoItem != null) {
            return dtoItem;
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<D> getList() {
        List<E> list = EldermarkCollectionUtils.listFromIterable(getCrudRepository().findAll());
        return getMapper().entityListToDtoList(list);
    }

}
