package com.scnsoft.eldermark.dump.service.tabs;

import com.scnsoft.eldermark.dump.bean.DumpFilter;
import com.scnsoft.eldermark.dump.dao.ClientAssessmentResultDao;
import com.scnsoft.eldermark.dump.dao.ClientDao;
import com.scnsoft.eldermark.dump.model.GAD7PHQ9Scoring;
import com.scnsoft.eldermark.dump.specification.ClientAssessmentResultSpecificationGenerator;
import com.scnsoft.eldermark.dump.specification.ClientSpecificationGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class Gad7PHQ9ScoringGenerator {
//
//    @Autowired
//    private ClientDao clientDao;
//
//    @Autowired
//    private ClientSpecificationGenerator clientSpecifications;
//
//    @Autowired
//    private ClientAssessmentResultDao clientAssessmentResultDao;
//
//    @Autowired
//    private ClientAssessmentResultSpecificationGenerator assessmentResultSpecifications;
//
//    @Autowired
//    private
//
//
//    public List<GAD7PHQ9Scoring> gad7PHQ9Scoring(DumpFilter configuration) {
//        return clientDao.findAll(clientSpecifications.byIds(configuration))
//                .stream()
//                .map(client -> {
//                    var scoring = new GAD7PHQ9Scoring();
//                    scoring.setResidentId(client.getId());
//                    scoring.setFirstName(client.getFirstName());
//                    scoring.setLastName(client.getLastName());
//                    scoring.setOrganization(client.getOrganization().getName());
//                    scoring.setCommunity(client.getCommunity().getName());
//                    scoring.setGad7scores(
//                            clientAssessmentResultDao.findAll(assessmentResultSpecifications.gad7OfClient(client))
//                                    .stream()
//                                    .map(assessmentScoringService::calculateScore)
//                                    .collect(Collectors.toList()));
//
//                    scoring.setPhq9Scores(
//                            clientAssessmentResultDao.findAll(assessmentResultSpecifications.phq9OfClient(client))
//                                    .stream()
//                                    .map(assessmentScoringService::calculateScore)
//                                    .collect(Collectors.toList())
//                    );
//                    scoring.setActive(client.isActive());
//                    return scoring;
//                })
//                .collect(Collectors.toList());
//    }
}
