import React, { memo, useCallback, useEffect, useMemo, useState } from 'react'

import { Form } from 'reactstrap'

import { chain, filter, find, findWhere, flatten, groupBy, map, pick, size } from 'underscore'

import { useCustomFormFieldChange, useForm, useScrollable, useScrollToFormError } from 'hooks/common'

import { useAuthUser } from 'hooks/common/redux'

import {
  useESignCommunitiesQuery,
  useESignOrganizationsQuery,
  useESignRequestSubmit,
} from 'hooks/business/documents/e-sign'

import { useESignDocumentTemplateSchemesQuery } from 'hooks/business/documents/e-sign/template'

import { useClientsQuery } from 'hooks/business/directory/query'

import { ErrorViewer, Loader } from 'components'

import { CLIENT_STATUSES, PAGINATION } from 'lib/Constants'

import RequestSignatureProgress from '../RequestSignatureProgress/RequestSignatureProgress'
import FormFields from './components/Fields/Fields'

import Validator from 'validators/BulkSignatureRequestFormValidator'
import Entity from 'entities/e-sign/ESignBulkRequest'

import { DateUtils as DU, interpolate, isEmpty, isInteger } from 'lib/utils/Utils'

import { first, isNotEmpty, isUnary } from 'lib/utils/ArrayUtils'

import './BulkSignatureRequestForm.scss'
import useDocumentTemplatesByFolderQuery from "hooks/business/documents/useDocumentTemplatesByFolderQuery";


const { format } = DU

const { MAX_SIZE } = PAGINATION

const { ACTIVE } = CLIENT_STATUSES

const DATE_FORMAT = DU.formats.americanMediumDate

const DEFAULT_MESSAGE_TMPL = (
  'Please sign/review the document(s) by $0.\n' +
  'Please reach out to me if you have any questions.\n' +
  '$1. $2'
)

function getDefaultMessage(...args) {
  return interpolate(DEFAULT_MESSAGE_TMPL, ...args)
}

function getData(fields) {
  const data = fields.toJS()

  return {
    ...pick(data, 'templateIds', 'message', 'organizationId', 'expirationDate'),
    clientIds: flatten(
      map(data.communities, ({ clientIds }) => clientIds)
    ),
    originalData: data,
    isMultipleSignature: true
  }
}

function get7thDayAfterToday() {
  return DU.add(Date.now(), 7, 'day').getTime()
}

const scrollableStyles = { flex: 1 }

const whetherMultiplePeopleNeedToSignOptions = [
  {
    value: false,
    text: 'Single signature',
  }, {
    value: true,
    text: 'Multiple signatures',
  }
]

