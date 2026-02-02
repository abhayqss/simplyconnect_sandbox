import React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import { debounce } from 'lodash'

import SearchField from 'components/SearchField/SearchField'

import * as careTeamMemberListActions from 'redux/care/team/member/list/careTeamMemberListActions'

import './CareTeamMemberFilter.scss'

function mapStateToProps(state) {
    return {
        fields: state.care.team.member.list.dataSource.filter
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(careTeamMemberListActions, dispatch),
    }
}

class CareTeamMemberFilter extends React.PureComponent {
    static propTypes = {
        fields: PropTypes.shape({
            name: PropTypes.string
        }).isRequired
    }

    constructor(props) {
        super(props)

        this.state = {
            searchValue: ''
        }

        this.change = debounce(this.change, 300)
    }

    get actions() {
        return this.props.actions
    }

    change(changes) {
        return this.actions.changeFilter(changes)
    }

    onChangeField = (name, value) => {
        this.setState({ searchValue: value })
        this.change({ [name]: value })
    }

    onClear = () => {
        this.setState({ searchValue: '' })
        this.actions.clearFilter()
    }

    componentWillUnmount() {
        this.onClear()
    }

    render() {
        return (
            <div className='CareTeamMemberFilter'>
                <SearchField
                    name='name'
                    className='CareTeamMemberFilter-Field'
                    placeholder='Search by name'
                    value={this.state.searchValue}
                    onChange={this.onChangeField}
                    onClear={this.onClear}
                />
            </div>
        )
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CareTeamMemberFilter)
