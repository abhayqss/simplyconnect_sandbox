ExchangeApp.modules.Administration = (function () {
    // history of loaded segments
    var fragmentsMap = {};
    // root url
    var cFragmentUrl = {
        template: 'administration'
    };
    // flag if true then reloaded every time
    var state = {
        reloadNeeded: false
    };
    // load root routers (map url to module)
    var router = ExchangeApp.routers.ModuleRouter;
    // Utility
    var mdlUtils = ExchangeApp.utils.module;

    var loader = ExchangeApp.loaders.FragmentLoader.init('administration');

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
            var target = $(this).attr('data-target');

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

    function _randomize($trgFragment, trgRandom) {
        var cFragment = _getFragment();
        var $fragment = $trgFragment ? $trgFragment : cFragment.$html;
        var random = trgRandom ? trgRandom : cFragment.random;

        $fragment = mdlUtils.randomize($fragment, random, false);
        return $fragment;
    }

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
                    this.loaded();
                }
            });
        },

        loaded: function() {
            $.each($('.baseHeader a.ldr-head-lnk.active'), function() {
                $(this).removeClass('active');
                $(this).removeClass('bottom');
            });

            $('.baseHeader a.ldr-head-lnk.administrationLnk').addClass('active bottom');
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
            })
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
            return false;
        },

        reloadNeeded: function () {
            return state.reloadNeeded;
        }

    };
})();