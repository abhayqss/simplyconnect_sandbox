package com.scnsoft.eldermark.converter.incident;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import com.scnsoft.eldermark.dao.incident.IncidentTypeDao;
import com.scnsoft.eldermark.dto.dictionary.TextDto;
import com.scnsoft.eldermark.entity.incident.FreeText;
import com.scnsoft.eldermark.entity.incident.IncidentReport;
import com.scnsoft.eldermark.entity.incident.IncidentReportIncidentTypeFreeText;

@Component
public class IncidentTypeDtoToEntityConverter implements Converter<TextDto, IncidentReportIncidentTypeFreeText> {

    @Autowired
    private IncidentTypeDao incidentTypeDao;

    @Override
    public IncidentReportIncidentTypeFreeText convert(TextDto source) {
        IncidentReportIncidentTypeFreeText target = new IncidentReportIncidentTypeFreeText();
        target.setIncidentType(incidentTypeDao.getOne(source.getId()));
        target.setFreeText(StringUtils.isNotEmpty(source.getText()) ? new FreeText(source.getText()) : null);
        return target;
    }

    public List<IncidentReportIncidentTypeFreeText> convertList(List<TextDto> sourceList,
            IncidentReport incidentReport) {
        List<IncidentReportIncidentTypeFreeText> targetList = new ArrayList<>();
        for (TextDto sourceItem : sourceList) {
            IncidentReportIncidentTypeFreeText targetItem = convert(sourceItem);
            targetItem.setIncidentReport(incidentReport);
            targetList.add(targetItem);
        }
        return targetList;
    }

}
