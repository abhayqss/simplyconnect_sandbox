import DocumentTitle from "react-document-title";
import { Breadcrumbs } from "../../../../../components";
import React, { useEffect, useRef, useState } from "react";
import Footer from "../../../../../components/Footer/Footer";
import Building from "../Building";
import { useDispatch, useSelector } from "react-redux";
import MyPaginationComponent from "../../../../../lib/utils/PaginationUtils";
import { useLocation, useParams } from "react-router-dom";
import { getBuildingList } from "../../../../../redux/marketplace/Building/BuildingActions";
import { toNumberExcept } from "../../../../../lib/utils/Utils";
import community from "../../../../../entities/Community";
import Pagination from "../../../../../components/Pagination/Pagination";

const BuildingList = () => {
  const myRef = useRef();
  const location = useLocation();
  const totalRecords = useSelector((state) => state.building.totalCount);
  const [currentPage, setCurrentPage] = useState(1);

  const params = useParams();

  const organizationId = toNumberExcept(params.organizationId, [null, undefined]);
  useEffect(() => {
    if (myRef.current) {
      myRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [location]);

  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(getBuildingList(currentPage, 12, "", organizationId));
  }, [dispatch, currentPage, organizationId]);

  const buildingList = useSelector((state) => state.building.data);

  return (
    <DocumentTitle title="Simply Connect | Marketplace | BuildingList">
      <div className={"buildingDetailWrap"} ref={myRef}>
        <Breadcrumbs
          items={[
            { title: "Marketplace", href: "/marketplace", isEnabled: true },
            { title: "BuildingList", href: `/marketplace/buildingList/${organizationId}`, isActive: true },
          ]}
        />

        <Building currentPage={currentPage} buildingList={buildingList} organizationId={organizationId} />

        <div className={"paginationBox"}>
          <Pagination page={currentPage} size={12} totalCount={totalRecords} onPageChange={setCurrentPage} />

          {/*   <MyPaginationComponent totalRecords={totalRecords || 0} onPageChange={(currentPage) => {
            setCurrentPage(currentPage);
          }}/>*/}
        </div>

        <Footer theme="gray" className="markplaceFooter" />
      </div>
    </DocumentTitle>
  );
};

export default BuildingList;
