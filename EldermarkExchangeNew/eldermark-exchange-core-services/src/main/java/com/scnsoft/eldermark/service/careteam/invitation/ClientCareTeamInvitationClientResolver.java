package com.scnsoft.eldermark.service.careteam.invitation;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.exception.CareTeamInvitationClientHieConsentValidationException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

interface ClientCareTeamInvitationClientResolver {

    Client resolveClient(Long clientId);

    Optional<Client> findExistingClient(Client client) throws CareTeamInvitationClientHieConsentValidationException;

    List<Client> findForHiePolicyChangeAmong(Collection<Long> clientIds, Collection<Client> clients, Collection<Client> clientsToCheck);
}
