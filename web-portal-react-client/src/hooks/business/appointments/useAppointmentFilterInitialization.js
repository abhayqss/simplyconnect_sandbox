import { useEffect } from "react";

import { useQueryWatch } from "hooks/common";

import { useAuthUser } from "hooks/common/redux";

import { APPOINTMENT_STATUSES, CLIENT_STATUSES, CONTACT_STATUSES, SYSTEM_ROLES } from "lib/Constants";

import { first, isUnary, map } from "lib/utils/ArrayUtils";

const {
  SUPER_ADMINISTRATOR,
  ADMINISTRATOR,
  ORGANIZATION_ADMIN,
  BEHAVIORAL_HEALTH,
  QUALITY_ASSURANCE,
  COMMUNITY_ADMINISTRATOR,
  PARENT_GUARDIAN,
  PERSON_RECEIVING_SERVICES,
} = SYSTEM_ROLES;

const { PLANNED, TRIAGED, COMPLETED, RESCHEDULED } = APPOINTMENT_STATUSES;

const { ACTIVE } = CLIENT_STATUSES;
const { INACTIVE } = CONTACT_STATUSES;

export default function useAppointmentFilterInitialization({
  isSaved,
  organizationId,
  changeFields,
  updateDefaultData,
} = {}) {
  const user = useAuthUser();

  useQueryWatch({
    queryKey: [
      "AppointmentContacts",
      {
        statuses: [ACTIVE, INACTIVE],
        organizationId: user?.roleName !== SUPER_ADMINISTRATOR ? user?.organizationId : organizationId,
        withAccessibleCreatedAppointments: true,
      },
    ],
    enabled: !!organizationId,
    onSuccess: (data = []) => {
      if (isUnary(data)) {
        const changes = { creatorIds: [first(data).id] };

        updateDefaultData(changes);

        if (!isSaved()) {
          changeFields(changes);
        }
      }
    },
  });

  useQueryWatch({
    queryKey: [
      "AppointmentContacts",
      {
        statuses: [ACTIVE, INACTIVE],
        organizationId: user?.roleName !== SUPER_ADMINISTRATOR ? user?.organizationId : organizationId,
        withAccessibleScheduledAppointments: true,
      },
    ],
    enabled: !!organizationId,
    onSuccess: (data = []) => {
      if (isUnary(data)) {
        const changes = { serviceProviderIds: [first(data).id] };

        updateDefaultData(changes);

        if (!isSaved()) {
          changeFields(changes);
        }
      }
    },
  });

  useQueryWatch({
    queryKey: [
      "Directory.AppointmentTypes",
      {
        organizationId,
      },
    ],
    enabled: !!organizationId,
    onSuccess: (data) => {
      const changes = { types: map(data, (o) => o.name) };

      updateDefaultData(changes);

      if (!isSaved()) {
        changeFields(changes);
      }
    },
  });

  useEffect(() => {
    if (
      user &&
      ![
        SUPER_ADMINISTRATOR,
        ADMINISTRATOR,
        ORGANIZATION_ADMIN,
        QUALITY_ASSURANCE,
        COMMUNITY_ADMINISTRATOR,
        PARENT_GUARDIAN,
        PERSON_RECEIVING_SERVICES,
      ].includes(user?.roleName)
    ) {
      const changes = {
        creatorIds: [user.id],
        serviceProviderIds: [user.id],
      };

      updateDefaultData(changes);

      if (!isSaved()) {
        changeFields(changes);
      }
    }
  }, [user, isSaved, changeFields, updateDefaultData]);

  useEffect(() => {
    const changes = {
      clientStatuses: [ACTIVE],
      statuses: [PLANNED, TRIAGED, COMPLETED, RESCHEDULED],
    };

    updateDefaultData(changes);

    if (!isSaved()) {
      changeFields(changes);
    }
  }, [isSaved, changeFields, updateDefaultData]);
}
