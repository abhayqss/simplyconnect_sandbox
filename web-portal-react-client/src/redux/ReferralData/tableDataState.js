// initialState.js
const initialState = {
  inboundData: [],
  sorting: {
    field: null,
    order: null,
  },
  pagination: {
    page: 1,
    size: 15,
    totalCount: 0,
  },

  outboundData: [],
};

export default initialState;
