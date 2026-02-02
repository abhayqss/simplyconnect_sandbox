import React, {
    memo,
    useState,
    useEffect,
    useCallback
} from 'react'

import { Button } from 'reactstrap'

import { useToggle } from 'hooks/common'

import { ESignDocumentTemplateContextProvider } from 'contexts'

import { Modal } from 'components'

import { ConfirmDialog, SuccessDialog } from 'components/dialogs'

import {
    isEmpty
} from 'lib/utils/Utils'

import {
    E_SIGN_DOCUMENT_TEMPLATE_BUILDER_STEPS
} from 'lib/Constants'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import {
    ESignDocumentTemplateFieldEditor,
    ESignDocumentTemplateFileUploadForm,
    ESignDocumentTemplateOrganizationForm
} from './'

import './ESignDocumentTemplateEditor.scss'

const {
    FILE_UPLOAD,
    ORGANIZATION,
    FIELD_EDITOR,
} = E_SIGN_DOCUMENT_TEMPLATE_BUILDER_STEPS

const STEP = {
    [FILE_UPLOAD]: 0,
    [ORGANIZATION]: 0,
    [FIELD_EDITOR]: 1
}

const FORMS = {
    [FILE_UPLOAD]: ESignDocumentTemplateFileUploadForm,
    [ORGANIZATION]: ESignDocumentTemplateOrganizationForm,
    [FIELD_EDITOR]: ESignDocumentTemplateFieldEditor,
}

function Actions({ next, update, submit }) {
    return {
        [FILE_UPLOAD]: (...args) => {
            update(...args)
            next()
        },
        [ORGANIZATION]: (...args) => {
            update(...args)
            next()
        },
        [FIELD_EDITOR]: (...args) => {
            submit(update(...args))
        },
    }
}

function getTitle({ isEditing, isCopying }) {
    if (isCopying) return 'Create a Copy of E-sign Template'
    return `${isEditing ? 'Edit' : 'Create'} E-sign Template`
}

function isFirstStep(step) {
    return [STEP[FILE_UPLOAD], STEP[ORGANIZATION]].includes(step)
}

function isLastStep(step) {
    return step === STEP[FIELD_EDITOR]
}

