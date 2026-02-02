import React from 'react'

import {
	render
} from 'lib/test-utils'

import {
	add, year, month
} from 'date-arithmetic'

import {
	format,
	getEndOfMonth,
	getStartOfMonth,
	getEndOfWeek,
	getEndOfWeekTime,
	getStartOfWeek,
	getStartOfWeekTime,
} from 'lib/utils/DateUtils'

import { SelectedDate } from './DatePicker'

const MODE = {
	DATE: 'date',
	DATE_RANGE: 'date-range',
	MONTH: 'month'
}

describe('[Calendar] <DatePicker>:', function () {
	describe('<SelectedDate>:', function () {
		it('Correct display value for mode=DAY:', function () {
			const date = Date.now()

			const {
				getByTestId
			} = render(<SelectedDate mode={MODE.DATE} val={date}/>)

			const node = getByTestId('date-picker_selected-date')
			expect(node).toHaveTextContent(format(date, 'MMMM dd, YYYY'))
		})

		it('Correct display value for mode=DATE_RANGE:', function () {
			const week = [
				getStartOfWeek(Date.now(), 1),
				getEndOfWeek(Date.now(), 1)
			]

			const [startDate, endDate] = week

			const {
				getByTestId
			} = render(<SelectedDate mode={MODE.DATE_RANGE} val={week}/>)

			let dateText = ''

			if (year(startDate) !== year(endDate)) {
				dateText = `${format(startDate, 'MMMM dd, YYYY')} - ${format(endDate, 'MMMM dd, YYYY')}`
			} else if (month(startDate) !== month(endDate)) {
				dateText = `${format(startDate, 'MMMM dd')} - ${format(endDate, 'MMMM dd, YYYY')}`
			} else {
				dateText = `${format(startDate, 'MMMM dd')} - ${format(endDate, 'dd, YYYY')}`
			}

			const node = getByTestId('date-picker_selected-date')
			expect(node).toHaveTextContent(dateText)
		})

		it('Correct display value for mode=DATE_RANGE and NO and date:', function () {
			const week = [getStartOfWeekTime(Date.now())]

			const {
				getByTestId
			} = render(<SelectedDate mode={MODE.DATE_RANGE} val={week}/>)

			const node = getByTestId('date-picker_selected-date')
			expect(node).toHaveTextContent(`${format(week[0], 'MMMM dd, YYYY')} -`)
		})

		it('Correct display value for mode=DATE_RANGE and different years:', function () {
			const week = [getStartOfWeekTime(Date.now()), add(getEndOfWeekTime(Date.now()), 1, 'year')]

			const {
				getByTestId
			} = render(<SelectedDate mode={MODE.DATE_RANGE} val={week}/>)

			const node = getByTestId('date-picker_selected-date')
			expect(node).toHaveTextContent(`${format(week[0], 'MMMM dd, YYYY')} - ${format(week[1], 'MMMM dd, YYYY')}`)
		})

		it('Correct display value for mode=MONTH:', function () {
			const date = Date.now()
			const startDate = getStartOfMonth(date)
			const endDate = getEndOfMonth(date)

			const {
				getByTestId
			} = render(<SelectedDate mode={MODE.MONTH} val={date}/>)

			const node = getByTestId('date-picker_selected-date')
			expect(node).toHaveTextContent(`${format(startDate, 'MMMM dd')} - ${format(endDate, 'dd, YYYY')}`)
		})
	})
})