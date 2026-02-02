import moment from "moment";
import * as PNUtils from "libphonenumber-js";
import momentDurationSetup from "moment-duration-format";

import _, { each } from "underscore";

import WebError from "../errors/WebError";

import {
  FILE_SIZE_FORMATS,
  SERVER_ERROR_CODES,
  PASSWORD_VALIDATIONS,
  PASSWORD_VALIDATION_TYPES,
  ALLOWED_FILE_FORMAT_MIME_TYPES,
} from "../Constants";

import { isObject, isEmpty as isEmptyObject } from "./ObjectUtils";

import { isString, isEmpty as isEmptyStr } from "./StringUtils";

import { isArray, isEmpty as isEmptyArray } from "./ArrayUtils";

const Dates = require("date-arithmetic");

const { MB } = FILE_SIZE_FORMATS;

const EXPONENTIAL_POWER_VARIANTS_FOR_SIZES = { KB: 1, MB: 2, GB: 3 };

const { NO_CLIENT_INFO_FOUND, NO_CONNECTION_OR_SERVER_IS_NOT_AVAILABLE } = SERVER_ERROR_CODES;

const MS_IN_SEC = 1000;
const MS_IN_MIN = MS_IN_SEC * 60;
const MS_IN_HOUR = MS_IN_MIN * 60;
const MS_IN_DAY = MS_IN_HOUR * 24;
const MS_IN_WEEK = MS_IN_DAY * 7;

const { DIGIT, LOWERCASE_LETTER, UPPERCASE_LETTER, SPECIAL_CHARACTERS } = PASSWORD_VALIDATION_TYPES;

momentDurationSetup(moment);

moment.fn.meridiem = function (newMeridiem) {
  if (!newMeridiem) {
    return this.format("A");
  }

  if (newMeridiem.toUpperCase() === "AM" && this.hours() > 12) {
    this.hours(this.hours() - 12);
  } else if (newMeridiem.toUpperCase() === "PM" && this.hours() <= 12) {
    this.hours(this.hours() + 12);
  }

  return this;
};

export function emptyPromise() {
  return new Promise((resolve) => {
    resolve(null);
  });
}

export function logicalXOR(a, b) {
  return !a !== !b;
}

export function capitalize(str) {
  return str ? str.charAt(0).toUpperCase() + lc(str.slice(1)) : str;
}

export function isNoConnectionOrServerIsNotAvailableError(error) {
  return error && error.error && error.error.code === NO_CONNECTION_OR_SERVER_IS_NOT_AVAILABLE;
}

export function isNoPatientInfoFoundError(error) {
  return error && error.error && error.error.code === NO_CLIENT_INFO_FOUND;
}

export function getTitleFromErrorCode(code = "") {
  return capitalize(code.split(".").join(" "));
}

export function getFieldErrorProps(obj) {
  if (!obj || typeof obj !== "object") {
    return [];
  }

  let props = _.keys(obj);
  return _.filter(props, (prop) => ~prop.indexOf("HasError") || ~prop.indexOf("ErrorMsg"));
}

export function parseEnumToWords(value, capitalizeFirst = false, capitalizeAll = false) {
  if (!value) {
    return value;
  }

  let words = value.split("_");
  let str = "";
  _.each(words, (w, idx) => {
    let part = w.toLowerCase() + " ";
    str += capitalizeAll || (capitalizeFirst && idx === 0) ? capitalize(part) : part;
  });

  return str.trim();
}

export function measure(node) {
  return node?.getBoundingClientRect();
}

function isDateValid(date) {
  // An invalid date object returns NaN for getTime()
  // and NaN is the only object not strictly equal to itself
  return _.isNaN(date.getTime());
}

