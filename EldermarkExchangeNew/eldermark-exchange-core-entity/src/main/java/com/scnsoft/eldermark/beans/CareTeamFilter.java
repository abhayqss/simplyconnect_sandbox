package com.scnsoft.eldermark.beans;

public class CareTeamFilter {

    private static final Affiliation DEFAULT_AFFILIATION = Affiliation.REGULAR;
    public static final String DEFAULT_AFFILIATION_VALUE = "REGULAR";

    private Long clientId;
    private Long communityId;
    private String name;
    private Affiliation affiliation = DEFAULT_AFFILIATION;

    private boolean canDeleteOnly;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Affiliation getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(Affiliation affiliation) {
        this.affiliation = affiliation;
    }

    public AffiliatedCareTeamType getAffiliationType() {
        return (affiliation == null ? DEFAULT_AFFILIATION : affiliation).toType();
    }

    public boolean getCanDeleteOnly() {
        return canDeleteOnly;
    }

    public void setCanDeleteOnly(boolean canDeleteOnly) {
        this.canDeleteOnly = canDeleteOnly;
    }

    public enum Affiliation {
        REGULAR(AffiliatedCareTeamType.REGULAR),
        AFFILIATED(AffiliatedCareTeamType.PRIMARY),
        BOTH(AffiliatedCareTeamType.REGULAR_AND_PRIMARY);

        private final AffiliatedCareTeamType type;

        Affiliation(AffiliatedCareTeamType type) {
            this.type = type;
        }

        public AffiliatedCareTeamType toType() {
            return type;
        }
    }
}
