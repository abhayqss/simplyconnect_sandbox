import React, { PureComponent } from "react";
import PropTypes from "prop-types";
import { isNumber, noop } from "underscore";

import Modal from "components/Modal/Modal";
import LoadCTMemberDetails from "actions/careTeam/LoadCTMemberDetails";
import { CareTeamMemberForm } from "../";

import "./CareTeamMemberEditor.scss";

class CareTeamMemberEditor extends PureComponent {
  static propTypes = {
    clientId: PropTypes.number,
    communityId: PropTypes.string,
    clientCommunityId: PropTypes.string,
    organizationId: PropTypes.string.isRequired,
    memberId: PropTypes.number,
    onClose: PropTypes.func,
    onSaveSuccess: PropTypes.func,
    title: PropTypes.string,
    clientFullName: PropTypes.string,
    clientTwilioConversationSid: PropTypes.string,
    memberListId: PropTypes.array,
    hasGroup: PropTypes.bool,
    careManagerId: PropTypes.number,
    client: PropTypes.any,
    activeTopTabNum: PropTypes.number,
  };

  static defaultProps = {
    onClose: noop,
    onSaveSuccess: noop,
    hasGroup: false,
    title: "care team member",
  };

  get actions() {
    return this.props.actions;
  }

  isEditMode() {
    return isNumber(this.props.memberId);
  }

  render() {
    const {
      memberId,
      title,
      organizationId,
      clientCommunityId,
      clientOrganizationId,
      client,
      clientId,
      communityId,
      affiliation,
      hasGroup,
      clientFullName,
      clientTwilioConversationSid,
      memberListId,
      careManagerId,
    } = this.props;
    const fullTitle = `${this.isEditMode() ? "Edit" : "Add"} ${title}`;
    return (
      <>
        {this.isEditMode() && <LoadCTMemberDetails params={{ memberId }} />}
        <Modal
          isOpen
          hasFooter={false}
          hasCloseBtn={false}
          onClose={this.onClose}
          className="CareTeamMemberEditor"
          title={fullTitle}
        >
          <CareTeamMemberForm
            organizationId={organizationId}
            clientId={clientId}
            client={client}
            clientCommunityId={clientCommunityId}
            clientOrganizationId={clientOrganizationId}
            clientFullName={clientFullName}
            careManagerId={careManagerId}
            communityId={communityId} //client communityId
            memberId={memberId}
            hasGroup={hasGroup}
            clientTwilioConversationSid={clientTwilioConversationSid}
            onClose={this.props.onClose}
            affiliation={affiliation}
            memberListId={memberListId}
            onSaveSuccess={this.props.onSaveSuccess}
            activeTopTabNum={this.props.activeTopTabNum}
          />
        </Modal>
      </>
    );
  }
}

export default CareTeamMemberEditor;
