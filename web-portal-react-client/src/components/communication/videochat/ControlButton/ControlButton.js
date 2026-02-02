import React from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import { IconButton } from 'components/buttons'

import './ControlButton.scss'

const COLORS = ['red', 'green']

export default function ControlButton (
    {
        color,
        className,
        ...props
    }
) {
    return (
        <IconButton
            {...props}
            className={cn(
                'VideoChatControlButton',
                { [`VideoChatControlButton_color_${color}`]: COLORS.includes(color) },
                className
            )}
        />
    )
}

ControlButton.propTypes = {
    name: PTypes.string,
    size: PTypes.number,
    color: PTypes.oneOf(COLORS),
    onClick: PTypes.func,
    tipText: PTypes.string,
    tipPlace: PTypes.string,
    className: PTypes.string,
    shouldHighLight: PTypes.bool
}

ControlButton.defaultProps = {
    color: 'green',
    shouldHighLight: false
}