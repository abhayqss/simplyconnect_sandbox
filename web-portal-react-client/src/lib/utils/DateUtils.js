import moment from 'moment'
import momentDurationSetup from 'moment-duration-format'

import {
    gt,
    add,
    date,
    diff,
    year,
    month,
    endOf,
    startOf
} from 'date-arithmetic'

import { isNumber } from './Utils'

momentDurationSetup(moment)

const MS_IN_SEC = 1000
const MS_IN_MIN = MS_IN_SEC * 60
const MS_IN_HOUR = MS_IN_MIN * 60
const MS_IN_DAY = MS_IN_HOUR * 24
const MS_IN_WEEK = MS_IN_DAY * 7

export const MILLI = {
    second: MS_IN_SEC,
    minute: MS_IN_MIN,
    hour: MS_IN_HOUR,
    day: MS_IN_DAY,
    week: MS_IN_WEEK
}

export const formats = {
    'default': 'YYYY/MM/dd',
    time: 'hh:mm AA',
    time2: 'h:mm AA',
    shortDate: 'MM/dd',
    mediumDate: 'YY/MM/dd',
    americanShortDate: 'M/d/YYYY',
    americanMediumDate: 'MM/dd/YYYY',
    separatedYearDate: 'MMM dd, YYYY',
    separatedYearDateTime: 'MMM dd, YYYY hh:mm AA',
    longDate: 'YYYY/MM/dd',
    longDateTime: 'YYYY/MM/dd HH:mm:ss',
    longDateMediumTime12: 'MM/dd/YYYY hh:mm AA',
    longDateMediumTime12TimeZone: 'MM/dd/YYYY hh:mm AA Z',
    isoDateTime: 'YYYY-MM-ddTHH:mm:ss.000'
}

function pad(val, len) {
    val = String(val)
    len = len || 2
    while (val.length < len) {
        val = '0' + val
    }
    return val
}

export function isDate(date) {
    return date instanceof Date
}

export function format(date, format, utc) {
    if (isNumber(date) || isDate(date)) {
        let df = formats
        let token = /d{1,4}|M{1,4}|YY(?:YY)?|([HhmsAa])\1?|[oS]|[zZ]/g

        if (isNumber(date)) date = new Date(date)

        format = String(df[format] || format || df['default'])

        let abbr = getTimeZoneAbbr(date)

        let prf = utc ? 'getUTC' : 'get'
        let d = date[prf + 'Date']()
        let D = date[prf + 'Day']()
        let M = date[prf + 'Month']()
        let Y = date[prf + 'FullYear']()
        let H = date[prf + 'Hours']()
        let m = date[prf + 'Minutes']()
        let s = date[prf + 'Seconds']()
        let o = utc ? 0 : date.getTimezoneOffset()
        let z = abbr.toLowerCase()
        let Z = abbr.toUpperCase()
        let flags = {
            d: d,
            dd: pad(d),
            ddd: i18n.dayNames[D],
            dddd: i18n.dayNames[D + 7],
            M: M + 1,
            MM: pad(M + 1),
            MMM: i18n.monthNames[M],
            MMMM: i18n.monthNames[M + 12],
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
            a: H < 12 ? 'a' : 'p',
            aa: H < 12 ? 'am' : 'pm',
            A: H < 12 ? 'A' : 'P',
            AA: H < 12 ? 'AM' : 'PM',
            o: (o > 0 ? '-' : '+') + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4),
            S: ['th', 'st', 'nd', 'rd'][d % 10 > 3 ? 0 : (d % 100 - d % 10 !== 10) * d % 10],
            z: '(' + z + ')',
            Z: '(' + Z + ')'
        }

        return format.replace(token, function (t) {
            return (t in flags) ? flags[t] : t.slice(1, t.length - 1)
        })
    }

    return ''
}

export const i18n = {
    dayNames: [
        'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat',
        'Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'
    ],
    monthNames: [
        'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec',
        'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'
    ]
}

export const diffDates = function (date1, date2, unit) {
    const diffInMs = Math.abs(+date1 - +date2)
    if (!unit) {
        return diffInMs
    }

    unit = unit.toLowerCase()
    const unitInMs = MILLI[unit]
    if (unitInMs) {
        return diffInMs / unitInMs
    } else if (unit === 'month' || unit === 'year') {
        const date1Less = date1 < date2
        const start = date1Less ? date1 : date2
        const end = date1Less ? date2 : date1
        let years = year(end) - year(start)
        let months = years * 12 + month(end) - month(start)
        let days = date(end) - date(start)
        if (days >= 0) {
            months++
        }
        return (unit === 'month') ? months : months / 12
    } else {
        return diffInMs
    }
}

export function isTomorrow(date) {
    if (!date) return false
    let d1 = startOf(new Date(), 'day')
    let d2 = startOf(new Date(date), 'day')
    return (diff(d1, d2, 'day') === 1)
}

export function isPast(date) {
    return new Date(date).getTime() < Date.now()
}

export function isPastDate(date) {
    if (!date) return false
    let d1 = startOf(new Date(), 'day')
    let d2 = startOf(new Date(date), 'day')
    return (gt(d1, d2, 'day') > 0)
}

export function isToday(date) {
    if (!date) return false
    let d1 = startOf(new Date(), 'day')
    let d2 = startOf(new Date(date), 'day')
    return (diff(d1, d2, 'day') === 0)
}

