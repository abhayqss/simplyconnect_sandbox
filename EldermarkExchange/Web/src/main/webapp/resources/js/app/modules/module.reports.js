ExchangeApp.modules.Reports = (function () {

    var fragmentsMap = {};

    var cFragmentUrl = {
        template: 'reports'
    };

    var state = {
        reloadNeeded: false
    };

    var router = ExchangeApp.routers.ModuleRouter;

    var mdlUtils = ExchangeApp.utils.module;
    var wgtUtils = ExchangeApp.utils.wgt;

    var loader = ExchangeApp.loaders.FragmentLoader.init('reports');

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

    function _grid(options) {
        var fragment = _getFragment();
        return wgtUtils.grid(fragment.$html, fragment.random, options);
    }

    function _alert(options) {
        var fragment = _getFragment();
        return wgtUtils.alert(fragment.$html, fragment.random, options);
    }

    function _getFragment() {
        return fragmentsMap[_stringifyUrl(cFragmentUrl, {params: true})];
    }

    function _getFragmentHolderId() {
        return _stringifyUrl(cFragmentUrl, {params: true}).replace(/[/&\?=]/g, '_');
    }

    function _getFragmentHolder() {
        return $(document.getElementById(_getFragmentHolderId()));
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

    function _initMenu() {
        var kyubitUrl = $find('#baseAddr').val();
        var microsoftUrl = $find('#SSRSUrl').val();
        $.ajax({
            url: 'reports/get-menu/'
        })
            .success(function (data) {
                var $menu = $find("#reportsMenu");
                $menu.treeview({
                    data: data,
                    showBorder: false,
                    borderColor: 'white',
                    selectedColor: '#009288',
                    selectedBackColor: '#ffffff',
                    onhoverColor: '#ffffff',
                    showIcon: false,
                    enableLinks: false,
                    expandCollapseIconPosition: 'right',
                    expandIcon: 'glyphicon glyphicon-triangle-right',
                    collapseIcon: 'glyphicon glyphicon-triangle-bottom',
                    onNodeSelected: function (event, data) {
                        if (data.href) {
                            var $iframe = $find("#reportFrame");
                            var path;
                            if (data.server == 'kyubit') {
                                path = kyubitUrl + '/Forms/Dashboard.aspx?DashboardID=' + data.href.substr(1) + '&align=left';
                                $iframe.attr('src', path);
                            } else {
                                path = microsoftUrl + ':444/ReportServer/Pages/ReportViewer.aspx' + data.href + '&rs:Command=Render&rc:stylesheet=HideToolBar';
                                $iframe.attr('src', path);
                            }
                        }
                    }
                });
            })
            .fail(function () {
                alert('Internal server error. Please contact administrator.');
            });
    }

    function _logIn() {
        var iframeName = 'kyubit_login_iframe';
        var iframe = document.createElement('iframe');
        iframe.name = iframeName;
        iframe.setAttribute("style", 'width: 0;height:0;overflow: hidden');
        document.body.appendChild(iframe);
        var form = $find("#loginForm").clone();
        document.body.appendChild(form.get(0));
        iframe.onload = function () {
            iframe.remove();
            $find("#loginForm").remove();
            $find("#reportsMenu").treeview('selectNode', [1]);
        };

        form.submit();
    }

    /*-------------widgets----------------*/


    return {
        init: function (url) {
            cFragmentUrl = url;
            this.renderHolder();
            this.loadFragment({
                onFragmentLoaded: function () {
                    _prepare();
                },
                onResourcesLoaded: function () {
                    var cFrg = _getFragment();
                    // initialize widgets
                    cFrg.widgets = {};
                    _initMenu();
                    //_initReport();
                    _logIn();
                    this.render();
                    this.show();
                    cFrg.inited = true;
                    this.loaded();
                }
            });
        },
        update: function (url) {
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

        loaded: function () {
            $.each($('.baseHeader .ldr-head-lnk.active'), function () {
                $(this).removeClass('active');
                $(this).removeClass('bottom');
            });
            var link = $('.baseHeader .ldr-head-lnk.reportsLnk');
            link.addClass('active');
            link.addClass('bottom');
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

        getFragment: function (url) {
            return fragmentsMap[_stringifyUrl(url, {params: true})];
        },

        isFragmentInited: function (url) {
            var fragment = this.getFragment(url);
            return fragment && fragment.inited;
        },

        reloadNeeded: function () {
            return state.reloadNeeded;
        }
    };
})();