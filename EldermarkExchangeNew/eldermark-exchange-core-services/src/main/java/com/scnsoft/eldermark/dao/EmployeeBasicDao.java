package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.EmployeeBasic;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeBasicDao extends AppJpaRepository<EmployeeBasic, Long> {

}
