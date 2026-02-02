ExchangeApp.managers.ModuleManager = (function () {

    var isProd = ExchangeApp.config.mode === 'production';

    var context = ExchangeApp.info.context;

    if (!context)
        context = '';

    function moduleUrl (path) {
        return context
            + '/resources/js/app/modules/'
            + path + (isProd ? '.min' : '') + '.js'
    }

    var urlModuleMap = {
        'PatientInfo': {js: moduleUrl('module.patient-info')},
        'PatientSearch': {js: moduleUrl('module.patient-search')},
        'Profile': {js: moduleUrl('module.profile')},
        'SecureMessaging': {js: moduleUrl('module.secure-messaging')},
        'SecureMessagingConfigWarning': {js: moduleUrl('module.secure-messaging.config-warning')},
        'SecureMessagingComposeMsg': {js: moduleUrl('module.secure-messaging.compose')},
        'SecureMessagingSetup': {js: moduleUrl('module.secure-messaging.setup')},
        'CareCoordination': {
            defaultChild: 'CareCoordinationEventsLog',
            js: moduleUrl('care-coordination/module.care-coordination'),
            template: 'care-coordination'
        },
        'CareCoordinationEventsLog': {
            parentModule: 'CareCoordination',
            js: moduleUrl('care-coordination/module.events-log'),
            template: 'care-coordination/events-log'
        },
        'CareCoordinationContacts': {
            parentModule: 'CareCoordination',
            js: moduleUrl('care-coordination/module.contacts')
        },
        'CareCoordinationPatients': {
            parentModule: 'CareCoordination',
            js: moduleUrl('care-coordination/module.patients')
        },
        'CareCoordinationPatientServicePlans': {
            parentModule: 'CareCoordination',
            js: moduleUrl('care-coordination/module.service-plans')
        },
        'CareCoordinationCommunities': {
            parentModule: 'CareCoordination',
            js: moduleUrl('care-coordination/module.communities')
        },
        'CareCoordinationOrganizations': {
            parentModule: 'CareCoordination',
            js: moduleUrl('care-coordination/module.organizations')
        },
        'Administration': {
            defaultChild: 'AdministrationSuggestedMatches',
            js: moduleUrl('administration/module.administration'),
            template: 'administration'
        },
        'AdministrationManualMatching': {
            parentModule: 'Administration',
            js: moduleUrl('administration/module.manual-matching'),
            template: 'administration/manual-matching'
        },
        'AdministrationSuggestedMatches': {
            parentModule: 'Administration',
            js: moduleUrl('administration/module.suggested-matches'),
            template: 'administration/suggested-matches'
        },
        'Reports': {
            js: moduleUrl('module.reports'),
            template: 'reports'
        },
        // 'Marketplace': {
        //     js: moduleUrl('module.marketplace')
        //     , template: 'marketplace22'
        // },
        'Header': {js: moduleUrl('module.header')},
        'Footer': {js: moduleUrl('module.footer')}
    };

    var alwaysShow = {Header: true, Footer: true};

    var loader;
    var modules = ExchangeApp.modules;

    $(document).on('ready', function () {
        var manager = ExchangeApp.managers.ModuleManager;
        manager.invoke({module: {name: 'Header', fragment: {url: {template: 'header'}}}});
        manager.invoke({module: {name: 'Footer', fragment: {url: {template: 'footer'}}}});
    });

    function _hideOthers(options) {
        var parent = urlModuleMap[options.module.name].parentModule;
        for (var key in modules) {
            var kp = urlModuleMap[key].parentModule;
            if (alwaysShow[key]) {
                continue;
            }
            if ((kp && parent && kp === parent) || (!kp && !parent) || (!kp && key !== parent)) {
                modules[key].hide();
            }
        }
    }

    return {
        urlModuleMap: {},
        invoke: function (options) {

            if (!loader) {
                loader = ExchangeApp.loaders.ModuleLoader;
            }

            var fragmentUrl = options.module.fragment.url;
            var moduleName = options.module.name;
            var parent = moduleName && urlModuleMap[moduleName].parentModule;

            var reloadNeeded = options.forceReload || (modules[moduleName] && modules[moduleName].reloadNeeded());

            if (modules[moduleName] && !reloadNeeded) {
                // if this module has been loaded previously

                if (!options.asyncLoad) {
                    _hideOthers(options);
                }
                if (parent) {
                    // if this module has a parent -> render the parent
                    modules[parent].loaded();
                    modules[parent].show();
                }

                if (modules[moduleName].isFragmentInited(fragmentUrl)) {
                    modules[moduleName].update(fragmentUrl);
                } else {
                    modules[moduleName].init(fragmentUrl);
                }
                modules[moduleName].show();
            }

            else if (moduleName) {
                // if it's the first time when this module is loaded

                if (!options.asyncLoad) {
                    _hideOthers(options);
                }

                if (urlModuleMap[moduleName].defaultChild) {
                    // if this module has children -> render the default child
                    parent = moduleName;
                    moduleName = urlModuleMap[moduleName].defaultChild;
                    fragmentUrl = ExchangeApp.utils.module.getUrl(urlModuleMap[moduleName].template);
                    if (!fragmentUrl) {
                        console.error('URL template is undefined for child module ' + moduleName);
                    }
                }

                // parent must to be, but undefined
                if (parent && !modules[parent]) {
                    // load parent, then load child
                    loader.load({
                        url: urlModuleMap[parent].js,
                        callback: function () {
                            var parentFragmentUrl = {template: urlModuleMap[parent].template};
                            if (modules[parent].isFragmentInited(parentFragmentUrl)) {
                                modules[parent].update(parentFragmentUrl);
                            } else {
                                modules[parent].init(parentFragmentUrl);
                            }

                            // render parent
                            modules[parent].show();

                            // override loaded method in order to render a child as soon as the parent is loaded
                            var originalLoaded = modules[parent].loaded;
                            modules[parent].loaded = function () {
                                if (originalLoaded) {
                                    var result = originalLoaded.apply(this, arguments);
                                }
                                loader.load({
                                    url: urlModuleMap[moduleName].js,
                                    callback: function () {
                                        if (modules[moduleName].isFragmentInited(fragmentUrl)) {
                                            modules[moduleName].update(fragmentUrl);
                                        } else {
                                            modules[moduleName].init(fragmentUrl);
                                        }
                                        if (!options.asyncLoad) {
                                            // render child
                                            modules[moduleName].show();
                                        }
                                    }
                                });
                                // restore original loaded method
                                modules[parent].loaded = originalLoaded;

                                return result;
                            };
                        }
                    });
                } else {
                    // Header or Footer
                    loader.load({
                        url: urlModuleMap[moduleName].js,
                        callback: function () {
                            if (modules[moduleName].isFragmentInited(fragmentUrl)) {
                                modules[moduleName].update(fragmentUrl);
                                if (!options.asyncLoad) {
                                    modules[options.module.name].show();
                                }
                            } else {
                                //TODO check if module changed to parent: if (options.module.name!=moduleName)???
                                modules[moduleName].init(fragmentUrl, modules[options.module.name]);
                            }
                            //if (!options.asyncLoad) {
                            //    modules[options.module.name].show();
                            //}
                        }
                    });
                }
            }
        },

        setLoader: function () {
            loader = ExchangeApp.loaders.ModuleLoader;
        },
        callback: function () {

        }
    }
})
();