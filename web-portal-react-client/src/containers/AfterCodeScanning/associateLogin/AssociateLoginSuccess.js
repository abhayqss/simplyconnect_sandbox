import successimg from '../../../images/Associate/success.svg';
import './associateLogin.scss';
import '../AfterCodeScanning.scss';
import logo from "../../../images/logo-m.svg";

const AssociateLoginSuccess = () => {
  return (
    <>
      <div className="AfterCodeScanningHeader">
        <img src={logo} alt=""/>
      </div>


      <div className='associateLoginSuccessWrap'>
        <img src={successimg} alt=""/>

        <div className="titleFirst">Association Success</div>
        <div className="title2">Avilacare Senior Living</div>

        <div className="benefit">
          <div className="benefitTitle">Associated benefit:</div>
          <div className="benefitInfo">
            After the building is associated, you can receive bids from the building in a timely manner.
          </div>
        </div>
      </div>
    </>

  );
};

export default AssociateLoginSuccess;
