import { Breadcrumbs, Loader, Table, Tabs } from "components";
import { Button } from "reactstrap";
import { ReactComponent as Warning } from "images/alert-yellow.svg";
import { Detail } from "../../../../components/business/common";
import { useParams } from "react-router-dom";
import DocumentTitle from "react-document-title";
import "./AssociationsDetail.scss";
import Actions from "../../../../components/Table/Actions/Actions";
import { DateUtils, getAddress, isBoolean, PhoneNumberUtils as PNU } from "../../../../lib/utils/Utils";
import { isEmpty as isEmptyStr, isString } from "../../../../lib/utils/StringUtils";
import { isArray, isEmpty as isEmptyArray } from "../../../../lib/utils/ArrayUtils";
import { isEmpty as isEmptyObject, isObject } from "../../../../lib/utils/ObjectUtils";
import React, { useEffect, useState } from "react";
import { PAGINATION } from "../../../../lib/Constants";
import { isNumber } from "underscore";
import cn from "classnames";
import { UpdateSideBarAction } from "actions/admin";
import AssociationEditor from "../AssociationEditor/AssociationEditor";
import { ConfirmDialog, SuccessDialog, WarningDialog } from "../../../../components/dialogs";
import AssociationConnectionModal from "../AssociationConnectionModal/AssociationConnectionModal";
import adminAssociationsService from "services/AssociationsService";
import moment from "moment";
import AssociationCreateContactModal from "../CreateContactModal/CreateContactModal";
import QrCode from "../QrCode/QrCode";
import { useAuthUser } from "../../../../hooks/common";
import { CheckboxField } from "../../../../components/Form";

const { format, formats } = DateUtils;
const ICON_SIZE = 36;
const DATE_FORMAT = formats.americanMediumDate;

