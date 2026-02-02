import React, { memo, useCallback, useEffect, useMemo, useState } from 'react'

import { Col, Form, Row } from 'reactstrap'

import {compact, map, sortBy} from 'underscore'

import { useCustomFormFieldChange, useForm, useScrollable, useScrollToFormError } from 'hooks/common'

import { useAuthUser } from 'hooks/common/redux'

import { useClientQuery } from 'hooks/business/client/queries'

import { useContactQuery } from 'hooks/business/admin/contact'

import { useDocumentTemplatesByFolderQuery } from 'hooks/business/documents'

import { useESignContactsQuery, useESignRequestSubmit, } from 'hooks/business/documents/e-sign'

import { useESignDocumentTemplateSchemesQuery } from 'hooks/business/documents/e-sign/template'


import { useDocumentSignatureRequestNotificationMethodsQuery } from 'hooks/business/directory/query'

import { ErrorViewer, Loader } from 'components'

import {
  DateField,
  PhoneField,
  RadioGroupField,
  SelectField,
  SelectFieldWithDirectory,
  TextField
} from 'components/Form'

import RequestSignatureProgress from '../RequestSignatureProgress/RequestSignatureProgress'

import Entity from 'entities/e-sign/ESignRequest'
import Validator from 'validators/SignatureRequestFormValidator'

import { E_SIGN_DOCUMENT_TEMPLATE_STATUSES } from 'lib/Constants'

import { DateUtils as DU, interpolate, isEmpty } from 'lib/utils/Utils'

import { first, isNotEmpty } from 'lib/utils/ArrayUtils'

import './SignatureRequestForm.scss'
import SelectDirectory from "../../../../../components/Form/SelectDirectory/SelectDirectory";
import SelectFieldTree from "../../../../../components/Form/SelectFieldTree/SelectField";

const YESTERDAY = DU.startOf(
  DU.add(Date.now(), 1, 'day'), 'day'
).getTime()

const { format } = DU

const DATE_FORMAT = DU.formats.americanMediumDate

const DEFAULT_MESSAGE_TMPL = (
  'Please sign/review the document(s) by $0.\n' +
  'Please reach out to me if you have any questions.\n' +
  '$1. $2'
)

function hasLinkedAccount(client) {
  return client?.associatedContact.id ?? false
}

function getDefaultMessage(...args) {
  return interpolate(DEFAULT_MESSAGE_TMPL, ...args)
}

function getData(fields) {
  return fields.toJS()
}

function get7thDayAfterToday() {
  return DU.add(Date.now(), 7, 'day').getTime()
}

function valueTextMapper({ id, name, title }) {
  return { value: id ?? name, text: title ?? name }
}

const scrollableStyles = { flex: 1 }

const recipientOptions = [
  { value: 'CLIENT', label: 'Client' },
  { value: 'SELF', label: 'Self' },
  { value: 'STAFF', label: 'Staff/Family Member' }
]