export function getAge(birthdayStr) {
  let birthday = new Date(birthdayStr);

  if (!birthdayStr || !isDateValid(birthday)) {
    return null;
  }

  let today = new Date();
  let age = today.getFullYear() - birthday.getFullYear();

  birthday.setFullYear(today.getFullYear());

  // if the birthday has not occurred yet this year, subtract 1
  if (today < birthday) {
    age--;
  }

  return age;
}

function pad(val, len) {
  val = String(val);
  len = len || 2;
  while (val.length < len) {
    val = "0" + val;
  }
  return val;
}

function getTimeZoneAbbr(date) {
  date = date || new Date();

  if (!(date instanceof Date)) {
    date = new Date(date);
  }

  let timeString = date.toTimeString();
  let abbr = timeString.match(/\([a-z ]+\)/i);
  if (abbr && abbr[0]) {
    // 17:56:31 GMT-0600 (CST)
    // 17:56:31 GMT-0600 (Central Standard Time)
    abbr = abbr[0].match(/[A-Z]/g);
    abbr = abbr ? abbr.join("") : undefined;
  } else {
    // 17:56:31 CST
    // 17:56:31 GMT+0800 (台北標準時間)
    abbr = timeString.match(/[A-Z]{3,5}/g);
    abbr = abbr ? abbr[0] : undefined;
  }

  return abbr;
}

export function compareVersions(v1, v2) {
  if (typeof v1 === "string" && typeof v2 === "string") {
    const digs1 = v1.split(".");
    const digs2 = v2.split(".");
    const max = Math.max(digs1.length, digs2.length);
    for (let i = 0; i < max; i++) {
      const a = i < digs1.length ? parseInt(digs1[i]) : 0;
      const b = i < digs2.length ? parseInt(digs2[i]) : 0;
      if (a > b) return 1;
      if (b > a) return -1;
    }
    return 0;
  } else throw new TypeError('All arguments must have "string" type');
}

export function isNotEqual(a, b) {
  return !_.isEqual(a, b);
}

export function isNullOrUndefined(v) {
  return v === null || v === void 0;
}

export function allAreNullOrUndefined(...args) {
  return _.every(args, (v) => isNullOrUndefined(v));
}

export function allAreNotNullOrUndefined(...args) {
  return _.every(args, (v) => !isNullOrUndefined(v));
}

export function isNull(v) {
  return v === null;
}

export function allAreNull(...args) {
  return _.every(args, (v) => isNull(v));
}

export function allAreNotNull(...args) {
  return _.every(args, (v) => !isNull(v));
}

export function anyIsNull(...args) {
  return _.any(args, (v) => isNull(v));
}

export function isNotNull(v) {
  return !isNull(v);
}

export function isNaN(v) {
  return _.isNumber(v) && v !== +v;
}

export function isUndefined(v) {
  return v === void 0;
}

export function allAreNotUndefined(...args) {
  return _.every(args, (v) => !isUndefined(v));
}

export function isNotNullOrUndefined(v) {
  return !isNull(v) && !isUndefined(v);
}

export function isBoolean(o) {
  return typeof o === "boolean";
}

// 0 - is not empty value
export function isEmpty(v, opts = {}) {
  const { allowEmptyBool = true } = opts;

  if ([null, undefined, NaN].includes(v)) return true;
  if (isString(v)) return isEmptyStr(v);
  if (isArray(v)) return isEmptyArray(v);
  if (isObject(v)) return isEmptyObject(v);
  if (isBoolean(v)) return !v && allowEmptyBool;

  return false;
}

// 0 - is not empty value
export function compact(o) {
  return _.without(o, false, null, "", undefined, NaN);
}

export function isNotEmpty(...args) {
  return !isEmpty(...args);
}

export function isTrueOrUndefined(v) {
  return v === true || isUndefined(v);
}

export function withoutEmptyValues(o) {
  return _.without(o, (v) => isEmpty(v));
}

export function omitEmptyProps(o) {
  return _.omit(o, (v, k) => isEmpty(v));
}

