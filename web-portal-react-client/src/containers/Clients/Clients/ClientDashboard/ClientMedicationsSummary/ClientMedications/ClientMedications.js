import React, { useMemo, useState, useCallback, useEffect } from "react";

import { ReactComponent as Remove } from "images/remove.svg";
import { ReactComponent as Warning } from "images/warning.svg";
import cn from "classnames";
import { map } from "underscore";

import Truncate from "react-truncate";

import { connect } from "react-redux";

import { ListGroup as List, ListGroupItem as ListItem } from "reactstrap";

import { Action, IconButton } from "components";

import { CheckboxField } from "components/Form";

import { useMedicationsQuery } from "hooks/business/client";

import { Tabs, Loader, ErrorViewer, Dropdown } from "components";

import { MedicationViewer } from "containers/Clients/Clients/Medications";

import { capitalize, DateUtils as DU } from "lib/utils/Utils";

import { MEDICATION_STATUSES } from "lib/Constants";

import { ReactComponent as ContributesToFallsIcon } from "images/falling-men.svg";
import { ReactComponent as MedicationAddIcon } from "images/Clients/medication.svg";

import "./ClientMedications.scss";
import ClientMedicationEditor from "../../ClientMedicationEditor/ClientMedicationEditor";
import { ConfirmDialog, SuccessDialog } from "../../../../../../components/dialogs";

const { format, formats } = DU;
import service from "services/ClientMedicationService";
const TIME_FORMAT = formats.time;
const DATE_FORMAT = formats.americanMediumDate;
const DATE_TIME_FORMAT = formats.longDateMediumTime12;

const { ACTIVE, UNKNOWN, INACTIVE } = MEDICATION_STATUSES;

const STATUS_NAMES = [ACTIVE, INACTIVE, UNKNOWN];

