import "./publicBuling.scss";
import service from "../../services/PublicBuilding";
import { useCallback, useEffect, useRef, useState } from "react";
import Pagination from "components/Pagination/Pagination";
import defaultImg from "../../images/marketplace/defaultImg.png";
import { CircularProgress } from "@mui/material";
import { path } from "lib/utils/ContextUtils";
import { useHistory } from "react-router-dom";
import _ from "lodash";

const PublicBuildingList = (props) => {
  const { searchText } = props;

  const history = useHistory();
  const [pagesize] = useState(6);
  const [page, setPage] = useState(1);
  const [listData, setListData] = useState([]);
  const [listTotal, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);

  const handleChange = (value) => {
    setPage(value);
  };

  const scrollRef = useRef(null);

  const debouncedFetchData = useCallback(
    _.debounce((searchText, page, pagesize) => {
      // 这里是您的数据获取逻辑...
      service.getPublicBuildingList({ name: searchText, page: page - 1, size: pagesize }).then(async (res) => {
        setLoading(true);

        const fetchLogos = res.data.map(async (item) => {
          try {
            if (item.mainLogoPath) {
              await service.getBuildingLogo(item.id).then((response) => {
                item.logo = response.data;
              });
              return Promise.resolve();
            }
            return Promise.resolve();
          } catch (e) {
            return Promise.resolve();
          }
        });
        await Promise.all(fetchLogos);

        setListData(res.data);
        setTotal(res.totalCount);

        setLoading(false);
      });
    }, 800),
    [], // 依赖数组为空，保证了函数不会在重新渲染时变化
  );
  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollIntoView({ behavior: "smooth" });
    }

    debouncedFetchData(searchText, page, pagesize);

    /*  const params = {
        name: searchText,
        page,
        size: pagesize,
      }
      service.getPublicBuildingList(params).then(async (res) => {

        setLoading(true);
        const fetchLogos = res.data.map(item => {
          if (item.mainLogoPath) {
            return service.getBuildingLogo(item.id).then(response => {
              item.logo = response.data
            })
          }
          return Promise.resolve();
        });
        await Promise.all(fetchLogos);

        setListData(res.data)
        setTotal(res.totalCount)

        setLoading(false)
      })*/
  }, [searchText, page, debouncedFetchData]);

  const gotoDetail = (id) => {
    history.push(path(`/simplyplace/buildingdetail/${id}`));
  };

  return (
    <>
      <div className="publicBulingListWrap" ref={scrollRef}>
        {loading ? (
          <div className="publicBulingListWrapLoading">
            <CircularProgress color="primary" />
          </div>
        ) : (
          <>
            {listData.map((item) => {
              return (
                <div className="publicBulingListBox" key={item.id} onClick={() => gotoDetail(item.id)}>
                  <img src={item.logo ? `data:image/png;base64,${item?.logo}` : defaultImg} alt="" />
                  <div>{item.name}</div>
                  <div className={"publicBulingListAddress"}>{item.displayAddress}</div>
                  <div style={{ color: "#e53935" }}>{item.zipCode}</div>
                </div>
              );
            })}
          </>
        )}
      </div>

      {listData.length > 0 && (
        <div style={{ marginTop: 20, marginBottom: 20 }}>
          <Pagination totalCount={listTotal} onPageChange={handleChange} page={page} size={pagesize} />
        </div>
      )}
    </>
  );
};
export default PublicBuildingList;
