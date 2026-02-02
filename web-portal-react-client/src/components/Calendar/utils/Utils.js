import {
    min,
    max,
    filter
} from 'underscore'

import {
    diff
} from 'date-arithmetic'

import {
    isEmpty
} from 'lib/utils/Utils'

import {
    isToday
} from 'lib/utils/DateUtils'

import {
    VIEW_MODE,
    HOUR_GRADATIONS
} from '../Constants'

const {
    DAY,
    TODAY,
    WEEK,
    WORK_WEEK,
    MONTH
} = VIEW_MODE

export function getEventTimeRange(e) {
    return { start: e.startDate, end: e.endDate }
}

export function areTimeRangesIntersected(r1, r2) {
    return (
        (r1.start >= r2.start && r1.start < r2.end)
        || (r1.start <= r2.start && r1.end > r2.start)
    )
}

export function getAggregatedTimeRange(ranges) {
    const r1 = min(ranges, o => o.start)
    const r2 = max(ranges, o => o.end)
    return { start: r1.start, end: r2.end }
}

export function getMaxTimeRangeIntersectionCount(ranges, options) {
    const { from, to, gradation = HOUR_GRADATIONS.FIVE_MINUTES } = options

    let count = 0

    if (from && to) {
        const delta = 60 * gradation * 60 * 1000

        const range = {
            start: from,
            end: from + delta
        }

        while (range.start < to) {
            const v = filter(
                ranges, o => areTimeRangesIntersected(range, o)
            ).length - 1

            if (v > count) count = v

            range.start += delta
            range.end += delta
        }
    }

    return count
}

export function canonizeDateRange(range) {
    if (isEmpty(range)) return range
    return [new Date(range[0]), new Date(range[1])]
}

export function getViewModeByDateRange(range) {
    if (isEmpty(range)) return

    const startDate = new Date(range[0])
    const endDate = range[1] && new Date(range[1])

    if (!endDate) {
        return isToday(startDate) ? TODAY : DAY
    }

    const diffInHours = diff(startDate, endDate, 'hours')
    const diffInDays = diff(startDate, endDate, 'day')

    if (diffInHours <= 24 && isToday(startDate)) return TODAY
    else if (diffInHours <= 24) return DAY
    else if (diffInDays <= 5) return WORK_WEEK
    else if (diffInDays <= 7) return WEEK
    else if (diffInDays >= 28) return MONTH
}