export function omitEmptyPropsDeep(o) {
  let map = isArray(o) ? _.map : _.mapObject;

  return map(omitEmptyProps(o), (v) => (isObject(v) ? omitEmptyPropsDeep(v) : v));
}

export function all(...args) {
  return {
    in: function (...vals) {
      return _.all(args, (o) => _.any(vals, (v) => o === v));
    },
    notIn: function (...vals) {
      return _.all(args, (o) => !_.any(vals, (v) => o === v));
    },
    equal: function (v) {
      return _.all(args, (o) => o === v);
    },
    notEqual: function (v) {
      return _.all(args, (o) => o !== v);
    },
  };
}

export function allAreEmpty(...args) {
  return _.all(args, (o) => isEmpty(o));
}

export function allAreNotEmpty(...args) {
  return _.all(args, (o) => isNotEmpty(o));
}

export function anyIsFalse(...args) {
  return _.any(args, (o) => o === false);
}

export function allAreFalse(...args) {
  return _.all(args, (o) => o === false);
}

export function allAreNotFalse(...args) {
  return _.all(args, (o) => o !== false);
}

export function allAreTrue(...args) {
  return _.all(args, (o) => o === true);
}

export function allAreTrueOrNull(...args) {
  return _.all(args, (o) => o === true || o === null);
}

export function values(o, ...keys) {
  return _.values(_.pick.apply(null, o, keys));
}

export function isNumber(v) {
  return typeof v === "number";
}

export function isInteger(v) {
  return Number.isInteger(v);
}

export function isPrimitive(v) {
  return [null, undefined].includes(v) || ["string", "number", "bigint", "boolean", "symbol"].includes(typeof v);
}

export function anyIsEmpty(...args) {
  return _.any(args, (o) => isEmpty(o));
}

export function anyIsNotEmpty(...args) {
  return _.any(args, (o) => isNotEmpty(o));
}

export function anyIsInteger(...args) {
  return _.any(args, (v) => isInteger(v));
}

export function allAreInteger(...args) {
  return _.all(args, (v) => isInteger(v));
}

export function toNumberExcept(v, excluded) {
  return excluded.includes(v) ? v : Number(v);
}

export function toBoolean(value) {
  return [true, "true", 1, "1", "on", "yes"].includes(value);
}

export function omitDeep(o, ...keys) {
  return _.mapObject(_.omit(o, ...keys), (v) => (isObject(v) && !isArray(v) ? omitDeep(v, ...keys) : v));
}

export function pickAs(o, ...keys) {
  const picked = _.pick(
    o,
    _.filter(keys, (k) => isString(k)),
  );

  const changes = _.filter(keys, (k) => isObject(k));

  _.each(changes, (ov) => {
    _.each(ov, (name, oldName) => {
      picked[name] = o[oldName];
    });
  });

  return picked;
}

export function includes(s) {
  return {
    any: function (...args) {
      return _.any(args, (o) => s.includes(o));
    },
    all: function (...args) {
      return _.all(args, (o) => s.includes(o));
    },
  };
}

export function formChangeChecker(o1, o2) {
  const filter = (v, k) => !(k.includes("HasError") || k.includes("ErrorText"));

  const firstObjectFields = _.pick(o1, filter);
  const secondObjectFields = _.pick(o2, filter);

  return _.isEqual(firstObjectFields, secondObjectFields);
}

export function hasObjectPrototype(o) {
  return Object.prototype.toString.call(o) === "[object Object]";
}

export function isPlainObject(o) {
  if (!hasObjectPrototype(o)) {
    return false;
  } // If has modified constructor

  const ctor = o.constructor;

  if (typeof ctor === "undefined") {
    return true;
  } // If has modified prototype

  const prot = ctor.prototype;

  if (!hasObjectPrototype(prot)) {
    return false;
  } // If constructor does not have an Object-specific method

  if (!_.has(prot, "isPrototypeOf")) {
    return false;
  } // Most likely a plain Object

  return true;
}

