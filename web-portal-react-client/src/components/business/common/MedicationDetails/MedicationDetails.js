import React from "react";

import cn from "classnames";

import { Detail as BaseDetail } from "components/business/common";

import { isNotEmpty, allAreEmpty, DateUtils as DU } from "lib/utils/Utils";

import { MEDICATION_STATUS_COLORS } from "lib/Constants";

import "./MedicationDetails.scss";

const { format } = DU;

const formatDate = (date) => format(date, DU.formats.americanMediumDate);
const formatTime = (date) => format(date, DU.formats.time);
const formatDateTime = (date) => format(date, DU.formats.longDateMediumTime12);

function hasPrescribedBy(prescribedBy) {
  return !allAreEmpty(...Object.values(prescribedBy || {}));
}

function hasPharmacy(details) {
  return !allAreEmpty(details.pharmacyCode, details.pharmacyName, details.pharmacyPhone);
}

function hasDispensingPharmacy(details) {
  return !allAreEmpty(details.dispensingPharmacyCode, details.dispensingPharmacyName, details.dispensingPharmacyPhone);
}

function SubDetail({ title, children }) {
  return (
    isNotEmpty(children) && (
      <div className="MedicationSubDetail">
        <span className="MedicationSubDetail-Title">{title}</span>
        <span className="MedicationSubDetail-Value">{children}</span>
      </div>
    )
  );
}

function Detail({ title, children, titleClassName, valueClassName }) {
  return (
    <BaseDetail
      title={title}
      titleClassName={cn("MedicationDetail-Title", titleClassName)}
      valueClassName={cn("MedicationDetail-Value", valueClassName)}
      className="MedicationDetail"
    >
      {children}
    </BaseDetail>
  );
}

export default function MedicationDetails({ data = {} }) {
  const prescribedBy = data.prescribedBy;
  const isCanEdit = data.webManuallyCreated;

  return (
    isNotEmpty(data) && (
      <>
        <Detail title="Medication name/strength">{data.name}</Detail>

        <Detail title="NDC">{data.ndc}</Detail>

        <Detail title="Started">{formatDateTime(data.startedDate)}</Detail>

        {data.stoppedDate && (
          <Detail title="Stopped" valueClassName="flex-column">
            <SubDetail title="Prescription end date">{formatDate(data.stoppedDate)}</SubDetail>
            <SubDetail title="Prescription end time">{formatTime(data.stoppedDate)}</SubDetail>
          </Detail>
        )}

        <Detail title="Status">
          <div
            className="MedicationDetail-Status"
            style={{ backgroundColor: MEDICATION_STATUS_COLORS[data.statusName] }}
          >
            {data.statusTitle}
          </div>
        </Detail>

        <Detail title="Refill date">{formatDateTime(data.refillDate)}</Detail>

        <Detail title="Dosage quantity">{data.dosageQuantity}</Detail>

        <Detail title="Frequency">{data.frequency}</Detail>

        <Detail title="Recurrence">{data.recurrence}</Detail>

        <Detail title="Directions">{data.directions}</Detail>

        <Detail title="Indicated for">{data.indicatedFor}</Detail>

        {hasPrescribedBy(prescribedBy) && (
          <Detail title="Prescribed by" valueClassName="d-flex flex-column">
            <SubDetail title="Code">{prescribedBy.code}</SubDetail>
            <SubDetail title="Name">
              {prescribedBy.firstName} {prescribedBy.lastName}
            </SubDetail>
            <SubDetail title="Speciality">{prescribedBy.speciality}</SubDetail>
            <SubDetail title="Work phone">{prescribedBy.workPhone}</SubDetail>
            <SubDetail title="Email">{prescribedBy.email}</SubDetail>
            <SubDetail title="Organization">{prescribedBy.organizationName}</SubDetail>
            <SubDetail title="Community">{prescribedBy.communityName}</SubDetail>
            <SubDetail title="Address">{prescribedBy.address}</SubDetail>
            <SubDetail title="Ext Pharmacy ID">{prescribedBy.extPharmacyId}</SubDetail>
            <SubDetail title="NPI">{prescribedBy.npi}</SubDetail>
            <SubDetail title="Date prescribed">{formatDate(data.prescribedDate)}</SubDetail>
            <SubDetail title="Prescription quantity">{data.prescriptionQuantity}</SubDetail>
            <SubDetail title="Prescription expiration date">{formatDate(data.prescriptionExpirationDate)}</SubDetail>
          </Detail>
        )}

        {hasDispensingPharmacy(data) && (
          <Detail title="Dispensing pharmacy" valueClassName="d-flex flex-column">
            <SubDetail title="Code">{data.dispensingPharmacyCode}</SubDetail>
            <SubDetail title="Name">{data.dispensingPharmacyName}</SubDetail>
            <SubDetail title="Phone">{data.dispensingPharmacyPhone}</SubDetail>
          </Detail>
        )}

        {hasPharmacy(data) && (
          <Detail title="Pharmacy" valueClassName="d-flex flex-column">
            <SubDetail title="Code">{data.pharmacyCode}</SubDetail>
            <SubDetail title="Name">{data.pharmacyName}</SubDetail>
            <SubDetail title="Phone">{data.pharmacyPhone}</SubDetail>
          </Detail>
        )}

        <Detail title="Pharm RXID">{data.pharmRxid}</Detail>

        <Detail title="Pharmacy origin date">{formatDateTime(data.pharmacyOriginDate)}</Detail>

        <Detail title="End date future">{formatDateTime(data.endDateFuture)}</Detail>

        <Detail title="Stop delivery after date">{formatDate(data.stopDeliveryAfterDate)}</Detail>

        <Detail title="Last update">{data.lastUpdate}</Detail>

        <Detail title="Origin">{data.origin}</Detail>

        <Detail title="Date recorded">{formatDate(data.recordedDate)}</Detail>

        <Detail title="Recorded by">{data.recordedByName}</Detail>

        <Detail title="Date modified">{formatDate(data.editedDate)}</Detail>

        <Detail title="Modified by">{data.editedByName}</Detail>

        <Detail title="Comment">{data.comment}</Detail>

        <Detail title="Data Source" valueClassName="d-flex flex-column">
          <SubDetail title="Organization name">{data.organizationName}</SubDetail>
          <SubDetail title="Community name">{data.communityName}</SubDetail>
        </Detail>
      </>
    )
  );
}
