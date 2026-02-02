import { useMutation } from "@tanstack/react-query";
import service from "services/CareTeamMemberService";

function fetch({ id }) {
  return service.deleteByIdForNonclinical(id);
}

export default function useCareTeamNonclinicalMemberDeletion(options) {
  return useMutation(fetch, options);
}