function SignatureRequestForm(
  {
    clientId,
    communityIds,
    organizationId,
    defaultData,
    documentId,
    templateId: defaultTemplateId,

    children,

    onCancel,
    onSubmitSuccess
  }
) {
  const [error, setError] = useState(false)
  const [isFetching, setFetching] = useState(false)
  const [needValidation, setNeedValidation] = useState(false)
  const [lastSelectedContact, setLastSelectedContact] = useState(null)

  const user = useAuthUser()

  const {
    fields,
    errors,
    isValid,
    validate,
    isChanged,
    changeField,
    changeFields
  } = useForm('SignatureRequest', Entity, Validator)

  const data = useMemo(() => fields.toJS(), [fields])

  const {
    changeDateField,
    changeSelectField,
  } = useCustomFormFieldChange(changeField)

  const {
    templateIds,
    recipientId,
    recipientType,
    expirationDate,
    message
  } = data

  const isSelfSelected = recipientType === 'SELF'
  const isStaffSelected = recipientType === 'STAFF'
  const isClientSelected = recipientType === 'CLIENT'

  const defaultMessage = useMemo(() => interpolate(
    DEFAULT_MESSAGE_TMPL,
    format(expirationDate, DATE_FORMAT),
    user.fullName,
    user.email ?? ''
  ), [user, expirationDate])

  const { Scrollable, scroll } = useScrollable()

  const onScroll = useScrollToFormError('.SignatureRequestForm', scroll)

  const {
    data: client
  } = useClientQuery({ clientId }, {
    enabled: Boolean(clientId)
  })

  const {
    data: contact
  } = useContactQuery({ contactId: recipientId }, {
    enabled: isStaffSelected && Boolean(recipientId)
  })

  const {
    data: contacts = []
  } = useESignContactsQuery({
    clientId, documentId
  }, { staleTime: 0 })

  const mappedContacts = useMemo(() => (
    contacts.map(valueTextMapper)
  ), [contacts])
  
  const {
    isFetching: isFetchingDocumentTemplates,
    data: templateTypes = []
  } = useDocumentTemplatesByFolderQuery({ communityIds }, {
    staleTime: 0,
    enabled: isNotEmpty(communityIds),
  })


  const mappedTemplateTreeTypes = useMemo(() => {
  if(templateTypes.length >0) {
  templateTypes.templates = templateTypes?.templates?.filter(template => template.statusName !== 'DRAFT');
// Loop through folders and filter templates in each
  templateTypes?.folders?.forEach(folder => {
    folder.templates = folder?.templates?.filter(template => template.statusName !== 'DRAFT');

    // Recursively filter subfolders
    if (folder.folders) {
      folder?.folders?.forEach(subfolder => {
        subfolder.templates = subfolder?.templates?.filter(template => template.statusName !== 'DRAFT');
      });
    }
  });
}
    return templateTypes;
  },[templateTypes]);

  const {
    data: notificationMethods = []
  } = useDocumentSignatureRequestNotificationMethodsQuery()

  const mappedNotificationMethods = useMemo(() => (
    notificationMethods
      .filter(o => o.name !== 'SIGN_NOW' || isClientSelected)
      .map(o => ({
        ...valueTextMapper(o),
        isDisabled: (
          o.name === 'CHAT'
          && (
            !user.areConversationsEnabled
            || (isClientSelected && !hasLinkedAccount(client))
          )
        )
      }))
  ), [user, client, isClientSelected, notificationMethods])

  const { mutateAsync: submit } = useESignRequestSubmit({
    onError: setError,
    onSuccess: onSubmitSuccess
  })

  useESignDocumentTemplateSchemesQuery({ templateIds, clientId }, {
    staleTime: 0,
    enabled: isNotEmpty(templateIds)
  })

  const validationOptions = useMemo(() => ({
    included: {
      isClientSelected,
      hasLinkedAccount: hasLinkedAccount(client)
    }
  }), [client, isClientSelected])

  function setDefaultData() {
    if (user && client && isEmpty(defaultData)) {
      const expirationDate = get7thDayAfterToday()

      const data = {
        expirationDate,
        recipientId: clientId,
        recipientType: 'CLIENT',
        recipientFullName: client.fullName,
        email: client.email,
        phone: client.cellPhone,
        message: getDefaultMessage(
          format(expirationDate, DATE_FORMAT),
          user.fullName,
          user.email ?? ''
        )
      }

      if (user.areConversationsEnabled && hasLinkedAccount(client)) {
        data.notificationMethod = 'CHAT'
      }

      changeFields(data, true)
    } else if (!isEmpty(defaultData)) {
      changeFields(defaultData)
    }
  }

  function tryToSubmit(e) {
    e.preventDefault()

    setFetching(true)

    validate(validationOptions)
      .then(async () => {
        await submit(getData(fields))
      })
      .catch(() => {
        onScroll()
        setFetching(false)
        setNeedValidation(true)
      })
  }

  function cancel() {
    onCancel(isChanged)
  }

  function validateIf() {
    if (needValidation) {
      validate(validationOptions)
        .then(() => setNeedValidation(false))
        .catch(() => setNeedValidation(true))
    }
  }

  function onChangeContact() {
    if (contact) {
      changeFields({
        email: contact.login,
        phone: contact.mobilePhone ?? contact.phone,
        recipientFullName: `${contact?.firstName} ${contact?.lastName}`
      })
      setLastSelectedContact(contact)
    }
  }

  function setDefaultTemplate() {
    if (defaultTemplateId) {
      changeField('templateIds', [defaultTemplateId])
    }
  /*  if (defaultTemplateId?.length === 1) {
      changeField('templateIds', [first(templateTypes).id])
    }*/
  }

  function clearNotificationMethod() {
    if (recipientType) {
      changeField('notificationMethod', null)
    }
  }

  const onChangeRecipientTypeField = useCallback((name, value) => {
    let email = client.email
    let phone = client.cellPhone
    let recipientId = clientId
    let recipientFullName = client.fullName

    if (value === 'SELF') {
      recipientId = user.id
      email = user.email
      phone = user.phone
      recipientFullName = user.fullName
    }

    if (value === 'STAFF') {
      recipientId = lastSelectedContact?.id

      recipientFullName = (
        `${lastSelectedContact?.firstName} ${lastSelectedContact?.lastName}`
      )

      email = (
        lastSelectedContact?.login
      )

      phone = (
        lastSelectedContact?.mobilePhone
        ?? lastSelectedContact?.phone
      )
    }

    changeFields({
      email,
      phone,
      recipientId,
      [name]: value,
      recipientFullName
    })
  }, [
    user,
    client,
    clientId,
    changeFields,
    lastSelectedContact
  ])

  const onChangeExpirationDate = useCallback((name, value) => {
    changeDateField(name, value)

    if (message === defaultMessage) {
      changeField('message', getDefaultMessage(
        format(value, DATE_FORMAT),
        user.fullName,
        user.email ?? ''
      ))
    }
  }, [
    user,
    message,
    changeField,
    defaultMessage,
    changeDateField
  ])

  useEffect(clearNotificationMethod, [recipientType, changeField])
  useEffect(onChangeContact, [contact, changeFields])
  useEffect(validateIf, [onScroll, validate, needValidation, validationOptions])
  useEffect(setDefaultData, [user, client, clientId, defaultData, changeFields])
  useEffect(setDefaultTemplate, [templateTypes, changeField, defaultTemplateId])

  return (
    <>
      <Form className="SignatureRequestForm" onSubmit={tryToSubmit}>
        {(isFetching || isFetchingDocumentTemplates) && (
          <Loader hasBackdrop/>
        )}

        <Scrollable style={scrollableStyles} className="SignatureRequestForm-Sections">
          <div className="SignatureRequestForm-Section">
            <RequestSignatureProgress
              className="SignatureRequestForm-Progress"
              hasSecondStep={!(documentId || defaultTemplateId)}
            />

            <Row>
              <Col md={6}>
              {/*  test 2 MultiSelectTree*/}
                <SelectFieldTree
                      name="templateIds"
                      label="Template*"
                      value={templateIds}
                      options={mappedTemplateTreeTypes}
                      onChange={changeField}
                      errorText={errors?.templateIds}
                      isMultiple
                  />
              </Col>

              <Col md={6}>
                <RadioGroupField
                  view="row"
                  name="recipientType"
                  title="Recipient*"
                  className="SignatureRequestForm-RadioGroupField"
                  selected={fields.recipientType}
                  errorText={errors.recipientType}
                  options={recipientOptions}
                  onChange={onChangeRecipientTypeField}
                />
              </Col>
            </Row>

            <Row>
              <Col md={6}>
                {isClientSelected && (
                  <TextField
                    isDisabled
                    name="recipientName"
                    value={client?.fullName}
                    label="Name*"
                    className="SignatureRequestForm-TextField"
                  />
                )}

                {isSelfSelected && (
                  <TextField
                    isDisabled
                    name="recipientName"
                    value={user?.fullName}
                    label="Name*"
                    className="SignatureRequestForm-TextField"
                  />
                )}

                {isStaffSelected && (
                  <SelectField
                    name="recipientId"
                    value={fields.recipientId}
                    label="Name*"
                    hasKeyboardSearch
                    hasKeyboardSearchText
                    className="SignatureRequestForm-SelectField"
                    errorText={errors.recipientId}
                    options={mappedContacts}
                    onChange={changeField}
                  />
                )}
              </Col>

              <Col md={6}>
                <DateField
                  name="expirationDate"
                  value={fields.expirationDate}
                  dateFormat="MM/dd/yyyy"
                  label="Expiration Date*"
                  placeholder="Select date"
                  minDate={YESTERDAY}
                  className="SignatureRequestForm-DateField"
                  errorText={errors?.expirationDate}
                  onChange={onChangeExpirationDate}
                />
              </Col>
            </Row>
            {!isSelfSelected && (
              <>
                <Row>
                  <Col md={6}>
                    <SelectField
                      name="notificationMethod"
                      value={fields.notificationMethod}
                      label="Notification Method*"
                      className="SignatureRequestForm-SelectField"
                      errorText={errors.notificationMethod}
                      options={mappedNotificationMethods}
                      onChange={changeSelectField}
                    />
                  </Col>

                  <Col md={6}>
                    {fields.notificationMethod === 'SMS' && (
                      <PhoneField
                        name="phone"
                        value={fields.phone ?? '+1'}
                        label="Phone #*"
                        autoFormat
                        alwaysDefaultMask
                        defaultMask="...-...-...."
                        placeholder="XXX-XXX-XXXX"
                        className="SignatureRequestForm-TextField"
                        errorText={errors.phone}
                        onChange={changeField}
                      />
                    )}

                    {fields.notificationMethod === 'EMAIL' && (
                      <TextField
                        name="email"
                        value={fields.email}
                        label="Email*"
                        className="SignatureRequestForm-TextField"
                        errorText={errors.email}
                        maxLength={318}
                        onChange={changeField}
                      />
                    )}
                  </Col>
                </Row>

                <Row>
                  <Col md={6}>
                    {fields.notificationMethod === 'EMAIL'
                      && isClientSelected
                      && !hasLinkedAccount(client)
                      && (
                        <PhoneField
                          name="phone"
                          value={fields.phone ?? '+1'}
                          label="Phone #*"
                          autoFormat
                          alwaysDefaultMask
                          defaultMask="...-...-...."
                          placeholder="XXX-XXX-XXXX"
                          className="SignatureRequestForm-TextField"
                          errorText={errors.phone}
                          onChange={changeField}
                        />
                      )
                    }
                  </Col>
                </Row>

                <Row>
                  <Col>
                    {(!isClientSelected || hasLinkedAccount(client)) && (
                      <TextField
                        type="textarea"
                        name="message"
                        value={fields.message}
                        label="Message"
                        className="SignatureRequestForm-TextAreaField"
                        errorText={errors.message}
                        maxLength={256}
                        numberOfRows={5}
                        onChange={changeField}
                      />
                    )}
                  </Col>
                </Row>
              </>
            )}
          </div>
        </Scrollable>

        <div className="SignatureRequestForm-Footer">
          {children?.({
            cancel,
            isValidToSubmit: isValid
          })}
        </div>
      </Form>

      {error && (
        <ErrorViewer
          isOpen
          error={error}
          onClose={() => setError(null)}
        />
      )}
    </>
  )
}

export default memo(SignatureRequestForm)
