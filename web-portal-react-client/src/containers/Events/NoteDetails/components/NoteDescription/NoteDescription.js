import React, { memo, useMemo, useCallback } from "react";

import cn from "classnames";

import { isNumber } from "underscore";

import { Link } from "react-router-dom";

import { useDirectoryData } from "hooks/common";

import Detail from "components/business/common/Detail/Detail";
import ScrollTop from "components/ScrollTop/ScrollTop";

import calcTotalTimeRangeUnits from "../../../calcTotalTimeRangeUnits";

import { isEmpty, DateUtils as DU } from "lib/utils/Utils";

import { path } from "lib/utils/ContextUtils";

import "./NoteDescription.scss";

const { format, formats } = DU;
const TIME_FORMAT = formats.time;
const DATE_FORMAT = formats.americanMediumDate;
const DATE_AND_TIME_FORMAT = formats.longDateMediumTime12;

const NOTE_STATUS_COLORS = {
  UPDATED: "#bbdefb",
  CREATED: "#d5f3b8",
};

const getFullResourceName = (o) => [o.providerName, o.resourceName].filter((v) => v).join(", ");

function ClientLink({ id, name, hasComma, isDisabled }) {
  const title = name + (hasComma ? "," : "");

  return isDisabled ? (
    <span className="NoteDescription-Link_Disabled">{title}</span>
  ) : (
    <Link to={path(`/clients/${id}`)} className="NoteDescription-Link">
      {title}
    </Link>
  );
}

