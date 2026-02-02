package com.scnsoft.eldermark.dump.service.generator;

import com.scnsoft.eldermark.dump.bean.DumpFilter;
import com.scnsoft.eldermark.dump.dao.AdmittanceHistoryDao;
import com.scnsoft.eldermark.dump.dao.ClientDao;
import com.scnsoft.eldermark.dump.entity.*;
import com.scnsoft.eldermark.dump.model.*;
import com.scnsoft.eldermark.dump.specification.ClientSpecificationGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ClientDemographicsDumpGenerator implements DumpGenerator {

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private AdmittanceHistoryDao admittanceHistoryDao;

    @Autowired
    private ClientSpecificationGenerator clientSpecifications;

    @Override
    public List<Dump> generateDump(DumpFilter filter) {
        var dump = new ClientDemographicsDump();
        dump.setDemographicList(createDemographics(filter));
        return Collections.singletonList(dump);
    }

    private List<ClientDemographicInfo> createDemographics(DumpFilter filter) {
        var clients = clientDao.findAll(clientSpecifications.byOrganizationId(filter.getOrganizationId()));
        return clients.stream()
                .map(client -> {
                    var demographic = new ClientDemographicInfo();
                    DumpGeneratorUtils.fillClientBaseInfo(demographic, client);

                    demographic.setIntakeDate(client.getIntakedate());
                    demographic.setAdmitDate(getLatestAdmitDate(client));
                    demographic.setBirthDate(client.getBirthDate());
                    demographic.setGender(Optional.ofNullable(client.getGender()).map(DisplayableNamedEntity::getDisplayName).orElse(null));
                    demographic.setAddress(DumpGeneratorUtils.convertFirstAddress(client.getPerson().getAddresses()));
                    demographic.setRace(Optional.ofNullable(client.getRace()).map(CcdCode::getDisplayName).orElse(null));

                    return demographic;
                }).collect(Collectors.toList());
    }


    private Instant getLatestAdmitDate(Client source) {
        if (source.getCommunity() != null) {
            var admittanceHistory = admittanceHistoryDao.findByClient_IdAndCommunityId(source.getId(), source.getCommunity().getId());

            var admits = admittanceHistory.stream().map(AdmittanceHistory::getAdmitDate).filter(Objects::nonNull).collect(Collectors.toCollection(TreeSet::new));
            Optional.ofNullable(source.getAdmitDate()).ifPresent(admits::add);

            if (admits.descendingIterator().hasNext()) {
                return admits.descendingIterator().next();
            }
        }
        return null;
    }

    @Override
    public DumpType getDumpType() {
        return DumpType.CLIENT_DEMOGRAPHICS;
    }
}
