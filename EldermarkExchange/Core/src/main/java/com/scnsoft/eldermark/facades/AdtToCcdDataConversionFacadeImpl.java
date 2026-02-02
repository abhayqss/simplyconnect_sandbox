package com.scnsoft.eldermark.facades;

import com.scnsoft.eldermark.dao.carecoordination.AdtMessageDao;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.services.cda.ClinicalDocumentService;
import com.scnsoft.eldermark.services.cda.ClinicalDocumentVO;
import com.scnsoft.eldermark.services.transformer.adt.toCcd.AdtToCcdDataConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Provider;
import java.util.Date;

@Service
@Transactional
public class AdtToCcdDataConversionFacadeImpl implements AdtToCcdDataConversionFacade {

    @Autowired
    private ResidentService residentService;

    @Autowired
    private AdtMessageDao adtMessageDao;

    @Autowired
    private Provider<AdtToCcdDataConverter> adtToCcdDataConverterProvider;

    @Autowired
    private ClinicalDocumentService clinicalDocumentService;

    @Override
    public void convertAndSave(Long residentId, Long adtMessageId, Date eventDate) {
        final AdtToCcdDataConverter adtToCcdDataConverter = buildConverter(residentId, eventDate);
        final ClinicalDocumentVO clinicalDocumentVO = convert(adtToCcdDataConverter, adtMessageId);
        saveDocumentVO(clinicalDocumentVO);
    }

    private AdtToCcdDataConverter buildConverter(Long residentId, Date eventDate) {
        final Resident resident = fetchResident(residentId);
        return adtToCcdDataConverterProvider.get().withResident(resident).withEventDate(eventDate);
    }

    private Resident fetchResident(Long residentId) {
        return residentService.getResident(residentId);
    }

    private ClinicalDocumentVO convert(AdtToCcdDataConverter converter, Long adtMessageId) {
        final AdtMessage adtMessage = fetchAdtMessage(adtMessageId);
        return converter.convert(adtMessage);
    }

    private AdtMessage fetchAdtMessage(Long adtMessageId) {
        return adtMessageDao.findOne(adtMessageId);
    }

    private void saveDocumentVO(ClinicalDocumentVO clinicalDocumentVO) {
        clinicalDocumentService.saveClinicalDocument(clinicalDocumentVO.getRecordTarget(), clinicalDocumentVO);
    }
}
