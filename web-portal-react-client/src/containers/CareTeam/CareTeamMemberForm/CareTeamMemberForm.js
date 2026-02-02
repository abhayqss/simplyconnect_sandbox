import React, { PureComponent } from "react";

import PTypes from "prop-types";

import { debounce, isBoolean, isNumber, noop } from "underscore";

import memo from "memoize-one";

import cn from "classnames";

import $ from "jquery";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { Button, Col, Form, Row } from "reactstrap";

import "./CareTeamMemberForm.scss";

import { Action, Loader, Scrollable } from "components";

import { CheckboxField, SelectField, TextField } from "components/Form";

import widthDirectoryData from "hocs/withDirectoryData";

import LoadCTMemberRoles from "actions/directory/LoadCTMemberRoles";
import LoadCTMemberEmployees from "actions/directory/LoadCTMemberEmployees";
import LoadCTMemberChannels from "actions/directory/LoadCTMemberChannels";
import LoadContactOrganizations from "actions/careTeam/LoadContactOrganizations";
import LoadCTMemberGroupedEvents from "actions/directory/LoadCTMemberGroupedEvents";
import LoadCTMemberResponsibilities from "actions/directory/LoadCTMemberResponsibilities";

import * as contactDetailsActions from "redux/contact/details/contactDetailsActions";
import * as careTeamMemberFormActions from "redux/care/team/member/form/careTeamMemberFormActions";
import organizationListActions from "redux/care/team/member/organization/list/careTeamMemberContactOrganizationListActions";

import * as careTeamMemberRolesActions from "redux/directory/care/team/role/list/careTeamRoleListActions";
import * as careTeamEmployeeListActions from "redux/directory/care/team/employee/list/careTeamEmployeeListActions";
import * as notificationPreferencesActions from "redux/directory/care/team/notification/preference/list/careTeamNotificationPreferenceListActions";

import { NotificationPreference, NotificationPreferencesSection } from "./";

import { NOTIFICATION_CHANNELS_TYPES, NOTIFICATION_RESPONSIBILITY_TYPES, SYSTEM_ROLES } from "lib/Constants";

import { first, isInteger } from "lib/utils/Utils";
import { Response } from "lib/utils/AjaxUtils";
import { ConversationService } from "factories";
import service from "services/CareTeamMemberService";
import { useAuthUser } from "../../../hooks/common";
import videoChatActions from "../../../redux/video-chat/videoChatActions";
import { WarningDialog } from "../../../components/dialogs";

const conversationService = ConversationService();

const { PARENT_GUARDIAN, PERSON_RECEIVING_SERVICES } = SYSTEM_ROLES;

function areNotificationsDisabled(responsibility) {
  return [VIEWABLE, NOT_VIEWABLE].includes(responsibility);
}

function mapStateToProps(state) {
  const { form, details } = state.care.team.member;

  return {
    fields: form.fields,
    errors: form.validation.errors,
    isFetching: details.isFetching,
    isValid: form.isValid(),
    isChanged: form.isChanged(),

    directory: state.directory,
    organizations: state.care.team.member.organization.list.dataSource.data,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      ...bindActionCreators(careTeamMemberFormActions, dispatch),
      employee: { details: bindActionCreators(contactDetailsActions, dispatch) },
      organizations: bindActionCreators(organizationListActions, dispatch),
      notificationsPreferences: bindActionCreators(notificationPreferencesActions, dispatch),
      employees: bindActionCreators(careTeamEmployeeListActions, dispatch),
      roles: bindActionCreators(careTeamMemberRolesActions, dispatch),
      chatGroup: bindActionCreators(videoChatActions, dispatch),
    },
  };
}

const { EMAIL, PUSH_NOTIFICATION } = NOTIFICATION_CHANNELS_TYPES;

const { VIEWABLE, NOT_VIEWABLE } = NOTIFICATION_RESPONSIBILITY_TYPES;

// const { addParticipants } = useConversations();

/**
 * @deprecated Should be rewritten using React.FC + react-query
 */
