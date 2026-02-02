import React, { Fragment, useEffect, useMemo, useState } from "react";

import cn from "classnames";

import { Link } from "react-router-dom";

import { Button, UncontrolledPopover as Popover, UncontrolledTooltip as Tooltip } from "reactstrap";

import { Modal, Table } from "components";

import { EditButton, IconButton } from "components/buttons";

import Avatar from "containers/Avatar/Avatar";

import { path } from "lib/utils/ContextUtils";
import { DateUtils as DU } from "lib/utils/Utils";
import { CONTACT_STATUSES, SYSTEM_ROLES, VENDOR_SYSTEM_ROLES } from "lib/Constants";

import "./ContactList.scss";

import { ReactComponent as LockB } from "images/Admin/lockBlue.svg";
import { ReactComponent as Confirm } from "images/cog.svg";
import { TextField } from "../../../../components/Form";
import service from "../../../../services/ContactService";
import { ErrorDialog, SuccessDialog } from "../../../../components/dialogs";
import { useAuthUser } from "../../../../hooks/common";
import { useQueryClient } from "@tanstack/react-query";

const DATE_TIME_FORMAT = DU.formats.longDateMediumTime12;

const STATUS_COLORS = {
  [CONTACT_STATUSES.ACTIVE]: "#d5f3b8",
  [CONTACT_STATUSES.CONFIRMED]: "#bbdefb",
  [CONTACT_STATUSES.PENDING]: "#ffffff",
  [CONTACT_STATUSES.EXPIRED]: "#fde1d5",
  [CONTACT_STATUSES.INACTIVE]: "#e0e0e0",
};

