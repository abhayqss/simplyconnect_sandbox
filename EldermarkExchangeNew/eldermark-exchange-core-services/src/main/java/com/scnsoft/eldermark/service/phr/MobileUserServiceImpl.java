package com.scnsoft.eldermark.service.phr;

import com.scnsoft.eldermark.dao.phr.MobileAuthTokenDao;
import com.scnsoft.eldermark.entity.phr.MobileUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MobileUserServiceImpl implements MobileUserService {

    @Autowired
    private MobileAuthTokenDao phrExternalAuthTokenDao;

    @Override
    public boolean isActiveMobileUser(MobileUser mobileUser) {
        return phrExternalAuthTokenDao.hasActiveByUserMobileId(mobileUser);
    }
}
