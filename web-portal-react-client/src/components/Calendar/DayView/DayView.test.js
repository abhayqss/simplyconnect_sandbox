import React from 'react'

import {
	render
} from 'lib/test-utils'

import {
	format,
	getDateTime
} from 'lib/utils/DateUtils'

import DayView from './DayView'

describe('[Calendar] <DayView>:', function () {
	it('Date display value is correct:', function () {
		const date = Date.now()
		const DAY_DATE_FORMAT = 'd dddd'
		const formattedDate = format(date, DAY_DATE_FORMAT)
		const [dayDate, dayTitle] = formattedDate.split(' ')

		const {
			getByTestId
		} = render(<DayView date={date}/>)

		const dateNode = getByTestId(`${getDateTime(date)}-day-view_day-date`)
		expect(dateNode).toHaveTextContent(dayDate)

		const dayTitleNode = getByTestId(`${getDateTime(date)}-day-view_day-title`)
		expect(dayTitleNode).toHaveTextContent(dayTitle)
	})
})