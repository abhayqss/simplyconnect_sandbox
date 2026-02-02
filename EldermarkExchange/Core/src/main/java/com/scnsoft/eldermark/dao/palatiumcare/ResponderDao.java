package com.scnsoft.eldermark.dao.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.Responder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("notifyResponderDao")
public interface ResponderDao extends CrudRepository<Responder, Long> {}
