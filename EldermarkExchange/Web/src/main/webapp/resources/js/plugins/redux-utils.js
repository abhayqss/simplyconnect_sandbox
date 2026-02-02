(function (root, factory) {
    if (typeof define === 'function' && define.amd) {
        define([], factory);
    } else if (typeof module === 'object' && module.exports) {
        module.exports = factory();
    } else {
        root.returnExports = factory();
    }
}(this, function () {
    return {
        connect: function (mapStateToProps, mapDispatchToProps) {
            return function (Component) {
                if (Component && Component.prototype) {
                    var unsubscribe = null;

                    function ComponentWrapper() {
                        Component.apply(this, arguments);
                    }

                    ComponentWrapper.prototype.getStore = function () {
                        var getStore = Component.prototype.getStore;
                        return getStore ? getStore.apply(this) : null;
                    };

                    ComponentWrapper.prototype = Object.create(Component.prototype);
                    ComponentWrapper.prototype.constructor = ComponentWrapper;

                    ComponentWrapper.prototype.getDefaultProps = function () {
                        var store = this.getStore();
                        var props = Component.prototype.getDefaultProps.apply(this);

                        return store ? Object.assign(
                            {},
                            props,
                            mapStateToProps(store.getState()),
                            mapDispatchToProps(store.dispatch)
                        ) : props;
                    };

                    ComponentWrapper.prototype.componentDidMount = function () {
                        var store = this.getStore();

                        if (store) {
                            mapDispatchToProps && mapDispatchToProps(store.dispatch);

                            var me = this;
                            unsubscribe = store.subscribe(function () {
                                me.update(mapStateToProps ? mapStateToProps(store.getState()) : {});
                            })
                        }

                        Component.prototype.componentDidMount.apply(this);
                    };

                    ComponentWrapper.prototype.componentWillUnmount = function () {
                        unsubscribe && unsubscribe();
                        Component.prototype.componentWillUnmount.apply(this);
                    };

                    return ComponentWrapper;
                }
            }
        }
    };
}));