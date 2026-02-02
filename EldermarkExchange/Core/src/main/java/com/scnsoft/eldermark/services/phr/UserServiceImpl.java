package com.scnsoft.eldermark.services.phr;

import com.scnsoft.eldermark.dao.phr.AuthTokenDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.entity.phr.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private AuthTokenDao authTokenDao;
    
    @Autowired
    private UserDao userDao;

    @Override
    @Transactional(readOnly = true)
    public boolean isActiveMobileUser(Long userId) {
        return authTokenDao.hasActiveByUserMobileId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userDao.findOne(userId);
    }
}