class CareTeamMemberForm extends PureComponent {
  static propTypes = {
    isLoading: PTypes.bool,
    memberId: PTypes.number,
    clientId: PTypes.number,
    client: PTypes.any,
    prospectId: PTypes.number,
    clientCommunityId: PTypes.number,
    communityId: PTypes.number,
    organizationId: PTypes.number.isRequired,
    twilioConversationSid: PTypes.string,
    nonclinicalConversationSid: PTypes.string,
    careManagerId: PTypes.number,
    employeeSelectOptionsNoClinicalTeam: PTypes.array,
    employeeSelectOptionsNoClinicalTeamRole: PTypes.array,
    haveRoleForRemind: PTypes.bool,
    changeEditClientModel: PTypes.func,
  };

  scrollableRef = React.createRef();

  state = {
    minHeight: null,
    channels: [],
    responsibility: null,
    isWarningDialogOpen: false,
    errorTitle: "",
    employeeSelectOptionsNoClinicalTeam: [],
    employeeSelectOptionsNoClinicalTeamRole: [],
    missedMedicationReminderId: "",
    pmmaEventId: "",
  };

  constructor(props) {
    super(props);

    this.getRoleSelectOptions = memo(this.getRoleSelectOptions);
    this.getEmployeeSelectOptions = memo(this.getEmployeeSelectOptions);
    this.getOrganizationsSelectOptions = memo(this.getOrganizationsSelectOptions);
    this.getResponsibilitySelectOptions = memo(this.getResponsibilitySelectOptions);
    this.getNotificationTypeSelectOptions = memo(this.getNotificationTypeSelectOptions);

    this.debounceValidate = debounce(this.validate, 300);
  }

  get roleSelectOptions() {
    const { roles } = this.getDirectoryData();

    return this.getRoleSelectOptions(roles.length);
  }

  get actions() {
    return this.props.actions;
  }

  get isValid() {
    return this.props.isValid;
  }

  componentWillUnmount() {
    this.actions.clear();
    this.actions.roles.clear();
    this.actions.employees.clear();
    this.actions.organizations.clear();
  }

  onAddMinHeight = () => {
    this.setState({ minHeight: 700 });
  };

  onRemoveMinHeight = () => {
    this.setState({ minHeight: null });
  };

  getRole = (value) => {
    if (value) {
      service
        .getNonClinicalTeamRole(value, true)
        .then((res) => {
          const result = res.data.map(({ id, title }) => ({
            value: id,
            text: title,
          }));

          this.setState({
            employeeSelectOptionsNoClinicalTeamRole: result,
          });
        })
        .catch(() => {
          this.setState({
            employeeSelectOptionsNoClinicalTeamRole: [],
          });
        });
    }
  };

  onChangeField = (name, value, shouldUpdateHashCode) => {
    shouldUpdateHashCode = isBoolean(shouldUpdateHashCode) ? shouldUpdateHashCode : false;

    if (this.props.activeTopTabNum === 1 && value) {
      this.getRole(value);
    }

    this.changeField(name, value, shouldUpdateHashCode);

    if (!this.isValid) this.debounceValidate();
  };
  // change role search notificationPreferences
  onChangeRoleField = (name, value) => {
    const roleName = this.roleSelectOptions.find((o) => o.value === value)?.text;
    this.setState({
      channels: [],
      responsibility: null,
    });

    this.changeField(name, value);
    this.changeField("roleName", roleName);
    this.changeNotificationPreferences(value);
  };

  changeNotificationPreferences = async (roleId) => {
    let data = [];

    if (isNumber(roleId)) {
      data = (await this.fetchNotificationPreferences(roleId)).data;
    }

    this.actions.changeNotificationPreferences(data);
  };

  onChangeAllNotificationResponsibilities = (id, value) => {
    this.setState({ responsibility: value });

    this.actions.changeAllNotificationResponsibilities(value);

    this.updateAllNotificationChannels(value);

    if (!this.isValid) this.debounceValidate();
  };

  onChangeAllNotificationChannels = (id, channels) => {
    this.setState({ channels });

    this.actions.changeAllNotificationChannels(channels, this.state.missedMedicationReminderId);

    if (!this.isValid) this.debounceValidate();
  };

  onChangeNotificationResponsibility = () => {
    this.setState({ responsibility: null });
    if (!this.isValid) this.debounceValidate();
  };

  onChangeNotificationChannels = () => {
    this.setState({ channels: [] });
    if (!this.isValid) this.debounceValidate();
  };

