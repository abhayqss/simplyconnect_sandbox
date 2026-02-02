import SuccessImg from '../../../images/success2.svg';
import './AssociateRegister.scss';
import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { saveVendorFormData } from "../../../redux/QrCode/QrcodeActions";

const CompleteRegistration = () => {
  const dispatch = useDispatch();

  useEffect(() => {
    sessionStorage.removeItem('_A_F');
    dispatch(saveVendorFormData(null));
  }, []);

  return (
    <div className='CompleteRegistration'>
      <img src={SuccessImg} alt=""/>

      <div className="registrationSuccessTitle">
        Successfully
      </div>

      <div className="registrationSuccessInfo">
          The vendor account needs 3-5 days to be verified, and you will be
        notified by email after the approval.
      </div>

      <div className="registrationSuccessBox">
        <div className="registrationSuccessBoxTitle">
          Associated benefit:
        </div>

        <div className="registrationSuccessBoxInfo">
          After the building is associated, you can receive bids from the building in a timely manner.
        </div>
      </div>
    </div>
  );
}

export default CompleteRegistration;
