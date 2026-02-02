import React, { memo } from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import {
    range,
    filter,
    groupBy
} from 'underscore'

import {
    add,
    weekday
} from 'date-arithmetic'

import {
    getEndOfWeekAndDayTime,
    getStartOfWeekAndDayTime
} from 'lib/utils/DateUtils'

import {
    TEvent
} from '../../types'

import {
    WEEK_DAY_COUNT,
    WORK_WEEK_DAY_COUNT,
    DAY_START_HOUR,
    FIRST_DAY_OF_WEEK
} from '../../Constants'

import { DayEventLayoutGrid } from '../../DayView'

import './WeekEventLayoutGrid.scss'

function WeekEventLayoutGrid(
    {
        events,
        firstDay,
        startDate,
        className,
        hasWeekendDays,
        hourSegmentHeight,
        onAddEvent,
        onPickEvent,
        onDoublePickEvent,
        renderEventDescription
    }
) {
    startDate = getStartOfWeekAndDayTime(startDate, firstDay)
    const endDate = getEndOfWeekAndDayTime(startDate, firstDay)

    events = filter(events, e => (
        e.startDate >= startDate
        && e.startDate < endDate
    ))

    const count = (
        hasWeekendDays ? WEEK_DAY_COUNT : WORK_WEEK_DAY_COUNT
    )

    const groupedByDay = groupBy(
        events, e => weekday(new Date(e.startDate), undefined, firstDay)
    )

    return (
        <div className={cn("WeekEventLayoutGrid", className)}>
            {range(count).map(i => {
                return (
                    <DayEventLayoutGrid
                        key={i}
                        date={add(new Date(startDate), i, 'day')}
                        events={groupedByDay[i] || []}
                        dayStartHour={DAY_START_HOUR}
                        hourSegmentHeight={hourSegmentHeight}
                        onAddEvent={onAddEvent}
                        onPickEvent={onPickEvent}
                        onDoublePickEvent={onDoublePickEvent}
                        renderEventDescription={renderEventDescription}
                    />
                )
            })}
        </div>
    )
}

WeekEventLayoutGrid.propTypes = {
    events: PTypes.arrayOf(TEvent),
    firstDay: PTypes.number,
    gradation: PTypes.number,
    dayStartHour: PTypes.number,
    startOfWeek: PTypes.number,
    hourSegmentHeight: PTypes.number,
    className: PTypes.string,
    onAddEvent: PTypes.func,
    onPickEvent: PTypes.func,
    onDoublePickEvent: PTypes.func,
    renderEventDescription: PTypes.func,
}

WeekEventLayoutGrid.defaultProps = {
    firstDay: FIRST_DAY_OF_WEEK
}

export default memo(WeekEventLayoutGrid)