import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { PrimaryFilter } from "../../../../../components";
import { useAuthUser } from "../../../../../hooks/common";
import { setCommunity, setOrganization } from "../../../../../redux/Vendor/vendorActions";
import { useOrganizationsQuery, useCommunitiesQuery } from "../../../../../hooks/business/directory/query";

const PrimaryFilterWrapper = () => {
  const dispatch = useDispatch();
  const { organizationId, communityId } = useSelector((state) => state.Vendor);
  const user = useAuthUser();

  const { data: organizations } = useOrganizationsQuery();
  const { data: communities = [] } = useCommunitiesQuery(
    { organizationId },
    {
      enabled: !!organizationId,
    },
  );

  // 组件初始化时从 localStorage 恢复选中状态
  useEffect(() => {
    const storedOrganizationId = Number(localStorage.getItem("vendorSelectedOrganizationId"));
    const storedCommunityId = Number(localStorage.getItem("vendorSelectedCommunityId"));

    if (storedOrganizationId && storedCommunityId) {
      dispatch(setOrganization(storedOrganizationId));
      dispatch(setCommunity(storedCommunityId));
    } else if (user) {
      const defaultOrganizationId = user.organizationId;
      const defaultCommunityId = user.communityId;

      dispatch(setOrganization(defaultOrganizationId));
      dispatch(setCommunity(defaultCommunityId));
    }
  }, []);

  useEffect(() => {
    if (organizationId && communities.length > 0) {
      const storedCommunityId = Number(localStorage.getItem("vendorSelectedCommunityId"));
      if (storedCommunityId) {
        dispatch(setCommunity(storedCommunityId));
        return;
      }

      dispatch(setCommunity(communities[0].id));
      localStorage.setItem("vendorSelectedCommunityId", communities[0].id);
    }
  }, [organizationId, communities]);

  // 选择组织时存储到 localStorage，并选择第一个社区
  const handleOrganizationChange = async (newOrganizationId) => {
    dispatch(setOrganization(newOrganizationId));
    localStorage.setItem("vendorSelectedOrganizationId", newOrganizationId);
    localStorage.removeItem("vendorSelectedCommunityId");
  };

  // 选择社区时存储到 localStorage
  const handleCommunityChange = (newCommunityId) => {
    dispatch(setCommunity(newCommunityId));
    localStorage.setItem("vendorSelectedCommunityId", newCommunityId);
  };

  return (
    <PrimaryFilter
      organizations={organizations}
      communities={communities}
      data={{ organizationId, communityId }}
      onChangeOrganizationField={handleOrganizationChange}
      onChangeCommunityField={handleCommunityChange}
      isCommunityMultiSelection={false}
    />
  );
};

export default PrimaryFilterWrapper;
