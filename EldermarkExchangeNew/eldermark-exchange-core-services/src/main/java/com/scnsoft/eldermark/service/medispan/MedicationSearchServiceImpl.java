package com.scnsoft.eldermark.service.medispan;

import com.scnsoft.eldermark.service.medispan.dto.*;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MedicationSearchServiceImpl implements MedicationSearchService {

    private final MediSpanService mediSpanService;

    public MedicationSearchServiceImpl(MediSpanService mediSpanService) {
        this.mediSpanService = mediSpanService;
    }

    @Override
    public List<MedicationSearchResult> findByName(String name, int count, int offset) {
        var dispensableDrugs = mediSpanService.findDispensableDrugsByName(
                name,
                List.of(
                        MediSpanDispensableDrug.MEDI_SPAN_ID_FIELD,
                        MediSpanDispensableDrug.NAME_FIELD,
                        MediSpanDispensableDrug.STRENGTH_FIELD,
                        MediSpanDispensableDrug.DOSE_FORM_FIELD,
                        MediSpanDispensableDrug.ROUTED_DRUG_FIELD,
                        MediSpanDispensableDrug.PACKAGED_DRUGS_FIELD,
                        MediSpanDispensableDrug.DISPENSABLE_GENERIC_PRODUCT_FIELD
                ),
                count,
                offset
        );

        return constructSearchResults(dispensableDrugs);
    }

    @Override
    public Optional<MedicationSearchResult> findByNdc(String ndc) {
        return mediSpanService.findPackagedDrugByNdc(ndc, List.of(
                        MediSpanPackagedDrug.DISPENASABLE_DRUG_FIELD
                ))
                .flatMap(packagedDrug -> {
                    var dispensableDrug = packagedDrug.getDispensableDrug();
                    if (!MediSpanDispensableDrug.MEDI_SPAN_ID_FIELD.equals(dispensableDrug.getIdField())) {
                        throw new IllegalStateException("Unexpected id field name " + dispensableDrug.getIdField() + " for dispensable drug");
                    }
                    return mediSpanService.findDispensableDrugByMediSpanId(dispensableDrug.getValue(), List.of(
                            MediSpanDispensableDrug.MEDI_SPAN_ID_FIELD,
                            MediSpanDispensableDrug.NAME_FIELD,
                            MediSpanDispensableDrug.STRENGTH_FIELD,
                            MediSpanDispensableDrug.DOSE_FORM_FIELD,
                            MediSpanDispensableDrug.ROUTED_DRUG_FIELD,
                            MediSpanDispensableDrug.DISPENSABLE_GENERIC_PRODUCT_FIELD
                    ));
                })
                .map(dispensableDrug -> {

                    var result = new MedicationSearchResult();

                    result.setMediSpanId(dispensableDrug.getMediSpanId());
                    result.setName(dispensableDrug.getName());
                    result.setNdcCodes(List.of(ndc));
                    getDoseForm(dispensableDrug)
                            .map(MediSpanDoseForm::getName)
                            .filter(StringUtils::isNotBlank)
                            .ifPresent(result::setDoseForm);
                    Optional.ofNullable(dispensableDrug.getStrength())
                            .filter(StringUtils::isNotBlank)
                            .ifPresent(result::setStrength);
                    result.setGpi(dispensableDrug.getDispensableGenericProduct().getValue());

                    var routedDrug = dispensableDrug.getRoutedDrug();
                    if (routedDrug.getIdField() != null && MediSpanRoutedDrug.MEDI_SPAN_ID_FIELD.equals(routedDrug.getIdField())) {
                        mediSpanService.findRoutedDrugByMediSpanId(
                                        routedDrug.getValue(),
                                        List.of(
                                                MediSpanRoutedDrug.MEDI_SPAN_ID_FIELD,
                                                MediSpanRoutedDrug.ROUTE_FIELD
                                        )
                                )
                                .flatMap(this::getRoute)
                                .map(MediSpanRoute::getName)
                                .filter(StringUtils::isNotBlank)
                                .ifPresent(result::setRoute);
                    }

                    return result;
                });
    }

    @Override
    public Optional<MedicationSearchResult> findByMediSpanId(String mediSpanId) {
        var dispensableDrug = mediSpanService.findDispensableDrugByMediSpanId(
                mediSpanId,
                List.of(
                        MediSpanDispensableDrug.MEDI_SPAN_ID_FIELD,
                        MediSpanDispensableDrug.NAME_FIELD,
                        MediSpanDispensableDrug.STRENGTH_FIELD,
                        MediSpanDispensableDrug.DOSE_FORM_FIELD,
                        MediSpanDispensableDrug.ROUTED_DRUG_FIELD,
                        MediSpanDispensableDrug.PACKAGED_DRUGS_FIELD,
                        MediSpanDispensableDrug.DISPENSABLE_GENERIC_PRODUCT_FIELD
                )
        );

        return dispensableDrug
                .map(List::of)
                .flatMap(it -> constructSearchResults(it).stream().findFirst());
    }

    private List<MedicationSearchResult> constructSearchResults(List<MediSpanDispensableDrug> dispensableDrugs) {
        var dispensableDrugIdToRouteMap = getDispensableDrugMediSpanIdToRouteMap(dispensableDrugs);
        var dispensableDrugToNdcListMap = getDispensableDrugMediSpanIdToNdcListMap(dispensableDrugs);
        var dispensableDrugIdToDoseForm = getDispensableDrugMediSpanIdToDoseFormNameMap(dispensableDrugs);

        return dispensableDrugs.stream()
                .map(dispensableDrug -> {
                    var dto = new MedicationSearchResult();
                    dto.setMediSpanId(dispensableDrug.getMediSpanId());
                    dto.setName(dispensableDrug.getName());
                    Optional.ofNullable(dispensableDrug.getStrength())
                            .filter(StringUtils::isNotBlank)
                            .ifPresent(dto::setStrength);
                    dto.setRoute(dispensableDrugIdToRouteMap.get(dispensableDrug.getMediSpanId()));
                    dto.setNdcCodes(dispensableDrugToNdcListMap.get(dispensableDrug.getMediSpanId()));
                    dto.setDoseForm(dispensableDrugIdToDoseForm.get(dispensableDrug.getMediSpanId()));
                    dto.setGpi(dispensableDrug.getDispensableGenericProduct().getValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private Map<String, List<String>> getDispensableDrugMediSpanIdToNdcListMap(List<MediSpanDispensableDrug> dispensableDrugs) {
        var packagedDrugPpids = dispensableDrugs.stream()
                .map(MediSpanDispensableDrug::getPackagedDrugs)
                .flatMap(Collection::stream)
                .peek(it -> {
                    if (!MediSpanPackagedDrug.PPID_FIELD.equals(it.getIdField())) {
                        throw new IllegalStateException("Unexpected id field name " + it.getIdField() + " for packaged drug");
                    }
                })
                .map(MediSpanIdField::getValue)
                .collect(Collectors.toSet());

        var packageDrugPpidToNdcMap = mediSpanService.findPackagedDrugsPpids(packagedDrugPpids, List.of(
                        MediSpanPackagedDrug.NDC_FIELD,
                        MediSpanPackagedDrug.PPID_FIELD
                ))
                .stream()
                .collect(Collectors.toMap(MediSpanPackagedDrug::getPpid, MediSpanPackagedDrug::getNdc));

        return dispensableDrugs.stream()
                .collect(Collectors.toMap(
                        MediSpanDispensableDrug::getMediSpanId,
                        dispensableDrug ->
                                dispensableDrug.getPackagedDrugs().stream()
                                        .map(MediSpanIdField::getValue)
                                        .map(packageDrugPpidToNdcMap::get)
                                        .filter(StringUtils::isNotBlank)
                                        .collect(Collectors.toList())
                ));
    }

    private Map<String, String> getDispensableDrugMediSpanIdToDoseFormNameMap(List<MediSpanDispensableDrug> dispensableDrugs) {
        var doseFormIds = dispensableDrugs.stream()
                .map(MediSpanDispensableDrug::getDoseForm)
                .peek(doseForm -> {
                    if (!MediSpanDoseForm.MEDI_SPAN_ID_FIELD.equals(doseForm.getIdField())) {
                        throw new IllegalStateException("Unexpected id field name " + doseForm.getIdField() + " for dose form");
                    }
                })
                .map(MediSpanIdField::getValue)
                .collect(Collectors.toList());

        var doseFormIdToNameMap = mediSpanService.findDoseFormsByMediSpanIds(
                        new HashSet<>(doseFormIds),
                        List.of(
                                MediSpanDoseForm.MEDI_SPAN_ID_FIELD,
                                MediSpanDoseForm.NAME_FIELD
                        )
                )
                .stream()
                .filter(it -> StringUtils.isNotBlank(it.getName()))
                .collect(Collectors.toMap(MediSpanDoseForm::getMediSpanId, MediSpanDoseForm::getName));

        return dispensableDrugs.stream()
                .collect(Collectors.toMap(
                        MediSpanDispensableDrug::getMediSpanId,
                        it -> doseFormIdToNameMap.get(it.getDoseForm().getValue())
                ));
    }

    private Map<String, String> getDispensableDrugMediSpanIdToRouteMap(List<MediSpanDispensableDrug> dispensableDrugs) {

        var dispensableToRoutedMediSpanIdMap = dispensableDrugs.stream()
                .peek(dispensableDrug -> {
                    var routedDrug = dispensableDrug.getRoutedDrug();
                    if (!MediSpanRoutedDrug.MEDI_SPAN_ID_FIELD.equals(routedDrug.getIdField())) {
                        throw new IllegalStateException("Unexpected id field name " + routedDrug.getIdField() + " for routed drug");
                    }
                })
                .collect(Collectors.toMap(
                        MediSpanDispensableDrug::getMediSpanId,
                        dispensableDrug -> dispensableDrug.getRoutedDrug().getValue()
                ));

        var routedDrugs = mediSpanService.findRoutedDrugByMediSpanIds(
                new HashSet<>(dispensableToRoutedMediSpanIdMap.values()),
                List.of(
                        MediSpanRoutedDrug.MEDI_SPAN_ID_FIELD,
                        MediSpanRoutedDrug.ROUTE_FIELD
                )
        );

        var routedDrugIdToRouteIdMap = routedDrugs.stream()
                .filter(routedDrug -> routedDrug.getRoute().getIdField() != null)
                .peek(routedDrug -> {
                    var routeIdField = routedDrug.getRoute().getIdField();
                    if (!MediSpanRoute.ID_FIELD.equals(routeIdField)) {
                        throw new IllegalStateException("Unexpected id field name " + routeIdField + " for route");
                    }
                })
                .collect(Collectors.toMap(MediSpanRoutedDrug::getMediSpanId, it -> it.getRoute().getValue()));

        var routeIdToNameMap = mediSpanService.findRoutesByIds(
                        new HashSet<>(routedDrugIdToRouteIdMap.values()),
                        List.of(
                                MediSpanRoute.ID_FIELD,
                                MediSpanRoute.NAME_FIELD
                        )
                ).stream()
                .collect(Collectors.toMap(MediSpanRoute::getId, MediSpanRoute::getName));

        return dispensableDrugs.stream()
                .map(dispensableDrug -> Pair.of(
                        dispensableDrug.getMediSpanId(),
                        Optional.ofNullable(dispensableToRoutedMediSpanIdMap.get(dispensableDrug.getMediSpanId()))
                                .flatMap(routedDrugId -> Optional.ofNullable(routedDrugIdToRouteIdMap.get(routedDrugId)))
                                .flatMap(routeId -> Optional.ofNullable(routeIdToNameMap.get(routeId)))
                                .filter(StringUtils::isNotBlank)
                                .orElse(null)
                ))
                .filter(it -> it.getSecond() != null)
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    private Optional<MediSpanRoute> getRoute(MediSpanRoutedDrug routedDrug) {
        var route = routedDrug.getRoute();
        if (route.getIdField() == null) {
            return Optional.empty();
        }
        if (MediSpanRoute.ID_FIELD.equals(route.getIdField())) {
            return mediSpanService.findRouteById(route.getValue());
        } else {
            throw new IllegalStateException("Unexpected id field name " + route.getIdField() + " for route");
        }
    }

    private Optional<MediSpanDoseForm> getDoseForm(MediSpanDispensableDrug dispensableDrug) {
        var doseFrom = dispensableDrug.getDoseForm();
        if (MediSpanDoseForm.MEDI_SPAN_ID_FIELD.equals(doseFrom.getIdField())) {
            return mediSpanService.findDoseFormByMediSpanId(doseFrom.getValue());
        } else {
            throw new IllegalStateException("Unexpected id field name " + doseFrom.getIdField() + " for dose form");
        }
    }
}
