import React from 'react'

import { each } from 'underscore'

import { connect } from 'react-redux'

function mapStateToProps(state) {
    return { directory: state.directory }
}

export default function widthDirectoryData (Component) {
    return connect(mapStateToProps, null)(class extends React.Component {
        getDirectoryData = config => {
            const out = {}
            const { directory } = this.props

            each(config, (path, name) => {
                out[name] = directory.getIn([
                    ...path, 'list', 'dataSource', 'data']
                ) || []
            })

            return out
        }

        render() {
            return (
                <Component
                    {...this.props}
                    getDirectoryData={this.getDirectoryData}
                />
            )
        }
    })
}