import React, { Component } from "react";

import cn from "classnames";

import { debounce, map } from "lodash";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";
import { Link, Redirect } from "react-router-dom";

import DocumentTitle from "react-document-title";

import { Badge, Button, Col, Row, UncontrolledTooltip as Tooltip } from "reactstrap";

import Table from "components/Table/Table";
import Actions from "components/Table/Actions/Actions";
import SearchField from "components/SearchField/SearchField";
import Breadcrumbs from "components/Breadcrumbs/Breadcrumbs";
import ErrorViewer from "components/ErrorViewer/ErrorViewer";
import ConfirmDialog from "components/dialogs/ConfirmDialog/ConfirmDialog";
import SuccessDialog from "components/dialogs/SuccessDialog/SuccessDialog";

import OrganizationEditor from "./OrganizationEditor/OrganizationEditor";

import { UpdateSideBarAction } from "actions/admin";

import * as sideBarActions from "redux/sidebar/sideBarActions";

import * as organizationFormActions from "redux/organization/form/organizationFormActions";
import * as organizationListActions from "redux/organization/list/organizationListActions";
import * as organizationCountActions from "redux/organization/count/organizationCountActions";
import * as canAddOrganizationActions from "redux/organization/can/add/canAddOrganizationActions";

import { PAGINATION, SERVER_ERROR_CODES } from "lib/Constants";

import { path } from "lib/utils/ContextUtils";
import { DateUtils, isEmpty } from "lib/utils/Utils";

import { ReactComponent as Warning } from "images/alert-yellow.svg";
import service from "services/OrganizationService";

import "./Organizations.scss";

const { FIRST_PAGE } = PAGINATION;

const { format, formats } = DateUtils;

const ICON_SIZE = 36;
const DATE_FORMAT = formats.americanMediumDate;

function isIgnoredError(e = {}) {
  return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE;
}

function mapStateToProps(state) {
  const { can, list, form, count } = state.organization;

  return {
    error: list.error,
    isFetching: list.isFetching,
    dataSource: list.dataSource,
    shouldReload: list.shouldReload,

    can,
    form,
    count,

    auth: state.auth,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      ...bindActionCreators(organizationListActions, dispatch),
      form: bindActionCreators(organizationFormActions, dispatch),
      count: bindActionCreators(organizationCountActions, dispatch),
      can: {
        add: bindActionCreators(canAddOrganizationActions, dispatch),
      },

      sidebar: bindActionCreators(sideBarActions, dispatch),
    },
  };
}

class Organizations extends Component {
  state = {
    selected: null,

    shouldOpenDetails: false,

    isEditorOpen: false,
    isDeleteOpen: false,
    isDeleteSuccessDialogOpen: false,
    isSaveSuccessDialogOpen: false,
    isCancelEditConfirmDialogOpen: false,
  };
  update = debounce((isReload, page) => {
    const { isFetching, shouldReload, dataSource: ds } = this.props;

    if (isReload || shouldReload || (!isFetching && isEmpty(ds.data))) {
      const { actions } = this.props;
      const { field, order } = ds.sorting;

      const { page: p, size } = ds.pagination;

      actions.load({
        size,
        page: page || p,
        ...ds.filter.toJS(),
        sort: `${field},${order}`,
      });
    }
  }, 450);

  componentDidMount() {
    this.refresh();

    this.canAdd();
    this.loadCount();
  }

  componentDidUpdate() {
    if (this.props.shouldReload) {
      this.refresh();
    }
  }

  onRefresh = (page) => {
    this.refresh(page);
  };

  onChangeFilterField = (name, value) => {
    this.changeFilterField(name, value);
  };

  onClearSearchField = (name, value) => {
    this.changeFilterField(name, "");
  };

  onAdd = () => {
    this.setState({ isEditorOpen: true });
  };

  onEdit = (organization) => {
    this.setState({
      isEditorOpen: true,
      selected: organization,
    });
  };

  onDelete = (organization) => {
    this.setState({
      isDeleteOpen: true,
      selected: organization,
    });
  };

  onCloseCancelEditConfirmDialog = () => {
    this.setState({
      isCancelEditConfirmDialogOpen: false,
    });
  };

