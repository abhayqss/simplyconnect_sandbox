import React, {
    useMemo,
    useState,
    useCallback
} from 'react'

import {
    map,
    all,
    each,
    filter,
    reject,
    isObject
} from 'underscore'

import cn from 'classnames'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { Badge, Button } from 'reactstrap'

import {
    Modal,
    Table,
    Loader,
    Scrollable
} from 'components'

import { CheckboxField } from 'components/Form'
import { ConfirmDialog } from 'components/dialogs'

import { useResponse } from 'hooks/common'
import { useAuthUser } from 'hooks/common/redux'

import * as servicePlanFormActions from 'redux/client/servicePlan/form/servicePlanFormActions'

import {
    isEmpty,
    snakeToTitle
} from 'lib/utils/Utils'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import './AssessmentToServicePlanEditor.scss'

function prepareData(data) {
    let count = 0
    const prepared = []

    each(data, (o, i) => {
        const className = i % 2 === 0 ? 'odd' : 'even'

        const { questions } = o.section || o

        each(questions, (q, j) => {
            const total = questions.length
            const { alreadyExists } = o.need

            prepared.push({
                index: count++,
                questionIndex: j,
                need: j === 0 ? o.need : { alreadyExists },
                question: q,
                questions,
                section: o.section,
                className: cn(
                    className,
                    total > 1 && (
                        j === 0 ? `${className}-first` : (
                            j < (total - 1) ? `${className}-medium` : `${className}-last`
                        )
                    ),
                    alreadyExists && 'disabled'
                )
            })
        })
    })

    return prepared
}

function mapStateToProps(state) {
    return { client: state.client }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: {
            servicePlan: {
                form: bindActionCreators(servicePlanFormActions, dispatch)
            }
        }
    }
}

