import React, { memo, useRef, useMemo, useCallback } from "react";

import { map, noop, range, first, last, filter, isNumber } from "underscore";

import cn from "classnames";
import PTypes from "prop-types";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory, { PaginationProvider } from "react-bootstrap-table2-paginator";

import TableMobile from "./TableMobile/TableMobile";

import { ReactComponent as TopChevron } from "images/chevron-top.svg";
import { ReactComponent as LeftChevron } from "images/chevron-left.svg";
import { ReactComponent as RightChevron } from "images/chevron-right.svg";
import { ReactComponent as BottomChevron } from "images/chevron-bottom.svg";

import { ReactComponent as Cog } from "images/cog.svg";

import { ReactComponent as LoaderImg } from "images/loader.svg";

import "./Table.scss";

const PAGINATION_OPTIONS = {
  custom: true,
  paginationSize: 4,
  pageStartIndex: 1,
  hideSizePerPage: true,
  alwaysShowAllBtns: true,
  withFirstAndLast: true,
};

function Caption(props) {
  const { title, className, hasOptions, onOptions } = props;

  return (
    <div className={cn("Table-Caption", className)}>
      {title ? <div className="Table-Title">{title}</div> : null}
      {hasOptions ? <Cog className="Table-Options" onClick={onOptions} /> : null}
    </div>
  );
}

function renderSortCaret(order) {
  const dropup = (
    <div key="dropup" className="dropup chevron-green">
      <TopChevron />
    </div>
  );

  const dropdown = (
    <div key="dropdown" className="dropdown">
      <BottomChevron />
    </div>
  );

  if (!order) return <div className="order">{[dropup, dropdown]}</div>;

  if (order === "asc") return <div className="order">{dropup}</div>;

  return <div className="order">{dropdown}</div>;
}

function renderHeaderCell(column, colIndex, { sortElement, filterElement }) {
  return (
    <div>
      {filterElement}
      <span className="Table-ColumnTitle">{column.text}</span>
      {sortElement}
    </div>
  );
}

function renderLoadingIndication() {
  return (
    <div className="Table-Loader">
      <LoaderImg className="Table-LoaderImg" />
    </div>
  );
}

function getDisplayedPageNumbers(size, page, pageSize, totalCount) {
  if (totalCount > 0) {
    const pageCount = Math.ceil(totalCount / pageSize);
    const numbers = range(1, pageCount + 1);

    //+1: it doesnt make sense to put '...' if size differs by 1
    //ex: size: 4, numbers: 12345
    if (pageCount <= size + 1) return numbers;

    //when page < size we should start pagination from 1 upto size - 1
    //ex: size: 4, page: 3, numbers 1234...
    //ex: size: 4, page: 4, numbers 2345
    if (page < size) return [...first(numbers, size), "...", pageCount];

    //use the same logic but from the end when page close to pageCount by size - 1
    //ex: size: 4, page: 6, numbers 5678
    if (pageCount - page + 1 < size) return [1, "...", ...last(numbers, size)];

    //starting from the page - size + 1 upto page + 1, then '...' and the very last page
    //ex: size: 4, page: 4, numbers 2345...55
    return [...filter(numbers, (num) => num <= page + 1 && num > page - size + 1), "...", pageCount];
  }

  return [];
}

function pageButtonRenderer(props) {
  const { page, title, active, disabled, onPageChange: cb } = props;

  const isArrow = ["<", ">"].includes(page);

  if (["<<", ">>"].includes(page)) return null;

  return (
    <li key={page} className={cn("page-item", { arrow: isArrow }, { active }, { disabled })}>
      <a
        href="#"
        title={title}
        className={cn("page-link", { active })}
        onClick={(e) => {
          e.preventDefault();
          cb && cb(page);
        }}
      >
        {page === "<" ? <LeftChevron className="prev-page__icon" /> : null}
        {page === ">" ? <RightChevron className="next-page__icon" /> : null}
        {page === "..." ? "..." : null}
        {isNumber(page) ? page : null}
      </a>
    </li>
  );
}

