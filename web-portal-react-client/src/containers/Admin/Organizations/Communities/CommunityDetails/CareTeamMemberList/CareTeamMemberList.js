import React, { useCallback, useEffect, useState } from "react";

import { Badge, Button, UncontrolledTooltip as Tooltip } from "reactstrap";

import { useHistory } from "react-router-dom";

import { useToggle } from "hooks/common";

import {
  useCanAddCareTeamMemberQuery,
  useCanViewCareTeamQuery,
  useCareTeamMemberDeletion,
  useCareTeamMembersQuery,
} from "hooks/business/care-team";

import { SearchField } from "components";

import { ConfirmDialog, SuccessDialog } from "components/dialogs";

import { CareTeamMemberEditor, CareTeamMemberList as CTMemberList } from "containers/CareTeam";

import { isInteger } from "lib/utils/Utils";

import { path } from "lib/utils/ContextUtils";

import { ReactComponent as Warning } from "images/alert-yellow.svg";

import "./CareTeamMemberList.scss";

const COLUMNS = ["contactName", "description", "contacts", "@actions"];

const COLUMNS_MOBILE = ["contactName", "contacts"];

function CareTeamMemberList({ type, title, communityId, organizationId, clientCommunityId, clientOrganizationId }) {
  const [searchValue, setSearchValue] = useState("");
  const [selected, setSelected] = useState({});

  const [isEditorOpen, toggleEditor] = useToggle();
  const [isSaveSuccessDialogOpen, toggleSaveSuccessDialog] = useToggle();
  const [isDeleteConfirmDialogOpen, toggleDeleteConfirmDialog] = useToggle();
  const [isCancelEditConfirmDialogOpen, toggleCancelEditConfirmDialog] = useToggle();

  const history = useHistory();

  const {
    sort,
    fetch,
    refresh,
    pagination,
    isFetching,
    data: { data } = {},
  } = useCareTeamMembersQuery({
    filter: { name: searchValue },
    communityId,
    affiliation: type,
    organizationId,
  });

  const { data: canAdd } = useCanAddCareTeamMemberQuery({
    communityId,
    affiliation: type,
    organizationId,
  });

  const { mutateAsync: remove } = useCareTeamMemberDeletion(selected?.id);

  const { refetch: refetchCanView } = useCanViewCareTeamQuery({ communityId }, { enabled: isInteger(communityId) });

  function onAdd() {
    setSelected(null);

    toggleEditor();
  }

  function onEdit(member) {
    setSelected(member);

    toggleEditor();
  }

  function onDelete(member) {
    setSelected(member);

    toggleDeleteConfirmDialog();
  }

  const onConfirmDelete = async () => {
    onCloseConfirmDeleteDialog();

    await remove({ id: selected?.id });

    const canView = await refetchCanView();
    //const canViewOrganization = await refetchCanViewOrganization()

    if (canView) refresh();
    /*else if (canViewOrganization) {
            history.push(path(`/admin/organizations/${organizationId}`))
        } */ else history.push(path("/admin/organizations"));
  };

  const onCancelConfirmDialog = useCallback(() => {
    setSelected(null);
    toggleCancelEditConfirmDialog();
  }, [toggleCancelEditConfirmDialog]);

  const onCompleteSave = useCallback(() => {
    setSelected(null);
    toggleSaveSuccessDialog();
  }, [toggleSaveSuccessDialog]);

  const onCloseConfirmDeleteDialog = useCallback(() => {
    setSelected(null);
    toggleDeleteConfirmDialog();
  }, [toggleDeleteConfirmDialog]);

  const onSaveSuccess = useCallback(() => {
    refresh();
    toggleEditor();
    toggleSaveSuccessDialog();
  }, [refresh, toggleEditor, toggleSaveSuccessDialog]);

  const onCloseEditor = useCallback(
    (isFormDataChanged = false) => {
      const isEditMode = selected?.id;

      if (isEditMode && !isFormDataChanged) {
        setSelected(null);
      }

      toggleEditor(isFormDataChanged);
      toggleCancelEditConfirmDialog(isFormDataChanged);
    },
    [selected, toggleEditor, toggleCancelEditConfirmDialog],
  );

  const confirmDeleteTitle = !selected?.isPrimaryContact
    ? `${selected?.contactName} will be deleted from the care team`
    : `The selected care team member is a primary contact for the client. ${selected?.contactName} will be deleted from the care team and will no longer be the primary contact for the client`;

  useEffect(() => {
    fetch();
  }, [fetch]);

  return (
    <>
      <div className="CareTeamMemberList-Caption">
        <div className="CareTeamMemberList-CaptionHeader">
          <div className="CareTeamMemberList-Title">
            <span className="CareTeamMemberList-TitleText">{title}</span>

            {!!pagination.totalCount && (
              <Badge color="info" className="Badge Badge_place_top-right">
                {pagination.totalCount}
              </Badge>
            )}
          </div>

          <div className="CareTeamMemberList-ControlPanel">
            {canAdd && (
              <div className="flex-1 text-right">
                <Button id={`addCareTeamMember-${type}`} color="success" className="AddCareMemberBtn" onClick={onAdd}>
                  Add Member
                </Button>

                <Tooltip
                  placement="top"
                  trigger="click hover"
                  className="CareTeamMemberList-Tooltip"
                  target={`addCareTeamMember-${type}`}
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
                  Add Care Team Member
                </Tooltip>
              </div>
            )}
          </div>
        </div>
      </div>
      <div className="CareTeamMemberFilter">
        <SearchField
          name="name"
          className="CareTeamMemberFilter-Field"
          placeholder="Search by name"
          value={searchValue}
          onChange={(_, value) => setSearchValue(value)}
          onClear={() => setSearchValue("")}
        />
      </div>
      <CTMemberList
        pagination={pagination}
        isFetching={isFetching}
        data={data}
        columns={COLUMNS}
        columnsMobile={COLUMNS_MOBILE}
        onSort={sort}
        onEdit={onEdit}
        onDelete={onDelete}
        onRefresh={refresh}
      />
      {isEditorOpen && (
        <CareTeamMemberEditor
          communityId={communityId}
          clientCommunityId={clientCommunityId}
          clientOrganizationId={clientOrganizationId}
          organizationId={organizationId}
          title="community care team member"
          memberId={selected && selected.id}
          affiliation={type}
          onSaveSuccess={onSaveSuccess}
          onClose={onCloseEditor}
          activeTopTabNum={0}
        />
      )}
      {isCancelEditConfirmDialogOpen && (
        <ConfirmDialog
          isOpen
          icon={Warning}
          confirmBtnText="OK"
          title="The updates will not be saved"
          onConfirm={onCloseEditor}
          onCancel={onCancelConfirmDialog}
        />
      )}
      {isDeleteConfirmDialogOpen && (
        <ConfirmDialog
          isOpen
          icon={Warning}
          confirmBtnText="Confirm"
          title={confirmDeleteTitle}
          onConfirm={onConfirmDelete}
          onCancel={onCloseConfirmDeleteDialog}
        />
      )}
      {isSaveSuccessDialogOpen && (
        <SuccessDialog
          isOpen
          title={
            selected?.employeeId
              ? "The notification preferences have been updated."
              : "The person has been added to community care team."
          }
          buttons={[
            {
              text: "OK",
              color: "success",
              onClick: onCompleteSave,
            },
          ]}
        />
      )}
    </>
  );
}

export default CareTeamMemberList;
