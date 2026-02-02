import React, {
    memo,
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import PTypes from 'prop-types'

import { WeekDays } from '../'
import { DayTimeGrid } from '../../DayView'

import './WeekTimeGrid.scss'

function WeekTimeGrid(
    {
        startDate,
        hasWeekendDays,
        gradation,
        is12hFormat,
        dayStartHour,
        highlightedTime
    }
) {
    return (
        <div className="WeekTimeGrid">
            <WeekDays
                startDate={startDate}
                hasWeekends={hasWeekendDays}
                className="margin-left-75 margin-bottom-20"
            />
            <DayTimeGrid
                gradation={gradation}
                is12hFormat={is12hFormat}
                dayStartHour={dayStartHour}
                highlightedTime={highlightedTime}
            />
        </div>
    )
}

WeekTimeGrid.propTypes = {
    startDate: PTypes.oneOfType([PTypes.number, PTypes.object]),
    hasWeekendDays: PTypes.bool,
    gradation: PTypes.number,
    is12hFormat: PTypes.bool,
    highlightedTime: PTypes.oneOfType([PTypes.number, PTypes.object]),
    dayStartHour: PTypes.number
}

export default memo(WeekTimeGrid)