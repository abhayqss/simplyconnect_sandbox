import { getYear } from 'date-fns'

export const DEFAULT_YEAR_ITEM_NUMBER = 12

export function getYearsPeriod(date, yearItemNumber = DEFAULT_YEAR_ITEM_NUMBER) {
    const endPeriod = Math.ceil(getYear(date) / yearItemNumber) * yearItemNumber
    const startPeriod = endPeriod - (yearItemNumber - 1)
    return { startPeriod, endPeriod }
}