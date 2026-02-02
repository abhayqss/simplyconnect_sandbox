package com.scnsoft.eldermark.dao.ccd;

import com.scnsoft.eldermark.shared.ccd.CcdSectionDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GenericCcdSectionDao<T extends CcdSectionDto> {
    public List<T> getSectionDto(Long residentId, Pageable pageable, Boolean aggregated);

    public long getSectionDtoCount(Long residentId, Boolean aggregated);
}
