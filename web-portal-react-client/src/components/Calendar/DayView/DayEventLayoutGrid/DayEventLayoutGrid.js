import React, {
    memo,
    useRef,
    useMemo,
    useEffect,
    useCallback
} from 'react'

import PTypes from 'prop-types'

import {
    any,
    max,
    omit,
    range,
    filter,
    reduce,
    sortBy,
    groupBy
} from 'underscore'

import {
    hours,
    minutes,
    date as getDate,
} from 'date-arithmetic'

import {
    noop
} from 'lib/utils/FuncUtils'

import {
    Event
} from '../'

import {
    TEvent
} from '../../types'

import {
    getEventTimeRange,
    getAggregatedTimeRange,
    getMaxTimeRangeIntersectionCount
} from '../../utils/Utils'

import {
    DIMENSIONS,
    HOURS_IN_DAY,
    HOUR_GRADATIONS,
    DAY_START_HOUR
} from '../../Constants'

import './DayEventLayoutGrid.scss'

function getRowIndexByTime(time, gradation, dayStartHour = 8) {
    const h = hours(new Date(time))
    const m = minutes(new Date(time))
    return (h - dayStartHour) / gradation + m / 60 / gradation
}

function groupEventsByNonIntersection(events = []) {
    let groups = []

    if (!events?.length) return groups

    events = sortBy(events, 'startDate')

    const ranges = events.map(getEventTimeRange)
    const agRange = getAggregatedTimeRange(ranges)

    const count = getMaxTimeRangeIntersectionCount(ranges, {
        from: agRange.start,
        to: agRange.end,
        gradation: HOUR_GRADATIONS.FIVE_MINUTES
    })

    for (let i = 0; i < count + 1; i++) {
        groups.push([])
    }

    events.forEach(e => {
        for (let i = 0; i < groups.length; i++) {
            if (!groups[i]?.length) {
                groups[i].push(e)
                break
            }

            const ranges = groups[i].map(getEventTimeRange)

            const count = getMaxTimeRangeIntersectionCount(
                [...ranges, getEventTimeRange(e)], {
                from: e.startDate,
                to: e.endDate,
                gradation: HOUR_GRADATIONS.FIVE_MINUTES
            })

            if (count === 0) {
                groups[i].push(e)
                break
            }
        }
    })

    return groups
}

//todo bug on very long events
function DayEventLayoutGrid(
    {
        date,
        events,
        gradation,
        dayStartHour,
        hourSegmentHeight,
        onAddEvent,
        onPickEvent,
        onDoublePickEvent,
        renderEventDescription
    }
) {
    const ref = useRef()

    events = filter(events, e => (
        getDate(new Date(e.startDate)) === getDate(new Date(date))
    ))

    events = sortBy(events, 'startDate')

    const groupedByStartDate = useMemo(() => omit(
        groupBy(events, e => getRowIndexByTime(e.startDate, gradation, dayStartHour)),
        (v, k) => k < 0
    ), [events, gradation, dayStartHour])

    const eventRowIndexes = useMemo(() => {
        const indexes = []

        for (let index in groupedByStartDate) {
            const event = max(groupedByStartDate[+index], e => (
                e.endDate - e.startDate
            ))

            const rowCount = ((
                event.endDate - event.startDate
            ) / (1000 * 60 * 60)) / gradation

            for (let i = 0; i < rowCount; i++) {
                indexes.push(+index + i)
            }
        }

        return indexes
    }, [gradation, groupedByStartDate])

    const eventRanges = useMemo(
        () => events.map(getEventTimeRange), [events]
    )

    const eventAggregatedRange = useMemo(
        () => getAggregatedTimeRange(eventRanges), [eventRanges]
    )

    const maxIntersectionCount = useMemo(
        () => getMaxTimeRangeIntersectionCount(eventRanges, {
            from: eventAggregatedRange.start,
            to: eventAggregatedRange.end,
            gradation: HOUR_GRADATIONS.FIVE_MINUTES
        }),
        [eventRanges, eventAggregatedRange]
    )

    const groupedByNonIntersection = useMemo(
        () => groupEventsByNonIntersection(events), [events]
    )

    function findNonIntersectionEventGroupIndex(event) {
        return reduce(groupedByNonIntersection, (m, g, i) => {
            if (any(g, e => e.id === event.id)) m = i
            return m
        }, 0)
    }

    const _onDoublePickEvent = useCallback((data, e) => {
        e.stopPropagation()
        onDoublePickEvent(data)
    }, [onDoublePickEvent])

    /*useEffect(() => {
        const node = ref.current
        node.style.setProperty('--col-count', maxIntersectionCount + 1)
    }, [maxIntersectionCount])*/

    return (
        <div ref={ref} data-testid="day-event-layout-grid" className="DayEventLayoutGrid">
            {range((HOURS_IN_DAY - dayStartHour + 1) / gradation).map((v, i) => (
                <div
                    key={v}
                    onDoubleClick={e => {
                        e.stopPropagation()

                        const h = Math.floor(i * gradation)
                        const m = i * gradation * 60 - h * 60

                        onAddEvent(minutes(hours(date, h), m))
                    }}
                    className="DayEventLayoutGrid-Cell"
                    style={eventRowIndexes.includes(i) && !groupedByStartDate[i] ? { zIndex: -1 } : {}}
                >
                    {groupedByStartDate[i] && groupedByStartDate[i].map(event => {
                        const index = findNonIntersectionEventGroupIndex(event)

                        const count = getMaxTimeRangeIntersectionCount(
                            eventRanges,
                            {
                                from: event.startDate,
                                to: event.endDate,
                                gradation: HOUR_GRADATIONS.FIVE_MINUTES
                            }
                        )

                        const height = (
                            ((event.endDate - event.startDate) / (1000 * 60 * 60)) * hourSegmentHeight - 1
                        )

                        return (
                            <Event
                                key={event.id}
                                onClick={onPickEvent}
                                onDoubleClick={_onDoublePickEvent}
                                style={{
                                    height,
                                    width: `calc(${100 / (count + 1) + '%'} - ${index && 1}px)`,
                                    left: `calc(${100 * index / (maxIntersectionCount + 1)}% + ${index && 1}px)`,
                                    ...height < 45 && { paddingTop: 5 }
                                }}
                                {...event}
                                tooltip={{
                                    render: renderEventDescription,
                                    className: "CalendarEventDescriptionPopup"
                                }}
                            >
                                {event.text || event?.renderBody()}
                            </Event>
                        )
                    })}
                </div>
            ))}
        </div>
    )
}

DayEventLayoutGrid.propTypes = {
    events: PTypes.arrayOf(TEvent),
    gradation: PTypes.number,
    dayStartHour: PTypes.number,
    hourSegmentHeight: PTypes.number,
    onAddEvent: PTypes.func,
    onPickEvent: PTypes.func,
    onDoublePickEvent: PTypes.func,
    renderEventDescription: PTypes.func
}

DayEventLayoutGrid.defaultProps = {
    events: [],
    gradation: HOUR_GRADATIONS.FIVE_MINUTES,
    dayStartHour: DAY_START_HOUR,
    hourSegmentHeight: DIMENSIONS.HOUR_SEGMENT_HEIGHT,
    onAddEvent: noop,
    onPickEvent: noop,
    onDoublePickEvent: noop,
}

export default memo(DayEventLayoutGrid)