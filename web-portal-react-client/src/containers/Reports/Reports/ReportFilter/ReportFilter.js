import React, { useMemo, useEffect, useCallback, useState } from "react";

import { any, map, last, where, filter, findWhere, mapObject } from "underscore";

import { useHistory, useLocation } from "react-router-dom";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { Row, Col, Button } from "reactstrap";

import { withDirectoryData } from "hocs";

import { useMutationWatch, useDownloadingStatusInfoToast } from "hooks/common";

import { useCustomFilter, usePrimaryFilter } from "hooks/common/redux";

import { useReportTypesQuery, useCommunitiesQuery, useOrganizationsQuery } from "hooks/business/directory/query";

import { DateField, SelectField } from "components/Form";

import reportListActions from "redux/report/list/reportListActions";
import * as reportDocumentActions from "redux/report/document/reportDocumentActions";

import { defer, isEmpty, isInteger, toBoolean, isNotEmpty, DateUtils as DU } from "lib/utils/Utils";

import { compact } from "lib/utils/ArrayUtils";

import { getQueryParams as getRawQueryParams } from "lib/utils/UrlUtils";

import "./ReportFilter.scss";
import useClientsQuery from "../../../../hooks/business/directory/query/useClientsQuery";
import { useQuery } from "@tanstack/react-query";
import DirectoryService from "services/DirectoryService";
import { Loader } from "../../../../components";

const COVID_19_LOG = "COVID_19_LOG";

export const NAME = "REPORT_FILTER";
export const PRIMARY_NAME = "PRIMARY_REPORT_FILTER_NAME";

const DEFAULT_DATA = {
  reportType: null,
  fromDate: null,
  toDate: null,
};

function getDefaultTimeRange() {
  return {
    fromDate: DU.startOf(Date.now(), "month").getTime(),
    toDate: DU.endOf(Date.now(), "day").getTime(),
  };
}

function getDefaultData() {
  return {
    ...DEFAULT_DATA,
    ...getDefaultTimeRange(),
  };
}

function getStartOfMonthTime(date) {
  return getStartOfDayTime(DU.startOf(date, "month"));
}

function getEndOfMonthTime(date) {
  return getEndOfDayTime(DU.endOf(date, "month"));
}

function getStartOfDayTime(date) {
  return DU.startOf(date, "day").getTime();
}

function getEndOfDayTime(date) {
  return DU.endOf(date, "day").getTime();
}

function get1stOfOctoberDateTime() {
  return DU.startOf(DU.add(DU.month(Date.now(), 9), -1, "year"), "month").getTime();
}

function get30thOfSeptemberDateTime() {
  return DU.month(DU.endOf(DU.date(Date.now(), 30), "day"), 8).getTime();
}

function filterCommunities(data) {
  return where(data, { canViewOrHasAccessibleClient: true });
}

function valueTextMapper({ id, name, title, label }) {
  return { value: id || name, text: title || label || name };
}

function valueTextMapperForClient({ id, fullName }) {
  return { value: id, text: fullName };
}

function valueTextMapperForWorkflow({ code, name }) {
  return { value: code, text: name };
}

function getQueryParams(search) {
  return mapObject(getRawQueryParams(search), (v, k) => {
    if (k === "communityIds") return v ? compact(v.map((o) => +o)) : [];
    return v;
  });
}

function mapStateToProps(state) {
  const { list, document } = state.report;

  return {
    errors: list.errors,
    fields: list.dataSource.filter,
    isFilterValid: list.isFilterValid,
    isFilterChanged: list.isFilterChanged(),
    document,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      ...bindActionCreators(reportListActions, dispatch),
      document: bindActionCreators(reportDocumentActions, dispatch),
    },
  };
}