function stableStringifyReplacer(_key, value) {
  if (typeof value === "function") {
    throw new Error();
  }

  if (isPlainObject(value)) {
    return Object.keys(value)
      .sort()
      .reduce(function (result, key) {
        result[key] = value[key];
        return result;
      }, {});
  }

  return value;
}

export function stableStringify(value) {
  return JSON.stringify(value, stableStringifyReplacer);
}

export function parseJSON(text) {
  try {
    return JSON.parse(text);
  } catch (e) {
    console.log(e);
  }
}

export class Cache {
  #cache = {};

  #serializeKey = (key) => stableStringify(isArray(key) ? key : [key]);

  set(key, data) {
    this.#cache[this.#serializeKey(key)] = data;
  }

  get(key) {
    return this.#cache[isString(key) ? key : this.#serializeKey(key)];
  }

  keys() {
    return Object.keys(this.#cache);
  }
}

export class Time {
  saved = 0;

  now() {
    return Date.now();
  }

  save() {
    this.saved = this.now();
  }

  passedFromSaved() {
    return this.now() - this.saved;
  }
}

export class Timer {
  #time = 0;
  #maxTime = 0;
  #intervalId = null;

  constructor({ step = 1000, maxTime }) {
    this.step = step;
    this.#maxTime = maxTime;
  }

  onTimeChange() {}

  get currentTime() {
    return this.#time;
  }

  set currentTime(value) {
    this.#time = value;
    this.onTimeChange(value);
  }

  countdown(onCountdownSuccess) {
    this.#intervalId = setInterval(() => {
      if (this.currentTime >= this.#maxTime) {
        this.stop();
        onCountdownSuccess();
      }

      this.currentTime++;
    }, this.step);
  }

  stop() {
    this.currentTime = 0;
    clearInterval(this.#intervalId);
  }
}

