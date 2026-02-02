package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.phr.MobileUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MobileUserDao extends JpaRepository<MobileUser, Long> {

    MobileUser findByEmployee(Employee employee);

    boolean existsByEmployee(Employee employee);

    List<MobileUser> findAllByClientIn(List<Client> clients);

}
