import React, { useCallback } from "react";

import { connect } from "react-redux";

import { useEventNotePrimaryFilter } from "hooks/business/event";

import { PrimaryFilter } from "components";

import { isEmpty } from "lib/utils/Utils";

import "./EventNotePrimaryFilter.scss";

export const NAME = "EVENT_NOTE_PRIMARY_FILTER";

function mapStateToProps(state) {
  return {
    fields: state.event.note.composed.list.dataSource.filter,
  };
}

function EventNotePrimaryFilter({ fields }) {
  const { communityIds, organizationId } = fields;

  const { changeField, communities, organizations } = useEventNotePrimaryFilter();

  const onChangeField = useCallback(
    (name, value) => {
      if (name === "communityIds") {
        const allCount = communities.length;
        const prevCount = communityIds.length;

        const shouldReload = isEmpty(value) ? allCount !== prevCount : !(prevCount === 0 && allCount === value.length);

        changeField(name, value, shouldReload);
      } else changeField(name, value);
    },
    [changeField, communities, communityIds],
  );

  return (
    <PrimaryFilter
      className="EventNotePrimaryFilter"
      communities={communities}
      organizations={organizations}
      onChangeField={onChangeField}
      data={{ organizationId, communityIds }}
    />
  );
}

export default connect(mapStateToProps)(EventNotePrimaryFilter);
