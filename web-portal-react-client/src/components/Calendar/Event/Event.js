import React, {
    memo,
    useCallback
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import {
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import { noop } from 'lib/utils/FuncUtils'

import { values } from 'lib/utils/ObjectUtils'

import { TEvent } from '../types'

import {
    EVENT_COLORS,
    EVENT_DECORATORS
} from '../Constants'

import './Event.scss'

const COLOR_LIST = values(EVENT_COLORS)

const DECORATORS = values(EVENT_DECORATORS)

function Event(
    {
        id,
        name,
        title,
        type,
        data,
        color,
        decorators,
        startDate,
        endDate,
        children,
        render,
        onClick,
        onDoubleClick,
        style,
        tooltip,
        className
    }
) {
    const _onClick = useCallback(e => {
        onClick({ id, title, name, type, data }, e)
    }, [id, title, name, type, data, onClick])

    const _onDoubleClick = useCallback(e => {
        onDoubleClick({ id, title, name, type, data }, e)
    }, [id, title, name, type, data, onDoubleClick])

    return (
        <div
            id={`event-${id}`}
            style={style}
            onClick={_onClick}
            onDoubleClick={_onDoubleClick}
            className={cn(
                'CalendarEvent',
                color && `CalendarEvent_color_${color}`,
                decorators?.map(o => `CalendarEvent_decorator_${o}`),
                className
            )}
        >
            {render ? render({ name, title, type, startDate, endDate, children }) : (
                <>
                    <div className="CalendarEvent-Title">{title}</div>
                    <div className="CalendarEvent-Type">{type}</div>
                    <div className="CalendarEvent-Body">{children}</div>
                </>
            )}
            {tooltip && (
                <Tooltip
                    trigger="legacy"
                    target={`event-${id}`}
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
                    {...tooltip}
                    className={cn('CalendarEventTooltip', tooltip.className)}
                >
                    {tooltip.render ? tooltip.render(data) : tooltip.text}
                </Tooltip>
            )}
        </div>
    )
}

Event.propTypes = {
    ...TEvent,
    data: PTypes.object,
    style: PTypes.object,
    className: PTypes.string,
    tooltip: PTypes.object,
    color: PTypes.oneOf(COLOR_LIST),
    decorators: PTypes.arrayOf(PTypes.oneOf(DECORATORS)),
    render: PTypes.func,
    onClick: PTypes.func,
    onDoubleClick: PTypes.func,
}

Event.defaultProps = {
    decorators: [],
    onClick: noop,
    onDoubleClick: noop,
}

export default memo(Event)