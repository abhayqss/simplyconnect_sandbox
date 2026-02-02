import React, { useCallback, useEffect, useState } from "react";
import "./Search.scss";
import { useDispatch, useSelector } from "react-redux";
import { ReactComponent as NoData } from "images/empty.svg";
import { getBuildingList } from "../../../../redux/marketplace/Building/BuildingActions";
import { getVendorList } from "../../../../redux/marketplace/Vendor/VendorActions";
import { Link } from "react-router-dom";
import { path } from "../../../../lib/utils/ContextUtils";
import defaultImg from "../../../../images/marketplace/defaultImg.png";
import Pagination from "components/Pagination/Pagination";
import _ from "lodash";
import SendInquiryModel from "../Building/sendInquiry/sendInquiryModel";
import ReferralRequestEditor from "../../../Referrals/ReferralRequestEditor/ReferralRequestEditor";
import { isInteger } from "../../../../lib/utils/Utils";
import { useAuthUser } from "hooks/common";
import {ONLY_VIEW_ROLES, VENDOR_ROLES } from "../../../../lib/Constants";
import { Button } from "reactstrap";

const MarketplaceSearch = (props) => {
  const dispatch = useDispatch();
  const user = useAuthUser();
  const { data: buildingList, totalCount: buildingTotal } = useSelector((state) => state.building);
  const { vendorList, vendorTotal } = useSelector((state) => state.vendor);

  const {
    searchData,
    searchType,
    setIsSearch,
    setSearchValue,
    setSelectValue,
    organizationId,
    isFromSearch,
    setIsFromSearch,
    clientId,
  } = props;
  const [currentPage, setCurrentPage] = useState(1);
  const [countSize] = useState(12);
  const result = searchType.id === "Building" ? buildingList : vendorList;
  const total = searchType.id === "Building" ? buildingTotal : vendorTotal;

  const [showSendInquiryModel, setShowSendInquiryModel] = useState(false);
  const [buildingId, setBuildingId] = useState();
  const [isReferralRequestEditorOpen, setIsReferralRequestEditorOpen] = useState(false);

  const [vendorId, setVendorId] = useState();

  const [comName, setComName] = useState();
  const [orgName, setOrgName] = useState();
  const [comId, setComId] = useState();
  // vendor communityIds
  const [communityIds, setCommunityIds] = useState([]);
  const [vendorReferDisabled, setVendorReferDisabled] = useState(false);

  useEffect(() => {
    setCurrentPage(1);
    if (
      searchType.id === "Vendor" &&
      (VENDOR_ROLES.includes(user.roleName) || ONLY_VIEW_ROLES.includes(user.roleName))
    ) {
      setVendorReferDisabled(true);
    } else {
      setVendorReferDisabled(false);
    }
  }, [user.roleName, searchType.id]);

  const featData = _.debounce(() => {
    if (searchType.id === "Building") {
      dispatch(getBuildingList(currentPage, 12, searchData, organizationId));
    }

    if (searchType.id === "Vendor") {
      const params = {
        name: searchData,
        page: currentPage - 1,
        size: 12,
      };

      dispatch(getVendorList(params));
    }
  }, 600);

  useEffect(() => {
    featData();

    return () => {
      featData.cancel(); // 取消未完成的 debounce 操作
    };
  }, [searchData, searchType.id, currentPage, organizationId]);

  const goBack = () => {
    setIsSearch(false);
    setSearchValue("");
    setSelectValue({ label: "Building", id: "Building" });
  };

  const handleChange = (value) => {
    setCurrentPage(value);
  };

  const sendRequest = (id, buildingId) => {
    if (id === "Building") {
      setShowSendInquiryModel(true);
      setBuildingId(buildingId);
    }
  };

  const onOpenReferralRequestEditor = (item) => {
    setIsReferralRequestEditorOpen(true);
    setIsFromSearch(true);

    if (searchType.id === "Vendor") {
      setBuildingId(null);

      setCommunityIds(item.communityIds);

      setVendorId(item.id);
      setOrgName(item.name);
    } else {
      setBuildingId(item.id);
      setVendorId(null);

      setComId(item.id);
      setComName(item.name);
      setOrgName(item.orgname);
    }
  };

  const onCloseReferralRequestEditor = useCallback(() => {
    setIsReferralRequestEditorOpen(false);
  }, []);

  const onSaveReferralRequestSuccess = useCallback(() => {
    setIsReferralRequestEditorOpen(false);
  }, []);

  return (
    <>
      <div className="backToHome" onClick={goBack}>
        Marketplace
      </div>

      <div className="marketplaceSearchList">
        {result.length > 0 &&
          result.map((item) => {
            return (
              <div className="SearchBox" key={item.id}>
                <Link
                  to={path(
                    searchType.id === "Building"
                      ? `/marketplace/buildingDetail/${item.id}/${organizationId}`
                      : `/marketplace/vendorDetail/${item.id}`,
                  )}
                >
                  <div className="SearchBoxImgBox">
                    <img
                      src={item.logo ? `data:image/png;base64,${item?.logo}` : defaultImg}
                      alt=""
                      className="searchImg"
                    />
                  </div>
                </Link>

                <div className="searchCenter">
                  <div className="searchName">{item.name || "-"}</div>
                  <div className="searchAddress">
                    {item.displayAddress || "-"} <span className="searchZipCode">{item.zipCode || "-"}</span>
                  </div>
                  <div className="searchPhone">{item.phone || "-"}</div>
                </div>

                <div className="marketplaceSearchButton">
                  {searchType.id === "Building" && (
                    <Button color={"success"} onClick={() => sendRequest(searchType.id, buildingId)}>
                      Send {searchType.id === "Building" ? "Inquiry" : "EMAIL"}
                    </Button>
                  )}

                  <Button
                    color={"success"}
                    outline
                    disabled={searchType.id === "Vendor" && !item.canRefer}
                    onClick={() => onOpenReferralRequestEditor(item)}
                  >
                    Refer
                  </Button>
                </div>
              </div>
            );
          })}
        {result.length === 0 && (
          <div className="empty-page">
            <NoData className="empty-Img" />
            <div style={{ marginTop: "20px" }}>No Data</div>
          </div>
        )}
      </div>

      <SendInquiryModel
        isOpen={showSendInquiryModel}
        onClose={() => setShowSendInquiryModel(false)}
        buildingId={buildingId}
      />

      <div className={"paginationBox"}>
        <Pagination size={countSize} page={currentPage} onPageChange={handleChange} totalCount={total} />
      </div>

      <ReferralRequestEditor
        isFromSearch={isFromSearch}
        vendorId={vendorId}
        isFromVendor={searchType.id === "Vendor"}
        isOpen={isReferralRequestEditorOpen}
        communityId={buildingId || vendorId}
        organizationId={searchType.id === "Vendor" ? vendorId : organizationId}
        marketplace={{
          communityId: searchType.id === "Vendor" ? communityIds : comId,
          organizationId: searchType.id === "Vendor" ? "" : organizationId,
          organizationName: searchType.id === "Vendor" ? "" : orgName,
          communityName: searchType.id === "Vendor" ? "" : comName,
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
    </>
  );
};

export default MarketplaceSearch;
