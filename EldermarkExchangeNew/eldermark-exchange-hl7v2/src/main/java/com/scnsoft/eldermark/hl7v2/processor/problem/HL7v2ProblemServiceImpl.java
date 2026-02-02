package com.scnsoft.eldermark.hl7v2.processor.problem;

import com.scnsoft.eldermark.dao.ProblemDao;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.Problem;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.entity.xds.message.DG1ListSegmentContainingMessage;
import com.scnsoft.eldermark.hl7v2.dao.specification.HL7v2ProblemSpecificationGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional
public class HL7v2ProblemServiceImpl implements HL7v2ProblemService {
    private static final Logger logger = LoggerFactory.getLogger(HL7v2ProblemServiceImpl.class);

    @Autowired
    private HL7v2ProblemFactory hl7v2ProblemFactory;

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private HL7v2ProblemSpecificationGenerator specificationGenerator;

    @Override
    public void updateProblems(Client client, AdtMessage adtMessage) {
        logger.info("Updating client {} problems", client.getId());
        if (!(adtMessage instanceof DG1ListSegmentContainingMessage)) {
            logger.info("Won't update problems: Message is not of type DG1ListSegmentContainingMessage");
            return;
        }

        var dg1list = ((DG1ListSegmentContainingMessage) adtMessage).getDg1List();

        if (CollectionUtils.isEmpty(dg1list)) {
            logger.info("Diagnosis list is empty");
            return;
        }

        var problemsStream = dg1list.stream()
                .map(dg1 -> hl7v2ProblemFactory.createProblem(client, dg1, adtMessage))
                .filter(Optional::isPresent)
                .map(Optional::get);

        saveDeduplicated(problemsStream);
    }

    private void saveDeduplicated(Stream<Problem> problemsStream) {
        problemsStream.forEach(problem -> {
            if (!isExists(problem)) {
                //save one by one so that problems within the same message are also deduplicated
                problem = problemDao.save(problem);

                logger.info("Saved problem {}", problem.getId());
            } else {
                logger.info("Duplicate problem detected");
            }
        });
    }

    private boolean isExists(Problem problem) {
        return problemDao.exists(specificationGenerator.isExists(problem));
    }
}
