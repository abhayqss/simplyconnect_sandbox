import React, { PureComponent } from "react";

import cn from "classnames";
import memo from "memoize-one";
import PropTypes from "prop-types";

import { isEqual, noop } from "underscore";

import $ from "jquery";

import { connect } from "react-redux";
import { bindActionCreators, compose } from "redux";

import { Button, Col, Form, Row } from "reactstrap";

import withSelectOptions from "hocs/withSelectOptions";

import { ErrorViewer, Loader, Scrollable } from "components";

import { CheckboxField, FileField, PhoneField, SelectField, TextField } from "components/Form";

import { WarningDialog } from "components/dialogs";

import { ClearUnassociatedClientsAction } from "actions/clients";

import * as createContactFormActions from "redux/contact/form/createContactFormActions";
import * as contactDetailsActions from "redux/contact/details/contactDetailsActions";
import * as communityListActions from "redux/contact/community/list/communityListActions";
import * as communityDetailsActions from "redux/community/details/communityDetailsActions";

import stateListActions from "redux/directory/state/list/stateListActions";
import clientListActions from "redux/directory/client/list/clientListActions";

import { ReactComponent as Info } from "images/info.svg";

import { isEmpty, isInteger, isNotEmpty, omitDeep } from "lib/utils/Utils";

import { Response } from "lib/utils/AjaxUtils";

import "./CreateContactForm.scss";
import adminVendorService from "services/AdminVendorService";
import { useAuthUser } from "../../../../hooks/common";

let changeLog = [];

const phoneTooltipHint = {
  target: "phone-hint",
  render: () => (
    <ul className="CreateContactForm-TooltipBody">
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
  const { list, form, role, details, community } = state.contact;

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
    auth: state.auth,
    client: state.client,

    directory: state.directory,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      ...bindActionCreators(createContactFormActions, dispatch),

      details: {
        ...bindActionCreators(contactDetailsActions, dispatch),
      },

      community: {
        list: bindActionCreators(communityListActions, dispatch),
        details: bindActionCreators(communityDetailsActions, dispatch),
      },

      directory: {
        state: { list: bindActionCreators(stateListActions, dispatch) },
        client: { list: bindActionCreators(clientListActions, dispatch) },
      },
    },
  };
}

class CreateContactForm extends PureComponent {
  static propTypes = {
    clientId: PropTypes.string,
    vendorId: PropTypes.string,
    contactId: PropTypes.string,
    isPendingContact: PropTypes.bool,
    isExpiredContact: PropTypes.bool,
    isShowRoleSelect: PropTypes.bool,
  };

  scrollableRef = React.createRef();

  state = {
    defaultCommunity: null,
    isStateHintOpen: false,
    shouldScrollToError: false,
    emailDuplication: false,
    emailDuplicationErrorText: false,
    isMarkedForDeletionDialogOpen: false,
    isPrimaryContactWarningDialogOpen: false,
    isAssociatedClientsLimitDialogOpen: false,
  };

  constructor(props) {
    super(props);

    this.getStatesSelectOptions = this.props.MemoizedSelectOptions(["state"]);

    this.getMemoizedCommunities = memo(this.getMemoizedCommunities, isEqual);
    this.getMemoizedOrganizations = memo(this.getMemoizedOrganizations, isEqual);
  }

  get actions() {
    return this.props.actions;
  }

