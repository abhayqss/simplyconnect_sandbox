import React, { Component } from "react";

import PropTypes from "prop-types";

import { noop, isNumber } from "underscore";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { withRouter } from "react-router-dom";

import Modal from "components/Modal/Modal";
import ConfirmDialog from "components/dialogs/ConfirmDialog/ConfirmDialog";

import LoadServicePlanDetailsAction from "actions/clients/LoadServicePlanDetailsAction";

import * as servicePlanDetailsActions from "redux/client/servicePlan/details/servicePlanDetailsActions";

import { ReactComponent as Info } from "images/info.svg";
import { ReactComponent as Warning } from "images/alert-yellow.svg";

import "./ServicePlanEditor.scss";

import ScoringInfoHint from "./ScoringInfoHint/ScoringInfoHint";
import ServicePlanForm from "../ServicePlanForm/ServicePlanForm";

function mapStateToProps(state) {
  const { form, details } = state.client.servicePlan;

  return { form, details };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      details: bindActionCreators(servicePlanDetailsActions, dispatch),
    },
  };
}

class ServicePlanEditor extends Component {
  static propTypes = {
    isOpen: PropTypes.bool,
    planId: PropTypes.number,
    clientId: PropTypes.number,

    onClose: PropTypes.func,
    onSaveSuccess: PropTypes.func,
  };

  static defaultProps = {
    onClose: noop,
    onSaveSuccess: noop,
  };

  modal = React.createRef();

  state = {
    step: 0,
    isScoringHintOpen: false,
    isConfirmDialogOpen: false,
  };

  componentWillUnmount() {
    this.actions.details.clear();
  }

  onClose = () => {
    if (this.props.form.isChanged()) {
      this.setState({
        isConfirmDialogOpen: true,
      });
    } else this.props.onClose();
  };

  onCloseConfirmDialog = () => {
    this.setState({
      isConfirmDialogOpen: false,
    });
  };

  onEnterScoringHint = () => {
    this.setState({
      isScoringHintOpen: true,
    });
  };

  onLeaveScoringHint = () => {
    this.setState({
      isScoringHintOpen: false,
    });
  };

  onChangeStep = (step) => {
    this.setState({ step });
  };

  get isEditMode() {
    return isNumber(this.props.planId);
  }

  get actions() {
    return this.props.actions;
  }

  render() {
    const {
      isOpen,

      planId,
      clientId,

      details,

      onClose,
      onSaveSuccess,
    } = this.props;

    const { step, isScoringHintOpen, isConfirmDialogOpen } = this.state;

    return (
      <>
        <Modal
          isOpen={isOpen}
          ref={this.modal}
          hasFooter={false}
          onClose={this.onClose}
          className="ServicePlanEditor"
          footerClassName="ServicePlanEditor-Footer"
          renderHeader={() => (
            <>
              {step === 0 ? `${this.isEditMode ? "Edit" : "Create"} Service Plan` : "Scoring"}
              {step > 0 && (
                <div className="position-relative">
                  <Info
                    className="ScoringHint-Icon"
                    onMouseEnter={this.onEnterScoringHint}
                    onMouseLeave={this.onLeaveScoringHint}
                  />
                  {isScoringHintOpen && <ScoringInfoHint />}
                </div>
              )}
            </>
          )}
        >
          <LoadServicePlanDetailsAction params={{ clientId, planId }} shouldPerform={() => this.isEditMode} />
          <ServicePlanForm
            planId={planId}
            clientId={clientId}
            isLoading={this.isEditMode && details.isFetching}
            onCancel={this.onClose}
            onSubmitSuccess={onSaveSuccess}
            onCurrentUnit={this.onChangeStep}
          />
        </Modal>
        {isConfirmDialogOpen && (
          <ConfirmDialog
            isOpen
            icon={Warning}
            confirmBtnText="OK"
            title="The updates will not be saved"
            onConfirm={onClose}
            onCancel={this.onCloseConfirmDialog}
          />
        )}
      </>
    );
  }
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ServicePlanEditor));
