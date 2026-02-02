import React from 'react'

import {
	map,
	range
} from 'underscore'

import moment from 'moment'

import {
	render
} from 'lib/test-utils'

import {
	getRandomInt
} from 'lib/utils/Utils'

import {
	EVENT_COLORS, HOURS_IN_DAY
} from '../../Constants'

import DayEventLayoutGrid from './DayEventLayoutGrid'

import {
	DIMENSIONS,
	DAY_START_HOUR,
	HOUR_GRADATIONS
} from '../../Constants'

export function getRandomArrayElement(arr) {
	let idx = getRandomInt(0, arr.length - 1)
	return arr[idx]
}

export function getTimeRange(date = Date.now(), { offset = 30, size = 30 } = {}) {
	const remainder = offset - (moment(date).minute() % size)
	return {
		from: moment(date).add(remainder, 'minutes').toDate(),
		to: moment(date).add(remainder + size, 'minutes').toDate()
	}
}

function getRandTimeRange(date = Date.now()) {
	const hours = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22]
	const minutes = [5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55]
	const durations = [30, 60, 90, 120, 150, 180, 210]
	return getTimeRange(
		moment(date).startOf('day')
			.add(getRandomArrayElement(hours), 'hour')
			.add(getRandomArrayElement(minutes), 'minutes'),
		{ size: getRandomArrayElement(durations) }
	)
}

function getRandEvent() {
	const range = getRandTimeRange()
	return {
		id: getRandomInt(0, 999999),
		type: 'Test event type',
		title: 'Test event title',
		color: getRandomArrayElement(Object.values(EVENT_COLORS)),
		text: 'Test event text',
		startDate: range.from.getTime(),
		endDate: range.to.getTime()
	}
}

function generateEvents(count) {
	return map(range(0, count), getRandEvent)
}

describe('[Calendar] <DayEventLayoutGrid>:', function () {
	it('Correct cell count:', function () {
		const gradation = HOUR_GRADATIONS.FIVE_MINUTES
		const dayStartHour = DAY_START_HOUR
		const segmentHeight = DIMENSIONS.HOUR_SEGMENT_HEIGHT
		const segmentCount = (HOURS_IN_DAY - dayStartHour + 1) / gradation

		const { getByTestId } = render(
			<DayEventLayoutGrid
				date={Date.now()}
				gradation={gradation}
				dayStartHour={dayStartHour}
				hourSegmentHeight={segmentHeight}
			/>
		)

		const node = getByTestId("day-event-layout-grid")
		expect(node.querySelectorAll('.DayEventLayoutGrid-Cell').length).toEqual(segmentCount)
	})

	it('Correct displayed event count:', function () {
		const count = 10
		const events = generateEvents(count)

		const { getByTestId } = render(
			<DayEventLayoutGrid
				date={Date.now()}
				events={events}
			/>
		)

		const node = getByTestId("day-event-layout-grid")
		expect(node.querySelectorAll('.CalendarEvent').length).toEqual(count)
	})
})