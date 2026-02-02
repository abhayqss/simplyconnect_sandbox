import React, { useCallback, useMemo } from "react";

import cn from "classnames";
import { TPrimaryFilter } from "types";

import { map, noop } from "underscore";

import { MultiSelect } from "components";

import "./PrimaryFilter.scss";

function valueTextMapper({ id, name, label }) {
  return { value: id, text: label || name };
}

export default function PrimaryFilter({
  organizations,
  communities,

  data,
  onChangeField,
  onChangeOrganizationField,
  onChangeCommunityField,

  className,
  classNameOrg,

  hasCommunityField,
  isCommunityMultiSelection,

  getCommunityFieldOption,
  getOrganizationFieldOption,
}) {
  const { organizationId } = data;

  const communityFieldName = isCommunityMultiSelection ? "communityIds" : "communityId";

  const mappedOrganizations = useMemo(
    () => map(organizations, getOrganizationFieldOption || valueTextMapper),
    [organizations, getOrganizationFieldOption],
  );

  const mappedCommunities = useMemo(
    () => map(communities, getCommunityFieldOption || valueTextMapper),
    [communities, getCommunityFieldOption],
  );
  const onChangeOrganization = useCallback(
    (v) => {
      if (organizationId !== v) {
        onChangeOrganizationField(v);
        onChangeField("organizationId", v);
      }
    },
    [organizationId, onChangeField, onChangeOrganizationField],
  );

  const onChangeCommunity = useCallback(
    (v) => {
      onChangeCommunityField(v);
      onChangeField(communityFieldName, v);
    },
    [onChangeField, communityFieldName, onChangeCommunityField],
  );

  return (
    <div className={cn("PrimaryFilter", className)}>
      <MultiSelect
        hasValueTooltip
        hasKeyboardSearch
        hasEmptyValue={false}
        name="organizationId"
        value={data.organizationId}
        placeholder="Organization"
        className={cn("PrimaryFilter-Field", classNameOrg)}
        options={mappedOrganizations}
        onChange={onChangeOrganization}
      />
      {hasCommunityField && (
        <MultiSelect
          hasValueTooltip
          name={communityFieldName}
          value={data[communityFieldName]}
          placeholder="Community"
          isMultiple={isCommunityMultiSelection}
          hasAllOption={isCommunityMultiSelection && communities?.length > 1}
          className={cn("PrimaryFilter-Field", { "PrimaryFilter-Field_frozen": communities?.length === 1 })}
          options={mappedCommunities}
          onChange={onChangeCommunity}
        />
      )}
    </div>
  );
}

PrimaryFilter.propTypes = TPrimaryFilter;

PrimaryFilter.defaultProps = {
  areFieldsReversed: false,
  hasCommunityField: true,
  isCommunityMultiSelection: true,
  onChangeField: noop,
  onChangeOrganizationField: noop,
  onChangeCommunityField: noop,
};
