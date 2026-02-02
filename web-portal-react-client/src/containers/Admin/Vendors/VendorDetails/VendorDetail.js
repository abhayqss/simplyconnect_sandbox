import DocumentTitle from "react-document-title";
import { UpdateSideBarAction } from "../../../../actions/admin";
import React, { useEffect, useState } from "react";
import cn from "classnames";
import Breadcrumbs from "../../../../components/Breadcrumbs/Breadcrumbs";
import { ReactComponent as Warning } from "images/alert-yellow.svg";
import { Button, UncontrolledTooltip as Tooltip } from "reactstrap";
import { isEmpty as isEmptyStr, isString } from "../../../../lib/utils/StringUtils";
import { isArray, isEmpty as isEmptyArray } from "../../../../lib/utils/ArrayUtils";
import { isEmpty as isEmptyObject, isObject } from "../../../../lib/utils/ObjectUtils";
import { DateUtils, getAddress, isBoolean, PhoneNumberUtils as PNU } from "../../../../lib/utils/Utils";
import { Loader, Table, Tabs } from "../../../../components";
import { Detail } from "../../../../components/business/common";
import "./VendorDetail.scss";
import { Link, useParams } from "react-router-dom";
import Actions from "components/Table/Actions/Actions";
import { findVendorDetail } from "redux/vendorAdmin/vendorListActions";
import { useDispatch, useSelector } from "react-redux";
import adminVendorService from "services/AdminVendorService";
import VendorConnectionModal from "../VendorConnectionModal/VendorConnectionModal";
import { ConfirmDialog, SuccessDialog } from "components/dialogs";
import CreateContactModal from "../CreateContactModal/CreateContactModal";
import moment from "moment/moment";
import service from "services/Marketplace";
import { ReactComponent as Pencil } from "images/pencil.svg";
import { useAuthUser } from "../../../../hooks/common";
import { CASE_MANAGER_ROLE } from "../../../../lib/Constants";
import ErrorDialog from "../../../../components/dialogs/ErrorDialog/ErrorDialog";
import VendorsEditor from "../VendorsEditor/VendorsEditor";
import { compact } from "underscore";

const { formats } = DateUtils;

const defaultTableList = {
  data: [
    {
      id: 1,
      name: "Aegis Living Bellevue",
      communityOID: 965465,
      bids: 23,
      orgName: "Aegis Senior Communities, LLC",
      state: "American Samoa(AS)",
    },
    {
      id: 2,
      name: "Aegis Living Dana Point",
      communityOID: 9635465,
      bids: 223,
      orgName: "Aegis Senior Communities, LLC",
      state: "American Samoa(AS)",
    },
    {
      id: 3,
      name: "Aegis of San Fr",
      communityOID: 965465,
      bids: 323,
      orgName: "Aegis Senior Communities, LLC",
      state: "American Samoa(AS)",
    },
  ],
  pagination: {
    total: 20,
    page: 1,
    size: 10,
  },
};

