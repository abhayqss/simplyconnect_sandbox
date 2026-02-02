package com.scnsoft.eldermark.hl7v2.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.InNetworkInsurance;
import com.scnsoft.eldermark.hl7v2.entity.HL7InsuranceMapping;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HL7InsuranceMappingDao extends AppJpaRepository<HL7InsuranceMapping, String> {

    @Query("Select m.inNetworkInsurance from HL7InsuranceMapping m where m.hl7InsuranceName = :name")
    Optional<InNetworkInsurance> findInsuranceByMappedName(@Param("name") String name);

}