  componentDidMount() {
    this.loadDirectoryData();

    const { contactId } = this.props;

    if (this.isEditing()) {
      this.loadDetails(contactId).then(
        Response(({ data }) => {
          const { communityId } = data;

          this.changeFields({
            ...data,
            address: data.address || {},
            enableContactVendor: data.enableContact,
          });
        }),
      );
    } else {
      this.changeFields({
        enableContactVendor: this.props.vendorDetailData.premium !== "static",
      });
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

  onChangeField = (name, value) => {
    // vendorDetailData
    if (name === "vendorAddressUsed") {
      const { vendorDetailData } = this.props;
      this.changeField(name, value).then(() => {
        this.onFieldChanged(name, value);
      });
      if (value) {
        this.onChangeAddressField("street", vendorDetailData.street);
        this.onChangeAddressField("city", vendorDetailData.city);
        this.onChangeAddressField("stateId", vendorDetailData.state);
        this.onChangeAddressField("zip", vendorDetailData.zipCode);
      } else {
        this.onChangeAddressField("street", null);
        this.onChangeAddressField("city", null);
        this.onChangeAddressField("stateId", null);
        this.onChangeAddressField("zip", null);
      }
    } else if (name === "careTeamRoleCode") {
      if (value) {
        this.changeField(name, value).then(() => {
          this.onFieldChanged(name, value);
        });
        this.setState({
          careTeamRoleCodeHasError: false,
        });
      }
    } else {
      this.changeField(name, value).then(() => {
        this.onFieldChanged(name, value);
        if (!this.props.isValid) this.validate();
      });
    }
  };

  onChangeFieldWithLoginCheck = (name, value) => {
    this.changeField(name, value).then(() => {
      this.setState({
        emailDuplication: false,
      });
      this.onFieldChanged(name, value);
    });
  };

  onFieldChanged = (name, value) => {
    changeLog.push({ name, value });
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

  clear() {
    this.props.actions.clear();
  }

  setCommunityIfSingle(communities) {
    if (communities.length === 1) {
      this.changeField("communityId", communities[0]?.id);
    }
  }

  validate() {
    const { fields, actions } = this.props;

    const options = {
      included: {
        isEditMode: this.isEditing(),
        vendorAddressUsed: fields.vendorAddressUsed,
        careTeamRoleCode: fields.careTeamRoleCode,
      },
    };

    return actions.validate(fields.toJS(), options);
  }

  validateEmail(data) {
    const { fields, actions, contactId } = this.props;
    const body = {
      id: this.isEditing() ? fields.id || data.id || contactId || "" : "",
      avatar: fields.avatar,
      vendorId: Number(this.props.vendorId),
      login: fields.login,
      firstName: fields.firstName,
      lastName: fields.lastName,
      vendorAddressUsed: fields.vendorAddressUsed,
      address: {
        street: fields.address.street,
        city: fields.address.city,
        stateId: fields.address.stateId,
        zip: fields.address.zip,
      },
      phone: fields.phone,
      mobilePhone: fields.mobilePhone,
      fax: fields.fax,
      secureMail: fields.secureMail,
      careTeamRoleCode: fields.careTeamRoleCode,
      enableContact: fields.enableContactVendor || false,
    };
    return adminVendorService.judgeVendorEmail(body);
  }

  isEditing() {
    return isInteger(this.props.contactId);
  }

  loadDirectoryData() {
    const { actions, canEditRole } = this.props;

    const { state } = actions.directory;

    state.list.load();
  }

  loadDetails(contactId) {
    return this.props.actions.details.loadVendorContact(contactId);
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

  onClose = () => {
    this.props.onClose(this.isFormChanged());
  };

  isFormChanged() {
    const { details, fields } = this.props;

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
      "careTeamRoleCode",
      "enableContactVendor",
    ];

    if (this.isEditing()) {
      if (!details.data?.address) {
        excluded.push("address");
      }
    } else if (fields.vendorAddressUsed) {
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
            },
        filter,
      ),
    );
  }

  tryToSubmit = () => {
    this.submitIfValid();
  };

  submitIfValid = (data) => {
    if (this.props?.fields?.careTeamRoleCode) {
      this.setState({
        careTeamRoleCodeHasError: false,
      });
      this.validate().then(async (success) => {
        if (!success) return false;
        return await this.validateEmail(data).then((res) => {
          if (res.success) {
            if (res.data) {
              this.setState({
                emailDuplication: true,
                emailDuplicationErrorText: "login email already exists",
                shouldScrollToError: true,
              });
            } else {
              this.save().then(
                Response(({ data }) => {
                  this.props.onSubmitSuccess();
                }),
              );
            }
          } else {
            this.setState({ shouldScrollToError: true });
          }
        });
      });
    } else {
      this.setState({
        careTeamRoleCodeHasError: true,
      });
    }
  };

  onInvite = () => {
    this.validate().then((success) => {
      if (success) {
        this.invite().then(
          Response(({ data }) => {
            this.editInvite(data).then(
              Response(() => {
                this.props.editContactSuccess();
              }),
            );
          }),
        );
      } else {
        this.setState({ shouldScrollToError: true });
      }
    });
  };
  tryToEdit = (data) => {
    console.log(data, "data");
    this.validate().then((success) => {
      if (success) {
        this.editInvite(data).then(
          Response(() => {
            this.props.editContactSuccess();
          }),
        );
      } else {
        this.setState({ shouldScrollToError: true });
      }
    });
  };

  invite() {
    let { id } = this.props.fields;

    return this.props.actions.invite(id);
  }

  editInvite(data) {
    const { fields, contactId, actions } = this.props;
    const body = {
      id: this.isEditing() ? fields.id || data.id || contactId || "" : "",
      avatar: fields.avatar || null,
      vendorId: Number(this.props.vendorId),
      login: fields.login,
      firstName: fields.firstName,
      lastName: fields.lastName,
      careTeamRoleCode: fields.careTeamRoleCode,
      vendorAddressUsed: fields.vendorAddressUsed,
      address: {
        street: fields.address.street,
        city: fields.address.city,
        stateId: fields.address.stateId,
        zip: fields.address.zip,
      },
      phone: fields.phone,
      mobilePhone: fields.mobilePhone,
      fax: fields.fax,
      secureMail: fields.secureMail,
      enableContact: fields.enableContactVendor || false,
    };

    return actions.editCreateContact(body);
  }

  save(data) {
    const { actions, fields, contactId } = this.props;

    return actions.submitCreateContact({
      id: this.isEditing() ? fields.id || data.id || contactId || "" : "",
      avatar: fields.avatar || null,
      vendorId: Number(this.props.vendorId),
      login: fields.login,
      firstName: fields.firstName,
      lastName: fields.lastName,
      vendorAddressUsed: fields.vendorAddressUsed,
      address: {
        street: fields.address.street,
        city: fields.address.city,
        stateId: fields.address.stateId,
        zip: fields.address.zip,
      },
      phone: fields.phone,
      mobilePhone: fields.mobilePhone,
      fax: fields.fax,
      secureMail: fields.secureMail,
      careTeamRoleCode: fields?.careTeamRoleCode,
      enableContact: fields?.enableContactVendor || false,
    });
  }

  scroll(...args) {
    this.scrollableRef.current?.scroll(...args) || noop();
  }

  scrollToError() {
    const $field = $(".CreateContactForm .form-control.is-invalid").eq(0);
    if ($field) this.scroll($field);
  }

  render() {
    const {
      fields,
      details,
      className,
      isFetching,
      isPendingContact,
      isExpiredContact,
      isShowRoleSelect,
      hieAgreement,
    } = this.props;

    const {
      isMarkedForDeletionDialogOpen,
      isPrimaryContactWarningDialogOpen,
      isAssociatedClientsLimitDialogOpen,
      emailDuplication,
      emailDuplicationErrorText,
      careTeamRoleCodeHasError,
    } = this.state;

    const {
      firstName,
      firstNameHasError,
      firstNameErrorText,

      lastName,
      lastNameHasError,
      lastNameErrorText,

      login,
      loginHasError,
      loginErrorText,

      avatar,
      avatarHasError,
      avatarErrorText,

      careTeamRoleCode,

      avatarName,

      address,
      vendorAddressUsed,

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
      enableContactVendor,
      enableContactVendorHasError,
      enableContactVendorErrorText,

      status,
    } = fields;

    const states = this.getStatesSelectOptions();

    const isPendingOrExpiredContact = isPendingContact || isExpiredContact;

    const isPrimaryContactForActiveClients = isNotEmpty(details?.data?.activePrimaryContactClientNames);
    const isPrimaryContactForInactiveClients = !!details?.data?.inactivePrimaryContactClientsCount;
    const user = useAuthUser();
    const isSuperAdmin = user.roleName === "ROLE_SUPER_ADMINISTRATOR";
    const roleOptions = [
      {
        text: "Doctor",
        value: "ROLE_DOCTOR_CODE",
      },
      {
        text: "Pharmacist",
        value: "ROLE_PHARMACIST_VENDOR_CODE",
      },
      {
        text: "Clinical Staff",
        value: "ROLE_VENDOR_CODE",
      },
    ];
    const nonClinicalVendorRoleOptions = [
      {
        text: "Non-Clinical Staff",
        value: "ROLE_NON_CLINICAL_VENDOR",
      },
    ];
    return (
      <Form className={cn("CreateContactForm", className)}>
        <ClearUnassociatedClientsAction
          isMultiple
          params={{ isFetching }}
          shouldPerform={(prevParams) => {
            return isFetching && !prevParams.isFetching;
          }}
        />
        {(details.isFetching || isFetching) && <Loader style={{ position: "fixed" }} hasBackdrop />}
        <Scrollable ref={this.scrollableRef}>
          <div className="CreateContactForm-Section">
            <div className="CreateContactForm-SectionTitle">General Data</div>
            <Row>
              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="firstName"
                  value={firstName}
                  label="First Name*"
                  className="CreateContactForm-TextField"
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
                  className="CreateContactForm-TextField"
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
                  className="CreateContactForm-TextField"
                  isDisabled={isPendingOrExpiredContact || this.isEditing()}
                  hasError={loginHasError || emailDuplication}
                  errorText={loginErrorText || emailDuplicationErrorText}
                  onChange={this.onChangeFieldWithLoginCheck}
                />
              </Col>
            </Row>
            <Row>
              <Col lg={8} md={8}>
                <FileField
                  name="avatar"
                  value={avatar ? avatar.name : avatarName}
                  label="User photo"
                  className="CreateContactForm-FileField"
                  hasHint={true}
                  // isDisabled={isPendingOrExpiredContact}
                  hasError={avatarHasError}
                  errorText={avatarErrorText}
                  hintText={"Supported file types: JPG, PNG, GIF | Max 1 mb"}
                  onChange={this.onChangeField}
                />
              </Col>
              {isShowRoleSelect && (
                <Col lg={4} md={4}>
                  <SelectField
                    name="careTeamRoleCode"
                    value={careTeamRoleCode}
                    label="Role*"
                    hasValueTooltip
                    options={hieAgreement ? roleOptions : nonClinicalVendorRoleOptions}
                    className="CreateContactForm-TextField"
                    placeholder="Select Role"
                    isDisabled={this.isEditing() && !isSuperAdmin}
                    hasError={careTeamRoleCodeHasError}
                    errorText={careTeamRoleCodeHasError ? "Please fill in the required field" : ""}
                    onChange={this.onChangeField}
                  />
                </Col>
              )}
            </Row>
          </div>
          <div className="CreateContactForm-Section">
            <div className="CreateContactForm-SectionTitle">Contact Info</div>
            <Row>
              <Col md={4}>
                <CheckboxField
                  name="vendorAddressUsed"
                  value={vendorAddressUsed}
                  label="Use vendor address"
                  className="CreateContactForm-CheckboxField"
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
                  className="CreateContactForm-TextField"
                  isDisabled={vendorAddressUsed}
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
                  className="CreateContactForm-TextField"
                  isDisabled={vendorAddressUsed}
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
                  className="CreateContactForm-TextField"
                  placeholder="Select State"
                  isDisabled={vendorAddressUsed}
                  hasError={address.stateIdHasError}
                  errorText={address.stateIdErrorText}
                  onChange={this.onChangeAddressField}
                />
              </Col>
              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="zip"
                  value={address.zip}
                  label="Zip Code*"
                  className="CreateContactForm-TextField"
                  maxLength={5}
                  isDisabled={vendorAddressUsed}
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
                  className="CreateContactForm-TextField"
                  haserror={mobilePhoneHasError}
                  errorText={mobilePhoneErrorText}
                  onChange={this.onChangeField}
                  renderLabelIcon={() => <Info id="mobile-phone-hint" className="CreateContactForm-InfoIcon" />}
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
                  className="CreateContactForm-TextField"
                  errorText={phoneErrorText}
                  onChange={this.onChangeField}
                  renderLabelIcon={() => <Info id="phone-hint" className="CreateContactForm-InfoIcon" />}
                  tooltip={phoneTooltipHint}
                />
              </Col>
              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="fax"
                  value={fax || ""}
                  label="Fax"
                  className="CreateContactForm-TextField"
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
                  className="CreateContactForm-TextField"
                  hasError={secureMailHasError}
                  errorText={secureMailErrorText}
                  onChange={this.onChangeField}
                />
              </Col>
            </Row>
          </div>
          <div className="CreateContactForm-Section">
            <div className="CreateContactForm-SectionTitle">Settings</div>
            <Row>
              <Col md={4}>
                <CheckboxField
                  name="enableContactVendor"
                  value={enableContactVendor}
                  label="Enable Contact" // premium
                  className="CreateContactForm-CheckboxField"
                  isDisabled={
                    !this.isEditing() || isPendingOrExpiredContact || this.props.vendorDetailData.premium === "static"
                  }
                  onChange={this.onChangeField}
                  hasError={enableContactVendorHasError}
                  errorText={enableContactVendorErrorText}
                />
              </Col>
            </Row>
          </div>
        </Scrollable>

        <div className="CreateContactForm-Buttons">
          <>
            <Button outline color="success" disabled={isFetching} onClick={this.onClose}>
              Close
            </Button>

            {!isPendingOrExpiredContact && !this.isEditing() && (
              <Button color="success" disabled={isFetching} onClick={this.tryToSubmit}>
                Invite
              </Button>
            )}
            {!isPendingOrExpiredContact && this.isEditing() && (
              <Button color="success" disabled={isFetching} onClick={this.tryToEdit}>
                Save
              </Button>
            )}

            {this.isEditing() && isPendingOrExpiredContact && (
              <Button color="success" disabled={isFetching} onClick={this.onInvite}>
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
                <p>{`The selected user is a primary contact for ${isPrimaryContactForActiveClients ? details?.data?.activePrimaryContactClientNames.join(", ") : ""}${isPrimaryContactForActiveClients && isPrimaryContactForInactiveClients ? " and " : ""}${isPrimaryContactForInactiveClients ? details?.data?.inactivePrimaryContactClientsCount + " inactive client(s)" : ""}.`}</p>
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
        {this.props?.error?.code && (
          <ErrorViewer
            isOpen
            error={this.props?.error}
            onClose={() => {
              this.actions.clearError();
            }}
          />
        )}
      </Form>
    );
  }
}

export default compose(connect(mapStateToProps, mapDispatchToProps), withSelectOptions)(CreateContactForm);
