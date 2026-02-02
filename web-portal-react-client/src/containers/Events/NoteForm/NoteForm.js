import React, { memo, useMemo, useState, useEffect, useCallback } from "react";

import { get, map, filter, reduce, isNumber } from "underscore";

import { connect } from "react-redux";
import { bindActionCreators, compose } from "redux";

import { Form, Col, Row, Button, UncontrolledTooltip as Tooltip } from "reactstrap";

import { withAutoSave } from "hocs";

import { TextField, DateField, SelectField, RadioGroupField } from "components/Form";

import { useScrollable, useDirectoryData, useSelectOptions, useScrollToFormError } from "hooks/common";

import { useNoteTypesQuery, useAdmittancesQuery, useEncounterTypesQuery } from "hooks/business/directory";

import { useClientProgramNoteTypesQuery } from "hooks/business/directory/query";

import { useFormSubmit } from "hooks/common/redux";

import noteFormActions from "redux/note/form/noteFormActions";
import * as errorActions from "redux/error/errorActions";
import servicePlanActions from "redux/client/servicePlan/controlled/controlledActions";

import { useNoteFormBehaviour } from "../hooks";

import ResourcesDialog from "./ResourcesDialog/ResourcesDialog";
import EncounterSection from "./EncounterSection/EncounterSection";

import { DateUtils as DU, omitEmptyProps } from "lib/utils/Utils";
import { getObjectPaths } from "lib/utils/ObjectUtils";

import "./NoteForm.scss";

const { format, formats } = DU;

const DATE_FORMAT = formats.longDateMediumTime12;

const scrollableStyles = { flex: 1 };

const YES_NO_OPTIONS = [
  { value: true, label: "Yes" },
  { value: false, label: "No" },
];

const CLIENT_PROGRAM = "CLIENT_PROGRAM";
const SERVICE_STATUS_CHECK = "SERVICE_STATUS_CHECK";

const getFullResourceName = (o) => [o.providerName, o.resourceName].filter((v) => v).join(", ");

const isSSCheckType = (type) => type?.name === SERVICE_STATUS_CHECK;
const isClientProgramType = (type) => type?.name === CLIENT_PROGRAM;

function mapStateToProps(state) {
  const { servicePlan } = state.client;

  return {
    state: state.note.form,
    user: state.auth.login.user.data,
    details: state.note.details.data,

    servicePlan: servicePlan.controlled.data,
    resourceNames: servicePlan.resourceName.list.dataSource.data,
  };
}

const messageIfNoServicePlanProvided = `The "Service Status Check" note type is enabled if the client has "in development" service plan with "Ongoing  Service?" value = "Yes" and  the "Resource name" field is populated`;

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      ...bindActionCreators(noteFormActions, dispatch),
      error: bindActionCreators(errorActions, dispatch),
      servicePlan: {
        controlled: bindActionCreators(servicePlanActions, dispatch),
      },
    },
  };
}

function getData(data, isSPNoteType, isCPNoteType) {
  const { encounter, clientProgram, serviceStatusCheck } = data;

  return {
    ...data,
    clientProgram: isCPNoteType ? clientProgram : null,
    encounter: {
      ...encounter,
      clinicianId: encounter.clinicianId > 0 ? encounter.clinicianId : null,
    },
    serviceStatusCheck: isSPNoteType ? serviceStatusCheck : null,
  };
}

