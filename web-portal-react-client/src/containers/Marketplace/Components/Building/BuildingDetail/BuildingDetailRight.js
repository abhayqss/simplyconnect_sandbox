import NoFeedsImg from "images/marketplace/noList.svg";
import { ReactComponent as Empty } from "images/empty.svg";
import { useEffect, useState } from "react";
import server from "services/Marketplace";
import { useSelector } from "react-redux";
import moment from "moment";

const BuildingDetailRight = () => {
  const detailData = useSelector((state) => state.building.detailData);
  const [sendInquiryData, setSendInquiryData] = useState([]);

  useEffect(() => {
    if (detailData?.id) {
      server.findSendInquiry(detailData.id).then((res) => {
        setSendInquiryData(res.data);
      });
    }
  }, [detailData]);

  return (
    <>
      {/*  <div className="notifactionFeeds">
        <div className="buildingDetailRightHeader">
          <div className="buildingDetailRightHeaderTitle">
            Notification Feeds
          </div>
          <div className="buildingDetailRightHeaderViewMore">
            <span>View More</span>
            <span>></span>
          </div>
        </div>

        <div className="buildingDetailRightBox">
          <div className="noFeeds">
            <img src={NoFeedsImg} alt="" />
            <div className="noFeedsText">No announcement yet</div>
          </div>
        </div>
      </div>*/}

      <div className="referhistory">
        <div className="buildingDetailRightHeader">
          <div className="buildingDetailRightHeaderTitle">Inquiries Received</div>
          <div className="buildingDetailRightHeaderViewMore"></div>
        </div>

        <div className="buildingDetailRightBox">
          {sendInquiryData.length === 0 && (
            <div className="empty-box">
              <Empty className="empty-img" />
            </div>
          )}
          {sendInquiryData.length > 0 && (
            <div className="referhistoryWrap">
              <>
                {sendInquiryData.map((item) => {
                  return (
                    <div className="referhistoryBox" key={item.id}>
                      <div className="referhistoryBoxHeader">
                        <span>{moment(item.createTime).format("MM/DD")}</span>
                        <span>{moment(item.createTime).format("HH:mm")}</span>
                      </div>

                      <div className="referhistoryInfo">Provider: {item.fullName}</div>

                      <div className={"referDescription"}>Description: {item.description}</div>
                    </div>
                  );
                })}
              </>
            </div>
          )}
        </div>
      </div>

      {/* <div className="recentBids">
        <div className="buildingDetailRightHeader">
          <div className="buildingDetailRightHeaderTitle">
            Recent Bids
          </div>
          <div className="buildingDetailRightHeaderViewMore">
            <span>View More</span><span>></span>
          </div>
        </div>

        <div className="buildingDetailRightBox">

          <div className="recentBidsWrap">

             Loop rendering
            <div className="recentBidsBox">

              <div className="recentBidsWrapLeft">
                <div>Purchase a batch of tables and chairs</div>
                <div className='recentBidsWrapLeftTime'>
                  <div>Date: 09/22 09:00</div>
                  <div>Amount:$90,000</div>
                </div>
              </div>

              <div className='recentBidsBoxStatusActive'>
                <img src={ActiveImg} alt=""/>
                Active
              </div>

            </div>

            <div className="recentBidsBox">

              <div className="recentBidsWrapLeft">
                <div>Purchase a batch of tables and chairs</div>
                <div className='recentBidsWrapLeftTime'>
                  <div>Date: 09/22 09:00</div>
                  <div>Amount:$90,000</div>
                </div>
              </div>

              <div className='recentBidsBoxStatusActive'>
                <img src={ActiveImg} alt=""/>
                Active
              </div>

            </div>
            <div className="recentBidsBox">

              <div className="recentBidsWrapLeft">
                <div>Purchase a batch of tables and chairs</div>
                <div className='recentBidsWrapLeftTime'>
                  <div>Date: 09/22 09:00</div>
                  <div>Amount:$90,000</div>
                </div>
              </div>

              <div className='recentBidsBoxStatusClosed'>
                <img src={ClosedImg} alt=""/>
                Closed
              </div>

            </div>
            <div className="recentBidsBox">

              <div className="recentBidsWrapLeft">
                <div>Purchase a batch of tables and chairs</div>
                <div className='recentBidsWrapLeftTime'>
                  <div>Date: 09/22 09:00</div>
                  <div>Amount:$90,000</div>
                </div>
              </div>

              <div className='recentBidsBoxStatusActive'>
                <img src={ActiveImg} alt=""/>
                Active
              </div>

            </div>
            <div className="recentBidsBox">

              <div className="recentBidsWrapLeft">
                <div>Purchase a batch of tables and chairs</div>
                <div className='recentBidsWrapLeftTime'>
                  <div>Date: 09/22 09:00</div>
                  <div>Amount:$90,000</div>
                </div>
              </div>

              <div className='recentBidsBoxStatusActive'>
                <img src={ActiveImg} alt=""/>
                Active
              </div>

            </div>


          </div>


        </div>
      </div>*/}
    </>
  );
};

export default BuildingDetailRight;
