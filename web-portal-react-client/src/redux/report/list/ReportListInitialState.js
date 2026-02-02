import { State } from "redux/utils/List";

const { Record } = require("immutable");

export default State(
  {
    errors: null,
    isFilterValid: true,
    dataSource: Record({
      filter: Record({
        organizationId: null,
        communityIds: [],
        clientIds: [],
        workflowId: null,
        reportType: null,
        fromDate: null,
        toDate: null,
      })(),
    })(),
  },
  {
    isPageable: false,
    isSortable: false,
  },
);
