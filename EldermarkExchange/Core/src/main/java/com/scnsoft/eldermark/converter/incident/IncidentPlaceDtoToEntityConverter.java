package com.scnsoft.eldermark.converter.incident;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import com.scnsoft.eldermark.dao.incident.IncidentPlaceTypeDao;
import com.scnsoft.eldermark.dto.dictionary.TextDto;
import com.scnsoft.eldermark.entity.incident.FreeText;
import com.scnsoft.eldermark.entity.incident.IncidentReport;
import com.scnsoft.eldermark.entity.incident.IncidentReportIncidentPlaceTypeFreeText;

@Component
public class IncidentPlaceDtoToEntityConverter implements Converter<TextDto, IncidentReportIncidentPlaceTypeFreeText> {

    @Autowired
    IncidentPlaceTypeDao incidentPlaceTypeDao;

    @Override
    public IncidentReportIncidentPlaceTypeFreeText convert(TextDto source) {
        IncidentReportIncidentPlaceTypeFreeText target = new IncidentReportIncidentPlaceTypeFreeText();
        target.setIncidentPlaceType(incidentPlaceTypeDao.getOne(source.getId()));
        target.setFreeText(StringUtils.isNotEmpty(source.getText()) ? new FreeText(source.getText()) : null);
        return target;
    }

    public List<IncidentReportIncidentPlaceTypeFreeText> convertList(List<TextDto> sourceList,
            IncidentReport incidentReport) {
        List<IncidentReportIncidentPlaceTypeFreeText> targetList = new ArrayList<>();
        for (TextDto sourceItem : sourceList) {
            IncidentReportIncidentPlaceTypeFreeText targetItem = convert(sourceItem);
            targetItem.setIncidentReport(incidentReport);
            targetList.add(targetItem);
        }
        return targetList;
    }
}
