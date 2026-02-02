import React from 'react'

import { ConfirmDialog } from '../'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

export default function CancelConfirmDialog(
    {
        isOpen,
        title,
        children,
        onConfirm,
        onCancel,
        ...props
    }
) {
    return isOpen ? (
        <ConfirmDialog
            isOpen
            {...props}
            icon={Warning}
            confirmBtnText="OK"
            title={title || children}
            onConfirm={onConfirm}
            onCancel={onCancel}
        />
    ) : null
}

CancelConfirmDialog.defaultProps = {
    title: 'The updates will not be saved.'
}