import React, { useMemo } from "react";

import cn from "classnames";

import { map, compact, without } from "underscore";

import { Link } from "react-router-dom";
import { UncontrolledTooltip as Tooltip } from "reactstrap";

import { Table } from "components";

import { IconButton } from "components/buttons";

import Avatar from "containers/Avatar/Avatar";

import { RESPONSIVE_BREAKPOINTS } from "lib/Constants";

import { isEmpty, measure, DateUtils as DU } from "lib/utils/Utils";

import { path } from "lib/utils/ContextUtils";

import { ReactComponent as Asset } from "images/asset.svg";
import { ReactComponent as Delete } from "images/delete.svg";
import { ReactComponent as Diskette } from "images/diskette.svg";

import ClientMatches from "../../Clients/Clients/ClientMatches/ClientMatches";

import "./ClientRecordList.scss";

const { TABLET_LANDSCAPE } = RESPONSIVE_BREAKPOINTS;

const DATE_FORMAT = DU.formats.americanMediumDate;

function formatDate(date) {
  return DU.format(date, DATE_FORMAT);
}

function ClientLink({ id, name, hasComma, isDisabled }) {
  const title = name + (hasComma ? "," : "");

  return isDisabled ? (
    <span className="ClientRecordList-Link_disabled">{title}</span>
  ) : (
    <Link to={path(`/clients/${id}`)} className="ClientRecordList-Link">
      {title}
    </Link>
  );
}

