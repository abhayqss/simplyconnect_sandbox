import React, {
    Fragment,
    useContext
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import { SignatureRequestContext } from 'contexts'

import { first } from 'lib/utils/Utils'
import { noop } from 'lib/utils/FuncUtils'
import { reject } from 'lib/utils/ArrayUtils'

import { E_SIGN_REQUEST_STEPS } from 'lib/Constants'

import { ReactComponent as CheckMark } from 'images/check-mark.svg'

import './RequestSignatureProgress.scss'

const {
    SIGNATURE_REQUEST,
    DOCUMENT_TEMPLATE,
    DOCUMENT_TEMPLATE_PREVIEW,
    MULTIPLE_SIGNATURE_REQUEST,
    DOCUMENT_TEMPLATE_PREVIEW_MULTIPLE
} = E_SIGN_REQUEST_STEPS

const STEPS = [SIGNATURE_REQUEST, DOCUMENT_TEMPLATE, DOCUMENT_TEMPLATE_PREVIEW]

const TITLES = {
    [SIGNATURE_REQUEST]: 'Enter E-sign Data',
    [MULTIPLE_SIGNATURE_REQUEST]: 'Enter E-sign Data',
    [DOCUMENT_TEMPLATE]: 'Populate Template Fields',
    [DOCUMENT_TEMPLATE_PREVIEW]: 'Preview Document',
    [DOCUMENT_TEMPLATE_PREVIEW_MULTIPLE]: 'Preview Document'
}

function isFirstStep(step) {
    return step === first(STEPS)
}

function RequestSignatureProgress(
    {
        onChangeStep = noop,
        hasSecondStep,
        className
    }
) {
    const { step } = useContext(SignatureRequestContext)

    const steps = reject(STEPS, (o, i) => {
        return (i === 1 && !hasSecondStep)
    })

    const currentStepIndex = steps.findIndex(o => o === step)

    return (
        <div className={cn('RequestSignatureProgress', className)}>
            {steps.map((o, index) => {
                const number = index + 1
                const isPastStep = index < currentStepIndex
                const isActiveStep = index === currentStepIndex

                return (
                    <Fragment key={o}>
                        {!isFirstStep(o) && (
                            <div className="RequestSignatureProgress-Separator" />
                        )}

                        <div
                            key={o}
                            className="RequestSignatureProgress-Step"
                            onClick={onChangeStep}
                        >
                            <div className={cn('RequestSignatureProgress-Round', {
                                'RequestSignatureProgress-Round_active': isActiveStep,
                                'RequestSignatureProgress-Round_past': isPastStep,
                            })}>
                                {isPastStep ? (
                                    <CheckMark className="RequestSignatureProgress-CheckMark" />
                                ) : (
                                    <div className="RequestSignatureProgress-Number">{number}</div>
                                )}
                            </div>

                            <div className="RequestSignatureProgress-Title" >
                                {TITLES[o]}
                            </div>
                        </div>
                    </Fragment>
                )
            })}
        </div>
    )
}

RequestSignatureProgress.propTypes = {
    onChangeStep: PTypes.func,
    hasSecondStep: PTypes.bool,
    className: PTypes.string
}

RequestSignatureProgress.defaultProps = {
    onChangeStep: noop,
    hasSecondStep: true
}

export default RequestSignatureProgress
