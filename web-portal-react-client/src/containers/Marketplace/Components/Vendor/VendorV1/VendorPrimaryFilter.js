import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { PrimaryFilter } from "../../../../../components";
import { useAuthUser } from "../../../../../hooks/common";
import { setCommunity, setOrganization } from "../../../../../redux/Vendor/vendorActions";
import { useOrganizationsQuery, useCommunitiesQuery } from "../../../../../hooks/business/directory/query";
import directory from "../../../../../redux/directory/directoryReducer";

const PrimaryFilterWrapper = () => {
  const dispatch = useDispatch();
  const { organizationId, communityId } = useSelector((state) => state.Vendor);
  const user = useAuthUser();

  const { data: organizations } = useOrganizationsQuery(); // Hook to fetch organizations
  const { data: communities = [] } = useCommunitiesQuery(
    { organizationId },
    {
      enabled: !!organizationId, // Fetch communities only if organizationId is truthy
    },
  ); // Hook to fetch communities

  useEffect(() => {
    if (user) {
      const defaultOrganizationId = user.organizationId;
      const defaultCommunityId = user.communityId;

      dispatch(setOrganization(defaultOrganizationId));

      dispatch(setCommunity(defaultCommunityId));
    }
  }, [user.organizationId, user.communityId]);

  useEffect(() => {
    if (organizationId && communities.length > 0) {
      dispatch(setCommunity(communities[0].id));
    }
  }, [organizationId, communities]);

  const handleOrganizationChange = async (newOrganizationId) => {
    dispatch(setOrganization(newOrganizationId));
  };

  const handleCommunityChange = (newCommunityId) => {
    dispatch(setCommunity(newCommunityId));
  };

  return (
    <PrimaryFilter
      organizations={organizations} // Pass organizations as prop to PrimaryFilter
      communities={communities}
      data={{ organizationId, communityId }} // Set communityFieldName dynamically
      onChangeOrganizationField={handleOrganizationChange}
      onChangeCommunityField={handleCommunityChange}
      isCommunityMultiSelection={false}
    />
  );
};

export default PrimaryFilterWrapper;
