import React, { PureComponent } from "react";
import PropTypes from "prop-types";
import { isNumber, noop } from "underscore";

import Modal from "components/Modal/Modal";
import LoadCTMemberDetails from "actions/careTeam/LoadCTMemberDetails";
import { CareTeamMemberForm } from "../";

import "./CareTeamMemberEditor.scss";
import ClientEditor from "../../Clients/Clients/ClientEditor/ClientEditor";
import { WarningDialog } from "../../../components/dialogs";
import services from "../../../services/CareTeamMemberService";

class CareTeamMemberEditor extends PureComponent {
  state = {
    showEditModel: false,
    isRefresh: false,
    waringDialog: false,
  };

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
    haveRoleForRemind: PropTypes.bool,
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

  changeEditClientModel = (status) => {
    this.setState({ showEditModel: status });
  };

  changeIsRefresh = () => {
    this.setState({ isRefresh: !this.state.isRefresh });
  };

  changeWaringDialog = () => {
    this.setState({
      waringDialog: !this.state.waringDialog,
    });
  };

  setRemindShow = (clientId) => {
    services.neverShowRemindFill(clientId).then((res) => {
      this.setState({
        canShowReminder: res,
      });
    });
  };

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
      haveRoleForRemind,
    } = this.props;

    const fullTitle = `${this.isEditMode() ? "Edit" : "Add"} ${title}`;
    return (
      <>
        {/* 传入 过滤的id */}
        {/*{this.isEditMode() && <LoadCTMemberDetails params={{ memberId }} />}*/}
        <Modal
          isOpen
          hasFooter={false}
          hasCloseBtn={false}
          onClose={this.onClose}
          className="CareTeamMemberEditor"
          title={fullTitle}
        >
          <CareTeamMemberForm
            isEditMode={this.isEditMode()}
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
            haveRoleForRemind={haveRoleForRemind}
            changeEditClientModel={this.changeEditClientModel}
            isRefresh={this.state.isRefresh}
            changeIsRefresh={this.changeIsRefresh}
            changeWaringDialog={this.changeWaringDialog}
            waringDialog={this.state.waringDialog}
          />
        </Modal>

        <ClientEditor
          isOnDashboard={true}
          changeIsRefresh={this.changeIsRefresh}
          changeWaringDialog={this.changeWaringDialog}
          isRefresh={this.state.isRefresh}
          needPushClient={false}
          isOpen={this.state.showEditModel}
          clientId={Number(clientId)}
          isClientEmailRequired={false}
          isValidationNeed={false}
          onClose={() => this.changeEditClientModel(false)}
          onSaveSuccess={() => {
            this.changeEditClientModel(false);
          }}
        />

        <WarningDialog
          isOpen={this.state.waringDialog}
          title="Are you sure you don't want to show this message again?"
          buttons={[
            {
              text: "Cancel",
              onClick: () => {
                this.changeWaringDialog();
              },
            },
            {
              text: "OK",
              onClick: () => {
                this.changeWaringDialog();
                this.setRemindShow(clientId);
                this.changeIsRefresh();
              },
            },
          ]}
        />
      </>
    );
  }
}

export default CareTeamMemberEditor;
