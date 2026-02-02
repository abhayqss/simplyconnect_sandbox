package com.scnsoft.eldermark.services.phr;

import com.scnsoft.eldermark.entity.phr.User;

public interface UserService {

    boolean isActiveMobileUser(Long userId);

    User getUserById(Long userId);

}
