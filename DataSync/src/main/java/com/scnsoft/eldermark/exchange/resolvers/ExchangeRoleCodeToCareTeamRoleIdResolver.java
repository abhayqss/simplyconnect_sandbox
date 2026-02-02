package com.scnsoft.eldermark.exchange.resolvers;

import com.scnsoft.eldermark.framework.Pair;

public interface ExchangeRoleCodeToCareTeamRoleIdResolver {
    Pair<Long, Integer> getIdAndAssigningPriority(String name);
}