const AssociationDetail = (props) => {
  const params = useParams();
  const user = useAuthUser();
  const currentURL = window.location.href;
  const urlParts = currentURL.split("/");
  const associationId = urlParts[urlParts.length - 1];
  const [isFetching, setIsFetching] = useState(false);
  const [itemCommunitiesName, setItemCommunitiesName] = useState("");
  const [associationDetailData, setAssociationDetailData] = useState();
  const [shouldReload, setShouldReload] = useState(false);
  const [showQrList, setShowQrList] = useState(false);
  const [showQrModal, setShowQrModal] = useState(false);
  const [isDownload, setIsDownload] = useState(false);
  const [isEditorDetailOpen, setIsEditorDetailOpen] = useState(false);
  const [tableList, setTableList] = useState();
  const [allTableListData, setAllTableListData] = useState();
  const [tableListPagination, setTableListPagination] = useState({
    page: 1,
    size: 10,
    totalCount: 0,
  });
  const [tab, setTab] = useState(0);
  const [page, setPage] = useState(1);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isAssociationCommunityEditorOpen, setIsAssociationCommunityEditorOpen] = useState(false);
  const [isSaveSuccessDialogOpen, setIsSaveSuccessDialogOpen] = useState(false);
  const [isCancelEditConfirmDialogOpen, setIsCancelEditConfirmDialogOpen] = useState(false);
  const [isAssociationConnectionModalShow, setIsAssociationConnectionModalShow] = useState(false);
  const [isDisaffiliationConfirmDialogOpen, setIsDisaffiliationConfirmDialogOpen] = useState(false);
  const [stepOneSelectedData, setStepOneSelectedData] = useState([]);
  const [finalSelectedData, setFinalSelectedData] = useState([]);
  const [itemDisReferData, setItemDisReferData] = useState();
  const [isCreateContactShow, setIsCreateContactShow] = useState(false);
  const [contactId, setContactId] = useState("");
  const [selected, setSelected] = useState();
  const [isWarningShow, setIsWarningShow] = useState(false);
  const [warningError, setWarningError] = useState("");
  const [isShowSuccessDialog, setIsShowSuccessDialog] = useState(false);

  const { MAX_SIZE, FIRST_PAGE } = PAGINATION;

  const [sort, setSort] = useState(null);
  const [isContactAddSuccessDialogShow, setIsContactAddSuccessDialogShow] = useState(false);

  const [publicBuildingListShow, setPublicBuildingListShow] = useState(false);
  const [publicVendorListShow, setPublicVendorListShow] = useState(false);

  const formatStringDate = (value) => (value ? moment(value, "MM/DD/YYYY HH:mm A").toDate().getTime() : null);

  useEffect(() => {
    setIsFetching(true);
    getDetail();
  }, [associationId]);

  useEffect(() => {
    const params = {
      page: page - 1,
      size: 10,
      sort,
      associationId,
    };
    if (tab === 0) {
      findCommunities(params);
    } else if (tab === 1) {
      findVendors(params);
    } else if (tab === 2) {
      findOrganizations(params);
    } else if (tab === 3) {
      findContact(params);
    }
  }, [associationId, tab, page, sort]);
  useEffect(() => {
    getAllTableList();
  }, [tab, associationId]);
  const getAllTableList = () => {
    const params = {
      page: page - 1,
      size: 99999,
      associationId,
    };
    if (tab === 0) {
      findAllCommunities(params);
    } else if (tab === 1) {
      findAllVendors(params);
    } else if (tab === 2) {
      findAllOrganizations(params);
    } else if (tab === 3) {
      findAllContact(params);
    }
  };
  const getDetail = () => {
    adminAssociationsService.FeatAssociationDetail(associationId).then((res) => {
      if (res.success) {
        setAssociationDetailData(res.data);
        setPublicBuildingListShow(res.data.externalBuildingShow);
        setPublicVendorListShow(res.data.externalVendorShow);
      }
      setIsFetching(false);
    });
  };

  const findCommunities = (params) => {
    adminAssociationsService.FindAssociationsCommunity(params).then((res) => {
      if (res.success) {
        setTableList(res.data);
        setTableListPagination({
          page: page,
          size: 10,
          totalCount: res.totalCount,
        });
      }
    });
  };
  const findAllCommunities = (params) => {
    adminAssociationsService.FindAssociationsCommunity(params).then((res) => {
      if (res.success) {
        setAllTableListData(res.data);
        setStepOneSelectedData(res.data);
      }
    });
  };

  const findVendors = (params) => {
    adminAssociationsService.FindAssociationsVendor(params).then((res) => {
      if (res.success) {
        setTableList(res.data);
        setTableListPagination({
          page: page,
          size: 10,
          totalCount: res.totalCount,
        });
      }
    });
  };
  const findAllVendors = (params) => {
    adminAssociationsService.FindAssociationsVendor(params).then((res) => {
      if (res.success) {
        setAllTableListData(res.data);
        setStepOneSelectedData(res.data);
      }
    });
  };

  const findOrganizations = (params) => {
    adminAssociationsService.FindAssociationsOrganization(params).then((res) => {
      if (res.success) {
        setTableList(res.data);
        setTableListPagination({
          page: page,
          size: 10,
          totalCount: res.totalCount,
        });
      }
    });
  };
  const findAllOrganizations = (params) => {
    adminAssociationsService.FindAssociationsOrganization(params).then((res) => {
      if (res.success) {
        setAllTableListData(res.data);
        const data = res.data?.map((item) => {
          return item.organization;
        });
        setStepOneSelectedData(data);
      }
    });
  };
  const findContact = (params) => {
    adminAssociationsService.FindAssociationsContact(params).then((res) => {
      if (res.success) {
        setTableList(res.data);
        setTableListPagination({
          page: page,
          size: 10,
          totalCount: res.totalCount,
        });
      }
    });
  };
  const findAllContact = (params) => {
    adminAssociationsService.FindAssociationsContact(params).then((res) => {
      if (res.success) {
        setAllTableListData(res.data);
        setStepOneSelectedData(res.data);
      }
    });
  };

  const isLoading = () => {
    return isFetching || shouldReload;
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

  const showQrListFc = () => {
    setShowQrList(!showQrList);
  };

  const showQrModalFc = () => {
    setShowQrModal(!showQrModal);
    setShowQrList(!showQrList);
  };

  const downloadQrCode = () => {
    setIsDownload(true);
  };
  const onEdit = () => {
    setIsEditorDetailOpen(true);
  };

  const onSort = (field, order) => {
    setSort(`${field},${order}`);
  };

  const onChangeTab = (tabItem) => {
    if (tab !== tabItem) {
      setSort(null);
      setTab(tabItem);
      setTableList([]);
    }
  };

  const onCloseEditorDetail = (shouldConfirm = false) => {
    setIsSaveSuccessDialogOpen(false);
    setIsEditorDetailOpen(shouldConfirm);
    setIsCancelEditConfirmDialogOpen(shouldConfirm);
    getDetail();
  };
  const onSaveSuccessDetail = () => {
    setIsSaveSuccessDialogOpen(true);
    setIsEditorDetailOpen(false);
  };
  // connection modal
  const onCloseAssociationConnectionModal = () => {
    setIsAssociationConnectionModalShow(false);
    getAllTableList();
  };

  const onShowAssociationConnectionModal = () => {
    const params = {
      page: 0,
      size: 9999,
      associationId,
    };
    if (tab === 3) {
      setIsCreateContactShow(true);
    } else {
      setIsAssociationConnectionModalShow(true);
    }
  };

  const changePublicCurrentListStatus = (id, type, showExternal) => {
    adminAssociationsService
      .changePubicCurrentStatus({ id, type, showExternal })
      .then(() => {
        if (type === "building") {
          setPublicBuildingListShow(showExternal);
        } else if (type === "vendor") {
          setPublicVendorListShow(showExternal);
        }
      })
      .catch(() => {
        if (type === "building") {
          setPublicBuildingListShow(!showExternal);
        } else if (type === "vendor") {
          setPublicVendorListShow(!showExternal);
        }

        setIsWarningShow(true);
        setWarningError("Switching anomaly");
      });
  };

  const onPublicShowCurrentList = () => {
    if (tab === 0) {
      changePublicCurrentListStatus(associationDetailData.id, "building", !publicBuildingListShow);
    } else if (tab === 1) {
      changePublicCurrentListStatus(associationDetailData.id, "vendor", !publicVendorListShow);
    }
  };

  const onAsset = (data) => {
    setIsDisaffiliationConfirmDialogOpen(true);
    setItemCommunitiesName(data.name || data.orgName);
  };

  const onContactSubmit = () => {
    setIsCreateContactShow(false);
    setIsContactAddSuccessDialogShow(true);
  };
  const onContactSubmitSuccess = () => {
    setIsContactAddSuccessDialogShow(false);
    setContactId(null);
    setSelected(null);
    if (page === 1) {
      adminAssociationsService
        .FindAssociationsContact({ associationId, page: page - 1, size: 10 })
        .then((res) => {
          if (res.success) {
            setTableListPagination({
              page: page,
              size: 10,
              totalCount: res.totalCount,
            });
            setTableList(res.data);
            setIsFetching(false);
          }
        })
        .catch((error) => {
          setIsWarningShow(true);
          setWarningError(error.message);
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

  const onDisaffiliationConfirmDialogConfirm = () => {
    if (tab === 0) {
      const params = {
        associationId,
        disReferId: itemDisReferData?.id,
      };
      disAddCommunity(params);
    } else if (tab === 1) {
      const params = {
        associationId,
        disReferId: itemDisReferData?.id,
      };
      disAddVendor(params);
    } else if (tab === 2) {
      const params = {
        associationId,
        disReferId: itemDisReferData?.organizationId,
      };
      disAddOrganization(params);
    }
  };

  const disAddCommunity = (params) => {
    adminAssociationsService.DisAddAssociationsCommunity(params).then((res) => {
      if (res.success) {
        setIsDisaffiliationConfirmDialogOpen(false);
        setIsShowSuccessDialog(true);
      }
    });
  };
  const refreshTableList = () => {
    setIsShowSuccessDialog(false);
    if (tab === 0) {
      if (page === 1) {
        const params = {
          page: 0,
          size: 10,
          sort,
          associationId,
        };
        findCommunities(params);
      } else {
        setPage(1);
      }
      findAllCommunities({ page: 0, size: 9999, associationId });
    } else if (tab === 1) {
      if (page === 1) {
        const params = {
          page: 0,
          size: 10,
          sort,
          associationId,
        };
        findVendors(params);
      } else {
        setPage(1);
      }
      findAllVendors({
        page: 0,
        size: 9999,
        associationId,
      });
    } else if (tab === 2) {
      if (page === 1) {
        const params = {
          page: 0,
          size: 10,
          sort,
          associationId,
        };
        findOrganizations(params);
      } else {
        setPage(1);
      }

      findAllOrganizations({
        page: 0,
        size: 9999,
        associationId,
      });
    }
  };
  const disAddVendor = (params) => {
    adminAssociationsService.DisAddAssociationsVendor(params).then((res) => {
      if (res.success) {
        setIsDisaffiliationConfirmDialogOpen(false);
        setIsShowSuccessDialog(true);
      }
    });
  };
  const disAddOrganization = (params) => {
    adminAssociationsService.DisAddAssociationsOrg(params).then((res) => {
      if (res.success) {
        setIsDisaffiliationConfirmDialogOpen(false);
        setIsShowSuccessDialog(true);
      }
    });
  };

  const onDisaffiliationConfirmDialogConfirmCancel = () => {
    setIsDisaffiliationConfirmDialogOpen(false);
  };

  const modalConfirm = () => {
    setIsSubmitting(true);
    const selectedId = [];
    finalSelectedData.map((item) => {
      return selectedId.push(item.id);
    });

    if (tab === 0) {
      adminAssociationsService
        .AddAssociationsCommunity({
          associationId: associationId,
          referIds: selectedId,
        })
        .then((res) => {
          if (res.success) {
            setIsSubmitting(false);
            setIsAssociationConnectionModalShow(false);
            if (page === 1) {
              findCommunities({
                page: 0,
                size: 10,
                associationId,
              });
            } else {
              setPage(1);
            }
          }
        });
    } else if (tab === 1) {
      adminAssociationsService
        .AddAssociationsVendor({
          associationId: associationId,
          referIds: selectedId,
        })
        .then((res) => {
          if (res.success) {
            setIsSubmitting(false);
            setIsAssociationConnectionModalShow(false);
            if (page === 1) {
              findVendors({
                page: 0,
                size: 10,
                associationId,
              });
            } else {
              setPage(1);
            }
          }
        });
    } else if (tab === 2) {
      adminAssociationsService
        .AddAssociationsOrg({
          associationId: associationId,
          referIds: selectedId,
        })
        .then((res) => {
          if (res.success) {
            setIsSubmitting(false);
            setIsAssociationConnectionModalShow(false);
            if (page === 1) {
              findOrganizations({
                page: 0,
                size: 10,
                associationId,
              });
            } else {
              setPage(1);
            }
          }
        });
    }
  };

  const COMMUNITYCOLUMNS = [
    {
      dataField: "name",
      text: "Name",
      sort: true,
      headerStyle: {
        width: "240px",
      },
      onSort: onSort,
      formatter: (v, row) => {
        return <div className="CommunityList-CommunityName">{v}</div>;
      },
    },
    {
      dataField: "oid",
      text: "Community OID",
      sort: true,
      onSort: onSort,
    },
    {
      dataField: "orgName",
      text: "Org.Name",
      headerAlign: "left",
      align: "left",
      headerStyle: {
        width: "270px",
      },
    },
    {
      dataField: "stateTitle",
      text: "State",
      headerAlign: "left",
      align: "left",
      sort: true,
      onSort: onSort,
      headerStyle: {
        width: "180px",
      },
      formatter: (v, row) => {
        return <> {v || "-"}</>;
      },
    },
    {
      dataField: "@actions",
      text: "",
      headerStyle: {
        width: "60px",
      },
      align: "right",
      formatter: (v, row) => (
        <div
          onClick={() => {
            setItemDisReferData(row);
          }}
        >
          <Actions
            data={v}
            hasUnlink={associationDetailData?.canEdit}
            iconSize={ICON_SIZE}
            unlinkMessage="Disaffiliate from this community"
            onUnlink={() => onAsset(row)}
          />
        </div>
      ),
    },
  ];

  const COMMUNITYCOLUMNSMOBILE = ["name", "stateTitle"];
  const VENDORCOLUMNS = [
    {
      dataField: "name",
      text: "Name",
      sort: true,
      headerStyle: {
        width: "240px",
      },
      onSort: onSort,
      formatter: (v, row) => {
        return <div className="CommunityList-CommunityName">{v}</div>;
      },
    },
    {
      dataField: "premium",
      text: "Premium",
      headerAlign: "left",
      align: "left",
      headerStyle: {
        width: "270px",
      },
      formatter: (v, row) => {
        return <>{v ? "Yes" : "No"}</>;
      },
    },
    {
      dataField: "@actions",
      text: "",
      headerStyle: {
        width: "60px",
      },
      align: "right",
      formatter: (v, row) => (
        <div onClick={() => setItemDisReferData(row)}>
          <Actions
            data={v}
            hasUnlink={associationDetailData?.canEdit}
            iconSize={ICON_SIZE}
            unlinkMessage="Disaffiliate from this vendor"
            onUnlink={() => onAsset(row)}
          />
        </div>
      ),
    },
  ];
  const VENDORCOLUMNSMOBILE = ["name", "premium"];

  const ORGANIZATIONCOLUMNS = [
    {
      dataField: "orgName",
      text: "Name",
      sort: true,
      onSort: onSort,
      formatter: (v, row) => {
        return <div className="CommunityList-CommunityName">{v}</div>;
      },
    },
    {
      dataField: "stateName",
      text: "State",
      formatter: (v, row) => {
        return (
          <div className="Association-connection-modal-substance">
            <span className={"MultiSelect-Template-Text"}>{v}</span>
          </div>
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
      formatter: (v, row) => (
        <div onClick={() => setItemDisReferData(row)}>
          <Actions
            data={v}
            iconSize={ICON_SIZE}
            hasUnlink={associationDetailData?.canEdit}
            unlinkMessage="Disaffiliate from this organization"
            onUnlink={() => onAsset(row)}
          />
        </div>
      ),
    },
  ];
  const ORGANIZATIONCOLUMNSMOBILE = ["name", "state"];
  const TeamTable = [
    {
      dataField: "fullName",
      text: "Name",
      sort: true,
      onSort: onSort,
      formatter: (v, row) => {
        return <>{v}</>;
      },
    },
    {
      dataField: "login",
      text: "Login Email",
      sort: true,
      onSort: onSort,
      formatter: (v, row) => {
        return <>{v || "-"}</>;
      },
    },
    {
      dataField: "lastSessionDateTime",
      text: "Last session",
      sort: true,
      onSort: onSort,
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
      align: "left",
      formatter: (v, row) => {
        return (
          <>
            {v === 0 && <div className="association-active-btn">Active</div>}
            {v === 1 && <div className="association-pending-btn">Pending</div>}
            {v === 2 && <div className="association-expired-btn">Expired</div>}
            {v === 3 && <div className="association-inactive-btn">Inactive</div>}
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
      formatter: (v, row) => (
        <div
          onClick={() => {
            setContactId(row.id);
            setSelected(row);
          }}
        >
          <Actions
            data={v}
            hasEditAction={associationDetailData?.canEdit}
            iconSize={ICON_SIZE}
            editHintMessage="Edit this contact"
            onEdit={onEditContact}
          />
        </div>
      ),
    },
  ];

  const TEAMCOLUMNSMOBILE = ["name", "status"];
  const AllColumns = [COMMUNITYCOLUMNS, VENDORCOLUMNS, ORGANIZATIONCOLUMNS, TeamTable];
  const columnsMobile = [COMMUNITYCOLUMNSMOBILE, VENDORCOLUMNSMOBILE, ORGANIZATIONCOLUMNSMOBILE, TEAMCOLUMNSMOBILE];
  return (
    <>
      <DocumentTitle
        title={
          isAssociationCommunityEditorOpen
            ? isNumber(associationId)
              ? "Simply Connect | Admin | Associations | Association Detail | Edit Community Detail"
              : "Simply Connect | Admin | Associations | Association Detail | Create Community"
            : "Simply Connect | Admin | Associations | Association Detail"
        }
      >
        <div className={cn("AssociationDetail")}>
          <UpdateSideBarAction />
          {isLoading() && <Loader />}
          {isEmpty(associationDetailData) && <h4>No Data</h4>}
          {!isLoading() && !isEmpty(associationDetailData) && (
            <>
              {/* Edit detail*/}
              {isEditorDetailOpen && (
                <AssociationEditor
                  isOpen={isEditorDetailOpen}
                  associationId={Number(associationId)}
                  onClose={onCloseEditorDetail}
                  onSaveSuccess={onSaveSuccessDetail}
                />
              )}
              {/* Save success*/}
              {isSaveSuccessDialogOpen && (
                <SuccessDialog
                  isOpen
                  title="Association details have been updated."
                  buttons={[
                    {
                      text: "OK",
                      onClick: () => {
                        onCloseEditorDetail();
                      },
                    },
                  ]}
                />
              )}
              {/* Qr Modal*/}
              {showQrModal && (
                <QrCode
                  showQrModalFc={showQrModalFc}
                  isOpen={showQrModal}
                  QrTitle={associationDetailData.name}
                  id={"qrcode"}
                  format={"PNG"}
                />
              )}
              {/*Disaffiliation modal*/}
              {isDisaffiliationConfirmDialogOpen && itemCommunitiesName && (
                <ConfirmDialog
                  isOpen={isDisaffiliationConfirmDialogOpen}
                  icon={Warning}
                  confirmBtnText="Confirm"
                  cancelBtnText="Cancel"
                  title={`Do you want to unlink ${itemCommunitiesName} from ${associationDetailData?.name}`}
                  onConfirm={onDisaffiliationConfirmDialogConfirm}
                  onCancel={onDisaffiliationConfirmDialogConfirmCancel}
                />
              )}
              {/* connection Modal */}
              {isAssociationConnectionModalShow && (
                <AssociationConnectionModal
                  tab={tab}
                  findTableList={tableList}
                  stepOneSelectedData={stepOneSelectedData}
                  setStepOneSelectedData={setStepOneSelectedData}
                  finalSelectedData={finalSelectedData}
                  setFinalSelectedData={setFinalSelectedData}
                  modalConfirm={modalConfirm}
                  isSubmitting={isSubmitting}
                  isOpen={isAssociationConnectionModalShow}
                  onClose={onCloseAssociationConnectionModal}
                />
              )}
              {/*creat contact modal*/}

              {isCreateContactShow && (
                <AssociationCreateContactModal
                  isOpen={isCreateContactShow}
                  onSubmit={onContactSubmit}
                  associationDetailData={associationDetailData}
                  editContactSuccess={editContactSuccess}
                  onClose={() => {
                    setContactId(null);
                    setIsCreateContactShow(false);
                    setSelected(null);
                  }}
                  associationId={associationId}
                  contactId={contactId}
                  isPendingContact={selected?.status === 1}
                  isExpiredContact={selected?.status === 2}
                ></AssociationCreateContactModal>
              )}

              {isContactAddSuccessDialogShow && (
                <SuccessDialog
                  isOpen={isContactAddSuccessDialogShow}
                  title={contactId ? `Contact member edited successfully.` : `Contact member invited successfully.`}
                  buttons={[{ text: "Ok", onClick: () => onContactSubmitSuccess() }]}
                />
              )}

              <Breadcrumbs
                items={[
                  { title: "Associations", href: "/admin/associations", isEnabled: true },
                  { title: "Association detail", href: `/admin/associations/${associationId}`, isActive: true },
                ]}
              />
              <div className="AssociationDetail-Header">
                <div className="AssociationDetail-Title">
                  <div className="AssociationDetail-TitleText" title={associationDetailData.name}>
                    {associationDetailData.name}
                  </div>
                </div>
                <div className="AssociationDetail-ControlPanel">
                  {associationDetailData?.canEdit && (
                    <Button color="success" className="AssociationDetail-EditButton" onClick={onEdit}>
                      Edit Details
                    </Button>
                  )}
                </div>
              </div>
              <div className="AssociationDetail-Body">
                <div className="margin-bottom-65">
                  <Detail
                    className="OrganizationDetail"
                    titleClassName="OrganizationDetail-Title"
                    valueClassName="OrganizationDetail-Value"
                    title="WEBSITE"
                  >
                    {associationDetailData?.website || "-"}
                  </Detail>
                  <Detail
                    className="OrganizationDetail"
                    titleClassName="OrganizationDetail-Title"
                    valueClassName="OrganizationDetail-Value"
                    title="COMPANY ID"
                  >
                    {associationDetailData?.companyId || "-"}
                  </Detail>
                  <Detail
                    className="OrganizationDetail"
                    titleClassName="OrganizationDetail-Title"
                    valueClassName="OrganizationDetail-Value"
                    title="EMAIL"
                  >
                    {associationDetailData?.email || "-"}
                  </Detail>
                  <Detail
                    className="OrganizationDetail"
                    titleClassName="OrganizationDetail-Title"
                    valueClassName="OrganizationDetail-Value"
                    title="PHONE"
                  >
                    {PNU.formatPhoneNumber(associationDetailData?.phone) || "-"}
                  </Detail>
                  <Detail
                    className="OrganizationDetail"
                    titleClassName="OrganizationDetail-Title"
                    valueClassName="OrganizationDetail-Value"
                    title="ADDRESS"
                  >
                    {getAddress({
                      city: associationDetailData?.city,
                      street: associationDetailData?.street,
                      state: associationDetailData?.stateName,
                      zip: associationDetailData?.zipCode,
                    }) || "-"}
                  </Detail>
                  {associationDetailData?.logoDataUrl && (
                    <Detail
                      className="OrganizationDetail"
                      titleClassName="OrganizationDetail-Title"
                      valueClassName="OrganizationDetail-Value"
                      title="LOGO"
                    >
                      <img className="OrganizationDetail-Logo" src={associationDetailData?.logoDataUrl} alt="" />
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
                  data={tableList}
                  pagination={tableListPagination}
                  columns={AllColumns[tab]}
                  columnsMobile={columnsMobile[tab]}
                  defaultSorted={[
                    {
                      dataField: "name",
                      order: "asc",
                    },
                  ]}
                  onRefresh={(num) => {
                    setPage(num);
                  }}
                  renderCaption={(title) => {
                    return (
                      <div className="CommunityList-Caption">
                        <div className="CommunityList-CaptionHeader">
                          <div className="CommunityList-Title">
                            <Tabs
                              containerClassName="AssociationDetailFor-TabsContainerm"
                              items={[
                                { title: "Communities", isActive: tab === 0, hasError: false },
                                { title: "Vendors", isActive: tab === 1, hasError: false },
                                { title: "Organizations", isActive: tab === 2, hasError: false },
                                { title: "Team", isActive: tab === 3, hasError: false },
                              ]}
                              onChange={onChangeTab}
                            />
                          </div>
                          <div className="CommunityList-ControlPanel">
                            {user.roleName === "ROLE_SUPER_ADMINISTRATOR" && (tab === 0 || tab === 1) && (
                              <>
                                <CheckboxField
                                  type="text"
                                  name="isCreateServicePlan"
                                  value={tab === 0 ? publicBuildingListShow : publicVendorListShow}
                                  label={tab === 0 ? "Community Display" : "Vendor Display"}
                                  onChange={() => onPublicShowCurrentList()}
                                />
                              </>
                            )}

                            {associationDetailData?.canEdit && (
                              <Button color="success" onClick={onShowAssociationConnectionModal}>
                                {tab === 0 && "Link communities"}
                                {tab === 1 && "Link vendors"}
                                {tab === 2 && "Link organizations"}
                                {tab === 3 && "Create Contact"}
                              </Button>
                            )}
                          </div>
                        </div>
                      </div>
                    );
                  }}
                />
                {isWarningShow && (
                  <WarningDialog
                    isOpen={isWarningShow}
                    title={warningError}
                    buttons={[
                      {
                        text: "OK",
                        onClick: () => setIsWarningShow(false),
                      },
                    ]}
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
                          refreshTableList();
                        },
                      },
                    ]}
                  />
                )}
              </div>
            </>
          )}
        </div>
      </DocumentTitle>
    </>
  );
};

export default AssociationDetail;
