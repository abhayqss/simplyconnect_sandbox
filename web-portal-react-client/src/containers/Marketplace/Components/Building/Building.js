import "./Building.scss";
import { Link } from "react-router-dom";
import { path } from "../../../../lib/utils/ContextUtils";
import { ReactComponent as Empty } from "images/empty.svg";
import defaultImg from "../../../../images/marketplace/defaultImg.png";
import ReferralRequestEditor from "../../../Referrals/ReferralRequestEditor/ReferralRequestEditor";
import React, { memo, useCallback, useState } from "react";
import { isInteger } from "../../../../lib/utils/Utils";
import PTypes from "prop-types";
import { noop } from "underscore";

import SendInquiryModel from "./sendInquiry/sendInquiryModel";
import { useAuthUser } from "../../../../hooks/common";
import { Button } from "reactstrap";
import { ONLY_VIEW_ROLES, SYSTEM_ROLES } from "../../../../lib/Constants";

const Building = (props) => {
  const { buildingList, clientId, organizationId, onChoose } = props;

  const user = useAuthUser();
  const [isReferralRequestEditorOpen, setIsReferralRequestEditorOpen] = useState(false);
  const [showSendInquiryModel, setShowSendInquiryModel] = useState(false);
  const [buildingId, setBuildingId] = useState();
  const [buildingName, setBuildingName] = useState();
  const [orgName, setOrgName] = useState();
  const isInAssociationSystem = ONLY_VIEW_ROLES.includes(user.roleName);

  const onOpenReferralRequestEditor = useCallback(() => {
    setIsReferralRequestEditorOpen(true);
  }, []);

  const onCloseReferralRequestEditor = useCallback(() => {
    setIsReferralRequestEditorOpen(false);
  }, []);

  const onSaveReferralRequestSuccess = useCallback(() => {
    setIsReferralRequestEditorOpen(false);
  }, []);
  return (
    <div className="BuildingDirectoryWarp">
      {buildingList.length > 0 &&
        buildingList.map((item) => {
          return (
            <div className="BuildingDirectoryBox" key={item.id}>
              <Link to={path(`/marketplace/buildingDetail/${item.id}/${organizationId}`)}>
                <img
                  src={item.logo ? `data:image/png;base64,${item?.logo}` : defaultImg}
                  alt=""
                  className="buildingImg"
                />
              </Link>

              <div className="buildingCenter">
                <div className="buildingName">{item.name || "-"}</div>
                <div className="buildingAddress">
                  {item.displayAddress} <span className="buildingZipCode">{item.zipCode || "-"}</span>
                </div>
                <div className="buildingPhone">{item.phone || "-"}</div>
              </div>

              <div className="buildingButton">
                <Button
                  className="buildingSend"
                  color={"success"}
                  onClick={() => {
                    setShowSendInquiryModel(true);
                    setBuildingId(item.id);
                  }}
                >
                  Send Inquiry
                </Button>

                <Button
                  color={"success"}
                  className="buildingRefer"
                  outline
                  className="buildingRefer"
                  onClick={() => {
                    onOpenReferralRequestEditor();
                    setBuildingId(item.id);
                    setBuildingName(item.name);
                    setOrgName(item.orgname);
                  }}
                >
                  Refer
                </Button>
              </div>
            </div>
          );
        })}
      {buildingList.length === 0 && (
        <div className="empty-box">
          <Empty className="empty-img" />
          <div className="empty-text">No Data</div>
        </div>
      )}

      <ReferralRequestEditor
        isOpen={isReferralRequestEditorOpen}
        isAssociation={isInAssociationSystem}
        communityId={buildingId}
        organizationId={organizationId}
        marketplace={{
          communityId: buildingId,
          organizationId: organizationId,
          organizationName: orgName,
          communityName: buildingName,
        }}
        onClose={onCloseReferralRequestEditor}
        onSaveSuccess={onSaveReferralRequestSuccess}
        successDialog={{
          text: `The request will be displayed in the "Outbound" section located under the "Referrals and Inquires" tab. You can see the details and manage status of the referral request there.`,
          ...(isInteger(clientId) && {
            buttons: {
              submit: {
                text: "Copy to Service plan",
                onClick: () => onChoose(data),
              },
            },
          }),
        }}
      />

      {showSendInquiryModel && (
        <SendInquiryModel
          isOpen={showSendInquiryModel}
          onClose={() => setShowSendInquiryModel(false)}
          buildingId={buildingId}
          user={user}
        />
      )}
    </div>
  );
};

Building.propTypes = {
  clientId: PTypes.number,
  communityId: PTypes.number,
  communityName: PTypes.string,
  onBack: PTypes.func,
  onChoose: PTypes.func,
};

Building.defaultProps = {
  onBack: noop,
  onChoose: noop,
};

export default memo(Building);
