import React, { memo } from 'react'

import cn from 'classnames'

import './WarningDialog.scss'

import Dialog from '../Dialog/Dialog'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

function WarningDialog({
    text,
    title,
    isOpen,
    buttons,
    children,
    className,
}) {
    return (
        <Dialog
            text={text}
            title={title}
            icon={Warning}
            isOpen={isOpen}
            buttons={buttons}
            className={cn('WarningDialog', className)}
        >
            {children}
        </Dialog>
    )
}

export default memo(WarningDialog)
