package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.ccd.SocialHistory;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.SocialHistorySection;

import java.util.List;
import java.util.Random;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@ExtendWith(MockitoExtension.class)
public class SocialHistoryParserTest {

    @Mock
    private CcdCodeFactory ccdCodeFactory;
    @InjectMocks
    private SocialHistoryParser socialHistoryParser;

    private final Random random = new Random();
    private final Organization organization = new Organization();
    private final Client resident = new Client(random.nextLong());
    {
        organization.setId(random.nextLong());
        resident.setOrganization(organization);
    }

    @Test
    public void testParseSection() throws Exception {
        final SocialHistorySection section = ConsolFactory.eINSTANCE.createSocialHistorySection();

//        when(ccdCodeFactory.convert(Matchers.any(CD.class))).then(MockitoAnswers.returnConvertedCode());

        final List<SocialHistory> result = socialHistoryParser.parseSection(resident, section);

        // TODO more validations
        //Assert.assertEquals(Arrays.<SocialHistory>asList(new SocialHistory(Long.valueOf(1))), result);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme