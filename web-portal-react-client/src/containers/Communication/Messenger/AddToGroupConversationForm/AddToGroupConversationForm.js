import React, { forwardRef, memo, useCallback, useEffect, useImperativeHandle, useMemo, useState } from "react";

import { compact, isNumber, values } from "underscore";

import { Button, Col, Form, Row, UncontrolledTooltip as Tooltip } from "reactstrap";

import {
  useAuthUser,
  useCustomFormFieldChange,
  useForm,
  useScrollable,
  useScrollToFormError,
  useSelectOptions,
} from "hooks/common";

import { useClientCareTeamMembersQuery, useParticipatingAccessibilityQuery } from "hooks/business/conversations";

import { Loader } from "components";

import { CheckboxField, SelectField, TextField } from "components/Form";

import AddToGroupConversation from "entities/AddToGroupConversation";
import AddToGroupConversationFormValidator from "validators/AddToGroupConversationFormValidator";

import { isEmpty, isInteger } from "lib/utils/Utils";

import { FormButtons } from "./builders";

import ClientSection from "./ClientSection/ClientSection";
import OtherContactsSection from "./OtherContactsSection/OtherContactsSection";
import CommunityCareTeamSection from "./CareTeamMembersSection/CareTeamMembersSection";

import { STEP } from "../Constants";

import "./AddToGroupConversationForm.scss";
import { ONLY_VIEW_ROLES } from "../../../../lib/Constants";

const { Set } = require("immutable");

const getErrorMessage = (isNewChat) =>
  `Please pick at least ${isNewChat ? "2 users" : "1 user"} to start a group chat `;

