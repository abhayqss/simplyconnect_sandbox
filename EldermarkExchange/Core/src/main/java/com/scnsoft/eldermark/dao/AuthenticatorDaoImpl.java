package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Authenticator;
import org.springframework.stereotype.Repository;

@Repository
public class AuthenticatorDaoImpl extends ResidentAwareDaoImpl<Authenticator> implements AuthenticatorDao {

    public AuthenticatorDaoImpl() {
        super(Authenticator.class);
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (Authenticator authenticator : this.listByResidentId(residentId)) {
            this.delete(authenticator);
            ++count;
        }

        return count;
    }

}
