package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.mobile.dto.medication.MedicationSearchResultDto;
import com.scnsoft.eldermark.mobile.dto.medication.SearchMedicationFilter;
import com.scnsoft.eldermark.service.medispan.MedicationSearchService;
import com.scnsoft.eldermark.service.medispan.dto.MedicationSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MedicationFacadeImpl implements MedicationFacade {

    @Autowired
    private MedicationSearchService medicationSearchService;

    @Autowired
    private Converter<MedicationSearchResult, MedicationSearchResultDto> medicationSearchResultConverter;

    @Override
    @PreAuthorize("@medicationSecurityService.canViewList()")
    public List<MedicationSearchResultDto> find(SearchMedicationFilter filter) {

        Stream<MedicationSearchResult> results;

        if (filter.getName() != null) {

            var count = Integer.MAX_VALUE;
            var offset = 0;
            if (filter.getSize() != null) {
                count = filter.getSize();
                if (filter.getPage() != null) {
                    offset = filter.getPage() * count;
                }
            }

            results = medicationSearchService.findByName(filter.getName(), count, offset).stream();
        } else if (filter.getNdcCode() != null) {
            results = medicationSearchService.findByNdc(filter.getNdcCode()).stream();
        } else {
            throw new InternalServerException(InternalServerExceptionType.NOT_IMPLEMENTED);
        }

        return results
                .map(medicationSearchResultConverter::convert)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(MedicationSearchResultDto::getName))
                .collect(Collectors.toList());
    }

    @Override
    public MedicationSearchResultDto findByMediSpanId(String mediSpanId) {

        var result = medicationSearchService.findByMediSpanId(mediSpanId)
                .orElseThrow(() -> new BusinessException(BusinessException.NOT_FOUND_CODE));

        return medicationSearchResultConverter.convert(result);
    }
}
