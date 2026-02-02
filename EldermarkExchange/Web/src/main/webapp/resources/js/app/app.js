
function App () {
    this.modules = {};
    this.loaders = {};
    this.routers = {};
    this.managers = {};
    this.services = {};
    this.utils = {};
    this.info = {};
    this.config = {};
    this.flux = null;
    this.redux = {};
}

App.prototype.configure = function (config) {
    this.config = config;
};

var ExchangeApp = new App();

(function () {
    var PRODUCTION = 'production';
    var DEVELOPMENT = 'development';

    var config = {
        mode: PRODUCTION
    };

    ExchangeApp.configure(config);
})();