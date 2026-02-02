package com.scnsoft.eldermark.service.medispan;

import com.scnsoft.eldermark.service.medispan.dto.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MediSpanServiceImpl implements MediSpanService {

    private static final long MAX_VALUES_SIZE = 1000;

    private final MediSpanApiGateway apiGateway;

    public MediSpanServiceImpl(MediSpanApiGateway apiGateway) {
        this.apiGateway = apiGateway;
    }

    @Override
    public List<MediSpanRoutedDrug> findRoutedDrugByMediSpanIds(Collection<String> mediSpanIds, List<String> fields) {

        return makeChunkedRequests(
                mediSpanIds,
                MediSpanRoutedDrug.MEDI_SPAN_ID_FIELD,
                fields,
                r -> apiGateway.getRoutedDrugs(r).getResults()
        );
    }

    @Override
    public Optional<MediSpanRoutedDrug> findRoutedDrugByMediSpanId(String mediSpanId, List<String> fields) {
        var request = new MediSpanRequest();
        request.setCriteria(List.of(
                MediSpanCriteria.isEqualTo(MediSpanRoutedDrug.MEDI_SPAN_ID_FIELD, mediSpanId)
        ));
        request.setFields(fields);

        var results = apiGateway.getRoutedDrugs(request).getResults();

        if (results.size() > 1) {
            throw new IllegalStateException("Found more than one Routed Drug with mediSpanId = " + mediSpanId);
        }

        return results.stream().findFirst();
    }

    @Override
    public Optional<MediSpanDispensableDrug> findDispensableDrugByMediSpanId(String mediSpanId, List<String> fields) {
        var request = new MediSpanRequest();
        request.setCriteria(List.of(
                MediSpanCriteria.isEqualTo(MediSpanDispensableDrug.MEDI_SPAN_ID_FIELD, mediSpanId)
        ));
        request.setFields(fields);

        var results = apiGateway.getDispensableDrugs(request).getResults();

        if (results.size() > 1) {
            throw new IllegalStateException("Found more than one Dispensable Drug with mediSpanId = " + mediSpanId);
        }

        return results.stream().findFirst();
    }

    @Override
    public List<MediSpanDispensableDrug> findDispensableDrugsByName(String name, List<String> fields) {
        return findDispensableDrugsByName(name, fields, Long.MAX_VALUE, 0);
    }

    @Override
    public List<MediSpanDispensableDrug> findDispensableDrugsByName(String name, List<String> fields, long count, long offset) {
        var request = new MediSpanRequest();
        request.setCriteria(List.of(
                MediSpanCriteria.contains(MediSpanDispensableDrug.NAME_FIELD, name)
        ));
        request.setFields(fields);
        request.setCount(String.valueOf(count));
        request.setStartIndex(String.valueOf(offset));

        return apiGateway.getDispensableDrugs(request).getResults();
    }

    @Override
    public List<MediSpanPackagedDrug> findPackagedDrugsPpids(Collection<String> ppids, List<String> fields) {
        return makeChunkedRequests(
                ppids,
                MediSpanPackagedDrug.PPID_FIELD,
                fields,
                r -> apiGateway.getPackagedDrugs(r).getResults()
        );
    }

    @Override
    public Optional<MediSpanPackagedDrug> findPackagedDrugByNdc(String ndc, List<String> fields) {
        var request = new MediSpanRequest();
        request.setCriteria(List.of(
                MediSpanCriteria.isEqualTo(MediSpanPackagedDrug.NDC_FIELD, ndc)
        ));
        request.setFields(fields);

        var results = apiGateway.getPackagedDrugs(request).getResults();

        if (results.size() > 1) {
            throw new IllegalStateException("Found more than one Packaged Drug with NDC = " + ndc);
        }

        return results.stream().findFirst();
    }

    @Override
    public List<MediSpanRoute> findRoutesByIds(Collection<String> ids, List<String> fields) {
        return makeChunkedRequests(
                ids,
                MediSpanRoute.ID_FIELD,
                fields,
                r -> apiGateway.getRoutes(r).getResults()
        );
    }

    @Override
    public Optional<MediSpanRoute> findRouteById(String id) {
        var request = new MediSpanRequest();
        request.setCriteria(List.of(
                MediSpanCriteria.isEqualTo(MediSpanRoute.ID_FIELD, id)
        ));
        request.setFields(MediSpanRequest.ALL_FIELDS);

        var results = apiGateway.getRoutes(request).getResults();

        if (results.size() > 1) {
            throw new IllegalStateException("Found more than one Medi-Span route with id = " + id);
        }

        return results.stream().findFirst();
    }

    @Override
    public List<MediSpanDoseForm> findDoseFormsByMediSpanIds(Collection<String> mediSpanIds, List<String> fields) {
        return makeChunkedRequests(
                mediSpanIds,
                MediSpanDoseForm.MEDI_SPAN_ID_FIELD,
                fields,
                r -> apiGateway.getDoseForms(r).getResults()
        );
    }

    @Override
    public Optional<MediSpanDoseForm> findDoseFormByMediSpanId(String mediSpanId) {

        var request = new MediSpanRequest();
        request.setCriteria(List.of(
                MediSpanCriteria.isEqualTo(MediSpanDoseForm.MEDI_SPAN_ID_FIELD, mediSpanId)
        ));
        request.setFields(MediSpanRequest.ALL_FIELDS);

        var results = apiGateway.getDoseForms(request).getResults();

        if (results.size() > 1) {
            throw new IllegalStateException("Found more than one Medi-Span dose form with mediSpanId = " + mediSpanId);
        }

        return results.stream().findFirst();
    }

    private <T> List<T> makeChunkedRequests(Collection<String> ids, String idField, List<String> fields, Function<MediSpanRequest, List<T>> requestExecutor) {

        var result = new ArrayList<T>();
        var processedCount = 0L;

        while (true) {
            var idsChunk = ids.stream()
                    .skip(processedCount)
                    .limit(MAX_VALUES_SIZE)
                    .collect(Collectors.toList());

            if (idsChunk.isEmpty()) break;

            var request = new MediSpanRequest();

            request.setCriteria(List.of(MediSpanCriteria.isEqualToAny(idField, idsChunk)));
            request.setFields(fields);

            result.addAll(requestExecutor.apply(request));
            processedCount += idsChunk.size();
        }

        return result;
    }
}