function ESignDocumentTemplateEditor(
    {
        isOpen,
        onClose,
        isCopying,
        documentId,
        templateId,
        communityId,
        organizationId,
        onUploadSuccess
    }
) {
    const [data, setData] = useState({})
    const [step, setStep] = useState(0)

    const isEditing = !isEmpty(templateId)
    const stepStory = [isCopying ? ORGANIZATION : FILE_UPLOAD, FIELD_EDITOR]

    const [isBackConfirmDialogOpen, toggleBackConfirmDialog] = useToggle(false)
    const [isCancelEditConfirmDialogOpen, toggleCancelEditConfirmDialog] = useToggle()
    const [isSaveSuccessDialogOpen, toggleSaveSuccessDialog] = useToggle()

    const [isCancelEditConfirmRequired, setCancelEditConfirmRequired] = useState(false)

    const stepName = stepStory[step]

    const actions = Actions({
        next,
        update: updateData,
        submit: onSubmitSuccess
    })

    const successTitle = (isEditing && !isCopying) ? 'The template has been updated' : 'The template has been created'

    const onBack = useCallback((isChanged) => {
        if (isChanged) {
            toggleBackConfirmDialog(true);
        } else {
            back()
        }

    }, [toggleBackConfirmDialog])

    const onBackConfirm = useCallback(() => {
        toggleBackConfirmDialog(false);
        back()
    }, [toggleBackConfirmDialog])

    const Form = FORMS[stepName]

    function back(steps) {
        setStep(step => steps ? step - steps : --step)
    }

    function next() {
        setStep(step => step + 1)
    }

    function updateData(o) {
        setData(pr => ({ ...pr, ...o }))
    }

    function submit(...args) {
        actions[stepName](...args)
    }

    function close() {
        toggleCancelEditConfirmDialog(false)
        _onClose()
    }

    function cancel() {
        if (isCancelEditConfirmRequired) {
            toggleCancelEditConfirmDialog(true)
        } else {
            _onClose()
        }
    }

    function onSubmitSuccess() {
        toggleSaveSuccessDialog(true);
        onUploadSuccess();
    }

    const _onClose = useCallback(() => {
        onClose();

        setStep(0);
        setData({});
    }, [onClose])

    const onCancel = useCallback(cancel, [isCancelEditConfirmRequired, _onClose])

    const onCloseSubmitDialog = useCallback(() => {
        toggleSaveSuccessDialog(false);
        _onClose();
    }, [_onClose, toggleSaveSuccessDialog])

    const onTemplateChanged = useCallback((isChanged) => {
        setCancelEditConfirmRequired(prev => (prev && isLastStep(step)) || isChanged)
    }, [step])

    useEffect(() => {
        setStep(STEP[isCopying ? ORGANIZATION : (
            isEditing ? FIELD_EDITOR : FILE_UPLOAD
        )])
    }, [isEditing, isCopying])

    return (
        <>
            {isOpen && (
                <Modal
                    hasCloseBtn
                    isOpen={isOpen}
                    onClose={onCancel}
                    className="ESignDocumentTemplateEditor"
                    title={getTitle({ isEditing, isCopying })}
                    hasFooter={false}
                >
                    <div className="ESignDocumentTemplateEditor-Body">
                        <ESignDocumentTemplateContextProvider
                            step={stepName}
                            templateData={data}
                        >
                            <Form
                                isCopying={isCopying}
                                documentId={documentId}
                                templateId={templateId}
                                communityId={communityId}
                                organizationId={organizationId}
                                onCancel={onCancel}
                                onBack={onBack}
                                onSubmitSuccess={submit}
                                onChanged={onTemplateChanged}
                            >
                                {({
                                    back,
                                    save,
                                    cancel,
                                    isValidToSave,
                                    isDraftEnabled,
                                    isValidToSubmit,
                                    isValidToComplete
                                }) => (
                                    <div className="ESignDocumentTemplateEditor-FormFooter">
                                        <div className="ESignDocumentTemplateEditor-Buttons">
                                            {!isFirstStep(step) && (!isEditing || isCopying) && (
                                                <Button
                                                    outline
                                                    color="success"
                                                    className="margin-right-25"
                                                    onClick={back}
                                                >
                                                    Back
                                                </Button>
                                            )}

                                            <Button
                                                outline
                                                color="success"
                                                className="margin-right-25"
                                                onClick={cancel}
                                            >
                                                Close
                                            </Button>
                                            {isLastStep(step) ? (
                                                <>
                                                    {isDraftEnabled && (
                                                        <Button
                                                            color="success"
                                                            onClick={save}
                                                            disabled={!isValidToSave}
                                                            className="ESignDocumentTemplateEditor-SubmitBtn margin-right-25"
                                                        >
                                                            Save Draft
                                                        </Button>
                                                    )}
                                                    <Button
                                                        color="success"
                                                        disabled={!isValidToComplete}
                                                        className="ESignDocumentTemplateEditor-SubmitBtn"
                                                    >
                                                        Complete
                                                    </Button>
                                                </>
                                            ) : (
                                                <Button
                                                    color="success"
                                                    disabled={!isValidToSubmit}
                                                    className="ESignDocumentTemplateEditor-SubmitBtn"
                                                >
                                                    Next
                                                </Button>
                                            )}
                                        </div>
                                    </div>
                                )}
                            </Form>
                        </ESignDocumentTemplateContextProvider>
                    </div>
                </Modal>
            )}

            {isBackConfirmDialogOpen && (
                <ConfirmDialog
                    isOpen
                    icon={Warning}
                    confirmBtnText="OK"
                    title="The changes will not be saved."
                    onConfirm={onBackConfirm}
                    onCancel={() => toggleBackConfirmDialog(false)}
                />
            )}

            {isSaveSuccessDialogOpen && (
                <SuccessDialog
                    isOpen
                    title={successTitle}
                    buttons={[
                        {
                            text: 'Close',
                            onClick: onCloseSubmitDialog
                        }
                    ]}
                />
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

export default memo(ESignDocumentTemplateEditor)