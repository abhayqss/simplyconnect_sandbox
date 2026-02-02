import cn from 'classnames'
import { each } from 'underscore'

import { Howl } from 'howler'

export function measure(node) {
    return node?.getBoundingClientRect()
}

export function css(node, style) {
    each(style, (propVal, propName) => {
        node.style[propName] = propVal
    })
}

export function addIframe(
    {
        place = 'start',
        parent,
        attributes
    }
) {
    if (typeof parent === 'string') {
        parent = document.querySelector(parent)
    }

    if (parent) {
        const iframe = document.createElement('iframe')

        each(attributes, (value, name) => {
            iframe[name] = value
        })

        if (place === 'start') parent.append(iframe)
        else parent.prepend(iframe)

        return iframe
    }
}

export function iframe(node = null) {
    return {
        add({
                place = 'start',
                parent,
                attributes
            }) {
            node = addIframe({
                place,
                parent,
                attributes
            })

            return this
        },
        node() {
            return node
        },
        find(selector) {
            return this.document().querySelector(selector)
        },
        findAll(selector) {
            return this.document().querySelectorAll(selector)
        },
        document() {
            return node.contentWindow.document
        },
        body(children) {
            const body = (
                node.contentWindow.document.body
            )

            if (children) body.append(children)

            return {
                node() {
                    return node
                },
                style(style) {
                    css(body, style)
                    return this
                },
                classNames(...classNames) {
                    body.classList.add(cn(classNames))
                    return this
                },
                find(selector) {
                    return body.querySelector(selector)
                },
                findAll(selector) {
                    return body.querySelectorAll(selector)
                }
            }
        }
    }
}

export function AudioPlayer(src, { isInfinite }) {
    let player = new Howl({
        src: [src],
        loop: isInfinite,
        volume: 1,
    })

    return {
        play: () => player.play(),
        stop: () => player.stop(),
    }
} 