function ReportFilter({ fields, errors, actions, document, isFilterValid, isFilterChanged }) {
  const [showFilterSelect, setShowFilterSelect] = useState(false);

  const [isSelectAll, setIsSelectAll] = useState(false);
  const [selectedCommunityIds, setSelectedCommunityIds] = useState([]);

  const { organizationId, communityIds, reportType, fromDate, toDate, clientIds, workflowId } = fields;
  let minFromDate;

  if (reportType === "ARIZONA_MATRIX_MONTHLY") {
    minFromDate = getStartOfMonthTime(Date.now());
  }

  let maxToDate;

  if (reportType === "ARIZONA_MATRIX_MONTHLY") {
    maxToDate = getEndOfMonthTime(Date.now());
  }

  let areAllRequiredDates = Boolean(fromDate && toDate);

  if (reportType === "IN_TUNE") {
    areAllRequiredDates = Boolean(fromDate);
  }

  const canExport = reportType && organizationId && isNotEmpty(communityIds) && areAllRequiredDates;

  const isNoToDate = reportType === "IN_TUNE";

  const history = useHistory();
  const location = useLocation();

  const withDownloadingStatusInfoToast = useDownloadingStatusInfoToast();

  const initialData = useMemo(() => location.search && getQueryParams(location.search), [location.search]);

  const {
    clear,
    change: changePrimary,
    changeField: changePrimaryField,
  } = usePrimaryFilter(PRIMARY_NAME, fields, actions);

  const { change, changeField } = useCustomFilter(NAME, fields, actions, {
    isChanged: isFilterChanged,
    defaultData: { ...getDefaultData(), reportType },
  });

  const { data: organizations } = useOrganizationsQuery({ includeAffiliated: true });

  const { areLabsEnabled } = findWhere(organizations, { id: organizationId }) || {};

  const mappedOrganizations = useMemo(() => map(organizations, valueTextMapper), [organizations]);

  const { data: communities, isFetching: communitiesFetching } = useCommunitiesQuery(
    { organizationId },
    {
      staleTime: 0,
      enabled: isInteger(organizationId),
      onSuccess: (data) => {
        changePrimary(
          {
            communityIds: isEmpty(communityIds) ? map(filterCommunities(data), (o) => o.id) : communityIds,
          },
          false,
          false,
        );
        setIsSelectAll(true);
        setSelectedCommunityIds(isEmpty(communityIds) ? map(filterCommunities(data), (o) => o.id) : communityIds);
      },
    },
  );

  const { data: clientsList, isFetching: clientsFetching } = useClientsQuery(
    {
      organizationId,
      communities: communityIds,
    },
    {
      staleTime: 300000,
      enabled: isInteger(organizationId) && isNotEmpty(communityIds) && showFilterSelect,
    },
  );

  const { data: workflowTemplateList, isFetching: workflowFetching } = useQuery({
    queryKey: ["reportWorkflow", organizationId, communityIds],
    queryFn: async () => {
      const result = await DirectoryService.findWorkflowTemplate({
        organizationId,
        communities: communityIds,
        page: 0,
        size: 99999,
      });

      return result.data;
    },
    enabled: isInteger(organizationId) && isNotEmpty(communityIds) && showFilterSelect,
    staleTime: 0,
  });

  const mappedCommunities = useMemo(() => map(communities, valueTextMapper), [communities]);
  const mappedClients = useMemo(() => map(clientsList, valueTextMapperForClient), [clientsList]);
  const mappedWorkflow = useMemo(() => map(workflowTemplateList, valueTextMapperForWorkflow), [workflowTemplateList]);

  const { data: { data: types } = {}, isFetching: reportTypesFetching } = useReportTypesQuery(
    {
      organizationId,
      selectedCommunityIds,
    },
    {
      staleTime: 300000,
      enabled: isInteger(organizationId)
        ? isSelectAll
          ? isInteger(organizationId)
          : isNotEmpty(selectedCommunityIds)
        : false,
      onSuccess: (resp) => {
        if (reportType && !any(resp.data, (o) => o.name === reportType)) {
          changeField("reportType", null);
        }
      },
    },
  );

  const mappedTypes = useMemo(
    () =>
      map(
        filter(types, (o) => areLabsEnabled || o.name !== COVID_19_LOG),
        valueTextMapper,
      ),
    [types, areLabsEnabled],
  );

  const validate = useCallback(() => {
    const excluded = [];

    if (reportType === "IN_TUNE") {
      excluded.push("toDate");
    }

    return actions.validate(fields.toJS(), { excluded });
  }, [fields, actions, reportType]);

  const onChangeCommunityField = useCallback(
    (name, value) => {
      changePrimary({ [name]: value });
    },
    [changePrimary],
  );

  const changeDateField = useCallback(
    (name, value) => {
      const dateTime = value && (name === "fromDate" ? getStartOfDayTime(value) : getEndOfDayTime(value));

      changeField(name, dateTime).then(() => {
        if (!isFilterValid) validate();
      });
    },
    [isFilterValid, validate, changeField],
  );

  const download = useCallback(() => {
    const data = fields.toJS();

    const body = {
      ...data,
      workflowCode: fields.workflowId,
    };

    if (isSelectAll) {
      delete body.communityIds;
    } else {
      delete body.organizationId;
    }

    delete body.workflowId;

    if (fields.communityIds.length === mappedCommunities.length) {
      data.communityIds = [];
    } else {
      data.organizationId = null;
    }

    withDownloadingStatusInfoToast(() => actions.document.download(body));
  }, [fields, actions, withDownloadingStatusInfoToast]);

  const onReset = useCallback(() => {
    clear(getDefaultTimeRange(), false, false, true);
  }, [clear]);

  const onExport = useCallback(() => {
    validate().then((success) => {
      success && download();
    });
  }, [validate, download]);

  useEffect(() => {
    change(getDefaultTimeRange(), false, true);
  }, [change, organizationId]);

  useEffect(() => {
    if (["HUD", "HUD_MFSC"].includes(reportType)) {
      const now = Date.now();

      const september30 = get30thOfSeptemberDateTime();

      change({
        fromDate: get1stOfOctoberDateTime(),
        toDate: DU.gt(now, september30) ? september30 : now,
      });
    } else if (reportType === "IN_TUNE") {
      change({
        fromDate: getStartOfDayTime(Date.now()),
        toDate: null,
      });
    } else if (reportType === "ARIZONA_MATRIX_MONTHLY") {
      change({
        fromDate: DU.startOf(Date.now(), "month").getTime(),
        toDate: DU.endOf(Date.now(), "month").getTime(),
      });
    } else {
      change(getDefaultTimeRange());
    }
  }, [change, reportType]);

  useEffect(() => {
    if (initialData) {
      changePrimary(initialData);
      change(initialData);
      defer().then(history.replace);
    }
  }, [change, history, download, initialData, changePrimary]);

  useMutationWatch(initialData, (prev) => {
    if (prev?.export) download();
  });

  useEffect(() => {
    if (reportType === "CLIENT_WORKFLOW") {
      setShowFilterSelect(true);
    } else {
      setShowFilterSelect(false);
    }
  }, [reportType]);

  return (
    <div className="ReportFilter">
      {(reportTypesFetching || clientsFetching || workflowFetching || communitiesFetching) && (
        <Loader hasBackdrop={true} />
      )}
      <Row>
        <Col lg={4} md={6} sm={6}>
          <SelectField
            type="text"
            name="organizationId"
            isDisabled={document.isFetching}
            options={mappedOrganizations}
            value={organizationId}
            label="Organization"
            placeholder="Organization"
            onChange={changePrimaryField}
          />
        </Col>
        <Col lg={4} md={6} sm={6}>
          <SelectField
            isMultiple
            value={communityIds}
            label="Community"
            name="communityIds"
            placeholder="Community"
            isDisabled={document.isFetching}
            className="ReportFilter-Field"
            options={mappedCommunities}
            onChange={(name, value) => {
              onChangeCommunityField(name, value);
              changeField("workflowId", null);
              changeField("clientIds", []);
              changeField("reportType", null);

              setSelectedCommunityIds(value);

              if (value.length === mappedCommunities.length) {
                setIsSelectAll(true);
              } else {
                setIsSelectAll(false);
              }
            }}
          />
        </Col>
        <Col lg={4} md={12} sm={12}>
          <SelectField
            value={reportType}
            label="Report"
            name="reportType"
            placeholder="Report"
            isDisabled={document.isFetching}
            className="ReportFilter-Field"
            options={mappedTypes}
            onChange={(name, value) => {
              changeField("reportType", value);
              changeField("workflowId", null);
              changeField("clientIds", []);
            }}
          />
        </Col>
      </Row>
      <Row>
        {/* workflow */}

        {showFilterSelect && (
          <>
            <Col lg={4} md={6} sm={6}>
              <SelectField
                label="Workflow"
                name="workflowId"
                placeholder="Workflow Template"
                isDisabled={document.isFetching}
                options={mappedWorkflow}
                value={workflowId}
                onChange={changeField}
              />
            </Col>

            {workflowId && (
              <Col lg={4} md={6} sm={6}>
                <SelectField
                  hasAllOption={false}
                  isMultiple
                  label="Client"
                  name="clientIds"
                  placeholder="Client"
                  isDisabled={document.isFetching}
                  options={mappedClients}
                  value={clientIds}
                  onChange={(name, value) => {
                    onChangeCommunityField(name, value);
                  }}
                />
              </Col>
            )}
          </>
        )}

        <Col lg={4} md={6} sm={6}>
          <DateField
            type="text"
            name="fromDate"
            value={fromDate}
            label="Date From*"
            maxDate={toDate}
            minDate={minFromDate}
            isDisabled={document.isFetching}
            className="ReportFilter-Field"
            errorText={last(errors?.fromDate)}
            onChange={changeDateField}
          />
        </Col>

        <Col lg={4} md={6} sm={6}>
          <DateField
            type="text"
            name="toDate"
            value={toDate}
            label="Date To*"
            minDate={fromDate}
            maxDate={maxToDate}
            isDisabled={isNoToDate || document.isFetching}
            className="ReportFilter-Field"
            errorText={last(errors?.toDate)}
            onChange={changeDateField}
          />
        </Col>

        <Col lg={4} md={12} className="d-flex align-items-center">
          <Button
            outline
            color="success"
            disabled={document.isFetching}
            className="ReportFilter-Btn margin-right-16"
            onClick={onReset}
          >
            Clear
          </Button>
          <Button
            color="success"
            className="ReportFilter-Btn"
            disabled={!canExport || document.isFetching}
            onClick={onExport}
          >
            Export
          </Button>
        </Col>
      </Row>
      <Row></Row>
    </div>
  );
}

export default connect(mapStateToProps, mapDispatchToProps)(withDirectoryData(ReportFilter));
