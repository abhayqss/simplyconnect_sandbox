import React, { Component } from "react";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { withRouter } from "react-router-dom";

import DocumentTitle from "react-document-title";

import { Row, Col } from "reactstrap";

import { omit, noop, values, compose } from "underscore";

import { Logo } from "components";

import { withExternalProviderUrlCheck } from "hocs";

import { Loader } from "components";

import LoginForm from "./LoginForm/LoginForm";
import OldPasswordForm from "./OldPasswordForm/OldPasswordForm";

import * as loginActions from "redux/auth/login/loginActions";
import * as logoutActions from "redux/auth/logout/logoutActions";
import * as loginFormActions from "redux/login/form/loginFormActions";
import * as oldPasswordFormActions from "redux/auth/password/old/form/oldPasswordFormActions";

import { primaryOrganizationStore } from "lib/stores";

import { SYSTEM_ROLES, SERVER_ERROR_CODES } from "lib/Constants";

import { allAreNotEmpty, ifElse, isNotEmpty } from "lib/utils/Utils";
import { path } from "lib/utils/ContextUtils";
import { Response } from "lib/utils/AjaxUtils";
import { getQueryParams } from "lib/utils/UrlUtils";
import { isNotEmptyOrBlank } from "lib/utils/ObjectUtils";

import "./Login.scss";

const { CONTENT_CREATOR } = SYSTEM_ROLES;

const { EXPIRED_CREDENTIALS } = SERVER_ERROR_CODES;

const ERRORS = {
  NO_PERMISSION: `You don't have permissions to see this record. Please contact your Administrator`,
  NOT_SYNCED: `The resident record has not been synced yet. Try again later.`,
};

function mapStateToProps(state) {
  const { auth, login, report } = state;
  return { auth, login, report };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      login: {
        ...bindActionCreators(loginActions, dispatch),
        form: bindActionCreators(loginFormActions, dispatch),
      },
      logout: bindActionCreators(logoutActions, dispatch),
      old: {
        password: {
          form: bindActionCreators(oldPasswordFormActions, dispatch),
        },
      },
    },
  };
}

class Login extends Component {
  state = {
    isLoaderShown: false,

    isSessionExpired: false,
    isNewUserCreated: false,
    isPasswordChanged: false,
    isPasswordExpired: false,
    isPasswordComplexityChanged: false,
  };

  componentDidMount() {
    const { state } = this.props.location;

    if (state) {
      this.setState({
        isNewUserCreated: state.isNewUserCreated,
        isSessionExpired: state.isSessionExpired,
        isPasswordChanged: state.isPasswordChanged,
        isPasswordExpired: state.isPasswordExpired,
        isPasswordComplexityChanged: state.isPasswordComplexityChanged,
      });
    }

    this.handleExtSystemLogin();
    this.handle4dLogin();
  }

  componentDidUpdate(prevProps) {
    const {
      location,
      auth: { login },
    } = this.props;

    const state = location.state || {};
    const prevState = prevProps.location.state || {};

    if (state.isLoginPopupOpen && !prevState.isLoginPopupOpen) {
      this.setState({ isLoginPopupOpen: true });
    }

    if (state.isSessionExpired && !prevState.isSessionExpired) {
      this.setState({ isSessionExpired: true });
    }

    if (state.isPasswordChanged && !prevState.isPasswordChanged) {
      this.setState({ isPasswordChanged: true });
    }

    if (state.isNewUserCreated && !prevState.isNewUserCreated) {
      this.setState({ isNewUserCreated: true });
    }

    if (state.isPasswordExpired && !prevState.isPasswordExpired) {
      this.setState({ isPasswordExpired: true });
    }

    if (state.isPasswordComplexityChanged && !prevState.isPasswordComplexityChanged) {
      this.setState({ isPasswordComplexityChanged: true });
    }

    if (login.error && !prevProps.auth.login.error) {
      if (login.error.code === EXPIRED_CREDENTIALS) {
        this.clearLoginError();
        this.setState({ isPasswordExpired: true });
      } else
        this.setState({
          isSessionExpired: false,
          isPasswordExpired: false,
          isPasswordChanged: false,
          isPasswordComplexityChanged: false,
        });
    }
  }

  onLoginSuccess = (user) => {
    const { history, location, isExternalProviderUrl } = this.props;

    this.clearPrimaryOrganizationIfExists();

    let nextPath = "/admin-events";

    /* if (user.isPaperlessHealthcareEnabled) {
         nextPath = '/paperless-healthcare'
     }

     if (user?.roleName === CONTENT_CREATOR) {
         nextPath = '/admin/organizations'
     }

     if (isExternalProviderUrl) {
         nextPath = '/external-provider/inbound-referrals'
     }*/

    history.push(location.state?.nextPath || path(nextPath));
  };

  onExtSystemLoginSuccess = ({ data: user }) => {
    const { history, location } = this.props;

    this.clearPrimaryOrganizationIfExists();

    let nextPath = "/admin-events";

    // if (user.isPaperlessHealthcareEnabled) {
    //   nextPath = "/paperless-healthcare";
    // }
    //
    // if (user?.roleName === CONTENT_CREATOR) {
    //   nextPath = "/admin/organizations";
    // }

    const params = getQueryParams(location.search);

    history.push(path(params?.target || nextPath));
  };

