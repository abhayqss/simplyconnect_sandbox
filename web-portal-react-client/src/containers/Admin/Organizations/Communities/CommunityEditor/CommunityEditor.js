import React, { memo } from 'react'

import {
    isNumber
} from 'underscore'

import Modal from 'components/Modal/Modal'

import './CommunityEditor.scss'

import CommunityForm from '../CommunityForm2/CommunityForm'

function CommunityEditor({
    isOpen,
    onClose,
    communityId,
    organizationId,
    defaultActiveTab,

    onSaveSuccess
}) {
    const isEditMode = isNumber(communityId)

    return (
        <Modal
            isOpen={isOpen}
            className="CommunityEditor"
            onClose={onClose}
            hasCloseBtn={false}
            hasFooter={false}
            title={isEditMode ? 'Edit community details' : 'Create community'}
            dialogTitle="The updates will not be saved."
        >
            <CommunityForm
                communityId={communityId}
                organizationId={organizationId}
                defaultActiveTab={defaultActiveTab}
                onClose={onClose}
                onSubmitSuccess={onSaveSuccess}
            />
        </Modal>
    )
}

export default memo(CommunityEditor)
