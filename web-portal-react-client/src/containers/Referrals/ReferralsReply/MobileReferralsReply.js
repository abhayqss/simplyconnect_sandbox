import "./MobilereferralsReply.scss";
import logo from "images/public/medicine/logo.svg";
import medicineImg from "images/public/medicine/medicine.svg";
import timeImg from "images/public/medicine/time.svg";
import okImg from "images/public/medicine/ok.svg";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import service from "services/ReferralService";
import { useParams } from "react-router-dom";
import { Loader } from "../../../components";
import React, { useState } from "react";
import moment from "moment-timezone";

const MobileReferralsReply = () => {
  const { clientId, medicationId, smsId } = useParams();
  const queryClient = useQueryClient();

  const [dialogOpen, setDialogOpen] = useState(false);

  const { data, isLoading } = useQuery({
    queryKey: ["mobileMedication", clientId, medicationId],
    queryFn: async () => {
      const result = await service.findMedicationDetails(clientId, medicationId);
      return result.data;
    },
  });

  const mutation = useMutation({
    mutationFn: (params) => {
      return service.reply(params);
    },
    onSuccess: () => {
      setDialogOpen(true);
      queryClient.invalidateQueries(["mobileMedication", clientId, medicationId]);
    },
  });

  window.addEventListener("resize", setHeight);
  function setHeight() {
    const vh = window.innerHeight * 0.01;
    document.documentElement.style.setProperty("--vh", `${vh}px`);
  }
  setHeight();

  let userTimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;

  return (
    <div className={"mobileReferralsReply"}>
      {isLoading && <Loader hasBackdrop />}
      {!isLoading && (
        <div className="mobileReferralsReplyBox">
          {mutation.isLoading && <Loader hasBackdrop />}

          <img src={logo} alt="" className={"logo"} />
          <img src={medicineImg} alt="" className={"medicineImg"} />

          <div className={"medicineTitle"}>{data?.name}</div>

          <div className="scheduledTime">
            <img src={timeImg} alt="" className="scheduledTimeImg" />
            <div className="scheduledTimeText">
              Scheduled Time:{moment(data?.scheduleDateTime).tz(userTimeZone).format("YYYY-MM-DD hh:mm A z")}
            </div>
          </div>

          <div className="medicinePlaceholder">
            Select "Yes" if you have taken your medication as scheduled, or "No" if you have not.
          </div>

          {data?.status !== 1 && data?.status !== 2 && (
            <>
              <div
                className={`button yesButton`}
                onClick={() => {
                  mutation.mutate({ clientId, medicationId, intake: true, smsId });
                }}
              >
                <span>Yes</span>
              </div>

              <div
                className={`button noButton`}
                onClick={() => {
                  mutation.mutate({ clientId, medicationId, intake: false, smsId });
                }}
              >
                <span>No</span>
              </div>
            </>
          )}

          {data?.status === 1 && (
            <div className={`button yesButton ${data.replyTime ? "yesDisabledButton" : ""} `}>
              <span>Yes</span>
              {data.replyTime && <span>({moment(data.replyTime).format("DD/MM hh:mm")})</span>}
            </div>
          )}

          {data?.status === 2 && (
            <div className={`button noButton ${data.replyTime ? "noDisabledButton" : ""}`}>
              <span>No</span>
              {data.replyTime && <span>({moment(data.replyTime).format("DD/MM hh:mm")})</span>}
            </div>
          )}
        </div>
      )}

      {dialogOpen && (
        <div className="modal">
          <div className="modalContent">
            <img src={okImg} alt="" className={"okImg"} />

            <div className="contentTitle">Thank you for your reply</div>

            <div className="contentText">Your response has been logged in the Simply Connect platform.</div>
          </div>
        </div>
      )}
    </div>
  );
};

export default MobileReferralsReply;
