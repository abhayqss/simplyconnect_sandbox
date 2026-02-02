package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface HL7CodeTableDao extends JpaRepository<HL7CodeTable, Long>, JpaSpecificationExecutor<HL7CodeTable> {

    default <T extends HL7CodeTable> Optional<T> findByCodeAndType(String code, Class<T> type) {
        return findOne((root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(HL7CodeTable_.code), code),
                        criteriaBuilder.equal(root.type(), type)
                )).map(type::cast);
    }
}
