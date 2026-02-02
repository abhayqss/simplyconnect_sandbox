package com.scnsoft.eldermark.framework.dao.source.filters;

import java.util.List;

public class SourceEntitiesFilter <ID extends Comparable<ID>> {
    private final Long lastSyncEpoch;
    private final Long currentSyncEpoch;
    private final ID idLowerBoundExclusive;
    private final ID idUpperBoundInclusive;
    private final List<ID> excludedIds;

    private SourceEntitiesFilter(Builder<ID> builder) {
        this.lastSyncEpoch = builder.lastSyncEpoch;
        this.currentSyncEpoch = builder.currentSyncEpoch;
        this.idLowerBoundExclusive = builder.idLowerBoundExclusive;
        this.idUpperBoundInclusive = builder.idUpperBoundInclusive;
        this.excludedIds = builder.excludedIds;
    }

    public Long getLastSyncEpoch() {
        return lastSyncEpoch;
    }

    public Long getCurrentSyncEpoch() {
        return currentSyncEpoch;
    }

    public ID getIdLowerBoundExclusive() {
        return idLowerBoundExclusive;
    }

    public ID getIdUpperBoundInclusive() {
        return idUpperBoundInclusive;
    }

    public List<ID> getExcludedIds() {
        return excludedIds;
    }

    public static class Builder<ID extends Comparable<ID>> {
        private Long lastSyncEpoch;
        private Long currentSyncEpoch;
        private ID idLowerBoundExclusive;
        private ID idUpperBoundInclusive;
        private List<ID> excludedIds;

        public Builder<ID> setLastSyncEpoch(Long lastSyncEpoch) {
            this.lastSyncEpoch = lastSyncEpoch;
            return this;
        }

        public Builder<ID> setCurrentSyncEpoch(Long currentSyncEpoch) {
            this.currentSyncEpoch = currentSyncEpoch;
            return this;
        }

        public Builder<ID> setIdLowerBoundExclusive(ID idLowerBound) {
            this.idLowerBoundExclusive = idLowerBound;
            return this;
        }

        public Builder<ID> setIdUpperBoundInclusive(ID idUpperBound) {
            this.idUpperBoundInclusive = idUpperBound;
            return this;
        }

        public Builder<ID> setExcludedIds(List<ID> excludedIds) {
            this.excludedIds = excludedIds;
            return this;
        }

        public SourceEntitiesFilter<ID> build() {
            return new SourceEntitiesFilter<ID>(this);
        }
    }
}
