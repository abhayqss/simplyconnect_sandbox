package com.scnsoft.eldermark.dao.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.HandsetHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HandsetHistoryDao extends CrudRepository<HandsetHistory, Long> {}