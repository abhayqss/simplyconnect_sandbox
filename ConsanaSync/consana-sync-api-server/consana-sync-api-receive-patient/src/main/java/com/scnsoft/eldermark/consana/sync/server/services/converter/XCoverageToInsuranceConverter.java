package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.model.entity.ConsanaResidentInsurance;
import com.scnsoft.eldermark.consana.sync.server.model.fhir.XCoverage;
import com.scnsoft.eldermark.consana.sync.server.model.fhir.XCoverageClass;
import org.hl7.fhir.instance.model.StringType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Optional.ofNullable;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class XCoverageToInsuranceConverter {

    public ConsanaResidentInsurance convert(XCoverage coverage) {
        return ofNullable(coverage)
                .map(XCoverage::getClass_)
                .map(this::setResidentInsuranceAndPlan)
                .orElse(null);
    }

    private ConsanaResidentInsurance setResidentInsuranceAndPlan(XCoverageClass coverageClass){
        String insuranceName = ofNullable(coverageClass.getName()).map(StringType::toString).orElse(null);
        String insurancePlanCode = ofNullable(coverageClass.getValue()).map(StringType::toString).orElse(null);

        if (insuranceName != null || insurancePlanCode != null){
            ConsanaResidentInsurance consanaResidentInsurance = new ConsanaResidentInsurance();
            consanaResidentInsurance.setInNetworkInsuranceName(insuranceName);
            consanaResidentInsurance.setInsurancePlanCode(insurancePlanCode);
            return consanaResidentInsurance;
        }
        return null;
    }

}
