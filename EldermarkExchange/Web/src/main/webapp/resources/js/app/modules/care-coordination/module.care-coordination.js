ExchangeApp.modules.CareCoordination = (function () {
    // history of loaded segments
    var fragmentsMap = {};
    // root url
    var cFragmentUrl = {
        template: 'care-coordination'
    };
    // flag if true then reloaded every time
    var state = {
        reloadNeeded: false
    };
    // load root routers (map url to module)
    var router = ExchangeApp.routers.ModuleRouter;
    // Utility
    var mdlUtils = ExchangeApp.utils.module;

    var loader = ExchangeApp.loaders.FragmentLoader.init('careCoordination');
    var sectionInitMap = {};
    var intialized = false;
    var COMMUNITY_COUNT_URL = 'care-coordination/communities/count';
    var PATIENTS_COUNT_URL = 'care-coordination/patients/count';

    function updateCommunityCount() {
        var communityCountTabPanel = $find('#communityCountTabPanel');
        $.ajax({
            type: 'GET',
            contentType: 'json',
            url: COMMUNITY_COUNT_URL,
            success: function(data){
                communityCountTabPanel.html(data);
            }
        });
    }

    function updatePatientsCount() {
        var patientsCountTabPanel = $find('#patientsCountTabPanel');
        $.ajax({
            type: 'GET',
            contentType: 'json',
            url: PATIENTS_COUNT_URL,
            success: function(data){
                patientsCountTabPanel.html(data);
            }
        });
    }

    /*-------------utils----------------*/

    function _prepare() {
        var cFragment = _getFragment();
        cFragment.random = mdlUtils.rand();
        _randomize();
        _ajaxify();
    }

    function _ajaxify($trgFragment, trgRandom) {
        var cFragment = _getFragment();

        if (!cFragment.ajaxMap) {
            cFragment.ajaxMap = {};
        }

        var $fragment = $trgFragment ? $trgFragment : cFragment.$html;
        var random = trgRandom ? trgRandom : cFragment.random;

        var urls = mdlUtils.findAjaxUrls($fragment, random);
        $.each(urls, function (i, url) {
            cFragment.ajaxMap[_stringifyUrl(url, {params: true})] = url;
        });

        mdlUtils.find($fragment, random, '[data-ajax-load="true"]').on('click', function () {
            // deselect all other tabs
            $(this).parent('li').parent('ul').children('li').each(function () {
                $(this).removeClass('active');
            });
            //  select us
            $(this).parent("li").addClass('active');
            var template = $(this).attr('data-ajax-url-tmpl');
            var vars = $(this).attr('data-ajax-url-vars');
            var params = $(this).attr('data-ajax-url-params');

            var url = mdlUtils.getUrl(template, vars, params);

            router.route(url);
            return false;
        });

        mdlUtils.find($fragment, random, '[data-ajax-anchor="true"]').on('click', function () {
            var $trg = $($(this).attr('href'));
            if ($trg.length) {
                $('html, body').animate({scrollTop: $trg.offset().top}, 200);
            }
            return false;
        });
    }

    function _getFragment() {
        return fragmentsMap[_stringifyUrl(cFragmentUrl, {params: true})];
    }

    function _getFragmentHolder() {
        return $(document.getElementById(_getFragmentHolderId()));
    }

    function _getFragmentHolderId() {
        return _stringifyUrl(cFragmentUrl, {params: true}).replace(/[/&\?=]/g, '_');
    }

    function _stringifyUrl(url, excludeOptions) {
        return mdlUtils.stringifyUrl(url, excludeOptions);
    }

    function $find(selector) {
        var fragment = _getFragment();
        return mdlUtils.find(fragment.$html, fragment.random, selector);
    }

    function _randomize($trgFragment, trgRandom) {
        var cFragment = _getFragment();
        var $fragment = $trgFragment ? $trgFragment : cFragment.$html;
        var random = trgRandom ? trgRandom : cFragment.random;

        $fragment = mdlUtils.randomize($fragment, random, false);
        return $fragment;
    }

    $.fn.clearForm = function () {
        return this.each(function () {
            var tag = this.tagName.toLowerCase();
            if (tag === 'form') {
                return $(':input:not([disabled="disabled"] ,[type="hidden"])', this).clearForm();
            }

            var type = this.type;
            if (type === 'text' || type === 'password' || tag === 'textarea') {
                this.value = '';
                $(this).trigger('refresh');
            }
            else if (tag === 'select') {
                this.selectedIndex = 0;
                $(this).trigger('refresh');
            }
            else if (type === 'checkbox' || type === 'radio') {
                this.checked = false;
                $(this).trigger('refresh');
            }
        });
    };

    /*function getMarkupTemplate () {
        return {'<>': 'table', 'class': 'defaultMain ldr-center-block', 'html': [
            {'<>': 'tbody', 'html': [
                {'<>': 'tr', 'html': [
                    {'<>': 'td', 'class': 'ldr-ui-body defaultBody', 'html': [
                        {'<>': 'div', 'role': 'tabpanel', 'html': [
                            {'<>': 'ul', 'role': 'tablist', 'class': 'nav nav-tabs', 'html': [
                                {'<>': 'li', 'id': 'patientsTab', 'role': 'presentation', 'html': [
                                    {
                                        '<>': 'a',
                                        'role': 'tab',
                                        'href': context + '/care-coordination/patients',
                                        'data-ajax-load': 'true',
                                        'data-ajax-url-tmpl': 'care-coordination/patients',
                                        'data-target': '#patientsTabContent',
                                        'html': [
                                            {'<>': 'span', 'text': 'Patients'}
                                        ]
                                    }
                                ]},
                                {'<>': 'li', 'id': 'eventsLogTab', 'role': 'presentation', 'class': 'active', 'html': [
                                    {
                                        '<>': 'a',
                                        'role': 'tab',
                                        'href': context + '/care-coordination/events-log',
                                        'data-ajax-load': 'true',
                                        'data-ajax-url-tmpl': 'care-coordination/events-log',
                                        'data-target': '#eventsLogTabContent',
                                        'aria-expanded': 'true',
                                        'html': [
                                            {'<>': 'span', 'text': 'Events'}
                                        ]
                                    }
                                ]},
                                {'<>': 'li', 'id': 'contactsTab', 'role': 'presentation', 'html': [
                                    {
                                        '<>': 'a',
                                        'role': 'tab',
                                        'href': context + '/care-coordination/contacts',
                                        'data-ajax-load': 'true',
                                        'data-ajax-url-tmpl': 'care-coordination/contacts',
                                        'data-target': '#contactsTabContent',
                                        'html': [
                                            {'<>': 'span', 'text': 'Contacts'}
                                        ]
                                    }
                                ]},
                                {'<>': 'li', 'id': 'communitiesTab', 'role': 'presentation', 'html': [
                                    {
                                        '<>': 'a',
                                        'role': 'tab',
                                        'href': context + '/care-coordination/templates/communities',
                                        'data-ajax-load': 'true',
                                        'data-ajax-url-tmpl': 'care-coordination/templates/communities',
                                        'data-target': '#communitiesTabContent',
                                        'html': [
                                            {'<>': 'span', 'text': 'Communities List'}
                                        ]
                                    }
                                ]}
                            ]},
                            {'<>': 'div', 'class': 'tab-content', 'html': [
                                {'<>': 'div', 'id': 'patientsTabContent', 'role': 'tabpanel', 'class': 'tab-pane patientsTabContent', 'html': []},
                                {'<>': 'div', 'id': 'eventsLogTabContent', 'role': 'tabpanel', 'class': 'tab-pane', 'html': []},
                                {'<>': 'div', 'id': 'communitiesTabContent', 'role': 'tabpanel', 'class': 'tab-pane', 'html': []},
                                {'<>': 'div', 'id': 'contactsTabContent', 'role': 'tabpanel', 'class': 'tab-pane', 'html': []},
                                {'<>': 'div', 'id': 'organizationsTabContent', 'role': 'tabpanel', 'class': 'tab-pane', 'html': []}
                            ]}
                        ]}
                    ]}
                ]}
            ]}
        ]};
    }*/

    return {
        init: function (url) {
            cFragmentUrl = url;
            this.renderHolder();

            this.loadFragment({
                onFragmentLoaded: function () {
                    _prepare();
                },
                onResourcesLoaded: function () {
                    var fragment = _getFragment();

                    fragment.widgets = {};
                    this.setEvents();
                    fragment.inited = true;
                    this.render();
                    this.show();
                    startListening();
                    this.loaded();
                }
            });
        },

        update: function (url) {
            if (url) {
                cFragmentUrl = url;
            }
            this.loaded();
        },

        getFragment: function (url) {
            return fragmentsMap[_stringifyUrl(url, {params: true})];
        },

        isFragmentInited: function (url) {
            var fragment = this.getFragment(url);
            return fragment && fragment.inited;
        },

        loadFragment: function (callbacks) {
            var self = this;
            loader.load({
                url: cFragmentUrl,
                callbacks: {
                    onFragmentLoaded: function (fragment) {
                        state.reloadNeeded = false;

                        var $fragment = $(fragment).find('#content .markup-frg');

                        var urlTmpl = _stringifyUrl(cFragmentUrl, {params: true});
                        fragmentsMap[urlTmpl] = {
                            $html: $fragment,
                            url: mdlUtils.clone(cFragmentUrl)
                        };

                        callbacks.onFragmentLoaded.apply(self);
                    },
                    onResourcesLoaded: function () {
                        callbacks.onResourcesLoaded.apply(self);
                    }
                }
            });
        },

        render: function () {
            var $moduleContent = _getFragment().$html;
            _getFragmentHolder().empty();
            _getFragmentHolder().append($moduleContent);
        },

        renderHolder: function () {
            if (document.getElementById(_getFragmentHolderId()))
                return;

            var $holder = $('<div/>');
            $holder.attr('id', _getFragmentHolderId());
            $holder.hide();

            $('#content').append($holder);
        },

        hide: function () {
            _getFragmentHolder().hide();
            $('#content').addClass('loading');
        },

        show: function () {
            if ($('#content > div:visible').length == 0) {
                _getFragmentHolder().show();
            }
            $('#content').removeClass('loading');
        },

        setEvents: function () {

            ExchangeApp.managers.EventManager.subscribe('community_list_changed', function() {
                updateCommunityCount();
            });

            ExchangeApp.managers.EventManager.subscribe('patients_list_changed', function() {
                updatePatientsCount();
            });

            return false;
        },

        reloadNeeded: function () {
            return state.reloadNeeded;
        },

        loaded: function () {
            $.each($('.baseHeader a.ldr-head-lnk.active'), function () {
                $(this).removeClass('active');
                $(this).removeClass('bottom');
            });

            $('.baseHeader a.ldr-head-lnk.careCoordinationLnk').addClass('active bottom');
        },

        routeToCommunityList: function() {
            $find("#communitiesTab").find('a').click()
        }

    };
})();