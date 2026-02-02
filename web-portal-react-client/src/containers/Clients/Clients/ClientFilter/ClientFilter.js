import React, { useCallback, useEffect, useMemo, useState } from "react";

import cn from "classnames";

import { findWhere, map, uniq, without } from "underscore";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { useQueryClient } from "@tanstack/react-query";

import { Button, Col, Row } from "reactstrap";

import { useDirectoryData } from "hooks/common";

import { useCustomFilter } from "hooks/common/redux";

import { useFilter } from "hooks/common/filter";

import { useClientPharmacyNamesQuery, useClientStatusesQuery } from "hooks/business/directory/query";

import { useGendersQuery, useInsuranceNetworkAggregatedNamesQuery } from "hooks/business/directory";

import { CheckboxField, DateField, SelectField, TextField } from "components/Form";

import clientListActions from "redux/client/list/clientListActions";

import { CLIENT_STATUSES } from "lib/Constants";
import { allAreNotEmpty, DateUtils as DU } from "lib/utils/Utils";
import { getDateTime } from "lib/utils/DateUtils";

import { NAME as PRIMARY_FILTER_NAME } from "../ClientPrimaryFilter/ClientPrimaryFilter";

import "./ClientFilter.scss";

const { ACTIVE, PENDING, DECLINED } = CLIENT_STATUSES;

const { format, formats } = DU;
const DATE_FORMAT = formats.americanMediumDate;

export const NAME = "CLIENT_FILTER";

const DEFAULT_DATA = {
  unit: null,
  ssnLast4: null,
  genderId: null,
  lastName: null,
  firstName: null,
  birthDate: null,
  medicareNumber: null,
  medicaidNumber: null,
  recordStatuses: [ACTIVE],
  pharmacyNames: [],
  isAdmitted: false,
  hasNoPharmacies: false,
  primaryCarePhysician: null,
  insuranceNetworkAggregatedName: null,
};

const NO_PHARMACY_ASSIGNED = "@@NO_PHARMACY_ASSIGNED";

function valueTextMapper({ id, name, title, label }) {
  return { value: id ?? name, text: title ?? label ?? name };
}

function mapStateToProps(state) {
  const { list } = state.client;

  return {
    isFetching: list.isFetching,
    fetchCount: list.fetchCount,
    fields: list.dataSource.filter,
    isChanged: list.isFilterChanged(),
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: bindActionCreators(clientListActions, dispatch),
  };
}

