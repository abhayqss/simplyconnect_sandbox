package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.specification.ClientExtApiSpecifications;
import com.scnsoft.eldermark.api.external.web.dto.ConsanaXrefPatientIdDto;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
class ConsanaClientXrefIdResolverImpl implements ConsanaClientXrefIdResolver {

    private static final Logger logger = LoggerFactory.getLogger(ConsanaClientXrefIdResolverImpl.class);

    private final ClientExtApiSpecifications clientExtApiSpecifications;
    private final ClientSpecificationGenerator clientSpecifications;
    private final ClientDao clientDao;

    @Autowired
    public ConsanaClientXrefIdResolverImpl(ClientExtApiSpecifications clientExtApiSpecifications,
                                           ClientSpecificationGenerator clientSpecifications, ClientDao clientDao) {
        this.clientExtApiSpecifications = clientExtApiSpecifications;
        this.clientSpecifications = clientSpecifications;
        this.clientDao = clientDao;
    }

    @Override
    public Optional<Long> resolveClientId(ConsanaXrefPatientIdDto consanaXrefPatientIdDto) {
        var byXrefDto = clientExtApiSpecifications.byConsanaXrefDto(consanaXrefPatientIdDto);
        var enabledForCommunity = clientExtApiSpecifications.clientCommunityConsanaSyncEnabled(true);
        var optIn = clientSpecifications.isOptedIn();

        var spec = byXrefDto.and(enabledForCommunity).and(optIn);
        var list = clientDao.findAll(spec, IdAware.class);

        if (list.size() > 1) {
            logger.warn("more than one client matches xref dto {}", consanaXrefPatientIdDto);
        }

        return list.stream().map(IdAware::getId).findFirst();
    }
}
