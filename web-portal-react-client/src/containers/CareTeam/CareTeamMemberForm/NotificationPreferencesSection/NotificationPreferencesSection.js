import React, { PureComponent } from "react";

import PropTypes from "prop-types";

import { noop } from "underscore";

import { connect } from "react-redux";
import { bindActionCreators, compose } from "redux";

import widthDirectoryData from "hocs/withDirectoryData";

import * as careTeamMemberFormActions from "redux/care/team/member/form/careTeamMemberFormActions";

import { isEmpty, isNotEmpty } from "lib/utils/Utils";

import { NOTIFICATION_CHANNELS_TYPES, NOTIFICATION_RESPONSIBILITY_TYPES } from "lib/Constants";

import { TChannel, TEventTypes, TResponsibility } from "./types";

import { NotificationPreference } from "../";

import service from "services/CareTeamMemberService";

const { List } = require("immutable");

function mapDispatchToProps(dispatch) {
  return {
    actions: bindActionCreators(careTeamMemberFormActions, dispatch),
  };
}

const { VIEWABLE, NOT_VIEWABLE } = NOTIFICATION_RESPONSIBILITY_TYPES;
const { EMAIL, PUSH_NOTIFICATION } = NOTIFICATION_CHANNELS_TYPES;

class NotificationPreferencesSection extends PureComponent {
  static propTypes = {
    title: PropTypes.string,
    eventGroup: PropTypes.arrayOf(TEventTypes).isRequired,
    channels: PropTypes.arrayOf(TChannel).isRequired,
    responsibilities: PropTypes.arrayOf(TResponsibility).isRequired,
    preferences: PropTypes.instanceOf(List).isRequired,
    onChangeResponsibility: PropTypes.func,
    onChangeChannels: PropTypes.func,

    onChangeTimeRange: PropTypes.func,

    missedMedicationReminderId: PropTypes.number | PropTypes.string,
    haveRoleForRemind: PropTypes.bool,
    isRefresh: PropTypes.bool,
    changeEditClientModel: PropTypes.func,
    changeIsRefresh: PropTypes.func,
    changeWaringDialog: PropTypes.func,
    waringDialog: PropTypes.bool,
    clientFullName: PropTypes.string,
  };

  static defaultProps = {
    title: "",
    onChangeResponsibility: noop,
    onChangeChannels: noop,
  };

  state = {
    canShowReminder: false,
  };

  onChangeResponsibility = async (eventTypeId, value) => {
    this.modifyChannelIfNeeded(eventTypeId, value);
    this.onChangePreference("responsibilityName", eventTypeId, value);
    this.props.onChangeResponsibility(eventTypeId, value);
  };

  onChangeChannel = async (id, value) => {
    await this.onChangePreference("channels", id, value);
    this.props.onChangeChannels(id, value);
  };

  onChangeThreshold = async (value) => {
    await this.onChangePreference("threshold", this.props.pmmaEventId, value);
  };

  onChangeTimeRange = async (eventTypeId, value) => {
    await this.onChangePreference("ratio", eventTypeId, value);
  };

  onChangePreference = async (property, eventTypeId, value) => {
    await this.actions.changeNotificationPreference(property, eventTypeId, value);
  };

  get actions() {
    return this.props.actions;
  }

  getDirectoryData() {
    return this.props.getDirectoryData({
      notificationPreferences: ["care", "team", "notification", "preference"],
    });
  }

  modifyChannelIfNeeded(id, respName) {
    if ([VIEWABLE, NOT_VIEWABLE].includes(respName)) {
      this.onChangeChannel(id, []);
    } else if (isEmpty(this.getData(id).channels)) {
      this.onChangeChannel(id, [EMAIL, PUSH_NOTIFICATION]);
    }
  }

  getData(id) {
    const byEventId = (np) => np.eventTypeId === id;

    let data = this.props.preferences.find(byEventId);

    if (data == null) {
      const { notificationPreferences } = this.getDirectoryData();

      data = notificationPreferences.find(byEventId);
    }

    return data || {};
  }

  getErrors(data) {
    const index = this.props.preferences.findIndex((np) => np === data);

    return this.props.errors[index] || {};
  }

  onCloseEditor() {
    this.props.changeEditClientModel(false);
  }

  onSaveSuccess() {
    this.props.changeEditClientModel(false);
  }

  componentDidMount() {
    this.fetchData();
  }

  componentDidUpdate(prevProps, prevState) {
    const { isRefresh } = this.props;
    // 检查 isRefresh 是否改变
    if (prevProps.isRefresh !== isRefresh) {
      this.fetchData();
    }
  }

  fetchData() {
    const { clientId, title } = this.props;
    if (title === "Medications Alerts & Reactions") {
      service.showRemindFill(clientId).then((res) => {
        this.setState({
          canShowReminder: res,
        });
      });
    }
  }

  render() {
    const {
      eventGroup,
      responsibilities,
      channels,
      title,
      missedMedicationReminderId,
      clientId,
      haveRoleForRemind,
      clientFullName,
      pmmaEventId,
      currentRoleName,
    } = this.props;
    return (
      <>
        <div className="CareTeamMemberForm-Section">
          <div className="CareTeamMemberForm-SectionTitle">{title}</div>

          {title === "Medications Alerts & Reactions" && this.state.canShowReminder && (
            <div className="remindUsersToFillInInformation">
              <div className="remindUsersToFillInInformationInfo">
                Please update {clientFullName}'s record to include phone number or email to ensure medication intake
                tracking.
              </div>

              {haveRoleForRemind && (
                <>
                  <div
                    className="remindUsersToFillInInformationEdit"
                    onClick={() => {
                      this.props.changeEditClientModel(true);
                    }}
                  >
                    Edit Record
                  </div>

                  <div className={"dividerLine"}>|</div>
                </>
              )}

              <div className="remindUsersToFillInInformationDontShow" onClick={() => this.props.changeWaringDialog()}>
                Don't show again
              </div>
            </div>
          )}

          {eventGroup.map((eventType) => {
            if (
              (currentRoleName !== "Pharmacist" && eventType.id === pmmaEventId) ||
              (currentRoleName !== "Pharmacist Vendor" && eventType.id === pmmaEventId)
            ) {
              return null;
            }

            const shouldHidePmmaEvent = currentRoleName !== "Pharmacist" && currentRoleName !== "Pharmacist Vendor";

            const data = this.getData(eventType.id);

            const pmmaData = eventType.id === missedMedicationReminderId ? this.getData(pmmaEventId) : null;

            const thresholdToPass = eventType.id === missedMedicationReminderId ? pmmaData?.threshold : data.threshold;

            const errors = this.getErrors(data);

            return (
              <NotificationPreference
                key={eventType.id}
                data={data}
                errors={errors}
                channels={channels}
                name={eventType.name}
                title={eventType.title}
                isDisabledResp={!data.canEdit}
                responsibilities={responsibilities}
                onChangeChannel={this.onChangeChannel}
                onChangeResponsibility={this.onChangeResponsibility}
                onChangeTimeRange={this.onChangeTimeRange}
                onChangeThreshold={this.onChangeThreshold}
                missedMedicationReminderId={missedMedicationReminderId}
                thresholdToPass={thresholdToPass}
                shouldHidePmmaEvent={shouldHidePmmaEvent}
              />
            );
          })}
        </div>
      </>
    );
  }
}

export default compose(connect(null, mapDispatchToProps), widthDirectoryData)(NotificationPreferencesSection);
