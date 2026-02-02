package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Guardian;
import org.springframework.stereotype.Repository;

@Repository
public class GuardianDaoImpl extends ResidentAwareDaoImpl<Guardian> implements GuardianDao {
    public GuardianDaoImpl() {
        super(Guardian.class);
    }
}
