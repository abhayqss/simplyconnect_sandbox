import React, { Component, } from 'react'

import {
    any,
    map,
    first
} from 'underscore'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { withRouter } from 'react-router-dom'

import {
    Form,
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import * as assessmentFormActions from 'redux/client/assessment/form/assessmentFormActions'

import { ASSESSMENT_TYPES } from 'lib/Constants'
import { isEmpty, isNotEmpty } from 'lib/utils/Utils'

import Loader from 'components/Loader/Loader'
import RadioGroupField from 'components/Form/RadioGroupField/RadioGroupField'

import 'components/InfoHint/InfoHint.scss'

import './AssessmentTypeForm.scss'

const {
    HOUSING,
    HMIS_ADULT_CHILD_REASESSMENT,
    HMIS_ADULT_CHILD_REASESSMENT_EXIT
} = ASSESSMENT_TYPES

function mapStateToProps(state) {
    return {
        fields: state.client.assessment.form.fields,
        types: state.directory.assessment.type.list,
        isAnyAssessmentInProcess: state.client.assessment.anyInProcess.value
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: {
            ...bindActionCreators(assessmentFormActions, dispatch)
        }
    }
}

class AssessmentTypeForm extends Component {
    componentDidMount() {
        if (isNotEmpty(this.types)) {
            if (!this.data.typeId) {
                this.changeField('typeId', first(
                    first(this.types).types
                ).id)
            }
        }
    }

    componentDidUpdate(prevProps) {
        if (isNotEmpty(this.types)) {
            if (isEmpty(prevProps.types.dataSource.data)) {
                if (!this.data.typeId) {
                    this.changeField('typeId', first(
                        first(this.types).types
                    ).id)
                }
            }
        }
    }

    onChangeField = (name, value) => {
        this.changeField(name, value)
    }

    onDeleteRow = () => {
        alert("Deleted")
    }

    get actions() {
        return this.props.actions
    }

    get clientId() {
        return +(
            this.props
                .match
                .params
                .clientId
        )
    }

    get types() {
        return (
            this.props
                .types
                .dataSource
                .data
        )
    }

    get data() {
        return this.props.fields.toJS()
    }

    changeField(name, value) {
        this.actions.changeField(name, value)
    }

    changeFields(changes) {
        this.actions.changeFields(changes)
    }

    render() {
        const {
            isAnyAssessmentInProcess
        } = this.props

        const {
            isFetching,
            dataSource: ds
        } = this.props.types
        return (
            <Form className="AssessmentTypeForm">
                {isFetching ? (
                    <Loader />
                ) : map(ds.data, group => (
                    any(group.types, o => o.canAdd) && (
                        <div key={group.id} className='AssessmentTypeForm-Section'>
                            <div className='AssessmentTypeForm-SectionTitle'>
                                {/*title*/}
                                {group.title}
                            </div>
                            <RadioGroupField
                                name='typeId'
                                selected={this.data.typeId}
                                className='AssessmentTypeForm-RadioGroupField'
                                options={map(group.types.filter(o => o.canAdd
                                    && ![
                                        HMIS_ADULT_CHILD_REASESSMENT,
                                        HMIS_ADULT_CHILD_REASESSMENT_EXIT
                                    ].includes(o.name)),
                                    o => ({
                                        label: o.title,
                                        value: o.id,
                                        ...o.name === HOUSING && isAnyAssessmentInProcess && {
                                            isDisabled: true,
                                            label: (
                                                <div>
                                                    <span id={`RadioGroupField-Radio_${o.id}`}>{o.title}</span>
                                                    <Tooltip
                                                        target={`RadioGroupField-Radio_${o.id}`}
                                                        modifiers={[
                                                            {
                                                                name: 'offset',
                                                                options: { offset: [0, 6] }
                                                            },
                                                            {
                                                                name: 'preventOverflow',
                                                                options: { boundary: document.body }
                                                            }
                                                        ]}
                                                    >
                                                        Can't create a new assessment as only one Housing Assessment can be in progress.
                                                    </Tooltip>
                                                </div>
                                            )
                                        }
                                    }))}
                                onChange={this.onChangeField}
                            />
                        </div>
                    )
                ))}
            </Form>
        )
    }
}

export default withRouter(
    connect(mapStateToProps, mapDispatchToProps)(AssessmentTypeForm)
)