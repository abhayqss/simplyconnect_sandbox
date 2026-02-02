package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.InformationRecipient;
import org.springframework.stereotype.Repository;

@Repository
public class InformationRecipientDaoImpl extends ResidentAwareDaoImpl<InformationRecipient> implements InformationRecipientDao {

    public InformationRecipientDaoImpl() {
        super(InformationRecipient.class);
    }

}
