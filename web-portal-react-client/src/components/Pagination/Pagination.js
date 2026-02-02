import React from "react";
import PropTypes from "prop-types";
import cn from "classnames";
import { map, filter, range, first, last, isNumber } from "underscore";
import { ReactComponent as LeftChevron } from "images/chevron-left.svg";
import { ReactComponent as RightChevron } from "images/chevron-right.svg";

const getDisplayedPageNumbers = (size, page, pageSize, totalCount) => {
  if (totalCount > 0) {
    const pageCount = Math.ceil(totalCount / pageSize);
    const numbers = range(1, pageCount + 1);

    if (pageCount <= size + 1) return numbers;

    if (page < size) return [...first(numbers, size), "...", pageCount];

    if (pageCount - page + 1 < size) return [1, "...", ...last(numbers, size)];

    return [...filter(numbers, (num) => num <= page + 1 && num > page - size + 1), "...", pageCount];
  }

  return [];
};

const pageButtonRenderer = (props) => {
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
};

const Pagination = ({ page, size, totalCount, onPageChange, paginationSize }) => {
  const pageCount = Math.ceil(totalCount / size);

  return (
    <div className="row react-bootstrap-table-pagination">
      <div className="col-md-6 col-xs-6 col-sm-6 col-lg-6" />
      <div className="react-bootstrap-table-pagination-list col-md-6 col-xs-6 col-sm-6 col-lg-6">
        <ul className="pagination react-bootstrap-table-page-btns-ul">
          {pageButtonRenderer({
            page: "<",
            disabled: page === 1,
            title: "Previous page",
            onPageChange: () => {
              if (page > 1) {
                onPageChange(page - 1);
              }
            },
          })}
          {map(getDisplayedPageNumbers(paginationSize, page, size, totalCount), (n) =>
            pageButtonRenderer(
              n === "..."
                ? { page: n, title: "Other pages" }
                : {
                    page: n,
                    active: page === n,
                    title: "Page " + n,
                    onPageChange: () => {
                      if (n !== page) {
                        onPageChange(n);
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
                onPageChange(page + 1);
              }
            },
          })}
        </ul>
      </div>
    </div>
  );
};

Pagination.propTypes = {
  page: PropTypes.number.isRequired,
  size: PropTypes.number.isRequired,
  totalCount: PropTypes.number.isRequired,
  onPageChange: PropTypes.func.isRequired,
  paginationSize: PropTypes.number,
};

Pagination.defaultProps = {
  paginationSize: 4,
};

export default Pagination;
