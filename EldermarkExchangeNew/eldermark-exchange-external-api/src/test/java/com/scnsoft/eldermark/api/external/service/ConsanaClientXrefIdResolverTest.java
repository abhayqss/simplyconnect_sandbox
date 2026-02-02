package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.specification.ClientExtApiSpecifications;
import com.scnsoft.eldermark.api.external.web.dto.ConsanaXrefPatientIdDto;
import com.scnsoft.eldermark.api.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.entity.Client;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsanaClientXrefIdResolverTest {

    @Mock
    private ClientExtApiSpecifications clientExtApiSpecifications;

    @Mock
    private ClientSpecificationGenerator clientSpecificationGenerator;

    @Mock
    private ClientDao clientDao;

    @InjectMocks
    private ConsanaClientXrefIdResolverImpl resolver;

    private static final String IDENTIFIER = "identifier";
    private static final String ORGANIZATION_OID = "organizationOid";
    private static final String COMMUNITY_OID = "communityoid";

    @Test
    void resolveClientId_NoClients_ReturnsEmpty() {
        var input = new ConsanaXrefPatientIdDto(IDENTIFIER, ORGANIZATION_OID, COMMUNITY_OID);

        var spec = setUpConsanaSpecification(input);
        when(clientDao.findAll(spec, IdAware.class)).thenReturn(Collections.emptyList());

        var actual = resolver.resolveClientId(input);

        assertThat(actual).isEmpty();
    }

    @Test
    void resolveClientId_OneClient_ReturnsClientId() {
        var input = new ConsanaXrefPatientIdDto(IDENTIFIER, ORGANIZATION_OID, COMMUNITY_OID);
        var clientId = TestDataGenerator.randomId();

        var spec = setUpConsanaSpecification(input);
        when(clientDao.findAll(spec, IdAware.class)).thenReturn(Collections.singletonList(() -> clientId));

        var actual = resolver.resolveClientId(input);

        assertThat(actual).contains(clientId);
    }

    private Specification<Client> setUpConsanaSpecification(ConsanaXrefPatientIdDto dto) {
        Specification<Client> byDto = Mockito.mock(Specification.class);
        Specification<Client> integrationEnabled = Mockito.mock(Specification.class);
        Specification<Client> spec1 = Mockito.mock(Specification.class);

        Specification<Client> optIn = Mockito.mock(Specification.class);
        Specification<Client> spec = Mockito.mock(Specification.class);

        when(clientExtApiSpecifications.byConsanaXrefDto(dto)).thenReturn(byDto);
        when(clientExtApiSpecifications.clientCommunityConsanaSyncEnabled(true)).thenReturn(integrationEnabled);
        when(clientSpecificationGenerator.isOptedIn()).thenReturn(optIn);
        when(byDto.and(integrationEnabled)).thenReturn(spec1);
        when(spec1.and(optIn)).thenReturn(spec);

        return spec;
    }
}