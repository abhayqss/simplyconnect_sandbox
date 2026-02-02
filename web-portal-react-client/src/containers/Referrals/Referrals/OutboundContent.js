import cn from "classnames";
import React, { useEffect, useMemo, useRef, useState } from "react";
import { REFERRAL_ENTITY_TYPES, REFERRAL_TYPES } from "../../../lib/Constants";
import { ReactComponent as Filter } from "images/filters.svg";
import ReferralFilter from "../ReferralFilter/ReferralFilter";
import { useDispatch, useSelector } from "react-redux";
import service from "services/ReferralService";
import { debounce, isEqual } from "lodash";
import { setPagination, setSort, setOutboundData } from "redux/ReferralData/tableDataActions";
import { Table } from "../../../components";
import { Link } from "react-router-dom";
import { path } from "../../../lib/utils/ContextUtils";

import { Badge, Collapse, UncontrolledTooltip as Tooltip } from "reactstrap";
import { DateUtils as DU } from "../../../lib/utils/Utils";
import { useExternalProviderRoleCheck } from "../../../hooks/business/external";

const { INBOUND, OUTBOUND } = REFERRAL_TYPES;
const { REFERRAL } = REFERRAL_ENTITY_TYPES;

const OutboundContent = ({ friendlyName, type, isClient, STATUS_COLORS, DATE_FORMAT }) => {
  const dispatch = useDispatch();

  const isExternalProvider = useExternalProviderRoleCheck();
  const [isFilterOpen, setIsFilterOpen] = useState(false);

  const { outboundData, pagination, sorting } = useSelector((state) => state.ReferData);

  const { filter } = useSelector((state) => state.referral.list.dataSource);

  const [isFetching, setIsFetching] = useState(false);

  const [filterParams, setFilterParams] = useState({});
  const [reload, setReload] = useState(false);

  const [isInit, setIsInit] = useState(true);

  const { field = null, order = null } = sorting ?? {};

  const { page: p, size } = pagination ?? {};

  useEffect(() => {
    if (!isInit) {
      return;
    }

    const filterData = filter.toJS();
    setReload(false);
    setFilterParams({
      type: "OUTBOUND",
      organizationId: filterData.organizationId,
      communityIds: filterData.communityIds,
      size,
      page: p,
      ...(field ? { sort: `${field},${order}` } : null),
    });
  }, [filter, p, sorting]);

  useEffect(() => {
    return () => {
      dispatch(
        setPagination({
          ...pagination,
          page: 1,
          totalCount: 0,
        }),
      );

      dispatch(
        setSort({
          field: null,
          order: null,
        }),
      );

      setIsInit(true);
    };
  }, [type]);

  useEffect(() => {
    const filterData = filter.toJS();

    if (!isInit) {
      setFilterParams({
        type: "OUTBOUND",
        serviceIds: filterData.serviceIds,
        priorityIds: filterData.priorityIds,
        statuses: filterData.statuses,
        referredBy: filterData.referredBy,
        organizationId: filterData.organizationId,
        communityIds: filterData.communityIds,
        includeWithEmptyPriority: filterData.includeWithEmptyPriority,
        includeWithEmptyReferredBy: filterData.includeWithEmptyReferredBy,
        includeWithEmptyService: filterData.includeWithEmptyService,
        assignedTo: filterData.assignedTo,
        size,
        page: p,
        ...(field ? { sort: `${field},${order}` } : null),
      });
    }
  }, [reload, p, sorting]);

  function useDeepCompareEffect(callback, dependencies) {
    const currentDependenciesRef = useRef();

    if (!isEqual(currentDependenciesRef.current, dependencies)) {
      currentDependenciesRef.current = dependencies;
    }

    useEffect(callback, [currentDependenciesRef.current]);
  }

  const onApply = () => {
    setReload(!reload);
    setIsInit(false);
    dispatch(
      setPagination({
        ...pagination,
        page: 1,
      }),
      setSort({
        field: null,
        order: null,
      }),
    );
  };

  const fetchData = (params) => {
    setIsFetching(true);
    service.find(params).then((res) => {
      dispatch(setOutboundData(res.data));

      dispatch(
        setPagination({
          ...pagination,
          totalCount: res.totalCount,
        }),
      );

      setIsFetching(false);
    });
  };

  const fetchDataDebounced = useMemo(() => debounce((params) => fetchData(params), 300), [fetchData]);

  useDeepCompareEffect(() => {
    if (filterParams?.communityIds?.length > 0 && typeof filterParams?.organizationId === "number") {
      fetchDataDebounced(filterParams);
    }
  }, [filterParams]);

  const onRefresh = (data) => {
    dispatch(
      setPagination({
        ...pagination,
        page: data,
      }),
    );
  };

  const toSort = (field, order) => {
    const data = {
      field,
      order,
    };
    dispatch(setSort(data));
  };

  return (
    <>
      <div className="Referrals-Header">
        <div className="Referrals-HeaderItem">
          <div className="Referrals-Title">
            <span className="Referrals-TitleText">
              {`${friendlyName} referrals${type === INBOUND ? " and inquiries" : ""}`}
            </span>
            {pagination.totalCount > 0 && !isFetching && (
              <Badge color="info" className="Badge Badge_place_top-right">
                {pagination.totalCount}
              </Badge>
            )}
          </div>
        </div>
        <div className="Referrals-HeaderItem">
          <div className="Referrals-Actions">
            <Filter
              id="referral-filter-icon"
              className={cn(
                "ReferralFilter-Icon",
                isFilterOpen ? "ReferralFilter-Icon_rotated_90" : "ReferralFilter-Icon_rotated_0",
              )}
              onClick={() => {
                setIsFilterOpen(!isFilterOpen);
              }}
            />
          </div>
        </div>
      </div>

      <Collapse isOpen={isFilterOpen}>
        <ReferralFilter type={OUTBOUND} className="margin-bottom-50" onApply={onApply} />
      </Collapse>

      {/* TABLE */}
      <Table
        hasHover
        hasOptions
        hasPagination
        keyField="id"
        hasCaption={false}
        noDataText={type === INBOUND ? "No inbound referrals and inquiries." : "No outbound referrals and inquiries."}
        isLoading={isFetching}
        className="ReferralList"
        containerClass="ReferralListContainer"
        data={outboundData || []}
        pagination={pagination}
        columns={[
          {
            dataField: "name",
            text: "Name",
            sort: true,
            onSort: toSort,
            formatter: (v, row, index, formatExtraData, isMobile) => {
              const basePath = isExternalProvider ? "/external-provider" : isClient ? `/clients/${clientId}` : "";

              const prefix = isMobile ? "m-" : "";
              const id = `${prefix}referral-req-${row.id || row.requestId}`;

              const url =
                type === INBOUND
                  ? row.entity === REFERRAL
                    ? `${type.toLowerCase()}-referrals/${row.id}/requests/${row.requestId}`
                    : `inbound-referrals/inquiries/${row.id}`
                  : `${type.toLowerCase()}-referrals/${row.id}`;

              return (
                <>
                  <div title={v} className="d-flex flex-row overflow-hidden">
                    <Link
                      id={id}
                      className="ReferralList-Client cursor-pointer"
                      to={path(`${basePath}/${url}`)}
                      onClick={() => {}}
                    >
                      {v}
                    </Link>
                  </div>
                  <Tooltip
                    target={id}
                    placement="top"
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
                    {`View ${type === OUTBOUND ? "referrals" : row.entity?.toLowerCase()} details`}
                  </Tooltip>
                </>
              );
            },
          },
          {
            dataField: "priorityTitle",
            text: "Priority",
            sort: true,
            onSort: toSort,
            headerStyle: { width: "10%" },
          },
          {
            dataField: "serviceTitle",
            text: "Service",
            sort: true,
            onSort: toSort,
          },
          {
            dataField: "statusName",
            text: "Status",
            sort: true,
            onSort: toSort,
            headerStyle: { width: "13%" },
            formatter: (v, row) => (
              <div className="Referral-Status" style={{ backgroundColor: STATUS_COLORS[v] }}>
                {row.statusTitle}
              </div>
            ),
          },
          {
            dataField: type === INBOUND ? "referredBy" : "referredTo",
            text: type === INBOUND ? "Referred by" : "Referred to",
            formatter: (v) => (type === OUTBOUND ? v?.join(", ") : v),
          },
          {
            dataField: "date",
            text: "Request date",
            sort: true,
            headerAlign: "right",
            align: "right",
            onSort: toSort,
            formatter: (v) => (v ? DU.format(v, DATE_FORMAT) : ""),
          },
        ]}
        columnsMobile={["clientName", "serviceTitle"]}
        onRefresh={onRefresh}
      />
    </>
  );
};

export default OutboundContent;
