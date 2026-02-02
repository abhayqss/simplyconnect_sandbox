package com.scnsoft.eldermark.dao.incident;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.incident.ClassMemberType;

@Repository
public interface ClassMemberTypeDao extends JpaRepository<ClassMemberType, Long>{

}