  getDirectoryData() {
    const path = ["care", "team"];

    return this.props.getDirectoryData({
      roles: [...path, "role"],
      channels: [...path, "channel"],
      employees: [...path, "employee"],
      responsibilities: [...path, "responsibility"],
      groupedEventTypes: [...path, "groupedEvent", "type"],
      notificationPreferences: [...path, "notification", "preference"],
    });
  }

  isEditMode() {
    return isNumber(this.props.memberId);
  }

  getEmployeeSelectOptions() {
    if (this.props.activeTopTabNum === 0) {
      const { employees } = this.getDirectoryData();
      return employees.map(({ id, name }) => ({
        value: id,
        text: name,
      }));
    } else {
      service
        .getVendorContacts(this.props.clientId, this.props.clientOrganizationId, this.props.clientCommunityId)
        .then((res) => {
          const result = res.data.map(({ id, name }) => ({
            value: id,
            text: name,
          }));

          this.setState({ employeeSelectOptionsNoClinicalTeam: result });
        })
        .catch(() => {
          this.setState({ employeeSelectOptionsNoClinicalTeam: [] });
        });
    }
  }

  getRoleSelectOptions() {
    const { roles } = this.getDirectoryData();

    return roles.map(({ id, title }) => ({
      value: id,
      text: title,
    }));
  }

  getNotificationTypeSelectOptions() {
    const { channels } = this.getDirectoryData();

    return channels.map(({ name, title }) => ({
      value: name,
      text: title,
    }));
  }

  getOrganizationsSelectOptions() {
    const { organizations } = this.props;

    return organizations.map(({ id, name }) => ({
      value: id,
      text: name,
    }));
  }

  getResponsibilitySelectOptions() {
    const { responsibilities } = this.getDirectoryData();

    return responsibilities.map((o) => ({
      ...o,
      isDisabled: !o.assignable,
      value: o.name,
      text: o.title,
    }));
  }

  async changeField(...args) {
    await this.actions.changeField(...args);

    if (!this.isValid) this.debounceValidate();
  }

  validate = () => {
    return this.actions.validate(this.props.fields.toJS());
  };

  updateAllNotificationChannels(responsibility) {
    const areEnabled = !areNotificationsDisabled(responsibility);

    if (areEnabled) {
      const prev = this.state.responsibility;

      if (areNotificationsDisabled(prev)) {
        this.setState({ channels: [] });
      }
    }

    this.actions.changeAllNotificationChannels(areEnabled ? [EMAIL, PUSH_NOTIFICATION] : [], {
      excludeDisabled: true,
      excludeEditableAndWithChannels: areEnabled,
    });
  }

  fetchNotificationPreferences(careTeamRoleId) {
    return this.actions.notificationsPreferences.load({ careTeamRoleId });
  }

  fetchEmployeeById(employeeId) {
    return this.actions.employee.details.load(employeeId, true);
  }

  addMissingNotificationPreferences = () => {
    const { notificationPreferences: directoryDataPreferences } = this.getDirectoryData();
    const { notificationsPreferences } = this.props.fields;

    const eventTypeIds = notificationsPreferences.map((o) => o.eventTypeId);
    const missingNotificationPreferences = directoryDataPreferences.filter(
      (o) => !eventTypeIds.includes(o.eventTypeId),
    );

    this.actions.changeFields({
      notificationsPreferences: [...missingNotificationPreferences, ...notificationsPreferences.toJS()],
    });
  };

  isAllEventsDisabled() {
    const { fields } = this.props;
    const isHCARoleSelected = fields.roleName === "HCA";

    return (
      isHCARoleSelected &&
      fields.notificationsPreferences.every((o) => {
        return o.responsibilityName === "N";
      })
    );
  }

  onClose = () => {
    this.props.onClose(this.props.isChanged);
  };

  onSave = async (e) => {
    e.preventDefault();

    const isValid = await this.validate();

    if (isValid) {
      await this.save();
    } else {
      this.scrollToError();
    }
  };

  async save() {
    const user = useAuthUser();

    if (!this.props.isChanged) {
      return this.props.onClose();
    }

    const {
      fields,
      clientId,
      prospectId,
      communityId,
      organizationId,
      clientTwilioConversationSid,
      hasGroup,
      clientFullName,
      memberListId,
      careManagerId,
    } = this.props;

    const data = {
      ...fields.toJS(),
      organizationId,
      clientId,
      prospectId,
      communityId,
      careteamManagerId: careManagerId,
      clinical: this.props.activeTopTabNum === 0,
    };

    const { success } = await this.actions.submit(data);
    if (success) {
      this.props.onSaveSuccess();
    }
  }

