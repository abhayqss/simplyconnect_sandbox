import { useParams } from 'react-router-dom';
import logo from '../../images/logo-m.svg';
import './AfterCodeScanning.scss';
import OrgDetail from "./OrgDetail/OrgDetail";
import BuildingDetail from "./BuildingDetail/BuildingDetail";

const AfterCodeScanning = () => {

  const { id, type } = useParams();
  return (
    <>
      <div className="AfterCodeScanningHeader">
        <img src={logo} alt=""/>
      </div>

      <div className="AfterCodeScanningWrap">
        {
          type === 'organization' ? <OrgDetail id={id} type={type}/> :
            <BuildingDetail id={id} type={type}/>
        }
      </div>


      {/*<div className='AfterCodeScanningWrap'>Id: {id}, Type: {type}</div>*/}
    </>
  );
};

export default AfterCodeScanning;
