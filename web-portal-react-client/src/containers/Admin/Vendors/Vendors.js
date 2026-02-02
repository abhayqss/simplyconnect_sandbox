import DocumentTitle from "react-document-title";
import { UpdateSideBarAction } from "../../../actions/admin";
import Table from "components/Table/Table";
import Actions from "components/Table/Actions/Actions";
import SearchField from "components/SearchField/SearchField";
import Breadcrumbs from "components/Breadcrumbs/Breadcrumbs";
import SuccessDialog from "components/dialogs/SuccessDialog/SuccessDialog";
import { Badge, Button, Col, Row, UncontrolledTooltip as Tooltip } from "reactstrap";
import { Link } from "react-router-dom";
import React, { useCallback, useEffect, useState } from "react";
import { getAdminVendorList } from "redux/vendorAdmin/vendorListActions";
import { useDispatch, useSelector } from "react-redux";
import { path } from "../../../lib/utils/ContextUtils";
import "./Vendors.scss";

import VendorsEditor from "./VendorsEditor/VendorsEditor";
import { compact } from "underscore";
import adminVendorService from "../../../services/AdminVendorService";
import { map } from "lodash";
import { useAuthUser } from "../../../hooks/common";
import { ConfirmDialog } from "../../../components/dialogs";

import { ReactComponent as Warning } from "images/alert-yellow.svg";
import { CASE_MANAGER_ROLE, ONLY_VIEW_ROLES, VENDOR_SYSTEM_ROLES } from "../../../lib/Constants";
import { CASE_MANAGER } from "../../../routes/config/Roles";
import adminAssociationsService from "../../../services/AssociationsService";

