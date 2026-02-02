import React, {
    memo,
    useState,
    useCallback
} from 'react'

import PropTypes from 'prop-types'

import {
    Modal
} from 'components'

import {
    ConfirmDialog,
} from 'components/dialogs'

import { useToggle } from 'hooks/common'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import { ESignDocumentTemplateFieldPropertyForm as Form } from ".."

import './ESignDocumentTemplateFieldPropertyEditor.scss'

function ESignDocumentTemplateFieldPropertyEditor(
    {
        isOpen,
        fieldType,
        fieldLabel,
        fieldValue,
        hasValue = true,

        onClose,
        onSubmitSuccess
    }
) {
    const [isCancelEditConfirmDialogOpen, toggleCancelEditConfirmDialog] = useToggle();
    const [isCancelEditConfirmRequired, setCancelEditConfirmRequired] = useState(false)

    function close() {
        toggleCancelEditConfirmDialog(false)
        onClose()
    }

    function cancel() {
        if (isCancelEditConfirmRequired) {
            toggleCancelEditConfirmDialog(true)
        } else {
            onClose()
        }
    }

    const onCancel = useCallback(cancel, [isCancelEditConfirmRequired, onClose])

    return (
        <>
            {isOpen && (
                <Modal
                    isOpen={isOpen}
                    onClose={onCancel}
                    className="ESignDocumentTemplateFieldPropertyEditor"
                    title={`Set ${fieldType} Properties`}
                    hasFooter={false}
                >
                    <Form
                        hasValue={hasValue}
                        fieldType={fieldType}
                        fieldLabel={fieldLabel}
                        fieldValue={fieldValue}

                        onCancel={onCancel}
                        onSubmitSuccess={onSubmitSuccess}
                        onChanged={setCancelEditConfirmRequired}
                    />
                </Modal>
            )}

            {isCancelEditConfirmDialogOpen && (
                <ConfirmDialog
                    isOpen
                    icon={Warning}
                    confirmBtnText="OK"
                    title="The changes will not be saved."
                    onConfirm={close}
                    onCancel={toggleCancelEditConfirmDialog}
                />
            )}
        </>
    )
}

ESignDocumentTemplateFieldPropertyEditor.propTypes = {
    isOpen: PropTypes.bool,
    hasValue: PropTypes.bool,
    fieldType: PropTypes.string,
    fieldValue: PropTypes.any,
    fieldLabel: PropTypes.string,

    onClose: PropTypes.func,
    onSubmitSuccess: PropTypes.func
}

export default memo(ESignDocumentTemplateFieldPropertyEditor)