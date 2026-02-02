import React from 'react'

import $ from 'jquery'

export default function withEvent (target = window) {
    return function (Component) {
        return class extends React.Component {
            onListen = (type, listener) => {
                $(target).on(type, listener)
            }

            onDetach = (type, listener) => {
                $(target).off(type, listener)
            }

            render() {
                return (
                    <Component
                        {...this.props}
                        listen={this.onListen}
                        detach={this.onDetach}
                    />
                )
            }
        }
    }
}