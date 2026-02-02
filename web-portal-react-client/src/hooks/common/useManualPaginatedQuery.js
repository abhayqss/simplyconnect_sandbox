import { useCallback, useEffect, useMemo, useState } from "react";

import { useMutation } from "@tanstack/react-query";

import { PAGINATION } from "lib/Constants";

const { MAX_SIZE } = PAGINATION;

export default function useManualPaginatedQuery(params, fetchFn, options) {
  const [page, setPage] = useState(1);
  const [totalCount, setTotalCount] = useState(0);
  const { name } = params;
  const [sorting, setSorting] = useState({
    field: params?.sorting?.field,
    order: params?.sorting?.order || "asc",
  });

  const pagination = useMemo(
    () => ({
      page: page,
      size: params.size ?? MAX_SIZE,
      totalCount,
    }),
    [page, params.size, totalCount],
  );

  const {
    data,
    reset,
    error,
    mutateAsync: _fetch,
    isLoading: isFetching,
  } = useMutation(
    (o) =>
      fetchFn({
        ...params,
        name,
        page,
        ...o,
        ...(sorting.field
          ? {
              sort: `${sorting.field},${sorting.order}`,
            }
          : null),
      }),
    options,
  );

  const fetch = useCallback(
    (params = {}) => {
      const page = params?.page ?? 1;
      setPage(page);
      return _fetch({ ...params, page });
    },
    [_fetch],
  );

  const refresh = useCallback(
    (page = 1) => {
      setPage(page);
      return _fetch({ page });
    },
    [_fetch],
  );

  const sort = useCallback(
    (field, order) => {
      setSorting({ field, order });
      return _fetch(field ? { sort: `${field},${order}` } : null);
    },
    [_fetch],
  );

  useEffect(
    function () {
      if (data?.totalCount) {
        setTotalCount(data.totalCount);
      } else setTotalCount(0);
    },
    [data],
  );

  return {
    data,
    sort,
    fetch,
    error,
    reset,
    refresh,
    sorting,
    isFetching,
    pagination,
  };
}
