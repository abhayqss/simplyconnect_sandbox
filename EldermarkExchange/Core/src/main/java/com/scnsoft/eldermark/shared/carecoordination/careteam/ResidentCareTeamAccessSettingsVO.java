package com.scnsoft.eldermark.shared.carecoordination.careteam;

import java.util.List;
import java.util.Map;

public class ResidentCareTeamAccessSettingsVO {
    List<Long> viewableResidentIds;
    Map<Long, List<Long>> notViewableResidentIdsWithEventTypes;

    public ResidentCareTeamAccessSettingsVO(List<Long> viewableResidentIds, Map<Long, List<Long>> notViewableResidentIdsWithEventTypes) {
        this.viewableResidentIds = viewableResidentIds;
        this.notViewableResidentIdsWithEventTypes = notViewableResidentIdsWithEventTypes;
    }

    public List<Long> getViewableResidentIds() {
        return viewableResidentIds;
    }

    public Map<Long, List<Long>> getNotViewableResidentIdsWithEventTypes() {
        return notViewableResidentIdsWithEventTypes;
    }
}
