import React, {
    memo,
    useMemo
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import {
    isToday,
    isPastDate,
    isCurrentMonth
} from 'lib/utils/DateUtils'

import {
    WEEK_SHORT_DAYS
} from '../../Constants'

import {
    getWeek,
    getMonthWeeks
} from '../utils/Utils'

import './MonthDateGrid.scss'

function MonthDateGrid({ date }) {
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

    return (
        <div className="MonthDateGrid">
            <div className="MonthDateGrid-WeekDays">
                {WEEK_SHORT_DAYS.map(title => (
                    <div key={title} className="MonthDateGrid-WeekDay">
                        {title}
                    </div>
                ))}
            </div>
            <div className="MonthDateGrid-Dates">
                {dates.map(date => (
                    <div
                        key={date}
                        className={cn(
                            "MonthDateGrid-Date",
                            isPastDate(date) && 'MonthDateGrid-Date_past',
                            isToday(date) && 'MonthDateGrid-Date_current'
                        )}
                    >
                        <div
                            className={cn(
                                'MonthDateGrid-DateTitle',
                                !isCurrentMonth(date) && 'MonthDateGrid-DateTitle_color_gray'
                            )}
                        >
                            {date.getDate()}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}

MonthDateGrid.propTypes = {
    date: PTypes.oneOfType([PTypes.number, PTypes.object])
}

MonthDateGrid.defaultProps = {
    date: Date.now()
}

export default memo(MonthDateGrid)