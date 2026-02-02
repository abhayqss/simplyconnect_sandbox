import React from 'react'

import { STEP } from '../../Constants'

function FormButtons({ step, buttons }) {
    const { 
        CloseButton,
        BackButton,
        NextButton,
        SaveButton,
    } = buttons

    return (
        <>
            {step === STEP.SELECT_TYPE && CloseButton}
            {step !== STEP.SELECT_TYPE && BackButton}
            {step !== STEP.SELECT_USER && NextButton}
            {step === STEP.SELECT_USER && SaveButton}
        </>
    )
}

export default FormButtons
