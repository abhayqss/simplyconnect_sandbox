import React, {
    memo,
    useMemo,
    useState,
    useCallback
} from 'react'

import cn from 'classnames'

import {
    useHistory,
    withRouter
} from 'react-router-dom'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { Button } from 'reactstrap'

import DocumentTitle from 'react-document-title'

import {
    Logo,
    ErrorViewer
} from 'components'

import {
    withExternalProviderUrlCheck
} from 'hocs'

import {
    useQueryParams,
    useIsRelevantHost
} from 'hooks/common'

import './ResetPassword.scss'

import ResetPasswordForm from './ResetPasswordForm/ResetPasswordForm'

import * as resetPasswordFormActions from 'redux/auth/password/reset/form/resetPasswordFormActions'

import {
    getQueryParams
} from 'lib/utils/UrlUtils'

import {
    path
} from 'lib/utils/ContextUtils'

function mapStateToProps (state) {
    const { form } = state.auth.password.reset

    return {
        error: form.error,
        fields: form.fields
    }
}

function mapDispatchToProps (dispatch) {
    return {
        actions: {
            form: bindActionCreators(resetPasswordFormActions, dispatch)
        }
    }
}

function ResetPassword(
    {
        error,
        fields,
        actions,
        className,
        isExternalProviderUrl
    }
) {
    const [isEmailSent, setEmailSent] = useState(false)

    const history = useHistory()
    const isRelevantHost = useIsRelevantHost()

    const { companyId } = useQueryParams()

    const resetError = useCallback(() => {
        actions.form.clearError()
    }, [actions])

    const onResetPasswordSuccess = useCallback(() => {
        setEmailSent(true)
    }, [])

    const onBack = useCallback(() => {

        if (isRelevantHost) {
            history.push(
                path(`${isExternalProviderUrl ? '/external-provider' : ''}/home`),
                { isLoginPopupOpen: true }
            )
        } else {
            history.goBack()
        }
    }, [
        history,
        isRelevantHost,
        isExternalProviderUrl
    ])

    return (
        <DocumentTitle title="Simply Connect | Reset Password">
            <div className={cn('ResetPassword', className)}>
                <div className="ResetPassword-Body">
                    <Logo
                        iconSize={76}
                        className="ResetPassword-LogoImage"
                    />
                    <div className="d-flex flex-column">
                            <span className="ResetPassword-Title">
                                Reset Your Password
                            </span>
                        <span className="ResetPassword-InfoText">
                                {isEmailSent ? (
                                    <span>
                                        An email with further instructions on resetting your
                                        password has been sent to <b>{fields.email}</b>
                                    </span>
                                ) : (
                                    `You can reset your password by providing your company ID and email address.
                                     A link will then be sent to the email provided directing you to reset your password.`
                                )}
                            </span>
                        {isEmailSent ? (
                            <Button
                                outline
                                color='success'
                                className="ResetPasswordForm-Btn"
                                onClick={onBack}
                            >
                                Back{isRelevantHost ? ' to login' : ''}
                            </Button>
                        ) : (
                            <ResetPasswordForm
                                companyId={companyId}
                                isExternalProvider={isExternalProviderUrl}
                                cancelButtonText={`Back${isRelevantHost ? ' to login' : ''}`}
                                onCancel={onBack}
                                onSubmitSuccess={onResetPasswordSuccess}
                            />
                        )}
                    </div>
                </div>
                {error && (
                    <ErrorViewer
                        isOpen
                        error={error}
                        onClose={resetError}
                    />
                )}
            </div>
        </DocumentTitle>
    )
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(
    withExternalProviderUrlCheck(memo(ResetPassword))
))