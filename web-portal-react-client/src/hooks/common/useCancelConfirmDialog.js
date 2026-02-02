import React, { useState, useMemo, useCallback } from 'react'
import { noop } from 'underscore'

import ConfirmDialog from 'components/dialogs/ConfirmDialog/ConfirmDialog'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

function useCancelConfirmDialog() {
    const [isOpen, toggle] = useState(false)

    const close = () => toggle(false)

    const confirm = useCallback((onConfirm) => () => {
        close()
        onConfirm()
    }, [])

    const dialog = useMemo(() => ({ onConfirm = noop }) =>
        isOpen ? (
            <ConfirmDialog
                isOpen
                icon={Warning}
                confirmBtnText="OK"
                title="The updates will not be saved."
                onConfirm={confirm(onConfirm)}
                onCancel={close}
            />
        ) : null,
    [confirm, isOpen])

    return [dialog, toggle]
}

export default useCancelConfirmDialog
