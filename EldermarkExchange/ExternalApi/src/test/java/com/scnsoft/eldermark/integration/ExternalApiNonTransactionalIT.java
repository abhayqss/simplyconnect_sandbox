package com.scnsoft.eldermark.integration;

import com.scnsoft.eldermark.services.carecoordination.OrganizationService;
import com.scnsoft.eldermark.shared.carecoordination.OrganizationDto;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import net.sf.ehcache.CacheManager;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

@Ignore("ehcache temporary disabled")
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "h2")
@SpringBootTest
public class ExternalApiNonTransactionalIT {

	@Autowired
	private OrganizationService organizationService;

	@Test
	@WithMockUser(roles = "SUPER_ADMINISTRATOR")
	public void hibernateL2CacheIsWorking() {
		OrganizationDto dto = new OrganizationDto();
		dto.setName(TestDataGenerator.randomName());

		dto = organizationService.create(dto, false);
		try {
            dto = organizationService.getOrganization(dto.getId());
            dto = organizationService.getOrganization(dto.getId());

            int size = CacheManager.ALL_CACHE_MANAGERS.get(0)
                    .getCache("database").getSize();
            assertThat(size, greaterThan(0));
        } finally {
		    if (dto != null && dto.getId() != null) {
                organizationService.deleteOrganization(dto.getId());
            }
        }
	}

}
