import React, { memo, useCallback, useEffect, useMemo } from "react";

import { Col, Row } from "reactstrap";

import { first, isEmpty } from "underscore";

import { SelectField } from "components/Form";

import { useCustomFormFieldChange, useSelectOptions } from "hooks/common";

import { useContactsQuery } from "hooks/business/conversations";

import BaseSection from "../BaseSection/BaseSection";

function OtherContactsSection({ name, fields, excludedIds, errors = {}, onChangeField }) {
  const organizationIds = useMemo(() => fields.organizationIds.toJS(), [fields.organizationIds]);
  const communityIds = useMemo(() => fields.communityIds.toJS(), [fields.communityIds]);

  const { data: contacts, isFetching: isFetchingContacts } = useContactsQuery(
    {
      communityIds,
      organizationIds,
    },
    {
      enabled: !fields.communityIds.isEmpty() && !fields.organizationIds.isEmpty(),
      onSuccess: (data) => {
        if (data.length === 1) {
          onChangeField(`${name}.ids`, [first(data).id]);
        }
      },
    },
  );

  const filteredContacts = useMemo(() => {
    return contacts?.filter((o) => !excludedIds.includes(o.id));
  }, [contacts, excludedIds]);

  const ids = useMemo(() => fields.ids.toJS(), [fields.ids]);
  const contactOptions = useSelectOptions(filteredContacts, { textProp: "name" });

  const onChangeBaseField = useCallback(
    (...args) => {
      onChangeField(...args);
      onChangeField(`${name}.ids`, []);
    },
    [name, onChangeField],
  );

  const { changeSelectField } = useCustomFormFieldChange(onChangeField);

  useEffect(() => {
    if (contactOptions?.length === 1 && fields.ids.isEmpty()) {
      onChangeField(`${name}.ids`, [first(contactOptions).id]);
    }
  }, [name, fields.ids, contactOptions, onChangeField]);

  return (
    <div className="AddToGroupConversationForm-Section">
      <div className="AddToGroupConversationForm-SectionTitle">Contacts</div>

      <Row>
        <BaseSection
          name={name}
          fields={fields}
          errors={errors}
          params={{ withAccessibleContacts: true }}
          onChangeField={onChangeBaseField}
        />
      </Row>

      <Row>
        <Col>
          <SelectField
            name={`${name}.ids`}
            value={ids}
            hasTags
            isMultiple
            hasSearchBox
            options={contactOptions}
            label="Contacts"
            placeholder="Select contacts"
            className="LabOrderForm-SelectField"
            isDisabled={fields.communityIds.isEmpty() || isFetchingContacts || isEmpty(contactOptions)}
            onChange={(name, value) => {
              changeSelectField(name, value);

              const chatUserIds = filteredContacts.filter((m) => value.includes(m.id)).map((m) => m.chatUserId);

              changeSelectField("contacts.chatUserIds", chatUserIds);
            }}
            errorText={errors.ids}
          />
        </Col>
      </Row>
    </div>
  );
}

export default memo(OtherContactsSection);
