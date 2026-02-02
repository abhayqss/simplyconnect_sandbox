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

import { ESignDocumentTemplateFolderAssignerForm as Form } from ".."

import './ESignDocumentTemplateFolderAssigner.scss'

function ESignDocumentTemplateFolderAssigner(
    {
        isOpen,
        communityOptions,
        assignedFolders,

        onClose,
        onSubmitSuccess
    }
) {
    const [isCancelEditConfirmDialogOpen, toggleCancelEditConfirmDialog] = useToggle()
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
                    hasFooter={false}
                    hasCloseBtn={false}
                    title="Assign Template to Folder"
                    className="ESignDocumentTemplateFolderAssigner"

                    onClose={onCancel}
                >
                    <Form
                        assignedFolders={assignedFolders}
                        communityOptions={communityOptions}

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

ESignDocumentTemplateFolderAssigner.propTypes = {
    isOpen: PropTypes.bool,
    communityOptions: PropTypes.arrayOf(
        PropTypes.shape({ text: PropTypes.string, value: PropTypes.number })),
    assignedFolders: PropTypes.arrayOf(
        PropTypes.shape({ communityId: PropTypes.number, folderId: PropTypes.number })),

    onClose: PropTypes.func,
    onSubmitSuccess: PropTypes.func
}

export default memo(ESignDocumentTemplateFolderAssigner)