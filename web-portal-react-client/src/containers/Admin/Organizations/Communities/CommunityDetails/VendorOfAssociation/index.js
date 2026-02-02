import React, { useCallback, useEffect, useState } from "react";
import { SearchField } from "../../../../../../components";
import Table from "../../../../../../components/Table/Table";
import { Link, useParams } from "react-router-dom";
import { path } from "../../../../../../lib/utils/ContextUtils";
import { Badge, Button, Col, Row, UncontrolledTooltip as Tooltip } from "reactstrap";
import Actions from "../../../../../../components/Table/Actions/Actions";
import { SwitchField } from "../../../../../../components/Form";
import "./index.scss";
import { toNumberExcept } from "../../../../../../lib/utils/Utils";
import Services from "../../../../../../services/OrganizationService";
import _ from "lodash";
import AssociationConnectionModal from "../../../../Associations/AssociationConnectionModal/AssociationConnectionModal";
import adminVendorService from "../../../../../../services/AdminVendorService";
import SuccessDialog from "../../../../../../components/dialogs/SuccessDialog/SuccessDialog";
import { compact } from "underscore";
import { ErrorDialog, WarningDialog } from "../../../../../../components/dialogs";

const VendorOfAssociation = ({ canEdit, communityName }) => {
  const params = useParams();

  const [page, setPage] = useState(1);
  const [totalCount, setTotalCount] = useState(0);
  const [pagination, setPagination] = useState({});
  const [searchName, setSearchName] = useState("");
  const [vendorData, setVendorData] = useState([]);

  const [isLoading, setIsLoading] = useState(false);
  const [isFetch, setIsFetch] = useState(false);

  const communityId = toNumberExcept(params.commId, [null, undefined]);

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [linkVendorModelShow, setLinkVendorModelShow] = useState(false);
  const [tableList, setTableList] = useState();
  // Selected list
  const [stepOneSelectedData, setStepOneSelectedData] = useState([]);
  // Final selected list
  const [finalSelectedData, setFinalSelectedData] = useState([]);

  const [isSuccessDialogOpen, setIsSuccessDialogOpen] = useState(false);
  const [waringDialog, setWaringDialog] = useState(false);
  const [waringDialogText, setWaringDialogText] = useState("");
  const [showErrorDialog, setShowErrorDialog] = useState(false);
  const [errorTitle, setErrorTitle] = useState("");
  const [unlinkCurrentVendorId, setUnlinkCurrentVendorId] = useState();

  const onRefresh = (page) => {
    setPage(page);
  };

  const onClearSearchField = (name, value) => {
    setSearchName(value);
  };

  const onChangeFilterField = (name, value) => {
    setSearchName(value);
  };

  const switchChange = (name, check, row) => {
    const body = {
      communityId: row.communityId,
      vendorId: row.vendorId,
      primaryType: check,
    };

    Services.changeStatusOfMarketplace(body).then((res) => {
      setIsFetch(!isFetch);
    });
  };

  const transformedDataArray = (data) =>
    data.map((item) => {
      const { vendorId, vendorName, ...rest } = item; // 解构出 vendorId 和其它属性
      return { id: vendorId, name: vendorName, ...rest }; // 创建一个新的对象，将 id 作为 vendorId 的替代
    });
  // Define the debounced fetch function
  const debouncedFetch = useCallback(
    _.debounce((params) => {
      setIsLoading(true);
      Services.featVendorOfAssociation(params)
        .then((res) => {
          setVendorData(res.data);
          setStepOneSelectedData(transformedDataArray(res.data));
          setTotalCount(res.totalCount);
          setIsLoading(false);
        })
        .catch((e) => {
          setIsLoading(false);
        });
    }, 300), // Adjust the debounce delay as needed (e.g., 300ms)
    [], // Ensure useCallback dependencies are correct to avoid unnecessary re-creations
  );

  useEffect(() => {
    setIsLoading(true);
    const params = {
      communityId: communityId,
      keyword: searchName,
      page: page - 1,
      size: 10,
    };

    debouncedFetch(params);

    // Clean up the debounce effect on unmount
    return () => {
      debouncedFetch.cancel();
    };
  }, [communityId, searchName, isFetch, page]);

  useEffect(() => {
    setPagination({
      page: page,
      size: 10,
      totalCount,
    });
  }, [totalCount, page]);

  const vendorTableColumns = [
    {
      dataField: "vendorName",
      text: "Vendor Name",
      align: "left",
      headerAlign: "left",
      headerClasses: "VendorsListVendorName",
      formatter: (v, row, index, formatExtraData, isMobile) => {
        return (
          <>
            <div>
              <Link
                id={`${isMobile ? "m-" : ""}vendors-${row.vendorId}`}
                to={path(`/admin/vendors/${row.vendorId}/1`)}
                className="VendorsList-Association-OrganizationName"
              >
                {v}
              </Link>
            </div>
            <Tooltip
              placement="top"
              target={`${isMobile ? "m-" : ""}vendors-${row.vendorId}`}
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
          </>
        );
      },
    },
    {
      dataField: "companyType",
      text: "Type",
      align: "left",
      headerAlign: "left",
      headerClasses: "VendorListDefault",
      formatter: (v, row) => {
        return v?.name || "-";
      },
    },
    {
      dataField: "premium",
      text: "Premium Subscriber",
      align: "left",
      headerAlign: "left",
      headerClasses: "VendorListDefault",
      formatter: (v, row) => {
        if (v) {
          return <>Yes</>;
        } else {
          return <>No</>;
        }
      },
    },
    {
      dataField: "primaryType",
      text: "Marketplace",
      headerClasses: "VendorListMarketplace",
      align: "center",
      headerAlign: "center",
      formatter: (v, row, index, formatExtraData, isMobile) => {
        return (
          <>
            <div id={`${isMobile ? "m-" : ""}vendors-marketplace-${row.vendorId}`}>
              <SwitchField
                name="marketplace"
                isChecked={!!v}
                className="AppointmentDetails-SwitchField"
                onChange={(name, check) => {
                  switchChange(name, check, row);
                }}
              />
            </div>

            <Tooltip
              placement="top"
              target={`${isMobile ? "m-" : ""}vendors-marketplace-${row.vendorId}`}
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
              A maximum of 9 vendors can be selected to show up on the Marketplace main page.
            </Tooltip>
          </>
        );
      },
    },
    {
      dataField: "@actions",
      text: "Actions",
      align: "right",
      headerAlign: "right",
      headerClasses: "VendorListActions",
      formatter: (v, row) => {
        return (
          <Actions
            hasUnlink={canEdit}
            onUnlink={() => {
              setUnlinkCurrentVendorId(row.vendorId);
              setWaringDialogText(`Do you want to unlink ${row.vendorName} from ${communityName}`);
              setWaringDialog(true);
            }}
            unlinkMessage={"Unlink vendor"}
          />
        );
      },
    },
  ];
  const vendorTableColumnsNoMarketplace = [
    {
      dataField: "vendorName",
      text: "Vendor Name",
      align: "left",
      headerAlign: "left",
      headerClasses: "VendorsListVendorName",
      formatter: (v, row, index, formatExtraData, isMobile) => {
        return (
          <>
            <div>
              <Link
                id={`${isMobile ? "m-" : ""}vendors-${row.vendorId}`}
                to={path(`/admin/vendors/${row.vendorId}/0`)}
                className="VendorsList-Association-OrganizationName"
              >
                {v}
              </Link>
            </div>
            <Tooltip
              placement="top"
              target={`${isMobile ? "m-" : ""}vendors-${row.vendorId}`}
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
          </>
        );
      },
    },
    {
      dataField: "companyType",
      text: "Type",
      align: "left",
      headerAlign: "left",
      headerClasses: "VendorListDefault",
      formatter: (v, row) => {
        return v?.name || "-";
      },
    },
    {
      dataField: "premium",
      text: "Premium Subscriber",
      align: "left",
      headerAlign: "left",
      headerClasses: "VendorListDefault",
      formatter: (v, row) => {
        if (v) {
          return <>Yes</>;
        } else {
          return <>No</>;
        }
      },
    },
  ];

  const modalConfirm = () => {
    setIsSubmitting(true);
    const selectedId = [];
    finalSelectedData.map((item) => {
      return selectedId.push(item.id);
    });

    adminVendorService
      .linkVendors({
        organizationId: params.orgId,
        referIds: selectedId,
        communityId: params.commId,
      })
      .then(() => {
        setIsSubmitting(false);
        setLinkVendorModelShow(false);
        //  重新加载数据

        setIsFetch(!isFetch);
        setPage(1);
      })
      .catch(() => {
        setIsSubmitting(false);
      });
  };

  const unLinkVendor = () => {
    setIsLoading(true);

    const body = {
      vendorId: unlinkCurrentVendorId,
      disReferId: params.commId,
    };

    adminVendorService
      .DisAddVendorAssociateCommunities(body)
      .then((res) => {
        setIsSuccessDialogOpen(true);
        setIsLoading(false);
        setIsFetch(!isFetch);
      })
      .catch(() => {
        // setIsFetch(!isFetch);
        setIsLoading(false);
        setErrorTitle("Unbinding failed, please try again.");
        setShowErrorDialog(true);
      });
  };

  return (
    <>
      <div className="">
        <Table
          hasHover
          hasOptions
          hasPagination
          keyField="vendorId"
          noDataText="No results"
          title="Linked Vendors"
          containerClass="VendorsListContainer"
          data={vendorData}
          pagination={pagination}
          isLoading={isLoading}
          columns={canEdit ? vendorTableColumns : vendorTableColumnsNoMarketplace}
          columnsMobile={["name", "vendorName", "primaryType"]}
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
                  {canEdit && (
                    <>
                      <Button
                        color="success"
                        className="CareTeamMemberList-Action AddCareMemberBtn"
                        onClick={() => setLinkVendorModelShow(true)}
                        tooltip={{
                          placement: "top",
                          trigger: "click hover",
                          text: "Link Vendors",
                          className: "CareTeamMemberList-Tooltip",
                        }}
                      >
                        Link Vendors
                      </Button>
                    </>
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
      </div>

      {linkVendorModelShow && (
        <AssociationConnectionModal
          tab={1}
          stepOneSelectedData={stepOneSelectedData}
          setStepOneSelectedData={setStepOneSelectedData}
          finalSelectedData={finalSelectedData}
          setFinalSelectedData={setFinalSelectedData}
          modalConfirm={modalConfirm}
          isSubmitting={isSubmitting}
          isOpen={linkVendorModelShow}
          onClose={() => setLinkVendorModelShow(false)}
        />
      )}

      {isSuccessDialogOpen && (
        <SuccessDialog
          isOpen
          title={"Successfully Unlinked."}
          buttons={compact([
            {
              text: "Close",
              outline: true,
              onClick: () => {
                setIsSuccessDialogOpen(false);
              },
            },
          ])}
        />
      )}

      {
        <WarningDialog
          isOpen={waringDialog}
          toggle={() => setWaringDialog(!waringDialog)}
          title={waringDialogText}
          buttons={[
            {
              text: "Cancel",
              color: "outline-success",
              onClick: () => {
                setWaringDialog(!waringDialog);
              },
            },
            {
              text: "OK",
              onClick: () => {
                setWaringDialog(!waringDialog);
                unLinkVendor();
              },
            },
          ]}
          onCancel={() => setWaringDialog(!waringDialog)}
        />
      }

      {showErrorDialog && (
        <ErrorDialog
          isOpen
          title={errorTitle}
          buttons={[
            {
              text: "Close",
              onClick: () => {
                setShowErrorDialog(false);
              },
            },
          ]}
        />
      )}
    </>
  );
};

export default VendorOfAssociation;
