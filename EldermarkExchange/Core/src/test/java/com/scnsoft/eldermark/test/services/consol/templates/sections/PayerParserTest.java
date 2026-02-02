package com.scnsoft.eldermark.test.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.Participant2Factory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.Performer2Factory;
import com.scnsoft.eldermark.services.consol.templates.sections.PayerParser;
import com.scnsoft.eldermark.test.util.MockitoAnswers;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.PayersSection;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@RunWith(MockitoJUnitRunner.class)
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
    private final Database database = new Database();
    private final Resident resident = new Resident(random.nextLong());
    {
        database.setId(random.nextLong());
        resident.setDatabase(database);
    }


    @Test
    public void testParseSection() throws Exception {
        final PayersSection section = ConsolFactory.eINSTANCE.createPayersSection();

        when(ccdCodeFactory.convert(any(CD.class))).then(MockitoAnswers.returnConvertedCode());
        /*
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