import React, {
    useRef,
    useState,
    useEffect
} from 'react'

import cn from 'classnames'

import dCarousel from 'd-carousel'

import { asyncTimes } from 'lib/utils/Utils'

import { ReactComponent as Prev } from 'images/arrow-prev.svg'
import { ReactComponent as Next } from 'images/arrow-next.svg'

import './Carousel.scss'

function Slide({ children, className }) {
    return (
        <li className={cn("Carousel-Item d-carousel__item", className)}>
            {children}
        </li>
    )
}

export default function Carousel({ children, className, containerClassName }) {
    const ref = useRef()

    const [api, setApi] = useState(null)

    useEffect(() => {
        setApi(dCarousel(ref.current))
    }, [])

    useEffect(() => {
        if (api) {
            asyncTimes(
                5, () => api.forceRefresh(), 1000
            )
        }
    }, [api])

    return (
        <div className={cn("CarouselContainer", containerClassName)}>
            <div
                ref={ref}
                className={cn('Carousel d-carousel', className)}
            >
                <div className="d-carousel__outer">
                    <div className="d-carousel__inner">
                        <div>
                            <ul className="d-carousel__ul">
                                {children}
                            </ul>
                        </div>
                    </div>
                </div>
                <button className="d-carousel__prev">
                    <Prev/>
                </button>
                <button className="d-carousel__next">
                    <Next/>
                </button>
            </div>
        </div>
    )
}

Carousel.Slide = Slide