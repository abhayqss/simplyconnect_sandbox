import React, {
    memo,
    useMemo,
    useState,
    useCallback
} from 'react'

import cn from 'classnames'

import Modal from 'components/Modal/Modal'
import { CancelConfirmDialog } from 'components/dialogs'

import { capitalize } from 'lib/utils/Utils'

import { STEP } from '../Constants'

import Form from '../AddToConversationForm/AddToConversationForm'

import './ConversationParticipantPicker.scss'

function ConversationParticipantPicker(
    {
        isOpen,
        onClose,
        excludedContactIds,
        onComplete: onCompleteCb
    }
) {
    const [type, setType] = useState('')
    const [step, setStep] = useState(STEP.SELECT_TYPE)

    const [isConfirmOpen, toggleConfirm] = useState(false)

    const onChangeStep = useCallback(shift => {
        if (type) setStep(step + shift)
    }, [type, step])

    const onFormFieldChanged = useCallback((name, value) => {
        if (name === 'type') setType(value)
    }, [])

    function closeIfNotChanged(hasChanges) {
        if (hasChanges) {
            toggleConfirm(true)
        } else {
            onClose()
        }
    }

    const onConfirmClosing = () => {
        onClose()
        toggleConfirm(false)
    }

    const onCloseForm = useCallback(closeIfNotChanged, [onClose])

    const onComplete = useCallback(data => {
        setType('')
        setStep(STEP.SELECT_TYPE)

        onClose()
        onCompleteCb(data)
    }, [onClose, onCompleteCb])

    return (
        <>
            <Modal
                isOpen={isOpen}
                hasFooter={false}
                hasCloseBtn={false}
                title={`Select ${step === STEP.SELECT_TYPE ? 'user' : capitalize(type)}`}
                className={cn(
                    'ConversationParticipantPicker',
                    { 'ConversationParticipantPicker_size_sm': step === STEP.SELECT_TYPE }
                )}
            >
                <Form
                    step={step}
                    excludedContactIds={excludedContactIds}
                    onClose={onCloseForm}
                    onChangeStep={onChangeStep}
                    onFieldChanged={onFormFieldChanged}
                    onSubmitSuccess={onComplete}
                />
            </Modal>

            {isConfirmOpen && (
                <CancelConfirmDialog
                    isOpen
                    title="The changes will not be saved"
                    onCancel={() => toggleConfirm(false)}
                    onConfirm={onConfirmClosing}
                />
            )}
        </>
    )
}

export default memo(ConversationParticipantPicker)