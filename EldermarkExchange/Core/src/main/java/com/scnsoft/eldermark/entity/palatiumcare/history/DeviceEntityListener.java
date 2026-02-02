package com.scnsoft.eldermark.entity.palatiumcare.history;

import com.scnsoft.eldermark.entity.palatiumcare.Device;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.transaction.Transactional;

import static com.scnsoft.eldermark.entity.palatiumcare.history.Action.*;
import static javax.transaction.Transactional.TxType.MANDATORY;


public class DeviceEntityListener {

    private EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @PrePersist
    public void prePersist(Device target) {
        perform(target, INSERTED);
    }

    @PreUpdate
    public void preUpdate(Device target) {
        perform(target, UPDATED);
    }

    @PreRemove
    public void preRemove(Device target) {
        perform(target, DELETED);
    }

    @Transactional(MANDATORY)
    void perform(Device target, Action action) {
        entityManager.persist(new DeviceHistory(target, action));
    }

}