function AssessmentToServicePlanEditor(
    {
        actions,

        isOpen,

        client,
        clientId,

        onClose,
        onSaveSuccess,
        onExcludeQuestions,

        ...props
    }
) {
    const user = useAuthUser()

    const { assessment, servicePlan } = client
    const { needIdentification } = assessment.servicePlan

    const isFetching = (
        servicePlan.form.isFetching
        || needIdentification.isFetching
    )

    const [selected, setSelected] = useState([])

    const [areAvailableNeedsVisible, setAvailableNeedsVisible] = useState(false)
    const [areExcludedQuestionsVisible, setExcludedQuestionsVisible] = useState(false)

    const [isNewServicePlanConfirmDialogOpen, setNewServicePlanConfirmDialogOpen] = useState(false)

    const data = useMemo(() => {
        let prepared = prepareData(props.data)

        if (!areAvailableNeedsVisible) {
            prepared = reject(prepared, o => o.need.alreadyExists)
        }

        if (!areExcludedQuestionsVisible) {
            prepared = reject(prepared, o => (
                o.question?.isExcluded || o.section?.isExcluded
            ))
        }

        return prepared
    }, [props.data, areAvailableNeedsVisible, areExcludedQuestionsVisible])

    const { fullName } = client.details.data ?? {}

    const areAllSelectedQuestionsExcluded = all(selected, o => (
        o.question?.isExcluded || o.section?.isExcluded
    ))

    const nonSelectable = []

    each(data, o => {
        if (!o.need.domainName) {
            nonSelectable.push(o.index)
        }
    })

    const total = data.length - nonSelectable.length
    const availableNeedCount = filter(data, o => o.need.alreadyExists).length

    function save() {
        return actions.servicePlan.form.submit({
            dateCreated: Date.now(),
            createdBy: user.fullName,
            ...servicePlan.details.data,
            needs: [
                ...servicePlan.details.data?.needs || [],
                ...map(selected, o => o.need)
            ]
        }, { clientId })
    }

    const saveResponse = useResponse({
        onSuccess: onSaveSuccess
    })

    const onSelect = useCallback((row, shouldSelect) => {
        setSelected(shouldSelect ? [...selected, row] : reject(
            selected, o => o.index === row.index
        ))
    }, [selected])

    const onSelectAll = useCallback((shouldSelect, rows) => {
        setSelected(shouldSelect ? reject(rows, o => o.need.alreadyExists) : [])
    }, [setSelected])

    const onHide = useCallback(() => {
        onExcludeQuestions(map(selected, o => (
            o.section || o.questions
        )))
    }, [selected, onExcludeQuestions])

    const onSave = useCallback(() => {
        if (servicePlan.details.data) save().then(saveResponse)
        else setNewServicePlanConfirmDialogOpen(true)
    }, [user, actions, selected, servicePlan, saveResponse])

    const onConfirmSave = useCallback(() => {
        save().then(saveResponse)
        setNewServicePlanConfirmDialogOpen(false)
    }, [user, actions, selected, servicePlan, saveResponse])

    return (
        <>
            {isOpen && (
                <Modal
                    isOpen
                    hasCloseBtn={false}
                    title="Assessment to Service Plan"
                    className="AssessmentToServicePlanEditor"
                    footerClassName="AssessmentToServicePlanEditor-Actions"
                    renderFooter={() => (
                        <>
                            <div className="AssessmentToServicePlanEditor-ActionGroup">
                                <CheckboxField
                                    name="isAddedVisible"
                                    value={areAvailableNeedsVisible}
                                    label="Show Added"
                                    isDisabled={isFetching}
                                    className="AssessmentToServicePlanEditor-Action"
                                    onChange={(name, value) => setAvailableNeedsVisible(value)}
                                />
                                <CheckboxField
                                    name="isHiddenVisible"
                                    value={areExcludedQuestionsVisible}
                                    label="Show Hidden"
                                    isDisabled={isFetching}
                                    className="AssessmentToServicePlanEditor-Action"
                                    onChange={(name, value) => setExcludedQuestionsVisible(value)}
                                />
                            </div>
                            <div className="AssessmentToServicePlanEditor-ActionGroup">
                                <Button
                                    outline
                                    color="success"
                                    disabled={isFetching}
                                    onClick={onClose}
                                    className="AssessmentToServicePlanEditor-Action"
                                >
                                    Cancel
                                </Button>
                                <Button
                                    outline
                                    color="success"
                                    onClick={onHide}
                                    disabled={isFetching || isEmpty(selected) || areAllSelectedQuestionsExcluded}
                                    className="AssessmentToServicePlanEditor-Action"
                                >
                                    Hide{selected.length && !areAllSelectedQuestionsExcluded ? ` (${selected.length})` : ''}
                                </Button>
                                <Button
                                    color="success"
                                    onClick={onSave}
                                    disabled={isFetching || isEmpty(selected)}
                                    className="AssessmentToServicePlanEditor-Action AssessmentToServicePlanEditor-SaveBtn"
                                >
                                    Add<span className="AssessmentToServicePlanEditor-SaveBtnOptText"> to Service Plan</span>{selected.length ? ` (${selected.length})` : ''}
                                </Button>
                            </div>
                        </>
                    )}
                >
                    <div className="ServicePlanNeedIdentification">
                        {isFetching && (
                            <Loader style={{ position: 'fixed' }} hasBackdrop />
                        )}
                        <div className="ServicePlanNeedIdentification-Header">
                            <span className="ServicePlanNeedIdentification-Title">Needs Identified in Assessment</span>
                            <Badge color='info' className="Badge Badge_place_top-right">
                                {total}
                            </Badge>
                        </div>
                        <Table
                            hasHover
                            keyField="index"
                            hasCaption={false}
                            noDataText="No needs."
                            className="ServicePlanNeedIdentificationList"
                            containerClass="ServicePlanNeedIdentificationListContainer"
                            data={data}
                            columns={[
                                {
                                    dataField: 'need',
                                    text: 'Need',
                                    headerStyle: { width: '30%' },
                                    formatter: (need) => {
                                        const value = (
                                            need.needOpportunity
                                            || need.activationOrEducationTask
                                        ) || ''

                                        return (
                                            need.alreadyExists ? (
                                                <span className="text-decoration-line-through">
                                                    {value}
                                                </span>
                                            ) : value
                                        )
                                    }
                                },
                                {
                                    dataField: 'question.title',
                                    text: 'Assessment Question'
                                },
                                {
                                    dataField: 'question.value',
                                    text: 'Assessment Response',
                                    headerStyle: { width: '20%' },
                                    formatter: value => {
                                        return (
                                            isObject(value) ? map(value, (v, k) => (
                                                <div>{snakeToTitle(k)}: {v}</div>
                                            )) : value || ''
                                        )
                                    }
                                }
                            ]}
                            selectedRows={{
                                mode: 'checkbox',
                                clickToSelect: true,
                                onSelect,
                                onSelectAll,
                                nonSelectable,
                                selectionRenderer: ({ checked, disabled, rowIndex }) => (
                                    !disabled && (
                                        <CheckboxField
                                            value={data[rowIndex].need.alreadyExists || checked}
                                        />
                                    )
                                ),
                                selectionHeaderRenderer: () => (
                                    <CheckboxField
                                        value={selected.length > 0 && selected.length === (total - availableNeedCount)}
                                    />
                                )
                            }}
                            getRowClass={row => row.className}
                            columnsMobile={['question.title']}
                        />
                    </div>
                </Modal>
            )}
            {isNewServicePlanConfirmDialogOpen && (
                <ConfirmDialog
                    isOpen
                    icon={Warning}
                    confirmBtnText="OK"
                    title={`A new service plan will be created for ${fullName}`}
                    onConfirm={onConfirmSave}
                    onCancel={() => setNewServicePlanConfirmDialogOpen(false)}
                />
            )}
        </>
    )
}

export default connect(mapStateToProps, mapDispatchToProps)(AssessmentToServicePlanEditor)