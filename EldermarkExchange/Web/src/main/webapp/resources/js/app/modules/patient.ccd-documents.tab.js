ExchangeApp.utils.patientccd = (function(){
    var mdlUtils = ExchangeApp.utils.module;
    var router = ExchangeApp.routers.ModuleRouter;
    var findElement = function(id) {
        return $(id);
    };

    var _getFragment;
    var _grid;
    var _alert;
    var _isCCmodule;

    var cFragmentUrl;

    function $find(selector) {
        var fragment = _getFragment();
        return mdlUtils.find(fragment.$html, fragment.random, selector);
    }

    function _initCompanyName(){
        $.ajax({
            type: 'GET',
            contentType: 'json',
            url: findElement('#companyNameUrl').attr('href'),
            success: function (data) {
                findElement('#companyName').text(data);
            },
            error: function () {
                findElement('#companyName').text('My company');
            }
        });
    }

    function _initDocumentListWgt(residentId, databaseId, hashKey, aggregated, downloadUrlBasic, gridFunc) {
        var cFragment = _getFragment();
        cFragment.widgets.documentList = gridFunc( {
            tableId: 'documentList',
            searchFormId: 'documentFilterForm',
            totalDisplayRows: 25,
            colSettings: {
                documentType: {
                    mRender: function (data, type, full) {
                        if (full.cdaViewable === true) {
                            return "Ccd";
                        }
                        return (data[0].toUpperCase()
                            + data.toLowerCase().substring(1)).replace('_', ' ');
                    },
                    bSortable: false
                },
                dataSource: {
                    bSortable: false,
                    customRender: function (data, type, row) {
                        if (!data) return '';
                        return '<a href="#" id="resident-document-' + row.id + '" onclick="return false">' + data + '</a>';
                    }
                }
            },
            selectable: {
                type: 'multiple'
            },
            callbacks: {
                rowCallback: function (row, data, index) {
                    _showDocumentsDataSourceDetailsPopup($(row).find('#resident-document-'+data.id)[0], data.dataSource, data.dataSourceOid, data.community, data.communityOid);
                    var $link = $('<a>');
                    var downloadUrl;
                    if (data.cdaViewable === true) {
                        downloadUrl = 'documents/' + data.id + '/cda-view'
                        $link.attr('target', '_blank');
                        $link.attr('rel', 'nofollow noopener');
                    } else {
                        downloadUrl = downloadUrlBasic;

                        var docType = data.documentType.toLowerCase();
                        if (docType === 'ccd' || docType === 'facesheet') {
                            downloadUrl += docType + '/download';
                            data.id = docType;
                        } else if (docType === 'custom' || docType === 'nwhin' || docType === 'lab_results') {
                            downloadUrl += 'custom/' + data.id + '/download';
                        }

                        downloadUrl += '?hashKey=' + hashKey;
                        downloadUrl += '&databaseId=' + databaseId;
                        downloadUrl += '&documentName=' + data.documentTitle;
                        if (docType === 'ccd' || docType === 'facesheet') {
                            downloadUrl += '&aggregated=' + aggregated;
                        }
                        downloadUrl += '&timeZoneOffset=' + new Date().getTimezoneOffset();
                    }

                    $link.attr('href', downloadUrl);
                    $link.html(data.documentTitle);

                    var documentTitleTd = _getFragment().widgets.documentList.api().column('documentTitle:name').nodes()[index];
                    $(documentTitleTd).html($link);
                },
                errorCallback: function (error) {
                    mdlUtils.onAjaxError(error, function(e){
                        _alert({
                            action: 'add',
                            placeSelector: '.documents',
                            message: e.responseText,
                            closable: {
                                timer: 45000,
                                btn: true
                            }
                        });
                    });
                }
            }
        });
    }

    function _showDocumentsDataSourceDetailsPopup(element, orgName, orgOid, commName, commOid) {
        if (!element) {
            return;
        }
        var $trPopoverContent = findElement('#documentDataSourceDetailsTemplate').clone();

        var popoverId = 'documentDataSourceDetailsTemplate-' + element.id;
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
            'dataSourceName' : orgName,
            'dataSourceOID' : orgOid,
            'communityName' : commName,
            'communityOID' : commOid,
        };

        for (var key in data) {
            if (data[key]) {
                if (_isCCmodule) {
                    $trPopoverContent.find('#' + key).text(data[key]);
                } else {
                    mdlUtils.find($trPopoverContent, _getFragment().random, '#'+key).text(data[key]);
                }
            } else {
                if (_isCCmodule) {
                    $trPopoverContent.find('#' + key + 'Layout').hide();
                } else {
                    mdlUtils.find($trPopoverContent, _getFragment().random, '#'+key+'Layout').hide();
                }
            }
        }
    }

    function _doDocListMultiRowOperation(options, residentId, databaseId, hashKey, downloadDeleteBasicUrl, aggregatedBasic, updateDocListBadgeValue) {
        /*
         * document list operations: delete, download
         * */

        var documentIds = _getFragment().widgets.documentList.fnSettings().oInit.store.items;

        /*ajax request*/
        switch (options.operationName) {
            case 'delete':
                var customDocsIds = documentIds.filter(function (docId) {
                    if (docId.indexOf('ccd') == -1 && docId.indexOf('facesheet') == -1) {
                        return true;
                    } else {
                        var docName = docId == 'ccd' ? 'CCD.XML' : 'FACESHEET.PDF';
                        _alert({
                            action: 'add',
                            placeSelector: '.documents',
                            message: docName + ' cannot be deleted.',
                            closable: {
                                timer: 45000,
                                btn: true
                            }
                        });
                        return false;
                    }
                });

                var ajaxError = function (error) {
                    mdlUtils.onAjaxError(error, function(e){
                        _alert({
                            action: 'add',
                            placeSelector: '.documents',
                            message: e.responseText,
                            closable: {
                                timer: 45000,
                                btn: true
                            }
                        });
                    });
                };

                $.each(customDocsIds, function (index, docId) {
                    $.ajax({
                        type: 'DELETE',
                        data: {hashKey: hashKey},
                        url: downloadDeleteBasicUrl + 'custom/' + docId + '/delete?hashKey=' + hashKey + '&databaseId=' + databaseId,
                        beforeSend: function(xhr){
                            mdlUtils.csrf(xhr);
                        },
                        success: function () {
                            _getFragment().widgets.documentList.api().ajax.reload();
                            _getFragment().widgets.documentList.fnSettings().oInit.store.removeItem(docId);
                            updateDocListBadgeValue(-1);
                        },
                        error: ajaxError
                    });
                });
                break;

            case 'download':
                $.each(documentIds, function (index, docId) {
                    var data = $.grep(_getFragment().widgets.documentList.api().data(), function(e) {return e.id === docId})[0];

                    var docType = data.documentType.toLowerCase();
                    //var downloadUrl = $('#patientInfoUrl').attr('href') + residentId + '/documents/';
                    //var downloadUrl = $('#deleteDocumentUrl').attr('href') + residentId + '/';
                    var downloadUrl = downloadDeleteBasicUrl;
                    var aggregated = null;

                    if (docType === 'ccd' || docType === 'facesheet') {
                        downloadUrl += docType + '/download';
                        aggregated = aggregatedBasic;
                    } else if (docType === 'custom' || docType === 'nwhin' || docType === 'lab_results') {
                        downloadUrl += 'custom/' + docId + '/download';
                    }

                    $.fileDownload(
                        downloadUrl, {
                            data: {hashKey: hashKey, databaseId: databaseId, documentName: data.documentTitle, aggregated: aggregated},
                            failCallback: ajaxError
                        });
                });
                break;
        }
    }

    /*function _initDocumentsTotal(residentId, databaseId, hashKey, aggregated, badgeSelector, setDocListBadgeValueFunc){
        var url = findElement('#patientInfoUrl').attr('href') + residentId.toString() + '/documents/' + aggregated + '/total?hashKey=' + hashKey + '&databaseId=' + databaseId;

        $.ajax({
            type: 'GET',
            contentType: 'json',
            url: url,
            success: function (totalCount) {
                var isEmpty = (totalCount <= 0);
                setDocListBadgeValueFunc(isEmpty, totalCount);
            },
            error: function () {
                findElement(badgeSelector).addClass('hidden');
            }
        });
    }*/

    function _setDocumentListPanelEvents(residentId, databaseId, hashKey, downloadDeleteBasicUrl, aggregatedBasic, updateDocListBadgeValue, isCareCoordinationModule, composeBtnId){

        findElement('#docSearchBtn').on('click', function (e) {
            _getFragment().widgets.documentList.api().ajax.reload();
            return false;
        });

        findElement('#deleteDocsBtn').on('click', function (e) {
            _doDocListMultiRowOperation({operationName: 'delete'}, residentId, databaseId, hashKey, downloadDeleteBasicUrl, aggregatedBasic, updateDocListBadgeValue);
        });

        findElement('#downloadDocsBtn').on('click', function (e) {
            _doDocListMultiRowOperation({operationName: 'download'}, residentId, databaseId, hashKey, downloadDeleteBasicUrl, aggregatedBasic, updateDocListBadgeValue);
        });

        findElement('#' + composeBtnId).on('click', function(e){
            var documentIds = _getFragment().widgets.documentList.fnSettings().oInit.store.items;
            var customDocIds = [], reportTypes = [];

            documentIds.forEach(function(docId) {
                if (docId.indexOf('ccd') != -1 || docId.indexOf('facesheet') != -1)
                    reportTypes.push(docId);
                else
                    customDocIds.push(docId);
            });

            var template = $(this).attr('data-ajax-url-tmpl');
            var vars = $(this).attr('data-ajax-url-vars');
            var params = $(this).attr('data-ajax-url-params')
                + '&clinical=' + reportTypes
                + '&documentIds=' + customDocIds
                + '&isCCModule=' + isCareCoordinationModule;

            var url = mdlUtils.getUrl(template, vars, params);
            router.route(url);

            return false;
        });

        var badgeUpdatedHandler = function(e, count) {
            var $badge = $(e.target);
            if (count > 0) {
                $badge.find('.badgeValue').html(count);
                $badge.removeClass('hidden');
            } else {
                $badge.find('.badgeValue').html(0);
                $badge.addClass('hidden');
            }
        };

        var cFragment = _getFragment();
        cFragment.widgets.documentList.fnSettings().oInit
            .store.onCountChanged($find('.documents button .badge'), badgeUpdatedHandler);

        cFragment.widgets.documentList.fnSettings().oInit
            .store.onCountChanged($find('.documents a .badge'), badgeUpdatedHandler);
    }

    function _setFileInputChangeTrigger(formId) {
        findElement('#' + formId + ' input').on('change', function () {
            $(this).trigger('focusout');
        });
    }

    function _addUploadDocumentFormValidation(){
        _setFileInputChangeTrigger('uploadDocumentForm');

        return findElement("#uploadDocumentForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    document: {required: true, filesize: 1000000000},
                    sharingOption: {required: true}
                },
                messages: {
                    document: {
                        required: getErrorMessage("field.empty"),
                        filesize: "File must be less than 1 GB."
                    },
                    sharingOption: {
                        required: "Please select a sharing option."
                    }
                }
            })
        );
    }

    function _initUploadForm(residentId, databaseId, hashKey, containerSelector, basicUrl, updateDocListBadgeValue) {
        _addUploadDocumentFormValidation();

        findElement(":file").each(function (i, e) {
            var btnText = $(e).attr('data-buttonText');
            var enableIcon = $(e).attr('data-icon') == 'true';
            $(e).filestyle({icon: enableIcon, buttonText: btnText});
        });

        findElement("input[type='radio']").styler();

        findElement('#uploadDocumentForm').submit(function () {
            /*upload document form validation*/
            if (!$(this).valid()) return false;
            $(containerSelector).hide();
            $('#content').addClass('loading');

            // prevent double clicks
            findElement("#uploadDocumentBtn").prop('disabled', true);

            /*uploading document*/
            $.ajax(
                {
                    url: basicUrl + 'upload?hashKey=' + hashKey + '&databaseId=' + databaseId,
                    type: 'POST',
                    enctype: 'multipart/form-data',
                    data: new FormData(this),
                    processData: false,
                    contentType: false,
                    beforeSend: function(xhr){
                        mdlUtils.csrf(xhr);
                    },
                    success: function (success) {
                        findElement('.modal').modal('hide');
                        findElement("#uploadDocumentBtn").prop('disabled', false);
                        updateDocListBadgeValue(+1);

                        if(_getFragment().widgets.documentList) {
                            _getFragment().widgets.documentList.api().ajax.reload();
                        }
                        $('#content').removeClass('loading');
                        $(containerSelector).show();
                    },
                    error: function (error) {
                        findElement('.modal').modal('hide');
                        findElement("#uploadDocumentBtn").prop('disabled', false);
                        $('#content').removeClass('loading');
                        $(containerSelector).show();
                        mdlUtils.onAjaxError(error, function() {
                            _alert({
                                action: 'add',
                                placeSelector: '.documents',
                                message: error.responseText,
                                closable: {
                                    timer: 45000,
                                    btn: true
                                }
                            })
                        });
                    }
                }
            );
            return false;
        });

        findElement('.modal').on('hide.bs.modal', function () {
            findElement('#uploadDocumentForm')[0].reset();
            findElement("input[type='radio']").trigger('refresh')
        });
    }

    return {

        init: function (getFragmentFunc, gridFunc, alertFunc, cFragmentUrlObj, isCareCoordinationModule, findFunc) {
            _getFragment = getFragmentFunc;
            _grid = gridFunc;
            _alert = alertFunc;
            cFragmentUrl = cFragmentUrlObj;
            _isCCmodule = isCareCoordinationModule;
            if (findFunc) {
                findElement = findFunc;
            }
        },

        initCompanyName: function () {
            return _initCompanyName();
        },

        initDocumentList: function (residentId, databaseId, hashKey, aggregated, downloadUrl, gridFunc) {
            return _initDocumentListWgt(residentId, databaseId, hashKey, aggregated, downloadUrl, gridFunc);
        },

        /*initDocumentsTotal: function (residentId, databaseId, hashKey, aggregated, badgeSelector, setDocListBadgeValueFunc) {
            return _initDocumentsTotal(residentId, databaseId, hashKey, aggregated, badgeSelector, setDocListBadgeValueFunc);
        },*/

        initUploadForm: function(residentId, databaseId, hashKey, containerSelector,  basicUrl, updateDocListBadgeValue) {
            return _initUploadForm(residentId, databaseId, hashKey, containerSelector, basicUrl, updateDocListBadgeValue);
        },

        setDocumentListPanelEvents: function(residentId, databaseId, hashKey, downloadDeleteBasicUrl, aggregatedBasic, updateDocListBadgeValue, isCareCoordinationModule, composeBtnId) {
            return _setDocumentListPanelEvents(residentId, databaseId, hashKey, downloadDeleteBasicUrl, aggregatedBasic, updateDocListBadgeValue, isCareCoordinationModule, composeBtnId);
        }
    }
})();