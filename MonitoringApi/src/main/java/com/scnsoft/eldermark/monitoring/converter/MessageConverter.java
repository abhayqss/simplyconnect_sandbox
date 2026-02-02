package com.scnsoft.eldermark.monitoring.converter;

import com.scnsoft.eldermark.monitoring.dto.ItemType;
import com.scnsoft.eldermark.monitoring.dto.MonitorItem;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MessageConverter implements Converter<List<MonitorItem>, String> {

    @Override
    public String convert(List<MonitorItem> monitorItems) {
        var groupedMonitorItems = monitorItems.stream()
                .collect(Collectors.groupingBy(MonitorItem::getType, HashMap::new, Collectors.groupingBy(MonitorItem::isAvailable)));
        return groupedMonitorItems.entrySet().stream()
                .map(this::convertByType)
                .collect(Collectors.joining("\n"));
    }

    private String convertByType(Map.Entry<ItemType, Map<Boolean, List<MonitorItem>>> byType) {
        return byType.getValue().entrySet().stream()
                .map(byAvailability -> byType.getKey().getName() + convertByAvailability(byAvailability))
                .collect(Collectors.joining("\n"));
    }

    private String convertByAvailability(Map.Entry<Boolean, List<MonitorItem>> byAvailability) {
        var itemNames = byAvailability.getValue().stream()
                .map(MonitorItem::getName)
                .collect(Collectors.joining(", "));
        if (byAvailability.getKey()) {
            return (byAvailability.getValue().size() > 1 ? "s have" : " has") + " recovered: " + itemNames;
        } else {
            return (byAvailability.getValue().size() > 1 ? "s are" : " is") + " not available: " + itemNames;
        }
    }
}
