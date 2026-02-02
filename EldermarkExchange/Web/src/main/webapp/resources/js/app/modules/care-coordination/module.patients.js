ExchangeApp.modules.CareCoordinationPatients = (function () {
    // history of loaded segments
    var fragmentsMap = {};
    // root url
    var cFragmentUrl = {
        template: 'care-coordination/patients'
    };
    // flag if true then reloaded every time
    var state = {
        reloadNeeded: true
    };
    var holderContentId = "patientsTabContent";
    var patientActiveTab;
    var patientEventsActiveTab;
    // load root routers (map url to module)
    var router = ExchangeApp.routers.ModuleRouter;
    // Utility
    var mdlUtils = ExchangeApp.utils.module;
    var wgtUtils = ExchangeApp.utils.wgt;
    var patientTabUtils;

    var fragmentLoader = ExchangeApp.loaders.FragmentLoader.init('careCoordinationPatients');

    var moduleLoader = ExchangeApp.loaders.ModuleLoader;

    var componentLoader = ExchangeApp.loaders.ComponentLoader;

    var pModule;


    var currentOrganizationFilter;
    var currentCommunityFilter;

    var isIncidentReportPopupOpen;

    var currentTotalNotesCount;
    var openNoteDetailsOnNextPage = false;
    var openEventDetailsOnNextPage = false;
    var displayedNoteId = null;
    var showNoteFromHistory = false;
    var followUpCodes = ['CM_24H', 'CM_14D', 'CM_ADDITIONAL'];
    var encounterType = ['FACE_TO_FACE_ENCOUNTER', 'NON_FACE_TO_FACE_ENCOUNTER'];
    var assessmentsGridPopupOpening = false;
    var ccdModalFunctions = {
        problems: initProblemsCcdModal
    };
    var idsMap = {
        problems: 'problemObservationId',
        planOfCare: 'planOfCareId'
    };
    //var showMergedPatients = false;

    var assessmentState = {}

    function setAssessmentState(state) {
        assessmentState = $.extend({}, assessmentState, state)
    }

    function clearAssessmentState() {
        assessmentState = {}
    }

    var AssessmentStatus = Object.freeze({
        "IN_PROCESS": 0,
        "INACTIVE": 1,
        "COMPLETED": 2
    });
    var AssessmentActionType = Object.freeze({
        'ADD': 'Add',
        'EDIT': 'Edit'
    });
    var AssessmentReviewType = Object.freeze({
        'VIEW': 'view',
        'EDIT': 'edit'
    });

    var collapsedExpandedPanels = {
        accordions: 0,
        collapse: 0,
        expand: 0
    };

    var AssessmentsLocalStorageService = (function () {
        var LOCAL_STORAGE_KEY = 'asmnt_tabkey';

        function _saveData(data) {
            if (data) {
                while (true) {
                    try {
                        localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(data));
                        break;
                    } catch (e) {
                        //quota exceeded
                        if (e.code === 22) {
                            if (data.length === 0) {
                                break
                            }
                            //delete the oldest saved tab
                            data.splice(0, 1)
                        } else {
                            throw e
                        }
                    }
                }
            }
        }

        function _loadData() {
            return localStorage.getItem(LOCAL_STORAGE_KEY) ?
                JSON.parse(localStorage.getItem(LOCAL_STORAGE_KEY)) : [];
        }

        function _findIndex(data, assessmentId) {
            return data.findIndex(function (elem) {
                //intentional '==' as types on both sides can be either string or number
                return elem.id == assessmentId;
            })
        }

        return {
            saveTabKey: function (assessmentId, tabKey) {
                if (!localStorage || !assessmentId) {
                    return;
                }
                var tabsData = _loadData();
                var tabIndex = _findIndex(tabsData, assessmentId);
                if (tabIndex !== -1) {
                    tabsData.splice(tabIndex, 1);
                }
                tabsData.push({
                    id: assessmentId,
                    tab: tabKey
                });
                _saveData(tabsData)
            },

            loadTabKey: function (assessmentId) {
                if (!localStorage || !assessmentId) {
                    return;
                }
                var tabsData = _loadData();
                var tabIndex = _findIndex(tabsData, assessmentId);
                return tabIndex === -1 ? null : tabsData[tabIndex].tab;

            },

            changeId: function (oldAssessmentId, newAssessmentId) {
                if (!localStorage || !oldAssessmentId || !newAssessmentId) {
                    return
                }
                var tabsData = _loadData();
                var tabIndex = _findIndex(tabsData, oldAssessmentId);
                if (tabIndex !== -1) {
                    tabsData.push({
                        id: newAssessmentId,
                        tab: tabsData[tabIndex].tab
                    });
                    tabsData.splice(tabIndex, 1);
                }
                _saveData(tabsData)
            }
        }
    }());

    /*-------------utils----------------*/

    function _prepare() {
        var cFragment = _getFragment();
        cFragment.random = mdlUtils.rand();
        _randomize();
    }

    function _grid(options) {
        var fragment = _getFragment();
        return wgtUtils.grid(fragment.$html, fragment.random, options);
    }

    function _wizard($container, options) {
        return wgtUtils.wizard($container, '', options);
    }

    function _alert(options) {
        var fragment = _getFragment();
        return wgtUtils.alert(fragment.$html, fragment.random, options);
    }

    function _clearAlerts() {
        $find('.alert').remove();
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

    function _addCreatePatientValidation($container) {
        return $container.find("#patientForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    'email': {
                        required: "#noemail:unchecked",
                        maxlength: 255,
                        emails: true
                    },
                    'firstName': {
                        required: true,
                        minlength: 2,
                        maxlength: 128
                    },
                    'lastName': {
                        required: true,
                        minlength: 2,
                        maxlength: 128
                    },
                    'gender': {required: true},
                    //'birthDate': {dateExp:true, required: true},
                    'birthDate': {
                        dateExp: /^\d{2}\/\d{2}\/\d{4}$/,
                        required: function (element) {
                            return true;
                        }
                    },
                    'address.street': {
                        required: true,
                        minlength: 2,
                        maxlength: 255
                    },
                    'address.city': {
                        required: true,
                        minlength: 2,
                        maxlength: 128
                    },
                    'address.state': {required: true},
                    'address.zip': {
                        required: true,
                        lengthEqual: 5
                    },
                    'communityId': {required: true},
                    'phone': {phone: true},
                    'cellPhone': {
                        required: true,
                        phone: true
                    },
                    'ssn': {
                        required: true,
                        ssn: true
                    },
                    'groupNumber': {maxlength: 250},
                    'memberNumber': {maxlength: 250},
                    'medicareNumber': {maxlength: 250},
                    'medicaidNumber': {maxlength: 250},
                    'primaryCarePhysician': {maxlength: 250},
                    'referralSource': {maxlength: 250},
                    'currentPharmacyName': {maxlength: 250},
                    'deviceID': {
                        required: function (element) {
                            return (!$("input[name='deviceIDSecondary']").is(':blank'))
                        }
                    },
                    'deviceIDSecondary': {
                        notEqual: "input[name='deviceID']"
                    }
                },
                messages: {
                    'firstName': {
                        required: getErrorMessage("field.empty")
                    },
                    'lastName': {
                        required: getErrorMessage("field.empty")
                    },
                    'communityId': {
                        required: getErrorMessage("field.empty")
                    },
                    'deviceID': {
                        required: getErrorMessage("deviceID.empty")
                    },
                    'deviceIDSecondary': {
                        notEqual: getErrorMessage("deviceIDSecondary.unique")
                    },
                    phone: {
                        phone: getErrorMessage("field.phone.format")
                    },
                    cellPhone: {
                        required: getErrorMessage("field.empty"),
                        phone: getErrorMessage("field.phone.format")
                    },
                    birthDate: {
                        dateExp: getErrorMessage("field.dateFormat"),
                        required: getErrorMessage("field.empty")
                    },
                    state: {
                        stateUS: getErrorMessage("field.state")
                    },
                    insuranceId: {
                        required: getErrorMessage("field.empty")
                    }
                }
            })
        );
    }

    // ===================== Functions for Select2
    function formatRepo(repo) {
        if (repo.loading) {
            return repo.text;
        }
        var markup = '<div class="clearfix">' + repo.label + '</div>';

        return markup;
    }

    function formatRepoSelection(repo) {
        return repo.label;
    }

    // ============= end functions for Select2

    // ===============================================================================================================
    function _initCheckbox($container, checkBoxId, contentId) {
        $container.find('[id="' + checkBoxId + '"]').on('change', function (e) {
            if ($(this).context.checked) {
                $container.find('[id="' + contentId + '"]').show();
            } else {
                $container.find('[id="' + contentId + '"]').hide();
            }
        });
    }


    // =======================
    function _initDeleteCareTeamMemberConfirmationDialog($deleteCareTeamMemberContainer, careTeamMemberId, careTeamMemberName, affiliatedSection) {
        $.ajax({
            url: 'care-coordination/patients/patient/' + $('#currentPatientId').val() + '/care-team/' + careTeamMemberId + '/delete',
            headers: {'X-Content-Compressing': 'enabled'}
        }).success(function (data) {


            $deleteCareTeamMemberContainer.empty();
            $deleteCareTeamMemberContainer.append(data);
            var $modal = $deleteCareTeamMemberContainer.find("#deleteCareTeamMemberModal");

            var careTeamMemberToDeleteId = careTeamMemberId;
            $deleteCareTeamMemberContainer.find("#careTeamMemberName").html("<b>" + careTeamMemberName + "</b>");

            $deleteCareTeamMemberContainer.find("#deleteCareTeamMember").on('click', function () {

                $.ajax({
                    url: 'care-coordination/patients/patient/' + $('#currentPatientId').val() + '/care-team/' + careTeamMemberToDeleteId
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
                    if (affiliatedSection) {
                        _getFragment().widgets.affiliatedPatientCareTeam.api().ajax.reload();
                    }
                    else {
                        _getFragment().widgets.patientCareTeam.api().ajax.reload();
                    }
                    $deleteCareTeamMemberContainer.find("#deleteCareTeamMemberModal").modal('hide');

                });
                return false;
            });

            $deleteCareTeamMemberContainer.find('.cancelBtn').on('click', function () {
                $deleteCareTeamMemberContainer.find('#deleteCareTeamMemberModal').modal('hide');

            });

            $deleteCareTeamMemberContainer.find('#deleteCareTeamMemberModal').modal('show');
            $deleteCareTeamMemberContainer.find('#deleteCareTeamMemberModal').on('hidden.bs.modal', function () {
                $(this).remove();
            });

        });
    }

    function _updateEmailFieldRequiredStatus($container, disabled) {
        var emailInput = $container.find('[id="contact.email"]');
        var emailLabel = $container.find('[id="email.label"]');
        if (disabled) {
            emailLabel.text("Email");
            emailInput.parents('.form-group').removeClass("has-warning");
            emailInput.removeAttr('data-validated');
            emailInput.tooltip('destroy')
        } else {
            emailLabel.text("Email*");
        }
        emailInput.prop("disabled", disabled);
    }

    function _initCreateEditPatientDialog($container, patientId, $button) {
        if ($button.hasClass("pending")) {
            return;
        }
        $button.addClass("pending");
        $.ajax({
            url: 'care-coordination/patients/patient/' + patientId,
            headers: {'X-Content-Compressing': 'enabled'}
        })
            .success(function (data) {

                $container.empty();
                $container.append(data);
                _addCreatePatientValidation($container);

                var $form = $container.find("#patientForm");
                var $warnMsg = $form.find("#communityWarning");
                if ($form.find("#patient\\.communityId").val() == "") {
                    if (!$warnMsg.is(":visible")) {
                        $form.find("#communityWarning").show();
                    }
                } else {
                    if ($warnMsg.is(":visible")) {
                        $form.find("#communityWarning").hide();
                    }
                }

                var $modal = $container.find('#createPatientModal');
                //var headerTitle = (patientId ? 'Edit' : 'Add new') + ' Patient';
                //$container.find("#patientHeader").html(headerTitle);
                $("input.datepicker").datepicker()
                    .on('changeDate', function (ev) {
                        $(this).trigger("focus").trigger("blur");
                    });

                var $insuranceDropdown = $container.find('[id="patient.insuranceId"]');
                $insuranceDropdown.select2({
                    placeholder: 'Search by network name',
                    width: '100%'
                });

                var $insurancePlanDropdown = $container.find('[id="patient.insurancePlanId"]');
                $insuranceDropdown.on("change", function () {
                    $.ajax({
                        type: 'GET',
                        contentType: 'json',
                        url: 'care-coordination/patients/patient/' + patientId + '/insuranceId/' + $(this).val() + '/plans',
                        success: function (data) {
                            $insurancePlanDropdown[0].options.length = 1;
                            $.each(data, function (i, v) {
                                $insurancePlanDropdown[0].add(new Option(v.label, v.id));
                            });
                            if ($insurancePlanDropdown[0].options.length > 1) {
                                $insurancePlanDropdown.removeAttr('disabled');
                            } else {
                                $insurancePlanDropdown.attr('disabled', 'disabled');
                            }
                        }
                    });
                });

                var noEmailCheckbox = $container.find('[id="noemail"]');
                var emailInput = $container.find('[id="contact.email"]');
                if (patientId != 0) {
                    noEmailCheckbox.prop("checked", emailInput.is(":blank"));
                    noEmailCheckbox.prop("disabled", !emailInput.is(":blank"));
                    _updateEmailFieldRequiredStatus($container, noEmailCheckbox.is(":checked"));
                }
                noEmailCheckbox.change(function () {
                    _updateEmailFieldRequiredStatus($container, noEmailCheckbox.is(":checked"));
                });
                emailInput.keyup(function () {
                    noEmailCheckbox.prop("disabled", !emailInput.is(":blank"));
                });

                var $orgSelector = $("#patient\\.organizationId");
                $orgSelector.change(function () {
                    var $communitySelector = $("#patient\\.communityId");
                    $communitySelector.empty();
                    if ($orgSelector.val() == 0) {
                        $communitySelector.append('<option value="">-- Select --</option>');
                        if ($warnMsg.is(":visible")) {
                            $form.find("#communityWarning").hide();
                        }
                    } else {
                        $.ajax({
                            url: 'care-coordination/communities/byorg/' + $orgSelector.val(),
                            type: 'GET'
                        }).success(function (data) {
                            if (data.content.length > 1) {
                                $communitySelector.append('<option value="">-- Select --</option>');
                            }
                            $.each(data.content, function (i, e) {
                                $communitySelector.append('<option value="' + e.id + '">' + e.name + '</option>');
                            });
                            var $warnMsg = $form.find("#communityWarning");
                            if (data.content.length == 0) {
                                if (!$warnMsg.is(":visible")) {
                                    $form.find("#communityWarning").show();
                                }
                            } else {
                                if ($warnMsg.is(":visible")) {
                                    $form.find("#communityWarning").hide();
                                }
                            }

                        }).fail(function (response) {
                            $("#formError").text(response.responseText);
                        });
                    }

                });

                $container.find('[id="patient.intakeDate"]').datetimepicker({
                    format: 'MM/DD/YYYY hh:mm A (Z)'
                });

                // ============      Save button =========================
                var $saveButton = $container.find("#savePatient");
                $saveButton.on('click', function () {

                    var $form = $container.find("#patientForm");
                    if (!$form.valid()) {
                        return false;
                    }
                    var $th = $(this);
                    if (!$th.hasClass("pending")) {
                        $th.addClass("pending");
                        if (patientId != 0) {
                            _savePatient(patientId, $form, $modal, $th);
                        }
                        else {
                            $.ajax({
                                url: 'care-coordination/patients/find-matches',
                                type: 'POST',
                                data: $form.serialize(),
                                beforeSend: function (xhr) {
                                    mdlUtils.csrf(xhr);
                                }
                            }).success(function (data) {

                                if (!data) {
                                    _savePatient(patientId, $form, $modal, $th);
                                }
                                else {
                                    $modal.modal('hide');
                                    var $container = mdlUtils.find(_getFragment().$html, '', '#matchedPatientListContainer');
                                    $container.empty();
                                    $container.append(data);
                                    var $matchedPatientListModal = $container.find('#matchedPatientListModal');
                                    $container.find('#createRecord').on('click', function () {
                                        $matchedPatientListModal.modal('hide');
                                        _savePatient(patientId, $form, $modal, $th);
                                    });
                                    $matchedPatientListModal.modal({backdrop: 'static'});
                                    $matchedPatientListModal.on('hidden.bs.modal', function () {
                                        $(this).remove();
                                    });
                                }
                            }).fail(function (response) {
                                mdlUtils.onAjaxError(response, function () {
                                    bootbox.alert(response.responseText);
                                    $th.removeClass("pending");
                                });
                            });
                        }
                    }
                    return false;
                });

                $modal.modal({backdrop: 'static'});
                $modal.on('hidden.bs.modal', function () {
                    $(this).remove();
                });
                $button.removeClass("pending");
            })
            .fail(function () {
                $button.removeClass("pending");
                alert('Internal server error. Please contact administrator.');
            });
    }

    function _savePatient(patientId, form, modal, $button) {
        var deviceID = document.getElementById('patient.deviceID');
        var deviceIDSecondary = document.getElementById('patient.deviceIDSecondary');
        var facilityId = document.getElementById('patient.communityId');
        var url = 'care-coordination/patients/patient/' + patientId;

        if (deviceID != null && facilityId != null) {
            deviceID = deviceID.value;
            facilityId = facilityId.value;
            url += '/validateDevices?facilityId=' + facilityId + '&deviceId=' + deviceID;
            if (deviceIDSecondary != null) {
                url += '&deviceIdSecondary=' + deviceIDSecondary.value;
            }
        }
        $.ajax({
            url: url,
            type: 'GET'
        }).success(function (data) {
            if (data == '' || !(data instanceof Array)) {
                $.ajax({
                    url: 'care-coordination/patients/patient/' + patientId,
                    type: 'POST',
                    data: form.serialize(),
                    beforeSend: function (xhr) {
                        mdlUtils.csrf(xhr);
                    }
                }).success(function (data) {
                    _getFragment().widgets.patientsList.api().ajax.reload();
                    modal.modal('hide');
                    $('.modal-backdrop').remove();
                    ExchangeApp.managers.EventManager.publish('patients_list_changed');
                    showPatientDetails(data, null, patientId == 0, patientId != 0);
                }).fail(function (response) {
                    mdlUtils.onAjaxError(response, function () {
                        bootbox.alert(response.responseText);
                        $button.removeClass("pending");
                    });
                })
            } else {
                if (data instanceof Array) {
                    var mess = "";
                    for (i = 0; i < data.length; i++) {
                        mess += "Device with ID " + data[i].deviceID + " is already used by " + data[i].patientName + ". "
                    }
                    mess += "Do you want to change the selection?"
                    bootbox.confirm({
                        message: mess,
                        buttons: {
                            confirm: {
                                label: 'Yes',
                                className: 'btn-primary'
                            },
                            cancel: {
                                label: 'No',
                                className: 'btn-default'
                            }
                        },
                        callback: function (result) {
                            if (!result) {
                                $button.removeClass("pending");
                            } else {
                                $.ajax({
                                    url: 'care-coordination/patients/patient/' + patientId,
                                    type: 'POST',
                                    data: form.serialize(),
                                    beforeSend: function (xhr) {
                                        mdlUtils.csrf(xhr);
                                    }
                                }).success(function (data) {
                                    _getFragment().widgets.patientsList.api().ajax.reload();
                                    modal.modal('hide');
                                    $('.modal-backdrop').remove();
                                    ExchangeApp.managers.EventManager.publish('patients_list_changed');
                                    showPatientDetails(data, null, patientId == 0);
                                }).fail(function (response) {
                                    mdlUtils.onAjaxError(response, function () {
                                        bootbox.alert(response.responseText);
                                        $button.removeClass("pending");
                                    });
                                })
                            }
                        }
                    })

                }
            }
        }).fail(function (response) {
            mdlUtils.onAjaxError(response, function () {
                bootbox.alert(response.responseText);
                $button.removeClass("pending");
            });
        })
    }

    //==================

    function setServicePlanTabActive(patientId) {
        var $cc = $('#care-coordination');
        var $tabs = $cc.find('.patientTabs');
        var $active = $tabs.filter('.active');

        if ($active.attr('id') !== 'servicePlansTab') {
            $tabs.each(function () {
                var $tab = $(this);
                var id = $tab.attr('id');

                if (id === 'servicePlansTab') $tab.addClass('active');
                else $tab.removeClass('active');
            });

            $cc.find('.patientTabContent').each(function () {
                var $content = $(this);
                var id = $content.attr('id');

                if (id === 'patientServicePlansContent') $content.addClass('active');
                else $content.removeClass('active');
            });

            setTimeout(function () {
                router.route({template: 'care-coordination/patients/' + patientId + '/service-plans'});
            }, 300);
        }
    }

    function _initServicePlans(params) {
        var components = [
            'Component',
            'Widget',
            'ToolBar',
            'Grid',
            'Form',
            'Modal',
            'Popover',
            'Tabs',
            'Slider',
            'Table',
            'TableRow',
            'TableCell',
            'DynaFooter',
            'Loader',
            'SearchPanel',
            'AnchorLinkPanel',
            'care-coordination/patient/ServicePlanList',
            'care-coordination/patient/ServicePlanForm',
            'care-coordination/patient/ServicePlanFormNeedSection',
            'care-coordination/patient/ServicePlanFormGoalSection',
            'care-coordination/patient/ServicePlanFormScoringSection',
            'care-coordination/patient/ServicePlanModal',
            'care-coordination/patient/ServicePlanDetails',
            'care-coordination/patient/ServicePlanChangeHistoryList',
            'care-coordination/patient/ServicePlanDetailedInfoTabs',
            'care-coordination/patient/ServicePlanListPanel'
        ];

        componentLoader.load(components, function () {
            var patientId = $('#currentPatientId').val();

            var panel = new ServicePlanListPanel({
                container: params.container,
                patientId: patientId,
                onSavePlanSuccess: function () {
                    _initServicePlansTotal(patientId);
                    setServicePlanTabActive(patientId);
                }
            });

            _getFragment().widgets.servicePlanListPanel = panel;

            var $btn = mdlUtils.find(_getFragment().$html, '', '#addServicePlan');

            $btn.off().on('click', function () {
                $.ajax({
                    type: 'GET',
                    url: 'care-coordination/patients/patient/' + patientId + '/service-plans/can-create',
                    beforeSend: function (xhr) {
                        mdlUtils.csrf(xhr);
                    },
                    success: function (isAllowed) {
                        if (isAllowed) {
                            panel.onAddPlan();
                        }
                        else bootbox.alert('There is an active service plan. <br/>You ' +
                            'can not create a new one until the active plan is completed.');
                    },
                    fail: function () {
                        bootbox.alert('Cannot create a new service plan. ' +
                            'Please try again later.');
                    }
                });
            });

            panel.mount();
        });
    }

    // =================
    function _initNotificationPreferences(container, data) {
        var $notificationsContainer = container.find("#careTeamMemberNotificationPreferences");
        $notificationsContainer.empty();

        var $rowTemplateAll = container.find("#notificationPreferencesTemplate").clone();
        $rowTemplateAll.attr('id', 'notificationPreferencesTemplate-all');
        $rowTemplateAll.find(".eventType").html("All Events").css('font-style', 'italic');

        var $responsibilityAll = $rowTemplateAll.find(".responsibility");
        $responsibilityAll.append("<option selected disabled hidden style='display: none' value=''></option>");
        $responsibilityAll.selectpicker();
        $responsibilityAll.on("change", function (e) {
                $notificationsContainer.find(".responsibility:enabled").not(this).val($(this).val()).trigger("change");
            }
        );

        var $notificationTypeAll = $rowTemplateAll.find(".notificationType");
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
            } else {
                $responsibilityAll.selectpicker('val', 'N');
                $responsibilityAll.trigger('change');
            }
            dropdowns.find('.notesNotificationType').selectpicker('val', optionsToSet);
            var $noteDropdown = $notificationsContainer.find('.notes-dropdown-menu');
        });

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

                if (!e.canChange) {
                    $responsibility.prop('disabled', 'disabled');
                    $responsibility.addClass('notificationsDisabledSelect');
                }
                if (e.eventType === 'COVID-19') {
                    $responsibility.find("option[value='V']").prop("disabled",true);
                    $responsibility.find("option[value='N']").prop("disabled",true);
                }
                $responsibility.selectpicker('val', e.responsibility);

                var $notificationType = $rowTemplate.find(".notificationType");
                $notificationType.attr('name', 'notificationPreferences[' + i + '].notificationTypeList');
                $notificationType.attr('id', 'notificationPreferences-' + i);

                if (e.notificationTypeList) {
                    //Currently disabling "Bluestone bridge" and "Push Notifications" for Notes preferences as this functionality is not present
                    if (group.name === "Notes") {
                        e.notificationTypeList = e.notificationTypeList;
                    }
                    $notificationType.selectpicker('val', e.notificationTypeList);
                }

                if (group.name === "Notes") {
                    $notificationType.addClass('notesNotificationType');
                    var $dropdown = $rowTemplate.find(".notificationType > .dropdown-menu");
                    $dropdown.addClass('notes-dropdown-menu');
                }

                $notificationType.on('changed.bs.select', mdlUtils.allOptionHandler);
                $notificationType.on('change', function (e) {
                    if (!e.currentTarget.selectedOptions.length && $responsibility.is(':enabled')) {
                        $responsibility.selectpicker('val', 'N');
                    }
                });

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
        var url = 'care-coordination/patients/patient/' + $('#currentPatientId').val() + '/care-team/notification-preferences?careTeamRoleId=' + roleId;
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

    function _saveCareTeamMember($container, $modal, affiliatedSection, $button, ajaxLoader) {
        if ($button.hasClass('pending')) {
            return;
        }
        $button.addClass('pending');
        var $form = $container.find("#careTeamMemberForm");
        var formData = form2js('careTeamMemberForm', '.', true,
            function (node) {
                if (node.id && node.id.match(/callbackTest/)) {
                    return {
                        name: node.id,
                        value: node.innerHTML
                    };
                }
            });
        if (!$form.valid()) {
            return false;
        }
        delete formData._csrf;
        delete formData._;
        ajaxLoader.show();
        $.ajax({
            url: 'care-coordination/patients/patient/' + $('#currentPatientId').val() + '/care-team/',
            type: 'PUT',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(formData),
            beforeSend: function (xhr) {
                mdlUtils.csrf(xhr);
            }
        }).success(function (data) {
            if (affiliatedSection) {
                _getFragment().widgets.affiliatedPatientCareTeam.api().ajax.reload();
            }
            else {
                _getFragment().widgets.patientCareTeam.api().ajax.reload();
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
            //alert(response.responseText);
        });
        return false;
    }

    function _initCreateEditCareTeamMemberDialog($container, careTeamMemberId, careTeamMemberDescription, careTeamMemberEmployee, careTeamMemberRole, affiliatedSection, $button) {
        if ($button.hasClass("pending")) {
            return;
        }
        $button.addClass("pending");

        $.ajax({
            url: 'care-coordination/patients/patient/' + $('#currentPatientId').val() + '/care-team/' + (careTeamMemberId ? careTeamMemberId + '/' : '') + affiliatedSection,
            headers: {'X-Content-Compressing': 'enabled'}
        })
            .success(function (data) {

                $container.empty();
                $container.append(data);
                _addCreateTeamMemberValidation($container);
                var $modal = $container.find('#createCareTeamMemberModal');

                var headerTitle = (careTeamMemberId ? 'Edit ' : 'Add New ') + 'Patient Care Team Member';
                $container.find("#careTeamMemberHeader").html(headerTitle);
                $($container.find("#careTeamMemberId")).val(careTeamMemberId);

                $($container.find("#careTeamDescription")).val(careTeamMemberDescription);


                // ======================== init employee selector
                var $employeeSelect = $container.find("#careTeamEmployeeSelect");

                //var employeesUrl = affiliatedSection?'care-coordination/employees/affiliated/forPatient/'+ $('#currentPatientId').val():
                //    'care-coordination/employees';
                //var employeesUrl = affiliatedSection?'care-coordination/employees/affiliated/'+ $('#communityId').val():
                //    'care-coordination/employees';
                //
                //
                //$.ajax(employeesUrl).success(function (data) {
                //    $.each(data, function (i, e) {
                //        $employeeSelect.append('<option value="' + e.id + '">' + e.label + '</option>');
                //    });
                $employeeSelect.select2({
                    placeholder: 'Select Contact',
                    width: '100%'//,
                    //minimumInputLength: 2
                });
                $employeeSelect.on('select2:select', function (e) {
                    _updateNotificationPreferences($container);
                });
                if (careTeamMemberEmployee) {
                    $employeeSelect.val(careTeamMemberEmployee.id).trigger("change");
                    $employeeSelect.prop("disabled", true);

                }

                //_updateNotificationPreferences($container);
                //});


                // ===================== Init Role Select =================================
                var $roleSelect = $container.find("#careTeamRoleSelect");

                $roleSelect.select2({
                    placeholder: 'Select Role',
                    width: '100%'
                });
                $roleSelect.on('select2:select', function (e) {
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
                        //_saveCareTeamMember($container,$modal,affiliatedSection);
                        e.preventDefault();
                        return false;
                    }
                });
                $container.find('.cancelBtn').on('click', function () {
                    $container.find('#createCareTeamMemberModal').modal('hide');

                });

                $container.find('#createCareTeamMemberModal').modal('show');
                $container.find('#createCareTeamMemberModal').on('hidden.bs.modal', function () {
                    $(this).remove();
                });

                if (careTeamMemberId) {
                    _updateNotificationPreferences($container);
                }
                $button.removeClass("pending");
            })
            .fail(function () {
                $button.removeClass("pending");
                alert('Internal server error. Please contact administrator');
            });


    }

    function _initPatientCareTeam(affiliatedSection) {
        var $createCareTeamMemberContainer = mdlUtils.find(_getFragment().$html, '', '#createCareTeamContainer');
        var $deleteCareTeamMemberContainer = mdlUtils.find(_getFragment().$html, '', '#deleteCareTeamContainer');

        // patientCareTeam
        var widget = wgtUtils.grid(_getFragment().$html, '', {
            tableId: affiliatedSection ? "affiliatedPatientCareTeam" : "patientCareTeam",
            totalDisplayRows: 25,
            colSettings: {
                'description': {bSortable: false},
                'actions': {
                    width: '100px',
                    'className': 'dt-head-center'
                }
            },

            callbacks: {
                rowCallback: function (row, data, index) {
                    var $row = $(row);
                    //if (data) {
                    if (data && ($('#affiliatedView').val() == 'false' || affiliatedSection)) {
                        var actionsColumn = 'td:eq(3)';
                        if ($('#hasMerged').val() == 'true' && !affiliatedSection) {
                            actionsColumn = 'td:eq(4)';
                        }
                        if ($('#hasMerged').val() == 'true' && affiliatedSection) {
                            actionsColumn = 'td:eq(5)';
                        }
                        $row.children(actionsColumn).empty();

                        var careTeamMemberId = data.id;
                        var careTeamMemberEmployee = data.employee;
                        var careTeamMemberRole = data.role;
                        var careTeamMemberDescription = data.description;

                        var $tdRowActions = mdlUtils.find(_getFragment().$html, '', '#rowActions').clone();
                        if (data.editable || data.deletable || data.nucleusUserId) {
                            var tdRowActionsId = 'rowActions-' + index;
                            $tdRowActions.attr('id', tdRowActionsId);
                            $tdRowActions.removeClass('hidden');
                            $row.children(actionsColumn).append($tdRowActions);
                        }

                        if (data.editable) {
                            $tdRowActions.find(".editCareTeamMember").removeClass('hidden');
                            $tdRowActions.find(".editCareTeamMember").on('click', function () {
                                    _initCreateEditCareTeamMemberDialog($createCareTeamMemberContainer, careTeamMemberId, careTeamMemberDescription, careTeamMemberEmployee, careTeamMemberRole, affiliatedSection, $(this));
                                }
                            );
                            if (!data.deletable) {
                                $tdRowActions.css("padding-right", "60px");
                            }
                        }

                        if (data.deletable) {
                            $tdRowActions.find(".deleteCareTeamMember").removeClass('hidden');
                            $tdRowActions.find(".deleteCareTeamMember").on('click', function () {
                                _initDeleteCareTeamMemberConfirmationDialog($deleteCareTeamMemberContainer, careTeamMemberId, careTeamMemberEmployee.label, affiliatedSection);
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
                    //}
                }
                ,
                errorCallback: function (error) {
                    alert(error.responseText);
                    // TODO

                }
                ,
                footerCallback: function (tfoot, data, start, end, display) {
                    $(tfoot).hide();
                }
            }
        });
        if (affiliatedSection) {
            _getFragment().widgets.affiliatedPatientCareTeam = widget;
        }
        else {
            _getFragment().widgets.patientCareTeam = widget;
        }
    }

    function showMergedPatients(patientId, $row) {

        //var $mergedResidentRows = $(".forResidentId" + patientId);
        var $mergedResidentRows = $("[data-merged-id='" + patientId + "']");
        var $showMergedLink = $row.find('.showMergedLink');
        var showDeactivated = $find("#showDeactivated").val();
        if ($mergedResidentRows.length == 0) {
            $.ajax({
                url: 'care-coordination/patients/patient/' + patientId + '/getMergedPatients/' + showDeactivated,
                type: 'GET'
            }).success(function (data) {
                for (var i = 0; i < data.length; i++) {
                    var $mpRow = $('<tr class="mergedResident" data-id="' + data[i].id + '" data-merged-id="' + patientId + '"><td>' + data[i].firstName + '</td><td>' + data[i].lastName + '</td><td>' + (data[i].gender ? data[i].gender : "") + '</td><td>' + data[i].birthDate + '</td><td>' + data[i].ssn + '</td><td>' + data[i].eventCount + '</td><td>' + data[i].community + '</td><td>' + (data[i].dateCreated ? data[i].dateCreated : "") + '</td><td></td></tr>');
                    $mpRow.insertAfter($row);
                    $mpRow.on('click', function () {
                        var id = $(this).data("id");
                        showPatientDetails(id);
                    });
                    if (!data[i].active) {
                        $mpRow.css("color", "silver");
                        $mpRow.children('td:eq(0)').css("color", "rgb(165, 200, 200)");
                    }
                    if ($row.hasClass("even")) {
                        $mpRow.addClass('even');
                    }
                    else {
                        $mpRow.addClass('odd');
                    }
                }
                $row.addClass("hasMergedResidents");
                $showMergedLink.text("Hide Matches");
            }).fail(function (response) {
                //$("#formError").text(response.responseText);
                alert(response.responseText);
            });
        }
        else {
            $mergedResidentRows.remove();
            $showMergedLink.text("Show Matches");
            $row.removeClass("hasMergedResidents")
        }
    }

    function _initIncidentReportModal(element, endpoint, eventId, reportId) {
        requirejs(
            [
                'app/components/care-coordination/events/IncidentReportModal'
            ],
            function (Modal) {

                var modal = null;

                var options = {
                    isOpen: true,
                    container: document.body,
                    eventId: eventId,
                    reportId: reportId,
                    onHide: function (e) {
                        if (modal.isReportChanged() && !modal.isReportChangeIgnored()) {
                            e.preventDefault();

                            bootbox.confirm({
                                message: 'The changes will not be saved',
                                buttons: {
                                    confirm: {
                                        label: 'OK'
                                    },
                                    cancel: {
                                        label: 'CANCEL'
                                    }
                                },
                                callback: function (confirm) {
                                    if (confirm) {
                                        modal.ignoreReportChange();

                                        setTimeout(function () {
                                            modal.hide();
                                        })
                                    }
                                }
                            });
                        }
                    },
                    onSubmitReportSuccess: function (reportId) {
                        modal.hide();

                        options.reportId = reportId;

                        bootbox.confirm({
                            message: 'The updates have been saved. The pdf file sent to a mandated reporting governing body',
                            buttons: {
                                confirm: {
                                    label: 'View pdf file'
                                },
                                cancel: {
                                    label: 'Close'
                                }
                            },
                            callback: function (confirm) {
                                if (confirm) {
                                    var offset = new Date().getTimezoneOffset();
                                    window.open('ir/events/' + eventId + '/pdf-incident-report?timeZoneOffset=' + offset);
                                }
                            }
                        });

                        _showEventDetails(eventId);
                    },
                    onSubmitReportFailure: function () {
                        bootbox.alert('Cannot save the updates. Please try again later');
                    },
                    onSaveReportDraftSuccess: function (reportId) {
                        modal.hide();

                        options.reportId = reportId;

                        bootbox.confirm({
                            message: 'The incident report has been saved',
                            buttons: {
                                confirm: {
                                    label: 'Edit'
                                },
                                cancel: {
                                    label: 'Close'
                                }
                            },
                            callback: function (confirm) {
                                if (confirm) {
                                    modal = new Modal(options);
                                    modal.mount();
                                }
                            }
                        });

                        _showEventDetails(eventId);
                    },
                    onSaveReportDraftFailure: function () {
                        bootbox.alert('Cannot save the incident report. Please try again later');
                    }
                };

                element.on('click', function () {
                    modal = new Modal(options);
                    modal.mount();

                    setTimeout(function () {
                        $('#incidentReportPopup').css({'display': 'none'});
                    })
                });
            }
        );
    }

    function _initEventNotificationList() {
        isIncidentReportPopupOpen = true;

        _getFragment().widgets.patientEventNotificationList = wgtUtils.grid(_getFragment().$html, '', {
            tableId: "patientEventNotificationList",
            totalDisplayRows: 25,

            callbacks: {
                rowCallback: function (row, data, index) {
                    var $row = $(row);
                    if (data) {
                        $row.children('td:eq(0)').html(moment(data.dateTime).format('<b>MM/DD/YYYY</b> <br/> hh:mm A'));
                    }
                    $row.popover({
                        html: true,
                        content: data.sentToText + " with text : " + data.details,
                        trigger: 'hover',
                        delay: 300,
                        placement: function () {
                            $row.addClass('hasPopover');
                            return 'top';
                        }
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
        _getFragment().widgets.patientEventNotificationList.api().ajax.reload();
    }

    function _initEventList() {
        $('#eventDetails').hide();
        _getFragment().widgets.patientEventList = wgtUtils.grid(_getFragment().$html, '', {
            tableId: "patientEventList",
            totalDisplayRows: 25,
            selectable: {
                type: 'single'
            },

            callbacks: {
                rowCallback: function (row, data, index) {
                    var $row = $(row);

                    if (data != null) {
                        var eventId = data.eventId;
                        // clear row content
                        $row.children('td:eq(0)').html('<div class="col-md-8">' + data.residentName + ' <br/> ' + data.eventType + '</div><div class="col-md-4"> ' + moment(data.eventDate).format('<b>MM/DD/YYYY</b> <br/> hh:mm A') + '</div>');
                        $row.attr("data-id", eventId);

                        $row.on('click', function () {
                            // Update Event details section
                            _showEventDetails(eventId);

                        });
                        $row.css('cursor', 'pointer');
                    }
                },
                "drawCallback": function (settings) {
                    if (openEventDetailsOnNextPage && cFragmentUrl.params && cFragmentUrl.params.event) {
                        var $row = $find("[data-id='" + cFragmentUrl.params.event + "']");
                        $row.addClass("selected");
                        openEventDetailsOnNextPage = false;
                    }
                }
                ,
                errorCallback: function (error) {
                    alert(error.responseText);
                    // TODO
                },
                footerCallback: function (tfoot, data, start, end, display) {
                    //$(tfoot).show();
                    //
                    //var isHidden = data.length < 20;
                    //if (isHidden) {
                    $(tfoot).hide();
                    //}
                },
                headerCallback: function (thead, data, start, end, display) {
                    $(thead).hide();
                }
            }
        });
        _getFragment().widgets.patientEventList.api().ajax.reload();

        // ========================= Init Add new Event
        var $newEventContainer = mdlUtils.find(_getFragment().$html, '', '#createNewEventContainer');

        mdlUtils.find(_getFragment().$html, '', '#createNewEvent').on('click', function () {
            var $button = $(this);
            if ($button.hasClass("pending")) {
                return;
            }
            $button.addClass("pending");
            $.ajax({
                url: 'care-coordination/patients/patient/' + $('#currentPatientId').val() + '/event-new/',
                headers: {'X-Content-Compressing': 'enabled'}
            })
                .success(function (data) {


                    $newEventContainer.empty();
                    $newEventContainer.append(data);

                    _initCheckbox($newEventContainer, 'includeManager', 'includeManagerContent');
                    _initCheckbox($newEventContainer, 'includeResponsible', 'includeResponsibleContent');
                    _initCheckbox($newEventContainer, 'responsible.includeAddress', 'includeResponsibleAddressContent');
                    _initCheckbox($newEventContainer, 'eventDetails.followUpExpected', 'followUpDetailsContent');
                    _initCheckbox($newEventContainer, 'includeTreatingPhysician', 'includeTreatingPhysicianContent');
                    _initCheckbox($newEventContainer, 'treatingPhysician.includeAddress', 'includeTreatingPhysicianAddressContent');
                    _initCheckbox($newEventContainer, 'includeHospital', 'includeHospitalContent');
                    _initCheckbox($newEventContainer, 'treatingHospital.includeAddress', 'includeHospitalAddressContent');
                    _initCheckbox($newEventContainer, 'includeResponsible', 'includeResponsibleContent');

                    $newEventContainer.find('[id="eventDetails.eventDatetime"]').datetimepicker({
                        defaultDate: new Date(),
                        format: 'YYYY-MM-DD hh:mm A Z'
                    });

                    _addNewEventValidation($newEventContainer);

                    //var clicked = false;
                    $newEventContainer.find("#createNewEventBtn").on('click', function () {
                        var $form = $newEventContainer.find("#newEventForm");
                        if (!$form.valid()) {
                            return false;
                        }
                        var $th = $(this);
                        if (!$th.hasClass("pending")) {
                            $th.addClass("pending");
                            $.ajax({
                                url: $form.attr('action'),
                                data: new FormData($form[0]),
                                type: 'POST',
                                enctype: 'multipart/form-data',
                                processData: false,
                                contentType: false,
                                beforeSend: function (xhr) {
                                    mdlUtils.csrf(xhr);
                                },
                                success: function (data) {
                                    bootbox.alert('Event has been successfully created', function () {
                                        $newEventContainer.find("#createNewEventModal").modal('hide');
                                    });
                                },
                                error: function (error) {
                                    mdlUtils.onAjaxError(error, function () {
                                        alert(error.responseText);
                                    });
                                    $newEventContainer.find("#createNewEventModal").modal('hide');
                                    $th.removeClass("pending");
                                }
                            }).complete(function () {
                                $newEventContainer.find("#createNewEventModal").hide();
                                _getFragment().widgets.patientEventList.api().ajax.reload();
                            });
                        }
                        return false;
                    });

                    //mdlUtils.randomize($newEventContainer, _getFragment().random, false)

                    $button.removeClass("pending");
                    $newEventContainer.find("#createNewEventModal").modal('show');
                })
                .fail(function (response) {
                    $button.removeClass("pending");
                    alert(response.responseText); // TODO
                });
            return false;
        });

    }

    function _showEventDetails(eventId, successCallback, failCallback) {
        $.ajax({
            type: 'GET',
            url: 'care-coordination/patients/patient/' + $('#currentPatientId').val() + '/events/' + eventId + "/event-details",
            headers: {'X-Content-Compressing': 'enabled'}
        }).success(function (data) {
            var $eventDetails = mdlUtils.find(_getFragment().$html, '', "#eventDetails");
            $eventDetails.empty();
            $eventDetails.append(data);
            $eventDetails.show();

            _initEventNotificationList();

            $eventDetails.find('#downloadBtn').on('click', function (e) {
                $.fileDownload(
                    "care-coordination/events-log/event/" + eventId + "/download-pdf", {});
            });

            $eventDetails.find('#incidentReport').on('click', function (e) {
                $('#incidentReportPopup').css({'display': 'flex'});
            });

            $eventDetails.on('click', function (e) {
                if (!$(e.target).closest('#incidentReport').length) {
                    $('#incidentReportPopup').css({'display': 'none'});
                }
            });

            var $editIRBtn = $eventDetails.find('#editIncidentReport');

            if ($editIRBtn.length) {
                var reportId = $editIRBtn.data('reportId');
                _initIncidentReportModal(mdlUtils.find(_getFragment().$html, '', '#editIncidentReport'), '', eventId, reportId);
            } else {
                _initIncidentReportModal(mdlUtils.find(_getFragment().$html, '', '#createIncidentReport'), '', eventId);
            }

            var $viewIRBtn = $eventDetails.find('#viewIncidentReport');

            if ($viewIRBtn.length) {
                $viewIRBtn.on('click', function (e) {
                    var offset = new Date().getTimezoneOffset();
                    window.open('ir/events/' + eventId + '/pdf-incident-report?timeZoneOffset=' + offset);
                });
            }

            $eventDetails.find('#downloadBtn').on('click', function (e) {
                $.fileDownload(
                    "care-coordination/events-log/event/" + eventId + "/download-pdf", {});
            });

            _initNotesModal(mdlUtils.find(_getFragment().$html, '', '#addEventNote'),
                'care-coordination/notes/event/' + eventId + '/new-note',
                'A note has been created',
                function (data) {
                    var patientId = $('#currentPatientId').val();
                    _initNotesTotal(patientId);
                    _showNote(data, patientId)
                }
            );

            mdlUtils.find($eventDetails, _getFragment().random, '.relatedNoteLink').on('click', function () {
                var template = $(this).attr('data-ajax-url-tmpl');
                var vars = $(this).attr('data-ajax-url-vars');
                var params = $(this).attr('data-ajax-url-params');

                var url = mdlUtils.getUrl(template, vars, params);
                router.route(url);
                return false;
            });

            if (successCallback) {
                successCallback();
            }
        }).fail(function (response) {
            if (failCallback) {
                failCallback(response);
            }
        });
    }

    function _selectByIndex(table, index) {
        var row = table.api().row(index);
        row.nodes().to$().click();
        if (!row.nodes().to$().hasClass('selected')) {
            row.nodes().to$().addClass("selected");
        }
    }

    function _selectFirstNote() {
        _getFragment().widgets.patientNoteList.api().ajax.reload(function () {
            _selectByIndex(_getFragment().widgets.patientNoteList, 0);
        });
    }

    function _initAssessmentsButton() {
        var $addAssessmentButton = mdlUtils.find(_getFragment().$html, '', '#addAssessmentResults');
        $addAssessmentButton.on('click', function () {
            _initCreateAssessmentResultDialog(mdlUtils.find(_getFragment().$html, '', '#assessmentContainer'), $('#currentPatientId').val(), $(this));
        });
    }

    function _initCreateAssessmentResultDialog($container, patientId, $button) {
        if ($button.hasClass("pending")) {
            return;
        }
        $button.addClass("pending");
        $.ajax({
            url: 'care-coordination/assessment/patient/' + patientId + '/new-result',
            headers: {'X-Content-Compressing': 'enabled'}
        })
            .success(function (data) {
                $container.empty();
                $container.append(data);
                $container.find("input[type='radio']").styler();
                var $modal = $container.find('#addAssessmentResultModal');
                _updateBtns(0);

                var $nextButton = $container.find("#nextBtn");

                var $modalContentContainer = mdlUtils.find(_getFragment().$html, '', '#assessmentModalContent');
                var $modalContentContainerSurvey = mdlUtils.find(_getFragment().$html, '', '#assessmentModalContentSurvey');

                $nextButton.on('click', function () {
                    $.ajax({
                        url: 'care-coordination/assessment/patient/' + patientId + '/new-assessment-data',
                        headers: {'X-Content-Compressing': 'enabled'}
                    })
                        .success(function (data) {
                            var assessmentIdForm = $('input[name=assessmentId]:checked', '#assessmentTypeForm').val();

                            $modalContentContainer.hide();
                            $modalContentContainerSurvey.empty();
                            $modalContentContainerSurvey.append(data);

                            $("#assessmentIdFormField").val(assessmentIdForm);

                            setAssessmentState({
                                employeeName: $(data).find('input[name="employeeName"]').val()
                            });

                            $container
                                .find('#dateCompleted')
                                .datetimepicker({
                                    defaultDate: new Date(),
                                    format: 'MM/DD/YYYY hh:mm:ss A Z',
                                    maxDate: new Date()
                                })
                                .on('dp.change', function () {
                                    setAssessmentState({isChanged: true})
                                });

                            $container
                                .find('#subjective')
                                .on('change', function () {
                                    setAssessmentState({isChanged: true})
                                });

                            loadAssessment($container, $modal, AssessmentActionType.ADD);
                        });
                });

                $modal.modal('show');
                $modal.on('hidden.bs.modal', function () {
                    $(this).remove();
                });

                $button.removeClass("pending");
            })
            .fail(function (data, data2) {
                console.log(data, data2);
                $button.removeClass("pending");
                alert('Internal server error. Please contact administrator.');
            });

        return false;
    }

    function _initEditAssessmentResultDialog($container, patientId, assessmentResultId, assessmentId, $button) {
        if ($button.hasClass("pending")) {
            return;
        }
        $button.addClass("pending");
        $.ajax({
            url: 'care-coordination/assessment/patient/' + patientId + '/edit-assessment-data/' + assessmentResultId,
            headers: {'X-Content-Compressing': 'enabled'},
            beforeSend: function () {
                $('#loader-div').removeClass('hidden');
            },
            complete: function () {
                $('#loader-div').addClass('hidden');
            }
        })
            .success(function (data) {
                $container.empty();
                $container.append(data);

                $("#assessmentIdFormField").val(assessmentId);

                $container
                    .find('#dateCompleted')
                    .datetimepicker({
                        defaultDate: new Date(),
                        format: 'MM/DD/YYYY hh:mm:ss A Z',
                        maxDate: new Date()
                    })
                    .on('dp.change', function () {
                        setAssessmentState({isChanged: true})
                    });

                $container
                    .find('#subjective')
                    .on('change', function () {
                        setAssessmentState({isChanged: true})
                    });

                var $modal = $container.find('#addAssessmentResultModal');

                loadAssessment($container, $modal, AssessmentActionType.EDIT, assessmentResultId);

                $modal.modal('show');

                $modal.on('hidden.bs.modal', function () {
                    $(this).remove();
                });

                $button.removeClass("pending");
            })
            .fail(function (data, data2) {
                console.log(data, data2);
                $button.removeClass("pending");
                alert('Internal server error. Please contact administrator.');
            });
        return false;
    }

    function _downloadAssessmentResult(patientId, assessmentResultId, assessmentId, $button) {
        if ($button.hasClass("pending")) {
            return;
        }
        $button.addClass("pending");

        var ajaxDownloadOptions = {
            data: {timeZoneOffset: new Date().getTimezoneOffset()},
            successCallback: function () {
                $button.removeClass("pending");
            },
            failCallback: function (error) {
                $button.removeClass("pending");
                _alert({
                    action: 'add',
                    placeSelector: '.patientInfoBody',
                    message: error.responseText,
                    closable: {
                        timer: 45000,
                        btn: true
                    }
                });
                window.console.log(error);
            }
        };
        $.fileDownload(
            'care-coordination/assessment/patient/' + patientId + '/assessment/' + assessmentResultId + '/download', ajaxDownloadOptions
        )
    }

    function collapseExpandPanels() {
        var $collapseButton = $(".sv-collapse-all-sections-btn");
        var $expandButton = $(".sv-expand-all-sections-btn");

        if (collapsedExpandedPanels.collapse === collapsedExpandedPanels.accordions) {
            $collapseButton.addClass("is-disabled");
            $expandButton.removeClass("is-disabled");
            collapsedExpandedPanels.expand = 0;
            collapsedExpandedPanels.collapse = collapsedExpandedPanels.accordions;
        } else if (collapsedExpandedPanels.expand === collapsedExpandedPanels.accordions) {
            $collapseButton.removeClass("is-disabled");
            $expandButton.addClass("is-disabled");
            collapsedExpandedPanels.collapse = 0;
            collapsedExpandedPanels.expand = collapsedExpandedPanels.accordions;
        } else {
            $collapseButton.removeClass("is-disabled");
            $expandButton.removeClass("is-disabled");
        }
    }

    function setAnchors(survey) {
        survey.panelAnchors = [];
        collapsedExpandedPanels.accordions = 0;

        survey
            .onAfterRenderPanel
            .add(function (survey, options) {
                var divLink = document.createElement("div");

                if (options.panel.panelAnchor) {
                    divLink.id = options.panel.panelAnchor;
                    survey.panelAnchors.push(options.panel.panelAnchor);
                    var parentElement = options.htmlElement.querySelector("h4");
                    if (parentElement != null) {
                        parentElement.parentElement.prepend(divLink);
                        $(parentElement).addClass("assessmentAnchorPanelHead left-padding-10");
                    }
                }

                if (options.panel.assessmentSectionAnchor) {

                    var $panel = $(options.htmlElement);
                    var $panelHeader = $panel.find("h4:eq(0)");

                    collapsedExpandedPanels.accordions += 1;
                    $panelHeader.addClass("accordion-header");

                    var $anchorPanel = $(
                        '<div style="display: none" class="sv-anchor-panel left-padding-10">'
                    );

                    options.panel.css =
                        survey.panelAnchors.forEach(function (element) {
                            var a = document.createElement("a");
                            a.className = "assessment-anchors";
                            a.text = element;
                            a.href = "#" + element;

                            $anchorPanel.append(a);
                        });

                    $panelHeader.after($anchorPanel);

                    function toggleAnchors() {
                        if ($panelHeader.find('.sv_expanded').size() > 0) {
                            $anchorPanel.show();
                            collapsedExpandedPanels.expand += 1;
                            collapsedExpandedPanels.collapse = (collapsedExpandedPanels.collapse === 0) ? 0 : collapsedExpandedPanels.collapse - 1;
                        } else {
                            $anchorPanel.hide();
                            collapsedExpandedPanels.collapse += 1;
                            collapsedExpandedPanels.expand = (collapsedExpandedPanels.expand === 0) ? 0 : collapsedExpandedPanels.expand - 1;
                        }
                    }

                    $panelHeader.on('click', function () {
                        toggleAnchors();
                        collapseExpandPanels();
                    });

                    toggleAnchors();
                    collapseExpandPanels();

                    survey.panelAnchors = [];
                    $panel.find('.has-error').size() > 0 && $panel.find('.accordion-header').css('border-bottom-color', 'red');
                }

                if (options.panel.thirdLevel) {
                    var panelHeader = options.htmlElement.querySelector("h4");
                    $(panelHeader).addClass("third-level left-padding-10");
                }

                var $element = $(options.htmlElement);

                $element.find('input[aria-label]').each(function () {
                    var titles = ['Date started'];

                    var title = $(this).attr('aria-label');

                    if (~titles.indexOf(title)) {
                        $(this).blur();
                    }
                });
            });
        return survey;
    }

    function addTabsToModal(survey, isView) {
        survey
            .onAfterRenderSurvey
            .add(function (survey) {
                var commonAssessmentDiv = document.createElement("div");
                commonAssessmentDiv.className = "assessmentWzrd__tabs";
                var assessmentTabs = document.createElement("ul");
                var assessmentParentElement = $(".assessmentWzrd");
                survey.pages.forEach(function (page) {
                    if (page.visible != false) {
                        var li = document.createElement("li");
                        var a = document.createElement("a");
                        var label = document.createElement("label");
                        li.className = "assessmentWizardLnk";
                        a.className = "ldr-ui-label ldr-head-lnk table-cell-box modal-tab modal-tab-text assessmentDetailsLnk";
                        label.className = "ldr-ui-label lnk-text";
                        label.innerHTML = page.name;
                        a.appendChild(label);
                        li.appendChild(a);
                        assessmentTabs.appendChild(li);
                    }
                });
                //add history tab
                if (isView == true) {
                    $("#assessmentTabHeader").hide();
                    var li = document.createElement("li");
                    var a = document.createElement("a");
                    var label = document.createElement("label");
                    li.className = "assessmentWizardLnk";
                    a.className = "ldr-ui-label ldr-head-lnk table-cell-box modal-tab modal-tab-text assessmentDetailsLnk";
                    label.className = "ldr-ui-label lnk-text";
                    label.innerHTML = "Changes";
                    a.appendChild(label);
                    li.appendChild(a);
                    assessmentTabs.appendChild(li);
                }
                assessmentTabs.className = "nav nav-tabs no-bottom-margin wizard-nav-border";
                commonAssessmentDiv.appendChild(assessmentTabs);
                assessmentParentElement.prepend(commonAssessmentDiv);
                $(assessmentTabs).find("li").eq(0).addClass("active");
            });
        return survey;
    }

    function addTabsToModalHistory(survey) {
        survey
            .onAfterRenderSurvey
            .add(function (survey) {
                var commonAssessmentDiv = document.createElement("div");
                commonAssessmentDiv.className = "assessmentWzrd__tabs";
                var assessmentTabs = document.createElement("ul");
                var assessmentParentElement = $(".assessmentWzrdHistory");
                survey.pages.forEach(function (page) {
                    if (page.visible != false) {
                        var li = document.createElement("li");
                        var a = document.createElement("a");
                        var label = document.createElement("label");
                        li.className = "assessmentWizardLnk";
                        a.className = "ldr-ui-label ldr-head-lnk table-cell-box modal-tab modal-tab-text assessmentDetailsLnk";
                        label.className = "ldr-ui-label lnk-text";
                        label.innerHTML = page.name;
                        a.appendChild(label);
                        li.appendChild(a);
                        assessmentTabs.appendChild(li);
                    }
                });
                assessmentTabs.className = "nav nav-tabs no-bottom-margin wizard-nav-border";
                commonAssessmentDiv.appendChild(assessmentTabs);
                assessmentParentElement.prepend(commonAssessmentDiv);
                $(assessmentTabs).find("li").eq(0).addClass("active");
            });
        return survey;
    }

    function setAssessmentTabActive() {
        $('#care-coordination .patientTabs').each(function () {
            var $tab = $(this);
            var id = $tab.attr('id');

            if (id === 'assessmentsTab') $tab.addClass('active');
            else $tab.removeClass('active');
        });

        $('#care-coordination .patientTabContent').each(function () {
            var $content = $(this);
            var id = $content.attr('id');

            if (id === 'patientAssessmentsContent') $content.addClass('active');
            else $content.removeClass('active');
        });
    }

    function addBackToTopButton($modal) {
        var assessmentWizard = new AssessmentWizard({dom: $modal.find('.assessmentWzrd')});
        assessmentWizard.init();
    }

    function updateAssessmentContainers($container, $modal, actionType) {
        var $modalBodyContainer = $modal.find('.modal-body');
        var assessmentTabsPanel = mdlUtils.find(_getFragment().$html, '', '.assessmentWzrd__tabs');
        var $modalContentContainerSurvey = mdlUtils.find(_getFragment().$html, '', '#assessmentModalContentSurvey');
        var $modalContentContainerResult = mdlUtils.find(_getFragment().$html, '', '#assessmentModalContentResults');
        var $modalContentContainerReview = mdlUtils.find(_getFragment().$html, '', '#assessmentReviewContentContainer');

        $modalBodyContainer.removeClass('no-padding-review-modal-body');
        $container.find('.save-and-validate-button').show();
        $modalContentContainerSurvey.show();
        $modalContentContainerResult.show();
        assessmentTabsPanel.show();
        $modalContentContainerReview.hide();

        $container.find('#assessmentResultHeader').text(actionType + " " + $("#assessmentFullName").val());

        $container.find('.review-footer').hide();
    }

    function addReviewFooter(containerId, $container, resultForm, $modal, ajaxLoader, $wizard,
                             survey, assessmentReviewType, actionType) {

        $container.find('.default-footer').remove();

        var reviewFooter = new AssessmentFooter({
            container: $(containerId),
            totalPages: survey.pageCount > 0 ? survey.pageCount : 1,
            currentPage: survey.currentPageNo > 0 ? survey.currentPageNo : 1,
            className: 'review-footer',
            hasNextBtn: false,
            hasRightArrow: false,

            onBack: function () {
                updateAssessmentContainers($container, $modal, actionType);
                addDefaultAssessmentFooter(containerId, survey, $container, $modal, resultForm,
                    ajaxLoader, assessmentReviewType, actionType);
            },
            rightButtons: [
                {
                    text: 'COMPLETE',
                    cssClass: 'btn btn-primary normalFont',
                    onClick: function (e) {
                        ajaxLoader.show();

                        function sendDataCallback(message) {
                            setAssessmentTabActive();
                            $modal.css('visibility', 'hidden');
                            ajaxLoader.hide(true);

                            createPopUp($modal, $wizard, message, 'CANCEL', 'OK');
                        }

                        function sendDataOnSuccess() {
                            ajaxLoader.hide();
                            sendDataCallback('The assessment has been completed.');
                            $wizard.css('visibility', 'hidden');
                        }

                        function sendDataOnError() {
                            ajaxLoader.hide();
                            sendDataCallback('Cannot save the updates');
                        }

                        setTimeout(function () {
                            if (tabValidation($container, resultForm, $modal, survey)) {
                                sendDataToServer(survey, resultForm, AssessmentStatus.COMPLETED, sendDataOnSuccess,
                                    sendDataOnError);
                            } else {
                                ajaxLoader.hide();
                                updateAssessmentContainers($container, $modal, actionType);
                                if ($container.find('.default-footer').length === 0) {
                                    addDefaultAssessmentFooter(containerId, survey, $container, $modal, resultForm,
                                        ajaxLoader, assessmentReviewType, actionType);
                                }
                            }
                        }, 300);
                    }
                }
            ]
        });

        reviewFooter.render();
    }

    function addDefaultAssessmentFooter(containerId, survey, $container, $modal, resultForm, ajaxLoader, assessmentReviewType, actionType) {

        var $wizard = $modal.find('.assessmentWzrd');

        var footer = new AssessmentFooter({
            container: $(containerId),
            totalPages: survey.pageCount,
            currentPage: survey.currentPageNo,
            className: 'default-footer',

            onBack: function () {
                survey.currentPageNo -= 1;
                collapsedExpandedPanels = {
                    accordions: 0,
                    expand: 0,
                    collapse: 0
                };
            },
            onNext: function () {
                survey.currentPageNo += 1;
                collapsedExpandedPanels = {
                    accordions: 0,
                    expand: 0,
                    collapse: 0
                };
            },
            rightButtons: [
                {
                    text: 'REVIEW',
                    cssClass: 'btn btn-primary normalFont',
                    onClick: function (e) {
                        ajaxLoader.show();
                        setTimeout(function () {
                            loadAssessmentReview($container, $modal, survey, assessmentReviewType, function () {
                                addReviewFooter(containerId, $container, resultForm, $modal, ajaxLoader,
                                    $wizard, survey, assessmentReviewType, actionType);
                                ajaxLoader.hide();
                            })
                        }, 200);
                    }
                },
                {
                    text: 'SAVE AND CLOSE',
                    cssClass: 'btn btn-default normalFont right-margin-24',
                    onClick: function (e) {
                        ajaxLoader.show();

                        function sendDataCallback(message) {
                            setAssessmentTabActive();
                            ajaxLoader.hide();
                            $modal.css('visibility', 'hidden');
                            $wizard.css('visibility', 'hidden');

                            createPopUp($modal, $wizard, message, 'BACK TO ASSESSMENT', 'CLOSE');
                        }

                        function sendDataOnSuccess() {
                            sendDataCallback('The updates have been saved')
                        }

                        function sendDataOnError() {
                            sendDataCallback('Cannot save the updates')
                        }

                        sendDataToServer(survey, resultForm, AssessmentStatus.IN_PROCESS, sendDataOnSuccess,
                            sendDataOnError);
                    }
                },
                {
                    html: [
                        {
                            '<>': 'div',
                            'class': 'checkbox',
                            'html': [
                                {
                                    '<>': 'label',
                                    'html': [
                                        {
                                            '<>': 'input',
                                            'name': 'markAsInactive',
                                            'type': 'checkbox'
                                        },
                                        {
                                            '<>': 'span',
                                            'text': 'Mark as Inactive'
                                        }
                                    ]
                                }
                            ]
                        }
                    ],
                    onClick: function (e) {
                        var target = e.event.target;

                        if (target.tagName.toLowerCase() === 'input') {
                            ajaxLoader.show();

                            sendDataToServer(survey, resultForm, AssessmentStatus.INACTIVE, function () {
                                $modal.modal('hide');

                                ajaxLoader.hide();

                                bootbox.alert('The assessment has been marked as inactive', setAssessmentTabActive);
                            }, function () {
                                $modal.modal('hide');

                                ajaxLoader.hide();

                                bootbox.alert('Cannot mark this assessment as inactive');
                            });
                        }
                    }
                }
            ]
        });
        survey.onCurrentPageChanged.add(function () {
            footer.update({
                currentPage: survey.currentPageNo
            });
        });

        footer.render();
    }

    function createPopUp($modal, $wizard, message, leftButtonText, rightButtonText) {
        bootbox.confirm({
            message: message,
            buttons: {
                cancel: {
                    className: 'assessment-bootbox-btn btn-default normalFont right-margin-24',
                    label: leftButtonText
                },
                confirm: {
                    className: 'assessment-bootbox-btn btn-primary normalFont',
                    label: rightButtonText
                }
            },
            callback: function (result) {
                if (result) {
                    $modal.modal('hide');
                } else {
                    $modal.css('visibility', 'visible');
                    $wizard.css('visibility', 'visible');
                }
                setAssessmentTabActive();
            }
        });
    }

    function createDefaultPopUp($modal, $wizard, $modalFooter, message, leftButtonText, rightButtonText) {
        $modal.css('visibility', 'hidden');
        $(".default-modal-footer").css('visibility', 'hidden');
        $wizard.css('visibility', 'hidden');

        bootbox.confirm({
            message: message,
            buttons: {
                cancel: {
                    className: 'assessment-bootbox-btn btn-default normalFont right-margin-24',
                    label: leftButtonText
                },
                confirm: {
                    className: 'assessment-bootbox-btn btn-primary normalFont',
                    label: rightButtonText
                }
            },
            callback: function (result) {
                if (result) {
                    $modal.modal('hide');
                    clearAssessmentState()
                } else {
                    $modal.css('visibility', 'visible');
                    $modalFooter.css('visibility', 'visible');
                    $wizard.css('visibility', 'visible');

                }
                setAssessmentTabActive();
            }
        });
    }

    function tabValidation($container, resultForm, modal, survey) {
        var firstInvalidPageIndex = -1;
        $find('.error-tab-arrow').hide();
        survey.checkErrorsMode = "onValueChanged";
        survey.visiblePages.forEach(function (page) {
            if (page.hasErrors()) {
                if (firstInvalidPageIndex === -1) {
                    firstInvalidPageIndex = page.visibleIndex;
                }
                $container.find(".assessmentDetailsLnk").eq(page.visibleIndex).prepend('<span class="error-tab-arrow"/>');
            }
        });
        if (firstInvalidPageIndex !== -1) {
            if (firstInvalidPageIndex !== survey.currentPageNo) {
                survey.currentPageNo = firstInvalidPageIndex;
            }

            // perform validation

            //bug fix for focusFirstErrorQuestion - it doesn't work if scroll is in start of page
            $container.find('.wizard-content').scrollTo('max', 0);

            setTimeout(function () {
                survey.currentPage.focusFirstErrorQuestion();
            });
        }

        return firstInvalidPageIndex === -1
    }

    function surveyTabsProcess($container, survey, isView, isLSAOrg, isArchived) {
        var $tabs = $container.find("#assessmentTabHeader .assessmentWizardLnk");

        if ($container.find('.assessmentWzrd__tabs').size()) {
            $tabs = $container.find(".assessmentWzrd__tabs .assessmentWizardLnk");
        }

        var $footer = $container.find('.default-modal-footer');

        $footer.find('.wzBtn').hide();

        function clickTab($tab) {
            collapsedExpandedPanels = {
                accordions: 0,
                expand: 0,
                collapse: 0
            };
            $tab.find('a').trigger('click');
        }

        $footer.find('.backBtn').on('click', function () {
            clickTab($tabs.filter('.active').prev());
        });

        $footer.find(
            '.assessment-footer__left-arrow,' +
            '.assessment-footer__back-btn'
        ).on('click', function () {
            clickTab($tabs.filter('.active').prev());
        });

        $footer.find('.nextBtn').on('click', function () {
            clickTab($tabs.filter('.active').next());
        });

        $footer.find(
            '.assessment-footer__next-btn,' +
            '.assessment-footer__right-arrow'
        ).on('click', function () {
            clickTab($tabs.filter('.active').next());
        });

        function updateModalFooterForActiveTab(title) {
            $footer.find('.wzBtn').hide();

            $footer.find(
                '.assessment-footer__left-arrow,' +
                '.assessment-footer__back-btn,' +
                '.assessment-footer__next-btn,' +
                '.assessment-footer__right-arrow'
            ).hide();

            if (title === 'Demographics' || title === 'Assessment Details') {
                $footer.find('.nextBtn').show();

                $footer.find(
                    '.assessment-footer__next-btn,' +
                    '.assessment-footer__right-arrow'
                ).show();
            }

            else if ((isArchived ? title === 'Engagement' : title === 'Changes') || title === 'Assessment History') {
                $footer.find('.backBtn').show();

                $footer.find(
                    '.assessment-footer__left-arrow,' +
                    '.assessment-footer__back-btn'
                ).show();
            }

            else $footer.find(
                    '.assessment-footer__left-arrow,' +
                    '.assessment-footer__back-btn,' +
                    '.assessment-footer__next-btn,' +
                    '.assessment-footer__right-arrow'
                ).show();
        }

        var tabTitle = $tabs.filter('.active').find('.lnk-text').text().trim()

        isView && updateModalFooterForActiveTab(tabTitle);

        $tabs.on("click", 'a', function () {
            var text = $(this).find('.lnk-text').text().trim()

            collapsedExpandedPanels = {
                accordions: 0,
                expand: 0,
                collapse: 0
            };

            isView && updateModalFooterForActiveTab(text);

            if (text === 'Assessment History' || text === 'Changes') {
                $('.nav-tabs a[href="#historyAssessmentTab"]').tab('show');
            }

            else {
                if (isView) {
                    $('.nav-tabs a[href="#detailsAssessmentTab"]').tab('show');
                }

                survey.currentPageNo = survey.pagesValue.findIndex(function (page) {
                    return page.name === text
                })
            }

            $(this).parent().addClass("active");
            $(this).parent().siblings().removeClass("active");

            // perform validation
            if (survey.isCurrentPageHasErrors) {
                survey.pages[survey.currentPageNo].focusFirstErrorQuestion();
            }

            collapsedExpandedPanels.expand = 0;
            collapsedExpandedPanels.collapse = 0;

            $.each(survey.currentPage.getPanels(), function (index, panel) {
                if (panel.assessmentSectionAnchor && panel.isVisible) {
                    if (panel.isCollapsed) {
                        collapsedExpandedPanels.collapse += 1;
                    } else if (panel.isExpanded) {
                        collapsedExpandedPanels.expand += 1;
                    }
                }
            });
            collapseExpandPanels();
        });
    }

    function getCalendarType(calendarFlag) {
        var format,
            maxDate;
        switch (calendarFlag) {
            //no future dates
            case "customdatepicker":
                format = "MM/DD/YYYY hh:mm A Z";
                maxDate = new Date();
                break;
            case "withoutTime":
                format = "MM/DD/YYYY";
                maxDate = new Date();
                break;
            case "noLimit":
                format = "MM/DD/YYYY hh:mm A Z";
                maxDate = false;
                break;
            default:
                format = "MM/DD/YYYY hh:mm A Z";
                maxDate = new Date();
        }
        return {
            "format": format,
            "maxDate": maxDate
        };
    }

    function afterSurveyRendersQuestion(survey) {
        survey
            .onAfterRenderQuestion
            .add(function (survey, options) {
                $(".questionSurvey").addClass("left-padding-10");
                var question = options.question;
                var questionElement = $(options.htmlElement);

                if (question.htmlClass === 'priority-align') {
                    var priorityCheckBox = questionElement;
                    var priorityQuestion = priorityCheckBox.prev();

                    // bugfix CCN-4118
                    // After setting survey data priority questions are not checked
                    if (question.value) {
                        $(options.htmlElement).find('input').prop('checked', true)
                    }

                    if (priorityQuestion) {
                        priorityCheckBox.addClass('sv_qstn-priority-align');
                        priorityQuestion.addClass('sv_qstn-priority-question');
                    }
                }

                if (!question.calendarFlag) {

                    //todo restyle checkbox & radio buttons
                    if (questionElement.find('.iradio_square-blue').find('input').toArray().length > 0)
                        $.each(questionElement.find('.iradio_square-blue').find('input').toArray(), function () {
                            var $elementParent = $(this).parent();
                            var $elem = $(this);

                            if ($elem.attr('type') === "checkbox") {
                                $elementParent.addClass('jq-checkbox');
                                $elementParent.append('<div class="jq-checkbox__div"/>');

                            } else if ($elem.attr('type') === "radio") {
                                $elementParent.addClass('jq-radio');
                                $elementParent.append('<div class="jq-radio__div"/>');
                            }
                        });

                    if (!question.customdatepicker) {
                        return;
                    }
                } else {

                    var qid = question.inputId;

                    var $field = $('#' + qid);

                    var format = getCalendarType(question.calendarFlag).format;
                    var maxDate = getCalendarType(question.calendarFlag).maxDate;

                    $field.datetimepicker({
                        format: format,
                        maxDate: maxDate,
                        useCurrent: false,
                        defaultDate: false,
                        widgetPositioning: {
                            vertical: 'bottom'
                        }
                    });

                    $field.on("dp.change", function (e) {
                        options.question.value = $field.val();
                    });

                    var $calendarParent = $(options.htmlElement);
                    $calendarParent.css('position', 'relative');
                    $calendarParent.prepend($('<span class="glyphicon glyphicon-calendar assessment-calendar-button"></span>'));
                }
            });
        return survey;
    }

    function afterSurveyRendersPage(survey, $container) {
        survey.onAfterRenderPage
            .add(function (survey, options) {
                var page = options.page;
                var $html = $(options.htmlElement);

                var $collapseButton = $(".sv-collapse-all-sections-btn");
                var $expandButton = $(".sv-expand-all-sections-btn");

                $expandButton.on("click", function () {
                    $.each(page.rows, function (i, row) {
                        $.each(row.elements, function (j, elem) {
                            if (elem.isPanel) {
                                elem.expand();
                                collapsedExpandedPanels.expand += 1;
                                collapsedExpandedPanels.collapse = 0;
                                $(options.htmlElement).find('.accordion-header').not(":eq(0)").css('margin-top', '-1');
                                $(options.htmlElement).find('.accordion-header').not(":eq(0)").css('border-top', '1px solid #d8d8d8');
                            }
                        });
                    });

                    $html.find('.sv-anchor-panel').show();
                    $(this).addClass('is-disabled');
                    $collapseButton.removeClass("is-disabled");
                });

                $collapseButton.on("click", function () {
                    $.each(page.rows, function (i, row) {
                        if (i > 0) $.each(row.elements, function (j, elem) {
                            if (elem.isPanel) {
                                elem.collapse();
                                collapsedExpandedPanels.collapse += 1;
                                collapsedExpandedPanels.expand = 0;
                                $(options.htmlElement).find('.accordion-header').not(":eq(0)").css('margin-top', '0');
                                $(options.htmlElement).find('.accordion-header').not(":eq(0)").css('border-top', 'none');
                            }
                        });
                    });

                    $html.find('.sv-anchor-panel').hide();
                    $(this).addClass('is-disabled');
                    $expandButton.removeClass("is-disabled");
                });
                var $tab = $container.find('.assessmentWzrd__tabs .assessmentWizardLnk').eq(page.visibleIndex);
                $tab.addClass('active');
                $tab.siblings().removeClass('active');
            });
        return survey;
    }

    function loadAssessment($container, $modal, actionType, assessmentResultId) {

        var $form = $container.find("#assessmentResultForm");
        var $wizard = $('.assessmentWzrd');
        var $modalContent = $modal.find('.modal-content');
        var $modalFooter = $modal.find('.modal-footer');

        var ajaxLoader = mdlUtils.initAjaxLoader($modalContent, {
            loaderWillBeShown: function () {
                $wizard.css('visibility', 'hidden');
                $modalFooter.css('visibility', 'hidden');
            },
            loaderWillBeHidden: function () {
                $wizard.css('visibility', 'visible');
                $modalFooter.css('visibility', 'visible');
            }
        });
        $.ajax({
            url: 'care-coordination/assessment/assessment-details',
            data: $form.serialize(),
            type: 'POST',
            beforeSend: function (xhr) {
                mdlUtils.csrf(xhr);
                ajaxLoader.show();
            },
            complete: function () {
                ajaxLoader.hide();
            },
            success: function (data) {
                var isScoringEnabled = data.scoringEnabled;
                //todo
                var LSAType = data.type;
                var $resultForm = $container.find("#assessmentResultForm");
                $("#assessmentResultHeader").text(actionType + ' ' + data.name);
                var surveyResult = $("#resultJson").val();
                var myCss = {
                    pageTitle: "sectionHeadSurvey",
                    question: {
                        mainRoot: "questionSurvey",
                        title: "assessment-label"
                    },
                    panel: {
                        title: "assessmentPanelHead"
                    },
                    row: "assessment-row"
                };
                Survey.JsonObject.metaData.addProperty("questionbase", "customdatepicker:boolean");
                Survey.JsonObject.metaData.addProperty("questionbase", "priorityFlag:boolean");
                Survey.JsonObject.metaData.addProperty("questionbase", "calendarFlag:text");
                Survey.JsonObject.metaData.addProperty("questionbase", "htmlClass:text");
                Survey.JsonObject.metaData.addProperty("panel", "panelAnchor:text");
                Survey.JsonObject.metaData.addProperty("panel", "assessmentSectionAnchor:text");
                Survey.JsonObject.metaData.addProperty("panel", "thirdLevel:text");
                Survey.JsonObject.metaData.addProperty("page", "tabKey:number");

                var template;
                if (data.hasNumeration) {
                    template = '"questionTitleTemplate": "{title}{require}",'
                } else {
                    template = '"questionTitleTemplate": "{no}. {title}{require}",';
                }

                data.jsonContent = data.jsonContent.slice(0, 1) + template + data.jsonContent.slice(1);
                Survey.Survey.cssType = "bootstrap";
                var surveyJSON = JSON.parse(data.jsonContent);
                var survey = new Survey.Model(surveyJSON);

                survey.showCompletedPage = false;
                survey.showNavigationButtons = false;
                survey.focusFirstQuestionAutomatic = false;

                if (surveyResult != '') {
                    survey.data = JSON.parse(surveyResult);
                }

                survey.onValueChanged.add(function () {
                    setAssessmentState({isChanged: true})
                })

                if (LSAType) {
                    survey = addTabsToModal(survey, false);
                    survey = setAnchors(survey);
                    $(".assessmentGeneral").hide();
                    $(".assessmentComment").hide();
                    $(".modal-body").removeClass("new-modal-body");
                    $(".assessmentWzrd").removeClass("default-assessment-wizard");
                    if (actionType === AssessmentActionType.ADD) {
                        survey.data = {
                            'Completed by': assessmentState.employeeName,
                            'Date started': moment(survey.getQuestionByName("Date started").value).format('MM/DD/YYYY hh:mm:ss a Z')
                        };
                    }
                    $find(".save-and-validate-button").show();
                }

                $find(".save-and-validate-button").on('click', function (e) {
                    ajaxLoader.show();
                    setTimeout(function () {
                        sendDataToServer(survey, $resultForm, AssessmentStatus.IN_PROCESS,
                            function () {

                                tabValidation($container, $resultForm, $modal, survey);
                                ajaxLoader.hide();
                            },
                            function () {
                                tabValidation($container, $resultForm, $modal, survey);
                                ajaxLoader.hide();
                            }
                        )
                    }, 300);
                    return false;
                });

                survey = afterSurveyRendersQuestion(survey);
                survey = afterSurveyRendersPage(survey, $container);

                survey
                    .onComplete
                    .add(function (result) {
                        ajaxLoader.show();

                        sendDataToServer(survey, $resultForm, AssessmentStatus.COMPLETED,
                            ajaxLoader.hide, ajaxLoader.hide);
                        $modal.modal('hide');
                    });

                $("#assessmentContentContainer").Survey({
                    model: survey,
                    css: myCss
                });

                surveyTabsProcess($container, survey, false);

                if (!LSAType) {
                    _addAssessmentResultFormValidation();
                    if (isScoringEnabled) {
                        _updateBtns(2);
                        var $nextToScoringButton = $container.find("#nextBtnScoring");
                        $nextToScoringButton.on('click', function () {
                            loadAssessmentScoring($container, $modal, survey, ajaxLoader, surveyJSON)
                        });
                        var $modalContentContainerSurvey = mdlUtils.find(_getFragment().$html, '', '#assessmentModalContentSurvey');
                        var $modalContentContainerScoring = mdlUtils.find(_getFragment().$html, '', '#assessmentModalContentResults');
                        var $backButton = $container.find("#backBtn");
                        var $finishButton = $container.find("#saveAssessmentResultScoring");
                        $backButton.on('click', function () {
                            _updateBtns(2);
                            $modalContentContainerScoring.hide();
                            $modalContentContainerSurvey.show();
                            $("#assessmentResultHeader").text(actionType + " " + $("#assessmentFullName").val());
                            return false;
                        });
                        $finishButton.on('click', function () {
                            var result = survey.completeLastPage();
                            if (result) {
                                $modal.modal('hide');
                                createDefaultPopUp($modal, $wizard, $modalFooter, "The assessment results have been submitted", "CANCEL", "OK")
                            }
                            return false;
                        });
                    } else {
                        _updateBtns(1);
                        var $saveButton = $container.find("#saveAssessmentResult");
                        $saveButton.on('click', function () {
                            var $resultForm = $container.find("#assessmentResultForm");
                            if (!$resultForm.valid()) {
                                return false;
                            }
                            var result = survey.completeLastPage();
                            if (result) {
                                $modal.modal('hide');
                                createDefaultPopUp($modal, $wizard, $modalFooter, "The assessment results have been saved", "CANCEL", "OK");
                            }
                            return false;
                        });
                    }
                } else {
                    if (actionType === AssessmentActionType.EDIT && tabValidation($container, $resultForm, $modal, survey)) {
                        var currentTabKey = AssessmentsLocalStorageService.loadTabKey(assessmentResultId);
                        if (currentTabKey !== null && currentTabKey !== undefined) {
                            var pageToSwitch = survey.visiblePages.find(function (page) {
                                return page.tabKey === currentTabKey
                            });
                            if (pageToSwitch !== undefined) {
                                survey.currentPage = pageToSwitch;
                            }
                        }
                    } else {
                        $modal.on('shown.bs.modal', function () {
                            survey.currentPage.focusFirstErrorQuestion();
                        })
                    }
                    $modal.on('hide.bs.modal', function () {
                        AssessmentsLocalStorageService.saveTabKey($form.find('#assessmentResultId').val(), survey.currentPage.tabKey)
                    });
                    addDefaultAssessmentFooter('.assessmentWzrd', survey, $container, $modal, $resultForm,
                        ajaxLoader, AssessmentReviewType.VIEW, actionType);
                    $(".default-modal-footer").remove();
                    addBackToTopButton($modal);
                }

                var $closeIconButton = mdlUtils.find(_getFragment().$html, '', '#closeIconButton');

                var $modalFooter = $modal.find('.modal-footer');

                $closeIconButton.on('click', function () {
                    if (assessmentState.isChanged) {
                        createDefaultPopUp($modal, $wizard, $modalFooter, 'The updates will not be saved.', 'CANCEL', 'OK');
                    }

                    else {
                        $modal.modal('hide');
                        clearAssessmentState()
                    }

                    return false;
                });

                $container.find("#cancelBtn").on('click', function () {
                    if (assessmentState.isChanged) {
                        createDefaultPopUp($modal, $wizard, $modalFooter, 'The updates will not be saved.', 'CANCEL', 'OK');
                    }

                    else {
                        $modal.modal('hide');
                        clearAssessmentState()
                    }

                    return false
                });
            },
            error: function (error) {
                mdlUtils.onAjaxError(error, function () {
                    alert(error.responseText);
                });
            }
        });
    }

    function setReviewAnchors(survey, initialSurvey) {
        survey.pageAnchors = [];
        survey.accordionsAnchors = [];

        survey
            .onAfterRenderPage
            .add(function (survey, options) {
                var $page = $(options.htmlElement);
                var $anchorPanel = $('<div class="sv-anchor-panel left-padding-10">');

                var pageAnchors = $.uniqueSort(survey.pageAnchors);

                options.page.css =
                    $.each(pageAnchors, function (index, element) {
                        var a = document.createElement("a");
                        a.className = "assessment-review-page-anchors";
                        a.text = element;
                        a.href = "#" + element;
                        $anchorPanel.append(a);
                    });

                $page.prepend($anchorPanel);
            });

        survey
            .onAfterRenderPanel
            .add(function (survey, options) {
                var accordionDivLink = document.createElement("div");
                var panel = options.panel;
                var panelName = panel.name;

                var pageDiv = document.createElement("div");
                var pageAnchorDiv = document.createElement("div");

                if (panel.panelAnchor) {
                    var parentElement = options.htmlElement.querySelector("h4");
                    $(parentElement).addClass("assessmentAnchorPanelHead font-size-16");
                }

                if (panel.assessmentSectionAnchor) {

                    var panelParentPage = initialSurvey.getPanelByName(panelName).page;
                    var $panel = $(options.htmlElement);
                    var $panelHeader = $panel.find("h4:eq(0)");

                    //expand all accordions
                    panel.expand();

                    //restyle accordions
                    $panelHeader.addClass('no-pointer-events');
                    $panelHeader.find('.sv_panel_icon').hide();

                    $panelHeader.addClass("assessmentAnchorPanelHead");
                    $panelHeader.parent().addClass("review-panel");
                    $panelHeader.parent().find('.assessment-row:eq(0)').parent().addClass('review-inner-accordion-panel');

                    survey.pageAnchors.push(panelParentPage.name);
                    survey.accordionsAnchors.push(panelName);

                    $(pageAnchorDiv).attr('id', panelParentPage.name);
                    $(pageAnchorDiv).text(panelParentPage.name);

                    //add accordion anchor links to tab div panel
                    accordionDivLink.id = panel.assessmentSectionAnchor;
                    $panel.prepend(accordionDivLink);

                    var tabNames = $.find('.review-tab-div').map(function (elem) {
                        return $(elem).children().filter(':eq(0)').text();
                    });

                    if (tabNames.length === 0 || tabNames.indexOf(panelParentPage.name) === -1) {
                        //add tab divs
                        $(pageDiv).addClass('review-tab-div assessment__preview__page-anchor-panel');
                        $(pageDiv).prepend(pageAnchorDiv);

                        //add tab's div element to review page
                        $panel.before(pageDiv);
                    }
                }

                if (panel.parent.name === 'all' && survey.pageAnchors.indexOf(panelName) !== -1) {
                    var $anchorPanel = $('<div class="sv-anchor-tab-panel">');

                    panel.css =
                        survey.accordionsAnchors.forEach(function (element) {
                            var a = document.createElement("a");
                            a.className = "assessment-anchors";
                            a.text = element;
                            a.href = "#" + element;

                            $anchorPanel.append(a);
                        });

                    if ($('.review-tab-div').length > 0)
                        $('.review-tab-div').filter(function (index, elem) {
                            return $(elem).text() === panelName;
                        }).append($anchorPanel);

                    survey.accordionsAnchors = [];
                }

                if (panel.thirdLevel) {
                    var panelHeader = options.htmlElement.querySelector("h4");
                    $(panelHeader).addClass("third-level");
                }

                var $dom = $(options.htmlElement);

                $dom.find('input[aria-label]').each(function () {
                    var titles = ['Date started'];

                    var title = $(this).attr('aria-label');

                    if (~titles.indexOf(title)) {
                        $(this).blur();
                    }
                });
            });
        return survey;
    }

    function prioritySurveyQuestions(survey, assessmentReviewType) {
        var surveyPriorityQuestions = new Survey.Survey(survey.toJSON());

        surveyPriorityQuestions.data = survey.data;
        surveyPriorityQuestions.isSinglePage = true;
        surveyPriorityQuestions.showProgressBar = 'none';
        //settings for no one visible questions text
        Survey.surveyStrings.emptySurvey = "No questions for review";

        var questions = surveyPriorityQuestions.getAllQuestions();

        $.each(questions, function (index, question) {
            question.visible = false;
            if (question.priorityFlag && question.value) {
                if (assessmentReviewType === AssessmentReviewType.EDIT) {
                    // question is priority check box
                    question.visible = true;
                }

                var priorityQuestion = questions[index - 1];
                priorityQuestion.visible = true;
            }
        });

        if (assessmentReviewType === AssessmentReviewType.EDIT) {
            surveyPriorityQuestions = afterSurveyRendersQuestion(surveyPriorityQuestions);
        } else {
            surveyPriorityQuestions.mode = 'display';
        }

        surveyPriorityQuestions = setReviewAnchors(surveyPriorityQuestions, survey);
        return surveyPriorityQuestions;
    }

    function loadAssessmentReview($container, $modal, survey, assessmentReviewType, onSuccess) {
        var $resultForm = $container.find("#assessmentResultForm");

        var $modalBodyContainer = $container.find('.modal-body');
        var $modalContentContainerSurvey = mdlUtils.find(_getFragment().$html, '', '#assessmentModalContentSurvey');
        var $modalContentContainerResult = mdlUtils.find(_getFragment().$html, '', '#assessmentModalContentResults');
        var $modalContentContainerReview = mdlUtils.find(_getFragment().$html, '', '#assessmentReviewContentContainer');
        var assessmnetTabsPanel = mdlUtils.find(_getFragment().$html, '', '.assessmentWzrd__tabs');

        $container.find('#assessmentResultHeader').text('Priority Questions Preview');
        $container.find('.save-and-validate-button').hide();
        $modalBodyContainer.addClass('no-padding-review-modal-body');

        $modalContentContainerReview.css('min-height', '300px');

        $modalContentContainerResult.hide();
        $modalContentContainerSurvey.hide();
        assessmnetTabsPanel.hide();
        $modalContentContainerReview.empty();

        setTimeout(function () {
            var priorityQuestions = prioritySurveyQuestions(survey, assessmentReviewType);
            $("#resultJson").val(JSON.stringify(priorityQuestions));

            return $.ajax({
                url: 'care-coordination/assessment/review',
                data: new FormData($resultForm[0]),
                type: 'POST',
                enctype: 'multipart/form-data',
                processData: false,
                contentType: false,
                beforeSend: function (xhr) {
                    mdlUtils.csrf(xhr);
                }
            }).success(function (data) {
                $modalContentContainerReview.append(data);
                $modalContentContainerReview.css('min-height', '');

                priorityQuestions.render('#reviewContentContainer');
                $("#reviewContentContainer").Survey({
                    model: priorityQuestions
                });

                $modalContentContainerReview.show();

                onSuccess && onSuccess(data);
            }).fail(function (response) {
                mdlUtils.onAjaxError(response, function () {
                    $("#formError").text(response.responseText);
                });
            });
        }, 50);
    }

    function loadAssessmentScoring($container, $modal, survey, ajaxLoader, surveyJSON) {
        var $resultForm = $container.find("#assessmentResultForm");
        if (survey.isCurrentPageHasErrors) {
            setTimeout(function () {
                $container.find('.wizard-content').scrollTo('max', 0);
                survey.currentPage.focusFirstErrorQuestion();
            }, 100);

            return false;
        }
        if (!$resultForm.valid()) {
            return false;
        }

        //make a copy of survey and "complete" to ewmove hidden fields from scoring
        //clearInvisibleValues=onComplete to support restoring values of hidden questions in case of making them visible
        var surveyCopy = new Survey.Model(surveyJSON);
        surveyCopy.data = survey.data;
        surveyCopy.completeLastPage();

        $("#resultJson").val(JSON.stringify(surveyCopy.data));
        $.ajax({
            url: 'care-coordination/assessment/scoring',
            data: new FormData($resultForm[0]),
            type: 'POST',
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            beforeSend: function (xhr) {
                mdlUtils.csrf(xhr);
                ajaxLoader && ajaxLoader.show();
            }
        }).success(function (data) {
            ajaxLoader && ajaxLoader.hide();
            _updateBtns(3);
            var $modalContentContainerSurvey = mdlUtils.find(_getFragment().$html, '', '#assessmentModalContentSurvey');
            var $modalContentContainerScoring = mdlUtils.find(_getFragment().$html, '', '#assessmentModalContentResults');
            $modalContentContainerSurvey.hide();
            $modalContentContainerScoring.empty();
            $modalContentContainerScoring.append(data);
            $modalContentContainerScoring.show();
            $("#assessmentResultHeader").text($("#assessmentShortName").val() + " Scoring");
            $("#score").val($("#assessmentScore").val());
            _initAssessmentsGroupList(_getFragment().widgets.assessmentsScoringGroupList, "assessmentsScoringGroupList");

        }).fail(function (response) {
            ajaxLoader && ajaxLoader.hide();
            mdlUtils.onAjaxError(response, function () {
                $("#formError").text(response.responseText);
            });
        });
    }

    function _addAssessmentResultFormValidation() {
        return $("#assessmentResultForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    dateCompleted: {required: true},
                    comment: {maxlength: 5000}
                },
                messages: {
                    dateCompleted: {
                        required: getErrorMessage("field.empty")
                    }
                }
            })
        );
    }

    function sendDataToServer(survey, $form, status, onSuccess, onError) {
        $form.find("#resultJson").val(JSON.stringify(survey.data));

        var question = survey.getQuestionByName("Date started")

        if (question) {
            $form.find("#dateAssigned").val(
                moment(question.value).format('MM/DD/YYYY hh:mm:ss a Z')
            );
        }

        var $assessmentId = $form.find("#assessmentResultId");

        $.ajax({
            url: 'care-coordination/assessment/assessment/' + status,
            data: new FormData($form[0]),
            type: 'POST',
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            beforeSend: function (xhr) {
                mdlUtils.csrf(xhr);
            }
        }).success(function (data) {
            _getFragment().widgets.assessmentsList.api().ajax.reload();
            if (data) {
                if ($assessmentId.val() === '') {
                    _updateAssessmentsBadgeValue(+1);
                } else {
                    AssessmentsLocalStorageService.changeId($assessmentId.val(), data);
                }
            }
            $assessmentId.val(data);

            clearAssessmentState()

            onSuccess && onSuccess()
        }).fail(function (response) {
            mdlUtils.onAjaxError(response, function () {
                $("#formError").text(response.responseText);
            });
            onError && onError()
        }).complete(function () {
            _getFragment().widgets.patientNoteList.api().ajax.reload();
            _getFragment().widgets.patientEventList.api().ajax.reload();
            var patientId = $('#currentPatientId').val();
            _initNotesTotal(patientId);
        });
    }

    function _updateBtns(index) {
        var stepCss = ['.selectAssessmentTypeStep', '.assessmentResultStep', '.assessmentResultStepScoring', '.assessmentScoringStep'];
        _clearAlerts();
        $find('.wzBtns .btn').addClass('hidden');
        $find(stepCss[index]).removeClass('hidden');
    }

    function _initAssessmentsTotal(residentId) {
        $.ajax({
            type: 'GET',
            contentType: 'json',
            url: 'care-coordination/assessment/patient/' + residentId + '/total',
            success: function (totalCount) {
                var isEmpty = (totalCount <= 0);
                _setAssessmentsBadgeValue(isEmpty, totalCount);
            },
            error: function () {
                $('#notesCountTabPanel').addClass('hidden');
            }
        });
    }

    function _initServicePlansTotal(patientId) {
        $.ajax({
            type: 'GET',
            contentType: 'application/json',
            url: 'care-coordination/patients/patient/' + patientId + '/service-plans/total',
            success: function (totalCount) {
                var isEmpty = (totalCount <= 0);
                _setServicePlansBadgeValue(isEmpty, totalCount);
            }
        });
    }

    function _setAssessmentsBadgeValue(isEmpty, totalCount) {
        var $badge = $('#assessmentsCountTabPanel');
        $badge.html(isEmpty ? 0 : totalCount);
        $('#notesCountTabPanel').removeClass('hidden');
    }

    function _updateAssessmentsBadgeValue(addend) {
        var $badge = $('#assessmentsCountTabPanel');
        var newValue = parseInt($badge.html()) + addend;
        if (newValue > 0) {
            $badge.html(newValue);
        } else {
            $badge.html(0);
            $badge.addClass('hidden');
        }
    }

    function _setServicePlansBadgeValue(isEmpty, totalCount) {
        var $badge = $('#servicePlanCountTabPanel');
        $badge.html(isEmpty ? 0 : totalCount);
    }

    function _initAssessmentsList() {
        _getFragment().widgets.assessmentsList = wgtUtils.grid(_getFragment().$html, '', {
            tableId: "assessmentsList",
            totalDisplayRows: 25,
            searchFormId: "assessmentsFilterForm",
            order: [[2, 'desc']],
            callbacks: {
                rowCallback: function (row, data, index) {
                    var actionsColumn = 'td:eq(5)';
                    var $row = $(row);
                    $row.children(actionsColumn).empty();
                    var resultId = data.id;

                    if (data.editable == true || data.canBeDownloaded == true) {
                        var $tdRowActions = mdlUtils.find(_getFragment().$html, '', '#assessmentRowActions').clone();
                        var tdRowActionsId = 'assessmentRowActions-' + index;
                        $tdRowActions.attr('id', tdRowActionsId);
                        $tdRowActions.removeClass('hidden');
                        $row.children(actionsColumn).append($tdRowActions);

                        if (data.editable == true) {
                            var $editButton = $tdRowActions.find('.editResidentAssessmentResult');
                            $editButton.removeClass('invisible');
                            $editButton.on('click', function () {
                                    _initEditAssessmentResultDialog(mdlUtils.find(_getFragment().$html, '', '#assessmentContainer'), $('#currentPatientId').val(), resultId, data.assessmentId, $(this));
                                    return false;
                                }
                            );
                        }

                        if (data.canBeDownloaded == true) {
                            var $downloadButton = $tdRowActions.find('.downloadResidentAssessmentResult');
                            $downloadButton.removeClass('invisible');
                            $downloadButton.on('click', function () {
                                    _downloadAssessmentResult($('#currentPatientId').val(), resultId, data.assessmentId, $(this));
                                    return false;
                                }
                            );
                        }
                    }

                    $row.attr('style', 'cursor:pointer');
                    $row.on('click', function (e) {
                        _initViewAssessmentResultDialog(mdlUtils.find(_getFragment().$html, '', '#assessmentViewContainer'), resultId);
                    });
                },
                errorCallback: function (error) {
                    alert(error.responseText);
                },
                footerCallback: function (tfoot, data, start, end, display) {
                    $(tfoot).hide();
                }
            }
        });
        $("#searchAssessments").on('click', function () {
            _getFragment().widgets.assessmentsList.api().ajax.reload();
            return false;
        });
        _getFragment().widgets.assessmentsList.api().ajax.reload();
    }

    function _initAssessmentHistory() {
        _getFragment().widgets.assessmentsHistoryViewList = wgtUtils.grid(_getFragment().$html, '', {
            tableId: "assessmentsHistoryViewList",
            totalDisplayRows: 25,
            sort: false,
            callbacks: {
                rowCallback: function (row, data, index) {
                    var actionsColumn = 'td:eq(3)';
                    var $row = $(row);
                    $row.children(actionsColumn).empty();
                    var resultId = data.id;
                    if (index !== 0) {
                        var $tdRowActions = mdlUtils.find(_getFragment().$html, '', '#assessmentHistoryRowActions').clone();
                        var tdRowActionsId = 'assessmentHistoryRowActions-' + index;
                        $tdRowActions.attr('id', tdRowActionsId);
                        $tdRowActions.removeClass('hidden');
                        $row.children(actionsColumn).append($tdRowActions);
                        $tdRowActions.find('.seeHistoryLink').on('click', function () {
                                _initHistoryViewDialog(mdlUtils.find(_getFragment().$html, '', '#assessmentHistoryViewContainer'), resultId);
                                return false;
                            }
                        );
                    }
                    $row.attr('style', 'cursor:pointer');
                },
                errorCallback: function (error) {
                    alert(error.responseText);
                },
                footerCallback: function (tfoot, data, start, end, display) {
                    $(tfoot).hide();
                }
            }
        });
    }

    function _initHistoryViewDialog($container, assessmentResultId) {
        if (assessmentsGridPopupOpening) {
            return;
        }
        assessmentsGridPopupOpening = true;
        $.ajax({
            url: 'care-coordination/assessment/result/' + assessmentResultId + '/history/view',
            headers: {'X-Content-Compressing': 'enabled'}
        })
            .success(function (data) {
                $container.empty();
                $container.append(data);
                var $modal = $container.find('#viewAssessmentResultModal');
                mdlUtils.find(_getFragment().$html, '', '#assessmentViewContainer').hide();
                var scoringEnabled = $("#scoringEnabled").val();
                var hasNumeration = $("#hasNumeration").val();
                var LSAType = $("#assessmentType").val();
                if (scoringEnabled === 'true') {
                    _initAssessmentsGroupList(_getFragment().widgets.assessmentsScoringGroupViewHistoryList, "assessmentsScoringGroupViewHistoryList");
                }
                var myCss = {
                    pageTitle: "sectionHeadSurvey",
                    question: {
                        mainRoot: "questionSurvey",
                        title: "assessment-label"
                    },
                    panel: {
                        title: "assessmentPanelHead"
                    },
                    row: "assessment-row"
                };

                Survey.JsonObject.metaData.addProperty("questionbase", "priorityFlag:boolean");
                Survey.JsonObject.metaData.addProperty("questionbase", "calendarFlag:text");
                Survey.JsonObject.metaData.addProperty("questionbase", "htmlClass:text");
                Survey.JsonObject.metaData.addProperty("panel", "panelAnchor:text");
                Survey.JsonObject.metaData.addProperty("panel", "assessmentSectionAnchor:text");
                Survey.JsonObject.metaData.addProperty("panel", "thirdLevel:text");
                Survey.JsonObject.metaData.addProperty("page", "tabKey:number");

                var template;
                if (hasNumeration) {
                    template = '"questionTitleTemplate": "{title}{require}",'
                } else {
                    template = '"questionTitleTemplate": "{no}. {title}{require}",';
                }

                var surveyResult = $(data).find("#residentAssessmentResult").val();
                var surveyContent = $(data).find("#residentAssessmentContent").val();

                surveyContent = surveyContent.slice(0, 1) + template + surveyContent.slice(1);
                Survey.Survey.cssType = "bootstrap";
                var surveyJSON = JSON.parse(surveyContent);
                var survey = new Survey.Model(surveyJSON);
                survey.showCompletedPage = false;
                survey.showNavigationButtons = false;
                survey.mode = 'display';
                survey.data = JSON.parse(surveyResult);

                //todo
                if (LSAType == 'true') {
                    //remove first "guide" page
                    survey.pages.splice(0, 1);
                    survey = addTabsToModalHistory(survey);
                    survey = setAnchors(survey);
                    $("#assessmentViewSectionNameHistory").hide();
                    $("#assessmentGeneralHistory").hide();
                    $("#assessmentCompletedByHistory").hide();
                    $(".assessmentComment").hide();
                    //todo
                    $(".modal-body").removeClass("new-modal-body");
                    $(".assessmentWzrd").removeClass("default-assessment-wizard");

                    addBackToTopButton($modal);
                    $container.find('.up-scroller').css({
                        'bottom': '10px',
                        'right': '40px'
                    });
                }

                survey = afterSurveyRendersQuestion(survey);
                survey = afterSurveyRendersPage(survey, $container);

                $("#assessmentContentHistoryViewContainer").Survey({
                    model: survey,
                    css: myCss
                });
                $modal.modal('show');

                surveyTabsProcess($container, survey, true, LSAType == 'true', true);

                $modal.on({
                    'hide.bs.modal': function () {
                        $(this).remove();
                        $('.nav-tabs a[href="#historyAssessmentTab"]').tab('show');
                        mdlUtils.find(_getFragment().$html, '', '#assessmentViewContainer').show();
                    }
                });

                $container.find('#cancelBtn').on("click", function () {
                    $modal.modal('hide');
                    mdlUtils.find(_getFragment().$html, '', '#assessmentViewContainer').show();
                });
                assessmentsGridPopupOpening = false;
            })
            .fail(function (data, data2) {
                console.log(data, data2);
                assessmentsGridPopupOpening = false;
                alert('Internal server error. Please contact administrator.');
            });

        return false;
    }

    function _initViewAssessmentResultDialog($container, assessmentResultId) {
        if (assessmentsGridPopupOpening) {
            return;
        }
        assessmentsGridPopupOpening = true;
        $.ajax({
            url: 'care-coordination/assessment/result/' + assessmentResultId + '/view',
            headers: {'X-Content-Compressing': 'enabled'},
            beforeSend: function () {
                $('#loader-div').removeClass('hidden');
            },
            complete: function () {
                $('#loader-div').addClass('hidden');
            }
        })
            .success(function (data) {
                $container.empty();
                $container.append(data);
                var $modal = $container.find('#viewAssessmentResultModal');
                var scoringEnabled = $("#scoringEnabled").val();
                var hasNumeration = $("#hasNumeration").val();
                var isLSAOrg = $("#assessmentType").val();

                if (scoringEnabled == 'true') {
                    _initAssessmentsGroupList(_getFragment().widgets.assessmentsScoringGroupViewList, "assessmentsScoringGroupViewList");
                    _initAssessmentsGroupList(_getFragment().widgets.assessmentsHistoryViewList, "assessmentsHistoryViewList");
                }
                var myCss = {
                    pageTitle: "sectionHeadSurvey",
                    question: {
                        mainRoot: "questionSurvey",
                        title: "assessment-label"
                    },
                    panel: {
                        title: "assessmentPanelHead"
                    },
                    row: "assessment-row"
                };

                Survey.JsonObject.metaData.addProperty("questionbase", "priorityFlag:boolean");
                Survey.JsonObject.metaData.addProperty("questionbase", "calendarFlag:text");
                Survey.JsonObject.metaData.addProperty("questionbase", "htmlClass:text");
                Survey.JsonObject.metaData.addProperty("panel", "panelAnchor:text");
                Survey.JsonObject.metaData.addProperty("panel", "assessmentSectionAnchor:text");
                Survey.JsonObject.metaData.addProperty("panel", "thirdLevel:text");
                Survey.JsonObject.metaData.addProperty("page", "tabKey:number");

                var template;
                if (hasNumeration) {
                    template = '"questionTitleTemplate": "{title}{require}",'
                } else {
                    template = '"questionTitleTemplate": "{no}. {title}{require}",';
                }
                var surveyResult = $("#residentAssessmentResult").val();
                var surveyContent = $("#residentAssessmentContent").val();
                Survey.Survey.cssType = "bootstrap";
                Survey.JsonObject.metaData.addProperty("panel", "anchors:text");

                surveyContent = surveyContent.slice(0, 1) + template + surveyContent.slice(1);
                var surveyJSON = JSON.parse(surveyContent);
                var survey = new Survey.Model(surveyJSON);
                survey.showCompletedPage = false;
                survey.showNavigationButtons = false;
                survey.mode = 'display';
                //todo view
                if (isLSAOrg == 'true') {
                    survey.pages.splice(0, 1);
                    survey = addTabsToModal(survey, true);
                    survey = setAnchors(survey);
                    $("#assessmentViewSectionName").hide();
                    $(".assessmentGeneral").hide();
                    $(".assessmentComment").hide();
                    $(".modal-body").removeClass("new-modal-body");
                    $(".assessmentWzrd").removeClass("default-assessment-wizard");

                    addBackToTopButton($modal);
                    $container.find('.up-scroller').css({
                        'bottom': '10px',
                        'right': '40px'
                    });
                }

                survey = afterSurveyRendersQuestion(survey);
                survey = afterSurveyRendersPage(survey, $container);

                survey.data = JSON.parse(surveyResult);
                $("#assessmentContentViewContainer").Survey({
                    model: survey,
                    css: myCss
                });
                $modal.modal('show');

                $modal.on({
                    'hide.bs.modal': function () {
                        $(this).remove();
                    },
                    'shown.bs.modal': function () {
                        $('.nav-tabs a[href="#detailsAssessmentTab"]').tab('show');
                        surveyTabsProcess($container, survey, true, isLSAOrg == 'true');
                    }
                });

                $('.nav-tabs a[href="#historyAssessmentTab"]').on('shown.bs.tab', function () {
                    _initAssessmentHistory();
                });

                $container.find('.assessment-footer__left-arrow, assessment-footer__back-btn').hide();

                $container.find('#cancelBtn').on("click", function () {
                    $modal.modal('hide');
                });
                assessmentsGridPopupOpening = false;
            })
            .fail(function (data, data2) {
                console.log(data, data2);
                assessmentsGridPopupOpening = false;
                alert('Internal server error. Please contact administrator.');
            });

        return false;
    }

    function _initAssessmentsGroupList(list, listId) {
        list = wgtUtils.grid(_getFragment().$html, '', {
            tableId: listId,
            totalDisplayRows: 25,
            paginate: false,
            sort: false,
            callbacks: {
                rowCallback: function (row, data, index) {
                    //TODO
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
        list.api().ajax.reload();
    }


    function _initNoteList() {
        $('#noteDetails').hide();
        _getFragment().widgets.patientNoteList = wgtUtils.grid(_getFragment().$html, '', {
            tableId: "patientNoteList",
            totalDisplayRows: 25,
            selectable: {
                type: 'single'
            },

            callbacks: {
                rowCallback: function (row, data, index) {
                    var $row = $(row);

                    if (data != null) {
                        var noteId = data.noteId;
                        // clear row content
                        $row.children('td:eq(0)').html('<div class="col-md-8">' + data.type + ' <br/> ' + data.status + '</div><div class="col-md-4"> ' + moment(data.lastModifiedDate).format('<b>MM/DD/YYYY</b> <br/> hh:mm A') + '</div>');
                        $row.attr("data-id", noteId);
                        $row.on('click', function () {
                            // Update Note details section
                            _showNoteDetails(noteId);
                        });
                        $row.css('cursor', 'pointer');
                    }
                },
                "drawCallback": function (settings) {
                    if (openNoteDetailsOnNextPage) {
                        var $row = $find("[data-id='" + displayedNoteId + "']");
                        $row.addClass("selected");
                        if (showNoteFromHistory && cFragmentUrl.params && displayedNoteId != cFragmentUrl.params.note) {
                            $('#historyNoteId-' + cFragmentUrl.params.note).click();
                        }
                        showNoteFromHistory = false;
                        openNoteDetailsOnNextPage = false;
                    }
                },
                errorCallback: function (error) {
                    alert(error.responseText);
                    // TODO
                },
                footerCallback: function (tfoot, data, start, end, display) {
                    //$(tfoot).show();
                    //
                    //var isHidden = data.length < 20;
                    //if (isHidden) {
                    $(tfoot).hide();
                    //}
                },
                headerCallback: function (thead, data, start, end, display) {
                    $(thead).hide();
                },

                initComplete: function (settings, json) {
                    if (json.content !== undefined && json.content.length > 0
                        && (cFragmentUrl.params === undefined || cFragmentUrl.params.note === undefined)) {
                        _selectByIndex(_getFragment().widgets.patientNoteList, 0);
                    }
                }

            }
        });

        _initNotesModal(mdlUtils.find(_getFragment().$html, '', '#addNote'),
            'care-coordination/notes/patient/' + $('#currentPatientId').val() + '/new-note',
            'A note has been created',
            function (data) {
                var patientId = $('#currentPatientId').val();
                _initNotesTotal(patientId);
                _showNote(data, patientId)
            }
        );
    }

    function _initNotesModal(element, endpoint, successMsg, successCallback) {
        var $noteContainer = mdlUtils.find(_getFragment().$html, '', '#noteContainer');
        element.on('click', function () {
            var $button = $(this);
            if ($button.hasClass("pending")) {
                return;
            }
            $button.addClass("pending");
            $.ajax(endpoint)
                .success(function (data) {
                    $noteContainer.empty();
                    $noteContainer.append(data);

                    $noteContainer.find('[id="lastModifiedDate"]').datetimepicker({
                        defaultDate: new Date(),
                        format: 'YYYY-MM-DD hh:mm A Z',
                        maxDate: new Date()
                    });

                    $noteContainer.find('[id="encounterDate"]').datetimepicker({
                        defaultDate: new Date(),
                        format: 'MM/DD/YYYY'
                    });

                    $noteContainer.find('[id="from"]').timepicker({
                        timeFormat: 'h:mm p',
                        interval: 30,
                        minTime: '00',
                        maxTime: '11:30pm',
                        defaultTime: getAdjustedDate($noteContainer, new Date()),
                        startTime: '12:00am',
                        dynamic: false,
                        dropdown: true,
                        scrollbar: true
                    });
                    $noteContainer.find('[id="to"]').timepicker({
                        timeFormat: 'h:mm p',
                        interval: 30,
                        minTime: '00',
                        maxTime: '11:30pm',
                        defaultTime: getAdjustedDate($noteContainer, new Date()),
                        startTime: '12:00am',
                        dynamic: false,
                        dropdown: true,
                        scrollbar: true
                    });

                    $noteContainer.find('#timeZoneOffset').val(new Date().getTimezoneOffset());
                    var subTypeDropdown = $noteContainer.find('#subTypeId');
                    var admitDateDropdown = $noteContainer.find('#noteResidentAdmittanceHistoryDtoId');

                    var from = $noteContainer.find('#from');
                    var to = $noteContainer.find('#to');
                    _populateCalculatedFields($noteContainer);
                    from.on('blur', function () {
                        //Unavoidable.. change event is not working so need to add timeout to let the value get populated.
                        window.setTimeout(function () {
                            _populateCalculatedFields($noteContainer, true);
                        }, 500)
                    });

                    to.on('blur', function () {
                        //Unavoidable.. change event is not working so need to add timeout to let the value get populated.
                        window.setTimeout(function () {
                            _populateCalculatedFields($noteContainer, false);
                        }, 500)
                    });
                    subTypeDropdown.on('change', function () {
                        var selectedOption = $(this).find(':selected');
                        if ($.inArray(selectedOption.attr('data-encounter-code'), encounterType) >= 0) {
                            admitDateDropdown.attr('disabled', 'disabled');
                            $noteContainer.find('#encounter-note-type-content').show();
                        } else {
                            $noteContainer.find('#encounter-note-type-content').hide();
                        }
                    });
                    if (admitDateDropdown.children('option').length === 1) {    // taking '-- Select --' into consideration
                        admitDateDropdown.attr('disabled', 'disabled');
                        subTypeDropdown.find('[data-follow-up-code]').attr('disabled', 'disabled');
                        subTypeDropdown.find('[data-follow-up-code]').attr('title', 'Please add the admittance information or the intake date on the "Patient Details" screen');

                    } else {
                        subTypeDropdown.on('change', function () {
                            var selectedOption = $(this).find(':selected');
                            if ($.inArray(selectedOption.attr('data-follow-up-code'), followUpCodes) === -1) {
                                admitDateDropdown.prop('selectedIndex', 0);
                                admitDateDropdown.attr('disabled', 'disabled');
                                admitDateDropdown.blur();   //trigger revalidation of the field
                                subTypeDropdown.children('option').removeAttr('disabled');
                                subTypeDropdown.children('option').removeAttr('title');
                            }
                            else {
                                admitDateDropdown.removeAttr('disabled');
                                admitDateDropdown.children('option').removeAttr('title');
                                $.ajax('care-coordination/notes/patient/' + $('#currentPatientId').val() + '/followUp/'
                                    + selectedOption.attr('data-follow-up-code') + '/getTaken')
                                    .success(function (data) {
                                        admitDateDropdown.children('option').removeAttr('disabled');
                                        data.forEach(function (admitId) {
                                            if (admitDateDropdown.val() == admitId) {
                                                admitDateDropdown.prop('selectedIndex', 0);
                                            }
                                            var option = admitDateDropdown.find('[value=' + admitId + ']');
                                            option.attr('disabled', 'disabled');
                                            option.attr('title', '"' + selectedOption.text() + '" note has been already created for this admit/intake date.')
                                        });
                                    })
                            }
                        });

                        admitDateDropdown.on('change', function () {
                            var options = subTypeDropdown.children('option');
                            options.removeAttr('disabled');
                            options.removeAttr('title');
                            if ($(this).val()) {
                                $.ajax('care-coordination/notes/patient/' + $('#currentPatientId').val() + '/admit/'
                                    + $(this).val() + '/getTaken')
                                    .success(function (data) {
                                        data.forEach(function (followUpCode) {
                                            if (subTypeDropdown.find(':selected').attr('data-follow-up-code') === followUpCode) {
                                                subTypeDropdown.prop('selectedIndex', 0);
                                            }
                                            var option = subTypeDropdown.find('[data-follow-up-code=' + followUpCode + ']');
                                            option.attr('disabled', 'disabled');
                                            option.attr('title', '"' + option.text() + '" note has been already created for this admit/intake date.')
                                        });
                                    });
                            }
                        });
                    }

                    _addNoteValidation($noteContainer);

                    //var clicked = false;
                    $noteContainer.find("#submitNoteBtn").on('click', function () {
                        var $form = $noteContainer.find("#noteForm");
                        if (!$form.valid()) {
                            return false;
                        }
                        var $th = $(this);
                        if (!$th.hasClass("pending")) {
                            $th.addClass("pending");
                            $.ajax({
                                url: $form.attr('action'),
                                data: new FormData($form[0]),
                                type: 'POST',
                                enctype: 'multipart/form-data',
                                processData: false,
                                contentType: false,
                                beforeSend: function (xhr) {
                                    mdlUtils.csrf(xhr);
                                },
                                success: function (data) {
                                    $noteContainer.find("#noteModal").modal('hide');
                                    bootbox.alert(successMsg, function () {
                                        if (successCallback !== undefined) {
                                            successCallback(data);
                                        }
                                    });
                                },
                                error: function (error) {
                                    $noteContainer.find("#noteModal").modal('hide');
                                    mdlUtils.onAjaxError(error, function () {
                                        alert(error.responseText);
                                    });
                                    $th.removeClass("pending");
                                }
                            });
                        }
                        return false;
                    });
                    //mdlUtils.randomize($newEventContainer, _getFragment().random, false)

                    $button.removeClass("pending");
                    $noteContainer.find("#noteModal").modal('show');
                })
                .fail(function (response) {
                    $button.removeClass("pending");
                    alert(response.responseText); // TODO
                });
            return false;
        });
    }


    function getAdjustedDate($container, date) {
        if ($container.find('#from')[0].value != "" || $container.find('#to')[0].value != "")
            return;

        var m = date.getMinutes();
        var dt = date;
        if (m != 0) {
            var diff = (m > 30) ? 60 - m : 30 - m;
            dt = new Date(date.getTime() + diff * 60000);
        }
        return formatAMPM(dt);
    }

    function formatAMPM(date) {
        var hours = date.getHours();
        var minutes = date.getMinutes();
        var ampm = hours >= 12 ? 'pm' : 'am';
        hours = hours % 12;
        hours = hours ? hours : 12; // the hour '0' should be '12'
        minutes = minutes < 10 ? '0' + minutes : minutes;
        return hours + ':' + minutes + ' ' + ampm;
    }

    function _populateCalculatedFields($container, resetByFrom) {
        var from = $container.find('#from');
        var to = $container.find('#to');
        var totalTimeSpent = $container.find('#totalTimeSpent');
        var range = $container.find('#range');
        var unit = $container.find('#unit');
        var startTime = moment(from.val(), "hh:mm A");
        if (resetByFrom) {
            endTime = moment(startTime).add(30, 'minutes').format("h:mm A");
            to.val(endTime);
        }
        var endTime = moment(to.val(), "hh:mm A");
        var duration = moment.duration(endTime.diff(startTime)).asMinutes();
        if (duration < 1) {
            endTime = moment(startTime).add(30, 'minutes').format("h:mm A");
            to.val(endTime);
            duration = 30;
        }
        totalTimeSpent.val(duration);
        var m = Math.floor(duration / 15);
        var r = duration % 15;
        if (r > 7) {
            m += 1;
        }
        var startRange = m * 15 - 7;
        var endRange = m * 15 + 7;
        if (startRange < 0) {
            startRange = 0;
        }
        range.val(startRange + " mins - " + endRange + " mins");
        unit.val(m);
    }

    function _addNoteValidation($container) {
        return $container.find("#noteForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    subjective: {
                        required: function (element) {
                            return $("#objective").is(':blank') && $("#assessment").is(':blank') && $("#plan").is(':blank');
                        },
                        maxlength: 20000
                    },
                    objective: {maxlength: 20000},
                    assessment: {maxlength: 20000},
                    plan: {maxlength: 20000},
                    clinicianCompletingEncounter: {maxlength: 256},
                    lastModifiedDate: {
                        required: true
                    },
                    'subType.id': {
                        required: true
                    },
                    'noteResidentAdmittanceHistoryDto.id': {
                        required: function (element) {
                            return !element.disabled;
                        }
                    },
                    encouterNoteTypeId: {
                        required: true
                    },
                    encounterDate: {
                        required: true
                    },
                    from: {
                        required: true
                    },
                    to: {
                        required: true
                    }
                },
                messages: {
                    subjective: {
                        required: getErrorMessage("field.empty")
                    },
                    lastModifiedDate: {
                        required: getErrorMessage("field.empty")
                    },
                    'subType.id': {
                        required: getErrorMessage("field.empty")
                    },
                    'noteResidentAdmittanceHistoryDto.id': {
                        required: getErrorMessage("field.empty")
                    },
                    encouterNoteTypeId: {
                        required: getErrorMessage("field.empty")
                    },
                    encounterDate: {
                        required: getErrorMessage("field.empty")
                    },
                    from: {
                        required: getErrorMessage("field.empty")
                    },
                    to: {
                        required: getErrorMessage("field.empty")
                    }
                }
            })
        )
    }

    function _fetchNotesTotal(residentId, successCallback) {
        $.ajax({
            type: 'GET',
            contentType: 'json',

            url: 'care-coordination/notes/patient/' + residentId + '/total',
            success: function (totalCount) {
                successCallback(totalCount < 0 ? 0 : totalCount);
            },
            error: function () {
                $('#notesCountTabPanel').addClass('hidden');
            }
        });
    }

    function _initNotesTotal(residentId, hashKey, databaseId) {
        currentTotalNotesCount = 0;
        _fetchNotesTotal(residentId, function (totalCount) {
            currentTotalNotesCount = totalCount;
            _setNotesBadgeValue(totalCount);
        });
    }

    function _setNotesBadgeValue(totalCount) {
        var $badge = $('#notesCountTabPanel');
        $badge.html(totalCount);
        $badge.removeClass('hidden');
    }

    function _updateNotesBadgeValue(addend) {
        var $badge = $('#notesCountTabPanel');
        var newValue = parseInt($badge.html()) + addend;
        if (newValue > 0) {
            $badge.html(newValue);
        } else {
            $badge.html(0);
            $badge.addClass('hidden');
        }
    }

    function _addNewEventValidation($container) {
        return $container.find("#newEventForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    'employee.roleId': {required: true},

                    'manager.firstName': {
                        required: true,
                        minlength: 2,
                        maxlength: 128
                    },
                    'manager.lastName': {
                        required: true,
                        minlength: 2,
                        maxlength: 128
                    },
                    'manager.email': {
                        required: false,
                        email: true
                    },
                    'manager.phone': {
                        required: false,
                        phone: true
                    },
                    //
                    'responsible.firstName': {
                        required: true,
                        minlength: 2,
                        maxlength: 128
                    },
                    'responsible.lastName': {
                        required: true,
                        minlength: 2,
                        maxlength: 128
                    },
                    'responsible.address.street': {
                        required: true,
                        minlength: 2,
                        maxlength: 255
                    },
                    'responsible.address.city': {
                        required: true,
                        minlength: 2,
                        maxlength: 128
                    },
                    'responsible.address.state.id': {required: true},
                    'responsible.address.zip': {
                        required: true,
                        positiveInteger: true,
                        lengthEqual: 5
                    },
                    //
                    'eventDetails.eventDatetime': {required: true},
                    'eventDetails.situation': {maxlength: 5000},
                    'eventDetails.background': {maxlength: 5000},
                    'eventDetails.assessment': {maxlength: 5000},
                    'eventDetails.followUpDetails': {
                        required: true,
                        maxlength: 5000
                    },

                    'treatingPhysician.firstName': {
                        required: true,
                        minlength: 2,
                        maxlength: 128
                    },
                    'treatingPhysician.lastName': {
                        required: true,
                        minlength: 2,
                        maxlength: 128
                    },
                    'treatingPhysician.address.street': {
                        required: true,
                        minlength: 2,
                        maxlength: 255
                    },
                    'treatingPhysician.address.city': {
                        required: true,
                        minlength: 2,
                        maxlength: 128
                    },
                    'treatingPhysician.address.state.id': {required: true},
                    'treatingPhysician.address.zip': {
                        required: true,
                        positiveInteger: true,
                        lengthEqual: 5
                    },
                    'treatingPhysician.phone': {
                        required: false,
                        phone: true
                    },
                    //
                    'treatingHospital.name': {
                        required: true,
                        minlength: 2,
                        maxlength: 300
                    },
                    'treatingHospital.address.street': {
                        required: true,
                        minlength: 2,
                        maxlength: 255
                    },
                    'treatingHospital.address.city': {
                        required: true,
                        minlength: 2,
                        maxlength: 128
                    },
                    'treatingHospital.address.state.id': {required: true},
                    'treatingHospital.address.zip': {
                        required: true,
                        positiveInteger: true,
                        lengthEqual: 5
                    },
                    'treatingHospital.phone': {
                        required: false,
                        phone: true
                    }
                },
                messages: {
                    'manager.firstName': {
                        required: getErrorMessage("field.empty")
                    },
                    'manager.lastName': {
                        required: getErrorMessage("field.empty")
                    },
                    gender: {
                        required: getErrorMessage("field.empty")
                    },
                    dateOfBirth: {
                        dateExp: getErrorMessage("field.dateFormat"),
                        required: getErrorMessage("field.empty")
                    },
                    ssn: {
                        integer: getErrorMessage("field.integer"),
                        lengthEqual: getErrorMessage("field.lengthEqual.4"),
                        required: getErrorMessage("field.empty")
                    },
                    state: {
                        stateUS: getErrorMessage("field.state")
                    }
                }
            })
        );
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

    function _initPatientsList() {
        _getFragment().widgets.patientsList = _grid({
            tableId: "patientsList",
            searchFormId: "patientsFilter",
            totalDisplayRows: 25,
            colSettings: {
                //birthDate: {width: '70px'}//,
                //actions: {width: '75px'}
            },
            // order by Last Name, then by First Name
            order: [[1, 'asc'], [0, 'asc']],

            callbacks: {
                rowCallback: function (row, data, index) {
                    var $row = $(row);
                    var patientId = data.id;
                    $row.css('cursor', 'pointer');

                    $row.on('click', function () {
                        // Show PatientDetails
                        showPatientDetails(patientId, data);
                    });

                    if (!data.active) {
                        $row.css("color", "silver");
                        $row.children('td:eq(0)').css("color", "rgb(165, 200, 200)");
                    }

                    var $actionsTd = $row.children('td:eq(8)');
                    $actionsTd.empty();
                    if (data.hasMerged) {
                        //var $tdRowActions = mdlUtils.find(_getFragment().$html, '', '.showMergedLink').clone();
                        var $tdRowActions = $('<a class="showMergedLink">Show Matches</a>');
                        //var tdRowActionsId = 'rowActions-' + index;
                        //$tdRowActions.attr('id', tdRowActionsId);
                        //$tdRowActions.removeClass('hidden');
                        $actionsTd.on('click', function () {
                                showMergedPatients(data.id, $row);
                                return false;
                            }
                        );
                        $actionsTd.append($tdRowActions);
                        //$actionsTd.css( "padding-top", 0 );
                    }


                },
                errorCallback: function (error) {
                    alert(error.responseText);
                },
                footerCallback: function (tfoot, data, start, end, display) {
                    $(tfoot).hide();
                }
            }
        });
        _getFragment().widgets.patientsList.api().ajax.reload();
        currentOrganizationFilter = ExchangeApp.modules.Header.getCurrentOrganizationFilter();
        currentCommunityFilter = ExchangeApp.modules.Header.getCurrentCommunityFilter();
    }

    function _initDocumentsTotal(residentId, hashKey, databaseId) {
        var aggregated = true;

        //var url = $find('#patientInfoUrl').attr('href') + residentId.toString() + '/documents/' + aggregated + '/total?hashKey=' + hashKey + '&databaseId=' + databaseId;

        $.ajax({
            type: 'GET',
            contentType: 'json',
            url: 'patient-info/' + residentId + '/documents/' + aggregated + '/total?hashKey=' + hashKey + '&databaseId=' + databaseId,
            success: function (totalCount) {
                var isEmpty = (totalCount <= 0);
                _setDocListBadgeValue(isEmpty, totalCount);
            },
            error: function () {
                $('#documentsCountTabPanel').addClass('hidden');
            }
        });
    }

    function _setDocListBadgeValue(isEmpty, totalCount) {
        var $badge = $('#documentsCountTabPanel');
        $badge.html(isEmpty ? 0 : totalCount);
        $('#documentsCountTabPanel').removeClass('hidden');
    }

    function _updateDocListBadgeValue(addend) {
        var $badge = $('#documentsCountTabPanel');
        var newValue = parseInt($badge.html()) + addend;
        if (newValue > 0) {
            $badge.html(newValue);
        } else {
            $badge.html(0);
            $badge.addClass('hidden');
        }
    }

    function _initCcdSections(residentId, hashKey) {
        var sectionNames =
            ['allergies', 'medications', 'problems', 'procedures', 'results', 'encounters',
                'advanceDirectives', 'familyHistory', 'vitalSigns', 'immunizations', 'payerProviders',
                'medicalEquipment', 'socialHistory', 'planOfCare'];

        for (var i = 0; i < sectionNames.length; i++) {
            _initCcdTotal(sectionNames[i], residentId, hashKey);
        }

        $('[data-ajax-anchor="true"]').on('click', function () {
            var $trg = $($(this).attr('href')).parent();
            if ($trg.length) {
                $('html, body').animate({scrollTop: $trg.offset().top}, 200);
            }
            return false;
        });

        /*dynamic panels loading*/
        $('.ccdDetailItem .collapse, .ccdHeaderDetails .collapse').on('show.bs.collapse', function () {
            var $this = $(this),
                url = $this.attr('href'),
                $content = $(this).find('.clp-pnl-content');

            var disabled = $('[href*="' + $this.attr('id') + '"]').hasClass('disabled');
            if (disabled) return false;

            if (url) {
                var id = $this.attr('id');
                $.get(url, function (data, code, xhr) {
                    var $fragment = $(data);
                    var cFragment = _getFragment();

                    //_randomize($fragment, cFragment.random);

                    $content.html($fragment);

                    var listId = id + 'List';

                    _getFragment().widgets[listId] = wgtUtils.grid(_getFragment().$html, '', {
                        tableId: listId,
                        totalDisplayRows: 15,
                        colSettings: {
                            plannedActivity: {
                                bSortable: true,
                                customRender: function (data, type, row) {
                                    if (!data)
                                        return '';
                                    if (row.isFreeText) {
                                        return '<a href="narrative/ccd/' + id + '/' + row[idsMap[id]] + '/' + data + '" id="' + id + '-name-free-text' + row.id + '" target="_blank" rel="nofollow noopener">' + data + '</a>'
                                    }
                                    return data;
                                }
                            },
                            name: {
                                bSortable: true,
                                customRender: function (data, type, row) {
                                    if (!data)
                                        return '';
                                    if (row.viewable) {
                                        return '<a href="#" id="' + id + '-name-' + row.id + '" data-observation-id="' + row[idsMap[id]] + '">' + data + '</a>'
                                    }
                                    return data;
                                }
                            },
                            dataSource: {
                                //bSortable: false,
                                customRender: function (data, type, row) {
                                    if (!data) return '';
                                    if (row.manual === true) {
                                        return '<span>Simply Connect HIE</span>';
                                    }
                                    return '<a href="#" id="problems-source-' + row.id + '" onclick="return false" class="dataSourceCol">' + data + '</a>';
                                }
                            },
                            supportingDocuments: {
                                bSortable: false,
                                customRender: function (data) {
                                    if (!data)
                                        return '';
                                    return data.split('|').reduce(function (sum, docUrl, index) {
                                        return sum + (index == 0 ? '' : ', ') + '<a href="' + docUrl + '">Advance Directive</a>';
                                    }, '');
                                }
                            },
                            actions: {
                                width: '100px',
                                'className': 'actionsColumn'
                            }

                        },
                        callbacks: {
                            rowCallback: function (row, data, index) {
                                var $row = $(row);
                                showDataSourceDetailsPopup($row.find('#problems-source-' + data.id)[0], data.dataSource, data.dataSourceOid, data.community, data.communityOid);

                                var actionsColumn = 'td.actionsColumn';
                                var nameColumn = 'td:first-child';

                                $row.children(actionsColumn).empty();
                                var $tdRowActions = mdlUtils.find(_getFragment().$html, '', '#ccdRowActions').clone();

                                if (data.editable === true || data.deletable === true) {
                                    var tdRowActionsId = 'ccdRowActions-' + index;
                                    $tdRowActions.attr('id', tdRowActionsId);
                                    $tdRowActions.removeClass('hidden');
                                    $row.children(actionsColumn).append($tdRowActions);

                                    if (data.editable === true) {
                                        var $editBtn = $tdRowActions.find('.editCcd');
                                        $editBtn.removeClass('hidden');
                                        $editBtn.on('click', function () {
                                            initCcdModal.call(this, {
                                                sectionName: id,
                                                mode: 'edit',
                                                residentId: residentId,
                                                hashKey: hashKey,
                                                id: data[idsMap[id]]
                                            });
                                            return false;
                                        })

                                    }

                                    if (data.deletable === true) {
                                        var $deleteBtn = $tdRowActions.find('.deleteCcd');
                                        $deleteBtn.removeClass('hidden');
                                        $deleteBtn.on('click', function () {
                                            bootbox.confirm({
                                                title: "Delete the record?",
                                                message: "The record will be deleted.",
                                                buttons: {
                                                    cancel: {
                                                        label: 'Cancel'
                                                    },
                                                    confirm: {
                                                        label: 'Ok'
                                                    }
                                                },
                                                callback: function (result) {
                                                    if (result) {
                                                        $.ajax({
                                                            url: 'patient-info/' + residentId + '/ccd/' + id + '/delete/' + data[idsMap[id]] + '?hashKey=' + hashKey,
                                                            data: '',
                                                            type: 'POST',
                                                            enctype: 'multipart/form-data',
                                                            processData: false,
                                                            contentType: false,
                                                            beforeSend: function (xhr) {
                                                                mdlUtils.csrf(xhr);
                                                            },
                                                            success: function (data) {
                                                                bootbox.alert('The record has been deleted');
                                                            },
                                                            error: function (error) {
                                                                mdlUtils.onAjaxError(error, function () {
                                                                    alert(error.responseText);
                                                                });
                                                                // $th.removeClass("pending");
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                            return false;
                                        })
                                    }
                                }

                                if (data.viewable === true && !data.isFreeText === true) {
                                    $row.on('click', function (e) {
                                        initCcdModal.call(this, {
                                            sectionName: id,
                                            mode: 'view',
                                            residentId: residentId,
                                            hashKey: hashKey,
                                            id: data[idsMap[id]]
                                        });
                                        if (e) {
                                            e.preventDefault();
                                        }
                                    });
                                }

                            },
                            footerCallback: function (tfoot, data, start, end, display) {
                                $(tfoot).hide();
                            },
                        }
                        /*,

                                    selectable: {
                                        type: 'single'
                                    }*/
                    });
                    //_getFragment().widgets[listId].api().ajax.reload();

                    /*if (sectionInitMap[_cleanId(id)]) {
                        sectionInitMap[_cleanId(id)]();
                    } else {
                        _initPatientDetailsMasonryWgt();
                    }*/

                    //_ajaxify($fragment, cFragment.random);
                    $fragment.find('.addCcdLink').on('click', function (e) {
                        initCcdModal.call(this, {
                            sectionName: id,
                            mode: 'add',
                            residentId: residentId,
                            hashKey: hashKey
                        });
                        if (e) {
                            e.preventDefault();
                        }
                    })
                });
            }
        });

        $("#openAllBtn, #closeAllBtn").on('click', function () {
            $("#openAllBtn, #closeAllBtn").toggleClass('hidden');
            var operation = $(this).attr('id').indexOf('openAllBtn') >= 0 ? 'show' : 'hide';
            $('.ccdDetailItem .collapse').collapse(operation);
        });

        /*init 'ADD TO' dropdown*/
        $("[data-section-name]").on('click', function (e) {
            initCcdModal.call(this, {
                sectionName: $(this).attr('data-section-name'),
                mode: 'add',
                residentId: residentId,
                hashKey: hashKey
            });
            if (e) {
                e.preventDefault();
            }
        });

        $(".jumpLnk").on('click', function () {
            var href = $(this).attr('href');
            $((href)).collapse('show');
        });
    }

    function _initCcdTotal(sectionName, residentId, hashKey) {
        var aggregated = true;

        var url = 'patient-info/' + residentId + '/ccd/' + sectionName + '/' + aggregated + '/total?hashKey=' + hashKey;

        $.ajax({
            type: 'GET',
            contentType: 'json',
            url: url,
            success: function (totalCount) {
                var isEmpty = (totalCount <= 0);

                var $badge = $('#' + sectionName + 'CollapsedPanel' + ' .badge');
                $badge.find('.badgeValue').html(isEmpty ? 0 : totalCount);

                $badge.toggleClass('hidden', isEmpty);
                $('#' + sectionName + 'CollapsedPanel').toggleClass('disabled', isEmpty);
            },
            error: function () {
                $('#' + sectionName + 'CollapsedPanel' + ' .badge').addClass('hidden');
            }
        });
    }

    function showDataSourceDetailsPopup(element, orgName, orgOid, commName, commOid) {
        if (!element) {
            return;
        }
        var $trPopoverContent = $('#dataSourceDetailsTemplate').clone();

        var popoverId = 'dataSourceDetailsTemplate-' + element.id;
        $trPopoverContent.attr('id', popoverId);


        $trPopoverContent.removeClass('hidden');
        $(element).popover({
            html: true,
            content: $trPopoverContent[0],
            trigger: 'hover',
            placement: function () {
                return 'bottom';
            }
        });
        var data = {
            'dataSourceName': orgName,
            'dataSourceOID': orgOid,
            'communityName': commName,
            'communityOID': commOid,
        };

        for (var key in data) {
            if (data[key]) {
                $trPopoverContent.find('#' + key).text(data[key]);
            } else {
                $trPopoverContent.find('#' + key + 'Layout').hide();
            }
        }
    }

    function _initPatientCcdDocumentsTabUtils() {
        patientTabUtils = ExchangeApp.utils.patientccd;
        patientTabUtils.init(_getFragment, _grid, _alert, cFragmentUrl, true);
    }

    function showPatientDetails(patientId, data, isNew, forceProcessing, callback) {
        router.route({template: 'care-coordination/patients/' + patientId + '/details'});

        _initPatientCcdDocumentsTabUtils();
        var patientHashKey = data ? data.hashKey : null;
        var patientDatabaseId = data ? data.organizationId : null;
        patientActiveTab = null;
        patientEventsActiveTab = null;

        $.ajax({
            url: "care-coordination/patients/patient/" + patientId + "/details",
            headers: {'X-Content-Compressing': 'enabled'}
        }).success(data, function (data) {

            if (!forceProcessing && $find("#patientsContent").is(":hidden")) {
                return; // belated response from server -> do nothing
            }

            $find("#patientsContent").hide();
            var patientDetailsContent = $find("#patientDetailsContent");

            patientDetailsContent.hide();
            patientDetailsContent.empty();

            patientDetailsContent.append(data);
            patientDetailsContent.show();

            mdlUtils.find(_getFragment().$html, '', ".patientTabs").on('click', function () {
                var $tab = $(this);
                var tabId = $tab.attr('id');

                var template = 'care-coordination/patients/' + patientId;

                if (tabId === 'patientDetailsTab') template += '/details';
                if (tabId === 'patientEventsTab') template += '/events';
                if (tabId === 'ccdDetailsTab') template += '/ccd-details';
                if (tabId === 'documentsTab') template += '/documents';
                if (tabId === 'notesTab') template += '/notes';
                if (tabId === 'assessmentsTab') template += '/assessments';

                if (tabId === 'servicePlansTab') {
                    _getFragment().widgets.servicePlanListPanel.update();
                    template += '/service-plans';
                }

                router.route({template: template});
            });

            _initPatientEvents(patientId);

            if (patientHashKey === null) {
                patientHashKey = patientDetailsContent.find('#hashKey').val();
            }
            if (patientDatabaseId === null) {
                patientDatabaseId = patientDetailsContent.find('#organizationId').val();
            }

            _initDocumentsTotal(patientId, patientHashKey, patientDatabaseId);
            _initNotesTotal(patientId);
            _initAssessmentsTotal(patientId);
            _initServicePlansTotal(patientId);
            _initCcdSections(patientId, patientHashKey);

            // init actions
            _initPatientCareTeam(false);
            _initPatientCareTeam(true);
            _initEventList();
            _initNoteList();
            _initAssessmentsButton();
            _initAssessmentsList();
            _initServicePlans({
                container: mdlUtils.find(_getFragment().$html, '', '#patientServicePlansContent')
            });

            patientTabUtils.initCompanyName();

            var downloadUrl = 'patient-info/' + patientId + '/documents/';
            patientTabUtils.initDocumentList(patientId, patientDatabaseId, patientHashKey, true, downloadUrl, function (options) {
                return wgtUtils.grid(_getFragment().$html, '', options);
            });
            var downloadDeleteBasicUrl = $('#deleteDocumentUrl').attr('href') + patientId + '/';
            patientTabUtils.setDocumentListPanelEvents(patientId, patientDatabaseId, patientHashKey, downloadDeleteBasicUrl, true, _updateDocListBadgeValue, true, 'composeMsgBtnCC');
            patientTabUtils.initUploadForm(patientId, patientDatabaseId, patientHashKey, '#care-coordination', downloadDeleteBasicUrl, _updateDocListBadgeValue);

            if (isNew) {
                $("#newlyCreatedAlert").show();
            }

            $(".draggableBox").draggable({
                containment: "window",
                handle: ".boxHeader",
                cursor: "move",
                opacity: 0.35,
                iFrameFix: true
            });

            mdlUtils.find(_getFragment().$html, '', '#videoCallLink').on('click', function () {
                if (initCall()) {
                    startCall();
                }
                return false;
            });

            var growlId = 0;
            mdlUtils.find(_getFragment().$html, '', '#testIncomingCall').on('click', function () {
                var growl = $.notify({
                    // options
                    icon: 'resources/images/incoming-call.png',
                    //icon: 'resources/images/incoming-call-emergency.png',
                    title: 'Tommy Walker',
                    message: 'Incoming call'
                }, {
                    // settings
                    element: 'body',
                    type: "info",
                    allow_dismiss: false,
                    newest_on_top: false,
                    showProgressbar: false,
                    placement: {
                        from: "bottom",
                        align: "right"
                    },
                    offset: 20,
                    spacing: 10,
                    z_index: 1061,
                    delay: 0,//12000,
                    animate: {
                        enter: 'animated fadeInUp',
                        exit: 'animated fadeOutDown'
                    },
                    icon_type: 'image',
                    template: '<div id="growl-' + ++growlId + '" data-notify="container" class="col-xs-11 col-sm-6 col-md-3 alert alert-{0} nucleus-incoming-call" role="alert">' +
                    '<button type="button" aria-hidden="true" class="close" data-notify="dismiss">×</button>' +
                    '<div class="ldr-ui-layout row">' +
                    '   <div class="ldr-ui-layout col-sm-6 col-xs-6" style="/*background-color: dimgrey*/">' +
                    '       <div class="ldr-ui-layout title-container"><span data-notify="title">{1}</span></div>' +
                    '       <div class="ldr-ui-layout message-container">' +
                    '           <span data-notify="icon"></span> <span data-notify="message">{2}</span>' +
                    '       </div>' +
                    '   </div>' +
                    '   <div class="ldr-ui-layout col-md-offset-2 col-sm-offset-1 col-xs-offset-1 col-sm-4 col-xs-4" style="/*background-color: darkgreen;*/ padding: 0">' +
                    '       <div class="ldr-ui-layout row" style="/*background-color: green;*/">' +
                    '       <div class="ldr-ui-layout col-sm-6 col-xs-6" style="/*background-color: darkblue;*/ padding: 0">' +
                    '           <a role="button" class="ldr-ui-btn btn answer-call-with-video"><img src="resources/images/answer-call-with-video.png"/></a>' +
                    '       </div>' +
                    '       <div class="ldr-ui-layout col-sm-6 col-xs-6" style="/*background-color: darkred;*/ padding: 0">' +
                    '           <a role="button" class="ldr-ui-btn btn decline-call"><img src="resources/images/end-cancel-call.png"/></a>' +
                    '       </div></div>' +
                    '   </div>' +
                    '   <div class="progress" data-notify="progressbar">' +
                    '       <div class="progress-bar progress-bar-{0}" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0;"></div>' +
                    '   </div>' +
                    '</div>' +
                    '<a href="{3}" target="{4}" data-notify="url"></a>' +
                    '</div>'
                });

                var $growl = $('#growl-' + growlId);
                $growl.find('a.answer-call-with-video').click(function () {
                    if (initCall()) {
                        growl.close();
                        //$growl.find('[data-notify="dismiss"]').trigger('click');
                    }
                });
                $growl.find('a.decline-call').click(function () {
                    growl.close();
                    //$growl.find('[data-notify="dismiss"]').trigger('click');
                });
                return false;
            });

            if (callback !== undefined) {
                callback();
            }
        });
    }

    function _initPatientEvents(patientId) {

        mdlUtils.find(_getFragment().$html, '', ".patientTabs").on('click', function () {
            var $tab = $(this);
            var href = $tab.find('a').attr('href');

            if (cFragmentUrl.params && (cFragmentUrl.params.note && href !== '#patientNotesContent' ||
                cFragmentUrl.params.event && href !== '#patientEventsContent')) {
                router.route({template: cFragmentUrl.template});
            }
        });

        mdlUtils.find(_getFragment().$html, '', ".backToPatientList").on('click', function () {
            $find("#patientDetailsContent").hide();
            $find("#patientsContent").show();
        });

        var $container = mdlUtils.find(_getFragment().$html, '', '#createCareTeamContainer');
        var $createCareTeamMemberButton = mdlUtils.find(_getFragment().$html, '', '#createCareTeamMember');

        $createCareTeamMemberButton.on('click', function () {
            _initCreateEditCareTeamMemberDialog($container, null, null, null, null, false, $(this));
        });

        var $createAffiliatedCareTeamMemberButton = mdlUtils.find(_getFragment().$html, '', '#createAffiliatedCareTeamMember');
        $createAffiliatedCareTeamMemberButton.on('click', function () {
            _initCreateEditCareTeamMemberDialog($container, null, null, null, null, true, $(this));
        });

        $container = mdlUtils.find(_getFragment().$html, '', '#editPatientContainer');
        var $editPatientButton = mdlUtils.find(_getFragment().$html, '', '#editPatient');
        $editPatientButton.on('click', function () {
            _initCreateEditPatientDialog($container, patientId, $(this));
        });
        var $deactivatePatientButton = mdlUtils.find(_getFragment().$html, '', '#deactivateRecord');
        $deactivatePatientButton.on('click', function () {
            $.ajax({
                url: 'care-coordination/patients/patient/' + patientId + '/toggle-activation',
                type: 'POST',
                beforeSend: function (xhr) {
                    mdlUtils.csrf(xhr);
                }
            }).success(function (data) {
                var $activationAlert = mdlUtils.find(_getFragment().$html, '', '#activationAlert');
                if (data) {
                    $deactivatePatientButton.text('DEACTIVATE RECORD');
                    $activationAlert.text('This record has been activated.');

                }
                else {
                    $deactivatePatientButton.text('ACTIVATE RECORD');
                    $activationAlert.text('This record has been deactivated.');
                }
                $activationAlert.show();
            }).fail(function (response) {
                mdlUtils.onAjaxError(response, function () {
                    bootbox.alert(response.responseText);
                });
            });
            return false;
        });

        var $recordLnk = $('.patientDetailsCcdLink');
        if ($recordLnk.length > 0) {
            var template = $recordLnk.attr('data-ajax-url-tmpl');
            var vars = $recordLnk.attr('data-ajax-url-vars');
            var params = $recordLnk.attr('data-ajax-url-params');

            $recordLnk.on('click', function () {
                //router.route(mdlUtils.getUrl(url, variables, params));
                var url = mdlUtils.getUrl(template, vars, params);
                if (template == 'patient-search') {
                    router.reload(url);
                }
                else {
                    router.route(url);
                    $.each($('.baseHeader .ldr-head-lnk.active'), function () {
                        $(this).removeClass('active');
                        $(this).removeClass('bottom');
                    });
                    var link = $('.baseHeader .ldr-head-lnk.personalHealthRecordLnk ');
                    link.addClass('active bottom');
                }
                return false;
            });
        }
    }

    function _showLatestNote(noteId, patientId) {
        showNoteFromHistory = true;
        $.ajax({
            url: 'care-coordination/notes/' + noteId + '/latest'
        }).success(
            function (data) {
                _showNote(data, patientId);
            }
        );
    }

    function _showNote(noteId, patientId) {
        _showNoteDetails(noteId, function () {
            $('[href="#patientNotesContent"]').click();
            displayedNoteId = noteId;
            $.ajax({
                url: 'care-coordination/notes/patient/' + patientId + '/' + displayedNoteId + '/page-number'
            }).success(
                function (data) {
                    openNoteDetailsOnNextPage = true;
                    _getFragment().widgets.patientNoteList.api().page(data).draw(false);
                }
            );
        });
    }

    function _showNoteDetails(noteId, successCallback) {
        var historyNoteIdRegexp = /historyNoteId-(\d+)/;
        $.ajax({
            url: 'care-coordination/notes/' + noteId + "/note-details?timeZoneOffset=" + new Date().getTimezoneOffset(),
            headers: {'X-Content-Compressing': 'enabled'}
        })
            .success(function (data) {
                var $noteDetails = mdlUtils.find(_getFragment().$html, '', "#noteDetails");
                $noteDetails.hide();
                $noteDetails.empty();
                $noteDetails.append(data);
                $noteDetails.show();

                //init edit modal
                _initNotesModal(mdlUtils.find(_getFragment().$html, '', '#editNote'),
                    'care-coordination/notes/' + noteId + '/edit?timeZoneOffset=' + new Date().getTimezoneOffset(),
                    'A note has been updated', function (data) {
                        _showNote(data, $('#currentPatientId').val());
                    });

                //init view modal
                $.each(_getFragment().$html.find('.viewNoteLink'), function (i, noteLink) {
                        var id = historyNoteIdRegexp.exec(noteLink.id)[1];
                        _initNotesModal($(noteLink),
                            'care-coordination/notes/' + id + '/view');
                    }
                );

                mdlUtils.find(_getFragment().$html, '', '#relatedEventId').on('click', function () {
                    var template = $(this).attr('data-ajax-url-tmpl');
                    var vars = $(this).attr('data-ajax-url-vars');
                    var params = $(this).attr('data-ajax-url-params');

                    var url = mdlUtils.getUrl(template, vars, params);
                    router.route(url);
                    return false;
                });

                if (successCallback !== undefined) {
                    successCallback();
                }
            });
    }

    function initCcdModal(options) {
        if (ccdModalFunctions[options.sectionName] === undefined) {
            return;
        }
        var $ccdContainer = mdlUtils.find(_getFragment().$html, '', '#ccdContainer');
        var $button = $(this);
        if ($button.hasClass("pending")) {
            return;
        }
        $button.addClass("pending");
        var endpoint = 'patient-info/' + options.residentId + '/ccd/' + options.sectionName + '/' + options.mode;
        if (options.mode === 'view' || options.mode === 'edit') {
            endpoint += '/' + options.id;
        }
        endpoint += '?hashKey=' + options.hashKey;
        $.ajax({
            url: endpoint,
            headers: {'X-Content-Compressing': 'enabled'}
        })
            .success(function (data) {
                $ccdContainer.empty();
                $ccdContainer.append(data);

                ccdModalFunctions[options.sectionName]($ccdContainer, options);

                $button.removeClass("pending");
                $(window).resize(function () {
                    $ccdContainer.find('.modal-body').css({'max-height': $(window).height() - 2 * (30 + 80)});
                });
                $(window).trigger('resize');
                $ccdContainer.find('#' + options.sectionName + 'CcdModal').modal('show');
            })
            .fail(function (response) {
                $button.removeClass("pending");
                alert(response.responseText); // TODO
            });
        return false;
    }

    function initProblemsCcdModal($container, options) {
        if (options.mode === 'add') {
            $container.find('#problem\\.startDate').datetimepicker({
                useCurrent: false,
                defaultDate: false,
                format: 'MM/DD/YYYY hh:mm A (Z)',
                maxDate: new Date()
            });

            $container.find('#problem\\.onSetDate').datetimepicker({
                format: 'MM/DD/YYYY',
                maxDate: new Date()
            });

            $container.find('#problem\\.recordedDate').datetimepicker({
                defaultDate: new Date(),
                format: 'MM/DD/YYYY hh:mm A (Z)',
                maxDate: new Date()
            });
        }

        var validator = _addProblemsValidation($container);

        var $endDate = $container.find('#problem\\.endDate');
        $endDate.attr('disabled', 'disabled');

        var $statusDropdown = $container.find("#problem\\.status\\.id");

        $statusDropdown.on('change', function () {
            var $selectedOption = $(this).find(':selected');

            // 413322009 - 'Resolved' status code
            if ($selectedOption.attr('data-code') === "413322009") {
                $endDate
                    .removeAttr('disabled')
                    .datetimepicker({
                        format: 'MM/DD/YYYY hh:mm A (Z)',
                        maxDate: new Date()
                    });
            } else {
                $endDate.attr('disabled', 'disabled');
                $endDate.val('')
            }
        });

        var $primaryRadio = $container.find('[name="primary"]');
        var $primaryRadioNo = $primaryRadio.filter(function () {
            return this.value === 'false'
        });
        var $primaryRadioYes = $primaryRadio.filter(function () {
            return this.value === 'true'
        });

        var alreadyPrimary = $primaryRadioYes.is(':checked');

        if (!alreadyPrimary) {
            //todo click area
            $primaryRadioNo.on('click', function () {
                alreadyPrimary = false;
            });
            $primaryRadioYes.on('click', function () {
                if ($primaryRadioYes.hasClass('pending') || alreadyPrimary) {
                    return;
                }
                $primaryRadioYes.addClass('pending');
                $.ajax('patient-info/' + options.residentId + '/ccd/problems/primary?hashKey=' + options.hashKey)
                    .success(function (data) {
                        if (data !== -1) {
                            bootbox.confirm({
                                title: "Change primary diagnosis?",
                                message: "Primary diagnosis is already set. Do you want to change the selection?",
                                buttons: {
                                    cancel: {
                                        label: 'No'
                                    },
                                    confirm: {
                                        label: 'Yes'
                                    }
                                },
                                callback: function (result) {
                                    $primaryRadioNo.prop('checked', !result);
                                    $primaryRadioYes.prop('checked', result);
                                    $primaryRadioYes.removeClass('pending');
                                    alreadyPrimary = result;

                                    //bootbox acts like modal and bootstrap doesn't support multiple modals
                                    //opened at the same time and removes this class from body which breaks
                                    //scrolling inside modal
                                    $(this).on('hidden.bs.modal', function () {
                                        $('body').addClass('modal-open')
                                    });
                                }
                            });

                        } else {
                            $primaryRadioYes.prop('checked', true);
                            $primaryRadioYes.removeClass('pending');
                        }
                        $primaryRadio.blur();
                    });
                return false;
            });
        }

        var problemValueDropdownToggle = wgtUtils.lifeSearch(_getFragment().$html, '', {
            select: '#problem\\.value\\.id',
            searchInput: '#problem\\.value\\.id_searchInput',
            notEmptyKeyUpCallBack: function (value) {
                _getFragment().widgets.problemValueDropdown.api().ajax.reload();
            },
            searchInputNotEmptyClickCallback: function (value) {
                _getFragment().widgets.problemValueDropdown.api().ajax.reload();
            }
        });

        _getFragment().widgets.problemValueDropdown = wgtUtils.grid(_getFragment().$html, '', {
            tableId: "problemValueDropdown",
            totalDisplayRows: 7,
            selectable: {
                type: 'single'
            },
            searchFormId: 'problem\\.value\\.id_searchInput',

            language: {
                sZeroRecords: 'No data found. Please contact your Administrator to solve the issue.'
            },

            callbacks: {
                rowCallback: function (row, data, index) {
                    var $row = $(row);
                    $row.click(function () {
                        problemValueDropdownToggle.setSelectValue(data.id, data.displayName);
                        problemValueDropdownToggle.close();
                    })
                },
                "drawCallback": function (settings) {
                },
                errorCallback: function (error) {
                },
                footerCallback: function (tfoot, data, start, end, display) {
                    $(tfoot).hide();
                },
                headerCallback: function (thead, data, start, end, display) {
                    //$(thead).hide();
                }
            }
        });

        _getFragment().widgets.diagnosisInformation = wgtUtils.grid(_getFragment().$html, '', {
            tableId: "problemDiagnosisInformation",
            paginate: false,
            selectable: {
                type: 'single'
            },
            searchFormId: 'problem\\.value\\.id',

            callbacks: {
                rowCallback: function (row, data, index) {
                    var $row = $(row);
                },
                "drawCallback": function (settings) {
                },
                errorCallback: function (error) {
                },
                footerCallback: function (tfoot, data, start, end, display) {
                    $(tfoot).hide();
                },
                headerCallback: function (thead, data, start, end, display) {
                    // $(thead).hide();
                }
            }
        });

        var $problemName = $container.find("#problem\\.value\\.id");

        var $diagnosisInformationWrapper = $container.find('#diagnosisInformationWrapper');
        $diagnosisInformationWrapper.hide();

        $problemName.on('change', function () {
            if ($(this).val() === '' || $(this).val() === null || $(this).val() === undefined) {
                $diagnosisInformationWrapper.hide();
            } else {
                $diagnosisInformationWrapper.show();
                _getFragment().widgets.diagnosisInformation.api().ajax.reload();
            }
        });

        $container.find("#submitNoteBtn").on('click', function () {
            var $form = $container.find("#problemsCcdForm");
            if (!$form.valid()) {
                validator.focusInvalid();
                return false;
            }
            var $th = $(this);
            if (!$th.hasClass("pending")) {
                $th.addClass("pending");
                $.ajax({
                    url: $form.attr('action'),
                    data: new FormData($form[0]),
                    type: 'POST',
                    enctype: 'multipart/form-data',
                    processData: false,
                    contentType: false,
                    beforeSend: function (xhr) {
                        mdlUtils.csrf(xhr);
                    },
                    success: function (data) {
                        $container.find("#problemsCcdModal").modal('hide');
                        _initCcdTotal(options.sectionName, options.residentId, options.hashKey);
                        _getFragment().widgets.problemsList.api().ajax.reload();
                        if (options.mode === 'add') {
                            _getFragment().widgets.patientEventList.api().ajax.reload();
                            bootbox.alert('Problem has been added');
                        } else if (options.mode === 'edit') {
                            bootbox.alert('Problem has been edited');
                        }
                    },
                    error: function (error) {
                        // $container.find("#problemsCcdModal").modal('hide');
                        mdlUtils.onAjaxError(error, function () {
                            alert(error.responseText);
                        });
                        $th.removeClass("pending");
                    }
                });
            }
            return false;
        });

        $container.find('.cancelBtn').on('click', function () {
            $container.find("#problemsCcdModal").modal('hide');
        });
    }

    function _addProblemsValidation($container) {
        return $container.find("#problemsCcdForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    'value.id': {
                        required: true
                    },
                    'value.id_searchInput': {
                        required: true
                    },
                    primary: {required: true},
                    'type.id': {required: true},
                    'status.id': {required: true},
                    recordedDate: {required: true},
                    comments: {maxlength: 20000}
                },
                messages: {
                    'value.id': {
                        required: getErrorMessage("field.empty")
                    },
                    'value.id_searchInput': {
                        required: getErrorMessage("field.empty")
                    },
                    primary: {
                        required: getErrorMessage("field.empty")
                    },
                    'type.id': {required: getErrorMessage("field.empty")},
                    'status.id': {required: getErrorMessage("field.empty")},
                    recordedDate: {required: getErrorMessage("field.empty")}
                }
            })
        )
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
                    _initPatientsList();

                    this.setEvents();

                    fragment.inited = true;

                    this.render();
                    this.show();
                }
            });
        },

        update: function (url) {

            if ((currentOrganizationFilter != ExchangeApp.modules.Header.getCurrentOrganizationFilter())
                || (JSON.stringify(currentCommunityFilter) != JSON.stringify(ExchangeApp.modules.Header.getCurrentCommunityFilter()))) {
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
            fragmentLoader.load({
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
            $.each($('#care-coordination').find('.patientTabs'), function () {
                if ($(this).hasClass('active')) {
                    var id = $(this).attr('id');
                    switch (id) {
                        case 'patientDetailsTab':
                            patientActiveTab = 'patientDetailsInfoContent';
                            break;
                        case 'patientEventsTab':
                            if (!(cFragmentUrl.params && cFragmentUrl.params.event)) {
                                patientActiveTab = 'patientEventsContent';
                            }
                            break;
                        case 'ccdDetailsTab':
                            patientActiveTab = 'patientCCDContent';
                            break;
                        case 'documentsTab':
                            patientActiveTab = 'patientDocumentsContent';
                            break;
                        case 'notesTab':
                            if (!(cFragmentUrl.params && cFragmentUrl.params.note)) {
                                patientActiveTab = 'patientNotesContent';
                            }
                            break;
                    }
                    $(this).children('a').blur();
                }
            });
            $.each($('#care-coordination').find('.patientEventTabs'), function () {
                if ($(this).hasClass('active')) {
                    var id = $(this).attr('id');
                    switch (id) {
                        case 'patientEventsDescriptionTab':
                            patientEventsActiveTab = 'patientEventsDescriptionContent';
                            break;
                        case 'patientSentNotificationsTab':
                            patientEventsActiveTab = 'patientSentNotificationsContent';
                            break;
                    }
                }
            });
            _getFragmentHolder().hide();
            $('#content').addClass('loading');
        },

        show: function () {
            //fragment is not yet initialized => nothing to show
            if (_getFragment() === undefined || !_getFragment().inited) {
                return;
            }
            // activate tab
            if ($('#content').find(".tab-pane:visible").length == 0) {
                $.each($('#care-coordination').find('.nav li'), function () {
                    $(this).removeClass('active');
                });
                $('a[data-target^="#' + holderContentId + '"]').parent('li').addClass('active');
                // render content
                _getFragmentHolder().show();
            }

            var currentPatientId = $('#currentPatientId').val();
            if (currentPatientId !== undefined) {
                _fetchNotesTotal(currentPatientId, function (totalCount) {
                    if (totalCount !== currentTotalNotesCount) {
                        currentTotalNotesCount = totalCount;
                        _setNotesBadgeValue(totalCount);
                        _selectFirstNote();
                    }
                });
            }

            if (cFragmentUrl.params) {
                if (cFragmentUrl.params.note && cFragmentUrl.params.patient) {
                    //show the note...
                    var noteId = cFragmentUrl.params.note;
                    var patientId = cFragmentUrl.params.patient;

                    if ($('#currentPatientId').val() == patientId) {
                        _showLatestNote(noteId, patientId);
                    } else {
                        showPatientDetails(patientId, null, false, true, function () {
                            _showLatestNote(noteId, patientId);
                        });
                    }
                } else if (cFragmentUrl.params.event) {
                    //or show the event...
                    var showEvent = function (patientId) {
                        _showEventDetails(cFragmentUrl.params.event, function () {
                            $('[href="#patientEventsContent"]').click();
                            $.ajax('care-coordination/patients/patient/' + patientId + '/event/' + cFragmentUrl.params.event + '/page-number').success(function (data) {
                                openEventDetailsOnNextPage = true;
                                _getFragment().widgets.patientEventList.api().page(data).draw(false);
                            });
                        }, function (response) {
                            bootbox.alert(response.responseText);
                            router.route({template: cFragmentUrl.template});
                        });
                    };
                    if ($('#currentPatientId').val() === undefined) {
                        showPatientDetails(cFragmentUrl.params.patient, null, false, true, function () {
                            showEvent(cFragmentUrl.params.patient);
                        });
                    } else {
                        showEvent($('#currentPatientId').val());
                    }
                }
            } else {
                //or show previously opened tab
                if (patientActiveTab) {
                    $('[href="#' + patientActiveTab + '"]').click();
                }
            }

            if (patientEventsActiveTab) {
                $('a[href^="#' + patientEventsActiveTab + '"]').parent('li').addClass('active');
            }
            $('#content').removeClass('loading');
        },

        setEvents: function () {
            $find(".datepicker").datepicker();
            $find(".datepicker").change(function () {
                var dt = new Date($(this).val());
                if (!isNaN(dt.getTime())) {
                    $(this).datepicker('setValue', dt);
                }
                else {
                    $(this).val('');
                }
            });

            $find("input[type='radio']").styler();

            $find("#patientSearch").on('click', function () {
                _getFragment().widgets.patientsList.api().order([[1, 'asc'], [0, 'asc']]);
                _getFragment().widgets.patientsList.api().ajax.reload();
                return false;
            });

            $find("#showDeactivatedRecords").on('change', function () {
                $find("#showDeactivated").val(this.checked);
                _getFragment().widgets.patientsList.api().ajax.reload();
                return false;
            });

            $find("#patientSearchClear").on('click', function () {
                $find("#patientsFilter").clearForm();
                return false;
            });

            $find("#addPatient").on('click', function () {
                _initCreateEditPatientDialog($find('#addPatientContainer'), 0, $(this));
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