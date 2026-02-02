ExchangeApp.modules.SecureMessaging = (function () {

    var fragmentsMap = {};

    var cFragmentUrl = {
        template: 'secure-messaging'
    };

    var state = {
        reloadNeeded: false
    };

    var router = ExchangeApp.routers.ModuleRouter;

    var mdlUtils = ExchangeApp.utils.module;
    var wgtUtils = ExchangeApp.utils.wgt;

    var loader = ExchangeApp.loaders.FragmentLoader.init('secureMessaging');

    /*-------------utils----------------*/

    function _prepare(){
        var cFragment = _getFragment();
        cFragment.random = mdlUtils.rand();
        _randomize();
        _ajaxify();
    }

    function _ajaxify($trgFragment, trgRandom){
        var cFragment = _getFragment();

        if(!cFragment.ajaxMap){
            cFragment.ajaxMap = {};
        }

        var $fragment = $trgFragment ? $trgFragment : cFragment.$html;
        var random = trgRandom ? trgRandom : cFragment.random;

        var urls = mdlUtils.findAjaxUrls($fragment, random);
        $.each(urls, function(i, url){
            cFragment.ajaxMap[_stringifyUrl(url, {params: true})] = url;
        });

        mdlUtils.find($fragment, random, '[data-ajax-load="true"]').on('click', function(){
            var template = $(this).attr('data-ajax-url-tmpl');
            var vars = $(this).attr('data-ajax-url-vars');
            var params = $(this).attr('data-ajax-url-params');

            var url = mdlUtils.getUrl(template, vars, params);
            router.route(url);
            return false;
        });

        mdlUtils.find($fragment, random, '[data-ajax-anchor="true"]').on('click', function(){
            var $trg = $($(this).attr('href'));
            if($trg.length){
                $('html, body').animate({ scrollTop: $trg.offset().top}, 200);
            }
            return false;
        });
    }
    
    function _grid(options){
        var fragment = _getFragment();
        return wgtUtils.grid(fragment.$html, fragment.random, options);
    }

    function _alert(options){
        var fragment = _getFragment();
        return wgtUtils.alert(fragment.$html, fragment.random, options);
    }
    
    function _getFragment(){
        return fragmentsMap[_stringifyUrl(cFragmentUrl, {params: true})];
    }

    function _getFragmentHolderId(){
        return _stringifyUrl(cFragmentUrl, {params: true}).replace(/[/&\?=]/g, '_');
    }

    function _getFragmentHolder(){
        return $(document.getElementById(_getFragmentHolderId()));
    }

    function _stringifyUrl(url, excludeOptions){
        return mdlUtils.stringifyUrl(url, excludeOptions);
    }

    function $find(selector) {
        var fragment = _getFragment();
        return mdlUtils.find(fragment.$html, fragment.random, selector);
    }

    function _randomize($trgFragment, trgRandom){
        var cFragment = _getFragment();
        var $fragment = $trgFragment ? $trgFragment : cFragment.$html;
        var random = trgRandom ? trgRandom : cFragment.random;

        $fragment = mdlUtils.randomize($fragment, random, false);
        return $fragment;
    }


    /*-------------widgets----------------*/

    function _initMsgListWgt() {
        _getFragment().widgets.msgList = _grid({
            tableId: 'msgList',
            searchFormId: 'msgFilterForm',
            colSettings: {
                select: {bSortable: false},
                subject: {bSortable: false},
                from: {bSortable: false},
                date: {bSortable: false, width: '70px'}
            },
            language: {
                sProcessing: '<div class="table-loader-msg">Please wait. Retrieving your messages...</div>' +
                    '<div class="table-loader-img"><img src="' + ExchangeApp.info.context + '/resources/images/ajax-loader.gif"></div>'
            },
            totalDisplayRows: 25,
            selectable: {
                type: 'single',
                callback: function (row, data, index) {
                    $find('.msgDetail')
                        .addClass('tab-loading')
                        .children().remove();

                    $find('#msgDetailModal').modal('toggle');

                    $.ajax(
                        {
                            url: 'secure-messaging/' + data.messageId,
                            success: function(view){
                                // randomize
                                var $modalView = mdlUtils.randomize($(view), _getFragment().random, false);

                                //ajaxify
                                mdlUtils.find($modalView, _getFragment().random, '[data-ajax-load="true"]').on('click', function(){
                                    var template = $(this).attr('data-ajax-url-tmpl');
                                    var vars = $(this).attr('data-ajax-url-vars');
                                    var params = $(this).attr('data-ajax-url-params');

                                    var url = mdlUtils.getUrl(template, vars, params);
                                    router.route(url);
                                    return false;
                                });

                                // set events
                                _setMsgDetailEvents($modalView);

                                // show
                                $find('.msgDetail')
                                    .removeClass('tab-loading')
                                    .html($modalView);

                                // mark as seen
                                if (!data.seen) {
                                    $.ajax(
                                        {
                                            url: 'secure-messaging/' + data.messageId + '/seen',
                                            success: function() {
                                                row.removeClass('bold');
                                                //broadcast
                                                ExchangeApp.managers.EventManager.publish('inbox_count_changed');
                                            }
                                        }
                                    );
                                }
                            },
                            error: function (error) {
                                mdlUtils.onAjaxError(error, function(e){
                                    $('.msgDetail').removeClass('tab-loading');
                                    _alert({
                                        action: 'add',
                                        placeSelector: '.msgDetails',
                                        message: e.responseText,
                                        closable: {
                                            timer: 45000,
                                            btn: true
                                        }
                                    });
                                });
                            }
                        }
                    );
                }
            },
            callbacks: {
                drawCallback: function(settings){
                    $find(this).find('input').styler();
                },
                rowCallback: function (row, data, index) {
                    if(!data.seen)
                        $(row).addClass('bold');
                },
                errorCallback: function (error) {
                    mdlUtils.onAjaxError(error, function(e){
                        _alert({
                            action: 'add',
                            placeSelector: '.msgListBox .boxBody',
                            message: e.responseText
                        });
                    });
                },
                footerCallback: function( tfoot, data, start, end, display) {
                    $(tfoot).show();

                    var isHidden = data.length < 20;
                    if(isHidden){
                        $(tfoot).hide();
                    }
                }
            }});
    }

    function _setMsgDetailEvents($modalView) {
        var messageId = [mdlUtils.find($modalView, _getFragment().random, '.msgDetails').attr('data-message-id')];

        mdlUtils.find($modalView, _getFragment().random, '#deleteMsgBtn').on('click', function(){
            $.ajax({
                url: 'secure-messaging/' + messageId + '/delete',
                type: 'GET',
                success: function (success) {
                    _getFragment().widgets.msgList.fnSettings().oInit.store.removeItem(messageId);
                    _getFragment().widgets.msgList.api().ajax.reload();
                    $find('#msgDetailModal').modal('toggle');
                    ExchangeApp.managers.EventManager.publish('inbox_count_changed');
                },
                error: function (error) {
                    mdlUtils.onAjaxError(error, function(e){
                        $find('#msgDetailModal').modal('toggle');
                        _alert({
                            action: 'add',
                            placeSelector: '.msgListBox .boxBody',
                            message: e.responseText,
                            closable: {
                                timer: 45000,
                                btn: true
                            }
                        });
                    });
                }
            });
        });
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
                    var cFrg = _getFragment();

                    // initialize widgets
                    cFrg.widgets = {};
                    _initMsgListWgt();

                    this.setEvents();

                    this.render();
                    this.show();

                    cFrg.inited = true;
                    this.loaded();
                }
            });
        },

        update: function (url) {
            var initiated = false;
            $.ajax({
                    type: 'GET',
                    context:this,
                    url: 'secure-messaging/employee-secure-email-activated',
                    success: function(data){
                        if (!data) {
                         this.init(url);
                        } else {
                            cFragmentUrl = url;
                            var $msgListGrid = _getFragment().widgets.msgList;
                            if ($msgListGrid) {
                                 $msgListGrid.api().ajax.reload();
                            }
                            this.loaded();
                         }
                    },
                    error: function(error) {
                        ExchangeApp.utils.module.onAjaxError(error)
                    }
                });
        },

        loadFragment: function(callbacks){
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
                }})
        },

        loaded:function(){
            $.each($('.baseHeader .ldr-head-lnk.active'), function(){
                 $(this).removeClass('active');
                 $(this).removeClass('bottom');
             });
             var link = $('.baseHeader .ldr-head-lnk.secureMsgLnk');
             link.addClass('active');
             link.addClass('bottom');
        },
        render: function () {
            var $moduleContent = _getFragment().$html;
            _getFragmentHolder().empty();
            _getFragmentHolder().append($moduleContent);
        },

        renderHolder: function() {
            if(document.getElementById(_getFragmentHolderId()))
                return;

            var $holder = $('<div/>');
            $holder.attr('id', _getFragmentHolderId());
            $holder.hide();

            $('#content').append($holder);
        },

        hide: function () {
            _getFragmentHolder().hide();

            var fragment = _getFragment();
            if (fragment) {
                $find('#msgDetailModal').modal('hide');
            }
            $('#content').addClass('loading');
        },

        show: function () {
            if ($('#content > div:visible').length == 0) {
                _getFragmentHolder().show();
            }
            $('#content').removeClass('loading');
        },

        getFragment: function(url){
            return  fragmentsMap[_stringifyUrl(url, {params: true})];
        },

        isFragmentInited: function(url){
            var fragment = this.getFragment(url);
            return fragment && fragment.inited;
        },

        setEvents: function () {
            $find('.secureMsgMenuItem a').on('click', function(){
                var parent = $(this).parent('li');
                parent.addClass('active');
                parent.siblings('li').removeClass('active');

                var value = $(this).attr('href').replace('#','');
                $find("#messageType").val(value.toUpperCase());

                _getFragment().widgets.msgList.api().ajax.reload();
            });

            $find('#deleteMsgsBtn').on('click', function () {
                $.each(_getFragment().widgets.msgList.fnSettings().oInit.store.items, function(index, messageId) {
                    $.ajax({
                        url: 'secure-messaging/' + messageId + '/delete',
                        type: 'GET',
                        success: function (success) {
                            _getFragment().widgets.msgList.fnSettings().oInit.store.removeItem(messageId);
                            _getFragment().widgets.msgList.api().ajax.reload();
                            ExchangeApp.managers.EventManager.publish('inbox_count_changed');
                        },
                        error: function (error) {
                            mdlUtils.onAjaxError(error, function(e){
                                _alert({
                                    action: 'add',
                                    placeSelector: '.msgListBox .boxBody',
                                    message: e.responseText,
                                    closable: {
                                        timer: 45000,
                                        btn: true
                                    }
                                });
                            });
                        }
                    });
                });
            });

            $find('#activateSesBtn').on('click', function(event) {
                $(event.target).prop('disabled', true);

                $.ajax({
                    url: 'secure-messaging/activate',
                    type: 'POST',
                    beforeSend: function(xhr){
                        mdlUtils.csrf(xhr);
                    },
                    success: function (data) {
                        $(event.target).prop('disabled', false);

                        _alert({
                            action: 'add',
                            type: 'alert alert-info',
                            placeSelector: '.msgConfigWarningBox .boxBody',
                            message: 'Your Secure Messaging account was activated successfully.',
                            closable: {
                                btn: true
                            }
                        });

                        window.setTimeout(function () {
                            var router = ExchangeApp.routers.ModuleRouter;

                            var url = mdlUtils.getUrl('secure-messaging', null, null);
                            router.route(url);
                            router.reload();
                        }, 2000);
                    },
                    error: function (error) {
                        $(event.target).prop('disabled', false);

                        mdlUtils.onAjaxError(error, function(e){
                            _alert({
                                action: 'add',
                                placeSelector: '.msgConfigWarningBox .boxBody',
                                message: e.responseText,
                                closable: {
                                    timer: 45000,
                                    btn: true
                                }
                            });
                        });
                    }
                });
            });

            var $msgListGrid = _getFragment().widgets.msgList;
            if ($msgListGrid) {
                $msgListGrid.fnSettings().oInit
                    .store.onCountChanged($find('#deleteMsgsBtn .badge'), function(e, count) {
                        var $badge = $(e.target);
                        if (count > 0) {
                            $badge.find('.badgeValue').html(count);
                            $badge.removeClass('hidden');
                        } else {
                            $badge.find('.badgeValue').html(0);
                            $badge.addClass('hidden');
                        }
                    }
                );
            }

            ExchangeApp.managers.EventManager.subscribe('messaging_setup_changed', function() {
                state.reloadNeeded = true;
            });
        },

        reloadNeeded: function() {
            return state.reloadNeeded;
        }
    };
})();