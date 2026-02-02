package com.scnsoft.eldermark.hl7v2.processor.insurance;

import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.InNetworkInsuranceDao;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.InNetworkInsurance;
import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.entity.xds.message.IN1ListSegmentContainingMessage;
import com.scnsoft.eldermark.entity.xds.segment.IN1InsuranceSegment;
import com.scnsoft.eldermark.hl7v2.dao.HL7InsuranceMappingDao;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class HL7v2InsuranceServiceImpl implements HL7v2InsuranceService {
    private static final Logger logger = LoggerFactory.getLogger(HL7v2InsuranceServiceImpl.class);

    @Autowired
    private InNetworkInsuranceDao inNetworkInsuranceDao;

    @Autowired
    private HL7InsuranceMappingDao hl7InsuranceMappingDao;

    @Autowired
    private ClientDao clientDao;

    @Override
    @Transactional
    public void updateInsurances(Client client, AdtMessage adtMessage) {
        logger.info("Updating client {} insurance", client.getId());

        if (client.getInNetworkInsurance() != null) {
            logger.info("Won't update incurance: Client already has insurance.");
            return;
        }

        if (!(adtMessage instanceof IN1ListSegmentContainingMessage)) {
            logger.info("Adt message is not of type {}", IN1ListSegmentContainingMessage.class.getSimpleName());
            return;
        }

        var in1List = ((IN1ListSegmentContainingMessage) adtMessage).getIn1List();

        if (CollectionUtils.isEmpty(in1List)) {
            logger.info("Message doesn't contain insurances");
            return;
        }

        var in1 = in1List.get(0);
        if (in1.getInsuranceCompanyName() == null ||
                StringUtils.isEmpty(in1.getInsuranceCompanyName().getOrganizationName())
        ) {
            logger.info("Insurance name is empty in IN1");
            return;
        }

        var insuranceCompanyName = in1.getInsuranceCompanyName().getOrganizationName();

        var newInsurance = getOrCreateInsurance(insuranceCompanyName, in1);
        client.setInNetworkInsurance(newInsurance);
        client.setInsurancePlan(Optional.ofNullable(in1.getInsurancePlanId()).map(CECodedElement::getText).orElse(null));
        clientDao.save(client);
    }

    private InNetworkInsurance getOrCreateInsurance(String insuranceCompanyName, IN1InsuranceSegment in1) {
        var code = InsuranceUtils.buildInsuranceCode(insuranceCompanyName);
        return findInsurance(insuranceCompanyName, code, in1)
                .orElseGet(() -> createInsurance(insuranceCompanyName, code, in1));
    }

    private Optional<InNetworkInsurance> findInsurance(String insuranceCompanyName, String code, IN1InsuranceSegment in1) {
        return inNetworkInsuranceDao.findFirstByKey(code)
                .or(() -> inNetworkInsuranceDao.findFirstByDisplayName(insuranceCompanyName))
                //in case we have insurance under different name on our side use mapping table where we can save such discrepancies
                .or(() -> hl7InsuranceMappingDao.findInsuranceByMappedName(insuranceCompanyName));
    }

    private InNetworkInsurance createInsurance(String insuranceCompanyName, String code, IN1InsuranceSegment in1) {
        var insurance = new InNetworkInsurance();
        insurance.setIn1InsuranceSegment(in1);
        insurance.setDisplayName(insuranceCompanyName);
        insurance.setKey(code);
        insurance.setPopular(false);
        return inNetworkInsuranceDao.save(insurance);
    }
}
