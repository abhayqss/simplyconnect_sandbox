import './publicBuling.scss'
import scImg from '../../images/marketplace/sc.png';
import SearchImg from '../../images/marketplace/search.svg';
import womanImg from '../../images/marketplace/woman.png';
import seniorCare from '../../images/marketplace/seniorCare.png';
import { useEffect, useState } from "react";
import PublicBuildingList from "./publicBuildingList";

const PublicBuilding = () => {
  const [searchText, setSearchText] = useState('');

  useEffect(() => {
    const value = sessionStorage.getItem('_search');
    if (value) {
      setSearchText(value);
    }
  }, [sessionStorage.getItem('_search')]);

  return (

    <>
      <div className='publicBulingWrap'>

        <div className="publicBulingWrapTop" style={searchText ? { marginBottom: 0 } : {}}>
          <div className='publicBulingWrapTopBox'>
            <div className='publicBulingHeader'>Find a great place to live for your loved one.</div>

            <div className="searchBox">
              <img src={scImg} alt="" className='searchScImg'/>

              <label className={'buildingSearchLabel'}>
                <input type="text" placeholder={'City,State,Zip'} onChange={(e) => {
                  setSearchText(e.target.value);
                  sessionStorage.setItem('_search', e.target.value);
                }}
                       value={searchText}/>
                <img src={SearchImg} alt="" className='searchImg'/>
              </label>

            </div>
          </div>
        </div>

        {
          !searchText ? <>
            <div className="publicBulingWrapBigBox">
              <img src={womanImg} alt="" className='publicBulingWrapBigBoxWomanImg'/>


              <div className='publicBulingWrapBigBoxRight'>
                <img src={seniorCare} alt=""/>

                <div className='publicBulingWrapBigBoxRightLocation'>Location</div>

                <div>
                  Location is an important factor in terms of proximity to loved ones or a desire to remain in a
                  particular
                  area. However, location also dictates how a facility operates based on local laws. For example, some
                  states
                  limit the amount of medical assistance that can be offered to residents. In those states, the emphasis
                  is
                  typically on socialization more than medical care. Be sure the facility you choose can provide the
                  services
                  you require.
                </div>
              </div>
            </div>

            <>

              <img src={womanImg} alt="" className='womanImg'/>

              <div className='publicBulingInfo'>
                <img src={seniorCare} alt=""/>
              </div>

              <div className="publicBuildingInfoText">
                <div>Location</div>

                <div>
                  Location is an important factor in terms of proximity to loved ones or a desire to remain in a
                  particular
                  area. However, location also dictates how a facility operates based on local laws. For example, some
                  states
                  limit the amount of medical assistance that can be offered to residents. In those states, the emphasis
                  is
                  typically on socialization more than medical care. Be sure the facility you choose can provide the
                  services
                  you require.
                </div>
              </div>

            </>
          </> : <PublicBuildingList searchText={searchText}/>
        }


        <div className='backgroundBox'/>

        <div className='publicBulingInfoEnd'>
          Pick from our directory and look for "Simply Connect" premium partners to ensure world class care
          /communication
        </div>


      </div>
    </>

  );
};

export default PublicBuilding;
