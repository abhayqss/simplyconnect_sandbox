import moment from 'moment'

import { capitalize } from 'lib/utils/Utils'

import { PERIOD_UNIT_NAME } from 'lib/Constants'

const { EVERY_DAY, EVERY_WEEK, EVERY_MONTH, NEVER } = PERIOD_UNIT_NAME

const formatDateToString = date => moment(date).format('MM/DD/YYYY')
const capitalizeWeekday = weekday => capitalize(weekday.toLowerCase())

const getName = (period, isPlural) => isPlural ? `${period}s` : period
const getUntil = (noEndDate, until) => noEndDate ? '' : `until ${formatDateToString(until)}`

const PERIOD_FORMAT_MAP = {
    [NEVER]: () => 'Never',
    [EVERY_DAY]: formatDayRepeatTime,
    [EVERY_WEEK]: formatWeekRepeatTime,
    [EVERY_MONTH]: formatMonthRepeatTime,
}

function formatTime(date) {
    return moment(date).format('LT')
}

function formatDayRepeatTime(data) {
    let isPlural = data.periodFrequency > 1
    let until = getUntil(data.noEndDate, data.until)

    return `Every ${data.periodFrequency} ${getName('day', isPlural)} ${until}`
}

function formatWeekRepeatTime(data) {
    let isPlural = data.periodFrequency > 1
    let until = getUntil(data.noEndDate, data.until)
    let weekdays = data.weekdays ? `on ${data.weekdays.map(capitalizeWeekday).join(', ')}` : ''

    return `Every ${data.periodFrequency} ${getName('week', isPlural)} ${weekdays} ${until}`
}

function formatMonthRepeatTime(data) {
    let isPlural = data.periodFrequency > 1
    let date = moment(data.startDate).date()
    let until = getUntil(data.noEndDate, data.until)

    return `On day ${date} of every ${data.periodFrequency} ${getName('month', isPlural)} ${until}`
}

export function getTimeRange(date = Date.now(), { offset = 30, size = 30 } = {}) {
    const remainder = offset - (moment(date).minute() % size)
    return {
        from: moment(date).add(remainder, 'minutes').toDate(),
        to: moment(date).add(remainder + size, 'minutes').toDate()
    }
}

export function getFormattedTimeRange(date = Date.now(), { offset = 30, size = 30 } = {}) {
    const range = getTimeRange(date, { offset, size })
    return { to: formatTime(range.to), from: formatTime(range.from) }
}