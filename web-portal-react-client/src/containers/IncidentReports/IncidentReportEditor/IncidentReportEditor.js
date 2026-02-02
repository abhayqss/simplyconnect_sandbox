import React, { memo, useState, useCallback, useRef } from 'react'

import { useHistory } from 'react-router-dom'
import { isNumber } from 'underscore'

import Modal from 'components/Modal/Modal'
import SuccessDialog from 'components/dialogs/SuccessDialog/SuccessDialog'

import IncidentReportForm from '../IncidentReportForm/IncidentReportForm'

import { useCancelConfirmDialog } from 'hooks/common'

import { path } from 'lib/utils/ContextUtils'

import './IncidentReportEditor.scss'

function IncidentReportEditor({
    isOpen,
    onClose,
    eventId,
    clientId,
    reportId,
    organizationId,
    ...props
}) {
    const form = useRef()
    const history = useHistory()
    const isEditMode = isNumber(reportId)

    const [CancelConfirmDialog, setCancelConfirmDialogOpen] = useCancelConfirmDialog()
    const [isSuccessDialogOpen, setIsSuccessDialogOpen] = useState(false)
    const [isSuccessDraftDialogOpen, setIsSuccessDraftDialogOpen] = useState(false)

    const [newIncidentReportId, setNewIncidentReportId] = useState(null)

    function viewIncidentReport() {
        setIsSuccessDialogOpen(false)
        history.push(path(`/incident-reports/${newIncidentReportId}`))
    }

    function closeIfNotChanged(hasChanges) {
        if (hasChanges) {
            setCancelConfirmDialogOpen(true)
        } else {
            onClose()
        }
    }

    function onSubmitSuccess(id, isDraft) {
        onClose()
        props.onSaveSuccess(id)
        setNewIncidentReportId(id)

        isDraft ? setIsSuccessDraftDialogOpen(true) : setIsSuccessDialogOpen(true)
    }

    const onForwardContext = useCallback(context => form.current = context, [])

    const onSaveSuccess = useCallback(onSubmitSuccess, [onClose, props.onSaveSuccess])
    const onViewIncidentReport = useCallback(viewIncidentReport, [newIncidentReportId])
    const onCloseForm = useCallback(closeIfNotChanged, [onClose, setCancelConfirmDialogOpen])

    return (
        <>
            <CancelConfirmDialog onConfirm={onClose} />

            {isSuccessDraftDialogOpen && (
                <SuccessDialog
                    isOpen
                    title="The incident report has been saved."
                    buttons={[
                        {
                            text: 'Close',
                            outline: true,
                            onClick: () => {
                                setIsSuccessDraftDialogOpen(false)
                            }
                        },
                        {
                            text: 'Edit',
                            onClick: () => {
                                props.onEditDraft({
                                    eventId,
                                    clientId,
                                    id: newIncidentReportId
                                })

                                setIsSuccessDraftDialogOpen(false)
                            }
                        },
                    ]}
                />
            )}

            {isSuccessDialogOpen && (
                <SuccessDialog
                    isOpen
                    title="The report has been submitted."
                    buttons={[
                        {
                            text: 'Close',
                            outline: true,
                            onClick: () => setIsSuccessDialogOpen(false)
                        },
                        {
                            text: 'View',
                            onClick: onViewIncidentReport
                        },
                    ]}
                />
            )}

            <Modal
                isOpen={isOpen}
                hasFooter={false}
                onClose={() => form.current?.onCancel()}
                title={`${isEditMode ? 'Edit' : 'Create'} incident report`}
                className="IncidentReportEditor"
            >
                <IncidentReportForm
                    eventId={eventId}
                    clientId={clientId}
                    reportId={reportId}
                    organizationId={organizationId}
                    onClose={onCloseForm}
                    onSubmitSuccess={onSaveSuccess}
                    forwardContext={onForwardContext}
                />
            </Modal>
        </>
    )
}

export default memo(IncidentReportEditor)
