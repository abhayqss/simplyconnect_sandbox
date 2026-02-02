import { useMutation } from "@tanstack/react-query";

import service from "services/ReferralService";

function submit(data) {
  return service.saveVendor(data);
}

function useVendorReferralRequestSubmit(options) {
  return useMutation(submit, options);
}

export default useVendorReferralRequestSubmit;
