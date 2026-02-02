function useVideoPopupDOMProperties() {
    const isDesktopView = window.innerWidth > 1024
    const isTabletView = window.innerWidth <= 1024 && window.innerWidth > 667
    const isMobileView = window.innerWidth <= 667

    let size

    switch (true) {
        case isDesktopView:
            size = 'big'
            break

        case isTabletView:
            size = 'medium'
            break

        case isMobileView:
            size = 'auto'
            break

        default:
            size = 'big'
            break
    }

    return {
        size,
        isTabletView,
        isMobileView,
        isDesktopView,
    }
}

export default useVideoPopupDOMProperties
