package com.scnsoft.eldermark.service.twilio;

import com.scnsoft.eldermark.exception.BusinessException;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class ConversationUtils {

    private ConversationUtils(){}

    public static String employeeIdToIdentity(Long employeeId) {
        return "e" + employeeId;
    }

    public static Set<String> employeeIdsToIdentity(Collection<Long> employeeIds) {
        return CollectionUtils.emptyIfNull(employeeIds).stream()
                .map(ConversationUtils::employeeIdToIdentity)
                .collect(Collectors.toSet());
    }

    public static Expression<String> employeeIdToIdentity(Path<Long> employeeId, CriteriaBuilder cb) {
        //todo add employeeId to personal and group chat tables
        return cb.concat(cb.literal("e"), employeeId.as(String.class));
    }


    public static List<Long> employeeIdsFromIdentities(Collection<String> identities) {
        identities.forEach(ConversationUtils::validateEmployeeIdentity);
        return identities.stream()
                .map(ConversationUtils::employeeIdFromIdentity)
                .collect(Collectors.toList());
    }

    public static Map<String, Long> employeeIdsFromIdentitiesMap(Collection<String> identities) {
        identities.forEach(ConversationUtils::validateEmployeeIdentity);
        return identities.stream()
                .collect(Collectors
                        .toMap(Function.identity(), ConversationUtils::employeeIdFromIdentity));
    }

    public static Long employeeIdFromIdentity(String identity) {
        validateEmployeeIdentity(identity);
        return Long.parseLong(identity.substring(1));
    }

    public static void validateEmployeeIdentity(String identity) {
        if (!identity.startsWith("e")) {
            throw new BusinessException("Invalid user identity [" + identity + "]");
        }
    }
}