  onCloseEditor = (shouldConfirm = false) => {
    this.setState((s) => ({
      selected: shouldConfirm ? s.selected : null,
      isEditorOpen: shouldConfirm,
      isSaveSuccessDialogOpen: false,
      isCancelEditConfirmDialogOpen: shouldConfirm,
    }));
  };
  onCancelDeleteOrg = (shouldConfirm = false) => {
    this.setState((s) => ({
      selected: shouldConfirm ? s.selected : null,
      isDeleteOpen: shouldConfirm,
    }));
  };
  onConfirmDeleteOrg = (id) => {
    service.deleteOrganization(id).then((res) => {
      if (res.success) {
        this.setState((s) => ({
          isDeleteOpen: false,
          isDeleteSuccessDialogOpen: true,
          selected: null,
        }));
      }
    });
  };

  onSuccessDeleteOrganization = () => {
    this.setState((s) => ({
      isDeleteSuccessDialogOpen: false,
    }));
    this.onRefresh();
  };

  onSaveSuccess = (id, isNew) => {
    this.refresh();

    this.setState((s) => ({
      isEditorOpen: false,
      isSaveSuccessDialogOpen: true,
      selected: isNew ? { id, isNew } : s.selected,
    }));
  };

  onDetails = () => {
    this.setState({
      shouldOpenDetails: true,
      isSaveSuccessDialogOpen: false,
    });
  };

  onConfigureOrganization = (organization) => {
    alert("Coming soon!");
  };

  onSort = (field, order) => {
    this.sort(field, order);
  };

  onResetError = () => {
    const { actions } = this.props;

    actions.clearError();
    actions.form.clearError();
  };

  getError() {
    const { error, form } = this.props;

    return error || form.error;
  }

  sort(field, order) {
    this.props.actions.sort(field, order);
  }

  refresh(page) {
    this.update(true, page || FIRST_PAGE);
  }

  clear() {
    this.props.actions.clear();
  }

  changeFilterField(name, value, shouldReload) {
    this.props.actions.changeFilterField(name, value, shouldReload);
  }

  loadCount() {
    this.props.actions.count.load();
  }

  canAdd() {
    this.props.actions.can.add.load();
  }

