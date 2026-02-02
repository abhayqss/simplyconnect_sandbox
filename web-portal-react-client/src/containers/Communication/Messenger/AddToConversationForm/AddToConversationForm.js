import React, { memo, useMemo, useState, useEffect, useCallback } from "react";

import { first, isNumber, findWhere } from "underscore";

import { Col, Row, Form, Button, UncontrolledTooltip as Tooltip } from "reactstrap";

import { useForm, useScrollable, useSelectOptions, useScrollToFormError, useAuthUser } from "hooks/common";

import {
  useClientsQuery,
  useContactsQuery,
  useCommunitiesQuery,
  useOrganizationsQuery,
  useParticipatingAccessibilityQuery,
} from "hooks/business/conversations";

import { Loader } from "components";

import { SelectField, RadioGroupField } from "components/Form";

import AddToConversation from "entities/AddToConversation";
import AddToConversationFormValidator from "validators/AddToConversationFormValidator";

import { anyIsInteger } from "lib/utils/Utils";

import { FormButtonBuilder } from "./builders";

import { PARTICIPANT_TYPE, STEP } from "../Constants";

import "./AddToConversationForm.scss";
import { ONLY_VIEW_ROLES, SYSTEM_ROLES } from "../../../../lib/Constants";

const { CLIENT, CONTACT } = PARTICIPANT_TYPE;

