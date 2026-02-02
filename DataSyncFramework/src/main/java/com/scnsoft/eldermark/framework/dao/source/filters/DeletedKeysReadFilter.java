package com.scnsoft.eldermark.framework.dao.source.filters;


public class DeletedKeysReadFilter {
    private final Long lastSyncEpoch;
    private final Long currentSyncEpoch;
    private final String tableName;
    private final Long idLowerBoundExclusive;
    private final Integer limit;
    private final boolean isOrderById;

    private DeletedKeysReadFilter(Builder builder) {
        this.lastSyncEpoch = builder.lastSyncEpoch;
        this.currentSyncEpoch = builder.currentSyncEpoch;
        this.tableName = builder.tableName;
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

    public String getTableName() {
        return tableName;
    }

    public Long getIdLowerBoundExclusive() {
        return idLowerBoundExclusive;
    }

    public Integer getLimit() {
        return limit;
    }

    public boolean isOrderById() {
        return isOrderById;
    }

    public static final class Builder {
        private Long lastSyncEpoch;
        private Long currentSyncEpoch;
        private String tableName;
        private Long idLowerBoundExclusive;
        private Integer limit;
        private boolean isOrderById;

        public Builder setLastSyncEpoch(Long lastSyncEpoch) {
            this.lastSyncEpoch = lastSyncEpoch;
            return this;
        }

        public Builder setCurrentSyncEpoch(Long currentSyncEpoch) {
            this.currentSyncEpoch = currentSyncEpoch;
            return this;
        }

        public Builder setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder setIdLowerBoundExclusive(Long idLowerBoundExclusive) {
            this.idLowerBoundExclusive = idLowerBoundExclusive;
            return this;
        }

        public Builder setLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Builder setOrderById(boolean orderById) {
            isOrderById = orderById;
            return this;
        }

        public DeletedKeysReadFilter build() {
            return new DeletedKeysReadFilter(this);
        }
    }
}
