package com.scnsoft.eldermark.services.transformer.adt.toCcd.impl;

import com.scnsoft.eldermark.entity.AdtMessageAwareEntity;
import com.scnsoft.eldermark.entity.Problem;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.entity.xds.message.DG1ListSegmentContainingMessage;
import com.scnsoft.eldermark.entity.xds.segment.AdtDG1DiagnosisSegment;
import com.scnsoft.eldermark.services.cda.ClinicalDocumentVO;
import com.scnsoft.eldermark.services.transformer.adt.toCcd.AdtToCcdDataConverter;
import com.scnsoft.eldermark.services.transformer.adt.toCcd.segment.Dg1ToProblemConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Scope("prototype")
public class AdtToCcdDataConverterImpl implements AdtToCcdDataConverter {

    private Resident resident;
    private Date eventDate;

    @Autowired
    private Provider<Dg1ToProblemConverter> dg1ToProblemConverterProvider;

    @Override
    public ClinicalDocumentVO convert(AdtMessage adtMessage) {
        final ClinicalDocumentVO result = createClinicalDocument();

        result.setProblems(convertDg1ToProblems(adtMessage));

        return result;
    }

    private ClinicalDocumentVO createClinicalDocument() {
        final ClinicalDocumentVO documentVO = new ClinicalDocumentVO();
        documentVO.setRecordTarget(resident);
        return documentVO;
    }

    private List<Problem> convertDg1ToProblems(AdtMessage adtMessage) {
        final List<AdtDG1DiagnosisSegment> diagnosis = fetchDg1List(adtMessage);
        final Dg1ToProblemConverter converter = buildConverter();
        final List<Problem> problems = converter.convertList(diagnosis);
        populateWithAdtMessage(problems, adtMessage);
        return problems;
    }

    private List<AdtDG1DiagnosisSegment> fetchDg1List(AdtMessage adtMessage) {
        if (adtMessage instanceof DG1ListSegmentContainingMessage) {
            return ((DG1ListSegmentContainingMessage) adtMessage).getDg1List();
        }
        return Collections.emptyList();
    }

    private Dg1ToProblemConverter buildConverter() {
        return dg1ToProblemConverterProvider.get().withResident(resident).withEventDate(eventDate);
    }

    private void populateWithAdtMessage(List<? extends AdtMessageAwareEntity> entities, AdtMessage adtMessage) {
        for(AdtMessageAwareEntity entity: entities) {
            entity.setAdtMessage(adtMessage);
        }
    }


    @Override
    public AdtToCcdDataConverter withResident(Resident resident) {
        this.resident = resident;
        return this;
    }

    @Override
    public AdtToCcdDataConverter withEventDate(Date eventDate) {
        this.eventDate = eventDate;
        return this;
    }
}