function Table({
  sort,
  data,
  title,
  columns,
  keyField,
  expandRow,
  rowEvents,
  selectedRows,
  defaultSorted,
  className,
  containerClass,
  isRemote,
  isStriped,
  isLoading,
  hasBorders,
  hasCaption,
  hasOptions,
  hasHover,
  hasPagination,
  paginationClass,
  paginationStyle,
  pagination,
  noDataText,
  renderCaption,
  columnsMobile,
  onRefresh: onRefreshCb,
  getRowStyle: getRowStyleCb,
  getRowClass: getRowClassCb,
}) {
  const colCount = columns.length + (selectedRows ? 1 : 0);

  const { page, size, totalCount } = pagination;

  const pageCount = Math.ceil(totalCount / size);

  const ref = useRef();

  const decoratedColumns = useMemo(
    () =>
      columns?.map((col) => {
        col = { headerFormatter: renderHeaderCell, ...col };
        return col.sort ? { ...col, sortCaret: renderSortCaret } : col;
      }),
    [columns],
  );

  const onRefresh = useCallback(
    (type, { page }) => {
      if (!(type === "sort")) {
        onRefreshCb(page);
      }
    },
    [onRefreshCb],
  );

  const getRowStyle = useCallback(
    (row, rowIndex) => {
      return getRowStyleCb(row, rowIndex) ?? {};
    },
    [getRowStyleCb],
  );

  const getRowClass = useCallback(
    (row, rowIndex) => {
      return getRowClassCb ? getRowClassCb(row, rowIndex) : rowIndex % 2 === 0 ? "odd" : "even";
    },
    [getRowClassCb],
  );

  return (
    <div ref={ref} className={cn("TableContainer", { HasMobileVersion: !!columnsMobile.length }, containerClass)}>
      <PaginationProvider
        pagination={paginationFactory({
          ...PAGINATION_OPTIONS,
          ...(hasPagination && {
            page,
            sizePerPage: size,
            totalSize: totalCount,
          }),
        })}
      >
        {({ paginationProps, paginationTableProps }) => (
          <>
            <TableMobile
              sort={sort}
              remote={isRemote}
              expandRow={expandRow}
              selectRow={selectedRows}
              defaultSorted={defaultSorted}
              data={isLoading ? [] : data}
              columns={decoratedColumns}
              columnsMobile={columnsMobile}
              keyField={keyField}
              classes={cn("Table", className)}
              headerClasses={"Table-Header"}
              striped={isStriped}
              hover={hasHover}
              bordered={hasBorders}
              rowStyle={getRowStyle}
              rowEvents={rowEvents}
              caption={
                hasCaption ? (
                  renderCaption ? (
                    renderCaption(title, true)
                  ) : (
                    <Caption title={title} hasOptions={hasOptions} onOptions={noop} />
                  )
                ) : (
                  ""
                )
              }
              onTableChange={onRefresh}
              noDataIndication={isLoading ? renderLoadingIndication : noDataText}
              rowClasses={getRowClass}
              {...paginationTableProps}
            />

            <BootstrapTable
              sort={sort}
              remote={isRemote}
              expandRow={expandRow}
              selectRow={selectedRows}
              defaultSorted={defaultSorted}
              data={isLoading ? [] : data}
              columns={decoratedColumns}
              keyField={keyField}
              classes={cn("Table", className, { Table_empty: !data?.length })}
              headerClasses={"Table-Header"}
              striped={isStriped}
              hover={hasHover}
              bordered={hasBorders}
              rowStyle={getRowStyle}
              rowEvents={rowEvents}
              caption={
                hasCaption ? (
                  renderCaption ? (
                    renderCaption(title, false)
                  ) : (
                    <Caption title={title} hasOptions={hasOptions} onOptions={noop} />
                  )
                ) : (
                  ""
                )
              }
              onTableChange={onRefresh}
              noDataIndication={isLoading ? renderLoadingIndication : noDataText}
              rowClasses={getRowClass}
              {...paginationTableProps}
            />
            {hasPagination && (
              <div style={paginationStyle} className={cn("row react-bootstrap-table-pagination", paginationClass)}>
                <div className="col-md-6 col-xs-6 col-sm-6 col-lg-6" />
                <div className="react-bootstrap-table-pagination-list col-md-6 col-xs-6 col-sm-6 col-lg-6">
                  <ul className="pagination react-bootstrap-table-page-btns-ul">
                    {pageButtonRenderer({
                      page: "<",
                      disabled: page === 1,
                      title: "Previous page",
                      onPageChange: () => {
                        if (page > 1) {
                          paginationProps.onPageChange(page - 1);
                        }
                      },
                    })}
                    {map(getDisplayedPageNumbers(paginationProps.paginationSize, page, size, totalCount), (n) =>
                      pageButtonRenderer(
                        n === "..."
                          ? { page: n, title: "Other pages" }
                          : {
                              page: n,
                              active: page === n,
                              title: "Page " + n,
                              onPageChange: () => {
                                if (n !== page) {
                                  paginationProps.onPageChange(n);
                                }
                              },
                            },
                      ),
                    )}
                    {pageButtonRenderer({
                      page: ">",
                      disabled: page === pageCount,
                      title: "Next page",
                      onPageChange: () => {
                        if (page < pageCount) {
                          paginationProps.onPageChange(page + 1);
                        }
                      },
                    })}
                  </ul>
                </div>
              </div>
            )}
          </>
        )}
      </PaginationProvider>
    </div>
  );
}

Table.propTypes = {
  columns: PTypes.arrayOf(PTypes.object),
  data: PTypes.arrayOf(PTypes.object),
  keyField: PTypes.string,
  noDataText: PTypes.string,

  sort: PTypes.shape({
    order: PTypes.oneOf(["asc", "desc"]),
    dataField: PTypes.string,
  }),
  defaultSorted: PTypes.arrayOf(
    PTypes.shape({
      order: PTypes.oneOf(["asc", "desc"]),
      dataField: PTypes.string,
    }),
  ),

  hasHover: PTypes.bool,
  hasOptions: PTypes.bool,
  hasBorders: PTypes.bool,
  hasCaption: PTypes.bool,
  hasPagination: PTypes.bool,
  paginationClass: PTypes.string,

  isRemote: PTypes.bool,
  isStriped: PTypes.bool,
  isLoading: PTypes.bool,

  title: PTypes.oneOfType([PTypes.node, PTypes.string]),
  renderCaption: PTypes.func,
  rowEvents: PTypes.object,
  expandRow: PTypes.object,
  pagination: PTypes.object,
  selectedRows: PTypes.object,

  className: PTypes.string,
  containerClass: PTypes.string,

  getRowStyle: PTypes.func,
  getRowClass: PTypes.func,

  onRefresh: PTypes.func,
  onSelect: PTypes.func,

  columnsMobile: PTypes.array,
};

Table.defaultProps = {
  data: [],
  columns: [],
  keyField: "id",
  noDataText: "No Data",

  isRemote: true,
  isStriped: false,
  isLoading: false,

  hasHover: false,
  hasHeader: false,
  hasCaption: true,
  hasBorders: false,
  hasPagination: false,

  rowEvents: {},
  pagination: {},

  getRowStyle: noop,

  onRefresh: noop,
  onSelect: noop,

  columnsMobile: [],
};

export default memo(Table);
