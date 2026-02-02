import React, {
    memo,
    useMemo,
    useCallback
} from 'react'

import PTypes from 'prop-types'

import {
    map,
    reduce,
    sortBy,
    groupBy
} from 'underscore'

import {
    hours, minutes
} from 'date-arithmetic'

import {
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import {
    Scrollable
} from 'components'

import {
    keys,
    pick
} from 'lib/utils/ObjectUtils'

import {
    noop
} from 'lib/utils/FuncUtils'

import {
    format,
    formats,
    getDateTime,
    getStartOfDayTime,
} from 'lib/utils/DateUtils'

import { ReactComponent as Add } from 'images/add.svg'

import { TEvent } from '../../types'

import Event from './Event/Event'

import {
    getWeek,
    getMonthWeeks
} from '../utils/Utils'

import './MonthEventLayoutGrid.scss'

const EVENT_SEGMENT_COUNT = 3

const DATE_FORMAT = formats.americanMediumDate
const TIME_FORMAT = formats.time2

export const Cell = memo(function Cell(
    {
        date,
        events,
        eventSegmentCount,
        canAddEventInPastDate,
        onAddEvent,
        onPickEvent,
        onDoublePickEvent,
        renderEventSummary,
        renderEventDescription
    }
) {
    date = getDateTime(date)

    const sortedEvents = sortBy(events, 'startDate')

    const groupedByTime = groupBy(
        sortedEvents, e => format(e.startDate, TIME_FORMAT)
    )

    const displayedGroups = pick(
        groupedByTime,
        keys(groupedByTime).slice(0, eventSegmentCount)
    )

    const displayedCount = reduce(displayedGroups, (m, g) => {
        m += g.length
        return m
    }, 0)

    const _onAddEvent = useCallback(e => {
        e.stopPropagation()
        const now = new Date()
        onAddEvent(minutes(hours(date, hours(now)), minutes(now)))
    }, [date, onAddEvent])

    const _onDoublePickEvent = useCallback((data, e) => {
        e.stopPropagation()
        onDoublePickEvent(data)
    }, [onDoublePickEvent])

    return (
        <div
            onDoubleClick={_onAddEvent}
            className="MonthEventLayoutGrid-Cell"
            data-testid={`${date}-month-event-layout-grid-cell`}
        >
            <div className="v-flexbox">
                {map(displayedGroups, group => {
                    const events = group.map(e => {
                        return (
                            <Event
                                {...e}
                                key={e.id}
                                tooltip={{
                                    render: renderEventDescription,
                                    className: 'CalendarEventDescriptionPopup'
                                }}
                                onClick={onPickEvent}
                                onDoubleClick={_onDoublePickEvent}
                            />
                        )
                    })

                    return (
                        group.length > 1 ? (
                            <div className="SimultaneousEventGroup">
                                {events}
                            </div>
                        ) : (
                            <>{events}</>
                        )
                    )
                })}
            </div>
            <div className="MonthEventLayoutGrid-Slot">
                {(canAddEventInPastDate || date >= getStartOfDayTime(Date.now())) ? (
                    <Add
                        onClick={_onAddEvent}
                        className="MonthEventLayoutGrid-AddEventBtn"
                    />
                ) : <div/>}
                {keys(groupedByTime).length > eventSegmentCount ? (
                    <>
                        <div
                            id={`date-${date}-show-more-btn`}
                            className="MonthEventLayoutGrid-ShowMoreEvents"
                        >
                            + {events.length - displayedCount} More
                        </div>
                        <Tooltip
                            trigger="legacy"
                            target={`date-${date}-show-more-btn`}
                            modifiers={[
                                {
                                    name: 'offset',
                                    options: { offset: [0, 6] }
                                },
                                {
                                    name: 'preventOverflow',
                                    options: { boundary: document.body }
                                }
                            ]}
                            className="CalendarEventListPopup"
                        >
                            <div className="CalendarEventList">
                                <div className="CalendarEventList-Header">
                                    <div className="CalendarEventList-Title">
                                        {format(date, 'ddd, MMM dd')}
                                    </div>
                                </div>
                                <Scrollable className="flex-1 padding-right-10">
                                    {sortedEvents.map(e => (
                                        <div className="CalendarEventListItem">
                                            {renderEventSummary(e.data)}
                                        </div>
                                    ))}
                                </Scrollable>
                            </div>
                        </Tooltip>
                    </>
                ) : (<div/>)}
            </div>
        </div>
    )
})

Cell.propTypes = {
    date: PTypes.oneOfType([PTypes.number, PTypes.object]),
    events: PTypes.arrayOf(TEvent),
    canAddEventInPastDate: PTypes.bool,
    eventSegmentCount: PTypes.number,
    onAddEvent: PTypes.func,
    onPickEvent: PTypes.func,
    onDoublePickEvent: PTypes.func,
    renderEventSummary: PTypes.func,
    renderEventDescription: PTypes.func
}

Cell.defaultProps = {
    events: [],
    canAddEventInPastDate: false,
    eventSegmentCount: EVENT_SEGMENT_COUNT,
    onAddEvent: noop,
    onPickEvent: noop,
    onDoublePickEvent: noop,
    renderEventSummary: noop,
    renderEventDescription: noop
}

function MonthEventLayoutGrid(
    {
        date,
        events,
        onAddEvent,
        onPickEvent,
        onDoublePickEvent,
        renderEventSummary,
        renderEventDescription
    }
) {
    const weeks = useMemo(
        () => getMonthWeeks(date), [date]
    )

    const dates = useMemo(() => {
        const dates = []

        weeks.forEach(weekStart => {
            getWeek(weekStart).forEach(
                date => dates.push(date)
            )
        })

        return dates
    }, [weeks])

    const groupedByDate = useMemo(() => groupBy(
        events, e => format(e.startDate, DATE_FORMAT)
    ), [events])

    return (
        <div className="MonthEventLayoutGrid">
            {dates.map(date => (
                <Cell
                    key={date.getTime()}
                    date={date.getTime()}
                    events={groupedByDate[format(date, DATE_FORMAT)]}
                    onAddEvent={onAddEvent}
                    onPickEvent={onPickEvent}
                    onDoublePickEvent={onDoublePickEvent}
                    renderEventSummary={renderEventSummary}
                    renderEventDescription={renderEventDescription}
                />
            ))}
        </div>
    )
}

MonthEventLayoutGrid.propTypes = {
    date: PTypes.oneOfType([PTypes.number, PTypes.object]),
    events: PTypes.arrayOf(TEvent),
    onAddEvent: PTypes.func,
    onPickEvent: PTypes.func,
    onDoublePickEvent: PTypes.func,
    renderEventSummary: PTypes.func,
    renderEventDescription: PTypes.func
}

MonthEventLayoutGrid.defaultProps = {
    events: [],
    date: Date.now()
}

export default memo(MonthEventLayoutGrid)