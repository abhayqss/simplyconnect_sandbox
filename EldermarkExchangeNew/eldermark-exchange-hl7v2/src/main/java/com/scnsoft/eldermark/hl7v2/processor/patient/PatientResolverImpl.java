package com.scnsoft.eldermark.hl7v2.processor.patient;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.hl7v2.model.PatientIdentifiersHolder;
import com.scnsoft.eldermark.hl7v2.model.PersonIdentifier;
import com.scnsoft.eldermark.hl7v2.processor.patient.mpi.HL7v2MpiService;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PatientResolverImpl implements PatientResolver {
    private static final Logger logger = LoggerFactory.getLogger(PatientResolverImpl.class);


    @Autowired
    private HL7v2MpiService hl7v2MpiService;

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> resolvePatient(PatientIdentifiersHolder patientIdentifiersHolder, MessageSource messageSource) {
        //todo add organization restrictions based on integration partner?
        var prioritized = messageSource.getHl7v2IntegrationPartner().prioritizePatientIdentifiersForSearch(patientIdentifiersHolder);
        logger.info("Prioritized ids for client search {}", prioritized);

        for (var pid : prioritized) {
            var mpi = PersonIdentifier.createMPIFromPersonIdentifier(pid);
            var foundMPI = hl7v2MpiService.findMPI(mpi);
            if (foundMPI != null) {
//                if (foundMPI.getMerged().equals("Y")) {
//                    foundMPI.setMerged("N");
//                    hl7v2MpiService.save(foundMPI);
//                    //TODO unmerge
//                    //2022 edit regarding previous comment: why would we need to unmerge?
//                }

                logger.info("Found MPI {}", foundMPI.getRegistryPatientId());
                if (foundMPI.getClient() != null) {
                    return Optional.of(foundMPI.getClient());
                }
            }
        }

        return Optional.empty();
    }

}
