import React, { memo } from 'react'

import { useDroppable } from '@dnd-kit/core'

function Droppable({ id, data, children, ...props }) {
    const {
        setNodeRef
    } = useDroppable({ id, data })

    return (
        <div ref={setNodeRef} {...props}>
            {children}
        </div>
    )
}

export default memo(Droppable)

