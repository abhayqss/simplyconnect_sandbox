requirejs([], function () {
    var mode = ExchangeApp.config.mode;

    window.path = function (path) {
        return path + (mode === 'production' ? '.min' : '');
    };

    function url (path, isFile) {
        isFile = isFile !== false;
        var isProd = ExchangeApp.config.mode === 'production';
        return ExchangeApp.info.context + path + (isFile && isProd ? '.min' : '')
    }

    requirejs.config({
        baseUrl: url('/resources/js', false),
        paths: {
            'app': url('/resources/js/app', false),
            'Immutable': url('/resources/js/plugins/immutable'),
            'underscore': url('/resources/js/plugins/underscore'),
            'classnames': url('/resources/js/plugins/classnames'),
            'superagent': url('/resources/js/plugins/superagent'),
            'keyMirror': url('/resources/js/plugins/key-mirror'),
            'redux': url('/resources/js/plugins/redux'),
            'redux-thunk': url('/resources/js/plugins/redux-thunk'),
            'redux-utils': url('/resources/js/plugins/redux-utils'),
            'date-math': url('/resources/js/plugins/date-math')
        }
    });

    requirejs(['app/main'])
});