const Vendor = (props) => {
  const ICON_SIZE = 36;
  const dispatch = useDispatch();
  const tableList = useSelector((state) => state.adminVendor.data);
  const isFetching = useSelector((state) => state.adminVendor.isTableFetching);
  const totalCount = useSelector((state) => state.adminVendor.totalCount);
  const [page, setPage] = useState(1);
  const [pagination, setPagination] = useState({});
  const [sort, setSort] = useState();
  const [isEditorOpen, setIsEditorOpen] = useState(false);
  const [selectVendorId, setSelectVendorId] = useState();
  const [isEdit, setIsEdit] = useState(false);
  const [isSuccessDialogOpen, toggleSuccessDialog] = useState(false);
  const [searchName, setSearchName] = useState("");

  const [vendorStatus, setVendorStatus] = useState(null);

  const [isShowAccessDialogOpen, setIsShowAccessDialogOpen] = useState(false);
  const [isShowRejectDialogOpen, setIsShowRejectDialogOpen] = useState(false);

  const user = useAuthUser();
  const isInAssociationSystem = ONLY_VIEW_ROLES.includes(user.roleName);
  const [canAddVendor, setCanAddVendor] = useState(false);
  useEffect(() => {
    adminVendorService.canAddVendor().then((res) => {
      setCanAddVendor(res.data);
    });
  }, []);

  useEffect(() => {
    const debouncedDispatch = _.debounce(() => {
      dispatch(
        getAdminVendorList({
          name: searchName,
          page: page - 1,
          size: 10,
          sort: sort,
        }),
      );
    }, 800);

    debouncedDispatch();

    return () => {
      debouncedDispatch.cancel();
    };
  }, [page, sort, searchName]);

  useEffect(() => {
    setPagination({
      page: page,
      size: 10,
      totalCount,
    });
  }, [totalCount, page]);

  const onSort = (field, sort) => {
    setSort(`${field},${sort}`);
  };
  const onSaveSuccess = (data) => {
    setIsEditorOpen(false);
    toggleSuccessDialog(true);
    setSelectVendorId(null);
  };

  const onRefresh = (page) => {
    setPage(page);
  };
  const onCloseEditor = () => {
    setIsEditorOpen(false);
    setSelectVendorId(null);
  };

  const onActionConfirm = (id) => {
    // vendorId  approve
    const data = {
      vendorId: id,
      approve: true,
    };
    adminVendorService.judgeVendorPending(data).then((res) => {
      if (res.success) {
        setIsEdit(false);
        setIsEditorOpen(false);
        setIsShowAccessDialogOpen(false);

        dispatch(
          getAdminVendorList({
            name: searchName,
            page: page - 1,
            size: 10,
            sort: sort,
          }),
        );
      }
    });
  };

  const onActionRemove = (id) => {
    const data = {
      vendorId: id,
      approve: false,
    };
    adminVendorService.judgeVendorPending(data).then((res) => {
      if (res.success) {
        setIsEdit(false);

        setIsEditorOpen(false);

        setIsShowRejectDialogOpen(false);
        dispatch(
          getAdminVendorList({
            name: searchName,
            page: page - 1,
            size: 10,
            sort: sort,
          }),
        );
      }
    });
  };

  const onAddVendor = () => {
    setIsEdit(false);
    setIsEditorOpen(true);
    setSelectVendorId(null);
  };
  const onEditVendor = (id, editStatus) => {
    setIsEdit(true);
    setSelectVendorId(id);
    setIsEditorOpen(true);
    if (editStatus !== null && editStatus !== undefined) {
      setVendorStatus(true);
    } else {
      setVendorStatus(false);
    }
  };

  const onChangeFilterField = (name, value) => {
    setSearchName(value);
    setPage(1);
  };
  const onClearSearchField = (name, value) => {
    setSearchName(value);
  };
  const isVendorAdmin = user?.roleName === "ROLE_VENDOR_ADMIN_CODE" || false;

  const onRejectVendor = (id) => {
    setSelectVendorId(id);
    setIsShowRejectDialogOpen(true);
  };

  const onConfirmVendor = (id) => {
    setSelectVendorId(id);
    setIsShowAccessDialogOpen(true);
  };

  return (
    <>
      <DocumentTitle title="Simply Connect | Admin | Vendors">
        <>
          <UpdateSideBarAction />
          <div className="Vendors">
            <Breadcrumbs
              items={[
                { title: "Admin", href: "/admin/organizations", isActive: false },
                { title: "Vendors", href: "/admin/vendors", isActive: true },
              ]}
            />

            <Table
              hasHover
              hasOptions
              hasPagination
              keyField="id"
              noDataText="No results"
              title="Vendors"
              isLoading={isFetching}
              className="VendorsList"
              containerClass="VendorsListContainer"
              data={tableList}
              pagination={pagination}
              columns={[
                {
                  dataField: "name",
                  text: "Name",
                  sort: true,
                  onSort,
                  formatter: (v, row, index, formatExtraData, isMobile) => {
                    return (
                      <>
                        {row.canView ? (
                          <div className="d-flex flex-row overflow-hidden">
                            <Link
                              id={`${isMobile ? "m-" : ""}vendors-${row.id}`}
                              to={path(`/admin/vendors/${row.id}/${row.canEdit ? 1 : 0}`)}
                              className="VendorsList-OrganizationName"
                            >
                              {v}
                            </Link>
                            <Tooltip
                              placement="top"
                              target={`${isMobile ? "m-" : ""}vendors-${row.id}`}
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
                              View vendor details
                            </Tooltip>
                          </div>
                        ) : (
                          <div className="VendorsList-OrganizationName">{v}</div>
                        )}
                      </>
                    );
                  },
                },
                {
                  dataField: "companyId",
                  text: "Company ID",
                  sort: true,
                  align: "right",
                  headerAlign: "right",
                  headerClasses: "VendorsList-CommunitiesColHeader",
                  onSort,
                  formatter: (v, row) => {
                    return v;
                  },
                },
                {
                  dataField: "communityCount",
                  text: "Communities",
                  sort: true,
                  align: "right",
                  headerAlign: "right",
                  headerClasses: "VendorsList-CommunitiesColHeader",
                  onSort: onSort,
                  formatter: (v, row) => {
                    return v ? (
                      <>
                        <a
                          tabIndex={0}
                          data-toggle="tooltip"
                          id={`vendor-${row.id}_com-count`}
                          className="Vendor-not-zero"
                        >
                          {v}
                        </a>
                        <Tooltip
                          trigger="focus"
                          placement="top"
                          target={`vendor-${row.id}_com-count`}
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
                          <div className="VendorsList-AffiliatedOrganizationName-List">
                            {map(row?.referCommunity, (o) => (
                              <div key={o.id} className="VendorsList-AffiliatedOrganizationName">
                                {o.canView ? (
                                  <Link
                                    key={`affiliated-com-${o.id}`}
                                    // to={path(`/admin/organizations/${o.id}`)}
                                    className="VendorsList-AffiliatedOrganizationName"
                                  >
                                    {o.name}
                                  </Link>
                                ) : (
                                  <div style={{ cursor: "default" }} className="VendorsList-AffiliatedOrganizationName">
                                    {o.name}
                                  </div>
                                )}
                              </div>
                            ))}
                          </div>
                        </Tooltip>
                      </>
                    ) : (
                      0
                    );
                  },
                },
                {
                  dataField: "organizationCount",
                  text: "Organizations",
                  sort: true,
                  align: "right",
                  headerAlign: "right",
                  headerClasses: "VendorsList-CommunitiesColHeader",
                  onSort: onSort,
                  formatter: (v, row) => {
                    return v ? (
                      <>
                        <a
                          tabIndex={0}
                          data-toggle="tooltip"
                          id={`vendor-${row.id}_org-count`}
                          className="Vendor-not-zero"
                        >
                          {v}
                        </a>
                        <Tooltip
                          trigger="focus"
                          placement="top"
                          target={`vendor-${row.id}_org-count`}
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
                          <div className="VendorsList-AffiliatedOrganizationName-List">
                            {map(row?.referOrganization, (o) => (
                              <div key={o.id} className="VendorsList-AffiliatedOrganizationName">
                                {o.canView ? (
                                  <Link
                                    key={`affiliated-com-${o.id}`}
                                    className="VendorsList-AffiliatedOrganizationName"
                                  >
                                    {o.name}
                                  </Link>
                                ) : (
                                  <div style={{ cursor: "default" }} className="VendorsList-AffiliatedOrganizationName">
                                    {o.name}
                                  </div>
                                )}
                              </div>
                            ))}
                          </div>
                        </Tooltip>
                      </>
                    ) : (
                      0
                    );
                  },
                },

                {
                  dataField: "status",
                  text: "Status",
                  align: "left",
                  headerAlign: "left",
                  headerStyle: {
                    width: "120px",
                  },
                  sort: true,
                  onSort,
                  headerClasses: "VendorsList-CommunitiesColHeader",
                  formatter: (v, row) => {
                    return (
                      <>
                        {v === 0 && <div className="red-color">Pending</div>}
                        {v === 1 && <div>Verified</div>}
                        {v === 2 && <div className="blue-color">Reject</div>}
                      </>
                    );
                  },
                },
                {
                  dataField: "premium",
                  text: "Premium",
                  sort: true,
                  align: "right",
                  headerAlign: "right",
                  onSort: onSort,
                  headerStyle: {
                    width: "120px",
                  },
                  formatter: (v, row) => {
                    const PREMIUM = v?.charAt(0)?.toUpperCase() + v?.slice(1) || "-";
                    if (row.status === 1) {
                      return <>{PREMIUM}</>;
                    } else {
                      return <>-</>;
                    }
                  },
                },
                {
                  dataField: "@actions",
                  text: "",
                  headerStyle: {
                    width: "120px",
                  },
                  align: "right",
                  formatter: (v, row) => {
                    return (
                      <>
                        <Actions
                          data={row}
                          hasEditAction={row.status === 1 && row.canEdit}
                          iconSize={ICON_SIZE}
                          editHintMessage="Edit vendor details"
                          onEdit={() => onEditVendor(row.id)}
                        />
                        {row.status === 0 && (
                          <div className="VendorList-Actions-Box">
                            {user.roleName === "ROLE_SUPER_ADMINISTRATOR" ||
                            user.roleName === "ROLE_VENDOR_CONCIERGE_CODE" ? (
                              <>
                                <Actions
                                  data={row}
                                  hasConfirm
                                  iconSize={ICON_SIZE}
                                  confirmMessage="Confirm"
                                  onConfirm={() => onConfirmVendor(row.id)}
                                />

                                <Actions
                                  data={row}
                                  hasRemove
                                  iconSize={ICON_SIZE}
                                  removeMessage="Reject"
                                  onRemove={() => onRejectVendor(row.id)}
                                />

                                <Actions
                                  data={row}
                                  hasEditAction
                                  iconSize={ICON_SIZE}
                                  editHintMessage="Edit vendor details"
                                  onEdit={() => onEditVendor(row.id, status)}
                                />
                              </>
                            ) : (
                              <>-</>
                            )}
                          </div>
                        )}

                        {row.status === 2 && <div className="VendorList-Actions-Box">-</div>}
                      </>
                    );
                  },
                },
              ]}
              columnsMobile={["name", "communityCount"]}
              defaultSorted={[
                {
                  dataField: "name",
                  order: "asc",
                },
              ]}
              renderCaption={(title) => {
                return (
                  <div className="VendorsList-Caption">
                    <div className="VendorsList-CaptionHeader">
                      <span className="VendorsList-Title">
                        <span className="VendorsList-TitleText">{title}</span>
                        {pagination.totalCount ? (
                          <Badge color="info" className="Badge Badge_place_top-right">
                            {pagination.totalCount}
                          </Badge>
                        ) : null}
                      </span>
                      {canAddVendor && (
                        <div className="VendorsList-ControlPanel">
                          <Button
                            color="success"
                            className="AddVendorsBtn"
                            disabled={isInAssociationSystem}
                            onClick={onAddVendor}
                          >
                            Add Vendor
                          </Button>
                        </div>
                      )}
                    </div>
                    <div className="VendorsList-Filter">
                      <Row>
                        <Col md={6} lg={4}>
                          <SearchField
                            name="name"
                            value={searchName}
                            placeholder="Search by vendor name"
                            onClear={onClearSearchField}
                            onChange={onChangeFilterField}
                          />
                        </Col>
                      </Row>
                    </div>
                  </div>
                );
              }}
              onRefresh={onRefresh}
            />
            {isEditorOpen && (
              <VendorsEditor
                isOpen={isEditorOpen}
                vendorId={selectVendorId}
                onClose={onCloseEditor}
                onSaveSuccess={onSaveSuccess}
                vendorStatus={vendorStatus}
                onActionRemove={onActionRemove}
                onActionConfirm={onActionConfirm}
              />
            )}

            {isSuccessDialogOpen && (
              <SuccessDialog
                isOpen
                title={`The vendor  has been 
                        ${isEdit ? "updated" : "created"}.`}
                buttons={compact([
                  {
                    text: "Close",
                    outline: true,
                    onClick: () => {
                      toggleSuccessDialog(false);
                      setIsEditorOpen(false);
                      dispatch(
                        getAdminVendorList({
                          name: searchName,
                          page: page - 1,
                          size: 10,
                          sort: sort,
                        }),
                      );
                    },
                  },
                ])}
              />
            )}
            {isShowRejectDialogOpen && (
              <ConfirmDialog
                isOpen
                icon={Warning}
                confirmBtnText="OK"
                title="Vendor's request will be denied."
                onConfirm={() => onActionRemove(selectVendorId)}
                onCancel={() => {
                  setSelectVendorId(null);
                  setIsShowRejectDialogOpen(false);
                }}
              />
            )}
            {isShowAccessDialogOpen && (
              <ConfirmDialog
                isOpen
                icon={Warning}
                confirmBtnText="OK"
                title="Vendor's request will be approved."
                onConfirm={() => onActionConfirm(selectVendorId)}
                onCancel={() => {
                  setSelectVendorId(null);
                  setIsShowAccessDialogOpen(false);
                }}
              />
            )}
          </div>
        </>
      </DocumentTitle>
    </>
  );
};
export default Vendor;
