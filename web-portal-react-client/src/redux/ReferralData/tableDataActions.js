import { SET_INBOUND_DATA, SET_PAGINATION, SET_SORT, SET_OUTBOUND_DATA } from "./actionTypes";

export const setInboundData = (data) => ({
  type: SET_INBOUND_DATA,
  payload: data,
});

export const setPagination = (data) => ({
  type: SET_PAGINATION,
  payload: data,
});

export const setSort = (data) => ({
  type: SET_SORT,
  payload: data,
});

export const setOutboundData = (data) => ({
  type: SET_OUTBOUND_DATA,
  payload: data,
});
