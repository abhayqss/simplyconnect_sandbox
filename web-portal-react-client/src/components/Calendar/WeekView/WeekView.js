import React, {
    memo, useMemo
} from 'react'

import PTypes from 'prop-types'

import {
    useCurrentTime
} from 'hooks/common'

import { TEvent } from '../types'

import Scrollable from '../Scrollable/Scrollable'

import {
    WeekDays,
    WeekEventLayoutGrid,
} from './'

import {
    DAY_START_HOUR,
    FIRST_DAY_OF_WEEK, HOURS_IN_DAY
} from '../Constants'

import { DayTimeGrid } from '../DayView'

import './WeekView.scss'

function WeekView(
    {
        events,
        firstDay,
        startDate,
        dayStartHour,
        hasWeekendDays,
        dayTimeGradation,
        autoScrollToHour,
        hourSegmentHeight,
        isTodayTimeVisible,
        onAddEvent,
        onPickEvent,
        onDoublePickEvent,
        renderEventDescription
    }
) {
    const currentTime = useCurrentTime(60 * 1000)

    const autoScroll = useMemo(() => {
        if (!autoScrollToHour) return

        return {
            offset: autoScrollToHour * hourSegmentHeight,
            containerHeight: (HOURS_IN_DAY - autoScrollToHour + 1) * hourSegmentHeight
        }
    }, [autoScrollToHour, hourSegmentHeight])

    return (
        <div className="WeekView">
            <div className="WeekView-Header">
                <WeekDays
                    firstDay={firstDay}
                    startDate={startDate}
                    hasWeekends={hasWeekendDays}
                />
            </div>
            <Scrollable
                offset={autoScroll.offset}
                style={{ height: autoScroll.containerHeight }}
                className="padding-top-10"
            >
                <div className="WeekView-Body">
                    <DayTimeGrid
                        dayStartHour={dayStartHour}
                        gradation={dayTimeGradation}
                        segmentHeight={hourSegmentHeight}
                        highlightedTime={isTodayTimeVisible ? currentTime : null}
                    />
                    <WeekEventLayoutGrid
                        events={events}
                        firstDay={firstDay}
                        startDate={startDate}
                        gradation={dayTimeGradation}
                        hasWeekendDays={hasWeekendDays}
                        dayStartHour={dayStartHour}
                        hourSegmentHeight={hourSegmentHeight}
                        onAddEvent={onAddEvent}
                        onPickEvent={onPickEvent}
                        onDoublePickEvent={onDoublePickEvent}
                        renderEventDescription={renderEventDescription}
                    />
                </div>
            </Scrollable>
        </div>
    )
}

WeekView.propTypes = {
    events: PTypes.arrayOf(TEvent),
    startDate: PTypes.oneOfType([PTypes.object, PTypes.number]),
    dayStartHour: PTypes.number,
    firstDay: PTypes.number,
    hasWeekendDays: PTypes.bool,
    hourSegmentHeight: PTypes.number,
    dayTimeGradation: PTypes.number,
    autoScrollToHour: PTypes.number,
    isTodayTimeVisible: PTypes.bool,
    onAddEvent: PTypes.func,
    onPickEvent: PTypes.func,
    onDoublePickEvent: PTypes.func,
    renderEventDescription: PTypes.func,
}

WeekView.defaultProps = {
    firstDay: FIRST_DAY_OF_WEEK,
    dayStartHour: DAY_START_HOUR,
    startDate: Date.now(),
    isTodayTimeVisible: true
}

export default memo(WeekView)