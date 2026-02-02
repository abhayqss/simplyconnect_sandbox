import React, { useState, useEffect } from "react";

import { map } from "underscore";

import { Button, UncontrolledTooltip as Tooltip } from "reactstrap";

import Tabs from "components/Tabs/Tabs";
import Modal from "components/Modal/Modal";
import Loader from "components/Loader/Loader";
import Dropdown from "components/Dropdown/Dropdown";
import SuccessDialog from "components/dialogs/SuccessDialog/SuccessDialog";

import NoteEditor from "../../NoteEditor/NoteEditor";
import NoteDescription from "./NoteDescription/NoteDescription";
import NoteChangeHistory from "./NoteChangeHistory/NoteChangeHistory";

import { ReactComponent as EditNoteIcon } from "images/edit-note.svg";

import { RESPONSIVE_BREAKPOINTS } from "lib/Constants";

import "./NoteDetails.scss";

const TAB = {
  DESCRIPTION: 0,
  HISTORY: 1,
};

const TAB_TITLE = {
  [TAB.DESCRIPTION]: "Note Description",
  [TAB.HISTORY]: "Change History",
};

const TAB_TITLE_ADAPTED = {
  [TAB.DESCRIPTION]: "Description",
  [TAB.HISTORY]: "History",
};

const { TABLET_PORTRAIT } = RESPONSIVE_BREAKPOINTS;

function NoteDetails({
  data,
  noteId,
  canEdit,
  onClose,
  isGroup,
  clientId,
  isLoading,
  onPickEvent,
  onSaveSuccess,
  organizationId,
  onUpdateSuccessDialogOpen,
  isWorkflowEvent = false,
}) {
  const [isEditorOpen, setIsEditorOpen] = useState(false);

  const [isUpdateSuccessDialogOpen, setIsUpdateSuccessDialogOpen] = useState(false);

  const [tab, setTab] = useState(0);

  const { width: windowHeight } = document.body.getBoundingClientRect();

  const options = map(TAB, (value) => ({
    value,
    text: TAB_TITLE[value],
    isActive: tab === value,
    onClick: () => setTab(value),
  }));

  const onAddNote = () => setIsEditorOpen(true);
  const onCloseEditor = () => setIsEditorOpen(false);

  const onSaveNoteSuccess = () => {
    onCloseEditor();
    onUpdateSuccessDialogOpen();
    setIsUpdateSuccessDialogOpen(true);
  };

  const onCloseUpdateSuccessDialog = () => {
    setIsUpdateSuccessDialogOpen(false);
    onSaveSuccess();
  };

  useEffect(() => {
    setTab(0);
  }, [noteId]);

  let content = (
    <div className="NoteDetails">
      <div className="NoteDetails-Header">
        <Tabs
          items={options.map((o, i) => ({
            ...o,
            title: windowHeight < 1200 ? TAB_TITLE_ADAPTED[i] : o.text,
          }))}
          onChange={setTab}
          className="NoteDetails-Tabs"
          containerClassName="NoteDetails-TabsContainer"
        />

        <Dropdown
          value={tab}
          items={options}
          toggleText={TAB_TITLE[tab]}
          className="NoteDetails-Dropdown Dropdown_theme_blue"
        />

        {tab === 0 && canEdit && (
          <>
            <EditNoteIcon id="edit-note-icon" className="NoteDetails-CreateEventNoteBtn" onClick={onAddNote} />
            <Tooltip
              placement="top"
              target="edit-note-icon"
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
              Edit Note
            </Tooltip>
          </>
        )}
      </div>

      {tab === 0 &&
        (isLoading ? (
          <Loader style={{ marginTop: 10, marginBottom: 10 }} />
        ) : (
          <NoteDescription data={data} clientId={clientId} onPickEvent={onPickEvent} />
        ))}

      {tab === 1 && <NoteChangeHistory noteId={noteId} clientId={clientId} organizationId={organizationId} />}
      {isEditorOpen && (
        <NoteEditor
          isGroup={isGroup}
          isOpen={isEditorOpen}
          noteId={noteId}
          clientId={clientId}
          clientName={data?.clientName}
          organizationId={organizationId}
          onSaveSuccess={onSaveNoteSuccess}
          onClose={onCloseEditor}
        />
      )}
      {isUpdateSuccessDialogOpen && (
        <SuccessDialog
          isOpen
          title="The note has been updated."
          buttons={[
            {
              text: "Close",
              outline: true,
              onClick: onCloseUpdateSuccessDialog,
            },
          ]}
        />
      )}
    </div>
  );

  return windowHeight < TABLET_PORTRAIT || isWorkflowEvent ? (
    <Modal
      isOpen={true}
      onClose={onClose}
      hasCloseBtn={false}
      title={TAB_TITLE[tab]}
      className="NoteDetailsMobile"
      renderFooter={() => (
        <Button color="success" onClick={onClose}>
          Close
        </Button>
      )}
    >
      {content}
    </Modal>
  ) : (
    content
  );
}

export default NoteDetails;
