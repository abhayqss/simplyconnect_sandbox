package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.PersonAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonAddressDao extends JpaRepository<PersonAddress, Long>, CustomPersonAddressDao {
    List<PersonAddress> findByPerson_Id(Long personId);
}
