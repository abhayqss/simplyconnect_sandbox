package com.scnsoft.eldermark.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.scnsoft.eldermark.util.StreamUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dao.AdtMessageDao;
import com.scnsoft.eldermark.dao.EventDao;
import com.scnsoft.eldermark.dto.client.BillingInfoDto;
import com.scnsoft.eldermark.dto.client.BillingItemDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.client.ClientHealthPlan;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.entity.xds.message.IN1ListSegmentContainingMessage;
import com.scnsoft.eldermark.entity.xds.segment.IN1InsuranceSegment;
import com.scnsoft.eldermark.service.BillingService;
import com.scnsoft.eldermark.service.ClientService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillingFacadeImpl implements BillingFacade {

    @Autowired
    private BillingService billingService;

    @Autowired
    private EventDao eventDao;

    @Autowired
    private AdtMessageDao adtMessageDao;

    @Autowired
    private ListAndItemConverter<ClientHealthPlan, BillingItemDto> clientHealthPlanToBillingListItemConverter;

    @Autowired
    private Converter<Client, BillingItemDto> networkInsuranceToBillingConverter;

    @Autowired
    ListAndItemConverter<AdtMessage, BillingItemDto> adtMessageBillingListItemConverter;

    @Autowired
    private ListAndItemConverter<IN1InsuranceSegment, BillingItemDto> iN1InsuranceSegmentToBillingItemListAndItemConveter;

    @Autowired
    private ClientService clientService;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canView(#clientId)")
    public BillingInfoDto findByClientId(@P("clientId") Long clientId) {
        var clientBillingInfo = billingService.findOptionalById(clientId);
        BillingInfoDto billingInfoDto = new BillingInfoDto();
        List<BillingItemDto> billingItemDtoList = new ArrayList<BillingItemDto>();
        if (clientBillingInfo.isPresent()) {
            Client client = clientBillingInfo.get();
            billingInfoDto.setMedicaidNumber(client.getMedicaidNumber());
            billingInfoDto.setMedicareNumber(client.getMedicareNumber());
            billingItemDtoList.addAll(clientHealthPlanToBillingListItemConverter.convertList(client.getHealthPlans()));
            billingItemDtoList.add(networkInsuranceToBillingConverter.convert(client));
        }

        List<Long> adtMsgIds = eventDao.getAdtMsgByClientIds(clientService.findAllMergedClientsIds(clientId));
        List<AdtMessage> adtMessages = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(adtMsgIds))
            adtMessages = adtMessageDao.findAllById(adtMsgIds);

        for (AdtMessage adtMessage : adtMessages) {
            if (adtMessage instanceof IN1ListSegmentContainingMessage) {
                IN1ListSegmentContainingMessage insuranceMessage = (IN1ListSegmentContainingMessage) adtMessage;
                if (CollectionUtils.isNotEmpty(insuranceMessage.getIn1List())) {
                    billingItemDtoList.addAll(iN1InsuranceSegmentToBillingItemListAndItemConveter
                            .convertList(insuranceMessage.getIn1List()));
                }
            }
        }
        billingInfoDto.setItems(billingItemDtoList.stream()
                .filter(StreamUtils.distinctByKey(BillingItemDto::getInsurancePlanConcat)).collect(Collectors.toList()));
        return billingInfoDto;
    }
}
