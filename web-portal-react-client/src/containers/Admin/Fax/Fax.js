import React, { useCallback, useEffect, useState } from "react";

import DocumentTitle from "react-document-title";
import Actions from "components/Table/Actions/Actions";
import { Button, ButtonGroup, Col, Row } from "reactstrap";
import { ConfirmDialog, WarningDialog } from "components/dialogs";

import { useHistory } from "react-router-dom";
import { ReactComponent as Warning } from "images/alert-yellow.svg";
import { ReactComponent as Refresh } from "images/Admin/refresh.svg";
import { useAuthUser, useDownloadingStatusInfoToast, useQueryParams, useToggle } from "hooks/common";

import Table from "components/Table/Table";
import SearchField from "components/SearchField/SearchField";
import Breadcrumbs from "components/Breadcrumbs/Breadcrumbs";
import FaxViewer from "./FaxViewer/FaxViewer";

import { UpdateSideBarAction } from "actions/admin";
import { DateUtils, isInteger } from "lib/utils/Utils";

import "./Fax.scss";
import FaxEditor from "./FaxEditor/FaxEditor";
import SuccessDialog from "../../../components/dialogs/SuccessDialog/SuccessDialog";
import { compact, first } from "underscore";
import moment from "moment/moment";
import adminFaxService from "services/FaxService";
import { Loader, PrimaryFilter } from "../../../components";
import { useCommunityPrimaryFilterDirectory } from "hooks/business/Marketplace";
import { usePrimaryFilter } from "hooks/common/filter";
import service from "services/CommunityService";
import { SelectField } from "../../../components/Form";

const { format, formats } = DateUtils;

const ICON_SIZE = 36;