export let DateUtils = {
  ...{
    MILLI: {
      second: MS_IN_SEC,
      minute: MS_IN_MIN,
      hour: MS_IN_HOUR,
      day: MS_IN_DAY,
      week: MS_IN_WEEK,
    },

    format: function (date, format, utc) {
      if (_.isNumber(date) || _.isDate(date)) {
        let df = DateUtils.formats;
        let token = /d{1,4}|M{1,4}|YY(?:YY)?|([HhmsAa])\1?|[oS]|[zZ]/g;

        if (_.isNumber(date)) date = new Date(date);

        format = String(df[format] || format || df["default"]);

        let abbr = getTimeZoneAbbr(date);

        let prf = utc ? "getUTC" : "get";
        let d = date[prf + "Date"]();
        let D = date[prf + "Day"]();
        let M = date[prf + "Month"]();
        let Y = date[prf + "FullYear"]();
        let H = date[prf + "Hours"]();
        let m = date[prf + "Minutes"]();
        let s = date[prf + "Seconds"]();
        let o = utc ? 0 : date.getTimezoneOffset();
        let z = abbr.toLowerCase();
        let Z = abbr.toUpperCase();
        let flags = {
          d: d,
          dd: pad(d),
          ddd: DateUtils.i18n.dayNames[D],
          dddd: DateUtils.i18n.dayNames[D + 7],
          M: M + 1,
          MM: pad(M + 1),
          MMM: DateUtils.i18n.monthNames[M],
          MMMM: DateUtils.i18n.monthNames[M + 12],
          YY: String(Y).slice(2),
          YYYY: Y,
          h: H % 12 || 12,
          hh: pad(H % 12 || 12),
          H: H,
          HH: pad(H),
          m: m,
          mm: pad(m),
          s: s,
          ss: pad(s),
          a: H < 12 ? "a" : "p",
          aa: H < 12 ? "am" : "pm",
          A: H < 12 ? "A" : "P",
          AA: H < 12 ? "AM" : "PM",
          o: (o > 0 ? "-" : "+") + pad(Math.floor(Math.abs(o) / 60) * 100 + (Math.abs(o) % 60), 4),
          S: ["th", "st", "nd", "rd"][d % 10 > 3 ? 0 : (((d % 100) - (d % 10) !== 10) * d) % 10],
          z: "(" + z + ")",
          Z: "(" + Z + ")",
        };

        return format.replace(token, function (t) {
          return t in flags ? flags[t] : t.slice(1, t.length - 1);
        });
      }

      return "";
    },

    formats: {
      default: "YYYY/MM/dd",
      time: "hh:mm AA",
      shortDate: "MM/dd",
      mediumDate: "YY/MM/dd",
      americanShortDate: "M/d/YYYY",
      americanMediumDate: "MM/dd/YYYY",
      americanMediumTime: "MM/dd/YYYY HH:mm",
      separatedYearDate: "MMM dd, YYYY",
      separatedYearDateTime: "MMM dd, YYYY hh:mm AA",
      longDate: "YYYY/MM/dd",
      longDateTime: "YYYY/MM/dd HH:mm:ss",
      longDateMediumTime12: "MM/dd/YYYY hh:mm AA",
      longDateMediumTime12TimeZone: "MM/dd/YYYY hh:mm AA Z",
      isoDateTime: "YYYY-MM-ddTHH:mm:ss.000",
    },

    i18n: {
      dayNames: [
        "Sun",
        "Mon",
        "Tue",
        "Wed",
        "Thu",
        "Fri",
        "Sat",
        "Sunday",
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
      ],
      monthNames: [
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec",
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December",
      ],
    },

    diffDates: function (date1, date2, unit) {
      const diffInMs = Math.abs(+date1 - +date2);
      if (!unit) {
        return diffInMs;
      }

      unit = unit.toLowerCase();
      const unitInMs = DateUtils.MILLI[unit];
      if (unitInMs) {
        return diffInMs / unitInMs;
      } else if (unit === "month" || unit === "year") {
        const date1Less = date1 < date2;
        const start = date1Less ? date1 : date2;
        const end = date1Less ? date2 : date1;
        let years = DateUtils.year(end) - DateUtils.year(start);
        let months = years * 12 + DateUtils.month(end) - DateUtils.month(start);
        let days = DateUtils.date(end) - DateUtils.date(start);
        if (days >= 0) {
          months++;
        }
        return unit === "month" ? months : months / 12;
      } else {
        return diffInMs;
      }
    },

    isTomorrow: function (date) {
      let d1 = DateUtils.startOf(new Date(), "day");
      let d2 = DateUtils.startOf(date, "day");
      return DateUtils.diff(d1, d2, "day") === 1;
    },

    isToday: function (date) {
      let d1 = DateUtils.startOf(new Date(), "day");
      let d2 = DateUtils.startOf(date, "day");
      return DateUtils.diff(d1, d2, "day") === 0;
    },

    isYesterday: function (date) {
      let d1 = DateUtils.startOf(new Date(), "day");
      let d2 = DateUtils.startOf(date, "day");
      return DateUtils.diff(d2, d1, "day") === 1;
    },

    startOf: function (date, unit) {
      return DateUtils.startOf(date, unit);
    },

    endOf: function (date, unit) {
      return DateUtils.endOf(date, unit);
    },

    parseSecDuration(duration) {
      const secPerMinute = 60;
      const secPerHour = 3600;

      let hours = Math.floor(duration / secPerHour);
      duration = duration - hours * secPerHour;
      let minutes = Math.floor(duration / secPerMinute);
      let seconds = duration - minutes * secPerMinute;

      return (hours ? `${hours}h:` : "") + (minutes ? `${minutes}m:` : "") + `${seconds}s`;
    },

    splitByMonth(start, end) {
      const endDate = end || new Date().getTime();
      const startDate = DateUtils.startOf(start || endDate, "month");
      let d = DateUtils.startOf(endDate, "month").getTime();
      let values = [];
      while (d >= startDate) {
        values.push(d);
        d = DateUtils.add(d, -1, "month").getTime();
      }
      return values;
    },

    daysInMonth: function (year, month) {
      return new Date(year, month, 0).getDate();
    },

    getTimeZoneAbbr: getTimeZoneAbbr,

    toUTC: function (v) {
      const date = v instanceof Date ? v : new Date(v);

      return new Date(date.getTime() + date.getTimezoneOffset() * 60000);
    },

    localize: function (v) {
      const date = v instanceof Date ? v : new Date(v);

      return new Date(date.getTime() - date.getTimezoneOffset() * 60000);
    },

    formatDuration: function (value, unit = "ms", format = "h[h] m[m] s[s]") {
      return value ? moment.duration(value, unit).format(format) : "";
    },

    getTodayStartOfDayTime: function () {
      return Dates.startOf(Date.now(), "day").getTime();
    },

    getTodayEndOfDayTime: function () {
      return Dates.endOf(Date.now(), "day").getTime();
    },
  },
  ...Dates,
};