export function isYesterday(date) {
    let d1 = startOf(new Date(), 'day')
    let d2 = startOf(date, 'day')
    return (diff(d2, d1, 'day') === 1)
}

export function isSameDay(date1, date2) {
    return moment(date1).isSame(date2, 'day')
}

export function splitByMonth(start, end) {
    const endDate = end || new Date().getTime()
    const startDate = startOf(start || endDate, 'month')
    let d = startOf(endDate, 'month').getTime()
    let values = []
    while (d >= startDate) {
        values.push(d)
        d = add(d, -1, 'month').getTime()
    }
    return values
}

export function daysInMonth(year, month) {
    return new Date(year, month, 0).getDate()
}

export function getTimeZoneAbbr(date) {
    date = date || new Date()

    if (!(date instanceof Date)) {
        date = new Date(date)
    }

    let timeString = date.toTimeString()
    let abbr = timeString.match(/\([a-z ]+\)/i)
    if (abbr && abbr[0]) {
        // 17:56:31 GMT-0600 (CST)
        // 17:56:31 GMT-0600 (Central Standard Time)
        abbr = abbr[0].match(/[A-Z]/g)
        abbr = abbr ? abbr.join('') : undefined
    } else {
        // 17:56:31 CST
        // 17:56:31 GMT+0800 (台北標準時間)
        abbr = timeString.match(/[A-Z]{3,5}/g)
        abbr = abbr ? abbr[0] : undefined
    }

    return abbr
}

export function toUTC(v) {
    const date = v instanceof Date ? v : new Date(v)

    return new Date(
        date.getTime() + date.getTimezoneOffset() * 60000
    )
}

export function localize(v) {
    const date = v instanceof Date ? v : new Date(v)

    return new Date(
        date.getTime() - date.getTimezoneOffset() * 60000
    )
}

export function getDateTime(date) {
    return new Date(date).getTime()
}

export function isCurrentMonth(date) {
    return new Date().getMonth() === new Date(date).getMonth()
}

export function formatDuration(value, unit = 'ms', format = 'h[h] m[m] s[s]') {
    return value ? moment.duration(value, unit).format(format) : ''
}

export function getStartOfDay(date) {
    return startOf(new Date(date), 'day')
}

export function getStartOfDayTime(date) {
    return getStartOfDay(date).getTime()
}

export function getEndOfDay(date) {
    return endOf(new Date(date), 'day')
}

export function getEndOfDayTime(date) {
    return getEndOfDay(date).getTime()
}

export function getTodayStartOfDayTime() {
    return startOf(Date.now(), 'day').getTime()
}

export function getTodayEndOfDayTime() {
    return endOf(Date.now(), 'day').getTime()
}

export function getStartOfWeek(date, firstDay) {
    return startOf(new Date(date), 'week', firstDay)
}

export function getStartOfWeekAndDay(date, firstDay) {
    return startOf(getStartOfWeek(date, firstDay), 'day')
}

export function getEndOfWeek(date, firstDay, includeWeekends = true) {
    return add(endOf(new Date(date), 'week', firstDay), includeWeekends ? 0 : -2, 'day')
}

export function getEndOfWeekAndDay(date, firstDay, includeWeekends) {
    return endOf(getEndOfWeek(date, firstDay, includeWeekends), 'day')
}

export function getStartOfWeekTime(date, firstDay) {
    return getStartOfWeek(date, firstDay).getTime()
}

export function getStartOfWeekAndDayTime(date, firstDay) {
    return getStartOfWeekAndDay(date, firstDay).getTime()
}

export function getEndOfWeekTime(date, firstDay) {
    return getEndOfWeek(date, firstDay).getTime()
}

export function getEndOfWeekAndDayTime(date, firstDay) {
    return getEndOfWeekAndDay(date, firstDay).getTime()
}

export function getStartOfMonth(date) {
    return startOf(new Date(date), 'month')
}

export function getStartOfMonthTime(date) {
    return startOf(new Date(date), 'month').getTime()
}

export function getEndOfMonth(date) {
    return endOf(new Date(date), 'month')
}

export function getEndOfMonthTime(date) {
    return endOf(new Date(date), 'month').getTime()
}

export function getSplittedDay(intervalInMinutes) {
    const itemsNumber = Math.round(24 * 60 / intervalInMinutes)

    let timeList = []

    for (let i = 0; i < itemsNumber; i++) {
        const time = moment()
            .set({
                hour: 0,
                minute: 0,
            })
            .add(intervalInMinutes * i, "minutes")
            .format("LT")

        timeList.push(time)
    }

    return timeList;
}

export function parseDate(s, format = 'MM/DD/YYYY') {
    if (!s) return null
    return moment(s, format).toDate()
}

export function parseTime(rawValue) {
    return moment(rawValue, formats.time2)
}

export function setTime(date, momentTime) {
    return moment(date).set({
        hour: moment(momentTime).hours(),
        minute: moment(momentTime).minutes()
    }).startOf("minute")
}

export function moveTime(date, offset = 15, unit = 'm') {
    return moment(date).add(offset, unit)
}

export function isValid12hTimeFormat(value) {
    return /^(0?\d|1[0-2]):([0-5]\d)\s?(?:AM|PM)$/i.test(value)
}