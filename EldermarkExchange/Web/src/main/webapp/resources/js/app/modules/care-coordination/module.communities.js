ExchangeApp.modules.CareCoordinationCommunities = (function () {

    // history of loaded segments
    var fragmentsMap = {};
    // root url
    var cFragmentUrl = {
        template: 'care-coordination/templates/communities'
    };

    var checkUniquenessUrl = 'care-coordination/communities/isUnique';

    var holderContentId = "communitiesTabContent";
    // flag if true then reloaded every time
    var state = {
        reloadNeeded: false
    };
    // load root routers (map url to module)
    var router = ExchangeApp.routers.ModuleRouter;
    // Utility
    var mdlUtils = ExchangeApp.utils.module;
    var wgtUtils = ExchangeApp.utils.wgt;

    var loader = ExchangeApp.loaders.FragmentLoader.init('careCoordinationCommunities');

    var pModule;

    var currentOrganizationFilter;

    var ActionType = Object.freeze({'ADD': 'create', 'EDIT': 'edit'});

    var planSelectedOptions = new Map();

	var marketplaceService = null;
    ServiceProvider
        .getService("MarketplaceService")
        .then(function(service) {
            marketplaceService = service;
        }).fail(function(error) {
            alert(error);
        });

    /*-------------utils----------------*/

    function _prepare() {
        var cFragment = _getFragment();
        cFragment.random = mdlUtils.rand();
        _randomize();
        _ajaxify();
    }

    function _wizard($container, options) {
        return wgtUtils.wizard($container, '', options);
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

    function _cleanId(id) {
        var fragment = _getFragment();
        if (id) return id.replace(fragment.random, '');
        return id;
    }


    function _clearAlerts() {
        $find('.alert').remove();
    }

    function _get(id) {
        return $find('[id="' + id + _getFragment().random + '"]');
    }

    function _grid(options) {
        var fragment = _getFragment();
        return wgtUtils.grid(fragment.$html, fragment.random, options);
    }


    function _addStyler() {
        $find('input').styler();
    }

    // =======================

    function _initNotificationPreferences(container, data) {
        var $notificationsContainer = container.find("#careTeamMemberNotificationPreferences");
        $notificationsContainer.empty();

        var $rowTemplateAll = container.find("#notificationPreferencesTemplate").clone();
        $rowTemplateAll.attr('id', 'notificationPreferencesTemplate-all');
        $rowTemplateAll.find(".eventType").html("All Events").css('font-style', 'italic');

        var $responsibilityAll = $rowTemplateAll.find(".responsibility");
        $responsibilityAll.append("<option selected disabled hidden style='display: none' value=''></option>");
        $responsibilityAll.select2({minimumResultsForSearch: Infinity, width: '100%'});
        $responsibilityAll.on("change", function (e) {
                $notificationsContainer.find(".responsibility:enabled").not(this).val($(this).val()).trigger("change");
            }
        );

        var $notificationTypeAll = $rowTemplateAll.find(".notificationType");
        // $notificationTypeAll.append("<option selected disabled hidden style='display: none' value=''></option>");
        // $notificationTypeAll.select2({minimumResultsForSearch: Infinity, width: '100%'});
        $notificationTypeAll.selectpicker();
        $notificationTypeAll.on('changed.bs.select', mdlUtils.allOptionHandler);
        $notificationTypeAll.on("change", function (e) {
                var dropdowns = $notificationsContainer.find(".notificationType").not(this);
                dropdowns.not('.notesNotificationType').selectpicker('val', $(this).val());
                var optionsToSet = null;
                if ($(this).val()) {
                    if ($(this).val().find(function (x) {
                        return x === 'ALL'
                    })) {
                        optionsToSet = [];
                        $(this).children('option').each(function (i, option) {
                            optionsToSet.push(option.value);
                        });
                    }
                    else {
                        optionsToSet = $(this).val();
                    }
                    // optionsToSet = optionsToSet.filter(function(x) {
                    //     return x !== "PUSH_NOTIFICATION" && x !== "BLUE_STONE" && x !== "ALL";
                    // })
                } else {
                    $responsibilityAll.select2('val', 'N');
                    $responsibilityAll.trigger('change');
                }
                dropdowns.find('.notesNotificationType').selectpicker('val', optionsToSet);
                var $noteDropdown = $notificationsContainer.find('.notes-dropdown-menu');
                // $noteDropdown.find('[data-original-index=4]').addClass('disabled');
                // $noteDropdown.find('[data-original-index=6]').addClass('disabled');
            }
        );

        $rowTemplateAll.removeClass('hidden');
        $notificationsContainer.append($rowTemplateAll);

        var i = 0;
        $.each(data, function (k, group) {
            $.each(group.notificationPreferences, function (j, e) {
                var $rowTemplate = container.find("#notificationPreferencesTemplate").clone();

                $rowTemplate.attr('id', 'notificationPreferencesTemplate-' + i);
                $rowTemplate.find(".eventType").html(e.eventType);


                var $notificationPreferencesId = $rowTemplate.find(".notificationPreferencesId");
                $notificationPreferencesId.val(e.id);
                $notificationPreferencesId.attr('name', 'notificationPreferences[' + i + '].id');

                var $eventTypeId = $rowTemplate.find(".eventTypeId");
                $eventTypeId.val(e.eventTypeId);
                $eventTypeId.attr('name', 'notificationPreferences[' + i + '].eventTypeId');

                var $responsibility = $rowTemplate.find(".responsibility");
                $responsibility.attr('name', 'notificationPreferences[' + i + '].responsibility');

                if (e.eventType === 'COVID-19') {
                    $responsibility.find("option[value='V']").prop("disabled",true);
                    $responsibility.find("option[value='N']").prop("disabled",true);
                }
                $responsibility.select2({minimumResultsForSearch: Infinity, width: '100%'});
                $responsibility.select2('val', e.responsibility);

                var $notificationType = $rowTemplate.find(".notificationType");
                $notificationType.attr('name', 'notificationPreferences[' + i + '].notificationTypeList');
                $notificationType.attr('id', 'notificationPreferences-' + i);
                // $notificationType.select2({minimumResultsForSearch: Infinity, width: '100%'});
                $notificationType.selectpicker();

                if (e.notificationTypeList) {
                    //Currently disabling "Bluestone bridge" and "Push Notifications" for Notes preferences as this functionality is not present
                    if (group.name === "Notes") {
                        // e.notificationTypeList = e.notificationTypeList.filter(function(x) {
                        //     return x !== "PUSH_NOTIFICATION" && x !== "BLUE_STONE";
                        // })
                        e.notificationTypeList = e.notificationTypeList;
                    }
                    $notificationType.selectpicker('val', e.notificationTypeList);
                }
                if (group.name === "Notes") {
                    $notificationType.addClass('notesNotificationType');
                    var $dropdown = $rowTemplate.find(".notificationType > .dropdown-menu");
                    $dropdown.addClass('notes-dropdown-menu');
                    // $dropdown.find('[data-original-index=4]').addClass('disabled');
                    // $dropdown.find('[data-original-index=6]').addClass('disabled');
                }

                $notificationType.on('changed.bs.select', mdlUtils.allOptionHandler);
                $notificationType.on('change', function (e) {
                    if (!e.currentTarget.selectedOptions.length && $responsibility.is(':enabled')) {
                        $responsibility.select2('val', 'N');
                    }
                });

                if (!e.canChange) {
                    $responsibility.prop('disabled', true);
                }

                $responsibility.on('change', function () {
                    if ($(this).val() === 'N') {
                        $notificationType.selectpicker('val', []);
                    }
                });
                $rowTemplate.removeClass('hidden');

                if (j == 0) {
                    $notificationsContainer.append('<div class="sectionHead notificationPreferenceHead">' + group.name + '</div>');
                }
                else if (j == group.notificationPreferences.length - 1) {
                    $rowTemplate.css("height", "52px");
                }
                $notificationsContainer.append($rowTemplate);
                i++;
            });
        });
    }

    function _updateNotificationPreferences(container) {
        var employeeId = container.find("#careTeamEmployeeSelect").val();
        var roleId = container.find("#careTeamRoleSelect").val();
        var careTeamMemberId = container.find('#careTeamMemberId').val();

        if (!roleId) {
            return;
        }
        var url = 'care-coordination/communities/community/' + $('#currentCommunityId').val() + '/care-team/notification-preferences?careTeamRoleId=' + roleId;
        if (employeeId) {
            url += '&employeeId=' + employeeId;
        }
        if (careTeamMemberId) {
            url += '&careTeamMemberId=' + careTeamMemberId;
        }
        $.ajax(url)
            .success(function (data) {
                _initNotificationPreferences(container, data);
            });
    }

    function _initDeleteCareTeamMemberConfirmationDialog($deleteCareTeamMemberContainer, careTeamMemberId, careTeamMemberName, employeeIdToDelete, affiliatedSection) {
        $.ajax('care-coordination/communities/community/' + $('#currentCommunityId').val() + '/care-team/' + careTeamMemberId + '/delete').success(function (data) {
                $deleteCareTeamMemberContainer.empty();
                $deleteCareTeamMemberContainer.append(data);
            var careTeamMemberToDeleteId = careTeamMemberId;
            $deleteCareTeamMemberContainer.find("#careTeamMemberName").html("<b>" + careTeamMemberName + "</b>");
            $deleteCareTeamMemberContainer.find("#deleteCareTeamMember").on('click', function () {

                function _finishRemove() {
                    if (affiliatedSection) {
                        _getFragment().widgets.affiliatedCommunityCareTeam.api().ajax.reload();
                        if ($("#copySettings").length > 0) {
                            $.ajax('care-coordination/communities/community/' + $('#currentCommunityId').val() + '/show-copy-settings/').success(function (data) {
                                if (data) {
                                    $("#copySettings").show();
                                }
                            });
                        }
                    }
                    else {
                        _getFragment().widgets.communityCareTeam.api().ajax.reload();
                    }
                    $deleteCareTeamMemberContainer.find("#deleteCareTeamMemberModal").modal('hide');
                }

                $.ajax({
                    url: 'care-coordination/communities/community/' + $('#currentCommunityId').val() + '/care-team/' + careTeamMemberToDeleteId
                    , type: 'DELETE',
                    beforeSend: function (xhr) {
                        mdlUtils.csrf(xhr);
                    }
                })
                    .fail(function (error) {
                        mdlUtils.onAjaxError(error, function () {
                            alert(error.responseText);
                        });
                    }).complete(function () {
                    if ($('#isCCAdmin').val() != 'true') {
                        $.ajax({
                            type: 'GET',
                            contentType: 'json',
                            url: 'care-coordination/communities/community/' + $('#currentCommunityId').val() + '/has-view-access',
                            success: function (data) {
                                if (data != true) {
                                    $deleteCareTeamMemberContainer.find("#deleteCareTeamMemberModal").modal('hide');
                                    $find("#communityDetailsContent").hide();
                                    $find("#communitiesContent").show();
                                    _getFragment().widgets.communitiesList.api().ajax.reload();
                                    ExchangeApp.managers.EventManager.publish('community_list_changed');
                                } else {
                                    _finishRemove();
                                }
                            }
                        });
                    } else {
                        _finishRemove();
                    }
                });
                return false;
            });

            $deleteCareTeamMemberContainer.find('.cancelBtn').on('click', function () {
                $deleteCareTeamMemberContainer.find("#deleteCareTeamMemberModal").modal('hide');

            });

            $deleteCareTeamMemberContainer.find("#deleteCareTeamMemberModal").modal('show');
            $deleteCareTeamMemberContainer.find("#deleteCareTeamMemberModal").on('hidden.bs.modal', function () {
                $(this).remove();
            });
        });
    }

    function _saveCareTeamMember ($container, $modal, affiliatedSection, $button, ajaxLoader) {
        if ($button.hasClass('pending')) {
            return;
        }
        $button.addClass('pending');
        var $form = $container.find("#careTeamMemberForm");
        var formData = form2js('careTeamMemberForm', '.', true,
            function (node) {
                if (node.id && node.id.match(/callbackTest/)) {
                    return {name: node.id, value: node.innerHTML};
                }
            });
        if (!$form.valid()) {
            return false;
        }
        delete formData._csrf;
        delete formData._;

        ajaxLoader.show();
        $.ajax({
            url: 'care-coordination/communities/community/' + $('#currentCommunityId').val() + '/care-team/',
            type: 'PUT',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(formData),
            beforeSend: function (xhr) {
                mdlUtils.csrf(xhr);
            }
        }).success(function (data) {
            if (affiliatedSection) {
                _getFragment().widgets.affiliatedCommunityCareTeam.api().ajax.reload();
                $("#copySettings").hide();
            }
            else {
                _getFragment().widgets.communityCareTeam.api().ajax.reload();
            }
            $modal.modal('hide');
            ajaxLoader.hide();
            $button.removeClass('pending');
        }).fail(function (response) {
            ajaxLoader.hide();
            mdlUtils.onAjaxError(response, function () {
                $button.removeClass('pending');
                $("#formError").text(response.responseText);
            });
        });
        return false;
    }

    function _initCreateEditCareTeamMemberDialog($container, careTeamMemberId, careTeamMemberDescription, careTeamMemberEmployee, careTeamMemberRole, affiliatedSection, roleEditable) {
        $.ajax('care-coordination/communities/community/' + $('#currentCommunityId').val() + '/care-team/' + (careTeamMemberId ? careTeamMemberId + '/' : '') + affiliatedSection + '/' + roleEditable)
            .success(function (data) {
                $container.empty();
                $container.append(data);
                _addCreateTeamMemberValidation($container);
                var $modal = $container.find('#createCareTeamMemberModal');
                var headerTitle = (careTeamMemberId ? 'Edit' : 'Create') + ' Community Care Team Member';
                $container.find("#careTeamMemberHeader").html(headerTitle);
                $($container.find("#careTeamMemberId")).val(careTeamMemberId);
                $($container.find("#careTeamDescription")).val(careTeamMemberDescription);

                // ======================== init employee selector
                var $employeeSelect = $container.find("#careTeamEmployeeSelect");
                $employeeSelect.select2({
                    placeholder: 'Select Contact',
                    width: '100%'
                });
                $employeeSelect.on('change', function (e) {
                    _updateNotificationPreferences($container);
                });
                if (careTeamMemberEmployee) {
                    $employeeSelect.val(careTeamMemberEmployee.id).trigger("change");
                    $employeeSelect.attr("disabled", true);

                }

                // ===================== Init Role Select =================================
                var $roleSelect = $container.find("#careTeamRoleSelect");

                $roleSelect.select2({
                    placeholder: 'Select Role',
                    width: '100%'
                });
                $roleSelect.on('change', function (e) {
                    _updateNotificationPreferences($container);
                });
                if (careTeamMemberRole) {
                    $roleSelect.val(careTeamMemberRole.id).trigger("change");
                }

                // ============      Save button =========================
                var $modalContent = $container.find('.modal-content');
                var $modalBody = $container.find('.modal-body');
                var ajaxLoader = mdlUtils.initAjaxLoader($modalContent, {
                    loaderWillBeShown: function () {
                        $modalBody.css('visibility', 'hidden');
                    },
                    loaderWillBeHidden: function () {
                        $modalBody.css('visibility', 'visible');
                    }
                });

                $container.find("#saveCareTeamMember").on('click', function () {
                    _saveCareTeamMember($container, $modal, affiliatedSection, $(this), ajaxLoader);
                });

                $container.find("#careTeamMemberForm input").keydown(function (e) {
                    if (e.which === 13) {
                        e.preventDefault();
                        return false;
                    }
                });

                $container.find('#createCareTeamMemberModal').find('.cancelBtn').on('click', function () {
                    $modal.modal('hide');

                });

                $container.find('#createCareTeamMemberModal').modal('show');
                $container.find('#createCareTeamMemberModal').on('hidden.bs.modal', function () {
                    $(this).remove();
                });

                if (careTeamMemberId) {
                    _updateNotificationPreferences($container);
                }
            })
            .fail(function () {
                alert('Internal server error. Please contact administrator.');
            });
    }

    function _addCreateTeamMemberValidation($container) {
        var careTeamForm = $container.find("#careTeamMemberForm");
        return careTeamForm.validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    'careTeamEmployeeSelect': {
                        required: true
                    },
                    'careTeamRoleSelect': {
                        required: true
                    }
                },
                messages: {
                    'careTeamEmployeeSelect': {
                        required: getErrorMessage("field.empty")
                    },
                    'careTeamRoleSelect': {
                        required: getErrorMessage("field.empty")
                    }
                }
            })
        );
    }

    function _initCommunityCareTeam(affiliatedSection) {
        var widget = wgtUtils.grid(_getFragment().$html, '', {
            tableId: affiliatedSection ? "affiliatedCommunityCareTeam" : "communityCareTeam",
            totalDisplayRows: 25,
            colSettings: {
                'description': {bSortable: false},
                'actions': {width: '100px', 'className': 'dt-head-center'}
            },

            callbacks: {
                rowCallback: function (row, data, index) {
                    var $row = $(row);
                    if (data && ($('#affiliatedView').val() != 'true' || affiliatedSection)) {
                        var actionsColumn = 'td:eq(3)';
                        $row.children(actionsColumn).empty();

                        if (data.editable || data.deletable || data.nucleusUserId) {
                            var $tdRowActions = mdlUtils.find(_getFragment().$html, '', '#rowActions').clone();
                            var tdRowActionsId = 'rowActions-' + index;
                            $tdRowActions.attr('id', tdRowActionsId);
                            $tdRowActions.removeClass('hidden');
                            $row.children(actionsColumn).append($tdRowActions);
                        }

                        var careTeamMemberId = data.id;
                        var careTeamMemberEmployee = data.employee;
                        var careTeamMemberRole = data.role;
                        var careTeamMemberDescription = data.description;
                        var careTeamMemberRoleEditable = data.roleEditable;

                        var $createCareTeamContainer = mdlUtils.find(_getFragment().$html, '', '#createCareTeamContainer');
                        var $deleteCareTeamMemberContainer = mdlUtils.find(_getFragment().$html, '', '#deleteCareTeamContainer');

                        if (data.editable) {
                            $tdRowActions.find(".editCareTeamMember").removeClass('hidden');
                            $tdRowActions.find('.editCareTeamMember').on('click', function () {
                                    _initCreateEditCareTeamMemberDialog($createCareTeamContainer, careTeamMemberId, careTeamMemberDescription, careTeamMemberEmployee, careTeamMemberRole, affiliatedSection, careTeamMemberRoleEditable);
                                }
                            );
                            if (!data.deletable) {
                                $tdRowActions.css("padding-right", "60px");
                            }
                        }
                        if (data.deletable) {
                            $tdRowActions.find(".deleteCareTeamMember").removeClass('hidden');
                            $tdRowActions.find('.deleteCareTeamMember').on('click', function () {
                                _initDeleteCareTeamMemberConfirmationDialog($deleteCareTeamMemberContainer, careTeamMemberId, careTeamMemberEmployee.label, careTeamMemberEmployee.id, affiliatedSection);
                            });
                        }
                        if (data.nucleusUserId) {
                            $tdRowActions.find(".videoCallCareTeamMember").removeClass('hidden');
                            $tdRowActions.find(".videoCallCareTeamMember").on('click', function () {
                                    if (initCall()) {
                                        startCall(data.nucleusUserId);
                                    }
                                }
                            );
                        }
                    }
                },
                errorCallback: function (error) {
                    alert(error.responseText);
                    // TODO
                },
                footerCallback: function (tfoot, data, start, end, display) {
                    $(tfoot).hide();
                }
            }
        });
        if (affiliatedSection) {
            _getFragment().widgets.affiliatedCommunityCareTeam = widget;
        }
        else {
            _getFragment().widgets.communityCareTeam = widget;
        }
    }

    function showCommunityDetails(communityId, data, isNew) {
        // Show Community Details
        $.ajax("care-coordination/communities/community/" + communityId + "/details").success(data, function (data) {
            if ($find("#communitiesContent").is(":hidden") && $find("#communityDetailsContent").is(":hidden")) {
                return; // belated response from server -> do nothing
            }

            $find("#communitiesContent").hide();
            $find("#communityDetailsContent").hide();
            $find("#communityDetailsContent").empty();
            $find("#communityDetailsContent").append(data);
            $find("#communityDetailsContent").show();

            mdlUtils.find(_getFragment().$html, '', ".backToCommunityList").on('click', function () {
                if (currentOrganizationFilter != ExchangeApp.modules.Header.getCurrentOrganizationFilter()) {
                    ExchangeApp.modules.CareCoordination.routeToCommunityList();
                }
                else {
                    $find("#communityDetailsContent").hide();
                    $find("#communitiesContent").show();
                }
            });

            var $container = mdlUtils.find(_getFragment().$html, '', '#createCareTeamContainer');
            var $createCareTeamMemberButton = mdlUtils.find(_getFragment().$html, '', '#createCareTeamMember');
            $createCareTeamMemberButton.on('click', function () {
                _initCreateEditCareTeamMemberDialog($container, null, null, null, null, false, true);
            });
            var $createAffiliatedCareTeamMemberButton = mdlUtils.find(_getFragment().$html, '', '#createAffiliatedCareTeamMember');
            $createAffiliatedCareTeamMemberButton.on('click', function () {
                _initCreateEditCareTeamMemberDialog($container, null, null, null, null, true, true);
            });
            //todo edit
            var $editCommunityButton = mdlUtils.find(_getFragment().$html, '', '#editCommunity');
            $editCommunityButton.on('click', function () {
                _initCreateEditCommunityDialog($find('#createCommunityContainer'), communityId, $(this), ActionType.EDIT);
            });
            var $copySettingsButton = mdlUtils.find(_getFragment().$html, '', '#copySettings');
            $copySettingsButton.on('click', function () {
                _initcopySettingsDialog($container, communityId);
            });

            // init actions
            _initCommunityCareTeam(false);
            _initCommunityCareTeam(true);
            if (isNew) {
                $("#newlyCreatedAlert").show();
            }

            if (!ExchangeApp.modules.CareCoordinationOrganizations && $('#isCCSuperAdmin').val() == 'true') {
                var commFrUrl = {
                    template: 'care-coordination/templates/organizations'
                };
                ExchangeApp.managers.ModuleManager.invoke({
                    module: {name: 'CareCoordinationOrganizations', fragment: {url: commFrUrl}},
                    initParents: false,
                    forceReload: undefined,
                    asyncLoad: true
                });
            }

            $('a[id^=initialCommunityDetailsLink],a[id^=affiliatedCommunityDetailsLink]').each(function (i) {
                var clickedCommunityId = $(this).children('input').first().val();
                var organizationId = $(this).children('input').last().val();
                $(this).click(function () {
                    var data;
                    ExchangeApp.modules.Header.setCurrentOrg(organizationId);
                    ExchangeApp.modules.Header.showCurrentOrg();
                    showCommunityDetails(clickedCommunityId, data, false);
                });
            });

            $('a[id^=initialDatabaseDetailsLink],a[id^=affiliatedDatabaseDetailsLink]').each(function (i) {
                var clickedOrganizationId = $(this).children('input').first().val();
                $(this).click(function () {
                    var data;
                    ExchangeApp.modules.Header.setCurrentOrg(clickedOrganizationId);
                    ExchangeApp.modules.Header.hideCurrentOrg();
                    ExchangeApp.modules.CareCoordinationCommunities.hide();
                    ExchangeApp.modules.CareCoordinationOrganizations.show();
                    ExchangeApp.modules.CareCoordinationOrganizations.showOrgDetails(clickedOrganizationId);
                });
            });
        });
    }

    function _initCommunitiesList() {
        _getFragment().widgets.communitiesList = _grid({
            tableId: "communitiesList",
            searchFormId: "communityFilterForm",
            totalDisplayRows: 25,
            colSettings: {
                'createdAutomatically': {
                    customRender: function (data) {
                        if (!data)
                            return '';
                        else {
                            return 'Yes';
                        }
                    }
                }
            },
            callbacks: {
                rowCallback: function (row, data, index) {
                    var $row = $(row);
                    var communityId = data.id;
                    $row.attr('style', 'cursor:pointer');
                    $row.on('click', function () {
                        // Show Community Details
                        showCommunityDetails(communityId, data, false);
                    });
                },
                errorCallback: function (error) {
                    alert(error.responseText);
                    // TODO
                },
                footerCallback: function (tfoot, data, start, end, display) {
                    $(tfoot).hide();
                }
            }
        });
        _getFragment().widgets.communitiesList.api().ajax.reload();
        currentOrganizationFilter = ExchangeApp.modules.Header.getCurrentOrganizationFilter();
    }

    function _addCreateCommunityValidation($container, communityId) {
        var communityForm = $container.find("#communityForm");
        return communityForm.validate(
            new ExchangeApp.utils.wgt.Validation({
                ignore: [],
                scrollToFirstInvalid: true,
                container: $container.find('#createCommunityModal'),
                rules: {
                    'name': {
                        required: true,
                        maxlength: 100,
                        minlength: 3,
                        remote: {
                            url: checkUniquenessUrl,
                            type: 'GET',
                            data: {name: communityForm.find("#name").val(), id: communityId}
                        }
                    },
                    'oid': {
                        required: true,
                        maxlength: 30,
                        remote: {
                            url: checkUniquenessUrl,
                            type: 'GET',
                            data: {oid: communityForm.find("#oid").val(), id: communityId}
                        }
                    },
                    'phone': {required: true, maxlength: 16, phone: true},
                    'email': {required: true, maxlength: 255, email: true},
                    'street': {
                        minlength: 2,
                        maxlength: 255,
                        requiredIf: {conditionFunction: isMarketplace.bind(communityForm, communityForm)},
                    },
                    'city': {
                        minlength: 2,
                        maxlength: 100,
                        requiredIf: {conditionFunction: isMarketplace.bind(communityForm, communityForm)},
                    },
                    'stateId': {
                        requiredIf: {conditionFunction: isMarketplace.bind(communityForm, communityForm)},
                    },
                    'postalCode': {
                        zipcodeUS: true,
                        requiredIf: {conditionFunction: isMarketplace.bind(communityForm, communityForm)},
                    },
                    'marketplace.servicesSummaryDescription': {
                        // language=JSRegexp
                        pattern: '[0-9a-zA-Z !"#$%&\'’()*+,\\s\\-./:;<=>?@\\\\[\\]^_`{|}~]*'
                    },
                    'marketplace.appointmentsEmail': {maxlength: 150, email: true},
                    'marketplace.appointmentsSecureEmail': {
                        maxlength: 150,
                        email: true,
                        requiredIf: {conditionFunction: appointmentsAllowed.bind(communityForm, communityForm)}
                    },
                    'logo': {fileIsPicture: true, filesize: 1048576, fileRatio: [1.5, 5]}
                },
                messages: {
                    'name': {
                        required: getErrorMessage("field.empty"),
                        remote: 'Name of Community should be unique'
                    },
                    'oid': {
                        required: getErrorMessage("field.empty"),
                        remote: 'Community OID should be unique'
                    },
                    'phone': {
                        phone: getErrorMessage("field.phone.format")
                    },
                    'street': {
                        requiredIf: 'All address fields should be filled, if community is discoverable in Marketplace',
                        skip_or_fill_minimum: 'All address fields should be filled, if at least one of the fields is not empty'
                    },
                    'city': {
                        requiredIf: 'All address fields should be filled, if community is discoverable in Marketplace',
                        skip_or_fill_minimum: 'All address fields should be filled, if at least one of the fields is not empty'
                    },
                    'stateId': {
                        requiredIf: 'All address fields should be filled, if community is discoverable in Marketplace',
                        skip_or_fill_minimum: 'All address fields should be filled, if at least one of the fields is not empty'
                    },
                    'postalCode': {
                        requiredIf: 'All address fields should be filled, if community is discoverable in Marketplace',
                        skip_or_fill_minimum: 'All address fields should be filled, if at least one of the fields is not empty'
                    },
                    'marketplace.servicesSummaryDescription': {
                        pattern: 'Invalid format. Allowed: letters, numbers, space and special symbols'
                    },
                    'marketplace.appointmentsSecureEmail': {
                        requiredIf: $.validator.messages.required
                    },
                    'logo': {
                        fileIsPicture: 'File should be of picture format',
                        filesize: 'Maximum file size is 1 MB',
                        fileRatio: 'Image ratio should be between 1.5:1 and 5:1'
                    }
                }
            })
        );
    }

    function isMarketplace(commForm) {
        return commForm.find('#commConfirmVisibilityInMarketplace').is(":checked");
    }

    function appointmentsAllowed(commForm) {
        return commForm.find('#allowAppointments').is(":checked");
    }

    function _getAllTextForSelectedOptions(combobox) {
    	var selectedOptionsLength=0;
		var nonSelectedOption=0;
		var option=$(combobox).find('option')[0];
		var previousParentOption=$(option).parent().prev();
        var $dropdown = $(combobox);
		$(previousParentOption).find('ul')
		.children().filter(function (index,li) {
			if(li.outerText =='None' || li.outerText =='All'|| li.outerText.length==0 ){
				nonSelectedOption++;
			}

			 if($(li).attr('class')==='selected')
				 selectedOptionsLength++;
			 });

			 var selectedOptionsLengthFromOptionTag =$(previousParentOption)
			 .find('ul')
			 .children().length - nonSelectedOption;

		 if(selectedOptionsLength === selectedOptionsLengthFromOptionTag){
			 var ed=$(previousParentOption)
			 .prev()
			 .find('span')[0];
			 $(ed).text('All');
			 $(combobox).val(0);
			 $(previousParentOption)
			 .find('ul')
			 .children().filter(function (index,li) {
				$(li).removeClass('selected');

				 if(li.outerText =='All'){
					$(li).addClass('selected');
				 }
			 })
		 }
    }

    function _getMultiSelection($combobox) {
        var selectedValues = $combobox.selectpicker('val') || [];
        var options = $combobox.find('option');
        // is 'All' selected?
        if (selectedValues.indexOf('0') > -1) {
            return $.map(options, function(option) {
                if (option.text.toLowerCase() !== 'none') return option.value;
            }).filter(function(value) {
                return value > 0;
            });
        }
        // is 'None' selected?
        var noneValueId = $.map(options, function(option) {
            if (option.text.toLowerCase() === 'none') return option.value
        });

        if (selectedValues.indexOf(noneValueId[0]) > -1) {
            return $.map(options, function(option) {
                if (option.text.toLowerCase() === 'none') return option.value;
            }).filter(function(value) {
                return value > 0;
            });
        }
        return selectedValues;
	}

    function getSelectedInNetworkInsurancePlanIds () {
        var map = {};
        $('#in-network-insurance-table').find('tr').each(function () {
            var $tr = $(this);

            if($tr.attr('role')) {
                var networkId = $tr.data('network-id');

                if (networkId) {
                    var $select = $tr.find('select.selected-plan');
                    map[networkId] = $select.size() ? _getMultiSelection($select) : [];
                }
            }
        });
        return map;
	}

	function initPrimaryFocusReferencedColumns($primaryFocus) {
    	$("#commPrimaryFocus").on("changed.bs.select", function(e, clickedIndex, newValue, oldValue) {
          var selectedValues = $primaryFocus.selectpicker('val') || [];
            var options = $primaryFocus.find('option');

            marketplaceService.communityTypesIds(selectedValues)
                .then(function(data) {
                    var $communityTypeSelect = $('#commCommunityType');
                    var previousSelected = _getMultiSelection($communityTypeSelect);
                    var option = "";
                	data.forEach(function(items) {
                		option +="<optgroup>";
                    	items.forEach(function(item) {
                    		option += "<option class='optionSpace' value='" + item.id + "'>" + item.label + "</option>";
                    	});
                    	option +="</optgroup>";
                    });
                    $communityTypeSelect.html(option);
                    $communityTypeSelect.val(previousSelected);
                    $communityTypeSelect.selectpicker('refresh');

                })
                .fail(function(e) {});

            marketplaceService.serviceTreatmentApproaches(selectedValues)
            .then(function(data) {
                var $servicesTreatmentApproachesSelect = $('#commServicesTreatmentApproaches');
                var previousSelected = _getMultiSelection($servicesTreatmentApproachesSelect);
                var option = "";
            	data.forEach(function(items) {
            		option +="<optgroup>";
                	items.forEach(function(item) {
                		option += "<option class='optionSpace' value='" + item.id + "'>" + item.label + "</option>";
                	});
                	option +="</optgroup>";
                });
                $servicesTreatmentApproachesSelect.html(option);
                $servicesTreatmentApproachesSelect.val(previousSelected)
                $servicesTreatmentApproachesSelect.selectpicker('refresh');

            })
            .fail(function(e) {});
    	});
    }

    function updateMarketPlace(dataObj) {
        dataObj.marketplace = {};
        dataObj.marketplace.confirmVisibility = dataObj["marketplace.confirmVisibility"];
        dataObj.marketplace.servicesSummaryDescription = dataObj["marketplace.servicesSummaryDescription"];
        dataObj.marketplace.prerequisite = dataObj["marketplace.prerequisite"];
        dataObj.marketplace.exclusion = dataObj["marketplace.exclusion"];
        dataObj.marketplace.allowAppointments = dataObj["marketplace.allowAppointments"];
        dataObj.marketplace.appointmentsEmail = dataObj["marketplace.appointmentsEmail"];
        dataObj.marketplace.appointmentsSecureEmail = dataObj["marketplace.appointmentsSecureEmail"];
        dataObj.marketplace.allInsurancesAccepted = dataObj["marketplace.allInsurancesAccepted"];
        dataObj.marketplace.primaryFocusIds = _getMultiSelection($("#commPrimaryFocus"));
        dataObj.marketplace.communityTypeIds = _getMultiSelection($("#commCommunityType"));
        dataObj.marketplace.levelOfCareIds = _getMultiSelection($("#commLevelsOfCare"));
        dataObj.marketplace.ageGroupIds = _getMultiSelection($("#commAgeGroupsAccepted"));
        dataObj.marketplace.serviceTreatmentApproachIds = _getMultiSelection($("#commServicesTreatmentApproaches"));
        dataObj.marketplace.emergencyServiceIds = _getMultiSelection($("#commEmergencyServices"));
        dataObj.marketplace.languageServiceIds = _getMultiSelection($("#commLanguageServices"));
        dataObj.marketplace.ancillaryServiceIds = _getMultiSelection($("#commAncillaryServices"));

        dataObj.marketplace.selectedInNetworkInsurancePlanIds = getSelectedInNetworkInsurancePlanIds();

        delete dataObj["marketplace.confirmVisibility"];
        delete dataObj["marketplace.servicesSummaryDescription"];
        delete dataObj["marketplace.prerequisite"];
        delete dataObj["marketplace.exclusion"];
        delete dataObj["marketplace.allowAppointments"];
        delete dataObj["marketplace.appointmentsEmail"];
        delete dataObj["marketplace.appointmentsSecureEmail"];
        delete dataObj["marketplace.primaryFocusIds"];
        delete dataObj["marketplace.communityTypeIds"];
        delete dataObj["marketplace.levelOfCareIds"];
        delete dataObj["marketplace.ageGroupIds"];
        delete dataObj["marketplace.serviceTreatmentApproachIds"];
        delete dataObj["marketplace.emergencyServiceIds"];
        delete dataObj["marketplace.languageServiceIds"];
        delete dataObj["marketplace.ancillaryServiceIds"];
        delete dataObj["marketplace.selectedInNetworkInsuranceIds"];
        delete dataObj["marketplace.selectedInNetworkInsurancePlanIds"];
        delete dataObj["marketplace.acceptedInsurancePlanIds"];
        delete dataObj["marketplace.allInsurancesAccepted"];

        delete dataObj["_marketplace.confirmVisibility"];
        delete dataObj["_marketplace.allowAppointments"];
        delete dataObj["_marketplace.primaryFocusIds"];
        delete dataObj["_marketplace.communityTypeIds"];
        delete dataObj["_marketplace.levelOfCareIds"];
        delete dataObj["_marketplace.ageGroupIds"];
        delete dataObj["_marketplace.serviceTreatmentApproachIds"];
        delete dataObj["_marketplace.emergencyServiceIds"];
        delete dataObj["_marketplace.languageServiceIds"];
        delete dataObj["_marketplace.ancillaryServiceIds"];
        delete dataObj["_marketplace.selectedInNetworkInsuranceIds"];
        delete dataObj["_marketplace.selectedInNetworkInsurancePlanIds"];
        delete dataObj["_marketplace.acceptedInsurancePlanIds"];
        delete dataObj["_marketplace.allInsurancesAccepted"];

        return dataObj;
    }

    function saveOrUpdateCommunity($form, communityId, $modal, $button) {
        $button.addClass("pending");
        var url = 'care-coordination/communities';
        var requestMethod = 'POST';
        var isNew = true;
        if (communityId) {
            url += '/' + communityId;
            requestMethod = 'PUT';
            isNew = false;
        }

        $form.find('#communityOid').removeAttr('disabled');
        var dataObj = $form.serializeJSON();

        var logoChanged = dataObj.logoChanged;
        var logoRemoved = dataObj.logoRemoved;

        delete dataObj['logoChanged'];
        delete dataObj['logoRemoved'];

        // jQUery serializeJSON plugin doesn't play well with Spring MVC Forms
        dataObj = updateMarketPlace(dataObj);

        var updateAndClose = function (data) {
            _getFragment().widgets.communitiesList.api().ajax.reload();
            $modal.modal('hide');
            showCommunityDetails(data.id, data, isNew);
            ExchangeApp.managers.EventManager.publish('community_list_changed');
        };

        delete dataObj._csrf;

        $.ajax({
            url: url,
            type: requestMethod,
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(dataObj),
            beforeSend: function (xhr) {
                mdlUtils.csrf(xhr);
            }
        }).success(function (data) {
            //Update logo
            if (logoRemoved == true) {
                var logoRemoveUrl = 'care-coordination/communities/' + data.id + '/logo';
                $.ajax({
                    url: logoRemoveUrl,
                    type: 'DELETE',
                    cache: false,
                    dataType: 'json',
                    processData: false,
                    contentType: false,
                    beforeSend: function (xhr) {
                        mdlUtils.csrf(xhr);
                    }
                }).success(function (data2) {
                    updateAndClose(data);
                }).fail(function (e) {
                    mdlUtils.onAjaxError(e, function () {
                        console.log(e);
                        alert('There were problems deleting logo');
                        updateAndClose(data);
                    });
                });
            } else if (logoChanged == true) {
                var logoUrl = 'care-coordination/communities/' + data.id + '/logo';
                var file = $('#logoCommunityInput')[0].files[0];
                var fileData = new FormData();
                fileData.append('logo', file);
                $.ajax({
                    url: logoUrl,
                    type: 'POST',
                    data: fileData,
                    cache: false,
                    dataType: 'json',
                    processData: false,
                    contentType: false,
                    beforeSend: function (xhr) {
                        mdlUtils.csrf(xhr);
                    }
                }).success(function (data2) {
                    updateAndClose(data);
                }).fail(function (e, r) {
                    mdlUtils.onAjaxError(e, function () {
                        console.log(e, r);
                        alert('There were problems updating logo');
                        updateAndClose(data);
                    });
                });
            } else {
                updateAndClose(data);
            }
        }).fail(function (response) {
            mdlUtils.onAjaxError(response, function () {
                alert(response.responseText);
                $button.removeClass("pending");
            });
        });
    }

    function _initcopySettingsDialog($container, communityId) {
        $.ajax('care-coordination/communities/community/' + communityId + '/copy-settings')
            .success(function (data) {
                $container.empty();
                $container.append(data);
                var $modal = $container.find('#copySettingsModal');
                var $copyButton = $container.find("#copySettingsBtn");
                // ============      Save button =========================
                $copyButton.on('click', function () {
                    $.ajax('care-coordination/communities/community/' + communityId + '/copy-settings/' + $modal.find('#copySettingsCommunitySelect').val())
                        .success(function (data2) {
                            _getFragment().widgets.affiliatedCommunityCareTeam.api().ajax.reload();
                            $modal.modal('hide');
                            $('#copySettings').hide();
                        }).fail(function (e) {
                        console.log(e);
                        alert(e.responseText);
                    });
                    return false;
                });
                $modal.modal('show');
                $modal.on('hidden.bs.modal', function () {
                    $(this).remove();
                });
            })
            .fail(function (data, data2) {
                console.log(data, data2);
                alert('Internal server error. Please contact administrator.');
            });
        return false;
    }

    function _markMatch(text, term) {
        // Find where the match is
        var match = text.toUpperCase().indexOf(term.toUpperCase());
        var $result = $('<span></span>');

        // If there is no match, move on
        if (match < 0) {
            return $result.text(text);
        }

        // Put in whatever text is before the match
        $result.text(text.substring(0, match));
        // Mark the match
        var $match = $('<span class="select2-rendered__match"></span>');
        $match.text(text.substring(match, match + term.length));
        // Append the matching text
        $result.append($match);
        // Put in whatever is after the match
        $result.append(text.substring(match + term.length));
        return $result;
    }

    function _initCreateEditCommunityDialog($container, communityId, $button, actionType) {
        if ($button.hasClass("pending")) {
            return;
        }
        $button.addClass("pending");
        $.ajax({
            url: 'care-coordination/templates/communities/edit/' + communityId,
            beforeSend: function () {
                $('.ajaxLoader').removeClass('hidden');
            },
            complete: function () {
                $('.ajaxLoader').addClass('hidden');
            }
        })
            .success(function (data) {
                $container.empty();
                $container.append(data);

                _addCreateCommunityValidation($container, communityId, true);
                _initWizardWgt($container, actionType);

                var $modal = $container.find('#createCommunityModal');
                var $saveButton = $container.find("#saveCommunity");
                var $allowAppointmentsCheckbox = $container.find("#allowAppointments");
                var $commAppointmentsSecureEmailInput = $container.find("#commAppointmentsSecureEmail");
                var $commAppointmentsSecureEmailDiv = $container.find("#commAppointmentsSecureEmailDiv");
                var $commAppointmentsSecureEmailLabel = $container.find("label[for='commAppointmentsSecureEmail']");
                var $commConfirmVisibilityInMarketplaceCheckbox = $container.find('#commConfirmVisibilityInMarketplace');

                var $addressPart = $modal.find(":input.addressPart");
                var $addressPartLabel = $modal.find("label.addressPart");

                // ============      Save button =========================
                $saveButton.on('click', function () {
                    var $form = $container.find("#communityForm");
                    if (!$form.valid()) {
                        // jump to tab where validation failed
                        var isLegalInfoTabValid = $("#legalInfoTab").has(".has-warning").length <= -(0.0);
                        var isMarketplaceTabValid = $("#marketplaceTab").has(".has-warning").length <= -(0.0);
                        if (isMarketplaceTabValid && !isLegalInfoTabValid) {
                            $('#legalInfoHeadLnk').click(); // so dirty
                        }
                        return false;
                    }
                    if (!$(this).hasClass("pending")) {
                        saveOrUpdateCommunity($form, communityId, $modal, $(this))
                    }
                    return false;
                });

                // ============      Appointments > Allow appointments checkbox =========================
                var defaultSecureEmailLabelCaption = $commAppointmentsSecureEmailLabel.text();
                $allowAppointmentsCheckbox.change(function () {
                    if (this.checked) {
                        $commAppointmentsSecureEmailLabel.text(defaultSecureEmailLabelCaption + '*');
                    } else {
                        $commAppointmentsSecureEmailLabel.text(defaultSecureEmailLabelCaption);
                        if ('' === $commAppointmentsSecureEmailInput.val()) {
                            $commAppointmentsSecureEmailInput.removeClass('error');
                            $commAppointmentsSecureEmailDiv.removeClass('has-warning');
                        }
                    }
                });
                if ($allowAppointmentsCheckbox.is(":checked")) {
                    $commAppointmentsSecureEmailLabel.text(defaultSecureEmailLabelCaption + '*');
                }

                // ============      'Confirm that community will be visible in MarketPlace' checkbox =========================
                $commConfirmVisibilityInMarketplaceCheckbox.change(function () {
                    if (this.checked) {
                        $addressPartLabel.find("span").show();
                    } else {
                        if ($addressPart.val() === "") {
                            $addressPartLabel.find("span").hide();
                            $addressPartLabel.parent().removeClass("has-warning");
                        }
                    }
                });
                if ($addressPart.val() !== "" || $commConfirmVisibilityInMarketplaceCheckbox.is(":checked")) {
                    $addressPartLabel.find("span").show();
                }

                // ============      Legal Info > Address > any field =========================
                $addressPart.change(function () {
                    if ($(this).valid()) {
                        var needResetWarnings;
                        if ($(this).val() === "") {
                            // if this input is empty and other inputs are empty too -> reset validation warnings
                            needResetWarnings = $addressPart.not(this).val() === "";
                            var $form = $container.find("#communityForm");
                            if (needResetWarnings && !isMarketplace($form)) {
                                $addressPartLabel.find("span").hide();
                            }
                        } else {
                            // if this input is not empty and other inputs contain valid non-empty values -> reset validation warnings
                            var otherValues = $addressPart.not(this).map(function () {
                                return $(this).val();
                            });
                            needResetWarnings = ($.inArray("", otherValues) === -1) && $addressPart.not(this).valid();
                        }
                        if (needResetWarnings) {
                            $addressPartLabel.parent().removeClass("has-warning");
                        }
                    }
                    if ($(this).val().length > 1) {
                        $addressPartLabel.find("span").show();
                    }
                });

                $modal.modal('show');
                $modal.on('hidden.bs.modal', function () {
                    $(this).remove();
                });


                $("#oidHelp").popover();
                $("#communityLogoHelp").popover();
                $("#confirmCommunityHelp").popover();

                function tabContentPaddingSize($div) {
                    var tableHeight = 0;
                    var startPadding = 25;

                    var tabContent = $(".organizationWizardContent");
                    var table = $(".table-div");

                    if (parseInt($div.css('height'), 10) >= 220) {
                        if (table) {
                            tableHeight = table.height() != null ? table.height() : 0;
                        }
                        tabContent.css('paddingBottom', startPadding + parseInt($div.css('height'), 10) - tableHeight + "px");
                    } else {
                        tabContent.css('paddingBottom', startPadding + parseInt($div.css('height'), 10) + "px");
                    }
                    if ($div.css('display') === 'none') {
                        tabContent.css('paddingBottom', startPadding + "px");
                    }
                }

                function defaultSearchList(liList, upToDefaultList) {
                    var noMatchText = "No networks found";

                    Array.from(liList).forEach(function (li) {
                        var firstLiChildElem = $(li).children(':first').children(':first');

                        if ($(li).text() === noMatchText && upToDefaultList) {
                            $(li).remove();
                        }

                        if (firstLiChildElem !== null)
                            firstLiChildElem.html($(li).text().fontcolor("#333333"));
                        $(li).show();
                    });
                }

                function hideSelectedOptions(optionList, $searchIcon) {
                    var $searchInput = $("#searchInput");

                    Array.from(optionList).forEach(function (option) {
                        //hide option 'I'll choose my insurance later'
                        if ($(option).hasClass("selected") || $(option).text() === 'I\'ll choose my insurance later') {
                            $(option).hide();

                            if ($searchInput.val()) {
                                $searchIcon.removeClass("search-icon").addClass("cancel-search-icon");
                            }
                        }
                    });
                    return optionList;
                }

                $("#searchInput").on({
                    "keyup": function () {
                        var input = $("#searchInput");
                        var filter = input.val().toLowerCase();
                        var $div = $("#selectSearch").prev();
                        var liList = $div.find("ul").find("li");
                        var $searchIcon = $("#searchIcon");

                        $div.css("display", "block");
                        $searchIcon.removeClass("cancel-search-icon").addClass("search-icon");

                        defaultSearchList(liList, false);

                        if (filter !== null && filter !== "") {
                            $searchIcon.removeClass("search-icon").addClass("cancel-search-icon");
                            Array.from(liList).forEach(function (li) {
                                var liChild = $(li).children(':first').children(':first');

                                if ($(li).text().toLowerCase().indexOf(filter) < 0) {
                                    $(li).hide();
                                }
                                if (liChild !== null) {
                                    var selection = liChild.text().toLowerCase().indexOf(filter);
                                    liChild.html($(li).text().substring(0, selection)
                                        + $(li).text().substring(selection, selection + filter.length).fontcolor("#454F81")
                                        + $(li).text().substring(selection + filter.length));
                                }
                            });

                            liList = hideSelectedOptions(liList, $searchIcon);

                            var ul = $div.find("ul");
                            var hiddenLi = ul.find("li").filter(":hidden");
                            var noMatch = document.createElement("li");
                            var noMatchText = "No networks found";

                            if (hiddenLi.length === liList.length) {
                                $(noMatch).text(noMatchText);

                                ul.append(noMatch);
                            } else if (ul.children(':last').text() === noMatchText) {
                                Array.from(ul.children()).map(function (li) {
                                    if ($(li).text() === 'No networks found') {
                                        $(li).remove();
                                    }
                                })
                            }
                        } else {
                            defaultSearchList(liList, true);
                            hideSelectedOptions(liList, $searchIcon);
                        }

                        tabContentPaddingSize($div);
                    },
                    "click": function () {
                        var div = $("#selectSearch").prev();
                        var liList = div.find("ul").find("li");

                        tabContentPaddingSize(div);
                        div.css("display", "block");

                        liList = hideSelectedOptions(liList, $('.searchIcon'));

                        var handler = function (event) {
                            if (!$(event.target).is('#searchInput')) {
                                onUpdateCommunity(event);
                                $('#createCommunityModal').off("click", handler)
                            }
                        }

                        $('#createCommunityModal').on("click", handler);
                    },
                    "blur": function () {
                        var $searchSelect = $("#selectSearch");
                        var optionsList = $searchSelect.prev().find("ul").find("li");
                        $searchSelect.change(function () {

                            $("#tableElement").css("display", "none");
                        });
                        hideSelectedOptions(optionsList, $('#searchIcon'));
                    }
                });

                $("#tableElement").on("click", ".remove-button", function () {
                    var networkName = $(this).closest('tr').data('network-name');
                    var search_component = $("#selectSearch");
                    var div = search_component.prev();
                    var liList = div.find("ul").find("li");
                    var $tableElement = $(this);

                    $.each(search_component.find(':selected'), function (index, elem) {
                        if ($(elem).text() === networkName) {
                            $(elem).prop("selected", false);
                        }
                    });

                    $.each(liList, function (index, li) {
                        if ($(li).text() === networkName) {
                            $(li).removeClass("selected");
                            $(li).show();
                        }
                    });

                    $(this).closest('tr').remove();

                    if ($tableElement.find('td').length === 0)
                        $tableElement.empty();
                });

                $("#searchIcon").on("click", function () {
                    var div = $("#selectSearch").prev();

                    if ($(this).hasClass("cancel-search-icon")) {
                        var liList = div.find("ul").find("li");

                        $("#searchInput").val('');

                        defaultSearchList(liList, true);
                        hideSelectedOptions(liList, $(this));

                        $(this).removeClass("cancel-search-icon").addClass("search-icon");
                    }
                    div.css("display", "block");
                    tabContentPaddingSize(div);

                    var handler = function (event) {
                        if (!$(event.target).is('#searchIcon')) {
                            onUpdateCommunity(event);
                            $('#createCommunityModal').off("click", handler)
                        }
                    }

                    $('#createCommunityModal').on("click", handler);
                });

                function loadNetworkTableList () {
                    var $networkTable = $("#tableElement");
                    var $selectSearch = $("#selectSearch");

                    var networkIds = _getMultiSelection($selectSearch);

                    if (networkIds.length !== 0) {
                        $.ajax({
                            type: "GET",
                            contentType: "application/json",
                            url: "care-coordination/templates/communities/" + communityId + "/networks/" + networkIds,
                            dataType: "html"
                        }).success(function (data) {
                            $networkTable.empty();
                            $networkTable.append(data);

                            var $tableSelectPickers = $networkTable.find(".spicker");
                            $tableSelectPickers.selectpicker();
                            $tableSelectPickers.on('changed.bs.select', mdlUtils.noneOptionHandler);
                            $tableSelectPickers.on('changed.bs.select', mdlUtils.allOptionHandler);

                            $networkTable.find('select.spicker').each(function () {
                                var $select = $(this);

                                if (!$select.selectpicker('val')) {
                                    $select.selectpicker('val', 0);
                                }
                            });
                        });
                        $networkTable.css("display", "block");
                        $networkTable.find('.selectedPlan').find('.filter-option').addClass('network-table-filter-option');
                    } else {
                        $networkTable.css("display", "none");
                        $networkTable.empty();
                    }
                }

                loadNetworkTableList();

                function onUpdateCommunity (event) {
                    var $target = $(event.target);
                    var $table = $("#tableElement");
                    var $selectSearch = $("#selectSearch");
                    var $selectSearchDropdownMenu = $selectSearch.siblings('.dropdown-menu');
                    var optionsList = $selectSearch.prev().find("ul").find("li");
                    var $searchIcon = $("#searchIcon");
                    var $searchInput = $("#searchInput");

                    $table.find('.selected-plan').find('.filter-option').addClass('network-table-filter-option');

                    if ($target.closest('button').parent().hasClass('selected-plan')) {
                        var $dropdown_menu = $target.closest('button').next();

                        $dropdown_menu.addClass('plan_select_button_dropdown');
                        $dropdown_menu.find('ul').find('li').find('a').addClass('ellipsis-text');

                        $table.find('.selectedPlan').find('.filter-option').addClass('network-table-filter-option');
                    }

                    else {
                        if (!$target.is($selectSearchDropdownMenu)
                            && !$target.is($searchInput)
                            && !$target.is($searchIcon)
                            && !$target.parent().hasClass('modal-table-row')) {

                            $searchIcon.removeClass("cancel-search-icon").addClass("search-icon");
                            $searchInput.val('');
                            $selectSearchDropdownMenu.css('display', 'none');

                            loadNetworkTableList();
                        }
                        tabContentPaddingSize($selectSearchDropdownMenu);
                        $table.find('.selectedPlan').find('.filter-option').addClass('network-table-filter-option');
                    }
                    $table.find('.selected-plan').find('.filter-option').addClass('network-table-filter-option');
                    hideSelectedOptions(optionsList, $searchIcon);
                }

                $find(":file").each(function (i, e) {
                    var btnText = $(e).attr('data-buttonText');
                    var enableIcon = $(e).attr('data-icon') == 'true';
                    var placeholder = $(e).attr('data-placeholder');
                    $(e).filestyle({icon: enableIcon, buttonText: btnText});
                    $(e).filestyle('placeholder', placeholder ? placeholder : 'File Not Chosen');

                    var cancelBtn = $('<a href="#" class="cancel-icon" id="removeLogo"></a>');

                    $('.input-group input').after(cancelBtn);

                    if (placeholder !== 'File Not Chosen' && placeholder !== "") {
                        $(".cancel-icon").css({
                            paddingTop: '3px',
                            opacity: 1,
                            zIndex: 3
                        });
                    }

                    $(e).on('change', function () {
                        $(".cancel-icon").css({
                            paddingTop: '3px',
                            opacity: 1,
                            zIndex: 3
                        });

                        $("#logoChanged").val(1);
                        $("#logoRemoved").val(0);

                    });
                    cancelBtn.on('click', function () {
                        $("#logoRemoved").val(1);

                        $(e).filestyle('clear');
                        $(e).filestyle('placeholder', 'File Not Chosen');
                        $(e).parents('.form-group').removeClass('has-warning');
                        $(e).siblings('.bootstrap-filestyle').tooltip("destroy");

                        $(this).css({
                            paddingTop: 0,
                            opacity: 0,
                            zIndex: 0
                        });

                        return false;
                    })
                });

                $button.removeClass("pending");
                $('input:checkbox').styler();

                // multi selection comboboxes
                initPrimaryFocusReferencedColumns($("#commPrimaryFocus"));
                var $selectPickers = $container.find("select.spicker");
                $selectPickers.selectpicker();
                $selectPickers.on('changed.bs.select', mdlUtils.noneOptionHandler);
                $selectPickers.on('changed.bs.select', mdlUtils.allOptionHandler);

                _getAllTextForSelectedOptions('#commAgeGroupsAccepted');
            	_getAllTextForSelectedOptions('#commAncillaryServices');
            	_getAllTextForSelectedOptions('#commEmergencyServices');

                $.each($selectPickers.selectpicker(), function (index, select) {
                    $.each(select, function (optionIndex, option) {
                        if ($(option).val() != -1 && $(option).text().toLowerCase() === 'none') {
                            $(option).parent().prev().find('ul').children().eq(optionIndex).hide();
                            if ($(option).prop('selected')) {
                                $(option).parent().prev().find('ul').children()
                                    .filter(function (index, li) {
                                        return $(li).text().toLowerCase() === 'none' && $(li).css('display') !== 'none';
                                    }).addClass('selected');
                            }
                        } else {
                            $(option).parent().prev().find('li').filter(function (index, li) {
                                return $(li).text().toLowerCase() !== 'none' && $(li).css('display') !== 'none';
                            }).on('click', function (e) {
                                if ($(e.target).text().toLowerCase() !== 'none') {
                                    $(option).parent().find(':selected').filter(function (index, value) {
                                        return $(value).text().toLowerCase() === 'none'
                                    }).prop('selected', false);
                                    $(option).parent().prev().find('li').filter(function (index, li) {
                                        return $(li).text().toLowerCase() === 'none' && $(li).css('display') !== 'none';
                                    }).removeClass('selected');
                                    $(option).parent().prev().prev().find('.filter-option').text().toLowerCase().replace('none, ', '');
                                }
                            });
                        }
                    })
                });

            })
            .fail(function (data, data2) {
                console.log(data, data2);
                $button.removeClass("pending");
                alert('Internal server error. Please contact administrator.');
            });

        return false;
    }

    /*-------------widgets----------------*/
    function _initWizardWgt($container, actionType) {
        var cFragment = _getFragment();
        var $communityWizardContainer = $('#createCommWzd');

        if (actionType === ActionType.ADD || (actionType === ActionType.EDIT && !_validateLegalInfoStep())) {
            $communityWizardContainer.tabs({
                disabled: [1]
            });
        }

        cFragment.widgets.createCommWizard = _wizard($container,
            {
                wizardId: 'createCommWzd',
                tabClass: 'nav nav-tabs',
                onNext: function (tab, navigation, index) {
                    switch (index) {
                        case 1:
                            if (_validateLegalInfoStep()) {
                                $communityWizardContainer.tabs('enable', index);
                            } else {
                                $communityWizardContainer.tabs({
                                    disabled: [index]
                                });
                            }
                            return _validateLegalInfoStep();
                        default:
                            return false;
                    }
                },
                onFinish: function (tab, navigation, index) {
                    return true;
                },
                onPrevious: function (index) {
                    $communityWizardContainer.tabs({active: index});
                    return true;
                },
                onTabClick: function (activetab, navigation, currentIndex, nextIndex) {
                    if (nextIndex <= currentIndex) {
                        return;
                    }
                    switch (nextIndex) {
                        case 1:
                            if (_validateLegalInfoStep()) {
                                $communityWizardContainer.tabs({
                                    enable: nextIndex,
                                    active: nextIndex
                                });
                            } else {
                                $communityWizardContainer.tabs({
                                    disabled: [nextIndex],
                                    active: currentIndex
                                });
                            }
                            return _validateLegalInfoStep();
                        default:
                            return false;
                    }
                },
                onTabShow: function (tab, navigation, index) {
                    index = index < 0 ? 0 : index;
                    $communityWizardContainer.tabs({active: index});
                    _updateBtns(index);
                    return true;
                },
                nextSelector: $find('.wzBtns .next'),
                previousSelector: $find('.wzBtns .previous'),
                finishSelector: $find('.wzBtns .finish')
            }
        );
        _setActiveStep(cFragment.widgets.createCommWizard.dataset.activeStep);
        $find('.wzBtns .next').removeClass('disabled');
    }

    function _updateBtns(index) {
        var stepCss = ['.legalInfoStep', '.marketplaceStep'];

        _clearAlerts();
        $find('.wzBtns .btn').addClass('hidden');
        $find(stepCss[index]).removeClass('hidden');
    }

    function _setActiveStep(index) {
        $find('.nav>li').removeClass('active');
        if (index !== 2) $find('.nav>li:eq(' + index + ')').addClass('active');
        $find('.nav>li:lt(' + index + ')').addClass('wz-done');

        $find('.tab-pane').removeClass('active');
        $find('.tab-pane:eq(' + index + ')').addClass('active');

        if (index === 2) {
            $find('.tab-pane:eq(1)').addClass('active');
        }
        _updateBtns(index);
    }

    var _clearValidationErrors = function ($tabContainer) {
        var badInputs = $tabContainer.find(".has-warning");
        badInputs.removeClass("has-warning");
        badInputs.find(":input").tooltip('destroy')
            .removeAttr('data-validated');
    };

    function _validateLegalInfoStep() {
        // if inputs at this tab are not valid -> cancel transition to the next tab
        var isFormValid = $("#communityForm").valid();
        var isTabValid = $("#legalInfoTab").has(".has-warning").length <= 0;
        if (!isFormValid) {
            _clearValidationErrors($("#marketplaceTab"));
        }
        return isFormValid ? true : isTabValid;
    }

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
                    _initCommunitiesList();
                    this.setEvents();
                    fragment.inited = true;
                    this.render();
                    this.show();
                }
            });
        },

        update: function (url) {
            cFragmentUrl = url;
            if (currentOrganizationFilter != ExchangeApp.modules.Header.getCurrentOrganizationFilter()) {
                ExchangeApp.routers.ModuleRouter.reload();
            }
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
                        _addStyler();
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
            $find("#createCommunity").on('click', function () {
                _initCreateEditCommunityDialog($find('#createCommunityContainer'), 0, $(this), ActionType.ADD);
            });
            $find("#searchCommunity").on('click', function () {
                _getFragment().widgets.communitiesList.api().ajax.reload();
                return false;
            });
            return false;
        },

        reloadNeeded: function () {
            return state.reloadNeeded;
        },

        getParentModule: function () {
            return pModule;
        },

        showCommunityDetails: function (communityId, data, isNew) {
            showCommunityDetails(communityId, data, isNew);
        }
    };
})();