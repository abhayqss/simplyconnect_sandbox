import React, {
	memo,
	useMemo
} from 'react'

import PTypes from 'prop-types'

import {
	useCurrentTime
} from 'hooks/common'

import {
	format,
	getDateTime
} from 'lib/utils/DateUtils'

import Scrollable from '../Scrollable/Scrollable'

import {
	DayTimeGrid,
	DayEventLayoutGrid
} from './'

import {
	TEvent
} from '../types'

import {
	DIMENSIONS,
	HOURS_IN_DAY,
	DAY_START_HOUR,
	WORK_DAY_START_HOUR
} from '../Constants'

import './DayView.scss'

const DAY_DATE_FORMAT = 'd dddd'

function DayView(
	{
		date,
		events,
		dayStartHour,
		dayTimeGradation,
		hourSegmentHeight,
		isTodayTimeVisible,
		autoScrollToHour,
		onAddEvent,
		onPickEvent,
		onDoublePickEvent,
		renderEventDescription
	}
) {
	const currentTime = useCurrentTime(60 * 1000)
	const formattedDate = format(date, DAY_DATE_FORMAT)

	const autoScroll = useMemo(() => {
		if (!autoScrollToHour) return

		return {
			offset: autoScrollToHour * hourSegmentHeight,
			containerHeight: (HOURS_IN_DAY - autoScrollToHour + 1) * hourSegmentHeight
		}
	}, [autoScrollToHour, hourSegmentHeight])

	return (
		<div
			className="DayView"
			data-testid={`${getDateTime(date)}-day-view`}
		>
			<div className="DayView-Header">
				<div className="DayView-Day">
					<span
						className="font-size-15"
						data-testid={`${getDateTime(date)}-day-view_day-date`}
                    >
                        {formattedDate.split(' ')[0]}&nbsp;
                    </span>
					<span data-testid={`${getDateTime(date)}-day-view_day-title`}>
						{formattedDate.split(' ')[1]}
					</span>
				</div>
			</div>
			<Scrollable
				offset={autoScroll.offset}
				style={{ height: autoScroll.containerHeight }}
				className="padding-top-10"
			>
				<div className="DayView-Body">
					<DayTimeGrid
						dayStartHour={dayStartHour}
						gradation={dayTimeGradation}
						segmentHeight={hourSegmentHeight}
						highlightedTime={isTodayTimeVisible ? currentTime : null}
					/>
					<DayEventLayoutGrid
						date={date}
						events={events}
						dayStartHour={dayStartHour}
						gradation={dayTimeGradation}
						onAddEvent={onAddEvent}
						onPickEvent={onPickEvent}
						hourSegmentHeight={hourSegmentHeight}
						onDoublePickEvent={onDoublePickEvent}
						renderEventDescription={renderEventDescription}
					/>
				</div>
			</Scrollable>
		</div>
	)
}

DayView.propTypes = {
	events: PTypes.arrayOf(TEvent),
	date: PTypes.oneOfType([PTypes.object, PTypes.number]),
	isTodayTimeVisible: PTypes.bool,
	dayStartHour: PTypes.number,
	dayTimeGradation: PTypes.number,
	hourSegmentHeight: PTypes.number,
	autoScrollToHour: PTypes.number,
	onAddEvent: PTypes.func,
	onPickEvent: PTypes.func,
	onDoublePickEvent: PTypes.func,
	renderEventDescription: PTypes.func
}

DayView.defaultProps = {
	date: Date.now(),
	isTodayTimeVisible: true,
	dayStartHour: DAY_START_HOUR,
	autoScrollToHour: WORK_DAY_START_HOUR,
	hourSegmentHeight: DIMENSIONS.HOUR_SEGMENT_HEIGHT
}

export default memo(DayView)