function NoteDescription({ data, clientId, onPickEvent }) {
  const eventId = data?.eventId;
  const subTypeId = data?.subTypeId;

  const { units, totalTime, range } = useMemo(
    () => calcTotalTimeRangeUnits(data?.encounter?.fromDate, data?.encounter?.toDate),
    [data],
  );

  const { noteTypes } = useDirectoryData({ noteTypes: ["note", "type"] });
  const selectedNoteType = useMemo(() => noteTypes.find((type) => type.id === subTypeId), [noteTypes, subTypeId]);

  const onClickEvent = useCallback(() => onPickEvent(eventId), [eventId, onPickEvent]);

  return isEmpty(data) ? (
    <div className="EventDetails-NoDataText">No Details</div>
  ) : (
    <div className="overflow-auto">
      <div className="NoteDetails-Section NoteDescription">
        <div className="NoteDescription-Title">
          <div className="NoteDescription-TitleText">Summary</div>
        </div>

        <Detail title="Clients" className="Detail-Clients">
          {data.clients?.map((client, i, array) => {
            const needComma = array.length > 1 && i < array.length - 1;

            return (
              <ClientLink
                id={client.id}
                key={client.id}
                name={client.name}
                hasComma={needComma}
                isDisabled={client.id === clientId || !client.canView}
              />
            );
          })}
        </Detail>

        <Detail title="Client">
          {isNumber(data.clientId) && (
            <ClientLink
              id={data.clientId}
              name={data.clientName}
              isDisabled={data.clientId === clientId || !data.canViewClient}
            />
          )}
        </Detail>

        <Detail title="Type">{data.typeTitle}</Detail>

        <Detail title="Subtype">{selectedNoteType?.title}</Detail>

        <Detail title="Note name">{data.noteName}</Detail>

        <Detail title="Admit / Intake Date">{format(data.admitDate, DATE_AND_TIME_FORMAT)}</Detail>

        {data.eventId && (
          <Detail title="Event">
            <span onClick={onClickEvent} className="NoteDescription-Link margin-right-8">
              {data.eventTypeTitle}
            </span>
            {format(data.eventDate, DATE_AND_TIME_FORMAT)}
          </Detail>
        )}

        <Detail title="Status">
          <span className="NoteDescription-Subtype" style={{ backgroundColor: NOTE_STATUS_COLORS[data.statusName] }}>
            {data.statusTitle}
          </span>
        </Detail>

        <Detail title="Note date and time">{format(data.noteDate, DATE_AND_TIME_FORMAT)}</Detail>

        <Detail title="Last modified date">{format(data.lastModified, DATE_AND_TIME_FORMAT)}</Detail>

        <Detail title="Person submitting note">{data.author}</Detail>

        <Detail title="Role">{data.authorRoleTitle}</Detail>
      </div>

      {data.clientProgram && (
        <div className="NoteDetails-Section NoteDescription">
          <div className="NoteDescription-Title">
            <div className="NoteDescription-TitleText">Client Program</div>
          </div>

          <Detail title="Program sub type">{data.clientProgram.typeTitle}</Detail>

          <Detail title="Service Provider for Program">{data.clientProgram.serviceProvider}</Detail>

          <Detail title="Start Date of Program">{format(data.clientProgram.startDate, DATE_FORMAT)}</Detail>

          <Detail title="End Date of Program">{format(data.clientProgram.endDate, DATE_FORMAT)}</Detail>
        </div>
      )}

      {data.encounter && (
        <div className="NoteDetails-Section NoteDescription">
          <div className="NoteDescription-Title">
            <div className="NoteDescription-TitleText">Encounter</div>
          </div>

          <Detail title="Person Completing the Encounter">
            {data.encounter.clinicianTitle || data.encounter.otherClinician}
          </Detail>

          <Detail title="Encounter type">{data.encounter.typeTitle}</Detail>

          <Detail title="Encounter date from">{format(data.encounter.fromDate, DATE_FORMAT)}</Detail>

          <Detail title="Time from">{format(data.encounter.fromDate, TIME_FORMAT)}</Detail>

          <Detail title="Encounter date to">{format(data.encounter.toDate, DATE_FORMAT)}</Detail>

          <Detail title="Time to">{format(data.encounter.toDate, TIME_FORMAT)}</Detail>

          <Detail title="Total time spent">{totalTime}</Detail>

          <Detail title="Range">{range}</Detail>

          <Detail title="Units">{units}</Detail>
        </div>
      )}

      <div className="NoteDetails-Section NoteDescription">
        <div className="NoteDescription-Title">
          <div className="NoteDescription-TitleText">Description</div>
        </div>

        {data.serviceStatusCheck && (
          <>
            <Detail title="Service plan">
              <Link
                to={{
                  pathname: path(`/clients/${clientId}/service-plans`),
                  state: { servicePlanId: data.serviceStatusCheck.servicePlanId },
                }}
                className={cn("NoteDescription-Link", {
                  "NoteDescription-Link_Disabled": !data.serviceStatusCheck.canViewServicePlan,
                })}
              >
                {format(data.serviceStatusCheck.servicePlanCreatedDate, DATE_AND_TIME_FORMAT)}
              </Link>
            </Detail>
            <Detail title="Resource name">{getFullResourceName(data.serviceStatusCheck)}</Detail>
            <Detail title="Person who did the audit / check">{data.serviceStatusCheck.auditPerson}</Detail>
            <Detail title="Date of check">{format(data.serviceStatusCheck.checkDate, DATE_AND_TIME_FORMAT)}</Detail>
            <Detail title="Next date of check">
              {format(data.serviceStatusCheck.nextCheckDate, DATE_AND_TIME_FORMAT)}
            </Detail>
            <Detail title="Is the service being provided?">
              {data.serviceStatusCheck.serviceProvided ? "Yes" : "No"}
            </Detail>
          </>
        )}

        <Detail title="Subjective">{data.subjective}</Detail>

        <Detail title="Objective">{data.objective}</Detail>

        <Detail title="Assessment">{data.assessment}</Detail>

        <Detail title="Plan">{data.plan}</Detail>
      </div>

      <ScrollTop scrollable=".App-Content, .SideBar-Content" scrollTopBtnClass="NoteDescription-ScrollTopBtn" />
    </div>
  );
}

export default memo(NoteDescription);
