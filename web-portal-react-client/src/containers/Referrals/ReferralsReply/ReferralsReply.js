import { Link, useParams } from "react-router-dom";
import "./ReferralsReply.scss";
import scLogo from "images/simplyconnect-logo.svg";
import { path } from "lib/utils/ContextUtils";
import replyImg from "images/public/referrals.png";

import service from "services/ReferralService";
import React, { useEffect, useState } from "react";
import { Loader } from "../../../components";
import NavigationBar from "../../NavigationBar/NavigationBar";
import { ConfirmDialog } from "../../../components/dialogs";

const ReferralsReply = () => {
  const { clientId, medicationId, intake, smsId } = useParams();

  const user = JSON.parse(localStorage.getItem("AUTHENTICATED_USER") || "{}");

  const [data, setData] = useState(undefined);
  const [isLoading, setLoading] = useState(false);
  const [showConfirmDialog, setShowConfirmDialog] = useState(false);

  useEffect(() => {
    setData(null);
    setLoading(true);
    const params = {
      clientId,
      medicationId,
      intake,
      smsId,
    };

    if (clientId) {
      service
        .reply(params)
        .then((res) => {
          setLoading(false);
          setData(res.data);
        })
        .catch(() => {
          setData(undefined);
          setLoading(false);
          setShowConfirmDialog(true);
        });
    }
  }, [clientId, medicationId, intake, smsId]);

  return (
    <div className="replyBox">
      {!user.id ? (
        <div className="replyHeader">
          <img src={scLogo} alt="" />

          <Link to={path("/home")}>
            <div className="replySignIn">Sign In</div>
          </Link>
        </div>
      ) : (
        <NavigationBar />
      )}

      {data !== undefined && (
        <div className="replyContent">
          {isLoading && <Loader hasBackdrop />}

          <div className="replyContentBox">
            <img src={replyImg} alt="" />

            <div className="title">{data ? "Thank you for your reply" : "Reply Already Sent"}</div>

            <div className="secTitle">
              {data
                ? "Your response has been logged in the Simply Connect platform."
                : "You have already responded to this email, and the information has been successfully recorded in the Simply Connect platform."}
            </div>
          </div>
        </div>
      )}

      {showConfirmDialog && (
        <ConfirmDialog
          isOpen
          title="Request exception."
          onConfirm={() => {
            setShowConfirmDialog(false);
          }}
          onCancel={() => setShowConfirmDialog(false)}
        />
      )}
    </div>
  );
};

export default ReferralsReply;
