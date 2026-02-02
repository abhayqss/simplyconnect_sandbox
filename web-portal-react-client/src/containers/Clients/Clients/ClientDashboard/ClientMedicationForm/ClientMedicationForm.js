import { ErrorViewer, Loader, Scrollable } from "../../../../../components";
import React, { useCallback, useEffect, useMemo, useState } from "react";
import { Button, Col, Form, Row } from "reactstrap";
import { DateField, SelectField, TextField } from "../../../../../components/Form";
import { useForm, useScrollable, useScrollToFormError } from "../../../../../hooks/common";
import ClientMedication from "entities/ClientMedication";
import ClientMedicationFormSchemeValidator from "validators/ClientMedicationFormSchemeValidator";
import { getDateTime } from "lib/utils/DateUtils";
import "./ClientMedicationForm.scss";
import service from "services/ClientMedicationService";
import { WarningDialog } from "components/dialogs";
import { debounce } from "lodash";

import { SERVER_ERROR_CODES } from "lib/Constants";
import { isEmpty, omitEmptyPropsDeep } from "../../../../../lib/utils/Utils";

function isIgnoredError(e = {}) {
  return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE;
}
const ClientMedicationForm = (props) => {
  const { onSubmitSuccess, isEditing, onCancel, medicationId, clientId } = props;
  const [medicationData, setMedicationData] = useState();
  const scrollableStyles = { flex: 1 };
  const [isFetching, setIsFetching] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [showErrorDialog, setShowErrorDialog] = useState(false);
  const [searchMedicationName, setSearchMedicationName] = useState();
  const [searchMedicationData, setSearchMedicationData] = useState([]);
  const [medicationOptions, setMedicationOptions] = useState([]);
  const [needValidation, setNeedValidation] = useState(false);
  const [NDCOptions, setNDCOptions] = useState([]);
  const [isNDCDisabled, setIsNDCDisabled] = useState(false);

  const { fields, errors, validate, isChanged, clearFields, changeField, changeFields } = useForm(
    "ClientMedicationFormEditor",
    ClientMedication,
    ClientMedicationFormSchemeValidator,
  );

  function init() {
    if (medicationData) {
      let data = omitEmptyPropsDeep(medicationData);
      changeFields(
        {
          ...data,
          status: data.statusName,
        },
        true,
      );
    }
  }

  useEffect(() => {
    init();
  }, [medicationData, changeFields]);

  const { Scrollable, scroll } = useScrollable();
  const onScroll = useScrollToFormError(".ClientMedicationForm", scroll);
  const statusOptions = [
    {
      value: "ACTIVE",
      text: "Active",
    },
    {
      value: "COMPLETED",
      text: "Inactive",
    },
    {
      value: "UNKNOWN",
      text: "Unknown",
    },
  ];
  useEffect(() => {
    if (isEditing) {
      getMedicationDetail(medicationId);
    }
  }, [isEditing, medicationId]);

  const getMedicationDetail = (id) => {
    setIsFetching(true);
    service
      .findById(id, { clientId })
      .then((res) => {
        setMedicationData(res);
        setSearchMedicationName(res.name);
        setIsFetching(false);
      })
      .catch((e) => {
        setIsFetching(false);
        setShowErrorDialog(true);
        setErrorMessage(e);
      });
  };

  const onChangeMedicationName = (name, value) => {
    changeField(name, value);
    changeField("nds", "");
    if (value) {
      getNdcOptions(value);
    }
  };

  const getSearchMedication = (value) => {
    setIsFetching(true);
    if (value)
      service
        .fetchAllMedicationList({ name: value })
        .then((res) => {
          setSearchMedicationData(res);
          const data = res.map((item) => {
            return { value: item?.mediSpanId, text: item?.name };
          });
          setMedicationOptions(data);
          if (isEditing) {
            changeField("name", data[0].value);
            getNdcOptions(data[0].value);
          }
          setIsFetching(false);
        })
        .catch((e) => {
          setIsFetching(false);
          setMedicationOptions([]);
          setShowErrorDialog(true);
          setErrorMessage(e);
        });
  };

  const getNdcOptions = (id) => {
    service
      .fetchNDCForMedication(id)
      .then((res) => {
        if (res?.ndcCodes?.length > 0) {
          const data = res?.ndcCodes.map((item) => {
            return {
              value: item,
              text: item,
            };
          });
          setNDCOptions(data);
          changeField("ndc", data[0].value);
          if (data.length === 1) {
            setIsNDCDisabled(true);
          } else {
            setIsNDCDisabled(false);
          }
        } else {
          setNDCOptions([
            {
              value: "N/A",
              text: "N/A",
            },
          ]);
          changeField("ndc", "N/A");
          setIsNDCDisabled(true);
        }
      })
      .catch((e) => {
        setShowErrorDialog(true);
        setErrorMessage(e);
        setIsFetching([
          {
            value: "N/A",
            text: "N/A",
          },
        ]);
      });
  };
  const debouncedSetSearchMedicationName = useMemo(() => debounce((value) => setSearchMedicationName(value), 500), []);

  const onChangeMedicationSearch = (name, value) => {
    debouncedSetSearchMedicationName(value);
  };

  useEffect(() => {
    if (searchMedicationName) {
      getSearchMedication(searchMedicationName);
    }
  }, [searchMedicationName]);
  const clearMedicationSearch = (name, value) => {
    setSearchMedicationName("");
    setMedicationOptions([]);
    changeField("ndc", "");
    setIsNDCDisabled(true);
    setNDCOptions([]);
  };

  const onChangeDateField = useCallback(
    (name, value) => {
      changeField(name, value ? value.getTime() : null, false);
    },
    [changeField],
  );

  const onFormSubmit = (e) => {
    setIsFetching(true);
    validate()
      .then(() => {
        const params = fields.toJS();
        const body = isEditing
          ? { ...params, mediSpanId: fields.name, id: medicationId }
          : { ...params, mediSpanId: fields.name };
        service
          .addMedicationForClient({
            clientId,
            body,
          })
          .then((res) => {
            onSubmitSuccess();
            setNeedValidation(false);
            setIsFetching(false);
          })
          .catch((e) => {
            setShowErrorDialog(true);
            setErrorMessage(e);
            setIsFetching(false);
          });
      })
      .catch((e) => {
        onScroll();
        setIsFetching(false);
        setNeedValidation(true);
      });
  };
  return (
    <>
      <Form className="ClientMedicationForm" onSubmit={onFormSubmit}>
        {isFetching && <Loader style={{ position: "fixed" }} hasBackdrop />}

        <Scrollable style={scrollableStyles}>
          <Row>
            <Col md={6} lg={6}>
              <SelectField
                hasSearchBox
                name="name"
                value={fields.name}
                options={medicationOptions}
                label="Medication Name/Strength *"
                className="ClientMedication-SelectField"
                errorText={errors.name}
                isDisabled={isFetching}
                placeholder={"Search"}
                onChangeSearchText={onChangeMedicationSearch}
                onChange={onChangeMedicationName}
                onClearSearchText={clearMedicationSearch}
              />
            </Col>
            <Col md={6} lg={6}>
              <SelectField
                name="ndc"
                value={fields.ndc}
                options={NDCOptions}
                label="NDC *"
                className="ClientMedication-SelectField"
                errorText={errors.ndc}
                onChange={changeField}
                isDisabled={isNDCDisabled}
              />
            </Col>
          </Row>
          <Row>
            <Col md={6} lg={6}>
              <SelectField
                name="status"
                value={fields.status}
                options={statusOptions}
                label="Status *"
                className="ClientMedication-SelectField"
                errorText={errors.status}
                placeholder={"Select"}
                onChange={changeField}
              />
            </Col>
            <Col md={3} lg={3}>
              <TextField
                type={"text"}
                name="frequency"
                value={fields.frequency}
                options={medicationOptions}
                label="Frequency"
                className="ClientMedication-SelectField"
                errorText={errors.frequency}
                onChange={changeField}
              />
            </Col>
            <Col md={3} lg={3}>
              <TextField
                type={"text"}
                name="directions"
                value={fields.directions}
                options={medicationOptions}
                label="Directions"
                className="ClientMedication-SelectField"
                errorText={errors.directions}
                onChange={changeField}
              />
            </Col>
          </Row>

          <Row>
            <Col lg={6} md={6}>
              <DateField
                name="startedDate"
                value={fields.startedDate ? getDateTime(fields.startedDate) : null}
                dateFormat="MM/dd/yyyy hh:mm a"
                label="Started"
                placeholder="Select date"
                className="ClientFilter-DateField"
                onChange={onChangeDateField}
              />
            </Col>
            <Col lg={6} md={6}>
              <DateField
                name="stoppedDate"
                value={fields.stoppedDate ? getDateTime(fields.stoppedDate) : null}
                dateFormat="MM/dd/yyyy hh:mm a"
                label="Stopped"
                placeholder="Select date"
                className="ClientFilter-DateField"
                onChange={onChangeDateField}
              />
            </Col>
          </Row>
          <Row>
            <Col md={6} lg={6}>
              <TextField
                name="dosageQuantity"
                value={fields.dosageQuantity}
                options={medicationOptions}
                label="Dosage Quantity"
                className="ClientMedication-SelectField"
                errorText={errors.dosageQuantity}
                onChange={changeField}
              />
            </Col>
            <Col md={6} lg={6}>
              <TextField
                type="text"
                name="indicatedFor"
                value={fields.indicatedFor}
                options={medicationOptions}
                label="Indicated for"
                className="ClientMedication-SelectField"
                errorText={errors.indicatedFor}
                onChange={changeField}
              />
            </Col>
          </Row>
          <Row>
            <Col>
              <Col md={12} lg={12}>
                <TextField
                  type="text"
                  name="comment"
                  value={fields.comment}
                  label="Comment "
                  className="ClientMedication-SelectField"
                  errorText={errors.comment}
                  onChange={changeField}
                />
              </Col>
            </Col>
          </Row>
        </Scrollable>

        <div className="ClientMedication-Buttons">
          <Button outline color="success" disabled={isFetching} onClick={onCancel}>
            Cancel
          </Button>

          <Button color="success" onClick={onFormSubmit}>
            Confirm
          </Button>
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
};

export default ClientMedicationForm;
