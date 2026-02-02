package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.entity.EmployeeRequest;
import com.scnsoft.eldermark.entity.EmployeeRequestType;
import com.scnsoft.eldermark.entity.EmployeeRequest_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class EmployeeRequestSpecificationGenerator {

    public Specification<EmployeeRequest> createdBefore(Instant when) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(EmployeeRequest_.createdDateTime), when);
    }

    public Specification<EmployeeRequest> byTokenType(EmployeeRequestType type) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(EmployeeRequest_.tokenType), type);
    }

}