export const PhoneNumberUtils = {
  ...PNUtils,

  formatPhoneNumber(v, { defaultCountry = "US", ...other } = {}) {
    if (!v) return "";
    const asYouType = new PNUtils.AsYouType({ defaultCountry, ...other });
    return asYouType.input(v);
  },
};

export function getRandomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

export function getRandomFloat(min, max, precision) {
  return min + Number((Math.random() * (max - min + 1)).toFixed(precision));
}

export function getAddress({ city = "", street = "", state = "", stateName = "", zip = "" } = {}, sep = "") {
  const s = `${street ? street + sep : ""} ${city ? city + sep : ""} ${state ? state + sep : stateName ? stateName + sep : ""} ${zip || ""}`;
  return s.replace(/\s+/g, " ");
}

export function getStandardFormattedAddress({ city = "", street = "", state = "", zip = "" } = {}) {
  const s = `${street ? street + "," : ""} ${city ? city + "," : ""} ${state ?? ""} ${zip || ""}`;
  return s.replace(/\s+/g, " ");
}

export function camel(s) {
  return s
    .replace(/(?:^\w|[A-Z]|\b\w)/g, (letter, index) => {
      return index === 0 ? letter.toLowerCase() : letter.toUpperCase();
    })
    .replace(/(\s|'|"|-)+/g, "");
}

export function defer(delay = 0, ...args) {
  return new Promise((resolve) => {
    return setTimeout(() => {
      resolve.apply(null, args);
    }, delay);
  });
}

export function deferred(fn) {
  return function (...args) {
    _.defer(fn, ...args);
  };
}

export function delay(delay = 0, ...args) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve.apply(null, args);
    }, delay);
  });
}

export function promise(...args) {
  return Promise.resolve(...args);
}

export function interpolate(s, ...vals) {
  for (let i = 0; i < vals.length; i++) {
    s = s.replace(new RegExp(`\\$${i}`, "g"), vals[i]);
  }
  return s;
}

export function isEqLn(a = [], b = []) {
  return a?.length === b?.length;
}

export function addMissing(dest, ...sources) {
  return dest.concat(...sources);
}

export function getFileFormatByMimeType(mime) {
  return _.findKey(ALLOWED_FILE_FORMAT_MIME_TYPES, (v) => v === mime);
}

export function getDataPage(data, pagination) {
  const { page, size } = pagination;

  return _.filter(data, (o, i) => {
    const start = page * size;
    const end = start + size;
    return i >= start && i < end;
  });
}

export function trimStartIfString(v) {
  return isString(v) ? v.trimStart() : v;
}

export function camelToRegular(s) {
  return s.replace(/([A-Z])/g, " $1").replace(/^./, function (str) {
    return str.toUpperCase();
  });
}

