import { State } from "redux/utils/List";
import { getEndOfDayTime, getStartOfDayTime } from "../../../../../lib/utils/DateUtils";
import { add } from "date-arithmetic";

const { Record } = require("immutable");
function threeMonthsAgo() {
  return add(new Date(), -3, "month");
}

export default State({
  error: null,
  isFetching: false,
  shouldReload: false,
  dataSource: Record({
    data: [],
    pagination: Record({
      page: 1,
      size: 15,
      totalCount: 0,
    })(),
    filter: Record({
      clientId: null,
      communityIds: [],
      organizationId: null,
      eventTypeId: null,
      noteTypeId: null,
      fromDate: getStartOfDayTime(threeMonthsAgo()),
      toDate: getEndOfDayTime(Date.now()),
      onlyEventsWithIR: false,
    })(),
  })(),
});
