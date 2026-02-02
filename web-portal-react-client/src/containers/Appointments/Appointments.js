import React, { memo, useCallback, useMemo, useState } from "react";

import { useHistory } from "react-router-dom";

import cn from "classnames";

import { saveAs } from "file-saver";

import { Badge, Button, Collapse } from "reactstrap";

import DocumentTitle from "react-document-title";

import { Calendar, ErrorViewer, Footer } from "components";

import { SuccessDialog } from "components/dialogs";

import { AppointmentDetails, AppointmentSummary } from "components/business/Appointments/Calendar";

import { useDownloadingStatusInfoToast, useMemoEffect, useQueryInvalidation, useQueryParams } from "hooks/common";

import { useAuthUser } from "hooks/common/redux";

import {
  useAppointmentExport,
  useAppointmentFilterCombination,
  useAppointmentListState,
  useAppointmentsQuery,
  useAppointmentUnarchivedIdQuery,
  useCanAddAppointmentsQuery,
  useReducedAppointmentFilterData,
} from "hooks/business/appointments";

import { allAreInteger, defer, isInteger } from "lib/utils/Utils";

import { map } from "lib/utils/ArrayUtils";

import {
  getEndOfDayTime,
  getStartOfDayTime,
  getTodayEndOfDayTime,
  getTodayStartOfDayTime,
  isPastDate,
} from "lib/utils/DateUtils";

import { CONTACT_STATUSES, SYSTEM_ROLES } from "lib/Constants";

import { VIEW_MODE } from "components/Calendar/Constants";

import { ReactComponent as Filter } from "images/filters.svg";

import AppointmentViewer from "./AppointmentViewer/AppointmentViewer";
import AppointmentFilter from "./AppointmentFilter/AppointmentFilter";
import AppointmentEditor from "./AppointmentEditor/AppointmentEditor";
import AppointmentCancelEditor from "./AppointmentCancelEditor/AppointmentCancelEditor";
import AppointmentPrimaryFilter from "./AppointmentPrimaryFilter/AppointmentPrimaryFilter";

import { mapAppointmentToCalendarEvent } from "./lib/utils/DataMappingUtils";

import "./Appointments.scss";

const { ACTIVE, INACTIVE } = CONTACT_STATUSES;

const { SUPER_ADMINISTRATOR } = SYSTEM_ROLES;