const VendorDetail = (props) => {
  const params = useParams();
  const { vendorId, canEdit } = params;
  const ICON_SIZE = 36;
  const dispatch = useDispatch();
  const [tableData, setTableData] = useState(defaultTableList.data);
  const [originLinkData, setOriginLinkData] = useState([]);
  const [page, setPage] = useState(1);
  const [totalCount, setTotalCount] = useState(0);
  const [pagination, setPagination] = useState(defaultTableList.pagination);
  const [tab, setTab] = useState(0);
  const [isFetching, setIsFetching] = useState(false);
  const [shouldReload, setShouldReload] = useState(false);
  const [itemDisReferData, setItemDisReferData] = useState();

  const [isDisaffiliationConfirmDialogOpen, setIsDisaffiliationConfirmDialogOpen] = useState(false);
  const [vendorDetailData, setVendorDetailData] = useState();
  const [isDetailFetching, setIsDetailFetching] = useState(false);

  const [isCreateContactShow, setIsCreateContactShow] = useState(false);
  const [contactId, setContactId] = useState("");
  const [columnsMobile, setColumnsMobile] = useState(["name", "oid"]);
  const [sort, setSort] = useState();
  const [isContactAddSuccessDialogShow, setIsContactAddSuccessDialogShow] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);
  const user = useAuthUser();
  const isCaseManager = CASE_MANAGER_ROLE.includes(user.roleName);
  const [canEditVendor, setCanEditVendor] = useState(Boolean(canEdit));
  const [canLinkVendor, setCanLinkVendor] = useState();
  const [isShowSuccessDialog, setIsShowSuccessDialog] = useState(false);
  const [isEditVendorModalOpen, setIsEditVendorModalOpen] = useState(false);
  const [isSuccessDialogOpen, toggleSuccessDialog] = useState(false);

  const formatStringDate = (value) => (value ? moment(value, "MM/DD/YYYY HH:mm A").toDate().getTime() : null);

  const getVendorDetailData = (vendorId) => {
    setIsDetailFetching(true);
    adminVendorService
      .findById(vendorId)
      .then((res) => {
        setVendorDetailData(res.data);
        setCanEditVendor(res.data.canEdit);
        setCanLinkVendor(res.data.canLink);
        console.log(res.data.canEdit);
        setIsDetailFetching(false);
      })
      .catch((error) => {
        setIsDetailFetching(false);
        setErrorMessage(error.message);
      });
  };

  useEffect(() => {
    getVendorDetailData(vendorId);
  }, [vendorId]);

  useEffect(() => {
    setPagination({
      page: page,
      size: 10,
      totalCount,
    });
  }, [totalCount, page]);

  const getOriginLinkCommunityData = () => {
    adminVendorService.viewVendorDetailCommunities({ vendorId, page: 0, size: 999999999 }).then((res) => {
      if (res.success) {
        setOriginLinkData(res.data);
      }
    });
  };
  const getOriginLinkOrganizationData = () => {
    adminVendorService.viewVendorDetailOrganizations({ vendorId, page: 0, size: 999999999 }).then((res) => {
      if (res.success) {
        setOriginLinkData(res.data);
      }
    });
  };

  useEffect(() => {
    const params = {
      vendorId,
      page: page - 1,
      size: 10,
      sort,
    };
    setIsFetching(true);
    if (tab === 0) {
      getOriginLinkCommunityData();
      adminVendorService.viewVendorDetailCommunities(params).then((res) => {
        if (res.success) {
          setTotalCount(res.totalCount);
          setTableData(res.data);
          setIsFetching(false);
        }
      });
      setColumnsMobile(["name", "oid"]);
    } else if (tab === 1) {
      getOriginLinkOrganizationData();
      adminVendorService.viewVendorDetailOrganizations(params).then((res) => {
        if (res.success) {
          setTotalCount(res.totalCount);
          setTableData(res.data);
          setIsFetching(false);
        }
      });
      setColumnsMobile(["organization", "@actions"]);
    } else if (tab === 2) {
      service.getVendorReferHistory(params).then((res) => {
        if (res.success) {
          setTotalCount(res.totalCount);
          setTableData(res.data);
          setIsFetching(false);
        }
      });
      setColumnsMobile(["data", "community"]);
    } else if (tab === 3) {
      setIsFetching(false);
      adminVendorService.viewVendorContactData(params).then((res) => {
        if (res.success) {
          setTotalCount(res.totalCount);
          setTableData(res.data);
          setIsFetching(false);
        }
      });
      setColumnsMobile(["name", "@actions"]);
    }
  }, [vendorId, tab, page, sort]);

  const onSort = (field, order) => {
    setSort(`${field},${order}`);
  };

  const CommunitiesTable = [
    {
      dataField: "name",
      text: "Name",
      sort: true,
      onSort: onSort,
      formatter: (v, row, index, formatExtraData, isMobile) => {
        return row.canView ? (
          <>
            <div className="d-flex flex-row overflow-hidden">
              <Link
                id={`${isMobile ? "m-" : ""}community-${row.id}`}
                className="CommunityList-CommunityName cursor-pointer"
              >
                {v}
              </Link>
            </div>
            <Tooltip
              placement="top"
              target={`${isMobile ? "m-" : ""}community-${row.id}`}
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
              View community details
            </Tooltip>
          </>
        ) : (
          <div title={v} id={"comm" + row.id} className="CommunityList-CommunityName">
            {v}
          </div>
        );
      },
    },
    {
      dataField: "oid",
      text: "Community OID",
      sort: true,
      onSort: onSort,
    },
    {
      dataField: "stateTitle",
      text: "State",
      /* sort: true,
      onSort: onSort,*/
    },
    {
      dataField: "@actions",
      text: "",
      headerStyle: {
        width: "60px",
      },
      align: "right",
      formatter: (v, row) => {
        console.log(canEditVendor);
        return (
          <div onClick={() => setItemDisReferData(row)}>
            <Actions
              data={v}
              hasUnlink={canEditVendor || isCaseManager}
              iconSize={ICON_SIZE}
              unlinkMessage="Disaffiliate from this community"
              onUnlink={onAsset}
            />
          </div>
        );
      },
    },
  ];

  const OrganizationsTable = [
    {
      dataField: "organization",
      text: "Name",
      sort: true,
      onSort: onSort,
      formatter: (v, row, index, formatExtraData, isMobile) => {
        return row.canView ? (
          <>
            <div className="d-flex flex-row overflow-hidden">
              <Link
                id={`${isMobile ? "m-" : ""}community-${row.id}`}
                // to={path(`/admin/organizations/${row.id}`)}
                className="CommunityList-CommunityName cursor-pointer"
              >
                {v?.name || "-"}
              </Link>
            </div>
            <Tooltip
              placement="top"
              target={`${isMobile ? "m-" : ""}community-${row.id}`}
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
              View organization details
            </Tooltip>
          </>
        ) : (
          <div title={v?.name || "-"} id={"comm" + row.id} className="CommunityList-CommunityName">
            {v?.name || "-"}
          </div>
        );
      },
    },
    {
      dataField: "stateName",
      text: "State",
      formatter: (v, row) => {
        return <>{v || row?.stateName || "-"}</>;
      },
    },
    {
      dataField: "associateDate",
      text: "Link Date",
      formatter: (v) => {
        return <>{moment(v).format("MM/DD/YYYY")}</>;
      },
    },
    {
      dataField: "@actions",
      text: "",
      headerStyle: {
        width: "60px",
      },
      align: "right",
      formatter: (v, row) => {
        return (
          <div onClick={() => setItemDisReferData(row)}>
            <Actions
              data={v}
              hasUnlink={canEditVendor || isCaseManager}
              iconSize={ICON_SIZE}
              unlinkMessage="Disaffiliate from this organization"
              onUnlink={onAsset}
            />
          </div>
        );
      },
    },
  ];
  const ReferHistoryTable = [
    {
      dataField: "referTime",
      text: "Date",
      sort: true,
      onSort: onSort,
      formatter: (v) => v && moment(v).format("MM/DD/YYYY  HH:mm"),
    },
    {
      dataField: "communityName",
      text: "Community",
      sort: true,
      onSort: onSort,
      formatter: (v) => (v ? v : "-"),
    },
    {
      dataField: "organizationName",
      text: "Organization",
      sort: true,
      onSort: onSort,
      formatter: (v) => (v ? v : "-"),
    },
  ];
  const TeamTable = [
    {
      dataField: "fullName",
      text: "Name",
      sort: true,
      onSort: onSort,
      style: (cell, row) =>
        row.status === 3 && {
          opacity: "0.5",
        },
      formatter: (v, row) => {
        return <>{v}</>;
      },
    },
    {
      dataField: "login",
      text: "Login Email",
      sort: true,
      onSort: onSort,
      style: (cell, row) =>
        row.status === 3 && {
          opacity: "0.5",
        },
      formatter: (v, row) => {
        return <>{row.login || "-"}</>;
      },
    },
    {
      dataField: "careTeamRoleCode",
      text: "Role",
      style: (cell, row) =>
        row.status === 3 && {
          opacity: "0.5",
        },
      formatter: (v, row) => {
        let code = "Clinical Staff";
        switch (v) {
          case "ROLE_VENDOR_CODE":
            break;
          case "ROLE_PHARMACIST_VENDOR_CODE":
            code = "Pharmacist";
            break;
          case "ROLE_DOCTOR_CODE":
            code = "Doctor";
            break;
          default:
            code = "Non-Clinical Staff";
            break;
        }
        return <>{code}</>;
      },
    },
    {
      dataField: "lastSessionDateTime",
      text: "Last session",
      sort: true,
      onSort: onSort,
      style: (cell, row) =>
        row.status === 3 && {
          opacity: "0.5",
        },
      formatter: (v, row) => {
        return <>{formatStringDate(v) || "-"}</>;
      },
    },
    {
      dataField: "status",
      text: "Status",
      headerStyle: {
        width: "140px",
      },
      style: (cell, row) =>
        row.status === 3 && {
          opacity: "0.5",
        },
      align: "left",
      formatter: (v, row) => {
        return (
          <>
            {v === 0 && <div className="Vendor-active-btn">Active</div>}
            {v === 1 && <div className="Vendor-pending-btn">Pending</div>}
            {v === 2 && <div className="Vendor-expired-btn">Expired</div>}
            {v === 3 && <div className="Vendor-inactive-btn">Inactive</div>}
          </>
        );
      },
    },
    {
      dataField: "@actions",
      text: "",
      headerStyle: {
        width: "60px",
      },
      align: "right",
      style: (cell, row) =>
        ((row.status === 1 && vendorDetailData?.premium === "static") ||
          (row.status === 2 && vendorDetailData?.premium === "static")) && {
          opacity: "0.5",
        },
      formatter: (v, row) => {
        return (
          <>
            {canEditVendor && vendorDetailData?.premium === "static" && (row.status === 2 || row.status === 1) ? (
              <div className="disabledPencil">
                <Pencil className="disabledPencil"></Pencil>
              </div>
            ) : (
              <div
                onClick={() => {
                  setContactId(row.id);
                  setSelected(row);
                }}
              >
                <Actions
                  data={v}
                  editDisabled={false}
                  hasEditAction={canEditVendor}
                  iconSize={ICON_SIZE}
                  editHintMessage="Edit this contact"
                  onEdit={onEditContact}
                />
              </div>
            )}
          </>
        );
      },
    },
  ];
  const ALL_DATA_TABLE = [CommunitiesTable, OrganizationsTable, ReferHistoryTable, TeamTable];

  const [tableColumns, setTableColumns] = useState(ALL_DATA_TABLE[0]);

  const [isVendorConnectionModalShow, setIsVendorConnectionModalShow] = useState(false);

  const [selected, setSelected] = useState();

  const vendorTabs = [
    { title: "Communities", isActive: tab === 0, hasError: false },
    { title: "Organizations", isActive: tab === 1, hasError: false },
    { title: "Referral History", isActive: tab === 2, hasError: false },
    { title: "Team", isActive: tab === 3, hasError: false },
  ];

  const AddVendorAssociateCommunities = (params) => {
    setIsFetching(true);
    adminVendorService.AddVendorAssociateCommunities(params).then((res) => {
      if (res.success) {
        setIsVendorConnectionModalShow(false);
        getOriginLinkCommunityData();
        const params2 = {
          page: 0,
          size: 10,
          vendorId,
        };
        adminVendorService.viewVendorDetailCommunities(params2).then((res) => {
          setTotalCount(res.totalCount);
          setTableData(res.data);
          setIsFetching(false);
        });
      }
    });
  };

  const AddVendorAssociateOrganizations = (params) => {
    setIsFetching(true);
    adminVendorService.AddVendorAssociateOrganizations(params).then((res) => {
      if (res.success) {
        getOriginLinkOrganizationData();
        setIsVendorConnectionModalShow(false);
        const params2 = {
          page: 0,
          size: 10,
          vendorId,
        };
        adminVendorService.viewVendorDetailOrganizations(params2).then((res) => {
          setTotalCount(res.totalCount);
          setTableData(res.data);
          setIsFetching(false);
        });
      }
    });
  };

  const onCloseVendorConnectionModal = () => {
    setIsVendorConnectionModalShow(false);
  };

  const onShowAssociationConnectionModal = () => {
    if (tab !== 3) {
      setIsVendorConnectionModalShow(true);
    } else if (tab === 3) {
      setIsCreateContactShow(true);
      setSelected([]);
      setContactId("");
    }
  };
  const onAsset = () => {
    setIsDisaffiliationConfirmDialogOpen(true);
  };

  const onDisaffiliationConfirmDialogConfirm = () => {
    if (tab === 0) {
      adminVendorService
        .DisAddVendorAssociateCommunities({
          vendorId,
          disReferId: itemDisReferData.id,
          organizationId: itemDisReferData.organizationId,
        })
        .then((res) => {
          if (res.success) {
            setIsDisaffiliationConfirmDialogOpen(false);
            setIsShowSuccessDialog(true);
          }
        })
        .catch((error) => {
          setIsDisaffiliationConfirmDialogOpen(false);
          setErrorMessage(error.message);
        });
    } else if (tab === 1) {
      adminVendorService
        .DisAddVendorAssociateOrganizations({
          vendorId,
          disReferId: itemDisReferData?.organization?.id,
        })
        .then((res) => {
          if (res.success) {
            setIsDisaffiliationConfirmDialogOpen(false);
            setIsShowSuccessDialog(true);
          }
        })
        .catch((error) => {
          setIsDisaffiliationConfirmDialogOpen(false);
          setErrorMessage(error.message);
        });
    } else if (tab === 2) {
      service
        .getVendorReferHistory({
          vendorId,
          page: 0,
          size: 10,
        })
        .then((res) => {
          if (res.success) {
            setTotalCount(res.totalCount);
            setTableData(res.data);
            setIsFetching(false);
          }
        })
        .catch((error) => {
          setErrorMessage(error.message);
        });
    }
  };

  const onDisaffiliationSuccessDialogOK = () => {
    setIsShowSuccessDialog(false);
    if (tab === 0) {
      getOriginLinkCommunityData();
      const params2 = {
        page: 0,
        size: 10,
        vendorId,
      };
      adminVendorService
        .viewVendorDetailCommunities(params2)
        .then((res) => {
          if (res.success) {
            setTotalCount(res.totalCount);
            setTableData(res.data);
            setIsFetching(false);
          }
        })
        .catch((error) => {
          setErrorMessage(error.message);
          setTotalCount(0);
          setTableData([]);
          setIsFetching(false);
        });
    } else if (tab === 1) {
      getOriginLinkOrganizationData();
      const params2 = {
        page: 0,
        size: 10,
        vendorId,
      };
      adminVendorService
        .viewVendorDetailOrganizations(params2)
        .then((res) => {
          if (res.success) {
            setTotalCount(res.totalCount);
            setTableData(res.data);
            setIsFetching(false);
          }
        })
        .catch((error) => {
          setErrorMessage(error.message);
          setTotalCount(0);
          setTableData([]);
          setIsFetching(false);
        });
    }
  };
  const onDisaffiliationConfirmDialogCancel = () => {
    setIsDisaffiliationConfirmDialogOpen(false);
    setItemDisReferData(null);
  };

  const isLoading = () => {
    return isFetching || shouldReload || isDetailFetching;
  };
  const isEmpty = (v, opts = {}) => {
    const { allowEmptyBool = true } = opts;

    if ([null, undefined, NaN].includes(v)) return true;
    if (isString(v)) return isEmptyStr(v);
    if (isArray(v)) return isEmptyArray(v);
    if (isObject(v)) return isEmptyObject(v);
    if (isBoolean(v)) return !v && allowEmptyBool;

    return false;
  };
  const can = {
    add: {
      value: true,
    },
  };
  const onChangeTab = (tabIndex) => {
    if (tab === tabIndex) return;
    setPage(1);
    setSort(null);
    setTableData([]);
    if (tabIndex === 0) {
      setTab(0);
      setTableColumns(CommunitiesTable);
    } else {
      setTab(tabIndex);
      setTableColumns(ALL_DATA_TABLE[tabIndex]);
    }
  };

  const onContactSubmit = () => {
    setIsCreateContactShow(false);
    setIsContactAddSuccessDialogShow(true);
  };
  const onContactSubmitSuccess = () => {
    setIsContactAddSuccessDialogShow(false);
    setContactId(null);
    refreshVendorContactData();
  };

  const refreshVendorContactData = () => {
    if (page === 1) {
      adminVendorService.viewVendorContactData({ vendorId, page: page - 1, size: 10 }).then((res) => {
        if (res.success) {
          setTotalCount(res.totalCount);
          setTableData(res.data);
          setIsFetching(false);
        }
      });
    } else {
      setPage(1);
    }
  };

  const onEditContact = () => {
    setIsCreateContactShow(true);
  };

  const editContactSuccess = () => {
    setIsCreateContactShow(false);
    setIsContactAddSuccessDialogShow(true);
  };

  const onCloseEditor = () => {
    setIsEditVendorModalOpen(false);
  };
  const onSaveSuccess = (data) => {
    setIsEditVendorModalOpen(false);
    setTab(0);
    setTableColumns(CommunitiesTable);
    toggleSuccessDialog(true);
  };

  const editVendorData = () => {
    setIsEditVendorModalOpen(true);
  };
  return (
    <>
      <DocumentTitle title={"Simply Connect | Admin | Vendors | Vendor Details"}>
        <div className={cn("VendorsDetails")}>
          <UpdateSideBarAction />
          {isLoading() && <Loader isCentered hasBackdrop />}
          {!isLoading() && !isEmpty(vendorDetailData) && (
            <>
              {tab === 0 && (
                <VendorConnectionModal
                  tab={0}
                  isOpen={isVendorConnectionModalShow}
                  originLinkData={originLinkData}
                  AddVendorAssociateCommunities={AddVendorAssociateCommunities}
                  AddVendorAssociateOrganizations={AddVendorAssociateOrganizations}
                  vendorId={vendorId}
                  onClose={onCloseVendorConnectionModal}
                />
              )}
              {tab === 1 && (
                <VendorConnectionModal
                  tab={1}
                  isOpen={isVendorConnectionModalShow}
                  originLinkData={originLinkData}
                  AddVendorAssociateCommunities={AddVendorAssociateCommunities}
                  AddVendorAssociateOrganizations={AddVendorAssociateOrganizations}
                  vendorId={vendorId}
                  onClose={onCloseVendorConnectionModal}
                />
              )}

              <Breadcrumbs
                items={[
                  { title: "Vendors", href: "/admin/vendors", isEnabled: true },
                  { title: "Vendor detail", href: "/admin/vendors", isActive: true },
                ]}
              />

              <div className="VendorDetail-Header">
                <div className="VendorDetail-Title">
                  <div className="VendorDetail-TitleText" title={vendorDetailData?.name}>
                    {vendorDetailData?.name || "-"}
                  </div>
                </div>
                <div className="VendorDetail-ControlPanel">
                  {canEditVendor && (
                    <Button color="success" className="AssociationDetail-EditButton" onClick={editVendorData}>
                      Edit Details
                    </Button>
                  )}
                </div>
              </div>

              <div className="VendorsDetail-Body">
                <div className="margin-bottom-65">
                  <Detail
                    className="VendorDetailData"
                    titleClassName="VendorDetailData-Title"
                    valueClassName="VendorDetailData-Value"
                    title="COMPANY ID"
                  >
                    {vendorDetailData.companyId || "-"}
                  </Detail>

                  <Detail
                    className="VendorDetailData"
                    titleClassName="VendorDetailData-Title"
                    valueClassName="VendorDetailData-Value"
                    title="WEBSITE"
                  >
                    {vendorDetailData.website || "-"}
                  </Detail>
                  <Detail
                    className="VendorDetailData"
                    titleClassName="VendorDetailData-Title"
                    valueClassName="VendorDetailData-Value"
                    title="EMAIL"
                  >
                    {vendorDetailData.email || "-"}
                  </Detail>
                  <Detail
                    className="VendorDetailData"
                    titleClassName="VendorDetailData-Title"
                    valueClassName="VendorDetailData-Value"
                    title="PHONE"
                  >
                    {PNU.formatPhoneNumber(vendorDetailData.phone) || "-"}
                  </Detail>

                  <Detail
                    className="VendorDetailData"
                    titleClassName="VendorDetailData-Title"
                    valueClassName="VendorDetailData-Value"
                    title="COMPANY TYPE"
                  >
                    {vendorDetailData?.companyType?.name || "-"}
                  </Detail>
                  <Detail
                    className="VendorDetailData"
                    titleClassName="VendorDetailData-Title"
                    valueClassName="VendorDetailData-Value"
                    title="EXP-YEAR"
                  >
                    {vendorDetailData.expYear || "-"}
                  </Detail>

                  <Detail
                    className="VendorDetailData"
                    titleClassName="VendorDetailData-Title"
                    valueClassName="VendorDetailData-Value"
                    title="CATEGORY"
                  >
                    {_.map(vendorDetailData?.vendorTypes, "name").join(", ")}
                  </Detail>

                  {vendorDetailData?.serviceTypes.length > 0 && (
                    <Detail
                      className="VendorDetailData"
                      titleClassName="VendorDetailData-Title"
                      valueClassName="VendorDetailData-Value"
                      title="SERVICE"
                    >
                      {_.map(vendorDetailData?.serviceTypes, "name").join(", ")}
                    </Detail>
                  )}
                  <Detail
                    className="VendorDetailData"
                    titleClassName="VendorDetailData-Title"
                    valueClassName="VendorDetailData-Value"
                    title="ADDRESS"
                  >
                    {vendorDetailData.address ||
                      getAddress(
                        {
                          city: vendorDetailData?.city,
                          street: vendorDetailData?.street,
                          state: vendorDetailData?.stateName,
                          zip: vendorDetailData?.zipCode,
                        },
                        ",",
                      ) ||
                      "-"}
                  </Detail>

                  {vendorDetailData.logoDataUrl && (
                    <Detail
                      className="VendorDetailData"
                      titleClassName="VendorDetailData-Title"
                      valueClassName="VendorDetailData-Value"
                      title="LOGO"
                    >
                      <img className="VendorDetailData-Logo" src={vendorDetailData.logoDataUrl} alt="" />
                    </Detail>
                  )}
                </div>

                <Table
                  hasHover
                  hasOptions
                  hasPagination
                  keyField="id"
                  title=""
                  isLoading={isFetching}
                  className="CommunityList"
                  containerClass="CommunityListContainer"
                  data={tableData}
                  pagination={pagination}
                  columns={tableColumns}
                  columnsMobile={columnsMobile}
                  sort={sort}
                  onRefresh={(num) => {
                    setPage(num);
                  }}
                  renderCaption={(title) => {
                    const buttonTextMap = {
                      0: "Link Communities",
                      1: "Link Organizations",
                      3: "Create Contact",
                    };

                    const shouldShowButton = (currentTab) => {
                      //vendor under role just can't link, caseManager can link can't create
                      if (canEditVendor && !canLinkVendor) {
                        return currentTab === 3;
                      } else {
                        if (isCaseManager) {
                          return currentTab !== 3;
                        } else {
                          return true;
                        }
                      }
                    };

                    return (
                      <div className="VendorCommunityList-Caption">
                        <div className="VendorCommunityList-CaptionHeader">
                          <div className="VendorCommunityList-Title">
                            <Tabs
                              containerClassName="VendorDetailForm-TabsContainerm"
                              items={vendorTabs}
                              onChange={onChangeTab}
                            />
                          </div>
                        </div>
                        {tab !== 2 && shouldShowButton(tab) && (
                          <div className="CommunityList-ControlPanel">
                            <Button color="success" onClick={onShowAssociationConnectionModal}>
                              {buttonTextMap[tab]}
                            </Button>
                          </div>
                        )}
                      </div>
                    );
                  }}
                />

                {isDisaffiliationConfirmDialogOpen && (
                  <ConfirmDialog
                    isOpen={isDisaffiliationConfirmDialogOpen}
                    icon={Warning}
                    confirmBtnText="Confirm"
                    cancelBtnText="Cancel"
                    title={`Do you want to unlink ${itemDisReferData?.name || itemDisReferData?.organization.name} from ${vendorDetailData.name}?`}
                    onConfirm={onDisaffiliationConfirmDialogConfirm}
                    onCancel={onDisaffiliationConfirmDialogCancel}
                  />
                )}

                {errorMessage && (
                  <ErrorDialog
                    isOpen={errorMessage}
                    title={errorMessage}
                    buttons={[
                      {
                        color: "success",
                        text: "Close",
                        onClick: () => setErrorMessage(null),
                      },
                    ]}
                  />
                )}

                {isCreateContactShow && (
                  <CreateContactModal
                    isOpen={isCreateContactShow}
                    hieAgreement={vendorDetailData?.hieAgreement}
                    onSubmit={onContactSubmit}
                    vendorDetailData={vendorDetailData}
                    editContactSuccess={editContactSuccess}
                    onClose={() => setIsCreateContactShow(false)}
                    vendorId={vendorId}
                    contactId={contactId}
                    isShowRoleSelect={true}
                    isPendingContact={selected?.status === 1} //2
                    isExpiredContact={selected?.status === 2}
                  ></CreateContactModal>
                )}

                {isContactAddSuccessDialogShow && (
                  <SuccessDialog
                    isOpen={isContactAddSuccessDialogShow}
                    title={contactId ? `Contact member edited successfully.` : `Contact member invited successfully.`}
                    buttons={[{ text: "Ok", onClick: () => onContactSubmitSuccess() }]}
                  />
                )}

                {isShowSuccessDialog && (
                  <SuccessDialog
                    isOpen={isShowSuccessDialog}
                    title={"Successfully Unlinked."}
                    buttons={[
                      {
                        text: "OK",
                        onClick: () => {
                          onDisaffiliationSuccessDialogOK();
                        },
                      },
                    ]}
                  />
                )}
              </div>
            </>
          )}

          {isEditVendorModalOpen && (
            <VendorsEditor
              isOpen={isEditVendorModalOpen}
              vendorId={vendorId}
              isEditVendor={true}
              onClose={onCloseEditor}
              onSaveSuccess={onSaveSuccess}
              vendorStatus={false}
            />
          )}
          {isSuccessDialogOpen && (
            <SuccessDialog
              isOpen
              title={`The vendor has been updated.`}
              buttons={compact([
                {
                  text: "Close",
                  outline: true,
                  onClick: () => {
                    toggleSuccessDialog(false);
                    // setIsEditVendorModalOpen(false);
                    getVendorDetailData(vendorId);
                  },
                },
              ])}
            />
          )}
        </div>
      </DocumentTitle>
    </>
  );
};

export default VendorDetail;
