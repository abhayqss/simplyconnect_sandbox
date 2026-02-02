import React, { Component } from 'react'

import { pick } from 'underscore'
import PropTypes from 'prop-types'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { Form, Button } from 'reactstrap'

import TextField from 'components/Form/TextField/TextField'

import './ResetPasswordForm.scss'

import * as resetPasswordFormActions from 'redux/auth/password/reset/form/resetPasswordFormActions'

import { Response } from 'lib/utils/AjaxUtils'

function mapStateToProps (state) {
    return {
        fields: state.auth.password.reset.form.fields
    }
}

function mapDispatchToProps (dispatch) {
    return {
        actions: {
            ...bindActionCreators(resetPasswordFormActions, dispatch)
        }
    }
}

class ResetPasswordForm extends Component {

    static propTypes = {
        companyId: PropTypes.string,
        cancelButtonText: PropTypes.string,
        onCancel: PropTypes.func,
        onSubmitSuccess: PropTypes.func
    }

    static defaultProps = {
        onCancel: () => {},
        onSubmitSuccess: () => {},
        cancelButtonText: 'Back to login'
    }

    componentDidMount() {
        this.clear()

        const { companyId } = this.props

        if (companyId) {
            this.changeField('companyId', companyId)
        }
    }

    onChangeFiled = (name, value) => {
        this.changeField(name, value)
    }

    onSubmit = (e) => {
        e.preventDefault()

        this.validate()
            .then(success => {
                if (success) {
                    this.submit()
                        .then(Response(this.props.onSubmitSuccess))
                }
            })
    }

    onCancel = () => {
        this.props.onCancel()
    }

    changeField (name, value) {
        this
            .props
            .actions
            .changeField(name, value)
    }

    validate () {
        const {
            fields,
            actions,
            isExternalProvider
        } = this.props

        return actions.validate(
            fields.toJS(),
            isExternalProvider ? {
                excluded: ['companyId']
            } : {}
        )
    }

    submit() {
        const {
            actions,
            fields: {
                email,
                companyId
            },
            isExternalProvider
        } = this.props

        const data = { email }

        if (!isExternalProvider) {
            data.companyId = companyId
        }

        return actions.submit(
            data, { isExternalProvider }
        )
    }

    clear () {
        this
            .props
            .actions
            .clear()
    }

    render () {
        const {
            fields,
            companyId,
            cancelButtonText,
            isExternalProvider
        } = this.props

        return (
            <Form className="ResetPasswordForm">
                {!isExternalProvider && (
                    <TextField
                        type="text"
                        name="companyId"
                        isDisabled={companyId}
                        value={companyId ?? fields.companyId}
                        hasError={fields.companyIdHasError}
                        errorText={fields.companyIdErrorText}
                        className="ResetPasswordForm-TextField"
                        placeholder="Company ID"
                        onChange={this.onChangeFiled}
                    />
                )}
                <TextField
                    type="text"
                    name="email"
                    value={fields.email}
                    hasError={fields.emailHasError}
                    errorText={fields.emailErrorText}
                    className="ResetPasswordForm-TextField"
                    placeholder="Email"
                    onChange={this.onChangeFiled}
                />
                <div className="margin-top-30">
                    <Button
                        outline
                        color='success'
                        className="ResetPasswordForm-Btn"
                        onClick={this.onCancel}>
                        {cancelButtonText}
                    </Button>
                    <Button
                        type="submit"
                        color='success'
                        className="ResetPasswordForm-Btn"
                        onClick={this.onSubmit}>
                        Send email
                    </Button>
                </div>
            </Form>
        )
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(ResetPasswordForm)