function AddToConversationForm({ step, excludedContactIds, onClose, onChangeStep, onFieldChanged, onSubmitSuccess }) {
  const [needValidation, setNeedValidation] = useState(false);

  const { fields, errors, validate, isChanged, changeField } = useForm(
    "AddToGroupConversation",
    AddToConversation,
    AddToConversationFormValidator,
  );

  console.log(fields, "fields");

  const { data: accessibility = {}, isFetching: isFetchingAccessibility } = useParticipatingAccessibilityQuery({
    excludeOneToOneParticipants: true,
  });
  const user = useAuthUser();
  const isInAssociationSystem = ONLY_VIEW_ROLES.includes(user.roleName);

  const types = useMemo(
    () => [
      {
        label: "Client",
        value: CLIENT,
        isDisabled: !accessibility.areClientsAccessible || isInAssociationSystem,
      },
      { label: "All Contacts", value: CONTACT, isDisabled: !accessibility.areContactsAccessible },
    ],
    [accessibility],
  );

  const { data: organizations, isFetching: isFetchingOrganizations } = useOrganizationsQuery(
    {
      withExcludedOneToOneParticipants: true,
      withAccessibleClients: fields.type === CLIENT,
      withAccessibleContacts: fields.type === CONTACT,
    },
    { enabled: !!fields.type },
  );

  const organizationOptions = useSelectOptions(organizations);

  const { data: communities, isFetching: isFetchingCommunities } = useCommunitiesQuery(
    {
      withExcludedOneToOneParticipants: true,
      organizationIds: [fields.organizationId],
      withAccessibleClients: fields.type === CLIENT,
      withAccessibleContacts: fields.type === CONTACT,
    },
    { enabled: isNumber(fields.organizationId) },
  );

  const communityOptions = useSelectOptions(communities);

  const { data: contactsDataSource, isFetching: isFetchingContacts } = useContactsQuery(
    {
      communityIds: [fields.communityId],
      organizationIds: [fields.organizationId],
    },
    {
      enabled: isNumber(fields.communityId) && isNumber(fields.organizationId) && fields.type === CONTACT,
    },
  );

  const contacts = useMemo(() => {
    return contactsDataSource?.filter((o) => !excludedContactIds.includes(o.id));
  }, [contactsDataSource, excludedContactIds]);

  const contactOptions = useSelectOptions(contacts, { textProp: "name" });

  const { data: clientDataSource, isFetching: isFetchingClients } = useClientsQuery(
    {
      communityIds: [fields.communityId],
      excludeParticipatingInOneToOne: true,
    },
    {
      enabled: isNumber(fields.communityId) && fields.type === CLIENT,
    },
  );

  const clients = useMemo(() => {
    return clientDataSource?.filter((o) => !excludedContactIds.includes(o.associatedContactId));
  }, [clientDataSource, excludedContactIds]);

  const clientOptions = useSelectOptions(clients, { textProp: "fullName" });

  const { Scrollable, scroll } = useScrollable();

  const onScroll = useScrollToFormError(".AddToConversation", scroll);

  const onChangeField = useCallback(
    (name, value) => {
      changeField(name, value);

      if (name === "clientId") {
        const { associatedContactId } = findWhere(clients, { id: value }) || {};

        changeField("contactId", associatedContactId);
      }

      onFieldChanged(name, value);
    },
    [clients, changeField, onFieldChanged],
  );

  const setDefaultOption = useCallback(
    (options, field) => {
      if (options.length === 1) {
        onChangeField(field, first(options).value);
      }
    },
    [onChangeField],
  );

  function cancel() {
    onClose(isChanged);
  }

  function next() {
    onChangeStep(1);
  }

  function back() {
    onChangeStep(-1);
  }

  function submit(e) {
    e.preventDefault();

    if (step !== STEP.SELECT_USER) return;

    validate()
      .then(() => {
        onSubmitSuccess(fields.toJS());
        setNeedValidation(false);
      })
      .catch(() => {
        onScroll();
        setNeedValidation(true);
      });
  }

  function validateIf() {
    if (needValidation) {
      validate()
        .then(() => setNeedValidation(false))
        .catch(() => setNeedValidation(true));
    }
  }

  function setDefaultOrganization() {
    setDefaultOption(organizationOptions, "organizationId");
  }

  function setDefaultCommunity() {
    setDefaultOption(communityOptions, "communityId");
  }

  function setDefaultId() {
    if (fields.type === CLIENT) {
      setDefaultOption(clientOptions, "clientId");
    } else {
      setDefaultOption(contactOptions, "contactId");
    }
  }

  const CloseButton = (
    <Button outline onClick={cancel} color="success" className="AddToConversationForm-Button">
      Cancel
    </Button>
  );

  const NextButton = (
    <Button onClick={next} color="success" disabled={!fields.type} className="AddToConversationForm-Button">
      Next
    </Button>
  );

  const BackButton = (
    <Button outline onClick={back} color="success" className="AddToConversationForm-Button">
      Back
    </Button>
  );

  const SaveButton = (
    <Button
      onClick={submit}
      color="success"
      disabled={!anyIsInteger(fields.clientId, fields.contactId)}
      className="AddToConversationForm-Button"
    >
      Save
    </Button>
  );

  const context = {
    step,
    buttons: { CloseButton, BackButton, NextButton, SaveButton },
  };

  const buttonsBuilder = new FormButtonBuilder(context);

  const FormActionButtons = buttonsBuilder.build();

  useEffect(() => {
    changeField("clientId", null);
    changeField("contactId", null);
    changeField("communityId", null);
    changeField("organizationId", null);
  }, [fields.type, changeField]);

  useEffect(validateIf, [needValidation, onScroll, validate]);

  useEffect(setDefaultOrganization, [organizationOptions, setDefaultOption]);
  useEffect(setDefaultCommunity, [communityOptions, setDefaultOption]);
  useEffect(setDefaultId, [fields.type, clientOptions, contactOptions, setDefaultOption]);

  return (
    <Form className="AddToConversationForm" onSubmit={submit}>
      {isFetchingAccessibility && <Loader style={{ position: "fixed" }} hasBackdrop />}

      <Scrollable style={{ flex: 1 }}>
        {step === STEP.SELECT_TYPE && (
          <div className="AddToConversationForm-Section">
            <Row>
              <Col>
                <RadioGroupField
                  name="type"
                  selected={fields.type}
                  className="AddToConversationForm-RadioGroupField"
                  options={types}
                  onChange={onChangeField}
                  errorText={errors.type}
                />
              </Col>
            </Row>
          </div>
        )}

        {step === STEP.SELECT_USER && (
          <div className="AddToConversationForm-Section">
            <Row>
              <Col lg={4} md={6} sm={6}>
                <SelectField
                  label="Organization"
                  name="organizationId"
                  value={fields.organizationId}
                  options={organizationOptions}
                  hasTooltip
                  isDisabled={isFetchingOrganizations}
                  placeholder="Organization"
                  onChange={onChangeField}
                />
              </Col>

              <Col lg={4} md={6} sm={6}>
                <SelectField
                  label="Community"
                  name="communityId"
                  value={fields.communityId}
                  options={communityOptions}
                  hasTooltip
                  hasKeyboardSearch
                  hasKeyboardSearchText
                  isDisabled={isFetchingCommunities}
                  placeholder="Community"
                  onChange={onChangeField}
                />
              </Col>

              <Col lg={4} md={6} sm={6}>
                {fields.type === CLIENT ? (
                  <SelectField
                    label="Client"
                    name="clientId"
                    value={fields.clientId}
                    options={clientOptions}
                    hasTooltip
                    hasKeyboardSearch
                    hasKeyboardSearchText
                    isDisabled={isFetchingClients || !clientOptions.length}
                    onChange={onChangeField}
                  />
                ) : (
                  <SelectField
                    label="Contact"
                    name="contactId"
                    value={fields.contactId}
                    options={contactOptions}
                    hasTooltip
                    hasKeyboardSearch
                    hasKeyboardSearchText
                    isDisabled={isFetchingContacts || !contactOptions.length}
                    onChange={onChangeField}
                  />
                )}
              </Col>

              {fields.type === CLIENT && !clientOptions.length && (
                <Tooltip
                  placement="top"
                  target="MultiSelect_Toggle__clientId"
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
                  No clients
                </Tooltip>
              )}

              {fields.type === CONTACT && !contactOptions.length && (
                <Tooltip
                  placement="top"
                  target="MultiSelect_Toggle__contactId"
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
                  No contacts
                </Tooltip>
              )}
            </Row>
          </div>
        )}
      </Scrollable>

      <div className="AddToConversationForm-Buttons">
        <FormActionButtons />
      </div>
    </Form>
  );
}

export default memo(AddToConversationForm);
