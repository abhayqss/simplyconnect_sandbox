import React from 'react'

import cn from 'classnames'

import './AlertPanel.scss'

const AlertPanel = ({ children, className, name }) => (
    <div className={cn('AlertPanel-Alert', className)} data-testid={name || 'alert-panel'}>
        <span className="AlertPanel-AlertText">
            {children}
        </span>
    </div>
)

export default AlertPanel
