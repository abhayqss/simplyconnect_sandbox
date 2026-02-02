/**
 * Created by stsiushkevich on 10.09.18.
 */

var Component = (function ($) {

    var propsLogs = {};

    var stateLogs = {};
    var stateCallbackLogs = {};

    function isObject(value) {
        var type = typeof value;
        return !!value && (type === 'object' || type === 'function');
    }

    function isFunction(value) {
        return isObject(value)
            && Object.prototype.toString.call(value) === '[object Function]';
    }

    function isPropsLogEmpty (id) {
        return propsLogs[id].length === 0
    }

    function logProps (id, props) {
        propsLogs[id].push(props)
    }

    function cleanPropsLog (id) {
        propsLogs[id] = [];
    }

    function isStateLogEmpty (id) {
        return stateLogs[id].length === 0
    }

    function logState (id, state) {
        stateLogs[id].push(state)
    }

    function cleanStateLog (id) {
        stateLogs[id] = [];
    }

    function logStateCallback (id, callback) {
        stateCallbackLogs[id].push(callback)
    }

    function cleanStateCallbackLog (id) {
        stateCallbackLogs[id] = [];
    }

    function Component (props) {
        Object.defineProperty(this, '$$id', {
            value: Math.random() * 10000000000000000000
        });

        propsLogs[this.$$id] = [];
        stateLogs[this.$$id] = [];
        stateCallbackLogs[this.$$id] = [];

        this.props = $.extend({}, this.getDefaultProps(), props);
    }

    Component.prototype.getDefaultProps = function () {
        return null;
    };

    Component.prototype.mount = function () {
        var template = this.render();

        if (template) {
            template['cmp-id'] = this.$$id;
            $(this.props.container).json2html({}, template);
        }

        this.componentDidMount();
    };

    Component.prototype.unmount = function () {
        this.componentWillUnmount();
        $('[cmp-id="'+ this.$$id +'"]').remove();
    };

    Component.prototype.remount = function () {
        var template = this.render();

        if (template) {
            template['cmp-id'] = this.$$id;

            var $cmp = $('[cmp-id="'+ this.$$id +'"]');
            var $container = $('<div>').insertAfter($cmp);

            $container.json2html({}, template);
            $container.children().unwrap();

            $cmp.remove();
        }

        this.componentDidMount();
    };

    Component.prototype.render = function () { return null };

    Component.prototype.componentDidMount = function () {};

    Component.prototype.componentWillUnmount = function () {};

    Component.prototype.update = function (props) {
        var prevProps = this.props;

        if (prevProps !== props) {
            this.props = $.extend({}, prevProps, props);

            logProps(this.$$id, prevProps);

            var me = this;
            setTimeout(function () {
                if (!isPropsLogEmpty(me.$$id)) {
                    var props = propsLogs[me.$$id][0];

                    cleanPropsLog(me.$$id);

                    me.componentDidUpdate(props, me.state);
                }
            }, 0);
        }
    };

    Component.prototype.componentDidUpdate = function (prevProps, prevState) {};

    Component.prototype.setState = function (state, callback) {
        if (this.state) {
            var prevState = this.state;

            if (prevState !== state) {
                this.state = $.extend({}, prevState, state);

                logState(this.$$id, prevState);

                if (isFunction(callback)) {
                    logStateCallback(this.$$id, callback);
                }

                var me = this;
                setTimeout(function () {
                    if (!isStateLogEmpty(me.$$id)) {
                        var state = stateLogs[me.$$id][0];

                        cleanStateLog(me.$$id);

                        var callbacks = stateCallbackLogs[me.$$id];

                        for (var i = 0; i < callbacks.length; i++) {
                            callbacks[i].call(me, state);
                        }

                        cleanStateCallbackLog(me.$$id);

                        me.componentDidUpdate(me.props, state);
                    }
                }, 0);
            }
        } else throw new Error('Component does not have a default state. ' +
            'Please set this.state = {...} in the constructor.');
    };

    return Component;
})($);