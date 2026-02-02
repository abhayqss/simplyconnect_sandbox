import React, { memo, useMemo, useState, useEffect, useCallback } from "react";

import { Col, Row, Form, Button } from "reactstrap";

import { map, filter, sortBy, groupBy } from "underscore";

import { Loader, ErrorViewer } from "components";

import { TextField, DateField, PhoneField, SelectField } from "components/Form";

import { useForm, useScrollable, useScrollToFormError, useCustomFormFieldChange } from "hooks/common";

import { useServicesQuery } from "hooks/business/directory/query";

import { useInquirySubmit } from "hooks/business/Marketplace/Public";

import { ReactComponent as Info } from "images/info.svg";

import Inquiry from "entities/Inquiry";
import InquiryFormValidator from "validators/InquiryFormValidator";

import "./InquiryForm.scss";

const scrollableStyles = { flex: 1 };

function InquiryForm({ communityId, serviceCategories, onCancel, onSubmitSuccess }) {
  const [error, setError] = useState(false);
  const [isFetching, setFetching] = useState(false);
  const [isValidationNeeded, setNeedValidation] = useState(false);

  const { fields, errors, isValid, validate, isChanged, changeField, changeFields } = useForm(
    "InquiryForm",
    Inquiry,
    InquiryFormValidator,
  );

  const { changeSelectField } = useCustomFormFieldChange(changeField);

  const { Scrollable, scroll } = useScrollable();

  const scrollToError = useScrollToFormError(".InquiryForm", scroll);

  const { data: services, isFetching: isFetchingServices } = useServicesQuery({ isAuthorizedAccess: false });

  const filteredServices = useMemo(
    () => filter(services, (s) => map(serviceCategories, (c) => c.id).includes(s.serviceCategoryId)),
    [services, serviceCategories],
  );

  const serviceSections = useMemo(() => {
    return sortBy(
      map(groupBy(filteredServices, "serviceCategoryId"), (data, id) => ({
        id: +id,
        title: data[0].serviceCategoryTitle,
        options: map(data, (o) => ({
          value: o.id,
          text: o.title,
        })),
      })),
      "title",
    );
  }, [filteredServices]);

  const { mutateAsync: submit } = useInquirySubmit(
    {},
    {
      onError: setError,
      onSuccess: onSubmitSuccess,
    },
  );

  const validationOptions = useMemo(() => {
    return {
      included: {
        email: fields.email,
        phone: fields.phone,
      },
    };
  }, [fields.email, fields.phone]);

  const validateIf = () => {
    if (isValidationNeeded) {
      validate(validationOptions)
        .then(() => setNeedValidation(false))
        .catch(() => setNeedValidation(true));
    }
  };

  const tryToSubmit = useCallback(() => {
    setFetching(true);

    validate(validationOptions)
      .then()
      .then(async () => {
        await submit({
          ...fields.toJS(),
        });

        setNeedValidation(false);
      })
      .catch(() => {
        scrollToError();
        setNeedValidation(true);
      })
      .finally(() => {
        setFetching(false);
      });
  }, [fields, submit, validate, scrollToError, validationOptions]);

  const onSubmit = useCallback(
    (e) => {
      e.preventDefault();
      tryToSubmit();
    },
    [tryToSubmit],
  );

  const setDefaultData = () => {
    changeFields(
      {
        communityId,
        createdDate: new Date().getTime(),
      },
      true,
    );
  };

  const cancel = () => {
    onCancel(isChanged);
  };

  useEffect(validateIf, [isValidationNeeded, validationOptions, validate]);
  useEffect(setDefaultData, [communityId, changeFields]);

  return (
    <>
      <Form className="MarketplaceInquiryForm" onSubmit={onSubmit}>
        {(isFetching || isFetchingServices) && <Loader style={{ position: "fixed" }} hasBackdrop />}

        <Scrollable style={scrollableStyles} className="MarketplaceInquiryForm-Sections">
          <div className="MarketplaceInquiryForm-Section">
            <div className="MarketplaceInquiryForm-SectionTitle">Inquiry</div>
            <Row>
              <Col md={6}>
                <DateField
                  name="createdDate"
                  isDisabled
                  value={fields.createdDate}
                  timeFormat="hh:mm aa"
                  dateFormat="MM/dd/yyyy hh:mm a"
                  label="Inquiry Date"
                  errorText={errors.createdDate}
                  className="MarketplaceInquiryForm-DateField"
                />
              </Col>
            </Row>
            <Row>
              <Col md={12}>
                <SelectField
                  type="text"
                  name="serviceId"
                  isSectioned
                  hasSearchBox
                  hasValueTooltip
                  value={fields.serviceId}
                  hasSectionTitle
                  hasSectionSeparator
                  label="Service"
                  placeholder="Select"
                  errorText={errors.serviceId}
                  sections={serviceSections}
                  onChange={changeSelectField}
                  className="MarketplaceInquiryForm-SelectField"
                />
              </Col>
            </Row>
          </div>

          <div className="MarketplaceInquiryForm-Section">
            <div className="MarketplaceInquiryForm-SectionTitle">Client</div>
            <Row>
              <Col md={6}>
                <TextField
                  type="text"
                  name="firstName"
                  value={fields.firstName}
                  maxLength={256}
                  label="First Name*"
                  className="MarketplaceInquiryForm-TextField"
                  errorText={errors.firstName}
                  onChange={changeField}
                />
              </Col>
              <Col md={6}>
                <TextField
                  type="text"
                  name="lastName"
                  value={fields.lastName}
                  maxLength={256}
                  label="Last Name*"
                  className="MarketplaceInquiryForm-TextField"
                  errorText={errors.lastName}
                  onChange={changeField}
                />
              </Col>
            </Row>

            <Row>
              <Col md={6}>
                <PhoneField
                  name="phone"
                  value={fields.phone}
                  label={"Phone #" + (!fields.email ? "*" : "")}
                  className="MarketplaceInquiryForm-PhoneField"
                  errorText={errors.phone}
                  onChange={changeField}
                  renderLabelIcon={() => <Info id="phone-hint" className="MarketplaceInquiryForm-LabelIcon" />}
                  tooltip={{
                    target: "phone-hint",
                    render: () => (
                      <ul className="MarketplaceInquiryForm-PhoneTooltipBody">
                        <li>Use digits only and "+" before country code.</li>
                        <li>Otherwise no spaces, dashes, or special symbols allowed.</li>
                      </ul>
                    ),
                  }}
                />
              </Col>
              <Col md={6}>
                <TextField
                  type="text"
                  name="email"
                  value={fields.email}
                  maxLength={256}
                  label={"Email" + (!fields.phone ? "*" : "")}
                  className="MarketplaceInquiryForm-TextField"
                  errorText={errors.email}
                  onChange={changeField}
                />
              </Col>
            </Row>

            <Row>
              <Col>
                <TextField
                  type="textarea"
                  name="notes"
                  value={fields.notes}
                  maxLength={256}
                  numberOfRows={5}
                  label="Notes"
                  className="MarketplaceInquiryForm-TextField"
                  errorText={errors.instructions}
                  onChange={changeField}
                />
              </Col>
            </Row>
          </div>
        </Scrollable>

        <div className="MarketplaceInquiryForm-Buttons">
          <Button outline color="success" onClick={cancel}>
            Cancel
          </Button>

          <Button color="success" disabled={isFetching || !isValid}>
            Submit
          </Button>
        </div>
      </Form>

      {error && <ErrorViewer isOpen error={error} onClose={() => setError(null)} />}
    </>
  );
}

export default memo(InquiryForm);
