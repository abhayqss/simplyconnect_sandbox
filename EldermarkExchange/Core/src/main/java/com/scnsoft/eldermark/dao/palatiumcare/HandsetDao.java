package com.scnsoft.eldermark.dao.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.Handset;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("notifyHandsetDao")
public interface HandsetDao extends CrudRepository<Handset, Long> {}
