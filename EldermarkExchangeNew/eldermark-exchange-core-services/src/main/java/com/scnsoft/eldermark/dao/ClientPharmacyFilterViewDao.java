package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.client.ClientPharmacyFilterView;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientPharmacyFilterViewDao extends AppJpaRepository<ClientPharmacyFilterView, Long> {
}
