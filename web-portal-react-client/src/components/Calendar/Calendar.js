import React, {
    memo,
    useMemo,
    useState,
    useCallback
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import { eq as eqDates } from 'date-arithmetic'

import {
    Loader
} from 'components'

import {
    useMutationWatch
} from 'hooks/common'

import {
    isNullOrUndefined
} from 'lib/utils/Utils'

import {
    noop
} from 'lib/utils/FuncUtils'

import {
    first
} from 'lib/utils/ArrayUtils'

import {
    isToday,

    getEndOfWeek,
    getStartOfWeek,

    getEndOfMonth,
    getStartOfMonth
} from 'lib/utils/DateUtils'

import Scrollable from './Scrollable/Scrollable'
import DatePicker from './DatePicker/DatePicker'
import ViewModeBar from './ViewModeBar/ViewModeBar'

import { DayView } from './DayView'
import { WeekView } from './WeekView'
import { MonthView } from './MonthView'

import {
    canonizeDateRange,
    getViewModeByDateRange
} from './utils/Utils'

import {
    VIEW_MODE,
    DIMENSIONS,
    HOURS_IN_DAY,
    DAY_START_HOUR,
    FIRST_DAY_OF_WEEK,
    WORK_DAY_START_HOUR
} from './Constants'

import './Calendar.scss'

const {
    DAY,
    TODAY,
    WEEK,
    WORK_WEEK,
    MONTH
} = VIEW_MODE

const CURRENT_WEEK_DATE_RANGE = [
    getStartOfWeek(new Date(), FIRST_DAY_OF_WEEK),
    getEndOfWeek(new Date(), FIRST_DAY_OF_WEEK)
]

function Calendar(
    {
        events,
        isLoading,
        defaultViewMode,
        defaultDateRange,
        autoScrollToHour,
        hourSegmentHeight,
        onAddEvent,
        onPickEvent,
        onDoublePickEvent,
        renderEventSummary,
        renderEventDescription,
        onChangeDate,
        onChangeDateRange,
        onChangeViewMode,
        className
    }
) {
    defaultDateRange = canonizeDateRange(defaultDateRange)

    const [date, setDate] = useState(
        defaultDateRange ? new Date(defaultDateRange[0]) : new Date()
    )

    const [dateRange, setDateRange] = useState(
        defaultDateRange ?? CURRENT_WEEK_DATE_RANGE
    )

    const [viewMode, setViewMode] = useState(
        defaultDateRange ? getViewModeByDateRange(defaultDateRange) : defaultViewMode
    )

    const autoScroll = useMemo(() => {
        if (!autoScrollToHour) return

        return {
            offset: autoScrollToHour * hourSegmentHeight + 30,
            containerHeight: (HOURS_IN_DAY - autoScrollToHour + 1) * hourSegmentHeight
        }
    }, [autoScrollToHour, hourSegmentHeight])

    const _onChangeDate = useCallback(value => {
        setDate(value)

        if ([DAY, TODAY].includes(viewMode)) {
            const nextViewMode = isToday(value) ? TODAY : DAY

            if (viewMode !== nextViewMode) {
                setViewMode(nextViewMode)
            }

            if (!eqDates(date, value, 'day')) {
                onChangeDate(value)
            }
        } else if (viewMode === MONTH) {
            onChangeDateRange([getStartOfMonth(value), getEndOfMonth(value)])
        }
    }, [date, viewMode, onChangeDate, onChangeDateRange])

    const _onChangeDateRange = useCallback(value => {
        if ([WEEK, WORK_WEEK].includes(viewMode)) {
            const range = [
                getStartOfWeek(value, FIRST_DAY_OF_WEEK),
                getEndOfWeek(value, FIRST_DAY_OF_WEEK, viewMode === WEEK)
            ]

            setDateRange(range)
            onChangeDateRange(range)
        } else {
            setDateRange(value)
            onChangeDateRange(value)
        }
    }, [viewMode, onChangeDateRange])

    const _onChangeViewMode = useCallback(mode => {
        setViewMode(mode)
        onChangeViewMode(mode)

        if (mode === TODAY) setDate(new Date())

        if ([DAY, TODAY].includes(mode)) {
            onChangeDate(mode === TODAY ? new Date() : date)
        } else if ([WEEK, WORK_WEEK].includes(mode)) {
            const range = [
                getStartOfWeek(dateRange[0], FIRST_DAY_OF_WEEK),
                getEndOfWeek(dateRange[0], FIRST_DAY_OF_WEEK, mode === WEEK)
            ]

            setDateRange(range)
            onChangeDateRange(range)
        } else if (mode === MONTH) {
            onChangeDateRange([
                getStartOfMonth(date),
                getEndOfMonth(date)
            ])
        }
    }, [
        date,
        dateRange,
        onChangeDate,
        onChangeViewMode,
        onChangeDateRange
    ])

    useMutationWatch(defaultDateRange, prev => {
        if (isNullOrUndefined(prev)) {
            setDateRange(defaultDateRange)
            setViewMode(getViewModeByDateRange(defaultDateRange))
            setDate(defaultDateRange[0])
        }
    })

    return (
        <div className={cn("Calendar", className)}>
            {isLoading && <Loader isCentered hasBackdrop />}
            <div className="Calendar-Header">
                <div className="Calendar-HeaderBody">
                    {[WEEK, WORK_WEEK].includes(viewMode) ? (
                        <DatePicker
                            mode="date-range"
                            value={dateRange}
                            onChange={_onChangeDateRange}
                        />
                    ) : (
                        <DatePicker
                            mode={[DAY, TODAY].includes(viewMode) ? 'date' : 'month'}
                            value={date}
                            onChange={_onChangeDate}
                        />
                    )}
                    <ViewModeBar
                        selectedMode={viewMode}
                        onChangeMode={_onChangeViewMode}
                    />
                </div>
            </div>
            {[DAY, TODAY].includes(viewMode) && (
                <DayView
                    date={date}
                    events={events}
                    autoScrollToHour={autoScrollToHour}
                    hourSegmentHeight={hourSegmentHeight}
                    isTodayTimeVisible={viewMode === TODAY}
                    onAddEvent={onAddEvent}
                    onPickEvent={onPickEvent}
                    dayStartHour={DAY_START_HOUR}
                    onDoublePickEvent={onDoublePickEvent}
                    renderEventDescription={renderEventDescription}
                />
            )}
            {[WEEK, WORK_WEEK].includes(viewMode) && (
                <WeekView
                    events={events}
                    isTodayTimeVisible
                    startDate={first(dateRange)}
                    hasWeekendDays={viewMode === WEEK}
                    dayStartHour={DAY_START_HOUR}
                    hourSegmentHeight={hourSegmentHeight}
                    autoScrollToHour={autoScrollToHour}
                    onAddEvent={onAddEvent}
                    onPickEvent={onPickEvent}
                    onDoublePickEvent={onDoublePickEvent}
                    renderEventDescription={renderEventDescription}
                />
            )}
            {viewMode === MONTH && (
                <MonthView
                    date={date}
                    events={events}
                    onPickEvent={onPickEvent}
                    onAddEvent={onAddEvent}
                    onDoublePickEvent={onDoublePickEvent}
                    renderEventSummary={renderEventSummary}
                    renderEventDescription={renderEventDescription}
                />
            )}
        </div>
    )
}

Calendar.propTypes = {
    events: PTypes.array,
    isLoading: PTypes.bool,
    defaultViewMode: PTypes.oneOf([TODAY, MONTH]),
    defaultDateRange: PTypes.array,
    autoScrollToHour: PTypes.number,
    hourSegmentHeight: PTypes.number,
    onPickEvent: PTypes.func,
    onChangeDate: PTypes.func,
    onChangeViewMode: PTypes.func,
    onChangeDateRange: PTypes.func,
    onDoublePickEvent: PTypes.func,
    renderEventSummary: PTypes.func,
    renderEventDescription: PTypes.func
}

Calendar.defaultProps = {
    events: [],
    defaultViewMode: TODAY,
    autoScrollToHour: WORK_DAY_START_HOUR,
    hourSegmentHeight: DIMENSIONS.HOUR_SEGMENT_HEIGHT,
    onChangeDate: noop,
    onChangeViewMode: noop,
    onChangeDateRange: noop,
}

export default memo(Calendar)
