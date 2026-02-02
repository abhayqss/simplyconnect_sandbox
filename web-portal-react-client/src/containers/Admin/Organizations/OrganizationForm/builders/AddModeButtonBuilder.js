import React from 'react'

import { STEP } from '../Constants'

import BaseButtonStrategy from './BaseButtonBuilder'

class AddModeButtonBuilder extends BaseButtonStrategy {
    build() {
        const {
            tab,
            buttons,
            permissions
        } = this.context

        const { 
            CloseButton,
            BackButton,
            NextButton,
            SaveButton,
        } = buttons

        return () => (
            <>
                {tab === STEP.LEGAL_INFO && CloseButton}
                {tab !== STEP.LEGAL_INFO && BackButton}
                {(permissions.canEditFeatures ?
                        tab !== STEP.FEATURES : tab === STEP.LEGAL_INFO
                ) && NextButton}
                {tab === STEP.FEATURES && permissions.canEditFeatures && SaveButton}
                {tab === STEP.MARKETPLACE && !permissions.canEditFeatures && SaveButton}
            </>
        )
    }
}

export default AddModeButtonBuilder
