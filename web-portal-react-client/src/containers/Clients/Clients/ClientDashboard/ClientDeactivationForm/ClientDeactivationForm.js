import React, { memo, useMemo, useState, useEffect } from "react";

import { Col, Row, Form, Button } from "reactstrap";

import { Loader, ErrorViewer } from "components";

import { DateField } from "components/Form";

import { TextField, SelectField } from "components/Form";

import { useForm, useScrollable, useScrollToFormError, useCustomFormFieldChange } from "hooks/common";

import { useClientDeactivation } from "hooks/business/client";

import { useClientDeactivationReasonsQuery } from "hooks/business/directory/query";

import ClientDeactivation from "entities/ClientDeactivation";
import ClientDeactivationFormValidator from "validators/ClientDeactivationFormValidator";

import { map } from "lib/utils/ArrayUtils";

import "./ClientDeactivationForm.scss";

const TODAY = Date.now();

const scrollableStyles = { flex: 1 };

function valueTextMapper({ id, name, title, label }) {
  return { value: id || name, text: title || label || name };
}

function ClientDeactivationForm({ clientId, onCancel, onSubmitSuccess }) {
  const [error, setError] = useState(false);
  const [isFetching, setFetching] = useState(false);
  const [isValidationNeeded, setNeedValidation] = useState(false);

  const { fields, errors, isValid, validate, isChanged, changeField, changeDateField } = useForm(
    "ClientDeactivationForm",
    ClientDeactivation,
    ClientDeactivationFormValidator,
  );

  const data = useMemo(() => fields.toJS(), [fields]);

  const { changeSelectField } = useCustomFormFieldChange(changeField);

  const { data: reasons, isFetching: isFetchingReasons } = useClientDeactivationReasonsQuery();

  const mappedReasons = useMemo(() => map(reasons, valueTextMapper), [reasons]);

  const { Scrollable, scroll } = useScrollable();

  const scrollToError = useScrollToFormError(".ClientDeactivationForm", scroll);

  const { mutateAsync: deactivate } = useClientDeactivation(
    {
      ...data,
      clientId,
    },
    {
      onError: setError,
      onSuccess: ({ data }) => {
        onSubmitSuccess(data);
      },
    },
  );

  function cancel() {
    onCancel(isChanged);
  }

  function validateIf() {
    if (isValidationNeeded) {
      validate()
        .then(() => setNeedValidation(false))
        .catch(() => setNeedValidation(true));
    }
  }

  function tryToSubmit(e) {
    e.preventDefault();

    setFetching(true);

    validate()
      .then()
      .then(async () => {
        await deactivate();
        setNeedValidation(false);
      })
      .catch(() => {
        scrollToError();
        setNeedValidation(true);
      })
      .finally(() => {
        setFetching(false);
      });
  }

  useEffect(validateIf, [isValidationNeeded, scrollToError, validate]);

  return (
    <>
      <Form className="ClientDeactivationForm" onSubmit={tryToSubmit}>
        {(isFetching || isFetchingReasons) && <Loader hasBackdrop />}

        <Scrollable style={scrollableStyles} className="ClientDeactivationForm-Sections">
          <div className="ClientDeactivationForm-Section">
            <Row>
              <Col md={4}>
                <DateField
                  name="exitDate"
                  value={fields.exitDate}
                  dateFormat="MM/dd/yyyy"
                  maxDate={TODAY}
                  label="Exit date*"
                  placeholder="Select date"
                  onChange={changeDateField}
                  errorText={errors.exitDate}
                  className="ClientDeactivationForm-DateField"
                />
              </Col>
              <Col md={4}>
                <SelectField
                  name="deactivationReason"
                  value={fields.deactivationReason}
                  options={mappedReasons}
                  label="Reason"
                  className="ClientDeactivationForm-SelectField"
                  errorText={errors.deactivationReason}
                  onChange={changeSelectField}
                />
              </Col>
            </Row>

            <Row>
              <Col md={12}>
                <TextField
                  type="textarea"
                  name="comment"
                  value={fields.comment}
                  label="Comment"
                  numberOfRows={10}
                  className="ClientDeactivationForm-TextField"
                  errorText={errors.comment}
                  maxLength={5000}
                  onChange={changeField}
                />
              </Col>
            </Row>
          </div>
        </Scrollable>

        <div className="ClientDeactivationForm-Buttons">
          <Button outline color="success" onClick={cancel}>
            Close
          </Button>

          <Button color="success" disabled={isFetching || !isValid}>
            Save
          </Button>
        </div>
      </Form>

      {error && <ErrorViewer isOpen error={error} onClose={() => setError(null)} />}
    </>
  );
}

export default memo(ClientDeactivationForm);
