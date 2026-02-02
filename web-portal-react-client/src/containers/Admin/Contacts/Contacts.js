import React, { memo, useState, useCallback, useEffect } from "react";

import cn from "classnames";

import { any } from "underscore";

import { Badge, Button, Collapse } from "reactstrap";

import DocumentTitle from "react-document-title";

import { useQueryClient } from "@tanstack/react-query";

import {
  useContactsQuery,
  useContactListState,
  useReducedContactFilterData,
  useContactFilterCombination,
} from "hooks/business/admin/contact";

import { Breadcrumbs, ErrorViewer } from "components";

import { UpdateSideBarAction } from "actions/admin";

import { isInteger } from "lib/utils/Utils";

import { SERVER_ERROR_CODES, CONTACT_STATUSES, VENDOR_SYSTEM_ROLES } from "lib/Constants";

import { ReactComponent as Filter } from "images/filters.svg";

import ContactList from "./ContactList/ContactList";
import ContactFilter from "./ContactFilter/ContactFilter";
import ContactPrimaryFilter from "./ContactPrimaryFilter/ContactPrimaryFilter";

import ContactEditor from "./ContactEditor/ContactEditor";
import ContactViewer from "./ContactViewer/ContactViewer";

import "./Contacts.scss";
import { useAuthUser } from "../../../hooks/common";

const { PENDING, EXPIRED } = CONTACT_STATUSES;

function isIgnoredError(e = {}) {
  return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE;
}

function Contacts({ className }) {
  const [selected, setSelected] = useState(null);

  const [isFilterOpen, toggleFilter] = useState(true);
  const [isEditorOpen, toggleEditor] = useState(false);
  const [isViewerOpen, toggleViewer] = useState(false);

  const { state, setError, clearError, changeFilter } = useContactListState();

  const { error } = state;

  const filterData = useReducedContactFilterData(state.filter.toJS());

  const canEditRole = selected?.canEditRole ?? true;

  const {
    sort,
    fetch,
    refresh,
    isFetching,
    pagination,
    data: { data } = {},
  } = useContactsQuery(filterData, { onError: setError });

  useEffect(() => {
    fetch();
  }, [JSON.stringify(filterData.communityIds)]);

  const { primary, custom } = useContactFilterCombination(
    {
      onChange: changeFilter,
    },
    {
      onChange: changeFilter,
      onApply: fetch,
      onReset: (isSaved) => isSaved && fetch(),
    },
    filterData,
  );
  const queryClient = useQueryClient();

  const communities = queryClient.getQueryData([
    "Directory.Communities",
    {
      organizationId: filterData.organizationId,
    },
  ]);
  const canAdd = any(communities, (o) => o.canAddContact);

  const onView = useCallback((o) => {
    toggleViewer(true);
    setSelected(isInteger(o) ? { id: o } : o);
  }, []);

  const onCloseViewer = useCallback(() => {
    setSelected(null);
    toggleViewer(false);
  }, []);

  const onAdd = useCallback(() => {
    toggleEditor(true);
    setSelected(null);
  }, []);

  const onEdit = useCallback((o) => {
    setSelected(o);
    toggleEditor(true);
  }, []);

  const onCloseEditor = useCallback(() => {
    setSelected(null);
    toggleEditor(false);
  }, []);

  return (
    <DocumentTitle title="Simply Connect | Admin | Contacts">
      <>
        <UpdateSideBarAction />
        <div className={cn("Contacts", className)}>
          <Breadcrumbs
            items={[
              { title: "Admin", href: "/admin/contacts" },
              { title: "Contacts", href: "/admin/contacts", isActive: true },
            ]}
            className="margin-bottom-10"
          />
          <ContactPrimaryFilter {...primary} className="margin-bottom-30" />
          <div className="Contacts-Header">
            <div className="Contacts-HeaderItem">
              <div className="Contacts-Title">
                <span className="Contacts-TitleText">Contacts</span>
                {pagination.totalCount ? (
                  <Badge color="info" className="Badge Badge_place_top-right">
                    {pagination.totalCount}
                  </Badge>
                ) : null}
              </div>
            </div>
            <div className="Contacts-HeaderItem">
              <div className="Contacts-Actions">
                <Filter
                  className={cn(
                    "ContactFilter-Icon",
                    isFilterOpen ? "ContactFilter-Icon_rotated_90" : "ContactFilter-Icon_rotated_0",
                  )}
                  onClick={() => toggleFilter(!isFilterOpen)}
                />
                {canAdd && (
                  <Button color="success" className="AddContactBtn" onClick={onAdd}>
                    Create Contact
                  </Button>
                )}
              </div>
            </div>
          </div>
          <Collapse isOpen={isFilterOpen}>
            <ContactFilter
              {...custom}
              organizationId={filterData.organizationId}
              communityIds={filterData.communityIds}
              className="margin-bottom-50"
            />
          </Collapse>

          <ContactList
            data={data}
            isFetching={isFetching}
            pagination={pagination}
            organizationId={state.filter.organizationId}
            onSort={sort}
            onEdit={onEdit}
            onRefresh={refresh}
            onView={onView}
          />

          {isViewerOpen && <ContactViewer isOpen contactId={selected?.id} onClose={onCloseViewer} />}

          {isEditorOpen && (
            <ContactEditor
              isOpen
              contactId={selected?.id}
              canEditRole={canEditRole}
              clientId={selected?.clientId}
              organizationId={filterData.organizationId}
              isPendingContact={selected?.status.name === PENDING}
              isExpiredContact={selected?.status.name === EXPIRED}
              onViewContact={onView}
              onClose={onCloseEditor}
              onSaveSuccess={fetch}
              onReInviteSuccess={fetch}
            />
          )}
          {error && !isIgnoredError(error) && <ErrorViewer isOpen error={error} onClose={clearError} />}
        </div>
      </>
    </DocumentTitle>
  );
}

export default memo(Contacts);
