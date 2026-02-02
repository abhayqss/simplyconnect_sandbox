package com.scnsoft.eldermark.dump.service.generator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.dump.bean.DumpFilter;
import com.scnsoft.eldermark.dump.dao.AssessmentDao;
import com.scnsoft.eldermark.dump.dao.ClientAssessmentResultDao;
import com.scnsoft.eldermark.dump.dao.ClientDao;
import com.scnsoft.eldermark.dump.entity.Client;
import com.scnsoft.eldermark.dump.entity.Community;
import com.scnsoft.eldermark.dump.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.dump.model.Dump;
import com.scnsoft.eldermark.dump.model.DumpType;
import com.scnsoft.eldermark.dump.model.RawComprehensiveAssessmentDump;
import com.scnsoft.eldermark.dump.model.RawComprehensiveAssessmentDumpEntry;
import com.scnsoft.eldermark.dump.model.assessment.AssessmentStructure;
import com.scnsoft.eldermark.dump.specification.ClientAssessmentResultSpecificationGenerator;
import com.scnsoft.eldermark.dump.util.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RawComprehensiveAssessmentDumpGenerator implements DumpGenerator {

    private static final TypeReference<Map<String, Object>> MAP_STRING_OBJECT
            = new TypeReference<>() {
    };

    private static final Sort SORT = Sort.by("client.firstName", "client.lastName", "dateCompleted");


    @Autowired
    private ClientAssessmentResultDao assessmentResultDao;

    @Autowired
    private AssessmentDao assessmentDao;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private ClientAssessmentResultSpecificationGenerator assessmentResultSpecificationGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<Dump> generateDump(DumpFilter filter) {
        try {
            var assessmentResults = loadAssessmentResults(filter);

            var structure = objectMapper.readValue(assessmentDao.findByCode("COMPREHENSIVE").orElseThrow().getContent(), AssessmentStructure.class);
            var dumps = new ArrayList<Dump>();
            switch (filter.getFileMode()) {
                case ONE_PER_COMMUNITY:
                    var communityNames = assessmentResults.stream()
                            .map(ClientAssessmentResult::getClient)
                            .map(Client::getCommunity)
                            .collect(StreamUtils.toMapOfUniqueKeysAndThen(Community::getId, Community::getName));

                    System.out.println("Total of " + communityNames.size() + " communities");
                    var assessmentsByCommunity = assessmentResults.stream()
                            .collect(Collectors.groupingBy(clientAssessmentResult -> clientAssessmentResult.getClient().getCommunity().getId()));

                    for (var entry : assessmentsByCommunity.entrySet()) {
                        var communityName = communityNames.get(entry.getKey());
                        dumps.add(createDump(entry.getValue(), structure, communityName));
                    }
                    break;
                case SINGLE:
                    dumps.add(createDump(assessmentResults, structure, null));
                    break;
                default:
                    throw new RuntimeException("Unknown FileType " + filter.getFileMode());
            }
            return dumps;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ClientAssessmentResult> loadAssessmentResults(DumpFilter filter) {
        var completedInOrg = assessmentResultSpecificationGenerator.comprehensiveCompletedByOrganizationId(filter.getOrganizationId());
        var withinPeriod = assessmentResultSpecificationGenerator.withinReportPeriod(filter.getFrom(), filter.getTo());
        var latest = assessmentResultSpecificationGenerator.leaveLatest(filter.getTo());

        return assessmentResultDao.findAll(completedInOrg.and(withinPeriod).and(latest), SORT);
    }

    private Dump createDump(List<ClientAssessmentResult> assessmentResults, AssessmentStructure assessmentStructure, String metaInfo) throws IOException {
        var dump = new RawComprehensiveAssessmentDump();
        dump.setMetaInformation(metaInfo);
        dump.setAssessmentStructure(assessmentStructure);

        var entries = new ArrayList<RawComprehensiveAssessmentDumpEntry>(assessmentResults.size());
        for (var assessmentResult : assessmentResults) {
            var entry = new RawComprehensiveAssessmentDumpEntry();
            var client = assessmentResult.getClient();
            entry.setCommunityName(client.getCommunity().getName());
            entry.setClientId(client.getId());
            entry.setClientName(client.getFullName());
            entry.setDateCompleted(assessmentResult.getDateCompleted());
            var responses = objectMapper.readValue(assessmentResult.getResult(), MAP_STRING_OBJECT);
            entry.setResponses(responses);
            entries.add(entry);
        }
        dump.setEntries(entries);
        return dump;
    }

    @Override
    public DumpType getDumpType() {
        return DumpType.RAW_COMPREHENSIVE_ASSESSMENT;
    }
}
