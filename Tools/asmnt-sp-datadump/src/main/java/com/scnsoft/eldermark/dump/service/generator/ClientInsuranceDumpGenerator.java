package com.scnsoft.eldermark.dump.service.generator;

import com.scnsoft.eldermark.dump.bean.DumpFilter;
import com.scnsoft.eldermark.dump.dao.ClientDao;
import com.scnsoft.eldermark.dump.model.ClientInsuranceDump;
import com.scnsoft.eldermark.dump.model.ClientInsuranceInfo;
import com.scnsoft.eldermark.dump.model.Dump;
import com.scnsoft.eldermark.dump.model.DumpType;
import com.scnsoft.eldermark.dump.specification.ClientSpecificationGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ClientInsuranceDumpGenerator implements DumpGenerator {

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private ClientSpecificationGenerator clientSpecifications;

    @Override
    public List<Dump> generateDump(DumpFilter filter) {
        var dump = new ClientInsuranceDump();

        dump.setClientInsuranceInfoList(createInsurance(filter));
        return Collections.singletonList(dump);
    }

    private List<ClientInsuranceInfo> createInsurance(DumpFilter filter) {
        var clients = clientDao.findAll(clientSpecifications.byOrganizationId(filter.getOrganizationId()));
        return clients.stream()
                .map(client -> {
                    var demographic = new ClientInsuranceInfo();

                    DumpGeneratorUtils.fillClientBaseInfo(demographic, client);
                    DumpGeneratorUtils.fillInsuranceInfo(demographic, client);

                    return demographic;
                }).collect(Collectors.toList());
    }

    @Override
    public DumpType getDumpType() {
        return DumpType.CLIENT_INSURANCE;
    }
}
