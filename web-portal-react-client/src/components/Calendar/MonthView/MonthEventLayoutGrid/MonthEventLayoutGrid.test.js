import React from 'react'

import moment from 'moment'

import {
	map,
	range,
	sortBy,
	reduce,
	groupBy
} from 'underscore'

import {
	render
} from 'lib/test-utils'

import { getRandomInt } from 'lib/utils/Utils'

import {
	format, formats
} from 'lib/utils/DateUtils'

import { keys, pick } from 'lib/utils/ObjectUtils'

import { Cell } from './MonthEventLayoutGrid'

import { EVENT_COLORS } from '../../Constants'

const DATE_FORMAT = formats.americanMediumDate
const TIME_FORMAT = formats.time2

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

describe('[Calendar] <MonthEventLayoutGridCell>:', function () {
	it('Events displayed correctly:', function () {
		const count = 12
		const date = Date.now()
		const displayedGroupCount = 3
		const events = generateEvents(count)

		const sortedEvents = sortBy(events, 'startDate')

		const groupedByTime = groupBy(
			sortedEvents, e => format(e.startDate, TIME_FORMAT)
		)

		const displayedGroups = pick(
			groupedByTime,
			keys(groupedByTime).slice(0, displayedGroupCount)
		)

		const displayedEventCount = reduce(displayedGroups, (m, g) => {
			m += g.length
			return m
		}, 0)

		const {
			getByTestId
		} = render(
			<Cell
				date={date}
				events={events}
				displayedEventCount={displayedGroupCount}
			/>
		)

		const node = getByTestId(`${date}-month-event-layout-grid-cell`)
		expect(node.querySelectorAll('.CalendarEvent').length).toEqual(displayedEventCount)

		if (count > displayedGroupCount) {
			const showMoreBtnNode = node.querySelector(`#date-${date}-show-more-btn`)
			expect(showMoreBtnNode).toHaveTextContent(`+ ${count - displayedEventCount} More`)
		} else {
			const showMoreBtnNode = node.querySelector(`#date-${date}-show-more-btn`)
			expect(showMoreBtnNode).not.toBeInTheDocument()
		}
	})
})