import React from 'react'

import {
	reject
} from 'underscore'

import {
	render
} from 'lib/test-utils'

import {
	uc
} from 'lib/utils/Utils'

import DayTimeGrid from './DayTimeGrid'

import {
	DIMENSIONS,
	DAY_START_HOUR,
	HOUR_RANGE_12H,
	HOUR_RANGE_24H,
	HOUR_GRADATIONS,
} from '../../Constants'

function to24h(v) {
	const [h, ampm] = v.split(' ')
	return uc(ampm) === 'PM' ? +h + 12 : +h
}

describe('[Calendar] <DayTimeGrid>:', function () {
	it('Correct time segment count:', function () {
		const is12hFormat = true
		const gradation = HOUR_GRADATIONS.HALF
		const dayStartHour = DAY_START_HOUR
		const segmentHeight = DIMENSIONS.HOUR_SEGMENT_HEIGHT

		const hourRange = reject(
			is12hFormat ? HOUR_RANGE_12H : HOUR_RANGE_24H,
			h => to24h(h) < dayStartHour
		)

		const { getByTestId } = render(
			<DayTimeGrid
				is12hFormat={is12hFormat}
				gradation={gradation}
				dayStartHour={dayStartHour}
				segmentHeight={segmentHeight}
			/>
		)

		const node = getByTestId("day-time-grid")
		expect(node.querySelectorAll('.DayTimeSegment').length).toEqual(hourRange.length)
	})

	it('Highlighted time is visible:', function () {
		const { getByTestId } = render(
			<DayTimeGrid
				highlightedTime={Date.now()}
			/>
		)

		const node = getByTestId("day-time-grid_highlighted-time")
		expect(node).toBeVisible()
		expect(node).toBeInTheDocument()
	})

	it('Highlighted time is not visible:', function () {
		const { getByTestId } = render(<DayTimeGrid/>)
		const node = getByTestId("day-time-grid")
		expect(node.querySelectorAll('.DayTimeGrid-HighlightedTime').length).toEqual(0)
	})
})