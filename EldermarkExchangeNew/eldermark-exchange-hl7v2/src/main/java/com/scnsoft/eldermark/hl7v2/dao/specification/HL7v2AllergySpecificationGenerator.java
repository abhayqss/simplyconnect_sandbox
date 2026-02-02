package com.scnsoft.eldermark.hl7v2.dao.specification;

import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.document.ccd.Allergy;
import com.scnsoft.eldermark.entity.document.ccd.Allergy_;
import com.scnsoft.eldermark.entity.document.facesheet.AllergyObservation_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class HL7v2AllergySpecificationGenerator {

    public Specification<Allergy> isExists(Allergy allergy) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            var firstAllergyObservation = allergy.getAllergyObservations().iterator().next();
            var allergyObservationsJoin = JpaUtils.getOrCreateSetJoin(root, Allergy_.allergyObservations);

            var sameClient = criteriaBuilder.equal(root.get(Allergy_.clientId), allergy.getClientId());

            var sameProductCode = HL7v2SpecificationUtils.compareCcdCode(
                    allergyObservationsJoin.get(AllergyObservation_.productCode),
                    firstAllergyObservation.getProductCode(),
                    criteriaBuilder
            );

            var sameProductText = HL7v2SpecificationUtils.compareString(allergyObservationsJoin.get(AllergyObservation_.productText),
                    firstAllergyObservation.getProductText(),
                    criteriaBuilder
            );

            var sameAllergenTypeCode = HL7v2SpecificationUtils.compareCcdCode(
                    allergyObservationsJoin.get(AllergyObservation_.adverseEventTypeCode),
                    firstAllergyObservation.getAdverseEventTypeCode(),
                    criteriaBuilder
            );

            return criteriaBuilder.and(
                    sameClient,
                    sameProductCode,
                    sameProductText,
                    sameAllergenTypeCode
            );
        });
    }
}