export default function ClientRecordList({
  data,
  pagination,
  isFetching,
  canRequestAccess,

  onSort,
  onRefresh,
  onRequestAccess,
}) {
  const isTablet = measure(document.body).width < TABLET_LANDSCAPE;

  const columns = useMemo(
    () =>
      compact([
        {
          dataField: "fullName",
          text: "Name",
          sort: false,
          headerAlign: "left",
          headerClasses: "ClientRecordListHeader-ClientName",
          style: (cell, row) =>
            !row.isActive && {
              opacity: "0.5",
            },
          onSort,
          formatter: (v, row, index, formatExtraData, isMobile) => {
            return (
              <div className="d-flex align-items-center">
                <Avatar
                  name={v}
                  id={row.avatarId}
                  className={cn(
                    "ClientRecordList-ClientAvatar",
                    !row.isActive && "ClientRecordList-ClientAvatar_black-white",
                  )}
                  {...(!row.isActive && { nameColor: "#e0e0e0" })}
                />
                <div className="d-flex flex-row overflow-hidden">
                  {row.canView && !canRequestAccess ? (
                    <>
                      <Link
                        id={`${isMobile ? "m-" : ""}client-record-${row.id}`}
                        to={path(`/clients/${row.id}`)}
                        className={cn("ClientRecordList-ClientName", row.avatarDataUrl && "margin-left-10")}
                      >
                        {v}
                      </Link>
                      <Tooltip
                        placement="top"
                        target={`${isMobile ? "m-" : ""}client-record-${row.id}`}
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
                        View client details
                      </Tooltip>
                    </>
                  ) : (
                    <div
                      title={v}
                      id={`${isMobile ? "m-" : ""}client-record-${row.id}`}
                      className={cn(
                        "ClientRecordList-ClientName",
                        !canRequestAccess && "ClientRecordList-ClientName_disabled",
                      )}
                    >
                      {v}
                    </div>
                  )}
                </div>
              </div>
            );
          },
        },
        {
          dataField: "gender",
          text: "Gender",
          sort: false,

          style: (cell, row) =>
            !row.isActive && {
              opacity: "0.5",
            },
          onSort,
        },
        {
          dataField: "birthDate",
          text: "Date of Birth",
          sort: false,
          align: "right",
          headerAlign: "right",
          style: (cell, row) =>
            !row.isActive && {
              opacity: "0.5",
            },
          onSort,
        },
        {
          dataField: "ssnLastFourDigits",
          text: "SSN",
          headerAlign: "right",
          align: "right",
          style: (cell, row) =>
            !row.isActive && {
              opacity: "0.5",
            },
          formatter: (v) => v && `###-##-${v}`,
        },
        !canRequestAccess && {
          dataField: "riskScore",
          text: "Risk score",
          align: "right",
          headerAlign: "right",
          sort: false,
          headerStyle: {
            width: "10%",
          },
          style: (cell, row) =>
            !row.isActive && {
              opacity: "0.5",
            },
          onSort,
        },
        {
          dataField: "community.name",
          text: "Community",
          sort: false,
          style: (cell, row) =>
            !row.isActive && {
              opacity: "0.5",
            },
          onSort,
          formatter: (v, row) => (
            <div className="ClientRecordList-ClientCommunity" title={row.community}>
              {row.community}
            </div>
          ),
        },
        {
          dataField: "createdDate",
          text: "Created",
          sort: false,
          align: "right",
          headerAlign: "right",
          style: (cell, row) =>
            !row.isActive && {
              opacity: "0.5",
            },
          onSort,
          formatter: (v) => v && formatDate(v),
        },
        isTablet
          ? {
              dataField: "merged",
              text: "Matching Records",
              formatter: (v) =>
                v?.length ? (
                  <div className="Detail-Clients">
                    {v.map((client, i) => (
                      <ClientLink
                        id={client.id}
                        name={client.fullName}
                        isDisabled={!client.isActive}
                        hasComma={v.length > 1 && i < v.length - 1}
                      />
                    ))}
                  </div>
                ) : null,
            }
          : null,
        canRequestAccess
          ? {
              dataField: "@actions",
              text: "",
              headerStyle: {
                width: "80px",
              },
              align: "right",
              formatter: (v, row) => (
                <div className="ClientRecordList-Actions">
                  <IconButton
                    size={36}
                    Icon={Diskette}
                    tooltip={
                      row.canRequestAccess
                        ? "Save to my Clients"
                        : "You have already tried to save the Client. Please contact your Administrator for more details"
                    }
                    disabled={!row.canRequestAccess}
                    id={`${row.id}-request-access`}
                    onClick={() => {
                      if (row.canRequestAccess) {
                        onRequestAccess(row);
                      }
                    }}
                    className="ClientRecordList-Action"
                  />
                </div>
              ),
            }
          : null,
      ]),
    [onSort, isTablet, onRequestAccess, canRequestAccess],
  );

  return (
    <Table
      hasHover
      hasOptions
      hasPagination
      keyField="id"
      title="Records"
      noDataText="No results."
      isLoading={isFetching}
      className="ClientRecordList"
      containerClass="ClientRecordListContainer"
      data={data}
      pagination={pagination}
      columns={columns}
      hasCaption={false}
      columnsMobile={["fullName", "gender"]}
      onRefresh={onRefresh}
      expandRow={
        !isTablet
          ? {
              onlyOneExpanding: true,
              showExpandColumn: true,
              expandColumnPosition: "right",
              expandHeaderColumnRenderer: () => null,
              parentClassName: "ClientRecordList-ExpandableRow",
              nonExpandable: without(
                map(data, (o) => isEmpty(o.merged) && o.id),
                false,
              ),
              expandColumnRenderer: ({ expanded, rowKey, expandable }) => {
                if (expandable) {
                  return (
                    <>
                      {expanded ? (
                        <Delete
                          id={"match-toggle-" + rowKey}
                          style={{ stroke: "#ffffff" }}
                          className="ClientRecordList-ShowMatchesActionItem"
                        />
                      ) : (
                        <Asset id={"match-toggle-" + rowKey} className="ClientRecordList-ShowMatchesActionItem" />
                      )}
                      <Tooltip
                        target={"match-toggle-" + rowKey}
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
                        {expanded ? "Hide Matches" : "Show Matches"}
                      </Tooltip>
                    </>
                  );
                }
              },
              renderer: (row) => <ClientMatches isOpen data={row.merged} />,
            }
          : undefined
      }
    />
  );
}
