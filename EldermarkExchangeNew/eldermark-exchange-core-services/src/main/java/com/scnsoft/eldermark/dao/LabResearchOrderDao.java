package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.IdProjectionRepository;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface LabResearchOrderDao extends JpaRepository<LabResearchOrder, Long>,
        JpaSpecificationExecutor<LabResearchOrder>,
        CustomLabResearchOrderDao,
        IdProjectionRepository<Long> {

    @Query("select case when count(o)> 0 then true else false end from LabResearchOrder o join o.client c where o.requisitionNumber=:requisitionNumber and c.organizationId = :organizationId")
    Boolean existsRequisitionNumberInOrganization(@Param("requisitionNumber") String requisitionNumber, @Param("organizationId") Long organizationId);

    Optional<LabResearchOrder> findFirstByClientIdAndRequisitionNumberAndStatus(Long clientId, String requisitionNumber,
                                                                                LabResearchOrderStatus status);

    Optional<LabResearchOrder> findFirstByClientIdAndSpecimenDateBeforeAndIsCovid19IsTrue(Long clientId, Instant instant);
}
