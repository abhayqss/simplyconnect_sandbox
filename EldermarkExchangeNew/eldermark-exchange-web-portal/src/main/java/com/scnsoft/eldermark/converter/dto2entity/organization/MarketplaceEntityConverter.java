package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.converter.base.ItemConverter;
import com.scnsoft.eldermark.dto.MarketplaceDto;
import com.scnsoft.eldermark.entity.Marketplace;
import com.scnsoft.eldermark.service.DirectoryService;
import com.scnsoft.eldermark.service.ServiceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MarketplaceEntityConverter implements ItemConverter<MarketplaceDto, Marketplace> {

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private ServiceTypeService serviceTypeService;

    @Override
    public Marketplace convert(MarketplaceDto source) {
        var marketplace = new Marketplace();
        convert(source, marketplace);
        return marketplace;
    }

    @Override
    public void convert(MarketplaceDto source, Marketplace target) {
        target.setSummary(source.getServicesSummaryDescription());
        if (source.getLanguageIds() != null)
            target.setLanguageServices(directoryService.findLanguageServicesByIds(source.getLanguageIds())
                    .collect(Collectors.toList()));
        if (source.getServiceCategoryIds() != null)
            target.setServiceCategories(
                    directoryService.findServiceCategories(source.getServiceCategoryIds()).collect(Collectors.toList()));
        if (source.getServiceIds() != null) {
            target.setServiceTypes(serviceTypeService.findAllById(source.getServiceIds()));
        }
    }
}
