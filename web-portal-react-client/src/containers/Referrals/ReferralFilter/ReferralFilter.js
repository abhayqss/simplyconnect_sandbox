import React, { useMemo, useState, useCallback } from "react";

import cn from "classnames";
import PTypes from "prop-types";

import { map, reject, sortBy, groupBy, compact } from "underscore";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { useParams } from "react-router-dom";

import { Row, Col, Button } from "reactstrap";

import { useMutationWatch, useDirectoryData } from "hooks/common";

import { useFilter } from "hooks/common/filter";
import { useAuthUser, useCustomFilter } from "hooks/common/redux";

import {
  usePrimaryFocusQuery,
  useReferralStatusesQuery,
  useTreatmentServicesQuery,
  useReferralPrioritiesQuery,
} from "hooks/business/directory";

import { useRecipientsQuery, useRequestSendersQuery } from "hooks/business/admin/referrals";

import { SelectField } from "components/Form";

import referralListActions from "redux/referral/list/referralListActions";

import { isInteger, isNotEmpty, allAreInteger } from "lib/utils/Utils";

import { REFERRAL_TYPES } from "lib/Constants";

import { NAME as PRIMARY_FILTER_NAME } from "../ReferralPrimaryFilter/ReferralPrimaryFilter";

import "./ReferralFilter.scss";

export const NAME = "REFERRAL_FILTER";

const NONE = "NONE";

const { INBOUND, OUTBOUND } = REFERRAL_TYPES;

function mapToIds(data) {
  return map(data, (o) => o.id);
}

function mapToNames(data) {
  return map(data, (o) => o.name);
}

function getName(prefixes = []) {
  return cn(prefixes, NAME).replace(/\s/g, "_");
}

function mapToValueText(data) {
  return map(data, ({ id, name, title }) => ({
    value: id || name,
    text: title,
  }));
}

function mapStateToProps(state) {
  const { list, recipient, request, community } = state.referral;

  return {
    isFetching: list.isFetching,
    fields: list.dataSource.filter,
    isChanged: list.isFilterChanged(),

    request,
    recipient,

    community,
    auth: state.auth,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: bindActionCreators(referralListActions, dispatch),
  };
}

