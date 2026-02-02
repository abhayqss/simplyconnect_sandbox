package com.scnsoft.eldermark.dao.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.NotifyResident;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotifyResidentDao extends CrudRepository<NotifyResident, Long> {}

