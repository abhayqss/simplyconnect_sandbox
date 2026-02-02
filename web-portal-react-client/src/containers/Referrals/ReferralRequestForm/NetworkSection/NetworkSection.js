import React, { memo, useMemo, useEffect } from "react";

import { isNumber } from "underscore";

import { Col, Row } from "reactstrap";

import Table from "components/Table/Table";
import AlertPanel from "components/AlertPanel/AlertPanel";
import SelectField from "components/Form/SelectField/SelectField";

import useNetworkList from "hooks/business/admin/referrals/useNetworkList";

import { ReactComponent as CrossIcon } from "images/delete.svg";

import "./NetworkSection.scss";

function NetworkSection({ errors, onChange, serviceIds, communityId }) {
  const params = useMemo(
    () => ({
      serviceIds,
      communityId,
    }),
    [communityId, serviceIds],
  );

  const {
    state,
    reset,
    fetchIf,
    changePage: onRefresh,
    removeOrganization,
    selectCommunities,
  } = useNetworkList(params);

  const { selected, fetchCount, isFetching, dataSource: ds } = state;

  const hasFetched = fetchCount > 0;

  const data = ds.getData().map((o) => ({
    ...o,
    selected: selected[o.id] || [],
    error: errors?.[o.id],
  }));

  useEffect(() => {
    if (!ds.data.length) reset();
  }, [ds.data.length, reset]);

  useEffect(() => {
    fetchIf(serviceIds.length && isNumber(communityId));
  }, [fetchIf, communityId, serviceIds]);

  useEffect(
    function () {
      if (hasFetched) {
        onChange(selected);
      }
    },
    [onChange, hasFetched, selected],
  );

  const Alert = useMemo(
    () => () => {
      switch (true) {
        case serviceIds.length < 1:
          return <AlertPanel>Please choose a service to enable service provider selection</AlertPanel>;

        case ds.data.length < 1:
          return (
            <AlertPanel>
              There are no partners configured for the client's community. The request will be shared with all of the
              organizations across Simply Connect platform.
            </AlertPanel>
          );

        default:
          return null;
      }
    },
    [ds.data.length, serviceIds.length],
  );

  return (
    <>
      {!!ds.data.length && (
        <Table
          hasPagination
          keyField="id"
          isLoading={isFetching}
          className="NetworkList"
          containerClass="NetworkListContainer"
          data={data}
          pagination={ds.pagination}
          columns={[
            {
              dataField: "title",
              text: "Organization name",
            },
            {
              text: "Service Provider",
              formatter: (v, row) => {
                const options = row.communities.map((o) => ({ value: o.id, text: o.title }));

                return (
                  <SelectField
                    name={row.id}
                    isMultiple
                    value={row.selected || []}
                    options={options}
                    className="ReferralRequestForm-SelectField"
                    onChange={selectCommunities}
                    errorText={row.error}
                    placeholder="Service Provider"
                  />
                );
              },
            },
            {
              align: "right",
              headerAlign: "right",
              headerStyle: {
                width: "15%",
              },
              formatter: (v, row) =>
                state.cache?.data.length !== 1 ? (
                  <CrossIcon onClick={() => removeOrganization(row.id)} className="NetworkList-Cross" />
                ) : null,
            },
          ]}
          onRefresh={onRefresh}
        />
      )}

      <Row>
        <Col>
          <Alert />
        </Col>
      </Row>
    </>
  );
}

export const PlainNetworkSection = memo(({ title, serviceIds, communityId, communityName, organizationName }) => {
  const data = [
    {
      title: organizationName,
      selected: [communityId],
    },
  ];

  const options = [
    {
      text: communityName,
      value: communityId,
    },
  ];

  return (
    <>
      {serviceIds.length > 0 ? (
        <Table
          hasPagination
          keyField="id"
          title={title}
          className="NetworkList"
          containerClass="NetworkListContainer"
          data={data}
          pagination={{
            page: 1,
            size: 1,
            totalCount: 1,
          }}
          columns={[
            {
              dataField: "title",
              text: "Organization name",
            },
            {
              text: "Service Provider",
              formatter: (_, row) => {
                return (
                  <SelectField
                    isMultiple
                    isDisabled
                    value={row.selected || []}
                    options={options}
                    className="ReferralRequestForm-MultiSelect"
                    placeholder="Service Provider"
                  />
                );
              },
            },
          ]}
        />
      ) : (
        <AlertPanel>Please choose a service to enable network selection.</AlertPanel>
      )}
    </>
  );
});

export default memo(NetworkSection);