function ReferralFilter({
  fields,
  actions,
  isChanged,

  type,
  recipient,
  request,
  className,

  onApply,
}) {
  const { communityIds } = fields;

  const user = useAuthUser();

  let { clientId } = useParams();

  clientId = parseInt(clientId);

  const isClient = isInteger(clientId);
  const name = getName([{ CLIENT: isClient }, type]);
  const organizationId = (isClient ? user : fields)?.organizationId;

  const { services, statuses, priorities } = useDirectoryData({
    services: ["treatment", "service"],
    statuses: ["referral", "status"],
    priorities: ["referral", "priority"],
  });

  const referents = recipient.list.dataSource.data;

  const senders = request.sender.list.dataSource.data;

  const [defaultData, setDefaultData] = useState({});

  function updateDefaultData(data) {
    setDefaultData((s) => ({ ...s, ...data }));
  }

  const { save: savePrimary } = useFilter(PRIMARY_FILTER_NAME);

  const { reset, apply, change, isSaved, changeField } = useCustomFilter(name, fields, actions, {
    isChanged,
    defaultData,
    onApplied: () => {
      savePrimary({ organizationId, communityIds });
    },
  });

  const data = fields.toJS();

  const filterStatuses = useCallback(
    (data) => reject(data, (o) => (type === INBOUND ? o.name === "CANCELED" : false)),
    [type],
  );

  usePrimaryFocusQuery({ organizationId });

  useTreatmentServicesQuery(
    { organizationId },
    {
      onSuccess: ({ data }) => {
        updateDefaultData({
          serviceIds: mapToIds(data),
          includeWithEmptyService: true,
        });

        if (!isSaved()) {
          changeField("serviceIds", mapToIds(data), false, true);
          changeField("includeWithEmptyService", true, false, true);
        }
      },
    },
  );

  useReferralStatusesQuery(
    { type },
    {
      condition: (prevParams) => type !== prevParams.type,
      onSuccess: ({ data }) => {
        const statuses = filterStatuses(data);
        updateDefaultData({ statuses: mapToNames(statuses) });
        !isSaved() && changeField("statuses", mapToNames(statuses), false, true);
      },
    },
  );

  useReferralPrioritiesQuery(
    { type },
    {
      condition: (prevParams) => type !== prevParams.type,
      onSuccess: ({ data }) => {
        updateDefaultData({
          priorityIds: mapToIds(data),
          includeWithEmptyPriority: true,
        });

        if (!isSaved()) {
          changeField("priorityIds", mapToIds(data), false, true);
          changeField("includeWithEmptyPriority", true, false, true);
        }
      },
    },
  );

  const primary = { communityIds, organizationId };

  useRecipientsQuery(primary, {
    onSuccess: ({ data }) => {
      updateDefaultData({ referredTo: mapToIds(data) });
      !isSaved() && changeField("referredTo", mapToIds(data), false, true);
    },
  });

  useRequestSendersQuery(primary, {
    onSuccess: ({ data }) => {
      updateDefaultData({
        referredBy: mapToIds(data),
        includeWithEmptyReferredBy: true,
      });

      if (!isSaved()) {
        changeField("referredBy", mapToIds(data), false, true);
        changeField("includeWithEmptyReferredBy", true, false, true);
      }
    },
  });

  const mappedServices = useMemo(() => {
    return [
      {
        id: NONE,
        hasTitle: false,
        options: [{ text: "None", value: NONE }],
      },
      ...sortBy(
        map(groupBy(services, "serviceCategoryId"), (data, id) => ({
          id: +id,
          title: data[0].serviceCategoryTitle,
          options: map(data, (o) => ({
            value: o.id,
            text: o.title,
          })),
        })),
        "title",
      ),
    ];
  }, [services]);

  const mappedPriorities = useMemo(() => [{ text: "None", value: NONE }, ...mapToValueText(priorities)], [priorities]);

  const mappedStatuses = useMemo(() => mapToValueText(filterStatuses(statuses)), [statuses]);

  const mappedReferents = useMemo(() => mapToValueText(referents), [referents]);

  const mappedSenders = useMemo(() => [{ text: "None", value: NONE }, ...mapToValueText(senders)], [senders]);

  const onChangeField = useCallback(
    (name, value) => {
      changeField(name, value, false);
    },
    [changeField],
  );

  const onChangeServiceField = useCallback(
    (name, value) => {
      changeField(
        name,
        reject(value, (v) => v === NONE),
        false,
      );
      changeField("includeWithEmptyService", isNotEmpty(value) ? value.includes(NONE) : undefined, false);
    },
    [changeField],
  );

  const onChangePriorityField = useCallback(
    (name, value) => {
      changeField(
        name,
        reject(value, (v) => v === NONE),
        false,
      );
      changeField("includeWithEmptyPriority", isNotEmpty(value) ? value.includes(NONE) : undefined, false);
    },
    [changeField],
  );

  const onChangeReferredByField = useCallback(
    (name, value) => {
      changeField(
        name,
        reject(value, (v) => v === NONE),
        false,
      );
      changeField("includeWithEmptyReferredBy", isNotEmpty(value) ? value.includes(NONE) : undefined, false);
    },
    [changeField],
  );

  useMutationWatch(organizationId, (prev) => {
    if (allAreInteger(prev, organizationId)) {
      const { statuses, priorityIds } = defaultData;
      change({ statuses, priorityIds }, false, true);
    }
  });

  useMutationWatch(type, () => {
    const { serviceIds } = defaultData;
    change({ serviceIds }, false, true);
  });

  return (
    <div className={cn("ReferralFilter", className)}>
      <Row>
        <Col lg={4} md={6} sm={6}>
          <SelectField
            label="Service"
            name="serviceIds"
            value={compact([...fields.serviceIds, data.includeWithEmptyService ? NONE : null])}
            isMultiple
            isSectioned
            hasValueTooltip
            sections={mappedServices}
            hasSectionTitle
            hasSectionIndicator
            hasSectionSeparator
            hasKeyboardSearch
            hasKeyboardSearchText
            placeholder="Select Service"
            onChange={onChangeServiceField}
          />
        </Col>
        <Col lg={4} md={6} sm={6}>
          <SelectField
            label="Priority"
            name="priorityIds"
            value={compact([...fields.priorityIds, data.includeWithEmptyPriority ? NONE : null])}
            options={mappedPriorities}
            isMultiple
            hasValueTooltip
            hasKeyboardSearch
            hasKeyboardSearchText
            placeholder="Select Priority"
            onChange={onChangePriorityField}
          />
        </Col>
        <Col lg={4} md={12} sm={12}>
          <SelectField
            label="Status"
            name="statuses"
            value={fields.statuses}
            options={mappedStatuses}
            isMultiple
            hasValueTooltip
            hasKeyboardSearch
            hasKeyboardSearchText
            placeholder="Select Status"
            onChange={onChangeField}
          />
        </Col>
      </Row>
      <Row>
        <Col lg={4}>
          {type === INBOUND ? (
            <SelectField
              label="Referred by"
              name="referredBy"
              value={compact([...fields.referredBy, data.includeWithEmptyReferredBy ? NONE : null])}
              options={mappedSenders}
              isMultiple
              hasValueTooltip
              hasKeyboardSearch
              hasKeyboardSearchText
              placeholder="Select Assignee"
              onChange={onChangeReferredByField}
            />
          ) : (
            <SelectField
              label="Referred to"
              name="referredTo"
              value={fields.referredTo}
              options={mappedReferents}
              isMultiple
              hasValueTooltip
              hasKeyboardSearch
              hasKeyboardSearchText
              placeholder="Select Assignee"
              onChange={onChangeField}
            />
          )}
        </Col>
        <Col lg={4} className="d-flex align-items-center">
          <Button outline color="success" className="ReferralFilter-Btn" onClick={reset}>
            Clear
          </Button>
          <Button color="success" className="ReferralFilter-Btn" onClick={onApply}>
            Apply
          </Button>
        </Col>
      </Row>
    </div>
  );
}

export default connect(mapStateToProps, mapDispatchToProps)(ReferralFilter);

ReferralFilter.propTypes = {
  type: PTypes.oneOf([INBOUND, OUTBOUND]),
};