function BulkSignatureRequestForm(
  {
    clients: defaultClients,
    defaultData,
    communityIds,
    organizationId: defaultOrganizationId,

    children,

    onCancel,
    onSubmitSuccess
  }
) {
  const [error, setError] = useState(false)
  const [isFetching, setFetching] = useState(false)
  const [needValidation, setNeedValidation] = useState(false)
  const [whetherMultiplePeopleNeedToSign, setWhetherMultiplePeopleNeedToSign] = useState(false);

  const user = useAuthUser()
  const {
    fields,
    errors,
    isValid,
    validate,
    isChanged,
    changeField,
    changeFields
  } = useForm('BulkSignatureRequest', Entity, Validator)

  const data = useMemo(() => fields.toJS(), [fields])

  const selectedCommunityIds = useMemo(() => map(data.communities, c => c.communityId), [data])
  const defaultClientIds = useMemo(() => map(defaultClients, c => c.id), [defaultClients])
  const [selectedClientsOptions, setSelectedClientsOptions] = useState([])
  const {
    data: organizations,
    isFetching: isFetchingOrganizations
  } = useESignOrganizationsQuery({}, {
    onSuccess: (organizations) => {
      if (size(organizations) === 1 && isEmpty(data.organizationId)) {
        changeField('organizationId', first(organizations).id)
      }
    },
    staleTime: 0
  })

  const {
    data: communities,
    isFetching: isFetchingCommunities
  } = useESignCommunitiesQuery(
    { organizationId: data.organizationId },
    {
      enabled: isInteger(data.organizationId),
      onSuccess: (communities) => {
        if (size(communities) === 1) {
          const community = first(communities)

          changeField('communities', [{
            communityId: community.id,
            clientIds: findWhere(data.communities, { communityId: community.id })?.clientIds || []
          }])
        }
      },
      staleTime: 0
    }
  )

  const {
    isFetching: isFetchingTemplates,
    data: templateTypes = []
  } = useDocumentTemplatesByFolderQuery({
    communityIds: selectedCommunityIds,
    isManuallyCreated: true
  }, {
    staleTime: 0,
    enabled: isNotEmpty(selectedCommunityIds),
  })

  const mappedTemplateTreeTypes = useMemo(() => {
    if (templateTypes.length > 0) {
      templateTypes.templates = templateTypes.templates.filter(template => template.statusName !== 'DRAFT');
      templateTypes.folders.forEach(folder => {
        folder.templates = folder.templates.filter(template => template.statusName !== 'DRAFT');
        if (folder.folders) {
          folder.folders.forEach(subfolder => {
            subfolder.templates = subfolder.templates.filter(template => template.statusName !== 'DRAFT');
          });
        }
      });
    }
    return templateTypes;
  }, [templateTypes]);

  const {
    data: clients
  } = useClientsQuery({
    size: MAX_SIZE,
    recordStatuses: [ACTIVE],
    communityIds: selectedCommunityIds
  }, {
    enabled: isNotEmpty(selectedCommunityIds),
    staleTime: 0
  })

  const groupedClients = useMemo(() => groupBy(clients, c => c.communityId), [clients])

  const {
    changeDateField
  } = useCustomFormFieldChange(changeField)

  const defaultMessage = useMemo(() => interpolate(
    DEFAULT_MESSAGE_TMPL,
    format(data.expirationDate, DATE_FORMAT),
    user.fullName,
    user.email ?? ''
  ), [user, data.expirationDate])

  const { Scrollable, scroll } = useScrollable()

  const onScroll = useScrollToFormError('.SignatureRequestForm', scroll)

  const { mutateAsync: submit } = useESignRequestSubmit({
    onError: setError,
    onSuccess: onSubmitSuccess
  })

  useESignDocumentTemplateSchemesQuery({
    templateIds: data.templateIds
  }, {
    staleTime: 0,
    enabled: isNotEmpty(data.templateIds)
  })

  const validationOptions = useMemo(() => ({
    included: {
      clientsWithoutPrimaryContact: chain(clients)
        .filter(c => !c.primaryContactTypeName)
        .pluck('id')
        .value()
    }
  }), [clients])

  function setDefaultData() {
    sessionStorage.setItem('whetherMultiplePeopleNeedToSign', false)
    changeField('whetherMultiplePeopleNeedToSign', false)

    if (user && isEmpty(defaultData?.originalData)) {
      const expirationDate = get7thDayAfterToday()

      const defaultData = {
        message: getDefaultMessage(
          format(expirationDate, DATE_FORMAT),
          user.fullName,
          user.email ?? ''
        ),
        expirationDate,
        organizationId: defaultOrganizationId
      }

      changeFields(defaultData, true)

      if (isNotEmpty(defaultClients)) {
        const communities = chain(defaultClients)
          .groupBy('communityId')
          .map((clients, communityId) => ({
            communityId: parseInt(communityId),
            clientIds: map(clients, c => c.id)
          }))
          .value()

        changeField('communities', communities)
      }
    } else if (!isEmpty(defaultData.originalData)) {
      changeFields(defaultData.originalData, true)
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


  const onChangeExpirationDate = useCallback((name, value) => {
    changeDateField(name, value)

    if (data.message === defaultMessage) {
      changeField('message', getDefaultMessage(
        format(value, DATE_FORMAT),
        user.fullName,
        user.email ?? ''
      ))
    }
  }, [
    user,
    data.message,
    changeField,
    defaultMessage,
    changeDateField
  ])

  const onChangeOrganization = useCallback((_, value) => {
    changeField('organizationId', value)
    changeField('templateIds', [])
    changeField('communities', [])
  }, [changeField])

  const onChangeCommunityIds = useCallback((_, value) => {
    changeField('templateIds', [])
    const sortedValue = chain(value)
      .map(v => find(communities, c => c.id === v))
      .sortBy(o => o.name)
      .map(o => o.id)
      .value()

    changeField('communities', map(sortedValue, communityId => {
      const prev = find(
        data.communities,
        ({ communityId: id }) => id === communityId
      )

      return {
        communityId,
        clientIds: prev?.clientIds || [],
      }
    }))
  }, [data, changeField, communities])

  const onChangeClientIds = useCallback((value, communityId) => {
    const clientsByCommunity = groupedClients[communityId]

    const activeValue = filter(value, id => {
      return !!find(clientsByCommunity, o => o.id === id)?.primaryContactTypeName
    })

    const newValue = map(data.communities, c => ({
      ...c,
      clientIds: c.communityId === communityId ? activeValue : c.clientIds
    }))

    changeField('communities', newValue)
    const selectedClients = clients.filter(item => value.includes(item.id))
    const selectedOptions = selectedClients?.map(item => ({
      value: item.id,
      text: item.fullName,
      ...item,
    }));
    setSelectedClientsOptions(selectedOptions)
    changeField('selectedClientsOptions', selectedOptions)
    sessionStorage.setItem('selectedClientsOptions', JSON.stringify(selectedOptions))

  }, [data, changeField, groupedClients, clients])

  useEffect(validateIf, [
    onScroll,
    validate,
    needValidation,
    validationOptions
  ])

  useEffect(setDefaultData, [
    user,
    defaultData,
    changeField,
    changeFields,
    communityIds,
    organizations,
    defaultClients,
    defaultOrganizationId
  ])

  return (
    <>
      {/*  第一步表单 */}
      <Form
        className="BulkSignatureRequestForm"
        onSubmit={tryToSubmit}
      >
        {(isFetching || isFetchingOrganizations) && (
          <Loader hasBackdrop/>
        )}

        <Scrollable
          style={scrollableStyles}
          className="BulkSignatureRequestForm-Sections"
        >
          <div className="BulkSignatureRequestForm-Section">
            <RequestSignatureProgress
              className="SignatureRequestForm-Progress"
            />

            <FormFields
              data={data}
              errors={errors}

              clients={clients}
              // templates={templates}
              templates={mappedTemplateTreeTypes}
              communities={communities}
              organizations={organizations}
              isCommunityDisabled={isFetchingCommunities || !data.organizationId}
              isOrganizationDisabled={isNotEmpty(defaultClientIds) || isUnary(organizations)}
              hasNoOrganizationTemplates={isNotEmpty(selectedCommunityIds) && !isFetchingTemplates && isEmpty(templateTypes)}

              onChangeField={changeField}
              onChangeClient={onChangeClientIds}
              onChangeCommunity={onChangeCommunityIds}
              onChangeOrganization={onChangeOrganization}
              onChangeExpirationDate={onChangeExpirationDate}
            />
          </div>
        </Scrollable>

        <div className="BulkSignatureRequestForm-Footer">
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

export default memo(BulkSignatureRequestForm)
