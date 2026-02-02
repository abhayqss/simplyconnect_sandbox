import React, { memo, useState, useEffect } from "react";

import { useParams } from "react-router-dom";

import { Button } from "reactstrap";

import DocumentTitle from "react-document-title";

import { Breadcrumbs, ErrorViewer } from "components";

import { WarningDialog } from "components/dialogs";

import OrganizationCategoryEditor from "./OrganizationCategoryEditor/OrganizationCategoryEditor";

import { useSideBarUpdate } from "hooks/common/redux";

import {
  useOrganizationCategoriesQuery,
  useCanAddOrganizationCategoryQuery,
  useDeleteOrganizationCategoryMutation,
} from "hooks/business/admin/organization";

import { getSideBarItems } from "../../SideBarItems";

import { OrganizationCategoryList } from "./";

import "./OrganizationCategories.scss";

function OrganizationCategories() {
  const [selected, setSelected] = useState(null);
  const [isDeleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [isOpenCategoriesEditor, setOpenCategoriesEditor] = useState(false);

  const { orgId: organizationId } = useParams();

  const {
    sort,
    fetch,
    refresh,
    isFetching,
    pagination,
    data: { data } = {},
  } = useOrganizationCategoriesQuery({ organizationId });

  const { data: canAdd } = useCanAddOrganizationCategoryQuery({ organizationId });

  const {
    error,
    reset,
    mutateAsync: remove,
    isLoading: isDeleting,
  } = useDeleteOrganizationCategoryMutation({
    onSuccess: fetch,
  });

  const updateSideBar = useSideBarUpdate();

  function addNewCategory() {
    setOpenCategoriesEditor(true);
  }

  function editCategory(category) {
    setSelected(category);
    setOpenCategoriesEditor(true);
  }

  function closeCategoriesEditor() {
    setSelected(null);
    setOpenCategoriesEditor(false);
  }

  function tryDeleting(category) {
    setSelected(category);
    setDeleteDialogOpen(true);
  }

  function closeDeleteDialog() {
    setSelected(null);
    setDeleteDialogOpen(false);
  }

  function deleteCategory() {
    remove(selected.id);
    closeDeleteDialog();
  }

  useEffect(() => {
    fetch();
  }, [fetch]);

  useEffect(() => {
    updateSideBar({
      isHidden: false,
      items: getSideBarItems(),
    });
  }, [updateSideBar]);

  return (
    <DocumentTitle title="Simply Connect | Admin | Vendors | Organization Details | Categories management">
      <div className="OrganizationCategories">
        <Breadcrumbs
          items={[
            { title: "Admin", href: "/admin/organizations" },
            { title: "Organization Details", href: `/admin/organizations/${organizationId}` },
            { title: "Categories Management", href: `#`, isActive: true },
          ]}
          className="margin-bottom-35"
        />
        <div className="OrganizationCategories-Header">
          <div className="OrganizationCategories-HeaderItem">
            <div className="OrganizationCategories-Title">Categories Management</div>
          </div>
          <div className="OrganizationCategories-HeaderItem">
            <div className="OrganizationCategories-Actions">
              {canAdd && (
                <Button color="success" className="OrganizationCategories-Action" onClick={addNewCategory}>
                  Add New Category
                </Button>
              )}
            </div>
          </div>
        </div>
        <OrganizationCategoryList
          data={data}
          isFetching={isFetching || isDeleting}
          pagination={pagination}
          onEdit={editCategory}
          onDelete={tryDeleting}
          onRefresh={refresh}
          onSort={sort}
        />
        <OrganizationCategoryEditor
          isOpen={isOpenCategoriesEditor}
          category={selected}
          onClose={closeCategoriesEditor}
          organizationId={organizationId}
          onSubmit={fetch}
        />
        {isDeleteDialogOpen && (
          <WarningDialog
            isOpen
            title="The category will be deleted"
            buttons={[
              {
                text: "Cancel",
                outline: true,
                onClick: closeDeleteDialog,
              },
              {
                text: "Delete",
                color: "success",
                onClick: deleteCategory,
              },
            ]}
          />
        )}
        {error && <ErrorViewer isOpen error={error} onClose={reset} />}
      </div>
    </DocumentTitle>
  );
}

export default memo(OrganizationCategories);