function ClientFilter({ fields, isChanged, actions, className, isProfessionalRole }) {
  const { communityIds, organizationId } = fields;

  const [defaultData, setDefaultData] = useState(DEFAULT_DATA);

  const [networkSearchText, setNetworkSearchText] = useState("");

  const { save: savePrimary } = useFilter(PRIMARY_FILTER_NAME);

  const queryClient = useQueryClient();

  function updateDefaultData(data) {
    setDefaultData((s) => ({ ...s, ...data }));
  }

  const { blur, focus, reset, apply, change, changeField } = useCustomFilter(NAME, fields, actions, {
    isChanged,
    defaultData,
    onApplied: () => {
      savePrimary({ organizationId, communityIds });
    },
    onRestored: (data) => {
      const name = data.insuranceNetworkAggregatedName;
      name && setNetworkSearchText(name);
    },
  });

  useGendersQuery();

  const { data: statuses } = useClientStatusesQuery(
    { organizationId },
    {
      onSuccess: (data) => {
        const status = findWhere(data, { name: ACTIVE });
        updateDefaultData({ recordStatuses: [status.name] });
      },
    },
  );

  useInsuranceNetworkAggregatedNamesQuery({
    organizationId,
    text: networkSearchText,
  });

  const { data: pharmacyNames } = useClientPharmacyNamesQuery(
    {
      organizationId,
      communityIds,
    },
    {
      enabled: allAreNotEmpty(organizationId, communityIds),
    },
  );

  const { genders, networks } = useDirectoryData({
    genders: ["gender"],
    networks: ["insurance", "network", "aggregated"],
  });

  const mappedGenders = useMemo(() => map(genders, valueTextMapper), [genders]);

  const filteredStatuses = useMemo(
    () => statuses?.filter((s) => (isProfessionalRole ? true : ![PENDING, DECLINED].includes(s.name))),
    [statuses, isProfessionalRole],
  );

  const mappedStatuses = useMemo(() => map(filteredStatuses, valueTextMapper), [filteredStatuses]);

  const mappedNetworks = useMemo(() => map(uniq(networks), (o) => ({ value: o, text: o })), [networks]);

  const pharmacyFieldValue = useMemo(() => {
    return [...fields.pharmacyNames, ...(fields.hasNoPharmacies ? [NO_PHARMACY_ASSIGNED] : [])];
  }, [fields]);

  const mappedPharmacies = useMemo(
    () => [
      {
        value: NO_PHARMACY_ASSIGNED,
        text: "No pharmacy assigned",
      },
      ...map(pharmacyNames, (o) => ({ value: o, text: o })),
    ],
    [pharmacyNames],
  );

  const onChangeField = useCallback(
    (name, value) => {
      changeField(name, value, false);
    },
    [changeField],
  );

  const onChangeDateField = useCallback(
    (name, value) => {
      changeField(name, value ? format(value, DATE_FORMAT) : null, false);
    },
    [changeField],
  );

  const onChangePharmacyField = useCallback(
    (name, value) => {
      change(
        value.includes(NO_PHARMACY_ASSIGNED)
          ? {
              [name]: without(value, NO_PHARMACY_ASSIGNED),
              hasNoPharmacies: true,
            }
          : {
              [name]: value,
              hasNoPharmacies: false,
            },
        false,
        false,
      );
    },
    [change],
  );

  const onClearPharmacyFieldSearchText = useCallback(() => {
    changeField("hasNoPharmacies", false, false, false);
  }, [changeField]);

  const onChangeNetworkSearchText = useCallback((name, value) => {
    setNetworkSearchText(value);
  }, []);

  const onClearNetworkSearchText = useCallback(() => {
    setNetworkSearchText("");
  }, []);

  const onReset = useCallback(() => {
    reset();

    if (fields.insuranceNetworkAggregatedName) {
      setNetworkSearchText("");
    }
  }, [reset, fields]);

  useEffect(
    () => () => {
      queryClient.invalidateQueries("ClientPharmacyNames");
    },
    [queryClient],
  );

  return (
    <div className={cn("ClientFilter", className)}>
      <Row>
        <Col lg={6}>
          <Row>
            <Col lg={6} md={6}>
              <TextField
                type="text"
                name="firstName"
                value={fields.firstName}
                label="First Name"
                className="ClientFilter-TextField"
                onBlur={blur}
                onFocus={focus}
                onChange={onChangeField}
              />
            </Col>
            <Col lg={6} md={6}>
              <TextField
                type="text"
                name="lastName"
                value={fields.lastName}
                label="Last Name"
                className="ClientFilter-TextField"
                onBlur={blur}
                onFocus={focus}
                onChange={onChangeField}
              />
            </Col>
          </Row>
        </Col>
        <Col lg={6}>
          <Row>
            <Col lg={4} md={4}>
              <SelectField
                name="genderId"
                value={fields.genderId}
                options={mappedGenders}
                label="Gender"
                placeholder="Gender"
                className="ClientFilter-SelectField"
                isMultiple={false}
                onChange={onChangeField}
              />
            </Col>
            <Col lg={4} md={4}>
              <DateField
                name="birthDate"
                value={fields.birthDate ? getDateTime(fields.birthDate) : null}
                dateFormat="MM/dd/yyyy"
                label="Date of Birth"
                placeholder="Select date"
                className="ClientFilter-DateField"
                onChange={onChangeDateField}
              />
            </Col>
            <Col lg={4} md={4}>
              <TextField
                type="text"
                name="ssnLast4"
                value={fields.ssnLast4}
                label="SSN"
                placeholder="Last 4 digits"
                className="ClientFilter-TextField"
                onBlur={blur}
                onFocus={focus}
                onChange={onChangeField}
              />
            </Col>
          </Row>
        </Col>
      </Row>
      <Row>
        <Col md={6} lg={3}>
          <SelectField
            hasAllOption
            name="recordStatuses"
            options={mappedStatuses}
            value={fields.recordStatuses}
            label="Record Status"
            placeholder={"Record status"}
            className="ClientFilter-SelectField"
            isMultiple={true}
            onChange={onChangeField}
          />
        </Col>
        <Col md={6} lg={3}>
          <TextField
            type="text"
            name="primaryCarePhysician"
            value={fields.primaryCarePhysician}
            label="Primary Care Physician"
            placeholder="Primary Care Physician"
            className="ClientFilter-TextField"
            onBlur={blur}
            onFocus={focus}
            onChange={onChangeField}
          />
        </Col>
        <Col md={12} lg={2}>
          <SelectField
            label="Insurance Network"
            name="insuranceNetworkAggregatedName"
            value={fields.insuranceNetworkAggregatedName}
            options={mappedNetworks}
            hasSearchBox
            isMultiple={false}
            placeholder="Insurance Network"
            className="ClientFilter-SelectField"
            onChange={onChangeField}
            onClearSearchText={onClearNetworkSearchText}
            onChangeSearchText={onChangeNetworkSearchText}
          />
        </Col>
        <Col md={12} lg={2}>
          <TextField
            type="text"
            name="unit"
            value={fields.unit}
            label="Unit #"
            placeholder="Unit #"
            className="ClientFilter-TextField"
            onBlur={blur}
            onFocus={focus}
            onChange={onChangeField}
          />
        </Col>
        <Col md={12} lg={2}>
          <CheckboxField
            label="Currently admitted"
            name="isAdmitted"
            value={fields.isAdmitted}
            tooltip={{
              target: "isAdmitted",
              render: () => (
                <div className="ClientFilter-CheckboxFieldTooltip">
                  If the filter is enabled, you’ll see only active records that:
                  <ul className="padding-left-20">
                    <li>Have no discharge dates OR</li>
                    <li>
                      Have both discharge dates and readmit dates and these readmit dates are after the discharge dates.
                    </li>
                  </ul>
                </div>
              ),
            }}
            className="ClientFilter-CheckboxField"
            onChange={onChangeField}
          />
        </Col>
      </Row>
      <Row>
        <Col md={12} lg={3}>
          <SelectField
            label="Pharmacy"
            name="pharmacyNames"
            value={pharmacyFieldValue}
            options={mappedPharmacies}
            isMultiple
            hasSearchBox
            hasTags={false}
            hasAllOption={false}
            placeholder="Pharmacy"
            className="ClientFilter-SelectField"
            onChange={onChangePharmacyField}
            onClearSearchText={onClearPharmacyFieldSearchText}
          />
        </Col>
        <Col md={6} lg={3}>
          <TextField
            type="text"
            maxLength={50}
            label="Medicaid #"
            name="medicaidNumber"
            placeholder="Medicaid #"
            value={fields.medicaidNumber}
            className="ClientFilter-TextField"
            onBlur={blur}
            onFocus={focus}
            onChange={onChangeField}
          />
        </Col>
        <Col md={6} lg={2}>
          <TextField
            type="text"
            maxLength={50}
            label="Medicare #"
            name="medicareNumber"
            placeholder="Medicare #"
            value={fields.medicareNumber}
            className="ClientFilter-TextField"
            onBlur={blur}
            onFocus={focus}
            onChange={onChangeField}
          />
        </Col>
        <Col md={12} lg={4} className="padding-top-31">
          <Button outline color="success" data-testid="clear-btn" className="margin-right-25" onClick={onReset}>
            Clear
          </Button>
          <Button color="success" data-testid="apply-btn" onClick={apply}>
            Apply
          </Button>
        </Col>
      </Row>
    </div>
  );
}

export default connect(mapStateToProps, mapDispatchToProps)(ClientFilter);
