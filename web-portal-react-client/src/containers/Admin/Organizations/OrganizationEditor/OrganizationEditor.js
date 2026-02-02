import React, { PureComponent, useState } from "react";

import PropTypes from "prop-types";

import { noop, isNumber } from "underscore";

import Modal from "components/Modal/Modal";

import "./OrganizationEditor.scss";

import OrganizationForm from "../OrganizationForm/OrganizationForm";

class OrganizationEditor extends PureComponent {
  static propTypes = {
    isOpen: PropTypes.bool,
    organizationId: PropTypes.number,

    onClose: PropTypes.func,
    onSaveSuccess: PropTypes.func,
  };
  state = {
    isFormChanged: false,
  };

  static defaultProps = {
    onClose: noop,
    onSaveSuccess: noop,
  };

  isEditMode() {
    return isNumber(this.props.organizationId);
  }
  changeIsFormChanged = (data) => {
    this.setState({
      isFormChanged: data,
    });
  };

  handleClose = () => {
    this.props.onClose(this.state.isFormChanged);
  };
  render() {
    const { isOpen, organizationId } = this.props;

    return (
      <Modal
        isOpen={isOpen}
        className="OrganizationEditor"
        hasCloseBtn={true}
        hasFooter={false}
        onClose={this.handleClose}
        title={this.isEditMode() ? "Edit organization details" : "Create organization"}
      >
        <OrganizationForm
          organizationId={organizationId}
          onClose={this.props.onClose}
          setIsFormChanged={this.changeIsFormChanged}
          onSaveSuccess={this.props.onSaveSuccess}
        />
      </Modal>
    );
  }
}

export default OrganizationEditor;
