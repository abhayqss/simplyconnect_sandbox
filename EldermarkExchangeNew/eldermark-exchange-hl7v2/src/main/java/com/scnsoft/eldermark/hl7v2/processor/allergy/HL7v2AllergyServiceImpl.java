package com.scnsoft.eldermark.hl7v2.processor.allergy;

import com.scnsoft.eldermark.dao.AllergyDao;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.Allergy;
import com.scnsoft.eldermark.entity.xds.message.AL1ListSegmentContainingMessage;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.hl7v2.dao.specification.HL7v2AllergySpecificationGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class HL7v2AllergyServiceImpl implements HL7v2AllergyService {
    private static final Logger logger = LoggerFactory.getLogger(HL7v2AllergyServiceImpl.class);

    @Autowired
    private HL7v2AllergyFactory allergyFactory;

    @Autowired
    private AllergyDao allergyDao;

    @Autowired
    private HL7v2AllergySpecificationGenerator hl7v2AllergySpecificationGenerator;

    @Override
    public void updateAllergies(Client client, AdtMessage adtMessage) {
        logger.info("Updating client {} allergies...", client.getId());
        if (!(adtMessage instanceof AL1ListSegmentContainingMessage)) {
            logger.info("Won't update allergies: Message is not of type AL1ListSegmentContainingMessage");
            return;
        }

        var al1List = ((AL1ListSegmentContainingMessage) adtMessage).getAL1List();

        if (CollectionUtils.isEmpty(al1List)) {
            logger.info("Won't update allergies: Message has no allergies");
            return;
        }

        var allergiesStream = al1List.stream()
                .map(al1 -> allergyFactory.createAllergy(client, al1, adtMessage))
                .filter(Optional::isPresent)
                .map(Optional::get);

        saveDeduplicated(allergiesStream);
    }

    private void saveDeduplicated(Stream<Allergy> allergiesStream) {
        allergiesStream.forEach(allergy -> {
            if (!isExists(allergy)) {
                //save one by one so that allergies within the same message are also deduplicated
                allergy = allergyDao.save(allergy);

                logger.info("Saved allergy {}", allergy.getId());
            } else {
                logger.info("Duplicate allergy detected");
            }
        });
    }

    private boolean isExists(Allergy allergy) {
        return allergyDao.exists(hl7v2AllergySpecificationGenerator.isExists(allergy));
    }
}
