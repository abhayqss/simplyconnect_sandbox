package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.entity.chat.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.Collections;

@Component
public class ChatSpecificationGenerator {

    public Specification<PersonalChat> personalChatBetweenAnyUserAndUser(Collection<String> identities, String identity2) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.and(
                        SpecificationUtils.in(criteriaBuilder, root.get(PersonalChat_.twilioIdentity1), identities),
                        criteriaBuilder.equal(root.get(PersonalChat_.twilioIdentity2), identity2)
                ),
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(PersonalChat_.twilioIdentity1), identity2),
                        SpecificationUtils.in(criteriaBuilder, root.get(PersonalChat_.twilioIdentity2), identities)
                )
        );
    }

    public Specification<PersonalChat> personalChatBetweenUsers(String identity1, String identity2) {
        return personalChatBetweenAnyUserAndUser(Collections.singletonList(identity1), identity2);
    }

    public Specification<PersonalChat> personalChatBetweenAnyUserAndClient(Collection<String> identities, Long clientId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.and(
                        SpecificationUtils.in(criteriaBuilder, root.get(PersonalChat_.twilioIdentity1), identities),
                        criteriaBuilder.equal(root.get(PersonalChat_.client2Id), clientId)
                ),
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(PersonalChat_.client1Id), clientId),
                        SpecificationUtils.in(criteriaBuilder, root.get(PersonalChat_.twilioIdentity2), identities)
                )
        );
    }

    public Specification<GroupChatParticipantHistory> groupChatBetweenAnyUserAndClient(Collection<String> identities, Long clientId) {
        return (root, query, criteriaBuilder) -> {
            var conversationsWithClient = query.subquery(String.class);
            var subClientRoot = conversationsWithClient.from(GroupChatParticipantHistory.class);
            conversationsWithClient
                    .select(subClientRoot.get(GroupChatParticipantHistory_.twilioConversationSid))
                    .where(notDeletedGroupChatMember(subClientRoot),
                            criteriaBuilder.equal(subClientRoot.get(GroupChatParticipantHistory_.clientId), clientId));

            var conversationsWithUser = query.subquery(String.class);
            var subUserRoot = conversationsWithUser.from(GroupChatParticipantHistory.class);
            conversationsWithUser
                    .select(subUserRoot.get(GroupChatParticipantHistory_.twilioConversationSid))
                    .where(notDeletedGroupChatMember(subUserRoot),
                            SpecificationUtils.in(criteriaBuilder, subUserRoot.get(GroupChatParticipantHistory_.twilioIdentity), identities));


            return criteriaBuilder.and(
                    root.get(GroupChatParticipantHistory_.twilioConversationSid).in(conversationsWithClient),
                    root.get(GroupChatParticipantHistory_.twilioConversationSid).in(conversationsWithUser)
            );
        };
    }

    public Specification<GroupChatParticipantHistory> groupChatBetweenAnyUserAndUser(Collection<String> identities, String identity2) {
        return (root, query, criteriaBuilder) -> {
            var conversationsWithEmployee = query.subquery(String.class);
            var subEmployeeRoot = conversationsWithEmployee.from(GroupChatParticipantHistory.class);
            conversationsWithEmployee
                    .select(subEmployeeRoot.get(GroupChatParticipantHistory_.twilioConversationSid))
                    .where(notDeletedGroupChatMember(subEmployeeRoot),
                            criteriaBuilder.equal(subEmployeeRoot.get(GroupChatParticipantHistory_.twilioIdentity), identity2));

            var conversationsWithUser = query.subquery(String.class);
            var subUserRoot = conversationsWithUser.from(GroupChatParticipantHistory.class);
            conversationsWithUser
                    .select(subUserRoot.get(GroupChatParticipantHistory_.twilioConversationSid))
                    .where(notDeletedGroupChatMember(subUserRoot),
                            SpecificationUtils.in(criteriaBuilder, subUserRoot.get(GroupChatParticipantHistory_.twilioIdentity), identities));


            return criteriaBuilder.and(
                    root.get(GroupChatParticipantHistory_.twilioConversationSid).in(conversationsWithEmployee),
                    root.get(GroupChatParticipantHistory_.twilioConversationSid).in(conversationsWithUser),
                    notDeletedGroupChatMember(root)
            );
        };
    }

    public Specification<GroupChatParticipantHistory> notDeletedGroupChatMember() {
        return (root, criteriaQuery, criteriaBuilder) -> notDeletedGroupChatMember(root);
    }

    public Specification<GroupChatParticipantHistory> groupChatEntriesByConversationSid(String conversationSid) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(GroupChatParticipantHistory_.twilioConversationSid), conversationSid);
    }

    public Specification<PersonalChat> inPersonalChat(String conversationSid, Collection<String> identities) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get(PersonalChat_.twilioConversationSid), conversationSid),
                criteriaBuilder.or(
                        SpecificationUtils.in(criteriaBuilder, root.get(PersonalChat_.twilioIdentity1), identities),
                        SpecificationUtils.in(criteriaBuilder, root.get(PersonalChat_.twilioIdentity2), identities))
        );
    }

    public Specification<GroupChatParticipantHistory> inGroupChat(String conversationSid, Collection<String> identities) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get(GroupChatParticipantHistory_.twilioConversationSid), conversationSid),
                notDeletedGroupChatMember(root),
                SpecificationUtils.in(criteriaBuilder, root.get(GroupChatParticipantHistory_.twilioIdentity), identities)
        );
    }

    public Specification<PersonalChat> personalChatsOfUsers(Collection<String> identities) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                SpecificationUtils.in(criteriaBuilder, root.get(PersonalChat_.twilioIdentity1), identities),
                SpecificationUtils.in(criteriaBuilder, root.get(PersonalChat_.twilioIdentity2), identities)
        );
    }

    public Specification<GroupChatParticipantHistory> groupChatEntriesOfUsers(Collection<String> identities) {
        return (root, query, criteriaBuilder) ->
                SpecificationUtils.in(
                        criteriaBuilder,
                        root.get(GroupChatParticipantHistory_.twilioIdentity),
                        identities
                );
    }

    public Specification<GroupChatParticipantHistory> groupChatEntriesOfChatsContainingAnyUser(Collection<String> identities) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            var subquery = criteriaQuery.subquery(String.class);
            var subGroupChatFrom = subquery.from(GroupChatParticipantHistory.class);

            subquery.select(subGroupChatFrom.get(GroupChatParticipantHistory_.twilioConversationSid))
                    .where(
                            notDeletedGroupChatMember(subGroupChatFrom),
                           SpecificationUtils.in(criteriaBuilder, root.get(GroupChatParticipantHistory_.twilioIdentity), identities)
                    );

            return criteriaBuilder.and(
                    root.get(GroupChatParticipantHistory_.twilioConversationSid).in(subquery),
                    notDeletedGroupChatMember(root)
            );
        };
    }

    private Predicate notDeletedGroupChatMember(Path<GroupChatParticipantHistory> root) {
        return root.get(GroupChatParticipantHistory_.deletedDatetime).isNull();
    }

    public Specification<PersonalChat> connectedPersonalChats() {
        return (root, criteriaQuery, criteriaBuilder) ->
                root.get(PersonalChat_.twilioConversationSid).in(
                        disconnectedValueTwilioConversation(criteriaQuery, criteriaBuilder, false)
                );
    }

    public Specification<PersonalChat> disconnectedPersonalChats() {
        return (root, criteriaQuery, criteriaBuilder) ->
                root.get(PersonalChat_.twilioConversationSid).in(
                        disconnectedValueTwilioConversation(criteriaQuery, criteriaBuilder, true)
                );
    }

    public Specification<GroupChatParticipantHistory> groupChatEntriesOfConnectedChats() {
        return (root, criteriaQuery, criteriaBuilder) ->
                root.get(GroupChatParticipantHistory_.twilioConversationSid).in(
                        disconnectedValueTwilioConversation(criteriaQuery, criteriaBuilder, false)
                );
    }

    public Specification<GroupChatParticipantHistory> groupChatEntriesOfDisconnectedChats() {
        return (root, criteriaQuery, criteriaBuilder) ->
                root.get(GroupChatParticipantHistory_.twilioConversationSid).in(
                        disconnectedValueTwilioConversation(criteriaQuery, criteriaBuilder, true)
                );
    }

    private Subquery<String> disconnectedValueTwilioConversation(AbstractQuery<?> query,
                                                                 CriteriaBuilder criteriaBuilder,
                                                                 boolean disconnected) {

        var subquery = query.subquery(String.class);
        var twilioChatRoot = subquery.from(TwilioConversation.class);

        return subquery
                .select(twilioChatRoot.get(TwilioConversation_.twilioConversationSid))
                .where(criteriaBuilder.equal(twilioChatRoot.get(TwilioConversation_.disconnected), disconnected));
    }

    public Specification<TwilioConversation> twilioConversationBySidIn(Collection<String> conversationSids) {
        return (root, criteriaQuery, criteriaBuilder) -> SpecificationUtils.in(
                criteriaBuilder,
                root.get(TwilioConversation_.twilioConversationSid),
                conversationSids
        );
    }
    public Specification<TwilioConversation> twilioConversationBySid(String conversationSid) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(TwilioConversation_.twilioConversationSid), conversationSid);
    }
}
