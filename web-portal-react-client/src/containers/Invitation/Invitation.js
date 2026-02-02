import React, { Component } from "react";

import cn from "classnames";

import { connect } from "react-redux";
import { bindActionCreators, compose } from "redux";
import { Link, Redirect, withRouter } from "react-router-dom";

import { Button } from "reactstrap";

import DocumentTitle from "react-document-title";

import { ErrorViewer, Logo } from "components";

import { SuccessDialog } from "components/dialogs";

import "./Invitation.scss";

import InvitationForm from "./InvitationForm/InvitationForm";

import * as createFormActions from "redux/auth/password/create/form/createFormActions";
import * as invitationRequestTokenActions from "redux/auth/invitation/request/token/invitationRequestTokenActions";

import { SERVER_ERROR_CODES } from "lib/Constants";

import { path } from "lib/utils/ContextUtils";
import { getQueryParams } from "lib/utils/UrlUtils";

const { EXPIRED_CREDENTIALS } = SERVER_ERROR_CODES;

const TERMS_OF_USE = "http://www.simplyconnect.me/terms-of-use";
const PRIVACY_POLICY = "http://www.simplyconnect.me/privacy-policy";

function mapStateToProps(state) {
  return { auth: state.auth };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      form: bindActionCreators(createFormActions, dispatch),
      request: {
        token: bindActionCreators(invitationRequestTokenActions, dispatch),
      },
    },
  };
}

class Invitation extends Component {
  state = {
    isLinkExpired: false,
    isSuccessDialogOpen: false,
    shouldRedirectToLogin: false,
  };

  componentDidMount() {
    const { search } = this.props.location;

    if (search) {
      const { token } = getQueryParams(search);
      this.changeFormField("token", token);
      this.validateRequestToken(token).catch(this.onValidateRequestTokenFailure);
    } else this.navigateToLogin();
  }

  onValidateRequestTokenFailure = (e) => {
    if (e.code === EXPIRED_CREDENTIALS) {
      this.setState({ isLinkExpired: true });
    } else this.navigateToLogin();
  };

  onAcceptSuccess = () => {
    this.setState({
      isSuccessDialogOpen: true,
    });
  };

  onNavigateToLogin = () => {
    this.navigateToLogin();
  };

  onCloseSuccessDialog = () => {
    this.setState({
      isSuccessDialogOpen: false,
      shouldRedirectToLogin: true,
    });
  };

  onResetError = () => {
    this.resetError();
  };

  getError() {
    return this.props.auth.password.create.form.error;
  }

  resetError() {
    this.props.actions.form.clearError();
  }

  changeFormField(name, value) {
    this.props.actions.form.changeField(name, value);
  }

  validateRequestToken(token) {
    return this.props.actions.request.token.validate(token);
  }

  navigateToLogin() {
    this.setState({
      shouldRedirectToLogin: true,
    });
  }

  render() {
    const {
      className,
      location: { search },
    } = this.props;

    const { isLinkExpired, isSuccessDialogOpen, shouldRedirectToLogin } = this.state;

    if (shouldRedirectToLogin) {
      return (
        <Redirect
          to={{
            state: { isLoginPopupOpen: true },
            pathname: path("/home"),
          }}
        />
      );
    }

    const { inviter, companyId, organizationId } = getQueryParams(search) || {};

    const error = this.getError();

    return (
      <DocumentTitle title="Simply Connect | Invitation">
        <div className={cn("Invitation", className)}>
          {isLinkExpired ? (
            <div className="Invitation-Body">
              <Logo iconSize={76} className="Invitation-LogoImage" />
              <div className="d-flex flex-column">
                <span className="Invitation-Title margin-top-25">Link has expired</span>
                <span className="Invitation-InfoText">
                  Please resubmit your registration using the Simply Connect mobile app.
                </span>
                <Button outline color="success" className="Invitation-Btn" onClick={this.onNavigateToLogin}>
                  Back to login
                </Button>
              </div>
            </div>
          ) : (
            <div className="Invitation-Body">
              <Logo iconSize={76} className="Invitation-LogoImage" />
              <div className="d-flex flex-column">
                <div className="d-flex flex-column">
                  <span className="Invitation-Title">
                    {inviter} has invited you to join the Simply Connect Platform.
                  </span>
                  <span className="Invitation-InfoText">
                    Please set up a password for future access
                    <Link className="Invitation-SimplyConnectLink" to={path("/home")} target="_blank">
                      Simply Connect.
                    </Link>
                  </span>
                  <span className="Invitation-EmailText">
                    Use <b>your email address</b> as the username and <b>{companyId}</b> as the company ID.
                  </span>
                  <InvitationForm
                    organizationId={organizationId}
                    onAcceptSuccess={this.onAcceptSuccess}
                    onDeclineSuccess={this.onNavigateToLogin}
                  />
                  <span className="Invitation-Text margin-top-20">
                    By clicking the Accept button, you agree to
                    <a className="NewPassword-SimplyConnectPolicyLink" href={PRIVACY_POLICY} target="_blank">
                      {"Simply Connect Policy "}
                    </a>
                    &
                    <a className="NewPassword-SimplyConnectPolicyLink" href={TERMS_OF_USE} target="_blank">
                      Terms of Use
                    </a>
                  </span>
                </div>
              </div>
            </div>
          )}
          {isSuccessDialogOpen && (
            <SuccessDialog
              isOpen
              className={className}
              buttons={[
                {
                  text: "OK",
                  color: "success",
                  onClick: this.onCloseSuccessDialog,
                },
              ]}
            >
              <div className="Invitation-SuccessText">
                Thank you for registering to the
                <Link className="Invitation-SimplyConnectLink" to={path("/home")}>
                  Simply Connect portal.
                </Link>
                <br />
                Please use <b>your email address</b> as login and <b>{companyId}</b> as company ID.
              </div>
            </SuccessDialog>
          )}
          {error && <ErrorViewer isOpen error={error} onClose={this.onResetError} />}
        </div>
      </DocumentTitle>
    );
  }
}

export default compose(withRouter, connect(mapStateToProps, mapDispatchToProps))(Invitation);