  onSSOLoginSuccess = ({ data, resNum }) => {
    const { targetClientId, canViewTargetClient, isTargetClientSynced } = data;

    this.clearPrimaryOrganizationIfExists();

    const navigate = ({ nextPath, ...state }) => this.props.history.push(nextPath, state);

    const goToClient = ifElse(
      () => canViewTargetClient,
      // () => navigate({ nextPath: path(`/clients/${targetClientId}`) }),
      () => navigate({ nextPath: path(`/admin-events`) }),
      // () => navigate({ nextPath: path(`/clients`), alertMessage: ERRORS.NO_PERMISSION }),
      () => navigate({ nextPath: path(`/admin-events`), alertMessage: ERRORS.NO_PERMISSION }),
    );

    const gotToClients = ifElse(
      () => isTargetClientSynced,
      goToClient,
      () => navigate({ nextPath: path(`/admin-events`), alertMessage: ~resNum ? ERRORS.NOT_SYNCED : null }),
    );

    gotToClients();
  };

  onChangePasswordSuccess = () => {
    this.setState({
      isPasswordExpired: false,
      isPasswordComplexityChanged: false,
    });

    this.clearLoginError();
  };

  onCloseLoginPopup = () => {
    this.setState({
      isPasswordExpired: false,
      isPasswordComplexityChanged: false,
    });
  };

  get actions() {
    return this.props.actions;
  }

  get error() {
    const { login, password } = this.props.auth;

    return login.error || password.old.form.error;
  }

  get authUser() {
    return this.props.auth.login.user.data;
  }

  clearErrors() {
    this.clearLoginError();
    this.clearOldPasswordFormError();
  }

  logout() {
    return this.actions.logout.logout();
  }

  handleExtSystemLogin() {
    const { location } = this.props;

    const params = getQueryParams(location.search, null, ["companyId", "username", "password"]);

    if (isNotEmptyOrBlank(params) && allAreNotEmpty(values(params))) {
      this.loginFromExtSystem(params);
    }
  }

  handle4dLogin() {
    const { location } = this.props;

    const parameters = getQueryParams(location.search, null, [
      "port",
      "userId",
      "resNum", // optional
      "companyId",
      "subdomain",
      "sessionId",
    ]);

    const getRequiredParams = (o) => omit(o, "resNum");
    const areAllFilled = (v) => isNotEmpty(v) && allAreNotEmpty(...v);

    const hasRequiredParams = compose(areAllFilled, values, getRequiredParams);

    if (hasRequiredParams(parameters)) {
      this.loginFrom4d(parameters);
    }
  }

  clearLoginError() {
    return this.actions.login.clearError();
  }

  clearLoginForm() {
    this.actions.login.form.clear();
  }

  clearOldPasswordForm() {
    this.actions.old.password.form.clear();
  }

  clearOldPasswordFormError() {
    this.actions.old.password.form.clearError();
  }

  clearPrimaryOrganizationIfExists() {
    const o = primaryOrganizationStore.get();
    if (o) primaryOrganizationStore.clear();
  }

  toggleLoader = (state) => {
    this.setState((prevState) => ({
      isLoaderShown: state ?? !prevState.isLoaderShown,
    }));
  };

  clearHistory = () => {
    this.props.history.replace();
  };

  loginFromExtSystem(params) {
    this.toggleLoader(true);

    this.actions.login
      .login(params)
      .then(Response(this.onExtSystemLoginSuccess, noop, this.clearHistory))
      .finally(this.toggleLoader);
  }

  loginFrom4d(params) {
    this.toggleLoader(true);

    this.actions.login
      .loginFrom4d(params)
      .then(Response(this.onSSOLoginSuccess, null, this.clearHistory))
      .finally(this.toggleLoader);
  }

  render() {
    const {
      isLoaderShown,

      isSessionExpired,
      isNewUserCreated,
      isPasswordChanged,
      isPasswordExpired,
      isPasswordComplexityChanged,
    } = this.state;

    const { login, isExternalProviderUrl } = this.props;

    const { username, companyId } = login.form.fields;

    return (
      <DocumentTitle title="Simply Connect | Login">
        <div className="Login">
          {isLoaderShown && <Loader hasBackdrop />}

          <div className="Login-Body">
            <Logo iconSize={55} className="Login-Logo" />
            <Row>
              <Col md={{ size: 4, offset: 4 }}>
                <div className="flex-1 d-flex flex-column align-items-center">
                  {isPasswordExpired ? (
                    <div className="flex-1">
                      <span className="NavigationBar-OldPasswordTitle">Create New Password</span>
                      <div className="d-flex flex-column">
                        {!isPasswordComplexityChanged && (
                          <span className="NavigationBar-OldPasswordInfoText">
                            Your password for {companyId}, {username} has expired and must be changed
                          </span>
                        )}
                        <OldPasswordForm
                          username={username}
                          companyId={companyId}
                          onCancel={this.onCloseLoginPopup}
                          onSubmitSuccess={this.onChangePasswordSuccess}
                          isExternalProvider={isExternalProviderUrl}
                        />
                      </div>
                    </div>
                  ) : (
                    <LoginForm isExternalProvider={isExternalProviderUrl} onLoginSuccess={this.onLoginSuccess} />
                  )}
                  {this.error && this.error.code !== EXPIRED_CREDENTIALS && (
                    <div className="Login-Alert">{this.error.message}</div>
                  )}
                  {isPasswordChanged && (
                    <div className="Login-Alert">
                      The password has been changed. Please log in with the new credentials
                    </div>
                  )}
                  {isPasswordComplexityChanged && (
                    <div className="Login-Alert">
                      Password complexity requirements changed. Please update your password
                    </div>
                  )}
                  {isNewUserCreated && (
                    <div className="Login-Alert">Thank you for registering to Simply Connect portal</div>
                  )}
                  {isSessionExpired && (
                    <div className="Login-Alert">You have been logged out due to inactivity. Please login again</div>
                  )}
                </div>
              </Col>
            </Row>
          </div>
        </div>
      </DocumentTitle>
    );
  }
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(withExternalProviderUrlCheck(Login)));
