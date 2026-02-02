import React from 'react'

import cn from 'classnames'

import moment from 'moment'

import './DateSeparator.scss'

const isToday = date => moment(date).isSame(new Date(), 'day')

function DateSeparator({ date, className }) {
    let formattedDate = isToday(date) ? 'Today' : moment(date).format('dddd MM/DD/YYYY')

    return (
        <div className={cn('DateSeparator', className)}>
            {formattedDate}
        </div>
    )
}

export default DateSeparator
