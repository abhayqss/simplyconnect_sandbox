/**
 * Created by stsiushkevich on 01.10.18.
 */

function ServicePlanChangeHistoryList(props) {
    Grid.apply(this, arguments);
}

ServicePlanChangeHistoryList.prototype = Object.create(Grid.prototype);
ServicePlanChangeHistoryList.prototype.constructor = ServicePlanChangeHistoryList;

ServicePlanChangeHistoryList.prototype.getDefaultProps = function () {
    var props = Grid.prototype.getDefaultProps.apply(this);

    var me = this;
    return $.extend({}, props, {
        id: 'servicePlanChangeHistoryList',
        columns: [
            {
                name: 'dateModified',
                title: 'Date',
                format: 'date',
                render: function (v) {
                    return (new Date(v)).format('mm/dd/yyyy HH:MM tt')
                }
            },
            {
                name: 'status',
                title: 'Status',
                format: 'string'
            },
            {
                name: 'author',
                title: 'Author',
                format: 'string'
            },
            {
                name: 'updates',
                title: 'Updates',
                format: 'fake',
                render: function (data, type, rowData) {
                    return rowData.dateModified !== me.props.planDateModified ? (
                        '<div class="updates__view-details-lnk">View Details</div>'
                    ) : '';
                }
            }
        ],
        callbacks: {
            rowCallback: function (row, data, index) {
                $(row).on('click', '.updates__view-details-lnk', function (e) {
                    me.props.onSelect(data);
                });
            },
            errorCallback: function (error) {
                alert(error.responseText);
            },
            footerCallback: function (tfoot, data, start, end, display) {
                $(tfoot).hide();
            }
        },
        hasSorting: false,
        noDataText: 'No History.',
        onSelect: function (data) {},
        isLoadingDeferred: true
    });
};

ServicePlanChangeHistoryList.prototype.getDataSource = function () {
    var planId = this.props.planId;
    var patientId = this.props.patientId;
    var context = ExchangeApp.info.context;

    return {
        ajax: {url: context + '/care-coordination/patients/patient/' + patientId + '/service-plans/' + planId + '/history'}
    };
};