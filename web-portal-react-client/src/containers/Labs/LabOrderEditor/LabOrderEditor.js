import React, { useState, useCallback } from 'react'

import { useHistory } from 'react-router-dom'
import { noop } from 'underscore'

import Modal from 'components/Modal/Modal'
import SuccessDialog from 'components/dialogs/SuccessDialog/SuccessDialog'

import LabOrderForm from '../LabOrderForm/LabOrderForm'

import { useCancelConfirmDialog } from 'hooks/common'

import { path } from 'lib/utils/ContextUtils'

import {
    LAB_RESEARCH_ORDER_STEPS as STEPS,
    LAB_RESEARCH_ORDER_TITLES as TITLES,
} from '../Constants'

import './LabOrderEditor.scss'

function LabOrderEditor({
    isOpen,
    onClose,
    onSubmit = noop,
    clientId,
    communityId,
    organizationId,
}) {
    const history = useHistory()

    const [CancelConfirmDialog, setCancelConfirmDialogOpen] = useCancelConfirmDialog()
    const [isSuccessDialogOpen, setIsSuccessDialogOpen] = useState(false)
    const [newOrderId, setNewOrderId] = useState(null)
    const [title, setTitle] = useState(TITLES[STEPS.FORM])

    function viewOrder() {
        setIsSuccessDialogOpen(false)
        history.push(path(`/labs/${newOrderId}`))
    }

    function closeIfNotChanged(hasChanges) {
        if (hasChanges) {
            setCancelConfirmDialogOpen(true)
        } else {
            onClose()
        }
    }

    function onSubmitSuccess(id) {
        onClose()
        onSubmit()
        setNewOrderId(id)
        setIsSuccessDialogOpen(true)
    }

    const onSaveSuccess = useCallback(onSubmitSuccess, [])
    const onViewOrder = useCallback(viewOrder, [newOrderId])
    const onCloseForm = useCallback(closeIfNotChanged, [onClose, setCancelConfirmDialogOpen])

    const onStepChanged = useCallback(step => setTitle(TITLES[step]), [])

    return (
        <>
            <CancelConfirmDialog onConfirm={onClose} />

            {isSuccessDialogOpen && (
                <SuccessDialog
                    isOpen
                    title="The order has been placed"
                    buttons={[
                        {
                            text: 'Close',
                            outline: true,
                            onClick: () => setIsSuccessDialogOpen(false)
                        },
                        {
                            text: 'View order',
                            onClick: onViewOrder
                        },
                    ]}
                />
            )}

            <Modal
                isOpen={isOpen}
                hasFooter={false}
                hasCloseBtn={false}
                title={title}
                className="LabOrderEditor"
            >
                <LabOrderForm
                    clientId={clientId}
                    onClose={onCloseForm}
                    communityId={communityId}
                    onChangedStep={onStepChanged}
                    organizationId={organizationId}
                    onSubmitSuccess={onSaveSuccess}
                />
            </Modal>
        </>
    )
}

export default LabOrderEditor
