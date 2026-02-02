import React, { PureComponent } from "react";

import cn from "classnames";
import memo from "memoize-one";
import PropTypes from "prop-types";

import { find, findWhere, first, groupBy, isEqual, isNumber, map, noop, size } from "underscore";

import { debounce } from "lodash";

import $ from "jquery";

import { Link } from "react-router-dom";

import { connect } from "react-redux";
import { bindActionCreators, compose } from "redux";

import { Button, Col, Form, Row, UncontrolledTooltip as Tooltip } from "reactstrap";

import withSelectOptions from "hocs/withSelectOptions";

import { Action, Loader, Scrollable } from "components";

import { CheckboxField, FileField, PhoneField, SelectField, TextField } from "components/Form";

import { WarningDialog } from "components/dialogs";

import { LoadCommunityAction } from "actions/admin";

import { LoadContactQAUnavailableRolesAction } from "actions/contact";

import {
  ClearUnassociatedClientsAction,
  LoadClientDetailsAction,
  LoadUnassociatedClientsAction,
} from "actions/clients";

import * as contactFormActions from "redux/contact/form/contactFormActions";
import * as contactDetailsActions from "redux/contact/details/contactDetailsActions";
import * as communityListActions from "redux/contact/community/list/communityListActions";
import organizationListActions from "redux/contact/organization/list/organizationListActions";
import * as communityDetailsActions from "redux/community/details/communityDetailsActions";

import stateListActions from "redux/directory/state/list/stateListActions";
import clientListActions from "redux/directory/client/list/clientListActions";
import systemRoleListActions from "redux/directory/contact/system/role/list/systemRoleListActions";

import { ReactComponent as Info } from "images/info.svg";

import { CONTACT_STATUSES, PROFESSIONAL_SYSTEM_ROLES, SYSTEM_ROLES } from "lib/Constants";

import { isEmpty, isInteger, isNotEmpty, omitDeep } from "lib/utils/Utils";

import { isEmptyOrBlank } from "lib/utils/ObjectUtils";

import { Response } from "lib/utils/AjaxUtils";

import { domain } from "lib/utils/DataMappingUtils";

import { path } from "lib/utils/ContextUtils";

import "./ContactForm.scss";
import { useSystemRolesQuery } from "../../../../hooks/business/directory/query";

const { PENDING } = CONTACT_STATUSES;

let changeLog = [];

const NO_COMMUNITY_ERROR_TEXT = "There is no community created for current organization.";

const { PERSON_RECEIVING_SERVICES } = SYSTEM_ROLES;

const PRIMARY_CONTACT_ROLES = [SYSTEM_ROLES.PARENT_GUARDIAN, SYSTEM_ROLES.PERSON_RECEIVING_SERVICES];
const COMMUNITY_MANAGEMENT_ROLES = [
  SYSTEM_ROLES.BEHAVIORAL_HEALTH,
  SYSTEM_ROLES.ORGANIZATION_ADMIN,
  SYSTEM_ROLES.VENDOR,
  SYSTEM_ROLES.DOCTOR,
  SYSTEM_ROLES.CLINICIAN,
  SYSTEM_ROLES.PHARMACIST_VENDOR,
  SYSTEM_ROLES.NAVI_GUIDE,
  SYSTEM_ROLES.QUALITY_ASSURANCE,
  SYSTEM_ROLES.CASE_MANAGER,
];

const phoneTooltipHint = {
  target: "phone-hint",
  render: () => (
    <ul className="ContactForm-TooltipBody">
      <li>Digits only allowed</li>
      <li>No spaces, dashes, or special symbols</li>
      <li>Country code is required</li>
      <li>‘+’ may be a leading symbol</li>
    </ul>
  ),
};

const mobileTooltipHint = {
  ...phoneTooltipHint,
  placement: "top",
  target: "mobile-phone-hint",
};

function mapStateToProps(state) {
  const { list, form, role, details, community, organization } = state.contact;

  return {
    error: form.error,
    fields: form.fields,
    isValid: form.isValid,
    isFetching: form.isFetching,
    isValidLoginField: form.isValidLoginField,

    list,
    role,
    details,
    community: {
      list: community.list,
      details: state.community.details,
    },
    organization,
    auth: state.auth,
    client: state.client,

    directory: state.directory,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      ...bindActionCreators(contactFormActions, dispatch),

      details: {
        ...bindActionCreators(contactDetailsActions, dispatch),
      },

      community: {
        list: bindActionCreators(communityListActions, dispatch),
        details: bindActionCreators(communityDetailsActions, dispatch),
      },

      organization: {
        list: bindActionCreators(organizationListActions, dispatch),
      },

      directory: {
        state: { list: bindActionCreators(stateListActions, dispatch) },
        client: { list: bindActionCreators(clientListActions, dispatch) },
        system: {
          role: { list: bindActionCreators(systemRoleListActions, dispatch) },
        },
      },
    },
  };
}

