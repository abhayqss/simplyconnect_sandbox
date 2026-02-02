ExchangeApp.managers.EventManager = (function () {

    var subscribe = function(e, fn) {
            var mediator = ExchangeApp.managers.EventManager;

            if (!mediator.events[e]) mediator.events[e] = [];
            mediator.events[e].push({ context: this, callback: fn });
            return this;
        },

        publish = function(e) {
            var mediator = ExchangeApp.managers.EventManager;

            if (!mediator.events[e]) return false;

            var args = Array.prototype.slice.call(arguments, 1);
            for (var i = 0, l = mediator.events[e].length; i < l; i++) {
                var subscription = mediator.events[e][i];
                subscription.callback.apply(subscription.context, args);
            }
            return this;
        };

    return {
        events: {},
        publish: publish,
        subscribe: subscribe
    };
}());