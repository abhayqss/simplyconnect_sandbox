import React, { useMemo } from 'react'

import cn from 'classnames'
import { noop } from 'underscore'
import PTypes from 'prop-types'

import { UncontrolledTooltip as Tooltip } from 'reactstrap'

import './IconButton.scss'

export default function IconButton(
    {
        name,
        Icon,
        disabled,
        size = 36,
        tipText = '',
        tipPlace = 'left-start',
        className,
        onClick = noop,
        tipTrigger = 'click hover',
        shouldHighLight = true,
    }
) {
    const style = useMemo(() => (
        { width: size, height: size }
    ), [ size ])

    return (
        <div
            onClick={onClick}
            className={cn(
                'IconButton',
                className,
                { 'IconButton_disabled': disabled },
                { 'IconButton_highlighted': shouldHighLight }
            )}
        >
            <Icon id={name} style={style}/>
            {name && tipText && (
                <Tooltip
                    placement={tipPlace}
                    target={name}
                    trigger={tipTrigger}
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
                >
                    {tipText}
                </Tooltip>
            )}
        </div>
    )
}

IconButton.propTypes = {
    name: PTypes.string,
    disabled: PTypes.bool,
    tipText: PTypes.string,
    tipPlace: PTypes.string,
    className: PTypes.string
}