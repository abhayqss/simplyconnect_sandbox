package com.scnsoft.eldermark.facades;

import com.scnsoft.eldermark.shared.ResidentDto;
import com.scnsoft.eldermark.shared.ResidentFilter;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ResidentFacade {
    public List<ResidentDto> getResidents(ResidentFilter filter, Pageable pageable, boolean showSsn);

    public List<ResidentDto> getResidents(ResidentFilter filter);

    public Long getResidentCount(ResidentFilter filter);

    public ResidentDto getDefaultResident();

    public ResidentDto getResidentById(long residentId);

    List<ResidentDto> getResidentsByIds(Collection<Long> residentIds);

    Collection<ResidentDto> getMergedResidentsById(long residentId);

    List<ResidentDto> getProbablyMatchedResidentsById(long residentId);

    void updateMatchedResidents(Map<Long, Boolean> residents);

    public boolean assertHashKey(long residentId, String hashKey);
}