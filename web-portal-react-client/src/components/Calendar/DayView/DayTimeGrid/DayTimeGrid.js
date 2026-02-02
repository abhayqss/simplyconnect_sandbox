import React, {
    memo
} from 'react'

import PTypes from 'prop-types'

import {
    reject,
    findLastIndex
} from 'underscore'

import {
    hours,
    minutes
} from 'date-arithmetic'

import {
    uc
} from 'lib/utils/Utils'

import DayTimeSegment from '../DayTimeSegment/DayTimeSegment'

import {
    DIMENSIONS,
    HOUR_RANGE_12H,
    HOUR_RANGE_24H,
    HOUR_GRADATIONS,
    DAY_START_HOUR,
} from '../../Constants'

import './DayTimeGrid.scss'

function to24h(v) {
    const [h, ampm] = v.split(' ')
    return uc(ampm) === 'PM' ? +h + 12 : +h
}

function DayTimeGrid(
    {
        gradation,
        is12hFormat,
        segmentHeight,
        dayStartHour,
        highlightedTime
    }
) {
    const hourRange = reject(
        is12hFormat ? HOUR_RANGE_12H : HOUR_RANGE_24H,
        h => to24h(h) < dayStartHour
    )

    return (
        <div data-testid="day-time-grid" className="DayTimeGrid">
            {hourRange.map((title, i) => (
                <DayTimeSegment
                    key={i}
                    title={title}
                    height={segmentHeight}
                    gradation={gradation}
                />
            ))}
            {highlightedTime && (() => {
                const h = hours(new Date(highlightedTime))
                const m = minutes(new Date(highlightedTime))

                const index = findLastIndex(hourRange, v => h === to24h(v))

                return (
                    <div
                        className="DayTimeGrid-HighlightedTime"
                        data-testid="day-time-grid_highlighted-time"
                        style={{ top: (segmentHeight * index + segmentHeight / 60 * m) + 'px' }}
                    />
                )
            })()}
        </div>
    )
}

DayTimeGrid.propTypes = {
    gradation: PTypes.number,
    is12hFormat: PTypes.bool,
    segmentHeight: PTypes.number,
    highlightedTime: PTypes.oneOfType([PTypes.number, PTypes.object]),
    dayStartHour: PTypes.number
}

DayTimeGrid.defaultProps = {
    gradation: HOUR_GRADATIONS.HALF,
    is12hFormat: true,
    dayStartHour: DAY_START_HOUR,
    segmentHeight: DIMENSIONS.HOUR_SEGMENT_HEIGHT
}

export default memo(DayTimeGrid)