class ContactForm extends PureComponent {
  static propTypes = {
    clientId: PropTypes.number,
    contactId: PropTypes.number,
    organizationId: PropTypes.number,
    isPendingContact: PropTypes.bool,
    isExpiredContact: PropTypes.bool,
  };

  scrollableRef = React.createRef();

  state = {
    defaultCommunity: null,
    isStateHintOpen: false,
    shouldScrollToError: false,
    isMarkedForDeletionDialogOpen: false,
    isPrimaryContactWarningDialogOpen: false,
    isAssociatedClientsLimitDialogOpen: false,
    isCommunityManagementRole: false,
  };

  constructor(props) {
    super(props);

    this.getStatesSelectOptions = this.props.MemoizedSelectOptions(["state"]);
    this.getContactSystemRoleSelectOptions = this.props.MemoizedSelectOptions(["contact", "system", "role"], {
      text: "title",
      value: "id",
      disableCondition: [
        "ROLE_BEHAVIORAL_HEALTH",
        "ROLE_COMMUNITY_MEMBERS",
        "ROLE_HCA",
        "ROLE_PRIMARY_PHYSICIAN",
        "ROLE_SERVICE_PROVIDER",
        "ROLE_ADMINISTRATOR",
        "ROLE_CONTENT_CREATOR",
        "ROLE_TELE_HEALTH_NURSE",
        "ROLE_VENDOR_CODE",
        "ROLE_QUALITY_ASSURANCE_CODE",
        "ROLE_DOCTOR_CODE",
      ],
    });

    this.getClientSelectOptions = this.props.MemoizedSelectOptions(["client"], { value: "id", text: "fullName" });

    this.getMemoizedCommunities = memo(this.getMemoizedCommunities, isEqual);
    this.getMemoizedOrganizations = memo(this.getMemoizedOrganizations, isEqual);
    this.validateUniqDebounced = debounce(this.validateUniq, 300);
  }

  get actions() {
    return this.props.actions;
  }

  get shouldNotifyIfMarkedForDeletion() {
    const { fields } = this.props;

    return fields.markedForDeletionAt && fields.enableContact;
  }

  get shouldRemovePrimaryContacts() {
    const { fields, details, directory } = this.props;

    const systemRoleList = directory.contact.system.role.list.dataSource.data;

    const role = findWhere(systemRoleList, { id: fields.systemRoleId });

    return (
      this.isEditing() &&
      (size(details?.data?.activePrimaryContactClientNames) > 0 ||
        details?.data?.inactivePrimaryContactClientsCount > 0) &&
      !find(PRIMARY_CONTACT_ROLES, (r) => r === role?.name)
    );
  }

  componentDidMount() {
    this.loadDirectoryData();

    const { auth, client, clientId, contactId } = this.props;

    this.loadOrganizations();

    if (this.isEditing()) {
      this.loadDetails(contactId).then(
        Response(({ data }) => {
          const { communityId } = data;

          this.changeFields({
            ...data,
            address: data.address || {},
            associatedClientIds: map(data.associatedClients, (o) => o.id),
            professionals: PROFESSIONAL_SYSTEM_ROLES.includes(data.systemRoleName),
          });
          this.setState({
            isCommunityManagementRole: COMMUNITY_MANAGEMENT_ROLES.includes(data.systemRoleName),
          });

          this.loadCommunities(data.organizationId).then(
            Response(({ data }) => {
              !isNumber(communityId) && this.setCommunityIfSingle(data);
            }),
          );
        }),
      );
    } else if (isInteger(clientId)) {
      let { data } = client.details;

      if (data) {
        const defaultData = domain.mapClientToContact(data);

        const { communityId } = defaultData;

        this.changeFields({
          ...defaultData,
          isCommunityAddressUsed: isEmptyOrBlank(defaultData.address),
        });

        this.loadCommunities(defaultData.organizationId).then(
          Response(({ data }) => {
            !isNumber(communityId) && this.setCommunityIfSingle(data);
          }),
        );
      }
    } else {
      const organizationId = this.props.organizationId || auth.login.user.data.organizationId;

      this.changeFields({
        organizationId,
        enableContact: true,
      });

      this.loadCommunities(organizationId).then(
        Response(({ data }) => {
          this.setCommunityIfSingle(data);

          if (data?.length === 1) {
            this.setState({ defaultCommunity: data[0] });
          }
        }),
      );
    }
  }

  componentDidUpdate() {
    let { shouldScrollToError } = this.state;
    let { isValid, isValidLoginField } = this.props;

    let isInvalid = !isValid || !isValidLoginField;

    if (shouldScrollToError && isInvalid) {
      this.scrollToError();
      this.setState({ shouldScrollToError: false });
    }
  }

  componentWillUnmount() {
    changeLog = [];

    this.clear();
    this.actions.community.details.clear();
  }

