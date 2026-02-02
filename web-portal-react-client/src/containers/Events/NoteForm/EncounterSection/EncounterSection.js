import React, { memo, useMemo, useState, useEffect, useCallback } from "react";

import { compact } from "underscore";

import moment from "moment";

import { Row, Col } from "reactstrap";

import { TextField, DateField, SelectField } from "components/Form";

import { useMutationWatch, useSelectOptions, useDirectoryData } from "hooks/common";

import { useAuthUser } from "hooks/common/redux";

import { useNoteContactsQuery } from "hooks/business/event";

import { isEmpty, isInteger, isNotEmpty, anyIsEmpty, anyIsInteger, allAreNotEmpty } from "lib/utils/Utils";

import { setTime, moveTime } from "lib/utils/DateUtils";

import { addAsterix } from "lib/utils/StringUtils";

import calcTotalTimeRangeUnits from "../../calcTotalTimeRangeUnits";

const HALF_HOUR = 30;
const QUARTER_HOUR = 15;
const FACE_TO_FACE_ENCOUNTER = 19;
const NON_FACE_TO_FACE_ENCOUNTER = 20;

const isSameDay = (first, second) => moment(first).isSame(moment(second), "day");

function EncounterSection({
  state,
  errors = {},

  noteId,
  clientId,
  noteTypeId,

  onChangeField,
  onChangeFields,
}) {
  const user = useAuthUser();

  const isEditing = isInteger(noteId);
  const isClient = isInteger(clientId);

  const [isDefaultClinician, setIsDefaultClinician] = useState(!isEditing);

  const { typeId, toDate, fromDate, clinicianId, otherClinician, toTime } = state;

  const isEncounterNoteType = [FACE_TO_FACE_ENCOUNTER, NON_FACE_TO_FACE_ENCOUNTER].includes(noteTypeId);

  const isSameDaysSelected = isSameDay(fromDate, toDate);

  const { encounterTypes } = useDirectoryData({
    encounterTypes: ["note", "encounter", "type"],
  });

  const { data: contacts = [] } = useNoteContactsQuery(
    isClient ? { clientId } : { organizationId: user?.organizationId },
    {
      staleTime: 0,
      enabled: anyIsInteger(clientId, user?.organizationId),
    },
  );

  const { units, totalTime, range } = useMemo(() => {
    if (toTime) {
      return calcTotalTimeRangeUnits(fromDate, toDate);
    } else {
      return { units: null, totalTime: null, range: null };
    }
  }, [fromDate, toDate, toTime]);

  function filterEncounterTypes(noteTypeId) {
    // 复制原始遭遇类型数据
    let filteredTypes = [...encounterTypes];

    // 根据 noteTypeId 进行过滤
    if (noteTypeId === 19) {
      // 过滤掉 name 为 "NON_FACE_TO_FACE_VISIT_ATTEMPTED" 的遭遇类型
      filteredTypes = filteredTypes.filter((type) => type.name !== "NON_FACE_TO_FACE_VISIT_ATTEMPTED");
    } else if (noteTypeId === 20) {
      // 过滤掉 title 为 "FACE_TO_FACE_VISIT_ATTEMPTED" 的遭遇类型
      filteredTypes = filteredTypes.filter((type) => type.name !== "FACE_TO_FACE_VISIT_ATTEMPTED");
    }

    return filteredTypes;
  }

  const encounterTypeOptions = useSelectOptions(filterEncounterTypes(noteTypeId));

  const mappedContacts = useMemo(() => {
    return compact([...contacts, { id: -1, name: "Other" }]).map(({ id, name } = {}) => ({
      text: name,
      value: id,
    }));
  }, [contacts]);

  const onChangeDateField = useCallback(
    (name, dateValue) => {
      let value = dateValue?.getTime() || null;

      if (isNotEmpty(value) && name === "toDate") {
        if (value < fromDate) value = moveTime(fromDate).valueOf();
        else value = setTime(value, moveTime(fromDate)).valueOf();
      } else if (allAreNotEmpty(value, toDate) && value >= toDate) {
        onChangeField("toDate", moveTime(value).valueOf());
      }

      onChangeField(name, value);
    },
    [toDate, fromDate, onChangeField],
  );

  const onChangeTimeFromField = useCallback(
    (name, date) => {
      let value = date ? setTime(fromDate, date.getTime()).valueOf() : null;

      if (allAreNotEmpty(value, toDate) && value >= toDate) {
        onChangeField("toDate", moveTime(value).valueOf());
      }

      onChangeField(name, value);
    },
    [toDate, fromDate, onChangeField],
  );

  const onChangeTimeToField = useCallback(
    (name, date) => {
      let value = date ? setTime(toDate, date.getTime()).valueOf() : null;

      if (isNotEmpty(value) && value <= fromDate) {
        value = moveTime(fromDate).valueOf();
      }
      onChangeField("toDate", value);
      onChangeField("toTime", value);
      // setToTime(value);
    },
    [toDate, fromDate, toTime, onChangeField],
  );

  useEffect(() => {
    if (!isEditing) {
      onChangeField("clinicianId", user?.id);
    }
  }, [user, isEditing, onChangeField]);

  useEffect(() => {
    if (isNotEmpty(otherClinician)) {
      onChangeField("clinicianId", -1);
    }
  }, [otherClinician, contacts, onChangeField]);

  useEffect(() => {
    if (isDefaultClinician && isNotEmpty(contacts)) {
      setIsDefaultClinician(false);
      onChangeField("clinicianId", user?.id);
    }
  }, [user, contacts, isDefaultClinician, onChangeField]);

  useMutationWatch(isEncounterNoteType, () => {
    if (isEncounterNoteType) {
      let minutes = moment().minutes();
      const remainder = minutes % HALF_HOUR;

      if (remainder) {
        minutes = minutes < HALF_HOUR ? HALF_HOUR : HALF_HOUR * 2;
      }

      onChangeField("fromDate", moment().clone().set("minutes", minutes).startOf("minute").valueOf());
    } else {
      onChangeField("fromDate", moment().clone().valueOf());
      onChangeField("toDate", null);
    }
  });

  useEffect(() => {
    if (anyIsEmpty(fromDate, toDate)) {
      let minutes = moment().minutes();
      const remainder = minutes % HALF_HOUR;

      if (remainder) {
        minutes = minutes < HALF_HOUR ? HALF_HOUR : HALF_HOUR * 2;
      }

      if (isEmpty(fromDate)) {
        const date = moment()
          .clone()
          .set("minutes", isEncounterNoteType ? minutes : moment().minutes())
          .startOf("minute")
          .valueOf();

        onChangeField("fromDate", date);
      }

      if (isEncounterNoteType && isEmpty(toDate)) {
        const date = moment()
          .clone()
          .set("minutes", minutes + QUARTER_HOUR)
          .startOf("minute")
          .valueOf();

        onChangeField("toDate", date);
      }
    }
  }, [toDate, fromDate, onChangeField, onChangeFields, isEncounterNoteType]);

  return (
    <>
      <Row>
        {isEncounterNoteType && (
          <Col md={clinicianId < 0 ? 4 : 6}>
            <SelectField
              type="text"
              name="typeId"
              value={typeId}
              options={encounterTypeOptions}
              label={addAsterix("Encounter type").if(isEncounterNoteType)}
              className="NoteForm-SelectField"
              errorText={errors.typeId}
              onChange={onChangeField}
            />
          </Col>
        )}
        <Col md={clinicianId < 0 ? 4 : 6}>
          <SelectField
            label={addAsterix("Person Completing the Encounter").if(isEncounterNoteType)}
            name="clinicianId"
            value={clinicianId}
            options={mappedContacts}
            hasSearchBox
            isMultiple={false}
            placeholder="Type the name of the contact"
            className="NoteForm-SelectField"
            errorText={errors.clinicianId}
            onChange={onChangeField}
          />
        </Col>
        {clinicianId < 0 && (
          <Col md={4}>
            <TextField
              type="text"
              name="otherClinician"
              value={otherClinician}
              label="Other Person*"
              className="NoteForm-TextField"
              maxLength={256}
              errorText={errors.otherClinician}
              onChange={onChangeField}
            />
          </Col>
        )}
      </Row>

      <Row>
        <Col md={3}>
          <DateField
            name="fromDate"
            value={fromDate}
            label={addAsterix("Encounter Date From").if(isEncounterNoteType)}
            className="NoteForm-DateField"
            errorText={errors.fromDate}
            onChange={onChangeDateField}
          />
        </Col>

        <Col md={3}>
          <DateField
            name="fromDate"
            value={fromDate}
            testId="fromTime"
            label={addAsterix("Time From").if(isEncounterNoteType)}
            className="NoteForm-DateField"
            dateFormat="h:mm aa"
            timeFormat="h:mm aa"
            hasTimeSelect
            hasTimeSelectOnly
            errorText={errors.fromDate}
            onChange={onChangeTimeFromField}
          />
        </Col>

        <Col md={3}>
          <DateField
            name="toDate"
            value={toDate}
            label={addAsterix("Encounter Date To").if(isEncounterNoteType)}
            minDate={fromDate}
            className="NoteForm-DateField"
            errorText={!isSameDaysSelected ? errors.toDate : ""}
            onChange={onChangeDateField}
          />
        </Col>

        <Col md={3}>
          <DateField
            name="toTime"
            value={toTime}
            testId="toTime"
            label={addAsterix("Time To").if(isEncounterNoteType)}
            className="NoteForm-DateField"
            dateFormat="h:mm aa"
            timeFormat="h:mm aa"
            minTime={isSameDaysSelected ? moveTime(fromDate).valueOf() : undefined}
            hasTimeSelect
            hasTimeSelectOnly
            errorText={errors.toTime}
            onChange={onChangeTimeToField}
          />
        </Col>
      </Row>

      <Row>
        <Col md={3}>
          <TextField
            type="text"
            name="totalTime"
            value={totalTime}
            isDisabled
            label={addAsterix("Total time spent").if(isEncounterNoteType)}
            className="NoteForm-TextField"
          />
        </Col>
        <Col md={3}>
          <TextField
            type="text"
            name="range"
            value={range}
            label={addAsterix("Range").if(isEncounterNoteType)}
            isDisabled
            className="NoteForm-TextField"
          />
        </Col>
        <Col md={3}>
          <TextField
            type="text"
            name="units"
            value={units}
            isDisabled
            label={addAsterix("Units").if(isEncounterNoteType)}
            className="NoteForm-TextField"
          />
        </Col>
      </Row>
    </>
  );
}

export default memo(EncounterSection);
