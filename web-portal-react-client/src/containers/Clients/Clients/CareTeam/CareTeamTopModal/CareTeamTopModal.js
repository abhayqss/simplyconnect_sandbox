import "./CareTeamTopModal.scss";
import Modal from "../../../../../components/Modal/Modal";
import { Button } from "reactstrap";
import React, { useEffect, useState } from "react";
import { Table } from "../../../../../components";
import Avatar from "../../../../Avatar/Avatar";
import cn from "classnames";
import service from "services/CareTeamMemberService";

const CareTeamTopModal = (props) => {
  const { role, isOpen, onConfirm, onCancel, clientId, type, originalCareTeamId } = props;
  const [isFetching, setIsFetching] = useState(false);
  const [sort, setSort] = useState("");
  const [selectedMemberId, setSelectedMemberId] = useState(originalCareTeamId);
  const [roleCode, setRoleCode] = useState("");
  const [defaultDataTotal, setDefaultDataTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [defaultDataList, setDefaultDataList] = useState([]);
  const onSort = (field, order) => {
    setSort(`${field},${order}`);
  };
  const onSelected = (id) => {
    setSelectedMemberId(id);
  };

  const getRoleCode = (role) => {
    switch (role) {
      case "Care Coordinator":
        setRoleCode("ROLE_CARE_COORDINATOR");
        break;
      case "Caregiver":
        setRoleCode("ROLE_CAREGIVER_CODE");
        break;
      case "Doctor":
        setRoleCode("ROLE_DOCTOR_CODE");
        break;
      case "POA":
        setRoleCode("ROLE_POA_CODE");
        break;
      case "Nurse":
        setRoleCode("ROLE_NURSE");
        break;
      case "Pharmacist":
        setRoleCode("ROLE_PHARMACIST_VENDOR_CODE");
        break;
      case "Pharmacist Vendor":
        setRoleCode("ROLE_PHARMACIST_VENDOR_CODE");
        break;
      default:
        setRoleCode("ROLE_PREMIUM_CODE");
        break;
    }
  };
  useEffect(() => {
    getRoleCode(role);
  }, [role]);

  const getMemberList = (params = { page: 1, size: 10, clientId, affiliation: type, roleCode }) => {
    service.find(params).then((res) => {
      if (res.success) {
        setDefaultDataTotal(res.totalCount);
        setDefaultDataList(res.data);
      }
    });
  };

  useEffect(() => {
    roleCode &&
      getMemberList({
        page,
        clientId,
        size: 10,
        affiliation: type,
        roleCode,
        sort,
      });
  }, [page, clientId, type, sort, roleCode]);
  const memberColumns = [
    {
      dataField: "contactName",
      text: "Member",
      sort: true,
      headerStyle: {
        width: "380px",
      },
      headerClasses: "MemberList-FullNameHeader",
      onSort,
      formatter: (v, row, index, formatExtraData, isMobile) => {
        return (
          <div className="d-flex align-items-center row-cursor" onClick={() => onSelected(row.id)}>
            <div
              id={`${row.id}-select`}
              className={selectedMemberId === row.id ? "member-select-radio" : "member-no-select-radio "}
            ></div>
            <Avatar name={row.contactName} id={row.avatarId} className={"MemberList-Avatar"} />
            <div
              id={`${isMobile ? "m-" : ""}contact-${row.id}`}
              className={cn("MemberList-ContactName table-item-width-220 text-truncate ")}
            >
              <div title={row.contactName}>{row.contactName || "_"}</div>
              <div className="MemberList-MemberRelation">{row.roleName || "_"}</div>
            </div>
          </div>
        );
      },
    },
    {
      dataField: "organizationName",
      text: "Organization",
      sort: true,
      headerStyle: {
        width: "200px",
      },
      onSort,
      formatter: (v, row, index, formatExtraData, isMobile) => {
        return (
          <div className="global-member-table table-item-width-180 text-truncate " title={v}>
            {v}
          </div>
        );
      },
    },
    {
      dataField: "communityName",
      text: "Community",
      sort: true,
      onSort,
      formatter: (v, row, index, formatExtraData, isMobile) => {
        return (
          <div className="global-member-table table-item-width-180 text-truncate" title={v}>
            {v}
          </div>
        );
      },
    },
    {
      dataField: "phone",
      text: "Contacts",
      headerStyle: {
        width: "300px",
      },
      formatter: (v, row, index, formatExtraData, isMobile) => {
        return (
          <div className="global-member-table">
            {row.phone && <div>+{row.phone}</div>}
            <div className={"table-item-width text-truncate "} title={row.email || "-"}>
              {row.email || "-"}
            </div>
          </div>
        );
      },
    },
  ];
  const CareTeamTopCardMemberForm = () => {
    return (
      <>
        <Table
          className={"top-card-member-form"}
          hasHover
          hasOptions
          hasPagination
          keyField="id"
          hasCaption={false}
          title="Contacts"
          noDataText="No Member"
          isLoading={isFetching}
          containerClass="MemberListContainer"
          data={defaultDataList}
          columns={memberColumns}
          pagination={{ page: page, size: 10, totalCount: defaultDataTotal }}
          columnsMobile={["contactName"]}
          onRefresh={(num) => {
            setPage(num);
          }}
        />
      </>
    );
  };

  return (
    <>
      <Modal
        isOpen={isOpen}
        hasCloseBtn={true}
        onClose={onCancel}
        title={`Default ${role}`}
        className="member-change-content"
        renderFooter={() => (
          <>
            <Button outline color="success" onClick={onCancel}>
              Cancel
            </Button>
            <Button disabled={!selectedMemberId} color="success" onClick={() => onConfirm(selectedMemberId)}>
              Confirm
            </Button>
          </>
        )}
      >
        <div style={{ padding: "20px" }}>
          <CareTeamTopCardMemberForm />
        </div>
      </Modal>
    </>
  );
};
export default CareTeamTopModal;
