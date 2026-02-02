package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.DemoRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemoRequestDao extends JpaRepository<DemoRequest, Long> {
}