  scroll(...args) {
    this.scrollableRef.current?.scroll(...args) || noop();
  }

  scrollToError() {
    this.scroll(first($(".CareTeamMemberForm .form-control.is-invalid")));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.fields !== this.props.fields && this.props.activeTopTabNum === 1) {
      this.getRole(this.props.fields.employeeId);
    }

    const { groupedEventTypes } = this.getDirectoryData();
    let mmaEventId = null;
    let pmmaEventId = null;

    // 遍历 groupedEventTypes，查找 MMA 和 PMMA 的事件 ID
    for (const item of groupedEventTypes) {
      const mmaEvent = item.eventTypes.find((event) => event.name === "MMA");
      const pmmaEvent = item.eventTypes.find((event) => event.name === "PMMA");

      if (mmaEvent) {
        mmaEventId = mmaEvent.id;
      }
      if (pmmaEvent) {
        pmmaEventId = pmmaEvent.id;
      }

      // 如果已经找到 MMA 和 PMMA 的 ID，提前退出循环
      if (mmaEventId && pmmaEventId) {
        break;
      }
    }

    // 仅当新的 MMA ID 与当前状态中的不同时才更新状态
    if (mmaEventId !== this.state.missedMedicationReminderId) {
      this.setState({
        missedMedicationReminderId: mmaEventId,
      });
    }

