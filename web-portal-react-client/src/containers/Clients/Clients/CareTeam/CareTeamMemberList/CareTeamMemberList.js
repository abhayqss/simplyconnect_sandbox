import React, { useCallback, useEffect, useMemo, useState } from "react";

import { Badge } from "reactstrap";

import { useAuthUser, useToggle } from "hooks/common";

import { useCareTeamMemberDeletion, useNewCanAddCareTeamMemberQuery } from "hooks/business/care-team";

import { SearchField } from "components";

import { Button } from "components/buttons";

import { ConfirmDialog, SuccessDialog } from "components/dialogs";

import { CareTeamMemberEditor, CareTeamMemberList as CTMemberList } from "containers/CareTeam";

import { ReactComponent as Warning } from "images/alert-yellow.svg";

import "./CareTeamMemberList.scss";
import service from "services/CareTeamMemberService";
import clientService from "services/ClientService";
import { useConversations } from "hooks/business/conversations";
import CareTeamTopCard from "../CareTeamTopCard/CareTeamTopCard";
import { path } from "../../../../../lib/utils/ContextUtils";
import { useHistory } from "react-router-dom";
import { ConversationService } from "factories";
import useCareTeamNonclinicalMemberDeletion from "hooks/business/care-team/useCareTeamNonclinicalMemberDeletion";

const conversationService = ConversationService();
const COLUMNS = ["contactName", "careTeamManager", "organizationName", "communityName", "contacts", "@actions"];

const COLUMNS_MOBILE = ["contactName"];

