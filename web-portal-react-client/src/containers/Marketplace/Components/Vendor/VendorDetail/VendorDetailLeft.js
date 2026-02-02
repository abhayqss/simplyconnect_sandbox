import config from "../../../../../config";
import SuccessImg from "../../../../../images/marketplace/successClickImg.svg";
import defaultImg from "images/marketplace/defaultImg.png";
import { useDispatch, useSelector } from "react-redux";
import React, { useCallback, useEffect, useState } from "react";
import { Button } from "reactstrap";
import { ONLY_VIEW_ROLES, VENDOR_ROLES } from "../../../../../lib/Constants";
import { useAuthUser } from "hooks/common";
import { APIProvider, Map } from "@vis.gl/react-google-maps";
import { clearVendorDetail } from "../../../../../redux/marketplace/Vendor/VendorActions";
import { MarkerWithInfoWindow } from "../../../../../components/Map/MarkerWithInfoWindow";
import { Loader } from "../../../../../components";
import defaultLogo from "../../../../../images/public/vendorSmallLogo.svg";
import defaultPic from "../../../../../images/public/vendorDefault.svg";
import { Detail } from "../../../../../components/business/common";

const VendorDetailLeft = (props) => {
  const { setVendorReferModalOpen } = props;

  const [cameraProps, setCameraProps] = useState();
  const handleCameraChange = useCallback((ev) => setCameraProps(ev.detail));
  const detailData = useSelector((state) => state.vendor.vendorDetail);

  const user = useAuthUser();
  const [vendorReferDisabled, setVendorReferDisabled] = useState(false);

  const dispatch = useDispatch();

  useEffect(() => {
    if (VENDOR_ROLES.includes(user.roleName) || ONLY_VIEW_ROLES.includes(user.roleName)) {
      setVendorReferDisabled(true);
    } else {
      setVendorReferDisabled(false);
    }
  }, [user]);

  useEffect(() => {
    if (detailData?.location?.latitude !== null && detailData?.location !== undefined) {
      setCameraProps({
        center: { lat: detailData?.location?.latitude, lng: detailData?.location?.longitude },
      });

      return;
    }
    setCameraProps({
      center: { lat: 38.9072, lng: -77.0369 },
    });
  }, [detailData]);

  useEffect(() => {
    return () => {
      dispatch(clearVendorDetail());
    };
  }, []);
  return (
    <>
      {!detailData?.name && <Loader isCentered hasBackdrop />}

      <div className="vendorDetailLeftBox">
        <div className="vendorDetailInfo">
          <div className="vendorDetailInfoHeader">
            <div className="vendorDetailInfoHeaderLeft">
              <img src={detailData?.logo ? `data:image/png;base64,${detailData?.logo}` : defaultLogo} alt="" />

              <span>{detailData?.name}</span>
            </div>

            <Button outline color="success" disabled={!detailData?.canRefer} onClick={setVendorReferModalOpen}>
              Refer
            </Button>
          </div>

          <div className="vendorDetailInfoDetails">
            <div className="detailInfoLeft">
              <Detail
                className="VendorDetailData"
                titleClassName="VendorDetailData-Title"
                valueClassName="VendorDetailData-Value"
                title="PHARMACY OID"
              >
                {detailData?.oid || "-"}
              </Detail>{" "}
              <Detail
                className="VendorDetailData"
                titleClassName="VendorDetailData-Title"
                valueClassName="VendorDetailData-Value"
                title="LICENSE #"
              >
                {detailData?.license || "-"}
              </Detail>{" "}
              <Detail
                className="VendorDetailData"
                titleClassName="VendorDetailData-Title"
                valueClassName="VendorDetailData-Value"
                title="EMAIL"
              >
                {detailData?.email || "-"}
              </Detail>{" "}
              <Detail
                className="VendorDetailData"
                titleClassName="VendorDetailData-Title"
                valueClassName="VendorDetailData-Value"
                title="PHONE"
              >
                {detailData?.phone || "-"}
              </Detail>{" "}
              <Detail
                className="VendorDetailData"
                titleClassName="VendorDetailData-Title"
                valueClassName="VendorDetailData-Value"
                title="ADDRESS"
              >
                {detailData?.street || "-"} {detailData?.city || "-"} {detailData?.zipCode || "-"}
              </Detail>
              <Detail
                className="VendorDetailData"
                titleClassName="VendorDetailData-Title"
                valueClassName="VendorDetailData-Value"
                title="WEBSITE"
              >
                {detailData?.website || "-"}
              </Detail>
              <Detail
                className="VendorDetailData"
                titleClassName="VendorDetailData-Title"
                valueClassName="VendorDetailData-Value"
                title="CATEGORY"
              >
                {_.map(detailData?.vendorTypes, "name").join(", ") || "-"}
              </Detail>{" "}
              <Detail
                className="VendorDetailData"
                titleClassName="VendorDetailData-Title"
                valueClassName="VendorDetailData-Value"
                title="SERVICE"
              >
                {_.map(detailData?.serviceTypes, "name").join(", ") || "-"}
              </Detail>
            </div>

            <div className="vendorDetailInfoRight">
              <span className="vendorDetailInfoRightTitle">Operating Hours:</span>

              <div className="vendorDetailInfoRightTime">
                <div className="time">
                  <img src={SuccessImg} alt="" />

                  <div>Monday-Friday: {detailData?.operatingWorkDay || "-"}</div>
                </div>

                <div className="time">
                  <img src={SuccessImg} alt="" />

                  <div>Saturday: {detailData?.operatingSaturday || "-"}</div>
                </div>

                <div className="time">
                  <img src={SuccessImg} alt="" />

                  <div>Sunday: {detailData?.operatingSunday || "-"}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="detailPhoto">
          <div>
            <img
              src={
                detailData?.photos?.length >= 1 && detailData?.photos[0].url
                  ? `data:image/png;base64,${detailData?.photos[0].url}`
                  : defaultPic
              }
              alt=""
            />
          </div>
          <div>
            <img
              src={
                detailData?.photos?.length >= 2 && detailData?.photos[1].url
                  ? `data:image/png;base64,${detailData?.photos[1].url}`
                  : defaultPic
              }
              alt=""
            />
          </div>
          <div>
            <img
              src={
                detailData?.photos?.length >= 3 && detailData?.photos[2].url
                  ? `data:image/png;base64,${detailData?.photos[2].url}`
                  : defaultPic
              }
              alt=""
            />
          </div>
          <div>
            <img
              src={
                detailData?.photos?.length >= 4 && detailData?.photos[3].url
                  ? `data:image/png;base64,${detailData?.photos[3].url}`
                  : defaultPic
              }
              alt=""
            />
          </div>
        </div>

        <div className="detailIntroduction">{detailData?.introduction}</div>

        <div className="detailMap">
          <APIProvider apiKey={config.google.maps.apiKey}>
            <Map
              zoom={15}
              {...cameraProps}
              gestureHandling={"greedy"}
              disableDefaultUI={false}
              onCameraChanged={handleCameraChange}
              mapId={"8b15fd729aa51fc"}
            >
              {detailData?.location !== undefined && detailData?.location?.latitude !== null && (
                <MarkerWithInfoWindow
                  position={{ lat: detailData?.location?.latitude, lng: detailData?.location?.longitude }}
                  addressName={detailData?.name}
                  address={`${detailData?.street} ${detailData?.city} ${detailData?.zipCode}`}
                />
              )}
            </Map>
          </APIProvider>
        </div>
      </div>
    </>
  );
};

export default VendorDetailLeft;
