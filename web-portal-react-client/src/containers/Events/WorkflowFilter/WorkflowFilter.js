import "./WorkflowFilter.scss";
import React, { useMemo, useState } from "react";
import { Button, Col, Row } from "reactstrap";
import SelectField from "components/Form/SelectField/SelectField";
import { map } from "underscore";
import DateField from "../../../components/Form/DateField/DateField";
import { TextField } from "../../../components/Form";
import { set } from "msw";

function valueTextMapper({ id, title, fullName }) {
  return { value: id, text: title || fullName };
}

const statusOptions = [
  {
    value: "SUBMITTED",
    text: "Submitted",
  },
  {
    value: "INPROCESS",
    text: "In Process",
  },
  {
    value: "FEEDBACK",
    text: "Feedback",
  },
  {
    value: "APPROVED",
    text: "Approved",
  },
];

const WorkflowFilter = (props) => {
  const { communityIds, organizationId, setWorkflowFilterData } = props;
  const [workflowName, setWorkflowName] = useState("");
  const [clientName, setClientName] = useState("");
  const [searchCommunityIds, setSearchCommunityIds] = useState([]);
  const [submissionDate, setSubmissionDate] = useState(null); //getEndOfDayTime(Date.now())
  const [status, setStatus] = useState();

  const [communityList, setCommunityList] = useState([]);

  const maxDate = () => {
    return Date.now();
  };

  const mappedCommunities = useMemo(() => map(communityList, valueTextMapper), [communityList]);

  const onChangeWorkflowName = (name, value) => {
    setWorkflowName(value);
  };
  const onChangeClientName = (name, value) => {
    setClientName(value);
  };
  const onChangeCommunityIds = (name, value) => {
    setSearchCommunityIds(value);
  };
  const onChangeDate = (name, value) => {
    if (value) {
      setSubmissionDate(+new Date(value));
    } else {
      setSubmissionDate(null);
    }
  };
  const onChangeStatus = (name, value) => {
    setStatus(value);
  };

  const onReset = () => {
    setStatus(null);
    setClientName(null);
    setWorkflowName(null);
    setSearchCommunityIds([]);
    setWorkflowFilterData({});
    setSubmissionDate(null);
  };
  const onApply = () => {
    setWorkflowFilterData({
      status: status,
      clientName: clientName,
      workflowName: workflowName,
      submissionDate: submissionDate,
    });
  };
  return (
    <>
      <div className={"Workflow-Filter"}>
        <Row>
          <Col>
            <TextField
              type="text"
              name="clientName"
              value={clientName}
              label="Client"
              placeholder="Search by client name"
              onChange={onChangeClientName}
            />
          </Col>
          <Col>
            <TextField
              type="text"
              name="workflowName"
              value={workflowName}
              label="Workflow Name"
              placeholder="Search by workflow name"
              onChange={onChangeWorkflowName}
            />
          </Col>
          {/* <Col>
            <SelectField
              label="Community"
              name="commuityIds"
              hasKeyboardSearch
              isMultiple={true}
              hasKeyboardSearchText
              value={communityIds}
              placeholder="Select"
              options={mappedCommunities}
              onChange={onChangeCommunityIds}
            />
          </Col>*/}
          <Col xl={4} lg={4} md={4}>
            <DateField
              name="submissionDate"
              dateFormat="MM/dd/yyyy"
              label="Submission date"
              value={submissionDate}
              maxDate={Date.now()}
              onChange={onChangeDate}
            />
          </Col>
        </Row>
        <Row>
          <Col xl={4} lg={4} md={4}>
            <SelectField
              label="Status"
              name="status"
              isMultiple={true}
              hasAllOption={true}
              value={status}
              placeholder="Select"
              options={statusOptions}
              onChange={onChangeStatus}
            />
          </Col>
          <Col xl={4} lg={4} md={4}>
            <Button data-testid="clear-btn" outline color="success" onClick={onReset} className="Workflow-Btn">
              Clear
            </Button>
            <Button data-testid="apply-btn" color="success" onClick={onApply} className="Workflow-Btn">
              Apply
            </Button>
          </Col>
        </Row>
      </div>
    </>
  );
};

export default WorkflowFilter;
