import React, {
    memo
} from 'react'

import PTypes from 'prop-types'

import { noop } from 'lib/utils/FuncUtils'

import { values } from 'lib/utils/ObjectUtils'

import {
    format,
    formats
} from 'lib/utils/DateUtils'

import { TEvent } from '../../../types'

import BaseEvent from '../../../Event/Event'

import { EVENT_COLORS } from '../../../Constants'

import './Event.scss'

const COLOR_LIST = values(EVENT_COLORS)

function Event(props) {
    return (
        <BaseEvent
            {...props}
            render={({ title, startDate }) => (
                <div className="h-flexbox justify-content-between">
                    <div className="CalendarEvent-Title">
                        {title}
                    </div>
                    <div className="CalendarEvent-StartTime">
                        {format(startDate, formats.time2)}
                    </div>
                </div>
            )}
        />
    )
}

Event.propTypes = {
    ...TEvent,
    tooltip: PTypes.object,
    className: PTypes.string,
    style: PTypes.object,
    color: PTypes.oneOf(COLOR_LIST),
    onClick: PTypes.func,
    onDoubleClick: PTypes.func,
}

Event.defaultProps = {
    onClick: noop,
    onDoubleClick: noop,
}

export default memo(Event)