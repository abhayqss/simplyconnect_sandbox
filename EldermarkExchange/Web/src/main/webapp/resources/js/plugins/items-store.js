(function() {

    this.ItemsStore = function () {
        this.items = [];
        this.triggerCallbacks = {};

        initEventType.call(this, 'countChanged');
    };

    this.ItemsStore.prototype.onCountChanged = function (element, handler) {
        subscribe.call(this, 'countChanged', element, handler);
    };

    this.ItemsStore.prototype.addItem = function (itemId) {
        if (this.items.indexOf(itemId) !== -1)
            return;

        this.items.push(itemId);

        fire.call(this, 'countChanged', this.items.length);
    };

    this.ItemsStore.prototype.removeItem = function (itemId) {
        var index = this.items.indexOf(itemId);
        this.items.splice(index, 1);

        fire.call(this, 'countChanged', this.items.length);
    };

    this.ItemsStore.prototype.removeAllItems = function () {
        this.items = [];

        fire.call(this, 'countChanged', 0);
    };

    this.ItemsStore.prototype.hasItem = function (itemId) {
        return this.items.indexOf(itemId) !== -1;
    };


    var initEventType = function (eventType) {
        this.triggerCallbacks[eventType] = [];
    };

    var subscribe = function (eventType, element, handler) {
        var callback = function (eventType, param) {
            this.trigger(eventType, param);
        };
        this.triggerCallbacks[eventType].push(callback.bind(element));

        element.on(eventType, handler);
    };

    var fire = function (eventType, param) {
        this.triggerCallbacks[eventType].forEach(function(callback) {
            callback(eventType, [param]);
        });
    };
}());
