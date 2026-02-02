import React, { memo, useState, useEffect, useCallback } from "react";

import cn from "classnames";

import { useHistory } from "react-router-dom";

import { Badge, Button, Collapse } from "reactstrap";

import DocumentTitle from "react-document-title";

import { ErrorViewer } from "components";

import { SuccessDialog } from "components/dialogs";

import { useQueryInvalidation } from "hooks/common";

import {
  useSideBarUpdate,
  useProspectsQuery,
  useProspectListState,
  useCanAddProspectQuery,
  useReducedProspectFilterData,
  useProspectFilterCombination,
  useProspectFilterDefaultDataCache,
} from "hooks/business/Prospects";

import { isInteger } from "lib/utils/Utils";

import { isUnary } from "lib/utils/ArrayUtils";

import { path } from "lib/utils/ContextUtils";

import { ReactComponent as Filter } from "images/filters.svg";

import ProspectList from "./ProspectList/ProspectList";
import ProspectEditor from "./ProspectEditor/ProspectEditor";
import ProspectFilter from "./ProspectFilter/ProspectFilter";
import ProspectPrimaryFilter from "./ProspectPrimaryFilter/ProspectPrimaryFilter";

import "./Prospects.scss";

/**
 * @TODO FR-PRSP-17	
        Profile pictures of prospects with 'Inactive' and "Converted to Client"  status should be grayed out within the list.
*/
function Prospects() {
  const [edited, setEdited] = useState({});

  const [isFilterOpen, toggleFilter] = useState(false);

  const [isEditorOpen, toggleEditor] = useState(false);
  const [isSaveSuccessDialogOpen, toggleSaveSuccessDialog] = useState(false);

  const history = useHistory();

  const invalidate = useQueryInvalidation();

  const { state, clearError, changeFilter } = useProspectListState();

  const { error } = state;

  const filterData = useReducedProspectFilterData(state.filter.toJS());

  const {
    sort,
    fetch,
    refresh,
    isFetching,
    pagination,
    data: { data = [] } = {},
  } = useProspectsQuery(
    {
      ...filterData,
    },
    {
      staleTime: 0,
    },
  );

  //@TODO Checkboxes and Request signature button are out of scope of this task. it will be implemented in scope of Esign
  // const {
  //     select,
  //     selected,
  //     unSelect,
  //     selectAll,
  //     unSelectAll,
  //     areAllSelected
  // } = useSelection(data)

  // const communityIdsBySelected = useMemo(
  //     () => uniq(pluck(selected, 'communityId')),
  //     [selected]
  // )

  const { primary, custom } = useProspectFilterCombination(
    {
      onChange: changeFilter,
      onApply: fetch,
    },
    {
      onChange: changeFilter,
      onApply: fetch,
      onReset: (isSaved) => isSaved && fetch(),
    },
  );

  useProspectFilterDefaultDataCache({
    organizationId: filterData.organizationId,
  });

  useSideBarUpdate(null, { isHidden: true });

  const { data: canAdd } = useCanAddProspectQuery(
    {
      organizationId: filterData.organizationId,
    },
    {
      staleTime: 0,
      enabled: isInteger(filterData.organizationId),
    },
  );

  const open = useCallback(
    (id) => {
      history.push(path(`/prospects/${id}`));
    },
    [history],
  );

  const openEdited = useCallback(() => {
    if (edited) open(edited.id);
  }, [open, edited]);

  // const onSelect = useCallback(
  //     (prospect, isSelected) => isSelected ? select(prospect) : unSelect(prospect),
  //     [select, unSelect]
  // )

  // const onSelectAll = useCallback(
  //     (isSelected, prospects) => isSelected ? selectAll(prospects) : unSelectAll(prospects),
  //     [selectAll, unSelectAll]
  // )

  const onEdit = useCallback((o) => {
    setEdited(o);
    toggleEditor(true);
  }, []);

  const onCloseEditor = useCallback(() => {
    setEdited(null);
    toggleEditor(false);
  }, []);

  const onSaveSuccess = useCallback(
    (id) => {
      refresh();
      setEdited({ id });
      toggleSaveSuccessDialog(true);
      toggleEditor(false);
    },
    [refresh],
  );

  const onCloseSuccessDialog = useCallback(() => {
    setEdited(null);
    toggleSaveSuccessDialog(false);
  }, []);

  return (
    <DocumentTitle title="Simply Connect | Prospects">
      <>
        <div className="Prospects">
          <ProspectPrimaryFilter {...primary} className="margin-bottom-30" />
          <div className="Prospects-Header">
            <div className="Prospects-HeaderItem">
              <div className="Prospects-Title">
                <div className="Prospects-TitleText">Prospects</div>
                {pagination.totalCount > 0 && (
                  <Badge color="info" className="Badge Badge_place_top-right">
                    {pagination.totalCount}
                  </Badge>
                )}
              </div>
            </div>
            <div className="Prospects-HeaderItem">
              <div className="Prospects-Actions">
                <Filter
                  className={cn(
                    "ProspectFilter-Icon",
                    "Prospects-Action",
                    isFilterOpen ? "ProspectFilter-Icon_rotated_90" : "ProspectFilter-Icon_rotated_0",
                  )}
                  onClick={() => toggleFilter(!isFilterOpen)}
                />
                {/* {canAddESignRequest && (
                                    <Button
                                        outline
                                        color='success'
                                        onClick={() => toggleRequestSignature()}
                                        className="margin-right-24"
                                    >
                                        Request Signature
                                    </Button>
                                )} */}
                {canAdd && (
                  <>
                    <Button
                      color="success"
                      onClick={() => toggleEditor(true)}
                      id="add-appointment-btn"
                      className="AddAppointmentBtn"
                    >
                      Add New Prospect
                    </Button>
                  </>
                )}
              </div>
            </div>
          </div>
          <Collapse isOpen={isFilterOpen}>
            <ProspectFilter
              {...custom}
              organizationId={filterData.organizationId}
              communityIds={filterData.communityIds}
              className="margin-bottom-40"
            />
          </Collapse>
          <ProspectList
            data={data}
            // selected={selected}
            isFetching={isFetching}
            pagination={pagination}
            noDataText={isFilterOpen ? "No results." : "No records found"}
            // isSelectable={canAddESignRequest}
            // areAllSelected={areAllSelected}
            organizationId
            onSort={sort}
            onEdit={onEdit}
            // onSelect={onSelect}
            onRefresh={refresh}
            // onSelectAll={onSelectAll}
          />
        </div>

        <ProspectEditor
          isOpen={isEditorOpen}
          prospectId={edited?.id}
          organizationId={filterData.organizationId}
          communityId={isUnary(filterData.communityIds) ? filterData.communityIds[0] : undefined}
          onClose={onCloseEditor}
          onSaveSuccess={onSaveSuccess}
        />

        {/* <RequestSignatureEditor
                    isOpen={isRequestSignatureOpen}
                    onClose={() => toggleRequestSignature()}
                    organizationId={filterData.organizationId}
                    communityIds={communityIdsBySelected}
                    clients={selected}
                    isMultipleRequest
                    onUploadSuccess={() => onRequestSignatureSuccess()}
                /> */}

        {isSaveSuccessDialogOpen && (
          <SuccessDialog
            isOpen
            title={`The prospect record has been ${edited?.id ? "updated" : "created"}`}
            buttons={[
              { text: "Close", onClick: onCloseSuccessDialog },
              { text: "View record", onClick: openEdited },
            ]}
          />
        )}

        {error && <ErrorViewer isOpen error={error} onClose={clearError} />}
      </>
    </DocumentTitle>
  );
}

export default memo(Prospects);
