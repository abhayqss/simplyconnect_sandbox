ExchangeApp.modules.CareCoordinationContacts = (function () {
    // history of loaded segments
    var fragmentsMap = {};
    // root url
    var cFragmentUrl = {
        template: 'care-coordination/contacts'
    };
    // flag if true then reloaded every time
    var state = {
        reloadNeeded: true
    };
    var holderContentId = "contactsTabContent";
    // load root routers (map url to module)
    var router = ExchangeApp.routers.ModuleRouter;
    // Utility
    var mdlUtils = ExchangeApp.utils.module;
    var wgtUtils = ExchangeApp.utils.wgt;

    var loader = ExchangeApp.loaders.FragmentLoader.init('careCoordinationContacts');
    var pModule;


    var currentOrganizationFilter;


    /*-------------utils----------------*/

    function _prepare() {
        var cFragment = _getFragment();
        cFragment.random = mdlUtils.rand();
        _randomize();
        // _ajaxify();
    }

    function _grid(options) {
        var fragment = _getFragment();
        return wgtUtils.grid(fragment.$html, fragment.random, options);
    }

    function _getFragment() {
        return fragmentsMap[_stringifyUrl(cFragmentUrl, {params: true})];
    }

    function _getFragmentHolder() {
        return $('[id^="' + _getFragmentHolderId() + '"]');
    }

    function _getFragmentHolderId() {
        return holderContentId;
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

    function _addCreateContactValidation($container) {
        return $container.find("#contactForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    //
                    'email': {required: true, maxlength: 255, emails: true},
                    'firstName': {required: true, minlength: 2, maxlength: 128},
                    'lastName': {required: true, minlength: 2, maxlength: 128},
                    'role.id': {required: true},
                    'address.street': {required: true, minlength: 2, maxlength: 255},
                    'address.city': {required: true, minlength: 2, maxlength: 128},
                    'address.state.id': {required: true},
                    'address.zip': {required: true, lengthEqual: 5},
                    'phone': {required: true, phone:true},
                    'fax': {phone:true}
                },
                messages: {
                    'firstName': {
                        required: getErrorMessage("field.empty")
                    },
                    'lastName': {
                        required: getErrorMessage("field.empty")
                    },
                    phone: {
                        phone: getErrorMessage("field.phone.format")
                    },
                    fax: {
                        phone: getErrorMessage("field.phone.format")
                    },
                    state: {
                        stateUS: getErrorMessage("field.state")
                    }
                }
            })
        );
    }

    function _initCreateEditContactDialog($container, contactId, viewOnly, $button) {
        if ($button.hasClass("pending")) {
            return;
        }
        $button.addClass("pending");
        $.ajax('care-coordination/contacts/contact/' + contactId)
            .success(function (data) {
                $container.empty();
                $container.append(data);
                var $modal = $container.find('#createContactModal');
                var headerTitle;
                if (viewOnly) {
                    $container.find('input,select').prop("disabled", true);
                    $container.find('.btn-group').hide();
                    $container.find('#faxHelp').hide();
                    headerTitle = 'View Contact';
                }
                else {
                    _addCreateContactValidation($container);
                    var expired = $container.find("#expired").attr("data-value") == "true";
                    var $saveButton = $container.find("#saveContact");
                    if (!expired) {
                        headerTitle = (contactId ? 'Edit' : 'Create') + ' Contact';
                        $saveButton.html(contactId ? "SUBMIT" : "SEND INVITE");
                    }
                    else {
                        headerTitle = "Expired Contact";
                        $saveButton.html("RE-INVITE USER");
                    }
                    // ============      Save button =========================
                    $saveButton.on('click', function () {

                        var $form = $container.find("#contactForm");
                        if (!$form.valid()) {
                            return false;
                        }


                        if (!expired) {
                            if (!$form.find("#enabledExchangeId").is(":checked") && $form.find("#contact\\.secureMessaging").val()) {
                                var $warnMsg = $form.find("#phrWarning");
                                if (!$warnMsg.is(":visible")) {
                                    $form.find("#phrWarning").show();
                                    return false;
                                }
                            }

                            $.ajax({
                                url: 'care-coordination/contacts/contact/' + contactId,
                                type: 'POST',
                                data: $form.serialize(),
                                beforeSend: function(xhr){
                                    mdlUtils.csrf(xhr);
                                }
                            }).success(function (data) {
                                _getFragment().widgets.contactsList.api().ajax.reload();
                                $modal.modal('hide');
                                $('.modal-backdrop').remove();
                            }).fail(function (response) {
                                mdlUtils.onAjaxError(response, function () {
                                    $("#formError").text(response.responseText);
                                });
                            });
                        }
                        else {
                            $.ajax({
                                url: 'care-coordination/contacts/contact/' + contactId + '/sendNewInvitation',
                                type: 'POST',
                                beforeSend: function(xhr){
                                    mdlUtils.csrf(xhr);
                                }
                            }).success(function (data) {
                                _getFragment().widgets.contactsList.api().ajax.reload();
                                $modal.modal('hide');
                                $('.modal-backdrop').remove();
                                bootbox.alert('A new invitation has been sent');
                            }).error(function (data) {
                                mdlUtils.onAjaxError(response, function () {
                                    $("#formError").text(response.responseText);
                                });
                            });
                        }
                        return false;
                    });

                    var $orgSelector = $("#contact\\.organization");
                    var unaffiliatedOrgId = $("#unafilliatedDatabaseId").val();
                    $orgSelector.change(function() {
                        $.ajax({
                            url: 'care-coordination/communities/byorg/'+$orgSelector.val(),
                            type: 'GET'
                        }).success(function (data) {
                            var $communitySelector = $("#contact\\.community");
                            $communitySelector.empty();
                            $.each(data.content, function (i, e) {
                                $communitySelector.append('<option value="' + e.id + '">' + e.name + '</option>');
                            });
                            if (unaffiliatedOrgId == $orgSelector.val()) {
                                $("#enabledExchangeId").prop( "checked", false);
                                $("#enabledExchangeId").prop( "disabled", true);
                            } else {
                                $("#enabledExchangeId").prop( "disabled", false);
                            }
                        }).fail(function (response) {
                            $("#formError").text(response.responseText);
                        });
                    });
                    
                    // system role on change event
                    var $roleSelector = $("#contact\\.role\\.id");                    
                    function roleChangeEvent(){
                    	var  selectNotQARole= ["Parent/Guardian", "Person Receiving Services"]
                    	if(selectNotQARole.includes($roleSelector.find("option:checked").text())){
                    		$("#qaIncidentReportsId").prop("checked", false);
                    		$("#qaIncidentReportsId").attr("disabled", true);
                    	}else{
                    		$("#qaIncidentReportsId").removeAttr("disabled");
                    	} 
                    }                    
                    roleChangeEvent();
                    $roleSelector.change(function(){                    	
                    	roleChangeEvent();
                    });

                    $("#faxHelp").popover();
                    $modal.find("#secureMessagingHelp1").popover();
                    $modal.find("#secureMessagingHelp2").popover();
                }

                $container.find("#contactHeader").html(headerTitle);

                $modal.find("#secureMessagingError1").popover({
                    html : true,
                    content : function() {
                        return $("#secureMessagingError1-content").html();
                    }
                });
                $modal.find("#secureMessagingError2").popover();
                $container.find('#createContactModal').modal({backdrop: 'static'});
                $container.find('#createContactModal').on('hidden.bs.modal', function () {
                    $(this).remove();
                });
                $button.removeClass("pending");
            })
            .fail(function () {
                $button.removeClass("pending");
                alert('Internal server error. Please contact administrator.');
            });	        
    }

    function _initContactsList() {
        var $createContactContainer = $find('#createContactContainer');

        _getFragment().widgets.contactsList = _grid({
            tableId: "contactsList",
            searchFormId: "contactsFilter",
            totalDisplayRows: 25,
            colSettings: {
                'actions': {width: '50px', 'className': 'dt-head-center'},
                'email' : {orderable : false},
                'phone' : {width: '100px', orderable : false}
            },
            // order by Name, then by Status
            order: [[0, 'asc'], [2, 'asc']],
            callbacks: {
                rowCallback: function (row, data, index) {
                    var $row = $(row);
                    if (data) {
                        var contactId = data.id;
                        var $actionsTd = $row.children('td:eq(5)');
                        $actionsTd.empty();
                        if (data.editable || data.viewOnly) {
                            var $tdRowActions = _getFragment().$html.find('.rowActions.hidden').clone();
                            var tdRowActionsId = 'rowActions-' + index;
                            $tdRowActions.attr('id', tdRowActionsId);
                            $tdRowActions.removeClass('hidden');
                            $tdRowActions.find('.editContact').on('click', function () {
                                  _initCreateEditContactDialog($createContactContainer, contactId,data.viewOnly,$(this));
                                }
                            );
                            if (data.editable) {
                                $tdRowActions.find('.glyphicon').addClass("glyphicon-pencil");
                            }
                            else if (data.viewOnly){
                                $tdRowActions.find('.glyphicon').addClass("glyphicon-eye-open");
                            }
                            $actionsTd.append($tdRowActions);
                        } else {
                            $actionsTd.css('height','32px');
                        }
                    }
                },
                errorCallback: function (error) {
                    if (error.status==403){
                        // var pathContext = location.pathname.match(/\/\w+/);
                        // var context = (pathContext) ? pathContext[0] : "";
                        // window.location.href = context + '/login?invalid-session=true';
                        window.location.href = 'login?invalid-session=true';
                    }
                    else {
                        alert(error.responseText);
                    }
                },
                footerCallback: function (tfoot, data, start, end, display) {
                    $(tfoot).hide();
                }
            }
        });
        _getFragment().widgets.contactsList.api().ajax.reload();
        currentOrganizationFilter = ExchangeApp.modules.Header.getCurrentOrganizationFilter();
    }

    $.fn.serializeObject = function () {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function () {
            if (o[this.name] !== undefined) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    };


    // ====================================
    return {
        init: function (url, parentModule) {
            cFragmentUrl = url;
            pModule = parentModule;

            this.loadFragment({
                onFragmentLoaded: function () {
                    _prepare();
                },
                onResourcesLoaded: function () {
                    var fragment = _getFragment();

                    fragment.widgets = {};
                    _initContactsList();

                    this.setEvents();

                    fragment.inited = true;

                    this.render();
                    this.show();
                }
            });
        },

        update: function (url) {

            if (currentOrganizationFilter != ExchangeApp.modules.Header.getCurrentOrganizationFilter()) {
                ExchangeApp.routers.ModuleRouter.reload();
            }


            cFragmentUrl = url;
            //$.each($('#care-coordination').find('.nav li'), function(){$(this).removeClass('active')});
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

                        var $fragment = $(fragment).find('#content').children();

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

        hide: function () {
            _getFragmentHolder().hide();
            $('#content').addClass('loading');
        },

        show: function () {
            // activate tab
            if ($('#content').find(".tab-pane:visible").length == 0) {
                $.each($('#care-coordination').find('.nav li'), function () {
                    $(this).removeClass('active');
                });
                $('a[data-target^="#' + holderContentId + '"]').parent('li').addClass('active');
                // render content
                _getFragmentHolder().show();
            }
            $('#content').removeClass('loading');
        },

        setEvents: function () {

            $find("#contactSearch").on('click', function () {
                _getFragment().widgets.contactsList.api().order([[0, 'asc'], [2, 'asc']]);
                _getFragment().widgets.contactsList.api().ajax.reload();
                return false;
            });

            $find("#contactSearchClear").on('click', function () {
                $find("#contactsFilter").clearForm();
                return false;
            });

            $find("#roleId, #status").select2({
                width: '100%'
                , minimumResultsForSearch: Infinity
            });

            $find("#createContact").on('click', function () {
               _initCreateEditContactDialog($find('#createContactContainer'), 0, false, $(this));
                return false;
            });

            return false;

        },

        reloadNeeded: function () {
            return state.reloadNeeded;
        },
        getParentModule: function () {
            return pModule;
        }
    };


})();