package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.dao.carecoordination.CareCoordinationResidentDao;
import com.scnsoft.eldermark.dao.carecoordination.EventDao;
import com.scnsoft.eldermark.dao.carecoordination.NoteDao;
import com.scnsoft.eldermark.dao.carecoordination.NoteSubTypeDao;
import com.scnsoft.eldermark.entity.AdmitIntakeResidentDate;
import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.entity.NoteSubType;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteSubTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.scnsoft.eldermark.dao.carecoordination.NoteSubTypeDao.ORDER_BY_DESCRIPTION;
import static com.scnsoft.eldermark.dao.carecoordination.NoteSubTypeDao.ORDER_BY_POSITION;

@Service
@Transactional(readOnly = true)
public class NoteSubTypeServiceImpl implements NoteSubTypeService {
    private static final Logger logger = LoggerFactory.getLogger(NoteSubTypeServiceImpl.class);


    @Autowired
    private NoteSubTypeDao noteSubTypeDao;

    @Autowired
    private NoteDao noteDao;

    @Autowired
    private EventDao eventDao;

    @Autowired
    private CareCoordinationResidentDao careCoordinationResidentDao;

    @Autowired
    private ResidentService residentService;

    private List<NoteSubTypeDto> cache;
    private EnumMap<NoteSubType.FollowUpCode, NoteSubType> mappingByFollowUpCode;
    private Map<Long, NoteSubType> mappingById;
    private NoteSubType other;
    private NoteSubType assessment;

    @PostConstruct
    protected void postConstruct() {
        final List<NoteSubType> subTypeList = noteSubTypeDao.findAll(new Sort(ORDER_BY_POSITION, ORDER_BY_DESCRIPTION));
        cache = new ArrayList<>();
        mappingByFollowUpCode = new EnumMap<>(NoteSubType.FollowUpCode.class);
        mappingById = new HashMap<>();
        for (NoteSubType subType : subTypeList) {
            cache.add(new NoteSubTypeDto(subType.getId(), subType.getDescription(), subType.getFollowUpCode(), subType.getEncounterCode(), subType.isPhrHidden()));
            mappingById.put(subType.getId(), subType);
            if (subType.getFollowUpCode() != null) {
                mappingByFollowUpCode.put(subType.getFollowUpCode(), subType);
            }
            if (subType.getCode().equalsIgnoreCase("OTHER")) {
                other = subType;
            }
            if (subType.getCode().equalsIgnoreCase("ASSESSMENT_NOTE")) {
                assessment = subType;
            }
        }
    }


    public List<NoteSubTypeDto> getAllSubTypes() {
        return new ArrayList<>(cache);
    }

    @Override
    public NoteSubType getByFollowUpCode(NoteSubType.FollowUpCode followUpCode) {
        return mappingByFollowUpCode.get(followUpCode);
    }

    @Override
    public NoteSubType getOtherSubType() {
        return other;
    }

    @Override
    public NoteSubType getById(Long subTypeId) {
        return mappingById.get(subTypeId);
    }

    @Override
    public NoteSubType getAssessmentSubType() {
        return assessment;
    }

    @Override
    public List<NoteSubType.FollowUpCode> getTakenFollowUpForAdmitDate(Long residentId, Long admittanceHistoryId) {
        final CareCoordinationResident careCoordinationResident = careCoordinationResidentDao.get(residentId);
        final List<NoteSubType.FollowUpCode> result = new ArrayList<>();
        for (NoteSubType.FollowUpCode code: NoteSubType.FollowUpCode.values()) {
            if (admittanceHistoryId != 0 && noteDao.countByResident_IdAndSubTypeAndAdmittanceHistory_IdAndArchivedIsFalse(residentId,
                    getByFollowUpCode(code),
                    admittanceHistoryId) > 0) {
                result.add(code);
            //additional check for intake date with hardcoded zero id
            } else if (admittanceHistoryId == 0 && noteDao.countByResident_IdAndSubTypeAndIntakeDateAndArchivedIsFalse(residentId,
                    getByFollowUpCode(code),
                    careCoordinationResident.getIntakeDate()) > 0) {
                result.add(code);
            }
        }
        return result;
    }

    @Override
    public List<NoteSubType.FollowUpCode> getTakenFollowUpForAdmitDateForEvent(Long eventId, Long admittanceHistoryId) {
        return getTakenFollowUpForAdmitDate(eventDao.get(eventId).getResident().getId(), admittanceHistoryId);
    }

    @Override
    public List<Long> getTakenAdmitIntakeHistoryIdForSubType(Long residentId, NoteSubType.FollowUpCode followUpCode) {
        final List<AdmitIntakeResidentDate> admitIntakeHistories = residentService.getAdmitIntakeHistoryFiltered(residentId);
        final List<Long> result = new ArrayList<>();
        for (AdmitIntakeResidentDate admitIntakeHistory : admitIntakeHistories) {
            if (admitIntakeHistory.getId().equals(0L) && noteDao.countByResident_IdAndSubTypeAndIntakeDateAndArchivedIsFalse(residentId,
                    getByFollowUpCode(followUpCode),
                    admitIntakeHistory.getAdmitIntakeDate()) > 0 ||
                    noteDao.countByResident_IdAndSubTypeAndAdmittanceHistory_IdAndArchivedIsFalse(residentId,
                            getByFollowUpCode(followUpCode),
                            admitIntakeHistory.getId()) > 0) {
                result.add(admitIntakeHistory.getId());
            }
        }
        return result;
    }
}