  render() {
    const { className } = this.props;

    const {
      selected,

      shouldOpenDetails,

      isEditorOpen,
      isDeleteOpen,
      isDeleteSuccessDialogOpen,
      isSaveSuccessDialogOpen,
      isCancelEditConfirmDialogOpen,
    } = this.state;

    const { can, count, isFetching, dataSource: ds } = this.props;

    if (shouldOpenDetails) {
      return <Redirect push to={path(`admin/organizations/${selected.id}`)} />;
    }

    const error = this.getError();

    return (
      <DocumentTitle title="Simply Connect | Admin | Organizations">
        <>
          <UpdateSideBarAction />
          <div className={cn("Organizations", className)}>
            <Breadcrumbs
              items={[
                { title: "Admin", href: "/admin/organizations" },
                { title: "Organizations", href: "/admin/organizations", isActive: true },
              ]}
            />
            <Table
              hasHover
              hasOptions
              hasPagination
              keyField="id"
              noDataText="No results"
              title="Organizations"
              isLoading={isFetching}
              className="OrganizationList"
              containerClass="OrganizationListContainer"
              data={ds.data}
              pagination={ds.pagination}
              columns={[
                {
                  dataField: "name",
                  text: "Name",
                  sort: true,
                  onSort: this.onSort,
                  formatter: (v, row, index, formatExtraData, isMobile) => {
                    return (
                      <>
                        {row.canView ? (
                          <>
                            <div className="d-flex flex-row overflow-hidden">
                              <Link
                                id={`${isMobile ? "m-" : ""}organization-${row.id}`}
                                to={path(`/admin/organizations/${row.id}`)}
                                F
                                className="OrganizationList-OrganizationName"
                              >
                                {v}
                              </Link>
                            </div>
                            <Tooltip
                              placement="top"
                              target={`${isMobile ? "m-" : ""}organization-${row.id}`}
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
                          <span
                            className={cn(
                              "overflow-hidden",
                              "OrganizationList-OrganizationName",
                              "OrganizationList-OrganizationName_disabled",
                            )}
                          >
                            {v}
                          </span>
                        )}
                      </>
                    );
                  },
                },
                {
                  dataField: "communityCount",
                  text: "Communities",
                  sort: false,
                  align: "right",
                  headerAlign: "right",
                  headerClasses: "OrganizationList-CommunitiesColHeader",
                  onSort: this.onSort,
                  formatter: (v, row) => {
                    return v ? (
                      <>
                        <a
                          tabIndex={0}
                          data-toggle="tooltip"
                          id={`organization-${row.id}_comm-count`}
                          className="OrganizationList-AffiliatedOrganizationCount"
                        >
                          {v}
                        </a>
                        <Tooltip
                          trigger="focus"
                          placement="top"
                          target={`organization-${row.id}_comm-count`}
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
                          <div className="OrganizationList-AffiliatedOrganizations">
                            {map(row?.communityNameList, (o) => (
                              <div key={o} className="OrganizationList-AffiliatedOrganization">
                                {o.canView ? (
                                  <Link
                                    key={`affiliated-organization-${o.id}`}
                                    className="OrganizationList-AffiliatedOrganizationName"
                                  >
                                    {o}
                                  </Link>
                                ) : (
                                  <div
                                    style={{ cursor: "default" }}
                                    className="OrganizationList-AffiliatedOrganizationName"
                                  >
                                    {o}
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
                  sort: false,
                  align: "right",
                  headerAlign: "right",
                  headerClasses: "OrganizationList-Vendors-ColHeader",
                  onSort: false,
                  formatter: (v, row) => {
                    return v ? (
                      <>
                        <a
                          tabIndex={0}
                          data-toggle="tooltip"
                          id={`organization-${row.id}_vendor-count`}
                          className="OrganizationList-AffiliatedOrganizationCount"
                        >
                          {v}
                        </a>
                        <Tooltip
                          trigger="focus"
                          placement="top"
                          target={`organization-${row.id}_vendor-count`}
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
                          <div className="OrganizationList-AffiliatedOrganizations">
                            {map(row?.vendorNameList, (o) => (
                              <div key={o} className="OrganizationList-AffiliatedOrganization">
                                {o.canView ? (
                                  <Link
                                    key={`affiliated-organization-${o.id}`}
                                    className="OrganizationList-AffiliatedOrganizationName"
                                  >
                                    {o}
                                  </Link>
                                ) : (
                                  <div
                                    style={{ cursor: "default" }}
                                    className="OrganizationList-AffiliatedOrganizationName"
                                  >
                                    {o}
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
                  dataField: "associationCount",
                  text: "Associations",
                  sort: false,
                  align: "right",
                  headerAlign: "right",
                  headerClasses: "OrganizationList-AffiliatedColHeader",
                  onSort: this.onSort,
                  formatter: (v, row) => {
                    return v ? (
                      <>
                        <a
                          tabIndex={0}
                          data-toggle="tooltip"
                          id={`organization-${row.id}_association-count`}
                          className="OrganizationList-AffiliatedOrganizationCount"
                        >
                          {v}
                        </a>
                        <Tooltip
                          trigger="focus"
                          placement="top"
                          target={`organization-${row.id}_association-count`}
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
                          <div className="OrganizationList-AffiliatedOrganizations">
                            {map(row.associationNameList, (o) => (
                              <div key={o.id} className="OrganizationList-AffiliatedOrganization">
                                {o.canView ? (
                                  <Link
                                    key={`affiliated-organization-${o.id}`}
                                    // to={path(`/admin/organizations/${o.id}`)}
                                    className="OrganizationList-AffiliatedOrganizationName"
                                  >
                                    {o}
                                  </Link>
                                ) : (
                                  <div
                                    style={{ cursor: "default" }}
                                    className="OrganizationList-AffiliatedOrganizationName"
                                  >
                                    {o}
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
                  dataField: "affiliatedOrganizations",
                  text: "Affiliated Organizations",
                  sort: false,
                  align: "right",
                  headerAlign: "right",
                  headerClasses: "OrganizationList-AffiliatedOrganizationsColHeader",
                  onSort: this.onSort,
                  formatter: (v, row) => {
                    return v?.length > 0 ? (
                      <>
                        <a
                          tabIndex={0}
                          data-toggle="tooltip"
                          id={`organization-${row.id}_affiliated-count`}
                          className="OrganizationList-AffiliatedOrganizationCount"
                        >
                          {v?.length}
                        </a>
                        <Tooltip
                          trigger="focus"
                          placement="top"
                          target={`organization-${row.id}_affiliated-count`}
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
                          <div className="OrganizationList-AffiliatedOrganizations">
                            {map(v, (o) => (
                              <div key={o.id} className="OrganizationList-AffiliatedOrganization">
                                {o.canView ? (
                                  <Link
                                    key={`affiliated-organization-${o.id}`}
                                    to={path(`/admin/organizations/${o.id}`)}
                                    className="OrganizationList-AffiliatedOrganizationName"
                                  >
                                    {o.name}
                                  </Link>
                                ) : (
                                  <div
                                    style={{ cursor: "default" }}
                                    className="OrganizationList-AffiliatedOrganizationName"
                                  >
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
                  dataField: "createdAutomatically",
                  text: "Created Automatically",
                  sort: false,
                  onSort: this.onSort,
                  headerClasses: "OrganizationList-CreatedAutomaticallyColHeader",
                  formatter: (v) => (v ? "Yes" : "No"),
                },
                {
                  dataField: "lastModified",
                  text: "Modified On",
                  sort: false,
                  align: "right",
                  headerAlign: "right",
                  headerStyle: { width: "10%" },
                  onSort: this.onSort,
                  formatter: (v) => v && format(v, DATE_FORMAT),
                },
                {
                  dataField: "@actions",
                  text: "",
                  headerStyle: {
                    width: "120px",
                  },
                  align: "right",
                  formatter: (v, row) => (
                    <div style={{ display: "flex" }}>
                      <Actions
                        data={row}
                        hasEditAction={row.canEdit}
                        iconSize={ICON_SIZE}
                        configureHintMessage="Configure password"
                        editHintMessage="Edit organization details"
                        onEdit={this.onEdit}
                        onConfigure={this.onConfigureOrganization}
                      />
                      <Actions
                        data={row}
                        hasDeleteAction={row?.canDelete}
                        iconSize={ICON_SIZE}
                        deleteHintMessage="Delete organization item"
                        onDelete={this.onDelete}
                      />
                    </div>
                  ),
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
                  <div className="OrganizationList-Caption">
                    <div className="OrganizationList-CaptionHeader">
                      <span className="OrganizationList-Title">
                        <span className="OrganizationList-TitleText">{title}</span>
                        {ds.pagination.totalCount ? (
                          <Badge color="info" className="Badge Badge_place_top-right">
                            {ds.pagination.totalCount}
                          </Badge>
                        ) : null}
                      </span>
                      <div className="OrganizationList-ControlPanel">
                        {can.add.value && (
                          <Button color="success" className="AddOrganizationBtn" onClick={this.onAdd}>
                            Add Organization
                          </Button>
                        )}
                      </div>
                    </div>
                    <div className="OrganizationList-Filter">
                      <Row>
                        <Col md={6} lg={4}>
                          <SearchField
                            name="name"
                            value={ds.filter.name}
                            placeholder="Search by organization name"
                            onClear={this.onClearSearchField}
                            onChange={this.onChangeFilterField}
                          />
                        </Col>
                      </Row>
                    </div>
                  </div>
                );
              }}
              onRefresh={this.onRefresh}
            />
            {isEditorOpen && (
              <OrganizationEditor
                isOpen={isEditorOpen}
                organizationId={selected && selected.id}
                onClose={this.onCloseEditor}
                onSaveSuccess={this.onSaveSuccess}
              />
            )}

            {isDeleteOpen && (
              <ConfirmDialog
                isOpen={isDeleteOpen}
                icon={Warning}
                confirmBtnText="Sure"
                title="The organization will be delete"
                onConfirm={() => this.onConfirmDeleteOrg(selected.id)}
                onCancel={this.onCancelDeleteOrg}
              />
            )}

            {isDeleteSuccessDialogOpen && (
              <SuccessDialog
                isOpen={isDeleteSuccessDialogOpen}
                className={className}
                title={`Organization has been deleted`}
                buttons={[
                  {
                    text: "OK",
                    onClick: this.onSuccessDeleteOrganization,
                  },
                ]}
              />
            )}

            {isCancelEditConfirmDialogOpen && (
              <ConfirmDialog
                isOpen
                icon={Warning}
                confirmBtnText="OK"
                title="The updates will not be saved"
                onConfirm={this.onCloseEditor}
                onCancel={this.onCloseCancelEditConfirmDialog}
              />
            )}

            {isSaveSuccessDialogOpen && (
              <SuccessDialog
                isOpen
                className={className}
                title={`Organization ${selected.isNew ? "has been created" : "details have been updated"}.`}
                buttons={
                  selected.isNew
                    ? [
                        {
                          text: "Close",
                          outline: true,
                          className: "min-width-120 margin-left-80",
                          onClick: () => {
                            this.onCloseEditor();
                          },
                        },
                        {
                          text: "View Details",
                          className: "min-width-120 margin-right-80",
                          onClick: this.onDetails,
                        },
                      ]
                    : [
                        {
                          text: "OK",
                          onClick: this.onDetails,
                        },
                      ]
                }
              />
            )}
            {error && !isIgnoredError(error) && <ErrorViewer isOpen error={error} onClose={this.onResetError} />}
          </div>
        </>
      </DocumentTitle>
    );
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Organizations);
