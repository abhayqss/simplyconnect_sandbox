package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.phr.MobileUserClientRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MobileUserClientRecordDao extends JpaRepository<MobileUserClientRecord, Long> {

    List<MobileUserClientRecord> getAllByClientIn(List<Client> clients);

}
