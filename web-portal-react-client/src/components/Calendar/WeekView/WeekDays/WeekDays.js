import React, {
    memo
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import {
    range
} from 'underscore'

import {
    eq,
    add
} from 'date-arithmetic'

import {
    format,
    getStartOfWeek
} from 'lib/utils/DateUtils'

import {
    WEEK_DAY_COUNT,
    WORK_WEEK_DAY_COUNT,
    FIRST_DAY_OF_WEEK
} from '../../Constants'

import './WeekDays.scss'

const DAY_FORMAT = 'dd ddd'

function WeekDays({ firstDay, startDate, hasWeekends, className }) {
    startDate = getStartOfWeek(startDate, firstDay)

    const count = (
        hasWeekends ? WEEK_DAY_COUNT : WORK_WEEK_DAY_COUNT
    )

    return (
        <div className={cn("WeekDays", className)}>
            {range(count).map(i => {
                const date = add(startDate, i, 'day')
                const formatted = format(date, DAY_FORMAT)
                const isCurrent = eq(date, new Date(), 'day')
                const isDisabled = false //todo When is disabled?
                const dateAndName = formatted.split(' ')

                return (
                    <div
                        key={i}
                        className={cn(
                            'WeekDays-Day',
                            { 'WeekDays-Day_current': isCurrent },
                            { 'WeekDays-Day_disabled': isDisabled },
                        )}
                    >
                        <span className="WeekDays-DayDate">{dateAndName[0]}</span>&nbsp;
                        <span className="WeekDays-DayName">{dateAndName[1]}</span>
                    </div>
                )
            })}
        </div>
    )
}

WeekDays.propTypes = {
    firstDay: PTypes.number,
    startDate: PTypes.oneOfType([PTypes.number, PTypes.object]),
    hasWeekends: PTypes.bool,
    className: PTypes.string
}

WeekDays.defaultProps = {
    firstDay: FIRST_DAY_OF_WEEK,
    hasWeekends: true
}

export default memo(WeekDays)