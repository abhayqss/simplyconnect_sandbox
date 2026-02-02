import React, { useEffect, useState } from "react";

import DocumentTitle from "react-document-title";
import Actions from "components/Table/Actions/Actions";
import { path } from "lib/utils/ContextUtils";
import { Badge, Button, Col, Row, UncontrolledTooltip as Tooltip } from "reactstrap";

import Table from "components/Table/Table";
import SearchField from "components/SearchField/SearchField";
import Breadcrumbs from "components/Breadcrumbs/Breadcrumbs";

import { UpdateSideBarAction } from "actions/admin";
import { DateUtils } from "lib/utils/Utils";

import "./Associations.scss";
import { Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { clearAssociationDetail, featAssociationsList } from "../../../redux/Associations/AssociationsActions";
import AssociationEditor from "./AssociationEditor/AssociationEditor";
import SuccessDialog from "../../../components/dialogs/SuccessDialog/SuccessDialog";
import { compact } from "underscore";
import { map } from "lodash";
import moment from "moment/moment";

const { format, formats } = DateUtils;
import { ReactComponent as Asset } from "images/public/link.svg";
import { ReactComponent as NoAsset } from "images/public/noLink.svg";
import { useAuthUser } from "../../../hooks/common";
import {
  ALL_VENDORS_ROLES,
  CASE_MANAGER_ROLE,
  ONLY_VIEW_ROLES,
  SYSTEM_ROLES,
  VENDOR_SYSTEM_ROLES,
} from "../../../lib/Constants";
import adminAssociationsService, { AssociationsService } from "../../../services/AssociationsService";

const ICON_SIZE = 36;
const DATE_FORMAT = formats.americanMediumDate;

const Associations = () => {
  const dispatch = useDispatch();
  const { AssociationsList, AssociationsListTotal } = useSelector((state) => state.Associations);
  const user = useAuthUser();
  const isInOnlyViewAssociationRoles = ALL_VENDORS_ROLES.includes(user.roleName);
  const isCanViewCannotEditAssociationRoles = CASE_MANAGER_ROLE.includes(user.roleName);
  const [searchName, setSearchName] = useState("");
  const [isFetching, setIsFetching] = useState(false);
  const [showAdd, setShowAdd] = useState(false);
  const [selected, setSelected] = useState();
  const [isSuccessDialogOpen, toggleSuccessDialog] = useState(false);
  const [sort, setSort] = useState();
  const [page, setPage] = useState(1);

  const onChangeFilterField = (name, value) => {
    setSearchName(value);
  };
  const onClearSearchField = (name, value) => {
    setSearchName(value);
  };

  const [canAddAssociation, setCanAddAssociation] = useState(false);

  useEffect(() => {
    adminAssociationsService.canAddAssociation().then((res) => {
      setCanAddAssociation(res.data);
    });
  }, []);

  useEffect(() => {
    setIsFetching(true);
    const fetchData = _.debounce(() => {
      const params = {
        name: searchName,
        page: page - 1,
        size: 10,
        sort,
      };
      dispatch(featAssociationsList(params));
      setIsFetching(false);
    }, 300);

    fetchData();

    return () => {
      fetchData.cancel();
    };
  }, [searchName, page, sort]);

  const onSaveSuccess = () => {
    toggleSuccessDialog(true);
    setShowAdd(false);
  };

  const onConfirmSuccess = () => {
    setIsFetching(true);
    if (page === 1) {
      const params = {
        page: 0,
        size: 10,
        name: searchName,
      };
      setTimeout(() => {
        dispatch(featAssociationsList(params));
        toggleSuccessDialog(false);
        setIsFetching(false);
      }, 400);
    } else {
      toggleSuccessDialog(false);
      setPage(1);
      setSelected(null);
    }
  };

  const onEdit = (data) => {
    setSelected(data);
    setShowAdd(true);
  };

  const onSort = (field, sort) => {
    setSort(`${field},${sort}`);
  };

  const assetClick = (id) => {
    const params = new URLSearchParams({ associationId: id }).toString();
    const fullURL = `${process.env.REACT_APP_PUBLIC_URL}?${params}`;

    window.open(fullURL);
  };
  const { VENDOR } = SYSTEM_ROLES;
  let associationsColumns = [
    {
      dataField: "name",
      text: "Name",
      sort: true,
      onSort: onSort,
      headerClasses: "AssociationList-NameColHeader",
      formatter: (v, row, index, formatExtraData, isMobile) => {
        return (
          <>
            {!isInOnlyViewAssociationRoles ? (
              <div className="d-flex flex-row overflow-hidden">
                <Link
                  id={`${isMobile ? "m-" : ""}association-${row.id}`}
                  to={path(`/admin/associations/${row.id}`)}
                  className="AssociationList-AssociationName"
                >
                  {v}
                </Link>
                <Tooltip
                  placement="top"
                  target={`${isMobile ? "m-" : ""}association-${row.id}`}
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
                  View association details
                </Tooltip>
              </div>
            ) : (
              <div className="AssociationList-AssociationName">{v}</div>
            )}
          </>
        );
      },
    },
    {
      dataField: "buildingCount",
      text: "Communities",
      sort: true,
      align: "right",
      headerAlign: "right",
      headerClasses: "AssociationList-CommunitiesColHeader",
      onSort: onSort,
      formatter: (v, row) => {
        return v ? (
          <>
            <a
              tabIndex={0}
              data-toggle="tooltip"
              id={`association-${row.id}_building-count`}
              className="Associations-form-not-zero"
            >
              {v}
            </a>
            <Tooltip
              trigger="focus"
              placement="top"
              target={`association-${row.id}_building-count`}
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
              <div className="AssociationList-AffiliatedOrganizationName-List">
                {map(row?.referBuilding, (o) => (
                  <div key={o.id} className="AssociationList-AffiliatedOrganizationName">
                    {o.canView ? (
                      <Link
                        key={`affiliated-building-${o.id}`}
                        // to={path(`/admin/organizations/${o.id}`)}
                        className="AssociationList-AffiliatedOrganizationName"
                      >
                        {o.name}
                      </Link>
                    ) : (
                      <div style={{ cursor: "default" }} className="AssociationList-AffiliatedOrganizationName">
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
      dataField: "vendorCount",
      text: "Vendors",
      sort: true,
      align: "right",
      headerAlign: "right",
      headerClasses: "AssociationList-VendorsColHeader",
      onSort: onSort,
      formatter: (v, row) => {
        return v ? (
          <>
            <a
              tabIndex={0}
              data-toggle="tooltip"
              id={`association-${row.id}_vendor-count`}
              className="Associations-form-not-zero"
            >
              {v}
            </a>
            <Tooltip
              trigger="focus"
              placement="top"
              target={`association-${row.id}_vendor-count`}
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
              <div className="AssociationList-AffiliatedOrganizationName-List">
                {map(row?.referVendor, (o) => (
                  <div key={o.id} className="AssociationList-AffiliatedOrganizationName">
                    {o.canView ? (
                      <Link
                        key={`affiliated-vendor-${o.id}`}
                        // to={path(`/admin/organizations/${o.id}`)}
                        className="AssociationList-AffiliatedOrganizationName"
                      >
                        {o.name}
                      </Link>
                    ) : (
                      <div style={{ cursor: "default" }} className="AssociationList-AffiliatedOrganizationName">
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
      headerClasses: "AssociationList-OrganizationsColHeader",
      onSort: onSort,
      formatter: (v, row) => {
        return v ? (
          <>
            <a
              tabIndex={0}
              data-toggle="tooltip"
              id={`association-${row.id}_org-count`}
              className="Associations-form-not-zero"
            >
              {v}
            </a>
            <Tooltip
              trigger="focus"
              placement="top"
              target={`association-${row.id}_org-count`}
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
              <div className="AssociationList-AffiliatedOrganizationName-List">
                {map(row?.referOrganization, (o) => (
                  <div key={o.id} className="AssociationList-AffiliatedOrganizationName">
                    {o.canView ? (
                      <Link
                        key={`affiliated-org-${o.id}`}
                        // to={path(`/admin/organizations/${o.id}`)}
                        className="AssociationList-AffiliatedOrganizationName"
                      >
                        {o.name}
                      </Link>
                    ) : (
                      <div style={{ cursor: "default" }} className="AssociationList-AffiliatedOrganizationName">
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
      dataField: "automatically",
      text: "Created Automatically",
      sort: true,
      onSort: onSort,
      align: "right",
      headerAlign: "right",
      headerClasses: "AssociationList-CreatedAutomaticallyColHeader",
      formatter: (v) => (v ? "Yes" : "No"),
    },
    {
      dataField: "modifyTime",
      text: "Modified On",
      sort: true,
      align: "right",
      headerAlign: "right",
      headerStyle: { width: "10%" },
      onSort: onSort,
      formatter: (v) => v && moment(v).format("MM/DD/YYYY"),
    },
    {
      dataField: "@actions",
      text: "",
      headerStyle: {
        width: "60px",
      },
      align: "right",
      formatter: (v, row) => (
        <div className={"AssociationsActions"}>
          <>
            <Actions
              data={row}
              hasEditAction={row?.canEdit}
              iconSize={ICON_SIZE}
              editHintMessage="Edit association details"
              onEdit={(row) => {
                onEdit(row);
              }}
            />
            <div className={"Actions"}>
              {!(row.externalBuildingShow === false && row.externalVendorShow === false) ? (
                <Asset
                  className={`AssociationsActinsAsset`}
                  onClick={() => {
                    if (row.externalBuildingShow || row.externalVendorShow) {
                      assetClick(row.id);
                    }
                  }}
                />
              ) : (
                <NoAsset className={`AssociationsActinsAsset AssociationsActinsAssetDisabled`} />
              )}
            </div>
          </>
        </div>
      ),
    },
  ];

  return (
    <DocumentTitle title="Simply Connect | Admin | Associations">
      <>
        <UpdateSideBarAction />
        <div className={"Associations"}>
          <Breadcrumbs
            items={[
              { title: "Admin", href: "/admin/organizations" },
              { title: "Associations", href: "/admin/associations", isActive: true },
            ]}
          ></Breadcrumbs>
          <Table
            hasHover
            hasOptions
            hasPagination
            keyField="id"
            noDataText="No results"
            title="Associations"
            isLoading={isFetching}
            className={"AssociationsList"}
            containerClass={"AssociationsListContainer"}
            data={AssociationsList}
            pagination={{ ...AssociationsListTotal, page: page }}
            columns={associationsColumns}
            columnsMobile={["name", "communityCount"]}
            onRefresh={(num) => {
              setPage(num);
            }}
            renderCaption={(title) => {
              return (
                <div className="AssociationList-Caption">
                  <div className="AssociationList-CaptionHeader">
                    <span className="AssociationList-Title">
                      <span className="AssociationList-TitleText">{title}</span>
                      {AssociationsListTotal.totalCount ? (
                        <Badge color="info" className="Badge Badge_place_top-right">
                          {AssociationsListTotal.totalCount}
                        </Badge>
                      ) : null}
                    </span>
                    {canAddAssociation && (
                      <div className="AssociationList-ControlPanel">
                        <Button
                          color="success"
                          className="AddAssociationBtn"
                          onClick={() => {
                            setSelected(null);
                            setShowAdd(true);
                          }}
                        >
                          Add Association
                        </Button>
                      </div>
                    )}
                  </div>
                  <div className="AssociationList-Filter">
                    <Row>
                      <Col md={6} lg={4}>
                        <SearchField
                          name="name"
                          value={searchName}
                          placeholder="Search by association name"
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
            <AssociationEditor
              isOpen={showAdd}
              associationId={selected && selected?.id}
              onClose={() => {
                setSelected(null);
                setShowAdd(false);
                dispatch(clearAssociationDetail());
              }}
              onSaveSuccess={onSaveSuccess}
            />
          )}
          {isSuccessDialogOpen && (
            <SuccessDialog
              isOpen
              title={
                selected?.id || selected ? "Association details have been updated." : "The association has been created"
              }
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
        </div>
      </>
    </DocumentTitle>
  );
};

export default Associations;
