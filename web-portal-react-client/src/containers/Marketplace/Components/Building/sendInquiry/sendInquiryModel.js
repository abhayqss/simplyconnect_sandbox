import { Loader, Modal, Scrollable } from "components";
import React, { useCallback, useEffect, useState } from "react";

import { Col, Row, Form, Button } from "reactstrap";

import { TextField, PhoneField } from "components/Form";

import { ReactComponent as Info } from "images/info.svg";
import { useAuthUser, useScrollToFormError } from "../../../../../hooks/common";
import service from "services/Marketplace";
import { Dialog, SuccessDialog, WarningDialog } from "components/dialogs";
import "./sendInquiryModel.scss";

const cellPhoneHint = {
  placement: "top",
  target: "cell-phone-hint",
  render: () => (
    <ul className="AssociationForm-TooltipBody">
      <li>Use digits only and "+" before country code.</li>
      <li>Otherwise no spaces, dashes, or special symbols allowed.</li>
      {/*<li>Digits only allowed</li>
      <li>No spaces, dashes, or special symbols</li>
      <li>Country code is required</li>
      <li>‘+’ may be a leading symbol</li>*/}
    </ul>
  ),
};

const SendInquiryModel = (props) => {
  const { isOpen, onClose, buildingId, user } = props;

  const onScroll = useScrollToFormError(".ReferRequestForm", scroll);

  const errText = "Please fill in the required field";
  const phoneErrorText = "Please enter the correct mobile phone number.";

  const [isThePhoneNumberCorrect, setIsThePhoneNumberCorrect] = useState(false);
  const [inquiryData, setInquiryData] = useState({
    firstName: user?.firstName,
    lastName: user?.lastName,
    sourceEmail: user?.email,
    phoneNumber: user?.mobilePhone,
    description: "",
  });
  const [whetherToVerify, setWhetherToVerify] = useState(false);
  const requiredFields = ["firstName", "lastName", "sourceEmail", "phoneNumber"];

  const [showSuccessDialog, setShowSuccessDialog] = useState(false);
  const [showWaringDialog, setShowWaringDialog] = useState(false);
  const [isFetching, setIsFetching] = useState(false);

  useEffect(() => {
    const reg = /\+?\d{10,16}/;
    if (reg.test(inquiryData.phoneNumber)) {
      setIsThePhoneNumberCorrect(true);
    } else {
      setIsThePhoneNumberCorrect(false);
    }
  }, [inquiryData.phoneNumber]);

  const changeFields = (name, value) => {
    whetherToVerify && setWhetherToVerify(true);

    setInquiryData({
      ...inquiryData,
      [name]: value,
    });
  };

  const allFieldsFilled = (data, required) => {
    return required.every((field) => !!data[field]);
  };

  const tryToSubmit = useCallback((e) => {
    e.preventDefault();
    e.stopPropagation();
    e.nativeEvent.stopImmediatePropagation();

    setWhetherToVerify(true);
    setIsFetching(true);

    if (allFieldsFilled(inquiryData, requiredFields) && isThePhoneNumberCorrect) {
      const body = {
        ...inquiryData,
        buildingId,
      };

      service.sendInquiry(body).then((res) => {
        if (res.data) {
          setShowSuccessDialog(true);
          setIsFetching(false);
        } else {
          setShowWaringDialog(true);
          setIsFetching(false);
        }
      });
    } else {
      onScroll();
      setIsFetching(false);
    }
  });

  return (
    <>
      {isOpen && (
        <Modal
          isOpen
          hasFooter={false}
          hasCloseBtn={true}
          onClose={() => {
            onClose();
            setInquiryData({
              firstName: "",
              lastName: "",
              sourceEmail: "",
              phoneNumber: "",
              description: "",
            });
          }}
          title="Send Inquiry"
          className="SendInquiryModal"
        >
          <div style={{ padding: 20 }}>
            <Form className="ReferRequestForm" onSubmit={tryToSubmit}>
              {isFetching && <Loader style={{ position: "fixed" }} hasBackdrop />}
              <Scrollable style={{ flex: 1 }}>
                <Row>
                  <Col lg={6} md={12}>
                    <TextField
                      type="text"
                      name="firstName"
                      value={inquiryData.firstName}
                      label="First Name*"
                      className="ReferralRequestForm-TextField"
                      onChange={changeFields}
                      errorText={whetherToVerify && !inquiryData.firstName && errText}
                    />
                  </Col>

                  <Col lg={6} md={12}>
                    <TextField
                      type="text"
                      name="lastName"
                      value={inquiryData.lastName}
                      label="Last Name*"
                      className="ReferralRequestForm-TextField"
                      onChange={changeFields}
                      errorText={whetherToVerify && !inquiryData.lastName && errText}
                    />
                  </Col>
                </Row>

                <Row>
                  <Col lg={6} md={12}>
                    <TextField
                      type="email"
                      name="sourceEmail"
                      value={inquiryData.sourceEmail}
                      label="Email*"
                      className="OrganizationForm-TextField"
                      errorText={whetherToVerify && !inquiryData.sourceEmail && errText}
                      maxLength={256}
                      onChange={changeFields}
                    />
                  </Col>

                  <Col lg={6} md={12}>
                    <PhoneField
                      name={"phoneNumber"}
                      value={inquiryData.phoneNumber}
                      label="Phone*"
                      className="AssociationForm-PhoneField"
                      errorText={whetherToVerify && !isThePhoneNumberCorrect && phoneErrorText}
                      onChange={changeFields}
                      renderLabelIcon={() => <Info id="cell-phone-hint" className="AssociationForm-InfoIcon" />}
                      tooltip={cellPhoneHint}
                    />
                  </Col>
                </Row>
                <Row></Row>

                <Row>
                  <Col lg={12}>
                    <TextField
                      type="textarea"
                      name="description"
                      value={inquiryData.description}
                      label="Description"
                      onChange={changeFields}
                    />
                  </Col>
                </Row>

                <div className="SendInquiryModal-ReferralRequestForm-Buttons">
                  <Button
                    outline
                    color="success"
                    onClick={() => {
                      onClose();
                      setInquiryData({
                        firstName: "",
                        lastName: "",
                        sourceEmail: "",
                        phoneNumber: "",
                        description: "",
                      });
                    }}
                  >
                    Cancel
                  </Button>
                  <Button color="success" type={"submit"}>
                    Submit
                  </Button>
                </div>
              </Scrollable>
            </Form>
          </div>
        </Modal>
      )}

      <SuccessDialog
        isOpen={showSuccessDialog}
        title="Send Inquiry Success"
        buttons={[
          {
            text: "Close",
            onClick: () => {
              setShowSuccessDialog(false);
              onClose();
              setInquiryData({
                firstName: "",
                lastName: "",
                sourceEmail: "",
                phoneNumber: "",
                description: "",
              });
            },
          },
        ]}
      />

      <WarningDialog
        isOpen={showWaringDialog}
        title="Send exception, please contact the administrator."
        buttons={[
          {
            text: "Close",
            onClick: () => {
              setShowWaringDialog(false);
            },
          },
        ]}
      />
    </>
  );
};

export default SendInquiryModel;
