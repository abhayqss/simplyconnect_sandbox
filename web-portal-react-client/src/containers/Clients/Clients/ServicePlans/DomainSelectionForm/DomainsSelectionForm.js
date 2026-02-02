import React, { memo, useState, useMemo, useCallback } from 'react'

import { compose, bindActionCreators } from 'redux'
import { connect } from 'react-redux'

import { sortBy } from 'underscore'

import { Form, Col, Row, Button, Label } from 'reactstrap'

import CheckboxField from 'components/Form/CheckboxField/CheckboxField'

import {
    useForm,
    useResponse,
    useScrollable,
    useDownloadingStatusInfoToast
} from 'hooks/common'

import DomainsSelection from 'entities/DomainsSelection'
import DomainsSelectionFormValidator from 'validators/DomainsSelectionFormValidator'

import * as errorActions from 'redux/error/errorActions'
import * as actions from 'redux/client/servicePlan/details/servicePlanDetailsActions'

import './DomainsSelectionForm.scss'

const mapDispatchToProps = dispatch => ({
    actions: {
        ...bindActionCreators(actions, dispatch),
        error: bindActionCreators(errorActions, dispatch),
    }
})

const getData = (fields) => fields.toJS()

function DomainsSelectionForm({
    domains,
    actions,
    onClose,
    clientId,
    servicePlanId,
    onSubmitSuccess,
}) {
    const [isFetching, setIsFetching] = useState(false)

    const withDownloadingStatusInfoToast = useDownloadingStatusInfoToast()

    const sortedDomains = useMemo(() => sortBy(domains, 'name'), [domains])

    const {
        fields,
        isChanged,
        changeField,
    } = useForm('DomainsSelection', DomainsSelection, DomainsSelectionFormValidator)

    const isAllChosen = fields.domainIds.size === domains.length

    const { Scrollable } = useScrollable()

    const onResponse = useResponse({
        onFailure: actions.error.change,
        onSuccess: useCallback(({ data }) => onSubmitSuccess(data), [onSubmitSuccess]),
        onUnknown: actions.error.change
    })

    function cancel() {
        onClose(isChanged)
    }

    async function submit(e) {
        e.preventDefault()

        setIsFetching(true)

        try {
            const response = await withDownloadingStatusInfoToast(
                () => actions.download(clientId, servicePlanId, getData(fields))
            )

            onResponse(response)
        } catch (error) {
            actions.error.change(error)
        } finally {
            setIsFetching(false)
        }
    }

    function onToggleAllDomains() {
        let value = isAllChosen
                ? fields.domainIds.clear()
                : fields.domainIds.clear().push(...domains.map(o => o.id))

        changeField('domainIds', value)
    }

    function onToggleDomain(domainId) {
        let index = fields.domainIds.findIndex(id => id === domainId)

        let value = ~index
            ? fields.domainIds.remove(index)
            : fields.domainIds.push(domainId)

        changeField('domainIds', value)
    }

    const onCancel = useCallback(cancel, [onClose, isChanged])

    return (
        <Form className="DomainsSelectionForm" onSubmit={submit}>
            <Scrollable style={{ flex: 1 }}>
                <div className="DomainsSelectionForm-Section">
                    <Label className="DomainsSelectionForm-Label">Domains to include in the pdf file</Label>

                    <Row>
                        <Col md="12">
                            <CheckboxField
                                label="All"
                                name="domainIds"
                                value={isAllChosen}
                                onChange={onToggleAllDomains}
                                className="DomainsSelectionForm-CheckboxField"
                            />
                        </Col>

                        {sortedDomains.map((domain) => (
                            <Col md="12" key={domain.id}>
                                <CheckboxField
                                    label={domain.name}
                                    name={domain.id}
                                    value={fields.domainIds.includes(domain.id)}
                                    onChange={() => onToggleDomain(domain.id)}
                                    className="DomainsSelectionForm-CheckboxField"
                                />
                            </Col>
                        ))}
                    </Row>
                </div>
            </Scrollable>

            <div className="DomainsSelectionForm-Buttons">
                <Button
                    outline
                    color="success"
                    onClick={onCancel}
                >
                    Cancel
                </Button>

                <Button
                    color="success"
                    disabled={isFetching || !fields.domainIds.size}
                >
                    Download Pdf
                </Button>
            </div>
        </Form>
    )
}

export default compose(
    memo,
    connect(null, mapDispatchToProps)
)(DomainsSelectionForm)