    // 仅当新的 PMMA ID 与当前状态中的不同时才更新状态
    if (pmmaEventId !== this.state.pmmaEventId) {
      this.setState({
        pmmaEventId: pmmaEventId,
      });
    }
  }

  render() {
    const {
      fields,
      errors,
      isFetching,

      hasGroup,
      clientId,
      prospectId,
      affiliation,
      communityId,
      organizations,
      haveRoleForRemind,
      changeEditClientModel,
      isRefresh,
      changeIsRefresh,
      waringDialog,
      changeWaringDialog,
    } = this.props;

    const { responsibility, isWarningDialogOpen, errorTitle, missedMedicationReminderId, pmmaEventId } = this.state;
    const { channels, employees, responsibilities, groupedEventTypes } = this.getDirectoryData();
    const hasNotificationPreferences =
      fields.id != null || (fields.roleId != null && fields.notificationsPreferences.size !== 0);

    const employeeSelectOptions = this.getEmployeeSelectOptions(employees.length); //处理Contact Name

    const responsibilitySelectOptions = this.getResponsibilitySelectOptions(responsibilities.length); // 根据接口数据 处理权限 Responsibility

    const notificationTypeSelectOptions = this.getNotificationTypeSelectOptions(channels.length); //channels select

    const organizationsSelectOptions = this.getOrganizationsSelectOptions(organizations.length);

    return (
      <>
        <Form className="CareTeamMemberForm" onSubmit={this.onSave}>
          <LoadCTMemberRoles
            isMultiple
            params={{
              clientId,
              prospectId,
              contactId: fields.employeeId,
            }}
            shouldPerform={(prev) => Boolean(fields.employeeId) && prev.contactId !== fields.employeeId}
          />
          <LoadContactOrganizations
            params={{
              clientId,
              prospectId,
              communityId,
              affiliation,
            }}
            shouldPerform={() => !this.isEditMode()}
            onPerformed={({ data } = {}) => {
              if (data?.length === 1) {
                this.onChangeField("employeeOrganizationId", first(data).id, true);
              }
            }}
          />
          <LoadCTMemberEmployees
            isMultiple
            params={{
              clientId,
              prospectId,
              communityId: this.props.clientCommunityId,
              organizationId: fields.employeeOrganizationId,
            }}
            shouldPerform={(prev) =>
              isNumber(fields.employeeOrganizationId) && prev.organizationId !== fields.employeeOrganizationId
            }
          />
          <LoadCTMemberChannels
            params={{
              isProspect: isInteger(prospectId),
            }}
          />
          <LoadCTMemberGroupedEvents
            params={{
              isProspect: isInteger(prospectId),
            }}
          />
          <LoadCTMemberResponsibilities />
          <Action
            shouldPerform={() => this.isEditMode() && isNumber(fields.roleId)}
            action={() => {
              this.fetchNotificationPreferences(fields.roleId).then(this.addMissingNotificationPreferences);
            }}
          />
          <Action
            isMultiple
            params={{ organizationId: fields.employeeOrganizationId }}
            shouldPerform={(prev) =>
              isNumber(fields.employeeOrganizationId) && prev.organizationId !== fields.employeeOrganizationId
            }
            action={() => {
              this.onChangeField("employeeId", null, true);
            }}
          />
          <Action
            isMultiple
            params={{ roleLength: this.roleSelectOptions.length }}
            shouldPerform={(prev) => {
              return (
                !this.isEditMode() &&
                prev.roleLength !== this.roleSelectOptions.length &&
                this.roleSelectOptions.length === 1
              );
            }}
            action={() => {
              this.onChangeRoleField("roleId", first(this.roleSelectOptions)?.value);
            }}
          />
          <Action
            isMultiple
            params={{ employeeId: fields.employeeId }}
            shouldPerform={(prev) => prev.employeeId !== null && fields.employeeId === null}
            action={() => {
              this.onChangeRoleField("roleId", null);
            }}
          />
          <Action
            isMultiple
            params={{ employeeId: fields.employeeId }}
            shouldPerform={(prev) =>
              !this.isEditMode() && fields.employeeId !== null && fields.employeeId !== prev.employeeId
            }
            action={() => {
              this.onChangeRoleField("roleId", null);
              this.fetchEmployeeById(fields.employeeId).then(
                Response(({ data }) => {
                  if ([PARENT_GUARDIAN, PERSON_RECEIVING_SERVICES].includes(data.systemRoleName)) {
                    this.onChangeRoleField("roleId", data.systemRoleId);
                  }
                }),
              );
            }}
          />

          <Scrollable ref={this.scrollableRef}>
            <div className="CareTeamMemberForm-Section">
              <div className="CareTeamMemberForm-SectionTitle">General</div>
              <Row>
                <Col md={8}>
                  {this.isEditMode() ? (
                    <TextField
                      isDisabled
                      value={fields.employeeOrganizationName}
                      label="Organization*"
                      className="CareTeamMemberForm-TextField"
                    />
                  ) : (
                    <SelectField
                      name="employeeOrganizationId"
                      value={fields.employeeOrganizationId}
                      options={organizationsSelectOptions}
                      label="Organization*"
                      className="CareTeamMemberForm-SelectField"
                      placeholder="Select Organization"
                      isDisabled={organizationsSelectOptions.length <= 1}
                      errorText={errors.employeeOrganizationId}
                      onChange={this.onChangeField}
                      onExpand={this.onAddMinHeight}
                      onCollapse={this.onRemoveMinHeight}
                    />
                  )}
                </Col>
              </Row>
              <Row>
                <Col md={4}>
                  {this.isEditMode() ? (
                    <TextField
                      isDisabled
                      value={fields.employeeName}
                      label="Contact Name*"
                      className="CareTeamMemberForm-TextField"
                      errorText={errors.description}
                    />
                  ) : (
                    <SelectField
                      hasSearchBox
                      name="employeeId"
                      value={fields.employeeId}
                      options={
                        this.props.activeTopTabNum === 0
                          ? employeeSelectOptions
                          : this.state.employeeSelectOptionsNoClinicalTeam
                      }
                      label="Contact Name*"
                      className="CareTeamMemberForm-SelectField"
                      placeholder="Select Contact"
                      isDisabled={!isNumber(fields.employeeOrganizationId)}
                      hasError={!!errors.employeeId}
                      errorText={errors.employeeId}
                      onChange={this.onChangeField}
                      onExpand={this.onAddMinHeight}
                      onCollapse={this.onRemoveMinHeight}
                    />
                  )}
                </Col>

                <Col md={4}>
                  {fields.canChangeRole ? (
                    <SelectField
                      name="roleId"
                      value={fields.roleId}
                      options={
                        this.props.activeTopTabNum === 0
                          ? this.roleSelectOptions
                          : this.state.employeeSelectOptionsNoClinicalTeamRole
                      }
                      label="Role*"
                      placeholder="Select"
                      className={cn("CareTeamMemberForm-SelectField", {
                        RestrictedHeight: !isNumber(fields.roleId),
                      })}
                      isDisabled={
                        this.props.activeTopTabNum === 0
                          ? this.roleSelectOptions.length <= 1 || !fields.employeeId
                          : this.state.employeeSelectOptionsNoClinicalTeamRole.length < 1
                      }
                      errorText={errors.roleId}
                      onChange={this.onChangeRoleField}
                      onExpand={this.onAddMinHeight}
                      onCollapse={this.onRemoveMinHeight}
                    />
                  ) : (
                    <TextField
                      isDisabled
                      value={fields.roleName}
                      label="Role*"
                      className="CareTeamMemberForm-TextField"
                    />
                  )}
                </Col>

                <Col md={4}>
                  <TextField
                    maxDigits={256}
                    name="description"
                    value={fields.description}
                    label="Description"
                    className="CareTeamMemberForm-TextField"
                    hasError={!!errors.description}
                    errorText={errors.description}
                    onChange={this.onChangeField}
                  />
                </Col>
              </Row>
              {clientId != null && (
                <Row>
                  <Col md={12}>
                    <CheckboxField
                      name="includeInFaceSheet"
                      value={fields.includeInFaceSheet}
                      hasError={fields.includeInFaceSheetHasError}
                      errorText={fields.includeInFaceSheetErrorText}
                      label="Include contact in the facesheet document"
                      onChange={this.onChangeField}
                    />
                  </Col>
                </Row>
              )}
            </div>

            {isFetching && <Loader hasBackdrop />}

            {hasNotificationPreferences && (
              <>
                <div className="CareTeamMemberForm-Section CareTeamMemberForm-Section__AllEvents">
                  <div className="CareTeamMemberForm-SectionTitle">Notification Preferences</div>

                  <NotificationPreference
                    showLabel
                    title="All Events"
                    data={{
                      id: "all",
                      eventTypeId: "all",
                      responsibilityName: responsibility,
                      channels: this.state.channels,
                    }}
                    placeholder="Select"
                    isDisabledResp={this.isAllEventsDisabled()}
                    isDisabledChannel={this.isAllEventsDisabled()}
                    channels={notificationTypeSelectOptions}
                    responsibilities={responsibilitySelectOptions}
                    onChangeResponsibility={this.onChangeAllNotificationResponsibilities}
                    onChangeChannel={this.onChangeAllNotificationChannels}
                  />
                </div>
                {/*// 除了notification 之外的全部的类型*/}
                {groupedEventTypes.map((group) => {
                  return (
                    <NotificationPreferencesSection
                      key={group.title}
                      title={group.title}
                      eventGroup={group.eventTypes}
                      errors={errors.notificationsPreferences}
                      channels={notificationTypeSelectOptions}
                      responsibilities={responsibilitySelectOptions}
                      preferences={fields.notificationsPreferences}
                      onChangeResponsibility={this.onChangeNotificationResponsibility}
                      onChangeChannels={this.onChangeNotificationChannels}
                      missedMedicationReminderId={missedMedicationReminderId}
                      clientId={clientId}
                      haveRoleForRemind={haveRoleForRemind}
                      changeEditClientModel={changeEditClientModel}
                      isRefresh={isRefresh}
                      changeIsRefresh={changeIsRefresh}
                      changeWaringDialog={changeWaringDialog}
                      waringDialog={waringDialog}
                      clientFullName={this.props.clientFullName}
                      pmmaEventId={pmmaEventId}
                      currentRoleName={fields.roleName}
                    />
                  );
                })}
              </>
            )}
          </Scrollable>

          <div className="CareTeamMemberForm-Buttons">
            <Button outline color="success" onClick={this.onClose}>
              Cancel
            </Button>

            <Button color="success" disabled={isFetching} onClick={this.onSave}>
              Save
            </Button>
          </div>

          {isWarningDialogOpen && (
            <WarningDialog
              isOpen
              title={errorTitle}
              buttons={[
                {
                  text: "OK",
                  onClick: () => this.setState({ isWarningDialogOpen: false, errorTitle: "" }),
                },
              ]}
            />
          )}
        </Form>
      </>
    );
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(widthDirectoryData(CareTeamMemberForm));
