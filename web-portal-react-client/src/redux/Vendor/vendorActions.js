// actions/vendorActions.js
export const SET_ORGANIZATION = "SET_ORGANIZATION";
export const SET_COMMUNITY = "SET_COMMUNITY";

export const setOrganization = (organizationId) => ({
  type: SET_ORGANIZATION,
  payload: organizationId,
});

export const setCommunity = (communityId) => ({
  type: SET_COMMUNITY,
  payload: communityId,
});
