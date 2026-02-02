import "./CareTeamTopCard.scss";
import React, { Fragment, useEffect, useMemo, useState } from "react";
import HasMemberCard from "../TopCardItem/HasMemberCard";
import NoMemberCard from "../TopCardItem/NoMemberCard";
import CareTeamTopModal from "../CareTeamTopModal/CareTeamTopModal";
import service from "../../../../../services/CareTeamMemberService";
import { ConfirmDialog, SuccessDialog } from "../../../../../components/dialogs";
import { ReactComponent as LoaderImg } from "images/loader.svg";
import { ReactComponent as Warning } from "images/alert-yellow.svg";
const CareTeamTopCard = (props) => {
  const { clientId, listDeleteAndRefreshTopCard, activeTopTabNum, setActiveTopTabNum, canAdd } = props;
  // { value: "ROLE_CAREGIVER_CODE", text: "Caregiver", hasMember: false },

  const defaultRoleList = [
    { value: "ROLE_CARE_COORDINATOR", text: "Care Coordinator", hasMember: false },
    { value: "ROLE_NURSE", text: "Nurse", hasMember: false },
    { value: "ROLE_DOCTOR_CODE", text: "Doctor", hasMember: false },
    { value: "ROLE_PHARMACIST_VENDOR_CODE", text: "Pharmacist Vendor", hasMember: false },
    { value: "ROLE_POA_CODE", text: "POA", hasMember: false },
  ];
  const [careTeamRoleList, setCareTeamRoleList] = useState(defaultRoleList);

  const [isAddRoleOpen, setIsAddRoleOpen] = useState(false);
  const [searchRole, setSearchRole] = useState("");
  const [originalCareTeamId, setOriginalCareTeamId] = useState();
  const [isSuccessDialogOpen, setIsSuccessDialogOpen] = useState(false);
  const [isFetching, setIsFetching] = useState(true);
  const [isConfirmDeleteDialogOpen, setIsConfirmDeleteDialogOpen] = useState(false);
  const [deleteCareTeamId, setDeleteCareTeamId] = useState(null);
  const changeActiveTab = (number) => {
    setActiveTopTabNum(number);
  };
  const type = useMemo(() => {
    return "REGULAR";
  }, [activeTopTabNum]);

  const clickToAdd = (role, id) => {
    setSearchRole(role);
    setOriginalCareTeamId(id || null);
    setIsAddRoleOpen(true);
  };
  const clickToDelete = (role, id) => {
    setDeleteCareTeamId(id);
    setIsConfirmDeleteDialogOpen(true);
  };
  const replaceItemsInArray = (A, B) => {
    return A.map((itemA) => {
      const foundItem = B.find((itemB) => itemB.roleCode === itemA.value);
      return foundItem ? foundItem : itemA;
    });
  };
  const getDefaultMemberList = (params) => {
    setIsFetching(true);

    service.findDefaultMember(params).then((res) => {
      if (res.success) {
        const { data } = res;
        if (data.length) {
          const dataFilter = replaceItemsInArray(defaultRoleList, data);
          setCareTeamRoleList(dataFilter);
          setIsFetching(false);
        } else {
          setCareTeamRoleList(defaultRoleList);
          setIsFetching(false);
        }
      }
    });
  };
  const onConfirmAddMember = (id) => {
    service.setPrimaryDefault({ clientId, careTeamId: id }).then((res) => {
      if (res.success) {
        setIsAddRoleOpen(false);
        setIsSuccessDialogOpen(true);
        setIsFetching(false);
      }
    });
  };

  const onRemove = () => {
    service.setPrimaryDefault({ clientId, careTeamId: deleteCareTeamId, removeDefault: true }).then((res) => {
      if (res.success) {
        setIsConfirmDeleteDialogOpen(false);
        getDefaultMemberList({
          clientId,
          affiliation: type,
        });
      }
    });
  };

  useEffect(() => {
    getDefaultMemberList({
      clientId,
      affiliation: type,
    });
  }, [clientId, type]);

  useEffect(() => {
    if (listDeleteAndRefreshTopCard)
      getDefaultMemberList({
        clientId,
        affiliation: type,
      });
  }, [listDeleteAndRefreshTopCard]);

  const onCloseSuccessDialog = () => {
    getDefaultMemberList({
      clientId,
      affiliation: type,
    });
    setIsSuccessDialogOpen(false);
  };
  return (
    <>
      {/* topTab*/}
      <div className={"top-tab"}>
        <div className={activeTopTabNum === 0 ? "active-tab" : "normal-tab"} onClick={() => changeActiveTab(0)}>
          Clinical Team
        </div>
        <div className={activeTopTabNum === 1 ? "active-tab" : "normal-tab"} onClick={() => changeActiveTab(1)}>
          Non-clinical Team
        </div>
      </div>

      {activeTopTabNum === 0 && (
        <>
          <div className="Top-second-title">Primary Care Team</div>
          <div className={"card-container"}>
            {isFetching && <LoaderImg className="Top-LoaderImg" />}
            {careTeamRoleList?.map((item, index) => {
              return (
                <Fragment key={index}>
                  {item?.id ? (
                    <HasMemberCard
                      canAdd={canAdd}
                      className={"card-box"}
                      itemDetail={item}
                      deleteMember={clickToDelete}
                      changeMember={clickToAdd}
                    />
                  ) : (
                    <NoMemberCard canAdd={canAdd} className={"card-box"} itemDetail={item} clickToAdd={clickToAdd} />
                  )}
                </Fragment>
              );
            })}
          </div>
        </>
      )}

      {isAddRoleOpen && (
        <CareTeamTopModal
          isOpen={isAddRoleOpen}
          role={searchRole}
          clientId={clientId}
          originalCareTeamId={originalCareTeamId}
          type={type}
          onCancel={() => {
            setIsAddRoleOpen(false);
          }}
          onConfirm={onConfirmAddMember}
        />
      )}
      {isSuccessDialogOpen && (
        <SuccessDialog
          isOpen
          buttons={[
            {
              text: "OK",
              color: "success",
              onClick: onCloseSuccessDialog,
            },
          ]}
        >
          <div className={"Dialog-Title"}>Operation successful.</div>
        </SuccessDialog>
      )}

      {isConfirmDeleteDialogOpen && (
        <ConfirmDialog
          isOpen
          icon={Warning}
          confirmBtnText="OK"
          title="The member will be remove."
          onConfirm={() => onRemove(deleteCareTeamId)}
          onCancel={() => {
            setDeleteCareTeamId(null);
            setIsConfirmDeleteDialogOpen(false);
          }}
        />
      )}
    </>
  );
};

export default CareTeamTopCard;
