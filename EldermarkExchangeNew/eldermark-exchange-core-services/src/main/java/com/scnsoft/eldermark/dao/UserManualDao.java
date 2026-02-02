package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.UserManual;
import org.springframework.stereotype.Repository;

@Repository
public interface UserManualDao extends AppJpaRepository<UserManual, Long> {
}
