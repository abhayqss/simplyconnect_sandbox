import { range } from 'underscore'

import {
    add,
    endOf,
    startOf,
    weekday,
    date as getDate,
} from 'date-arithmetic'

import {
    FIRST_DAY_OF_WEEK
} from '../../Constants'

export function getWeek(date) {
    const dates = []

    let start = startOf(
        new Date(date), 'week', FIRST_DAY_OF_WEEK
    )

    for (let i = 0; i < 7; i++) {
        dates.push(add(start, i, 'day'))
    }

    return dates
}

export function getMonthDates(date) {
    const end = endOf(date, 'month')
    return range(1, end.getDate())
}

export function getMonthWeekCount(date) {
    let count = 4

    const start = startOf(date, 'month')
    const end = endOf(date, 'month')

    if (weekday(start, undefined, FIRST_DAY_OF_WEEK) > 1) count++
    if (weekday(end, undefined, FIRST_DAY_OF_WEEK) < 7) count++

    return count
}

export function getMonthWeeks(date) {
    const weeks = []

    const monthStart = startOf(date, 'month')
    const weekStart = startOf(monthStart, 'week', FIRST_DAY_OF_WEEK)

    for (let i = 0; i < getMonthWeekCount(date); i++) {
        weeks.push(add(weekStart, i, 'week'))
    }

    return weeks
}