import { useDispatch, useSelector } from "react-redux";
import React, { useCallback, useEffect, useMemo, useState } from "react";
import { Button, Col, Form, Row } from "reactstrap";
import { Loader } from "components";
import { useForm, useScrollable, useScrollToFormError, useSelectOptions } from "hooks/common";
import { FileField, PhoneField, SelectField, TextField } from "components/Form";
import "./AssociationForm.scss";
import Association from "entities/Association";
import AssociationFormValidator from "validators/AssociationFormValidator";
import { ReactComponent as Info } from "images/info.svg";
import { getAssociationDetail } from "redux/Associations/AssociationsActions";
import { omitEmptyPropsDeep } from "lib/utils/Utils";
import adminAssociationsService from "services/AssociationsService";
import { useStatesQuery } from "hooks/business/directory/query";
import { WarningDialog } from "../../../../components/dialogs";

function AssociationForm(props) {
  const dispatch = useDispatch();

  const { associationId, onSaveSuccess, onCancel, isEdit } = props;
  const { fields, validate, isChanged, clearFields, changeField, changeFields, errors } = useForm(
    "AssociationForm",
    Association,
    AssociationFormValidator,
  );

  const { data: states = [] } = useStatesQuery();

  const { AssociationDetail } = useSelector((state) => state.Associations);
  const { Scrollable, scroll } = useScrollable();
  const onScroll = useScrollToFormError(".AssociationForm", scroll);
  const [needValidation, setNeedValidation] = useState(false);
  const [isFetching, setIsFetching] = useState(false);
  const scrollableStyles = { flex: 1 };
  const stateOptions = useSelectOptions(states, { textProp: "label" });
  const [errorMessage, setErrorMessage] = useState("");
  const [showErrorDialog, setShowErrorDialog] = useState(false);

  const webSiteHint = {
    placement: "top",
    target: "web-site-hint",
    render: () => (
      <ul className="AssociationForm-TooltipBody">
        <li>The maximum file size for uploads is 1 MB</li>
        <li>Only image files (JPG, GIF, PNG) are allowed</li>
        <li>Recommended aspect ratio is 3:1</li>
        <li>Recommended image resolution is 42x147</li>
      </ul>
    ),
  };
  const cellPhoneHint = {
    placement: "top",
    target: "cell-phone-hint",
    render: () => (
      <ul className="AssociationForm-TooltipBody">
        <li>Use digits only and "+" before country code. </li>
        <li>Otherwise no spaces, dashes, or special symbols allowed.</li>
      </ul>
    ),
  };
  const companyIdHint = {
    placement: "top",
    target: "company-Id-hint",
    render: () => (
      <ul className="AssociationForm-TooltipBody">
        <li>Digits only allowed</li>
      </ul>
    ),
  };

  useEffect(() => {
    setIsFetching(true);
    if (isEdit) {
      dispatch(getAssociationDetail(associationId));
    }
  }, [associationId]);

  useEffect(() => {
    setIsFetching(false);
    init();
  }, [AssociationDetail, changeFields]);

  const init = () => {
    if (AssociationDetail) {
      let data = omitEmptyPropsDeep(AssociationDetail);
      changeFields(
        {
          ...data,
          logo: data.logo,
          logoPic: null,
        },
        true,
      );
    }
  };

  const validationOptions = useMemo(() => {
    return {};
  }, [fields]);

  const onChangeLogoField = useCallback(
    (name, value) => {
      if (value) {
        changeField(name, value);
      } else {
        changeFields({ logoPic: null, logoPicName: "" });
      }
    },
    [changeField, changeFields],
  );

  function validateIf() {
    if (needValidation) {
      validate(validationOptions)
        .then(() => setNeedValidation(false))
        .catch(() => setNeedValidation(true));
    }
  }

  useEffect(() => {
    validateIf();
  }, [needValidation, onScroll, validate, validationOptions]);

  const onFormSubmit = async (e) => {
    e.preventDefault();
    setIsFetching(true);
    validate()
      .then(() => {
        const params = fields.toJS();
        adminAssociationsService
          .AddAssociation(params)
          .then((res) => {
            if (res.success) {
              onSaveSuccess();
              setNeedValidation(false);
            }
          })
          .catch((e) => {
            setShowErrorDialog(true);
            setErrorMessage(e.message);
          });
      })
      .catch(() => {
        onScroll();
        setNeedValidation(true);
      })
      .finally(() => {
        setIsFetching(false);
      });
  };
  return (
    <>
      <Form className={"AssociationForm"} onSubmit={onFormSubmit}>
        {isFetching && <Loader style={{ position: "fixed" }} hasBackdrop />}
        <Scrollable style={scrollableStyles}>
          <div className="AssociationForm-Section">
            <div className="AssociationForm-SectionTitle">General Data</div>
            <Row>
              <Col md={8}>
                <TextField
                  type="text"
                  name="name"
                  value={fields.name}
                  label="Association Name*"
                  maxLength={256}
                  className="AssociationtForm-TextField"
                  errorText={errors.name}
                  isDisabled={!fields.isActive}
                  onChange={changeField}
                />
              </Col>
              <Col md={4}>
                <TextField
                  type="text"
                  name="website"
                  value={fields.website}
                  label="Web Site*"
                  maxLength={256}
                  className="AssociationtForm-TextField"
                  errorText={errors.website}
                  isDisabled={!fields.isActive}
                  onChange={changeField}
                  renderLabelIcon={() => <Info id="web-site-hint" className="AssociationForm-InfoIcon" />}
                  tooltip={webSiteHint}
                />
              </Col>
              <Col md={4}>
                <TextField
                  type="text"
                  name="companyId"
                  value={fields.companyId}
                  label="Company ID*"
                  maxLength={256}
                  className="AssociationtForm-TextField"
                  errorText={errors.companyId}
                  isDisabled={!fields.isActive || isEdit}
                  onChange={changeField}
                  renderLabelIcon={() => <Info id="company-Id-hint" className="AssociationForm-InfoIcon" />}
                  tooltip={companyIdHint}
                />
              </Col>
              <Col md={4}>
                <TextField
                  type="text"
                  name="email"
                  value={fields.email}
                  label="Email*"
                  maxLength={256}
                  className="AssociationtForm-TextField"
                  errorText={errors.email}
                  isDisabled={!fields.isActive}
                  onChange={changeField}
                />
              </Col>
              <Col md={4}>
                <PhoneField
                  name={"phone"}
                  value={fields.phone}
                  label="Phone*"
                  className="AssociationForm-PhoneField"
                  errorText={errors.phone}
                  isDisabled={!fields.isActive}
                  onChange={changeField}
                  renderLabelIcon={() => <Info id="cell-phone-hint" className="AssociationForm-InfoIcon" />}
                  tooltip={cellPhoneHint}
                />
              </Col>
            </Row>
            <div className="AssociationForm-SectionTitle">Address</div>
            <Row>
              <Col md={4}>
                <TextField
                  type="text"
                  name="street"
                  value={fields.street}
                  label="Street*"
                  maxLength={256}
                  className="AssociationtForm-TextField"
                  errorText={errors.street}
                  isDisabled={!fields.isActive}
                  onChange={changeField}
                />
              </Col>
              <Col md={4}>
                <TextField
                  type="text"
                  name="city"
                  value={fields.city}
                  label="City*"
                  maxLength={256}
                  className="AssociationtForm-TextField"
                  errorText={errors.city}
                  isDisabled={!fields.isActive}
                  onChange={changeField}
                />
              </Col>
              <Col lg={4} md={4}>
                <SelectField
                  name="state"
                  value={Number(fields.state)}
                  label="State*"
                  options={stateOptions}
                  placeholder="Select"
                  className="AssociationtForm-TextField"
                  isMultiple={false}
                  errorText={errors.state}
                  onChange={changeField}
                />
              </Col>
              <Col md={4}>
                <TextField
                  type="number"
                  name="zipCode"
                  value={fields.zipCode}
                  label="Zip Code*"
                  maxLength={5}
                  maxDigits={99999}
                  className="AssociationtForm-TextField"
                  errorText={errors.zipCode}
                  onChange={changeField}
                />
              </Col>
            </Row>
            <div className="AssociationForm-SectionTitle">Association Logo</div>
            <Row>
              <Col lg={8} md={6}>
                <FileField
                  hasHint
                  name="logoPic"
                  value={fields?.logoPic ? fields?.logoPic?.name : AssociationDetail.logo}
                  label="Select file"
                  renderLabelIcon={() => <Info id="Logo-Hint" className="CommunityForm-LabelIcon" />}
                  tooltip={{
                    target: "Logo-Hint",
                    boundariesElement: document.body,
                    text: `The maximum file size for uploads is 1 MB,
                         Only image files (JPG,JPEG, GIF, PNG) are allowed,
                         Recommended aspect ratio is 8:1
                         `,
                  }}
                  className="CommunityForm-FileField"
                  errorText={errors.logo || errors.logoPic?.size || errors.logoPic?.type}
                  hintText="Supported file types: JPG, PNG, GIF | Max 1 mb"
                  onChange={onChangeLogoField}
                />
              </Col>
            </Row>
          </div>
        </Scrollable>
        <div className="AssociationForm-Buttons">
          <Button
            outline
            color="success"
            disabled={isFetching}
            onClick={() => {
              onCancel(isChanged);
            }}
          >
            Cancel
          </Button>

          <Button color="success">Save</Button>
        </div>
      </Form>
      {showErrorDialog && (
        <WarningDialog
          isOpen
          title={errorMessage}
          buttons={[
            {
              text: "Close",
              onClick: () => {
                setShowErrorDialog(false);
                setErrorMessage("");
              },
            },
          ]}
        />
      )}
    </>
  );
}

export default AssociationForm;
