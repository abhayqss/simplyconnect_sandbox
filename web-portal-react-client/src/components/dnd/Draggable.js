import React, { memo } from 'react'

import PTypes from 'prop-types'

import { useDraggable } from '@dnd-kit/core'

import { CSS } from '@dnd-kit/utilities'

function Draggable({ id, data, style, isDisabled, canTransform, children }) {
    const {
        listeners,
        setNodeRef,
        attributes,
        transform
    } = useDraggable({ id, data })

    return (
        <div
            ref={setNodeRef}
            style={{
                ...style,
                ...canTransform && !isDisabled && {
                    transform: CSS.Translate.toString(transform)
                }
            }}
            {...listeners}
            {...attributes}
        >
            {children}
        </div>
    )
}

Draggable.propTypes = {
    id: PTypes.oneOfType([PTypes.number, PTypes.string]),
    data: PTypes.object,
    style: PTypes.object,
    isDisabled: PTypes.bool,
    canTransform: PTypes.bool,
    className: PTypes.string
}

Draggable.defaultProps = {
    isDisabled: false,
    canTransform: false
}

export default memo(Draggable)

