import React from "react";
import { Col, Row } from "reactstrap";
import { RadioGroupField, TextField } from "components/Form";

export default function ShareThroughSection({ fields, errors, onChangeField, isAssociation }) {
  const CHANNEL_OPTIONS = [
    { label: "Secure Email", value: "EMAIL" },
    { label: "FAX", value: "FAX" },
  ];

  return (
    <div className="ReferralRequestForm-Section">
      {/*<div className="ReferralRequestForm-SectionTitle"></div>*/}
      <Row>
        <Col md={12} style={{ display: "flex", alignItems: "center" }}>
          <RadioGroupField
            title="Share referral Through*"
            name="marketplace.sharedChannel"
            value={fields.marketplace.sharedChannel}
            options={CHANNEL_OPTIONS}
            onChange={onChangeField}
            className="ReferralRequestForm-RadioGroupField"
            errorText={errors.marketplace?.sharedChannel}
          />
        </Col>
      </Row>
      {fields.marketplace.sharedChannel === "FAX" && (
        <Row>
          <Col md={4}>
            <TextField
              type="text"
              label="Fax*"
              name="marketplace.sharedFax"
              value={fields.marketplace.sharedFax}
              onChange={onChangeField}
              placeholder="Fax"
              maxLength={16}
              errorText={errors.marketplace?.sharedFax}
              className="ReferralRequestForm-TextField"
            />
          </Col>
          <Col md={4}>
            <TextField
              type="text"
              label="Phone #*"
              name="marketplace.sharedPhone"
              value={fields.marketplace.sharedPhone}
              onChange={onChangeField}
              placeholder="Phone"
              maxLength={16}
              errorText={errors.marketplace?.sharedPhone}
              className="ReferralRequestForm-TextField"
            />
          </Col>
        </Row>
      )}
      {fields.marketplace.sharedChannel === "FAX" && (
        <Row>
          <Col>
            <TextField
              type="textarea"
              label="FAX Cover Sheet Comment"
              name="marketplace.sharedFaxComment"
              value={fields.marketplace.sharedFaxComment}
              onChange={onChangeField}
              placeholder="Comment"
              numberOfRows={3}
              maxLength={1500}
              errorText={errors.marketplace?.sharedFaxComment}
              className="ReferralRequestForm-TextField"
            />
          </Col>
        </Row>
      )}
    </div>
  );
}
