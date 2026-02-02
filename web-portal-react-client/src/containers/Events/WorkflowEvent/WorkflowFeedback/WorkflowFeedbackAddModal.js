import { Modal } from "../../../../components";
import { Button, Col } from "reactstrap";
import React, { useEffect, useState } from "react";
import "./WorkflowFeedback.scss";
import TextField from "../../../../components/Form/TextField/TextField";
import { RadioGroupField } from "../../../../components/Form";

const WorkflowFeedbackAddModal = (props) => {
  const { isOpen, close, confirmData, setConfirmData, question } = props;

  const [title, setTitle] = useState("Add");

  const [feedbackType, setFeedbackType] = useState("All");
  const [content, setContent] = useState("");

  const [canConfirm, setCanConfirm] = useState(false);

  const options = [
    { value: "All", label: `Tag + content` },
    { value: "Only", label: "Only tag" },
  ];

  const onChangeField = (name, value) => {
    setFeedbackType(value);
  };

  const changeContent = (name, value) => {
    setContent(value);
  };

  useEffect(() => {
    if (feedbackType === "All") {
      if (content === "") {
        setCanConfirm(false);
      } else {
        setCanConfirm(true);
      }
    } else {
      setCanConfirm(true);
    }
  }, [feedbackType, content]);

  useEffect(() => {
    let foundIndex = confirmData.findIndex((item) => item.question === question);
    if (foundIndex !== -1) {
      setTitle("Edit");
      setFeedbackType(confirmData[foundIndex].feedbackType);
      setContent(confirmData[foundIndex].content);
    } else {
      setTitle("Add");
      setFeedbackType("All");
      setContent("");
    }
  }, []);

  function updateArray(question, feedbackType, content) {
    let foundIndex = confirmData.findIndex((item) => item.question === question);

    if (foundIndex !== -1) {
      // 如果找到了相同question，替换该项
      confirmData[foundIndex] = { question, feedbackType, content };
    } else {
      // 如果没有找到相同question，新增至数组
      confirmData.push({ question, feedbackType, content });
    }
    setConfirmData([...confirmData]);
  }

  const confirm = () => {
    updateArray(question, feedbackType, content);

    close();
  };

  return (
    <>
      <Modal
        isOpen={isOpen}
        hasCloseBtn={true}
        onClose={close}
        title={`${title} Feedback`}
        className={"workflowFeedbackAddModal"}
        bodyClassName={"workflowFeedbackAddModalBody"}
        renderFooter={() => (
          <div>
            <Button outline color="success" className="AssessmentViewer-NextBtn" onClick={close}>
              Cancel
            </Button>

            <Button color="success" disabled={!canConfirm} onClick={confirm}>
              Confirm
            </Button>
          </div>
        )}
      >
        <RadioGroupField
          label={"Feedback Type"}
          view="row"
          name="sharingOption"
          className="DocumentForm-RadioGroupField"
          selected={feedbackType}
          // hasError={!!errors.sharingOption}
          // errorText={errors.sharingOption}
          options={options}
          onChange={onChangeField}
        />

        <div>
          <Col md={12}>
            <TextField
              type="textarea"
              name="description"
              value={content}
              label="Feedback content"
              className="workflowAddFeedback-TextArea"
              numberOfRows={8}
              onChange={changeContent}
            />
          </Col>
        </div>
      </Modal>
    </>
  );
};

export default WorkflowFeedbackAddModal;
