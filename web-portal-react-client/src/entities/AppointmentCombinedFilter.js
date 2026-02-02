import AppointmentFilter from "./AppointmentFilter";

const { Record } = require("immutable");

const AppointmentCombinedFilter = Record({
  organizationId: null,
  communityIds: [],
  ...AppointmentFilter().toJS(),
});

export default AppointmentCombinedFilter;
