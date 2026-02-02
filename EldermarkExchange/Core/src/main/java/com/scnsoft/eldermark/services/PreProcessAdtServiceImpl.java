package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.entity.xds.message.DG1ListSegmentContainingMessage;
import com.scnsoft.eldermark.entity.xds.segment.AdtDG1DiagnosisSegment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PreProcessAdtServiceImpl implements PreProcessAdtService {

    @Override
    public AdtMessage preprocessMessage(AdtMessage adtMessage) {
        if (adtMessage instanceof DG1ListSegmentContainingMessage) {
            removeDG1Duplicates(adtMessage);
        }
        return adtMessage;
    }

    public void removeDG1Duplicates(AdtMessage adtMessage){
        List<AdtDG1DiagnosisSegment> adtDG1DiagnosisSegments = ((DG1ListSegmentContainingMessage) adtMessage).getDg1List();
        Map<String, AdtDG1DiagnosisSegment> uniqueDiagnosisMap = new HashMap<>();
        for (AdtDG1DiagnosisSegment diagnosisSegment : adtDG1DiagnosisSegments){
            if (diagnosisSegment.getDiagnosisCode() != null &&
                diagnosisSegment.getDiagnosisCode().getIdentifier() != null    &&
                uniqueDiagnosisMap.get(diagnosisSegment.getDiagnosisCode().getIdentifier()) == null){

                    uniqueDiagnosisMap.put(diagnosisSegment.getDiagnosisCode().getIdentifier(), diagnosisSegment);
            }
        }
        List<AdtDG1DiagnosisSegment> uniqueDiagnosisList = new ArrayList<>(uniqueDiagnosisMap.values());
        adtDG1DiagnosisSegments.removeAll(adtDG1DiagnosisSegments);
        adtDG1DiagnosisSegments.addAll(uniqueDiagnosisList);
    }

}
