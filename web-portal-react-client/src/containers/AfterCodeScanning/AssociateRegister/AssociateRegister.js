import logo from "../../../images/logo-m.svg";
import "../AfterCodeScanning.scss";
import "./AssociateRegister.scss";

import Box from "@mui/material/Box";
import Stepper from "@mui/material/Stepper";
import Step from "@mui/material/Step";
import StepLabel from "@mui/material/StepLabel";
import VendorInformationForm from "./VendorInformationForm";
import { useState } from "react";
import AccountInformationForm from "./AccountInformationForm";
import CompleteRegistration from "./CompleteRegistration";
import { useParams } from "react-router-dom";

const steps = ["Vendor Information", "Account Information", "Complete Registration"];
const AssociateRegister = () => {
  const { name } = useParams();
  const [activeStep, setActiveStep] = useState(0);

  return (
    <>
      <div className="AfterCodeScanningHeader">
        <img src={logo} alt="" />
      </div>

      <div className="associateRegisterWrap">
        <div className="associateRegisterTitle">
          <div>After registering the vendor account,</div>
          <div>associate it with {name}</div>
        </div>
        <Box sx={{ width: "100%" }}>
          <Stepper activeStep={activeStep} alternativeLabel>
            {steps.map((label) => (
              <Step key={label}>
                <StepLabel>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>
        </Box>
        {activeStep === 0 && <VendorInformationForm setActiveStep={setActiveStep} />}
        {activeStep === 1 && <AccountInformationForm setActiveStep={setActiveStep} />}
        {activeStep === 2 && <CompleteRegistration />}
      </div>
    </>
  );
};

export default AssociateRegister;
