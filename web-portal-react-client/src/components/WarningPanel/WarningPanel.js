import React from 'react'

import cn from 'classnames'

import { ReactComponent as Warning } from 'images/warning-1.svg'

import './WarningPanel.scss'

function WarningPanel({ children, className }) {
    return (
        <div className={cn('WarningPanel', className)}>
            <div className="WarningPanel-IconArea">
                <Warning className="WarningPanel-Icon" />
            </div>

            <div className="WarningPanel-MessageBox">
                <div className="WarningPanel-Message">
                    {children}
                </div>
            </div>
        </div>
    )
}

export default WarningPanel
