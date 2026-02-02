package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.phr.VitalSignReferenceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by averazub on 1/10/2017.
 */
@Repository
public interface VitalSignReferenceInfoDao extends JpaRepository<VitalSignReferenceInfo, Long> {
}
