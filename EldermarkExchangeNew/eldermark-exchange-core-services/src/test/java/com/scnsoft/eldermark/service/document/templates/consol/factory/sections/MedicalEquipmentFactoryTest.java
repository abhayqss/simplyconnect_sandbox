package com.scnsoft.eldermark.service.document.templates.consol.factory.sections;

import com.scnsoft.eldermark.entity.document.ccd.MedicalEquipment;
import com.scnsoft.eldermark.entity.document.ccd.ProductInstance;
import com.scnsoft.eldermark.util.TestUtil;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.eclipse.mdht.uml.cda.ParticipantRole;
import org.eclipse.mdht.uml.cda.Supply;
import org.eclipse.mdht.uml.hl7.datatypes.IVL_TS;
import org.junit.jupiter.api.Test;
import org.openhealthtools.mdht.uml.cda.consol.MedicalEquipmentSection;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MedicalEquipmentFactoryTest {
    private static final long RESIDENT_ID = 49L;

    @Test
    public void testBuildingTemplate() {
        // 1. init
        MedicalEquipment medicalEquipmentMock = new MedicalEquipment();
        medicalEquipmentMock.setId(123L);
        medicalEquipmentMock.setStatusCode("statusCode");
        medicalEquipmentMock.setEffectiveTimeHigh(new Date());
        medicalEquipmentMock.setMoodCode("EVN");
        medicalEquipmentMock.setQuantity(1);

        ProductInstance productInstanceMock = new ProductInstance();
        productInstanceMock.setDeviceCode(TestUtil.createCcdCodeMock());
        productInstanceMock.setId(256L);
        productInstanceMock.setScopingEntityId("scopingEntityId");
        medicalEquipmentMock.setProductInstance(productInstanceMock);

        List<MedicalEquipment> medicalEquipmentMocks = new ArrayList<>();
        medicalEquipmentMocks.add(medicalEquipmentMock);

        /*
        MedicalEquipmentDao daoMock = EasyMock.createMock(MedicalEquipmentDao.class);
        EasyMock.expect(daoMock.listByResidentId(RESIDENT_ID, false));
        EasyMock.expectLastCall().andReturn(new HashSet<>(medicalEquipmentMocks));
        EasyMock.replay(daoMock);*/

        MedicalEquipmentFactory medicalEquipmentFactory = new MedicalEquipmentFactory();
        // dependency on DAO is not required for parsing anymore
        //medicalEquipmentFactory.setMedicalEquipmentDao(daoMock);

        // 2. test
        final MedicalEquipmentSection section = medicalEquipmentFactory.buildTemplateInstance(medicalEquipmentMocks);

        // 3. verify
        assertNotNull(section);
        //EasyMock.verify(daoMock);

        assertEquals("2.16.840.1.113883.10.20.22.2.23", section.getTemplateIds().get(0).getRoot());
        assertEquals("46264-8", section.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", section.getCode().getCodeSystem());

        List<Supply> supplies = section.getSupplies();
        assertNotNull(supplies);

        Supply supply = supplies.get(0);
        assertNotNull(supply);
        assertEquals("SPLY", supply.getClassCode().toString());
        assertEquals(medicalEquipmentMock.getMoodCode(), supply.getMoodCode().getName());
        assertEquals("2.16.840.1.113883.10.20.22.4.50", supply.getTemplateIds().get(0).getRoot());
        assertEquals(medicalEquipmentMock.getId().toString(), supply.getIds().get(0).getExtension());
        assertEquals(medicalEquipmentMock.getStatusCode(), supply.getStatusCode().getCode());
        assertEquals(CcdUtils.formatSimpleDate(medicalEquipmentMock.getEffectiveTimeHigh()), ((IVL_TS) supply.getEffectiveTimes().get(0)).getHigh().getValue());
        assertEquals(BigInteger.valueOf(medicalEquipmentMock.getQuantity()), supply.getQuantity().getValue().toBigInteger());

        assertNotNull(supply.getParticipants());

        ParticipantRole participantRole = supply.getParticipants().get(0).getParticipantRole();
        assertEquals("PRD", supply.getParticipants().get(0).getTypeCode().getName());
        assertEquals(productInstanceMock.getId().toString(), participantRole.getIds().get(0).getExtension());
        assertEquals("MANU", participantRole.getClassCode().getName());
        assertEquals("2.16.840.1.113883.10.20.22.4.37", participantRole.getTemplateIds().get(0).getRoot());
        assertEquals(productInstanceMock.getDeviceCode().getCode(), participantRole.getPlayingDevice().getCode().getCode());
        assertEquals(productInstanceMock.getDeviceCode().getCodeSystem(), participantRole.getPlayingDevice().getCode().getCodeSystem());
        assertEquals(productInstanceMock.getScopingEntityId(), participantRole.getScopingEntity().getIds().get(0).getExtension());
    }
}
