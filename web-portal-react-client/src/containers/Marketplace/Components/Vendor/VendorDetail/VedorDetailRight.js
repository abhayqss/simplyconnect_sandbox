import moment from "moment/moment";
import { ReactComponent as Empty } from "images/empty.svg";
import { Loader } from "../../../../../components";

const VedorDetailRight = (props) => {
  const { referHistoryList, loading } = props;
  return (
    <div className="vendorDetailRightWrap">
      <div className="referhistory">
        <div className="vendorDetailRightHeader">
          <div className="vendorDetailRightHeaderTitle">Referral History</div>
          {/* <div className="vendorDetailRightHeaderViewMore">
            <span>View More</span><span>></span>
           </div>*/}
        </div>
        <div className="vendorDetailRightBox">
          {loading && <Loader className={"vendorReferLoading"} />}

          <div className="referhistoryWrap">
            {referHistoryList &&
              referHistoryList.map((item, index) => {
                return (
                  <div className="referhistoryBox" key={index}>
                    <div className="referhistoryBoxHeader">
                      {/*<span>08/22</span><span>09:00</span>*/}
                      <span> {item?.referTime ? moment(item?.referTime).format("MM/DD/YYYY HH:mm") : "-"}</span>
                    </div>

                    <div className="referhistoryInfo" title={item?.referringIndividual}>
                      Provider:{item?.referringIndividual}
                    </div>

                    <div className={"referDescription"} title={item.referContent}>
                      Description: {item.referContent}
                    </div>
                  </div>
                );
              })}
            {referHistoryList?.length === 0 && (
              <>
                <div className="empty-box">
                  <Empty className="empty-img" />
                </div>
              </>
            )}
          </div>
        </div>
      </div>

      {/* 暂时隐藏*/}
      {/* <div className="recentBids">
        <div className="vendorDetailRightHeader">
          <div className="vendorDetailRightHeaderTitle">
            Recent Bids
          </div>
          <div className="vendorDetailRightHeaderViewMore">
            <span>View More</span><span>></span>
          </div>
        </div>

        <div className="vendorDetailRightBox">

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
    </div>
  );
};

export default VedorDetailRight;