  onToggleStateHint = () => {
    this.setState((s) => ({
      isStateHintOpen: !s.isStateHintOpen,
    }));
  };

  onChangeField = (name, value) => {
    this.changeField(name, value).then(() => {
      this.onFieldChanged(name, value);
      if (!this.props.isValid) this.validate();
    });
  };

  onChangeFields = (name, value) => {
    this.changeField(name, value).then(() => {
      this.onFieldChanged(name, value);
      if (!this.props.isValid) this.validate();
    });
  };

  onChangeAssociatedClientsField = (name, value, onCancel) => {
    if (value.length > 100) {
      onCancel();
      this.setState({ isAssociatedClientsLimitDialogOpen: true });
    } else this.onChangeField(name, value);
  };

  onChangeFieldWithLoginCheck = (name, value) => {
    this.changeField(name, value).then(() => {
      this.onFieldChanged(name, value);

      if (!this.props.isValid) this.validate();

      this.validateUniqDebounced();
    });
  };

  onFieldChanged = (name, value) => {
    changeLog.push({ name, value });

    if (name === "organizationId") {
      this.changeFields({
        communityId: null,
        communityIdHasError: false,
        communityIdErrorText: "",
        otherCommunityIds: null,
        otherCommunityIdsHasError: false,
        otherCommunityIdsErrorText: "",
      });

      this.loadCommunities(value).then(
        Response(({ data }) => {
          if (isEmpty(data)) {
            this.changeFields({
              communityIdHasError: true,
              communityIdErrorText: NO_COMMUNITY_ERROR_TEXT,
            });
          }

          this.setCommunityIfSingle(data);
        }),
      );
    }

    if (name === "avatar" && !value) {
      this.changeField("avatarName", "");
    }
  };

  onChangeAddressField = (name, value) => {
    this.changeFields({ address: { [name]: value } }).then(() => {
      changeLog.push({ name, value });

      if (!this.props.isValid) this.validate();
    });
  };

  onChangeSystemRoleField = (name, value) => {
    const role = findWhere(this.props.directory.contact.system.role.list.dataSource.data, { id: value });
    this.setState({
      isCommunityManagementRole: COMMUNITY_MANAGEMENT_ROLES.includes(role?.name),
    });

    this.changeFields({
      [name]: value,
      professionals: PROFESSIONAL_SYSTEM_ROLES.includes(role?.name),
    }).then(() => {
      changeLog.push({ name, value });

      if (!this.props.isValid) this.validate();
    });
  };

  clear() {
    this.props.actions.clear();
  }

  setCommunityIfSingle(communities) {
    if (communities.length === 1) {
      this.changeField("communityId", communities[0].id);
      this.changeField("otherCommunityIds", [communities[0].id]);
    }
  }

  validate() {
    const { fields, actions } = this.props;

    const options = {
      included: {
        isEditMode: this.isEditing(),
        isCommunityAddressUsed: fields.isCommunityAddressUsed,
        isRoleIsBehavioralHealth: fields.systemRoleId === 6,
      },
    };

    return actions.validate(fields.toJS(), options);
  }

  validateUniq = () => {
    if (this.isEditing()) {
      return Promise.resolve(true);
    }

    const { login, organizationId } = this.props.fields;

    return this.props.actions.validateUniq({ organizationId, login });
  };

  isEditing() {
    return isInteger(this.props.contactId);
  }

  loadDirectoryData() {
    const { actions, canEditRole } = this.props;

    const { state, system } = actions.directory;

    state.list.load();
    system.role.list.load({ isEditable: canEditRole });
  }

  loadDetails(contactId, shouldNotSave) {
    return this.props.actions.details.load(contactId, shouldNotSave);
  }

  loadOrganizations() {
    return this.props.actions.organization.list.load({ excludeAffiliated: true });
  }

  loadCommunities(organizationId) {
    return this.props.actions.community.list.load({ organizationId });
  }

  changeFields(changes) {
    return this.props.actions.changeFields(changes);
  }

  changeField(name, value) {
    return this.props.actions.changeField(name, value);
  }

  getMemoizedCommunities(communities, { communityName, communityId }) {
    communities = isEmpty(communities)
      ? isInteger(communityId)
        ? [{ id: communityId, name: communityName }]
        : []
      : communities;

    return communities
      .filter((o) => o.canAddContact || o.id === communityId)
      .map(({ id, name }) => ({ text: name, value: id }));
  }

  getMemoizedOrganizations(organizations, { organizationId, organizationName }) {
    organizations = isEmpty(organizations)
      ? isInteger(organizationId) && organizationName
        ? [{ id: organizationId, label: organizationName }]
        : []
      : organizations;

    return organizations.map(({ id, label }) => ({ text: label, value: id }));
  }

  getGroupedClients(clients) {
    return map(groupBy(clients, "communityId"), (group, id) => ({
      id: +id,
      title: first(group).communityName,
      options: map(group, (o) => ({ value: o.id, text: o.fullName })),
    }));
  }