// My  favourite   - color \is green and somethingElse -> my-favourite-color-is-green-and-something-else
export function hyphenate(...vals) {
  return _.map(vals, (v) =>
    v
      .trim()
      .replace(/(\s*-+\s*)|\s+|\.|\\|\//g, "-")
      .replace(/([a-zA-Z])(?=[A-Z])/g, "$1-")
      .replace("#", "-")
      .toLowerCase(),
  ).join("-");
}

// my-favourite-color-is-green -> My Favourite Color Is Green
export function hyphenatedToTitle(s) {
  return _.map(s.split("-"), (w) => capitalize(w)).join(" ");
}

//my_favourite_color -> my favourite color (capitalizeFirst: My favourite color; capitalizeAll: My Favourite Color)
export function snakeToTitle(s, capitalizeFirst = false, capitalizeAll = false) {
  return _.map(s.split("_"), (w, idx) => (capitalizeAll || (capitalizeFirst && idx === 0) ? capitalize(w) : w)).join(
    " ",
  );
}

export function containsIgnoreCase(a, b) {
  if (a === null || b === null) return;
  return a?.toLowerCase()?.includes(b?.toLowerCase());
}

export function getDataUrl(data, mediaType, isBase64 = true) {
  return `data:${mediaType ? `${mediaType};` : ""}${isBase64 ? "base64" : ""},${data}`;
}

export function repeatStringNumTimes(str, times) {
  let repeatedStr = "";

  while (times > 0) {
    repeatedStr += str;
    times--;
  }
  return repeatedStr;
}

const getInitialsFromOneWord = ifElse(
  (word = "") => word.length > 1,
  (word = "") => `${_.get(word, 0)}${_.get(word, 1)}`,
  (word = "") => _.first(word),
);

const getInitialsFromTwoWords = (first = "", second = "") => {
  return first.charAt(0) + second.charAt(0);
};

export const getInitialsFromString = ifElse(
  (first, second) => !!second,
  getInitialsFromTwoWords,
  getInitialsFromOneWord,
);

const getInitialsFromFullName = (fullName) => {
  const [firstName, lastName] = fullName.trim().split(/ +/gm);

  return getInitialsFromString(firstName, lastName);
};

export const getInitials = ifElse(
  ({ fullName } = {}) => !!fullName,
  ({ fullName } = {}) => getInitialsFromFullName(fullName),
  ({ firstName, lastName } = {}) => getInitialsFromTwoWords(firstName, lastName),
);

export function getFullName(o) {
  return o ? `${o.firstName} ${o.lastName}` : "";
}

export function getPasswordRegExp(options) {
  const { length, upperCaseCount, lowerCaseCount, alphabeticCount, arabicNumeralCount, nonAlphaNumeralCount } = options;

  let pattern = `(?=.{${length ?? 6},}$)`;

  if (alphabeticCount) {
    pattern += `(?=[a-zA-Z]{${alphabeticCount}})`;
  }

  if (arabicNumeralCount) {
    pattern += `(?=${repeatStringNumTimes(PASSWORD_VALIDATIONS[DIGIT], arabicNumeralCount)})`;
  }

  if (upperCaseCount) {
    pattern += `(?=${repeatStringNumTimes(PASSWORD_VALIDATIONS[UPPERCASE_LETTER], upperCaseCount)})`;
  }

  if (lowerCaseCount) {
    pattern += `(?=${repeatStringNumTimes(PASSWORD_VALIDATIONS[LOWERCASE_LETTER], lowerCaseCount)})`;
  }

  if (nonAlphaNumeralCount) {
    pattern += `(?=${repeatStringNumTimes(PASSWORD_VALIDATIONS[SPECIAL_CHARACTERS], nonAlphaNumeralCount)})`;
  }

  return new RegExp(pattern);
}

export function setInObject(o, path, value) {
  path = path.split(/[[\]\.]+/);

  while (path.length - 1) {
    let n = path.shift();
    if (!(n in o)) o[n] = {};
    o = o[n];
  }

  o[path[0]] = value;
}

export function lc(s) {
  return s?.toLowerCase();
}

export function uc(s) {
  return s?.toUpperCase();
}

function buildMonthCalendar() {
  let now = moment();

  let calendar = [];
  let startDay = now.clone().startOf("month").startOf("week");
  let endDay = now.clone().endOf("month").endOf("week");

  startDay = startDay.clone().subtract(1, "day");

  while (startDay.isBefore(endDay, "day")) {
    calendar.push(startDay.add(1, "day").clone());
  }

  return calendar;
}

export function buildCalendar(unit) {
  switch (unit) {
    case "month":
      return buildMonthCalendar();

    default:
      return buildMonthCalendar();
  }
}
export function convertListToObjectBy(list = [], key) {
  const o = {};

  each(list, (x) => {
    o[x[key]] = x;
  });

  return o;
}

export function getFirstNodeInViewPort(nodes, viewPortTop) {
  return Array.prototype.find.call(nodes, (node) => measure(node).y >= viewPortTop);
}

export function getLastNodeIndexInViewPort(nodes, viewPortBottom) {
  let index = Array.prototype.findIndex.call(nodes, (node) => measure(node).bottom >= viewPortBottom);

  return nodes?.length > 0 ? (index < 0 ? nodes.length - 1 : index) : null;
}

export class AbortionController {
  #controller = null;

  constructor() {
    this.#controller = new AbortController();
  }

  get signal() {
    return this.#controller.signal;
  }

  abort() {
    this.#controller.abort();
    this.#controller = new AbortController();
  }
}

export function cancelable(fn) {
  let controller = new AbortController();

  function Error(payload) {
    return new WebError({ message: "Cancelled", body: payload });
  }

  function cleanup() {
    controller = new AbortController();
  }

  function cancel() {
    controller.abort();
    cleanup();
  }

  function wrappedFn(...args) {
    cancel();

    let isAborted = false;

    return new Promise((resolve, reject) => {
      controller.signal.onabort = () => {
        isAborted = true;
      };

      fn(...args)
        .then((result) => {
          isAborted ? reject(Error(result)) : resolve(result);
        })
        .catch(reject);
    });
  }

  return [wrappedFn, cancel];
}

export function ifElse(condition, whenTrue = _.noop, whenFalse = _.noop) {
  return function (...args) {
    return condition(...args) ? whenTrue(...args) : whenFalse(...args);
  };
}

export function asyncTimes(n, fn, interval = 0) {
  let i = 0;
  const o = setInterval(() => {
    if (fn && _.isFunction(fn)) fn(i++);
    if (i === n) clearInterval(o);
  }, interval);
}

export const StringUtils = {
  concatIf: function (s1, s2, condition) {
    if (typeof s2 === "string") {
      return s1 + (condition ? s2 : "");
    }

    if (isObject(s2)) {
      let s = s1;

      each(s2, (v, k) => {
        s += v ? k : "";
      });

      return s;
    }

    return s1;
  },
};

export function first(array) {
  return [].concat(array).shift();
}

export function last(array) {
  return [].concat(array).pop();
}

export function stopImmediatePropagation(e) {
  e.preventDefault();
  e.stopPropagation();
}

export function findIndexes(source, condition = () => {}) {
  if (!(source || condition)) return [];

  const indexes = [];
  source.forEach((o, i) => {
    if (condition(o)) indexes.push(i);
  });

  return indexes;
}

export function getA4WidthByHeight(height) {
  const ratio = 1 / Math.sqrt(2);
  const fullWIdth = height * ratio;
  return Math.trunc(fullWIdth);
}

export function formatSSN(ssn) {
  return ssn ? `###-##-${ssn.substring(5, 9)}` : "";
}
