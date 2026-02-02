import React, { Component } from 'react'

import PropTypes from 'prop-types'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { Link } from 'react-router-dom'

import { Button, Form } from 'reactstrap'

import { TextField } from 'components/Form'

import * as loginActions from 'redux/auth/login/loginActions'
import * as loginFormActions from 'redux/login/form/loginFormActions'

import { path } from 'lib/utils/ContextUtils'
import { Response } from 'lib/utils/AjaxUtils'

import Cookies from 'js-cookie';

import './LoginForm.scss'

function mapStateToProps(state) {
  const {
    auth,
    login: { form }
  } = state

  return {
    fields: form.fields,
    auth
  }
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      ...bindActionCreators(loginFormActions, dispatch),
      auth: {
        login: bindActionCreators(loginActions, dispatch)
      }
    }
  }
}

class LoginForm extends Component {

  static propTypes = {
    onLoginSuccess: PropTypes.func,
    isExternalProvider: PropTypes.bool
  }

  static defaultProps = {
    isExternalProvider: false,
    onLoginSuccess: () => {
    }
  }

  removeAllCookies = () => {
    const cookies = Cookies.get(); // 获取所有 cookie

    for (let cookie in cookies) {
      Cookies.remove(cookie); // 删除每一个 cookie
    }
  };

  componentDidMount() {
    sessionStorage.clear();
    localStorage.clear();
    this.removeAllCookies();
  }

  onChangeField = (name, value) => {
    this.changeField(name.split('-')[0], value)
  }

  onLogin = e => {
    e.preventDefault()

    this.validate().then(success => {
      if (success) {
        this.login().then(Response(({ data }) => {
          this.props.onLoginSuccess(data)
        }))
      }
    })
  }

  changeField(name, value) {
    this.props
      .actions
      .changeField(name, value)
  }

  validate() {
    const {
      actions,
      fields,
      isExternalProvider
    } = this.props

    return actions.validate(
      fields.toJS(),
      isExternalProvider ? {
        excluded: ['companyId']
      } : {}
    )
  }

  login() {
    const {
      actions,
      fields: {
        username,
        password,
        companyId
      },
      isExternalProvider
    } = this.props


    return actions
      .auth
      .login
      .login({
        username,
        password,
        ...!isExternalProvider && {
          companyId
        }
      }, { isExternalProvider })
  }

  render() {
    const {
      fields: {
        companyId,
        companyIdHasError,
        companyIdErrorText,

        username,
        usernameHasError,
        usernameErrorText,

        password,
        passwordHasError,
        passwordErrorText
      },
      isExternalProvider
    } = this.props

    return (
      <Form autoComplete="OFF" className="LoginForm text-left">
        {!isExternalProvider && (
          <TextField
            type="text"
            name="companyId"
            value={companyId}
            className="LoginForm-TextField"
            label="Company ID"
            hasError={companyIdHasError}
            errorText={companyIdErrorText}
            onChange={this.onChangeField}
          />
        )}
        <TextField
          type="text"
          name="username"
          value={username}
          hasError={usernameHasError}
          errorText={usernameErrorText}
          className="LoginForm-TextField"
          label="Login"
          onChange={this.onChangeField}
        />
        <TextField
          type="password"
          name="password"
          hasError={passwordHasError}
          errorText={passwordErrorText}
          className="LoginForm-TextField"
          label="Password"
          onChange={this.onChangeField}
        />
        <div className="d-flex justify-content-between align-items-center margin-bottom-20">
          <Link
            className="LoginForm-ForgetPasswordLink"
            to={path(`${isExternalProvider ? '/external-provider' : ''}/reset-password-request`)}
          >
            Forgot Password?
          </Link>
          <Button
            type="submit"
            color='success'
            className="LoginForm-LoginBtn"
            onClick={this.onLogin}>
            Sign in
          </Button>
        </div>
      </Form>
    )
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(LoginForm)