  onClose = () => {
    this.props.onClose(this.isFormChanged());
  };

  isFormChanged() {
    const { auth, details, fields } = this.props;

    const { defaultCommunity } = this.state;

    const organizationId = this.props.organizationId || auth.login.user.data.organizationId;

    const excluded = [
      "id",
      "state",
      "status",
      "avatar",
      "avatarId",
      "stateName",
      "stateAbbr",
      "avatarName",
      "middleName",
      "displayName",
      "professionals",
      "communityName",
      "displayAddress",
      "systemRoleName",
      "systemRoleTitle",
      "organizationName",
      "associatedClients",
      "shouldRemoveAvatar",
      "secureMessagingEnabled",
      "isSecureMessagingEnabled",
    ];

    if (this.isEditing()) {
      if (!details.data?.address) {
        excluded.push("address");
      }

      if (!details.data?.associatedClientIds) {
        excluded.push("associatedClientIds");
      }
    } else if (fields.isCommunityAddressUsed) {
      excluded.push("address");
    }

    const filter = (v, k) =>
      k.includes("HasError") || k.includes("ErrorCode") || k.includes("ErrorText") || excluded.includes(k);

    return !isEqual(
      omitDeep(fields.toJS(), filter),
      omitDeep(
        this.isEditing()
          ? details.data
          : {
              ...fields.clear().toJS(),
              organizationId,
              communityId: defaultCommunity?.id ?? null,
            },
        filter,
      ),
    );
  }

  tryToSubmit = () => {
    if (this.shouldRemovePrimaryContacts) {
      this.setState({ isPrimaryContactWarningDialogOpen: true });
    } else if (this.shouldNotifyIfMarkedForDeletion) {
      this.setState({ isMarkedForDeletionDialogOpen: true });
    } else this.submitIfValid();
  };

  onInvite = () => {
    this.invite().then(
      Response(({ data }) => {
        this.props.onReInviteSuccess(data);
      }),
    );
  };

  invite() {
    let { id } = this.props.fields;

    return this.props.actions.invite(id);
  }

  save() {
    const { actions, fields } = this.props;

    let data = fields.toJS();

    const filter = (v, k) => k.includes("HasError") || k.includes("ErrorCode") || k.includes("ErrorText");

    return actions.submit({
      ...omitDeep(data, filter),
      avatar: fields.avatar,
      shouldRemoveAvatar: this.isEditing() && !(fields.avatar || fields.avatarName),
      shouldRemovePrimaryContacts: this.isEditing() && this.shouldRemovePrimaryContacts,
    });
  }

  submitIfValid = () => {
    const { details } = this.props;
    this.validate()
      .then(async (success) => {
        if (!success) return false;
        return await this.validateUniq();
      })
      .then((success) => {
        if (success) {
          this.save().then(
            Response(({ data }) => {
              if (this.isEditing() && (details.data.statusCode === 2 || details.data.statusCode === 1)) {
                this.onInvite();
              } else {
                this.props.onSubmitSuccess(data, !this.isEditing());
              }
            }),
          );
        } else {
          this.setState({ shouldScrollToError: true });
        }
      });
  };

  scroll(...args) {
    this.scrollableRef.current?.scroll(...args) || noop();
  }

  scrollToError() {
    const $field = $(".ContactForm .form-control.is-invalid").eq(0);
    if ($field) this.scroll($field);
  }

