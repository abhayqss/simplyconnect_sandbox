package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.client.ClientPrimaryContact;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientPrimaryContactDao extends AppJpaRepository<ClientPrimaryContact, Long> {
}
