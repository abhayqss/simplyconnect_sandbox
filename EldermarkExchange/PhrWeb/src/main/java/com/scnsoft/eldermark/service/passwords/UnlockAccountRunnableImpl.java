package com.scnsoft.eldermark.service.passwords;

import com.scnsoft.eldermark.dao.password.UserPasswordSecurityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author phomal
 * Created on 11/14/2017.
 */
@Component("UnlockAccountRunnable_User")
@Scope("prototype")
class UnlockAccountRunnableImpl implements UnlockAccountRunnable {

    // FIXME inject userId in constructor to make this class thread-safe
    // FIXED New instances are being obtained via Provider<T>.
    private Long userId;

    private UserPasswordSecurityDao userPasswordSecurityDao;

    @Autowired
    public UnlockAccountRunnableImpl(UserPasswordSecurityDao userPasswordSecurityDao) {
        this.userPasswordSecurityDao = userPasswordSecurityDao;
    }

    @Override
    @Transactional
    public void run() {
        userPasswordSecurityDao.unlockAccount(userId);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