function AddToGroupConversationForm(
  {
    step,
    onClose,
    groupName,
    onChangeStep,
    onSubmitSuccess,
    isNewConversation,
    areClientsExcluded,
    excludedContactIds = [],
  },
  ref,
) {
  const user = useAuthUser();
  const isAddMember = sessionStorage.getItem("isAddMember") === "true";
  const isInAssociationSystem = ONLY_VIEW_ROLES.includes(user.roleName);
  const isClient = user.roleName === "ROLE_PERSON_RECEIVING_SERVICES";

  const [needValidation, setNeedValidation] = useState(false);

  const { clear, fields, errors, validate, isChanged, clearField, changeField, changeFields } = useForm(
    "AddToGroupConversation",
    AddToGroupConversation,
    AddToGroupConversationFormValidator,
  );

  const { changeSelectField } = useCustomFormFieldChange(changeField);

  const { data: accessibility = {}, isFetching: isFetchingAccessibility } = useParticipatingAccessibilityQuery();

  const { data: clientCareTeamMembers, isFetching: isClientCareTeamFetching } = useClientCareTeamMembersQuery(
    { clientId: fields.client.id },
    { enabled: isNumber(fields.client.id) },
  );

  const clientCareTeamMemberIds = useMemo(
    () => fields.client.careTeamMemberIds.toJS(),
    [fields.client.careTeamMemberIds],
  );

  const filteredClientCareTeamMembers = useMemo(() => {
    return clientCareTeamMembers?.filter((o) => !excludedContactIds.includes(o.id));
  }, [clientCareTeamMembers, excludedContactIds]);

  const clientCareTeamMemberOptions = useSelectOptions(filteredClientCareTeamMembers, { textProp: "fullName" });

  const canCreateOtherContacts = Object.values(accessibility).some((v) => v);

  const selectedTypeValues = useMemo(() => values(fields.types.toJS()), [fields.types]);
  const isNoTypeChosen = isEmpty(compact(selectedTypeValues));
  const isOneTypeChosen = isAddMember
    ? compact(selectedTypeValues).length === 2
    : compact(selectedTypeValues).length === 1;
  const isClientTypeOnlyChosen = fields.types.client && isOneTypeChosen;

  const notEnoughUsersSelected = useMemo(() => {
    let minimumValue = isAddMember ? 1 : isNewConversation ? 2 : 1;

    let idSet = Set();

    if (!areClientsExcluded) {
      const contactId = fields.client.associatedContactId;

      if (isInteger(contactId)) {
        idSet = idSet.add(contactId);
      }
    }

    return (
      fields.contacts.ids
        .toSet()
        .union(idSet.toJS())
        .union(fields.careTeamMembers.ids)
        .union(fields.client.careTeamMemberIds).size < minimumValue
    );
  }, [
    isNewConversation,
    areClientsExcluded,
    fields.contacts.ids,
    fields.careTeamMembers.ids,
    fields.client.associatedContactId,
    fields.client.careTeamMemberIds,
  ]);

  const getExcludedContactIds = useCallback(
    (exception) => {
      let ids = [fields.contacts.ids, fields.careTeamMembers.ids];

      return ids.reduce((result, ids) => {
        if (ids === exception) return result;

        return [...result, ...(ids && ids.toJS())];
      }, excludedContactIds);
    },
    [excludedContactIds, fields.contacts.ids, fields.careTeamMembers.ids],
  );

  const excludedCTMemberIds = useMemo(() => {
    return getExcludedContactIds(fields.careTeamMembers.ids);
  }, [fields.careTeamMembers.ids, getExcludedContactIds]);

  const filteredExcludedContactIds = useMemo(() => {
    return getExcludedContactIds(fields.contacts.ids);
  }, [fields.contacts.ids, getExcludedContactIds]);

  const { Scrollable, scroll } = useScrollable();

  const onScroll = useScrollToFormError(".AddToGroupConversationForm", scroll);

  const onChangeType = useCallback(
    (field, value) => {
      changeField(field, value);

      if (field === "types.client" && !value) {
        changeField("types.clientCareTeam", value);
      }

      if (isClient) {
        if (field === "types.clientCareTeam") {
          changeField("client.id", user.associatedClients[0].id);
          changeField("client.communityId", user.communityId);
        }
      }
    },
    [changeField],
  );

  function cancel() {
    onClose(isChanged);
  }

  function next() {
    onChangeStep(1);
  }

  function back() {
    resetForm();
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

  function resetForm() {
    if (step === STEP.SELECT_USER) {
      clear();
      changeFields({
        types: fields.types,
        groupName,
      });
    }
  }

  function setDefaultData() {
    if (groupName) {
      changeFields({ groupName }, true);
    }
  }

  useEffect(setDefaultData, [groupName, changeFields]);
  useEffect(validateIf, [needValidation, onScroll, validate]);

  useImperativeHandle(ref, () => ({ cancel }));

  const CloseButton = (
    <Button outline onClick={cancel} color="success" className="AddToGroupConversationForm-Button">
      Cancel
    </Button>
  );

  const NextButton = (
    <>
      <div id="add-to-group-cv-form--next-btn" className="AddToGroupConversationForm-NextButtonWrapper">
        <Button
          onClick={next}
          color="success"
          disabled={isNoTypeChosen || (isNewConversation && isClientTypeOnlyChosen)}
          className="AddToGroupConversationForm-Button"
        >
          Next
        </Button>
        {isNoTypeChosen && (
          <Tooltip
            target="add-to-group-cv-form--next-btn"
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
            No any clients or contacts chosen to start a conversation with
          </Tooltip>
        )}
        {isClientTypeOnlyChosen && (
          <Tooltip
            target="add-to-group-cv-form--next-btn"
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
            You should choose any extra contact to start a conversation with
          </Tooltip>
        )}
      </div>
    </>
  );

  const BackButton = (
    <Button outline onClick={back} color="success" className="AddToGroupConversationForm-Button">
      Back
    </Button>
  );

  const SaveButton = (
    <>
      <div id="add-to-group-submit">
        <Button
          onClick={submit}
          color="success"
          disabled={notEnoughUsersSelected}
          className="AddToGroupConversationForm-Button"
        >
          Save
        </Button>
      </div>

      {notEnoughUsersSelected && (
        <Tooltip
          target="add-to-group-submit"
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
          {getErrorMessage(isNewConversation)}
        </Tooltip>
      )}
    </>
  );

  return (
    <Form className="AddToGroupConversationForm" onSubmit={submit}>
      {isFetchingAccessibility && <Loader style={{ position: "fixed" }} hasBackdrop />}

      <Scrollable style={{ flex: 1 }}>
        {step === STEP.SELECT_TYPE && (
          <div className="AddToGroupConversationForm-Section">
            <Row>
              <Col md="12">
                <CheckboxField
                  name="types.client"
                  label="Client"
                  value={fields.types.client}
                  isDisabled={!accessibility.areClientsAccessible || isInAssociationSystem}
                  className="AddToGroupConversationForm-CheckboxField"
                  onChange={onChangeType}
                />
              </Col>

              <Col md="12">
                <CheckboxField
                  name="types.clientCareTeam"
                  label="Client care team members"
                  isDisabled={
                    isClient ? !isClient : !fields.types.client || !accessibility.areClientCareTeamMembersAccessible
                  }
                  value={fields.types.clientCareTeam}
                  className="AddToGroupConversationForm-CheckboxField"
                  onChange={onChangeType}
                />
              </Col>

              <Col md="12">
                <CheckboxField
                  name="types.communityCareTeam"
                  label="Community care team members"
                  isDisabled={!accessibility.areCommunityCareTeamMembersAccessible}
                  value={fields.types.communityCareTeam}
                  className="AddToGroupConversationForm-CheckboxField"
                  onChange={onChangeType}
                />
              </Col>

              <Col md="12">
                <CheckboxField
                  name="types.other"
                  label="Other contacts"
                  isDisabled={!canCreateOtherContacts}
                  value={fields.types.other}
                  className="AddToGroupConversationForm-CheckboxField"
                  onChange={onChangeType}
                />
              </Col>
            </Row>
          </div>
        )}

        {step === STEP.SELECT_USER && (
          <>
            {!isAddMember && (
              <div className="AddToGroupConversationForm-Section">
                <Row>
                  <Col>
                    <TextField
                      type="text"
                      name="groupName"
                      value={fields.groupName}
                      label="Group Name"
                      maxLength={256}
                      className="AddToGroupConversationForm-TextField"
                      onChange={changeField}
                    />
                  </Col>
                </Row>
              </div>
            )}

            {!isClient && fields.types.client && (
              <ClientSection
                fields={fields.client}
                errors={errors.client}
                onClearField={clearField}
                onChangeField={changeField}
                includeNonAssociated={fields.types.clientCareTeam}
                excludedContactIds={!fields.types.clientCareTeam ? excludedContactIds : []}
              />
            )}

            {fields.types.clientCareTeam && (
              <div className="AddToGroupConversationForm-Section">
                <div className="AddToGroupConversationForm-SectionTitle">Client Care Team</div>

                <Row>
                  <Col>
                    <SelectField
                      hasTags
                      isMultiple
                      name="client.careTeamMemberIds"
                      value={clientCareTeamMemberIds}
                      hasKeyboardSearch
                      hasKeyboardSearchText
                      options={clientCareTeamMemberOptions}
                      label="Client Care Team"
                      placeholder="Select team members"
                      className="AddToGroupConversationForm-SelectField"
                      isDisabled={
                        !isNumber(fields.client.id) || isClientCareTeamFetching || isEmpty(clientCareTeamMemberOptions)
                      }
                      onChange={(name, value) => {
                        const selectedMembers = filteredClientCareTeamMembers.filter((member) =>
                          value.includes(member.id),
                        );
                        const chatUserIds = selectedMembers.map((member) => member.chatUserId);
                        changeSelectField(name, value);
                        changeSelectField("client.chatUserIds", chatUserIds);
                      }}
                      errorText={errors.client?.careTeamMemberIds}
                    />
                  </Col>
                </Row>
              </div>
            )}

            {fields.types.communityCareTeam && (
              <CommunityCareTeamSection
                name="careTeamMembers"
                excludedIds={excludedCTMemberIds}
                fields={fields.careTeamMembers}
                errors={errors.careTeamMembers}
                onChangeField={changeField}
              />
            )}

            {fields.types.other && (
              <OtherContactsSection
                name="contacts"
                fields={fields.contacts}
                errors={errors.contacts}
                excludedIds={filteredExcludedContactIds}
                onChangeField={changeField}
              />
            )}
          </>
        )}
      </Scrollable>

      <div className="AddToGroupConversationForm-Buttons">
        <FormButtons
          step={step}
          buttons={{
            CloseButton,
            BackButton,
            NextButton,
            SaveButton,
          }}
        />
      </div>
    </Form>
  );
}

export default memo(forwardRef(AddToGroupConversationForm));
