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
    associationId: PropTypes.string,
    contactId: PropTypes.string,
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
          });
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

  clearFormData = () => {
    this.changeFields({
      firstName: "",
      lastName: "",
      login: "",
      avatar: null,
      address: {
        street: "",
        city: "",
        stateId: "",
        zip: "",
      },
      avatarName: "",
      associationAddressUsed: false,
      phone: "",
      mobilePhone: "",
      fax: "",
      secureMail: "",
    });
  };

  componentWillUnmount() {
    changeLog = [];
    this.clear();
    this.clearFormData();
    this.actions.community.details.clear();
  }

  onChangeField = (name, value) => {
    if (name === "associationAddressUsed") {
      const { associationDetailData } = this.props;
      this.changeField(name, value).then(() => {
        this.onFieldChanged(name, value);
      });
      if (value) {
        this.onChangeAddressField("street", associationDetailData.street);
        this.onChangeAddressField("city", associationDetailData.city);
        this.onChangeAddressField("stateId", associationDetailData.state);
        this.onChangeAddressField("zip", associationDetailData.zipCode);
      } else {
        this.onChangeAddressField("street", null);
        this.onChangeAddressField("city", null);
        this.onChangeAddressField("stateId", null);
        this.onChangeAddressField("zip", null);
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
        associationAddressUsed: fields.associationAddressUsed,
      },
    };

    return actions.validate(fields.toJS(), options);
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
    return this.props.actions.details.loadAssociation(contactId);
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
    ];

    if (this.isEditing()) {
      if (!details.data?.address) {
        excluded.push("address");
      }
    } else if (fields.associationAddressUsed) {
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

  submitIfValid = () => {
    this.validate().then((success) => {
      if (success) {
        this.save().then(
          Response(({ data }) => {
            this.props.onSubmitSuccess();
          }),
        );
      } else {
        this.setState({ shouldScrollToError: true });
      }
    });
  };

  tryToEdit = (data) => {
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

  invite() {
    let { id } = this.props.fields;

    return this.props.actions.invite(id);
  }

  editInvite(data) {
    const { fields, contactId, actions } = this.props;

    const body = {
      id: this.isEditing() ? fields.id || data.id || contactId || "" : "",
      avatar: fields.avatar || null,
      associationId: Number(this.props.associationId),
      login: fields.login,
      firstName: fields.firstName,
      lastName: fields.lastName,
      associationAddressUsed: fields.associationAddressUsed,
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
    };

    return actions.editAssociationContactData(body);
  }

  save(data) {
    const { actions, fields, contactId } = this.props;

    return actions.submitAssociationCreateContact({
      id: this.isEditing() ? fields.id || data.id || contactId || "" : "",
      avatar: fields.avatar,
      associationId: Number(this.props.associationId),
      login: fields.login,
      firstName: fields.firstName,
      lastName: fields.lastName,
      associationAddressUsed: fields.associationAddressUsed,
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
    });
  }
  clear(data) {
    const { actions } = this.props;

    return actions.clearError();
  }

  scroll(...args) {
    this.scrollableRef.current?.scroll(...args) || noop();
  }

  scrollToError() {
    const $field = $(".CreateContactForm .form-control.is-invalid").eq(0);
    if ($field) this.scroll($field);
  }

  render() {
    const { fields, details, className, isFetching, isPendingContact, isExpiredContact, error } = this.props;

    const { isMarkedForDeletionDialogOpen, isPrimaryContactWarningDialogOpen, isAssociatedClientsLimitDialogOpen } =
      this.state;

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

      avatarName,

      address,
      associationAddressUsed,

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
    } = fields;

    const states = this.getStatesSelectOptions();

    const isPendingOrExpiredContact = isPendingContact || isExpiredContact;

    const isPrimaryContactForActiveClients = isNotEmpty(details?.data?.activePrimaryContactClientNames);
    const isPrimaryContactForInactiveClients = !!details?.data?.inactivePrimaryContactClientsCount;

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
                  // isDisabled={isPendingOrExpiredContact || this.isEditing()}
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
                  // isDisabled={isPendingOrExpiredContact || this.isEditing()}
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
                  hasError={loginHasError}
                  errorText={loginErrorText}
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
            </Row>
          </div>
          <div className="CreateContactForm-Section">
            <div className="CreateContactForm-SectionTitle">Contact Info</div>
            <Row>
              <Col md={4}>
                <CheckboxField
                  name="associationAddressUsed"
                  value={associationAddressUsed}
                  label="Use association address"
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
                  isDisabled={associationAddressUsed}
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
                  isDisabled={associationAddressUsed}
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
                  isDisabled={associationAddressUsed}
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
                  className="CreateContactForm-TextField"
                  maxLength={5}
                  isDisabled={associationAddressUsed}
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
                  // isDisabled={isPendingOrExpiredContact}
                  hasError={secureMailHasError}
                  errorText={secureMailErrorText}
                  onChange={this.onChangeField}
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
