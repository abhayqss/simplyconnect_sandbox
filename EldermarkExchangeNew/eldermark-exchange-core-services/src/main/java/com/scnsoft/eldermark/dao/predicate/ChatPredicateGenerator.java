package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.dao.specification.SpecificationUtils;
import com.scnsoft.eldermark.entity.chat.PersonalChat;
import com.scnsoft.eldermark.entity.chat.PersonalChat_;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.Collection;

@Component
public class ChatPredicateGenerator {

    public Predicate usersParticipatingInPersonalWithAny(Collection<String> identities, Expression<String> user,
                                                         AbstractQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var subQuery = query.subquery(Integer.class);
        var subRoot = subQuery.from(PersonalChat.class);

        return criteriaBuilder.exists(subQuery
                .select(criteriaBuilder.literal(1))
                .where(criteriaBuilder.or(
                        criteriaBuilder.and(
                                SpecificationUtils.in(criteriaBuilder, subRoot.get(PersonalChat_.twilioIdentity1), identities),
                                criteriaBuilder.equal(subRoot.get(PersonalChat_.twilioIdentity2), user)
                        ),
                        criteriaBuilder.and(
                                criteriaBuilder.equal(subRoot.get(PersonalChat_.twilioIdentity1), user),
                                SpecificationUtils.in(criteriaBuilder, subRoot.get(PersonalChat_.twilioIdentity2), identities)
                        )
                ))
        );
    }
}
