import React, {Component} from 'react'

import PropTypes from 'prop-types'
import { isNumber, pick } from 'underscore'

import {connect} from 'react-redux'
import {bindActionCreators} from 'redux'

import {Form, Button} from 'reactstrap'

import TextField from 'components/Form/TextField/TextField'

import './NewPasswordForm.scss'

import * as newPasswordFormActions from 'redux/auth/password/new/form/newPasswordFormActions'
import * as complexityRulesActions from 'redux/auth/password/complexity/rules/complexityRulesActions'

import { Response } from 'lib/utils/AjaxUtils'

import { ReactComponent as Oval } from 'images/oval.svg'

function Detail ({ children }) {
    return (
        <div className="NewPassword-Detail">
            <Oval className="NewPassword-DetailIcon" />
            <span className="NewPassword-DetailText">
                {children}
            </span>
        </div>
    )
}

function mapStateToProps (state) {
    const { auth } = state

    const {
        form
    } = auth.password.new

    return {
        error: form.error,
        fields: form.fields,
        auth,
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: {
            ...bindActionCreators(newPasswordFormActions, dispatch),
            complexity: {
                rules: bindActionCreators(complexityRulesActions, dispatch)
            }
        }
    }
}

class NewPasswordForm extends Component {

    static propTypes = {
        type: PropTypes.oneOf(['create', 'reset']),
        email: PropTypes.string,
        organizationId: PropTypes.number,
        isExternalProvider: PropTypes.bool,

        onCancel: PropTypes.func,
        onSubmitSuccess: PropTypes.func
    }

    static defaultProps = {
        type: 'create',
        canSubmit: true,
        isExternalProvider: false,

        onCancel: () => {},
        onSubmitSuccess: () => {}
    }

    componentDidMount () {
        this.loadComplexityRules()

        this.changeField('email', this.props.email)
    }

    componentWillUnmount () {
        this.clear()
    }

    onChangeField = (name, value) => {
        this.changeField(name, value)
    }

    onSubmit = (e) => {
        e.preventDefault()

        let isNew = this.props.type === 'create'

        this.validate().then(success => {
            if (success) {
                this.submit()
                    .then(Response(() => this.props.onSubmitSuccess(isNew)))
            }
        })
    }

    onCancel = (e) => {
        e.preventDefault()
        this.props.onCancel(e)
    }

    loadComplexityRules () {
        const {
            actions,
            organizationId,
            isExternalProvider
        } = this.props

        actions.complexity.rules.load({
            organizationId, isExternalProvider
        })
    }

    changeField (name, value) {
        this
            .props
            .actions
            .changeField(name, value)
    }

    validate () {
        const {
            auth,
            type,
            actions,
            fields: {
                firstName,
                lastName,
                password,
                confirmPassword
            }
        } = this.props

        return actions.validate(
            {
                firstName,
                lastName,
                password,
                confirmPassword
            },
            {
                ...(type === 'reset') && {
                    excluded: ['firstName', 'lastName']
                },
                ...auth.password.complexity.rules.data || {}
            }
        )
    }

    submit () {
        const {
            type,
            actions,
            fields: {
                token,
                firstName,
                lastName,
                password
            },
            isExternalProvider
        } = this.props

        let data = { token, password }

        if (type === 'reset') {
            return actions.reset(data)
        }

        if (isExternalProvider) {
            return actions.create(
                { ...data, firstName, lastName },
                { isExternalProvider }
            )
        }

        return actions.create(data)
    }

    clear () {
        this.props.actions.clear()
    }

    render () {
        const {
            type,
            auth,
            email,
            fields,
            isExternalProvider
        } = this.props

        const {
            firstName,
            firstNameHasError,
            firstNameErrorText,

            lastName,
            lastNameHasError,
            lastNameErrorText,

            password,
            passwordHasError,
            passwordErrorText,

            confirmPassword,
            confirmPasswordHasError,
            confirmPasswordErrorText
        } = fields

        const {
            length,
            upperCaseCount,
            lowerCaseCount,
            alphabeticCount,
            arabicNumeralCount,
            nonAlphaNumeralCount
        } = auth.password.complexity.rules.data || {}

        return (
            <Form className="NewPasswordForm">
                {type === 'reset' && (
                    <TextField
                        type="text"
                        name="email"
                        value={email}
                        isDisabled={true}
                        className="NewPasswordForm-TextField"
                    />
                )}
                {isExternalProvider && type === 'create' && (
                    <>
                        <TextField
                            type="text"
                            name="firstName"
                            value={firstName}
                            hasError={firstNameHasError}
                            errorText={firstNameErrorText}
                            className="NewPasswordForm-TextField"
                            placeholder="First Name"
                            onChange={this.onChangeField}
                        />
                        <TextField
                            type="text"
                            name="lastName"
                            value={lastName}
                            hasError={lastNameHasError}
                            errorText={lastNameErrorText}
                            className="NewPasswordForm-TextField"
                            placeholder="Last Name"
                            onChange={this.onChangeField}
                        />
                    </>
                )}
                <TextField
                    type="password"
                    name="password"
                    value={password}
                    hasError={passwordHasError}
                    errorText={passwordErrorText}
                    className="NewPasswordForm-TextField"
                    placeholder="Password"
                    onChange={this.onChangeField}
                />
                <TextField
                    type="password"
                    name="confirmPassword"
                    value={confirmPassword}
                    hasError={confirmPasswordHasError}
                    errorText={confirmPasswordErrorText}
                    className="NewPasswordForm-TextField"
                    placeholder="Confirm Password"
                    onChange={this.onChangeField}
                />
                {isNumber(length) && (
                    <div className='NewPassword-PasswordComplexityRules'>
                        <div className="NewPassword-Text">
                            Password requirements:
                        </div>
                        <Detail>{length} characters minimum</Detail>
                        {arabicNumeralCount > 0 && (
                            <Detail>{arabicNumeralCount} number(s)</Detail>
                        )}
                        {nonAlphaNumeralCount > 0 && (
                            <Detail>{nonAlphaNumeralCount} special symbol(s) (e.g. @#$%!)</Detail>
                        )}
                        {alphabeticCount > 0 && (
                            <Detail>{alphabeticCount} alphabetic character(s) minimum</Detail>
                        )}
                        {(lowerCaseCount > 0) && (
                            <Detail>{lowerCaseCount} lowercase character(s)</Detail>
                        )}
                        {(upperCaseCount > 0) && (
                            <Detail>{upperCaseCount} uppercase character(s)</Detail>
                        )}
                        <Detail>No spaces</Detail>
                    </div>
                )}
                <div>
                    <Button
                        outline
                        color='success'
                        className="NewPasswordForm-Btn"
                        onClick={this.onCancel}>
                        Cancel
                    </Button>
                    <Button
                        type="submit"
                        color='success'
                        className="NewPasswordForm-Btn"
                        onClick={this.onSubmit}>
                        Save
                    </Button>
                </div>
            </Form>
        )
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(NewPasswordForm)