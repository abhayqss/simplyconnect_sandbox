package com.scnsoft.eldermark.framework.dao.source.filters;


public class MaxIdFilter <ID extends Comparable<ID>> {
    private final ID idLowerBoundExclusive;
    private final Integer limit;
    private final boolean isOrderById;
    private final Long lastSyncEpoch;
    private final Long currentSyncEpoch;

    private MaxIdFilter(Builder<ID> builder) {
        this.lastSyncEpoch = builder.lastSyncEpoch;
        this.currentSyncEpoch = builder.currentSyncEpoch;
        this.idLowerBoundExclusive = builder.idLowerBoundExclusive;
        this.limit = builder.limit;
        this.isOrderById = builder.isOrderById;
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

    public Integer getLimit() {
        return limit;
    }

    public boolean isOrderById() {
        return isOrderById;
    }

    public static class Builder<ID extends Comparable<ID>> {
        private ID idLowerBoundExclusive;
        private Integer limit;
        private boolean isOrderById;
        private Long lastSyncEpoch;
        private Long currentSyncEpoch;

        public Builder<ID> setLastSyncEpoch(Long lastSyncEpoch) {
            this.lastSyncEpoch = lastSyncEpoch;
            return this;
        }

        public Builder<ID> setCurrentSyncEpoch(Long currentSyncEpoch) {
            this.currentSyncEpoch = currentSyncEpoch;
            return this;
        }

        public Builder<ID> setIdLowerBoundExclusive(ID idLowerBoundExclusive) {
            this.idLowerBoundExclusive = idLowerBoundExclusive;
            return this;
        }

        public Builder<ID> setLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Builder<ID> setOrderById(boolean orderById) {
            isOrderById = orderById;
            return this;
        }

        public MaxIdFilter<ID> build() {
            return new MaxIdFilter<ID>(this);
        }
    }
}
