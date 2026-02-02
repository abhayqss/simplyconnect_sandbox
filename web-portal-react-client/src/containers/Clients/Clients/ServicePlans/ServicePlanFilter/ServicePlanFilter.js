import React, { Component } from 'react'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { Row, Col } from 'reactstrap'

import { SearchField } from 'components'

import * as servicePlanListActions from 'redux/client/servicePlan/list/servicePlanListActions'

import './ServicePlanFilter.scss'

function mapStateToProps (state) {
    return {
        fields: state.client.servicePlan.list.dataSource.filter
    }
}

function mapDispatchToProps (dispatch) {
    return {
        actions: bindActionCreators(servicePlanListActions, dispatch)
    }
}

class ServicePlanFilter extends Component {
    onClear = () => {
        this.actions.clearFilter()
    }

    onChangeField = (name, value) => {
        this.actions.changeFilterField(
            name, value
        )
    }

    get actions () {
        return this.props.actions
    }

    render () {
        return (
            <div className="ServicePlanFilter">
                <Row>
                    <Col md={6} lg={4}>
                        <SearchField
                            name='searchText'
                            value={this.props.fields.searchText}
                            placeholder='Search'
                            onChange={this.onChangeField}
                            onClear={this.onClear}
                        />
                    </Col>
                </Row>
            </div>
        )
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(ServicePlanFilter)