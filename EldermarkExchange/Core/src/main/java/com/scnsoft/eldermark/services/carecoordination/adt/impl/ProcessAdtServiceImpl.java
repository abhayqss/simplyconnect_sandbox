package com.scnsoft.eldermark.services.carecoordination.adt.impl;

import com.scnsoft.eldermark.dao.JpaAdmittanceHistoryDao;
import com.scnsoft.eldermark.dao.carecoordination.AdtMessageDao;
import com.scnsoft.eldermark.entity.AdmittanceHistory;
import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.entity.xds.message.PIDSegmentContainingMessage;
import com.scnsoft.eldermark.entity.xds.message.PV1SegmentContainingMessage;
import com.scnsoft.eldermark.entity.xds.segment.PIDPatientIdentificationSegment;
import com.scnsoft.eldermark.entity.xds.segment.PV1PatientVisitSegment;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.services.carecoordination.adt.ProcessAdtService;
import com.scnsoft.eldermark.shared.carecoordination.AdtDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ProcessAdtServiceImpl implements ProcessAdtService {

    @Autowired
    private AdtMessageDao adtMessageDao;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private JpaAdmittanceHistoryDao admittanceHistoryDao;

    //TODO careCoordinationResident should be manually saved now after the call of this method. Refactor update of resident fields on receiving adt message
    @Override
    public void processAdmitDischargeDates(final AdtDto adtDto, final CareCoordinationResident careCoordinationResident) {
        final Long msgId = adtDto.getMsgId();
        final AdtMessage adt =  adtMessageDao.findOne(msgId);
        if (!(adt instanceof PV1SegmentContainingMessage)) {
            return;
        }
        final PV1SegmentContainingMessage adtMessage = (PV1SegmentContainingMessage)adt;
        final PV1PatientVisitSegment patientVisitSegment = adtMessage.getPv1();

        //update resident's admit/discharge date fields
        final Date adtDischargeDatetime = patientVisitSegment.getDischargeDatetime();
        final Date latestExistingDischargeDate = careCoordinationResident.getDischargeDate();
        if (adtDischargeDatetime != null) {
            if (latestExistingDischargeDate == null || latestExistingDischargeDate.before(adtDischargeDatetime)) {
                careCoordinationResident.setDischargeDate(adtDischargeDatetime);
            }
        }
        final Date adtAdmitDateTime = patientVisitSegment.getAdmitDatetime();
        final Date latestExistingAdmitDate = careCoordinationResident.getAdmitDate();
        if (adtAdmitDateTime != null)  {
            if (latestExistingAdmitDate == null || latestExistingAdmitDate.before(adtAdmitDateTime)) {
                careCoordinationResident.setAdmitDate(adtAdmitDateTime);
            }
        }

        //create/update resident admittance history record
        AdmittanceHistory admittanceHistory = null;
        if (adtAdmitDateTime != null) {
            admittanceHistory = admittanceHistoryDao.getByResidentIdAndAdmitDate(careCoordinationResident.getId(), adtAdmitDateTime);
        }
        if (admittanceHistory == null && adtDischargeDatetime != null) {
            admittanceHistory = admittanceHistoryDao.getByResidentIdAndDischargeDate(careCoordinationResident.getId(), adtDischargeDatetime);
        }
        if (admittanceHistory == null) {
            admittanceHistory = new AdmittanceHistory();
        }

        if (adtAdmitDateTime != null) {
            admittanceHistory.setAdmitDate(patientVisitSegment.getAdmitDatetime());
        }
        if (adtDischargeDatetime != null) {
            admittanceHistory.setDischargeDate(patientVisitSegment.getDischargeDatetime());
        }
        final Database database = careCoordinationResident.getDatabase();
        admittanceHistory.setDatabase(database);
        admittanceHistory.setOrganizationId(careCoordinationResident.getFacility() != null ? careCoordinationResident.getFacility().getId() : null);
        if (database != null) {
            admittanceHistory.setDatabaseId(database.getId());
        }
        admittanceHistory.setResident(getResidentService().convert(careCoordinationResident));
        admittanceHistoryDao.save(admittanceHistory);
    }

    //TODO careCoordinationResident should be manually saved now after the call of this method. Refactor
    @Override
    public void processDeathDate(AdtDto adtDto, CareCoordinationResident careCoordinationResident) {
        final Long msgId = adtDto.getMsgId();
        final AdtMessage adt =  adtMessageDao.findOne(msgId);
        if (!(adt instanceof PIDSegmentContainingMessage)) {
            return;
        }
        final PIDSegmentContainingMessage adtMessage = (PIDSegmentContainingMessage)adt;
        final PIDPatientIdentificationSegment patientIdentificationSegment = adtMessage.getPid();

        if (patientIdentificationSegment.getPatientDeathDateAndTime() != null) {
            careCoordinationResident.setDeathDate(patientIdentificationSegment.getPatientDeathDateAndTime());
        }
    }

    public AdtMessageDao getAdtMessageDao() {
        return adtMessageDao;
    }

    public void setAdtMsgDao(final AdtMessageDao adtMsgDao) {
        this.adtMessageDao = adtMsgDao;
    }

    public JpaAdmittanceHistoryDao getAdmittanceHistoryDao() {
        return admittanceHistoryDao;
    }

    public void setAdmittanceHistoryDao(final JpaAdmittanceHistoryDao admittanceHistoryDao) {
        this.admittanceHistoryDao = admittanceHistoryDao;
    }

    public ResidentService getResidentService() {
        return residentService;
    }

    public void setResidentService(final ResidentService residentService) {
        this.residentService = residentService;
    }
}
