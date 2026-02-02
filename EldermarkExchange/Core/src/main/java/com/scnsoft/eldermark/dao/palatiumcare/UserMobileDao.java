package com.scnsoft.eldermark.dao.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.UserMobile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("notifyUserMobileDao")
public interface UserMobileDao extends CrudRepository<UserMobile, Long> {


}
