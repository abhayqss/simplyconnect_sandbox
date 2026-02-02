import React, {
    memo,
    useState,
    useCallback,
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

import { ESignDocumentTemplateFieldRuleForm as Form } from "../"

import './ESignDocumentTemplateFieldRuleEditor.scss'

function ESignDocumentTemplateFieldRuleEditor(
    {
        isOpen,
        fields,
        signatures,
        rules,

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
                    hasCloseBtn
                    title="Rules"
                    isOpen={isOpen}
                    hasFooter={false}
                    onClose={onCancel}
                    className="ESignDocumentTemplateFieldRuleEditor"
                >
                    <Form
                        rules={rules}
                        fields={fields}
                        signatures={signatures}

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

ESignDocumentTemplateFieldRuleEditor.propTypes = {
    isOpen: PropTypes.bool,
    rules: PropTypes.array,
    fields: PropTypes.array,
    signatures: PropTypes.array,


    onClose: PropTypes.func,
    onSubmitSuccess: PropTypes.func
}

export default memo(ESignDocumentTemplateFieldRuleEditor)