const FaxList = () => {
  const history = useHistory();
  const user = useAuthUser();
  const [searchName, setSearchName] = useState("");
  const [isFetching, setIsFetching] = useState(false);
  const [isDownLoadFetching, setIsDownLoadFetching] = useState(false);
  const [showAdd, setShowAdd] = useState(false);
  const [selected, setSelected] = useState();
  const [FaxDataList, setFaxDataList] = useState();
  const [FaxDataTotal, setFaxDataTotal] = useState();
  const [FaxSentDataList, setFaxSentDataList] = useState();
  const [FaxSentDataTotal, setFaxSentDataTotal] = useState();
  const [isViewerOpen, toggleViewer] = useState(false);
  const [isSuccessDialogOpen, toggleSuccessDialog] = useState(false);
  const [sort, setSort] = useState(null);
  const [page, setPage] = useState(1);
  const [isSent, setIsSent] = useState(false);
  const [isConfirmDeleteDialogOpen, toggleConfirmDeleteDialog] = useToggle();
  const [direction, setDirection] = useState();
  const [isShowWarningDialog, setIsShowWarningDialog] = useState(false);
  const [loginFax, setLoginFax] = useState();
  const [faxListType, setFaxListType] = useState("Received");

  const primaryFilter = usePrimaryFilter("FEATURED_COMMUNITY_PRIMARY_FILTER", {
    isCommunityMultiSelection: false,
  });

  const { communityId, organizationId } = primaryFilter.data;

  const { changeField } = primaryFilter;
  const { communities, organizations } = useCommunityPrimaryFilterDirectory(
    { organizationId, communityId },
    { actions: primaryFilter.changeCommunityField },
  );
  const onChangeFilterField = (name, value) => {
    setSearchName(value);
  };
  const onClearSearchField = (name, value) => {
    setSearchName(value);
  };

  const [canSendFax, setCanSendFax] = useState(false);

  const getSendTipText = () => {
    adminFaxService
      .JudgeCanFax()
      .then((res) => {
        if (res.success) {
          if (res.data) {
            setCanSendFax(true);
            setLoginFax(res.data);
          } else {
            setCanSendFax(false);
            setLoginFax(null);
          }
        }
      })
      .catch((error) => {
        setCanSendFax(false);
        setLoginFax(null);
      });
  };

  useEffect(() => {
    getSendTipText();
  }, []);

  useEffect(() => {
    setIsFetching(true);
    communityId && getFaxData();
    return () => {
      getFaxData.cancel();
    };
  }, [searchName, page, sort, isSent, organizationId, communityId]);

  const getFaxData = _.debounce(() => {
    const params = {
      keyWords: searchName,
      page: page - 1,
      size: 15,
      sort,
      organizationId,
      communityId,
    };
    if (isSent) {
      getSentFaxDataList(params);
    } else {
      refreshFaxDataList();
    }
  }, 300);

  const onCloseConfirmDeleteDialog = useCallback(() => {
    setSelected(null);

    toggleConfirmDeleteDialog();
  }, []);
  const getReceivedFaxDataList = (params) => {
    // sent
    adminFaxService.ReceivedFaxList(params).then(
      (res) => {
        if (res.success) {
          setIsFetching(false);

          setFaxDataList(res.data);
          setFaxDataTotal(res.totalCount);
        }
      },
      () => {
        setIsFetching(false);
        setFaxDataList([]);
      },
    );
  };

  const getSentFaxDataList = (params) => {
    // sent
    adminFaxService.SentFaxList(params).then(
      (res) => {
        if (res.success) {
          setIsFetching(false);

          setFaxSentDataList(res.data);
          setFaxSentDataTotal(res.totalCount);
        }
      },
      () => {
        setIsFetching(false);
        setFaxSentDataList([]);
      },
    );
  };
  const refreshFaxDataList = () => {
    setIsFetching(true);
    adminFaxService
      .RefreshCloudFax({ communityId })
      .then((res) => {
        if (res.success) {
          const params = {
            keyWords: searchName,
            page: page - 1,
            size: 15,
            sort,
            organizationId,
            communityId,
          };
          getReceivedFaxDataList(params);
        }
      })
      .catch(() => {
        const params = {
          keyWords: searchName,
          page: page - 1,
          size: 15,
          sort,
          organizationId,
          communityId,
        };
        getReceivedFaxDataList(params);
      });
  };
  const onSaveSuccess = () => {
    setSelected(null);

    toggleSuccessDialog(true);
    setShowAdd(false);
    getFaxData();
  };

  const onConfirmSuccess = () => {
    setIsFetching(true);
    if (page === 1) {
      setTimeout(() => {
        toggleSuccessDialog(false);
        setIsFetching(false);
      }, 400);
    } else {
      toggleSuccessDialog(false);
      setPage(1);
    }
  };

  const onDownloadReceived = (row) => {
    const params = {
      faxId: row.id,
      direction: "Inbound",
      communityId,
    };
    onDownload(params);
  };

  const onDownloadSent = (row) => {
    const params = {
      faxId: row.id,
      direction: "Outbound",
      communityId,
    };
    onDownload(params);
  };

  const onDownload = (params) => {
    setIsDownLoadFetching(true);
    adminFaxService
      .DownloadFax(params)
      .then((res) => {
        if (res.success) {
          handleViewFile(res.data);
        }
      })
      .catch((e) => {
        setIsDownLoadFetching(false);
        setIsShowWarningDialog(true);
      });
  };
  const handleViewFile = (base64Content) => {
    try {
      setIsDownLoadFetching(false);
      const binaryContent = atob(base64Content);

      const uint8Array = new Uint8Array(binaryContent.length);
      for (let i = 0; i < binaryContent.length; i++) {
        uint8Array[i] = binaryContent.charCodeAt(i);
      }

      const blob = new Blob([uint8Array], { type: "application/pdf" });
      const url = URL.createObjectURL(blob);
      window.open(url, "_blank");
    } catch (error) {
      setIsDownLoadFetching(false);
      console.error("Error viewing file:", error);
    }
  };
  const onDeleteReceived = (row) => {
    toggleConfirmDeleteDialog();
    setSelected(row);
    setDirection("Inbound");
  };

  const onDeleteSent = (row) => {
    toggleConfirmDeleteDialog();
    setSelected(row);
    setDirection("Outbound");
  };
  const onDelete = useCallback(() => {
    setSelected(null);
    toggleConfirmDeleteDialog();
    const params = {
      faxId: selected.id,
      direction,
    };
    adminFaxService.DeleteFax(params).then((res) => {
      if (res.success) {
        getFaxData();
      }
    });
  }, [selected, toggleConfirmDeleteDialog]);

  const onSort = (field, sort) => {
    setSort(`${field},${sort}`);
  };
  const onView = (o) => {
    toggleViewer(true);
    setSelected(isInteger(o) ? { id: o } : o);
  };

  const onCloseViewer = () => {
    setSelected(null);
    toggleViewer(false);
  };
  const onClickButtonGroup = (name) => {
    setSort("");
    if (name === "Received") {
      setIsSent(false);
    } else {
      setIsSent(true);
    }
  };
  const onChangeGroupTab = (name, value) => {
    setSort("");
    setFaxListType(value);
    if (value === "Received") {
      setIsSent(false);
    } else {
      setIsSent(true);
    }
  };

  let FaxColumns = [
    {
      dataField: "receiveFaxNumber",
      text: "From",
      sort: true,
      onSort: onSort,
      headerClasses: "FaxList-NameColHeader",
      formatter: (v, row) => {
        if (v.length > 0) {
          if (v.indexOf("-") !== -1) {
            return <div className="num">{v}</div>;
          } else {
            let str = v.substring(0, 3) + "-" + v.substring(3, 6) + "-" + v.substring(6);
            return <div className="num">{str}</div>;
          }
        } else {
          return "-";
        }
      },
    },
    {
      dataField: "pageCount",
      text: "Page",
      sort: true,
      onSort: onSort,
      align: "left",
      headerAlign: "left",
      headerClasses: "FaxList-CreatedAutomaticallyColHeader",
    },
    {
      dataField: "faxDate",
      text: "Date",
      sort: true,
      align: "left",
      headerAlign: "left",
      headerStyle: { width: "15%" },
      onSort: onSort,
      formatter: (v) => v && moment(v).format("MM/DD/YYYY HH:mm"),
    },
    {
      dataField: "status",
      text: "Status",
      sort: true,
      onSort: onSort,
      align: "left",
      headerAlign: "left",
      headerClasses: "FaxList-CreatedAutomaticallyColHeader",
      formatter: (v, row) => {
        return v ? (
          <>
            {v === "SUCCESS" ? (
              <div className="success">Success</div>
            ) : v === "SENT" ? (
              <div className="send">Sending</div>
            ) : (
              <div className="fail">Fail</div>
            )}
          </>
        ) : (
          ""
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
      formatter: (v, row) => {
        return (
          <>
            {row.status === "SUCCESS" ? (
              <Actions
                data={row}
                hasDownloadAction={true}
                iconSize={ICON_SIZE}
                downloadHintMessage="Download fax details"
                onDownload={(row) => {
                  onDownloadReceived(row);
                }}
              />
            ) : row.status === "SENT" ? (
              <Actions
                data={row}
                hasDownloadAction={true}
                cannotDownload={true}
                iconSize={ICON_SIZE}
                className={"notAllowed"}
                downloadHintMessage="Can not download fax details"
                onDownload={(e) => {
                  e.preventDefault();
                }}
              />
            ) : (
              <Actions
                data={row}
                hasDeleteAction={true}
                iconSize={ICON_SIZE}
                editHintMessage="delete fax details"
                onDelete={(row) => {
                  onDeleteReceived(row);
                }}
              />
            )}
          </>
        );
      },
    },
  ];

  let FaxSentColumns = [
    {
      dataField: "receiveFaxNumber",
      text: "To",
      sort: true,
      onSort: onSort,
      headerClasses: "FaxList-NameColHeader",
      formatter: (v, row) => {
        if (v.length > 0) {
          if (v.indexOf("-") !== -1) {
            return <div className="num">{v}</div>;
          } else {
            let str = v.substring(0, 3) + "-" + v.substring(3, 6) + "-" + v.substring(6);
            return <div className="num">{str}</div>;
          }
        } else {
          return "-";
        }
      },
    },

    {
      dataField: "contact",
      text: "Contact Name",
      sort: true,
      onSort: onSort,
      align: "left",
      headerAlign: "left",
      headerClasses: "FaxList-CreatedAutomaticallyColHeader",
      formatter: (v, row) => {
        return v ? (
          <>
            {v.fullName}
            {/* <div className='num' onClick={() => onView(row.id)} >{v}</div> */}
          </>
        ) : (
          ""
        );
      },
    },
    {
      dataField: "roleName",
      text: "Role",
      sort: true,
      onSort: onSort,
      align: "left",
      headerAlign: "left",
      headerStyle: { width: "20%" },
      headerClasses: "FaxList-CreatedAutomaticallyColHeader",
    },
    {
      dataField: "page",
      text: "Page",
      sort: true,
      onSort: onSort,
      align: "left",
      headerAlign: "left",
      headerClasses: "FaxList-CreatedAutomaticallyColHeader",
    },
    {
      dataField: "createTime",
      text: "Date",
      sort: true,
      align: "left",
      headerAlign: "left",
      headerStyle: { width: "15%" },
      onSort: onSort,
      formatter: (v) => v && moment(v).format("MM/DD/YYYY HH:mm"),
    },
    {
      dataField: "status",
      text: "Status",
      sort: true,
      onSort: onSort,
      align: "left",
      headerAlign: "left",
      headerClasses: "FaxList-CreatedAutomaticallyColHeader",
      formatter: (v, row) => {
        return v ? <>{v === "SUCCESS" ? <div className="success">{v}</div> : <div className="fail">{v}</div>}</> : "";
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
          <>
            {row.status === "SUCCESS" ? (
              <Actions
                data={row}
                hasDownloadAction={true}
                iconSize={ICON_SIZE}
                downloadHintMessage="Download fax details"
                onDownload={(row) => {
                  onDownloadSent(row);
                }}
              />
            ) : row.status === "SENT" ? (
              <Actions
                data={row}
                hasDownloadAction={true}
                iconSize={ICON_SIZE}
                cannotDownload={true}
                className={"notAllowed"}
                downloadHintMessage="Can not download fax details"
                onDownload={(e) => {}}
              />
            ) : (
              <Actions
                data={row}
                hasDeleteAction={true}
                iconSize={ICON_SIZE}
                editHintMessage="delete fax details"
                onDelete={(row) => {
                  onDeleteSent(row);
                }}
              />
            )}
          </>
        );
      },
    },
  ];

  return (
    <DocumentTitle title="Simply Connect | Admin | Fax">
      <>
        {isDownLoadFetching && <Loader isCentered hasBackdrop />}
        <UpdateSideBarAction />
        <div className={"Fax"}>
          <Breadcrumbs
            items={[
              { title: "Admin", href: "/admin/organizations" },
              { title: "Fax", href: "/admin/fax", isActive: true },
            ]}
          ></Breadcrumbs>
          <PrimaryFilter
            {...primaryFilter}
            communities={communities}
            organizations={organizations}
            hasCommunityField={true}
            onChageField={changeField}
            onChangeOrganizationField={(organizationId) => primaryFilter.changeOrganizationField(organizationId)}
            onChangeCommunityField={(communityId) => {
              primaryFilter.changeCommunityField(communityId);
            }}
            isCommunityMultiSelection={false}
            classNameOrg="classNameOrg"
          />
          <Table
            hasHover
            hasOptions
            hasPagination
            keyField="id"
            noDataText="No results"
            title="Fax"
            isLoading={isFetching}
            className={"FaxList"}
            containerClass={"FaxListContainer"}
            data={!isSent ? FaxDataList : FaxSentDataList}
            pagination={
              isSent
                ? {
                    page: page,
                    size: 15,
                    totalCount: FaxSentDataTotal,
                  }
                : {
                    page: page,
                    size: 15,
                    totalCount: FaxDataTotal,
                  }
            }
            columns={!isSent ? FaxColumns : FaxSentColumns}
            columnsMobile={["name", "communityCount"]}
            onRefresh={(num) => {
              setPage(num);
            }}
            renderCaption={(title) => {
              return (
                <div className="FaxList-Caption">
                  <div className="FaxList-CaptionHeader">
                    <span className="FaxList-Title">
                      <span className="FaxList-TitleText">{title}</span>
                    </span>
                    <div className="FaxList-ControlPanel">
                      {!isSent && canSendFax && (
                        <Button outline color={"success"} className={"margin-right-10"} onClick={refreshFaxDataList}>
                          Sync Data
                        </Button>
                      )}
                      <Button
                        color="success"
                        className="AddFaxBtn"
                        onClick={() => {
                          setSelected(null);
                          setShowAdd(true);
                        }}
                        disabled={!canSendFax}
                        hasTip={!canSendFax}
                        tipText={`You don't have permissions to send a fax.`}
                        tipPlace="top"
                      >
                        Send Fax
                      </Button>
                    </div>
                  </div>
                  <div className="FaxList-Filter">
                    <Row>
                      <Col lg={2} md={2}>
                        <SelectField
                          hasEmptyValue={false}
                          name="faxlistType"
                          options={[
                            {
                              value: "Received",
                              text: "Received",
                            },
                            { value: "Sent", text: "Sent" },
                          ]}
                          value={faxListType}
                          onChange={onChangeGroupTab}
                        />
                      </Col>
                      <Col lg={8} md={8}>
                        <SearchField
                          name="content"
                          value={searchName}
                          placeholder="Search keywords"
                          onClear={onClearSearchField}
                          onChange={onChangeFilterField}
                        />
                      </Col>
                    </Row>
                  </div>
                </div>
              );
            }}
          ></Table>
          {showAdd && (
            <FaxEditor
              isOpen={showAdd}
              associationId={selected && selected?.id}
              loginFax={loginFax}
              onClose={() => {
                setSelected(null);
                setShowAdd(false);
                // dispatch(clearAssociationDetail())
              }}
              onSaveSuccess={onSaveSuccess}
            />
          )}
          {isViewerOpen && <FaxViewer isOpen FaxId={selected && selected?.id} onClose={onCloseViewer} />}

          {isSuccessDialogOpen && (
            <SuccessDialog
              isOpen
              title={`The fax is being sent. You can check its status once the process is complete.`}
              buttons={compact([
                {
                  text: "Close",
                  outline: true,
                  onClick: () => {
                    onConfirmSuccess();
                  },
                },
              ])}
            />
          )}
          {isConfirmDeleteDialogOpen && (
            <ConfirmDialog
              isOpen
              icon={Warning}
              confirmBtnText="Delete"
              title={`The document will be permanently deleted`}
              onConfirm={onDelete}
              onCancel={onCloseConfirmDeleteDialog}
            />
          )}
        </div>
        {isShowWarningDialog && (
          <WarningDialog
            isOpen={isShowWarningDialog}
            title={"This fax has expired and cannot be downloaded."}
            buttons={[
              {
                text: "Ok",
                onClick: () => {
                  setIsShowWarningDialog(false);
                },
              },
            ]}
          />
        )}
      </>
    </DocumentTitle>
  );
};

export default FaxList;
