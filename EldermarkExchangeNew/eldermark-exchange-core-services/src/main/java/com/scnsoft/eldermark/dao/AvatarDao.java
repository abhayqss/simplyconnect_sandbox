package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.IdProjectionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.Avatar;

@Repository
public interface AvatarDao extends JpaRepository<Avatar, Long>, IdProjectionRepository<Long> {
}
