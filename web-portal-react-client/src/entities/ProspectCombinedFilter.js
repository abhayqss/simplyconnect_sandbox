import ProspectFilter from "./ProspectFilter";

const { Record } = require("immutable");

const ProspectCombinedFilter = Record({
  organizationId: null,
  communityIds: [],
  ...ProspectFilter().toJS(),
});

export default ProspectCombinedFilter;
