ExchangeApp.routers.ModuleRouter = (function () {

    var moduleManager = ExchangeApp.managers.ModuleManager;
    var utils = ExchangeApp.utils.module;

    var currentPageUrl;
    var prevPageUrl;

    var urlModuleMap = {
        'patient-info/{residentId}':                'PatientInfo',
        'patient-info/{residentId}/aggregated':     'PatientInfo',
        'patient-search':                           'PatientSearch',
        'profile/show':                             'Profile',
        'secure-messaging':                         'SecureMessaging',
        'secure-messaging/config-warning':          'SecureMessagingConfigWarning',
        'secure-messaging/compose':                 'SecureMessagingComposeMsg',
        'secure-messaging/setup':                   'SecureMessagingSetup',
        'patient-info/{residentId}/compose':        'SecureMessagingComposeMsg',
        'secure-messaging/{messageId}/reply-to':    'SecureMessagingComposeMsg',
        'care-coordination':                        'CareCoordination',
        'care-coordination/patients':               'CareCoordinationPatients',
        'care-coordination/contacts':               'CareCoordinationContacts',
        'care-coordination/events-log':             'CareCoordinationEventsLog',
        'care-coordination/templates/communities':  'CareCoordinationCommunities',
        'care-coordination/templates/organizations':'CareCoordinationOrganizations',
        'care-coordination/service-plans':          'CareCoordinationPatientServicePlans',
        'administration':                           'Administration',
        'administration/manual-matching':           'AdministrationManualMatching',
        'administration/suggested-matches':         'AdministrationSuggestedMatches',
        'reports':                                  'Reports',
        // 'marketplace222':                              'Marketplace',
        'header':                                   'Header',
        'footer':                                   'Footer'
    };

    function _pushState(state) {
        $.bbq.pushState(state);
    }

    function _getState(key) {
        return $.bbq.getState(key);
    }

    function _back() {
        history.back();
    }

    function _navigate(url, initParents, forceReload) {
        moduleManager.invoke({
            module: {name: urlModuleMap[url.template], fragment: {url: url}},
            initParents: initParents,
            forceReload: forceReload
        });

        ExchangeApp.managers.EventManager.publish('page_changed', {
            url: url,
            module: urlModuleMap[url.template],
            previousUrl: prevPageUrl
        });
        prevPageUrl = currentPageUrl;
        currentPageUrl = url.template;

    }

    function _reload(url) {
        if (!url) {
            url = _getState('url');
        }

        if (url)
            _navigate(url, false, true);

    }

    $(window).on('hashchange', function () {
        var url = _getState('url');

        if (url)
            _navigate(url, false);

    });

    $(document).on('ready', function () {
        var url = _getState('url');
        if (!url) {
            _pushState({url:utils.getUrl(ExchangeApp.info.startPage, null, ExchangeApp.info.params)});
        } else {
            _navigate(url, true);
        }
    });

    return {
        route: function (url) {
            // url, nocache
            if (!moduleManager) this.setManager();

            _pushState({url: url});
        },
        back: function () {
            _back();
        },
        reload: function(url) {
            _reload(url);
        },
        setManager: function () {
            moduleManager = ExchangeApp.managers.ModuleManager;
        },

        getUrlTemplate: function() {
            var url = _getState('url');
            return (url) ? url.template : null;
        }
    }
})();