package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.Client;

import java.util.List;

public interface IdCommunityIdAssociatedEmployeeIdsAware extends IdCommunityIdAware, AssociatedEmployeeIdsAware {

    static IdCommunityIdAssociatedEmployeeIdsAware of(Long id, Long communityId, Long associatedEmployeeId) {
        return new IdCommunityIdAssociatedEmployeeIdsAware() {

            @Override
            public Long getId() {
                return id;
            }

            @Override
            public Long getCommunityId() {
                return communityId;
            }

            @Override
            public List<Long> getAssociatedEmployeeIds() {
                return associatedEmployeeId == null
                        ? List.of()
                        : List.of(associatedEmployeeId);
            }
        };
    }

    static IdCommunityIdAssociatedEmployeeIdsAware of(Client client) {
        return new IdCommunityIdAssociatedEmployeeIdsAware() {

            @Override
            public Long getId() {
                return client.getId();
            }

            @Override
            public Long getCommunityId() {
                return client.getCommunityId();
            }

            @Override
            public List<Long> getAssociatedEmployeeIds() {
                return client.getAssociatedEmployeeIds();
            }
        };
    }
}
