package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.event.incident.ClassMemberType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassMemberTypeDao extends JpaRepository<ClassMemberType, Long> {

}
