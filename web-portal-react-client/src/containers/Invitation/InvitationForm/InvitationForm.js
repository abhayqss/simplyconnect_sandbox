import React, { Component } from "react";

import PropTypes from "prop-types";

import { pick } from "underscore";

import cn from "classnames";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { Form, Button } from "reactstrap";

import TextField from "components/Form/TextField/TextField";

import "./InvitationForm.scss";

import * as createFormActions from "redux/auth/password/create/form/createFormActions";
import * as complexityRulesActions from "redux/auth/password/complexity/rules/complexityRulesActions";

import { Response } from "lib/utils/AjaxUtils";

// Modern Password Requirements Component - Same as ContactList
const PasswordRequirements = ({ password = "" }) => {
  const requirements = [
    {
      id: "length",
      text: "At least 8 characters",
      test: (pwd) => pwd.length >= 8,
    },
    {
      id: "uppercase",
      text: "At least one uppercase letter",
      test: (pwd) => /[A-Z]/.test(pwd),
    },
    {
      id: "letters",
      text: "At least 2 letters",
      test: (pwd) => (pwd.match(/[a-zA-Z]/g) || []).length >= 2,
    },
    {
      id: "numbers",
      text: "At least 2 numbers",
      test: (pwd) => (pwd.match(/\d/g) || []).length >= 2,
    },
    {
      id: "special",
      text: "At least one special character (!@#$%^&*)",
      test: (pwd) => /[!@#$%^&*]/.test(pwd),
    },
  ];

  return (
    <div className="PasswordRequirements">
      {requirements.map((req) => {
        const isMet = password && req.test(password);
        const isActive = password.length > 0;

        return (
          <div
            key={req.id}
            className={cn("PasswordRequirements-Item", {
              "PasswordRequirements-Item--met": isMet,
              "PasswordRequirements-Item--active": isActive,
            })}
          >
            <div
              className={cn("PasswordRequirements-Checkbox", {
                "PasswordRequirements-Checkbox--checked": isMet,
              })}
            >
              {isMet && (
                <svg className="PasswordRequirements-CheckIcon" viewBox="0 0 12 10" fill="none">
                  <path
                    d="M1 5L4.5 8.5L11 1.5"
                    stroke="white"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  />
                </svg>
              )}
            </div>
            <span className="PasswordRequirements-Text">{req.text}</span>
          </div>
        );
      })}
    </div>
  );
};

function mapStateToProps(state) {
  const { auth } = state;

  return {
    fields: auth.password.create.form.fields,
    auth,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      ...bindActionCreators(createFormActions, dispatch),
      complexity: {
        rules: bindActionCreators(complexityRulesActions, dispatch),
      },
    },
  };
}

class InvitationForm extends Component {
  static propTypes = {
    organizationId: PropTypes.number,

    onAcceptSuccess: PropTypes.func,
    onDeclineSuccess: PropTypes.func,
  };

  static defaultProps = {
    onAcceptSuccess: () => {},
    onDeclineSuccess: () => {},
  };

  componentDidMount() {
    this.loadPasswordComplexityRules();
  }

  componentWillUnmount() {
    this.clear();
  }

  onChangeField = (field, value) => {
    this.props.actions.changeField(field, value);
  };

  onAccept = (e) => {
    e.preventDefault();

    this.validate().then((success) => {
      if (success) {
        this.accept().then(Response(this.props.onAcceptSuccess));
      }
    });
  };

  onDecline = (e) => {
    e.preventDefault();

    this.decline().then(Response(this.props.onDeclineSuccess));
  };

  loadPasswordComplexityRules() {
    const { actions, organizationId } = this.props;

    actions.complexity.rules.load({ organizationId });
  }

  changeField(name, value) {
    this.props.actions.changeField(name, value);
  }

  validate() {
    const { auth, fields, actions } = this.props;

    return actions.validate(fields.toJS(), auth.password.complexity.rules.data || {});
  }

  accept() {
    const { fields, actions } = this.props;

    return actions.submit(
      pick(fields.toJS(), (v, k) => !(k.includes("HasError") || k.includes("ErrorText") || k === "confirmPassword")),
    );
  }

  decline() {
    const {
      actions,
      fields: { token },
    } = this.props;

    return actions.decline(token);
  }

  clear() {
    this.props.actions.clear();
  }

  validatePassword(password) {
    const requirements = [
      (pwd) => pwd.length >= 8,
      (pwd) => /[A-Z]/.test(pwd),
      (pwd) => (pwd.match(/[a-zA-Z]/g) || []).length >= 2,
      (pwd) => (pwd.match(/\d/g) || []).length >= 2,
      (pwd) => /[!@#$%^&*]/.test(pwd),
    ];

    return password && requirements.every((test) => test(password));
  }

  render() {
    const { fields } = this.props;

    const {
      password,
      passwordHasError,
      passwordErrorText,

      confirmPassword,
      confirmPasswordHasError,
      confirmPasswordErrorText,
    } = fields;

    const isPasswordValid = this.validatePassword(password);
    const isConfirmPasswordValid = confirmPassword && password === confirmPassword;
    const isFormValid = isPasswordValid && isConfirmPasswordValid;

    return (
      <Form autoComplete="OFF" className="InvitationForm">
        <TextField
          type="password"
          name="password"
          value={password}
          hasError={passwordHasError}
          errorText={passwordErrorText}
          className="InvitationForm-TextField"
          placeholder="Password"
          autoComplete="new-password"
          onChange={this.onChangeField}
        />

        <PasswordRequirements password={password} />

        <TextField
          type="password"
          name="confirmPassword"
          value={confirmPassword}
          hasError={confirmPasswordHasError}
          errorText={confirmPasswordErrorText}
          className="InvitationForm-TextField"
          placeholder="Confirm Password"
          autoComplete="new-password"
          onChange={this.onChangeField}
        />

        <div className="margin-top-50">
          <Button outline color="success" className="InvitationForm-Btn" onClick={this.onDecline}>
            Decline
          </Button>
          <Button
            type="submit"
            color="success"
            className="InvitationForm-Btn"
            onClick={this.onAccept}
            disabled={!isFormValid}
          >
            Accept
          </Button>
        </div>
      </Form>
    );
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(InvitationForm);
