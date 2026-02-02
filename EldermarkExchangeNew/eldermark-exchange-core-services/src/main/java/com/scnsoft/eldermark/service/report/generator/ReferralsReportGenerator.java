package com.scnsoft.eldermark.service.report.generator;

import com.google.common.collect.Lists;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.referrals.*;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ReferralDao;
import com.scnsoft.eldermark.dao.ReferralRequestDao;
import com.scnsoft.eldermark.dao.specification.ReferralSpecificationGenerator;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.referral.*;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.beans.reports.enums.ReportType.REFERRALS;

@Service
@Transactional(readOnly = true)
public class ReferralsReportGenerator extends DefaultReportGenerator<ReferralsReport> {

    @Autowired
    private ReferralSpecificationGenerator referralSpecificationGenerator;

    @Autowired
    private ReferralDao referralDao;

    @Autowired
    private ReferralRequestDao referralRequestDao;

    @Override
    public ReferralsReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new ReferralsReport();
        populateReportingCriteriaFields(filter, report);
        var outboundReferralsByCommunity = getOutboundReferrals(filter, permissionFilter);
        var inboundReferralsByCommunity = getInboundReferrals(filter, permissionFilter);
        for (var community : filter.getAccessibleCommunityIdsAndNames()) {
            var outboundReferrals = outboundReferralsByCommunity.getOrDefault(community.getId(), Collections.emptyList());
            var inboundReferrals = inboundReferralsByCommunity.getOrDefault(community.getId(), Collections.emptyList());
            if (outboundReferrals.size() == 0 && inboundReferrals.size() == 0) {
                continue;
            }
            addTotalReferralsRows(report, community, outboundReferrals, inboundReferrals, filter.getInstantFrom(), filter.getInstantTo());
            addInboundReferReferralsRows(report, community, inboundReferrals, filter.getInstantFrom(), filter.getInstantTo());
            addOutboundReferReferralsRows(report, community, outboundReferrals, filter.getInstantFrom(), filter.getInstantTo());
        }
        return report;
    }

    @Override
    public ReportType getReportType() {
        return REFERRALS;
    }

    private Map<Long, List<Referral>> getOutboundReferrals(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var hasAccess = referralSpecificationGenerator.hasAccessToOutboundReferrals(permissionFilter);
        var byClientCommunities = referralSpecificationGenerator.byClientCommunities(filter.getAccessibleCommunityIdsAndNames());
        var betweenDates = referralSpecificationGenerator.betweenDates(filter.getInstantFrom(), filter.getInstantTo());
        return referralDao.findAll(byClientCommunities.and(betweenDates.and(hasAccess))).stream()
                .filter(StreamUtils.distinctByKey(Referral::getId))
                .collect(Collectors.groupingBy(ref -> ref.getClient().getCommunityId()));
    }

    private Map<Long, List<ReferralRequest>> getInboundReferrals(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var hasAccessToRequest = referralSpecificationGenerator.hasAccessToInboundRequests(permissionFilter);
        var requestByClientCommunities = referralSpecificationGenerator.requestByCommunities(filter.getAccessibleCommunityIdsAndNames());
        var betweenRequestDates = referralSpecificationGenerator.requestBetweenDates(filter.getInstantFrom(), filter.getInstantTo());
        return referralRequestDao.findAll(requestByClientCommunities.and(betweenRequestDates.and(hasAccessToRequest))).stream()
                .filter(req -> isInboundRequestVisibleByStatus(req, filter.getInstantFrom(), filter.getInstantTo()))
                .filter(StreamUtils.distinctByKey(ReferralRequest::getId))
                .collect(Collectors.groupingBy(ReferralRequest::getCommunityId));
    }

    private void addTotalReferralsRows(ReferralsReport report, IdNameAware community, List<Referral> outboundReferrals, List<ReferralRequest> inboundReferrals, Instant from, Instant to) {
        var refRow = new TotalReferralsRow();
        refRow.setCommunityName(community.getName());
        refRow.setTotalNumberOfOutboundReferrals(outboundReferrals.stream().map(Referral::getId).distinct().count());
        refRow.setTotalNumberOfInboundReferrals(inboundReferrals.stream().map(ReferralRequest::getId).distinct().count());
        Optional.ofNullable(report.getTotalReferralsRows())
                .ifPresentOrElse(list -> list.add(refRow), () -> report.setTotalReferralsRows(Lists.newArrayList(refRow)));
    }

    private void addInboundReferReferralsRows(ReferralsReport report, IdNameAware community, List<ReferralRequest> inboundReferrals, Instant from, Instant to) {
        var referralRequestsByReferringCommunity = inboundReferrals.stream()
                .collect(Collectors.groupingBy(req -> req.getReferral().getClient().getCommunity()));
        for (var refCommunity : referralRequestsByReferringCommunity.keySet()) {
            var refRow = new ReferReferralsRow();
            refRow.setCommunityName(community.getName());
            refRow.setReferCommunityName(refCommunity.getName());

            var inboundReferralsByReferringCommunity = referralRequestsByReferringCommunity.get(refCommunity);
            var referralsByIfPresentLastResponse = inboundReferralsByReferringCommunity.stream()
                    .collect(Collectors.partitioningBy(req -> req.getLastResponse() != null));

            var pendingReferrals = referralsByIfPresentLastResponse.getOrDefault(false, Collections.emptyList());
            var cancelledReferralsCount = pendingReferrals.stream()
                    .filter(r -> ReferralStatus.CANCELED == getLatestReferralStatus(r.getReferral(), from, to))
                    .count();

            var referralsCountByResponseStatus = referralsByIfPresentLastResponse.getOrDefault(true, Collections.emptyList()).stream()
                    .collect(Collectors.groupingBy(req -> req.getLastResponse().getResponse(), Collectors.counting()));

            refRow.setNumberOfPendingReferrals(pendingReferrals.size() - cancelledReferralsCount);
            refRow.setNumberOfPreadmitReferrals(referralsCountByResponseStatus.getOrDefault(ReferralResponse.PRE_ADMIT, 0L));
            refRow.setNumberOfAcceptedReferrals(referralsCountByResponseStatus.getOrDefault(ReferralResponse.ACCEPTED, 0L));
            refRow.setNumberOfDeclinedReferrals(referralsCountByResponseStatus.getOrDefault(ReferralResponse.DECLINED, 0L));
            refRow.setNumberOfCanceledReferrals(cancelledReferralsCount);
            refRow.setTotalNumberOfReferrals(inboundReferralsByReferringCommunity.size());
            Optional.ofNullable(report.getInReferReferralsRows())
                    .ifPresentOrElse(list -> list.add(refRow), () -> report.setInReferReferralsRows(Lists.newArrayList(refRow)));

            inboundReferralsByReferringCommunity.stream()
                    .collect(Collectors.groupingBy(req -> req.getReferral().getReferringIndividual()))
                    .forEach((individual, referralRequests) -> addInboundIndividualReferReferralRow(report, community, refCommunity, individual, referralRequests));
        }
    }

    private void addInboundIndividualReferReferralRow(ReferralsReport report, IdNameAware community, Community refCommunity, String referringIndividual, List<ReferralRequest> referralRequests) {
        var refRow = new InboundIndividualReferReferralRow();
        refRow.setCommunityName(community.getName());
        refRow.setReferCommunityName(refCommunity.getName());
        refRow.setReferringIndividualName(referringIndividual);
        var referredClientNum = referralRequests.stream().map(req -> req.getReferral().getClient().getId()).distinct().count();
        refRow.setNumberOfReferredClients(referredClientNum);
        var acceptedReferralsCount = referralRequests.stream()
                .flatMap(req -> req.getResponses().stream().map(ReferralRequestResponse::getResponse))
                .filter(refRes -> ReferralResponse.ACCEPTED == refRes)
                .count();
        refRow.setNumberOfAcceptedReferrals(acceptedReferralsCount);
        refRow.setReferralSuccessRate((double) refRow.getNumberOfAcceptedReferrals() / referralRequests.size());
        Optional.ofNullable(report.getInIndividualReferReferralRows())
                .ifPresentOrElse(list -> list.add(refRow),
                        () -> report.setInIndividualReferReferralRows(Lists.newArrayList(refRow)));

    }

    private void addOutboundReferReferralsRows(ReferralsReport report, IdNameAware community, List<Referral> outboundReferrals, Instant from, Instant to) {
        var referralsByReferredCommunity = outboundReferrals.stream()
                .flatMap(ref -> ref.getReferralRequests().stream().map(req -> new Pair<>(req.getCommunity(), ref)))
                .collect(Collectors.groupingBy(Pair::getFirst, Collectors.mapping(Pair::getSecond, Collectors.toList())));
        for (var refCommunity : referralsByReferredCommunity.keySet()) {
            var refRow = new ReferReferralsRow();
            refRow.setCommunityName(community.getName());
            refRow.setReferCommunityName(refCommunity.getName());
            var outboundReferralsByReferredCommunity = referralsByReferredCommunity.get(refCommunity);
            var referralsCountByStatus = outboundReferralsByReferredCommunity.stream()
                    .collect(Collectors.groupingBy(ref -> getLatestReferralStatus(ref, from, to), Collectors.counting()));
            refRow.setNumberOfPendingReferrals(referralsCountByStatus.getOrDefault(ReferralStatus.PENDING, 0L));
            refRow.setNumberOfPreadmitReferrals(referralsCountByStatus.getOrDefault(ReferralStatus.PRE_ADMIT, 0L));
            refRow.setNumberOfAcceptedReferrals(referralsCountByStatus.getOrDefault(ReferralStatus.ACCEPTED, 0L));
            refRow.setNumberOfDeclinedReferrals(referralsCountByStatus.getOrDefault(ReferralStatus.DECLINED, 0L));
            refRow.setNumberOfCanceledReferrals(referralsCountByStatus.getOrDefault(ReferralStatus.CANCELED, 0L));
            refRow.setTotalNumberOfReferrals(outboundReferralsByReferredCommunity.size());
            Optional.ofNullable(report.getOutReferReferralsRows())
                    .ifPresentOrElse(list -> list.add(refRow), () -> report.setOutReferReferralsRows(Lists.newArrayList(refRow)));

            outboundReferralsByReferredCommunity.stream()
                    .collect(Collectors.groupingBy(Referral::getReferringIndividual))
                    .forEach((individual, referrals) -> addOutboundIndividualReferReferralsRow(report, community, refCommunity, individual, referrals, from, to));
        }
    }

    private void addOutboundIndividualReferReferralsRow(ReferralsReport report, IdNameAware community, Community refCommunity, String referringIndividual, List<Referral> referrals, Instant from, Instant to) {
        var refRow = new OutboundIndividualReferReferralRow();
        refRow.setCommunityName(community.getName());
        refRow.setReferCommunityName(refCommunity.getName());
        refRow.setReferringIndividualName(referringIndividual);
        var acceptedReferralsCount = referrals.stream()
                .map(ref -> getLatestReferralStatus(ref, from, to))
                .filter(ReferralStatus.ACCEPTED::equals)
                .count();
        refRow.setNumberOfAcceptedReferrals(acceptedReferralsCount);
        refRow.setReferralSuccessRate((double) refRow.getNumberOfAcceptedReferrals() / referrals.size());
        Optional.ofNullable(report.getOutIndividualReferReferralRows())
                .ifPresentOrElse(list -> list.add(refRow),
                        () -> report.setOutIndividualReferReferralRows(Lists.newArrayList(refRow)));
    }

    private ReferralStatus getLatestReferralStatus(Referral referral, Instant from, Instant to) {
        if (isActualDate(referral.getModifiedDate(), from, to)) {
            return referral.getReferralStatus();
        }
        var latestReferralHistory = getLatestReferralHistory(referral, from, to);
        if (latestReferralHistory == null) {
            return referral.getReferralStatus();
        }
        return latestReferralHistory.getReferralStatus();
    }

    private ReferralHistory getLatestReferralHistory(Referral referral, Instant from, Instant to) {
        return referral.getReferralHistories().stream().filter(h -> isActualDate(h.getModifiedDate(), from, to))
                .max(Comparator.comparing(ReferralHistory::getModifiedDate)).orElse(null);
    }

    private boolean isInboundRequestVisibleByStatus(ReferralRequest request, Instant from, Instant to) {
        var latestReferralHistory = getLatestReferralHistory(request.getReferral(), from, to);
        if (latestReferralHistory == null) {
            return true;
        }
        var referral = latestReferralHistory.getReferral();
        var latestStatus = getLatestReferralStatus(referral, from, to);
        var updatedReferralRequestId = isActualDate(referral.getModifiedDate(), from, to)
                ? Optional.ofNullable(referral.getUpdatedByResponse())
                .map(ReferralRequestResponse::getReferralRequest)
                .map(ReferralRequest::getId)
                .orElse(null)
                : latestReferralHistory.getUpdatedByResponse().getReferralRequest().getId();
        return ReferralStatus.PENDING == latestStatus || ReferralStatus.DECLINED == latestStatus ||
                ((ReferralStatus.PRE_ADMIT == latestStatus || ReferralStatus.ACCEPTED == latestStatus) &&
                        request.getId().equals(updatedReferralRequestId));
    }

    private boolean isActualDate(Instant date, Instant from, Instant to) {
        return !date.isBefore(from) && !date.isAfter(to);
    }
}
