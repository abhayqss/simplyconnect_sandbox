import React, {
    memo,
    useRef,
    useMemo,
    useState,
    useCallback
} from 'react'

import cn from 'classnames'

import {
    reject,
    compact
} from 'underscore'

import Modal from 'components/Modal/Modal'
import { CancelConfirmDialog } from 'components/dialogs'

import { STEP } from '../Constants'

import Form from '../AddToGroupConversationForm/AddToGroupConversationForm'

import './GroupConversationParticipantPicker.scss'

const defaultStep = () => STEP.SELECT_TYPE

function GroupConversationParticipantPicker(
    {
        isOpen,
        onClose,
        groupName,
        isNewConversation,
        areClientsExcluded,
        excludedContactIds = [],
        onComplete: onCompleteCb
    }
) {
    const formRef = useRef()

    const [step, setStep] = useState(defaultStep)
    const [isConfirmOpen, toggleConfirm] = useState(false)

    let classNames = cn('GroupConversationParticipantPicker', {
        'GroupConversationParticipantPicker_size_sm': step === STEP.SELECT_TYPE
    })

    function changeStep(shift) {
        let nextStep = step + shift

        setStep(nextStep)
    }

    function closeIfNotChanged(hasChanges) {
        if (hasChanges) {
            toggleConfirm(true)
        } else {
            onClose()
            setStep(defaultStep)
        }
    }

    const onConfirmClosing = () => {
        onClose()
        toggleConfirm(false)
        setStep(defaultStep)
    }

    const onCloseForm = useCallback(closeIfNotChanged, [onClose])

    const onComplete = useCallback(data => {
        onClose()
        setStep(defaultStep)

        const {
            client,
            contacts,
            groupName,
            careTeamMembers
        } = data

        let clientId = null
        let associatedContactId = null

        if (!areClientsExcluded) {
            associatedContactId = client.associatedContactId
            if (associatedContactId) clientId = client.id
        }

        const contactIds = reject(compact([
            associatedContactId,
            ...client.careTeamMemberIds,
            ...careTeamMembers.ids,
            ...contacts.ids
        ]), id => excludedContactIds.includes(id))

        onCompleteCb({ clientId, contactIds, groupName })
    }, [
        onClose,
        onCompleteCb,
        areClientsExcluded,
        excludedContactIds
    ])

    return (
        <>
            <Modal
                isOpen={isOpen}
                hasFooter={false}
                title="Add to Group"
                onClose={() => formRef.current.cancel()}
                className={classNames}
            >
                <Form
                    step={step}
                    ref={formRef}
                    groupName={groupName}
                    isNewConversation={isNewConversation}
                    areClientsExcluded={areClientsExcluded}
                    excludedContactIds={excludedContactIds}
                    onClose={onCloseForm}
                    onChangeStep={changeStep}
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

export default memo(GroupConversationParticipantPicker)