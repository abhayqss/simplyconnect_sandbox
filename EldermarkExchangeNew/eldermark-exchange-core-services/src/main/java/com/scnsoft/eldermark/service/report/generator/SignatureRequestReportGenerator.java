package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.projection.DocumentSignatureHistoryCommentsAware;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.signature.*;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.signature.DocumentSignatureHistoryDao;
import com.scnsoft.eldermark.dao.specification.DocumentSignatureHistorySpecificationGenerator;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureHistory_;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest_;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SignatureRequestReportGenerator extends DefaultReportGenerator<SignatureRequestReport> {

    private static final Sort HISTORY_SORT = Sort.by(
            Sort.Order.asc(
                    String.join(
                            ".",
                            DocumentSignatureHistory_.REQUEST,
                            DocumentSignatureRequest_.CLIENT,
                            Client_.COMMUNITY,
                            Community_.NAME
                    )
            ),
            Sort.Order.asc(
                    String.join(
                            ".",
                            DocumentSignatureHistory_.REQUEST,
                            DocumentSignatureRequest_.CLIENT,
                            Client_.COMMUNITY,
                            Community_.ID
                    )
            ),
            Sort.Order.asc(
                    String.join(
                            ".",
                            DocumentSignatureHistory_.REQUEST,
                            DocumentSignatureRequest_.CLIENT,
                            Client_.FIRST_NAME
                    )
            ),
            Sort.Order.asc(
                    String.join(
                            ".",
                            DocumentSignatureHistory_.REQUEST,
                            DocumentSignatureRequest_.CLIENT,
                            Client_.LAST_NAME
                    )
            ),
            Sort.Order.asc(DocumentSignatureHistory_.DATE)
    );

    @Autowired
    private DocumentSignatureHistorySpecificationGenerator signatureHistorySpecificationGenerator;

    @Autowired
    private DocumentSignatureHistoryDao signatureHistoryDao;

    @Override
    public SignatureRequestReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new SignatureRequestReport();
        populateReportingCriteriaFields(filter, report);

        var specification = signatureHistorySpecificationGenerator.hasAccess(permissionFilter)
                .and(signatureHistorySpecificationGenerator.byClientCommunityIn(filter.getAccessibleCommunityIdsAndNames()))
                .and(signatureHistorySpecificationGenerator.withinPeriod(filter.getInstantFrom(), filter.getInstantTo()));

        var historyDetails = signatureHistoryDao.findAll(
                specification,
                SignatureHistoryDetails.class,
                HISTORY_SORT
        );

        var historyByOrganization = new LinkedHashMap<Long, List<SignatureHistoryDetails>>();
        historyDetails.forEach(history -> historyByOrganization.computeIfAbsent(
                        history.getRequestClientOrganizationId(),
                        organizationId -> new ArrayList<>()
                )
                .add(history));

        var rows = historyByOrganization.values().stream()
                .map(this::createRow)
                .collect(Collectors.toList());

        report.setRows(rows);

        return report;
    }

    private SignatureRequestReportRow createRow(List<SignatureHistoryDetails> organizationHistory) {
        var row = new SignatureRequestReportRow();
        row.setOrganizationName(organizationHistory.get(0).getRequestClientOrganizationName());

        var historyByCommunity = new LinkedHashMap<Long, List<SignatureHistoryDetails>>();
        organizationHistory.forEach(history -> historyByCommunity.computeIfAbsent(
                        history.getRequestClientCommunityId(),
                        communityId -> new ArrayList<>()
                )
                .add(history));

        var communityRows = historyByCommunity.values().stream()
                .map(this::createCommunityRow)
                .collect(Collectors.toList());

        row.setCommunityRows(communityRows);

        return row;
    }

    private SignatureRequestReportRowCommunity createCommunityRow(List<SignatureHistoryDetails> communityHistory) {
        var row = new SignatureRequestReportRowCommunity();
        row.setCommunityName(communityHistory.get(0).getRequestClientCommunityName());

        var historyByClient = new LinkedHashMap<Long, List<SignatureHistoryDetails>>();
        communityHistory.forEach(history -> historyByClient.computeIfAbsent(
                        history.getRequestClientId(),
                        communityId -> new ArrayList<>()
                )
                .add(history));

        var clientRows = historyByClient.values().stream()
                .map(this::createClientRow)
                .collect(Collectors.toList());

        row.setClientRows(clientRows);

        return row;
    }

    private SignatureRequestReportRowClient createClientRow(List<SignatureHistoryDetails> clientHistory) {

        var row = new SignatureRequestReportRowClient();
        var firstItem = clientHistory.get(0);
        row.setClientId(firstItem.getRequestClientId());
        row.setClientName(
                CareCoordinationUtils.getFullName(
                        firstItem.getRequestClientFirstName(),
                        firstItem.getRequestClientLastName()
                )
        );

        var historyByDocument = new LinkedHashMap<Long, List<SignatureHistoryDetails>>();
        clientHistory.forEach(history -> historyByDocument.computeIfAbsent(
                        history.getDocumentId(),
                        communityId -> new ArrayList<>()
                )
                .add(history));

        var documentRows = historyByDocument.values().stream()
                .map(this::createDocumentRow)
                .collect(Collectors.toList());

        row.setDocumentRows(documentRows);

        return row;
    }

    private SignatureRequestReportRowDocument createDocumentRow(List<SignatureHistoryDetails> documentHistory) {

        var row = new SignatureRequestReportRowDocument();
        row.setTemplateName(documentHistory.get(0).getRequestSignatureTemplateTitle());
        row.setActionRows(
                documentHistory.stream()
                        .map(this::createActionRow)
                        .collect(Collectors.toList())
        );

        return row;
    }

    private SignatureRequestReportRowAction createActionRow(SignatureHistoryDetails actionHistory) {
        var row = new SignatureRequestReportRowAction();
        row.setActionDateTime(actionHistory.getDate());
        row.setActorName(CareCoordinationUtils.getFullName(actionHistory.getActorFirstName(), actionHistory.getActorLastName()));
        row.setSignatureStatusName(actionHistory.getActionTitle());
        row.setActorRoleName(actionHistory.getActorRoleName());
        row.setComments(actionHistory);
        return row;
    }

    @Override
    public ReportType getReportType() {
        return ReportType.SIGNATURE_REQUESTS;
    }

    interface SignatureHistoryDetails extends DocumentSignatureHistoryCommentsAware {
        Long getDocumentId();

        Long getRequestClientId();

        String getRequestClientFirstName();

        String getRequestClientLastName();

        String getActorFirstName();

        String getActorLastName();

        String getActorRoleName();

        Long getRequestClientCommunityId();

        String getRequestClientCommunityName();

        Long getRequestClientOrganizationId();

        String getRequestClientOrganizationName();

        String getActionTitle();

        String getRequestSignatureTemplateTitle();
    }
}