function NoteForm({
  user,
  state,
  actions,
  noteId,
  details,
  eventId,
  onClose,
  clientId,
  clientName,
  servicePlan,
  resourceNames,
  organizationId,
  autoSaveAdapter,
  onSubmitSuccess,
}) {
  const {
    fields,
    isFetching,
    validation: { errors },
  } = state;

  const {
    plan,
    noteDate,
    encounter,
    subTypeId,
    objective,
    assessment,
    subjective,
    admitDateId,
    clientProgram,
    serviceStatusCheck,
  } = fields;

  const [isResourcesDialogOpen, setIsResourcesDialogOpen] = useState(false);
  const [shouldSaveWithErrors, setShouldSaveWithErrors] = useState(false);
  const [isAutoSaving, setAutoSaving] = useState(false);

  const isEditMode = isNumber(noteId);
  const isEventNote = isNumber(eventId);
  const params = useMemo(
    () => (isNumber(organizationId) ? { organizationId } : { clientId }),
    [clientId, organizationId],
  );

  const { noteTypes, admitDates } = useDirectoryData({
    noteTypes: ["note", "type"],
    admitDates: ["note", "admittance"],
  });

  const { data: clientProgramNoteTypes } = useClientProgramNoteTypesQuery();

  const selectedAdmitDate = useMemo(
    () => admitDates.find((date) => date.id === admitDateId),
    [admitDateId, admitDates],
  );
  const takenNoteTypeIds = useMemo(() => selectedAdmitDate?.takenNoteTypeIds || [], [selectedAdmitDate]);
  const selectedNoteType = useMemo(() => noteTypes.find((type) => type.id === subTypeId), [noteTypes, subTypeId]);

  const isCMTypeSelected = selectedNoteType?.followUpCode != null;
  const isSSCTypeSelected = isSSCheckType(selectedNoteType);
  const isClientProgramTypeSelected = isClientProgramType(selectedNoteType);

  const noteTypeOptions = useMemo(() => {
    return noteTypes
      .filter((type) => type.canCreate)
      .filter((type) => !isEventNote || type.canCreateEventNote)
      .map((type) => ({
        text: type.title,
        value: type.id,
        tooltip: isSSCheckType(type) && servicePlan == null ? messageIfNoServicePlanProvided : null,
        isDisabled: isSSCheckType(type) ? servicePlan == null : takenNoteTypeIds.includes(type.id),
      }));
  }, [isEventNote, noteTypes, servicePlan, takenNoteTypeIds]);

  const CMSubTypes = useMemo(() => {
    return noteTypes.filter((type) => type.followUpCode).map((type) => type.id);
  }, [noteTypes]);

  const EncounterSubTypes = useMemo(() => {
    return noteTypes.filter((type) => type.encounterCode).map((type) => type.id);
  }, [noteTypes]);

  const admitDatesOptions = useMemo(() => {
    return admitDates.map((admitDate) => ({
      text: format(new Date(admitDate.date), DATE_FORMAT),
      value: admitDate.id,
      isDisabled: admitDate.takenNoteTypeIds.includes(selectedNoteType?.id),
    }));
  }, [admitDates, selectedNoteType]);

  const clientProgramNoteTypeOptions = useSelectOptions(clientProgramNoteTypes);

  const { scroll, Scrollable } = useScrollable();
  const scrollToError = useScrollToFormError(".NoteForm", scroll);

  const validationOptions = useMemo(
    () => ({
      included: {
        CMSubTypes,
        EncounterSubTypes,
        serviceStatusCheckSelected: isSSCTypeSelected,
        clientProgramSelected: isClientProgramTypeSelected,
      },
    }),
    [CMSubTypes, EncounterSubTypes, isSSCTypeSelected, isClientProgramTypeSelected],
  );

  const validate = useCallback(
    (data) => {
      return actions.validate(data, validationOptions);
    },
    [actions, validationOptions],
  );

  const validation = useMemo(
    () => ({
      options: validationOptions,
    }),
    [validationOptions],
  );

  const submit = useFormSubmit(state, actions, params, { validation });

  const onChangeField = useCallback(
    (field, value) => {
      actions.changeField(field, value);
    },
    [actions],
  );

  const onChangeFields = useCallback(
    (changes) => {
      actions.changeFields(changes);
    },
    [actions],
  );

  const onChangeDateField = useCallback(
    (field, dateValue) => {
      const value = dateValue ? dateValue.getTime() : "";

      onChangeField(field, value);
    },
    [onChangeField],
  );

  const onChangeEncounterField = useCallback(
    (field, value) => onChangeField(`encounter.${field}`, value),
    [onChangeField],
  );

  const onChangeEncounterFields = useCallback((changes) => onChangeFields({ encounter: changes }), [onChangeFields]);

  const onCancel = useCallback(() => onClose(state.isChanged()), [onClose, state]);

  const data = useMemo(
    () => getData(state.fields.toJS(), isSSCTypeSelected, isClientProgramTypeSelected),
    [isSSCTypeSelected, isClientProgramTypeSelected, state],
  );

  const onSubmitFailure = useCallback(
    (e) => {
      actions.error.change(e);
    },
    [actions],
  );

  const { onSubmit } = useNoteFormBehaviour(
    state,
    {
      validate,
      scrollToError,
      submit: useCallback(() => submit(data), [data, submit]),
    },
    {
      onCancel,
      onSuccess: onSubmitSuccess,
      onFailure: onSubmitFailure,
    },
  );

  const autoSave = useCallback(() => {
    validate(state.fields.toJS()).then((success) => {
      if (success) {
        setAutoSaving(true);
        actions.submit({ isAutoSave: true, ...data }, params).then(() => setAutoSaving(false));
      } else setShouldSaveWithErrors(true);
    });
  }, [data, state, params, actions, validate]);

  useEffect(() => {
    if (autoSaveAdapter && isEditMode && !isEventNote) {
      autoSaveAdapter.init({ onSave: autoSave });
    }
  }, [autoSave, isEditMode, isEventNote, autoSaveAdapter]);

  useEffect(() => {
    if (errors && shouldSaveWithErrors && !isAutoSaving) {
      setAutoSaving(true);

      const errorFields = filter(getObjectPaths(errors.toJS()), (path) => !!get(errors.toJS(), path));

      actions
        .submit(
          {
            ...getData(
              {
                ...state.fields.toJS(),
                ...reduce(
                  errorFields,
                  (acc, field) => ({
                    ...acc,
                    [field]: details[field],
                  }),
                  {},
                ),
              },
              isSSCTypeSelected,
              isClientProgramTypeSelected,
            ),
            isAutoSave: true,
          },
          params,
        )
        .then(() => setAutoSaving(false));
    }
  }, [
    state,
    params,
    errors,
    details,
    actions,
    isAutoSaving,
    isSSCTypeSelected,
    shouldSaveWithErrors,
    isClientProgramTypeSelected,
  ]);

  const onResourceDialogOpen = useCallback(() => setIsResourcesDialogOpen(true), []);
  const onResourceDialogClose = useCallback(() => setIsResourcesDialogOpen(false), []);

  const onSelectResource = useCallback(
    function onSelectResourceCb(resource) {
      onResourceDialogClose();
      actions.changeFields({ serviceStatusCheck: resource });
    },
    [actions, onResourceDialogClose],
  );

  useNoteTypesQuery();
  useEncounterTypesQuery();
  useAdmittancesQuery(clientId);

  useEffect(() => {
    const now = new Date().getTime();
    const data = !isEditMode
      ? {
          eventId,
          noteDate: now,
          serviceStatusCheck: {
            checkDate: now,
            auditPerson: user?.fullName,
            servicePlanId: servicePlan?.id,
            servicePlanCreatedDate: servicePlan?.dateCreated,
          },
        }
      : omitEmptyProps(details);

    actions.changeFields(data, true);
  }, [user, actions, details, eventId, isEditMode, resourceNames, servicePlan]);

  useEffect(() => {
    if (selectedAdmitDate && !isCMTypeSelected) {
      onChangeField("admitDateId", null);
    }
  }, [isCMTypeSelected, selectedAdmitDate, onChangeField]);

  useEffect(() => {
    if (!isEventNote) {
      actions.servicePlan.controlled.load(clientId);

      return () => {
        actions.servicePlan.controlled.clear();
      };
    }
  }, [actions, clientId, isEventNote]);

  useEffect(() => () => actions.clear(), [actions]);

  return (
    <>
      <Form className="NoteForm" onSubmit={onSubmit}>
        <Scrollable style={scrollableStyles}>
          <div className="NoteForm-Section">
            <Row>
              <Col md={4}>
                <TextField
                  type="text"
                  name="author"
                  value={user?.fullName}
                  isDisabled
                  label="Person Submitting Note*"
                  className="NoteForm-TextField"
                />
              </Col>
              <Col md={4}>
                <TextField
                  type="text"
                  name="clientName"
                  value={clientName}
                  isDisabled
                  label="Client*"
                  className="NoteForm-TextField"
                />
              </Col>
              <Col md={4}>
                <DateField
                  type="text"
                  hasTimeSelect
                  name="noteDate"
                  value={noteDate}
                  isDisabled={isEditMode}
                  label="Note Date and Time*"
                  className="NoteForm-TextField"
                  dateFormat="MM/dd/yyyy hh:mm a"
                  timeFormat="h:mm aa"
                  errorText={errors.noteDate}
                  onChange={onChangeDateField}
                />
              </Col>
            </Row>
            <Row>
              <Col md={6}>
                <SelectField
                  type="text"
                  hasSearchBox
                  name="subTypeId"
                  value={subTypeId}
                  isDisabled={isEditMode}
                  options={noteTypeOptions}
                  label="Note Type*"
                  className="NoteForm-SelectField"
                  errorText={errors.subTypeId}
                  onChange={onChangeField}
                />
              </Col>
              <Col md={6}>
                <SelectField
                  id="admitDateId"
                  name="admitDateId"
                  value={admitDateId}
                  options={admitDatesOptions}
                  isDisabled={!isCMTypeSelected || !admitDatesOptions.length}
                  label="Admit / Intake Date*"
                  className="NoteForm-TextField"
                  errorText={errors.admitDateId}
                  onChange={onChangeField}
                />

                {(!isCMTypeSelected || !admitDatesOptions.length) && (
                  <Tooltip
                    target="admitDateId"
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
                    There is no admit/intake date. Please add the admittance information or the intake date on the "Edit
                    Client" screen.
                  </Tooltip>
                )}
              </Col>
            </Row>

            {isClientProgramTypeSelected && (
              <>
                <Row>
                  <Col md={6}>
                    <SelectField
                      hasSearchBox
                      name="clientProgram.typeId"
                      value={clientProgram.typeId}
                      options={clientProgramNoteTypeOptions}
                      label="Program sub type*"
                      className="NoteForm-SelectField"
                      errorText={errors.clientProgram?.typeId}
                      onChange={onChangeField}
                    />
                  </Col>

                  <Col md={6}>
                    <TextField
                      type="text"
                      name="clientProgram.serviceProvider"
                      value={clientProgram.serviceProvider}
                      label="Service Provider for Program*"
                      className="NoteForm-TextField"
                      maxLength={256}
                      errorText={errors.clientProgram?.serviceProvider}
                      onChange={onChangeField}
                    />
                  </Col>
                </Row>

                <Row>
                  <Col md={6}>
                    <DateField
                      type="text"
                      name="clientProgram.startDate"
                      value={clientProgram.startDate}
                      label="Start Date of Program*"
                      className="NoteForm-DateField"
                      dateFormat="MM/dd/yyyy"
                      maxTime={clientProgram.endDate}
                      maxDate={clientProgram.endDate}
                      errorText={errors.clientProgram?.startDate}
                      onChange={onChangeDateField}
                    />
                  </Col>

                  <Col md={6}>
                    <DateField
                      type="text"
                      popperPlacement="bottom-end"
                      name="clientProgram.endDate"
                      value={clientProgram.endDate}
                      label="End Date of Program*"
                      className="NoteForm-DateField"
                      dateFormat="MM/dd/yyyy"
                      minTime={clientProgram.startDate}
                      minDate={clientProgram.startDate}
                      errorText={errors.clientProgram?.endDate}
                      onChange={onChangeDateField}
                    />
                  </Col>
                </Row>
              </>
            )}

            {isSSCTypeSelected && (
              <>
                <Row>
                  <Col md={6}>
                    <TextField
                      isDisabled
                      type="text"
                      name="serviceStatusCheck.servicePlanCreatedDate"
                      value={format(serviceStatusCheck.servicePlanCreatedDate, DATE_FORMAT)}
                      label="Service Plan*"
                      className="NoteForm-TextField"
                    />
                  </Col>
                  <Col md={6}>
                    <Row className="NoteForm-ResourceNameSection">
                      <Col className="NoteForm-ResourceName">
                        <TextField
                          isDisabled
                          type="text"
                          name="serviceStatusCheck.resourceName"
                          placeholder="Select"
                          value={getFullResourceName(serviceStatusCheck)}
                          label="Resource Name*"
                          errorText={errors.serviceStatusCheck.providerName || errors.serviceStatusCheck.resourceName}
                        />

                        <Button
                          color="success"
                          className="NoteForm-AddResourceBtn margin-bottom-20"
                          onClick={onResourceDialogOpen}
                        >
                          {serviceStatusCheck.providerName ? "Change" : "Add"}
                        </Button>
                      </Col>
                    </Row>
                  </Col>
                </Row>

                <Row>
                  <Col md={6}>
                    <TextField
                      type="text"
                      name="serviceStatusCheck.auditPerson"
                      value={serviceStatusCheck.auditPerson}
                      label="Person Who Did the Audit / Check*"
                      className="NoteForm-TextField"
                      maxLength={256}
                      errorText={errors.serviceStatusCheck.auditPerson}
                      onChange={onChangeField}
                    />
                  </Col>
                  <Col md={3}>
                    <DateField
                      type="text"
                      hasTimeSelect
                      name="serviceStatusCheck.checkDate"
                      value={serviceStatusCheck.checkDate}
                      label="Date of Check*"
                      className="NoteForm-TextField"
                      dateFormat="MM/dd/yyyy hh:mm a"
                      timeFormat="h:mm aa"
                      maxTime={serviceStatusCheck.nextCheckDate}
                      maxDate={serviceStatusCheck.nextCheckDate}
                      errorText={errors.serviceStatusCheck.checkDate}
                      onChange={onChangeDateField}
                    />
                  </Col>
                  <Col md={3}>
                    <DateField
                      type="text"
                      hasTimeSelect
                      popperPlacement="bottom-end"
                      name="serviceStatusCheck.nextCheckDate"
                      value={serviceStatusCheck.nextCheckDate}
                      label="Next Date of Check"
                      className="NoteForm-DateField NoteForm-DateField_NextCheckDate"
                      dateFormat="MM/dd/yyyy hh:mm a"
                      timeFormat="h:mm aa"
                      minTime={serviceStatusCheck.checkDate}
                      minDate={serviceStatusCheck.checkDate}
                      onChange={onChangeDateField}
                    />
                  </Col>
                </Row>
                <Row>
                  <Col>
                    <RadioGroupField
                      view="row"
                      className="NoteForm-RadioGroupField"
                      name="serviceStatusCheck.serviceProvided"
                      title="Is the service being provided?*"
                      selected={serviceStatusCheck.serviceProvided}
                      hasError={!!errors.serviceStatusCheck.serviceProvided}
                      errorText={errors.serviceStatusCheck.serviceProvided}
                      options={YES_NO_OPTIONS}
                      onChange={onChangeField}
                    />
                  </Col>
                </Row>
              </>
            )}

            <EncounterSection
              noteId={noteId}
              clientId={clientId}
              noteTypeId={subTypeId}
              organizationId={organizationId}
              state={encounter}
              errors={errors.encounter}
              onChangeField={onChangeEncounterField}
              onChangeFields={onChangeEncounterFields}
            />

            <Row>
              <Col md={12}>
                <TextField
                  type="textarea"
                  name="subjective"
                  value={subjective}
                  label="Subjective*"
                  className="NoteForm-TextArea"
                  maxLength={20000}
                  errorText={errors.subjective}
                  onChange={onChangeField}
                />
              </Col>
            </Row>
            <Row>
              <Col md={12}>
                <TextField
                  type="textarea"
                  name="objective"
                  value={objective}
                  label="Objective"
                  maxLength={20000}
                  className="NoteForm-TextArea"
                  onChange={onChangeField}
                />
              </Col>
            </Row>
            <Row>
              <Col md={12}>
                <TextField
                  type="textarea"
                  name="assessment"
                  value={assessment}
                  label="Assessment"
                  maxLength={20000}
                  className="NoteForm-TextArea"
                  onChange={onChangeField}
                />
              </Col>
            </Row>
            <Row>
              <Col md={12}>
                <TextField
                  type="textarea"
                  name="plan"
                  value={plan}
                  label="Plan"
                  maxLength={20000}
                  className="NoteForm-TextArea"
                  onChange={onChangeField}
                />
              </Col>
            </Row>
          </div>
        </Scrollable>

        <div className="NoteForm-Buttons">
          <Button outline color="success" onClick={onCancel}>
            Cancel
          </Button>
          <Button color="success" disabled={isFetching}>
            {isEditMode ? "Save" : "Submit"}
          </Button>
        </div>
      </Form>

      <ResourcesDialog
        clientId={clientId}
        onSelect={onSelectResource}
        isOpen={isResourcesDialogOpen}
        onClose={onResourceDialogClose}
      />
    </>
  );
}

export default compose(memo, connect(mapStateToProps, mapDispatchToProps), withAutoSave())(NoteForm);
