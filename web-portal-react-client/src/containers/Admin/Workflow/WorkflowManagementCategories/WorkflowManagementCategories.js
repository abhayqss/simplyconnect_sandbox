import "./WorkflowManagementCategories.scss";
import { UpdateSideBarAction } from "../../../../actions/admin";
import { Breadcrumbs, PrimaryFilter } from "../../../../components";
import React, { useEffect, useState } from "react";
import DocumentTitle from "react-document-title";
import Table from "../../../../components/Table/Table";
import Actions from "../../../../components/Table/Actions/Actions";
import { useAuthUser } from "../../../../hooks/common";
import { Col, Row } from "reactstrap";
import Button from "../../../../components/buttons/Button/Button";
import AddWorkflowCategoriesModal from "../AddWorkflowCategoriesModal/AddWorkflowCategoriesModal";
import adminWorkflowCategoryService from "../../../../services/AdminWorkflowCategoryService";
import { ErrorDialog, SuccessDialog } from "../../../../components/dialogs";
import { useCommunityPrimaryFilterDirectory } from "../../../../hooks/business/Marketplace";
import { usePrimaryFilter } from "../../../../hooks/common/filter";
import SearchField from "../../../../components/SearchField/SearchField";

const WorkflowManagementCategories = () => {
  const user = useAuthUser();
  const isSuperAdmin = user.roleName === "ROLE_SUPER_ADMINISTRATOR";

  const [sort, setSort] = useState("");
  const [isFetching, setIsFetching] = useState(false);
  const [workflowCategoryData, setWorkflowCategoryData] = useState([]);
  const [page, setPage] = useState(1);
  const [isShowAddCategoryModal, setIsShowAddCategoryModal] = useState(false);
  const [isSaveWorkflowCategorySuccessDialog, setIsSaveWorkflowCategorySuccessDialog] = useState(false);
  const [isShowSaveError, setIsShowSaveError] = useState(false);
  const [errorText, setErrorText] = useState("");
  const [isEdit, setIsEdit] = useState(false);
  const [editId, setEditId] = useState(false);
  const [editCategoryName, setEditCategoryName] = useState();
  const [addModalLoading, setAddModalLoading] = useState(false);
  const [editCategoryOrganizationId, setEditCategoryOrganizationId] = useState(null);
  const [totalCount, setTotalCount] = useState(0);
  const [searchName, setSearchName] = useState(null);
  const primaryFilter = usePrimaryFilter("FEATURED_COMMUNITY_PRIMARY_FILTER", {
    isCommunityMultiSelection: false,
  });
  const { organizationId } = primaryFilter.data;
  useEffect(() => {
    organizationId &&
      getAllCategoryListData({
        page: page - 1,
        size: 10,
        organizationId,
        sort,
        categoryName: searchName,
      });
  }, [organizationId, page, sort, searchName]);

  const onSort = (field, sort) => {
    setSort(`${field},${sort}`);
  };
  const onEditItem = (data) => {
    setIsEdit(true);
    setEditId(data.id);
    setEditCategoryOrganizationId(organizationId);
    setEditCategoryName(data.categoryName);
    setIsShowAddCategoryModal(true);
  };

  const onShowAddNewModal = () => {
    setIsShowAddCategoryModal(true);
    setEditCategoryOrganizationId(null);
    setEditCategoryName(null);
  };
  const onConfirmAddWorkflowCategory = (params) => {
    setAddModalLoading(true);
    adminWorkflowCategoryService
      .saveCategory(params)
      .then((res) => {
        if (res.success) {
          if (params.categoryId) {
            setIsEdit(true);
            setEditId(null);
          } else {
            setIsEdit(false);
          }
          setIsShowAddCategoryModal(false);
          setIsSaveWorkflowCategorySuccessDialog(true);
          setAddModalLoading(false);
        }
      })
      .catch((error) => {
        setErrorText(error.message);
        setIsShowSaveError(true);
        setAddModalLoading(false);
      });
  };
  const onCancelAddWorkflowCategory = () => {
    setIsShowAddCategoryModal(false);
  };
  const changeSearchName = (_, value) => {
    setSearchName(value);
  };

  const onCloseSuccessDialog = () => {
    setIsSaveWorkflowCategorySuccessDialog(false);
    // 刷新页面
    getAllCategoryListData({
      page: page - 1,
      size: 10,
      organizationId,
      sort,
      categoryName: searchName,
    });
  };

  const getAllCategoryListData = (params) => {
    setIsFetching(true);
    adminWorkflowCategoryService
      .getAllCategory(params)
      .then((res) => {
        if (res.success) {
          setWorkflowCategoryData(res.data);
          setIsFetching(false);
          setTotalCount(res.totalCount);
        }
      })
      .catch((e) => {
        setErrorText(e.message);
        setIsShowSaveError(true);
        setIsFetching(false);
      });
  };

  const columns = [
    {
      dataField: "categoryName",
      text: "Category",
      sort: true,
      onSort: onSort,
      headerClasses: "workflow-category",
      formatter: (v, row) => {
        return <div className="workflow-category-name">{v}</div>;
      },
    },

    {
      dataField: "templateCount",
      text: "Number of workflow",
      sort: true,
      onSort: onSort,
      headerClasses: "workflow-category" + "",
      formatter: (v, row) => {
        return <div className="category-name">{v}</div>;
      },
    },
    {
      dataField: "@actions",
      text: "",
      headerStyle: {
        width: "80px",
      },
      align: "left",
      formatter: (v, row) => {
        return (
          <Actions
            data={row}
            hasEditAction={true}
            iconSize={36}
            editHintMessage="edit details"
            onEdit={(row) => {
              onEditItem(row);
            }}
          />
        );
      },
    },
  ];

  const { communities, organizations } = useCommunityPrimaryFilterDirectory(
    { organizationId },
    { actions: primaryFilter.changeCommunityField },
  );
  return (
    <>
      <DocumentTitle title={"Simply Connect | Admin | workflow Management | categories Management"}>
        <div className={"Workflow-Management"}>
          <UpdateSideBarAction />
          <Breadcrumbs
            className={"margin-bottom-10"}
            items={[
              { title: "Admin", href: "/admin/workflowManagement" },
              {
                title: isSuperAdmin ? "Workflow Library" : "Workflow Management",
                href: "/admin/workflowManagement",
              },
              {
                title: "Categories Management",
                href: `#`,
                isActive: true,
              },
            ]}
          />
          <PrimaryFilter
            communities={communities}
            organizations={organizations}
            {...primaryFilter}
            hasCommunityField={false}
            onChangeOrganizationField={(organizationId) => primaryFilter.changeOrganizationField(organizationId)}
            isCommunityMultiSelection={false}
            classNameOrg="classNameOrg"
          />
          <Table
            hasHover
            hasOptions
            hasPagination={true}
            keyField={"id"}
            noDataText={"No data"}
            title="Manage Categories"
            isLoading={isFetching}
            className={"workflow-categories-list"}
            data={workflowCategoryData}
            columns={columns}
            pagination={{ page, size: 10, totalCount }}
            columnsMobile={["category", "number", "@action"]}
            onRefresh={(num) => {
              setPage(num);
            }}
            renderCaption={(title) => {
              return (
                <>
                  <div className={"workflow-category-captionHeader margin-bottom-10"}>
                    <Row>
                      <Col>
                        <div className={"workflow-category-title"}>{title}</div>
                      </Col>
                      <Col style={{ display: "flex", justifyContent: "flex-end" }}>
                        <Button color={"success"} onClick={onShowAddNewModal} className={"workflow-category-add-new"}>
                          Add New Category
                        </Button>
                      </Col>
                    </Row>
                    <Row className={"margin-top-15"}>
                      <Col md={6} lg={6}>
                        <SearchField
                          name="searchName"
                          value={searchName}
                          placeholder="Search keywords"
                          onClear={() => {
                            setSearchName(null);
                          }}
                          onChange={changeSearchName}
                        />
                      </Col>
                    </Row>
                  </div>
                </>
              );
            }}
          />
        </div>
      </DocumentTitle>
      {isShowAddCategoryModal && (
        <AddWorkflowCategoriesModal
          addModalLoading={addModalLoading}
          isOpen={isShowAddCategoryModal}
          categoryId={editId}
          editCategoryName={editCategoryName}
          categoryOrganizationId={Number(editCategoryOrganizationId)}
          onConfirm={(params) => onConfirmAddWorkflowCategory(params)}
          onCancel={onCancelAddWorkflowCategory}
          isSuperAdmin={isSuperAdmin}
        />
      )}
      {isSaveWorkflowCategorySuccessDialog && (
        <SuccessDialog
          isOpen={isSaveWorkflowCategorySuccessDialog}
          title={isEdit ? `The category was edited successfully.` : `The category was created successfully`}
          buttons={[
            {
              text: "Close",
              onClick: () => {
                onCloseSuccessDialog();
              },
            },
          ]}
        />
      )}
      {isShowSaveError && (
        <ErrorDialog
          isOpen={isShowSaveError}
          title={errorText}
          buttons={[
            {
              text: "OK",
              onClick: () => {
                setIsShowSaveError(false);
                setErrorText("");
              },
            },
          ]}
        ></ErrorDialog>
      )}
    </>
  );
};

export default WorkflowManagementCategories;
