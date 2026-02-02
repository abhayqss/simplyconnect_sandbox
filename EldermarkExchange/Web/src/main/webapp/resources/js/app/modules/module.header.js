ExchangeApp.modules.Header = (function () {

    var fragmentsMap = {};

    var cFragmentUrl = {
        template: 'header'
    };

    var state = {
        reloadNeeded: false
    };

    var router = ExchangeApp.routers.ModuleRouter;

    var mdlUtils = ExchangeApp.utils.module;

    var loader = ExchangeApp.loaders.FragmentLoader.init('header');

    var ORGANIZATION_SELECT_LIST_URL = 'care-coordination/organizations/selectList';
    var ORGANIZATION_CHANGE_ORG_URL = 'care-coordination/admin/databaseId';
    var COMMUNITY_SELECT_LIST_URL = 'care-coordination/communities/selectList';
    var COMMUNITY_CHANGE_ORG_URL = 'care-coordination/admin/communityId';
    var currentOrganizationFilter;
    var currentCommunityFilter;

    var currentUserId;

    var communityChangeTimerId = null;

    /*-------------utils----------------*/

    function _prepare(){
        var cFragment = _getFragment();
        cFragment.$holder = $('#header');
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

    function _getFragment(){
        return fragmentsMap[_stringifyUrl(cFragmentUrl, {params: true})];
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

    function _underlineTabOnLoad() {
        var urlTemplate =  ExchangeApp.routers.ModuleRouter.getUrlTemplate();
        if (urlTemplate.indexOf('secure-messaging') > -1) {
            $find('.secureMsgLnk').addClass('bottom active');
        } else if (urlTemplate.indexOf('patient') > -1) {
            $find('.personalHealthRecordLnk').addClass('bottom active');
        } else if (urlTemplate.indexOf('care-coordination') > -1) {
            $find('.careCoordinationLnk').addClass('bottom active');
        } else if (urlTemplate.indexOf('reports') > -1) {
            $find('.reportsLnk').addClass('bottom active');
        } else if (urlTemplate.indexOf('administration') > -1) {
            $find('.administrationLnk').addClass('bottom active');
        }
        // else if (urlTemplate.indexOf('marketplace222') > -1) {
        //     $find('.marketplaceLnk').addClass('bottom active');
        // }
        updateSubMenu();
    }

    function updateSubMenu() {

        var urlTemplate =  ExchangeApp.routers.ModuleRouter.getUrlTemplate();


        var panel = $find('#subMenu');
        var communityLabel = $find('#subMenuCommunityLabel');
        var communityInput = $find('#subMenuCommunityInput');
        var orgLabel = $find('#subMenuOrgLabel');
        var orgInput = $find('#subMenuOrgInput');

        if (!panel) return;


        var showPanel = true;
        var showOrgList = true;
        var showCommunityList = true;

        if (urlTemplate.indexOf('care-coordination')===-1) {
            showPanel=false;
            showOrgList = false;
            showCommunityList = false;
        } else if (urlTemplate.indexOf('organizations')!==-1) {
            showOrgList = false;
            showCommunityList = false;
        } else if (urlTemplate.indexOf('contacts')!==-1) {
            //showOrgList = false;
            showCommunityList = false;
        } else if (urlTemplate.indexOf('communities')!==-1) {
            showCommunityList = false;
        }

        if (urlTemplate.endsWith('care-coordination')) {
            communityInput = null;
            orgInput=null;
        }

        if (showPanel===true) {
            panel.show();
            if (communityInput) {
                if (showCommunityList) {
                    communityLabel.css('display','table-cell');
                    communityInput.css('display','table-cell');
                } else {
                    communityLabel.hide();
                    communityInput.hide();
                }
            }
        //$find('#availableOrganizationChooser > option').length>1
            if (orgInput) {
                if (showOrgList) {
                    orgLabel.css('display','table-cell');
                    orgInput.css('display','table-cell');
                } else {
                    orgLabel.hide();
                    orgInput.hide();
                }
            }

        } else {
            panel.hide();
        }

    }

    function initAndUpdateOrgList() {
        var orgInput = $find('#availableOrganizationChooser');
        if (orgInput.length !== 0) {
            $.ajax({
                type: 'GET',
                contentType: 'json',
                url: ORGANIZATION_SELECT_LIST_URL,
                success: function (data) {

                    orgInput.empty();
                    /*orgInput.append($("<option />").val(0).text('Not Chosen'));*/
                    for (var i in data) {
                        var item = data[i];
                        var option = $("<option />").attr('value', item.id).text(item.label);
                        if (item.selected) {
                            option.attr('selected', 'selected');
                            currentOrganizationFilter = item.id;
                        }
                        orgInput.append(option);
                    }
                    _abbreviate(orgInput.find('option:selected'), 230);
                    orgInput.selectpicker('refresh');
                    //if (data.length <=1) {
                    //    orgInput.parent().hide();
                    //}
                }
            });
        }
    }

    function initAndUpdateCommunityList() {
        var communityInput = $find('#availableCommunitiesChooser');
        if (communityInput) {
            $.ajax({
                type: 'GET',
                contentType: 'json',
                url: COMMUNITY_SELECT_LIST_URL,
                success: function(data){
                    communityInput.empty();
                    currentCommunityFilter = [];
                    for (var i in data) {
                        var item = data[i];
                        var option = $("<option />").attr('value',item.id).text(item.label);
                        if (item.selected) {
                            option.attr('selected', 'selected');
                            communityInput.push(item.id);
                        }
                        communityInput.append(option);
                    }
                    communityInput.selectpicker('refresh');
                    communityInput.trigger('chosen:updated');
                }
            });
        }
    }

    function _truncateTextToWidth(text, width) {
        var textToTruncate = text;
        var testWidthSpan = $find("#testWidthSpan");
        testWidthSpan.html(textToTruncate);
        var textWidth = testWidthSpan.width();
        var addDots = false;
        while(textWidth > width) {
            addDots = true;
            textToTruncate = textToTruncate.substr(0, textToTruncate.length - 1);
            testWidthSpan.html(textToTruncate);
            textWidth = testWidthSpan.width();
        }
        if(addDots){
            textToTruncate = textToTruncate + "...";
        }
        return textToTruncate;
    }

    function _abbreviate($option, maxWidth) {
        var selectedText = $option.text();
        selectedText = _truncateTextToWidth(selectedText, maxWidth);
        $option.text(selectedText);
    }

    return {
        init: function (url) {
            cFragmentUrl = url;

            this.loadFragment({
                onFragmentLoaded: function () {
                    _prepare();
                },
                onResourcesLoaded: function(){
                    var fragment = _getFragment();

                    fragment.widgets = {};

                    this.setEvents();
                    _underlineTabOnLoad();

                    fragment.inited = true;

                    this.render();
                }
            });
        },

        update: function (url) {
            cFragmentUrl = url;
        },

        getFragment: function(url){
            return  fragmentsMap[_stringifyUrl(url, {params: true})];
        },

        isFragmentInited: function(url){
            var fragment = this.getFragment(url);
            return fragment && fragment.inited;
        },

        loadFragment: function(callbacks){
            var self = this;
            loader.load({
                url: cFragmentUrl,
                callbacks: {
                    onFragmentLoaded: function(fragment){
                        state.reloadNeeded = false;

                        var $fragment = $(fragment).find('#header .markup-frg');

                        var urlTmpl = _stringifyUrl(cFragmentUrl, {params: true});
                        fragmentsMap[urlTmpl] = {
                            $html: $fragment,
                            url: mdlUtils.clone(cFragmentUrl)
                        };

                        callbacks.onFragmentLoaded.apply(self);
                    },
                    onResourcesLoaded: function(){
                        callbacks.onResourcesLoaded.apply(self);
                    }
                }})
        },

        render: function () {
            var fragment = _getFragment();
            if (fragment && fragment.$holder) {
                fragment.$holder.empty();
                fragment.$holder.append(fragment.$html);
            }
        },

        hide: function () {
            var fragment = _getFragment();
            if(fragment){
                fragment.$holder.hide();
            }
        },

        show: function () {
            var fragment = _getFragment();
            if (fragment && fragment.$holder) {
                fragment.$holder.show();
            }
        },

        setEvents: function () {
            $find('.logOut').on('click', function() {
                $.ajax({
                    type: 'GET',
                    url: 'session-active',
                    success: function(data){
                        $find('#logoutForm').submit();
                        $.ajax({
                            type: "GET",
                            url: window.auditReportLogoutUrl
                        });
                    },
                    error: function(err) {
                        mdlUtils.onAjaxError(err);
                    }
                });
                return false;
            });

            $find('.ldr-head-lnk').on('click', function(){
                $find('.ldr-head-lnk.active').removeClass('bottom active');
                $(this).addClass('bottom active');
            });

            $find('.userOptions .option').on('click', function(){
                $find('.userOptions').removeClass('open');
            });

            $find('.manageSES').on('click', function(){
                var $tab = $find('.secureMsgLnk');
                if (!$tab.hasClass('bottom active')) {
                    $find('.ldr-head-lnk.active').removeClass('bottom active');
                    $tab.addClass('bottom active');
                }
            });

            $.ajax({
                type: 'GET',
                contentType: 'json',
                url: $find('#unreadInboxCountUrl').attr('href'),
                success: function(data){
                    $find('#secureMessageTotal').toggleClass('hidden', !data);
                    $find('#secureMessageTotal').text(data);
                }
            });

            $.ajax({
                type: 'GET',
                contentType: 'json',
                url: $find('#employeInfoUrl').attr('href'),
                success: function(data){
                    $find('.user').text(data.fullName);
                    if (data.roleDisplayName) {
                        $find('.roleLabel').text(data.roleDisplayName);
                    }
                    currentUserId = data.id;
                    if (data.logoUrl) {
                        $find('#mainLogoImage').attr('src', "resources/images/internal/"+data.logoUrl).attr('height',70); //.attr('width', 149)
                        $find('#sponsoredLogoHeader').show();
                    } else {
                        $find('#defaultLogoHeader').show();
                    }
                    if (data.alternativeLogoUrl) {
                        $find('#altLogoImage').attr('src', "resources/images/internal/"+data.alternativeLogoUrl);
                        $find('#altLogoBlock').css('display', "table-cell");
                    }
                }
            });

            //-- Care Coordination Organizations and Communities
            var orgInput = $find('#availableOrganizationChooser');
            if (orgInput.length!=0) {
                initAndUpdateOrgList();
                orgInput.on('change',function( events, selector , data, handler ) {
                    _abbreviate(orgInput.find('option:selected'), 230);
                    $.ajax({
                        type: 'POST',
                        contentType: 'json',
                        url: ORGANIZATION_CHANGE_ORG_URL+"?databaseId="+orgInput.val(),
                        beforeSend: function(xhr){
                            mdlUtils.csrf(xhr);
                        },
                        success: function(data){
                            if (data.showCommunitiesTab==true && !$('[id^=communitiesTab]').is(":visible")) {
                                $('[id^=communitiesTab]').show();
                            } else if (data.showCommunitiesTab==false && $('[id^=communitiesTab]').is(":visible")) {
                                $('[id^=communitiesTab]').hide();
                            }
                            currentOrganizationFilter = orgInput.val();
                            if (null === router.getUrlTemplate()) {
                                // url template is null for a click on item in organization list
                                router.reload();
                            } else {
                                router.reload({template: router.getUrlTemplate()});
                            }
                            ExchangeApp.managers.EventManager.publish('community_list_changed');
                            ExchangeApp.managers.EventManager.publish('patients_list_changed');
                        },
                        error: function (error) {
                            mdlUtils.onAjaxError(error);
                        }
                    });
                    ExchangeApp.managers.EventManager.publish('org_and_community_settings_changed', {
                        org: orgInput.val()
                    });
                });
            }
            var communityInput = $find('#availableCommunitiesChooser');
            if ((communityInput)&&(communityInput.length!=0)) {
                //communityInput.chosen({
                //    width: '350px'
                //});
                //communityInput.selectpicker();
                initAndUpdateCommunityList();

                communityInput.on('changed.bs.select', function (e, clickedIndex, newValue, oldValue) {
                //communityInput.on('change',function( events, selector , data, handler ) {
                    var communityInputVal = $(this).val();
                    if (!communityInputVal || (clickedIndex ==0 && newValue)) {
                        //$(this).selectpicker('deselectAll');
                        $(this).selectpicker('val', 0);
                        $(this).selectpicker('refresh');
                    }
                    else if(clickedIndex>0 && newValue) {
                        $(this).find('[value=0]').prop('selected', false);
                        $(this).selectpicker('refresh');
                    }

                    //if (!communityInputVal) {
                    //    $(this).selectpicker('val', 0);
                    //    $(this).selectpicker('refresh');
                    //    //communityInput.val('0');
                    //    //communityInput.trigger('chosen:updated');
                    //}
                    //else if ((communityInputVal.length>1)&&(communityInputVal.indexOf('0')!=-1)) {
                    //    communityInputVal.splice(communityInputVal.indexOf('0'),1)
                    //    communityInput.val(communityInputVal);
                    //    communityInput.trigger('chosen:updated');
                    //}
                    if (communityChangeTimerId) clearTimeout(communityChangeTimerId);
                    communityChangeTimerId = setTimeout(function() {    //??????????????????????????????
                        $.ajax({
                            type: 'POST',
                            contentType: 'json',
                            url: COMMUNITY_CHANGE_ORG_URL + "?communityId=" + communityInput.val(),
                            beforeSend: function(xhr){
                                mdlUtils.csrf(xhr);
                            },
                            success: function (data) {
                                currentCommunityFilter = communityInput.val();
                                router.reload({template: router.getUrlTemplate()});
                                ExchangeApp.managers.EventManager.publish('patients_list_changed');
                            },
                            error: function (error) {
                                mdlUtils.onAjaxError(error);
                            }
                        });

                        ExchangeApp.managers.EventManager.publish('org_and_community_settings_changed', {
                            community: communityInputVal
                        });
                    }, 300);
                });
            }



            var updateSecureEmail = function() {
                $.ajax({
                    type: 'GET',
                    contentType: 'json',
                    url: $find('#employeeSecureEmailUrl').attr('href'),
                    success: function(data){
                        $find('.email').text(data);
                    },
                    error: function(response){
                        mdlUtils.onAjaxError(response, function() {
                            $find('.email').text(response.responseText);
                        });
                    }
                });
            };

            var updateInboxCount = function() {
                $.ajax({
                    type: 'GET',
                    contentType: 'json',
                    url: $find('#unreadInboxCountUrl').attr('href'),
                    success: function(data){
                        $find('#secureMessageTotal').toggleClass('hidden', !data);
                        $find('#secureMessageTotal').text(data);
                    }
                });
            };

            updateSecureEmail();
            updateInboxCount();

            ExchangeApp.managers.EventManager.subscribe('inbox_count_changed', function() {
                updateInboxCount();
            });

            ExchangeApp.managers.EventManager.subscribe('messaging_setup_changed', function() {
                updateInboxCount();
                updateSecureEmail();
            });

            ExchangeApp.managers.EventManager.subscribe('page_changed', function() {
                updateSubMenu();
            });

            ExchangeApp.managers.EventManager.subscribe('community_list_changed', function() {
                initAndUpdateCommunityList();
            });

            ExchangeApp.managers.EventManager.subscribe('org_list_changed', function() {
                initAndUpdateOrgList();
            });

            ExchangeApp.managers.EventManager.subscribe('change_org_list', function(e) {
                console.log("change org list ", e);
            });


        },

        reloadNeeded: function() {
            return state.reloadNeeded;
        },

        getCurrentOrganizationFilter: function() {
            return currentOrganizationFilter;
        },
        getCurrentCommunityFilter: function() {
            return currentCommunityFilter ? currentCommunityFilter.slice(0) : undefined;
        },
        setCurrentOrg: function(orgId) {
            $find('#availableOrganizationChooser').val(orgId).change();
        },
        showCurrentOrg: function() {
            $find('#subMenuOrgLabel').show();
            $find('#subMenuOrgInput').show();
        },
        addOptionToOrganizationChooserIfAbsent: function (orgId, orgName) {
            var orgInput = $find('#availableOrganizationChooser');
            if (orgInput.has('option[value="' + orgId + '"]').length === 0) {
                orgInput.append($('<option>', {
                    value: orgId,
                    text: orgName
                }));
            }
        },
        hideCurrentOrg: function() {
            $find('#subMenuOrgLabel').hide();
            $find('#subMenuOrgInput').hide();
        },
        getCurrentUserId: function() {
            return currentUserId;
        }
    };
})();