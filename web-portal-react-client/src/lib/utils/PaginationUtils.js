import React, { useState } from 'react';
import { Pagination } from 'react-bootstrap';
import './MyPagination.scss';

function MyPaginationComponent({ totalRecords, onPageChange }) {

  const [active, setActive] = useState(1);

  const items = [];
  const totalPages = Math.ceil(totalRecords / 12);
  const maxItems = 10;

  const handlePageChange = (number) => {
    setActive(number);
    if (onPageChange) {
      onPageChange(number);
    }
  }

  if (totalPages <= maxItems) {
    // 当总页数小于或等于10页时，正常显示所有页面
    for (let number = 1; number <= totalPages; number++) {
      items.push(
        <Pagination.Item key={number} active={number === active} onClick={(e) => {
          e.stopPropagation();
          handlePageChange(number)
        }}>
          {number}

        </Pagination.Item>
      );
    }
  } else {
    // 当总页数超过10页时，进行特殊处理
    let startPage = active - 4;
    let endPage = active + 4;

    if (startPage < 1) {
      endPage -= (startPage - 1);
      startPage = 1;
    }
    if (endPage > totalPages) {
      startPage -= (endPage - totalPages);
      endPage = totalPages;
    }

    for (let number = startPage; number <= endPage; number++) {
      items.push(
        <Pagination.Item
          key={number}
          active={number === active}
          onClick={(e) => {
            e.stopPropagation();
            handlePageChange(number);
          }}>
          {number}
        </Pagination.Item>
      );
    }

    // 如果 startPage 大于1，则在开始处添加过渡按钮
    if (startPage > 1) {
      items.unshift(<Pagination.Ellipsis key="startEllipsis"/>);
    }

    // 如果 endPage 小于 totalPages，则在末尾添加过渡按钮
    if (endPage < totalPages) {
      items.push(<Pagination.Ellipsis key="endEllipsis"/>);
    }
  }

  return <Pagination className='myPagination'>{items}</Pagination>;
}

export default MyPaginationComponent;
