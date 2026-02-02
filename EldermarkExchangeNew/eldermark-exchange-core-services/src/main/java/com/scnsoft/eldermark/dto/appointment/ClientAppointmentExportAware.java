package com.scnsoft.eldermark.dto.appointment;

import com.scnsoft.eldermark.beans.projection.ClientCommunityIdNameAware;
import com.scnsoft.eldermark.beans.projection.ClientIdNamesAware;
import com.scnsoft.eldermark.beans.projection.ClientOrganizationIdNameAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentNotificationMethod;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentReminder;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentServiceCategory;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentStatus;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentType;

import java.time.Instant;
import java.util.Set;

public interface ClientAppointmentExportAware extends ClientIdNamesAware, ClientCommunityIdNameAware, ClientOrganizationIdNameAware, IdAware {
    Instant getDateFrom();
    Instant getDateTo();
    ClientAppointmentStatus getStatus();
    String getCreatorFirstName();
    String getCreatorLastName();
    Set<Long> getServiceProviderIds();
    String getTitle();
    String getLocation();
    ClientAppointmentType getType();
    ClientAppointmentServiceCategory getServiceCategory();
    String getReferralSource();
    String getReasonForVisit();
    String getDirectionsInstructions();
    String getNotes();
    Set<ClientAppointmentReminder> getReminders();
    Set<ClientAppointmentNotificationMethod> getNotificationMethods();
    String getPhone();
    String getEmail();
    Boolean getIsExternalProviderServiceProvider();
    Boolean getClientActive();
}
