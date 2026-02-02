import logo from "../../../images/logo-m.svg";
import "../AfterCodeScanning.scss";
import "./associateLogin.scss";
import LoginForm from "../../Login/LoginForm/LoginForm";
import { useHistory, useParams } from "react-router-dom";
import { path } from "lib/utils/ContextUtils";
import { useDispatch, useSelector } from "react-redux";
import { associatedBuilding, associatedOrg } from "../../../redux/QrCode/QrcodeActions";

const AssociateLogin = () => {
  const history = useHistory();
  const { id, type, name } = useParams();
  const dispatch = useDispatch();

  const { associatedOrgSuccess, associatedBuildingSuccess } = useSelector((state) => state.Qrcode);

  const onLoginSuccess = (e) => {
    if (e.token) {
      if (type === "organization") {
        dispatch(associatedOrg(id, id));

        setTimeout(() => {
          if (associatedOrgSuccess) {
            history.push(path(`/associate/${id}/${type}/login/success`));
          } else {
            localStorage.clear();
            return false;
          }
        }, 400);
      } else {
        dispatch(associatedBuilding(id, id));
        setTimeout(() => {
          if (associatedBuildingSuccess) {
            history.push(path(`/associate/${id}/${type}/login/success`));
          } else {
            localStorage.clear();
            return false;
          }
        }, 400);
      }
    }
  };

  return (
    <>
      <div className="AfterCodeScanningHeader">
        <img src={logo} alt="" />
      </div>

      <div className="loginForm">
        <div className="associateTitle">
          <div>Please login to associate with</div>
          <div>{name}</div>
        </div>

        <LoginForm onLoginSuccess={onLoginSuccess} />
      </div>
    </>
  );
};
export default AssociateLogin;
