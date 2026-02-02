package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.phr.AccountType;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.entity.phr.UserAccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author phomal
 * Created on 5/10/2017.
 */
@Repository
public interface UserAccountTypeDao extends JpaRepository<UserAccountType, Long> {

    UserAccountType findByUserAndCurrentIsTrue(User user);

    List<UserAccountType> findByUser(User user);

    List<UserAccountType> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE UserAccountType u set u.current = false where u.user = :user")
    int resetCurrentAccountType(@Param("user") User user);

    @Modifying
    @Query("UPDATE UserAccountType u set u.current=true where u.user = :user AND u.accountType = :accountType")
    int setCurrentAccountType(@Param("user") User user, @Param("accountType") AccountType accountType);

}
