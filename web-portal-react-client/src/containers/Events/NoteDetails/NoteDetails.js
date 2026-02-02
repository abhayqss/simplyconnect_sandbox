import React, { useEffect } from "react";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { isNumber } from "underscore";

import Details from "./components/NoteDetails";

import { useResponse } from "hooks/common";

import noteFormActions from "redux/note/form/noteFormActions";
import noteDetailsActions from "redux/note/details/noteDetailsActions";

import { NOTE_TYPES } from "lib/Constants";

function isGroupNote(data) {
  return data?.typeName === NOTE_TYPES.GROUP_NOTE;
}

function mapStateToProps(state) {
  const { details } = state.note;

  return {
    data: details.data,
    isFetching: details.isFetching,
    fetchCount: details.fetchCount,
    shouldReload: details.shouldReload,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      form: bindActionCreators(noteFormActions, dispatch),
      details: bindActionCreators(noteDetailsActions, dispatch),
    },
  };
}

function NoteDetails({
  data,
  noteId,
  onClose,
  actions,
  clientId,
  isFetching,
  isClientActive,
  onPickEvent,
  shouldReload,
  onLoadSuccess,
  onLoadFailure,
  onSaveSuccess,
  organizationId,
  onUpdateSuccessDialogOpen,
  isWorkflowEvent,
}) {
  const isGroup = isGroupNote(data);
  const isEventsView = isNumber(organizationId);
  const canEdit = data?.canEdit && (isGroup ? isEventsView : isClientActive);

  const onResponse = useResponse({
    onFailure: onLoadFailure,
    onSuccess: onLoadSuccess,
  });

  useEffect(
    function loadDetailsCb() {
      actions.details
        .load({
          clientId,
          noteId,
          organizationId,
        })
        .then(onResponse);
    },
    [noteId, actions, clientId, onResponse, shouldReload, organizationId],
  );

  return (
    <Details
      isWorkflowEvent={isWorkflowEvent}
      data={data}
      noteId={noteId}
      canEdit={canEdit}
      isGroup={isGroup}
      onClose={onClose}
      clientId={clientId}
      isLoading={isFetching}
      onPickEvent={onPickEvent}
      organizationId={organizationId}
      onSaveSuccess={onSaveSuccess}
      onUpdateSuccessDialogOpen={onUpdateSuccessDialogOpen}
    />
  );
}

export default connect(mapStateToProps, mapDispatchToProps)(NoteDetails)