function ClientMedications({ status, clientId, getMedicationStatisticsQuery, className, onChangeStatus }) {
  const [clientMedicationList, setClientMedicationList] = useState([]);
  const [isFetching, setIsFetching] = useState(false);
  const [selected, setSelected] = useState(null);
  const [isViewerOpen, toggleViewerOpen] = useState(false);
  const [isErrorViewerOpen, toggleErrorViewer] = useState(false);
  const [isContributesToFalls, setContributesToFalls] = useState(false);
  const [isShowAddMedicationModal, setIsShowAddMedicationModal] = useState(false);
  const [isShowOperationMedicationSuccess, setIsShowOperationMedicationSuccess] = useState(false);
  const [isDelete, setIsDelete] = useState(false);
  const [isEdit, setIsEdit] = useState(false);
  const [deleteItemId, setDeleteItemId] = useState(null);
  const [isConfirmDialogShow, setIsConfirmDialogShow] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const addMedicationForClient = () => {
    setIsShowAddMedicationModal(true);
  };
  const tabs = map(MEDICATION_STATUSES, (value) => ({
    title: value.toLowerCase(),
    isActive: value === status,
  }));

  useEffect(() => {
    getClientMedicationList();
  }, [status, clientId, isContributesToFalls]);

  const getClientMedicationList = () => {
    setIsFetching(true);
    service
      .find({
        clientId,
        includeActive: status === ACTIVE,
        includeInactive: status === INACTIVE,
        includeUnknown: status === UNKNOWN,
        contributesToFalls: isContributesToFalls,
      })
      .then((res) => {
        setIsFetching(false);
        setClientMedicationList(res);
      })
      .catch((e) => {
        toggleErrorViewer(true);
        setErrorMessage(e);
      });
  };

  const options = useMemo(
    () =>
      map(STATUS_NAMES, (name) => ({
        value: name,
        text: capitalize(name),
        isActive: name === status,
        onClick: () => onChangeStatus(name),
      })),
    [onChangeStatus, status],
  );

  const onSelect = (o) => {
    setSelected(o);
    toggleViewerOpen(true);
  };

  const onCloseViewer = useCallback(() => {
    setSelected(null);
    toggleViewerOpen(false);
  }, []);

  const onCloseErrorViewer = useCallback(() => {
    toggleErrorViewer(false);
    setErrorMessage("");
  }, []);

  const onChangeTab = (index) => {
    onChangeStatus(STATUS_NAMES[index]);
  };

  const onChangeContributesToFalls = (name, value) => {
    setContributesToFalls(value);
  };

  const addMedicationSuccess = () => {
    toggleViewerOpen(false);
    setSelected(null);
    setIsShowAddMedicationModal(false);
    setIsShowOperationMedicationSuccess(true);
  };

  const onDeleteMedication = () => {
    service
      .deleteMedicationItem({
        clientId,
        medicationId: deleteItemId,
      })
      .then((res) => {
        toggleViewerOpen(false);
        setIsConfirmDialogShow(false);
        setIsShowOperationMedicationSuccess(true);
      })
      .catch((e) => {
        toggleErrorViewer(true);
        setErrorMessage(e);
      });
  };

  const confirmOperate = () => {
    setIsShowOperationMedicationSuccess(false);
    setIsDelete(false);
    setDeleteItemId(null);
    setIsEdit(false);
    getClientMedicationList();
    getMedicationStatisticsQuery();
  };

  const onEditMedication = () => {
    setIsEdit(true);
    setIsShowAddMedicationModal(true);
  };
  const onConfirmDeleteMedication = () => {
    setIsDelete(true);
    setDeleteItemId(selected?.id);
    setIsConfirmDialogShow(true);
  };
  return (
    <div className={cn("ClientMedications", className)}>
      <div className="Client-Medication-Top-Action">
        <Tabs
          items={tabs}
          onChange={onChangeTab}
          isDisabled={isFetching}
          className="ClientMedications-Tabs"
          containerClassName="ClientMedications-TabsContainer"
        />
        {/*  <div className={"Client-Medication-Add-Btn"} onClick={addMedicationForClient}>
          <MedicationAddIcon />
          <div className="Add-Medication-Text">Add Medication</div>
        </div>*/}
      </div>
      <Dropdown
        value={status}
        items={options}
        toggleText={capitalize(status)}
        className="ClientMedications-Dropdown Dropdown_theme_blue"
      />
      <CheckboxField
        name="isContributeToFalls"
        value={isContributesToFalls}
        label="Show only medications that may contribute to falls"
        className="ClientMedications-CheckboxField"
        onChange={onChangeContributesToFalls}
      />
      {isFetching ? (
        <Loader />
      ) : (
        <>
          <List className="ClientMedicationList">
            {clientMedicationList?.map((o, i) => (
              <ListItem
                key={o.id}
                className="ClientMedicationList-Item ClientMedication"
                style={i % 2 === 0 ? { backgroundColor: "#f9f9f9" } : null}
              >
                <div className="d-flex flex-row align-items-center">
                  {o.potentialAdverseEffects && (
                    <IconButton
                      shouldHighLight={false}
                      Icon={ContributesToFallsIcon}
                      name={`potentialAdverseEffects-${o.id}`}
                      tipText={o.potentialAdverseEffects}
                      className="ClientMedication-ContributesToFallsIcon margin-right-15"
                    />
                  )}
                  <div className="flex-1 margin-right-15">
                    <div className="ClientMedication-Name" onClick={() => onSelect(o)}>
                      {o.name}
                    </div>
                    <div className="ClientMedication-Instruction">
                      <Truncate lines={1}>{o.directions}</Truncate>
                    </div>
                  </div>

                  {o?.webManuallyCreated && (
                    <IconButton
                      shouldHighLight={true}
                      Icon={Remove}
                      onClick={() => {
                        setIsDelete(true);
                        setDeleteItemId(o.id);
                        setIsConfirmDialogShow(true);
                      }}
                      name={`medication-${o.id}`}
                      tipText={"Delete medication"}
                      className="ClientMedication-ContributesToFallsIcon  Delete-Medication-Btn margin-right-15"
                    />
                  )}
                  {(o.startedDate || o.stoppedDate) && (
                    <div>
                      <div className="ClientMedication-Date">
                        {o.startedDate ? format(o.startedDate, DATE_TIME_FORMAT) : "-"}
                      </div>
                      <div className="ClientMedication-Date">
                        {o.stoppedDate ? format(o.stoppedDate, DATE_TIME_FORMAT) : "-"}
                      </div>
                    </div>
                  )}
                </div>
              </ListItem>
            ))}
          </List>

          {clientMedicationList.length === 0 && <div className="ClientMedicationList-Fallback">No medications</div>}
        </>
      )}
      {isViewerOpen && (
        <MedicationViewer
          isOpen={isViewerOpen}
          clientId={clientId}
          medicationId={selected?.id}
          onClose={onCloseViewer}
          onDeleteMedication={onConfirmDeleteMedication}
          onEditMedication={onEditMedication}
          isCanEdit={selected?.webManuallyCreated}
        />
      )}
      {isErrorViewerOpen && <ErrorViewer isOpen error={errorMessage} onClose={onCloseErrorViewer} />}

      {isShowAddMedicationModal && (
        <ClientMedicationEditor
          isOpen={isShowAddMedicationModal}
          clientId={clientId}
          medicationId={selected?.id}
          onClose={() => {
            setIsShowAddMedicationModal(false);
            setIsEdit(false);
          }}
          onSaveSuccess={addMedicationSuccess}
        ></ClientMedicationEditor>
      )}
      {isShowOperationMedicationSuccess && (
        <SuccessDialog
          isOpen
          title={
            isDelete
              ? `The medication has been deleted.`
              : isEdit
                ? `The medication has been edited.`
                : `The medication has been created.`
          }
          buttons={[
            {
              text: "Close",
              onClick: () => confirmOperate(),
            },
          ]}
        />
      )}
      {isConfirmDialogShow && (
        <ConfirmDialog
          isOpen
          icon={Warning}
          confirmBtnText="OK"
          title="The medication will be delete."
          onConfirm={onDeleteMedication}
          onCancel={() => {
            setIsDelete(false);
            setDeleteItemId(null);
            setIsConfirmDialogShow(false);
          }}
        />
      )}
    </div>
  );
}

export default connect()(ClientMedications);
