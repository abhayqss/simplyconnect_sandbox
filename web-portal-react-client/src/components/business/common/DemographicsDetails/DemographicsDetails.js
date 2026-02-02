import React from "react";

import cn from "classnames";

import { Detail as BaseDetail } from "components/business/common";

import { getAddress } from "lib/utils/Utils";

import "./DemographicsDetails.scss";

function Detail({ children, ...props }) {
  return (
    <BaseDetail
      {...props}
      className={cn("DemographicsDetail", props.className)}
      titleClassName={cn("DemographicsDetail-Title", props.titleClassName)}
      valueClassName={cn("DemographicsDetail-Value", props.valueClassName)}
    >
      {children}
    </BaseDetail>
  );
}

export default function DemographicsDetails({ data = {} }) {
  return (
    <>
      <Detail title="GENDER">{data.gender}</Detail>

      <Detail title="DATE OF BIRTH">{data.birthDate}</Detail>

      <Detail title="MARITAL STATUS">{data.maritalStatus}</Detail>

      <Detail title="RACE">{data.race}</Detail>

      <Detail title="CELL PHONE">{data.cellPhone}</Detail>

      <Detail title="HOME PHONE">{data.phone}</Detail>

      <Detail title="ORGANIZATION">{data.organization ?? data.organizationTitle}</Detail>

      <Detail title="COMMUNITY">{data.community ?? data.communityTitle}</Detail>

      <Detail title="ADDRESS">
        {data.address &&
          getAddress(
            {
              ...data.address,
              state: data.address.stateName,
            },
            ",",
          )}
      </Detail>

      <Detail title="EMAIL">{data.email}</Detail>
    </>
  );
}
