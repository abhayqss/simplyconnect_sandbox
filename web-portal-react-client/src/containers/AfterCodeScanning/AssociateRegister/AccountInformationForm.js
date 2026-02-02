import { Button, Col, Form, Row } from "reactstrap";
import React, { useEffect, useState } from "react";
import { PhoneField, TextField } from "../../../components/Form";
import { useDispatch, useSelector } from "react-redux";
import { qrCreate } from "../../../redux/QrCode/QrcodeActions";
import _ from "lodash";
import { useParams } from "react-router-dom";

const AccountInformationForm = (props) => {
  const { setActiveStep } = props;
  const dispatch = useDispatch();
  const urlParams = useParams();

  const { vendorFormData, qrCreadeSuccess } = useSelector((state) => state.Qrcode);
  console.log(vendorFormData, "vendorFormData");

  const [whetherToVerify, setWhetherToVerify] = useState(false);
  const [isThePhoneNumberCorrect, setIsThePhoneNumberCorrect] = useState(false);
  const [isNextClick, setIsNextClick] = useState(false);
  const [verifyIfThePasswordIsCorrect, setVerifyIfThePasswordIsCorrect] = useState(false);
  const requiredFields = ["firstName", "lastName", "email", "accountPhone", "password", "confirmPassword"];
  const [isPasswordMeetsRules, setIsPasswordMeetsRules] = useState(false);
  const [isEmailMeetsRules, setIsEmailMeetsRules] = useState(false);

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    accountPhone: "",
    password: "",
    confirmPassword: "",
  });

  useEffect(() => {
    const reg = /\+?\d{10,16}/;
    if (reg.test(formData.accountPhone)) {
      setIsThePhoneNumberCorrect(true);
    } else {
      setIsThePhoneNumberCorrect(false);
    }
  }, [formData.accountPhone]);

  useEffect(() => {
    if (formData.password === formData.confirmPassword) {
      setVerifyIfThePasswordIsCorrect(true);
    } else {
      setVerifyIfThePasswordIsCorrect(false);
    }
  }, [formData.confirmPassword]);

  const allFieldsFilled = (data, required) => {
    return required.every((field) => !!data[field]);
  };

  const thePasswordMeetsTheRules = (password) => {
    const regex = /^(?=.{8,})(?=(?:[^a-zA-Z]*[a-zA-Z]){2})(?=(?:[^0-9]*[0-9]){2})(?=.*[A-Z])(?=.*\W).*$/;
    const result = regex.test(password);

    if (result) {
      setIsPasswordMeetsRules(true);
    } else {
      setIsPasswordMeetsRules(false);
    }
  };
  const theEmailMeetsTheRules = (email) => {
    const regex = /\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/;
    const result = regex.test(email);
    if (result) {
      setIsEmailMeetsRules(true);
    } else {
      setIsEmailMeetsRules(false);
    }
  };

  useEffect(() => {
    thePasswordMeetsTheRules(formData.password);
  }, [formData.password]);

  useEffect(() => {
    theEmailMeetsTheRules(formData.email);
  }, [formData.email]);

  const submit = () => {
    sessionStorage.setItem("_A_F", JSON.stringify(formData));

    setWhetherToVerify(true);

    if (
      allFieldsFilled(formData, requiredFields) &&
      isThePhoneNumberCorrect &&
      verifyIfThePasswordIsCorrect &&
      isPasswordMeetsRules
    ) {
      const params = {
        ...formData,
        ...vendorFormData,
        type: urlParams.type,
        primaryId: urlParams.id,
        oid: `${Date.now()}-${_.random(1000, 9999)}`,
      };

      dispatch(qrCreate(params));
    }
  };

  useEffect(() => {
    const data = JSON.parse(sessionStorage.getItem("_A_F"));
    if (data) {
      setFormData(data);
    }
  }, []);

  useEffect(() => {
    if (qrCreadeSuccess) {
      setActiveStep(2);
    }
  }, [qrCreadeSuccess]);

  const back = () => {
    sessionStorage.setItem("_A_F", JSON.stringify(formData));
    setActiveStep(0);
  };

  const changeField = (name, value) => {
    isNextClick && setWhetherToVerify(true);
    setFormData({
      ...formData,
      [name]: value, // 更新特定的字段
    });
  };

  return (
    <>
      <Form className="AccountInformatinForm">
        <div className="AccountInformationTitle">Account Information</div>

        <Row>
          <Col lg={4} md={4}>
            <TextField
              type="text"
              name="firstName"
              value={formData.firstName} //
              label="First Name*"
              errorText={whetherToVerify && !formData.firstName && "Please fill in the required field"}
              onChange={changeField}
            />
          </Col>

          <Col lg={4} md={4}>
            <TextField
              type="text"
              name="lastName"
              value={formData.lastName} //
              label="Last Name*"
              errorText={whetherToVerify && !formData.lastName && "Please fill in the required field"}
              onChange={changeField}
            />
          </Col>

          <Col lg={4} md={4}>
            <TextField
              type="text"
              name="email"
              value={formData.email} //
              label="Email*"
              errorText={whetherToVerify && !isEmailMeetsRules && "Please enter a valid email."}
              onChange={changeField}
            />
          </Col>
        </Row>

        <Row>
          <Col lg={4} md={4}>
            <PhoneField
              name="accountPhone"
              value={formData.accountPhone} //
              label="Account phone number*"
              errorText={
                whetherToVerify && !isThePhoneNumberCorrect && "Please fill in the correct mobile phone number."
              }
              onChange={changeField}
            />
          </Col>
        </Row>

        <div className="AccountInformationTitle">Password</div>

        <Row>
          <Col lg={4} md={4}>
            <TextField
              type="password"
              name="password"
              value={formData.password} //
              label="Password*"
              errorText={
                whetherToVerify &&
                !isPasswordMeetsRules &&
                "Length greater than 8 characters, at least 2 letters, 2 numbers, one uppercase letter and one symbol."
              }
              onChange={changeField}
            />
          </Col>

          <Col lg={4} md={4}>
            <TextField
              type="password"
              name="confirmPassword"
              value={formData.confirmPassword} //
              label="Confirm password*"
              errorText={whetherToVerify && !verifyIfThePasswordIsCorrect && "Please enter the correct password."}
              onChange={changeField}
            />
          </Col>
        </Row>

        <Row>
          <Col lg={4} md={4}>
            <Button style={{ width: "100%" }} color="success" onClick={submit}>
              {"Next"}
            </Button>
          </Col>
        </Row>

        <Row>
          <Col lg={4} md={4}>
            <Button
              style={{ width: "100%", marginTop: 20 }}
              color="primary"
              outline
              onClick={back}
              // disabled={isFetching}
            >
              {"Prev"}
            </Button>
          </Col>
        </Row>
      </Form>
    </>
  );
};

export default AccountInformationForm;
