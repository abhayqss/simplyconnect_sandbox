import React, { useCallback, useEffect, useRef, useState } from "react";
import DocumentTitle from "react-document-title";
import { Breadcrumbs } from "components";

import "./VendorDetail.scss";
import Footer from "components/Footer/Footer";
import VendorDetailLeft from "./VendorDetailLeft";
import VedorDetailRight from "./VedorDetailRight";
import { useLocation, useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { getVendorDetail } from "redux/marketplace/Vendor/VendorActions";
import { SuccessDialog } from "components/dialogs";
import service from "services/Marketplace";
import ReferralRequestEditor from "../../../../Referrals/ReferralRequestEditor/ReferralRequestEditor";
import { useAuthUser } from "hooks/common";
import { ONLY_VIEW_ROLES, SYSTEM_ROLES } from "../../../../../lib/Constants";

const VendorDetail = () => {
  const location = useLocation();
  const myRef = useRef(null);
  const dispatch = useDispatch();
  const user = useAuthUser();
  const [vendorReferModalOpen, setVendorReferModalOpen] = useState(false);
  const [isReferSuccessDialogOpen, setIsReferSuccessDialogOpen] = useState(false);
  const [referHistoryList, setReferHistoryList] = useState([]);
  const [loading, setLoading] = useState(false);
  const { id } = useParams();
  const [breadcrumbsItems, setBreadcrumbsItems] = useState([]);

  useEffect(() => {
    if (location.state) {
      setBreadcrumbsItems([
        { ...location.state },
        { title: "VendorDetail", href: `/marketplace/vendorDetail`, isActive: true },
      ]);
    } else {
      setBreadcrumbsItems([
        { title: "Marketplace", href: "/marketplace", isEnabled: true },
        { title: "VendorDetail", href: `/marketplace/vendorDetail`, isActive: true },
      ]);
    }
  }, [location.state]);

  const isInAssociationSystem = ONLY_VIEW_ROLES.includes(user.roleName);
  useEffect(() => {
    if (myRef.current) {
      myRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [location]);

  useEffect(() => {
    dispatch(getVendorDetail(id));
  }, []);

  const onReferSuccess = () => {
    setVendorReferModalOpen(false);
    setIsReferSuccessDialogOpen(true);
    getVendorHistory();
  };
  const onCloseReferModal = () => {
    getVendorHistory();
    setVendorReferModalOpen(false);
  };
  const isSuperAdmin = () => {
    return JSON.parse(localStorage.getItem("AUTHENTICATED_USER")).roleName === "ROLE_SUPER_ADMINISTRATOR";
  };

  const getVendorHistory = () => {
    setLoading(true);
    service.getVendorReferHistory({ vendorId: id, page: 0, size: 4, sort: "referTime,desc" }).then((res) => {
      setReferHistoryList(res.data);
      setLoading(false);
    });
  };

  useEffect(() => {
    getVendorHistory();
  }, [id]);

  const onSaveReferralRequestSuccess = useCallback(() => {
    getVendorHistory();
    setVendorReferModalOpen(false);
  }, []);

  const detailData = useSelector((state) => state.vendor.vendorDetail);
  return (
    <>
      <DocumentTitle title="Simply Connect | Marketplace | VendorDetail">
        <div className={"vendorDetailWrap"} ref={myRef}>
          <Breadcrumbs items={breadcrumbsItems} />

          <div className="vendorDetailBox">
            <div className="vendorDetailLeft">
              <VendorDetailLeft setVendorReferModalOpen={setVendorReferModalOpen} />
            </div>
            <div className="vendorDetailRight">
              <VedorDetailRight referHistoryList={referHistoryList} loading={loading} />
            </div>

            <ReferralRequestEditor
              isFromVendor={true}
              isOpen={vendorReferModalOpen}
              isAssociation={isInAssociationSystem}
              marketplace={{}}
              categoryAndServiceData={detailData?.vendorTypes}
              clinicalVendor={detailData?.hieAgreement}
              vendorId={id}
              organizationId={id}
              onClose={onCloseReferModal}
              onSaveSuccess={onSaveReferralRequestSuccess}
              successDialog={{
                text: `The request will be displayed in the "Outbound" section located under the "Referrals and Inquires" tab. You can see the details and manage status of the referral request there.`,
              }}
            />

            {isReferSuccessDialogOpen && (
              <SuccessDialog
                isOpen
                title="Refer have been send."
                buttons={[
                  {
                    text: "OK",
                    onClick: () => {
                      setIsReferSuccessDialogOpen(false);
                    },
                  },
                ]}
              />
            )}
          </div>

          <Footer theme="gray" className="markplaceFooter" />
        </div>
      </DocumentTitle>
    </>
  );
};

export default VendorDetail;
