import React from 'react'

import { AlertPanel as Panel } from 'components'

function AlertPanel({
    formData: value,
    className,
}) {
    return (
        <Panel className={className}>
            {value}
        </Panel>
    )
}

export default AlertPanel
