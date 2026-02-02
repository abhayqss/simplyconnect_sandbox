define(['underscore', path('./store')], function (_, store) {
    if (_.isEmpty(ExchangeApp.redux)) {
        ExchangeApp.redux = { store: store };
    }
});