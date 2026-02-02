import React, {
    memo
} from 'react'

import PTypes from 'prop-types'

import { TEvent } from '../types'

import {
    MonthDateGrid,
    MonthEventLayoutGrid
} from './'

import './MonthView.scss'

function MonthView(
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
    return (
        <div className="MonthView">
            <MonthDateGrid
                date={date}
            />
            <MonthEventLayoutGrid
                date={date}
                events={events}
                onAddEvent={onAddEvent}
                onPickEvent={onPickEvent}
                onDoublePickEvent={onDoublePickEvent}
                renderEventSummary={renderEventSummary}
                renderEventDescription={renderEventDescription}
            />
        </div>
    )
}

MonthView.propTypes = {
    date: PTypes.oneOfType([PTypes.number, PTypes.object]),
    events: PTypes.arrayOf(TEvent),
    onAddEvent: PTypes.func,
    onPickEvent: PTypes.func,
    onDoublePickEvent: PTypes.func,
    renderEventSummary: PTypes.func,
    renderEventDescription: PTypes.func
}

MonthView.defaultProps = {
    events: [],
    date: Date.now()
}

export default memo(MonthView)