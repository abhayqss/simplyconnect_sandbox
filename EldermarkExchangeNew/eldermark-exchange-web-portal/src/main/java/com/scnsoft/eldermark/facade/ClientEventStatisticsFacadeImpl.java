package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.EventGroupStatistics;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.EventGroupStatisticsDto;
import com.scnsoft.eldermark.dto.EventStatisticsDto;
import com.scnsoft.eldermark.dto.EventStatisticsFilterDto;
import com.scnsoft.eldermark.service.ClientEventStatisticsService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientEventStatisticsFacadeImpl implements ClientEventStatisticsFacade {

    @Autowired
    private ClientEventStatisticsService clientEventStatisticsService;

    @Autowired
    private ListAndItemConverter<EventGroupStatistics, EventGroupStatisticsDto> eventGroupStatisticsEntityConverter;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    @Transactional(readOnly = true)
//    access to client's event is checked by permission filter in DB
    public List<EventStatisticsDto> findEventGroupCountByClientId(@P("clientId") Long clientId, EventStatisticsFilterDto filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        var result = filter.getEventStatisticsFilterDates().stream()
                .map(dateFilter -> {
                    EventStatisticsDto eventStatisticsDto = new EventStatisticsDto();
                    var list = clientEventStatisticsService.findEventStatistics(clientId, permissionFilter,
                            DateTimeUtils.toInstant(dateFilter.getFromDate()),
                            DateTimeUtils.toInstant(dateFilter.getToDate()));
                    var eventStatisticsConvert = eventGroupStatisticsEntityConverter.convertList(list);
                    eventStatisticsDto.setEventGroupStatisticsList(eventStatisticsConvert);
                    eventStatisticsDto.setFromDate(dateFilter.getFromDate());
                    eventStatisticsDto.setToDate(dateFilter.getToDate());



                    return eventStatisticsDto;
                })
                .collect(Collectors.toList());

        return result;
    }

}