function CareTeamMemberList({
  type,
  title,
  clientFullName,
  clientTwilioConversationSid,
  clientNonTwilioConversationSid,
  clientId,
  careManagerId,
  showTitle,
  showGroupChat,
  hasActions,
  showTopCard,
  refreshComponents,
  onComponentsRefresh,
}) {
  const user = useAuthUser();
  const history = useHistory();
  const [searchValue, setSearchValue] = useState("");
  const [selected, setSelected] = useState({});

  const [isEditorOpen, toggleEditor] = useToggle();
  const [isSaveSuccessDialogOpen, toggleSaveSuccessDialog] = useToggle();
  const [isDeleteConfirmDialogOpen, toggleDeleteConfirmDialog] = useToggle();
  const [isCancelEditConfirmDialogOpen, toggleCancelEditConfirmDialog] = useToggle();

  const [page, setPage] = useState(1);
  const [totalCount, setTotalCount] = useState(0);
  const [sort, setSort] = useState("");
  const [isFetching, setIsFetching] = useState(false);
  const [memberListData, setMemberListData] = useState([]);
  const [memberListId, setMemberListId] = useState([]);
  const [clientData, setClientData] = useState([]);
  const [listDeleteAndRefreshTopCard, setListDeleteAndRefreshTopCard] = useState(false);
  const [employeeIsOnly, setEmployeeIsOnly] = useState(false);
  const [loginUserInGroup, setLoginUserInGroup] = useState(true);
  const [loginUserInNonGroup, setLoginUserInNonGroup] = useState(true);

  const [activeTopTabNum, setActiveTopTabNum] = useState(0);
  const isCommunityDoctor = user.roleName === "ROLE_COMMUNITY_DOCTOR";

  useEffect(() => {
    clientId &&
      conversationService
        .findLoginUserInGroup({
          clientId,
          clinical: activeTopTabNum === 0,
        })
        .then((res) => {
          if (res.success) {
            if (activeTopTabNum === 0) {
              setLoginUserInGroup(res?.data);
            } else {
              setLoginUserInNonGroup(res?.data);
            }
          }
        });
  }, [clientId, activeTopTabNum]);
  const onSort = (field, order) => {
    setSort(`${field},${order}`);
  };
  const getClientData = () => {
    clientService
      .findById(clientId, {
        response: { extractDataOnly: true },
      })
      .then((res) => {
        setClientData(res);
      });
  };

  useEffect(() => {
    getClientData();
  }, [clientId]);
  const judgeLoginUserIn = () => {
    conversationService
      .findLoginUserInGroup({
        clientId,
        clinical: activeTopTabNum === 0,
      })
      .then((res) => {
        if (res.success) {
          setLoginUserInGroup(res?.data);
        }
      });
  };
  const refresh = (params = { page: 1, size: 15, clientId, affiliation: type }) => {
    if (activeTopTabNum === 0) {
      service.find(params).then((res) => {
        if (res.success) {
          setTotalCount(res.totalCount);
          setMemberListData(res.data);
          const ids = res?.data?.map((item) => {
            return item.employeeId;
          });
          setMemberListId(ids);
          showGroupChat && setListDeleteAndRefreshTopCard(false);
        }
      });
    } else {
      service.findNonClinicalTeam(params).then((res) => {
        setMemberListData(res.data);
      });
    }
  };
  // /AFFILIATED   REGULAR
  useEffect(() => {
    refresh({
      page,
      size: 15,
      clientId,
      affiliation: type,
      name: searchValue,
      sort,
    });
  }, [sort, page, clientId, type, searchValue, refreshComponents, activeTopTabNum]);

  const pagination = useMemo(
    () => ({
      page: page,
      size: 15,
      totalCount,
    }),
    [page, totalCount],
  );

  const { data: canAdd } = useNewCanAddCareTeamMemberQuery({ clientId });

  const { mutateAsync: remove } = useCareTeamMemberDeletion(selected?.id);
  const { mutateAsync: removeNonclinical } = useCareTeamNonclinicalMemberDeletion(selected?.id);

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
    const employeeIds = memberListData.map((item) => {
      return item.employeeId;
    });
    const occurrences = employeeIds.filter((item) => item === member.employeeId).length;
    if (occurrences > 1) {
      setEmployeeIsOnly(false);
    } else {
      setEmployeeIsOnly(true);
    }
    toggleDeleteConfirmDialog();
  }

  const { deleteParticipants } = useConversations();

  const onConfirmDelete = async () => {
    onCloseConfirmDeleteDialog();
    setIsFetching(true);

    if (activeTopTabNum === 0) {
      await remove({ id: selected?.id });
    } else {
      await removeNonclinical({ id: selected?.id });
    }

    /*    // delete careTeamMember from chat group
        employeeIsOnly &&
          (await deleteParticipants(
            clientTwilioConversationSid || clientData?.twilioConversationSid,
            [selected.employeeId],
            careManagerId || clientData.careTeamManager,
          ));*/
    setListDeleteAndRefreshTopCard(true);
    onComponentsRefresh();
    judgeLoginUserIn();
    setIsFetching(false);
  };

  const onCancelConfirmDialog = useCallback(() => {
    setSelected(null);
    toggleCancelEditConfirmDialog();
  }, [toggleCancelEditConfirmDialog]);

  const onCompleteSave = useCallback(() => {
    setSelected(null);
    toggleSaveSuccessDialog();
    getClientData();
  }, [toggleSaveSuccessDialog]);

  const onCloseConfirmDeleteDialog = useCallback(() => {
    setSelected(null);
    toggleDeleteConfirmDialog();
  }, [toggleDeleteConfirmDialog]);

  const onSaveSuccess = useCallback(() => {
    toggleEditor();
    toggleSaveSuccessDialog();
    onComponentsRefresh();
    getClientData();
    judgeLoginUserIn();
  }, [refresh, onComponentsRefresh, getClientData, judgeLoginUserIn, toggleEditor, toggleSaveSuccessDialog]);

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
    ? `${selected?.contactName} will be deleted from the care team and group chat`
    : `The selected care team member is a primary contact for the client. ${selected?.contactName} will be deleted from the care team and will no longer be the primary contact for the client`;

  const goGroupChat = (member) => {
    history.push(path("/chats"), {
      conversationSid: member.conversationSid,
      shouldSelectConversation: true,
    });
  };
  return (
    <>
      <div className="CareTeamMemberList-CaptionHeader">
        {showTitle && (
          <div className="CareTeamMemberList-Title">
            <span className="CareTeamMemberList-TitleText">{title}</span>

            {!!totalCount && (
              <Badge color="info" className="Badge Badge_place_top-right">
                {totalCount}
              </Badge>
            )}
          </div>
        )}
      </div>
      {showTopCard && (
        <CareTeamTopCard
          clientId={clientId}
          canAdd={canAdd?.data}
          listDeleteAndRefreshTopCard={listDeleteAndRefreshTopCard}
          activeTopTabNum={activeTopTabNum}
          setActiveTopTabNum={setActiveTopTabNum}
        />
      )}
      <div className="CareTeamMemberFilter">
        <SearchField
          name="name"
          className="CareTeamMemberFilter-Field"
          placeholder="Search by name"
          value={searchValue}
          onChange={(_, value) => {
            setSearchValue(value);
          }}
          onClear={() => setSearchValue("")}
        />
        <div className="CareTeamMemberList-Actions d-flex ">
          {showGroupChat && (
            <div className=" text-right">
              <Button
                color="success"
                outline
                className="CareTeamMemberList-Action AddCareMemberBtn"
                onClick={() => {
                  goGroupChat({
                    conversationSid:
                      activeTopTabNum === 0
                        ? clientTwilioConversationSid || clientData?.twilioConversationSid || null
                        : clientNonTwilioConversationSid || clientData?.nonclinicalConversationSid || null,
                  });
                }}
                tooltip={{
                  placement: "top",
                  trigger: "click hover",
                  text: "Start group Chat",
                  className: "CareTeamMemberList-Tooltip",
                }}
                disabled={
                  activeTopTabNum === 0
                    ? (!clientTwilioConversationSid && !clientData?.twilioConversationSid) || !loginUserInGroup
                    : (!clientNonTwilioConversationSid && !clientData?.nonclinicalConversationSid) ||
                      !loginUserInNonGroup
                }
              >
                Group Chat
              </Button>
            </div>
          )}
          {canAdd?.data && (
            <div className=" margin-left-10 text-right">
              <Button
                color="success"
                className="CareTeamMemberList-Action AddCareMemberBtn"
                onClick={onAdd}
                tooltip={{
                  placement: "top",
                  trigger: "click hover",
                  text: "Add Care Team Member",
                  className: "CareTeamMemberList-Tooltip",
                }}
                disabled={activeTopTabNum === 1 && isCommunityDoctor}
              >
                Add Member
              </Button>
            </div>
          )}
        </div>
      </div>
      <CTMemberList
        pagination={pagination}
        isFetching={isFetching}
        hasActions={hasActions}
        hasOptedOutIndication
        data={memberListData}
        columns={COLUMNS}
        careManagerId={careManagerId}
        columnsMobile={COLUMNS_MOBILE}
        onSort={onSort}
        onEdit={onEdit}
        onDelete={onDelete}
        onRefresh={refresh}
        setPage={setPage}
      />

      {isEditorOpen && (
        <CareTeamMemberEditor
          clientId={clientId}
          client={clientData}
          organizationId={String(user?.organizationId)}
          clientCommunityId={String(clientData?.communityId)}
          clientOrganizationId={String(clientData?.organizationId)}
          clientFullName={clientFullName}
          listNumber={totalCount}
          careManagerId={careManagerId || clientData.careTeamManager}
          memberListId={memberListId}
          title={"client care team member"}
          memberId={selected && selected.id}
          affiliation={type}
          hasGroup={!!clientData?.twilioConversationSid || !!clientTwilioConversationSid}
          clientTwilioConversationSid={clientTwilioConversationSid}
          onSaveSuccess={onSaveSuccess}
          onClose={onCloseEditor}
          activeTopTabNum={activeTopTabNum}
          haveRoleForRemind={canAdd?.data}
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
              : "The person has been added to care team."
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
