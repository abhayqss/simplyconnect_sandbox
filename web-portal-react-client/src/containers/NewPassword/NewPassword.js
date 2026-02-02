import React, { Component } from 'react'

import cn from 'classnames'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { withRouter } from 'react-router'
import { Redirect } from 'react-router-dom'

import { Button } from 'reactstrap'

import DocumentTitle from 'react-document-title'

import { Logo, ErrorViewer } from 'components'

import { withExternalProviderUrlCheck } from 'hocs'

import * as loginFormActions from 'redux/login/form/loginFormActions'
import * as newPasswordFormActions from 'redux/auth/password/new/form/newPasswordFormActions'
import * as resetPasswordRequestTokenActions
    from 'redux/auth/password/reset/request/token/resetPasswordRequestTokenActions'

import config from 'config'

import {
    matches,
    getQueryParams
} from 'lib/utils/UrlUtils'

import {
    path
} from 'lib/utils/ContextUtils'

import NewPasswordForm from './NewPasswordForm/NewPasswordForm'

import './NewPassword.scss'

const TERMS_OF_USE = 'http://www.simplyconnect.me/terms-of-use'
const PRIVACY_POLICY = 'http://www.simplyconnect.me/privacy-policy'

function mapStateToProps(state) {
    const { auth } = state

    const {
        form
    } = auth.password.new

    return { form, auth }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: {
            form: bindActionCreators(newPasswordFormActions, dispatch),

            request: {
                token: bindActionCreators(resetPasswordRequestTokenActions, dispatch)
            },

            login: {
                form: bindActionCreators(loginFormActions, dispatch)
            }
        }
    }
}

class NewPassword extends Component {
    state = {
        error: null,
        isLinkExpired: false,
        isNewUserCreated: false,
        isPasswordChanged: false,
        shouldRedirectToLogin: false,
        shouldRedirectToResetPassword: false,
        isPasswordResetRequestInvalid: false
    }

    componentDidMount() {
        const {
            history,
            location: {
                state,
                search,
                pathname
            }
        } = this.props

        if (search) {
            const { token } = getQueryParams(search)

            this.changeFormField('token', token)

            if (!state?.shouldCancelTokenValidation) {
                this.validateRequestToken(token)
                    .catch(this.onRequestTokenValidationFailure)
            } else {
                history.replace(pathname + search, {
                    ...state, shouldCancelTokenValidation: false
                })
            }
        } else this.cancel()
    }

    onCloseErrorViewer = () => {
        this.resetError()

        if (this.state.isPasswordResetRequestInvalid) {
            this.props.history.replace(
                path('/reset-password-request')
            )
        }
    }

    onCreatePasswordSuccess = (isNew) => {
        this.clearLoginFormFields()

        this.setState({
            isNewUserCreated: isNew,
            isPasswordChanged: !isNew,
            shouldRedirectToLogin: true
        })
    }

    onRequestTokenValidationFailure = error => {
        this.setState({
            error, isPasswordResetRequestInvalid: true
        })
    }

    onCancel = () => {
        this.cancel()
    }

    getError() {
        return (
            this.state.error
            || this.props.form.error
        )
    }

    resetError () {
        this.setState({ error: null })
        this.props.actions.form.clearError()
    }

    changeFormField(name, value) {
        this.props
            .actions
            .form
            .changeField(name, value)
    }

    clearLoginFormFields() {
        this.props
            .actions
            .login
            .form
            .clear()
    }

    validateRequestToken(token) {
        const {
            actions,
            isExternalProviderUrl
        } = this.props

        return actions.request.token.validate(
            token, { isExternalProviderUrl }
        )
    }

    cancel() {
        this.setState({
            shouldRedirectToLogin: true
        })
    }

    render() {
        const {
            className,
            isExternalProviderUrl,
            location: {
                state,
                search,
                pathname
            }
        } = this.props

        const {
            isLinkExpired,
            isNewUserCreated,
            isPasswordChanged,
            shouldRedirectToLogin,
            isPasswordResetRequestInvalid
        } = this.state

        const isRelevantHost = (
            config.location.host === window?.location?.host
        )

        if (shouldRedirectToLogin) {
            return (
                <Redirect
                    to={{
                        pathname: path(`${isExternalProviderUrl ? '/external-provider' : ''}/${isRelevantHost ? 'home' : ''}`),
                        state: {
                            isNewUserCreated,
                            isPasswordChanged,
                            isLoginPopupOpen: true,
                            nextPath: state?.nextPath
                        }
                    }}
                />
            )
        }

        const {
            email,
            organizationId
        } = getQueryParams(search) || {}

        const error = this.getError()

        const isReset = matches('*/reset-password*', pathname)

        return (
            <DocumentTitle
                title="Simply Connect | Create New Password">
                <div className={cn('NewPassword', className)}>
                    {isLinkExpired ? (
                        <div className="NewPassword-Body">
                            <Logo
                                iconSize={76}
                                className="NewPassword-LogoImage"
                            />
                            <div className="d-flex flex-column">
                                <span style={{ marginTop: 25 }} className="NewPassword-Title">
                                    Link has expired
                                </span>
                                <span className="NewPassword-InfoText">
                                    The link has expired. Please contact 
                                    the Sender to resend the invitation
                                </span>
                                <Button
                                    outline
                                    color='success'
                                    className="NewPassword-Btn"
                                    onClick={this.onCancel}>
                                    Back to login
                                </Button>
                            </div>
                        </div>
                    ) : (
                        <div className="NewPassword-Body">
                            <Logo
                                iconSize={76}
                                className="NewPassword-LogoImage"
                            />
                            <div className="d-flex flex-column">
                                <span className="NewPassword-Title">
                                    Create {isReset ? 'New' : ''} Password
                                </span>
                                <span className="NewPassword-InfoText">
                                    You are creating a new password for the Simply Connect Platform
                                </span>
                                <NewPasswordForm
                                    email={email}
                                    organizationId={organizationId}
                                    type={isReset ? 'reset' : 'create'}
                                    isExternalProvider={isExternalProviderUrl}
                                    onCancel={this.onCancel}
                                    onSubmitSuccess={this.onCreatePasswordSuccess}
                                />
                                {isExternalProviderUrl && (
                                    <div className="NewPassword-Text margin-top-20">
                                        By clicking the Save button, you agree to
                                        <a className="NewPassword-SimplyConnectPolicyLink"
                                           href={PRIVACY_POLICY}
                                           target="_blank">
                                            {'Simply Connect Policy '}
                                        </a>
                                        &
                                        <a className="NewPassword-SimplyConnectLink"
                                           href={TERMS_OF_USE}
                                           target="_blank">
                                            Terms of Use
                                        </a>
                                    </div>
                                )}
                            </div>
                        </div>
                    )}
                    {error && (
                        <ErrorViewer
                            isOpen
                            error={error}
                            closeBtnText={isPasswordResetRequestInvalid ? 'Ok' : 'Close'}
                            onClose={this.onCloseErrorViewer}
                        />
                    )}
                </div>
            </DocumentTitle>
        )
    }
}

export default withRouter(
    connect(mapStateToProps, mapDispatchToProps)(
        withExternalProviderUrlCheck(NewPassword)
    )
)