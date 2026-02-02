package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.phr.UserAvatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author phomal
 * Created on 5/24/2017.
 */
@Repository
public interface UserAvatarDao extends JpaRepository<UserAvatar, Long> {

    UserAvatar getByUserId(Long userId);

    int deleteByUserId(Long userId);

}