function Appointments() {
  const history = useHistory();

  const { appointmentChainId } = useQueryParams();

  const user = useAuthUser();

  const [selected, setSelected] = useState(null);
  const [calendarResetCount, setCalendarResetCount] = useState(0);
  const [isDuplicating, setDuplicating] = useState(false);

  const [isFilterOpen, toggleFilter] = useState(true);
  const [isEditorOpen, toggleEditor] = useState(false);
  const [isViewerOpen, toggleViewer] = useState(false);
  const [isCancelEditorOpen, toggleCancelEditor] = useState(false);
  const [isSaveSuccessDialogOpen, toggleSaveSuccessDialog] = useState(false);

  const [dateFrom, setDateFrom] = useState(getTodayStartOfDayTime());
  const [dateTo, setDateTo] = useState(getTodayEndOfDayTime());

  const { state, setError, clearError, changeFilter } = useAppointmentListState();

  const withDownloadingStatusInfoToast = useDownloadingStatusInfoToast();

  const { error } = state;

  const filterData = useReducedAppointmentFilterData(state.filter.toJS());

  useAppointmentUnarchivedIdQuery(
    {
      appointmentChainId,
    },
    {
      enabled: isInteger(appointmentChainId),
      onSuccess: (id) => {
        setSelected({ id });
        toggleViewer(true);

        history.replace();
      },
      staleTime: 0,
    },
  );

  const {
    fetch,
    refresh,
    isFetching,
    pagination,
    data: { data = [] } = {},
  } = useAppointmentsQuery(
    {
      dateTo,
      dateFrom,
      ...filterData,
    },
    {
      onError: setError,
      staleTime: 0,
    },
  );

  const { primary, custom } = useAppointmentFilterCombination(
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

  const invalidateQuery = useQueryInvalidation();

  const { isLoading: isExporting, mutateAsync: exportFiltered } = useAppointmentExport({
    onError: setError,
  });

  const { data: canAdd } = useCanAddAppointmentsQuery(
    {
      organizationId: filterData.organizationId,
    },
    {
      staleTime: 0,
      enabled: isInteger(filterData.organizationId),
    },
  );

  const onExport = useCallback(() => {
    withDownloadingStatusInfoToast(() =>
      exportFiltered({ ...filterData, dateFrom, dateTo }).then(({ data, name }) => {
        saveAs(data, name);
      }),
    );
  }, [dateTo, dateFrom, filterData, exportFiltered, withDownloadingStatusInfoToast]);

  const mappedAppointments = useMemo(() => map(data, mapAppointmentToCalendarEvent), [data]);

  const onToggleFilter = useCallback(() => {
    toggleFilter(!isFilterOpen);
  }, [isFilterOpen]);

  const onChangeDate = useCallback(
    (date) => {
      setDateTo(getEndOfDayTime(date));
      setDateFrom(getStartOfDayTime(date));
      defer().then(refresh);
    },
    [refresh],
  );

  const onChangeDateRange = useCallback(
    ([from, to]) => {
      setDateTo(to.getTime());
      setDateFrom(from.getTime());
      defer().then(refresh);
    },
    [refresh],
  );

  const onEdit = useCallback((o) => {
    setSelected(o);
    toggleViewer(false);
    toggleEditor(true);
  }, []);

  const onCancel = useCallback((o) => {
    setSelected(o);
    toggleViewer(false);
    toggleCancelEditor(true);
  }, []);

  const onCloseCancelEditor = useCallback((id) => {
    setSelected(null);
    toggleCancelEditor(false);
  }, []);

  const onCancelSuccess = useCallback(() => {
    toggleCancelEditor(false);
    toggleViewer(false);
    setSelected(null);
    refresh();
  }, [refresh]);

  const onSaveSuccess = useCallback(() => {
    toggleSaveSuccessDialog(true);

    toggleEditor(false);

    Promise.all([
      //creators
      invalidateQuery("AppointmentContacts", {
        statuses: [ACTIVE, INACTIVE],
        withAccessibleCreatedAppointments: true,
        organizationId: user?.roleName !== SUPER_ADMINISTRATOR ? user?.organizationId : filterData.organizationId,
      }),
      //external + no service providers
      invalidateQuery("Appointment.Participation", {
        organizationId: filterData.organizationId,
      }),
      //service providers
      invalidateQuery("AppointmentContacts", {
        statuses: [ACTIVE, INACTIVE],
        withAccessibleScheduledAppointments: true,
        organizationId: user?.roleName !== SUPER_ADMINISTRATOR ? user?.organizationId : filterData.organizationId,
      }),
      //clients
      invalidateQuery("Directory.Clients", {
        recordStatuses: filterData.clientStatuses,
        communityIds: filterData.communityIds,
        organizationId: filterData.organizationId,
        withAccessibleAppointments: filterData.clientsWithAccessibleAppointments,
      }),
    ]).then(() => refresh());
  }, [user, refresh, filterData, toggleEditor, invalidateQuery, toggleSaveSuccessDialog]);

  const onCloseSuccessDialog = useCallback(() => {
    setSelected(null);
    setDuplicating(false);

    toggleSaveSuccessDialog(false);
  }, [setSelected, setDuplicating, toggleSaveSuccessDialog]);

  const onAdd = useCallback((date) => {
    if (!isPastDate(date)) {
      setSelected({ date: date.getTime() });
      toggleEditor(true);
    }
  }, []);

  const onView = useCallback((o) => {
    setSelected(o);
    toggleViewer(true);
  }, []);

  const onCloseViewer = useCallback(() => {
    setSelected(null);
    toggleViewer(false);
  }, []);

  const onCloseEditor = useCallback(() => {
    setSelected(null);
    toggleEditor(false);
    setDuplicating(false);
  }, []);

  const onDuplicate = useCallback((o) => {
    setSelected(o);
    toggleViewer(false);
    toggleEditor(true);
    setDuplicating(true);
  }, []);

  useMemoEffect(
    (memo) => {
      const prev = memo();
      const { organizationId } = filterData;

      if (organizationId !== prev && allAreInteger(organizationId, prev)) {
        setCalendarResetCount((v) => v + 1);
        setDateFrom(getTodayStartOfDayTime());
        setDateTo(getTodayEndOfDayTime());
      }

      memo(organizationId);
    },
    [filterData],
  );

  return (
    <DocumentTitle title="Simply Connect | Appointments">
      <>
        <div className="Appointments">
          <AppointmentPrimaryFilter {...primary} className="margin-bottom-30" />
          <div className="Appointments-Header">
            <div className="Appointments-HeaderItem">
              <div className="Appointments-Title">
                <div className="Appointments-TitleText">Appointments</div>
                {pagination.totalCount > 0 ? (
                  <Badge color="info" className="Badge Badge_place_top-right">
                    {pagination.totalCount}
                  </Badge>
                ) : null}
              </div>
            </div>
            <div className="Appointments-HeaderItem">
              <div className="Appointments-Actions">
                <Filter
                  className={cn(
                    "AppointmentFilter-Icon",
                    "Appointments-Action",
                    isFilterOpen ? "AppointmentFilter-Icon_rotated_90" : "AppointmentFilter-Icon_rotated_0",
                  )}
                  onClick={onToggleFilter}
                />
                <Button
                  outline
                  color="success"
                  onClick={onExport}
                  // disabled={!user?.hieAgreement}
                  className="margin-right-24"
                >
                  Export
                </Button>
                {canAdd && (
                  <>
                    <Button
                      color="success"
                      onClick={() => toggleEditor(true)}
                      id="add-appointment-btn"
                      className="AddAppointmentBtn"
                    >
                      Create Appointment
                    </Button>
                  </>
                )}
              </div>
            </div>
          </div>
          <Collapse isOpen={isFilterOpen}>
            <AppointmentFilter
              {...custom}
              organizationId={filterData.organizationId}
              communityIds={filterData.communityIds}
              className="margin-bottom-40"
            />
          </Collapse>

          <Calendar
            key={`calendar${calendarResetCount}`}
            isLoading={isFetching || isExporting}
            events={mappedAppointments}
            onAddEvent={onAdd}
            onDoublePickEvent={onView}
            defaultViewMode={VIEW_MODE.TODAY}
            defaultDateRange={dateFrom && [dateFrom, dateTo]}
            renderEventSummary={(e) => <AppointmentSummary data={e} onView={onView} />}
            renderEventDescription={(e) => (
              <AppointmentDetails
                data={e}
                onView={onView}
                onEdit={onEdit}
                onCancel={onCancel}
                onDuplicate={onDuplicate}
                className="max-height-550"
              />
            )}
            onChangeDate={onChangeDate}
            onChangeDateRange={onChangeDateRange}
            className="margin-bottom-30"
          />

          <AppointmentViewer
            isOpen={isViewerOpen}
            appointmentId={selected?.id}
            onEdit={onEdit}
            onCancel={onCancel}
            onClose={onCloseViewer}
            onDuplicate={onDuplicate}
          />

          <AppointmentEditor
            isOpen={isEditorOpen}
            appointmentId={selected?.id}
            appointmentDate={selected?.date}
            organizationId={filterData.organizationId}
            isDuplicating={isDuplicating}
            onClose={onCloseEditor}
            onSaveSuccess={onSaveSuccess}
          />

          <AppointmentCancelEditor
            isOpen={isCancelEditorOpen}
            appointmentId={selected?.id}
            onClose={onCloseCancelEditor}
            onCancelSuccess={onCancelSuccess}
          />

          {error && <ErrorViewer isOpen error={error} onClose={clearError} />}

          {isSaveSuccessDialogOpen && (
            <SuccessDialog
              isOpen
              title={`The appointment has been ${selected?.id && !isDuplicating ? "updated" : "created"}`}
              buttons={[
                {
                  text: "Close",
                  onClick: onCloseSuccessDialog,
                },
              ]}
            />
          )}
        </div>
        <Footer theme="gray" hasLogo={false} className="Appointments-Footer" />
      </>
    </DocumentTitle>
  );
}

export default memo(Appointments);
