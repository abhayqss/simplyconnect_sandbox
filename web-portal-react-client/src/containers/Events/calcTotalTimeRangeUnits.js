import { DateUtils as DU } from "lib/utils/Utils";

const { diffDates } = DU;

const UNIT_TIME = 15;

export default function calcTotalTimeRangeUnits(from, to) {
  console.log(from, to, "form ,to");
  if (!from || !to) {
    return { units: null, totalTime: null, range: null };
  }

  let totalTime = Math.floor(diffDates(from, to) / (60 * 1000));

  let units = Math.floor(totalTime / UNIT_TIME);
  let remainder = totalTime % UNIT_TIME;

  if (remainder > 7) {
    units += 1;
  }

  let startRange = units * UNIT_TIME - 7;
  let endRange = units * UNIT_TIME + 7;

  if (startRange < 0) {
    startRange = 0;
  }

  return {
    units: units.toString(),
    totalTime: totalTime.toString(),
    range: `${startRange} mins - ${endRange} mins`,
  };
}
