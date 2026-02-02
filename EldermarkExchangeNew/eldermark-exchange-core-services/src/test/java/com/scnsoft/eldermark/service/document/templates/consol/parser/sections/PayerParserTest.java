package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.ccd.Payer;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.Participant2Factory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.Performer2Factory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.PayersSection;

import java.util.List;
import java.util.Random;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@ExtendWith(MockitoExtension.class)
public class PayerParserTest {

    @Mock
    private CcdCodeFactory ccdCodeFactory;
    @Mock
    private Performer2Factory performer2Factory;
    @Mock
    private Participant2Factory participant2Factory;
    @InjectMocks
    private PayerParser payerParser;

    private final Random random = new Random();
    private final Organization database = new Organization();
    private final Client resident = new Client(random.nextLong());
    {
        database.setId(random.nextLong());
        resident.setOrganization(database);
    }


    @Test
    public void testParseSection() throws Exception {
        final PayersSection section = ConsolFactory.eINSTANCE.createPayersSection();

        /*
        when(ccdCodeFactory.convert(any(CD.class))).then(MockitoAnswers.returnConvertedCode());
        when(performer2Factory.parsePayers(any(), any(), any())).thenReturn(Arrays.<Performer2Factory.PayerWrapper>asList(new Performer2Factory.PayerWrapper()));
        when(performer2Factory.parseGuarantors(any(), any(), any())).thenReturn(Arrays.<Performer2Factory.GuarantorWrapper>asList(new Performer2Factory.GuarantorWrapper()));
        when(participant2Factory.parseCoverageTarget(any(), any(), any(), any())).thenReturn(new Participant(Long.valueOf(1)));
        when(participant2Factory.parseHolder(any(), any(), any(), any())).thenReturn(new Participant(Long.valueOf(1)));
        */

        final List<Payer> result = payerParser.parseSection(resident, section);

        // TODO more validations
        //Assert.assertEquals(Arrays.<Payer>asList(new Payer(Long.valueOf(1))), result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme