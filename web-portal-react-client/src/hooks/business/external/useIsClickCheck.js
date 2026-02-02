import { SYSTEM_ROLES } from "lib/Constants";
import authUserStore from "lib/stores/AuthUserStore";

const { PERSON_RECEIVING_SERVICES, CLIENT_POA } = SYSTEM_ROLES;

export default function useIsClickCheck() {
  const user = authUserStore.get();
  return user?.roleName === PERSON_RECEIVING_SERVICES || user?.roleName === CLIENT_POA;
}