// Modern Password Requirements Component
const PasswordRequirements = ({ password = "" }) => {
  const requirements = [
    {
      id: "length",
      text: "At least 8 characters",
      test: (pwd) => pwd.length >= 8,
    },
    {
      id: "uppercase",
      text: "At least one uppercase letter",
      test: (pwd) => /[A-Z]/.test(pwd),
    },
    {
      id: "letters",
      text: "At least 2 letters",
      test: (pwd) => (pwd.match(/[a-zA-Z]/g) || []).length >= 2,
    },
    {
      id: "numbers",
      text: "At least 2 numbers",
      test: (pwd) => (pwd.match(/\d/g) || []).length >= 2,
    },
    {
      id: "special",
      text: "At least one special character (!@#$%^&*)",
      test: (pwd) => /[!@#$%^&*]/.test(pwd),
    },
  ];

  return (
    <div className="PasswordRequirements">
      {requirements.map((req) => {
        const isMet = password && req.test(password);
        const isActive = password.length > 0;

        return (
          <div
            key={req.id}
            className={cn("PasswordRequirements-Item", {
              "PasswordRequirements-Item--met": isMet,
              "PasswordRequirements-Item--active": isActive,
            })}
          >
            <div
              className={cn("PasswordRequirements-Checkbox", {
                "PasswordRequirements-Checkbox--checked": isMet,
              })}
            >
              {isMet && (
                <svg className="PasswordRequirements-CheckIcon" viewBox="0 0 12 10" fill="none">
                  <path
                    d="M1 5L4.5 8.5L11 1.5"
                    stroke="white"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  />
                </svg>
              )}
            </div>
            <span className="PasswordRequirements-Text">{req.text}</span>
          </div>
        );
      })}
    </div>
  );
};

export default function ContactList({
  data,
  pagination,
  isFetching,
  organizationId,

  onSort,
  onView,
  onEdit,
  onRefresh,
}) {
  const user = useAuthUser();
  const queryClient = useQueryClient();

  const columns = useMemo(
    () => [
      {
        dataField: "fullName",
        text: "User",
        sort: true,
        headerClasses: "ContactList-FullNameHeader",
        style: (cell, row) =>
          row.status.name === "INACTIVE" && {
            opacity: "0.5",
          },
        onSort,
        formatter: (v, row, index, formatExtraData, isMobile) => {
          return (
            <div className="d-flex align-items-center">
              <Avatar
                name={row.fullName}
                id={row.avatarId}
                className={cn("ContactList-Avatar", row.status.name === "INACTIVE" && "ContactList-Avatar_black-white")}
                {...(row.status.name === "INACTIVE" && { nameColor: "#e0e0e0" })}
              />
              <div
                id={`${isMobile ? "m-" : ""}contact-${row.id}`}
                onClick={() => onView(row.id)}
                className={cn("ContactList-ContactName", row.avatarDataUrl && "margin-left-10")}
              >
                {row.fullName}
              </div>
              <Tooltip
                placement="top"
                target={`${isMobile ? "m-" : ""}contact-${row.id}`}
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
                View contact details
              </Tooltip>
            </div>
          );
        },
      },
      {
        dataField: "systemRoleTitle",
        text: "System Role",
        sort: true,
        onSort,
        style: (cell, row) =>
          row.status.name === "INACTIVE" && {
            opacity: "0.5",
          },
      },
      {
        dataField: "status",
        text: "Status",
        sort: true,
        textAlign: "left",
        headerAlign: "left",
        headerStyle: { width: "10%" },
        onSort,
        style: (cell, row) =>
          row.status.name === "INACTIVE" && {
            opacity: "0.5",
          },
        formatter: (v) => (
          <span
            style={{
              backgroundColor: STATUS_COLORS[v.name] || null,
              ...(v.name === CONTACT_STATUSES.PENDING
                ? {
                    color: "#898989",
                    border: "1px solid #bfbdbd",
                  }
                : {}),
            }}
            className="ContactList-Status"
          >
            {v.title}
          </span>
        ),
      },
      {
        dataField: "memberships",
        text: "Membership",
        align: "right",
        headerAlign: "right",
        headerStyle: { width: "10%" },
        style: (cell, row) =>
          row.status.name === "INACTIVE" && {
            opacity: "0.5",
          },
        formatter: (v, row) => {
          const countSum = v.clientCount + v.communityCount + v.referralsProcessingCount;

          return countSum > 0 ? (
            <Fragment>
              <div id={`contact-${row.id}-membership`} className="ContactList-MembershipLink">
                {countSum}
              </div>

              <Popover
                trigger="legacy"
                placement="right"
                className="ContactMembershipHint"
                innerClassName="ContactMembershipHint-Body"
                target={`contact-${row.id}-membership`}
              >
                {v.communityCount > 0 && (
                  <Fragment>
                    <span className="ContactMembership-Title">Communities</span>
                    {v.communities.map((o) => (
                      <Link
                        key={o.id}
                        className="ContactMembership-Community"
                        to={path(`/admin/organizations/${organizationId}/communities/${o.id}`)}
                      >
                        {o.title}
                      </Link>
                    ))}
                  </Fragment>
                )}
                {v.referralsProcessingCount > 0 && (
                  <Fragment>
                    <span className="ContactMembership-Title">Referrals Processing</span>
                    {v.referralsProcessing.map((o) => (
                      <Link
                        key={o.id}
                        className="ContactMembership-Client"
                        to={path(`/admin/organizations/${organizationId}/communities/${o.id}`)}
                      >
                        {o.title}
                      </Link>
                    ))}
                  </Fragment>
                )}
                {v.clientCount > 0 && (
                  <Fragment>
                    <span className="ContactMembership-Title">Clients</span>
                    {v.clients.map((o) => (
                      <Link key={o.id} className="ContactMembership-Client" to={path(`/clients/${o.id}`)}>
                        {o.title}
                      </Link>
                    ))}
                  </Fragment>
                )}
              </Popover>
            </Fragment>
          ) : null;
        },
      },
      {
        dataField: "login",
        text: "Login",
        sort: true,
        onSort,
        style: (cell, row) => ({
          wordBreak: "break-word",
          ...(row.status.name === "INACTIVE" && {
            opacity: "0.5",
          }),
        }),
      },
      {
        dataField: "lastSessionDate",
        text: "Last session",
        sort: true,
        onSort,
        style: (cell, row) => ({
          wordBreak: "break-word",
          ...(row.status.name === "INACTIVE" && {
            opacity: "0.5",
          }),
        }),
        formatter: (v) => (v ? DU.format(v, DATE_TIME_FORMAT) : ""),
      },
      {
        dataField: "phone",
        text: "Phone",
        align: "right",
        headerAlign: "right",
        headerStyle: { width: "13%" },
        style: (cell, row) => ({
          wordBreak: "break-word",
          ...(row.status.name === "INACTIVE" && {
            opacity: "0.5",
          }),
        }),
      },
      {
        dataField: "@actions",
        text: "",
        headerStyle: {
          width: "100px",
        },
        formatter: (v, row) => {
          return (
            <div className="ContactList-Actions">
              {row.canEdit &&
                !(user.roleName === SYSTEM_ROLES.VENDOR_CONCIERGE && row.systemRoleTitle === "Super Administrator") && (
                  <EditButton
                    id={`edit-contact-${row.id}-btn`}
                    onClick={() => onEdit(row)}
                    tipText="Edit contact's details"
                    className="ContactList-Action"
                  />
                )}

              {localInfo?.canResetPassword && row.status?.name === "ACTIVE" && (
                <IconButton id={"password-change"} Icon={LockB} onClick={() => changePassword(row.id)} />
              )}
              {localInfo?.canResetPassword && row.status?.name === "PENDING" && (
                <IconButton id={"password-change"} Icon={Confirm} onClick={() => allowActive(row.id)} />
              )}
            </div>
          );
        },
      },
    ],
    [onSort, onEdit, onView, organizationId],
  );

  const [modifyId, setModifyId] = useState(null);
  const [changePasswordModelShow, setChangePasswordModelShow] = useState(false);

  const [showManualDialog, setShowManualDialog] = useState(false);
  const [activeManualId, setActiveManualId] = useState(null);
  const [isActivateAccountModelShow, setIsActivateAccountModelShow] = useState(false);
  const [activePassword, setActivePassword] = useState("");
  const [activeConfirmPassword, setActiveConfirmPassword] = useState("");

  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [whetherToVerify, setWhetherToVerify] = useState(false);
  const [showDialog, setShowDialog] = useState(false);
  const [showErrorDialog, setShowErrorDialog] = useState(false);
  const [localInfo, setLocalInfo] = useState();
  const [errorText, setErrorText] = useState("");

  const errorTextConfirm = "Passwords do not match.";
  const passwordReg = /^(?=.*[!@#$%^&*])(?=.*[a-zA-Z].*[a-zA-Z])(?=.*[A-Z])(?=.*\d.*\d).{8,}$/;

  useEffect(() => {
    const localInfo = JSON.parse(localStorage.getItem("AUTHENTICATED_USER"));
    setLocalInfo(localInfo);
  }, [localStorage.getItem("AUTHENTICATED_USER")]);

  const changePassword = (userId) => {
    setChangePasswordModelShow(true);
    setModifyId(userId);
    setPassword("");
    setConfirmPassword("");
    setWhetherToVerify(false);
  };

  const allowActive = (contactId) => {
    setActiveManualId(contactId);
    setIsActivateAccountModelShow(true);
    setActivePassword("");
    setActiveConfirmPassword("");
    setWhetherToVerify(false);
  };

  const passwordNext = (password) => {
    return passwordReg.test(password);
  };

  const isPasswordCurrent = (confirmPassword) => {
    if (confirmPassword && password) {
      return confirmPassword === password;
    }
  };

  const isActivePasswordCurrent = (activeConfirmPassword) => {
    if (activeConfirmPassword && activePassword) {
      return activeConfirmPassword === activePassword;
    }
  };

  const changePasswordModify = () => {
    setWhetherToVerify(true);
    if (password && confirmPassword && passwordNext(password) && isPasswordCurrent(confirmPassword)) {
      service
        .modifyPassword(modifyId, confirmPassword)
        .then((res) => {
          if (res.success) {
            closeModel();
            setShowDialog(true);
          }
        })
        .catch((e) => {
          setErrorText(e.message);
          setShowErrorDialog(true);
        });
    }
  };

  const closeModel = () => {
    setChangePasswordModelShow(false);
    setWhetherToVerify(false);
    setPassword("");
    setConfirmPassword("");
  };

  const closeActiveModel = () => {
    setIsActivateAccountModelShow(false);
    setWhetherToVerify(false);
    setActivePassword("");
    setActiveConfirmPassword("");
  };

  const manualCreate = () => {
    setWhetherToVerify(true);
    if (
      activePassword &&
      activeConfirmPassword &&
      passwordNext(activePassword) &&
      isActivePasswordCurrent(activeConfirmPassword)
    ) {
      service
        .manualCreatePassword(activeManualId, activeConfirmPassword)
        .then((res) => {
          if (res.success) {
            closeActiveModel();
            onRefresh();
            setShowManualDialog(true);
          }
        })
        .catch((e) => {
          setErrorText(e.message);
          setShowErrorDialog(true);
        });
    }
  };

  return (
    <>
      <Table
        hasHover
        hasOptions
        hasPagination
        keyField="id"
        hasCaption={false}
        title="Contacts"
        noDataText="No Contacts"
        isLoading={isFetching}
        className="ContactList"
        containerClass="ContactListContainer"
        data={data}
        pagination={pagination}
        columns={columns}
        columnsMobile={["firstName", "systemRoleTitle"]}
        onRefresh={onRefresh}
      />

      {showDialog && (
        <SuccessDialog
          isOpen
          title="Password changed successfully."
          buttons={[
            {
              text: "OK",
              onClick: () => {
                setShowDialog(false);
              },
            },
          ]}
        />
      )}
      {showManualDialog && (
        <SuccessDialog
          isOpen
          title="Account activation successfully."
          buttons={[
            {
              text: "OK",
              onClick: () => {
                setShowManualDialog(false);
              },
            },
          ]}
        />
      )}

      {showErrorDialog && (
        <ErrorDialog
          isOpen
          title={errorText}
          buttons={[
            {
              text: "OK",
              onClick: () => {
                setShowErrorDialog(false);
              },
            },
          ]}
        />
      )}

      <Modal
        title={"Change Password"}
        isOpen={changePasswordModelShow}
        hasFooter={false}
        hasCloseBtn={true}
        onClose={closeModel}
        className={"ChangePasswordModel"}
      >
        <TextField
          type="password"
          name="password"
          className="LoginForm-TextField"
          label="Password"
          value={password}
          onChange={(name, value) => setPassword(value)}
        />

        <PasswordRequirements password={password} />

        <TextField
          type="password"
          name="confirmPassword"
          className="LoginForm-TextField"
          label="Confirm password"
          value={confirmPassword}
          errorText={whetherToVerify && confirmPassword && !isPasswordCurrent(confirmPassword) && errorTextConfirm}
          onChange={(name, value) => setConfirmPassword(value)}
        />

        <Button
          type="submit"
          color="success"
          className={"changePasswordSubmit"}
          onClick={changePasswordModify}
          disabled={!password || !confirmPassword || !passwordNext(password) || !isPasswordCurrent(confirmPassword)}
        >
          Modify
        </Button>
      </Modal>

      <Modal
        title={"Set password and activate account"}
        isOpen={isActivateAccountModelShow}
        hasFooter={false}
        hasCloseBtn={true}
        onClose={closeActiveModel}
        className={"ChangePasswordModel"}
      >
        <TextField
          type="password"
          name="activePassword"
          className="LoginForm-TextField"
          label="Password"
          value={activePassword}
          onChange={(name, value) => setActivePassword(value)}
        />

        <PasswordRequirements password={activePassword} />

        <TextField
          type="password"
          name="activeConfirmPassword"
          className="LoginForm-TextField"
          label="Confirm password"
          value={activeConfirmPassword}
          errorText={
            whetherToVerify &&
            activeConfirmPassword &&
            !isActivePasswordCurrent(activeConfirmPassword) &&
            errorTextConfirm
          }
          onChange={(name, value) => setActiveConfirmPassword(value)}
        />

        <Button
          type="submit"
          color="success"
          className={"changePasswordSubmit"}
          onClick={manualCreate}
          disabled={
            !activePassword ||
            !activeConfirmPassword ||
            !passwordNext(activePassword) ||
            !isActivePasswordCurrent(activeConfirmPassword)
          }
        >
          Confirm
        </Button>
      </Modal>
    </>
  );
}
