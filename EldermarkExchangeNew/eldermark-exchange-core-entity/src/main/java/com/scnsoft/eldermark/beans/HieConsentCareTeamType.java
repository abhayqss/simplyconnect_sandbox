package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.beans.security.projection.entity.ClientSecurityAwareEntity;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HieConsentCareTeamType {

    //this one is needed for community care team... todo more explanation
    public static final Long ANY_TARGET_CLIENT_ID = -1L;

    private final Long clientId;
    private final Path<Client> clientPath;
    private final boolean includesCurrent;
    private final boolean includesOnHold;

    private List<OptimizationHints> optimizationHints;

    private HieConsentCareTeamType(Long clientId, Path<Client> clientPath, boolean includesCurrent, boolean includesOnHold) {
        this.clientId = clientId;
        this.clientPath = clientPath;
        this.includesCurrent = includesCurrent;
        this.includesOnHold = includesOnHold;
    }

    public static HieConsentCareTeamType current(Long clientId) {
        return new HieConsentCareTeamType(Objects.requireNonNull(clientId), null, true, false);
    }

    public static HieConsentCareTeamType currentWithOptimizations(ClientSecurityAwareEntity client) {
        return current(client.getId())
                .withOptimizationHints(client.getOrganizationId(), client.getHieConsentPolicyType());
    }


    public static HieConsentCareTeamType current(Path<Client> clientFrom) {
        return new HieConsentCareTeamType(null, Objects.requireNonNull(clientFrom), true, false);
    }

    public static HieConsentCareTeamType currentForAny(Collection<ClientSecurityAwareEntity> clients) {
        return new HieConsentCareTeamType(null, null, true, false)
                .withOptimizationHints(clients);
    }

    public static HieConsentCareTeamType onHold(Long clientId) {
        return new HieConsentCareTeamType(Objects.requireNonNull(clientId), null, false, true);
    }

    public static HieConsentCareTeamType onHoldWithOptimizations(ClientSecurityAwareEntity client) {
        return onHold(client.getId())
                .withOptimizationHints(client.getOrganizationId(), client.getHieConsentPolicyType());
    }

    public static HieConsentCareTeamType onHold(From<?, Client> clientFrom) {
        return new HieConsentCareTeamType(null, Objects.requireNonNull(clientFrom), false, true);
    }

    public static HieConsentCareTeamType onHoldForAny(Collection<ClientSecurityAwareEntity> clients) {
        return new HieConsentCareTeamType(null, null, false, true)
                .withOptimizationHints(clients);
    }


    public static HieConsentCareTeamType currentAndOnHold() {
        return new HieConsentCareTeamType(null, null, true, true);
    }

    public boolean isIncludesCurrent() {
        return includesCurrent;
    }

    public boolean isIncludesOnHold() {
        return includesOnHold;
    }

    public Long getClientId() {
        return clientId;
    }

    public Path<Client> getClientPath() {
        return clientPath;
    }

    private HieConsentCareTeamType withOptimizationHints(Long clientOrganizationId, HieConsentPolicyType policyType) {
        if (clientId == null || ANY_TARGET_CLIENT_ID.equals(clientId)) {
            throw new RuntimeException("Optimization hints can be applied for specific client only");
        }
        this.optimizationHints = List.of(new OptimizationHints(clientOrganizationId, policyType));
        return this;
    }

    private HieConsentCareTeamType withOptimizationHints(Collection<ClientSecurityAwareEntity> clients) {
        this.optimizationHints = clients.stream()
                .map(client -> new OptimizationHints(client.getOrganizationId(), client.getHieConsentPolicyType()))
                .collect(Collectors.toList());;
        return this;
    }

    public List<OptimizationHints> getOptimizationHints() {
        return optimizationHints;
    }

    public static class OptimizationHints {
        private final Long clientOrganizationId;
        private final HieConsentPolicyType policyType;

        private OptimizationHints(Long clientOrganizationId, HieConsentPolicyType policyType) {
            this.clientOrganizationId = Objects.requireNonNull(clientOrganizationId);
            this.policyType = policyType;
        }

        public Long getClientOrganizationId() {
            return clientOrganizationId;
        }

        public HieConsentPolicyType getPolicyType() {
            return policyType;
        }
    }
}
