package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.MpiMergedResidents;

import java.util.List;

public interface MPIMergedResidentsDao extends BaseDao<MpiMergedResidents> {
    List<Long> listProbablyMatchedResidents(Long residentId);
    MpiMergedResidents insertAutomerged(Long survivingResidentId, Long mergedResidentId, boolean probablyMatched, Double confidence);
    void deleteMergesBetween(Long survivingResidentId, Long mergedResidentId);
}