  render() {
    const {
      role,
      client,
      fields,
      details,
      clientId,
      className,
      isFetching,
      canEditRole,
      isPendingContact,
      isExpiredContact,
    } = this.props;

    const {
      isMarkedForDeletionDialogOpen,
      isPrimaryContactWarningDialogOpen,
      isAssociatedClientsLimitDialogOpen,
      isCommunityManagementRole,
    } = this.state;

    const {
      firstName,
      firstNameHasError,
      firstNameErrorText,

      lastName,
      lastNameHasError,
      lastNameErrorText,

      systemRoleId,
      systemRoleIdHasError,
      systemRoleIdErrorText,

      professionals,
      professionalsHasError,
      professionalsErrorText,

      login,
      loginHasError,
      loginErrorText,

      organizationId,
      organizationIdHasError,
      organizationIdErrorText,

      communityId,
      communityIdHasError,
      communityIdErrorText,
      // 1011
      otherCommunityIds,
      otherCommunityIdsHasError,
      otherCommunityIdsErrorText,

      associatedClientIds,

      avatar,
      avatarHasError,
      avatarErrorText,

      avatarName,

      status,

      address,
      isCommunityAddressUsed,

      phone,
      phoneHasError,
      phoneErrorText,

      mobilePhone,
      mobilePhoneHasError,
      mobilePhoneErrorText,

      fax,
      faxHasError,
      faxErrorText,

      secureMail,
      secureMailHasError,
      secureMailErrorText,

      /*enabledSearchCapability,
      enabledSearchCapabilityHasError,
      enabledSearchCapabilityErrorText,*/

      enableContact,
      enableContactHasError,
      enableContactErrorText,

      qaIncidentReports,
      qaIncidentReportsHasError,
      qaIncidentReportsErrorText,
    } = fields;

    const defaultData = domain.mapClientToContact(client.details.data);

    const { communityName, organizationName } = details.data || defaultData || {};

    const states = this.getStatesSelectOptions();

    const organizations = this.props.organization.list.dataSource.data;

    const organizationSelectOptions = this.getMemoizedOrganizations(organizations, {
      organizationId,
      organizationName,
    });

    const communities = this.props.community.list.dataSource.data;

    const community = this.props.community.details.data;

    const communitySelectOptions = this.getMemoizedCommunities(communities, { communityName, communityId });

    const unassociatedClients = this.props.client.unassociated.list.dataSource.data;

    const clients = this.isEditing()
      ? [...unassociatedClients, ...(details.data?.associatedClients || [])]
      : unassociatedClients;

    const mappedClients = this.getGroupedClients(clients);

    const systemRoles = this.props.getDirectoryData(["contact", "system", "role"]);

    const systemRole = findWhere(systemRoles, { id: systemRoleId });

    const isPersonReceivingServicesRole = systemRole?.name === PERSON_RECEIVING_SERVICES;

    const mappedSystemRoles = this.getContactSystemRoleSelectOptions();
    console.log(mappedSystemRoles, "roles");
    const qaUnavailableRoleIds = map(role.qaUnavailable.list.dataSource.data, (o) => o.id);

    /*const qaUnavailableRoleIds = this.props.getDirectoryData(['contact', 'system', 'role'])
        .filter(o => QA_UNAVAILABLE_ROLES.includes(o.name))
        .map(o => o.id)*/

    const isPendingOrExpiredContact = isPendingContact || isExpiredContact;

    const isPrimaryContactForActiveClients = isNotEmpty(details?.data?.activePrimaryContactClientNames);
    const isPrimaryContactForInactiveClients = !!details?.data?.inactivePrimaryContactClientsCount;

    return (
      <Form className={cn("ContactForm", className)}>
        <Action
          isMultiple
          shouldPerform={() => qaIncidentReports && qaUnavailableRoleIds.includes(systemRoleId)}
          action={() => this.onChangeField("qaIncidentReports", false)}
        />

        <Action
          isMultiple
          shouldPerform={() => isInteger(clientId) && isNotEmpty(systemRoles) && !isInteger(systemRoleId)}
          action={() => {
            const role = findWhere(systemRoles, { name: PERSON_RECEIVING_SERVICES });
            this.changeField("systemRoleId", role?.id);
          }}
        />

        <LoadContactQAUnavailableRolesAction />

        <ClearUnassociatedClientsAction
          isMultiple
          params={{ isFetching }}
          shouldPerform={(prevParams) => {
            return isFetching && !prevParams.isFetching;
          }}
        />

        <LoadUnassociatedClientsAction
          isMultiple
          params={{
            isFetching,
            organizationId,
            isPersonReceivingServicesRole,
          }}
          shouldPerform={(prevParams) =>
            isPersonReceivingServicesRole &&
            isInteger(organizationId) &&
            ((!isFetching && prevParams.isFetching) ||
              !prevParams.isPersonReceivingServicesRole ||
              organizationId !== prevParams.organizationId)
          }
        />

        <LoadClientDetailsAction
          params={{ clientId }}
          shouldPerform={() => isInteger(clientId) && (!client.details.data || clientId !== client.details.data.id)}
          onPerformed={({ data }) => {
            const defaultData = domain.mapClientToContact(data);

            this.changeFields({
              ...defaultData,
              isCommunityAddressUsed: isEmptyOrBlank(defaultData.address),
            });
          }}
        />

        <LoadCommunityAction
          isMultiple
          params={{ communityId }}
          shouldPerform={(prevParams) => isInteger(communityId) && communityId !== prevParams.communityId}
        />

        <Action
          isMultiple
          params={{ communityId }}
          shouldPerform={(prevParams) => !communityId && communityId !== prevParams.communityId}
          action={() => this.actions.community.details.clear()}
        />

        <Action
          isMultiple
          params={{ isCommunityAddressUsed }}
          shouldPerform={(prevParams) => isCommunityAddressUsed !== prevParams.isCommunityAddressUsed}
          action={() => {
            const { city, street, stateId, zipCode, zip } = isCommunityAddressUsed
              ? community ?? {}
              : this.isEditing()
                ? details.data?.address ?? {}
                : isInteger(clientId)
                  ? defaultData?.address ?? {}
                  : {};

            this.changeFields({
              address: {
                city,
                street,
                stateId,
                zip: zipCode ?? zip,
              },
            });
          }}
        />

        <Action
          isMultiple
          params={{ community }}
          shouldPerform={(prevParams) => community !== prevParams.community}
          action={() => {
            if (isCommunityAddressUsed) {
              const { city, street, stateId, zipCode } = community ?? {};

              this.changeFields({
                address: {
                  city,
                  street,
                  stateId,
                  zip: zipCode,
                },
              });
            }
          }}
        />

        {(details.isFetching || isFetching) && <Loader style={{ position: "fixed" }} hasBackdrop />}
        <Scrollable ref={this.scrollableRef}>
          <div className="ContactForm-Section">
            <div className="ContactForm-SectionTitle">General Data</div>
            <Row>
              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="firstName"
                  value={firstName}
                  label="First Name*"
                  className="ContactForm-TextField"
                  // isDisabled={isPendingOrExpiredContact}
                  hasError={firstNameHasError}
                  errorText={firstNameErrorText}
                  onChange={this.onChangeField}
                />
              </Col>
              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="lastName"
                  value={lastName}
                  label="Last Name*"
                  className="ContactForm-TextField"
                  // isDisabled={isPendingOrExpiredContact}
                  hasError={lastNameHasError}
                  errorText={lastNameErrorText}
                  onChange={this.onChangeField}
                />
              </Col>
              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="login"
                  value={login}
                  label="Login*"
                  placeholder="Email"
                  className="ContactForm-TextField"
                  isDisabled={isPendingOrExpiredContact || this.isEditing()}
                  hasError={loginHasError}
                  errorText={loginErrorText}
                  onChange={this.onChangeFieldWithLoginCheck}
                />
              </Col>
            </Row>
            <Row>
              <Col lg={8} md={6}>
                {canEditRole ? (
                  <SelectField
                    name="systemRoleId"
                    value={systemRoleId}
                    hasValueTooltip
                    options={mappedSystemRoles}
                    label="System Role*"
                    className="ContactForm-SelectField"
                    isDisabled={
                      isPendingContact || isExpiredContact || (this.isEditing() && isPersonReceivingServicesRole)
                    }
                    renderLabelIcon={() => <Info id="role-info-hint" className="ContactForm-SelectFieldLabelIcon" />}
                    hasError={systemRoleIdHasError}
                    errorText={systemRoleIdErrorText}
                    onChange={this.onChangeSystemRoleField}
                  />
                ) : (
                  <TextField
                    isDisabled
                    name="systemRole"
                    label="System Role*"
                    renderLabelIcon={() => <Info id="role-info-hint" className="ContactForm-SelectFieldLabelIcon" />}
                    value={systemRole?.title || details?.data?.systemRoleTitle}
                    className="ContactForm-TextField"
                  />
                )}
                <Tooltip
                  autohide={false}
                  boundariesElement={document.body}
                  className="RoleInfoHint"
                  target="role-info-hint"
                  modifiers={[
                    {
                      name: "offset",
                      options: { offset: [0, 6] },
                    },
                    {
                      name: "preventOverflow",
                      options: { boundary: document.body },
                    },
                  ]}
                >
                  Refer to the Contacts user guide to see the definition of the roles.&nbsp;
                  <Link className="link" to={path("/help/user-manuals")} onClick={() => {}}>
                    View
                  </Link>
                </Tooltip>
              </Col>
              <Col lg={4} md={6}>
                <CheckboxField
                  isDisabled
                  name="professionals"
                  value={professionals}
                  label="Professionals"
                  className="ContactForm-CheckboxField"
                  hasError={professionalsHasError}
                  errorText={professionalsErrorText}
                />
              </Col>
            </Row>
            <Row>
              <Col lg={4} md={6}>
                <SelectField
                  name="organizationId"
                  value={organizationId}
                  hasValueTooltip
                  hasAllOption={false}
                  hasNoneOption={false}
                  hasKeyboardSearch
                  hasKeyboardSearchText
                  options={organizationSelectOptions}
                  label="Organization*"
                  className="ContactForm-SelectField"
                  isDisabled={isPendingOrExpiredContact || this.isEditing()}
                  hasError={organizationIdHasError}
                  errorText={organizationIdErrorText}
                  onChange={this.onChangeFieldWithLoginCheck}
                />
              </Col>
              <Col lg={isPersonReceivingServicesRole ? 4 : 8} md={6}>
                <SelectField
                  type="text"
                  name="communityId"
                  value={communityId}
                  hasValueTooltip
                  hasAllOption={false}
                  hasNoneOption={false}
                  hasKeyboardSearch
                  hasKeyboardSearchText
                  options={communitySelectOptions}
                  label="Affiliated Community*"
                  className={cn("ContactForm-SelectField", { "no-pointer-events": communities.length <= 1 })}
                  isDisabled={isPendingOrExpiredContact}
                  hasError={communityIdHasError}
                  errorText={communityIdErrorText}
                  onChange={this.onChangeField}
                />
              </Col>
              {isCommunityManagementRole && (
                <Col lg={isPersonReceivingServicesRole ? 4 : 8} md={6}>
                  <SelectField
                    isMultiple
                    type="text"
                    name="otherCommunityIds"
                    value={otherCommunityIds}
                    hasValueTooltip
                    hasAllOption={true}
                    hasNoneOption={false}
                    hasKeyboardSearch
                    hasKeyboardSearchText
                    options={communitySelectOptions}
                    label="Managed Community*"
                    className={cn("ContactForm-SelectField", { "no-pointer-events": communities.length <= 1 })}
                    isDisabled={isPendingOrExpiredContact}
                    hasError={otherCommunityIdsHasError}
                    errorText={otherCommunityIdsErrorText}
                    onChange={this.onChangeFields}
                  />
                </Col>
              )}

              {isPersonReceivingServicesRole && (
                <Col lg={4} md={6}>
                  {isInteger(clientId) ? (
                    <TextField
                      isDisabled
                      label="Client"
                      value={client.details.data?.fullName}
                      className="ContactForm-TextField"
                    />
                  ) : (
                    <SelectField
                      isMultiple
                      isSectioned
                      name="associatedClientIds"
                      value={associatedClientIds}
                      hasValueTooltip
                      hasKeyboardSearch
                      hasKeyboardSearchText
                      sections={mappedClients}
                      hasSectionIndicator
                      hasSectionSeparator
                      label="Client"
                      className="ContactForm-SelectField"
                      isDisabled={isEmpty(mappedClients) || isInteger(clientId) || !isInteger(organizationId)}
                      onChange={this.onChangeAssociatedClientsField}
                    />
                  )}
                </Col>
              )}
            </Row>
            <Row>
              <Col lg={8} md={8}>
                <FileField
                  name="avatar"
                  value={avatar ? avatar.name : avatarName}
                  label="Profile photo"
                  className="ContactForm-FileField"
                  hasHint={true}
                  // isDisabled={isPendingOrExpiredContact}
                  hasError={avatarHasError}
                  errorText={avatarErrorText}
                  hintText={"Supported file types: JPG, PNG, GIF | Max 1 mb"}
                  onChange={this.onChangeField}
                />
              </Col>
            </Row>
          </div>
          <div className="ContactForm-Section">
            <div className="ContactForm-SectionTitle">Contact Info</div>
            <Row>
              <Col md={4}>
                <CheckboxField
                  name="isCommunityAddressUsed"
                  value={isCommunityAddressUsed}
                  label="Use community address"
                  className="ContactForm-CheckboxField"
                  // isDisabled={isPendingOrExpiredContact}
                  onChange={this.onChangeField}
                />
              </Col>
            </Row>
            <Row>
              <Col lg={8} md={6}>
                <TextField
                  type="text"
                  name="street"
                  value={address.street}
                  label="Street*"
                  className="ContactForm-TextField"
                  isDisabled={isCommunityAddressUsed}
                  hasError={address.streetHasError}
                  errorText={address.streetErrorText}
                  onChange={this.onChangeAddressField}
                />
              </Col>
              <Col lg={4} md={6}>
                <TextField
                  type="text"
                  name="city"
                  value={address.city}
                  label="City*"
                  className="ContactForm-TextField"
                  isDisabled={isCommunityAddressUsed}
                  hasError={address.cityHasError}
                  errorText={address.cityErrorText}
                  onChange={this.onChangeAddressField}
                />
              </Col>
            </Row>
            <Row>
              <Col lg={4} md={4}>
                <SelectField
                  name="stateId"
                  value={address.stateId}
                  label="State*"
                  hasValueTooltip
                  options={states}
                  className="ContactForm-TextField"
                  placeholder="Select State"
                  isDisabled={isCommunityAddressUsed}
                  hasError={address.stateIdHasError}
                  errorText={address.stateIdErrorText}
                  onChange={this.onChangeAddressField}
                />
              </Col>
              <Col lg={4} md={4}>
                <TextField
                  type="number"
                  name="zip"
                  value={address.zip}
                  label="Zip Code*"
                  className="ContactForm-TextField"
                  maxLength={5}
                  isDisabled={isCommunityAddressUsed}
                  hasError={address.zipHasError}
                  errorText={address.zipErrorText}
                  onChange={this.onChangeAddressField}
                />
              </Col>
              <Col lg={4} md={4}>
                <PhoneField
                  name="mobilePhone"
                  value={mobilePhone}
                  label="Mobile Phone #*"
                  className="ContactForm-TextField"
                  // isDisabled={isPendingOrExpiredContact}
                  errorText={mobilePhoneErrorText}
                  onChange={this.onChangeField}
                  renderLabelIcon={() => <Info id="mobile-phone-hint" className="ContactForm-InfoIcon" />}
                  tooltip={mobileTooltipHint}
                />
              </Col>
            </Row>
            <Row>
              <Col lg={4} md={4}>
                <PhoneField
                  name="phone"
                  value={phone}
                  label="Phone #"
                  className="ContactForm-TextField"
                  // isDisabled={isPendingOrExpiredContact}
                  errorText={phoneErrorText}
                  onChange={this.onChangeField}
                  renderLabelIcon={() => <Info id="phone-hint" className="ContactForm-InfoIcon" />}
                  tooltip={phoneTooltipHint}
                />
              </Col>
              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="fax"
                  value={fax || ""}
                  label="Fax"
                  className="ContactForm-TextField"
                  // isDisabled={isPendingOrExpiredContact}
                  hasError={faxHasError}
                  errorText={faxErrorText}
                  onChange={this.onChangeField}
                />
              </Col>
              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="secureMail"
                  value={secureMail}
                  label="Secure Email"
                  className="ContactForm-TextField"
                  // isDisabled={isPendingOrExpiredContact}
                  hasError={secureMailHasError}
                  errorText={secureMailErrorText}
                  onChange={this.onChangeField}
                />
              </Col>
            </Row>
          </div>
          <div className="ContactForm-Section">
            <div className="ContactForm-SectionTitle">Settings</div>
            <Row>
              <Col lg={4} md={4}>
                <CheckboxField
                  name="enableContact"
                  value={enableContact}
                  label="Enable contact"
                  className="ContactForm-CheckboxField"
                  hasError={enableContactHasError}
                  errorText={enableContactErrorText}
                  isDisabled={!this.isEditing() || isPendingOrExpiredContact || (status && status.name === PENDING)}
                  onChange={this.onChangeField}
                />
              </Col>

              <Col lg={4} md={4}>
                <CheckboxField
                  name="qaIncidentReports"
                  value={qaIncidentReports}
                  label="QA"
                  className="ContactForm-CheckboxField"
                  hasError={qaIncidentReportsHasError}
                  errorText={qaIncidentReportsErrorText}
                  isDisabled={qaUnavailableRoleIds.includes(systemRoleId)}
                  onChange={this.onChangeField}
                />
              </Col>
            </Row>
          </div>
        </Scrollable>

        <div className="ContactForm-Buttons">
          <>
            <Button outline color="success" disabled={isFetching} onClick={this.onClose}>
              Close
            </Button>

            {!isPendingOrExpiredContact && (
              <Button color="success" disabled={isFetching} onClick={this.tryToSubmit}>
                {this.isEditing() ? "Save" : "Send Invite"}
              </Button>
            )}

            {this.isEditing() && isPendingOrExpiredContact && (
              <Button color="success" disabled={isFetching} onClick={this.tryToSubmit}>
                Re-invite
              </Button>
            )}
          </>
        </div>
        {isAssociatedClientsLimitDialogOpen && (
          <WarningDialog
            isOpen
            title="You can select no more than 100 clients"
            buttons={[
              {
                text: "OK",
                onClick: () => this.setState({ isAssociatedClientsLimitDialogOpen: false }),
              },
            ]}
          />
        )}
        {isPrimaryContactWarningDialogOpen && (
          <WarningDialog
            isOpen
            title={
              <>
                <p>{`The selected user is a primary contact for ${
                  isPrimaryContactForActiveClients ? details?.data?.activePrimaryContactClientNames.join(", ") : ""
                }${isPrimaryContactForActiveClients && isPrimaryContactForInactiveClients ? " and " : ""}${
                  isPrimaryContactForInactiveClients
                    ? details?.data?.inactivePrimaryContactClientsCount + " inactive client(s)"
                    : ""
                }.`}</p>
                <p>{`${details?.data?.displayName} will no longer be the primary contact for the client(s) because you have changed the user role.`}</p>
              </>
            }
            buttons={[
              {
                text: "Cancel",
                color: "outline-success",
                onClick: () => this.setState({ isPrimaryContactWarningDialogOpen: false }),
              },
              {
                text: "Confirm",
                onClick: () => {
                  this.setState({ isPrimaryContactWarningDialogOpen: false });
                  this.submitIfValid();
                },
              },
            ]}
          />
        )}
        {isMarkedForDeletionDialogOpen && (
          <WarningDialog
            isOpen
            title="The end user has submitted a request to delete his/her account through the mobile app. The account will be activated. Please notify the end user."
            buttons={[
              {
                text: "Cancel",
                color: "outline-success",
                onClick: () => this.setState({ isMarkedForDeletionDialogOpen: false }),
              },
              {
                text: "Ok",
                onClick: () => {
                  this.setState({ isMarkedForDeletionDialogOpen: false });
                  this.submitIfValid();
                },
              },
            ]}
          />
        )}
      </Form>
    );
  }
}

export default compose(connect(mapStateToProps, mapDispatchToProps), withSelectOptions)(ContactForm);
