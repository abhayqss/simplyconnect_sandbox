import React from 'react'

import { STEP } from '../../Constants'

import BaseButtonStrategy from './BaseButtonBuilder'

class FormButtonBuilder extends BaseButtonStrategy {
    build() {
        const {
            step,
            buttons,
        } = this.context

        const { 
            CloseButton,
            BackButton,
            NextButton,
            SaveButton,
        } = buttons

        return () => (
            <>
                {step === STEP.SELECT_TYPE && CloseButton}
                {step !== STEP.SELECT_TYPE && BackButton}
                {step !== STEP.SELECT_USER && NextButton}
                {step === STEP.SELECT_USER && SaveButton}
            </>
        )
    }
}

export default FormButtonBuilder
